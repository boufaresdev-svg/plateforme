import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AjustementStockService } from '../../../../services/ajustement-stock.service';
import { ArticleService } from '../../../../services/article.service';
import { EntrepotService } from '../../../../services/entrepot.service';
import { StockService } from '../../../../services/stock.service';
import { CategoryService } from '../../../../services/category.service';
import { MarqueService } from '../../../../services/marque.service';
import { FournisseurService } from '../../../../services/fournisseur.service';
import { NotificationService } from '../../../../services/notification.service';
import { NotificationComponent } from '../../../shared/notification/notification.component';
import {
  AjustementStock,
  AddAjustementStockCommand,
  UpdateAjustementStockCommand,
  AjustementStockQuery,
  Article,
  Entrepot
} from '../../../../models/stock.model';

@Component({
  selector: 'app-ajustement-stock',
  standalone: true,
  imports: [CommonModule, FormsModule, NotificationComponent],
  templateUrl: './ajustement-stock.component.html',
  styleUrls: ['./ajustement-stock.component.css']
})
export class AjustementStockComponent implements OnInit {
  ajustements: AjustementStock[] = [];
  articles: Article[] = [];
  entrepots: Entrepot[] = [];
  
  showModal = false;
  isEditMode = false;
  loading = false;
  errorMessage: string = '';
  loadingStock = false;
  
  // Expose Math for template
  Math = Math;
  
  // Form data
  selectedAjustement: AjustementStock | null = null;
  formData: AddAjustementStockCommand = {
    articleId: '',
    entrepotId: '',
    quantiteAvant: 0,
    quantiteApres: 0,
    raison: '',
    notes: ''
  };
  
  // Filters
  filterArticleId: string = '';
  categoryFilter: string = '';
  marqueFilter: string = '';
  fournisseurFilter: string = '';
  entrepotFilter: string = '';
  filterStartDate: Date | null = null;
  filterEndDate: Date | null = null;
  
  // Filter data
  categories: any[] = [];
  marques: any[] = [];
  fournisseurs: any[] = [];
  
  // Pagination
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];
  
  constructor(
    private ajustementService: AjustementStockService,
    private articleService: ArticleService,
    private entrepotService: EntrepotService,
    private stockService: StockService,
    private categoryService: CategoryService,
    private marqueService: MarqueService,
    private fournisseurService: FournisseurService,
    private notificationService: NotificationService
  ) {}
  
  ngOnInit(): void {
    this.loadAjustements();
    this.loadArticles();
    this.loadEntrepots();
    this.loadCategories();
    this.loadMarques();
    this.loadFournisseurs();
  }
  
  loadAjustements(): void {
    this.loading = true;
    
    const query: AjustementStockQuery = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: 'dateAjustement',
      sortDirection: 'DESC'
    };
    
    if (this.categoryFilter) {
      query.categorieId = this.categoryFilter;
    }
    if (this.marqueFilter) {
      query.marqueId = this.marqueFilter;
    }
    if (this.fournisseurFilter) {
      query.fournisseurId = this.fournisseurFilter;
    }
    if (this.entrepotFilter) {
      query.entrepotId = this.entrepotFilter;
    }
    if (this.filterArticleId) {
      query.articleId = this.filterArticleId;
    }
    if (this.filterStartDate) {
      query.startDate = this.filterStartDate;
    }
    if (this.filterEndDate) {
      query.endDate = this.filterEndDate;
    }
    
    this.ajustementService.getAjustements(query).subscribe({
      next: (response) => {
        this.ajustements = Array.isArray(response.content) ? response.content : [];
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des ajustements:', error);
        this.ajustements = [];
        this.totalElements = 0;
        this.totalPages = 0;
        this.loading = false;
      }
    });
  }
  
  loadArticles(): void {
    this.articleService.getArticles().subscribe({
      next: (data: any) => {
        // Handle PageResponse structure with content array
        if (data && data.content && Array.isArray(data.content)) {
          this.articles = data.content;
        } else if (Array.isArray(data)) {
          this.articles = data;
        } else {
          this.articles = [];
        }
        console.log('Articles loaded:', this.articles.length);
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des articles:', error);
        this.articles = [];
      }
    });
  }
  
  loadEntrepots(): void {
    this.entrepotService.getEntrepots().subscribe({
      next: (data: any) => {
        // Handle both array and PageResponse structure
        if (data && data.content && Array.isArray(data.content)) {
          this.entrepots = data.content;
        } else if (Array.isArray(data)) {
          this.entrepots = data;
        } else {
          this.entrepots = [];
        }
        console.log('Entrepôts loaded:', this.entrepots.length);
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des entrepôts:', error);
        this.entrepots = [];
      }
    });
  }
  
  loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (data: any) => {
        this.categories = Array.isArray(data) ? data : [];
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des catégories:', error);
        this.categories = [];
      }
    });
  }
  
  loadMarques(): void {
    this.marqueService.getMarques().subscribe({
      next: (data: any) => {
        this.marques = Array.isArray(data) ? data : [];
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des marques:', error);
        this.marques = [];
      }
    });
  }
  
  loadFournisseurs(): void {
    this.fournisseurService.getAllFournisseurs().subscribe({
      next: (data: any) => {
        this.fournisseurs = Array.isArray(data) ? data : [];
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des fournisseurs:', error);
        this.fournisseurs = [];
      }
    });
  }
  
  openAddModal(): void {
    this.isEditMode = false;
    this.formData = {
      articleId: '',
      entrepotId: '',
      quantiteAvant: 0,
      quantiteApres: 0,
      raison: '',
      notes: ''
    };
    this.showModal = true;
  }
  
  onArticleOrEntrepotChange(): void {
    if (this.formData.articleId && this.formData.entrepotId) {
      this.loadingStock = true;
      this.stockService.getStockByArticleAndEntrepot(this.formData.articleId, this.formData.entrepotId).subscribe({
        next: (stock) => {
          this.formData.quantiteAvant = stock.quantite ?? 0;
          this.loadingStock = false;
        },
        error: (error) => {
          console.error('Erreur lors de la récupération du stock:', error);
          console.error('Error status:', error.status);
          console.error('Error message:', error.message);
          // If stock doesn't exist yet for this article/entrepot combination, set to 0
          this.formData.quantiteAvant = 0;
          this.loadingStock = false;
        }
      });
    } else {
      this.formData.quantiteAvant = 0;
      this.loadingStock = false;
    }
  }
  
  openEditModal(ajustement: AjustementStock): void {
    this.isEditMode = true;
    this.selectedAjustement = ajustement;
    this.formData = {
      articleId: ajustement.articleId,
      entrepotId: ajustement.entrepotId || '',
      quantiteAvant: ajustement.quantiteAvant,
      quantiteApres: ajustement.quantiteApres,
      raison: ajustement.raison || '',
      notes: ajustement.notes || ''
    };
    this.showModal = true;
  }
  
  closeModal(): void {
    this.showModal = false;
    this.selectedAjustement = null;
  }
  
  saveAjustement(): void {
    if (!this.formData.articleId || !this.formData.entrepotId || this.formData.quantiteAvant == null || this.formData.quantiteApres == null) {
      this.notificationService.error('Veuillez remplir tous les champs obligatoires (Article et Entrepôt sont requis)');
      return;
    }
    
    this.loading = true;
    
    if (this.isEditMode && this.selectedAjustement) {
      const updateCommand: UpdateAjustementStockCommand = {
        id: this.selectedAjustement.id,
        ...this.formData
      };
      
      this.ajustementService.updateAjustement(this.selectedAjustement.id, updateCommand).subscribe({
        next: () => {
          this.loadAjustements();
          this.closeModal();
          this.loading = false;
        },
        error: (error) => {
          console.error('Erreur lors de la mise à jour:', error);
          this.notificationService.error('Erreur lors de la mise à jour de l\'ajustement');
          this.loading = false;
        }
      });
    } else {
      this.ajustementService.createAjustement(this.formData).subscribe({
        next: () => {
          this.notificationService.success('Ajustement créé avec succès');
          this.loadAjustements();
          this.closeModal();
          this.loading = false;
        },
        error: (error) => {
          console.error('Erreur lors de la création:', error);
          this.loading = false;
        }
      });
    }
  }
  
  deleteAjustement(id: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet ajustement ?')) {
      this.loading = true;
      this.ajustementService.deleteAjustement(id).subscribe({
        next: () => {
          this.loadAjustements();
          this.loading = false;
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          this.notificationService.error('Erreur lors de la suppression de l\'ajustement');
          this.loading = false;
        }
      });
    }
  }
  
  filterByArticle(): void {
    if (this.filterArticleId) {
      this.loading = true;
      this.ajustementService.getAjustementsByArticle(this.filterArticleId).subscribe({
        next: (data) => {
          this.ajustements = Array.isArray(data) ? data : [];
          this.loading = false;
        },
        error: (error) => {
          console.error('Erreur lors du filtrage:', error);
          this.ajustements = [];
          this.loading = false;
        }
      });
    } else {
      this.loadAjustements();
    }
  }
  
  filterByDateRange(): void {
    if (this.filterStartDate && this.filterEndDate) {
      this.loading = true;
      this.ajustementService.getAjustementsByDateRange(this.filterStartDate, this.filterEndDate).subscribe({
        next: (data) => {
          this.ajustements = Array.isArray(data) ? data : [];
          this.loading = false;
        },
        error: (error) => {
          console.error('Erreur lors du filtrage:', error);
          this.ajustements = [];
          this.loading = false;
        }
      });
    } else {
      this.loadAjustements();
    }
  }
  
  clearFilters(): void {
    this.filterArticleId = '';
    this.filterStartDate = null;
    this.filterEndDate = null;
    this.loadAjustements();
  }
  
  get paginatedAjustements(): AjustementStock[] {
    // With backend pagination, just return the ajustements directly
    return this.ajustements;
  }
  
  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadAjustements();
    }
  }
  
  onPageSizeChange(): void {
    this.currentPage = 0;
    this.loadAjustements();
  }
  
  get pages(): number[] {
    const pages: number[] = [];
    for (let i = 0; i < this.totalPages; i++) {
      pages.push(i);
    }
    return pages;
  }
  
  changePageOld(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }
  
  getAdjustmentType(ajustement: number): string {
    if (ajustement > 0) return 'Entrée';
    if (ajustement < 0) return 'Sortie';
    return 'Aucun';
  }
  
  getAdjustmentClass(ajustement: number): string {
    if (ajustement > 0) return 'text-success';
    if (ajustement < 0) return 'text-danger';
    return 'text-muted';
  }
}
