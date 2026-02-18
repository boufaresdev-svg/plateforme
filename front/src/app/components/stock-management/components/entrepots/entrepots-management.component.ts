import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EntrepotService } from '../../../../services/entrepot.service';
import { Entrepot, AddEntrepotCommand, UpdateEntrepotCommand } from '../../../../models/stock.model';
import { NotificationService } from '../../../../services/notification.service';

@Component({
  selector: 'app-entrepots-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './entrepots-management.component.html',
  styleUrls: ['./entrepots-management.component.css']
})
export class EntrepotsManagementComponent implements OnInit {
  entrepots: Entrepot[] = [];
  filteredEntrepots: Entrepot[] = [];
  searchTerm: string = '';
  filterVille: string = '';
  filterStatut: string = '';
  filterActif: boolean | null = null;
  
  // Pagination properties
  currentPage: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];
  Math = Math;
  
  showModal: boolean = false;
  isEditMode: boolean = false;
  currentEntrepot: Entrepot | null = null;
  
  formData: AddEntrepotCommand | UpdateEntrepotCommand = this.getEmptyForm();
  
  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  statutOptions: string[] = [
    'DISPONIBLE',
    'EN_SERVICE',
    'MAINTENANCE',
    'COMPLET',
    'FERME'
  ];

  villes: string[] = [];

  constructor(
    private entrepotService: EntrepotService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadEntrepots();
  }

  loadEntrepots(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.entrepotService.getEntrepotsPaginated({
      page: this.currentPage,
      size: this.pageSize,
      searchTerm: this.searchTerm || undefined,
      ville: this.filterVille || undefined,
      statut: this.filterStatut || undefined,
      estActif: this.filterActif ?? undefined
    }).subscribe({
      next: (response) => {
        this.entrepots = response.content || [];
        this.filteredEntrepots = [...this.entrepots];
        this.totalElements = response.totalElements || 0;
        this.totalPages = response.totalPages || 0;
        this.extractVilles();
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors du chargement des entrepôts';
        console.error('Error loading entrepots:', error);
        this.loading = false;
      }
    });
  }

  extractVilles(): void {
    const villesSet = new Set(this.entrepots.map(e => e.ville).filter(v => v));
    this.villes = Array.from(villesSet).sort();
  }

  applyFilters(): void {
    this.currentPage = 0;
    this.loadEntrepots();
  }

  // Pagination methods
  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadEntrepots();
    }
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.loadEntrepots();
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  private applyFiltersOld(): void {
    this.filteredEntrepots = this.entrepots.filter(entrepot => {
      const matchesSearch = !this.searchTerm || 
        entrepot.nom.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        (entrepot.adresse && entrepot.adresse.toLowerCase().includes(this.searchTerm.toLowerCase())) ||
        (entrepot.responsable && entrepot.responsable.toLowerCase().includes(this.searchTerm.toLowerCase()));
      
      const matchesVille = !this.filterVille || entrepot.ville === this.filterVille;
      const matchesStatut = !this.filterStatut || entrepot.statut === this.filterStatut;
      const matchesActif = this.filterActif === null || entrepot.estActif === this.filterActif;
      
      return matchesSearch && matchesVille && matchesStatut && matchesActif;
    });
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.filterVille = '';
    this.filterStatut = '';
    this.filterActif = null;
    this.filteredEntrepots = this.entrepots;
  }

  openAddModal(): void {
    this.isEditMode = false;
    this.currentEntrepot = null;
    this.formData = this.getEmptyForm();
    this.showModal = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  openEditModal(entrepot: Entrepot): void {
    this.isEditMode = true;
    this.currentEntrepot = entrepot;
    this.formData = {
      id: entrepot.id,
      nom: entrepot.nom,
      description: entrepot.description || '',
      adresse: entrepot.adresse,
      ville: entrepot.ville,
      codePostal: entrepot.codePostal || '',
      telephone: entrepot.telephone || '',
      email: entrepot.email || '',
      responsable: entrepot.responsable || '',
      superficie: entrepot.superficie,
      capaciteMaximale: entrepot.capaciteMaximale,
      statut: entrepot.statut || 'DISPONIBLE',
      estActif: entrepot.estActif
    };
    this.showModal = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeModal(): void {
    this.showModal = false;
    this.formData = this.getEmptyForm();
    this.currentEntrepot = null;
    this.errorMessage = '';
    this.successMessage = '';
  }

  submitForm(): void {
    if (!this.validateForm()) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    if (this.isEditMode && this.currentEntrepot) {
      const updateCommand = this.formData as UpdateEntrepotCommand;
      this.entrepotService.updateEntrepot(this.currentEntrepot.id, updateCommand).subscribe({
        next: () => {
          this.successMessage = 'Entrepôt mis à jour avec succès';
          this.loadEntrepots();
          setTimeout(() => this.closeModal(), 1500);
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Erreur lors de la mise à jour';
          console.error('Error updating entrepot:', error);
          this.loading = false;
        }
      });
    } else {
      const addCommand = this.formData as AddEntrepotCommand;
      this.entrepotService.addEntrepot(addCommand).subscribe({
        next: () => {
          this.successMessage = 'Entrepôt ajouté avec succès';
          this.loadEntrepots();
          setTimeout(() => this.closeModal(), 1500);
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Erreur lors de l\'ajout';
          console.error('Error adding entrepot:', error);
          this.loading = false;
        }
      });
    }
  }

  deleteEntrepot(entrepot: Entrepot): void {
    if (!confirm(`Êtes-vous sûr de vouloir supprimer l'entrepôt "${entrepot.nom}" ?`)) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.entrepotService.deleteEntrepot(entrepot.id).subscribe({
      next: () => {
        this.successMessage = 'Entrepôt supprimé avec succès';
        this.loadEntrepots();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Erreur lors de la suppression';
        console.error('Error deleting entrepot:', error);
        this.loading = false;
        setTimeout(() => this.errorMessage = '', 5000);
      }
    });
  }

  validateForm(): boolean {
    if (!this.formData.nom || this.formData.nom.trim() === '') {
      this.notificationService.error('Le nom est obligatoire');
      return false;
    }

    if (!this.formData.adresse || this.formData.adresse.trim() === '') {
      this.notificationService.error('L\'adresse est obligatoire');
      return false;
    }

    if (!this.formData.ville || this.formData.ville.trim() === '') {
      this.notificationService.error('La ville est obligatoire');
      return false;
    }

    if (this.formData.email && !this.isValidEmail(this.formData.email)) {
      this.notificationService.error('Email invalide');
      return false;
    }

    if (this.formData.superficie && this.formData.superficie < 0) {
      this.notificationService.error('La superficie ne peut pas être négative');
      return false;
    }

    if (this.formData.capaciteMaximale && this.formData.capaciteMaximale < 0) {
      this.notificationService.error('La capacité maximale ne peut pas être négative');
      return false;
    }

    if (this.formData.capaciteMaximale && this.formData.capaciteMaximale < 0) {
      this.errorMessage = 'La capacité maximale ne peut pas être négative';
      return false;
    }

    return true;
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  getEmptyForm(): AddEntrepotCommand {
    return {
      nom: '',
      description: '',
      adresse: '',
      ville: '',
      codePostal: '',
      telephone: '',
      email: '',
      responsable: '',
      superficie: undefined,
      capaciteMaximale: undefined,
      statut: 'DISPONIBLE',
      estActif: true
    };
  }

  getCapacityPercentage(entrepot: Entrepot): number {
    if (!entrepot.capaciteMaximale || entrepot.capaciteMaximale === 0) {
      return 0;
    }
    const utilisation = (entrepot.capaciteUtilisee || 0) / entrepot.capaciteMaximale * 100;
    return Math.min(utilisation, 100);
  }

  getCapacityClass(percentage: number): string {
    if (percentage >= 90) return 'capacity-critical';
    if (percentage >= 70) return 'capacity-warning';
    return 'capacity-ok';
  }

  getStatutBadgeClass(statut: string): string {
    switch (statut) {
      case 'DISPONIBLE': return 'badge-success';
      case 'EN_SERVICE': return 'badge-primary';
      case 'MAINTENANCE': return 'badge-warning';
      case 'COMPLET': return 'badge-danger';
      case 'FERME': return 'badge-secondary';
      default: return 'badge-secondary';
    }
  }

  formatStatut(statut: string): string {
    return statut?.replace(/_/g, ' ') || '';
  }
}
