import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { Marque, AddMarqueCommand, UpdateMarqueCommand } from '../../../../models/stock.model';
import { MarqueService } from '../../../../services/marque.service';

@Component({
  selector: 'app-marques-management',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './marques-management.component.html',
  styleUrls: ['./marques-management.component.css']
})
export class MarquesManagementComponent implements OnInit {
  marques: Marque[] = [];
  filteredMarques: Marque[] = [];
  searchMarques: string = '';
  loading: boolean = false;
  error: string = '';
  
  // Pagination properties
  currentPage: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];
  Math = Math;
  
  showMarqueModal: boolean = false;
  isEditingMarque: boolean = false;
  currentMarque: Partial<Marque> = {};

  constructor(private marqueService: MarqueService) {}

  ngOnInit(): void {
    this.loadMarques();
  }

  private loadMarques(): void {
    this.loading = true;
    this.error = '';
    
    this.marqueService.getMarquesPaginated({
      page: this.currentPage,
      size: this.pageSize,
      nom: this.searchMarques || undefined
    }).subscribe({
      next: (response) => {
        this.marques = response.content || [];
        this.filteredMarques = [...this.marques];
        this.totalElements = response.totalElements || 0;
        this.totalPages = response.totalPages || 0;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Erreur lors du chargement des marques';
        this.loading = false;
        console.error('Error loading marques:', error);
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadMarques();
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadMarques();
    }
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.loadMarques();
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  getFilteredMarques(): Marque[] {
    if (!this.searchMarques.trim()) {
      return this.marques;
    }
    return this.marques.filter(marque =>
      marque.nom.toLowerCase().includes(this.searchMarques.toLowerCase()) ||
      marque.codeMarque?.toLowerCase().includes(this.searchMarques.toLowerCase()) ||
      marque.pays?.toLowerCase().includes(this.searchMarques.toLowerCase())
    );
  }

  showAddMarqueForm(): void {
    this.isEditingMarque = false;
    this.currentMarque = {
      nom: '',
      codeMarque: '',
      description: '',
      pays: '',
      siteWeb: '',
      urlLogo: '',
      nomContact: '',
      email: '',
      telephone: '',
      poste: '',
      estActif: true
    };
    this.showMarqueModal = true;
  }

  editMarque(marque: Marque): void {
    this.isEditingMarque = true;
    this.currentMarque = { ...marque };
    this.showMarqueModal = true;
  }

  saveMarqueForm(): void {
    if (!this.currentMarque.nom?.trim()) {
      this.error = 'Le nom de la marque est obligatoire';
      return;
    }

    this.loading = true;
    this.error = '';

    if (this.isEditingMarque && this.currentMarque.id) {
      const updateCommand: UpdateMarqueCommand = {
        id: this.currentMarque.id,
        nom: this.currentMarque.nom,
        codeMarque: this.currentMarque.codeMarque,
        description: this.currentMarque.description,
        pays: this.currentMarque.pays,
        siteWeb: this.currentMarque.siteWeb,
        urlLogo: this.currentMarque.urlLogo,
        nomContact: this.currentMarque.nomContact,
        email: this.currentMarque.email,
        telephone: this.currentMarque.telephone,
        poste: this.currentMarque.poste,
        estActif: this.currentMarque.estActif
      };

      this.marqueService.updateMarque(this.currentMarque.id, updateCommand).subscribe({
        next: () => {
          this.loading = false;
          this.closeMarqueModal();
          this.loadMarques();
        },
        error: (error) => {
          this.error = 'Erreur lors de la mise à jour de la marque';
          this.loading = false;
          console.error('Error updating marque:', error);
        }
      });
    } else {
      const addCommand: AddMarqueCommand = {
        nom: this.currentMarque.nom,
        codeMarque: this.currentMarque.codeMarque,
        description: this.currentMarque.description,
        pays: this.currentMarque.pays,
        siteWeb: this.currentMarque.siteWeb,
        urlLogo: this.currentMarque.urlLogo,
        nomContact: this.currentMarque.nomContact,
        email: this.currentMarque.email,
        telephone: this.currentMarque.telephone,
        poste: this.currentMarque.poste,
        estActif: this.currentMarque.estActif ?? true
      };

      this.marqueService.addMarque(addCommand).subscribe({
        next: () => {
          this.loading = false;
          this.closeMarqueModal();
          this.loadMarques();
        },
        error: (error) => {
          this.error = 'Erreur lors de l\'ajout de la marque';
          this.loading = false;
          console.error('Error adding marque:', error);
        }
      });
    }
  }

  deleteMarque(id: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette marque ?')) {
      this.loading = true;
      this.error = '';

      this.marqueService.deleteMarque(id).subscribe({
        next: () => {
          this.loading = false;
          this.loadMarques();
        },
        error: (error) => {
          this.error = 'Erreur lors de la suppression de la marque';
          this.loading = false;
          console.error('Error deleting marque:', error);
        }
      });
    }
  }

  closeMarqueModal(): void {
    this.showMarqueModal = false;
    this.currentMarque = {};
    this.error = '';
  }

  refreshMarques(): void {
    this.loadMarques();
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.style.display = 'none';
    const placeholder = img.nextElementSibling as HTMLElement;
    if (placeholder) {
      placeholder.style.display = 'flex';
    }
  }
}
