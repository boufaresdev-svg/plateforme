import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Formation, NiveauFormation, TypeFormation } from '../../../../models/formation/formation.model';
import { Domaine } from '../../../../models/formation/Domaine.model';
import { TypeFormations } from '../../../../models/formation/TypeFormations.model';
import { Categorie } from '../../../../models/formation/Categorie.model';
import { SousCategorie } from '../../../../models/formation/SousCategorie.model';
import { environment } from '../../../../../environment/environement';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-formation-metadata',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './metadata.component.html',
  styleUrls: ['./metadata.component.css']
})
export class FormationMetadataComponent implements OnInit {
  @Input() form!: Formation;
  @Input() domaines: Domaine[] = [];
  @Input() typesDyn: TypeFormations[] = [];
  @Input() categories: Categorie[] = [];
  @Input() sousCategories: SousCategorie[] = [];
  @Input() saving: boolean = false;
  
  @Output() formChange = new EventEmitter<Formation>();
  @Output() saveGeneralInfo = new EventEmitter<void>();
  @Output() domaineChange = new EventEmitter<void>();
  @Output() typeChange = new EventEmitter<void>();
  @Output() categorieChange = new EventEmitter<void>();
  @Output() imageFileChange = new EventEmitter<File | null>();
  
  niveaux = Object.values(NiveauFormation);
  types = Object.values(TypeFormation);

  // Image upload
  selectedImageFile: File | null = null;
  imagePreviewUrl: string | null = null;
  isUploadingImage = false;

  // Validation state
  validationErrors: { [key: string]: string } = {};
  touched: { [key: string]: boolean } = {};
  submitted: boolean = false;

  constructor() {}

  ngOnInit(): void {
    if (!this.form) {
      this.form = {
        theme: '',
        descriptionTheme: '',
        objectifGlobal: '',
        nombreHeures: undefined,
        prix: undefined,
        nombreMax: undefined,
        populationCible: '',
        typeFormation: TypeFormation.En_Ligne,
        niveau: NiveauFormation.Debutant
      };
    }
  }

  onFormChange(): void {
    this.formChange.emit(this.form);
    if (this.submitted) {
      this.validateForm();
    }
  }

  markFieldAsTouched(fieldName: string): void {
    this.touched[fieldName] = true;
    this.validateField(fieldName);
  }

  validateField(fieldName: string): void {
    delete this.validationErrors[fieldName];

    switch (fieldName) {
      case 'theme':
        if (!this.form.theme?.trim()) {
          this.validationErrors[fieldName] = 'Le nom de la formation est requis.';
        } else if (this.form.theme.trim().length < 3) {
          this.validationErrors[fieldName] = 'Le nom doit contenir au moins 3 caractères.';
        } else if (this.form.theme.trim().length > 200) {
          this.validationErrors[fieldName] = 'Le nom ne peut pas dépasser 200 caractères.';
        }
        break;
      
      case 'descriptionTheme':
        if (this.form.descriptionTheme && this.form.descriptionTheme.length > 1000) {
          this.validationErrors[fieldName] = 'La description ne peut pas dépasser 1000 caractères.';
        }
        break;
      
      case 'nombreHeures':
        if (this.form.nombreHeures !== undefined && this.form.nombreHeures !== null) {
          if (this.form.nombreHeures < 0) {
            this.validationErrors[fieldName] = 'Le nombre d\'heures ne peut pas être négatif.';
          } else if (this.form.nombreHeures > 10000) {
            this.validationErrors[fieldName] = 'Le nombre d\'heures semble trop élevé.';
          }
        }
        break;
      
      case 'prix':
        if (this.form.prix !== undefined && this.form.prix !== null) {
          if (this.form.prix < 0) {
            this.validationErrors[fieldName] = 'Le prix ne peut pas être négatif.';
          } else if (this.form.prix > 1000000) {
            this.validationErrors[fieldName] = 'Le prix semble trop élevé.';
          }
        }
        break;
      
      case 'nombreMax':
        if (this.form.nombreMax !== undefined && this.form.nombreMax !== null) {
          if (this.form.nombreMax < 1) {
            this.validationErrors[fieldName] = 'Le nombre maximum de participants doit être au moins 1.';
          } else if (this.form.nombreMax > 1000) {
            this.validationErrors[fieldName] = 'Le nombre de participants semble trop élevé.';
          }
        }
        break;
      
      case 'populationCible':
        if (this.form.populationCible && this.form.populationCible.length > 500) {
          this.validationErrors[fieldName] = 'La population cible ne peut pas dépasser 500 caractères.';
        }
        break;
      
      case 'typeFormation':
        if (!this.form.typeFormation) {
          this.validationErrors[fieldName] = 'Le type de formation est requis.';
        }
        break;
      
      case 'niveau':
        if (!this.form.niveau) {
          this.validationErrors[fieldName] = 'Le niveau est requis.';
        }
        break;
    }
  }

  validateForm(): boolean {
    this.validationErrors = {};
    
    // Validate all required and optional fields
    const fieldsToValidate = [
      'theme', 'descriptionTheme', 'nombreHeures', 'prix', 
      'nombreMax', 'populationCible', 'typeFormation', 'niveau'
    ];
    
    fieldsToValidate.forEach(field => this.validateField(field));
    
    return Object.keys(this.validationErrors).length === 0;
  }

  hasError(fieldName: string): boolean {
    return (this.touched[fieldName] || this.submitted) && !!this.validationErrors[fieldName];
  }

  getError(fieldName: string): string {
    return this.validationErrors[fieldName] || '';
  }

  onSaveGeneralInfo(): void {
    this.submitted = true;
    
    // Validate form
    if (!this.validateForm()) {
      Swal.fire({
        icon: 'warning',
        title: 'Erreurs de validation',
        html: 'Veuillez corriger les erreurs suivantes :<br><ul style="text-align: left; margin-top: 10px;">' + 
              Object.values(this.validationErrors).map(err => `<li>${err}</li>`).join('') + 
              '</ul>',
        confirmButtonText: 'OK'
      });
      return;
    }

    this.saveGeneralInfo.emit();
  }

  onDomaineChange(): void {
    this.domaineChange.emit();
    this.onFormChange();
  }

  onTypeChange(): void {
    this.typeChange.emit();
    this.onFormChange();
  }

  onCategorieChange(): void {
    this.categorieChange.emit();
    this.onFormChange();
  }

  // Image upload methods
  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      
      // Validate file type
      const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'image/gif', 'image/webp'];
      if (!allowedTypes.includes(file.type)) {
        Swal.fire({
          icon: 'error',
          title: 'Type de fichier non autorisé',
          text: 'Veuillez sélectionner une image (JPG, PNG, GIF ou WebP).'
        });
        return;
      }

      // Validate file size (max 5MB)
      const maxSize = 5 * 1024 * 1024;
      if (file.size > maxSize) {
        Swal.fire({
          icon: 'error',
          title: 'Fichier trop volumineux',
          text: 'La taille maximale autorisée est de 5 Mo.'
        });
        return;
      }

      this.selectedImageFile = file;
      this.imageFileChange.emit(file);
      
      // Create preview
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagePreviewUrl = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  removeImage(): void {
    this.selectedImageFile = null;
    this.imagePreviewUrl = null;
    this.form.imageUrl = undefined;
    this.imageFileChange.emit(null);
    this.onFormChange();
  }

  getImageUrl(): string | null {
    if (this.imagePreviewUrl) {
      return this.imagePreviewUrl;
    }
    if (this.form.imageUrl) {
      // If it's a relative path, prepend the base URL
      if (this.form.imageUrl.startsWith('/')) {
        return `${environment.formationUrl.replace('/api', '')}${this.form.imageUrl}`;
      }
      return this.form.imageUrl;
    }
    return null;
  }

  hasImage(): boolean {
    return !!(this.imagePreviewUrl || this.form.imageUrl);
  }

  getSelectedFile(): File | null {
    return this.selectedImageFile;
  }
}
