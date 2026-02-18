import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';

import { Formation, NiveauFormation, TypeFormation, ProgrammeDetaile, JourFormation, ContenuDetaille, StatutFormation as FormationStatut, ContenuFormation, ContenuWithJour } from '../../../../models/formation/formation.model';
import { Domaine } from '../../../../models/formation/Domaine.model';
import { TypeFormations } from '../../../../models/formation/TypeFormations.model';
import { Categorie } from '../../../../models/formation/Categorie.model';
import { SousCategorie } from '../../../../models/formation/SousCategorie.model';
import { FormationService } from '../../../../services/formation_managment/formation.service';
import { ContenuDetailleService } from '../../../../services/contenu-detaille.service';
import { PdfGeneratorService } from '../../../../services/formation_managment/pdf-generator.service';
import { FormateurService } from '../../../../services/formation_managment/Formateur.service';
import { ContenuJourFormationService } from '../../../../services/formation_managment/ContenuJourFormation.service';
import { PlanFormation, StatutFormation } from '../../../../models/formation/PlanFormation.model';
import { PlanFormationService } from '../../../../services/formation_managment/PlanFormation.service';
import { Subject, Subscription, debounceTime, distinctUntilChanged, map, switchMap, catchError, of, forkJoin } from 'rxjs';

// Import subcomponents
import { FormationMetadataComponent } from '../metadata/metadata.component';
import { FormationContenuComponent } from '../contenu/contenu.component';
import { FormationFormateurComponent } from '../formateur/formateur.component';
import { ContenuDetailleComponent } from '../../contenu-detaille/contenu-detaille.component';

// Simple filter pipe
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filter',
  standalone: true
})
export class FilterPipe implements PipeTransform {
  transform(items: any[], filter: any): any[] {
    if (!items || !filter) return items;
    return items.filter(item => {
      for (const key in filter) {
        if (item[key] !== filter[key]) return false;
      }
      return true;
    });
  }
}

@Component({
  selector: 'app-manage-formation',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    FilterPipe,
    FormationMetadataComponent,
    FormationContenuComponent,
    FormationFormateurComponent,
    ContenuDetailleComponent
  ],
  templateUrl: './manage-formation.component.html',
  styleUrls: ['./manage-formation.component.css']
})
export class ManageFormationComponent implements OnInit, OnDestroy {
  // View management
  currentView: 'metadata' | 'contenu' | 'formateur' | 'objectives' | null = null;
  
  niveaux = Object.values(NiveauFormation);
  types = Object.values(TypeFormation);
  formateurs: any[] = [];

  saving = false;
  isLoading = false;
  loadingMessage = '';
  
  // Track specific loading operations
  isLoadingFormation = false;
  isLoadingObjectives = false;
  isLoadingContent = false;
  isLoadingPlans = false;
  isLoadingFormateurs = false;
  selectedGlobal: any = null;
  selectedSpecific: any = null;
  showAddGlobalForm = false;
  showAddSpecificForm = false;
  showLinkForm = false;
  showGeneralInfo = false;
  showContentBuilder = false;
  
  // Form inputs for creating new objectives
  newGlobalObjectiveLabel = '';
  newSpecificObjectiveLabel = '';
  newSpecificGlobalId: number | undefined = undefined;
  
  // Edit mode tracking
  isEditingGlobal = false;
  editingGlobalId: number | null = null;
  isEditingSpecific = false;
  editingSpecificId: number | null = null;
  
  // Middle tree panel expand/collapse state
  expandedGlobals: number[] = [];
  expandedSpecifics: number[] = [];

  // Objective models
  private gid = 0;
  private sid = 0;
  objectifsGlobauxExemples: Array<{ id: number; label: string; selected: boolean; _backendData?: any }> = [];

  objectifsSpecifiquesExemples: Array<{ id: number; label: string; selected: boolean; globalId?: number; _backendData?: any }> = [];

  // Global Objectives Search
  showSearchGlobalObjectives = false;
  searchGlobalObjectivesQuery = '';
  globalObjectivesSearchResults: any[] = [];
  isSearchingGlobalObjectives = false;
  selectedGlobalObjectivesForLink: Record<number, boolean> = {};

  // Specific Objectives Search
  showSearchSpecificObjectives = false;
  searchSpecificObjectivesQuery = '';
  specificObjectivesSearchResults: any[] = [];
  isSearchingSpecificObjectives = false;
  selectedSpecificObjectivesForLink: Record<number, boolean> = {};
  targetGlobalObjectiveForSpecific: number | null = null;

  // Contenu Jour Search
  showSearchContenusJour = false;
  searchContenusJourQuery = '';
  contenusJourSearchResults: any[] = [];
  isSearchingContenusJour = false;
  selectedContenusJourForLink: Record<number, boolean> = {};
  targetObjectifSpecifiqueForContenu: number | null = null;
  targetPlanFormationForContenu: number | null = null;
  selectedNiveauForAssignment: number | null = null; // Level selection: 1=Débutant, 2=Intermédiaire, 3=Avancé
  availableNiveaux = [
    { id: 1, label: 'Débutant', icon: '🌱' },
    { id: 2, label: 'Intermédiaire', icon: '📈' },
    { id: 3, label: 'Avancé', icon: '🚀' }
  ];

  contentForm = {
    title: '',
    description: '',
    day: 1,
    specificId: undefined as number | undefined,
    detail: '',
    level: '',
    staff: '',
    hoursPractical: 0,
    hoursTheoretical: 0
  };

  contentItems: Array<{ id: number; title: string; description: string; detail?: string; level?: string; staff?: string; hoursPractical?: number; hoursTheoretical?: number; day: number; specificId?: number; order: number; assignedContentDetails?: number[]; _backendData?: any; _assignedContenusWithNiveau?: any[] }> = [];
  private contentIdCounter = 0;
  selectedContentForEdit: any = null;
  showEditContentForm = false;

  // Plans de Formation
  plansFormation: Array<{ id: number; titre: string; description?: string; dateDebut?: string; dateFin?: string; selected: boolean; _backendData?: any }> = [];
  private planIdCounter = 0;
  selectedPlan: any = null;

  // Content Detail Library
  contentDetailLibrary: any[] = [];
  selectedContentItem: any = null;
  showContentDetailAssigner = false;
  contentSearchQuery = '';
  searchResults: any[] | null = null;
  isSearching = false;
  contentSelections: Record<number, boolean> = {};
  private contentSearch$ = new Subject<string>();
  private contentSearchSub?: Subscription;
  isAssigning = false; // Track if assignment request is in progress

  // Content Detail Creator
  showContentDetailCreator = false;

  // Plan Generator
  showPlanGenerator = false;
  editingPlanId: number | null = null; // Track if editing an existing plan
  planGeneratorForm = {
    titre: '',
    description: '',
    dateDebut: '',
    dateFin: '',
    dateLancement: '',
    dateFinReel: '',
    formateurId: undefined as any,
    status: StatutFormation.PLANIFIEE,
    nombreJours: undefined as number | undefined
  };

  // Classification data (inspiré de V1)
  domaines: Domaine[] = [];
  typesDyn: TypeFormations[] = [];
  categories: Categorie[] = [];
  sousCategories: SousCategorie[] = [];

  form: Formation = {
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

  // Enforce saving general info before accessing other sections
  generalInfoSaved = false;

  // Image upload
  selectedImageFile: File | null = null;

  // Validation state for objectives and content
  objectiveValidationErrors: { [key: string]: string } = {};
  contentValidationErrors: { [key: string]: string } = {};

  // Loading state management
  private startLoading(message: string = 'Chargement en cours...'): void {
    this.isLoading = true;
    this.loadingMessage = message;
    this.cdr.detectChanges();
  }

  private stopLoading(): void {
    this.isLoading = false;
    this.loadingMessage = '';
    this.cdr.detectChanges();
  }

  // Helper to hide all forms/sections
  private hideAllForms(): void {
    this.showGeneralInfo = false;
    this.showAddGlobalForm = false;
    this.showAddSpecificForm = false;
    this.showLinkForm = false;
    this.showContentBuilder = false;
    this.showContentDetailAssigner = false;
    this.showPlanGenerator = false;
    this.showContentDetailCreator = false;
    this.showSearchGlobalObjectives = false;
    this.showSearchSpecificObjectives = false;
    this.showSearchContenusJour = false;
    this.showEditContentForm = false;
    
    // Reset search states
    this.searchGlobalObjectivesQuery = '';
    this.globalObjectivesSearchResults = [];
    this.searchSpecificObjectivesQuery = '';
    this.specificObjectivesSearchResults = [];
    this.searchContenusJourQuery = '';
    this.contenusJourSearchResults = [];
    this.contentSearchQuery = '';
    this.searchResults = null;
    
    // Reset selected items
    this.selectedGlobal = null;
    this.selectedSpecific = null;
    this.selectedContentItem = null;
    this.selectedContentForEdit = null;
    this.selectedPlan = null;
    
    this.cdr.detectChanges(); // Force immediate change detection
  }

  // Helper to clean HTML content for PDF display
  private cleanHtmlForPdf(html: string): string {
    if (!html) return '';
    
    // Create a temporary div to parse HTML
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = html;
    
    // Get text content only (strips all HTML tags)
    let text = tempDiv.textContent || tempDiv.innerText || '';
    
    // Clean up whitespace
    text = text.trim().replace(/\s+/g, ' ');
    
    // Remove HTML entities
    text = text.replace(/&nbsp;/g, ' ')
               .replace(/&amp;/g, '&')
               .replace(/&lt;/g, '<')
               .replace(/&gt;/g, '>')
               .replace(/&quot;/g, '"')
               .replace(/&#39;/g, "'");
    
    return text;
  }

  // Helper to prompt user to complete general information first
  private promptGeneralInfo(): void {
    Swal.fire({
      icon: 'info',
      title: 'Complétez les infos générales',
      html: 'Veuillez <strong>enregistrer</strong> les informations générales avant d\'utiliser les autres actions.',
      confirmButtonText: 'Aller aux infos',
      confirmButtonColor: '#10b981'
    }).then(() => {
      this.hideAllForms();
      this.showGeneralInfo = true;
    });
  }

  // Subcomponent navigation methods
  showMetadataView(): void {
    this.hideAllForms();
    this.currentView = 'metadata';
    this.showGeneralInfo = true;
  }

  showContenuView(): void {
    if (!this.generalInfoSaved) {
      this.promptGeneralInfo();
      return;
    }
    this.hideAllForms();
    this.currentView = 'contenu';
    this.showContentBuilder = true;
  }

  showFormateurView(): void {
    if (!this.generalInfoSaved) {
      this.promptGeneralInfo();
      return;
    }
    this.hideAllForms();
    this.currentView = 'formateur';
    this.showPlanGenerator = true;
  }

  // Event handlers for metadata component
  onFormChange(updatedForm: Formation): void {
    this.form = updatedForm;
  }

  onImageFileChange(file: File | null): void {
    this.selectedImageFile = file;
  }

  onSaveGeneralInfo(): void {
    this.save();
  }

  handleDomaineChange(): void {
    // Call the existing onDomaineChange method
    const domId = this.form.idDomaine;
    // reset downstream selections
    this.form.idType = undefined;
    this.form.idCategorie = undefined;
    this.form.idSousCategorie = undefined;
    this.typesDyn = [];
    this.categories = [];
    this.sousCategories = [];
    if (domId) {
      this.formationService.getTypesByDomaine(domId).subscribe({
        next: (ts) => { this.typesDyn = ts; },
        error: (e) => console.error('Erreur chargement types', e)
      });
    }
  }

  handleTypeChange(): void {
    const typeId = this.form.idType;
    // reset downstream
    this.form.idCategorie = undefined;
    this.form.idSousCategorie = undefined;
    this.categories = [];
    this.sousCategories = [];
    if (typeId) {
      this.formationService.getCategoriesByType(typeId).subscribe({
        next: (cs) => { this.categories = cs; },
        error: (e) => console.error('Erreur chargement catégories', e)
      });
    }
  }

  handleCategorieChange(): void {
    const catId = this.form.idCategorie;
    // reset downstream
    this.form.idSousCategorie = undefined;
    this.sousCategories = [];
    if (catId) {
      this.formationService.getSousCategoriesByCategorie(catId).subscribe({
        next: (scs) => { this.sousCategories = scs; },
        error: (e) => console.error('Erreur chargement sous-catégories', e)
      });
    }
  }

  // Event handlers for contenu component
  onContentItemsChange(contentItems: any[]): void {
    this.contentItems = contentItems;
    this.syncSelectionsToForm();
  }

  // Event handlers for formateur component
  onPlansFormationChange(plans: any[]): void {
    this.plansFormation = plans;
  }

  // Close subcomponent views
  closeSubcomponentView(): void {
    this.currentView = null;
    this.hideAllForms();
  }

  // Guarded UI navigation helpers
  onSelectGlobal(o: any): void {
    if (!this.generalInfoSaved) { this.promptGeneralInfo(); return; }
    this.hideAllForms();
    this.selectedGlobal = o;
    this.selectedSpecific = null;
  }

  onSelectSpecific(o: any): void {
    if (!this.generalInfoSaved) { this.promptGeneralInfo(); return; }
    this.hideAllForms();
    this.selectedSpecific = o;
    this.selectedGlobal = null;
  }

  openAddGlobalForm(): void {
    if (!this.generalInfoSaved) { this.promptGeneralInfo(); return; }
    this.hideAllForms();
    this.isEditingGlobal = false;
    this.editingGlobalId = null;
    this.newGlobalObjectiveLabel = '';
    this.selectedGlobal = null;
    this.selectedSpecific = null;
    this.showAddGlobalForm = true;
  }

  openSearchGlobalObjectives(): void {
    if (!this.generalInfoSaved) { this.promptGeneralInfo(); return; }
    this.hideAllForms();
    this.showSearchGlobalObjectives = true;
    this.searchGlobalObjectivesQuery = '';
    this.globalObjectivesSearchResults = [];
    this.selectedGlobalObjectivesForLink = {};
    this.selectedGlobal = null;
    this.selectedSpecific = null;
  }

  searchGlobalObjectives(): void {
    const query = this.searchGlobalObjectivesQuery.trim();
    if (!query) {
      this.globalObjectivesSearchResults = [];
      return;
    }

    this.isSearchingGlobalObjectives = true;

    // Use backend search endpoint with formation filter if available
    const searchObservable = this.form.idFormation
      ? this.formationService.searchObjectifsNotLinkedToFormation(this.form.idFormation, query)
      : this.formationService.searchObjectifsGlobaux(query);

    searchObservable.subscribe({
      next: (objectives: any[]) => {
        this.isSearchingGlobalObjectives = false;
        this.stopLoading();
        // Map backend response to UI format
        this.globalObjectivesSearchResults = objectives.map((obj: any) => ({
          id: obj.idObjectifGlobal,
          label: obj.libelle || obj.titre || 'Unnamed Objective',
          description: obj.description,
          tags: obj.tags,
          isSelected: this.selectedGlobalObjectivesForLink[obj.idObjectifGlobal] || false,
          _backendData: obj
        }));
        
      },
      error: (err) => {
        console.error('❌ Error searching objectives:', err);
        this.isSearchingGlobalObjectives = false;
        this.stopLoading();
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de rechercher les objectifs.'
        });
      }
    });
  }

  toggleGlobalObjectiveSelection(objectiveId: number): void {
    // Initialize if not present
    if (this.selectedGlobalObjectivesForLink[objectiveId] === undefined) {
      this.selectedGlobalObjectivesForLink[objectiveId] = true;
    } else {
      this.selectedGlobalObjectivesForLink[objectiveId] = !this.selectedGlobalObjectivesForLink[objectiveId];
    }
  }

  assignSelectedGlobalObjectives(): void {
    const selectedIds = Object.keys(this.selectedGlobalObjectivesForLink)
      .filter(id => this.selectedGlobalObjectivesForLink[parseInt(id)])
      .map(id => parseInt(id));

    if (selectedIds.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'Attention',
        text: 'Veuillez sélectionner au moins un objectif à attribuer à la formation.'
      });
      return;
    }

    if (!this.form.idFormation) {
      Swal.fire({
        icon: 'error',
        title: 'Erreur',
        text: 'La formation n\'a pas pu être trouvée. Veuillez vérifier que vous avez sauvegardé les informations générales de la formation.'
      });
      return;
    }

    this.startLoading('Attribution des objectifs...');

    // Link each selected objective to the formation
    const linkObservables = selectedIds.map(id => 
      this.formationService.linkObjectifGlobalToFormation(this.form.idFormation!, id)
    );

    // Execute all link operations in parallel
    forkJoin(linkObservables).subscribe({
      next: () => {
        
        // Reload the formation from backend to get updated objectives list
        this.formationService.getFormationById(this.form.idFormation!).subscribe({
          next: (formation) => {
            
            // Update the tree data with the fresh data from backend
            this.loadTreeData(formation);
            
            // Trigger change detection to update the UI immediately
            this.cdr.markForCheck();

            this.stopLoading();
            
            Swal.fire({
              icon: 'success',
              title: 'Objectifs attribués',
              text: `${selectedIds.length} objectif(s) ont été liés à la formation avec succès`,
              timer: 2000,
              showConfirmButton: false
            });

            this.closeSearchGlobalObjectives();
          },
          error: (err) => {
            console.error('❌ Error reloading formation after linking:', err);
            this.stopLoading();
            
            // Even if reload fails, add objectives locally as fallback
            selectedIds.forEach(id => {
              const objective = this.globalObjectivesSearchResults.find(r => r.id === id);
              if (objective && !this.objectifsGlobauxExemples.find(o => o.id === id)) {
                this.objectifsGlobauxExemples.push({
                  id: objective.id,
                  label: objective.label,
                  selected: true,
                  _backendData: objective._backendData
                });
              }
            });
            
            this.cdr.markForCheck();
            this.closeSearchGlobalObjectives();
          }
        });
      },
      error: (err: any) => {
        console.error('❌ Error linking objectives:', err);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de lier les objectifs à la formation. Veuillez réessayer.',
          footer: err.error?.message || 'Erreur inconnue'
        });
      }
    });
  }

  closeSearchGlobalObjectives(): void {
    this.showSearchGlobalObjectives = false;
    this.searchGlobalObjectivesQuery = '';
    this.globalObjectivesSearchResults = [];
    this.selectedGlobalObjectivesForLink = {};
    this.cdr.detectChanges();
  }

  getSelectedGlobalObjectivesCount(): number {
    return Object.values(this.selectedGlobalObjectivesForLink).filter(v => v).length;
  }

  // ========== SPECIFIC OBJECTIVES SEARCH AND COPY/LINK ==========

  showSelectGlobalWarning(): void {
    Swal.fire({
      icon: 'warning',
      title: 'Attention',
      text: 'Veuillez d\'abord sélectionner un objectif global pour pouvoir copier des objectifs spécifiques.'
    });
  }

  showSelectSpecificWarning(): void {
    Swal.fire({
      icon: 'warning',
      title: 'Attention',
      text: 'Veuillez d\'abord sélectionner un objectif spécifique pour pouvoir copier des contenus.'
    });
  }

  showSelectPlanWarning(): void {
    Swal.fire({
      icon: 'warning',
      title: 'Attention',
      text: 'Veuillez d\'abord sélectionner un plan de formation pour pouvoir copier des contenus.'
    });
  }

  openSearchSpecificObjectives(globalObjectiveId: number): void {
    if (!this.generalInfoSaved) { this.promptGeneralInfo(); return; }
    this.hideAllForms();
    this.showSearchSpecificObjectives = true;
    this.searchSpecificObjectivesQuery = '';
    this.specificObjectivesSearchResults = [];
    this.selectedSpecificObjectivesForLink = {};
    this.targetGlobalObjectiveForSpecific = globalObjectiveId;
    this.selectedGlobal = null;
    this.selectedSpecific = null;
  }

  searchSpecificObjectives(): void {
    const query = this.searchSpecificObjectivesQuery.trim();
    if (!query) {
      this.specificObjectivesSearchResults = [];
      return;
    }

    this.isSearchingSpecificObjectives = true;
    this.startLoading('Recherche des objectifs spécifiques...');

    this.formationService.searchObjectifsSpecifiques(query).subscribe({
      next: (objectives: any[]) => {
        this.isSearchingSpecificObjectives = false;
        this.stopLoading();
        // Map backend response to UI format
        this.specificObjectivesSearchResults = objectives.map((obj: any) => ({
          id: obj.idObjectifSpec,
          label: obj.titre || 'Unnamed Objective',
          description: obj.description,
          tags: obj.tags,
          objectifGlobalId: obj.objectifGlobalId,
          isSelected: this.selectedSpecificObjectivesForLink[obj.idObjectifSpec] || false,
          _backendData: obj
        }));
        
      },
      error: (err) => {
        console.error('❌ Error searching specific objectives:', err);
        this.isSearchingSpecificObjectives = false;
        this.stopLoading();
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de rechercher les objectifs spécifiques.'
        });
      }
    });
  }

  toggleSpecificObjectiveSelection(objectiveId: number): void {
    if (this.selectedSpecificObjectivesForLink[objectiveId] === undefined) {
      this.selectedSpecificObjectivesForLink[objectiveId] = true;
    } else {
      this.selectedSpecificObjectivesForLink[objectiveId] = !this.selectedSpecificObjectivesForLink[objectiveId];
    }
  }

  assignSelectedSpecificObjectives(): void {
    const selectedIds = Object.keys(this.selectedSpecificObjectivesForLink)
      .filter(id => this.selectedSpecificObjectivesForLink[parseInt(id)])
      .map(id => parseInt(id));

    if (selectedIds.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'Attention',
        text: 'Veuillez sélectionner au moins un objectif spécifique à copier et attribuer.'
      });
      return;
    }

    if (!this.targetGlobalObjectiveForSpecific) {
      Swal.fire({
        icon: 'error',
        title: 'Erreur',
        text: 'Aucun objectif global cible n\'a été défini.'
      });
      return;
    }

    this.startLoading('Attribution des objectifs spécifiques...');

    // Copy and link each selected objective
    const copyLinkObservables = selectedIds.map(id => 
      this.formationService.copyAndLinkObjectifSpecifique(id, this.targetGlobalObjectiveForSpecific!, this.form.idFormation!)
    );

    // Execute all copy/link operations in parallel
    forkJoin(copyLinkObservables).subscribe({
      next: () => {
        
        // Reload the formation to get the updated objectives
        this.formationService.getFormationById(this.form.idFormation!).subscribe({
          next: (formation) => {
            
            // Update the tree data with the fresh data from backend
            this.loadTreeData(formation);
            
            // Trigger change detection
            this.cdr.markForCheck();

            this.stopLoading();
            
            Swal.fire({
              icon: 'success',
              title: 'Objectifs spécifiques attribués',
              text: `${selectedIds.length} objectif(s) spécifique(s) ont été copiés et liés avec succès`,
              timer: 2000,
              showConfirmButton: false
            });

            this.closeSearchSpecificObjectives();
          },
          error: (err) => {
            console.error('❌ Error reloading formation after copying/linking:', err);
            this.stopLoading();
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Les objectifs ont été liés mais une erreur est survenue lors du rechargement.'
            });
            this.closeSearchSpecificObjectives();
          }
        });
      },
      error: (err: any) => {
        console.error('❌ Error copying/linking specific objectives:', err);
        this.stopLoading();
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de copier et lier les objectifs spécifiques. Veuillez réessayer.',
          footer: err.error?.message || 'Erreur inconnue'
        });
      }
    });
  }

  closeSearchSpecificObjectives(): void {
    this.showSearchSpecificObjectives = false;
    this.searchSpecificObjectivesQuery = '';
    this.specificObjectivesSearchResults = [];
    this.selectedSpecificObjectivesForLink = {};
    this.targetGlobalObjectiveForSpecific = null;
    this.cdr.detectChanges();
  }

  getSelectedSpecificObjectivesCount(): number {
    return Object.values(this.selectedSpecificObjectivesForLink).filter(v => v).length;
  }

  // ==================== CONTENU JOUR SEARCH ====================

  onClickSearchContenusJour(): void {
    if (!this.selectedSpecific) {
      this.showSelectSpecificWarning();
      return;
    }
    const specificId = this.selectedSpecific._backendData?.idObjectifSpec || this.selectedSpecific.id;
    this.openSearchContenusJour(specificId);
  }

  openSearchContenusJour(specificObjectiveId: number): void {
    if (!specificObjectiveId) {
      Swal.fire({
        icon: 'warning',
        title: 'Objectif spécifique requis',
        text: 'Veuillez sélectionner un objectif spécifique avant de rechercher des contenus.'
      });
      return;
    }

    this.hideAllForms();
    this.targetObjectifSpecifiqueForContenu = specificObjectiveId;
    this.targetPlanFormationForContenu = this.selectedPlan?.id || null;
    this.showSearchContenusJour = true;
    this.searchContenusJourQuery = '';
    this.contenusJourSearchResults = [];
    this.selectedContenusJourForLink = {};
  }

  searchContenusJour(): void {
    const query = this.searchContenusJourQuery.trim();
    
    this.isSearchingContenusJour = true;

    this.formationService.searchContenusJour({
      contenu: query || undefined,
      isCopied: true  // Only show copied contenus (those linked to objectives)
    }).subscribe({
      next: (contenus: any[]) => {
        this.isSearchingContenusJour = false;
        this.contenusJourSearchResults = contenus.map((contenu: any) => ({
          id: contenu.idContenuJour,
          label: contenu.contenu || 'Contenu sans titre',
          moyenPedagogique: contenu.moyenPedagogique,
          supportPedagogique: contenu.supportPedagogique,
          nbHeuresTheoriques: contenu.nbHeuresTheoriques,
          nbHeuresPratiques: contenu.nbHeuresPratiques,
          tags: contenu.tags,
          objectifSpecifiqueId: contenu.objectifSpecifiqueId,
          objectifSpecifiqueTitre: contenu.objectifSpecifiqueTitre,
          planFormationId: contenu.planFormationId,
          isSelected: this.selectedContenusJourForLink[contenu.idContenuJour] || false,
          _backendData: contenu
        }));
        
      },
      error: (err) => {
        console.error('❌ Error searching contenus jour:', err);
        this.isSearchingContenusJour = false;
        this.stopLoading();
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de rechercher les contenus jour.'
        });
      }
    });
  }

  toggleContenuJourSelection(contenuId: number): void {
    if (this.selectedContenusJourForLink[contenuId] === undefined) {
      this.selectedContenusJourForLink[contenuId] = true;
    } else {
      this.selectedContenusJourForLink[contenuId] = !this.selectedContenusJourForLink[contenuId];
    }
  }

  assignSelectedContenusJour(): void {
    const selectedIds = Object.keys(this.selectedContenusJourForLink)
      .filter(id => this.selectedContenusJourForLink[parseInt(id)])
      .map(id => parseInt(id));

    if (selectedIds.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'Attention',
        text: 'Veuillez sélectionner au moins un contenu à copier et attribuer.'
      });
      return;
    }

    if (!this.targetObjectifSpecifiqueForContenu) {
      Swal.fire({
        icon: 'error',
        title: 'Erreur',
        text: 'Objectif spécifique non défini.'
      });
      return;
    }

    // Get plan formation ID - check multiple sources
    let planFormationId: number | undefined;
    
    // First try: selected plan
    if (this.selectedPlan?.id) {
      planFormationId = this.selectedPlan.id;
    }
    // Second try: first plan in list
    else if (this.plansFormation && this.plansFormation.length > 0) {
      planFormationId = this.plansFormation[0].id;
    }
    // Third try: get from one of the search results (they all should have the same plan)
    else if (this.contenusJourSearchResults && this.contenusJourSearchResults.length > 0) {
      const firstResult = this.contenusJourSearchResults[0];
      planFormationId = (firstResult as any).planFormationId;
    }

    this.startLoading('Attribution des contenus...');

    const copyLinkObservables = selectedIds.map(id => 
      this.formationService.copyAndLinkContenuJour(
        id,
        this.targetObjectifSpecifiqueForContenu!,
        this.form.idFormation!,
        planFormationId ?? null,  // Convert undefined to null
        this.selectedNiveauForAssignment || undefined,
        this.selectedNiveauForAssignment ? this.getNiveauLabel(this.selectedNiveauForAssignment) : undefined
      )
    );

    forkJoin(copyLinkObservables).subscribe({
      next: () => {
        
        // Reload the specific objective's contenus
        if (this.selectedSpecific) {
          this.loadSpecificObjectiveContenus(this.selectedSpecific);
        }

        this.stopLoading();

        Swal.fire({
          icon: 'success',
          title: 'Contenus attribués',
          text: `${selectedIds.length} contenu(s) ont été copiés et liés avec succès`,
          timer: 2000,
          showConfirmButton: false
        });

        this.closeSearchContenusJour();
      },
      error: (err: any) => {
        console.error('❌ Error copying/linking contenus jour:', err);
        this.stopLoading();
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de copier et lier les contenus. Veuillez réessayer.',
          footer: err.error?.message || 'Erreur inconnue'
        });
      }
    });
  }

  closeSearchContenusJour(): void {
    this.showSearchContenusJour = false;
    this.searchContenusJourQuery = '';
    this.contenusJourSearchResults = [];
    this.selectedContenusJourForLink = {};
    this.targetObjectifSpecifiqueForContenu = null;
    this.targetPlanFormationForContenu = null;
    this.selectedNiveauForAssignment = null;
    this.cdr.detectChanges();
  }

  getNiveauLabel(niveauId: number): string {
    const niveau = this.availableNiveaux.find(n => n.id === niveauId);
    return niveau ? niveau.label.toUpperCase() : '';
  }

  getSelectedContenusJourCount(): number {
    return Object.values(this.selectedContenusJourForLink).filter(v => v).length;
  }

  // ==================== END CONTENU JOUR SEARCH ====================

  editGlobalObjective(objective: any): void {
    this.hideAllForms();
    this.isEditingGlobal = true;
    this.editingGlobalId = objective.id;
    this.newGlobalObjectiveLabel = objective.label;
    this.showAddGlobalForm = true;
  }

  editSpecificObjective(objective: any): void {
    if (!this.generalInfoSaved) { this.promptGeneralInfo(); return; }
    this.hideAllForms();
    this.isEditingSpecific = true;
    this.editingSpecificId = objective.id;
    this.newSpecificObjectiveLabel = objective.label;
    // Try to get globalId from multiple sources
    this.newSpecificGlobalId = objective.globalId || objective._backendData?.ObjectifGlobalId || objective._backendData?.objectifGlobalId || objective._backendData?.idObjectifGlobal || undefined;
    this.showAddSpecificForm = true;
  }

  openAddSpecificForm(): void {
    if (!this.generalInfoSaved) { this.promptGeneralInfo(); return; }
    this.hideAllForms();
    this.isEditingSpecific = false;
    this.editingSpecificId = null;
    this.newSpecificObjectiveLabel = '';
    this.newSpecificGlobalId = undefined;
    this.selectedSpecific = null;
    this.selectedGlobal = null;
    this.showAddSpecificForm = true;
  }

  openContentBuilder(): void {
    if (!this.generalInfoSaved) { this.promptGeneralInfo(); return; }
    this.hideAllForms();
    this.showContentBuilder = true;
  }

  onTreeSelectGlobal(g: any): void {
    if (!this.generalInfoSaved) { this.promptGeneralInfo(); return; }
    this.hideAllForms();
    this.selectedGlobal = g;
    this.selectedSpecific = null;
    this.showAddGlobalForm = false;
    this.showAddSpecificForm = false;
    this.showLinkForm = false;
    this.showContentBuilder = false;
    this.showContentDetailAssigner = false;
    this.showPlanGenerator = false;
    this.showContentDetailCreator = false;
  }

  onTreeSelectSpecific(s: any): void {
    if (!this.generalInfoSaved) { this.promptGeneralInfo(); return; }
    this.hideAllForms();
    this.selectedSpecific = s;
    this.selectedGlobal = null;
  }

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private formationService: FormationService,
    private contenuDetailleService: ContenuDetailleService,
    private contenuJourFormationService: ContenuJourFormationService,
    private pdfGeneratorService: PdfGeneratorService,
    private formateurService: FormateurService,
    private planFormationService: PlanFormationService,
    private cdr: ChangeDetectorRef
  ) {
    this.loadContentDetailLibrary();
    // Expand some nodes by default for visualization
    this.expandedGlobals = [1, 2, 3];
    this.expandedSpecifics = [1, 2, 3, 4];
  }

  ngOnInit(): void {
    // Display general information by default when opening the page
    this.showGeneralInfo = true;
    this.setupContentSearch();
    
    // Load formateurs from API
    this.loadFormateurs();
    
    // If navigated with a formation ID (Manage), preload the formation data
    this.route.queryParamMap.subscribe(params => {
      const idParam = params.get('id');
      const formationId = idParam ? Number(idParam) : null;
      
      if (formationId) {
        // EDITING MODE: Load domaines first, then load formation
        this.isLoadingFormation = true;
        this.startLoading('Chargement de la formation...');
        
        this.formationService.getAllDomaines().subscribe({
          next: (ds) => { 
            this.domaines = ds;
            // Now load the formation
            this.loadFormationForEdit(formationId);
          },
          error: (e) => {
            console.error('Erreur chargement domaines', e);
            this.stopLoading();
            this.isLoadingFormation = false;
          }
        });
      } else {
        // NEW FORMATION MODE: Start with empty objectives and content
        this.startLoading('Initialisation...');
        
        this.formationService.getAllDomaines().subscribe({
          next: (ds) => { 
            this.domaines = ds; 
            // Initialize with empty objectives and content
            this.objectifsGlobauxExemples = [];
            this.objectifsSpecifiquesExemples = [];
            this.contentItems = [];
            this.contentIdCounter = 0;
            
            this.stopLoading();
            
            // Auto-expand all globals and specifics in the tree when data loads
            setTimeout(() => {
              this.expandedGlobals = this.objectifsGlobauxExemples.map(g => g.id);
              this.expandedSpecifics = this.objectifsSpecifiquesExemples.map(s => s.id);
            }, 100);
          },
          error: (e) => {
            console.error('Erreur chargement domaines', e);
            this.stopLoading();
          }
        });
      }
    });
  }

  private loadObjectifsGlobaux(): void {
    this.formationService.getAllObjectifsGlobaux().subscribe({
      next: (objs) => {
        this.objectifsGlobauxExemples = objs.map((o: any) => ({
          id: o.idObjectifGlobal ?? o.id ?? ++this.gid,
          label: o.libelle || '',
          selected: false
        }));
        const ids = objs.map((o: any) => o.idObjectifGlobal ?? o.id).filter((id: any) => id != null);
        this.gid = ids.length ? Math.max(...ids) : this.gid;
      },
      error: (e) => console.error('Erreur chargement objectifs globaux', e)
    });
  }

  private loadObjectifsSpecifiques(): void {
    this.formationService.getAllObjectifsSpecifiques().subscribe({
      next: (objs) => {
        this.objectifsSpecifiquesExemples = objs.map((o: any) => ({
          id: o.idObjectifSpecifique ?? o.idObjectifSpec ?? o.id ?? ++this.sid,
          label: o.titre || '',
          selected: false,
          globalId: o.ObjectifGlobalId ?? o.objectifGlobalId ?? o.idObjectifGlobal,
          _backendData: o
        }));
        const ids = objs.map((o: any) => o.idObjectifSpec ?? o.id).filter((id: any) => id != null);
        this.sid = ids.length ? Math.max(...ids) : this.sid;
      },
      error: (e) => console.error('Erreur chargement objectifs spécifiques', e)
    });
  }

  private loadContenusJour(): void {
    this.formationService.getAllContenusJour().subscribe({
      next: (contenus) => {
        this.contentItems = contenus.map((c: any, index: number) => ({
          id: c.idContenuJour ?? ++this.contentIdCounter,
          title: c.contenu || '',
          description: c.moyenPedagogique || '',
          detail: c.supportPedagogique || '',
          hoursPractical: c.nbHeuresPratiques || 0,
          hoursTheoretical: c.nbHeuresTheoriques || 0,
          day: 1, // TODO: Calculate from idPlanFormation or add day field in backend
          specificId: c.objectifSpecifiqueId,
          order: index + 1,
          assignedContentDetails: c.assignedContenuDetailleIds || [],
          _backendData: c
        }));
        const ids = contenus.map((c: any) => c.idContenuJour).filter((id: any) => id != null);
        this.contentIdCounter = ids.length ? Math.max(...ids) : 0;
        
        // Load assigned contenu details for each content item to get hours
        this.loadAssignedContentDetailsForAll();
      },
      error: (e) => console.error('Erreur chargement contenus jour', e)
    });
  }

  // Load assigned contenu details for all content items to calculate hours
  private loadAssignedContentDetailsForAll(): void {
    this.contentItems.forEach(item => {
      // Only make API call if we have a valid backend idContenuJour
      if (item._backendData?.idContenuJour && item._backendData.idContenuJour > 0) {
        this.contenuJourFormationService.getAssignedContenus(item._backendData.idContenuJour).subscribe({
          next: (assignedContenus) => {
            let contenusArray = Array.isArray(assignedContenus) && assignedContenus.length === 1 && Array.isArray(assignedContenus[0])
              ? assignedContenus[0]
              : assignedContenus;
            
            (item as any)._assignedContenusWithNiveau = contenusArray;
            item.assignedContentDetails = contenusArray
              .map((c: any) => c.idContenuDetaille)
              .filter((id: any) => id != null);
            
            this.cdr.detectChanges();
          },
          error: () => {
            // API call failed, use hours from ContenuJourFormation (already loaded in contentItem)
          }
        });
      }
    });
  }

  private loadSpecificObjectiveContenus(specificObjective: any): void {
    const specificId = specificObjective._backendData?.idObjectifSpec || specificObjective.id;
    
    this.formationService.getContenusByObjectifSpecifique(specificId).subscribe({
      next: (contenus) => {
        // Update only the contenus for this specific objective
        const newContenus = contenus.map((c: any, index: number) => ({
          id: c.idContenuJour ?? ++this.contentIdCounter,
          title: c.contenu || '',
          description: c.moyenPedagogique || '',
          detail: c.supportPedagogique || '',
          hoursPractical: c.nbHeuresPratiques || 0,
          hoursTheoretical: c.nbHeuresTheoriques || 0,
          day: 1,
          specificId: c.objectifSpecifiqueId,
          order: index + 1,
          assignedContentDetails: c.assignedContenuDetailleIds || [],
          _backendData: c
        }));
        
        // Remove old contenus for this specific objective and add new ones
        this.contentItems = this.contentItems.filter(item => item.specificId !== specificId);
        this.contentItems.push(...newContenus);
        
        // Load assigned contenu details for the new contenus
        newContenus.forEach(item => {
          if (item._backendData?.idContenuJour) {
            this.contenuJourFormationService.getAssignedContenus(item._backendData.idContenuJour).subscribe({
              next: (assignedContenus) => {
                let contenusArray = Array.isArray(assignedContenus) && assignedContenus.length === 1 && Array.isArray(assignedContenus[0])
                  ? assignedContenus[0]
                  : assignedContenus;
                
                (item as any)._assignedContenusWithNiveau = contenusArray;
                item.assignedContentDetails = contenusArray
                  .map((c: any) => c.idContenuDetaille)
                  .filter((id: any) => id != null);
                
                this.cdr.detectChanges();
              },
              error: () => {}
            });
          }
        });
        
        this.cdr.markForCheck();
      },
      error: (e) => console.error('Erreur chargement contenus par objectif', e)
    });
  }

  private loadFormateurs(): void {
    this.isLoadingFormateurs = true;
    
    this.formateurService.getAllFormateurs().subscribe({
      next: (formateurs) => {
        this.formateurs = formateurs;
        this.isLoadingFormateurs = false;
      },
      error: (e) => {
        console.error('Erreur chargement formateurs', e);
        this.isLoadingFormateurs = false;
      }
    });
  }

  private loadFormationForEdit(id: number): void {
    this.isLoadingFormation = true;
    this.startLoading('Chargement de la formation...');
    
    this.formationService.getFormationById(id).subscribe({
      next: (formation) => {
        this.hydrateForm(formation);
        this.generalInfoSaved = true; // unlock rest of UI
        this.showGeneralInfo = true;
        
        // Load plans for this formation
        if (formation.idFormation) {
          this.loadPlansForFormation(formation.idFormation);
        }
        
        this.isLoadingFormation = false;
        this.stopLoading();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur chargement formation', err);
        this.isLoadingFormation = false;
        this.stopLoading();
        Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de charger la formation.' });
      }
    });
  }

  private hydrateForm(formation: Formation): void {
    
    // Update existing form object property by property for proper change detection
    this.form.idFormation = formation.idFormation;
    this.form.theme = formation.theme || '';
    this.form.descriptionTheme = formation.descriptionTheme || '';
    this.form.objectifGlobal = formation.objectifGlobal || '';
    this.form.objectifSpecifiqueGlobal = formation.objectifSpecifiqueGlobal || '';
    this.form.nombreHeures = formation.nombreHeures;
    this.form.prix = formation.prix;
    this.form.nombreMax = formation.nombreMax;
    this.form.populationCible = formation.populationCible || '';
    this.form.typeFormation = (formation.typeFormation as TypeFormation) || TypeFormation.En_Ligne;
    this.form.niveau = (formation.niveau as NiveauFormation) || NiveauFormation.Debutant;
    this.form.domaine = formation.domaine;
    this.form.type = formation.type;
    this.form.categorie = formation.categorie;
    this.form.sousCategorie = formation.sousCategorie;
    this.form.idDomaine = formation.domaine?.idDomaine;
    this.form.idType = formation.type?.idType;
    this.form.idCategorie = formation.categorie?.idCategorie;
    this.form.idSousCategorie = formation.sousCategorie?.idSousCategorie || formation.sousCategorie?.id;
    this.form.statut = formation.statut || FormationStatut.Brouillon;
    this.form.imageUrl = formation.imageUrl;
    this.form.programmesDetailes = formation.programmesDetailes || [];
    this.form.objectifsGlobaux = formation.objectifsGlobaux || [];
    // Load specific objectives (with contenus) from backend payload
    this.form.objectifsSpecifiques = (formation as any).objectifsSpecifiques || [];
    // Preserve already loaded contenus formation for PDF (no extra fetch)
    this.form.contenusFormation = (formation as any).contenusFormation || [];

    console.log('📋 Formation loaded:', {
      id: formation.idFormation,
      theme: formation.theme,
      programmesDetailesCount: this.form.programmesDetailes.length,
      programmesDetailes: this.form.programmesDetailes
    });

    // === LOAD ALL TREE DATA ===
    this.loadTreeData(formation);

    // Cascade-load dependent selects so dropdowns show correct values
    if (this.form.idDomaine) {
      // Find the matching domaine from the loaded list
      const matchingDomaine = this.domaines.find(d => d.idDomaine === this.form.idDomaine);
      if (matchingDomaine) {
        this.form.domaine = matchingDomaine;
      }
      
      this.formationService.getTypesByDomaine(this.form.idDomaine).subscribe({
        next: (types) => {
          this.typesDyn = types;
          
          // Find matching type from loaded list
          if (this.form.idType) {
            const matchingType = types.find(t => t.idType === this.form.idType);
            if (matchingType) {
              this.form.type = matchingType;
            }
            
            this.formationService.getCategoriesByType(this.form.idType).subscribe({
              next: (cats) => {
                this.categories = cats;
                
                // Find matching categorie from loaded list
                if (this.form.idCategorie) {
                  const matchingCategorie = cats.find(c => c.idCategorie === this.form.idCategorie);
                  if (matchingCategorie) {
                    this.form.categorie = matchingCategorie;
                  }
                  
                  this.formationService.getSousCategoriesByCategorie(this.form.idCategorie).subscribe({
                    next: (scs) => { 
                      this.sousCategories = scs;
                      
                      // Find matching sous-categorie from loaded list
                      if (this.form.idSousCategorie) {
                        const matchingSousCategorie = scs.find(sc => 
                          (sc.idSousCategorie === this.form.idSousCategorie) || 
                          (sc.id === this.form.idSousCategorie)
                        );
                        if (matchingSousCategorie) {
                          this.form.sousCategorie = matchingSousCategorie;
                        }
                      }
                      
                      this.cdr.detectChanges();
                    },
                    error: (e) => console.error('Erreur chargement sous-catégories', e)
                  });
                } else {
                  this.cdr.detectChanges();
                }
              },
              error: (e) => console.error('Erreur chargement catégories', e)
            });
          } else {
            this.cdr.detectChanges();
          }
        },
        error: (e) => console.error('Erreur chargement types', e)
      });
    }

  }

  close(): void {
    this.router.navigate(['/menu']);
  }

  refreshFormation(): void {
    if (!this.form.idFormation) {
      Swal.fire({
        icon: 'info',
        title: 'Aucune formation à actualiser',
        text: 'Veuillez d\'abord enregistrer la formation avant de l\'actualiser.'
      });
      return;
    }

    Swal.fire({
      icon: 'question',
      title: 'Actualiser les données ?',
      text: 'Cela rechargera toutes les données de la formation. Les modifications non enregistrées seront perdues.',
      showCancelButton: true,
      confirmButtonText: 'Actualiser',
      cancelButtonText: 'Annuler',
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33'
    }).then((result) => {
      if (result.isConfirmed) {
        this.startLoading('Actualisation des données...');
        
        // Hide all forms and reset selections
        this.hideAllForms();
        this.selectedGlobal = null;
        this.selectedSpecific = null;
        this.selectedContentForEdit = null;
        this.selectedContentItem = null;

        // Reload formation data
        this.loadFormationForEdit(this.form.idFormation!);

        Swal.fire({
          icon: 'success',
          title: 'Actualisé !',
          text: 'Les données ont été rechargées avec succès.',
          timer: 2000,
          showConfirmButton: false
        });
      }
    });
  }

  onQuit(): void {
    Swal.fire({
      icon: 'warning',
      title: 'Quitter la création ?',
      text: 'Des données non enregistrées peuvent être perdues. Êtes-vous sûr de vouloir quitter ?',
      showCancelButton: true,
      confirmButtonText: 'Quitter',
      cancelButtonText: 'Annuler',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.close();
      }
    });
  }

  get contentDays(): number[] {
    const days = Array.from(new Set(this.contentItems.map(c => c.day)));
    return days.sort((a, b) => a - b);
  }

  orderedContentByDay(day: number) {
    return this.contentItems
      .filter(c => c.day === day)
      .sort((a, b) => a.order - b.order);
  }

  // === Checkbox handlers to associate selections with the formation ===
  onToggleGlobalSelected(global: { id: number; label: string; selected: boolean }, checked: boolean): void {
    global.selected = checked;
    
    // Persist to backend immediately if formation exists
    if (this.form.idFormation) {
      if (checked) {
        // Link objectif global to formation
        this.formationService.linkObjectifGlobalToFormation(this.form.idFormation, global.id).subscribe({
          next: () => {
            this.syncSelectionsToForm();
          },
          error: (err) => {
            console.error('❌ Error linking objectif global:', err);
            global.selected = !checked; // Rollback on error
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Impossible de lier l\'objectif global. Veuillez réessayer.',
              timer: 2000
            });
          }
        });
      } else {
        // Unlink objectif global from formation
        this.formationService.unlinkObjectifGlobalFromFormation(this.form.idFormation, global.id).subscribe({
          next: () => {
            this.syncSelectionsToForm();
          },
          error: (err) => {
            console.error('❌ Error unlinking objectif global:', err);
            global.selected = !checked; // Rollback on error
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Impossible de délier l\'objectif global. Veuillez réessayer.',
              timer: 2000
            });
          }
        });
      }
    } else {
      // No formation yet, just update locally
      this.syncSelectionsToForm();
    }
  }

  onToggleSpecificSelected(specific: { id: number; label: string; selected: boolean; globalId?: number; _backendData?: any }, checked: boolean): void {
    specific.selected = checked;
    
    // Persist to backend immediately if formation exists
    if (this.form.idFormation) {
      if (checked) {
        // Link objectif specifique to formation
        this.formationService.linkObjectifSpecifiqueToFormation(this.form.idFormation, specific.id).subscribe({
          next: () => {
            this.syncSelectionsToForm();
          },
          error: (err) => {
            console.error('❌ Error linking objectif spécifique:', err);
            specific.selected = !checked; // Rollback on error
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Impossible de lier l\'objectif spécifique. Veuillez réessayer.',
              timer: 2000
            });
          }
        });
      } else {
        // Unlink objectif specifique from formation
        this.formationService.unlinkObjectifSpecifiqueFromFormation(this.form.idFormation, specific.id).subscribe({
          next: () => {
            this.syncSelectionsToForm();
          },
          error: (err) => {
            console.error('❌ Error unlinking objectif spécifique:', err);
            specific.selected = !checked; // Rollback on error
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Impossible de délier l\'objectif spécifique. Veuillez réessayer.',
              timer: 2000
            });
          }
        });
      }
    } else {
      // No formation yet, just update locally
      this.syncSelectionsToForm();
    }
  }

  onToggleContentSelected(contentId: number, checked: boolean): void {
    this.contentSelections[contentId] = checked;
    
    // Contenus are linked through ContenuJour which already has backend persistence
    // via the createContenuJour and assignContenus APIs
    this.syncSelectionsToForm();
  }

  // Build programmesDetailes from current selections (objectifs + contenus)
  private buildProgrammesFromSelections(): ProgrammeDetaile[] {
    console.log('🔨 buildProgrammesFromSelections called');
    console.log('objectifsGlobauxExemples count:', this.objectifsGlobauxExemples.length);
    console.log('objectifsSpecifiquesExemples count:', this.objectifsSpecifiquesExemples.length);
    console.log('contentItems count:', this.contentItems.length);
    
    const programmes: ProgrammeDetaile[] = [];

    // Helper to know if a content item is selected (if no selection done yet, consider all selected)
    const anySelectionMade = Object.keys(this.contentSelections).length > 0;
    const isSelected = (id: number) => (anySelectionMade ? !!this.contentSelections[id] : true);

    const anyGlobalSelected = this.objectifsGlobauxExemples.some(g => g.selected);
    const anySpecSelected = this.objectifsSpecifiquesExemples.some(s => s.selected);

    // Selected globals: explicit, or if none selected at all, take all as default
    const selectedGlobals = this.objectifsGlobauxExemples.filter(g =>
      g.selected || (!anyGlobalSelected && !anySpecSelected)
    );

    // For each global, add programmes for its selected specifics
    selectedGlobals.forEach(global => {
      const linkedSpecs = this.objectifsSpecifiquesExemples.filter(s =>
        s.globalId === global.id && (s.selected || (!anySpecSelected && !anyGlobalSelected))
      );
      linkedSpecs.forEach(spec => {
        // Contents linked to this specific objective and selected by checkbox
        const relatedContents = this.contentItems.filter(c => c.specificId === spec.id && isSelected(c.id));
        if (relatedContents.length === 0) return;

        // Group by day
        const dayMap = new Map<number, typeof relatedContents>();
        relatedContents.forEach(c => {
          if (!dayMap.has(c.day)) dayMap.set(c.day, []);
          dayMap.get(c.day)!.push(c);
        });

        const jours: JourFormation[] = [];
        dayMap.forEach((contents, dayNumber) => {
          const contenuDetailles: ContenuDetaille[] = contents.map(content => ({
            contenusCles: [
              content.title,
              ...(content.description ? [content.description] : []),
              ...(content.detail ? [content.detail] : [])
            ],
            methodesPedagogiques: `
              ${content.staff ? `Formateur: ${content.staff}` : ''}
              ${content.level ? `Niveau: ${content.level}` : ''}
            `.trim(),
            dureeTheorique: content.hoursTheoretical || 0,
            dureePratique: content.hoursPractical || 0
          }));

          jours.push({ numeroJour: dayNumber, contenus: contenuDetailles });
        });

        jours.sort((a, b) => a.numeroJour - b.numeroJour);
        programmes.push({ titre: spec.label, jours });
      });
    });

    // Orphan specifics (not linked to a global) that are selected
    const orphanSpecs = this.objectifsSpecifiquesExemples.filter(s =>
      s.globalId == null && (s.selected || (!anySpecSelected && !anyGlobalSelected))
    );
    orphanSpecs.forEach(spec => {
      const relatedContents = this.contentItems.filter(c => c.specificId === spec.id && isSelected(c.id));
      if (relatedContents.length === 0) return;

      const dayMap = new Map<number, typeof relatedContents>();
      relatedContents.forEach(c => {
        if (!dayMap.has(c.day)) dayMap.set(c.day, []);
        dayMap.get(c.day)!.push(c);
      });

      const jours: JourFormation[] = [];
      dayMap.forEach((contents, dayNumber) => {
        const contenuDetailles: ContenuDetaille[] = contents.map(content => ({
          contenusCles: [
            content.title,
            ...(content.description ? [content.description] : [])
          ],
          methodesPedagogiques: content.detail || '',
          dureeTheorique: content.hoursTheoretical || 0,
          dureePratique: content.hoursPractical || 0
        }));

        jours.push({ numeroJour: dayNumber, contenus: contenuDetailles });
      });

      jours.sort((a, b) => a.numeroJour - b.numeroJour);
      programmes.push({ titre: spec.label, jours });
    });

    return programmes;
  }

  // Apply selections to the form so they are saved with the formation
  private syncSelectionsToForm(): void {
    // Update formatted objectifs text
    this.applySelectedGlobauxToForm();

    // Update programme details from selections
    const programmes = this.buildProgrammesFromSelections();
    this.form.programmesDetailes = programmes;

    // Update total hours
    const totalHours = programmes
      .flatMap(p => p.jours)
      .flatMap(j => j.contenus)
      .reduce((sum, c) => sum + (c.dureeTheorique || 0) + (c.dureePratique || 0), 0);
    this.form.nombreHeures = totalHours;
  }

  

  // ---- Load Tree Data ----
  private loadTreeData(formation: Formation): void {
    // Build a map of ContenuDetaille hours from programmesDetailes
    // programmesDetailes → jours → contenus contains dureeTheorique/dureePratique from ContentLevel
    const contenuDetailleHoursMap = new Map<number, { theorique: number, pratique: number }>();
    console.log('📊 loadTreeData - programmesDetailes:', (formation as any).programmesDetailes);
    if ((formation as any).programmesDetailes) {
      (formation as any).programmesDetailes.forEach((programme: any) => {
        if (programme.jours) {
          programme.jours.forEach((jour: any) => {
            if (jour.contenus) {
              console.log(`📊 Jour ${jour.numeroJour} contenus:`, jour.contenus);
              jour.contenus.forEach((contenu: any) => {
                console.log(`📊 Contenu ID=${contenu.idContenuDetaille}, dureeTheorique=${contenu.dureeTheorique}, dureePratique=${contenu.dureePratique}`);
                if (contenu.idContenuDetaille) {
                  contenuDetailleHoursMap.set(contenu.idContenuDetaille, {
                    theorique: contenu.dureeTheorique || 0,
                    pratique: contenu.dureePratique || 0
                  });
                }
              });
            }
          });
        }
      });
    }
    console.log('📊 Built contenuDetailleHoursMap:', contenuDetailleHoursMap);

    // Populate global objectives from formation data
    if (formation.objectifsGlobaux && formation.objectifsGlobaux.length > 0) {
      this.objectifsGlobauxExemples = formation.objectifsGlobaux.map((og: any) => ({
        id: og.idObjectifGlobal,
        label: og.libelle || og.titre || 'Unnamed Global',
        selected: false,
        _backendData: og
      }));
    }

    // Populate specific objectives and their content from formation data
    if (formation.objectifsSpecifiques && formation.objectifsSpecifiques.length > 0) {
      this.objectifsSpecifiquesExemples = formation.objectifsSpecifiques.map((os: any) => ({
        id: os.idObjectifSpec,
        label: os.titre || 'Unnamed Specific',
        selected: false,
        globalId: os.idObjectifGlobal,
        _backendData: os
      }));

      // Populate content items from specific objectives' contenus
      this.contentItems = [];
      let contentIdCounter = 0;
      formation.objectifsSpecifiques.forEach((os: any) => {
        if (os.contenus && os.contenus.length > 0) {
          os.contenus.forEach((contenu: any) => {
            // Backend returns: contenu, moyenPedagogique, supportPedagogique, nbHeuresTheoriques, nbHeuresPratiques, numeroJour, staff, niveau
            const title = contenu.contenu || contenu.titre || contenu.title || 'Unnamed Content';
            
            // Get hours from programmesDetailes (ContentLevel hours) if available
            // Otherwise fallback to ContenuJourFormation hours (nbHeuresTheoriques/nbHeuresPratiques)
            let hoursPractical = 0;
            let hoursTheoretical = 0;
            
            // Check if we have assigned contenu detailles with hours from programmesDetailes
            console.log(`📊 ContenuJour "${title}" assignedContenuDetailleIds:`, contenu.assignedContenuDetailleIds);
            if (contenu.assignedContenuDetailleIds && contenu.assignedContenuDetailleIds.length > 0) {
              contenu.assignedContenuDetailleIds.forEach((cdId: number) => {
                const hours = contenuDetailleHoursMap.get(cdId);
                console.log(`📊 Looking up cdId=${cdId} in map:`, hours);
                if (hours) {
                  hoursTheoretical += hours.theorique;
                  hoursPractical += hours.pratique;
                }
              });
            }
            
            console.log(`📊 After lookup - hoursTheoretical=${hoursTheoretical}, hoursPractical=${hoursPractical}`);
            // Fallback to ContenuJourFormation hours if no assigned contenu detaille hours found
            if (hoursTheoretical === 0 && hoursPractical === 0) {
              hoursPractical = contenu.nbHeuresPratiques || contenu.hoursPractical || 0;
              hoursTheoretical = contenu.nbHeuresTheoriques || contenu.hoursTheoretical || 0;
              console.log(`📊 Using fallback nbHeures - hoursTheoretical=${hoursTheoretical}, hoursPractical=${hoursPractical}`);
            }
            
            const dayNumber = Number(contenu.numeroJour ?? contenu.day ?? 1);
            this.contentItems.push({
              id: contenu.idContenuJour || ++contentIdCounter,
              title: title,
              description: contenu.moyenPedagogique || contenu.description || '',
              detail: contenu.supportPedagogique || contenu.detail || '',
              level: contenu.niveau || contenu.level || '',
              staff: contenu.staff || '',
              hoursPractical: hoursPractical,
              hoursTheoretical: hoursTheoretical,
              day: dayNumber,
              specificId: os.idObjectifSpec,
              order: contenu.ordre || contentIdCounter,
              assignedContentDetails: contenu.assignedContentDetails || contenu.assignedContenuDetailleIds || [],
              _backendData: { ...contenu, numeroJour: dayNumber }
            });
          });
        }
      });
      this.contentIdCounter = contentIdCounter;
      
      // Load assigned contenu details for each content item to get hours from ContentLevel
      // NOTE: Hours are now calculated from programmesDetailes in loadTreeData, so this is optional
      // this.loadAssignedContentDetailsForAll();
    }

    // Expand all globals and specifics by default after data loads
    setTimeout(() => {
      this.expandedGlobals = this.objectifsGlobauxExemples.map(g => g.id);
      this.expandedSpecifics = this.objectifsSpecifiquesExemples.map(s => s.id);
    }, 100);
  }

  // ---- Tree helpers ----
  isGlobalExpanded(id: number): boolean {
    return this.expandedGlobals.includes(id);
  }

  toggleGlobalExpand(id: number): void {
    if (this.isGlobalExpanded(id)) {
      this.expandedGlobals = this.expandedGlobals.filter(g => g !== id);
    } else {
      this.expandedGlobals = [...this.expandedGlobals, id];
    }
  }

  isSpecificExpanded(id: number): boolean {
    return this.expandedSpecifics.includes(id);
  }

  toggleSpecificExpand(id: number): void {
    if (this.isSpecificExpanded(id)) {
      this.expandedSpecifics = this.expandedSpecifics.filter(s => s !== id);
    } else {
      this.expandedSpecifics = [...this.expandedSpecifics, id];
    }
  }

  specificsForGlobal(globalId: number) {
    // First, try to get specifics with matching globalId
    let filtered = this.objectifsSpecifiquesExemples.filter(s => s.globalId === globalId);
    
    // If no specifics found and this is the first global, also include orphaned specifics
    // (specifics with no globalId set, which happens when backend doesn't return it)
    if (filtered.length === 0 && globalId === this.objectifsGlobauxExemples[0]?.id) {
      const orphaned = this.objectifsSpecifiquesExemples.filter(s => !s.globalId || s.globalId === undefined || s.globalId === null);
      // Only show orphaned if they exist and no other global has claimed them
      const isOrphanedClaimedByOtherGlobal = orphaned.some(o => 
        this.objectifsGlobauxExemples.some(g => g.id !== globalId && this.specificsForGlobal(g.id).includes(o))
      );
      if (!isOrphanedClaimedByOtherGlobal && orphaned.length > 0) {
        filtered = orphaned;
      }
    }
    
    return filtered;
  }

  contentsForSpecific(specificId: number) {
    const filtered = this.contentItems
      .filter(c => c.specificId === specificId)
      .sort((a, b) => a.order - b.order);
    return filtered;
  }

  // ---- Hours rollups ----
  private itemHours(item: { hoursPractical?: number; hoursTheoretical?: number }): number {
    return (item.hoursPractical || 0) + (item.hoursTheoretical || 0);
  }

  // Calculate hours from assigned ContenuDetaille levels
  getContentItemHoursFromDetails(contentItem: any): { theorique: number, pratique: number } {
    let totalTheorique = 0;
    let totalPratique = 0;

    // First check if we have full objects loaded (from openContentDetailAssigner)
    if (contentItem._assignedContenusWithNiveau && contentItem._assignedContenusWithNiveau.length > 0) {
      contentItem._assignedContenusWithNiveau.forEach((cd: any) => {
        // The backend already sums hours from levels, so use direct values
        totalTheorique += cd.dureeTheorique || 0;
        totalPratique += cd.dureePratique || 0;
      });
      return { theorique: totalTheorique, pratique: totalPratique };
    }

    // Fallback: try to find in library using IDs
    if (contentItem.assignedContentDetails && contentItem.assignedContentDetails.length > 0) {
      const assignedDetails = this.contentDetailLibrary.filter(cd => 
        contentItem.assignedContentDetails.includes(cd.id)
      );

      assignedDetails.forEach(cd => {
        if (cd.levels && Array.isArray(cd.levels)) {
          cd.levels.forEach((level: any) => {
            totalTheorique += level.dureeTheorique || 0;
            totalPratique += level.dureePratique || 0;
          });
        } else {
          totalTheorique += cd.dureeTheorique || 0;
          totalPratique += cd.dureePratique || 0;
        }
      });
      
      if (totalTheorique > 0 || totalPratique > 0) {
        return { theorique: totalTheorique, pratique: totalPratique };
      }
    }

    // Final fallback: use hours from ContenuJourFormation (nbHeuresTheoriques/nbHeuresPratiques)
    // These are already loaded in the contentItem from the formation response
    return { 
      theorique: contentItem.hoursTheoretical || 0, 
      pratique: contentItem.hoursPractical || 0 
    };
  }

  // Get total hours for a content item (from assigned ContenuDetaille)
  private itemHoursFromDetails(item: any): number {
    const hours = this.getContentItemHoursFromDetails(item);
    return hours.theorique + hours.pratique;
  }

  totalHoursOverall(): number {
    return this.contentItems.reduce((sum, it) => sum + this.itemHoursFromDetails(it), 0);
  }

  totalHoursForSpecific(specificId: number): number {
    return this.contentItems
      .filter(c => c.specificId === specificId)
      .reduce((sum, it) => sum + this.itemHoursFromDetails(it), 0);
  }

  totalHoursForGlobal(globalId: number): number {
    const specificIds = this.objectifsSpecifiquesExemples.filter(s => s.globalId === globalId).map(s => s.id);
    return this.contentItems
      .filter(c => c.specificId && specificIds.includes(c.specificId))
      .reduce((sum, it) => sum + this.itemHoursFromDetails(it), 0);
  }

  addContentItem(): void {
    if (!this.contentForm.title?.trim()) {
      return;
    }

    // Validate that idObjectifSpecifique is provided
    if (!this.contentForm.specificId) {
      alert('Veuillez sélectionner un objectif spécifique');
      return;
    }
    
    // Prepare payload for backend
    const payload = {
      contenu: this.contentForm.title.trim(),
      moyenPedagogique: this.contentForm.description?.trim() || undefined,
      supportPedagogique: this.contentForm.detail?.trim() || undefined,
      nbHeuresTheoriques: Number(this.contentForm.hoursTheoretical) || 0,
      nbHeuresPratiques: Number(this.contentForm.hoursPractical) || 0,
      idObjectifSpecifique: this.contentForm.specificId,
      idPlanFormation: undefined, // TODO: Get from formation context when available
      numeroJour: Number(this.contentForm.day) || 1,
      staff: this.contentForm.staff?.trim() || undefined,
      niveau: this.contentForm.level?.trim() || undefined
    };

    // Call backend to create contenu jour
    this.isLoadingContent = true;
    this.startLoading('Création du contenu...');
    
    this.formationService.createContenuJour(payload).subscribe({
      next: (response) => {
        // Handle different response formats (mediator might wrap in array)
        let contenuJourData = response;
        if (Array.isArray(response) && response.length > 0) {
          contenuJourData = response[0];
        }
        
        const nextId = contenuJourData.idContenuJour || ++this.contentIdCounter;
        const maxOrderForDay = Math.max(0, ...this.contentItems.filter(c => c.day === this.contentForm.day).map(c => c.order));
        
        const newItem = {
          id: nextId,
          title: this.contentForm.title.trim(),
          description: this.contentForm.description?.trim() || '',
          detail: this.contentForm.detail?.trim() || '',
          level: this.contentForm.level?.trim() || '',
          staff: this.contentForm.staff?.trim() || '',
          hoursPractical: Number(this.contentForm.hoursPractical) || 0,
          hoursTheoretical: Number(this.contentForm.hoursTheoretical) || 0,
          day: this.contentForm.day,
          specificId: this.contentForm.specificId,
          order: maxOrderForDay + 1,
          assignedContentDetails: [],
          _backendData: contenuJourData
        };
        
        this.contentItems.push(newItem);

        this.contentForm = { title: '', description: '', detail: '', level: '', staff: '', hoursPractical: 0, hoursTheoretical: 0, day: 1, specificId: undefined };
        this.isLoadingContent = false;
        this.stopLoading();
      },
      error: (err) => {
        console.error('Erreur création contenu jour', err);
        this.isLoadingContent = false;
        this.stopLoading();
        alert('Erreur lors de la création du contenu jour');
      }
    });
  }

  editContentItem(item: any): void {
    this.hideAllForms();
    
    // Create a copy to avoid directly modifying the array item until save
    // Set this AFTER hideAllForms() so it doesn't get cleared
    this.selectedContentForEdit = {
      id: item.id,
      title: item.title || '',
      description: item.description || '',
      detail: item.detail || '',
      level: item.level || '',
      staff: item.staff || '',
      day: item.day || 1,
      specificId: item.specificId,
      hoursPractical: item.hoursPractical || 0,
      hoursTheoretical: item.hoursTheoretical || 0,
      order: item.order,
      assignedContentDetails: item.assignedContentDetails || [],
      _backendData: item._backendData,
      _originalItem: item // Keep reference to update later
    };
    
    this.showEditContentForm = true;
    this.selectedGlobal = null;
    this.selectedSpecific = null;
    // Scroll the right content section to top to make the form visible
    setTimeout(() => {
      const rightContent = document.querySelector('.right-content');
      if (rightContent) {
        rightContent.scrollTop = 0;
      }
    }, 0);
  }

  updateSelectedContentItem(): void {
    
    if (!this.selectedContentForEdit) return;

    // Validation
    this.contentValidationErrors = {};
    
    if (!this.selectedContentForEdit.title?.trim()) {
      this.contentValidationErrors['title'] = 'Le titre est obligatoire.';
      Swal.fire({
        icon: 'warning',
        title: 'Champ requis',
        text: 'Le titre du contenu est obligatoire.'
      });
      return;
    }

    if (this.selectedContentForEdit.title.trim().length < 3) {
      this.contentValidationErrors['title'] = 'Le titre doit contenir au moins 3 caractères.';
      Swal.fire({
        icon: 'warning',
        title: 'Titre trop court',
        text: 'Le titre doit contenir au moins 3 caractères.'
      });
      return;
    }

    if (this.selectedContentForEdit.title.trim().length > 200) {
      this.contentValidationErrors['title'] = 'Le titre ne peut pas dépasser 200 caractères.';
      Swal.fire({
        icon: 'warning',
        title: 'Titre trop long',
        text: 'Le titre ne peut pas dépasser 200 caractères.'
      });
      return;
    }

    if (!this.selectedContentForEdit.specificId) {
      this.contentValidationErrors['specificId'] = 'L\'objectif spécifique est obligatoire.';
      Swal.fire({
        icon: 'warning',
        title: 'Champ requis',
        text: 'Veuillez sélectionner un objectif spécifique.'
      });
      return;
    }

    if (this.selectedContentForEdit.day !== undefined && this.selectedContentForEdit.day < 1) {
      this.contentValidationErrors['day'] = 'Le jour doit être supérieur ou égal à 1.';
      Swal.fire({
        icon: 'warning',
        title: 'Jour invalide',
        text: 'Le jour doit être supérieur ou égal à 1.'
      });
      return;
    }

    if (this.selectedContentForEdit.hoursPractical !== undefined && this.selectedContentForEdit.hoursPractical < 0) {
      this.contentValidationErrors['hoursPractical'] = 'Les heures pratiques ne peuvent pas être négatives.';
      Swal.fire({
        icon: 'warning',
        title: 'Valeur invalide',
        text: 'Les heures pratiques ne peuvent pas être négatives.'
      });
      return;
    }

    if (this.selectedContentForEdit.hoursTheoretical !== undefined && this.selectedContentForEdit.hoursTheoretical < 0) {
      this.contentValidationErrors['hoursTheoretical'] = 'Les heures théoriques ne peuvent pas être négatives.';
      Swal.fire({
        icon: 'warning',
        title: 'Valeur invalide',
        text: 'Les heures théoriques ne peuvent pas être négatives.'
      });
      return;
    }

    // Update the original item in the array with the edited values
    const originalItem = this.selectedContentForEdit._originalItem;
    if (originalItem) {
      originalItem.title = this.selectedContentForEdit.title;
      originalItem.description = this.selectedContentForEdit.description;
      originalItem.detail = this.selectedContentForEdit.detail;
      originalItem.level = this.selectedContentForEdit.level;
      originalItem.day = this.selectedContentForEdit.day;
      originalItem.specificId = this.selectedContentForEdit.specificId;
      originalItem.hoursPractical = this.selectedContentForEdit.hoursPractical;
      originalItem.hoursTheoretical = this.selectedContentForEdit.hoursTheoretical;
      
      // Call backend update with the original item (which now has updated values)
      this.updateContentItem(originalItem);
    } else {
      // Fallback: update using selectedContentForEdit directly
      this.updateContentItem(this.selectedContentForEdit);
    }
    
    this.showEditContentForm = false;
    this.selectedContentForEdit = null;
  }

  deleteContentItem(id: number): void {
    const item = this.contentItems.find(c => c.id === id);
    if (!item) return;

    if (!confirm(`Êtes-vous sûr de vouloir supprimer "${item.title}" ?`)) {
      return;
    }

    // If item has backend ID, delete from backend
    if (item._backendData?.idContenuJour) {
      this.isLoadingContent = true;
      this.startLoading('Suppression du contenu...');
      
      this.formationService.deleteContenuJour(item._backendData.idContenuJour).subscribe({
        next: () => {
          this.contentItems = this.contentItems.filter(c => c.id !== id);
          this.isLoadingContent = false;
          this.stopLoading();
        },
        error: (err) => {
          console.error('Erreur suppression contenu jour', err);
          this.isLoadingContent = false;
          this.stopLoading();
          Swal.fire({ icon: 'error', title: 'Erreur', text: 'Erreur lors de la suppression du contenu jour' });
        }
      });
    } else {
      // No backend ID, just remove from array
      this.contentItems = this.contentItems.filter(c => c.id !== id);
    }
  }

  updateContentItem(item: any): void {
    if (!item._backendData?.idContenuJour) {
      alert('Impossible de mettre à jour: cet élément n\'a pas d\'identifiant backend');
      return;
    }

    const payload = {
      contenu: item.title,
      moyenPedagogique: item.description || undefined,
      supportPedagogique: item.detail || undefined,
      nbHeuresTheoriques: item.hoursTheoretical || 0,
      nbHeuresPratiques: item.hoursPractical || 0,
      idObjectifSpecifique: item.specificId || 0,
      idPlanFormation: item._backendData.idPlanFormation || undefined,
      numeroJour: item.day || 1,
      staff: item.staff || undefined,
      niveau: item.level || undefined
    };

    this.isLoadingContent = true;
    this.startLoading('Mise à jour du contenu...');
    
    this.formationService.updateContenuJour(item._backendData.idContenuJour, payload).subscribe({
      next: (response) => {
        item._backendData = response;
        this.isLoadingContent = false;
        this.stopLoading();
        Swal.fire({ 
          icon: 'success', 
          title: 'Contenu mis à jour', 
          text: 'Le contenu a été mis à jour avec succès!',
          timer: 2000,
          showConfirmButton: false
        });
      },
      error: (err) => {
        console.error('❌ Erreur mise à jour contenu jour', err);
        this.isLoadingContent = false;
        this.stopLoading();
        Swal.fire({ 
          icon: 'error', 
          title: 'Erreur', 
          text: 'Erreur lors de la mise à jour du contenu jour: ' + (err.error?.message || err.message || 'Erreur inconnue')
        });
      }
    });
  }

  moveContent(id: number, direction: -1 | 1): void {
    const item = this.contentItems.find(c => c.id === id);
    if (!item) return;
    
    const sameDay = this.contentItems.filter(c => c.day === item.day).sort((a, b) => a.order - b.order);
    const idx = sameDay.findIndex(c => c.id === id);
    const targetIdx = idx + direction;
    
    if (targetIdx < 0 || targetIdx >= sameDay.length) return;
    
    const swapWith = sameDay[targetIdx];
    const temp = item.order;
    item.order = swapWith.order;
    swapWith.order = temp;
    
    // Persist order changes to backend if items have backend IDs
    const backendId1 = item._backendData?.idContenuJour;
    const backendId2 = swapWith._backendData?.idContenuJour;
    
    if (backendId1 && item.order !== undefined) {
      this.contenuJourFormationService.updateContenuOrdre(backendId1, item.order).subscribe({
        error: (err) => console.error('Error updating order for item 1:', err)
      });
    }
    
    if (backendId2 && swapWith.order !== undefined) {
      this.contenuJourFormationService.updateContenuOrdre(backendId2, swapWith.order).subscribe({
        error: (err) => console.error('Error updating order for item 2:', err)
      });
    }
  }

  // Content Detail Library & Assignment
  loadContentDetailLibrary(): void {
    this.isLoadingContent = true;
    
    this.contenuDetailleService.getAllContenuDetaille().subscribe({
      next: (data) => {
        this.contentDetailLibrary = this.mapContentDetailList(data);
        this.isLoadingContent = false;
      },
      error: (err) => {
        console.error('Error loading content detail library:', err);
        this.isLoadingContent = false;
      }
    });
  }

  openContentDetailAssigner(contentItem: any): void {
    if (!this.generalInfoSaved) { this.promptGeneralInfo(); return; }
    
    this.hideAllForms();
    this.selectedContentItem = contentItem;
    this.showContentDetailAssigner = true;
    this.selectedGlobal = null;
    this.selectedSpecific = null;

    // Initialize array if it doesn't exist
    if (!contentItem.assignedContentDetails) {
      contentItem.assignedContentDetails = [];
    }

    // Trigger change detection to ensure view updates
    this.cdr.detectChanges();

    // Always reload from backend to get fresh niveau information
    if (contentItem._backendData?.idContenuJour) {
      this.contenuJourFormationService.getAssignedContenus(contentItem._backendData.idContenuJour).subscribe({
        next: (assignedContenus) => {
          // Unwrap if backend returns nested array (Mediator wrapper)
          let contenusArray = Array.isArray(assignedContenus) && assignedContenus.length === 1 && Array.isArray(assignedContenus[0])
            ? assignedContenus[0]
            : assignedContenus;
          
          // Store full objects with niveau for reference
          contentItem._assignedContenusWithNiveau = contenusArray;
          
          // Extract IDs and filter nulls
          const backendIds = contenusArray
            .map((c: any) => c.idContenuDetaille)
            .filter((id: any) => id != null);
          
          // Set the array with backend data
          contentItem.assignedContentDetails = backendIds;
          contentItem._assignmentsLoaded = true; // Mark as loaded
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('❌ Erreur lors du chargement des contenus assignés:', err);
          // Clean existing array on error
          contentItem.assignedContentDetails = contentItem.assignedContentDetails.filter((id: any) => id != null);
          contentItem._assignmentsLoaded = true; // Don't retry on error
          this.cdr.detectChanges();
        }
      });
    } else {
      // Clean local array
      contentItem.assignedContentDetails = contentItem.assignedContentDetails.filter((id: any) => id != null);
    }
  }

  closeContentDetailAssigner(): void {
    this.showContentDetailAssigner = false;
    this.selectedContentItem = null;
    this.contentSearchQuery = '';
    this.searchResults = null;
    this.isAssigning = false;
    this.cdr.detectChanges();
  }

  onContentSearchChange(query: string): void {
    this.contentSearchQuery = query;
    this.contentSearch$.next(query ?? '');
  }

  getFilteredContentDetails() {
    const source = this.searchResults ?? this.contentDetailLibrary;
    if (!this.contentSearchQuery || this.searchResults) {
      return source;
    }
    const query = this.contentSearchQuery.toLowerCase();
    return source.filter(c => 
      c.title.toLowerCase().includes(query) || 
      c.contenusCles.some((k: string) => k.toLowerCase().includes(query))
    );
  }

  private setupContentSearch(): void {
    this.contentSearchSub = this.contentSearch$
      .pipe(
        map((q) => (q ?? '').trim()),
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((term) => {
          if (!term) {
            this.isSearching = false;
            this.searchResults = null;
            return of([]);
          }

          this.isSearching = true;
          return this.contenuDetailleService.searchContenuDetaille(term).pipe(
            catchError((err) => {
              console.error('Erreur recherche supports pédagogiques:', err);
              this.isSearching = false;
              this.searchResults = null;
              return of([]);
            })
          );
        })
      )
      .subscribe((data) => {
        // If term was cleared during in-flight request
        if (!this.contentSearchQuery?.trim()) {
          this.searchResults = null;
          this.isSearching = false;
          return;
        }

        const payload = Array.isArray(data) && data.length === 1 && Array.isArray(data[0]) ? data[0] : data;
        this.searchResults = this.mapContentDetailList(payload);
        this.isSearching = false;
      });
  }

  ngOnDestroy(): void {
    this.contentSearchSub?.unsubscribe();
  }

  getAssignedNiveau(contentId: number): number {
    if (!this.selectedContentItem?._assignedContenusWithNiveau) {
      return 1;
    }
    
    const found = this.selectedContentItem._assignedContenusWithNiveau.find((c: any) => c.idContenuDetaille === contentId);
    return found?.niveau || 1;
  }

  changeContentNiveau(contentDetail: any, event: any): void {
    const newNiveau = parseInt(event.target.value);
    const niveauLabel = this.getNiveauLabel(newNiveau);
    
    const backendId = this.selectedContentItem._backendData?.idContenuJour || this.selectedContentItem.id;
    
    if (backendId && backendId > 0) {
      this.isAssigning = true;
      
      // Send only this single item with new niveau
      this.contenuJourFormationService.assignContenus(
        backendId,
        [contentDetail.id],
        newNiveau,
        niveauLabel
      ).subscribe({
        next: (response) => {
          this.isAssigning = false;
          
          // Update local cache
          if (this.selectedContentItem._assignedContenusWithNiveau) {
            const found = this.selectedContentItem._assignedContenusWithNiveau.find((c: any) => c.idContenuDetaille === contentDetail.id);
            if (found) {
              found.niveau = newNiveau;
              found.niveauLabel = niveauLabel;
            }
          }
          
          const niveauIcons = ['🌱', '📈', '🚀'];
          const niveauLabels = ['Débutant', 'Intermédiaire', 'Avancé'];
          
          Swal.fire({
            icon: 'success',
            title: 'Niveau modifié',
            html: `<p>"<strong>${contentDetail.title}</strong>"</p><p style="margin-top: 10px; color: #2196F3; font-weight: bold;">${niveauIcons[newNiveau - 1]} ${niveauLabels[newNiveau - 1]}</p>`,
            timer: 1500,
            showConfirmButton: false
          });
        },
        error: (err) => {
          console.error('❌ Erreur lors de la modification du niveau:', err);
          this.isAssigning = false;
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: 'Impossible de modifier le niveau. Veuillez réessayer.'
          });
          // Revert select to previous value
          event.target.value = this.getAssignedNiveau(contentDetail.id);
        }
      });
    }
  }

  assignContentDetailToItem(contentDetail: any): void {
    if (!this.selectedContentItem) {
      console.warn('⚠️ No content item selected');
      return;
    }

    // Prevent duplicate requests
    if (this.isAssigning) {
      console.warn('⚠️ Assignment already in progress');
      return;
    }
    
    if (!this.selectedContentItem.assignedContentDetails) {
      this.selectedContentItem.assignedContentDetails = [];
    }

    // Toggle behavior: if already assigned, remove it
    if (this.selectedContentItem.assignedContentDetails.includes(contentDetail.id)) {
      this.removeContentDetailFromItem(contentDetail.id);
      return;
    }

    // Check if content item has been saved to backend
    const backendId = this.selectedContentItem._backendData?.idContenuJour || this.selectedContentItem.id;
    if (!backendId || backendId <= 0) {
      Swal.fire({
        icon: 'warning',
        title: 'Enregistrement requis',
        text: 'Veuillez d\'abord enregistrer la formation avant d\'assigner des supports pédagogiques.',
        confirmButtonText: 'OK'
      });
      return;
    }

    // Show level selection dialog before assigning
    const availableLevels = this.getFormationLevels(contentDetail);
    
    if (availableLevels.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'Aucun niveau disponible',
        text: 'Ce contenu n\'a pas de niveaux définis.',
        confirmButtonText: 'OK'
      });
      return;
    }
    
    // Build buttons HTML only for available levels
    const levelButtons = availableLevels.map(level => {
      const levelMap: Record<string, {num: number, color: string, icon: string}> = {
        'Débutant': {num: 1, color: '#4CAF50', icon: '🌱'},
        'Intermédiaire': {num: 2, color: '#2196F3', icon: '📈'},
        'Avancé': {num: 3, color: '#FF9800', icon: '🚀'}
      };
      
      const info = levelMap[level];
      if (!info) return '';
      
      return `
        <button id="niveau-${info.num}" class="swal2-confirm swal2-styled niveau-btn" 
                style="background-color: ${info.color}; border: 3px solid transparent;">
          ${info.icon} ${level}
        </button>
      `;
    }).join('');
    
    Swal.fire({
      title: 'Sélectionner le niveau',
      html: `
        <p style="margin-bottom: 16px;">Choisissez le niveau pour "<strong>${contentDetail.title}</strong>" :</p>
        <div style="display: flex; flex-direction: column; gap: 12px;">
          ${levelButtons}
        </div>
        <p id="selected-niveau-indicator" style="margin-top: 16px; font-weight: bold; color: #2196F3; min-height: 20px;"></p>
        <button id="confirm-niveau" class="swal2-confirm swal2-styled" style="margin-top: 12px; display: none; background-color: #4CAF50;">
          ✅ Confirmer l'assignation
        </button>
      `,
      showConfirmButton: false,
      showCancelButton: true,
      cancelButtonText: 'Annuler',
      didOpen: () => {
        const popup = Swal.getPopup();
        if (popup) {
          let selectedLevel: number | null = null;
          const indicator = popup.querySelector('#selected-niveau-indicator');
          const confirmBtn = popup.querySelector('#confirm-niveau') as HTMLButtonElement;
          
          // Only add listeners for available levels
          availableLevels.forEach(level => {
            const levelMap: Record<string, number> = {
              'Débutant': 1,
              'Intermédiaire': 2,
              'Avancé': 3
            };
            const levelNum = levelMap[level];
            
            const btn = popup.querySelector(`#niveau-${levelNum}`) as HTMLButtonElement;
            if (btn) {
              btn.addEventListener('click', () => {
                selectedLevel = levelNum;
                
                // Update visual selection
                popup.querySelectorAll('.niveau-btn').forEach(b => {
                  (b as HTMLElement).style.border = '3px solid transparent';
                  (b as HTMLElement).style.transform = 'scale(1)';
                });
                btn.style.border = '3px solid #fff';
                btn.style.transform = 'scale(1.05)';
                
                // Show selected level
                const labels: Record<number, string> = {1: '🌱 Débutant', 2: '📈 Intermédiaire', 3: '🚀 Avancé'};
                if (indicator) {
                  indicator.textContent = '✓ Sélectionné: ' + labels[levelNum];
                }
                
                // Show confirm button
                if (confirmBtn) {
                  confirmBtn.style.display = 'block';
                }
              });
            }
          });
          
          // Confirm button handler
          if (confirmBtn) {
            confirmBtn.addEventListener('click', () => {
              if (selectedLevel) {
                Swal.close();
                this.proceedWithAssignment(contentDetail, selectedLevel);
              }
            });
          }
        }
      }
    });
  }

  private proceedWithAssignment(contentDetail: any, niveau: number): void {
    if (!this.selectedContentItem) return;

    // Only add this single content with its niveau
    const previousAssignments = [...this.selectedContentItem.assignedContentDetails];
    this.selectedContentItem.assignedContentDetails.push(contentDetail.id);

    // Get the backend ID - ONLY use _backendData, not the temporary local id
    const backendId = this.selectedContentItem._backendData?.idContenuJour;
    
    // Validate that we have a real backend ID
    if (!backendId || backendId <= 0) {
      Swal.fire({
        icon: 'error',
        title: 'Erreur',
        html: `
          <p>Impossible d'assigner le contenu détaillé.</p>
          <p style="color: #666; margin-top: 10px;">
            <small>Le contenu de jour n'a pas encore été enregistré dans la base de données.</small>
          </p>
        `,
        confirmButtonText: 'OK'
      });
      // Rollback the assignment
      this.selectedContentItem.assignedContentDetails = previousAssignments;
      return;
    }
    
    // Persist to backend
    this.isAssigning = true;
    
    const niveauLabel = this.getNiveauLabel(niveau);
    
    // Send only this single content item with its niveau
    this.contenuJourFormationService.assignContenus(
      backendId,
      [contentDetail.id], // Only this single item
      niveau,
      niveauLabel
    ).subscribe({
      next: (response) => {
        this.isAssigning = false;
          
          // Update local cache with niveau
          if (!this.selectedContentItem._assignedContenusWithNiveau) {
            this.selectedContentItem._assignedContenusWithNiveau = [];
          }
          this.selectedContentItem._assignedContenusWithNiveau.push({
            idContenuDetaille: contentDetail.id,
            titre: contentDetail.title,
            niveau: niveau,
            niveauLabel: niveauLabel
          });
          
          const niveauIcons = ['🌱', '📈', '🚀'];
          const niveauLabels = ['Débutant', 'Intermédiaire', 'Avancé'];
          const icon = niveauIcons[niveau - 1];
          const label = niveauLabels[niveau - 1];
          
          Swal.fire({
            icon: 'success',
            title: 'Contenu assigné',
            html: `<p>"<strong>${contentDetail.title}</strong>" a été assigné avec succès</p><p style="margin-top: 10px; color: #2196F3; font-weight: bold;">${icon} Niveau: ${label}</p>`,
            timer: 2000,
            showConfirmButton: false
          });
        },
        error: (err) => {
          this.isAssigning = false;
          // Rollback local change
          this.selectedContentItem.assignedContentDetails = previousAssignments;
          
          // Provide detailed error message
          let errorMessage = 'Impossible d\'assigner le contenu.';
          let errorDetail = '';
          
          if (err.status === 404) {
            errorMessage = 'Contenu de jour introuvable';
            errorDetail = 'Le contenu de jour n\'existe pas dans la base de données. Veuillez rafraîchir la page et réessayer.';
          } else if (err.error?.message) {
            errorDetail = err.error.message;
          } else if (err.message) {
            errorDetail = err.message;
          }
          
          Swal.fire({
            icon: 'error',
            title: errorMessage,
            html: errorDetail ? `<p>${errorDetail}</p>` : undefined,
            confirmButtonText: 'OK'
          });
        }
      });
  }

  removeContentDetailFromItem(contentDetailId: number): void {
    if (!this.selectedContentItem || !this.selectedContentItem.assignedContentDetails) {
      console.warn('⚠️ No selected content item or no assignments');
      return;
    }

    // Prevent duplicate requests
    if (this.isAssigning) {
      console.warn('⚠️ Assignment already in progress');
      return;
    }

    const previousAssignments = [...this.selectedContentItem.assignedContentDetails];
    this.selectedContentItem.assignedContentDetails = this.selectedContentItem.assignedContentDetails.filter(
      (id: number) => id !== contentDetailId
    );

    // Get the backend ID - try multiple sources for fallback
    const backendId = this.selectedContentItem._backendData?.idContenuJour || this.selectedContentItem.id;
    
    // Persist to backend if the content item has a backend ID
    if (backendId && backendId > 0) {
      this.isAssigning = true;
      
      // Filter out null/undefined values before sending
      const validIds = this.selectedContentItem.assignedContentDetails.filter((id: any) => id != null);
      
      this.contenuJourFormationService.assignContenus(
        backendId,
        validIds
      ).subscribe({
        next: (response) => {
          this.isAssigning = false;
          
          // Update local cache
          if (this.selectedContentItem._assignedContenusWithNiveau) {
            this.selectedContentItem._assignedContenusWithNiveau = 
              this.selectedContentItem._assignedContenusWithNiveau.filter((c: any) => c.idContenuDetaille !== contentDetailId);
          }
          
          Swal.fire({
            icon: 'success',
            title: 'Contenu retiré',
            text: 'Le contenu a été retiré et sauvegardé',
            timer: 1500,
            showConfirmButton: false
          });
        },
        error: (err) => {
          console.error('❌ Erreur lors du retrait:', err);
          this.isAssigning = false;
          // Rollback local change
          this.selectedContentItem.assignedContentDetails = previousAssignments;
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: 'Impossible de retirer le contenu. Veuillez réessayer.',
            footer: err.error?.message || err.message || 'Erreur inconnue'
          });
        }
      });
    } else {
      console.warn('⚠️ Content item has no backend ID yet - removal will be persisted when formation is saved');
      Swal.fire({
        icon: 'success',
        title: 'Contenu retiré localement',
        timer: 1500,
        showConfirmButton: false
      });
    }
  }

  getAssignedContentDetails(contentItem: any): any[] {
    if (!contentItem.assignedContentDetails || !contentItem.assignedContentDetails.length) {
      return [];
    }
    return this.contentDetailLibrary.filter(cd => contentItem.assignedContentDetails.includes(cd.id));
  }

  isContentDetailAssigned(contentDetail: any): boolean {
    if (!this.selectedContentItem || !this.selectedContentItem.assignedContentDetails) return false;
    return this.selectedContentItem.assignedContentDetails.includes(contentDetail.id);
  }

  // Get all unique formation levels from content detail levels
  getFormationLevels(contentDetail: any): string[] {
    if (!contentDetail.levels || !Array.isArray(contentDetail.levels)) return [];
    
    const allLevels = new Set<string>();
    
    contentDetail.levels.forEach((level: any) => {
      // First check if formationLevels array has explicit values
      const levelsList = level.formationLevels || level.selectedLevels || level.niveaux || level.levels || [];
      
      if (Array.isArray(levelsList) && levelsList.length > 0) {
        levelsList.forEach((l: string) => {
          if (l && typeof l === 'string') {
            allLevels.add(l);
          }
        });
      } else {
        // If formationLevels is empty, map levelNumber to formation level names
        // Only if the level has actual content (theorieContent or pratiqueContent or files)
        const hasContent = level.theorieContent || level.pratiqueContent || 
                          (level.files && level.files.length > 0);
        
        if (hasContent && level.levelNumber) {
          const levelMap: Record<number, string> = {
            1: 'Débutant',
            2: 'Intermédiaire',
            3: 'Avancé'
          };
          
          if (levelMap[level.levelNumber]) {
            allLevels.add(levelMap[level.levelNumber]);
          }
        }
      }
    });
    
    return Array.from(allLevels);
  }

  // Format level for display with icon
  formatLevelWithIcon(level: string): string {
    const levelMap: Record<string, string> = {
      'Débutant': '🌱 Débutant',
      'Debutant': '🌱 Débutant',
      'Intermédiaire': '📈 Intermédiaire',
      'Intermediaire': '📈 Intermédiaire',
      'Avancé': '🚀 Avancé',
      'Avance': '🚀 Avancé'
    };
    return levelMap[level] || level;
  }

  // ---- Content Detail Creator Methods ----
  openContentDetailCreator(): void {
    this.hideAllForms();
    this.showContentDetailCreator = true;
    this.selectedGlobal = null;
    this.selectedSpecific = null;
  }

  closeContentDetailCreator(): void {
    this.showContentDetailCreator = false;
    this.cdr.detectChanges();
  }

    onContentDetailSaved(): void {
      // Reload the content library after a new content is saved
      this.loadContentDetailLibrary();
      this.closeContentDetailCreator();
      Swal.fire({
        icon: 'success',
        title: 'Support pédagogique créé',
        text: 'Le support a été ajouté à la bibliothèque',
        timer: 2000,
        showConfirmButton: false
      });
    }

  getFileIcon(fileType: string): string {
    if (fileType.startsWith('video/')) return '🎥';
    if (fileType.startsWith('image/')) return '🖼️';
    if (fileType.includes('pdf')) return '📄';
    if (fileType.includes('word') || fileType.includes('document')) return '📝';
    if (fileType.includes('excel') || fileType.includes('spreadsheet')) return '📊';
    if (fileType.includes('powerpoint') || fileType.includes('presentation')) return '📽️';
    return '📎';
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }

  save(): void {
    if (!this.form.theme || !this.form.typeFormation || !this.form.niveau) {
      Swal.fire({ icon: 'warning', title: 'Champs requis', text: 'Veuillez remplir le thème, type et niveau.' });
      return;
    }

    // Ensure current selections (objectifs + contenus) are reflected on the form before saving
    this.syncSelectionsToForm();

    // Set status as draft (brouillon)
    this.form.statut = FormationStatut.Brouillon;

    this.saving = true;
    this.startLoading('Enregistrement en cours...');

    // Check if we're updating or creating
    if (this.form.idFormation) {
      // UPDATE existing formation
      this.formationService.updateFormation(this.form.idFormation, this.form).subscribe({
        next: (response: any) => {
          // Upload image if selected
          if (this.selectedImageFile && this.form.idFormation) {
            this.uploadFormationImage(this.form.idFormation, () => {
              this.generalInfoSaved = true;
              this.saving = false;
              this.stopLoading();
              Swal.fire({ 
                icon: 'success', 
                title: 'Formation mise à jour', 
                text: 'Les modifications ont été enregistrées avec succès.',
                timer: 3000, 
                showConfirmButton: false 
              });
              this.showGeneralInfo = false;
            });
          } else {
            this.generalInfoSaved = true;
            this.saving = false;
            this.stopLoading();
            Swal.fire({ 
              icon: 'success', 
              title: 'Formation mise à jour', 
              text: 'Les modifications ont été enregistrées avec succès.',
              timer: 3000, 
              showConfirmButton: false 
            });
            this.showGeneralInfo = false;
          }
        },
        error: (err) => {
          console.error('❌ Erreur mise à jour formation:', err);
          this.saving = false;
          this.stopLoading();
          Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de mettre à jour la formation.' });
        }
      });
    } else {
      // CREATE new formation
      this.formationService.createFormation(this.form).subscribe({
        next: (response: any) => {
          // Store the formation ID for future updates
          this.form.idFormation = response.idFormation || response[0]?.idFormation;
          
          // Upload image if selected
          if (this.selectedImageFile && this.form.idFormation) {
            this.uploadFormationImage(this.form.idFormation, () => {
              this.generalInfoSaved = true;
              this.saving = false;
              this.stopLoading();
              Swal.fire({ 
                icon: 'success', 
                title: 'Brouillon créé', 
                text: 'Les informations générales ont été enregistrées. Continuez à ajouter les objectifs et contenus.',
                timer: 3000, 
                showConfirmButton: false 
              });
              this.showGeneralInfo = false;
            });
          } else {
            this.generalInfoSaved = true;
            this.saving = false;
            this.stopLoading();
            Swal.fire({ 
              icon: 'success', 
              title: 'Brouillon créé', 
              text: 'Les informations générales ont été enregistrées. Continuez à ajouter les objectifs et contenus.',
              timer: 3000, 
              showConfirmButton: false 
            });
            this.showGeneralInfo = false;
          }
        },
        error: (err) => {
          console.error('❌ Erreur création formation:', err);
          this.saving = false;
          this.stopLoading();
          Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de créer la formation.' });
        }
      });
    }
  }

  /**
   * Upload formation image
   */
  private uploadFormationImage(formationId: number, onSuccess: () => void): void {
    if (!this.selectedImageFile) {
      onSuccess();
      return;
    }

    this.formationService.uploadFormationImage(formationId, this.selectedImageFile).subscribe({
      next: (response: any) => {
        // Update form with new image URL
        this.form.imageUrl = response.imageUrl;
        this.selectedImageFile = null;
        onSuccess();
      },
      error: (err) => {
        console.error('❌ Erreur upload image:', err);
        // Still consider the save successful, but warn about image
        Swal.fire({
          icon: 'warning',
          title: 'Formation enregistrée',
          text: 'La formation a été enregistrée mais l\'image n\'a pas pu être téléversée.',
          timer: 3000,
          showConfirmButton: false
        });
        this.saving = false;
        this.stopLoading();
        this.showGeneralInfo = false;
      }
    });
  }

  /**
   * Save the complete formation with all objectives and content
   * This is the main save button that validates and saves everything
   */
  saveCompleteFormation(): void {
    if (!this.form.idFormation) {
      Swal.fire({
        icon: 'warning',
        title: 'Informations générales manquantes',
        text: 'Veuillez d\'abord enregistrer les informations générales.'
      });
      return;
    }

    if (!this.form.theme || !this.form.typeFormation || !this.form.niveau) {
      Swal.fire({
        icon: 'warning',
        title: 'Champs requis',
        text: 'Veuillez remplir le thème, type et niveau.'
      });
      return;
    }

    this.saving = true;

    // Make sure the latest checkbox selections are applied to the form before persisting
    this.syncSelectionsToForm();

    // Prepare the complete formation data - just update with current form data
    // The objectives and content have already been saved individually
    const completeFormation: Formation = {
      ...this.form,
      statut: FormationStatut.Brouillon
    };

    // Update the formation with all data
    this.formationService.updateFormation(this.form.idFormation, completeFormation).subscribe({
      next: (response: any) => {
        this.saving = false;
        Swal.fire({
          icon: 'success',
          title: 'Formation enregistrée',
          text: 'La formation a été enregistrée avec succès.',
          timer: 3000,
          showConfirmButton: false
        }).then(() => {
          // Navigate back to menu/dashboard
          this.router.navigate(['/menu']);
        });
      },
      error: (err) => {
        this.saving = false;
        console.error('❌ Erreur lors de l\'enregistrement:', err);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible d\'enregistrer la formation. Vérifiez votre connexion et réessayez.'
        });
      }
    });
  }

  // ====== Cascading selects (Domaine -> Type -> Catégorie -> Sous-catégorie) ======
  onDomaineChange(): void {
    const domId = this.form.domaine?.idDomaine;
    this.form.idDomaine = domId;
    // reset downstream selections
    this.form.type = undefined as any;
    this.form.categorie = undefined as any;
    this.form.sousCategorie = undefined as any;
    this.form.idType = undefined;
    this.form.idCategorie = undefined;
    this.form.idSousCategorie = undefined;
    this.typesDyn = [];
    this.categories = [];
    this.sousCategories = [];
    if (domId) {
      this.formationService.getTypesByDomaine(domId).subscribe({
        next: (ts) => { this.typesDyn = ts; },
        error: (e) => console.error('Erreur chargement types', e)
      });
    }
  }

  onTypeChange(): void {
    const typeId = this.form.type?.idType;
    this.form.idType = typeId;
    // reset downstream
    this.form.categorie = undefined as any;
    this.form.sousCategorie = undefined as any;
    this.form.idCategorie = undefined;
    this.form.idSousCategorie = undefined;
    this.categories = [];
    this.sousCategories = [];
    if (typeId) {
      this.formationService.getCategoriesByType(typeId).subscribe({
        next: (cs) => { this.categories = cs; },
        error: (e) => console.error('Erreur chargement catégories', e)
      });
    }
  }

  onCategorieChange(): void {
    const catId = this.form.categorie?.idCategorie;
    this.form.idCategorie = catId;
    // reset downstream
    this.form.sousCategorie = undefined as any;
    this.form.idSousCategorie = undefined;
    this.sousCategories = [];
    if (catId) {
      this.formationService.getSousCategoriesByCategorie(catId).subscribe({
        next: (scs) => { this.sousCategories = scs; },
        error: (e) => console.error('Erreur chargement sous-catégories', e)
      });
    }
  }

  onSousCategorieChange(): void {
    const scId = this.form.sousCategorie?.idSousCategorie;
    this.form.idSousCategorie = scId;
  }

  addGlobalObjective(label: string) {
    const trimmed = (label || '').trim();
    if (trimmed) {
      this.objectifsGlobauxExemples.push({ id: ++this.gid, label: trimmed, selected: false });
    }
  }

  addSpecificObjective(label: string) {
    const trimmed = (label || '').trim();
    if (trimmed) {
      this.objectifsSpecifiquesExemples.push({ id: ++this.sid, label: trimmed, selected: false, globalId: undefined });
    }
  }

  saveGlobalObjective(): void {
    const trimmed = (this.newGlobalObjectiveLabel || '').trim();
    
    // Validation
    this.objectiveValidationErrors = {};
    if (!trimmed) {
      this.objectiveValidationErrors['global'] = 'Le nom de l\'objectif global est requis.';
      Swal.fire({
        icon: 'warning',
        title: 'Champ requis',
        text: 'Veuillez saisir le nom de l\'objectif global.'
      });
      return;
    }

    if (trimmed.length < 5) {
      this.objectiveValidationErrors['global'] = 'Le nom doit contenir au moins 5 caractères.';
      Swal.fire({
        icon: 'warning',
        title: 'Nom trop court',
        text: 'Le nom de l\'objectif global doit contenir au moins 5 caractères.'
      });
      return;
    }

    if (trimmed.length > 500) {
      this.objectiveValidationErrors['global'] = 'Le nom ne peut pas dépasser 500 caractères.';
      Swal.fire({
        icon: 'warning',
        title: 'Nom trop long',
        text: 'Le nom de l\'objectif global ne peut pas dépasser 500 caractères.'
      });
      return;
    }

    if (this.isEditingGlobal && this.editingGlobalId) {
      // Update existing objective
      this.isLoadingObjectives = true;
      this.startLoading('Mise à jour de l\'objectif...');
      
      this.formationService.updateObjectifGlobal(this.editingGlobalId, { libelle: trimmed }).subscribe({
        next: (updated: any) => {
          const index = this.objectifsGlobauxExemples.findIndex(o => o.id === this.editingGlobalId);
          if (index !== -1) {
            this.objectifsGlobauxExemples[index].label = updated?.libelle ?? trimmed;
          }
          this.isLoadingObjectives = false;
          this.stopLoading();
          this.resetGlobalForm();
        },
        error: (err: any) => {
          console.error('Erreur mise à jour objectif global', err);
          const index = this.objectifsGlobauxExemples.findIndex(o => o.id === this.editingGlobalId);
          if (index !== -1) {
            this.objectifsGlobauxExemples[index].label = trimmed;
          }
          this.isLoadingObjectives = false;
          this.stopLoading();
          this.resetGlobalForm();
        }
      });
    } else {
      // Create new objective - formationId is REQUIRED
      if (!this.form.idFormation) {
        Swal.fire({
          icon: 'warning',
          title: 'Formation non sauvegardée',
          text: 'Veuillez d\'abord enregistrer la formation avant d\'ajouter des objectifs.'
        });
        return;
      }

      this.isLoadingObjectives = true;
      this.startLoading('Création de l\'objectif...');

      this.formationService.createObjectifGlobal({ 
        formationId: this.form.idFormation,
        libelle: trimmed 
      }).subscribe({
        next: (created: any) => {
          const objectifId = created?.idObjectifGlobal ?? ++this.gid;
          
          this.objectifsGlobauxExemples.push({ 
            id: objectifId, 
            label: created?.libelle ?? trimmed, 
            selected: false,
            _backendData: created 
          });
          if (objectifId > this.gid) { this.gid = objectifId; }
          this.isLoadingObjectives = false;
          this.stopLoading();
          this.resetGlobalForm();
        },
        error: (err: any) => {
          console.error('Erreur création objectif global', err);
          this.isLoadingObjectives = false;
          this.stopLoading();
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: 'Erreur lors de la création de l\'objectif: ' + (err.error?.message || 'Erreur inconnue')
          });
        }
      });
    }
  }

  resetGlobalForm(): void {
    this.newGlobalObjectiveLabel = '';
    this.isEditingGlobal = false;
    this.editingGlobalId = null;
    this.showAddGlobalForm = false;
  }

  saveSpecificObjective(): void {
    const titre = this.newSpecificObjectiveLabel.trim();
    
    // Validation
    this.objectiveValidationErrors = {};
    if (!titre) {
      this.objectiveValidationErrors['specific'] = 'Le titre de l\'objectif spécifique est requis.';
      Swal.fire({
        icon: 'warning',
        title: 'Champ requis',
        text: 'Veuillez saisir le titre de l\'objectif spécifique.'
      });
      return;
    }

    if (titre.length < 5) {
      this.objectiveValidationErrors['specific'] = 'Le titre doit contenir au moins 5 caractères.';
      Swal.fire({
        icon: 'warning',
        title: 'Titre trop court',
        text: 'Le titre de l\'objectif spécifique doit contenir au moins 5 caractères.'
      });
      return;
    }

    if (titre.length > 500) {
      this.objectiveValidationErrors['specific'] = 'Le titre ne peut pas dépasser 500 caractères.';
      Swal.fire({
        icon: 'warning',
        title: 'Titre trop long',
        text: 'Le titre de l\'objectif spécifique ne peut pas dépasser 500 caractères.'
      });
      return;
    }

    const payload = {
      titre: titre,
      description: '',
      idContenuGlobal: null  // TODO: This should link to objectifGlobal, not contenuGlobal
    };

    if (this.isEditingSpecific && this.editingSpecificId !== null) {
      // UPDATE mode
      this.isLoadingObjectives = true;
      this.startLoading('Mise à jour de l\'objectif...');
      
      this.formationService.updateObjectifSpecifique(this.editingSpecificId, payload).subscribe({
        next: (updated: any) => {
          const idx = this.objectifsSpecifiquesExemples.findIndex(o => o.id === this.editingSpecificId);
          if (idx !== -1) {
            this.objectifsSpecifiquesExemples[idx] = {
              id: updated.idObjectifSpecifique ?? updated.idObjectifSpec ?? this.editingSpecificId,
              label: updated.titre || titre,
              selected: this.objectifsSpecifiquesExemples[idx].selected,
              globalId: updated.ObjectifGlobalId ?? updated.objectifGlobalId ?? updated.idObjectifGlobal ?? this.newSpecificGlobalId,
              _backendData: updated
            };
          }
          this.isLoadingObjectives = false;
          this.stopLoading();
          this.resetSpecificForm();
        },
        error: (err: any) => {
          console.error('Erreur lors de la modification de l\'objectif spécifique', err);
          this.isLoadingObjectives = false;
          this.stopLoading();
          Swal.fire({ icon: 'error', title: 'Erreur', text: 'Erreur lors de la modification.' });
        }
      });
    } else {
      // CREATE mode - formationId is REQUIRED
      if (!this.form.idFormation) {
        alert('Veuillez d\'abord enregistrer la formation avant d\'ajouter des objectifs.');
        return;
      }

      const createPayload = {
        formationId: this.form.idFormation,
        titre: titre,
        description: '',
        idObjectifGlobal: this.newSpecificGlobalId || null
      };

      this.isLoadingObjectives = true;
      this.startLoading('Création de l\'objectif...');

      this.formationService.createObjectifSpecifique(createPayload).subscribe({
        next: (created: any) => {
          const objectifSpecifiqueId = created.idObjectifSpecifique ?? created.idObjectifSpec ?? ++this.sid;
          
          this.objectifsSpecifiquesExemples.push({
            id: objectifSpecifiqueId,
            label: created.libelle || created.titre || titre,
            selected: false,
            globalId: created.ObjectifGlobalId ?? created.objectifGlobalId ?? created.idObjectifGlobal ?? this.newSpecificGlobalId,
            _backendData: created
          });
          this.isLoadingObjectives = false;
          this.stopLoading();
          this.resetSpecificForm();
        },
        error: (err: any) => {
          console.error('Erreur lors de la création de l\'objectif spécifique', err);
          this.isLoadingObjectives = false;
          this.stopLoading();
          Swal.fire({ icon: 'error', title: 'Erreur', text: 'Erreur: ' + (err.error?.message || 'Erreur inconnue') });
        }
      });
    }
  }

  resetSpecificForm(): void {
    this.isEditingSpecific = false;
    this.editingSpecificId = null;
    this.newSpecificObjectiveLabel = '';
    this.newSpecificGlobalId = undefined;
    this.showAddSpecificForm = false;
  }

  cancelAddGlobalObjective(): void {
    this.resetGlobalForm();
  }

  cancelAddSpecificObjective(): void {
    this.resetSpecificForm();
  }

  removeGlobalObjective(id: number) {
    // Call backend to delete
    this.isLoadingObjectives = true;
    this.startLoading('Suppression de l\'objectif...');
    
    this.formationService.deleteObjectifGlobal(id).subscribe({
      next: () => {
        this.objectifsGlobauxExemples = this.objectifsGlobauxExemples.filter(o => o.id !== id);
        if (this.selectedGlobal?.id === id) {
          this.selectedGlobal = null;
        }
        this.isLoadingObjectives = false;
        this.stopLoading();
      },
      error: (err) => {
        console.error('Erreur suppression objectif global', err);
        // Still remove from UI even if backend fails
        this.objectifsGlobauxExemples = this.objectifsGlobauxExemples.filter(o => o.id !== id);
        if (this.selectedGlobal?.id === id) {
          this.selectedGlobal = null;
        }
        this.isLoadingObjectives = false;
        this.stopLoading();
      }
    });
  }

  removeSpecificObjective(id: number) {
    // Call backend to delete
    this.isLoadingObjectives = true;
    this.startLoading('Suppression de l\'objectif...');
    
    this.formationService.deleteObjectifSpecifique(id).subscribe({
      next: () => {
        this.objectifsSpecifiquesExemples = this.objectifsSpecifiquesExemples.filter(o => o.id !== id);
        if (this.selectedSpecific?.id === id) {
          this.selectedSpecific = null;
        }
        this.isLoadingObjectives = false;
        this.stopLoading();
      },
      error: (err: any) => {
        console.error('Erreur lors de la suppression de l\'objectif spécifique', err);
        this.isLoadingObjectives = false;
        this.stopLoading();
        Swal.fire({ icon: 'error', title: 'Erreur', text: 'Erreur lors de la suppression.' });
      }
    });
  }

  updateSelectedSpecificObjective(): void {
    if (!this.selectedSpecific) return;

    const titre = this.selectedSpecific.label.trim();
    if (!titre) {
      alert('⚠️ Veuillez saisir le titre de l\'objectif spécifique.');
      return;
    }

    const payload = {
      titre: titre,
      description: '',
      idObjectifGlobal: this.selectedSpecific.globalId || null
    };

    this.isLoadingObjectives = true;
    this.startLoading('Mise à jour de l\'objectif...');

    this.formationService.updateObjectifSpecifique(this.selectedSpecific.id, payload).subscribe({
      next: (updated: any) => {
        const idx = this.objectifsSpecifiquesExemples.findIndex(o => o.id === this.selectedSpecific!.id);
        if (idx !== -1) {
          this.objectifsSpecifiquesExemples[idx] = {
            id: updated.idObjectifSpecifique ?? updated.idObjectifSpec ?? this.selectedSpecific!.id,
            label: updated.titre || titre,
            selected: this.objectifsSpecifiquesExemples[idx].selected,
            globalId: updated.ObjectifGlobalId ?? updated.objectifGlobalId ?? updated.idObjectifGlobal ?? this.selectedSpecific!.globalId,
            _backendData: updated
          };
          // Update selectedSpecific to reflect new data
          this.selectedSpecific = this.objectifsSpecifiquesExemples[idx];
        }
        this.isLoadingObjectives = false;
        this.stopLoading();
        Swal.fire({ icon: 'success', title: 'Succès', text: 'Objectif spécifique modifié avec succès.' });
      },
      error: (err: any) => {
        console.error('Erreur lors de la modification de l\'objectif spécifique', err);
        this.isLoadingObjectives = false;
        this.stopLoading();
        Swal.fire({ icon: 'error', title: 'Erreur', text: 'Erreur lors de la modification.' });
      }
    });
  }

  applySelectedGlobauxToForm() {
    const selectedGlobals = this.objectifsGlobauxExemples.filter(g => g.selected || this.objectifsSpecifiquesExemples.some(s => s.globalId === g.id));

    const lines: string[] = [];
    if (selectedGlobals.length) {
      lines.push('Objectifs globaux:');
      selectedGlobals.forEach(g => {
        lines.push(`• ${g.label}`);
        const linkedSpecs = this.objectifsSpecifiquesExemples.filter(s => s.globalId === g.id);
        linkedSpecs.forEach(s => lines.push(`   - ${s.label}`));
      });
    }

    const orphanSpecs = this.objectifsSpecifiquesExemples.filter(s => s.globalId == null && s.selected);
    if (orphanSpecs.length) {
      lines.push('');
      lines.push('Objectifs spécifiques non liés:');
      orphanSpecs.forEach(s => lines.push(`• ${s.label}`));
    }

    const aggregated = lines.join('\n');
    this.form.objectifGlobal = aggregated || this.form.objectifGlobal || '';
  }

  // Link/unlink specifics to the currently selected global
  onToggleSpecificLink(spec: any, linked: boolean): void {
    if (!this.selectedGlobal) return;
    // Assign to this global if checked; otherwise unlink if it was linked here
    if (linked) {
      spec.globalId = this.selectedGlobal.id;
    } else if (spec.globalId === this.selectedGlobal.id) {
      spec.globalId = undefined;
    }
  }

  // Generate Training Plan from current form data using PDF Generator
  generateTrainingPlan(): void {
    if (!this.generalInfoSaved) { this.promptGeneralInfo(); return; }
    if (!this.form.theme) {
      Swal.fire({
        icon: 'warning',
        title: 'Informations manquantes',
        text: 'Veuillez renseigner au moins le thème de la formation.'
      });
      return;
    }

    // Build programmesDetailes from objectives and content items
    const programmesDetailes: ProgrammeDetaile[] = [];
    
    // Get selected global objectives with their linked specifics
    const selectedGlobals = this.objectifsGlobauxExemples.filter(g => 
      g.selected || this.objectifsSpecifiquesExemples.some(s => s.globalId === g.id)
    );

    selectedGlobals.forEach(global => {
      const linkedSpecs = this.objectifsSpecifiquesExemples.filter(s => s.globalId === global.id);
      
      linkedSpecs.forEach(spec => {
        // Get content items for this specific objective
        const relatedContents = this.contentItems.filter(c => c.specificId === spec.id);
        
        if (relatedContents.length > 0) {
          // Group content by day
          const dayMap = new Map<number, typeof relatedContents>();
          relatedContents.forEach(content => {
            const dayNumber = Number(content._backendData?.numeroJour ?? content.day ?? 1);
            if (!dayMap.has(dayNumber)) {
              dayMap.set(dayNumber, []);
            }
            dayMap.get(dayNumber)!.push(content);
          });

          // Create JourFormation array
          const jours: JourFormation[] = [];
          dayMap.forEach((contents, dayNumber) => {
            const contenuDetailles: ContenuDetaille[] = contents.map(content => ({
              contenusCles: [
                content.title,
                ...(content.description ? [content.description] : []),
                ...(content.detail ? [content.detail] : [])
              ],
              methodesPedagogiques: `
                ${content.staff ? `Formateur: ${content.staff}` : ''}
                ${content.level ? `Niveau: ${content.level}` : ''}
              `.trim(),
              dureeTheorique: content.hoursTheoretical || 0,
              dureePratique: content.hoursPractical || 0
            }));

            jours.push({
              numeroJour: dayNumber,
              contenus: contenuDetailles
            });
          });

          // Sort days by number
          jours.sort((a, b) => a.numeroJour - b.numeroJour);

          programmesDetailes.push({
            titre: spec.label,
            jours: jours
          });
        }
      });
    });

    // Handle orphan specific objectives
    const orphanSpecs = this.objectifsSpecifiquesExemples.filter(s => s.globalId == null && s.selected);
    orphanSpecs.forEach(spec => {
      const relatedContents = this.contentItems.filter(c => c.specificId === spec.id);
      
      if (relatedContents.length > 0) {
        const dayMap = new Map<number, typeof relatedContents>();
        relatedContents.forEach(content => {
          const dayNumber = Number(content._backendData?.numeroJour ?? content.day ?? 1);
          if (!dayMap.has(dayNumber)) {
            dayMap.set(dayNumber, []);
          }
          dayMap.get(dayNumber)!.push(content);
        });

        const jours: JourFormation[] = [];
        dayMap.forEach((contents, dayNumber) => {
          const contenuDetailles: ContenuDetaille[] = contents.map(content => ({
            contenusCles: [
              content.title,
              ...(content.description ? [content.description] : []),
              ...(content.detail ? [content.detail] : [])
            ],
            methodesPedagogiques: `
              ${content.staff ? `Formateur: ${content.staff}` : ''}
              ${content.level ? `Niveau: ${content.level}` : ''}
            `.trim(),
            dureeTheorique: content.hoursTheoretical || 0,
            dureePratique: content.hoursPractical || 0
          }));

          jours.push({
            numeroJour: dayNumber,
            contenus: contenuDetailles
          });
        });

        jours.sort((a, b) => a.numeroJour - b.numeroJour);

        programmesDetailes.push({
          titre: spec.label,
          jours: jours
        });
      }
    });

    // If no structured content, create a simple programme from all content items
    if (programmesDetailes.length === 0 && this.contentItems.length > 0) {
      const dayMap = new Map<number, typeof this.contentItems>();
      this.contentItems.forEach(content => {
        const dayNumber = Number(content._backendData?.numeroJour ?? content.day ?? 1);
        if (!dayMap.has(dayNumber)) {
          dayMap.set(dayNumber, []);
        }
        dayMap.get(dayNumber)!.push(content);
      });

      const jours: JourFormation[] = [];
      dayMap.forEach((contents, dayNumber) => {
        const contenuDetailles: ContenuDetaille[] = contents.map(content => ({
          contenusCles: [
            content.title,
            ...(content.description ? [content.description] : [])
          ],
          methodesPedagogiques: content.detail || '',
          dureeTheorique: content.hoursTheoretical || 0,
          dureePratique: content.hoursPractical || 0
        }));

        jours.push({
          numeroJour: dayNumber,
          contenus: contenuDetailles
        });
      });

      jours.sort((a, b) => a.numeroJour - b.numeroJour);

      programmesDetailes.push({
        titre: 'Programme de formation',
        jours: jours
      });
    }

    // Apply selected objectives to form
    this.applySelectedGlobauxToForm();

    // Create mock PlanFormation for PDF generation
    const mockPlan: PlanFormation = {
      titre: `Plan de formation - ${this.form.theme}`,
      description: this.form.descriptionTheme || '',
      dateDebut: new Date().toISOString().split('T')[0],
      idPlanFormation: Math.floor(Math.random() * 10000)
    };

    // Prefer backend fetch to get full contenu detail files; if it fails, fall back to local extraction
    if (this.form.idFormation) {
      console.log('🔄 Fetching contenus detailles for formation:', this.form.idFormation);
      
      // Fetch both regular contenus and contenus with jour mapping (with error handling for each)
      forkJoin({
        contenusDetailles: this.contenuDetailleService.getContenuDetailleByFormationId(this.form.idFormation).pipe(
          catchError(err => {
            console.warn('⚠️ Error fetching contenusDetailles:', err);
            return of([]);
          })
        ),
        contenusWithJours: this.contenuDetailleService.getContenuDetailleByFormationWithJours(this.form.idFormation).pipe(
          catchError(err => {
            console.warn('⚠️ Error fetching contenusWithJours:', err);
            return of([]);
          })
        )
      }).subscribe({
        next: ({ contenusDetailles, contenusWithJours }) => {
          console.log('✅ Fetched contenusDetailles:', contenusDetailles);
          console.log('✅ Fetched contenusWithJours:', contenusWithJours);
          console.log('✅ contenusDetailles length:', contenusDetailles?.length);
          console.log('✅ contenusWithJours length:', contenusWithJours?.length);
          
          // Log the structure of first contenuDetaille to see if it has levels
          if (contenusDetailles && contenusDetailles.length > 0) {
            console.log('🔍 First contenuDetaille structure:', JSON.stringify(contenusDetailles[0], null, 2));
          }
          
          // If contenusWithJours is empty, build it from contenusDetailles + programmesDetailes mapping
          let effectiveContenusWithJours = contenusWithJours || [];
          console.log('🔍 effectiveContenusWithJours initial length:', effectiveContenusWithJours.length);
          
          if (effectiveContenusWithJours.length === 0 && contenusDetailles && contenusDetailles.length > 0) {
            console.log('🔄 Building contenusWithJours from contenusDetailles...');
            effectiveContenusWithJours = this.buildContenusWithJoursFromProgrammes(contenusDetailles, programmesDetailes);
            console.log('✅ Built contenusWithJours:', effectiveContenusWithJours);
          } else {
            console.log('⏭️ Skipping build - effectiveContenusWithJours.length:', effectiveContenusWithJours.length, 'contenusDetailles?.length:', contenusDetailles?.length);
          }
          
          const contenusFormation: ContenuFormation[] = (contenusDetailles || []).map((cd: any) => ({
            id: cd.idContenuDetaille,
            title: cd.titre || cd.title || 'Contenu',
            description: cd.methodesPedagogiques || cd.description || '',
            type: this.mapContentType(cd),
            size: this.calculateContentSize(cd),
            uploadDate: cd.dateCreation || new Date().toISOString(),
            fileName: this.extractFileName(cd),
            fileUrl: cd.fileUrl || cd.filePath || cd.cheminFichier || '',
            videoUrl: cd.videoUrl || ''
          }));

          const formationForPdf: Formation = {
            ...this.form,
            nombreHeures: this.totalHoursOverall(),
            programmesDetailes: programmesDetailes,
            contenusFormation: contenusFormation,
            contenusWithJours: effectiveContenusWithJours
          };

          this.generatePdfWithData(formationForPdf, mockPlan);
        },
        error: (err) => {
          console.error('❌ Error fetching contenus detailles:', err);
          const contenusFormation = this.extractContenusFormationFromProgrammes(programmesDetailes);
          const formationForPdf: Formation = {
            ...this.form,
            nombreHeures: this.totalHoursOverall(),
            programmesDetailes: programmesDetailes,
            contenusFormation: contenusFormation
          };
          this.generatePdfWithData(formationForPdf, mockPlan);
        }
      });
    } else {
      const contenusFormation = this.extractContenusFormationFromProgrammes(programmesDetailes);
      const formationForPdf: Formation = {
        ...this.form,
        nombreHeures: this.totalHoursOverall(),
        programmesDetailes: programmesDetailes,
        contenusFormation: contenusFormation
      };
      this.generatePdfWithData(formationForPdf, mockPlan);
    }
  }

  private generatePdfWithData(formationForPdf: Formation, mockPlan: any): void {
    // Ask user which type of PDF they want
    Swal.fire({
      title: 'Type de PDF',
      html: 'Quel type de fiche programme souhaitez-vous générer ?',
      icon: 'question',
      showCancelButton: true,
      showDenyButton: true,
      confirmButtonText: '📎 Avec liens vers contenus',
      denyButtonText: '📄 Simple (sans liens)',
      cancelButtonText: 'Annuler',
      confirmButtonColor: '#10b981',
      denyButtonColor: '#3b82f6'
    }).then((result) => {
      if (result.isConfirmed) {
        // Generate PDF WITH links
        this.pdfGeneratorService.generateFormationProgramPdfWithLinks(formationForPdf, mockPlan);
        Swal.fire({
          icon: 'success',
          title: 'Plan généré avec liens',
          html: 'Le plan de formation avec liens vers les contenus a été généré.<br><small>Les liens sont cliquables dans le PDF.</small>',
          timer: 4000
        });
      } else if (result.isDenied) {
        // Generate simple PDF WITHOUT links
        this.pdfGeneratorService.generateFormationProgramPdf(formationForPdf, mockPlan);
        Swal.fire({
          icon: 'success',
          title: 'Plan généré',
          html: 'Le plan de formation a été généré.<br><small>Utilisez la fonction d\'impression pour l\'enregistrer en PDF.</small>',
          timer: 4000
        });
      }
    });
  }

  savePlanFromGenerator(): void {
    if (!this.generalInfoSaved) {
      Swal.fire({
        icon: 'warning',
        title: 'Formation non sauvegardée',
        text: 'Veuillez d\'abord sauvegarder les informations générales de la formation.'
      });
      return;
    }

    // Enhanced validation
    const validationErrors: string[] = [];
    
    if (!this.planGeneratorForm.titre?.trim()) {
      validationErrors.push('Le titre est requis.');
    } else if (this.planGeneratorForm.titre.trim().length < 3) {
      validationErrors.push('Le titre doit contenir au moins 3 caractères.');
    } else if (this.planGeneratorForm.titre.trim().length > 200) {
      validationErrors.push('Le titre ne peut pas dépasser 200 caractères.');
    }
    
    if (!this.planGeneratorForm.dateDebut) {
      validationErrors.push('La date de début est requise.');
    }
    
    if (!this.planGeneratorForm.dateFin) {
      validationErrors.push('La date de fin est requise.');
    }
    
    if (this.planGeneratorForm.dateDebut && this.planGeneratorForm.dateFin) {
      const dateDebut = new Date(this.planGeneratorForm.dateDebut);
      const dateFin = new Date(this.planGeneratorForm.dateFin);
      if (dateFin < dateDebut) {
        validationErrors.push('La date de fin doit être postérieure à la date de début.');
      }
    }
    
    if (!this.planGeneratorForm.formateurId) {
      validationErrors.push('Le formateur est requis.');
    }

    if (validationErrors.length > 0) {
      Swal.fire({
        icon: 'warning',
        title: 'Erreurs de validation',
        html: '<ul style=\"text-align: left; margin-top: 10px;\">' + 
              validationErrors.map(err => `<li>${err}</li>`).join('') + 
              '</ul>',
        confirmButtonText: 'OK'
      });
      return;
    }

    if (!this.form.idFormation) {
      Swal.fire({
        icon: 'error',
        title: 'Erreur',
        text: 'Formation non trouvée. Veuillez sauvegarder la formation d\'abord.'
      });
      return;
    }

    this.saving = true;

    const planPayload: PlanFormation = {
      titre: this.planGeneratorForm.titre,
      description: this.planGeneratorForm.description,
      dateDebut: this.planGeneratorForm.dateDebut,
      dateFin: this.planGeneratorForm.dateFin,
      dateLancement: this.planGeneratorForm.dateLancement,
      dateFinReel: this.planGeneratorForm.dateFinReel,
      statusFormation: this.planGeneratorForm.status,
      idFormation: this.form.idFormation,
      idFormateur: this.planGeneratorForm.formateurId,
      nombreJours: this.planGeneratorForm.nombreJours
    };

    // Add idPlanFormation if updating
    if (this.editingPlanId !== null) {
      planPayload.idPlanFormation = this.editingPlanId;
    }

    // Determine if we're creating or updating
    const isUpdate = this.editingPlanId !== null;
    const serviceCall = isUpdate 
      ? this.planFormationService.updatePlan(this.editingPlanId!, planPayload)
      : this.planFormationService.createPlan(planPayload);

    serviceCall.subscribe({
      next: (res: any) => {
        this.saving = false;
        Swal.fire({
          icon: 'success',
          title: isUpdate ? 'Plan modifié' : 'Plan créé',
          text: `Le plan de formation a été ${isUpdate ? 'modifié' : 'créé'} avec succès.`,
          timer: 2000,
          showConfirmButton: false
        });

        // Reset form, editing state, and close panel
        this.planGeneratorForm = {
          titre: '',
          description: '',
          dateDebut: '',
          dateFin: '',
          dateLancement: '',
          dateFinReel: '',
          formateurId: undefined as any,
          status: StatutFormation.PLANIFIEE,
          nombreJours: undefined
        };
        this.editingPlanId = null;
        this.showPlanGenerator = false;

        // Reload plans list
        if (this.form.idFormation) {
          this.loadPlansForFormation(this.form.idFormation);
        }
      },
      error: (err: any) => {
        this.saving = false;
        console.error(`Erreur lors de la ${isUpdate ? 'modification' : 'création'} du plan:`, err);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: `Impossible de ${isUpdate ? 'modifier' : 'créer'} le plan de formation.`
        });
      }
    });
  }

  generatePlanPdf(): void {
    if (!this.generalInfoSaved) { this.promptGeneralInfo(); return; }
    // Validate form
    if (!this.planGeneratorForm.titre || !this.planGeneratorForm.description || 
        !this.planGeneratorForm.dateDebut || !this.planGeneratorForm.dateFin || 
        !this.planGeneratorForm.formateurId) {
      Swal.fire({
        icon: 'warning',
        title: 'Formulaire incomplet',
        text: 'Veuillez renseigner tous les champs requis.'
      });
      return;
    }

    if (!this.form.theme || !this.form.theme.trim()) {
      Swal.fire({
        icon: 'warning',
        title: 'Thème de formation manquant',
        html: 'Veuillez d\'abord renseigner le <strong>thème de la formation</strong> dans l\'onglet <strong>"📋 Informations Générales"</strong>.',
        confirmButtonText: 'Compris',
        confirmButtonColor: '#10b981'
      }).then(() => {
        // Auto-navigate to general info section
        this.showGeneralInfo = true;
        this.showPlanGenerator = false;
        this.showAddGlobalForm = false;
        this.showAddSpecificForm = false;
        this.showLinkForm = false;
        this.showContentBuilder = false;
        this.showContentDetailAssigner = false;
      });
      return;
    }

    // Use programmesDetailes from the loaded formation OR build from objectifs if empty
    let programmesDetailes: ProgrammeDetaile[] = this.form.programmesDetailes || [];
    
    // Sort jours within each programme to ensure proper ordering
    if (programmesDetailes.length > 0) {
      programmesDetailes.forEach(programme => {
        if (programme.jours && programme.jours.length > 0) {
          programme.jours.sort((a, b) => a.numeroJour - b.numeroJour);
        }
      });
    }
    
    // If no programmes exist, try building from objectifs and contenus
    if (programmesDetailes.length === 0) {
      console.warn('No programmesDetailes found, building from objectifs...');
      programmesDetailes = this.buildProgrammesFromFormObjectifs();
    }
    
    if (programmesDetailes.length === 0) {
      console.warn('Still no programmesDetailes after building from objectifs');
    }
    
    // Apply selected objectives to form
    this.applySelectedGlobauxToForm();

    // Create PlanFormation from form data
    const selectedFormateur = this.formateurs.find(f => f.idFormateur === this.planGeneratorForm.formateurId);
    const planFormation: PlanFormation = {
      titre: this.planGeneratorForm.titre,
      description: this.planGeneratorForm.description,
      dateDebut: this.planGeneratorForm.dateDebut,
      dateFin: this.planGeneratorForm.dateFin,
      statusFormation: this.planGeneratorForm.status,
      formateur: selectedFormateur ? {
        nom: selectedFormateur.nom,
        prenom: selectedFormateur.prenom
      } : { nom: '', prenom: '' },
      idPlanFormation: Math.floor(Math.random() * 10000)
    };

    // Create formation object for PDF using programmesDetailes from form
    const baseFormationForPdf: Formation = {
      ...this.form,
      nombreHeures: this.totalHoursOverall(),
      programmesDetailes: programmesDetailes,
      contenusFormation: []
    };

    // Fetch contenus détaillés AND contenusWithJours before showing PDF dialog
    const finishWithFormation = (
      contenusFormation: ContenuFormation[], 
      contenusWithJours: ContenuWithJour[] | undefined,
      enrichedProgrammes: ProgrammeDetaile[]
    ) => {
      const formationForPdf: Formation = {
        ...baseFormationForPdf,
        programmesDetailes: enrichedProgrammes, // Use enriched programmes with real hours
        contenusFormation,
        contenusWithJours
      };
      console.log('📋 FormationForPdf built with contenusWithJours:', contenusWithJours);
      console.log('📋 FormationForPdf enrichedProgrammes:', enrichedProgrammes);
      this.showPdfTypeDialog(formationForPdf, planFormation);
    };

    if (this.form.idFormation) {
      console.log('🔄 Fetching contenus detailles + contenusWithJours for formation (plan PDF):', this.form.idFormation);
      
      this.startLoading('Génération du PDF...');
      
      forkJoin({
        contenusDetailles: this.contenuDetailleService.getContenuDetailleByFormationId(this.form.idFormation).pipe(
          catchError(err => {
            console.error('❌ Error fetching contenusDetailles (plan PDF):', err);
            return of([]);
          })
        ),
        contenusWithJours: this.contenuDetailleService.getContenuDetailleByFormationWithJours(this.form.idFormation).pipe(
          catchError(err => {
            console.error('❌ Error fetching contenusWithJours (plan PDF):', err);
            return of([]);
          })
        )
      }).subscribe({
        next: ({ contenusDetailles, contenusWithJours }) => {
          console.log('✅ Fetched contenusDetailles (plan PDF):', contenusDetailles);
          console.log('✅ Fetched contenusWithJours (plan PDF):', contenusWithJours);
          
          this.stopLoading();
          
          // Build a map of ContenuDetaille hours from levels for enrichment
          const detailHoursMap = new Map<number, { theorique: number, pratique: number, levels: any[] }>();
          (contenusDetailles || []).forEach((cd: any) => {
            let totalTheorique = 0;
            let totalPratique = 0;
            if (cd.levels && Array.isArray(cd.levels) && cd.levels.length > 0) {
              cd.levels.forEach((level: any) => {
                totalTheorique += level.dureeTheorique || 0;
                totalPratique += level.dureePratique || 0;
              });
            } else {
              totalTheorique = cd.dureeTheorique || 0;
              totalPratique = cd.dureePratique || 0;
            }
            detailHoursMap.set(cd.idContenuDetaille, { 
              theorique: totalTheorique, 
              pratique: totalPratique,
              levels: cd.levels || []
            });
          });
          console.log('📊 Built detailHoursMap for PDF:', detailHoursMap);
          
          // Enrich programmesDetailes with actual hours from contenusDetailles levels
          const enrichedProgrammes = this.enrichProgrammesWithHours(programmesDetailes, detailHoursMap);
          
          const assignedIds = this.getAssignedContentIds();
          const contenusFormation: ContenuFormation[] = (contenusDetailles || [])
            .filter((cd: any) => assignedIds.size === 0 || assignedIds.has(cd.idContenuDetaille))
            .map((cd: any) => ({
              id: cd.idContenuDetaille,
              title: cd.titre || cd.title || 'Contenu',
              description: cd.methodesPedagogiques || cd.description || '',
              type: this.mapContentType(cd),
              size: this.calculateContentSize(cd),
              uploadDate: cd.dateCreation || new Date().toISOString(),
              fileName: this.extractFileName(cd),
              fileUrl: cd.fileUrl || cd.filePath || cd.cheminFichier || '',
              videoUrl: cd.videoUrl || ''
            }));
          
          // If backend didn't return contenusWithJours, build from local programmes data
          let effectiveContenusWithJours = contenusWithJours;
          if (!contenusWithJours || contenusWithJours.length === 0) {
            console.log('⚠️ No contenusWithJours from backend (plan PDF), building from local programmes...');
            effectiveContenusWithJours = this.buildContenusWithJoursFromProgrammes(contenusDetailles, enrichedProgrammes);
          }
          
          // Also enrich effectiveContenusWithJours with levels data for PDF support links
          if (effectiveContenusWithJours && contenusDetailles) {
            const detailMap = new Map<number, any>();
            contenusDetailles.forEach((cd: any) => detailMap.set(cd.idContenuDetaille, cd));

            effectiveContenusWithJours.forEach((item: any) => {
              const detail = detailMap.get(item.idContenuDetaille);
              if (detail) {
                // Enrich with hours from levels
                const hours = detailHoursMap.get(item.idContenuDetaille);
                if (hours) {
                  if (!item.dureeTheorique) item.dureeTheorique = hours.theorique;
                  if (!item.dureePratique) item.dureePratique = hours.pratique;
                }
                // Ensure levels are present
                if ((!item.levels || item.levels.length === 0) && detail.levels && detail.levels.length > 0) {
                  item.levels = detail.levels;
                }
              }
            });
          }

          console.log('🎯 EffectiveContenusWithJours (plan PDF):', effectiveContenusWithJours);
          
          finishWithFormation(contenusFormation, effectiveContenusWithJours, enrichedProgrammes);
        },
        error: (err) => {
          console.error('❌ Error in forkJoin (plan PDF):', err);
          const fallback = this.extractContenusFormationFromProgrammes(programmesDetailes);
          const localContenus = this.buildContenusWithJoursFromProgrammes([], programmesDetailes);
          finishWithFormation(fallback, localContenus, programmesDetailes);
        }
      });
    } else {
      const fallback = this.extractContenusFormationFromProgrammes(programmesDetailes);
      const localContenus = this.buildContenusWithJoursFromProgrammes([], programmesDetailes);
      finishWithFormation(fallback, localContenus, programmesDetailes);
    }
  }

  /**
   * Build programmesDetailes from form objectifs (backend data)
   * Used when programmesDetailes is not saved but objectifs exist
   */
  private buildProgrammesFromFormObjectifs(): ProgrammeDetaile[] {
    console.log('🔨 Building programmesDetailes from form objectifs...');
    console.log('form.objectifsSpecifiques:', this.form.objectifsSpecifiques);
    
    const programmesDetailes: ProgrammeDetaile[] = [];
    
    // Check if we have objectifs specifiques
    if (!this.form.objectifsSpecifiques || this.form.objectifsSpecifiques.length === 0) {
      console.warn('No objectifsSpecifiques in form');
      return programmesDetailes;
    }

    // Build a programme for each objectif specifique with its contenus
    this.form.objectifsSpecifiques.forEach((objectifSpec: any) => {
      console.log('Processing objectif:', objectifSpec);
      
      // Get contenus for this objectif from the backend data
      const contenus = objectifSpec.contenus || [];

      // Group contenus by numeroJour (day)
      const dayMap = new Map<number, any[]>();
      contenus.forEach((contenu: any) => {
        const dayNumber = Number(contenu.numeroJour ?? 1);
        if (!dayMap.has(dayNumber)) {
          dayMap.set(dayNumber, []);
        }
        dayMap.get(dayNumber)!.push(contenu);
      });

      // Create JourFormation array, sorted by day number
      const jours: JourFormation[] = [];
      const sortedDays = Array.from(dayMap.keys()).sort((a, b) => a - b);

      sortedDays.forEach(dayNumber => {
        const conteusForDay = dayMap.get(dayNumber) || [];
        
        let contenuDetailles: ContenuDetaille[] = conteusForDay.map((contenu: any) => {
          // Preserve the original contenu object with file information
          const contenuDetaille: any = {
            contenusCles: [contenu.contenu || contenu.titre || ''].filter(Boolean),
            methodesPedagogiques: contenu.moyenPedagogique || contenu.supportPedagogique || '',
            dureeTheorique: contenu.nbHeuresTheoriques || 0,
            dureePratique: contenu.nbHeuresPratiques || 0
          };

          // Attach file information if available (assignedContentDetails contains files)
          if (contenu.assignedContentDetails && Array.isArray(contenu.assignedContentDetails)) {
            // Map assignedContentDetails to files structure that PDF generator expects
            contenuDetaille.files = contenu.assignedContentDetails.map((detail: any) => ({
              idFile: detail.idContenuDetaille,
              fileName: detail.titre,
              filePath: detail.filePath || detail.cheminFichier,
              fileType: detail.typeFichier || this.getFileType(detail.titre || detail.filePath),
              fileSize: detail.tailleFichier
            }));
          }

          return contenuDetaille;
        });

        // Fallback: if no contenus for this day, create a neutral placeholder
        if (contenuDetailles.length === 0) {
          contenuDetailles = [{
            contenusCles: ['Contenu non spécifié'],
            methodesPedagogiques: objectifSpec.description || '',
            dureeTheorique: 0,
            dureePratique: 0
          }];
        }

        jours.push({
          numeroJour: dayNumber,
          contenus: contenuDetailles
        });
      });

      programmesDetailes.push({
        titre: objectifSpec.titre || 'Programme',
        jours: jours
      });
    });

    console.log('Built programmesDetailes:', programmesDetailes);
    return programmesDetailes;
  }

  private getFileType(filename: string): string {
    if (!filename) return 'application/octet-stream';
    const ext = filename.toLowerCase().split('.').pop();
    switch (ext) {
      case 'pdf': return 'application/pdf';
      case 'doc': return 'application/msword';
      case 'docx': return 'application/vnd.openxmlformats-officedocument.wordprocessingml.document';
      case 'xls': return 'application/vnd.ms-excel';
      case 'xlsx': return 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      case 'ppt': return 'application/vnd.ms-powerpoint';
      case 'pptx': return 'application/vnd.openxmlformats-officedocument.presentationml.presentation';
      case 'jpg':
      case 'jpeg': return 'image/jpeg';
      case 'png': return 'image/png';
      case 'mp4': return 'video/mp4';
      default: return 'application/octet-stream';
    }
  }

  private showPdfTypeDialog(formationForPdf: Formation, planFormation: PlanFormation): void {

    // Ask user which type of PDF they want
    Swal.fire({
      title: 'Type de PDF',
      html: 'Quel type de fiche programme souhaitez-vous générer ?',
      icon: 'question',
      showCancelButton: true,
      showDenyButton: true,
      confirmButtonText: '📎 Avec liens vers contenus',
      denyButtonText: '📄 Simple (sans liens)',
      cancelButtonText: 'Annuler',
      confirmButtonColor: '#10b981',
      denyButtonColor: '#3b82f6'
    }).then((result) => {
      if (result.isConfirmed) {
        // Generate PDF WITH links
        this.pdfGeneratorService.generateFormationProgramPdfWithLinks(formationForPdf, planFormation);
        Swal.fire({
          icon: 'success',
          title: 'Plan généré avec liens',
          html: 'Le plan de formation avec liens vers les contenus a été généré.<br><small>Les liens sont cliquables dans le PDF.</small>',
          timer: 4000
        });
      } else if (result.isDenied) {
        // Generate simple PDF WITHOUT links
        this.pdfGeneratorService.generateFormationProgramPdf(formationForPdf, planFormation);
        Swal.fire({
          icon: 'success',
          title: 'Plan généré',
          html: 'Le plan de formation a été généré.<br><small>Utilisez la fonction d\'impression pour l\'enregistrer en PDF.</small>',
          timer: 4000
        });
      }
    });

    // Reset form and close panel
    this.planGeneratorForm = {
      titre: '',
      description: '',
      dateDebut: '',
      dateFin: '',
      dateLancement: '',
      dateFinReel: '',
      formateurId: undefined as any,
      status: StatutFormation.PLANIFIEE,
      nombreJours: undefined
    };
    this.showPlanGenerator = false;
  }

  private mapContentDetail(item: any) {
    return {
      id: item.idContenuDetaille,
      title: item.titre,
      description: item.methodesPedagogiques || '',
      type: 'content',
      contenusCles: item.contenusCles || [],
      methodesPedagogiques: item.methodesPedagogiques,
      dureeTheorique: item.dureeTheorique,
      dureePratique: item.dureePratique,
      levels: item.levels || [],
      fileUrl: item.fileUrl || item.filePath || '',
      videoUrl: item.videoUrl || '',
      filePath: item.filePath || ''
    };
  }

  private mapContentDetailList(data: any[]): any[] {
    if (!Array.isArray(data)) return [];
    return data.map((item: any) => this.mapContentDetail(item));
  }

  // Load plans for formation
  private loadPlansForFormation(formationId: number): void {
    this.planFormationService.getPlansByFormation(formationId).subscribe({
      next: (plans) => {
        this.plansFormation = plans.map((plan) => {
          const mapped = {
            id: plan.idPlanFormation || 0, // Use the actual backend ID, fallback to 0
            titre: plan.titre || '',
            description: plan.description || '',
            dateDebut: plan.dateDebut || '',
            dateFin: plan.dateFin || '',
            selected: false,
            _backendData: plan
          };
          return mapped;
        });
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        // Don't show error popup, just log it
      }
    });
  }

  // Plan management methods
  selectPlan(plan: any): void {
    if (!this.plansFormation) return;
    
    // Toggle selection
    const currentSelection = plan.selected || false;
    this.plansFormation.forEach(p => p.selected = false);
    
    if (!currentSelection) {
      plan.selected = true;
      this.selectedPlan = plan;
    } else {
      this.selectedPlan = null;
    }
  }

  editPlanItem(plan: any): void {
    if (!plan || !plan.id) {
      alert('Impossible de modifier ce plan');
      return;
    }

    // Load plan data from backend to get the full details
    
    // Helper function to format date for HTML date input (YYYY-MM-DD)
    const formatDateForInput = (dateStr: string): string => {
      if (!dateStr) return '';
      // Extract just the date part if it's an ISO datetime (2026-01-08T00:00:00 -> 2026-01-08)
      return dateStr.split('T')[0];
    };
    
    // Populate the form with plan data
    this.editingPlanId = plan.id;
    console.log('📋 Editing plan, backend data:', plan._backendData);
    this.planGeneratorForm = {
      titre: plan.titre || '',
      description: plan.description || '',
      dateDebut: formatDateForInput(plan.dateDebut || plan._backendData?.dateDebut || ''),
      dateFin: formatDateForInput(plan.dateFin || plan._backendData?.dateFin || ''),
      dateLancement: formatDateForInput(plan._backendData?.dateLancement || ''),
      dateFinReel: formatDateForInput(plan._backendData?.dateFinReel || ''),
      formateurId: plan._backendData?.formateur?.idFormateur || plan._backendData?.idFormateur || undefined,
      status: plan._backendData?.statusFormation || StatutFormation.PLANIFIEE,
      nombreJours: plan._backendData?.nombreJours || plan.nombreJours
    };
    console.log('📋 Form populated with nombreJours:', this.planGeneratorForm.nombreJours);

    // Open the plan generator modal in edit mode
    this.showPlanGenerator = true;
    this.showContentDetailCreator = false;

  }

  deletePlanItem(planId: any): void {
    if (!planId) {
      alert('Impossible de supprimer ce plan');
      return;
    }

    Swal.fire({
      icon: 'warning',
      title: 'Supprimer le plan?',
      text: 'Êtes-vous sûr de vouloir supprimer ce plan? Cette action ne peut pas être annulée.',
      showCancelButton: true,
      confirmButtonText: 'Supprimer',
      cancelButtonText: 'Annuler',
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#6b7280'
    }).then((result) => {
      if (result.isConfirmed) {
        // Find the plan
        const planIndex = this.plansFormation.findIndex((p: any) => p.id === planId);
        if (planIndex === -1) return;

        const plan = this.plansFormation[planIndex];

        // If plan has backend ID, delete from backend
        if (plan._backendData?.idPlanFormation) {
          this.startLoading('Suppression du plan...');
          this.planFormationService.deletePlan(plan._backendData.idPlanFormation).subscribe({
            next: () => {
              this.plansFormation = this.plansFormation.filter((p: any) => p.id !== planId);
              if (this.selectedPlan?.id === planId) {
                this.selectedPlan = null;
              }
              this.stopLoading();
              Swal.fire({
                icon: 'success',
                title: 'Plan supprimé',
                text: 'Le plan a été supprimé avec succès.',
                timer: 2000,
                showConfirmButton: false
              });
            },
            error: (err: any) => {
              console.error('Erreur suppression plan', err);
              this.stopLoading();
              Swal.fire({
                icon: 'error',
                title: 'Erreur',
                text: 'Une erreur est survenue lors de la suppression du plan.'
              });
            }
          });
        } else {
          // No backend ID, just remove from array
          this.plansFormation = this.plansFormation.filter((p: any) => p.id !== planId);
          if (this.selectedPlan?.id === planId) {
            this.selectedPlan = null;
          }
          Swal.fire({
            icon: 'success',
            title: 'Plan supprimé',
            text: 'Le plan a été supprimé avec succès.',
            timer: 2000,
            showConfirmButton: false
          });
        }
      }
    });
  }

  // Helper methods for PDF content generation
  
  /**
   * Extract contenusFormation from programmesDetailes and objectifsSpecifiques
   * This is a workaround for the broken backend endpoint /by-formation/{id}
   */
  private extractContenusFormationFromProgrammes(programmesDetailes: ProgrammeDetaile[]): ContenuFormation[] {
    const contenusFormation: ContenuFormation[] = [];
    const seenIds = new Set<number>();

    console.log('🔍 Extracting contenus from programmesDetailes:', programmesDetailes);
    console.log('🔍 form.objectifsSpecifiques:', this.form.objectifsSpecifiques);

    // Strategy 1: Extract from form.objectifsSpecifiques (has the most complete data)
    if (this.form.objectifsSpecifiques && Array.isArray(this.form.objectifsSpecifiques)) {
      this.form.objectifsSpecifiques.forEach((objectifSpec: any) => {
        const contenus = objectifSpec.contenus || [];
        
        contenus.forEach((contenu: any) => {
          // Check if this contenu has assignedContentDetails (array of IDs) and resolve them against the library
          const assignedIds = Array.isArray(contenu.assignedContentDetails) ? contenu.assignedContentDetails : [];
          assignedIds.forEach((id: number) => {
            if (!id || seenIds.has(id)) { return; }
            const detail = this.getContentDetailById(id);
            if (!detail) { return; }

            seenIds.add(id);
            const filePath = detail.fileUrl || detail.filePath || '';
            const fileName = detail.title || this.extractFileNameFromPath(filePath);

            contenusFormation.push({
              id: id,
              title: fileName,
              description: detail.description || detail.methodesPedagogiques || '',
              type: this.mapContentTypeFromPath(filePath),
              size: detail.tailleFichier ? this.formatFileSize(detail.tailleFichier) : '~1 MB',
              uploadDate: detail.dateCreation || new Date().toISOString(),
              fileName: fileName,
              fileUrl: filePath,
              videoUrl: detail.videoUrl || ''
            });
          });
        });
      });
    }

    // Strategy 2: Extract from contentItems if available
    if (contenusFormation.length === 0 && this.contentItems && Array.isArray(this.contentItems)) {
      this.contentItems.forEach((item: any) => {
        if (item._backendData && item._backendData.assignedContentDetails) {
          const details = item._backendData.assignedContentDetails;
          
          if (Array.isArray(details)) {
            details.forEach((detail: any) => {
              const id = detail.idContenuDetaille || detail.id || detail;
              if (!id || seenIds.has(id)) { return; }

              // If detail is an object with paths, use it; otherwise resolve from library
              const resolved = (typeof detail === 'object') ? detail : this.getContentDetailById(id);
              if (!resolved) { return; }

              seenIds.add(id);
              const filePath = resolved.fileUrl || resolved.filePath || '';
              const fileName = resolved.titre || resolved.title || resolved.fileName || this.extractFileNameFromPath(filePath);

              contenusFormation.push({
                id: id,
                title: fileName,
                description: resolved.methodesPedagogiques || resolved.description || '',
                type: this.mapContentTypeFromPath(filePath),
                size: resolved.tailleFichier ? this.formatFileSize(resolved.tailleFichier) : '~1 MB',
                uploadDate: resolved.dateCreation || new Date().toISOString(),
                fileName: fileName,
                fileUrl: filePath,
                videoUrl: resolved.videoUrl || ''
              });
            });
          }
        }
      });
    }

    console.log('✅ Extracted contenusFormation:', contenusFormation);
    return contenusFormation;
  }

  private extractFileNameFromPath(filePath: string): string {
    if (!filePath) return 'fichier';
    const parts = filePath.split('/');
    return parts[parts.length - 1] || 'fichier';
  }

  private getAssignedContentIds(): Set<number> {
    const ids = new Set<number>();

    // From objectifsSpecifiques
    if (Array.isArray(this.form.objectifsSpecifiques)) {
      this.form.objectifsSpecifiques.forEach((objectif: any) => {
        const contenus = objectif?.contenus || [];
        contenus.forEach((c: any) => {
          const assigned = Array.isArray(c.assignedContentDetails) ? c.assignedContentDetails : [];
          assigned.forEach((id: any) => { if (id) ids.add(Number(id)); });
        });
      });
    }

    // From contentItems (built content list)
    if (Array.isArray(this.contentItems)) {
      this.contentItems.forEach((item: any) => {
        const assigned = item?._backendData?.assignedContentDetails || item?.assignedContentDetails;
        if (Array.isArray(assigned)) {
          assigned.forEach((id: any) => { if (id) ids.add(Number(id)); });
        }
      });
    }

    return ids;
  }

  private getContentDetailById(id: number): any | null {
    if (!id) return null;
    const source = this.contentDetailLibrary || [];
    return source.find((c: any) => c.id === id) || null;
  }

  private mapContentTypeFromPath(filePath: string): string {
    if (!filePath) return 'DOCUMENT';
    
    const ext = filePath.toLowerCase().split('.').pop();
    
    if (ext === 'pdf') return 'PDF';
    if (['mp4', 'avi', 'mov', 'wmv', 'webm'].includes(ext || '')) return 'VIDEO';
    if (['ppt', 'pptx'].includes(ext || '')) return 'PRESENTATION';
    if (['doc', 'docx', 'txt'].includes(ext || '')) return 'DOCUMENT';
    if (['jpg', 'jpeg', 'png', 'gif'].includes(ext || '')) return 'IMAGE';
    if (['xls', 'xlsx'].includes(ext || '')) return 'SPREADSHEET';
    
    return 'DOCUMENT';
  }

  private mapContentType(contentDetail: any): string {
    // Determine type based on file extension or explicit type field
    if (contentDetail.type) {
      return contentDetail.type.toUpperCase();
    }
    
    const fileName = contentDetail.fileName || contentDetail.filePath || contentDetail.fileUrl || '';
    const ext = fileName.toLowerCase().split('.').pop();
    
    if (ext === 'pdf') return 'PDF';
    if (['mp4', 'avi', 'mov', 'wmv'].includes(ext || '')) return 'VIDEO';
    if (['ppt', 'pptx'].includes(ext || '')) return 'PRESENTATION';
    if (['doc', 'docx', 'txt'].includes(ext || '')) return 'DOCUMENT';
    if (['jpg', 'jpeg', 'png', 'gif'].includes(ext || '')) return 'IMAGE';
    
    return 'DOCUMENT';
  }

  private calculateContentSize(contentDetail: any): string {
    // If size is already provided, use it
    if (contentDetail.size) {
      return contentDetail.size;
    }
    
    // Otherwise, return a generic size based on type
    const type = this.mapContentType(contentDetail);
    if (type === 'VIDEO') return '~50 MB';
    if (type === 'PDF') return '~2 MB';
    if (type === 'PRESENTATION') return '~5 MB';
    
    return '~1 MB';
  }

  private extractFileName(contentDetail: any): string {
    // Try to extract filename from various properties
    if (contentDetail.fileName) return contentDetail.fileName;
    
    const url = contentDetail.fileUrl || contentDetail.filePath || contentDetail.videoUrl || '';
    if (url) {
      const parts = url.split('/');
      return parts[parts.length - 1] || contentDetail.title + '.file';
    }
    
    return contentDetail.title + '.file';
  }

  /**
   * Enrich programmesDetailes with actual hours from ContenuDetaille levels
   * This ensures the PDF generator has real hours, not fallback values
   */
  private enrichProgrammesWithHours(
    programmesDetailes: ProgrammeDetaile[], 
    detailHoursMap: Map<number, { theorique: number, pratique: number, levels: any[] }>
  ): ProgrammeDetaile[] {
    // Deep clone to avoid mutating original
    const enriched: ProgrammeDetaile[] = JSON.parse(JSON.stringify(programmesDetailes));
    
    enriched.forEach(programme => {
      if (programme.jours && Array.isArray(programme.jours)) {
        programme.jours.forEach(jour => {
          if (jour.contenus && Array.isArray(jour.contenus)) {
            jour.contenus.forEach((contenu: any) => {
              // Try to match by title to contentItems, then look up hours
              const matchingContentItem = this.contentItems.find(ci => 
                ci.title === contenu.contenusCles?.[0] || 
                ci.title === contenu.titre
              );
              
              if (matchingContentItem && matchingContentItem.assignedContentDetails) {
                // Sum hours from all assigned ContenuDetaille
                let totalTheorique = 0;
                let totalPratique = 0;
                let allLevels: any[] = [];
                
                matchingContentItem.assignedContentDetails.forEach((cdId: number) => {
                  const hours = detailHoursMap.get(cdId);
                  if (hours) {
                    totalTheorique += hours.theorique;
                    totalPratique += hours.pratique;
                    if (hours.levels && hours.levels.length > 0) {
                      allLevels = allLevels.concat(hours.levels);
                    }
                  }
                });
                
                if (totalTheorique > 0 || totalPratique > 0) {
                  contenu.dureeTheorique = totalTheorique;
                  contenu.dureePratique = totalPratique;
                  contenu.levels = allLevels;
                  console.log(`📊 Enriched contenu "${contenu.contenusCles?.[0]}" with hours: theo=${totalTheorique}, prat=${totalPratique}`);
                }
              }
            });
          }
        });
      }
    });
    
    return enriched;
  }

  /**
   * Build contenusWithJours from contenusDetailles and programmesDetailes
   * Maps each contenu to its jour number using the programme structure
   */
  private buildContenusWithJoursFromProgrammes(contenusDetailles: any[], programmesDetailes: any[]): any[] {
    const result: any[] = [];
    
    // Create a map of contenu ID to contenu data for quick lookup
    const contenuMap = new Map<number, any>();
    contenusDetailles.forEach(cd => {
      if (cd.idContenuDetaille) {
        contenuMap.set(cd.idContenuDetaille, cd);
      }
    });
    
    // Iterate through programmes -> jours -> contenus to find the jour number for each contenu
    programmesDetailes.forEach(programme => {
      if (programme.jours && Array.isArray(programme.jours)) {
        programme.jours.forEach((jour: any) => {
          const numeroJour = jour.numeroJour || jour.idJour || 1;
          
          if (jour.contenus && Array.isArray(jour.contenus)) {
            jour.contenus.forEach((contenu: any) => {
              const contenuId = contenu.idContenuDetaille;
              
              // Find the full contenu data from the backend response, or use the current contenu
              const fullContenu = contenuMap.get(contenuId) || contenu;
              
              // Extract files from levels (if available)
              const files: any[] = [];
              if (fullContenu.levels && Array.isArray(fullContenu.levels)) {
                fullContenu.levels.forEach((level: any) => {
                  if (level.files && Array.isArray(level.files)) {
                    level.files.forEach((file: any) => {
                      files.push({
                        fileName: file.fileName || file.name || 'Document',
                        filePath: file.filePath || file.path || '',
                        fileType: file.fileType || file.type || '',
                        fileSize: file.fileSize || file.size || 0,
                        levelNumber: level.levelNumber || level.level || 1
                      });
                    });
                  }
                });
              }
              
              // Also check direct files property
              if (fullContenu.files && Array.isArray(fullContenu.files)) {
                fullContenu.files.forEach((file: any) => {
                  files.push({
                    fileName: file.fileName || file.name || 'Document',
                    filePath: file.filePath || file.path || '',
                    fileType: file.fileType || file.type || '',
                    fileSize: file.fileSize || file.size || 0,
                    levelNumber: 1
                  });
                });
              }
              
              // Add to result - include even without files so we can show hours in PDF
              result.push({
                idContenuDetaille: contenuId,
                titre: fullContenu.titre || fullContenu.title || contenu.titre || contenu.contenusCles?.[0] || 'Contenu',
                methodesPedagogiques: fullContenu.methodesPedagogiques || contenu.methodesPedagogiques || '',
                dureeTheorique: contenu.dureeTheorique || fullContenu.dureeTheorique || 0,
                dureePratique: contenu.dureePratique || fullContenu.dureePratique || 0,
                numeroJour: numeroJour,
                files: files
              });
            });
          }
        });
      }
    });
    
    return result;
  }
}
