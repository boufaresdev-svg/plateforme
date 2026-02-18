import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Fournisseur, DettesFournisseur, FournisseurStats, TranchePaiement, PageResponse, PaginationParams } from '../../models/fournisseur/fournisseur.model';
import { RapportDettesEntreprise, RapportFilterParams } from '../../models/fournisseur/rapport-dettes.model';
import { FournisseurService } from '../../services/fournisseur.service';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';
import { FournisseurDashboardComponent } from './dashboard/fournisseur-dashboard.component';
import { FournisseurListComponent } from './fournisseur-list/fournisseur-list.component';
import { DetteManagementComponent } from './dette-management/dette-management.component';
import { FournisseurReportComponent } from './fournisseur-report/fournisseur-report.component';

@Component({
  selector: 'app-fournisseur-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, FournisseurDashboardComponent, FournisseurListComponent, DetteManagementComponent, FournisseurReportComponent],
  templateUrl: './fournisseur-management.component.html',
  styleUrls: ['./fournisseur-management.component.css']
})
export class FournisseurManagementComponent implements OnInit {
  // Expose Math to template
  Math = Math;
  
  fournisseurs: Fournisseur[] = [];
  filteredFournisseurs: Fournisseur[] = [];
  dettes: DettesFournisseur[] = [];
  filteredDettes: DettesFournisseur[] = [];
  stats: FournisseurStats = {
    totalFournisseurs: 0,
    fournisseursActifs: 0,
    totalDettes: 0,
    dettesNonPayees: 0,
    dettesEnRetard: 0,
    montantDettesNonPayees: 0
  };

  // Pagination properties for Fournisseurs
  fournisseurPagination = {
    currentPage: 0,
    pageSize: 10,
    totalElements: 0,
    totalPages: 0,
    sortBy: 'nom',
    sortDirection: 'ASC' as 'ASC' | 'DESC',
    isLoading: false
  };

  // Pagination properties for Dettes
  dettePagination = {
    currentPage: 0,
    pageSize: 10,
    totalElements: 0,
    totalPages: 0,
    sortBy: 'createdAt',
    sortDirection: 'DESC' as 'ASC' | 'DESC',
    isLoading: false
  };

  // Page size options
  pageSizeOptions = [10, 25, 50, 100];

  currentView: 'dashboard' | 'fournisseurs' | 'dettes' | 'details' | 'rapport' = 'dashboard';
  selectedFournisseur: Fournisseur | null = null;
  selectedDette: DettesFournisseur | null = null;
  isEditMode = false;
  searchTerm = '';
  searchTermDette = '';
  filterPaid = '';
  filterOverdue = '';

  // Debt Report properties
  rapportDettes: RapportDettesEntreprise | null = null;
  isLoadingRapport = false;
  rapportDateDebut = '';
  rapportDateFin = '';
  rapportInclureDettesPayees = true;
  selectedFournisseurIds: string[] = [];

  // Enhanced debt management properties
  debtFilter = '';
  debtSort = 'date-desc';
  filteredFournisseurDettes: DettesFournisseur[] = [];

  // Validation error messages
  numeroFactureError = '';
  trancheValidationError = '';
  isValidatingNumeroFacture = false;
  numeroFactureValid = false;

  // Debounce subject for numeroFacture validation
  private numeroFactureSubject = new Subject<string>();

  // Tranches storage - Map of detteId to tranches array
  detteTranches: Map<string, TranchePaiement[]> = new Map();

  // Modal states
  showFournisseurForm = false;
  showDetteForm = false;
  submitted = false;

  fournisseurForm: FormGroup;
  detteForm: FormGroup;

  constructor(
    private fournisseurService: FournisseurService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.fournisseurForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(2)]],
      infoContact: ['', [Validators.required, Validators.email]],
      adresse: ['', [Validators.required]],
      telephone: ['', [Validators.required]],
      matriculeFiscale: ['', [Validators.required]]
    });

    this.detteForm = this.fb.group({
      numeroFacture: ['', [Validators.required]],
      titre: ['', [Validators.required]],
      description: [''],
      montantTotal: [0, [Validators.required, Validators.min(0)]],
      estPaye: [false],
      type: ['FOURNISSEUR'],
      datePaiementPrevue: ['', [Validators.required]],
      datePaiementReelle: [''],
      fournisseurId: ['', [Validators.required]],
      tranches: this.fb.array([])
    });
  }

  get tranches(): FormArray {
    return this.detteForm.get('tranches') as FormArray;
  }

  ngOnInit(): void {
    this.loadFournisseurs();
    this.loadDettes();
    
    // Load stats from backend (which calculates on all data, not paginated)
    this.updateStats();
    
    // Also subscribe to the observables for real-time updates
    this.fournisseurService.fournisseurs$.subscribe(() => {
      this.updateStats();
    });
    
    this.fournisseurService.dettes$.subscribe(() => {
      this.updateStats();
    });

    // Setup debounced numeroFacture validation
    this.setupNumeroFactureValidation();
  }

  setupNumeroFactureValidation(): void {
    // Subscribe to numeroFacture changes with debounce
    this.numeroFactureSubject.pipe(
      debounceTime(500), // Wait 500ms after user stops typing
      distinctUntilChanged(), // Only validate if value actually changed
      switchMap(numeroFacture => {
        // Clear previous errors
        this.numeroFactureError = '';
        this.numeroFactureValid = false;

        // Skip validation if empty
        if (!numeroFacture || numeroFacture.trim() === '') {
          this.isValidatingNumeroFacture = false;
          return of(false);
        }

        // Start validation
        this.isValidatingNumeroFacture = true;

        // Get current dette ID if editing
        const excludeDetteId = this.isEditMode && this.selectedDette ? this.selectedDette.id : undefined;

        // Check if numeroFacture exists
        return this.fournisseurService.checkNumeroFactureExists(numeroFacture, excludeDetteId);
      })
    ).subscribe({
      next: (exists) => {
        this.isValidatingNumeroFacture = false;
        if (exists) {
          this.numeroFactureError = "Une dette avec ce numéro de facture existe déjà";
          this.numeroFactureValid = false;
        } else {
          this.numeroFactureError = '';
          this.numeroFactureValid = true;
        }
      },
      error: () => {
        this.isValidatingNumeroFacture = false;
        this.numeroFactureError = '';
        this.numeroFactureValid = false;
      }
    });
  }

  // Called when user types in numeroFacture field
  onNumeroFactureChange(value: string): void {
    this.numeroFactureSubject.next(value);
  }

  // Check if form can be submitted (all validations pass)
  canSubmitDetteForm(): boolean {
    if (!this.detteForm.valid) return false;
    
    const numeroFacture = this.detteForm.get('numeroFacture')?.value;
    
    // If numeroFacture is provided, it must be valid
    if (numeroFacture && numeroFacture.trim() !== '') {
      if (this.isValidatingNumeroFacture) return false; // Still validating
      if (this.numeroFactureError) return false; // Has validation error
    }
    
    // Check tranche validation
    if (this.tranchesExceedTotal()) return false;
    
    return true;
  }

  loadData(): void {
    this.fournisseurService.fournisseurs$.subscribe(fournisseurs => {
      this.fournisseurs = fournisseurs;
      this.filteredFournisseurs = fournisseurs;
      this.filterFournisseurs();
      this.updateStats();
    });

    this.fournisseurService.dettes$.subscribe(dettes => {
      this.dettes = dettes;
      this.filteredDettes = dettes;
      this.filterDettes();
      this.updateStats();
    });
  }

  // Pagination methods for Fournisseurs
  loadFournisseurs(): void {
    this.loadFournisseursPaginated();
  }

  loadFournisseursPaginated(): void {
    this.fournisseurPagination.isLoading = true;
    const params: PaginationParams = {
      page: this.fournisseurPagination.currentPage,
      size: this.fournisseurPagination.pageSize,
      sortBy: this.fournisseurPagination.sortBy,
      sortDirection: this.fournisseurPagination.sortDirection
    };

    this.fournisseurService.getFournisseursPaginated(params).subscribe({
      next: (response: PageResponse<Fournisseur>) => {
        this.fournisseurs = response.content || [];
        this.filteredFournisseurs = response.content || [];
        this.fournisseurPagination.totalElements = response.totalElements;
        this.fournisseurPagination.totalPages = response.totalPages;
        this.fournisseurPagination.currentPage = response.page;
        this.fournisseurPagination.isLoading = false;
      },
      error: (error) => {
        this.fournisseurPagination.isLoading = false;
        this.fournisseurs = [];
        this.filteredFournisseurs = [];
        Swal.fire('Erreur', 'Erreur lors du chargement des fournisseurs', 'error');
      }
    });
  }

  onFournisseurPageChange(page: number): void {
    this.fournisseurPagination.currentPage = page;
    this.loadFournisseursPaginated();
  }

  onFournisseurPageSizeChange(size: number): void {
    this.fournisseurPagination.pageSize = size;
    this.fournisseurPagination.currentPage = 0;
    this.loadFournisseursPaginated();
  }

  onFournisseurSort(sortBy: string): void {
    if (this.fournisseurPagination.sortBy === sortBy) {
      this.fournisseurPagination.sortDirection = this.fournisseurPagination.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    } else {
      this.fournisseurPagination.sortBy = sortBy;
      this.fournisseurPagination.sortDirection = 'ASC';
    }
    this.fournisseurPagination.currentPage = 0;
    this.loadFournisseursPaginated();
  }

  getFournisseurPages(): number[] {
    const pages: number[] = [];
    const maxVisible = 5;
    let start = Math.max(0, this.fournisseurPagination.currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(this.fournisseurPagination.totalPages, start + maxVisible);
    
    if (end - start < maxVisible) {
      start = Math.max(0, end - maxVisible);
    }
    
    for (let i = start; i < end; i++) {
      pages.push(i);
    }
    return pages;
  }

  // Pagination methods for Dettes
  loadDettes(): void {
    this.loadDettesPaginated();
  }

  loadDettesPaginated(): void {
    this.dettePagination.isLoading = true;
    const params: PaginationParams = {
      page: this.dettePagination.currentPage,
      size: this.dettePagination.pageSize,
      sortBy: this.dettePagination.sortBy,
      sortDirection: this.dettePagination.sortDirection
    };

    this.fournisseurService.getDettesPaginated(params).subscribe({
      next: (response: PageResponse<DettesFournisseur>) => {
        this.dettes = response.content || [];
        this.filteredDettes = response.content || [];
        this.dettePagination.totalElements = response.totalElements;
        this.dettePagination.totalPages = response.totalPages;
        this.dettePagination.currentPage = response.page;
        this.dettePagination.isLoading = false;
        
        // Update filtered fournisseur dettes if viewing details
        if (this.currentView === 'details' && this.selectedFournisseur) {
          this.filterFournisseurDettes();
        }
      },
      error: (error) => {
        this.dettePagination.isLoading = false;
        this.dettes = [];
        this.filteredDettes = [];
        Swal.fire('Erreur', 'Erreur lors du chargement des dettes', 'error');
      }
    });
  }

  onDettePageChange(page: number): void {
    this.dettePagination.currentPage = page;
    this.loadDettesPaginated();
  }

  onDettePageSizeChange(size: number): void {
    this.dettePagination.pageSize = size;
    this.dettePagination.currentPage = 0;
    this.loadDettesPaginated();
  }

  onDetteSort(sortBy: string): void {
    if (this.dettePagination.sortBy === sortBy) {
      this.dettePagination.sortDirection = this.dettePagination.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    } else {
      this.dettePagination.sortBy = sortBy;
      this.dettePagination.sortDirection = 'ASC';
    }
    this.dettePagination.currentPage = 0;
    this.loadDettesPaginated();
  }

  getDettePages(): number[] {
    const pages: number[] = [];
    const maxVisible = 5;
    let start = Math.max(0, this.dettePagination.currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(this.dettePagination.totalPages, start + maxVisible);
    
    if (end - start < maxVisible) {
      start = Math.max(0, end - maxVisible);
    }
    
    for (let i = start; i < end; i++) {
      pages.push(i);
    }
    return pages;
  }

  updateStats(): void {
    this.fournisseurService.getStats().subscribe(stats => {
      this.stats = stats;
    });
  }

  setView(view: 'dashboard' | 'fournisseurs' | 'dettes' | 'details'): void {
    this.currentView = view;
    this.selectedFournisseur = null;
    this.selectedDette = null;
    this.isEditMode = false;
    this.showFournisseurForm = false;
    this.showDetteForm = false;
  }

  goBackToMenu(): void {
    this.router.navigate(['/menu']);
  }

  // Fournisseur Management
  showAddFournisseurForm(): void {
    this.selectedFournisseur = null;
    this.isEditMode = false;
    this.submitted = false;
    this.fournisseurForm.reset();
    this.showFournisseurForm = true;
    
    // Ensure we're on the fournisseurs view to show the form
    if (this.currentView !== 'fournisseurs') {
      this.currentView = 'fournisseurs';
    }
  }

  editFournisseur(fournisseur: Fournisseur): void {
    // Set the selected fournisseur and edit mode flags
    this.selectedFournisseur = { ...fournisseur }; // Create a copy to avoid reference issues
    this.isEditMode = true;
    this.submitted = false;
    
    // Populate the form with fournisseur data
    this.fournisseurForm.patchValue({
      nom: fournisseur.nom,
      infoContact: fournisseur.infoContact,
      adresse: fournisseur.adresse,
      telephone: fournisseur.telephone,
      matriculeFiscale: fournisseur.matriculeFiscale
    });
    
    // Show the form
    this.showFournisseurForm = true;
    
    // If currently on details page, switch to fournisseurs view to show the form
    if (this.currentView === 'details') {
      this.currentView = 'fournisseurs';
    }
  }

  closeFournisseurForm(): void {
    this.showFournisseurForm = false;
    this.submitted = false;
    this.isEditMode = false;
    this.selectedFournisseur = null;
    this.fournisseurForm.reset();
  }

  /**
   * @deprecated This method is no longer used. Fournisseur saving is now handled in fournisseur-list component.
   * Kept for backward compatibility only.
   */
  saveFournisseur(): void {
    // This method is deprecated - fournisseur-list component now handles save logic
  }

  deleteFournisseur(fournisseur: Fournisseur): void {
    Swal.fire({
      title: 'Êtes-vous sûr?',
      text: 'Cette action est irréversible!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Oui, supprimer!',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.fournisseurService.deleteFournisseur(fournisseur.id).subscribe({
          next: () => {
            Swal.fire('Supprimé!', 'Le fournisseur a été supprimé.', 'success');
          },
          error: (error) => {
            Swal.fire('Erreur', 'Erreur lors de la suppression: ' + error.message, 'error');
          }
        });
      }
    });
  }

  viewFournisseurDetails(fournisseur: Fournisseur): void {
    this.selectedFournisseur = fournisseur;
    this.currentView = 'details';
    
    // Initialize filtered dettes for this fournisseur
    this.filterFournisseurDettes();
    
    // Load tranches for all dettes of this fournisseur
    const dettes = this.getFournisseurDettes(fournisseur.id);
    dettes.forEach(dette => {
      this.loadTranchesForDette(dette.id);
    });
  }

  // Dashboard Component Event Handlers
  onDashboardViewChange(view: 'fournisseurs' | 'dettes'): void {
    this.setView(view);
  }

  onDashboardFournisseurSelect(fournisseur: Fournisseur): void {
    this.viewFournisseurDetails(fournisseur);
  }

  // FournisseurList Component Event Handlers
  onFournisseurListSearchTermChange(searchTerm: string): void {
    this.searchTerm = searchTerm;
    this.filterFournisseurs();
  }

  onFournisseurListFilterFournisseurs(): void {
    this.filterFournisseurs();
  }

  onFournisseurListSortChange(sortData: {field: string, direction: 'ASC' | 'DESC'}): void {
    this.fournisseurPagination.sortBy = sortData.field;
    this.fournisseurPagination.sortDirection = sortData.direction;
    this.onFournisseurSort(sortData.field);
  }

  onFournisseurListPageChange(page: number): void {
    this.onFournisseurPageChange(page);
  }

  onFournisseurListPageSizeChange(size: number): void {
    this.onFournisseurPageSizeChange(size);
  }

  onFournisseurListView(fournisseur: Fournisseur): void {
    this.viewFournisseurDetails(fournisseur);
  }

  onFournisseurListEdit(fournisseur: Fournisseur): void {
    this.editFournisseur(fournisseur);
  }

  onFournisseurListDelete(fournisseur: Fournisseur): void {
    this.deleteFournisseur(fournisseur);
  }

  onFournisseurListAdd(): void {
    this.showAddFournisseurForm();
  }

  onFournisseurListSaved(): void {
    // Reload fournisseurs list after save
    this.loadFournisseursPaginated();
  }

  onFournisseurListFormCancel(): void {
    this.closeFournisseurForm();
  }

  // Dette Management
  showAddDetteForm(): void {
    this.selectedDette = {} as DettesFournisseur; // Create empty dette object
    this.isEditMode = false;
    this.submitted = false;
    this.numeroFactureError = '';
    this.trancheValidationError = '';
    this.numeroFactureValid = false;
    this.isValidatingNumeroFacture = false;
    this.detteForm.reset();
    this.showDetteForm = true;
    
    // Ensure we're on the dettes view to show the form
    if (this.currentView !== 'dettes') {
      this.currentView = 'dettes';
    }
  }

  editDette(dette: DettesFournisseur): void {
    this.selectedDette = dette;
    this.isEditMode = true;
    this.submitted = false;
    this.numeroFactureError = '';
    this.trancheValidationError = '';
    this.numeroFactureValid = false;
    
    this.detteForm.patchValue({
      ...dette,
      datePaiementPrevue: this.formatDateForInput(dette.datePaiementPrevue),
      datePaiementReelle: dette.datePaiementReelle ? this.formatDateForInput(dette.datePaiementReelle) : ''
    });
    
    // Load existing tranches
    this.tranches.clear();
    if (dette.id) {
      this.fournisseurService.getTranchesById(dette.id).subscribe({
        next: (tranches) => {
          tranches.forEach(tranche => {
            const trancheGroup = this.fb.group({
              id: [tranche.id],
              montant: [tranche.montant, [Validators.required, Validators.min(0)]],
              dateEcheance: [this.formatDateForInput(tranche.dateEcheance), [Validators.required]],
              datePaiement: [tranche.datePaiement ? this.formatDateForInput(tranche.datePaiement) : ''],
              estPaye: [tranche.estPaye || false]
            });
            this.tranches.push(trancheGroup);
          });
          
          // Validate tranches after loading
          this.validateTranches();
        },
        error: () => {
          // Error loading tranches - silently handled
        }
      });
    }
    
    this.showDetteForm = true;
    
    // If currently on details page, switch to dettes view to show the form
    if (this.currentView === 'details') {
      this.currentView = 'dettes';
    }
  }

  // Tranche management methods
  addTranche(): void {
    const trancheGroup = this.fb.group({
      id: [null],
      montant: [0, [Validators.required, Validators.min(0)]],
      dateEcheance: ['', [Validators.required]],
      datePaiement: [''],
      estPaye: [false]
    });
    this.tranches.push(trancheGroup);
  }

  removeTranche(index: number): void {
    const tranche = this.tranches.at(index);
    const trancheId = tranche.get('id')?.value;
    
    if (trancheId) {
      // If tranche has an ID, it exists in backend, so delete it
      Swal.fire({
        title: 'Supprimer cette tranche?',
        text: 'Cette action est irréversible!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Oui, supprimer!',
        cancelButtonText: 'Annuler'
      }).then((result) => {
        if (result.isConfirmed) {
          this.fournisseurService.deleteTranche(trancheId).subscribe({
            next: () => {
              this.tranches.removeAt(index);
              Swal.fire('Supprimé!', 'La tranche a été supprimée.', 'success');
            },
            error: (error) => {
              Swal.fire('Erreur', 'Erreur lors de la suppression: ' + error.message, 'error');
            }
          });
        }
      });
    } else {
      // New tranche not yet saved, just remove from form
      this.tranches.removeAt(index);
    }
  }

  getTotalTranches(): number {
    return this.tranches.controls.reduce((sum, control) => {
      return sum + (control.get('montant')?.value || 0);
    }, 0);
  }

  // Get available amount for new tranches
  getAvailableAmount(): number {
    const montantTotal = this.detteForm.get('montantTotal')?.value || 0;
    const totalTranches = this.getTotalTranches();
    return Math.max(0, montantTotal - totalTranches);
  }

  // Check if tranches exceed montantTotal
  tranchesExceedTotal(): boolean {
    const montantTotal = this.detteForm.get('montantTotal')?.value || 0;
    const totalTranches = this.getTotalTranches();
    return totalTranches > montantTotal;
  }

  // Validate tranches and update error message
  validateTranches(): void {
    this.trancheValidationError = '';
    
    if (this.tranchesExceedTotal()) {
      const montantTotal = this.detteForm.get('montantTotal')?.value || 0;
      const totalTranches = this.getTotalTranches();
      this.trancheValidationError = `Le total des tranches (${totalTranches.toFixed(2)} DT) dépasse le montant total (${montantTotal.toFixed(2)} DT)`;
    }
  }

  // Called when tranche amount changes
  onTrancheAmountChange(): void {
    this.validateTranches();
  }

  // Called when montantTotal changes
  onMontantTotalChange(): void {
    this.validateTranches();
  }

  closeDetteForm(): void {
    this.showDetteForm = false;
    this.submitted = false;
    this.detteForm.reset();
    this.tranches.clear();
    this.numeroFactureError = '';
    this.trancheValidationError = '';
    this.numeroFactureValid = false;
    this.isValidatingNumeroFacture = false;
  }

  // NOTE: saveDette logic has been moved to dette-management.component.ts
  // This method is kept for backward compatibility but should not be used
  saveDette(): void {
    // This method is deprecated - dette-management component now handles save logic
  }

  deleteDette(dette: DettesFournisseur): void {
    Swal.fire({
      title: 'Êtes-vous sûr?',
      text: 'Cette action est irréversible!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Oui, supprimer!',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.fournisseurService.deleteDette(dette.id).subscribe({
          next: () => {
            Swal.fire('Supprimé!', 'La dette a été supprimée.', 'success');
            this.loadDettesPaginated();
          },
          error: (error) => {
            Swal.fire('Erreur', 'Erreur lors de la suppression: ' + error.message, 'error');
          }
        });
      }
    });
  }

  markAsPaid(dette: DettesFournisseur): void {
    const currentDate = new Date().toISOString().split('T')[0];
    
    const updateData = {
      estPaye: true,
      datePaiementReelle: currentDate
    };

    this.fournisseurService.updateDette(dette.id, updateData).subscribe({
      next: () => {
        Swal.fire('Succès', 'Dette marquée comme payée!', 'success');
        this.loadDettesPaginated();
      },
      error: (error) => {
        Swal.fire('Erreur', 'Erreur lors de la mise à jour: ' + error.message, 'error');
      }
    });
  }

  // Filter methods
  filterFournisseurs(): void {
    // If search term is empty, load paginated data normally
    if (!this.searchTerm || this.searchTerm.trim() === '') {
      this.loadFournisseursPaginated();
      return;
    }
    
    // Call search API with pagination
    this.fournisseurPagination.isLoading = true;
    this.fournisseurPagination.currentPage = 0; // Reset to first page on search
    
    const searchCriteria = {
      nom: this.searchTerm,
      infoContact: this.searchTerm,
      adresse: this.searchTerm
    };
    
    const paginationParams: PaginationParams = {
      page: this.fournisseurPagination.currentPage,
      size: this.fournisseurPagination.pageSize,
      sortBy: this.fournisseurPagination.sortBy,
      sortDirection: this.fournisseurPagination.sortDirection
    };
    
    this.fournisseurService.searchFournisseursPaginated(searchCriteria, paginationParams).subscribe({
      next: (response: PageResponse<Fournisseur>) => {
        this.fournisseurs = response.content || [];
        this.filteredFournisseurs = this.fournisseurs;
        this.fournisseurPagination.totalElements = response.totalElements || 0;
        this.fournisseurPagination.totalPages = response.totalPages || 0;
        this.fournisseurPagination.currentPage = response.page || 0;
        this.fournisseurPagination.isLoading = false;
      },
      error: (error) => {
        this.fournisseurs = [];
        this.filteredFournisseurs = [];
        this.fournisseurPagination.isLoading = false;
        Swal.fire('Erreur', 'Erreur lors de la recherche des fournisseurs', 'error');
      }
    });
  }

  filterDettes(): void {
    // If search term is empty and no filters, load paginated data normally
    if ((!this.searchTermDette || this.searchTermDette.trim() === '') && 
        this.filterPaid === '' && 
        this.filterOverdue === '') {
      this.loadDettesPaginated();
      return;
    }
    
    // Call search API with pagination
    this.dettePagination.isLoading = true;
    this.dettePagination.currentPage = 0; // Reset to first page on search
    
    const searchCriteria: any = {};
    
    // Add search term if provided
    if (this.searchTermDette && this.searchTermDette.trim() !== '') {
      searchCriteria.numeroFacture = this.searchTermDette;
      searchCriteria.titre = this.searchTermDette;
      searchCriteria.fournisseurNom = this.searchTermDette;
    }
    
    // Add paid filter if selected
    if (this.filterPaid !== '') {
      searchCriteria.estPaye = this.filterPaid === 'true';
    }
    
    // Add overdue filter if selected
    if (this.filterOverdue === 'true') {
      searchCriteria.overdue = true;
    }
    
    const paginationParams: PaginationParams = {
      page: this.dettePagination.currentPage,
      size: this.dettePagination.pageSize,
      sortBy: this.dettePagination.sortBy,
      sortDirection: this.dettePagination.sortDirection
    };
    
    this.fournisseurService.searchDettesPaginated(searchCriteria, paginationParams).subscribe({
      next: (response: PageResponse<DettesFournisseur>) => {
        this.dettes = response.content || [];
        this.filteredDettes = this.dettes;
        this.dettePagination.totalElements = response.totalElements || 0;
        this.dettePagination.totalPages = response.totalPages || 0;
        this.dettePagination.currentPage = response.page || 0;
        this.dettePagination.isLoading = false;
      },
      error: (error) => {
        this.dettes = [];
        this.filteredDettes = [];
        this.dettePagination.isLoading = false;
        Swal.fire('Erreur', 'Erreur lors de la recherche des dettes', 'error');
      }
    });
  }

  // Utility methods
  getFournisseurName(fournisseurId: string): string {
    if (!this.fournisseurs || !Array.isArray(this.fournisseurs)) {
      return 'N/A';
    }
    const fournisseur = this.fournisseurs.find(f => f.id === fournisseurId);
    return fournisseur ? fournisseur.nom : 'N/A';
  }

  formatDateForInput(date: Date | string): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toISOString().split('T')[0];
  }

  formatDate(date: Date | string): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('fr-FR');
  }

  isOverdue(dette: DettesFournisseur): boolean {
    return !dette.estPaye && new Date(dette.datePaiementPrevue) < new Date();
  }

  getDetteStatusClass(dette: DettesFournisseur): string {
    if (dette.estPaye) return 'badge-success';
    if (this.isOverdue(dette)) return 'badge-danger';
    return 'badge-warning';
  }

  getDetteStatusText(dette: DettesFournisseur): string {
    if (dette.estPaye) return 'Payée';
    if (this.isOverdue(dette)) return 'En retard';
    return 'En attente';
  }

  cancelForm(): void {
    if (this.showFournisseurForm) {
      this.closeFournisseurForm();
    } else if (this.showDetteForm) {
      this.closeDetteForm();
    }
  }

  // New dashboard methods
  getTopFournisseurs(): Fournisseur[] {
    if (!this.fournisseurs || !Array.isArray(this.fournisseurs)) {
      return [];
    }
    
    return [...this.fournisseurs]
      .sort((a, b) => (b.totalDettes?.parsedValue || 0) - (a.totalDettes?.parsedValue || 0))
      .slice(0, 5);
  }

  getRankClass(index: number): string {
    switch (index) {
      case 0: return 'rank-gold';
      case 1: return 'rank-silver';
      case 2: return 'rank-bronze';
      default: return 'rank-default';
    }
  }

  getRecentDettes(): DettesFournisseur[] {
    return this.dettes
      .sort((a, b) => new Date(b.datePaiementPrevue).getTime() - new Date(a.datePaiementPrevue).getTime())
      .slice(0, 5);
  }

  getTrendIcon(current: number, previous: number): string {
    if (current > previous) return '📈';
    if (current < previous) return '📉';
    return '➡️';
  }

  getTrendPercentage(current: number, previous: number): number {
    if (previous === 0) return 0;
    return ((current - previous) / previous) * 100;
  }

  getActivePercentage(): number {
    if (this.stats.totalFournisseurs === 0) return 0;
    return (this.stats.fournisseursActifs / this.stats.totalFournisseurs) * 100;
  }

  // FOOLPROOF percentage calculation - ONLY uses count numbers, ignores backend percentages
  getNormalizedPercentages(): { payees: number; impayes: number } {
    if (!this.rapportDettes || !this.rapportDettes.analyseStatut) {
      return { payees: 0, impayes: 0 };
    }
    
    // Get raw numbers from backend (these should be reliable)
    const nombrePayees = Number(this.rapportDettes.analyseStatut.nombreDettesPayees) || 0;
    const nombreImpayes = Number(this.rapportDettes.analyseStatut.nombreDettesImpayes) || 0;
    const total = nombrePayees + nombreImpayes;
    
    if (total === 0) {
      return { payees: 0, impayes: 0 };
    }
    
    // Calculate percentages from scratch
    const payeesPercent = (nombrePayees / total) * 100;
    const impayesPercent = (nombreImpayes / total) * 100;
    
    // Round to 1 decimal place
    const finalPayees = Math.round(payeesPercent * 10) / 10;
    const finalImpayes = Math.round(impayesPercent * 10) / 10;
    
    return { 
      payees: finalPayees, 
      impayes: finalImpayes 
    };
  }

  // Legacy methods kept for compatibility but now use the normalized calculation
  getCorrectPourcentagePayees(): number {
    return this.getNormalizedPercentages().payees;
  }

  getCorrectPourcentageImpayes(): number {
    return this.getNormalizedPercentages().impayes;
  }

  // Enhanced details view methods
  getInitials(name: string): string {
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  getActiveStatus(fournisseur: Fournisseur): string {
    if (!this.dettes || !Array.isArray(this.dettes)) {
      return 'Inactif';
    }
    
    const unpaidDettes = this.dettes.filter(d => 
      d.fournisseurId === fournisseur.id && !d.estPaye
    );
    return unpaidDettes.length > 0 ? 'Actif' : 'Inactif';
  }

  getActiveStatusClass(fournisseur: Fournisseur): string {
    const status = this.getActiveStatus(fournisseur);
    return status === 'Actif' ? 'stat-badge-success' : 'stat-badge-neutral';
  }

  // Debt management methods
  getFournisseurDettes(fournisseurId: string): DettesFournisseur[] {
    if (!this.dettes || !Array.isArray(this.dettes)) {
      return [];
    }
    return this.dettes.filter(dette => dette.fournisseurId === fournisseurId);
  }

  getTotalDettesAmount(fournisseurId: string): number {
    return this.getFournisseurDettes(fournisseurId)
      .reduce((total, dette) => total + dette.montantDu, 0);
  }

  getUnpaidDettesAmount(fournisseurId: string): number {
    return this.getFournisseurDettes(fournisseurId)
      .filter(dette => !dette.estPaye)
      .reduce((total, dette) => total + dette.montantDu, 0);
  }

  getPaidDettesAmount(fournisseurId: string): number {
    return this.getFournisseurDettes(fournisseurId)
      .filter(dette => dette.estPaye)
      .reduce((total, dette) => total + dette.montantDu, 0);
  }

  getOverdueDettesCount(fournisseurId: string): number {
    return this.getFournisseurDettes(fournisseurId)
      .filter(dette => !dette.estPaye && this.isOverdue(dette)).length;
  }

  getFilteredFournisseurDettes(fournisseurId: string): DettesFournisseur[] {
    let dettes = this.getFournisseurDettes(fournisseurId);

    // Apply filter
    switch (this.debtFilter) {
      case 'unpaid':
        dettes = dettes.filter(d => !d.estPaye);
        break;
      case 'paid':
        dettes = dettes.filter(d => d.estPaye);
        break;
      case 'overdue':
        dettes = dettes.filter(d => !d.estPaye && this.isOverdue(d));
        break;
    }

    // Apply sort
    switch (this.debtSort) {
      case 'date-desc':
        dettes.sort((a, b) => new Date(b.datePaiementPrevue).getTime() - new Date(a.datePaiementPrevue).getTime());
        break;
      case 'date-asc':
        dettes.sort((a, b) => new Date(a.datePaiementPrevue).getTime() - new Date(b.datePaiementPrevue).getTime());
        break;
      case 'amount-desc':
        dettes.sort((a, b) => b.montantDu - a.montantDu);
        break;
      case 'amount-asc':
        dettes.sort((a, b) => a.montantDu - b.montantDu);
        break;
    }

    return dettes;
  }

  filterFournisseurDettes(): void {
    if (!this.selectedFournisseur) {
      this.filteredFournisseurDettes = [];
      return;
    }
    this.filteredFournisseurDettes = this.getFilteredFournisseurDettes(this.selectedFournisseur.id);
  }

  sortFournisseurDettes(): void {
    this.filterFournisseurDettes();
  }

  getDetteItemClass(dette: DettesFournisseur): string {
    let classes = ['debt-item'];
    if (dette.estPaye) classes.push('paid');
    if (!dette.estPaye && this.isOverdue(dette)) classes.push('overdue');
    return classes.join(' ');
  }

  getDateClass(dette: DettesFournisseur): string {
    if (dette.estPaye) return 'date-paid';
    if (this.isOverdue(dette)) return 'date-overdue';
    return 'date-pending';
  }

  // Action methods
  addDetteForFournisseur(fournisseur: Fournisseur): void {
    this.selectedFournisseur = fournisseur;
    this.detteForm.patchValue({
      fournisseurId: fournisseur.id
    });
    this.showDetteForm = true;
    
    // If currently on details page, switch to dettes view to show the form
    if (this.currentView === 'details') {
      this.currentView = 'dettes';
    }
  }

  refreshFournisseurDettes(fournisseurId: string): void {
    // Use the specific fournisseur dettes endpoint for better performance and accuracy
    this.fournisseurService.getDettesByFournisseurId(fournisseurId).subscribe({
      next: (dettes) => {
        // Update the local dettes array with fresh data for this fournisseur
        // Remove old dettes for this fournisseur
        this.dettes = this.dettes.filter(dette => dette.fournisseurId !== fournisseurId);
        // Add the fresh dettes
        this.dettes = [...this.dettes, ...dettes];
        this.filteredDettes = [...this.dettes];
        
        // Clear and reload tranches for all dettes
        this.detteTranches.clear();
        dettes.forEach(dette => {
          this.loadTranchesForDette(dette.id);
        });
        
        // Update the service's dettes subject with the fresh data
        this.fournisseurService.dettes$.subscribe(allDettes => {
          // Update the service's dettes array
          const updatedDettes = allDettes.filter(dette => dette.fournisseurId !== fournisseurId);
          this.fournisseurService['dettesSubject'].next([...updatedDettes, ...dettes]);
        });
      },
      error: (error) => {
        // Error refreshing fournisseur dettes
      }
    });
  }

  // Force immediate reload of current fournisseur dettes (for use after create/update operations)
  forceReloadCurrentFournisseurDettes(): void {
    if (this.selectedFournisseur && this.currentView === 'details') {
      this.refreshFournisseurDettes(this.selectedFournisseur.id);
    }
  }

  markDetteAsPaid(dette: DettesFournisseur): void {
    Swal.fire({
      title: 'Confirmer le paiement',
      text: `Marquer cette dette de ${dette.montantDu} DT comme payée?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Oui, marquer comme payée',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        const currentDate = new Date().toISOString().split('T')[0];
        
        const updateData = {
          estPaye: true,
          datePaiementReelle: currentDate
        };
        
        this.fournisseurService.updateDette(dette.id, updateData).subscribe({
          next: () => {
            Swal.fire('Succès!', 'Dette marquée comme payée.', 'success');
            this.loadDettesPaginated();
          },
          error: (error) => {
            let errorMessage = 'Erreur lors de la mise à jour.';
            if (error.error && error.error.message) {
              errorMessage = error.error.message;
            } else if (error.message) {
              errorMessage = error.message;
            }
            
            Swal.fire('Erreur!', `Erreur lors de la mise à jour: ${errorMessage}`, 'error');
          }
        });
      }
    });
  }

  showDetteDetails(dette: DettesFournisseur): void {
    this.selectedDette = dette;
    
    // Load tranches for this dette
    this.loadTranchesForDette(dette.id, true);
    
    // Wait a moment for tranches to load
    setTimeout(() => {
      const tranches = this.getTranchesForDette(dette.id);
      const fournisseurName = this.getFournisseurName(dette.fournisseurId);
      
      let tranchesHtml = '';
      if (tranches.length > 0) {
        tranchesHtml = `
          <div style="margin-top: 1.5rem; padding-top: 1rem; border-top: 2px solid #e5e7eb;">
            <h4 style="color: #667eea; margin-bottom: 0.75rem;">💰 Tranches de Paiement (${tranches.length})</h4>
            ${tranches.map((tranche, i) => `
              <div style="background: ${tranche.estPaye ? '#f0fdf4' : '#fef3c7'}; padding: 0.75rem; border-radius: 6px; margin-bottom: 0.5rem; border-left: 3px solid ${tranche.estPaye ? '#22c55e' : '#f59e0b'};">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <strong style="color: #1f2937;">Tranche ${i + 1}</strong>
                  <span style="font-weight: 600; color: ${tranche.estPaye ? '#22c55e' : '#f59e0b'};">${tranche.estPaye ? '✓ Payée' : '⏳ En attente'}</span>
                </div>
                <div style="margin-top: 0.5rem; font-size: 0.9rem; color: #4b5563;">
                  <div>💵 Montant: <strong>${tranche.montant} DT</strong></div>
                  <div>📅 Échéance: ${this.formatDate(tranche.dateEcheance)}</div>
                  ${tranche.estPaye && tranche.datePaiement ? `<div style="color: #22c55e;">✓ Payée le: ${this.formatDate(tranche.datePaiement)}</div>` : ''}
                </div>
              </div>
            `).join('')}
            <div style="margin-top: 0.75rem; padding: 0.5rem; background: #eff6ff; border-radius: 6px; text-align: right;">
              <strong>Total Payé via Tranches:</strong> <span style="color: #22c55e; font-size: 1.1rem;">${this.getTotalPaidTranches(dette.id)} DT</span>
            </div>
          </div>
        `;
      }
      
      Swal.fire({
        title: 'Détails de la Dette',
        html: `
          <div style="text-align: left;">
            ${dette.titre ? `<div style="background: #667eea; color: white; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem;">
              <h3 style="margin: 0; font-size: 1.1rem;">📌 ${dette.titre}</h3>
            </div>` : ''}
            
            <div style="background: #f8fafc; padding: 1rem; border-radius: 8px; margin-bottom: 1rem;">
              <p style="margin: 0.5rem 0;"><strong>🏢 Fournisseur:</strong> ${fournisseurName}</p>
              ${dette.numeroFacture ? `<p style="margin: 0.5rem 0;"><strong>🧾 N° Facture:</strong> ${dette.numeroFacture}</p>` : ''}
              ${dette.description ? `<p style="margin: 0.5rem 0;"><strong>📝 Description:</strong> ${dette.description}</p>` : ''}
            </div>
            
            <div style="background: #fef3c7; padding: 1rem; border-radius: 8px; margin-bottom: 1rem;">
              <p style="margin: 0.5rem 0; font-size: 1.2rem;"><strong>💰 Montant Dû:</strong> <span style="color: #d97706; font-size: 1.5rem;">${dette.montantDu} DT</span></p>
              ${dette.montantTotal ? `<p style="margin: 0.5rem 0;"><strong>Montant Total:</strong> ${dette.montantTotal} DT</p>` : ''}
              <p style="margin: 0.5rem 0;"><strong>📊 Statut:</strong> <span style="background: ${dette.estPaye ? '#dcfce7' : '#fee2e2'}; padding: 0.25rem 0.75rem; border-radius: 12px; color: ${dette.estPaye ? '#166534' : '#991b1b'};">${this.getDetteStatusText(dette)}</span></p>
            </div>
            
            <div style="background: #eff6ff; padding: 1rem; border-radius: 8px;">
              <p style="margin: 0.5rem 0;"><strong>📅 Date Échéance:</strong> ${this.formatDate(dette.datePaiementPrevue)}</p>
              ${dette.datePaiementReelle ? `<p style="margin: 0.5rem 0; color: #22c55e;"><strong>✓ Payée le:</strong> ${this.formatDate(dette.datePaiementReelle)}</p>` : ''}
            </div>
            
            ${tranchesHtml}
          </div>
        `,
        width: '600px',
        confirmButtonText: 'Fermer',
        confirmButtonColor: '#667eea'
      });
    }, 500);
  }

  exportFournisseurData(fournisseur: Fournisseur): void {
    const dettes = this.getFournisseurDettes(fournisseur.id);
    const data = {
      fournisseur: fournisseur,
      dettes: dettes,
      statistics: {
        totalDettes: this.getTotalDettesAmount(fournisseur.id),
        dettesNonPayees: this.getUnpaidDettesAmount(fournisseur.id),
        dettesPayees: this.getPaidDettesAmount(fournisseur.id),
        dettesEnRetard: this.getOverdueDettesCount(fournisseur.id)
      }
    };

    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `fournisseur_${fournisseur.nom}_export.json`;
    link.click();
    window.URL.revokeObjectURL(url);

    Swal.fire('Exporté!', 'Les données ont été exportées avec succès.', 'success');
  }

  // Timeline methods
  getFournisseurTimeline(fournisseurId: string): any[] {
    const dettes = this.getFournisseurDettes(fournisseurId);
    const timeline: any[] = [];

    dettes.forEach(dette => {
      // Add creation event
      const detteTitle = dette.titre ? `Dette: ${dette.titre}` : `Dette de ${dette.montantDu} DT`;
      timeline.push({
        type: 'debt_created',
        title: 'Dette créée',
        description: detteTitle,
        date: dette.datePaiementPrevue, // Using this as creation date placeholder
        amount: dette.montantDu,
        detteId: dette.id
      });

      // Add tranche payment events
      const tranches = this.getTranchesForDette(dette.id);
      tranches.forEach((tranche, index) => {
        if (tranche.estPaye && tranche.datePaiement) {
          timeline.push({
            type: 'tranche_paid',
            title: `Tranche ${index + 1} payée`,
            description: `Paiement partiel pour "${dette.titre || 'Dette'}"`,
            date: tranche.datePaiement,
            amount: tranche.montant,
            detteId: dette.id
          });
        } else {
          // Add upcoming tranche due dates
          timeline.push({
            type: 'tranche_due',
            title: `Tranche ${index + 1} échéance`,
            description: `Paiement de ${tranche.montant} DT dû pour "${dette.titre || 'Dette'}"`,
            date: tranche.dateEcheance,
            amount: tranche.montant,
            detteId: dette.id,
            isPending: !tranche.estPaye
          });
        }
      });

      // Add full payment event if paid
      if (dette.estPaye && dette.datePaiementReelle) {
        timeline.push({
          type: 'debt_paid',
          title: 'Dette payée intégralement',
          description: `Paiement complet pour "${dette.titre || 'Dette'}"`,
          date: dette.datePaiementReelle,
          amount: dette.montantDu,
          detteId: dette.id
        });
      }
    });

    return timeline.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
  }

  getTimelineMarkerClass(event: any): string {
    switch (event.type) {
      case 'debt_created': return 'timeline-marker-created';
      case 'debt_paid': return 'timeline-marker-paid';
      case 'tranche_paid': return 'timeline-marker-tranche-paid';
      case 'tranche_due': return event.isPending ? 'timeline-marker-tranche-pending' : 'timeline-marker-tranche-due';
      default: return 'timeline-marker-default';
    }
  }

  // Track by functions for performance
  trackByDetteId(index: number, dette: DettesFournisseur): string {
    return dette.id;
  }

  trackByFournisseurId(index: number, fournisseur: Fournisseur): string {
    return fournisseur.id;
  }

  trackByTrancheId(index: number, tranche: TranchePaiement): string | number {
    return tranche.id || index;
  }

  // Load tranches for a specific debt
  loadTranchesForDette(detteId: string, forceReload: boolean = false): void {
    if (!this.detteTranches.has(detteId) || forceReload) {
      // Remove from cache if force reload
      if (forceReload) {
        this.detteTranches.delete(detteId);
      }
      this.fournisseurService.getTranchesById(detteId).subscribe({
        next: (tranches) => {
          this.detteTranches.set(detteId, tranches);
        },
        error: () => {
          this.detteTranches.set(detteId, []);
        }
      });
    }
  }

  // Get tranches for a specific debt
  getTranchesForDette(detteId: string): TranchePaiement[] {
    if (!this.detteTranches.has(detteId)) {
      this.loadTranchesForDette(detteId);
      return [];
    }
    return this.detteTranches.get(detteId) || [];
  }

  // Get total paid amount from tranches
  getTotalPaidTranches(detteId: string): number {
    const tranches = this.getTranchesForDette(detteId);
    return tranches
      .filter(t => t.estPaye)
      .reduce((sum, t) => sum + t.montant, 0);
  }

    // Get count of paid tranches
  getPaidTranchesCount(detteId: string): number {
    const tranches = this.getTranchesForDette(detteId);
    return tranches.filter(t => t.estPaye).length;
  }

  // Get solde total fournisseur from any dette of that fournisseur
  getSoldeTotalFournisseur(fournisseurId: string): number {
    if (!this.dettes || !Array.isArray(this.dettes)) {
      return 0;
    }
    
    const fournisseurDettes = this.dettes.filter(d => d.fournisseurId === fournisseurId);
    if (fournisseurDettes.length > 0) {
      const firstDette: any = fournisseurDettes[0];
      // Return from API if available
      if (firstDette.soldeTotalFournisseur !== undefined && firstDette.soldeTotalFournisseur !== null) {
        return firstDette.soldeTotalFournisseur;
      }
    }
    // Fallback: calculate from soldeRestant of all dettes
    return fournisseurDettes.reduce((sum, d) => sum + (d.soldeRestant || 0), 0);
  }

  // Debt Report Methods
  showRapport() {
    this.currentView = 'rapport';
    this.loadRapportDettes();
  }

  loadRapportDettes() {
    this.isLoadingRapport = true;
    this.rapportDettes = null; // Clear previous data
    
    const params: RapportFilterParams = {
      inclureDettesPayees: this.rapportInclureDettesPayees
    };

    if (this.rapportDateDebut) {
      params.dateDebut = this.rapportDateDebut;
    }
    if (this.rapportDateFin) {
      params.dateFin = this.rapportDateFin;
    }

    this.fournisseurService.getRapportDettes(params).subscribe({
      next: (rapport) => {
        this.rapportDettes = rapport;
        this.isLoadingRapport = false;
        
        // Validate data integrity for percentages
        if (rapport.analyseStatut) {
          const payees = rapport.analyseStatut.nombreDettesPayees || 0;
          const impayes = rapport.analyseStatut.nombreDettesImpayes || 0;
          const total = payees + impayes;
          
          if (total > 0) {
            // Force correct percentages calculation
            const correctPayeesPct = (payees / total) * 100;
            const correctImpayesPct = (impayes / total) * 100;

            // FORCE override backend percentages with correct ones
            rapport.analyseStatut.pourcentageDettesPayees = correctPayeesPct;
            rapport.analyseStatut.pourcentageDettesImpayes = correctImpayesPct;
          }
        }
      },
      error: (error) => {
        this.isLoadingRapport = false;
        this.rapportDettes = null;
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de charger le rapport des dettes. Veuillez vérifier votre connexion et réessayer.'
        });
      }
    });
  }

  applyRapportFilter() {
    this.loadRapportDettes();
  }

  clearRapportFilter() {
    this.rapportDateDebut = '';
    this.rapportDateFin = '';
    this.rapportInclureDettesPayees = true;
    this.loadRapportDettes();
  }

  getEvaluationClass(evaluation: string): string {
    switch (evaluation) {
      case 'BON':
        return 'badge-success';
      case 'MOYEN':
        return 'badge-warning';
      case 'CRITIQUE':
        return 'badge-danger';
      default:
        return 'badge-secondary';
    }
  }

  getTendanceClass(tendance: string): string {
    switch (tendance) {
      case 'EN_AMELIORATION':
        return 'text-success';
      case 'STABLE':
        return 'text-info';
      case 'EN_DETERIORATION':
        return 'text-danger';
      default:
        return 'text-secondary';
    }
  }

  getTendanceIcon(tendance: string): string {
    switch (tendance) {
      case 'EN_AMELIORATION':
        return '📈';
      case 'STABLE':
        return '➡️';
      case 'EN_DETERIORATION':
        return '📉';
      default:
        return '•';
    }
  }

  getNiveauClass(niveau: string): string {
    switch (niveau) {
      case 'URGENT':
        return 'alert-danger';
      case 'IMPORTANT':
        return 'alert-warning';
      case 'INFO':
        return 'alert-info';
      default:
        return 'alert-secondary';
    }
  }

  printRapport() {
    if (!this.rapportDettes) {
      Swal.fire({
        icon: 'warning',
        title: 'Attention',
        text: 'Aucun rapport à imprimer. Veuillez d\'abord charger le rapport.'
      });
      return;
    }

    const printContent = this.generateRapportPrintContent();
    const printWindow = window.open('', '_blank', 'width=1200,height=800');

    if (printWindow) {
      printWindow.document.write(`
        <!DOCTYPE html>
        <html lang="fr">
          <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Rapport des Dettes Fournisseurs - ${new Date(this.rapportDettes.infosGenerales.dateGeneration).toLocaleDateString('fr-FR')}</title>
            <style>
              * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
              }
              
              body { 
                font-family: 'Segoe UI', 'Arial', sans-serif; 
                margin: 0;
                padding: 30px 40px; 
                color: #2c3e50; 
                background-color: #fff; 
                line-height: 1.6;
                font-size: 13px;
              }
              
              .report-header {
                text-align: center;
                margin-bottom: 30px;
                padding-bottom: 20px;
                border-bottom: 3px solid #667eea;
              }
              
              .report-logo {
                font-size: 48px;
                margin-bottom: 10px;
              }
              
              h1 { 
                color: #2c3e50;
                font-size: 28px;
                font-weight: 700;
                margin: 10px 0;
                letter-spacing: -0.5px;
              }
              
              .report-subtitle {
                color: #6c757d;
                font-size: 14px;
                font-style: italic;
                margin-top: 5px;
              }
              
              h2 { 
                color: #667eea;
                font-size: 20px;
                font-weight: 600;
                margin: 30px 0 15px 0;
                padding-left: 12px;
                border-left: 4px solid #667eea;
                display: flex;
                align-items: center;
                gap: 8px;
              }
              
              h3 { 
                color: #495057;
                font-size: 16px;
                font-weight: 600;
                margin: 20px 0 12px 0;
              }
              
              .section {
                margin-bottom: 30px;
                page-break-inside: avoid;
              }
              
              .info-grid {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 12px;
                margin-bottom: 20px;
                background: #f8f9fa;
                padding: 20px;
                border-radius: 8px;
              }
              
              .info-item {
                display: flex;
                justify-content: space-between;
                padding: 10px 12px;
                background: white;
                border-left: 3px solid #667eea;
                border-radius: 4px;
              }
              
              .label {
                font-weight: 600;
                color: #495057;
                font-size: 13px;
              }
              
              .value {
                color: #2c3e50;
                font-weight: 600;
                font-size: 13px;
              }
              
              .stats-grid {
                display: grid;
                grid-template-columns: repeat(4, 1fr);
                gap: 15px;
                margin: 20px 0;
              }
              
              .stat-card {
                padding: 20px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                border-radius: 10px;
                text-align: center;
                color: white;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
              }
              
              .stat-number {
                font-size: 22px;
                font-weight: 700;
                margin-bottom: 5px;
                display: block;
              }
              
              .stat-label {
                font-size: 11px;
                opacity: 0.95;
                font-weight: 500;
                text-transform: uppercase;
                letter-spacing: 0.5px;
              }
              
              .status-chart {
                margin: 20px 0;
                background: #f8f9fa;
                padding: 20px;
                border-radius: 8px;
              }
              
              .status-item {
                margin: 12px 0;
                padding: 15px;
                background: white;
                border-radius: 6px;
              }
              
              .status-bar {
                height: 25px;
                border-radius: 12px;
                margin-bottom: 8px;
                position: relative;
                overflow: hidden;
                background: #e9ecef;
              }
              
              .status-bar-fill {
                height: 100%;
                border-radius: 12px;
                transition: width 0.3s ease;
              }
              
              .status-bar-fill.paid {
                background: linear-gradient(90deg, #28a745 0%, #20c997 100%);
              }
              
              .status-bar-fill.unpaid {
                background: linear-gradient(90deg, #dc3545 0%, #fd7e14 100%);
              }
              
              .status-label {
                display: flex;
                justify-content: space-between;
                font-weight: 600;
                color: #2c3e50;
                font-size: 13px;
              }
              
              table {
                width: 100%;
                border-collapse: collapse;
                margin: 20px 0;
                background: white;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
                border-radius: 8px;
                overflow: hidden;
              }
              
              th, td {
                border: 1px solid #dee2e6;
                padding: 12px 10px;
                text-align: left;
                font-size: 12px;
              }
              
              th {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                font-weight: 600;
                text-transform: uppercase;
                font-size: 11px;
                letter-spacing: 0.5px;
              }
              
              tbody tr:nth-child(even) {
                background-color: #f8f9fa;
              }
              
              tbody tr:hover {
                background-color: #e9ecef;
              }
              
              .evaluation-bon { 
                color: #28a745; 
                font-weight: 700;
                padding: 4px 8px;
                background: #d4edda;
                border-radius: 4px;
                display: inline-block;
              }
              
              .evaluation-moyen { 
                color: #ffc107; 
                font-weight: 700;
                padding: 4px 8px;
                background: #fff3cd;
                border-radius: 4px;
                display: inline-block;
              }
              
              .evaluation-critique { 
                color: #dc3545; 
                font-weight: 700;
                padding: 4px 8px;
                background: #f8d7da;
                border-radius: 4px;
                display: inline-block;
              }
              
              .footer {
                margin-top: 50px;
                padding-top: 20px;
                border-top: 2px solid #e9ecef;
                text-align: center;
                font-size: 11px;
                color: #6c757d;
              }
              
              .footer-date {
                font-weight: 600;
                color: #495057;
                margin-bottom: 5px;
              }
              
              .footer-company {
                font-style: italic;
              }
              
              .page-break {
                page-break-before: always;
                margin-top: 30px;
              }
              
              @media print {
                body { 
                  margin: 15px;
                  padding: 0;
                }
                
                .page-break { 
                  page-break-before: always; 
                }
                
                .section {
                  break-inside: avoid;
                }
                
                table {
                  break-inside: auto;
                }
                
                tr {
                  break-inside: avoid;
                  break-after: auto;
                }
                
                thead {
                  display: table-header-group;
                }
                
                tfoot {
                  display: table-footer-group;
                }
                
                .stat-card, .status-item {
                  box-shadow: none;
                  break-inside: avoid;
                }
              }
              
              @page {
                margin: 1.5cm;
                size: A4;
              }
            </style>
          </head>
          <body>
            ${printContent}
            <script>
              window.onload = function() {
                setTimeout(function() {
                  window.print();
                }, 250);
              }
              
              window.onafterprint = function() {
                window.close();
              }
            </script>
          </body>
        </html>
      `);
      printWindow.document.close();
    }
  }

  private generateRapportPrintContent(): string {
    if (!this.rapportDettes) return '';

    const dateGeneration = new Date(this.rapportDettes.infosGenerales.dateGeneration).toLocaleDateString('fr-FR', {
      year: 'numeric', month: 'long', day: 'numeric'
    });
    
    const periodeDebut = this.rapportDettes.infosGenerales.periodeDebut ? 
      new Date(this.rapportDettes.infosGenerales.periodeDebut).toLocaleDateString('fr-FR') : '';
    const periodeFin = this.rapportDettes.infosGenerales.periodeFin ? 
      new Date(this.rapportDettes.infosGenerales.periodeFin).toLocaleDateString('fr-FR') : '';

    return `
      <div class="report-header">
        <div class="report-logo">📊</div>
        <h1>Rapport des Dettes Fournisseurs</h1>
        <p class="report-subtitle">Analyse complète de la situation financière</p>
      </div>
      
      <div class="section">
        <h2>ℹ️ Informations Générales</h2>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">Date de génération</span>
            <span class="value">${dateGeneration}</span>
          </div>
          ${periodeDebut && periodeFin ? `
          <div class="info-item">
            <span class="label">Période d'analyse</span>
            <span class="value">${periodeDebut} → ${periodeFin}</span>
          </div>
          ` : ''}
          <div class="info-item">
            <span class="label">Nombre total de dettes</span>
            <span class="value">${this.rapportDettes.infosGenerales.nombreTotalDettes}</span>
          </div>
          <div class="info-item">
            <span class="label">Fournisseurs actifs</span>
            <span class="value">${this.rapportDettes.infosGenerales.nombreFournisseursActifs}</span>
          </div>
        </div>
      </div>

      <div class="section">
        <h2>💰 Synthèse Financière</h2>
        <div class="stats-grid">
          <div class="stat-card">
            <span class="stat-number">${this.rapportDettes.syntheseFinanciere.montantTotalDettes.toLocaleString('fr-FR', {minimumFractionDigits: 2})} DT</span>
            <span class="stat-label">Montant Total</span>
          </div>
          <div class="stat-card">
            <span class="stat-number">${this.rapportDettes.syntheseFinanciere.montantTotalPaye.toLocaleString('fr-FR', {minimumFractionDigits: 2})} DT</span>
            <span class="stat-label">Montant Payé (${this.rapportDettes.syntheseFinanciere.tauxPaiement.toFixed(1)}%)</span>
          </div>
          <div class="stat-card">
            <span class="stat-number">${this.rapportDettes.syntheseFinanciere.montantTotalImpaye.toLocaleString('fr-FR', {minimumFractionDigits: 2})} DT</span>
            <span class="stat-label">Montant Impayé</span>
          </div>
          <div class="stat-card">
            <span class="stat-number">${this.rapportDettes.syntheseFinanciere.montantEnRetard.toLocaleString('fr-FR', {minimumFractionDigits: 2})} DT</span>
            <span class="stat-label">En Retard</span>
          </div>
        </div>
      </div>

      <div class="section">
        <h2>📊 Distribution des Statuts</h2>
        <div class="status-chart">
          <div class="status-item">
            <div class="status-label">
              <span>✅ Dettes Payées: ${this.rapportDettes.analyseStatut.nombreDettesPayees}</span>
              <span>${this.getNormalizedPercentages().payees.toFixed(1)}%</span>
            </div>
            <div class="status-bar">
              <div class="status-bar-fill paid" style="width: ${this.getNormalizedPercentages().payees}%"></div>
            </div>
          </div>
          <div class="status-item">
            <div class="status-label">
              <span>⏳ Dettes Impayées: ${this.rapportDettes.analyseStatut.nombreDettesImpayes}</span>
              <span>${this.getNormalizedPercentages().impayes.toFixed(1)}%</span>
            </div>
            <div class="status-bar">
              <div class="status-bar-fill unpaid" style="width: ${this.getNormalizedPercentages().impayes}%"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="page-break"></div>

      <div class="section">
        <h2>🏭 Analyse par Fournisseur</h2>
        <table>
          <thead>
            <tr>
              <th>Fournisseur</th>
              <th style="text-align: center;">Nb Dettes</th>
              <th style="text-align: right;">Montant Total</th>
              <th style="text-align: right;">Montant Payé</th>
              <th style="text-align: right;">Montant Impayé</th>
              <th style="text-align: center;">Taux</th>
              <th style="text-align: center;">Évaluation</th>
            </tr>
          </thead>
          <tbody>
            ${this.rapportDettes.analyseParFournisseur.map(analyse => `
              <tr>
                <td><strong>${analyse.nomFournisseur}</strong></td>
                <td style="text-align: center;">${analyse.nombreDettes}</td>
                <td style="text-align: right;">${analyse.montantTotal.toLocaleString('fr-FR', {minimumFractionDigits: 2})} DT</td>
                <td style="text-align: right;">${analyse.montantPaye.toLocaleString('fr-FR', {minimumFractionDigits: 2})} DT</td>
                <td style="text-align: right;">${analyse.montantImpaye.toLocaleString('fr-FR', {minimumFractionDigits: 2})} DT</td>
                <td style="text-align: center;"><strong>${analyse.tauxPaiement.toFixed(1)}%</strong></td>
                <td style="text-align: center;">
                  <span class="evaluation-${analyse.evaluation.toLowerCase()}">${analyse.evaluation}</span>
                </td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>

      ${this.rapportDettes.analyseTemporelle.distributionMensuelle.donneesParMois.length > 0 ? `
      <div class="section">
        <h2>📅 Distribution Mensuelle</h2>
        <table>
          <thead>
            <tr>
              <th>Mois</th>
              <th style="text-align: center;">Nb Dettes</th>
              <th style="text-align: right;">Montant Total</th>
              <th style="text-align: right;">Montant Payé</th>
              <th style="text-align: right;">Montant Impayé</th>
            </tr>
          </thead>
          <tbody>
            ${this.rapportDettes.analyseTemporelle.distributionMensuelle.donneesParMois.map(mois => `
              <tr>
                <td><strong>${mois.mois}</strong></td>
                <td style="text-align: center;">${mois.nombreDettes}</td>
                <td style="text-align: right;">${mois.montantTotal.toLocaleString('fr-FR', {minimumFractionDigits: 2})} DT</td>
                <td style="text-align: right;">${mois.montantPaye.toLocaleString('fr-FR', {minimumFractionDigits: 2})} DT</td>
                <td style="text-align: right;">${mois.montantImpaye.toLocaleString('fr-FR', {minimumFractionDigits: 2})} DT</td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
      ` : ''}

      <div class="footer">
        <p class="footer-date">Rapport généré le ${new Date().toLocaleDateString('fr-FR', {
          weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
        })} à ${new Date().toLocaleTimeString('fr-FR')}</p>
        <p class="footer-company">Système de Gestion Fournisseurs - Platform Company</p>
      </div>
    `;
  }

  // ===============================
  // Event Handlers for Integrated Debt Management
  // ===============================

  onDetteManagementSearchTermChange(searchTerm: string): void {
    this.searchTermDette = searchTerm;
  }

  onDetteManagementFilterPaidChange(filterPaid: string): void {
    this.filterPaid = filterPaid;
  }

  onDetteManagementFilterOverdueChange(filterOverdue: string): void {
    this.filterOverdue = filterOverdue;
  }

  onDetteManagementFilterDettes(): void {
    this.filterDettes();
  }

  onDetteManagementSortChange(sortParams: { sortBy: string; sortDirection: 'ASC' | 'DESC' }): void {
    this.dettePagination.sortBy = sortParams.sortBy;
    this.dettePagination.sortDirection = sortParams.sortDirection;
    this.dettePagination.currentPage = 0;
    this.loadDettes();
  }

  onDetteManagementPageChange(page: number): void {
    this.onDettePageChange(page);
  }

  onDetteManagementPageSizeChange(pageSize: number): void {
    this.onDettePageSizeChange(pageSize);
  }

  onDetteManagementView(dette: DettesFournisseur): void {
    this.showDetteDetails(dette);
  }

  onDetteManagementEdit(dette: DettesFournisseur): void {
    this.editDette(dette);
  }

  onDetteManagementDelete(dette: DettesFournisseur): void {
    this.deleteDette(dette);
  }

  onDetteManagementAdd(): void {
    this.showAddDetteForm();
  }

  onDetteManagementMarkPaid(dette: DettesFournisseur): void {
    this.markAsPaid(dette);
  }

  onDetteManagementFormCancel(): void {
    this.closeDetteForm();
  }

  /**
   * Handle dette created event from dette-management component
   */
  onDetteCreated(): void {
    this.loadDettes();
    this.updateStats();
  }

  /**
   * Handle dette updated event from dette-management component
   */
  onDetteUpdated(): void {
    this.loadDettes();
    this.updateStats();
  }

  // ===============================
  // Event Handlers for FournisseurReportComponent
  // ===============================

  onRapportDateDebutChange(dateDebut: string): void {
    this.rapportDateDebut = dateDebut;
  }

  onRapportDateFinChange(dateFin: string): void {
    this.rapportDateFin = dateFin;
  }

  onRapportInclureDettesPayeesChange(inclure: boolean): void {
    this.rapportInclureDettesPayees = inclure;
  }

  onSelectedFournisseurIdsChange(ids: string[]): void {
    this.selectedFournisseurIds = ids;
  }

  onRapportApplyFilter(filterParams: RapportFilterParams): void {
    this.genererRapportWithParams(filterParams);
  }

  onRapportClearFilter(): void {
    this.clearRapportFilter();
  }

  onRapportPrint(): void {
    this.printRapport();
  }

  onRapportExport(format: string): void {
    if (format === 'pdf') {
      this.exportRapportToPDF();
    } else if (format === 'excel') {
      this.exportRapportToExcel();
    }
  }

  // ===============================
  // Helper Methods for Report Component
  // ===============================

  private genererRapportWithParams(filterParams: RapportFilterParams): void {
    this.isLoadingRapport = true;
    
    const params = {
      dateDebut: filterParams.dateDebut || null,
      dateFin: filterParams.dateFin || null,
      inclureDettesPayees: filterParams.inclureDettesPayees || false,
      fournisseurIds: filterParams.fournisseurIds || null
    };

    this.fournisseurService.getRapportDettes(params).subscribe({
      next: (rapport: RapportDettesEntreprise) => {
        this.rapportDettes = rapport;
        this.isLoadingRapport = false;
      },
      error: (error: any) => {
        this.isLoadingRapport = false;
        Swal.fire({
          title: 'Erreur',
          text: 'Impossible de générer le rapport',
          icon: 'error',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  private exportRapportToPDF(): void {
    if (!this.rapportDettes) {
      Swal.fire({
        title: 'Attention',
        text: 'Aucun rapport disponible à exporter',
        icon: 'warning',
        confirmButtonText: 'OK'
      });
      return;
    }

    // TODO: Implement PDF export functionality
    Swal.fire({
      title: 'Fonction en développement',
      text: 'L\'export PDF sera disponible prochainement',
      icon: 'info',
      confirmButtonText: 'OK'
    });
  }

  private exportRapportToExcel(): void {
    if (!this.rapportDettes) {
      Swal.fire({
        title: 'Attention',
        text: 'Aucun rapport disponible à exporter',
        icon: 'warning',
        confirmButtonText: 'OK'
      });
      return;
    }

    // TODO: Implement Excel export functionality
    Swal.fire({
      title: 'Fonction en développement',
      text: 'L\'export Excel sera disponible prochainement',
      icon: 'info',
      confirmButtonText: 'OK'
    });
  }
}
