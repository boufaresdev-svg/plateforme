import { Component, OnInit, OnChanges, SimpleChanges, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  Stock,
  StockLegacy,
  AddStockCommand,
  UpdateStockCommand,
  EntreeStockCommand,
  SortieStockCommand,
  TransfertStockCommand,
  Entrepot,
  StockLot
} from '../../../../models/stock.model';
import { StockService } from '../../../../services/stock.service';
import { MouvementStockService } from '../../../../services/mouvement-stock.service';
import { AjustementStockService } from '../../../../services/ajustement-stock.service';
import { EntrepotService } from '../../../../services/entrepot.service';
import { NotificationService } from '../../../../services/notification.service';
import { StockLotService } from '../../../../services/stock-lot.service';

@Component({
  selector: 'app-stock-operations',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './stock-operations.component.html',
  styleUrls: ['./stock-operations.component.css']
})
export class StockOperationsComponent implements OnInit, OnChanges {
  @Input() stock: Stock | StockLegacy | null = null;
  @Input() isVisible: boolean = false;
  @Output() close = new EventEmitter<void>();
  @Output() operationCompleted = new EventEmitter<void>();

  operationForm!: FormGroup;
  activeTab: 'entree' | 'sortie' | 'ajustement' | 'transfert' = 'entree';
  availableStocks: (Stock | StockLegacy)[] = [];
  entrepots: Entrepot[] = [];
  availableLots: StockLot[] = [];
  selectedLot: StockLot | null = null;
  latestLot: StockLot | null = null; // Store the most recent lot for price reference
  uploadedFile: File | null = null;
  isProcessing = false;
  entryMode: 'new' | 'existing' = 'new';

  constructor(
    private fb: FormBuilder,
    private stockService: StockService,
    private mouvementService: MouvementStockService,
    private ajustementService: AjustementStockService,
    private entrepotService: EntrepotService,
    private notificationService: NotificationService,
    private stockLotService: StockLotService
  ) { }

  ngOnInit(): void {
    this.initializeForm();
    this.loadAvailableStocks();
    this.loadEntrepots();
  }

  ngOnChanges(changes: SimpleChanges): void {
    // When stock changes (modal opens with new stock), load lots if applicable
    if (changes['stock'] && changes['stock'].currentValue) {
      // Always load the latest lot for price reference when modal opens
      this.loadLatestLotForPriceReference();
      
      if (this.activeTab === 'sortie' || this.activeTab === 'transfert') {
        this.loadAvailableLots();
      } else if (this.activeTab === 'entree' && this.entryMode === 'existing') {
        // Also load lots for entree when in 'existing' mode
        this.loadAvailableLots();
      }
    }
  }

  /**
   * Load the latest lot to display previous prices as a reference for the user
   */
  private loadLatestLotForPriceReference(): void {
    if (!this.stock) {
      this.latestLot = null;
      return;
    }

    let articleId = '';

    if (this.isLegacyStock(this.stock)) {
      articleId = this.stock.id;
    } else {
      articleId = this.stock.articleId || this.stock.article?.id || '';
    }

    if (!articleId) {
      this.latestLot = null;
      return;
    }

    // Get all lots for this article to find the most recent one
    this.stockLotService.getStockLotsByArticle(articleId).subscribe({
      next: (lots) => {
        if (lots && lots.length > 0) {
          // Sort by dateAchat descending to get the most recent lot
          const sortedLots = lots.sort((a, b) => 
            new Date(b.dateAchat).getTime() - new Date(a.dateAchat).getTime()
          );
          this.latestLot = sortedLots[0];
          console.log('📊 Latest lot loaded for price reference:', this.latestLot);
        } else {
          this.latestLot = null;
        }
      },
      error: (error) => {
        console.error('Erreur lors du chargement du dernier lot:', error);
        this.latestLot = null;
      }
    });
  }

  initializeForm(): void {
    this.operationForm = this.fb.group({
      // Common fields
      quantite: ['', [Validators.required, Validators.min(1)]],
      nouvelleQuantite: ['', [Validators.required, Validators.min(0)]],
      motif: [''],
      reference: [''],
      notes: [''],
      destinationEntrepotId: [''],
      
      // Batch/Lot fields for stock entry
      prixAchatUnitaire: ['', [Validators.min(0)]],
      prixVenteUnitaire: ['', [Validators.min(0)]],
      dateAchat: [new Date().toISOString().split('T')[0]],
      dateExpiration: [''],
      numeroFacture: [''],
      
      // Lot selection for stock exit and entry (add to existing)
      stockLotId: [''],
      entryMode: ['new'] // 'new' or 'existing'
    });

    // Update validators based on active tab
    this.updateFormValidators();
  }

  updateFormValidators(): void {
    const quantiteControl = this.operationForm.get('quantite');
    const nouvelleQuantiteControl = this.operationForm.get('nouvelleQuantite');
    const destinationEntrepotControl = this.operationForm.get('destinationEntrepotId');
    const prixAchatControl = this.operationForm.get('prixAchatUnitaire');
    const prixVenteControl = this.operationForm.get('prixVenteUnitaire');
    const stockLotIdControl = this.operationForm.get('stockLotId');

    // Reset validators
    quantiteControl?.clearValidators();
    nouvelleQuantiteControl?.clearValidators();
    destinationEntrepotControl?.clearValidators();
    prixAchatControl?.clearValidators();
    prixVenteControl?.clearValidators();
    stockLotIdControl?.clearValidators();

    switch (this.activeTab) {
      case 'entree':
        quantiteControl?.setValidators([Validators.required, Validators.min(1)]);
        // Validators for prices and lot depend on entry mode
        this.updateEntryModeValidators();
        this.loadAvailableLots(); // Load lots for selection
        break;
      case 'sortie':
        quantiteControl?.setValidators([
          Validators.required,
          Validators.min(1),
          Validators.max(this.getCurrentQuantity())
        ]);
        stockLotIdControl?.setValidators([Validators.required]);
        this.loadAvailableLots();
        break;
      case 'ajustement':
        nouvelleQuantiteControl?.setValidators([Validators.required, Validators.min(0)]);
        break;
      case 'transfert':
        quantiteControl?.setValidators([
          Validators.required,
          Validators.min(1),
          Validators.max(this.getCurrentQuantity())
        ]);
        destinationEntrepotControl?.setValidators([Validators.required]);
        stockLotIdControl?.setValidators([Validators.required]);
        this.loadAvailableLots();
        break;
    }

    quantiteControl?.updateValueAndValidity();
    nouvelleQuantiteControl?.updateValueAndValidity();
    destinationEntrepotControl?.updateValueAndValidity();
    prixAchatControl?.updateValueAndValidity();
    prixVenteControl?.updateValueAndValidity();
    stockLotIdControl?.updateValueAndValidity();
  }

  loadAvailableStocks(): void {
    this.stockService.getStocks().subscribe(stocks => {
      // Cast to (Stock | StockLegacy)[] because the service might return mixed types in reality
      // or we just want to be safe with the filter
      this.availableStocks = (stocks as (Stock | StockLegacy)[]).filter(s => s.id !== this.stock?.id);
    });
  }

  loadEntrepots(): void {
    this.entrepotService.getEntrepots().subscribe({
      next: (data: any) => {
        if (data && data.content && Array.isArray(data.content)) {
          this.entrepots = data.content;
        } else if (Array.isArray(data)) {
          this.entrepots = data;
        } else {
          this.entrepots = [];
        }
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des entrepôts:', error);
        this.entrepots = [];
      }
    });
  }

  loadAvailableLots(): void {
    if (!this.stock) return;

    let articleId = '';
    let entrepotId = '';

    if (this.isLegacyStock(this.stock)) {
      articleId = this.stock.id;
      entrepotId = '';
    } else {
      articleId = this.stock.articleId || this.stock.article?.id || '';
      entrepotId = this.stock.entrepotId || '';
    }

    if (!articleId) return;

    if ((this.activeTab === 'sortie' || this.activeTab === 'transfert') && entrepotId) {
      // Load available lots for stock exit or transfer
      console.log('🔍 Loading lots for sortie/transfert - articleId:', articleId, 'entrepotId:', entrepotId);
      this.stockLotService.getAvailableStockLots(articleId, entrepotId).subscribe({
        next: (lots) => {
          this.availableLots = lots;
          console.log('📦 Lots loaded:', lots);
        },
        error: (error) => {
          console.error('Erreur lors du chargement des lots:', error);
          this.availableLots = [];
        }
      });
    } else if (this.activeTab === 'entree') {
      // For entry, load lots if in 'existing' mode, otherwise clear
      if (this.entryMode === 'existing' && entrepotId) {
        this.loadLotsForEntry(articleId, entrepotId);
      } else {
        this.availableLots = [];
      }
    }
  }

  private loadLotsForEntry(articleId: string, entrepotId: string): void {
    console.log('🔍 Loading lots for entry - articleId:', articleId, 'entrepotId:', entrepotId);
    console.log('📦 Current stock:', this.stock);
    
    this.stockLotService.getAvailableStockLots(articleId, entrepotId).subscribe({
      next: (lots) => {
        this.availableLots = lots;
        console.log('📦 Lots disponibles pour ajout:', lots);
      },
      error: (error) => {
        console.error('Erreur lors du chargement des lots:', error);
        this.availableLots = [];
      }
    });
  }

  onLotSelected(lotId: string): void {
    this.selectedLot = this.availableLots.find(lot => lot.id === lotId) || null;
    
    // For sortie/transfert operations, validate quantity against available stock
    if (this.selectedLot && (this.activeTab === 'sortie' || this.activeTab === 'transfert')) {
      const quantiteControl = this.operationForm.get('quantite');
      quantiteControl?.setValidators([
        Validators.required,
        Validators.min(1),
        Validators.max(this.selectedLot.quantiteDisponible)
      ]);
      quantiteControl?.updateValueAndValidity();
    }
    
    // For entree in existing mode, pre-fill prices from selected lot
    if (this.selectedLot && this.activeTab === 'entree' && this.entryMode === 'existing') {
      this.operationForm.patchValue({
        prixAchatUnitaire: this.selectedLot.prixAchatUnitaire,
        prixVenteUnitaire: this.selectedLot.prixVenteUnitaire
      });
    }
  }

  onEntryModeChange(mode: 'new' | 'existing'): void {
    console.log('🔄 Entry mode changed to:', mode);
    this.entryMode = mode;
    this.operationForm.patchValue({ entryMode: mode });
    this.updateEntryModeValidators();
    
    // Handle lot loading based on mode
    if (mode === 'new') {
      this.selectedLot = null;
      this.operationForm.patchValue({ stockLotId: '' });
      this.availableLots = [];
    } else {
      // Load lots when switching to 'existing' mode
      if (!this.stock) return;
      
      let articleId = '';
      let entrepotId = '';

      if (this.isLegacyStock(this.stock)) {
        articleId = this.stock.id;
        entrepotId = '';
      } else {
        articleId = this.stock.articleId || this.stock.article?.id || '';
        entrepotId = this.stock.entrepotId || '';
      }

      console.log('🔍 Mode change - articleId:', articleId, 'entrepotId:', entrepotId);
      
      if (articleId && entrepotId) {
        this.loadLotsForEntry(articleId, entrepotId);
      } else {
        console.warn('⚠️ Missing articleId or entrepotId');
      }
    }
  }

  updateEntryModeValidators(): void {
    const prixAchatControl = this.operationForm.get('prixAchatUnitaire');
    const prixVenteControl = this.operationForm.get('prixVenteUnitaire');
    const stockLotIdControl = this.operationForm.get('stockLotId');
    const dateAchatControl = this.operationForm.get('dateAchat');
    
    if (this.entryMode === 'new') {
      // Creating new lot - prices and dates are required
      prixAchatControl?.setValidators([Validators.required, Validators.min(0)]);
      prixVenteControl?.setValidators([Validators.required, Validators.min(0)]);
      dateAchatControl?.setValidators([Validators.required]);
      stockLotIdControl?.clearValidators();
    } else {
      // Adding to existing lot - lot selection is required, prices are optional
      stockLotIdControl?.setValidators([Validators.required]);
      prixAchatControl?.clearValidators();
      prixVenteControl?.clearValidators();
      dateAchatControl?.clearValidators();
    }
    
    prixAchatControl?.updateValueAndValidity();
    prixVenteControl?.updateValueAndValidity();
    stockLotIdControl?.updateValueAndValidity();
    dateAchatControl?.updateValueAndValidity();
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      // Validate file type
      const allowedTypes = ['application/pdf', 'image/jpeg', 'image/png', 'image/jpg'];
      if (!allowedTypes.includes(file.type)) {
        this.notificationService.error('Type de fichier non autorisé. Utilisez PDF, JPG ou PNG.');
        return;
      }

      // Validate file size (max 5MB)
      const maxSize = 5 * 1024 * 1024;
      if (file.size > maxSize) {
        this.notificationService.error('Le fichier est trop volumineux (max 5MB).');
        return;
      }

      this.uploadedFile = file;
    }
  }

  formatLotDisplay(lot: StockLot): string {
    return this.stockLotService.formatLotDisplayName(lot);
  }

  setActiveTab(tab: 'entree' | 'sortie' | 'ajustement' | 'transfert'): void {
    this.activeTab = tab;
    this.operationForm.reset();
    this.updateFormValidators();
    
    // Load lots when switching to entree tab with existing mode
    if (tab === 'entree' && this.entryMode === 'existing') {
      if (!this.stock) return;
      
      let articleId = '';
      let entrepotId = '';

      if (this.isLegacyStock(this.stock)) {
        articleId = this.stock.id;
        entrepotId = '';
      } else {
        articleId = this.stock.articleId || this.stock.article?.id || '';
        entrepotId = this.stock.entrepotId || '';
      }

      if (articleId && entrepotId) {
        this.loadLotsForEntry(articleId, entrepotId);
      }
    }
  }

  getSubmitButtonText(): string {
    switch (this.activeTab) {
      case 'entree': return 'Ajouter au Stock';
      case 'sortie': return 'Retirer du Stock';
      case 'ajustement': return 'Ajuster le Stock';
      case 'transfert': return 'Effectuer le Transfert';
      default: return 'Exécuter';
    }
  }

  executeOperation(): void {
    if (this.operationForm.invalid || !this.stock) return;

    this.isProcessing = true;
    const formValue = this.operationForm.value;

    switch (this.activeTab) {
      case 'entree':
        this.executeEntree(formValue);
        break;

      case 'sortie':
        this.executeSortie(formValue);
        break;

      case 'ajustement':
        this.executeAjustement(formValue);
        break;

      case 'transfert':
        this.executeTransfert(formValue);
        break;
    }
  }

  private executeEntree(formValue: any): void {
    let articleId = '';
    let entrepotId = '';

    if (this.isLegacyStock(this.stock!)) {
      articleId = this.stock!.id;
      entrepotId = '';
    } else {
      articleId = this.stock!.articleId || this.stock!.article?.id || '';
      entrepotId = this.stock!.entrepotId || '';
    }

    // Check if adding to existing lot or creating new one
    if (this.entryMode === 'existing' && formValue.stockLotId) {
      // Add to existing lot
      this.addToExistingLot(articleId, entrepotId, formValue);
    } else {
      // Create new lot
      this.createNewLot(articleId, entrepotId, formValue);
    }
  }

  private addToExistingLot(articleId: string, entrepotId: string, formValue: any): void {
    console.log('➕ Adding to existing lot:', formValue.stockLotId);
    
    // If file is uploaded, upload it first
    if (this.uploadedFile) {
      console.log('📤 Uploading invoice file for existing lot:', this.uploadedFile.name);
      this.stockLotService.uploadInvoice(this.uploadedFile).subscribe({
        next: (response) => {
          console.log('✅ File uploaded successfully:', response);
          this.submitAddToExistingLot(articleId, entrepotId, formValue, response.url);
        },
        error: (error) => {
          console.error('❌ Error uploading file:', error);
          this.isProcessing = false;
          this.notificationService.error('Erreur lors du téléchargement de la facture');
        }
      });
    } else {
      this.submitAddToExistingLot(articleId, entrepotId, formValue);
    }
  }

  private submitAddToExistingLot(articleId: string, entrepotId: string, formValue: any, factureUrl?: string): void {
    // Use prices from the selected lot
    const command: EntreeStockCommand = {
      articleId: articleId,
      destinationEntrepotId: entrepotId,
      quantite: formValue.quantite,
      typeEntree: 'INVENTAIRE_POSITIF' as any,
      reference: formValue.reference || '',
      notes: formValue.notes || '',
      stockLotId: formValue.stockLotId,
      // Use the lot's existing prices
      prixAchatUnitaire: this.selectedLot?.prixAchatUnitaire,
      prixVenteUnitaire: this.selectedLot?.prixVenteUnitaire,
      // Optional: add new invoice to existing lot
      factureUrl: factureUrl,
      numeroFacture: formValue.numeroFacture || undefined
    };
    
    this.createEntreeCommand(command);
  }

  private createNewLot(articleId: string, entrepotId: string, formValue: any): void {
    console.log('🆕 Creating new lot');
    
    // If file is uploaded, upload it first and get the URL
    if (this.uploadedFile) {
      console.log('📤 Uploading invoice file:', this.uploadedFile.name);
      this.stockLotService.uploadInvoice(this.uploadedFile).subscribe({
        next: (response) => {
          console.log('✅ File uploaded successfully:', response);
          const command = this.buildNewLotCommand(articleId, entrepotId, formValue, response.url);
          this.createEntreeCommand(command);
        },
        error: (error) => {
          console.error('❌ Erreur lors du téléchargement de la facture:', error);
          this.isProcessing = false;
          this.notificationService.error('Échec du téléchargement de la facture');
        }
      });
    } else {
      const command = this.buildNewLotCommand(articleId, entrepotId, formValue);
      this.createEntreeCommand(command);
    }
  }

  private buildNewLotCommand(articleId: string, entrepotId: string, formValue: any, factureUrl?: string): EntreeStockCommand {
    return {
      articleId: articleId,
      destinationEntrepotId: entrepotId,
      quantite: formValue.quantite,
      typeEntree: 'INVENTAIRE_POSITIF' as any,
      reference: formValue.reference || '',
      notes: formValue.notes || '',
      prixAchatUnitaire: formValue.prixAchatUnitaire,
      prixVenteUnitaire: formValue.prixVenteUnitaire,
      dateAchat: formValue.dateAchat,
      dateExpiration: formValue.dateExpiration || undefined,
      numeroFacture: formValue.numeroFacture || undefined,
      factureUrl: factureUrl
    };
  }

  private createEntreeCommand(command: EntreeStockCommand): void {
    console.log('Creating entrée with command:', command);
    this.mouvementService.createEntree(command).subscribe({
      next: (response) => {
        this.notificationService.success('Entrée de stock et lot créés avec succès');
        this.isProcessing = false;
        this.operationCompleted.emit();
        this.closeModal();
        this.resetForm();
      },
      error: (error) => {
        console.error('Erreur lors de l\'entrée:', error);
        this.notificationService.error('Erreur lors de l\'entrée de stock');
        this.isProcessing = false;
      }
    });
  }

  private executeSortie(formValue: any): void {
    let articleId = '';
    let entrepotId = '';

    if (this.isLegacyStock(this.stock!)) {
      articleId = this.stock!.id;
      entrepotId = '';
    } else {
      articleId = this.stock!.articleId || this.stock!.article?.id || '';
      entrepotId = this.stock!.entrepotId || '';
    }

    const command: SortieStockCommand = {
      articleId: articleId,
      sourceEntrepotId: entrepotId,
      quantite: formValue.quantite,
      typeSortie: 'INVENTAIRE_NEGATIF' as any,
      reference: formValue.reference || '',
      motif: formValue.motif || '',
      notes: formValue.notes || '',
      stockLotId: formValue.stockLotId
    };

    this.mouvementService.createSortie(command).subscribe({
      next: (response) => {
        this.notificationService.success('Sortie de stock effectuée avec succès');
        this.isProcessing = false;
        this.operationCompleted.emit();
        this.closeModal();
        this.resetForm();
      },
      error: (error) => {
        console.error('Erreur lors de la sortie:', error);
        const errorMsg = error.error?.message || 'Erreur lors de la sortie de stock';
        this.notificationService.error(errorMsg);
        this.isProcessing = false;
      }
    });
  }

  private resetForm(): void {
    this.operationForm.reset({
      dateAchat: new Date().toISOString().split('T')[0]
    });
    this.uploadedFile = null;
    this.selectedLot = null;
  }

  private executeAjustement(formValue: any): void {
    let articleId = '';
    let entrepotId = '';

    if (this.isLegacyStock(this.stock!)) {
      articleId = this.stock!.id;
      entrepotId = '';
    } else {
      articleId = this.stock!.articleId || this.stock!.article?.id || '';
      entrepotId = this.stock!.entrepotId || '';
    }

    const command = {
      articleId: articleId,
      entrepotId: entrepotId,
      quantiteAvant: this.getCurrentQuantity(),
      quantiteApres: formValue.nouvelleQuantite,
      raison: formValue.motif,
      notes: formValue.motif
    };

    this.ajustementService.createAjustement(command).subscribe({
      next: (response) => {
        this.notificationService.success('Ajustement de stock effectué avec succès');
        this.isProcessing = false;
        this.operationCompleted.emit();
        this.closeModal();
      },
      error: (error) => {
        console.error('Erreur lors de l\'ajustement:', error);
        this.notificationService.error('Erreur lors de l\'ajustement de stock');
        this.isProcessing = false;
      }
    });
  }

  private executeTransfert(formValue: any): void {
    let articleId = '';
    let sourceEntrepotId = '';

    if (this.isLegacyStock(this.stock!)) {
      articleId = this.stock!.id;
      sourceEntrepotId = '';
    } else {
      articleId = this.stock!.articleId || this.stock!.article?.id || '';
      sourceEntrepotId = this.stock!.entrepotId || '';
    }

    const command: TransfertStockCommand = {
      articleId: articleId,
      sourceEntrepotId: sourceEntrepotId,
      destinationEntrepotId: formValue.destinationEntrepotId,
      quantite: formValue.quantite,
      reference: formValue.reference,
      motif: formValue.motif,
      notes: formValue.notes,
      stockLotId: formValue.stockLotId || '' // Lot selection - required field
    };

    this.mouvementService.createTransfert(command).subscribe({
      next: (response) => {
        this.notificationService.success('Transfert de stock effectué avec succès');
        this.isProcessing = false;
        this.operationCompleted.emit();
        this.closeModal();
      },
      error: (error) => {
        console.error('Erreur lors du transfert:', error);
        this.notificationService.error('Erreur lors du transfert de stock');
        this.isProcessing = false;
      }
    });
  }

  closeModal(event?: Event): void {
    if (event && event.target !== event.currentTarget) return;
    this.close.emit();
  }

  // Helper methods for backward compatibility
  isLegacyStock(stock: Stock | StockLegacy | null): stock is StockLegacy {
    return stock !== null && 'nom' in stock;
  }

  getStockName(): string {
    if (!this.stock) return '';
    if (this.isLegacyStock(this.stock)) {
      return this.stock.nom;
    }
    return this.stock.article?.nom || 'Article inconnu';
  }

  getStockStatus(): string {
    if (!this.stock) return '';
    if (this.isLegacyStock(this.stock)) {
      return this.stock.statut;
    }
    // For new stock, we need to determine status based on quantity
    return this.stock.quantite > 0 ? 'Disponible' : 'Rupture';
  }

  getCurrentQuantity(): number {
    if (!this.stock) return 0;
    
    // If we have lots loaded, calculate total from lots
    if (this.availableLots && this.availableLots.length > 0) {
      return this.availableLots.reduce((total, lot) => total + (lot.quantiteDisponible || 0), 0);
    }
    
    // Fallback to stock quantity
    if (this.isLegacyStock(this.stock)) {
      return this.stock.quantiteStock;
    }
    return this.stock.quantite;
  }

  getMinQuantity(): number {
    if (!this.stock) return 0;
    if (this.isLegacyStock(this.stock)) {
      return this.stock.quantiteMinimale;
    }
    return this.stock.article?.stockMinimum || 0;
  }

  getUnitPrice(): number {
    if (!this.stock) return 0;
    if (this.isLegacyStock(this.stock)) {
      return this.stock.prixUnitaire;
    }
    return this.stock.article?.prixUnitaire || this.stock.article?.prixVente || 0;
  }

  getStatusClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'disponible':
        return 'status-available';
      case 'rupture':
      case 'rupture de stock':
        return 'status-out-of-stock';
      case 'commande':
      case 'en commande':
        return 'status-ordered';
      default:
        return 'status-unavailable';
    }
  }

  getStockDisplayInfo(stock: Stock | StockLegacy): string {
    if (this.isLegacyStock(stock)) {
      return `${stock.nom} (${stock.quantiteStock} en stock)`;
    }
    const name = stock.article?.nom || 'Article inconnu';
    return `${name} (${stock.quantite} en stock)`;
  }
}