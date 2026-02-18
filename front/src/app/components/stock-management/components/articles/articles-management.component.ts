import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { Article, AddArticleCommand, UpdateArticleCommand, CategorieArticle, Marque } from '../../../../models/stock.model';
import { ArticleService } from '../../../../services/article.service';
import { CategoryService } from '../../../../services/category.service';
import { MarqueService } from '../../../../services/marque.service';
import { NotificationComponent } from '../../../shared/notification/notification.component';
import { NotificationService } from '../../../../services/notification.service';

@Component({
  selector: 'app-articles-management',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, NotificationComponent],
  templateUrl: './articles-management.component.html',
  styleUrls: ['./articles-management.component.css']
})
export class ArticlesManagementComponent implements OnInit {
  articles: Article[] = [];
  categories: CategorieArticle[] = [];
  marques: Marque[] = [];
  
  searchArticles: string = '';
  categorieFilter: string = '';
  marqueFilter: string = '';
  statusFilter: string = '';
  
  loading: boolean = false;
  error: string = '';
  
  // Pagination
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];
  Math = Math;
  
  showArticleModal: boolean = false;
  isEditingArticle: boolean = false;
  currentArticle: Partial<Article> = {};

  constructor(
    private articleService: ArticleService,
    private categoryService: CategoryService,
    private marqueService: MarqueService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadArticles();
    this.loadCategories();
    this.loadMarques();
  }

  private loadArticles(): void {
    this.loading = true;
    this.error = '';
    
    const query: any = {
      page: this.currentPage,
      size: this.pageSize
    };
    
    if (this.searchArticles) query.searchTerm = this.searchArticles;
    if (this.categorieFilter) query.categorie = this.categorieFilter;
    if (this.marqueFilter) query.marque = this.marqueFilter;
    if (this.statusFilter) query.estActif = this.statusFilter === 'active';
    
    this.articleService.getArticles(query).subscribe({
      next: (response) => {
        this.articles = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Erreur lors du chargement des articles';
        this.loading = false;
        console.error('Error loading articles:', error);
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
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadArticles();
  }

  getFilteredArticles(): Article[] {
    return this.articles;
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadArticles();
    }
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.loadArticles();
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
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

  showAddArticleForm(): void {
    this.isEditingArticle = false;
    this.currentArticle = {
      nom: '',
      codebare: '',
      description: '',
      categorieId: '',
      marqueId: '',
      imageUrl: '',
      unitesDeMesure: '',
      prixAchat: 0,
      prixVente: 0,
      tauxTaxe: 0,
      prixVenteHT: 0,
      stockMinimum: 0,
      stockMaximum: 0,
      estStockBasee: false,
      estStockElever: false,
      estActif: true
    };
    this.showArticleModal = true;
  }

  editArticle(article: Article): void {
    this.isEditingArticle = true;
    this.currentArticle = { 
      ...article,
      categorieId: typeof article.categorie === 'object' ? article.categorie?.id : article.categorie,
      marqueId: typeof article.marque === 'object' ? article.marque?.id : article.marque
    };
    this.showArticleModal = true;
  }

  saveArticleForm(): void {
    if (!this.currentArticle.nom?.trim()) {
      this.error = 'Le nom de l\'article est obligatoire';
      return;
    }

    // Validate numeric fields are not negative
    if (this.currentArticle.prixAchat !== undefined && this.currentArticle.prixAchat < 0) {
      this.notificationService.error('Le prix d\'achat ne peut pas être négatif');
      return;
    }
    if (this.currentArticle.prixVente !== undefined && this.currentArticle.prixVente < 0) {
      this.notificationService.error('Le prix de vente ne peut pas être négatif');
      return;
    }
    if (this.currentArticle.tauxTaxe !== undefined && (this.currentArticle.tauxTaxe < 0 || this.currentArticle.tauxTaxe > 100)) {
      this.notificationService.error('Le taux de taxe doit être entre 0 et 100%');
      return;
    }
    if (this.currentArticle.stockMinimum !== undefined && this.currentArticle.stockMinimum < 0) {
      this.notificationService.error('Le stock minimum ne peut pas être négatif');
      return;
    }
    if (this.currentArticle.stockMaximum !== undefined && this.currentArticle.stockMaximum < 0) {
      this.notificationService.error('Le stock maximum ne peut pas être négatif');
      return;
    }

    this.loading = true;
    this.error = '';

    if (this.isEditingArticle && this.currentArticle.id) {
      const updateCommand: UpdateArticleCommand = {
        id: this.currentArticle.id,
        nom: this.currentArticle.nom,
        sku: this.currentArticle.sku,
        codebare: this.currentArticle.codebare,
        description: this.currentArticle.description,
        categorieId: this.currentArticle.categorieId,
        marqueId: this.currentArticle.marqueId,
        imageUrl: this.currentArticle.imageUrl,
        unitesDeMesure: this.currentArticle.unitesDeMesure,
        prixAchat: this.currentArticle.prixAchat,
        prixVente: this.currentArticle.prixVente,
        tauxTaxe: this.currentArticle.tauxTaxe,
        prixVenteHT: this.currentArticle.prixVenteHT,
        stockMinimum: this.currentArticle.stockMinimum,
        stockMaximum: this.currentArticle.stockMaximum,
        estStockBasee: this.currentArticle.estStockBasee,
        estStockElever: this.currentArticle.estStockElever,
        estActif: this.currentArticle.estActif
      };

      this.articleService.updateArticle(this.currentArticle.id, updateCommand).subscribe({
        next: () => {
          this.loading = false;
          this.closeArticleModal();
          this.loadArticles();
        },
        error: (error) => {
          this.error = 'Erreur lors de la mise à jour de l\'article';
          this.loading = false;
          console.error('Error updating article:', error);
        }
      });
    } else {
      const addCommand: AddArticleCommand = {
        nom: this.currentArticle.nom,
        codebare: this.currentArticle.codebare,
        description: this.currentArticle.description,
        categorieId: this.currentArticle.categorieId,
        marqueId: this.currentArticle.marqueId,
        imageUrl: this.currentArticle.imageUrl,
        unitesDeMesure: this.currentArticle.unitesDeMesure,
        prixAchat: this.currentArticle.prixAchat,
        prixVente: this.currentArticle.prixVente,
        tauxTaxe: this.currentArticle.tauxTaxe,
        prixVenteHT: this.currentArticle.prixVenteHT,
        stockMinimum: this.currentArticle.stockMinimum,
        stockMaximum: this.currentArticle.stockMaximum,
        estStockBasee: this.currentArticle.estStockBasee,
        estStockElever: this.currentArticle.estStockElever,
        estActif: this.currentArticle.estActif ?? true
      };

      this.articleService.addArticle(addCommand).subscribe({
        next: () => {
          this.loading = false;
          this.closeArticleModal();
          this.loadArticles();
        },
        error: (error) => {
          this.error = 'Erreur lors de l\'ajout de l\'article';
          this.loading = false;
          console.error('Error adding article:', error);
        }
      });
    }
  }

  deleteArticle(id: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet article ?')) {
      this.loading = true;
      this.error = '';

      this.articleService.deleteArticle(id).subscribe({
        next: () => {
          this.loading = false;
          this.loadArticles();
        },
        error: (error) => {
          this.error = 'Erreur lors de la suppression de l\'article';
          this.loading = false;
          console.error('Error deleting article:', error);
        }
      });
    }
  }

  closeArticleModal(): void {
    this.showArticleModal = false;
    this.currentArticle = {};
    this.error = '';
  }

  refreshArticles(): void {
    this.loadArticles();
  }

  calculatePrixVenteHT(): void {
    if (this.currentArticle.prixVente && this.currentArticle.tauxTaxe) {
      this.currentArticle.prixVenteHT = this.currentArticle.prixVente / (1 + this.currentArticle.tauxTaxe / 100);
    }
  }

  // Helper methods for displaying relationship names
  getCategorieNom(article: Article): string {
    if (!article.categorie) return '-';
    return typeof article.categorie === 'object' ? article.categorie.nom : article.categorie;
  }

  getMarqueNom(article: Article): string {
    if (!article.marque) return '-';
    return typeof article.marque === 'object' ? article.marque.nom : article.marque;
  }
}
