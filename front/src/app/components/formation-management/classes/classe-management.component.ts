import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Classe, ClasseStats, ApprenantBasic } from '../../../models/formation/Classe.model';
import { ClasseService } from '../../../services/formation_managment/Classe.service';
import { FormationService } from '../../../services/formation_managment/formation.service';
import { PlanFormationService } from '../../../services/formation_managment/PlanFormation.service';
import { ApprenantService } from '../../../services/formation_managment/Apprenants.service';
import { Apprenant } from '../../../models/formation/Apprenant.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-classe-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './classe-management.component.html',
  styleUrls: ['./classe-management.component.css']
})
export class ClasseManagementComponent implements OnInit {
  @Input() formationId?: number;

  // Data
  classes: Classe[] = [];
  formations: any[] = [];
  planFormations: any[] = [];
  availableApprenants: Apprenant[] = [];
  stats: ClasseStats = { total: 0, active: 0 };

  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalItems = 0;
  totalPages = 0;

  // Search & Filter
  searchTerm = '';
  filterActive: 'all' | 'active' | 'inactive' = 'all';

  // UI State
  isLoading = false;
  showAddForm = false;
  showEditForm = false;
  showApprenantModal = false;

  // Form
  selectedClasse: Classe | null = null;
  classeForm: Partial<Classe> = this.getEmptyForm();

  // Apprenant selection
  selectedApprenantIds: number[] = [];

  constructor(
    private classeService: ClasseService,
    private formationService: FormationService,
    private planFormationService: PlanFormationService,
    private apprenantService: ApprenantService
  ) {}

  ngOnInit(): void {
    this.loadClasses();
    this.loadStats();
    this.loadFormations();
  }

  getEmptyForm(): Partial<Classe> {
    return {
      nom: '',
      description: '',
      capaciteMax: undefined,
      formationId: this.formationId,
      planFormationId: undefined,
      isActive: true,
      dateDebutAcces: undefined,
      dateFinAcces: undefined
    };
  }

  // ==================== Data Loading ====================

  loadClasses(): void {
    this.isLoading = true;

    if (this.formationId) {
      this.classeService.getClassesByFormation(this.formationId).subscribe({
        next: (classes) => {
          this.classes = classes;
          this.totalItems = classes.length;
          this.totalPages = 1;
          this.applyLocalFilters();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading classes', err);
          this.isLoading = false;
        }
      });
    } else {
      this.classeService.searchClasses(this.searchTerm, this.currentPage, this.pageSize).subscribe({
        next: (response) => {
          this.classes = response.content;
          this.totalItems = response.totalItems;
          this.totalPages = response.totalPages;
          this.applyLocalFilters();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading classes', err);
          this.isLoading = false;
        }
      });
    }
  }

  loadStats(): void {
    this.classeService.getStats().subscribe({
      next: (stats) => this.stats = stats,
      error: (err) => console.error('Error loading stats', err)
    });
  }

  loadFormations(): void {
    this.formationService.getAllFormations().subscribe({
      next: (formations: any) => {
        this.formations = Array.isArray(formations) ? formations : [];
      },
      error: (err: any) => console.error('Error loading formations', err)
    });
  }

  loadPlanFormations(formationId: number): void {
    this.planFormationService.getPlansByFormation(formationId).subscribe({
      next: (plans: any[]) => {
        this.planFormations = plans;
      },
      error: (err: any) => console.error('Error loading plan formations', err)
    });
  }

  loadAvailableApprenants(): void {
    this.apprenantService.getAllApprenants().subscribe({
      next: (apprenants: any[]) => {
        // Normalize apprenants to always have 'id' field (API returns 'idApprenant')
        this.availableApprenants = apprenants.map(a => ({
          ...a,
          id: a.id || a.idApprenant
        }));
        console.log('Loaded apprenants:', this.availableApprenants);
      },
      error: (err) => console.error('Error loading apprenants', err)
    });
  }

  applyLocalFilters(): void {
    if (this.filterActive === 'all') return;

    this.classes = this.classes.filter(c => {
      if (this.filterActive === 'active') return c.isActive;
      if (this.filterActive === 'inactive') return !c.isActive;
      return true;
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadClasses();
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.loadClasses();
  }

  onFormationChange(): void {
    if (this.classeForm.formationId) {
      this.loadPlanFormations(this.classeForm.formationId);
      this.classeForm.planFormationId = undefined;
    } else {
      this.planFormations = [];
    }
  }

  // ==================== Pagination ====================

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadClasses();
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadClasses();
    }
  }

  // ==================== CRUD Operations ====================

  openAddForm(): void {
    this.classeForm = this.getEmptyForm();
    if (this.formationId) {
      this.classeForm.formationId = this.formationId;
      this.loadPlanFormations(this.formationId);
    }
    this.showAddForm = true;
    this.showEditForm = false;
  }

  openEditForm(classe: Classe): void {
    this.selectedClasse = classe;
    this.classeForm = {
      nom: classe.nom,
      description: classe.description,
      capaciteMax: classe.capaciteMax,
      formationId: classe.formationId,
      planFormationId: classe.planFormationId,
      isActive: classe.isActive,
      dateDebutAcces: classe.dateDebutAcces,
      dateFinAcces: classe.dateFinAcces
    };
    if (classe.formationId) {
      this.loadPlanFormations(classe.formationId);
    }
    this.showEditForm = true;
    this.showAddForm = false;
  }

  cancelForm(): void {
    this.showAddForm = false;
    this.showEditForm = false;
    this.selectedClasse = null;
    this.classeForm = this.getEmptyForm();
    this.planFormations = [];
  }

  saveClasse(): void {
    if (!this.classeForm.nom) {
      Swal.fire({ icon: 'warning', title: 'Champ requis', text: 'Le nom est obligatoire.' });
      return;
    }

    this.isLoading = true;

    if (this.showEditForm && this.selectedClasse?.id) {
      this.classeService.updateClasse(this.selectedClasse.id, this.classeForm).subscribe({
        next: () => {
          Swal.fire({ icon: 'success', title: 'Succès', text: 'Classe mise à jour.', timer: 2000, showConfirmButton: false });
          this.cancelForm();
          this.loadClasses();
          this.loadStats();
        },
        error: (err) => {
          console.error('Error updating classe', err);
          Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de mettre à jour la classe.' });
          this.isLoading = false;
        }
      });
    } else {
      this.classeService.createClasse(this.classeForm).subscribe({
        next: () => {
          Swal.fire({ icon: 'success', title: 'Succès', text: 'Classe créée avec succès.', timer: 2000, showConfirmButton: false });
          this.cancelForm();
          this.loadClasses();
          this.loadStats();
        },
        error: (err) => {
          console.error('Error creating classe', err);
          Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de créer la classe.' });
          this.isLoading = false;
        }
      });
    }
  }

  deleteClasse(classe: Classe): void {
    Swal.fire({
      title: 'Confirmer la suppression',
      text: `Voulez-vous vraiment supprimer la classe "${classe.nom}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Supprimer',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed && classe.id) {
        this.classeService.deleteClasse(classe.id).subscribe({
          next: () => {
            Swal.fire({ icon: 'success', title: 'Supprimé', text: 'Classe supprimée.', timer: 2000, showConfirmButton: false });
            this.loadClasses();
            this.loadStats();
          },
          error: (err) => {
            console.error('Error deleting classe', err);
            Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de supprimer la classe.' });
          }
        });
      }
    });
  }

  toggleActive(classe: Classe): void {
    if (!classe.id) return;

    this.classeService.toggleActive(classe.id).subscribe({
      next: () => {
        const msg = classe.isActive ? 'désactivée' : 'activée';
        Swal.fire({ icon: 'success', title: 'Succès', text: `Classe ${msg}.`, timer: 2000, showConfirmButton: false });
        this.loadClasses();
        this.loadStats();
      },
      error: (err) => {
        console.error('Error toggling active', err);
        Swal.fire({ icon: 'error', title: 'Erreur', text: 'Opération échouée.' });
      }
    });
  }

  // ==================== Apprenant Management ====================

  openApprenantModal(classe: Classe): void {
    this.selectedClasse = classe;
    this.loadAvailableApprenants();
    // Initialize with current apprenant IDs, filtering out any undefined/null
    this.selectedApprenantIds = (classe.apprenants || [])
      .map(a => a.id)
      .filter((id): id is number => id !== null && id !== undefined);
    this.showApprenantModal = true;
  }

  closeApprenantModal(): void {
    this.showApprenantModal = false;
    this.selectedClasse = null;
    this.selectedApprenantIds = [];
  }

  isApprenantSelected(apprenantId: number): boolean {
    return this.selectedApprenantIds.includes(apprenantId);
  }

  toggleApprenantSelection(apprenantId: number): void {
    const index = this.selectedApprenantIds.indexOf(apprenantId);
    if (index > -1) {
      this.selectedApprenantIds.splice(index, 1);
    } else {
      this.selectedApprenantIds.push(apprenantId);
    }
  }

  saveApprenantSelection(): void {
    if (!this.selectedClasse?.id) return;

    const currentApprenantIds: number[] = (this.selectedClasse.apprenants || [])
      .map(a => a.id)
      .filter((id): id is number => id !== null && id !== undefined);
    
    const selectedIds: number[] = this.selectedApprenantIds.filter((id): id is number => id !== null && id !== undefined);
    
    const toAdd = selectedIds.filter(id => !currentApprenantIds.includes(id));
    const toRemove = currentApprenantIds.filter(id => !selectedIds.includes(id));

    // If nothing to add or remove, just close
    if (toAdd.length === 0 && toRemove.length === 0) {
      Swal.fire({ icon: 'info', title: 'Info', text: 'Aucun changement détecté.', timer: 1500, showConfirmButton: false });
      this.closeApprenantModal();
      return;
    }

    this.isLoading = true;

    // Add new apprenants
    if (toAdd.length > 0) {
      this.classeService.addMultipleApprenants(this.selectedClasse.id, toAdd).subscribe({
        next: () => {
          // Remove apprenants after adding
          this.removeApprenants(toRemove);
        },
        error: (err) => {
          console.error('Error adding apprenants', err);
          this.isLoading = false;
          Swal.fire({ icon: 'error', title: 'Erreur', text: 'Erreur lors de l\'ajout des apprenants.' });
        }
      });
    } else {
      this.removeApprenants(toRemove);
    }
  }

  private removeApprenants(toRemove: number[]): void {
    if (toRemove.length === 0 || !this.selectedClasse?.id) {
      this.finishApprenantUpdate();
      return;
    }

    const removePromises = toRemove.map(id =>
      this.classeService.removeApprenantFromClasse(this.selectedClasse!.id!, id).toPromise()
    );

    Promise.all(removePromises)
      .then(() => this.finishApprenantUpdate())
      .catch(err => {
        console.error('Error removing apprenants', err);
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Erreur', text: 'Erreur lors du retrait des apprenants.' });
      });
  }

  private finishApprenantUpdate(): void {
    this.isLoading = false;
    Swal.fire({ icon: 'success', title: 'Succès', text: 'Apprenants mis à jour.', timer: 2000, showConfirmButton: false });
    this.closeApprenantModal();
    this.loadClasses();
  }

  removeApprenant(classe: Classe, apprenant: ApprenantBasic): void {
    if (!classe.id) return;

    Swal.fire({
      title: 'Retirer l\'apprenant',
      text: `Retirer ${apprenant.nom} ${apprenant.prenom || ''} de cette classe?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Retirer',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.classeService.removeApprenantFromClasse(classe.id!, apprenant.id).subscribe({
          next: () => {
            Swal.fire({ icon: 'success', title: 'Succès', text: 'Apprenant retiré.', timer: 2000, showConfirmButton: false });
            this.loadClasses();
          },
          error: (err) => {
            console.error('Error removing apprenant', err);
            Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de retirer l\'apprenant.' });
          }
        });
      }
    });
  }

  // ==================== Helpers ====================

  getAssociationLabel(classe: Classe): string {
    if (classe.planFormationTitre) {
      return `📅 ${classe.planFormationTitre}`;
    }
    if (classe.formationTheme) {
      return `🎓 ${classe.formationTheme}`;
    }
    return '-';
  }

  getCapacityInfo(classe: Classe): string {
    if (!classe.capaciteMax) return `${classe.enrollmentCount || 0} inscrits`;
    return `${classe.enrollmentCount || 0} / ${classe.capaciteMax}`;
  }

  getCapacityClass(classe: Classe): string {
    if (!classe.capaciteMax) return '';
    const ratio = (classe.enrollmentCount || 0) / classe.capaciteMax;
    if (ratio >= 1) return 'full';
    if (ratio >= 0.8) return 'almost-full';
    return 'available';
  }

  getAccessDateLabel(classe: Classe): string {
    const formatDate = (date: string | undefined): string => {
      if (!date) return '';
      return new Date(date).toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
    };

    if (classe.dateDebutAcces && classe.dateFinAcces) {
      return `${formatDate(classe.dateDebutAcces)} - ${formatDate(classe.dateFinAcces)}`;
    }
    if (classe.dateDebutAcces) {
      return `À partir du ${formatDate(classe.dateDebutAcces)}`;
    }
    if (classe.dateFinAcces) {
      return `Jusqu'au ${formatDate(classe.dateFinAcces)}`;
    }
    return '';
  }
}
