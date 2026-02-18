import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StockOperationsComponent } from '../stock-operations/stock-operations.component';
import { StockTraceComponent } from '../stock-trace/stock-trace.component';
import {
  Article,
  Stock,
  StockLegacy,
  StockEntrepot,
  Entrepot,
  MouvementStock,
  TypeMouvement,
  CategorieStock,
  CategorieArticle,
  StatutStock,
  AddStockCommand,
  UpdateStockCommand,
  StockResponse,
  AddArticleCommand
} from '../../../../models/stock.model';
import { Fournisseur } from '../../../../models/fournisseur/fournisseur.model';

import { HttpClientModule } from '@angular/common/http';
import { ArticleService } from '../../../../services/article.service';
import { EntrepotService } from '../../../../services/entrepot.service';
import { StockService } from '../../../../services/stock.service';
import { FournisseurService } from '../../../../services/fournisseur.service';
import { CategoryService } from '../../../../services/category.service';
import { MarqueService } from '../../../../services/marque.service';
import { NotificationService } from '../../../../services/notification.service';
import { StockLotService } from '../../../../services/stock-lot.service';
import { StockLot } from '../../../../models/stock.model';

@Component({
  selector: 'app-stock-management-view',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, StockOperationsComponent, StockTraceComponent],
  templateUrl: './stock-management-view.component.html',
  styleUrls: ['./stock-management-view.component.css']
})
export class StockManagementViewComponent implements OnInit {
  // Stock entities (main stock items)
  stockItems: Stock[] = [];

  // Stock warehouse view
  stocks: StockEntrepot[] = [];

  entrepots: Entrepot[] = [];
  articles: Article[] = [];
  categories: CategorieArticle[] = [];
  marques: any[] = [];
  fournisseurs: Fournisseur[] = [];
  filteredFournisseurs: Fournisseur[] = [];

  searchStock: string = '';
  entrepotFilter: string = '';
  alertFilter: string = '';
  categoryFilter: string = '';
  marqueFilter: string = '';
  fournisseurFilter: string = '';
  fournisseurSearch: string = '';
  showFournisseurDropdown: boolean = false;
  selectedFournisseur: Fournisseur | null = null;

  showAdjustModal: boolean = false;
  showAddModal: boolean = false;
  selectedStock: StockEntrepot | null = null;
  selectedStockItem: Stock | null = null;
  stockLots: StockLot[] = [];
  
  // Cache for lot quantities per stock (key: articleId-entrepotId, value: total quantity)
  lotQuantityCache: Map<string, number> = new Map();

  // Article selection mode
  isNewArticle: boolean = true;
  selectedArticleId: string = '';
  selectedEntrepotId: string = '';

  adjustmentData: any = {
    type: '',
    quantity: 0,
    reason: '',
    comment: ''
  };

  newStockData: any = {
    nom: '',
    description: '',
    categorie: undefined,
    quantiteStock: 0,
    quantiteMinimale: 0,
    quantiteMaximale: 0,
    prixUnitaire: 0,
    fournisseur: '',
    emplacement: '',
    codeBarres: '',
    statut: StatutStock.DISPONIBLE,
    // Lot/Batch information
    prixAchatUnitaire: null,
    prixVenteUnitaire: null,
    dateAchat: new Date().toISOString().split('T')[0],
    dateExpirationLot: '',
    numeroFacture: ''
  };

  // File upload for facture
  uploadedFactureFile: File | null = null;

  // Stock operations modal
  showOperationsModal: boolean = false;
  selectedStockForOperations: Stock | null = null;

  // Stock trace modal
  showTraceModal: boolean = false;
  selectedStockForTrace: string = '';

  // Stock details modal
  showDetailsModal: boolean = false;

  // View mode
  currentStockView: 'list' | 'trace' = 'list';

  loading: boolean = false;
  error: string = '';

  // Pagination
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];

  // Make Math available in template
  Math = Math;

  constructor(
    private stockService: StockService,
    private articleService: ArticleService,
    private entrepotService: EntrepotService,
    private fournisseurService: FournisseurService,
    private categoryService: CategoryService,
    private marqueService: MarqueService,
    private notificationService: NotificationService,
    private stockLotService: StockLotService
  ) { }

  ngOnInit(): void {
    this.loadStocks();
    // Load only metadata for filters, not full datasets
    this.loadEntrepots();
    this.loadCategories();
    this.loadMarques();
    this.loadFournisseurs();
    // Articles will be loaded on-demand when needed for the modal
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    const clickedInside = target.closest('.fournisseur-select-container');
    
    if (!clickedInside && this.showFournisseurDropdown) {
      this.showFournisseurDropdown = false;
    }
  }

  loadStocks(): void {
    this.loading = true;
    this.error = '';

    const query: any = {
      page: this.currentPage,
      size: this.pageSize
    };
    
    if (this.searchStock) query.searchTerm = this.searchStock;
    if (this.categoryFilter) query.categorieId = this.categoryFilter;
    if (this.marqueFilter) query.marqueId = this.marqueFilter;
    if (this.fournisseurFilter) query.fournisseurId = this.fournisseurFilter;
    if (this.entrepotFilter) query.entrepotId = this.entrepotFilter;

    this.stockService.getAllStocks(query).subscribe({
      next: (response) => {
        this.stockItems = response.content.map(stockResponse => this.stockService.mapStockResponseToStock(stockResponse));
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;
        this.loading = false;
        // Load lot quantities for all stocks
        this.loadAllStockLotQuantities();
      },
      error: (error) => {
        this.error = 'Erreur lors du chargement des stocks';
        this.loading = false;
        this.stockItems = [];
        this.totalElements = 0;
        this.totalPages = 0;
        console.error('Error loading stocks:', error);
      }
    });
  }

  private loadArticles(): void {
    // Only load articles when needed (e.g., for dropdown in modal)
    // Use a reasonable page size instead of loading all articles
    this.articleService.getArticles({ page: 0, size: 50 }).subscribe({
      next: (response) => {
        this.articles = response.content;
      },
      error: (error) => {
        console.error('Error loading articles:', error);
      }
    });
  }

  private loadEntrepots(): void {
    this.entrepotService.getEntrepots().subscribe({
      next: (entrepots) => {
        this.entrepots = entrepots;
      },
      error: (error) => {
        console.error('Error loading entrepots:', error);
      }
    });
  }

  private loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
        this.categories = [];
      }
    });
  }

  private loadMarques(): void {
    this.marqueService.getMarques().subscribe({
      next: (marques) => {
        this.marques = marques;
      },
      error: (error) => {
        console.error('Error loading marques:', error);
        this.marques = [];
      }
    });
  }

  onSearch(): void {
    // Reload stocks from backend with filters instead of client-side filtering
    this.loadStocks();
  }

  getFilteredStocks(): StockEntrepot[] {
    let filtered = [...this.stocks];

    if (this.searchStock.trim()) {
      filtered = filtered.filter(stock =>
        stock.article?.nom.toLowerCase().includes(this.searchStock.toLowerCase()) ||
        stock.article?.code?.toLowerCase().includes(this.searchStock.toLowerCase())
      );
    }

    if (this.entrepotFilter) {
      filtered = filtered.filter(stock => stock.entrepotId === this.entrepotFilter);
    }

    if (this.categoryFilter) {
      filtered = filtered.filter(stock => stock.article?.categorieId === this.categoryFilter);
    }

    if (this.marqueFilter) {
      filtered = filtered.filter(stock => stock.article?.marqueId === this.marqueFilter);
    }

    if (this.alertFilter) {
      filtered = filtered.filter(stock => {
        if (this.alertFilter === 'low') return this.isLowStock(stock);
        if (this.alertFilter === 'out') return this.isOutOfStock(stock);
        if (this.alertFilter === 'excess') return this.isExcessStock(stock);
        return true;
      });
    }

    return filtered;
  }

  isLowStock(stock: StockEntrepot): boolean {
    return stock.quantite <= stock.stockMinimum;
  }

  isOutOfStock(stock: StockEntrepot): boolean {
    return stock.quantite === 0;
  }

  isExcessStock(stock: StockEntrepot): boolean {
    return stock.quantite > stock.stockMaximum;
  }

  getStockValue(stock: StockEntrepot): number {
    return stock.quantite * (stock.article?.prixAchat || 0);
  }

  getStockStatusClass(stock: StockEntrepot): string {
    if (this.isOutOfStock(stock)) return 'out';
    if (this.isLowStock(stock)) return 'low';
    if (this.isExcessStock(stock)) return 'excess';
    return 'normal';
  }

  getStockStatusText(stock: StockEntrepot): string {
    if (this.isOutOfStock(stock)) return 'Rupture';
    if (this.isLowStock(stock)) return 'Stock faible';
    if (this.isExcessStock(stock)) return 'Stock excessif';
    return 'Normal';
  }

  showAdjustmentModal(): void {
    this.showAdjustModal = true;
  }

  adjustStock(stock: StockEntrepot): void {
    this.selectedStock = stock;
    this.adjustmentData = {
      type: '',
      quantity: 0,
      reason: '',
      comment: ''
    };
    this.showAdjustModal = true;
  }

  viewMovements(stock: StockEntrepot): void {
    // Navigate to movements view for this stock
    console.log('View movements for stock:', stock);
  }

  saveAdjustment(): void {
    if (this.selectedStock && this.adjustmentData.type && this.adjustmentData.quantity !== null) {
      // Calculate new quantity
      let newQuantity = this.selectedStock.quantite;
      switch (this.adjustmentData.type) {
        case 'increase':
          newQuantity += this.adjustmentData.quantity;
          break;
        case 'decrease':
          newQuantity = Math.max(0, newQuantity - this.adjustmentData.quantity);
          break;
        case 'set':
          newQuantity = this.adjustmentData.quantity;
          break;
      }

      // Update stock
      // Update stock via API
      const updateCommand: UpdateStockCommand = {
        id: this.selectedStock.id,
        quantite: newQuantity,
        updatedBy: 'admin' // Should be current user
      };

      this.loading = true;
      this.stockService.updateStock(this.selectedStock.id, updateCommand).subscribe({
        next: () => {
          console.log('Stock adjustment saved');
          this.loading = false;
          this.closeAdjustModal();
          this.loadStocks(); // Reload data
        },
        error: (error) => {
          console.error('Error saving adjustment:', error);
          this.loading = false;
          this.notificationService.error('Erreur lors de l\'ajustement du stock');
        }
      });
    }
  }

  closeAdjustModal(): void {
    this.showAdjustModal = false;
    this.selectedStock = null;
    this.adjustmentData = {
      type: '',
      quantity: 0,
      reason: '',
      comment: ''
    };
  }

  exportStocks(): void {
    // Export functionality
    console.log('Exporting stocks...');
  }

  // Stock entity methods
  getFilteredStockItems(): Stock[] {
    // Apply client-side alert filter only
    if (this.alertFilter) {
      return this.stockItems.filter(stock => {
        if (this.alertFilter === 'low') return this.isLowStockItem(stock);
        if (this.alertFilter === 'out') return this.isOutOfStockItem(stock);
        if (this.alertFilter === 'excess') return this.isExcessStockItem(stock);
        return true;
      });
    }
    return this.stockItems;
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadStocks();
    }
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.loadStocks();
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

  isLowStockItem(stock: Stock): boolean {
    return this.getStockQuantity(stock) <= this.getStockMin(stock);
  }

  isOutOfStockItem(stock: Stock): boolean {
    return this.getStockQuantity(stock) === 0;
  }

  isExcessStockItem(stock: Stock): boolean {
    return this.getStockQuantity(stock) > this.getStockMax(stock);
  }

  getStockItemValue(stock: Stock): number {
    // First check if backend provides lot-based total value
    if (stock.valeurTotaleStock !== undefined && stock.valeurTotaleStock > 0) {
      return stock.valeurTotaleStock;
    }
    // If we have lots loaded (in details modal), calculate total value from lots
    if (this.selectedStockItem === stock && this.stockLots.length > 0) {
      return this.stockLots.reduce((total, lot) => {
        const lotValue = (lot.quantiteActuelle || 0) * (lot.prixAchatUnitaire || 0);
        return total + lotValue;
      }, 0);
    }
    // Fallback to calculated value from quantity * price
    return this.getStockQuantity(stock) * this.getStockPrice(stock);
  }

  getStockItemStatus(stock: Stock): string {
    if (this.isOutOfStockItem(stock)) return 'Rupture';
    if (this.isLowStockItem(stock)) return 'Stock faible';
    if (this.isExcessStockItem(stock)) return 'Stock excessif';
    return 'Normal';
  }

  getStockItemStatusClass(stock: Stock): string {
    if (this.isOutOfStockItem(stock)) return 'status-danger';
    if (this.isLowStockItem(stock)) return 'status-warning';
    if (this.isExcessStockItem(stock)) return 'status-info';
    return 'status-success';
  }

  adjustStockItem(stock: Stock): void {
    this.selectedStockItem = stock;
    this.showAdjustmentModal();
  }

  updateStockItemSearch(): void {
    this.currentPage = 1;
    this.loadStocks();
  }

  // Add Stock Modal Methods
  showAddStockModal(): void {
    this.showAddModal = true;
    this.isNewArticle = true;
    this.resetNewStockData();
    // Load articles only when modal is opened
    if (this.articles.length === 0) {
      this.loadArticles();
    }
  }

  closeAddStockModal(): void {
    this.showAddModal = false;
    this.resetNewStockData();
    this.uploadedFactureFile = null;
  }

  setArticleMode(isNew: boolean): void {
    this.isNewArticle = isNew;
    if (!isNew) {
      this.resetNewStockData();
    }
  }

  onArticleSelected(): void {
    if (this.selectedArticleId) {
      const selectedArticle = this.articles.find(a => a.id === this.selectedArticleId);
      if (selectedArticle) {
        // For existing articles, only update display fields and keep stock-specific data
        this.newStockData.nom = selectedArticle.nom;
        this.newStockData.description = selectedArticle.description || '';
        this.newStockData.prixUnitaire = selectedArticle.prixVente || selectedArticle.prixAchat || 0;
        this.newStockData.codeBarres = selectedArticle.codebare || selectedArticle.sku || '';
        // Keep current stock quantity and location as entered by user
      }
    }
  }

  resetNewStockData(): void {
    this.newStockData = {
      nom: '',
      description: '',
      categorie: undefined,
      quantiteStock: 0,
      quantiteMinimale: 0,
      quantiteMaximale: 0,
      prixUnitaire: 0,
      fournisseur: '',
      emplacement: '',
      codeBarres: '',
      statut: StatutStock.DISPONIBLE,
      // Lot/Batch information
      prixAchatUnitaire: null,
      prixVenteUnitaire: null,
      dateAchat: new Date().toISOString().split('T')[0],
      dateExpirationLot: '',
      numeroFacture: ''
    };
    this.selectedArticleId = '';
    this.selectedEntrepotId = '';
    this.uploadedFactureFile = null;
  }

  addNewStock(): void {
    // Validate numeric fields before proceeding
    if (this.newStockData.quantiteStock !== undefined && this.newStockData.quantiteStock < 0) {
      this.notificationService.error('La quantité ne peut pas être négative');
      return;
    }
    if (this.isNewArticle) {
      if (this.newStockData.quantiteMinimale !== undefined && this.newStockData.quantiteMinimale < 0) {
        this.notificationService.error('Le stock minimum ne peut pas être négatif');
        return;
      }
      if (this.newStockData.quantiteMaximale !== undefined && this.newStockData.quantiteMaximale < 0) {
        this.notificationService.error('Le stock maximum ne peut pas être négatif');
        return;
      }
      if (this.newStockData.prixUnitaire !== undefined && this.newStockData.prixUnitaire < 0) {
        this.notificationService.error('Le prix unitaire ne peut pas être négatif');
        return;
      }
      if (this.newStockData.quantiteMaximale !== undefined && 
          this.newStockData.quantiteMinimale !== undefined && 
          this.newStockData.quantiteMaximale < this.newStockData.quantiteMinimale) {
        this.notificationService.error('Le stock maximum doit être supérieur ou égal au stock minimum');
        return;
      }
    }

    if (this.isValidNewStock()) {
      this.loading = true;
      
      if (this.isNewArticle) {
        // First create the article, then create stock with that article ID
        this.createArticleAndStock();
      } else {
        // Use existing article ID to create stock
        this.createStockWithExistingArticle();
      }
    } else {
      this.notificationService.error('Veuillez remplir tous les champs obligatoires');
    }
  }

  private createArticleAndStock(): void {
    // Find the selected category ID
    const selectedCategory = this.categories.find(cat => cat.nom === this.newStockData.categorie);
    const categorieId = selectedCategory?.id;

    // Create the article first
    const addArticleCommand: AddArticleCommand = {
      nom: this.newStockData.nom,
      description: this.newStockData.description,
      codebare: this.newStockData.codeBarres,
      categorieId: categorieId,
      prixAchat: this.newStockData.prixUnitaire,
      prixVente: this.newStockData.prixUnitaire * 1.2, // Default margin
      stockMinimum: this.newStockData.quantiteMinimale,
      stockMaximum: this.newStockData.quantiteMaximale,
      estActif: true
    };

    this.articleService.addArticle(addArticleCommand).subscribe({
      next: (articleResponse) => {
        // Now create stock with the new article ID
        this.createStock(articleResponse.id);
      },
      error: (error) => {
        console.error('Error creating article:', error);
        this.loading = false;
        const errorMessage = error.error?.error || error.message || 'Erreur lors de la création de l\'article';
        this.notificationService.error(`Erreur lors de la création de l'article: ${errorMessage}`);
      }
    });
  }

  private createStockWithExistingArticle(): void {
    if (!this.selectedArticleId) {
      this.loading = false;
      this.notificationService.error('Veuillez sélectionner un article existant');
      return;
    }
    this.createStock(this.selectedArticleId);
  }

  private createStock(articleId: string): void {
    // If file is uploaded, upload it first
    if (this.uploadedFactureFile && this.newStockData.quantiteStock > 0) {
      this.stockLotService.uploadInvoice(this.uploadedFactureFile).subscribe({
        next: (response) => {
          console.log('✅ File uploaded successfully:', response);
          this.submitCreateStock(articleId, response.url);
        },
        error: (error) => {
          console.error('❌ Error uploading file:', error);
          this.loading = false;
          this.notificationService.error('Erreur lors du téléchargement de la facture');
        }
      });
    } else {
      this.submitCreateStock(articleId);
    }
  }

  private submitCreateStock(articleId: string, factureUrl?: string): void {
    const addCommand: AddStockCommand = {
      articleId: articleId,
      entrepotId: this.selectedEntrepotId,
      fournisseurId: this.selectedFournisseur?.id,
      quantite: this.newStockData.quantiteStock || 0,
      dateDexpiration: new Date(),
      createdBy: 'admin' // TODO: Get from auth service
    };

    // Add lot information if quantity > 0 and prices are provided
    if (this.newStockData.quantiteStock > 0 && this.newStockData.prixAchatUnitaire && this.newStockData.prixVenteUnitaire) {
      addCommand.prixAchatUnitaire = this.newStockData.prixAchatUnitaire;
      addCommand.prixVenteUnitaire = this.newStockData.prixVenteUnitaire;
      addCommand.dateAchat = this.newStockData.dateAchat;
      addCommand.dateExpiration = this.newStockData.dateExpirationLot || undefined;
      addCommand.numeroFacture = this.newStockData.numeroFacture || undefined;
      addCommand.factureUrl = factureUrl || undefined;
    }

    this.stockService.addStock(addCommand).subscribe({
      next: (response) => {
        console.log('New stock added:', response);
        this.loading = false;
        this.closeAddStockModal();
        this.loadStocks(); // Reload data
        this.notificationService.success('Stock ajouté avec succès!');
      },
      error: (error) => {
        console.error('Error adding stock:', error);
        this.loading = false;
        const errorMessage = error.error?.error || error.message || 'Erreur lors de l\'ajout du stock';
        this.notificationService.error(`Erreur lors de l'ajout du stock: ${errorMessage}`);
      }
    });
  }

  // File selection handler for facture upload
  onFactureFileSelected(event: any): void {
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

      this.uploadedFactureFile = file;
    }
  }

  isFormValidForSubmission(): boolean {
    return this.isValidNewStock();
  }

  private isValidNewStock(): boolean {
    // Common validation for stock quantity and warehouse
    const commonValidation = !!(
      this.newStockData.quantiteStock !== undefined &&
      this.newStockData.quantiteStock >= 0 &&
      this.selectedEntrepotId
    );

    // If quantity > 0, lot prices are required
    let lotValidation = true;
    if (this.newStockData.quantiteStock > 0) {
      lotValidation = !!(
        this.newStockData.prixAchatUnitaire !== null &&
        this.newStockData.prixAchatUnitaire !== undefined &&
        this.newStockData.prixAchatUnitaire >= 0 &&
        this.newStockData.prixVenteUnitaire !== null &&
        this.newStockData.prixVenteUnitaire !== undefined &&
        this.newStockData.prixVenteUnitaire >= 0
      );
    }

    if (this.isNewArticle) {
      // Additional validation for new articles
      const newArticleValidation = !!(
        this.newStockData.nom &&
        this.newStockData.categorie &&
        this.newStockData.quantiteMinimale !== undefined &&
        this.newStockData.quantiteMaximale !== undefined &&
        this.newStockData.prixUnitaire !== undefined &&
        this.newStockData.quantiteMinimale >= 0 &&
        this.newStockData.quantiteMaximale >= 0 &&
        this.newStockData.prixUnitaire >= 0 &&
        this.newStockData.quantiteMaximale >= this.newStockData.quantiteMinimale
      );
      return commonValidation && newArticleValidation && lotValidation;
    } else {
      // For existing articles, only need article selection
      return commonValidation && !!this.selectedArticleId && lotValidation;
    }
  }

  private generateStockId(): string {
    return 'STK-' + Date.now().toString();
  }

  // Stock Operations Methods
  openOperationsModal(stock: Stock): void {
    this.selectedStockForOperations = stock;
    this.showOperationsModal = true;
  }

  closeOperationsModal(): void {
    this.showOperationsModal = false;
    this.selectedStockForOperations = null;
  }

  onOperationCompleted(): void {
    // Reload data after operation
    this.loadStocks();
    // Refresh UI or show success message
    console.log('Stock operation completed successfully');
  }

  // Stock Trace Methods
  openTraceModal(stockId: string): void {
    this.selectedStockForTrace = stockId;
    this.showTraceModal = true;
  }

  closeTraceModal(): void {
    this.showTraceModal = false;
    this.selectedStockForTrace = '';
  }

  viewStockDetails(stock: Stock): void {
    this.selectedStockItem = stock;
    this.showDetailsModal = true;
    
    // Load lots for this stock (article + warehouse)
    if (stock.article?.id && stock.entrepot?.id) {
      this.loadStockLots(stock.article.id, stock.entrepot.id);
    }
  }

  loadAllStockLotQuantities(): void {
    // Load lot quantities for all stocks in current page
    this.stockItems.forEach(stock => {
      if (stock.article?.id && stock.entrepot?.id) {
        const cacheKey = `${stock.article.id}-${stock.entrepot.id}`;
        this.stockLotService.getStockLots({ 
          articleId: stock.article.id, 
          entrepotId: stock.entrepot.id
        }).subscribe({
          next: (lots) => {
            const totalQuantity = lots.reduce((sum, lot) => sum + (lot.quantiteActuelle || 0), 0);
            this.lotQuantityCache.set(cacheKey, totalQuantity);
          },
          error: (error) => {
            console.error('Error loading lots for stock:', error);
          }
        });
      }
    });
  }

  loadStockLots(articleId: string, entrepotId: string): void {
    this.stockLotService.getStockLots({ 
      articleId, 
      entrepotId
      // Load all lots to calculate total quantity
    }).subscribe({
      next: (lots: StockLot[]) => {
        this.stockLots = lots;
      },
      error: (error: any) => {
        console.error('Error loading stock lots:', error);
        this.stockLots = [];
      }
    });
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedStockItem = null;
    this.stockLots = [];
  }

  isExpiringSoon(lot: StockLot): boolean {
    if (!lot.dateExpiration) return false;
    const today = new Date();
    const expirationDate = new Date(lot.dateExpiration);
    const daysUntilExpiration = Math.floor((expirationDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
    return daysUntilExpiration > 0 && daysUntilExpiration <= 30;
  }

  isExpired(lot: StockLot): boolean {
    if (!lot.dateExpiration) return false;
    const today = new Date();
    const expirationDate = new Date(lot.dateExpiration);
    return expirationDate < today;
  }

  downloadLotFacture(lot: StockLot): void {
    if (!lot.factureUrl) {
      this.notificationService.error('Aucune facture disponible pour ce lot');
      return;
    }
    
    // Open the invoice URL in a new tab to download
    window.open(lot.factureUrl, '_blank');
  }

  viewStockTrace(stockId: string): void {
    this.openTraceModal(stockId);
  }

  // View switching methods
  setStockView(view: 'list' | 'trace'): void {
    this.currentStockView = view;

    if (view === 'trace') {
      // Could load global trace data here
    }
  }

  // Helper method to convert StockEntrepot to Stock for operations
  convertToStock(stockEntrepot: StockEntrepot): Stock {
    return {
      id: stockEntrepot.id,
      articleId: stockEntrepot.articleId,
      entrepotId: stockEntrepot.entrepotId,
      quantite: stockEntrepot.quantite,
      dateDexpiration: new Date(), // Default
      createdBy: 'system',
      updatedBy: 'system',
      createdAt: stockEntrepot.dateCreation,
      updatedAt: stockEntrepot.dateModification,
      article: {
        id: stockEntrepot.article?.id || '',
        nom: stockEntrepot.article?.nom || 'Article inconnu',
        description: stockEntrepot.article?.description || '',
        categorie: CategorieStock.AUTRE, // Default
        prixVente: stockEntrepot.article?.prixVente || 0,
        prixUnitaire: stockEntrepot.article?.prixUnitaire || 0,
        stockMinimum: stockEntrepot.stockMinimum,
        stockMaximum: stockEntrepot.stockMaximum,
        quantiteMinimale: stockEntrepot.stockMinimum,
        quantiteMaximale: stockEntrepot.stockMaximum,
        fournisseurPrincipal: stockEntrepot.article?.fournisseurPrincipal || '',
        codeBarres: stockEntrepot.article?.codeBarres || stockEntrepot.article?.codebare || '',
        estActif: stockEntrepot.article?.actif || true
      }
    };
  }
  // Helper methods for property access
  getStockName(stock: Stock): string {
    return stock.article?.nom || 'Article inconnu';
  }

  getStockDescription(stock: Stock): string {
    return stock.article?.description || '';
  }

  getStockCategory(stock: Stock): string {
    // First try to get the category name directly if available
    if (stock.article?.categorieNom) {
      return stock.article.categorieNom;
    }
    
    // If not, try to map from the category enum to display name
    const category = stock.article?.categorie;
    if (category) {
      return this.getCategoryDisplayName(category);
    }
    
    return 'Autre';
  }

  private getCategoryDisplayName(category: string | CategorieStock | any): string {
    // Handle different category types
    let categoryStr: string;
    if (typeof category === 'string') {
      categoryStr = category;
    } else if (typeof category === 'object' && category.toString) {
      categoryStr = category.toString();
    } else {
      categoryStr = String(category);
    }
    
    const categoryDisplayMap: { [key: string]: string } = {
      'MATERIEL_INFORMATIQUE': 'Matériel Informatique',
      'FOURNITURES_BUREAU': 'Fournitures de Bureau',
      'EQUIPEMENT_SECURITE': 'Équipement de Sécurité',
      'PIECES_DETACHEES': 'Pièces Détachées',
      'CONSOMMABLES': 'Consommables',
      'AUTRE': 'Autre'
    };
    
    return categoryDisplayMap[categoryStr] || categoryStr || 'Autre';
  }

  getStockQuantity(stock: Stock): number {
    // If we have lots loaded for this stock in details, calculate from lots
    if (this.selectedStockItem === stock && this.stockLots.length > 0) {
      return this.getTotalQuantityFromLots();
    }
    // Check cache for lot quantity
    if (stock.article?.id && stock.entrepot?.id) {
      const cacheKey = `${stock.article.id}-${stock.entrepot.id}`;
      const cachedQuantity = this.lotQuantityCache.get(cacheKey);
      if (cachedQuantity !== undefined) {
        return cachedQuantity;
      }
    }
    // Fallback to stock table quantity (may be out of sync)
    return stock.quantite || 0;
  }
  
  getTotalQuantityFromLots(): number {
    return this.stockLots.reduce((total, lot) => total + (lot.quantiteActuelle || 0), 0);
  }

  getStockMin(stock: Stock): number {
    return stock.article?.stockMinimum || stock.article?.quantiteMinimale || 0;
  }

  getStockMax(stock: Stock): number {
    return stock.article?.stockMaximum || stock.article?.quantiteMaximale || 0;
  }

  getStockPrice(stock: Stock): number {
    // First check if backend provides lot-based pricing
    if (stock.prixUnitaireMoyenVente !== undefined && stock.prixUnitaireMoyenVente > 0) {
      return stock.prixUnitaireMoyenVente;
    }
    // If we have lots loaded (in details modal), calculate weighted average price from lots
    if (this.selectedStockItem === stock && this.stockLots.length > 0) {
      const totalQuantity = this.stockLots.reduce((sum, lot) => sum + (lot.quantiteActuelle || 0), 0);
      if (totalQuantity > 0) {
        // Calculate weighted average selling price based on quantity
        const weightedTotal = this.stockLots.reduce((sum, lot) => {
          return sum + ((lot.quantiteActuelle || 0) * (lot.prixVenteUnitaire || 0));
        }, 0);
        return weightedTotal / totalQuantity;
      }
    }
    // Fallback to article price
    return stock.article?.prixVente || stock.article?.prixUnitaire || 0;
  }

  getWeightedAveragePurchasePrice(): number {
    // Calculate weighted average purchase price from lots
    if (this.stockLots.length === 0) return 0;
    const totalQuantity = this.stockLots.reduce((sum, lot) => sum + (lot.quantiteActuelle || 0), 0);
    if (totalQuantity === 0) return 0;
    const weightedTotal = this.stockLots.reduce((sum, lot) => {
      return sum + ((lot.quantiteActuelle || 0) * (lot.prixAchatUnitaire || 0));
    }, 0);
    return weightedTotal / totalQuantity;
  }

  getStockFournisseur(stock: Stock): string {
    return stock.fournisseur?.nom || '-';
  }

  getStockMarque(stock: Stock): string {
    if (stock.article?.marque) {
      return typeof stock.article.marque === 'string' 
        ? stock.article.marque 
        : stock.article.marque.nom;
    }
    return '-';
  }

  getStockEntrepot(stock: Stock): string {
    return stock.entrepot?.nom || '-';
  }

  getStockSupplier(stock: Stock): string {
    return stock.article?.fournisseurPrincipal || '';
  }

  getStockBarcode(stock: Stock): string {
    return stock.article?.codeBarres || '';
  }

  getStockStatus(stock: Stock): StatutStock {
    if (this.isOutOfStockItem(stock)) return StatutStock.RUPTURE;
    if (this.isLowStockItem(stock)) return StatutStock.DISPONIBLE; // Low stock is still available but low
    return StatutStock.DISPONIBLE;
  }

  // Fournisseur methods
  private loadFournisseurs(): void {
    this.fournisseurService.getAllFournisseurs().subscribe({
      next: (fournisseurs) => {
        console.log('Fournisseurs loaded:', fournisseurs);
        this.fournisseurs = fournisseurs || [];
        this.filteredFournisseurs = this.fournisseurs;
      },
      error: (error) => {
        console.error('Error loading fournisseurs:', error);
        this.fournisseurs = [];
        this.filteredFournisseurs = [];
      }
    });
  }

  onFournisseurInput(): void {
    this.filterFournisseurs();
  }

  onFournisseurFocus(): void {
    this.showFournisseurDropdown = true;
    this.filterFournisseurs();
  }

  filterFournisseurs(): void {
    try {
      console.log('Filtering fournisseurs, search term:', this.fournisseurSearch);
      console.log('Available fournisseurs:', this.fournisseurs);
      
      if (!this.fournisseurs) {
        this.filteredFournisseurs = [];
        return;
      }
      
      if (!this.fournisseurSearch || this.fournisseurSearch.trim() === '') {
        this.filteredFournisseurs = [...this.fournisseurs];
        return;
      }

      const searchTerm = this.fournisseurSearch.toLowerCase().trim();
      this.filteredFournisseurs = this.fournisseurs.filter(f => 
        (f.nom && f.nom.toLowerCase().includes(searchTerm)) ||
        (f.infoContact && f.infoContact.toLowerCase().includes(searchTerm))
      );
      console.log('Filtered fournisseurs:', this.filteredFournisseurs);
    } catch (error) {
      console.error('Error in filterFournisseurs:', error);
      this.filteredFournisseurs = [];
    }
  }

  selectFournisseur(fournisseur: Fournisseur): void {
    this.selectedFournisseur = fournisseur;
    this.fournisseurSearch = fournisseur.nom;
    this.showFournisseurDropdown = false;
  }

  clearFournisseur(): void {
    this.selectedFournisseur = null;
    this.fournisseurSearch = '';
    this.filteredFournisseurs = this.fournisseurs || [];
  }

  canCreateNewFournisseur(): boolean {
    if (!this.fournisseurSearch || this.fournisseurSearch.trim() === '') {
      return false;
    }
    return !this.fournisseurExists();
  }

  fournisseurExists(): boolean {
    if (!this.fournisseurSearch || !this.fournisseurs) {
      return false;
    }
    const searchTerm = this.fournisseurSearch.toLowerCase().trim();
    return this.fournisseurs.some(f => f.nom && f.nom.toLowerCase() === searchTerm);
  }

  createNewFournisseur(): void {
    if (!this.fournisseurSearch || this.fournisseurSearch.trim() === '') {
      return;
    }

    const addCommand = {
      nom: this.fournisseurSearch.trim(),
      infoContact: '',
      adresse: '',
      telephone: '',
      matriculeFiscale: ''
    };

    this.loading = true;
    this.fournisseurService.createFournisseur(addCommand).subscribe({
      next: (response) => {
        console.log('Fournisseur créé avec succès:', response);
        this.loading = false;
        // Reload fournisseurs to get the new one
        this.loadFournisseurs();
        // Auto-select the newly created fournisseur after a short delay
        setTimeout(() => {
          const newFournisseur = this.fournisseurs.find(f => 
            f.nom && f.nom.toLowerCase() === this.fournisseurSearch.toLowerCase().trim()
          );
          if (newFournisseur) {
            this.selectFournisseur(newFournisseur);
          }
        }, 500);
      },
      error: (error) => {
        console.error('Error creating fournisseur:', error);
        this.loading = false;
        this.notificationService.error('Erreur lors de la création du fournisseur');
      }
    });
  }
}