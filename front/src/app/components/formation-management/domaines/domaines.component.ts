import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

import { Domaine } from '../../../models/formation/Domaine.model';
import { TypeFormations } from '../../../models/formation/TypeFormations.model';
import { Categorie } from '../../../models/formation/Categorie.model';
import { SousCategorie } from '../../../models/formation/SousCategorie.model';

import { DomaineService } from '../../../services/formation_managment/Domaine.service';
import { TypeService } from '../../../services/formation_managment/Type.service';
import { CategorieService } from '../../../services/formation_managment/Categorie.service';
import { SousCategorieService } from '../../../services/formation_managment/SousCategorie.service';

@Component({
  selector: 'app-domaines',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './domaines.component.html',
  styleUrls: ['./domaines.component.css']
})
export class DomainesComponent implements OnInit {
  domaines: Domaine[] = [];
  types: TypeFormations[] = [];
  categories: Categorie[] = [];
  sousCategories: SousCategorie[] = [];

  selectedDomaine: Domaine | null = null;
  selectedType: TypeFormations | null = null;
  selectedCategorie: Categorie | null = null;
  selectedSousCategorie: SousCategorie | null = null;

  typesByDomaine: { [idDomaine: number]: TypeFormations[] } = {};
  categoriesByType: { [idType: number]: Categorie[] } = {};
  sousCategoriesByCategorie: { [idCategorie: number]: SousCategorie[] } = {};
  
  // Reverse lookups for finding parent relationships
  typeDomaineMap: { [idType: number]: number } = {}; // Maps typeId to domaineId
  categorieTypeMap: { [idCategorie: number]: number } = {}; // Maps categorieId to typeId

  isEditMode = false;
  searchTerm = '';
  currentTab: 'domaines' | 'types' | 'categories' | 'souscategories' = 'domaines';
  isLoading = false;

  // Form Data
  domaineFormData: Domaine = {
    nom: '',
    description: ''
  };

  typeFormData: TypeFormations = {
    nom: '',
    description: '',
    domaine: undefined
  };

  categorieFormData: Categorie = {
    nom: '',
    description: '',
    type: undefined
  };

  sousCategorieFormData: SousCategorie = {
    nom: '',
    description: '',
    categorie: undefined
  };

  // ID-based dropdown bindings
  selectedTypeDomaineId: number | null = null;
  selectedCategorieTypeId: number | null = null;
  selectedSousCategorieId: number | null = null;

  // Modal control
  showModal = false;
  modalType: 'domaine' | 'type' | 'categorie' | 'souscategorie' | null = null;
  modalTitle = '';

  constructor(
    private domaineService: DomaineService,
    private typeService: TypeService,
    private categorieService: CategorieService,
    private sousCategorieService: SousCategorieService
  ) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll() {
    this.isLoading = true;
    this.loadDomaines();
    this.loadTypes();
    this.loadCategories();
    this.loadSousCategories();
    setTimeout(() => {
      this.isLoading = false;
    }, 1000);
  }

  loadDomaines() {
    this.domaineService.getAllDomaines().subscribe({
      next: (data: any) => {
        this.domaines = data;
      },
      error: (error: any) => {
        Swal.fire('Erreur', 'Erreur lors du chargement des domaines: ' + (error?.message || JSON.stringify(error)), 'error');
      }
    });
  }

  loadTypes() {
    this.typesByDomaine = {}; // Clear before reload
    this.typeDomaineMap = {}; // Clear reverse lookup
    this.typeService.getAllTypes().subscribe({
      next: (data: any) => {
        // Handle wrapped array from backend
        const types = Array.isArray(data) && Array.isArray(data[0]) ? data[0] : data;
        this.types = types;
        types.forEach((type: any) => {
          if (type.idType && type.domaine?.idDomaine) {
            if (!this.typesByDomaine[type.domaine.idDomaine]) {
              this.typesByDomaine[type.domaine.idDomaine] = [];
            }
            this.typesByDomaine[type.domaine.idDomaine].push(type);
            // Store reverse lookup
            this.typeDomaineMap[type.idType] = type.domaine.idDomaine;
          }
        });
      },
      error: (error: any) => {
        console.error('Erreur:', error);
      }
    });
  }

  loadCategories() {
    this.categoriesByType = {}; // Clear before reload
    this.categorieTypeMap = {}; // Clear reverse lookup
    this.categorieService.getAllCategories().subscribe({
      next: (data: any) => {
        this.categories = data;
        data.forEach((cat: any) => {
          if (cat.idCategorie && cat.type?.idType) {
            if (!this.categoriesByType[cat.type.idType]) {
              this.categoriesByType[cat.type.idType] = [];
            }
            this.categoriesByType[cat.type.idType].push(cat);
            // Store reverse lookup
            this.categorieTypeMap[cat.idCategorie] = cat.type.idType;
          }
        });
      },
      error: (error: any) => {
        console.error('Erreur:', error);
      }
    });
  }

  loadSousCategories() {
    this.sousCategoriesByCategorie = {}; // Clear before reload
    this.sousCategorieService.getAllSousCategories().subscribe({
      next: (data: any) => {
        this.sousCategories = data;
        data.forEach((souscats: any) => {
          if (souscats.idSousCategorie && souscats.categorie?.idCategorie) {
            if (!this.sousCategoriesByCategorie[souscats.categorie.idCategorie]) {
              this.sousCategoriesByCategorie[souscats.categorie.idCategorie] = [];
            }
            this.sousCategoriesByCategorie[souscats.categorie.idCategorie].push(souscats);
          }
        });
      },
      error: (error: any) => {
        console.error('Erreur:', error);
      }
    });
  }

  filteredItems(): any[] {
    const searchLower = this.searchTerm.toLowerCase();
    switch (this.currentTab) {
      case 'domaines':
        return this.domaines.filter((d) => d.nom?.toLowerCase().includes(searchLower));
      case 'types':
        return this.types.filter((t) => t.nom?.toLowerCase().includes(searchLower));
      case 'categories':
        return this.categories.filter((c) => c.nom?.toLowerCase().includes(searchLower));
      case 'souscategories':
        return this.sousCategories.filter((s) => s.nom?.toLowerCase().includes(searchLower));
      default:
        return [];
    }
  }

  openModal(type: string) {
    this.modalType = type as any;
    this.showModal = true;
    this.updateModalTitle();
  }

  updateModalTitle() {
    const action = this.isEditMode ? 'Modifier' : 'Nouveau';
    const names: any = {
      domaine: 'domaine',
      type: 'type',
      categorie: 'catégorie',
      souscategorie: 'sous-catégorie'
    };
    this.modalTitle = `${action} ${names[this.modalType!] || ''}`;
  }

  closeModal() {
    this.showModal = false;
    this.modalType = null;
    this.resetForms();
  }

  resetForms() {
    this.domaineFormData = { nom: '', description: '' };
    this.typeFormData = { nom: '', description: '', domaine: undefined };
    this.categorieFormData = { nom: '', description: '', type: undefined };
    this.sousCategorieFormData = { nom: '', description: '', categorie: undefined };
    this.isEditMode = false;
    this.selectedDomaine = null;
    this.selectedType = null;
    this.selectedCategorie = null;
    this.selectedSousCategorie = null;
  }

  // =============== DOMAINES ===============
  editDomaine(domaine: Domaine) {
    this.domaineFormData = { ...domaine };
    this.selectedDomaine = domaine;
    this.isEditMode = true;
    this.openModal('domaine');
  }

  saveDomaine() {
    if (!this.domaineFormData.nom) {
      Swal.fire('Erreur', 'Le nom est requis', 'error');
      return;
    }

    if (this.isEditMode && this.selectedDomaine?.idDomaine) {
      this.domaineService.updateDomaine(this.selectedDomaine.idDomaine, this.domaineFormData).subscribe({
        next: () => {
          Swal.fire('Succès', 'Domaine mis à jour', 'success');
          this.closeModal();
          this.loadAll();
        },
        error: () => {
          Swal.fire('Erreur', 'Erreur lors de la mise à jour', 'error');
        }
      });
    } else {
      this.domaineService.createDomaine(this.domaineFormData).subscribe({
        next: () => {
          Swal.fire('Succès', 'Domaine créé', 'success');
          this.closeModal();
          this.loadAll();
        },
        error: () => {
          Swal.fire('Erreur', 'Erreur lors de la création', 'error');
        }
      });
    }
  }

  deleteDomaine(id: number) {
    Swal.fire({
      title: 'Confirmer la suppression',
      text: 'Êtes-vous sûr?',
      icon: 'warning',
      showCancelButton: true
    }).then((result: any) => {
      if (result.isConfirmed) {
        this.domaineService.deleteDomaine(id).subscribe({
          next: () => {
            Swal.fire('Supprimé', 'Domaine supprimé', 'success');
            this.loadAll();
          },
          error: () => {
            Swal.fire('Erreur', 'Erreur lors de la suppression', 'error');
          }
        });
      }
    });
  }

  // =============== TYPES ===============
  editType(type: TypeFormations) {
    this.typeFormData = { ...type };
    // Set ID for dropdown binding - handle both nested and flat format
    const domaineId = type.domaine?.idDomaine || (type as any).idDomaine;
    this.selectedTypeDomaineId = domaineId || null;
    this.selectedType = type;
    this.isEditMode = true;
    // Pre-set the domaine object so dropdown change doesn't break it
    if (this.selectedTypeDomaineId) {
      const foundDomaine = this.domaines.find(d => d.idDomaine === this.selectedTypeDomaineId);
      if (foundDomaine) {
        this.typeFormData.domaine = foundDomaine;
      }
    }
    this.openModal('type');
  }

  onTypeDomaineChange() {
    // Convert ID back to object only if the dropdown changed
    if (this.selectedTypeDomaineId !== null) {
      const foundDomaine = this.domaines.find(d => d.idDomaine === this.selectedTypeDomaineId);
      if (foundDomaine) {
        this.typeFormData.domaine = foundDomaine;
      }
    } else {
      this.typeFormData.domaine = undefined;
    }
  }

  saveType() {
    if (!this.typeFormData.nom) {
      Swal.fire('Erreur', 'Le nom est requis', 'error');
      return;
    }
    if (!this.typeFormData.domaine || !this.typeFormData.domaine.idDomaine) {
      Swal.fire('Erreur', 'Veuillez sélectionner un Domaine', 'error');
      return;
    }

    if (this.isEditMode && this.selectedType?.idType) {
      const updatePayload: { nom: string; description?: string; domaineId: number } = {
        nom: this.typeFormData.nom!,
        description: this.typeFormData.description,
        domaineId: this.typeFormData.domaine!.idDomaine
      };
      this.typeService.updateType(this.selectedType.idType, updatePayload as any).subscribe({
        next: () => {
          Swal.fire('Succès', 'Type mis à jour', 'success');
          this.closeModal();
          this.loadAll();
        },
        error: () => {
          Swal.fire('Erreur', 'Erreur lors de la mise à jour', 'error');
        }
      });
    } else {
      const createPayload: { nom: string; description?: string; domaineId: number } = {
        nom: this.typeFormData.nom!,
        description: this.typeFormData.description,
        domaineId: this.typeFormData.domaine!.idDomaine
      };
      this.typeService.createType(createPayload as any).subscribe({
        next: () => {
          Swal.fire('Succès', 'Type créé', 'success');
          this.closeModal();
          this.loadAll();
        },
        error: () => {
          Swal.fire('Erreur', 'Erreur lors de la création', 'error');
        }
      });
    }
  }

  deleteType(id: number) {
    Swal.fire({
      title: 'Confirmer la suppression',
      text: 'Êtes-vous sûr?',
      icon: 'warning',
      showCancelButton: true
    }).then((result: any) => {
      if (result.isConfirmed) {
        this.typeService.deleteType(id).subscribe({
          next: () => {
            Swal.fire('Supprimé', 'Type supprimé', 'success');
            this.loadAll();
          },
          error: () => {
            Swal.fire('Erreur', 'Erreur lors de la suppression', 'error');
          }
        });
      }
    });
  }

  // =============== CATEGORIES ===============
  editCategorie(categorie: Categorie) {
    this.categorieFormData = { ...categorie };
    // Set ID for dropdown binding - handle both nested and flat format
    const typeId = categorie.type?.idType || (categorie as any).idType;
    this.selectedCategorieTypeId = typeId || null;
    this.selectedCategorie = categorie;
    this.isEditMode = true;
    // Immediately restore the type object from types array
    if (this.selectedCategorieTypeId) {
      const foundType = this.types.find(t => t.idType === this.selectedCategorieTypeId);
      if (foundType) {
        this.categorieFormData.type = foundType;
      }
    }
    this.openModal('categorie');
  }

  onCategorieTypeChange() {
    // Convert ID back to object only if the dropdown changed
    if (this.selectedCategorieTypeId !== null) {
      const foundType = this.types.find(t => t.idType === this.selectedCategorieTypeId);
      if (foundType) {
        this.categorieFormData.type = foundType;
      }
    } else {
      this.categorieFormData.type = undefined;
    }
  }

  saveCategorie() {
    if (!this.categorieFormData.nom) {
      Swal.fire('Erreur', 'Le nom est requis', 'error');
      return;
    }
    if (!this.categorieFormData.type || !this.categorieFormData.type.idType) {
      Swal.fire('Erreur', 'Veuillez sélectionner un Type pour la catégorie', 'error');
      return;
    }

    if (this.isEditMode && this.selectedCategorie?.idCategorie) {
      const updatePayload: { nom: string; description?: string; idType: number } = {
        nom: this.categorieFormData.nom!,
        description: this.categorieFormData.description,
        idType: this.categorieFormData.type!.idType
      };
      this.categorieService.updateCategorie(this.selectedCategorie.idCategorie, updatePayload as any).subscribe({
        next: () => {
          Swal.fire('Succès', 'Catégorie mise à jour', 'success');
          this.closeModal();
          this.loadAll();
        },
        error: () => {
          Swal.fire('Erreur', 'Erreur lors de la mise à jour', 'error');
        }
      });
    } else {
      const createPayload: { nom: string; description?: string; idType: number } = {
        nom: this.categorieFormData.nom!,
        description: this.categorieFormData.description,
        idType: this.categorieFormData.type!.idType
      };
      this.categorieService.addCategorie(createPayload as any).subscribe({
        next: () => {
          Swal.fire('Succès', 'Catégorie créée', 'success');
          this.closeModal();
          this.loadAll();
        },
        error: () => {
          Swal.fire('Erreur', 'Erreur lors de la création', 'error');
        }
      });
    }
  }

  deleteCategorie(id: number) {
    Swal.fire({
      title: 'Confirmer la suppression',
      text: 'Êtes-vous sûr?',
      icon: 'warning',
      showCancelButton: true
    }).then((result: any) => {
      if (result.isConfirmed) {
        this.categorieService.deleteCategorie(id).subscribe({
          next: () => {
            Swal.fire('Supprimé', 'Catégorie supprimée', 'success');
            this.loadAll();
          },
          error: () => {
            Swal.fire('Erreur', 'Erreur lors de la suppression', 'error');
          }
        });
      }
    });
  }

  // =============== SOUS-CATEGORIES ===============
  editSousCategorie(souscategorie: SousCategorie) {
    this.sousCategorieFormData = { ...souscategorie };
    // Set ID for dropdown binding - handle both nested and flat format
    const categorieId = souscategorie.categorie?.idCategorie || (souscategorie as any).idCategorie;
    this.selectedSousCategorieId = categorieId || null;
    this.selectedSousCategorie = souscategorie;
    this.isEditMode = true;
    // Pre-set the categorie object so dropdown change doesn't break it
    if (this.selectedSousCategorieId) {
      const foundCategorie = this.categories.find(c => c.idCategorie === this.selectedSousCategorieId);
      if (foundCategorie) {
        this.sousCategorieFormData.categorie = foundCategorie;
      }
    }
    this.openModal('souscategorie');
  }

  onSousCategorieChange() {
    // Convert ID back to object only if the dropdown changed
    if (this.selectedSousCategorieId !== null) {
      const foundCategorie = this.categories.find(c => c.idCategorie === this.selectedSousCategorieId);
      if (foundCategorie) {
        this.sousCategorieFormData.categorie = foundCategorie;
      }
    } else {
      this.sousCategorieFormData.categorie = undefined;
    }
  }

  saveSousCategorie() {
    if (!this.sousCategorieFormData.nom) {
      Swal.fire('Erreur', 'Le nom est requis', 'error');
      return;
    }
    if (!this.sousCategorieFormData.categorie || !this.sousCategorieFormData.categorie.idCategorie) {
      Swal.fire('Erreur', 'Veuillez sélectionner une Catégorie', 'error');
      return;
    }

    if (this.isEditMode && this.selectedSousCategorie?.idSousCategorie) {
      const updatePayload: { nom: string; description?: string; idCategorie: number } = {
        nom: this.sousCategorieFormData.nom!,
        description: this.sousCategorieFormData.description,
        idCategorie: this.sousCategorieFormData.categorie!.idCategorie
      };
      this.sousCategorieService.updateSousCategorie(this.selectedSousCategorie.idSousCategorie, updatePayload as any).subscribe({
        next: () => {
          Swal.fire('Succès', 'Sous-catégorie mise à jour', 'success');
          this.closeModal();
          this.loadAll();
        },
        error: () => {
          Swal.fire('Erreur', 'Erreur lors de la mise à jour', 'error');
        }
      });
    } else {
      if (!this.sousCategorieFormData.categorie || !this.sousCategorieFormData.categorie.idCategorie) {
        Swal.fire('Erreur', 'Veuillez sélectionner une Catégorie', 'error');
        return;
      }
      const createPayload: { nom: string; description?: string; idCategorie: number } = {
        nom: this.sousCategorieFormData.nom!,
        description: this.sousCategorieFormData.description,
        idCategorie: this.sousCategorieFormData.categorie!.idCategorie
      };
      this.sousCategorieService.addSousCategorie(createPayload as any).subscribe({
        next: () => {
          Swal.fire('Succès', 'Sous-catégorie créée', 'success');
          this.closeModal();
          this.loadAll();
        },
        error: () => {
          Swal.fire('Erreur', 'Erreur lors de la création', 'error');
        }
      });
    }
  }

  deleteSousCategorie(id: number) {
    Swal.fire({
      title: 'Confirmer la suppression',
      text: 'Êtes-vous sûr?',
      icon: 'warning',
      showCancelButton: true
    }).then((result: any) => {
      if (result.isConfirmed) {
        this.sousCategorieService.deleteSousCategorie(id).subscribe({
          next: () => {
            Swal.fire('Supprimé', 'Sous-catégorie supprimée', 'success');
            this.loadAll();
          },
          error: () => {
            Swal.fire('Erreur', 'Erreur lors de la suppression', 'error');
          }
        });
      }
    });
  }
}
