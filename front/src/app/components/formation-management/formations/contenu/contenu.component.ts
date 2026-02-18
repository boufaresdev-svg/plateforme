import { Component, Input, Output, EventEmitter, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NiveauFormation } from '../../../../models/formation/formation.model';
import { Subject, Subscription, debounceTime, distinctUntilChanged } from 'rxjs';
import { ContenuDetailleService } from '../../../../services/contenu-detaille.service';
import { ContenuJourFormationService } from '../../../../services/formation_managment/ContenuJourFormation.service';
import Swal from 'sweetalert2';

interface ContentItem {
  id: number;
  title: string;
  description: string;
  detail?: string;
  level?: string;
  staff?: string;
  hoursPractical?: number;
  hoursTheoretical?: number;
  day: number;
  specificId?: number;
  order: number;
  assignedContentDetails?: number[];
  _backendData?: any;
}

@Component({
  selector: 'app-formation-contenu',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './contenu.component.html',
  styleUrls: ['./contenu.component.css']
})
export class FormationContenuComponent implements OnInit {
  @Input() formationId?: number;
  @Input() objectifsSpecifiques: any[] = [];
  @Input() formateurs: any[] = [];
  @Input() generalInfoSaved: boolean = false;
  @Input() contentItems: ContentItem[] = []; // Receive from parent
  
  @Output() contentItemsChange = new EventEmitter<ContentItem[]>();
  @Output() close = new EventEmitter<void>();

  niveaux = Object.values(NiveauFormation);
  
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
  selectedContentForEdit: any = null;
  showEditContentForm = false;

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
  isAssigning = false;

  // Content Detail Creator
  showContentDetailCreator = false;
  newContentDetail = {
    titre: '',
    contenusCles: [''],
    methodesPedagogiques: '',
    levels: [
      { levelNumber: 1, theorieContent: '', pratiqueContent: '', dureeTheorique: 0, dureePratique: 0, fileName: '', fileType: 'video', fileSize: 0, selectedLevels: [] as string[] }
    ]
  };
  
  selectedFiles: File[] = [];
  fileDescriptions: string[] = [];
  uploadedFiles: any[] = [];

  // Search Contenus Jour
  showSearchContenusJour = false;
  searchContenusJourQuery = '';
  contenusJourSearchResults: any[] = [];
  isSearchingContenusJour = false;
  selectedContenusJourForLink: Record<number, boolean> = {};
  targetObjectifSpecifiqueForContenu: number | null = null;
  selectedNiveauForAssignment: number | null = null;
  availableNiveaux = [
    { id: 1, label: 'Débutant', icon: '🌱' },
    { id: 2, label: 'Intermédiaire', icon: '📈' },
    { id: 3, label: 'Avancé', icon: '🚀' }
  ];

  constructor(
    private contenuDetailleService: ContenuDetailleService,
    private contenuJourFormationService: ContenuJourFormationService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.setupContentSearch();
  }

  ngOnDestroy(): void {
    if (this.contentSearchSub) {
      this.contentSearchSub.unsubscribe();
    }
  }

  private setupContentSearch(): void {
    this.contentSearchSub = this.contentSearch$
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(query => {
        if (query.trim()) {
          this.performContentSearch(query);
        } else {
          this.searchResults = null;
        }
      });
  }

  private performContentSearch(query: string): void {
    this.isSearching = true;
    // Search functionality to be implemented
    this.searchResults = [];
    this.isSearching = false;
    this.cdr.detectChanges();
  }

  get contentDays(): number[] {
    const days = new Set(this.contentItems.map(c => c.day));
    return Array.from(days).sort((a, b) => a - b);
  }

  orderedContentByDay(day: number): ContentItem[] {
    return this.contentItems
      .filter(c => c.day === day)
      .sort((a, b) => a.order - b.order);
  }

  addContentItem(): void {
    if (!this.contentForm.title?.trim()) {
      Swal.fire({
        icon: 'warning',
        title: 'Titre manquant',
        text: 'Veuillez renseigner un titre pour le contenu.'
      });
      return;
    }

    if (!this.contentForm.specificId) {
      Swal.fire({
        icon: 'warning',
        title: 'Objectif spécifique manquant',
        text: 'Veuillez sélectionner un objectif spécifique.'
      });
      return;
    }

    const maxOrder = this.contentItems
      .filter(c => c.day === this.contentForm.day)
      .reduce((max, c) => Math.max(max, c.order), 0);

    const payload = {
      contenu: this.contentForm.title.trim(),
      moyenPedagogique: this.contentForm.description?.trim() || undefined,
      supportPedagogique: this.contentForm.detail?.trim() || undefined,
      nbHeuresTheoriques: Number(this.contentForm.hoursTheoretical) || 0,
      nbHeuresPratiques: Number(this.contentForm.hoursPractical) || 0,
      idObjectifSpecifique: this.contentForm.specificId,
      numeroJour: this.contentForm.day || 1,
      staff: this.contentForm.staff || undefined,
      niveau: this.contentForm.level || undefined,
      idPlanFormation: undefined
    };

    this.contenuJourFormationService.addContenuJour(payload).subscribe({
      next: (response) => {
        let created = response;
        if (Array.isArray(response) && response.length > 0) {
          created = response[0];
        }

        const backendId = created?.idContenuJour || created?.id || (this.contentItems.reduce((max, c) => Math.max(max, c.id), 0) + 1);

        const newItem: ContentItem = {
          id: backendId,
          title: this.contentForm.title,
          description: this.contentForm.description,
          detail: this.contentForm.detail,
          level: this.contentForm.level,
          staff: this.contentForm.staff,
          hoursPractical: this.contentForm.hoursPractical,
          hoursTheoretical: this.contentForm.hoursTheoretical,
          day: this.contentForm.day,
          specificId: this.contentForm.specificId,
          order: maxOrder + 1,
          assignedContentDetails: [],
          _backendData: created
        };

        const updatedItems = [...this.contentItems, newItem];
        this.contentItemsChange.emit(updatedItems);
        
        // Reset form (keep day and specific for convenience)
        this.contentForm = {
          title: '',
          description: '',
          day: this.contentForm.day,
          specificId: this.contentForm.specificId,
          detail: '',
          level: '',
          staff: '',
          hoursPractical: 0,
          hoursTheoretical: 0
        };

        Swal.fire({
          icon: 'success',
          title: 'Contenu ajouté',
          timer: 1500,
          showConfirmButton: false
        });
      },
      error: (err) => {
        console.error('Erreur lors de la création du contenu jour', err);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible d\'ajouter le contenu. Veuillez réessayer.'
        });
      }
    });
  }

  editContentItem(item: ContentItem): void {
    this.selectedContentForEdit = { ...item };
    this.showEditContentForm = true;
  }

  saveEditedContent(): void {
    if (!this.selectedContentForEdit?.title?.trim()) {
      Swal.fire({
        icon: 'warning',
        title: 'Titre manquant',
        text: 'Veuillez renseigner un titre.'
      });
      return;
    }

    const index = this.contentItems.findIndex(c => c.id === this.selectedContentForEdit.id);
    if (index === -1) { return; }

    const payload = {
      contenu: this.selectedContentForEdit.title.trim(),
      moyenPedagogique: this.selectedContentForEdit.description?.trim() || undefined,
      supportPedagogique: this.selectedContentForEdit.detail?.trim() || undefined,
      nbHeuresTheoriques: Number(this.selectedContentForEdit.hoursTheoretical) || 0,
      nbHeuresPratiques: Number(this.selectedContentForEdit.hoursPractical) || 0,
      idObjectifSpecifique: this.selectedContentForEdit.specificId,
      numeroJour: this.selectedContentForEdit.day || 1,
      staff: this.selectedContentForEdit.staff || undefined,
      niveau: this.selectedContentForEdit.level || undefined,
      idPlanFormation: this.selectedContentForEdit._backendData?.idPlanFormation || undefined
    };

    const backendId = this.selectedContentForEdit._backendData?.idContenuJour;

    const applyLocalUpdate = () => {
      const updatedItems = [...this.contentItems];
      updatedItems[index] = { ...this.selectedContentForEdit };
      this.contentItemsChange.emit(updatedItems);
      this.showEditContentForm = false;
      this.selectedContentForEdit = null;
      Swal.fire({ icon: 'success', title: 'Contenu modifié', timer: 1500, showConfirmButton: false });
    };

    if (!backendId) {
      applyLocalUpdate();
      return;
    }

    this.contenuJourFormationService.updateContenuJour(backendId, payload).subscribe({
      next: (res) => {
        this.selectedContentForEdit._backendData = res;
        applyLocalUpdate();
      },
      error: (err) => {
        console.error('Erreur mise à jour contenu', err);
        Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de mettre à jour le contenu.' });
      }
    });
  }

  deleteContentItem(id: number): void {
    Swal.fire({
      icon: 'warning',
      title: 'Confirmer la suppression',
      text: 'Êtes-vous sûr de vouloir supprimer ce contenu?',
      showCancelButton: true,
      confirmButtonText: 'Supprimer',
      cancelButtonText: 'Annuler',
      confirmButtonColor: '#ef4444'
    }).then(result => {
      if (result.isConfirmed) {
        const updatedItems = this.contentItems.filter(c => c.id !== id);
        this.contentItemsChange.emit(updatedItems);
      }
    });
  }

  moveContent(id: number, direction: number): void {
    const index = this.contentItems.findIndex(c => c.id === id);
    if (index === -1) return;

    const item = this.contentItems[index];
    const dayItems = this.contentItems.filter(c => c.day === item.day);
    const dayIndex = dayItems.findIndex(c => c.id === id);
    
    if ((direction === -1 && dayIndex === 0) || (direction === 1 && dayIndex === dayItems.length - 1)) {
      return;
    }

    const swapIndex = dayIndex + direction;
    const temp = dayItems[dayIndex].order;
    dayItems[dayIndex].order = dayItems[swapIndex].order;
    dayItems[swapIndex].order = temp;

    const updatedItems = [...this.contentItems];
    this.contentItemsChange.emit(updatedItems);
  }

  openContentDetailAssigner(item: ContentItem): void {
    this.selectedContentItem = item;
    this.showContentDetailAssigner = true;
    this.contentSearchQuery = '';
    this.searchResults = null;
  }

  onContentSearchInput(): void {
    this.contentSearch$.next(this.contentSearchQuery);
  }

  toggleContentSelection(contentId: number): void {
    this.contentSelections[contentId] = !this.contentSelections[contentId];
  }

  getAssignedContentDetails(item: ContentItem): any[] {
    if (!item.assignedContentDetails || item.assignedContentDetails.length === 0) {
      return [];
    }
    return this.contentDetailLibrary.filter(cd => 
      item.assignedContentDetails!.includes(cd.id)
    );
  }

  // Calculate hours from assigned ContenuDetaille levels
  getContentItemHoursFromDetails(contentItem: any): { theorique: number, pratique: number } {
    let totalTheorique = 0;
    let totalPratique = 0;

    // First check if we have full objects loaded (with niveau data)
    if (contentItem._assignedContenusWithNiveau && contentItem._assignedContenusWithNiveau.length > 0) {
      contentItem._assignedContenusWithNiveau.forEach((cd: any) => {
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

      assignedDetails.forEach((cd: any) => {
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

    // Final fallback: use hours directly stored on the content item
    return { 
      theorique: contentItem.hoursTheoretical || 0, 
      pratique: contentItem.hoursPractical || 0 
    };
  }

  removeContentDetailFromItem(contentDetailId: number): void {
    if (this.selectedContentItem) {
      const updatedItems = this.contentItems.map(c => {
        if (c.id === this.selectedContentItem.id && c.assignedContentDetails) {
          return { ...c, assignedContentDetails: c.assignedContentDetails.filter(id => id !== contentDetailId) };
        }
        return c;
      });
      this.contentItemsChange.emit(updatedItems);
    }
  }

  onCloseContentDetailAssigner(): void {
    this.showContentDetailAssigner = false;
    this.selectedContentItem = null;
  }

  openContentDetailCreator(): void {
    this.showContentDetailCreator = true;
    this.resetContentDetailForm();
  }

  private resetContentDetailForm(): void {
    this.newContentDetail = {
      titre: '',
      contenusCles: [''],
      methodesPedagogiques: '',
      levels: [
        { levelNumber: 1, theorieContent: '', pratiqueContent: '', dureeTheorique: 0, dureePratique: 0, fileName: '', fileType: 'video', fileSize: 0, selectedLevels: [] }
      ]
    };
    this.selectedFiles = [];
    this.fileDescriptions = [];
    this.uploadedFiles = [];
  }

  onCloseContentDetailCreator(): void {
    this.showContentDetailCreator = false;
  }

  getSpecificObjectiveLabel(specificId: number): string {
    const objective = this.objectifsSpecifiques.find(s => s.id === specificId);
    return objective ? objective.label : 'N/A';
  }

  onClose(): void {
    this.close.emit();
  }
}
