import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { Fournisseur, DettesFournisseur, TranchePaiement } from '../../../models/fournisseur/fournisseur.model';
import { FournisseurService } from '../../../services/fournisseur.service';
import Swal from 'sweetalert2';
import { Subject, debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';

interface DetteFormData {
  id?: string;
  numeroFacture: string;
  titre: string;
  description: string;
  montantTotal: number;
  estPaye: boolean;
  type: string;
  datePaiementPrevue: string;
  datePaiementReelle: string;
  fournisseurId: string;
  tranches: TranchePaiement[];
}

@Component({
  selector: 'app-dette-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './dette-management.component.html',
  styleUrls: ['./dette-management.component.css']
})
export class DetteManagementComponent implements OnInit, OnChanges {
  // Expose Math to template
  Math = Math;

  @Input() dettes: DettesFournisseur[] = [];
  @Input() filteredDettes: DettesFournisseur[] = [];
  @Input() fournisseurs: Fournisseur[] = [];
  @Input() pagination = {
    currentPage: 0,
    pageSize: 10,
    totalElements: 0,
    totalPages: 0,
    sortBy: 'datePaiementPrevue',
    sortDirection: 'ASC' as 'ASC' | 'DESC',
    isLoading: false
  };

  @Input() searchTermDette = '';
  @Input() filterPaid = '';
  @Input() filterOverdue = '';
  @Input() showForm = false;
  @Input() isEditMode = false;
  @Input() selectedDette: DettesFournisseur | null = null;
  @Input() submitted = false;
  @Input() numeroFactureError = '';
  @Input() numeroFactureValid = false;
  @Input() isValidatingNumeroFacture = false;
  @Input() trancheValidationError = '';

  @Output() searchTermChange = new EventEmitter<string>();
  @Output() filterPaidChange = new EventEmitter<string>();
  @Output() filterOverdueChange = new EventEmitter<string>();
  @Output() filterDettes = new EventEmitter<void>();
  @Output() sortChange = new EventEmitter<{sortBy: string, sortDirection: 'ASC' | 'DESC'}>();
  @Output() pageChange = new EventEmitter<number>();
  @Output() pageSizeChange = new EventEmitter<number>();
  @Output() detteDetails = new EventEmitter<DettesFournisseur>();
  @Output() detteMarkPaid = new EventEmitter<DettesFournisseur>();
  @Output() detteEdit = new EventEmitter<DettesFournisseur>();
  @Output() detteDelete = new EventEmitter<DettesFournisseur>();
  @Output() detteAdd = new EventEmitter<void>();
  @Output() detteSave = new EventEmitter<DettesFournisseur>();
  @Output() formCancel = new EventEmitter<void>();
  @Output() numeroFactureChange = new EventEmitter<string>();
  @Output() trancheAdd = new EventEmitter<void>();
  @Output() trancheRemove = new EventEmitter<number>();
  @Output() detteCreated = new EventEmitter<void>();
  @Output() detteUpdated = new EventEmitter<void>();

  // Page size options
  pageSizeOptions = [10, 25, 50, 100];

  // Form
  detteForm: FormGroup;

  // Debounce subject for numeroFacture validation
  private numeroFactureSubject = new Subject<string>();

  constructor(
    private fb: FormBuilder,
    private fournisseurService: FournisseurService
  ) {
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
    // Initialize component
    // Setup debounced numeroFacture validation
    this.setupNumeroFactureValidation();
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Handle changes to selectedDette to populate form
    if (changes['selectedDette'] && changes['selectedDette'].currentValue) {
      this.populateForm(changes['selectedDette'].currentValue);
    }
  }

  /**
   * Setup numeroFacture validation with debounce
   */
  setupNumeroFactureValidation(): void {
    this.numeroFactureSubject.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(numeroFacture => {
        this.numeroFactureError = '';
        this.numeroFactureValid = false;

        if (!numeroFacture || numeroFacture.trim() === '') {
          this.isValidatingNumeroFacture = false;
          return of(false);
        }

        this.isValidatingNumeroFacture = true;
        const excludeDetteId = this.isEditMode && this.selectedDette ? this.selectedDette.id : undefined;
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

  /**
   * Handle search term changes
   */
  onSearchTermChange(value: string): void {
    this.searchTermDette = value;
    this.searchTermChange.emit(value);
    this.filterDettes.emit();
  }

  /**
   * Handle paid filter changes
   */
  onFilterPaidChange(value: string): void {
    this.filterPaid = value;
    this.filterPaidChange.emit(value);
    this.filterDettes.emit();
  }

  /**
   * Handle overdue filter changes
   */
  onFilterOverdueChange(value: string): void {
    this.filterOverdue = value;
    this.filterOverdueChange.emit(value);
    this.filterDettes.emit();
  }

  /**
   * Handle sorting
   */
  onSort(field: string): void {
    const newDirection = this.pagination.sortBy === field && this.pagination.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    this.pagination.sortBy = field;
    this.pagination.sortDirection = newDirection;
    this.sortChange.emit({ sortBy: field, sortDirection: newDirection });
  }

  /**
   * Handle page change
   */
  onPageChange(page: number): void {
    this.pageChange.emit(page);
  }

  /**
   * Handle page size change
   */
  onPageSizeChange(size: number): void {
    this.pageSizeChange.emit(size);
  }

  /**
   * Show dette details
   */
  showDetteDetails(dette: DettesFournisseur): void {
    this.detteDetails.emit(dette);
  }

  /**
   * Mark dette as paid
   */
  markAsPaid(dette: DettesFournisseur): void {
    this.detteMarkPaid.emit(dette);
  }

  /**
   * Edit dette
   */
  editDette(dette: DettesFournisseur): void {
    this.selectedDette = dette;
    this.isEditMode = true;
    this.populateForm(dette);
    this.showForm = true;
    this.detteEdit.emit(dette);
  }

  /**
   * Delete dette
   */
  deleteDette(dette: DettesFournisseur): void {
    this.detteDelete.emit(dette);
  }

  /**
   * Add new dette
   */
  showAddDetteForm(): void {
    this.isEditMode = false;
    this.selectedDette = null;
    this.detteForm.reset();
    this.submitted = false;
    this.showForm = true;
    this.detteAdd.emit();
  }

  /**
   * Cancel form
   */
  cancelForm(): void {
    this.showForm = false;
    this.isEditMode = false;
    this.selectedDette = null;
    this.detteForm.reset();
    this.tranches.clear();
    this.submitted = false;
    this.numeroFactureError = '';
    this.trancheValidationError = '';
    this.numeroFactureValid = false;
    this.isValidatingNumeroFacture = false;
    this.formCancel.emit();
  }

  /**
   * Save dette - COMPLETE LOGIC (moved from parent component)
   */
  saveDette(): void {
    this.submitted = true;
    this.numeroFactureError = '';
    this.trancheValidationError = '';

    // Validate numeroFacture if provided
    const numeroFacture = this.detteForm.get('numeroFacture')?.value;
    if (numeroFacture && numeroFacture.trim() !== '' && this.numeroFactureError) {
      Swal.fire('Erreur de validation', this.numeroFactureError, 'error');
      return;
    }

    // Client-side validation for tranches
    if (this.tranchesExceedTotal()) {
      this.validateTranches();
      Swal.fire('Erreur de validation', this.trancheValidationError, 'error');
      return;
    }

    // Check if form is valid and can be submitted
    if (!this.canSubmitDetteForm()) {
      Swal.fire('Erreur de validation', 'Veuillez corriger les erreurs dans le formulaire', 'error');
      return;
    }

    if (this.detteForm.valid) {
      const formValue = this.detteForm.value;
      const { tranches, ...detteData } = formValue;

      if (this.isEditMode && this.selectedDette) {
        // UPDATE EXISTING DETTE
        this.fournisseurService.updateDette(this.selectedDette.id, detteData).subscribe({
          next: () => {
            // Handle tranches update/creation
            if (tranches && tranches.length > 0) {
              const tranchePromises = tranches.map((tranche: any) => {
                if (tranche.id) {
                  // Update existing tranche
                  return this.fournisseurService.updateTranche(tranche.id, tranche).toPromise();
                } else {
                  // Create new tranche
                  return this.fournisseurService.createTranche({
                    ...tranche,
                    dettesFournisseurId: this.selectedDette!.id
                  }).toPromise().catch(err => {
                    if (err.status === 400) {
                      this.trancheValidationError = err.message;
                      throw err;
                    }
                    throw err;
                  });
                }
              });

              Promise.all(tranchePromises)
                .then(() => {
                  Swal.fire('Succès', 'Dette et tranches modifiées avec succès!', 'success');
                  this.detteUpdated.emit();
                  this.cancelForm();
                })
                .catch((error) => {
                  if (error.status === 400) {
                    Swal.fire('Erreur de validation', this.trancheValidationError || error.message, 'error');
                  } else {
                    Swal.fire('Attention', 'Dette modifiée mais erreur lors de la modification des tranches', 'warning');
                  }
                  this.detteUpdated.emit();
                  this.cancelForm();
                });
            } else {
              Swal.fire('Succès', 'Dette modifiée avec succès!', 'success');
              this.detteUpdated.emit();
              this.cancelForm();
            }
          },
          error: (error) => {
            if (error.status === 409) {
              this.numeroFactureError = error.message || "Une dette avec ce numéro de facture existe déjà";
              Swal.fire('Erreur de validation', this.numeroFactureError, 'error');
            } else {
              Swal.fire('Erreur', 'Erreur lors de la modification: ' + (error.message || 'Erreur inconnue'), 'error');
            }
          }
        });
      } else {
        // CREATE NEW DETTE
        this.fournisseurService.createDette(detteData).subscribe({
          next: (createdDette) => {
            // Verify that we have a valid dette with an ID
            if (!createdDette || !createdDette.id) {
              Swal.fire('Erreur', 'Dette créée mais l\'ID est manquant', 'error');
              this.cancelForm();
              return;
            }

            // Create tranches if any
            if (tranches && tranches.length > 0) {
              const tranchePromises = tranches.map((tranche: any) => 
                this.fournisseurService.createTranche({
                  montant: tranche.montant,
                  dateEcheance: tranche.dateEcheance,
                  datePaiement: tranche.datePaiement || null,
                  estPaye: tranche.estPaye || false,
                  dettesFournisseurId: createdDette.id
                }).toPromise().catch(err => {
                  if (err.status === 400) {
                    this.trancheValidationError = err.error?.message || err.message;
                    throw err;
                  }
                  throw err;
                })
              );

              Promise.all(tranchePromises)
                .then(() => {
                  Swal.fire('Succès', 'Dette et tranches créées avec succès!', 'success');
                  this.detteCreated.emit();
                  this.cancelForm();
                })
                .catch((error) => {
                  if (error.status === 400) {
                    Swal.fire('Erreur de validation', this.trancheValidationError || error.error?.message || error.message, 'error');
                  } else {
                    Swal.fire('Attention', 'Dette créée mais erreur lors de la création des tranches', 'warning');
                  }
                  this.detteCreated.emit();
                  this.cancelForm();
                });
            } else {
              Swal.fire('Succès', 'Dette créée avec succès!', 'success');
              this.detteCreated.emit();
              this.cancelForm();
            }
          },
          error: (error) => {
            if (error.status === 409) {
              this.numeroFactureError = error.message || "Une dette avec ce numéro de facture existe déjà";
              Swal.fire('Erreur de validation', this.numeroFactureError, 'error');
            } else {
              Swal.fire('Erreur', 'Erreur lors de la création: ' + (error.message || 'Erreur inconnue'), 'error');
            }
          }
        });
      }
    }
  }

  /**
   * Check if form can be submitted (all validations pass)
   */
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

  /**
   * Get total of all tranches
   */
  getTotalTranches(): number {
    return this.tranches.controls.reduce((sum, control) => {
      return sum + (control.get('montant')?.value || 0);
    }, 0);
  }

  /**
   * Get available amount for new tranches
   */
  getAvailableAmount(): number {
    const montantTotal = this.detteForm.get('montantTotal')?.value || 0;
    const totalTranches = this.getTotalTranches();
    return Math.max(0, montantTotal - totalTranches);
  }

  /**
   * Check if tranches exceed montantTotal
   */
  tranchesExceedTotal(): boolean {
    const montantTotal = this.detteForm.get('montantTotal')?.value || 0;
    const totalTranches = this.getTotalTranches();
    return totalTranches > montantTotal;
  }

  /**
   * Validate tranches and update error message
   */
  validateTranches(): void {
    this.trancheValidationError = '';
    
    if (this.tranchesExceedTotal()) {
      const montantTotal = this.detteForm.get('montantTotal')?.value || 0;
      const totalTranches = this.getTotalTranches();
      this.trancheValidationError = `Le total des tranches (${totalTranches.toFixed(2)} DT) dépasse le montant total (${montantTotal.toFixed(2)} DT)`;
    }
  }

  /**
   * Called when tranche amount changes
   */
  onTrancheAmountChange(): void {
    this.validateTranches();
  }

  /**
   * Called when montantTotal changes
   */
  onMontantTotalChange(): void {
    this.validateTranches();
  }

  /**
   * Handle numero facture change
   */
  onNumeroFactureChange(value: string): void {
    this.numeroFactureSubject.next(value);
  }

  /**
   * Add tranche
   */
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

  /**
   * Remove tranche
   */
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
              this.validateTranches();
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
      this.validateTranches();
    }
  }

  /**
   * Get fournisseur name by ID
   */
  getFournisseurName(fournisseurId: string): string {
    const fournisseur = this.fournisseurs.find(f => f.id === fournisseurId);
    return fournisseur?.nom || 'Fournisseur introuvable';
  }

  /**
   * Get dette status class
   */
  getDetteStatusClass(dette: DettesFournisseur): string {
    if (dette.estPaye) {
      return 'status-paid';
    } else if (this.isOverdue(dette)) {
      return 'status-overdue';
    } else {
      return 'status-pending';
    }
  }

  /**
   * Get dette status text
   */
  getDetteStatusText(dette: DettesFournisseur): string {
    if (dette.estPaye) {
      return '✅ Payée';
    } else if (this.isOverdue(dette)) {
      return '🚨 En retard';
    } else {
      return '⏳ En cours';
    }
  }

  /**
   * Check if dette is overdue
   */
  isOverdue(dette: DettesFournisseur): boolean {
    if (dette.estPaye) return false;
    const today = new Date();
    const dueDate = new Date(dette.datePaiementPrevue);
    return dueDate < today;
  }

  /**
   * Populate form with dette data
   */
  private populateForm(dette: DettesFournisseur): void {
    this.detteForm.patchValue({
      numeroFacture: dette.numeroFacture,
      titre: dette.titre,
      description: dette.description,
      montantTotal: dette.montantTotal || dette.montantDu,
      estPaye: dette.estPaye,
      type: dette.type || 'FOURNISSEUR',
      datePaiementPrevue: dette.datePaiementPrevue ? new Date(dette.datePaiementPrevue).toISOString().split('T')[0] : '',
      datePaiementReelle: dette.datePaiementReelle ? new Date(dette.datePaiementReelle).toISOString().split('T')[0] : '',
      fournisseurId: dette.fournisseurId
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
              dateEcheance: [new Date(tranche.dateEcheance).toISOString().split('T')[0], [Validators.required]],
              datePaiement: [tranche.datePaiement ? new Date(tranche.datePaiement).toISOString().split('T')[0] : ''],
              estPaye: [tranche.estPaye || false]
            });
            this.tranches.push(trancheGroup);
          });
          
          // Validate tranches after loading
          this.validateTranches();
        },
        error: (error) => {
          // Error loading tranches
        }
      });
    }
  }

  /**
   * Get form control for validation
   */
  getFormControl(fieldName: string) {
    return this.detteForm.get(fieldName);
  }

  /**
   * Check if form field has error
   */
  hasError(fieldName: string, errorType: string): boolean {
    const control = this.getFormControl(fieldName);
    return !!(control && control.errors && control.errors[errorType] && (control.touched || this.submitted));
  }

  /**
   * Get pagination pages array
   */
  getPaginationPages(): number[] {
    const pages: number[] = [];
    const totalPages = this.pagination.totalPages;
    const currentPage = this.pagination.currentPage;
    
    let start = Math.max(0, currentPage - 2);
    let end = Math.min(totalPages - 1, currentPage + 2);
    
    if (end - start < 4 && totalPages > 5) {
      if (start === 0) {
        end = Math.min(totalPages - 1, start + 4);
      } else if (end === totalPages - 1) {
        start = Math.max(0, end - 4);
      }
    }
    
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    
    return pages;
  }

  /**
   * Get form data for shared component
   */
  getFormDataForSharedComponent(): DetteFormData | null {
    if (!this.selectedDette) {
      return null;
    }

    return {
      id: this.selectedDette.id,
      numeroFacture: this.selectedDette.numeroFacture || '',
      titre: this.selectedDette.titre || '',
      description: this.selectedDette.description || '',
      montantTotal: this.selectedDette.montantTotal,
      estPaye: this.selectedDette.estPaye,
      type: this.selectedDette.type || 'FOURNISSEUR',
      datePaiementPrevue: this.selectedDette.datePaiementPrevue instanceof Date 
        ? this.selectedDette.datePaiementPrevue.toISOString().split('T')[0] 
        : '',
      datePaiementReelle: this.selectedDette.datePaiementReelle instanceof Date 
        ? this.selectedDette.datePaiementReelle.toISOString().split('T')[0] 
        : this.selectedDette.datePaiementReelle?.toString() || '',
      fournisseurId: this.selectedDette.fournisseurId,
      tranches: this.selectedDette.tranches || []
    };
  }

  /**
   * Handle form save from shared component
   */
  handleFormSave(formData: DetteFormData): void {
    const dette: DettesFournisseur = {
      id: formData.id || '',
      numeroFacture: formData.numeroFacture,
      titre: formData.titre,
      description: formData.description,
      montantTotal: formData.montantTotal,
      montantDu: formData.montantTotal, // Default to montantTotal
      estPaye: formData.estPaye,
      type: 'FOURNISSEUR',
      datePaiementPrevue: new Date(formData.datePaiementPrevue),
      datePaiementReelle: formData.datePaiementReelle ? new Date(formData.datePaiementReelle) : null,
      fournisseurId: formData.fournisseurId,
      tranches: formData.tranches || []
    };

    this.detteSave.emit(dette);
  }

  /**
   * TrackBy function for ngFor to improve performance
   */
  trackByDetteId(index: number, dette: DettesFournisseur): string {
    return dette.id;
  }
}