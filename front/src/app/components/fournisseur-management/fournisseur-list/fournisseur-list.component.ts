import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Fournisseur, PageResponse, PaginationParams } from '../../../models/fournisseur/fournisseur.model';
import { FournisseurService } from '../../../services/fournisseur.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-fournisseur-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './fournisseur-list.component.html',
  styleUrls: ['./fournisseur-list.component.css']
})
export class FournisseurListComponent implements OnInit, OnChanges {
  // Expose Math to template
  Math = Math;

  @Input() fournisseurs: Fournisseur[] = [];
  @Input() filteredFournisseurs: Fournisseur[] = [];
  @Input() pagination = {
    currentPage: 0,
    pageSize: 10,
    totalElements: 0,
    totalPages: 0,
    sortBy: 'nom',
    sortDirection: 'ASC' as 'ASC' | 'DESC',
    isLoading: false
  };

  @Input() searchTerm = '';
  @Input() showForm = false;
  @Input() isEditMode = false;
  @Input() selectedFournisseur: Fournisseur | null = null;

  @Output() searchTermChange = new EventEmitter<string>();
  @Output() filterFournisseurs = new EventEmitter<void>();
  @Output() sortChange = new EventEmitter<{field: string, direction: 'ASC' | 'DESC'}>();
  @Output() pageChange = new EventEmitter<number>();
  @Output() pageSizeChange = new EventEmitter<number>();
  @Output() fournisseurView = new EventEmitter<Fournisseur>();
  @Output() fournisseurEdit = new EventEmitter<Fournisseur>();
  @Output() fournisseurDelete = new EventEmitter<Fournisseur>();
  @Output() fournisseurAdd = new EventEmitter<void>();
  @Output() fournisseurSaved = new EventEmitter<void>(); // Notify parent after successful save
  @Output() formCancel = new EventEmitter<void>();

  // Page size options
  pageSizeOptions = [10, 25, 50, 100];

  // Form
  fournisseurForm: FormGroup;
  submitted = false;

  constructor(
    private fb: FormBuilder,
    private fournisseurService: FournisseurService
  ) {
    this.fournisseurForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(2)]],
      infoContact: ['', [Validators.required, Validators.email]],
      adresse: ['', [Validators.required]],
      telephone: ['', [Validators.required]],
      matriculeFiscale: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    // Initialize component
  }

  /**
   * Handle input changes from parent component
   */
  ngOnChanges(changes: SimpleChanges): void {
    // When selectedFournisseur changes and we're in edit mode, populate the form
    if (changes['selectedFournisseur'] && this.selectedFournisseur && this.isEditMode && this.showForm) {
      this.populateForm(this.selectedFournisseur);
    }
    
    // When showForm becomes true and we have a selectedFournisseur in edit mode
    if (changes['showForm'] && this.showForm && this.isEditMode && this.selectedFournisseur) {
      this.populateForm(this.selectedFournisseur);
    }
    
    // When isEditMode changes to true with a selectedFournisseur
    if (changes['isEditMode'] && this.isEditMode && this.selectedFournisseur && this.showForm) {
      this.populateForm(this.selectedFournisseur);
    }
  }

  /**
   * Handle search term changes
   */
  onSearchTermChange(value: string): void {
    this.searchTerm = value;
    this.searchTermChange.emit(value);
    this.filterFournisseurs.emit();
  }

  /**
   * Handle sorting
   */
  onSort(field: string): void {
    const newDirection = this.pagination.sortBy === field && this.pagination.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    this.pagination.sortBy = field;
    this.pagination.sortDirection = newDirection;
    this.sortChange.emit({ field, direction: newDirection });
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
   * View fournisseur details
   */
  viewFournisseurDetails(fournisseur: Fournisseur): void {
    this.fournisseurView.emit(fournisseur);
  }

  /**
   * Edit fournisseur
   */
  editFournisseur(fournisseur: Fournisseur): void {
    this.selectedFournisseur = fournisseur;
    this.isEditMode = true;
    this.populateForm(fournisseur);
    this.showForm = true;
    this.fournisseurEdit.emit(fournisseur);
  }

  /**
   * Delete fournisseur
   */
  deleteFournisseur(fournisseur: Fournisseur): void {
    this.fournisseurDelete.emit(fournisseur);
  }

  /**
   * Add new fournisseur
   */
  showAddFournisseurForm(): void {
    this.isEditMode = false;
    this.selectedFournisseur = null;
    this.fournisseurForm.reset();
    this.submitted = false;
    this.showForm = true;
    this.fournisseurAdd.emit();
  }

  /**
   * Cancel form
   */
  cancelForm(): void {
    this.showForm = false;
    this.isEditMode = false;
    this.selectedFournisseur = null;
    this.fournisseurForm.reset();
    this.submitted = false;
    this.formCancel.emit();
  }

  /**
   * Save fournisseur
   */
  saveFournisseur(): void {
    this.submitted = true;
    
    if (this.fournisseurForm.valid) {
      const formValue = this.fournisseurForm.value;
      const fournisseurData = {
        nom: formValue.nom,
        infoContact: formValue.infoContact,
        adresse: formValue.adresse,
        telephone: formValue.telephone,
        matriculeFiscale: formValue.matriculeFiscale
      };

      if (this.isEditMode && this.selectedFournisseur) {
        // Update existing fournisseur
        this.fournisseurService.updateFournisseur(this.selectedFournisseur.id, fournisseurData).subscribe({
          next: () => {
            Swal.fire('Succès', 'Fournisseur modifié avec succès!', 'success');
            this.cancelForm();
            this.fournisseurSaved.emit(); // Notify parent to reload data
          },
          error: (error) => {
            const errorMessage = error?.error?.message || 'Erreur lors de la modification du fournisseur';
            Swal.fire('Erreur', errorMessage, 'error');
          }
        });
      } else {
        // Create new fournisseur
        this.fournisseurService.createFournisseur(fournisseurData).subscribe({
          next: () => {
            Swal.fire('Succès', 'Fournisseur créé avec succès!', 'success');
            this.cancelForm();
            this.fournisseurSaved.emit(); // Notify parent to reload data
          },
          error: (error) => {
            const errorMessage = error?.error?.message || 'Erreur lors de la création du fournisseur';
            Swal.fire('Erreur', errorMessage, 'error');
          }
        });
      }
    }
  }

  /**
   * Populate form with fournisseur data
   */
  private populateForm(fournisseur: Fournisseur): void {
    this.fournisseurForm.patchValue({
      nom: fournisseur.nom,
      infoContact: fournisseur.infoContact,
      adresse: fournisseur.adresse,
      telephone: fournisseur.telephone,
      matriculeFiscale: fournisseur.matriculeFiscale
    });
  }

  /**
   * Get form control for validation
   */
  getFormControl(fieldName: string) {
    return this.fournisseurForm.get(fieldName);
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
}