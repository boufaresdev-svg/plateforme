import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Apprenant, StatusInscription, ApprenantStats } from '../../../models/formation/Apprenant.model';
import { ApprenantService } from '../../../services/formation_managment/Apprenants.service';
import { PlanFormationService } from '../../../services/formation_managment/PlanFormation.service';
import { PlanFormation } from '../../../models/formation/PlanFormation.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-apprenant-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './apprenant-management.component.html',
  styleUrls: ['./apprenant-management.component.css']
})
export class ApprenantManagementComponent implements OnInit {
  @Input() formationId?: number;
  @Input() planFormationId?: number;

  // Data
  apprenants: Apprenant[] = [];
  planFormations: PlanFormation[] = [];
  stats: ApprenantStats = { total: 0, active: 0, blocked: 0 };

  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalItems = 0;
  totalPages = 0;

  // Search & Filter
  searchTerm = '';
  filterStatus: 'all' | 'active' | 'blocked' | 'staff' = 'all';

  // UI State
  isLoading = false;
  showAddForm = false;
  showEditForm = false;
  showImportModal = false;
  showPasswordModal = false;

  // Form
  selectedApprenant: Apprenant | null = null;
  apprenantForm: Apprenant = this.getEmptyForm();

  // Import
  selectedFile: File | null = null;
  importPlanFormationId?: number;
  generatePasswordsOnImport = true;
  importResult: any = null;

  // Password
  newPassword = '';
  generatedPassword = '';

  statusInscriptionOptions = Object.values(StatusInscription);

  constructor(
    private apprenantService: ApprenantService,
    private planFormationService: PlanFormationService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Check for route params
    this.route.params.subscribe(params => {
      if (params['formationId']) {
        this.formationId = +params['formationId'];
      }
    });
    
    this.loadApprenants();
    this.loadStats();
    this.loadPlanFormations();
  }

  goBack(): void {
    this.router.navigate(['/formation']);
  }

  getEmptyForm(): Apprenant {
    return {
      nom: '',
      prenom: '',
      email: '',
      telephone: '',
      adresse: '',
      prerequis: '',
      statusInscription: StatusInscription.INSCRIT,
      isBlocked: false,
      isStaff: false,
      isActive: true
    };
  }

  // ==================== Data Loading ====================

  loadApprenants(): void {
    this.isLoading = true;
    
    if (this.formationId) {
      this.apprenantService.getApprenantsByFormationPaged(this.formationId, this.currentPage, this.pageSize)
        .subscribe({
          next: (response) => {
            this.apprenants = response.content;
            this.totalItems = response.totalItems;
            this.totalPages = response.totalPages;
            this.isLoading = false;
          },
          error: (err) => {
            console.error('Error loading apprenants', err);
            this.isLoading = false;
          }
        });
    } else {
      this.apprenantService.searchApprenants(this.searchTerm, this.currentPage, this.pageSize)
        .subscribe({
          next: (response) => {
            this.apprenants = response.content;
            this.totalItems = response.totalItems;
            this.totalPages = response.totalPages;
            this.applyLocalFilters();
            this.isLoading = false;
          },
          error: (err) => {
            console.error('Error loading apprenants', err);
            this.isLoading = false;
          }
        });
    }
  }

  loadStats(): void {
    this.apprenantService.getStats().subscribe({
      next: (stats) => this.stats = stats,
      error: (err) => console.error('Error loading stats', err)
    });
  }

  loadPlanFormations(): void {
    this.planFormationService.getAllPlans().subscribe({
      next: (plans: PlanFormation[]) => this.planFormations = plans,
      error: (err: any) => console.error('Error loading plan formations', err)
    });
  }

  applyLocalFilters(): void {
    if (this.filterStatus === 'all') return;
    
    this.apprenants = this.apprenants.filter(a => {
      switch (this.filterStatus) {
        case 'active': return a.isActive && !a.isBlocked;
        case 'blocked': return a.isBlocked;
        case 'staff': return a.isStaff;
        default: return true;
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadApprenants();
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.loadApprenants();
  }

  // ==================== Pagination ====================

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadApprenants();
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadApprenants();
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadApprenants();
  }

  // ==================== CRUD Operations ====================

  openAddForm(): void {
    this.apprenantForm = this.getEmptyForm();
    if (this.planFormationId) {
      this.apprenantForm.idPlanFormation = this.planFormationId;
    }
    this.showAddForm = true;
    this.showEditForm = false;
  }

  openEditForm(apprenant: Apprenant): void {
    this.selectedApprenant = apprenant;
    this.apprenantForm = { ...apprenant };
    this.showEditForm = true;
    this.showAddForm = false;
  }

  cancelForm(): void {
    this.showAddForm = false;
    this.showEditForm = false;
    this.selectedApprenant = null;
    this.apprenantForm = this.getEmptyForm();
  }

  saveApprenant(): void {
    if (!this.apprenantForm.nom || !this.apprenantForm.email) {
      Swal.fire({ icon: 'warning', title: 'Champs requis', text: 'Nom et email sont obligatoires.' });
      return;
    }

    this.isLoading = true;

    if (this.showEditForm && this.selectedApprenant?.id) {
      this.apprenantService.updateApprenant(this.selectedApprenant.id, this.apprenantForm).subscribe({
        next: () => {
          Swal.fire({ icon: 'success', title: 'Succès', text: 'Apprenant mis à jour.', timer: 2000, showConfirmButton: false });
          this.cancelForm();
          this.loadApprenants();
          this.loadStats();
        },
        error: (err) => {
          console.error('Error updating apprenant', err);
          Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de mettre à jour l\'apprenant.' });
          this.isLoading = false;
        }
      });
    } else {
      this.apprenantService.addApprenant(this.apprenantForm).subscribe({
        next: () => {
          Swal.fire({ icon: 'success', title: 'Succès', text: 'Apprenant créé avec succès.', timer: 2000, showConfirmButton: false });
          this.cancelForm();
          this.loadApprenants();
          this.loadStats();
        },
        error: (err) => {
          console.error('Error creating apprenant', err);
          Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de créer l\'apprenant.' });
          this.isLoading = false;
        }
      });
    }
  }

  deleteApprenant(apprenant: Apprenant): void {
    Swal.fire({
      title: 'Confirmer la suppression',
      text: `Voulez-vous vraiment supprimer ${apprenant.nom} ${apprenant.prenom || ''}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Supprimer',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed && apprenant.id) {
        this.apprenantService.deleteApprenant(apprenant.id).subscribe({
          next: () => {
            Swal.fire({ icon: 'success', title: 'Supprimé', text: 'Apprenant supprimé.', timer: 2000, showConfirmButton: false });
            this.loadApprenants();
            this.loadStats();
          },
          error: (err) => {
            console.error('Error deleting apprenant', err);
            Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de supprimer l\'apprenant.' });
          }
        });
      }
    });
  }

  // ==================== User Management ====================

  toggleBlock(apprenant: Apprenant): void {
    if (!apprenant.id) return;
    
    const action = apprenant.isBlocked 
      ? this.apprenantService.unblockApprenant(apprenant.id)
      : this.apprenantService.blockApprenant(apprenant.id);

    action.subscribe({
      next: () => {
        const msg = apprenant.isBlocked ? 'débloqué' : 'bloqué';
        Swal.fire({ icon: 'success', title: 'Succès', text: `Apprenant ${msg}.`, timer: 2000, showConfirmButton: false });
        this.loadApprenants();
        this.loadStats();
      },
      error: (err) => {
        console.error('Error toggling block', err);
        Swal.fire({ icon: 'error', title: 'Erreur', text: 'Opération échouée.' });
      }
    });
  }

  toggleStaff(apprenant: Apprenant): void {
    if (!apprenant.id) return;

    this.apprenantService.toggleStaff(apprenant.id).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Succès', text: 'Statut staff modifié.', timer: 2000, showConfirmButton: false });
        this.loadApprenants();
      },
      error: (err) => {
        console.error('Error toggling staff', err);
        Swal.fire({ icon: 'error', title: 'Erreur', text: 'Opération échouée.' });
      }
    });
  }

  // ==================== Password Management ====================

  openPasswordModal(apprenant: Apprenant): void {
    this.selectedApprenant = apprenant;
    this.newPassword = '';
    this.generatedPassword = '';
    this.showPasswordModal = true;
  }

  closePasswordModal(): void {
    this.showPasswordModal = false;
    this.selectedApprenant = null;
    this.newPassword = '';
    this.generatedPassword = '';
  }

  savePassword(): void {
    if (!this.selectedApprenant?.id || !this.newPassword) {
      Swal.fire({ icon: 'warning', title: 'Erreur', text: 'Veuillez saisir un mot de passe.' });
      return;
    }

    this.apprenantService.updatePassword(this.selectedApprenant.id, this.newPassword).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Succès', text: 'Mot de passe mis à jour.', timer: 2000, showConfirmButton: false });
        this.closePasswordModal();
      },
      error: (err) => {
        console.error('Error updating password', err);
        Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de mettre à jour le mot de passe.' });
      }
    });
  }

  generatePassword(): void {
    if (!this.selectedApprenant?.id) return;

    this.apprenantService.generatePassword(this.selectedApprenant.id).subscribe({
      next: (response) => {
        this.generatedPassword = response.password;
        Swal.fire({
          icon: 'success',
          title: 'Mot de passe généré',
          html: `Le nouveau mot de passe est: <strong>${response.password}</strong><br><small>Communiquez-le à l'apprenant.</small>`,
          confirmButtonText: 'OK'
        });
      },
      error: (err) => {
        console.error('Error generating password', err);
        Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de générer le mot de passe.' });
      }
    });
  }

  // ==================== CSV Import ====================

  openImportModal(): void {
    this.selectedFile = null;
    this.importPlanFormationId = this.planFormationId;
    this.generatePasswordsOnImport = true;
    this.importResult = null;
    this.showImportModal = true;
  }

  closeImportModal(): void {
    this.showImportModal = false;
    this.selectedFile = null;
    this.importResult = null;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedFile = input.files[0];
    }
  }

  importCsv(): void {
    if (!this.selectedFile) {
      Swal.fire({ icon: 'warning', title: 'Erreur', text: 'Veuillez sélectionner un fichier CSV.' });
      return;
    }

    this.isLoading = true;
    this.apprenantService.importFromCsv(
      this.selectedFile,
      this.importPlanFormationId,
      this.generatePasswordsOnImport
    ).subscribe({
      next: (result) => {
        this.importResult = result;
        this.isLoading = false;
        
        if (result.imported > 0) {
          Swal.fire({
            icon: 'success',
            title: 'Import terminé',
            html: `${result.imported} apprenant(s) importé(s).<br>${result.errors?.length || 0} erreur(s).`,
            confirmButtonText: 'OK'
          });
          this.loadApprenants();
          this.loadStats();
        } else {
          Swal.fire({
            icon: 'warning',
            title: 'Import échoué',
            text: 'Aucun apprenant importé. Vérifiez le format du fichier.'
          });
        }
      },
      error: (err) => {
        console.error('Error importing CSV', err);
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Erreur', text: 'Erreur lors de l\'import.' });
      }
    });
  }

  downloadCsvTemplate(): void {
    const headers = 'nom,prenom,email,telephone,adresse,prerequis\n';
    const sample = 'Dupont,Jean,jean.dupont@example.com,0612345678,123 Rue Example,Aucun\n';
    const csvContent = headers + sample;
    
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'template_apprenants.csv';
    link.click();
  }

  // ==================== Helpers ====================

  getStatusClass(apprenant: Apprenant): string {
    if (apprenant.isBlocked) return 'status-blocked';
    if (apprenant.isActive) return 'status-active';
    return 'status-inactive';
  }

  getStatusLabel(apprenant: Apprenant): string {
    if (apprenant.isBlocked) return 'Bloqué';
    if (apprenant.isActive) return 'Actif';
    return 'Inactif';
  }

  getPlanFormationLabel(apprenant: Apprenant): string {
    if (apprenant.planFormationTitre) return apprenant.planFormationTitre;
    if (apprenant.planFormation?.titre) return apprenant.planFormation.titre;
    return '-';
  }
}
