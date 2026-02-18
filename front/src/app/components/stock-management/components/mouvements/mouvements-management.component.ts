import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  MouvementStock,
  TypeMouvement,
  Article,
  Entrepot,
  StatutMouvement,
  TypeEntree,
  TypeSortie,
  EntreeStockCommand,
  SortieStockCommand,
  TransfertStockCommand,
  RetourStockCommand,
  MouvementStockResponse,
  MouvementStockQuery
} from '../../../../models/stock.model';
import { MouvementStockService } from '../../../../services/mouvement-stock.service';
import { ArticleService } from '../../../../services/article.service';
import { EntrepotService } from '../../../../services/entrepot.service';
import { CategoryService } from '../../../../services/category.service';
import { MarqueService } from '../../../../services/marque.service';
import { NotificationService } from '../../../../services/notification.service';
import { StockLotService } from '../../../../services/stock-lot.service';
import { NotificationComponent } from '../../../shared/notification/notification.component';
import { StockLot } from '../../../../models/stock.model';

@Component({
  selector: 'app-mouvements-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, NotificationComponent],
  templateUrl: './mouvements-management.component.html',
  styleUrls: ['./mouvements-management.component.css']
})
export class MouvementsManagementComponent implements OnInit {
  mouvements: MouvementStockResponse[] = [];
  articles: Article[] = [];
  entrepots: Entrepot[] = [];
  categories: any[] = [];
  marques: any[] = [];
  availableLots: StockLot[] = [];
  selectedLot: StockLot | null = null;
  movementLot: StockLot | null = null;
  latestLot: StockLot | null = null; // Store the most recent lot for price reference
  uploadedFile: File | null = null;
  
  // Entry mode for entrée operations
  entryMode: 'new' | 'existing' = 'new';
  
  // Active tab for movement type
  activeTab: 'entree' | 'sortie' | 'transfert' | 'retour' = 'entree';
  
  // Filters
  searchMovements: string = '';
  typeFilter: TypeMouvement | '' = '';
  categoryFilter: string = '';
  marqueFilter: string = '';
  entrepotFilter: string = '';
  dateFromFilter: string = '';
  dateToFilter: string = '';
  
  // Modals
  showMovementModal: boolean = false;
  showDetailsModal: boolean = false;
  selectedMovement: MouvementStockResponse | null = null;
  
  // Forms
  entreeForm!: FormGroup;
  sortieForm!: FormGroup;
  transfertForm!: FormGroup;
  retourForm!: FormGroup;
  
  // Enums for template
  TypeEntree = TypeEntree;
  TypeSortie = TypeSortie;
  StatutMouvement = StatutMouvement;
  TypeMouvement = TypeMouvement;
  
  // Loading states
  isLoading: boolean = false;
  isSaving: boolean = false;

  // Pagination
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];

  // Make Math available in template
  Math = Math;

  constructor(
    private mouvementStockService: MouvementStockService,
    private articleService: ArticleService,
    private entrepotService: EntrepotService,
    private categoryService: CategoryService,
    private marqueService: MarqueService,
    private fb: FormBuilder,
    private notificationService: NotificationService,
    private stockLotService: StockLotService
  ) {
    this.initializeForms();
  }

  ngOnInit(): void {
    this.loadData();
  }

  private initializeForms(): void {
    // Entrée Form
    this.entreeForm = this.fb.group({
      articleId: ['', Validators.required],
      destinationEntrepotId: ['', Validators.required],
      quantite: [1, [Validators.required, Validators.min(1)]],
      typeEntree: [TypeEntree.ACHAT, Validators.required],
      reference: [''],
      numeroBonReception: [''],
      referenceBonCommande: [''],
      dateMouvement: [new Date().toISOString().split('T')[0]],
      notes: [''],
      // Batch/Lot fields
      prixAchatUnitaire: ['', [Validators.required, Validators.min(0)]],
      prixVenteUnitaire: ['', [Validators.required, Validators.min(0)]],
      dateAchat: [new Date().toISOString().split('T')[0], Validators.required],
      dateExpiration: [''],
      numeroFacture: [''],
      // Entry mode and lot selection
      entryMode: ['new'],
      stockLotId: ['']
    });

    // Sortie Form
    this.sortieForm = this.fb.group({
      articleId: ['', Validators.required],
      sourceEntrepotId: ['', Validators.required],
      quantite: [1, [Validators.required, Validators.min(1)]],
      typeSortie: [TypeSortie.VENTE, Validators.required],
      reference: [''],
      numeroBonLivraison: [''],
      referenceCommandeClient: [''],
      destinataire: [''],
      motif: [''],
      dateMouvement: [new Date().toISOString().split('T')[0]],
      notes: [''],
      // Batch/Lot field
      stockLotId: ['', Validators.required]
    });

    // Transfert Form
    this.transfertForm = this.fb.group({
      articleId: ['', Validators.required],
      sourceEntrepotId: ['', Validators.required],
      destinationEntrepotId: ['', Validators.required],
      quantite: [1, [Validators.required, Validators.min(1)]],
      reference: [''],
      motif: [''],
      dateMouvement: [new Date().toISOString().split('T')[0]],
      notes: [''],
      // Batch/Lot field
      stockLotId: ['', Validators.required]
    });

    // Retour Form
    this.retourForm = this.fb.group({
      articleId: ['', Validators.required],
      sourceEntrepotId: [''],
      destinationEntrepotId: [''],
      quantite: [1, [Validators.required, Validators.min(1)]],
      typeRetour: ['RETOUR_ENTREE', Validators.required],
      reference: [''],
      dateMouvement: [new Date().toISOString().split('T')[0]]
    });
  }

  private loadData(): void {
    this.isLoading = true;
    
    // Load articles with reasonable page size
    this.articleService.getArticles({ page: 0, size: 50 }).subscribe(
      (response) => {
        this.articles = response.content;
      },
      (error) => {
        console.error('Error loading articles:', error);
        this.articles = [];
      }
    );

    // Load entrepots
    this.entrepotService.getEntrepots().subscribe(
      (entrepots) => {
        this.entrepots = entrepots;
      },
      (error) => {
        console.error('Error loading entrepots:', error);
        this.entrepots = [];
      }
    );

    // Load categories
    this.categoryService.getCategories().subscribe(
      (categories) => {
        this.categories = categories;
      },
      (error) => {
        console.error('Error loading categories:', error);
        this.categories = [];
      }
    );

    // Load marques
    this.marqueService.getMarques().subscribe(
      (marques) => {
        this.marques = marques;
      },
      (error) => {
        console.error('Error loading marques:', error);
        this.marques = [];
      }
    );

    // Load mouvements with filters
    this.loadMouvements();
  }

  private loadMouvements(): void {
    const query: MouvementStockQuery = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: 'dateMouvement',
      sortDirection: 'DESC'
    };
    
    if (this.categoryFilter) query.categorieId = this.categoryFilter;
    if (this.marqueFilter) query.marqueId = this.marqueFilter;
    if (this.entrepotFilter) query.entrepotId = this.entrepotFilter;
    if (this.typeFilter) query.typeMouvement = this.typeFilter as TypeMouvement;
    if (this.dateFromFilter) query.startDate = new Date(this.dateFromFilter);
    if (this.dateToFilter) query.endDate = new Date(this.dateToFilter);
    if (this.searchMovements) query.reference = this.searchMovements;

    this.mouvementStockService.getMouvements(query).subscribe(
      (response) => {
        console.log('Mouvements response:', response);
        console.log('Mouvements content:', response.content);
        console.log('Mouvements length:', response.content?.length);
        this.mouvements = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;
        this.isLoading = false;
        console.log('this.mouvements after assignment:', this.mouvements);
      },
      (error) => {
        console.error('Error loading mouvements:', error);
        this.isLoading = false;
        this.mouvements = [];
        this.totalElements = 0;
        this.totalPages = 0;
      }
    );
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadMouvements();
  }

  // Tab management
  setActiveTab(tab: 'entree' | 'sortie' | 'transfert' | 'retour'): void {
    this.activeTab = tab;
  }

  // Show add movement form
  showAddMovementForm(type?: 'entree' | 'sortie' | 'transfert' | 'retour'): void {
    if (type) {
      this.activeTab = type;
    }
    this.showMovementModal = true;
  }

  // Submit forms
  submitEntree(): void {
    if (this.entreeForm.valid) {
      this.isSaving = true;
      const formValue = this.entreeForm.value;

      // Check entry mode: new lot or existing lot
      if (this.entryMode === 'existing' && formValue.stockLotId) {
        this.addToExistingLotMouvement(formValue);
      } else {
        this.createNewLotMouvement(formValue);
      }
    }
  }

  private addToExistingLotMouvement(formValue: any): void {
    // If file is uploaded, upload it first
    if (this.uploadedFile) {
      this.stockLotService.uploadInvoice(this.uploadedFile).subscribe({
        next: (response) => {
          this.submitAddToExistingLot(formValue, response.url);
        },
        error: (error) => {
          console.error('❌ Error uploading file:', error);
          this.isSaving = false;
          this.notificationService.error('Erreur lors du téléchargement de la facture');
        }
      });
    } else {
      this.submitAddToExistingLot(formValue);
    }
  }

  private submitAddToExistingLot(formValue: any, factureUrl?: string): void {
    const command: EntreeStockCommand = {
      ...formValue,
      prixAchatUnitaire: this.selectedLot?.prixAchatUnitaire,
      prixVenteUnitaire: this.selectedLot?.prixVenteUnitaire,
      factureUrl: factureUrl
    };
    this.executeEntreeCommand(command);
  }

  private createNewLotMouvement(formValue: any): void {
    // If file is uploaded, upload it first and get the URL
    if (this.uploadedFile) {
      console.log('📤 Uploading invoice file:', this.uploadedFile.name);
      this.stockLotService.uploadInvoice(this.uploadedFile).subscribe({
        next: (response) => {
          console.log('✅ File uploaded successfully:', response);
          const command: EntreeStockCommand = {
            ...formValue,
            factureUrl: response.url
          };
          console.log('📦 Creating entry with factureUrl:', command.factureUrl);
          this.executeEntreeCommand(command);
        },
        error: (error) => {
          console.error('❌ Erreur lors du téléchargement de la facture:', error);
          this.isSaving = false;
          this.notificationService.error('Échec du téléchargement de la facture');
        }
      });
    } else {
      console.log('ℹ️ No file uploaded, proceeding without factureUrl');
      const command: EntreeStockCommand = formValue;
      this.executeEntreeCommand(command);
    }
  }

  private executeEntreeCommand(command: EntreeStockCommand): void {
    this.mouvementStockService.createEntree(command).subscribe(
      (response) => {
        this.isSaving = false;
        this.closeMovementModal();
        this.notificationService.success('Entrée de stock créée avec succès');
        this.loadData();
        this.uploadedFile = null; // Clear uploaded file
      },
      (error) => {
        console.error('Erreur lors de la création de l\'entrée:', error);
        this.isSaving = false;
        const errorMessage = error.error?.message || 'Erreur lors de la création de l\'entrée de stock';
        this.notificationService.error(errorMessage);
      }
    );
  }

  submitSortie(): void {
    if (this.sortieForm.valid) {
      this.isSaving = true;
      const command: SortieStockCommand = this.sortieForm.value;
      
      this.mouvementStockService.createSortie(command).subscribe(
        (response) => {
          this.isSaving = false;
          this.closeMovementModal();
          this.notificationService.success('Sortie de stock créée avec succès');
          this.loadData();
        },
        (error) => {
          console.error('Erreur lors de la création de la sortie:', error);
          this.isSaving = false;
        }
      );
    }
  }

  submitTransfert(): void {
    if (this.transfertForm.valid) {
      this.isSaving = true;
      const command: TransfertStockCommand = this.transfertForm.value;
      
      this.mouvementStockService.createTransfert(command).subscribe(
        (response) => {
          this.isSaving = false;
          this.closeMovementModal();
          this.notificationService.success('Transfert de stock créé avec succès');
          this.loadData();
        },
        (error) => {
          console.error('Erreur lors de la création du transfert:', error);
          this.isSaving = false;
        }
      );
    }
  }

  submitRetour(): void {
    if (this.retourForm.valid) {
      this.isSaving = true;
      const command: RetourStockCommand = this.retourForm.value;
      
      this.mouvementStockService.createRetour(command).subscribe(
        (response) => {
          this.isSaving = false;
          this.closeMovementModal();
          this.notificationService.success('Retour de stock créé avec succès');
          this.loadData();
        },
        (error) => {
          console.error('Erreur lors de la création du retour:', error);
          this.isSaving = false;
        }
      );
    }
  }

  getMovementsByType(type: string): MouvementStockResponse[] {
    const result = this.mouvements?.filter(m => m.typeMouvement === type) || [];
    console.log(`getMovementsByType(${type}):`, result.length, 'movements');
    return result;
  }

  getFilteredMovements(): MouvementStockResponse[] {
    const result = this.mouvements || [];
    console.log('getFilteredMovements():', result.length, 'movements', result);
    return result;
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadMouvements();
    }
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.loadMouvements();
  }

  get pages(): number[] {
    const pages: number[] = [];
    for (let i = 0; i < this.totalPages; i++) {
      pages.push(i);
    }
    return pages;
  }

  getPageNumbersOld(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    let startPage = Math.max(1, this.currentPage - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(this.totalPages, startPage + maxPagesToShow - 1);
    
    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(1, endPage - maxPagesToShow + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }

  getTypeClass(type: TypeMouvement): string {
    switch (type) {
      case TypeMouvement.ENTREE: return 'entree';
      case TypeMouvement.SORTIE: return 'sortie';
      case TypeMouvement.AJUSTEMENT: return 'ajustement';
      case TypeMouvement.TRANSFERT: return 'transfert';
      case TypeMouvement.RETOUR_ENTREE:
      case TypeMouvement.RETOUR_SORTIE: return 'retour';
      default: return '';
    }
  }

  getTypeIcon(type: TypeMouvement): string {
    switch (type) {
      case TypeMouvement.ENTREE: return '📥';
      case TypeMouvement.SORTIE: return '📤';
      case TypeMouvement.AJUSTEMENT: return '⚖️';
      case TypeMouvement.TRANSFERT: return '🔄';
      case TypeMouvement.RETOUR_ENTREE:
      case TypeMouvement.RETOUR_SORTIE: return '↩️';
      default: return '📦';
    }
  }

  getTypeText(type: TypeMouvement): string {
    switch (type) {
      case TypeMouvement.ENTREE: return 'Entrée';
      case TypeMouvement.SORTIE: return 'Sortie';
      case TypeMouvement.AJUSTEMENT: return 'Ajustement';
      case TypeMouvement.TRANSFERT: return 'Transfert';
      case TypeMouvement.RETOUR_ENTREE: return 'Retour Entrée';
      case TypeMouvement.RETOUR_SORTIE: return 'Retour Sortie';
      default: return 'Inconnu';
    }
  }

  closeMovementModal(): void {
    this.showMovementModal = false;
    this.entreeForm.reset();
    this.sortieForm.reset();
    this.transfertForm.reset();
    this.retourForm.reset();
    this.latestLot = null; // Reset the latest lot reference
    this.selectedLot = null;
    this.availableLots = [];
  }

  viewMovement(mouvement: MouvementStockResponse): void {
    this.selectedMovement = mouvement;
    this.showDetailsModal = true;
    
    // Load lot details if stockLotId exists
    if (mouvement.stockLotId) {
      this.loadMovementLot(mouvement.stockLotId);
    } else {
      this.movementLot = null;
    }
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedMovement = null;
    this.movementLot = null;
  }

  exportMovements(): void {
    this.mouvementStockService.exportToCsv(this.mouvements);
  }

  validateMouvement(id: string): void {
    this.mouvementStockService.validateMouvement(id).subscribe(
      () => {
        this.loadData();
      },
      (error) => {
        console.error('Erreur lors de la validation:', error);
      }
    );
  }

  // Batch/Lot management methods
  loadAvailableLots(articleId: string, entrepotId: string): void {
    if (!articleId || !entrepotId) {
      this.availableLots = [];
      return;
    }

    this.stockLotService.getAvailableStockLots(articleId, entrepotId).subscribe({
      next: (lots) => {
        this.availableLots = lots;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des lots:', error);
        this.availableLots = [];
      }
    });
  }

  onSortieArticleChange(articleId: string): void {
    const entrepotId = this.sortieForm.get('sourceEntrepotId')?.value;
    if (articleId && entrepotId) {
      this.loadAvailableLots(articleId, entrepotId);
    }
  }

  onSortieEntrepotChange(entrepotId: string): void {
    const articleId = this.sortieForm.get('articleId')?.value;
    if (articleId && entrepotId) {
      this.loadAvailableLots(articleId, entrepotId);
    }
  }
  
  onTransfertArticleChange(articleId: string): void {
    console.log('Transfer article changed:', articleId);
    const entrepotId = this.transfertForm.get('sourceEntrepotId')?.value;
    console.log('Current source entrepot:', entrepotId);
    if (articleId && entrepotId) {
      this.loadAvailableLots(articleId, entrepotId);
    } else {
      console.log('Not loading lots - missing article or entrepot');
    }
  }

  onTransfertEntrepotChange(entrepotId: string): void {
    console.log('Transfer entrepot changed:', entrepotId);
    const articleId = this.transfertForm.get('articleId')?.value;
    console.log('Current article:', articleId);
    if (articleId && entrepotId) {
      this.loadAvailableLots(articleId, entrepotId);
    } else {
      console.log('Not loading lots - missing article or entrepot');
    }
  }

  onLotSelected(lotId: string): void {
    this.selectedLot = this.availableLots.find(lot => lot.id === lotId) || null;
    
    // For sortie/transfert - validate quantity
    if (this.selectedLot && (this.activeTab === 'sortie' || this.activeTab === 'transfert')) {
      const quantiteControl = this.sortieForm.get('quantite');
      quantiteControl?.setValidators([
        Validators.required,
        Validators.min(1),
        Validators.max(this.selectedLot.quantiteDisponible)
      ]);
      quantiteControl?.updateValueAndValidity();
    }
    
    // For entree in existing mode - pre-fill prices
    if (this.selectedLot && this.activeTab === 'entree' && this.entryMode === 'existing') {
      this.entreeForm.patchValue({
        prixAchatUnitaire: this.selectedLot.prixAchatUnitaire,
        prixVenteUnitaire: this.selectedLot.prixVenteUnitaire
      });
    }
  }

  onEntryModeChange(mode: 'new' | 'existing'): void {
    this.entryMode = mode;
    this.entreeForm.patchValue({ entryMode: mode });
    this.updateEntryModeValidators();
    
    // Load lots when switching to existing mode
    if (mode === 'new') {
      this.selectedLot = null;
      this.entreeForm.patchValue({ stockLotId: '' });
      this.availableLots = [];
    } else {
      const articleId = this.entreeForm.get('articleId')?.value;
      const entrepotId = this.entreeForm.get('destinationEntrepotId')?.value;
      
      if (articleId && entrepotId) {
        this.loadAvailableLots(articleId, entrepotId);
      }
    }
  }

  updateEntryModeValidators(): void {
    const prixAchatControl = this.entreeForm.get('prixAchatUnitaire');
    const prixVenteControl = this.entreeForm.get('prixVenteUnitaire');
    const stockLotIdControl = this.entreeForm.get('stockLotId');
    const dateAchatControl = this.entreeForm.get('dateAchat');
    
    if (this.entryMode === 'new') {
      // Creating new lot - prices and dates are required
      prixAchatControl?.setValidators([Validators.required, Validators.min(0)]);
      prixVenteControl?.setValidators([Validators.required, Validators.min(0)]);
      dateAchatControl?.setValidators([Validators.required]);
      stockLotIdControl?.clearValidators();
    } else {
      // Adding to existing lot - lot selection is required
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

  onEntreeArticleOrEntrepotChange(): void {
    const articleId = this.entreeForm.get('articleId')?.value;
    const entrepotId = this.entreeForm.get('destinationEntrepotId')?.value;
    
    // Always load the latest lot for price reference when article changes
    if (articleId) {
      this.loadLatestLotForPriceReference(articleId);
    } else {
      this.latestLot = null;
    }
    
    // Only load lots if in existing mode
    if (this.entryMode === 'existing' && articleId && entrepotId) {
      this.loadAvailableLots(articleId, entrepotId);
    }
  }

  /**
   * Load the latest lot to display previous prices as a reference for the user
   */
  private loadLatestLotForPriceReference(articleId: string): void {
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

  formatLotDisplay(lot: StockLot): string {
    return this.stockLotService.formatLotDisplayName(lot);
  }
  
  getSelectedLotMaxQuantity(lotId: string): number {
    const lot = this.availableLots.find(l => l.id === lotId);
    return lot ? lot.quantiteDisponible : 999999;
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      console.log('📁 File selected:', file.name, 'Type:', file.type, 'Size:', file.size);
      const allowedTypes = ['application/pdf', 'image/jpeg', 'image/png', 'image/jpg'];
      if (!allowedTypes.includes(file.type)) {
        this.notificationService.error('Type de fichier non autorisé. Utilisez PDF, JPG ou PNG.');
        return;
      }

      const maxSize = 5 * 1024 * 1024;
      if (file.size > maxSize) {
        this.notificationService.error('Le fichier est trop volumineux (max 5MB).');
        return;
      }

      this.uploadedFile = file;
      console.log('✅ File accepted and stored in uploadedFile');
    }
  }

  loadMovementLot(lotId: string): void {
    this.stockLotService.getStockLotById(lotId).subscribe({
      next: (lot) => {
        this.movementLot = lot;
      },
      error: (error) => {
        // Silently handle 404 for old movements without lot info
        this.movementLot = null;
      }
    });
  }

  downloadInvoice(url: string): void {
    if (url) {
      // If URL is relative, prepend the API base URL
      const fullUrl = url.startsWith('http') ? url : `http://localhost:8080${url}`;
      console.log('📥 Downloading invoice from:', fullUrl);
      window.open(fullUrl, '_blank');
    } else {
      this.notificationService.error('Aucune facture disponible');
    }
  }

  isExpiringSoon(lot: any): boolean {
    if (!lot.dateExpiration) return false;
    const today = new Date();
    const expirationDate = new Date(lot.dateExpiration);
    const daysUntilExpiration = Math.floor((expirationDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
    return daysUntilExpiration > 0 && daysUntilExpiration <= 30;
  }

  isExpired(lot: any): boolean {
    if (!lot.dateExpiration) return false;
    const today = new Date();
    const expirationDate = new Date(lot.dateExpiration);
    return expirationDate < today;
  }

  cancelMouvement(id: string): void {
    if (confirm('Êtes-vous sûr de vouloir annuler ce mouvement ?')) {
      this.mouvementStockService.cancelMouvement(id).subscribe(
        () => {
          this.loadData();
        },
        (error) => {
          console.error('Erreur lors de l\'annulation:', error);
        }
      );
    }
  }
}