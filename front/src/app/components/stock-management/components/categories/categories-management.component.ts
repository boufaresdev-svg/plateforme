import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CategorieArticle, AddCategoryCommand, UpdateCategoryCommand } from '../../../../models/stock.model';
import { CategoryService } from '../../../../services/category.service';

@Component({
  selector: 'app-categories-management',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './categories-management.component.html',
  styleUrls: ['./categories-management.component.css']
})
export class CategoriesManagementComponent implements OnInit {
  categories: CategorieArticle[] = [];
  filteredCategories: CategorieArticle[] = [];
  searchCategories: string = '';
  loading: boolean = false;
  error: string = '';
  
  // Pagination properties
  currentPage: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];
  
  showCategorieModal: boolean = false;
  isEditingCategorie: boolean = false;
  currentCategorie: Partial<CategorieArticle> = {};
  
  // Math for template
  Math = Math;

  constructor(private categoryService: CategoryService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  private loadCategories(): void {
    this.loading = true;
    this.error = '';
    
    this.categoryService.getCategoriesPaginated({
      page: this.currentPage,
      size: this.pageSize,
      nom: this.searchCategories || undefined
    }).subscribe({
      next: (response) => {
        this.categories = response.content || [];
        this.filteredCategories = [...this.categories];
        this.totalElements = response.totalElements || 0;
        this.totalPages = response.totalPages || 0;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Erreur lors du chargement des catégories';
        this.loading = false;
        console.error('Error loading categories:', error);
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadCategories();
  }

  // Pagination methods
  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadCategories();
    }
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.loadCategories();
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  getFilteredCategories(): CategorieArticle[] {
    if (!this.searchCategories.trim()) {
      return this.categories;
    }
    return this.categories.filter(cat =>
      cat.nom.toLowerCase().includes(this.searchCategories.toLowerCase()) ||
      cat.description?.toLowerCase().includes(this.searchCategories.toLowerCase())
    );
  }

  showAddCategorieForm(): void {
    this.isEditingCategorie = false;
    this.currentCategorie = {
      nom: '',
      description: '',
      estActif: true
    };
    this.showCategorieModal = true;
  }

  editCategorie(categorie: CategorieArticle): void {
    this.isEditingCategorie = true;
    this.currentCategorie = { ...categorie };
    this.showCategorieModal = true;
  }

  saveCategorieForm(): void {
    if (!this.currentCategorie.nom?.trim()) {
      this.error = 'Le nom de la catégorie est obligatoire';
      return;
    }

    this.loading = true;
    this.error = '';

    if (this.isEditingCategorie && this.currentCategorie.id) {
      // Update existing category
      const updateCommand: UpdateCategoryCommand = {
        id: this.currentCategorie.id,
        nom: this.currentCategorie.nom,
        description: this.currentCategorie.description,
        estActif: this.currentCategorie.estActif
      };

      this.categoryService.updateCategory(this.currentCategorie.id, updateCommand).subscribe({
        next: () => {
          this.loading = false;
          this.closeCategorieModal();
          this.loadCategories();
        },
        error: (error) => {
          this.error = 'Erreur lors de la mise à jour de la catégorie';
          this.loading = false;
          console.error('Error updating category:', error);
        }
      });
    } else {
      // Add new category
      const addCommand: AddCategoryCommand = {
        nom: this.currentCategorie.nom,
        description: this.currentCategorie.description,
        estActif: this.currentCategorie.estActif ?? true
      };

      this.categoryService.addCategory(addCommand).subscribe({
        next: () => {
          this.loading = false;
          this.closeCategorieModal();
          this.loadCategories();
        },
        error: (error) => {
          this.error = 'Erreur lors de l\'ajout de la catégorie';
          this.loading = false;
          console.error('Error adding category:', error);
        }
      });
    }
  }

  deleteCategorie(id: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette catégorie ?')) {
      this.loading = true;
      this.error = '';

      this.categoryService.deleteCategory(id).subscribe({
        next: () => {
          this.loading = false;
          this.loadCategories();
        },
        error: (error) => {
          this.error = 'Erreur lors de la suppression de la catégorie';
          this.loading = false;
          console.error('Error deleting category:', error);
        }
      });
    }
  }

  closeCategorieModal(): void {
    this.showCategorieModal = false;
    this.currentCategorie = {};
    this.error = '';
  }

  refreshCategories(): void {
    this.loadCategories();
  }
}