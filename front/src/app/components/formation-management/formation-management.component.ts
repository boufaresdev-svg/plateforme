import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { QuillModule } from 'ngx-quill';

import { FormationService } from '../../services/formation_managment/formation.service';
import { FormateurService } from '../../services/formation_managment/Formateur.service';
import { PlanFormationService } from '../../services/formation_managment/PlanFormation.service';
import { CertificatService } from '../../services/formation_managment/Certificat.service';
import Swal from 'sweetalert2';
import { ContenuDetailleService, ContenuDetailleDto, ContentLevel } from '../../services/contenu-detaille.service';
import { environment } from '../../../environment/environement';

import { FormationsComponent } from './formations/formations.component';
import { FormateursComponent } from './formateurs/formateurs.component';
import { DomainesComponent } from './domaines/domaines.component';
import { ContenuDetailleComponent } from './contenu-detaille/contenu-detaille.component';
import { ApprenantManagementComponent } from './apprenants/apprenant-management.component';
import { ClasseManagementComponent } from './classes/classe-management.component';

@Component({
  selector: 'app-formation-management',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    QuillModule,
    FormationsComponent,
    FormateursComponent,
    DomainesComponent,
    ContenuDetailleComponent,
    ApprenantManagementComponent,
    ClasseManagementComponent
  ],
  templateUrl: './formation-management.component.html',
  styleUrls: ['./formation-management.component.css']
})
export class FormationManagementComponent implements OnInit {
  currentView:
    'dashboard'
    | 'formations'
    | 'formateurs'
    | 'domaines'
    | 'examens'
    | 'contenu'
    | 'apprenants'
    | 'classes' = 'dashboard';

  isLoadingStats = false;
  isLoadingContent = false;
  isLoadingFormateurs = false;

  stats = {
    totalFormations: 0,
    formationsEnCours: 0,
    formationsPlanifiees: 0,
    formationsTerminees: 0,
    formationsAnnulees: 0,
    formationsReportees: 0,
    totalParticipants: 0,
    participantsActifs: 0,
    certificationsDelivrees: 0,
    totalContenus: 0,
    totalFormateurs: 0,
    formationsByType: {} as { [key: string]: number },
    formationsByLevel: {} as { [key: string]: number },
    formationsThisMonth: 0,
    totalRevenue: 0,
    averageFormationPrice: 0,
    totalDomaines: 0,
    totalCategories: 0,
    totalSousCategories: 0,
    totalPlans: 0,
    activePlans: 0
  };

  formations: any[] = [];

  // Content Management
  showContentModal = false;
  showContentEditor = false;
  contentEditMode = false;
  contentList: Array<{
    id: number;
    title: string;
    description: string;
    type: string;
    size: string;
    uploadDate: string;
    formation: string;
    fileName: string;
    fileUrl?: string;
    videoUrl?: string;
    idFormation?: number;
    idJourFormation?: number;
    levels?: any[];
  }> = [];

  contentFormData: any = {
    title: '',
    description: '',
    type: undefined,
    idFormation: undefined,
    fileName: '',
    fileUrl: '',
    videoUrl: '',
    levels: [
      { level: 1, content: '', dureeTheorique: 0, dureePratique: 0, file: null, fileName: '', filePath: '', fileType: '', fileSize: 0 },
      { level: 2, content: '', dureeTheorique: 0, dureePratique: 0, file: null, fileName: '', filePath: '', fileType: '', fileSize: 0 },
      { level: 3, content: '', dureeTheorique: 0, dureePratique: 0, file: null, fileName: '', filePath: '', fileType: '', fileSize: 0 },
      { level: 4, content: '', dureeTheorique: 0, dureePratique: 0, file: null, fileName: '', filePath: '', fileType: '', fileSize: 0 },
      { level: 5, content: '', dureeTheorique: 0, dureePratique: 0, file: null, fileName: '', filePath: '', fileType: '', fileSize: 0 }
    ]
  };

  selectedLevel: number = 1;
  // Selected jour id - user needs to select a jour to load/manage content
  selectedJourId: number = 0;

  // Content Pagination
  contentCurrentPage = 0;
  contentPageSize = 10;
  contentTotalPages = 0;
  contentTotalItems = 0;

  // Content Details Modal
  showContentDetailsModal = false;
  selectedContentDetails: any = null;

  // Quill Editor Configuration
  quillModules = {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      ['blockquote', 'code-block'],
      [{ 'list': 'ordered'}, { 'list': 'bullet' }],
      [{ 'header': [1, 2, 3, false] }],
      [{ 'align': [] }],
      ['link', 'image'],
      ['clean']
    ]
  };

  constructor(
    private router: Router,
    private formationService: FormationService,
    private formateurService: FormateurService,
    private planFormationService: PlanFormationService,
    private certificatService: CertificatService,
    private contenuDetailleService: ContenuDetailleService
  ) {}

  ngOnInit() {
    this.loadStats();
    this.loadAllContent();
  }

  loadStats() {
    // Load comprehensive statistics from backend
    this.isLoadingStats = true;
    
    this.formationService.getStatistics().subscribe({
      next: (stats: any) => {
        this.stats = {
          totalFormations: stats.totalFormations || 0,
          formationsEnCours: stats.formationsEnCours || 0,
          formationsPlanifiees: stats.formationsPlanifiees || 0,
          formationsTerminees: stats.formationsTerminees || 0,
          formationsAnnulees: stats.formationsAnnulees || 0,
          formationsReportees: stats.formationsReportees || 0,
          totalParticipants: stats.totalParticipants || 0,
          participantsActifs: stats.participantsActifs || 0,
          certificationsDelivrees: stats.certificationsDelivrees || 0,
          totalContenus: stats.totalContenus || 0,
          totalFormateurs: stats.totalFormateurs || 0,
          formationsByType: stats.formationsByType || {},
          formationsByLevel: stats.formationsByLevel || {},
          formationsThisMonth: stats.formationsThisMonth || 0,
          totalRevenue: stats.totalRevenue || 0,
          averageFormationPrice: stats.averageFormationPrice || 0,
          totalDomaines: stats.totalDomaines || 0,
          totalCategories: stats.totalCategories || 0,
          totalSousCategories: stats.totalSousCategories || 0,
          totalPlans: stats.totalPlans || 0,
          activePlans: stats.activePlans || 0
        };
        console.log('📊 Statistics loaded:', this.stats);
        this.isLoadingStats = false;
      },
      error: (error: any) => {
        console.error('❌ Error loading statistics:', error);
        this.isLoadingStats = false;
        // Fallback to individual calls if statistics endpoint fails
        this.loadStatsFallback();
      }
    });

    // Also load formations for the list
    this.formationService.getAllFormations().subscribe({
      next: (data: any) => {
        this.formations = data;
      },
      error: (error: any) => console.error('Erreur:', error)
    });
  }

  loadStatsFallback() {
    // Fallback to old method if statistics endpoint fails
    this.formationService.getAllFormations().subscribe({
      next: (data: any) => {
        this.formations = data;
        this.stats.totalFormations = data.length;
      },
      error: (error: any) => console.error('Erreur:', error)
    });

    this.planFormationService.getAllPlans().subscribe({
      next: (data: any) => {
        this.stats.formationsEnCours = data.filter((p: any) => p.statusFormation === 'EN_COURS').length;
        this.stats.formationsPlanifiees = data.filter((p: any) => p.statusFormation === 'PLANIFIEE').length;
        this.stats.formationsTerminees = data.filter((p: any) => p.statusFormation === 'TERMINEE').length;
      },
      error: (error: any) => console.error('Erreur:', error)
    });

    this.certificatService.getAllCertificats().subscribe({
      next: (data: any) => {
        this.stats.certificationsDelivrees = data.length;
      },
      error: (error: any) => console.error('Erreur:', error)
    });
  }

  setView(view: string) {
    this.currentView = view as any;
    
    // Auto-load content when switching to content view
    if (view === 'contenu') {
      this.loadAllContent();
    }
  }

  goBackToMenu(): void {
    this.router.navigate(['/menu']);
  }

  navigateToApprenants(): void {
    this.currentView = 'apprenants';
  }

  // Helper methods for statistics calculations
  getTypePercentage(type: string): number {
    const count = this.stats.formationsByType[type] || 0;
    if (this.stats.totalFormations === 0) return 0;
    return (count / this.stats.totalFormations) * 100;
  }

  getLevelPercentage(level: string): number {
    const count = this.stats.formationsByLevel[level] || 0;
    if (this.stats.totalFormations === 0) return 0;
    return (count / this.stats.totalFormations) * 100;
  }

  // ==================== CONTENT MANAGEMENT ====================
  // Load all content from API (independent of jour)
  loadAllContent() {
    this.isLoadingContent = true;
    
    // Use paginated endpoint if available
    this.contenuDetailleService.getContenuDetaillePagedQ({
      page: this.contentCurrentPage,
      size: this.contentPageSize,
      sortBy: 'idContenuDetaille',
      sortDirection: 'ASC'
    }).subscribe({
      next: (data: any) => {
        const pagedContent = (data && data.content) ? data.content : [];

        // If the paginated API returns data, use it
        if (pagedContent.length > 0 || (data && data.total > 0)) {
          this.contentList = pagedContent.map((item: any) => this.mapContenuSummary(item));
          this.contentCurrentPage = data.page ?? 0;
          this.contentPageSize = data.size ?? this.contentPageSize;
          this.contentTotalItems = data.total ?? pagedContent.length;
          const derivedPages = (data.total !== undefined && data.size) ? Math.max(1, Math.ceil(data.total / data.size)) : 1;
          this.contentTotalPages = data.totalPages ?? derivedPages;
          this.isLoadingContent = false;
          return;
        }

        // Fallback: use full list endpoint then slice locally for current page
        this.contenuDetailleService.getAllContenuDetaille().subscribe({
          next: (allData: any[]) => {
            const mapped = (allData || []).map((item: any) => this.mapContenuFull(item));
            this.contentTotalItems = mapped.length;
            this.contentTotalPages = Math.max(1, Math.ceil(mapped.length / this.contentPageSize));
            const start = this.contentCurrentPage * this.contentPageSize;
            const end = start + this.contentPageSize;
            this.contentList = mapped.slice(start, end);
            this.isLoadingContent = false;
          },
          error: (fallbackError: any) => {
            console.error('Fallback content load failed:', fallbackError);
            this.contentList = [];
            this.isLoadingContent = false;
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Impossible de charger le contenu: ' + (fallbackError.message || 'Erreur inconnue')
            });
          }
        });
      },
      error: (error: any) => {
        console.error('Error loading content:', error);
        console.error('Error details:', error.error);
        this.contentList = [];
        this.isLoadingContent = false;
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de charger le contenu: ' + (error.message || 'Erreur inconnue')
        });
      }
    });
  }

  private mapContenuSummary(item: any) {
    return {
      id: item.idContenuDetaille,
      title: item.titre || 'Sans titre',
      description: item.description || item.methodesPedagogiques || item.methodePedagogique || 'Aucune description',
      tags: item.tags,
      type: 'document',
      size: this.calculateTotalSize(item),
      uploadDate: item.dateCreation || item.createdAt || item.uploadDate || new Date().toISOString().split('T')[0],
      formation: item.jourFormation ? `Jour ${item.jourFormation.idJourFormation}` : 'Non assigné',
      fileName: item.titre,
      fileUrl: '',
      videoUrl: '',
      idJourFormation: item.jourFormation?.idJourFormation,
      levels: item.levels || [],
      contenusCles: item.contenusCles || [],
      dureeTheorique: item.dureeTheorique,
      dureePratique: item.dureePratique
    };
  }

  private mapContenuFull(item: any) {
    return {
      id: item.idContenuDetaille,
      title: item.titre || 'Sans titre',
      description: item.methodesPedagogiques || 'Aucune description',
      type: 'document',
      size: this.calculateTotalSize(item),
      uploadDate: new Date().toISOString().split('T')[0],
      formation: item.jourFormation ? `Jour ${item.jourFormation.idJourFormation}` : 'Non assigné',
      fileName: item.titre,
      fileUrl: '',
      videoUrl: '',
      idJourFormation: item.jourFormation?.idJourFormation,
      levels: item.levels || [],
      contenusCles: item.contenusCles || [],
      dureeTheorique: item.dureeTheorique,
      dureePratique: item.dureePratique
    };
  }

  calculateTotalSize(item: any): string {
    let totalSize = 0;
    if (item.levels) {
      item.levels.forEach((level: any) => {
        if (level.fileSize) totalSize += level.fileSize;
      });
    }
    if (item.files) {
      item.files.forEach((file: any) => {
        if (file.fileSize) totalSize += file.fileSize;
      });
    }
    return totalSize > 0 ? this.formatFileSize(totalSize) : 'N/A';
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  }

  // Load content by jour (day) from API
  loadContentByJour(idJour: number) {
    if (!idJour || idJour <= 0) {
      Swal.fire({
        icon: 'warning',
        title: 'ID Jour requis',
        text: 'Veuillez entrer un ID de jour valide'
      });
      return;
    }

    this.contenuDetailleService.getAllByJour(idJour).subscribe({
      next: (data: any[]) => {
        this.contentList = data.map((item: any) => ({
          id: item.idContenuDetaille,
          title: item.titre,
          description: item.methodesPedagogiques || '',
          type: this.determineContentType(item),
          size: 'N/A',
          uploadDate: new Date().toISOString().split('T')[0],
          formation: 'Formation',
          fileName: item.titre,
          fileUrl: '',
          videoUrl: '',
          idJourFormation: idJour,
          levels: item.levels || []
        }));
        
        if (data.length === 0) {
          Swal.fire({
            icon: 'info',
            title: 'Aucun contenu',
            text: 'Aucun contenu trouvé pour ce jour de formation',
            timer: 2000,
            showConfirmButton: false
          });
        }
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement du contenu:', error);
        this.contentList = [];
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de charger le contenu: ' + (error.message || 'Erreur inconnue')
        });
      }
    });
  }

  // Determine content type based on available data
  determineContentType(item: any): string {
    if (item.levels && item.levels.length > 0) return 'document';
    return 'document';
  }

  openContentModal() {
    this.showContentEditor = true;
    this.contentEditMode = false;
    this.resetContentForm();
  }

  openContentEditor() {
    this.showContentEditor = true;
    this.contentEditMode = false;
    this.resetContentForm();
  }

  closeContentModal() {
    this.showContentModal = false;
    this.showContentEditor = false;
    this.resetContentForm();
  }

  closeContentEditor() {
    this.showContentEditor = false;
    this.resetContentForm();
  }

  onContentSaved() {
    this.showContentEditor = false;
    this.loadAllContent();
  }

  // View content details in modal
  viewContentDetails(content: any) {
    const id = content.idContenuDetaille || content.id;
    if (!id) { return; }

    this.contenuDetailleService.getContenuDetailleById(id).subscribe({
      next: (full) => {
        const detail = Array.isArray(full) ? full[0] : full;
        if (!detail) {
          Swal.fire({
            icon: 'warning',
            title: 'Aucun détail',
            text: "Aucune donnée retournée pour ce contenu"
          });
          return;
        }
        this.selectedContentDetails = this.mapContenuDetail(detail);
        this.showContentDetailsModal = true;
      },
      error: (err) => {
        console.error('Erreur chargement détail contenu:', err);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: "Impossible de charger le détail du contenu"
        });
      }
    });
  }

  closeContentDetailsModal() {
    this.showContentDetailsModal = false;
    this.selectedContentDetails = null;
  }

  editFromDetailsModal() {
    if (this.selectedContentDetails) {
      this.closeContentDetailsModal();
      this.editContentItem(this.selectedContentDetails);
    }
  }

  getLevelName(levelNumber: number): string {
    const levels = ['Débutant', 'Intermédiaire', 'Avancé'];
    return levels[levelNumber - 1] || `Niveau ${levelNumber}`;
  }

  private mapContenuDetail(item: any) {
    console.log('📦 Raw item from backend:', JSON.stringify(item, null, 2));
    
    const levels = (item.levels || []).map((lvl: any) => ({
      levelNumber: lvl.levelNumber,
      level: lvl.level,
      content: lvl.content || lvl.theorieContent || '',
      theorieContent: lvl.theorieContent || lvl.content || '',
      dureeTheorique: lvl.dureeTheorique || 0,
      dureePratique: lvl.dureePratique || 0,
      files: lvl.files || []
    }));
    
    console.log('📋 Mapped levels:', JSON.stringify(levels, null, 2));

    return {
      id: item.idContenuDetaille || item.id,
      title: item.titre || item.title,
      description: item.description || item.methodesPedagogiques || item.methodePedagogique || 'Aucune description',
      tags: item.tags,
      uploadDate: item.dateCreation || item.createdAt || item.uploadDate,
      idJourFormation: item.idJourFormation || item.jourFormation?.idJourFormation,
      levels
    };
  }

  previewFile(filePath: string) {
    if (!filePath) return;
    const baseUrl = environment.production ? 'https://api.platform.sms2i.com.tn/api' : 'http://localhost:8080/api';
    const url = `${baseUrl}/contenus-detailles/files/${filePath}`;
    window.open(url, '_blank');
  }

  downloadFile(filePath: string, fileName: string) {
    if (!filePath) return;
    const baseUrl = environment.production ? 'https://api.platform.sms2i.com.tn/api' : 'http://localhost:8080/api';
    const url = `${baseUrl}/contenus-detailles/files/${filePath}`;
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    link.click();
  }

  editContentItem(content: any) {
    const id = content.id || content.idContenuDetaille;
    if (!id) {
      console.error('No content ID found for editing');
      return;
    }

    // Fetch fresh data from backend (same as viewContentDetails)
    this.contenuDetailleService.getContenuDetailleById(id).subscribe({
      next: (full) => {
        const detail = Array.isArray(full) ? full[0] : full;
        if (!detail) {
          Swal.fire({
            icon: 'warning',
            title: 'Aucun détail',
            text: "Aucune donnée retournée pour ce contenu"
          });
          return;
        }
        
        // Map the fresh data and open editor
        const mappedContent = this.mapContenuDetail(detail);
        this.contentEditMode = true;
        this.showContentEditor = true;
        
        this.contentFormData = {
          id: mappedContent.id,
          title: mappedContent.title,
          description: mappedContent.description,
          tags: mappedContent.tags,
          idJourFormation: mappedContent.idJourFormation,
          levels: mappedContent.levels || []
        };
      },
      error: (err) => {
        console.error('Error loading content for edit:', err);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: "Impossible de charger le contenu pour modification"
        });
      }
    });
  }

  resetContentForm() {
    this.contentFormData = {
      id: undefined,
      title: '',
      description: '',
      type: 'document',
      idFormation: undefined,
      fileName: '',
      fileUrl: '',
      videoUrl: '',
      levels: [
        { level: 1, content: '', file: null, fileName: '', filePath: '', fileType: '', fileSize: 0 },
        { level: 2, content: '', file: null, fileName: '', filePath: '', fileType: '', fileSize: 0 },
        { level: 3, content: '', file: null, fileName: '', filePath: '', fileType: '', fileSize: 0 },
        { level: 4, content: '', file: null, fileName: '', filePath: '', fileType: '', fileSize: 0 },
        { level: 5, content: '', file: null, fileName: '', filePath: '', fileType: '', fileSize: 0 }
      ]
    };
    this.selectedLevel = 1;
  }

  selectLevel(levelNumber: number) {
    this.selectedLevel = levelNumber;
  }

  getCurrentLevelContent(): string {
    const level = this.contentFormData.levels?.find((l: any) => l.level === this.selectedLevel);
    return level?.content || '';
  }

  setCurrentLevelContent(content: string) {
    const level = this.contentFormData.levels?.find((l: any) => l.level === this.selectedLevel);
    if (level) level.content = content;
  }

  // File upload for specific level
  onLevelFileSelected(event: any, levelNumber: number) {
    const file = event.target.files[0];
    if (file) {
      const level = this.contentFormData.levels?.find((l: any) => l.level === levelNumber);
      if (level) {
        level.file = file;
        level.fileName = file.name;
        level.fileType = file.type;
        level.fileSize = file.size;
        
        Swal.fire({
          icon: 'success',
          title: 'Fichier sélectionné',
          text: `Niveau ${levelNumber}: ${file.name} (${this.formatFileSize(file.size)})`,
          timer: 2000,
          showConfirmButton: false
        });
      }
    }
  }

  // Remove file from specific level
  removeLevelFile(levelNumber: number) {
    const level = this.contentFormData.levels?.find((l: any) => l.level === levelNumber);
    if (level) {
      level.file = null;
      level.fileName = '';
      level.filePath = '';
      level.fileType = '';
      level.fileSize = 0;
    }
  }

  // Upload files after content is created/updated
  async uploadLevelFiles(contentId: number) {
    const uploadPromises: Promise<any>[] = [];
    
    for (const level of this.contentFormData.levels) {
      if (level.file) {
        const uploadPromise = this.contenuDetailleService
          .uploadLevelFiles(contentId, level.level, [level.file])
          .toPromise()
          .then((response: any) => {
            return response;
          })
          .catch((error: any) => {
            console.error(`Error uploading file for level ${level.level}:`, error);
            throw error;
          });
        
        uploadPromises.push(uploadPromise);
      }
    }
    
    if (uploadPromises.length > 0) {
      try {
        await Promise.all(uploadPromises);
      } catch (error) {
        console.error('Error uploading some files:', error);
        throw error;
      }
    }
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.contentFormData.fileName = file.name;
      this.contentFormData.fileUrl = `/uploads/content/${file.name}`;
      
      // Show success message
      Swal.fire({
        icon: 'success',
        title: 'Fichier sélectionné',
        text: `${file.name} (${this.formatFileSize(file.size)})`,
        timer: 2000,
        showConfirmButton: false
      });
    }
  }

  saveContent() {
    if (!this.contentFormData.title) {
      Swal.fire({
        icon: 'warning',
        title: 'Champs requis',
        text: 'Veuillez remplir le titre'
      });
      return;
    }

    const payload: ContenuDetailleDto = {
      titre: this.contentFormData.title,
      contenusCles: [],
      methodesPedagogiques: this.contentFormData.description || '',
      dureeTheorique: undefined,
      dureePratique: undefined,
      levels: (this.contentFormData.levels || []).map((lvl: any) => ({
        levelNumber: lvl.level,
        theorieContent: lvl.content || '',
        pratiqueContent: '',
        dureeTheorique: lvl.dureeTheorique || null,
        dureePratique: lvl.dureePratique || null
      } as ContentLevel)),
      idJourFormation: this.selectedJourId > 0 ? this.selectedJourId : undefined
    };
    
    
    if (this.contentEditMode && this.contentFormData.id) {
      // Update existing content
      this.contenuDetailleService.updateContenuDetaille(this.contentFormData.id, payload).subscribe({
        next: async (response) => {
          
          // Upload files if any
          try {
            await this.uploadLevelFiles(this.contentFormData.id);
            Swal.fire({
              icon: 'success',
              title: 'Contenu modifié',
              text: 'Le contenu et les fichiers ont été modifiés avec succès',
              timer: 2000,
              showConfirmButton: false
            });
          } catch (fileError) {
            Swal.fire({
              icon: 'warning',
              title: 'Contenu modifié',
              text: 'Le contenu a été modifié mais certains fichiers n\'ont pas pu être uploadés',
              timer: 3000,
              showConfirmButton: false
            });
          }
          
          this.loadAllContent();
          this.closeContentModal();
        },
        error: (error: any) => {
          console.error('Erreur lors de la modification:', error);
          console.error('Error details:', error.error);
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: 'Impossible de modifier le contenu: ' + (error.error?.message || error.message || 'Erreur inconnue')
          });
        }
      });
    } else {
      // Add new content
      this.contenuDetailleService.addContenuDetaille(payload).subscribe({
        next: async (response: any) => {
          
          // Get the created content ID
          const contentId = response[0]?.idContenuDetaille || response?.idContenuDetaille;
          
          if (contentId) {
            // Upload files if any
            try {
              await this.uploadLevelFiles(contentId);
              Swal.fire({
                icon: 'success',
                title: 'Contenu ajouté',
                text: 'Le contenu et les fichiers ont été ajoutés avec succès',
                timer: 2000,
                showConfirmButton: false
              });
            } catch (fileError) {
              Swal.fire({
                icon: 'warning',
                title: 'Contenu ajouté',
                text: 'Le contenu a été ajouté mais certains fichiers n\'ont pas pu être uploadés',
                timer: 3000,
                showConfirmButton: false
              });
            }
          } else {
            Swal.fire({
              icon: 'success',
              title: 'Contenu ajouté',
              text: 'Le contenu a été ajouté avec succès',
              timer: 2000,
              showConfirmButton: false
            });
          }
          
          this.loadAllContent();
          this.closeContentModal();
        },
        error: (error: any) => {
          console.error('Erreur lors de l\'ajout:', error);
          console.error('Error details:', error.error);
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: 'Impossible d\'ajouter le contenu: ' + (error.error?.message || error.message || 'Erreur inconnue')
          });
        }
      });
    }
  }

  downloadContent(content: any) {
    Swal.fire({
      icon: 'info',
      title: 'Téléchargement',
      text: `Téléchargement de ${content.fileName}...`,
      timer: 2000,
      showConfirmButton: false
    });
  }

  previewContent(content: any) {
    const baseUrl = environment.production ? 'https://api.platform.sms2i.com.tn/api' : 'http://localhost:8080/api';
    let previewHtml = `
      <div style="text-align: left; max-height: 70vh; overflow-y: auto; padding: 20px;">
        <!-- Header Section -->
        <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 25px; border-radius: 12px; margin-bottom: 25px; color: white; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
          <h2 style="margin: 0 0 10px 0; font-size: 28px; font-weight: 700;">📄 ${content.title}</h2>
          <div style="display: flex; gap: 15px; flex-wrap: wrap; margin-top: 15px;">
            <span style="background: rgba(255,255,255,0.2); padding: 6px 12px; border-radius: 20px; font-size: 14px;">
              📊 ${content.type}
            </span>
            <span style="background: rgba(255,255,255,0.2); padding: 6px 12px; border-radius: 20px; font-size: 14px;">
              🎓 ${content.formation}
            </span>
          </div>
        </div>

        <!-- Description Section -->
        ${content.description ? `
        <div style="background: #f8fafc; padding: 20px; border-radius: 10px; margin-bottom: 25px; border-left: 4px solid #667eea;">
          <h4 style="margin: 0 0 10px 0; color: #1e293b; font-size: 16px;">📝 Description</h4>
          <div style="color: #475569; line-height: 1.6;">${content.description}</div>
        </div>
        ` : ''}

        <!-- Levels Section -->
        <div style="margin-top: 25px;">
          <h3 style="color: #1e293b; margin-bottom: 20px; font-size: 22px; font-weight: 600; border-bottom: 3px solid #667eea; padding-bottom: 10px;">
            📚 Contenu par Niveau
          </h3>
    `;

    // Display all 5 levels with their content
    if (content.levels && content.levels.length > 0) {
      for (let i = 0; i < 5; i++) {
        const level = content.levels[i];
        const levelNumber = i + 1;
        
        if (level && (level.theorieContent || level.pratiqueContent || level.fileName || level.dureeTheorique || level.dureePratique)) {
          const hasContent = level.theorieContent || level.pratiqueContent;
          const hasDuration = level.dureeTheorique || level.dureePratique;
          const hasFile = level.fileName;
          
          previewHtml += `
            <div style="background: linear-gradient(to right, #f8fafc, #ffffff); padding: 20px; margin-bottom: 20px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); border-left: 5px solid #667eea;">
              <!-- Level Header -->
              <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 15px;">
                <h4 style="margin: 0; color: #1e293b; font-size: 18px; font-weight: 600;">
                  <span style="background: #667eea; color: white; padding: 4px 12px; border-radius: 8px; margin-right: 10px;">
                    Niveau ${levelNumber}
                  </span>
                </h4>
                ${hasDuration ? `
                  <div style="display: flex; gap: 12px;">
                    ${level.dureeTheorique ? `
                      <span style="background: #10b981; color: white; padding: 6px 12px; border-radius: 20px; font-size: 13px; font-weight: 500;">
                        ⏱️ Théorie: ${level.dureeTheorique}h
                      </span>
                    ` : ''}
                    ${level.dureePratique ? `
                      <span style="background: #f59e0b; color: white; padding: 6px 12px; border-radius: 20px; font-size: 13px; font-weight: 500;">
                        🛠️ Pratique: ${level.dureePratique}h
                      </span>
                    ` : ''}
                  </div>
                ` : ''}
              </div>

              <!-- Theory Content -->
              ${level.theorieContent ? `
                <div style="margin-bottom: 15px;">
                  <div style="background: #ecfdf5; padding: 12px 15px; border-radius: 8px; margin-bottom: 8px;">
                    <strong style="color: #059669; font-size: 15px;">📖 Contenu Théorique</strong>
                  </div>
                  <div style="background: white; padding: 15px; border-radius: 8px; border: 1px solid #d1fae5; line-height: 1.6; color: #374151;">
                    ${level.theorieContent}
                  </div>
                </div>
              ` : ''}

              <!-- Practice Content -->
              ${level.pratiqueContent ? `
                <div style="margin-bottom: 15px;">
                  <div style="background: #fef3c7; padding: 12px 15px; border-radius: 8px; margin-bottom: 8px;">
                    <strong style="color: #d97706; font-size: 15px;">⚡ Contenu Pratique</strong>
                  </div>
                  <div style="background: white; padding: 15px; border-radius: 8px; border: 1px solid #fde68a; line-height: 1.6; color: #374151;">
                    ${level.pratiqueContent}
                  </div>
                </div>
              ` : ''}

              <!-- Attached File -->
              ${level.fileName ? `
                <div style="background: linear-gradient(135deg, #a855f7 0%, #7c3aed 100%); padding: 15px; border-radius: 10px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 6px rgba(124, 58, 237, 0.2);">
                  <div style="color: white;">
                    <div style="font-weight: 600; margin-bottom: 4px;">📎 Fichier Attaché</div>
                    <div style="font-size: 14px; opacity: 0.9;">
                      📄 ${level.fileName}
                      ${level.fileSize ? `<span style="margin-left: 8px; opacity: 0.8;">(${this.formatFileSize(level.fileSize)})</span>` : ''}
                    </div>
                  </div>
                  <a href="${baseUrl}/contenus-detailles/files/${level.filePath}" 
                     target="_blank" 
                     style="background: white; color: #7c3aed; padding: 10px 20px; border-radius: 8px; text-decoration: none; font-weight: 600; font-size: 14px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); transition: all 0.3s;">
                    ⬇️ Télécharger
                  </a>
                </div>
              ` : ''}
            </div>
          `;
        } else {
          previewHtml += `
            <div style="background: #f8fafc; padding: 15px; margin-bottom: 15px; border-radius: 10px; border-left: 4px solid #cbd5e0; opacity: 0.7;">
              <div style="color: #64748b; font-style: italic;">
                <strong>Niveau ${levelNumber}:</strong> Aucun contenu disponible
              </div>
            </div>
          `;
        }
      }
    } else {
      previewHtml += '<div style="text-align: center; padding: 40px; color: #94a3b8;"><p style="font-size: 18px;">📭 Aucun contenu de cours disponible.</p></div>';
    }

    previewHtml += `
        </div>
      </div>
    `;

    Swal.fire({
      title: '',
      html: previewHtml,
      width: '1100px',
      showCloseButton: true,
      confirmButtonText: '✕ Fermer',
      confirmButtonColor: '#667eea',
      customClass: {
        popup: 'preview-content-modal',
        confirmButton: 'custom-confirm-button'
      },
      didOpen: () => {
        // Add custom styles for the modal
        const style = document.createElement('style');
        style.textContent = `
          .preview-content-modal {
            border-radius: 16px !important;
            padding: 0 !important;
          }
          .custom-confirm-button {
            border-radius: 8px !important;
            padding: 12px 30px !important;
            font-weight: 600 !important;
            font-size: 15px !important;
          }
        `;
        document.head.appendChild(style);
      }
    });
  }

  deleteContent(content: any) {
    Swal.fire({
      title: 'Confirmer la suppression',
      text: `Voulez-vous vraiment supprimer "${content.title}" ?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Oui, supprimer',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.contenuDetailleService.deleteContenuDetaille(content.id).subscribe({
          next: () => {
            Swal.fire({
              icon: 'success',
              title: 'Supprimé',
              text: 'Le contenu a été supprimé',
              timer: 2000,
              showConfirmButton: false
            });
            this.loadAllContent();
          },
          error: (error: any) => {
            console.error('Erreur lors de la suppression:', error);
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Impossible de supprimer le contenu'
            });
          }
        });
      }
    });
  }

  editContent(content: any) {
    const id = content.idContenuDetaille || content.id;
    if (!id) {
      console.error('No content ID found for editing');
      return;
    }

    // Fetch fresh data from backend (same as viewContentDetails)
    this.contenuDetailleService.getContenuDetailleById(id).subscribe({
      next: (full) => {
        const detail = Array.isArray(full) ? full[0] : full;
        if (!detail) {
          Swal.fire({
            icon: 'warning',
            title: 'Aucun détail',
            text: "Aucune donnée retournée pour ce contenu"
          });
          return;
        }
        
        // Map the fresh data and open editor
        const mappedContent = this.mapContenuDetail(detail);
        this.contentEditMode = true;
        this.showContentEditor = true;
        
        this.contentFormData = {
          id: mappedContent.id,
          title: mappedContent.title,
          description: mappedContent.description,
          tags: mappedContent.tags,
          idJourFormation: mappedContent.idJourFormation,
          levels: mappedContent.levels || []
        };
      },
      error: (err) => {
        console.error('Error loading content for edit:', err);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: "Impossible de charger le contenu pour modification"
        });
      }
    });
  }

  getFileIcon(mimeType: string): string {
    if (mimeType.includes('video')) return '🎥';
    if (mimeType.includes('image')) return '🖼️';
    if (mimeType.includes('pdf')) return '📕';
    if (mimeType.includes('word') || mimeType.includes('document')) return '📘';
    if (mimeType.includes('excel') || mimeType.includes('spreadsheet')) return '📊';
    if (mimeType.includes('powerpoint') || mimeType.includes('presentation')) return '📙';
    return '📄';
  }

  getTagsArray(tagsString: string): string[] {
    if (!tagsString) return [];
    return tagsString.split(';').map(t => t.trim()).filter(t => t);
  }

  // ==================== CONTENT PAGINATION ====================
  contentGoToPage(page: number) {
    if (page >= 0 && page < this.contentTotalPages) {
      this.contentCurrentPage = page;
      this.loadAllContent();
    }
  }

  contentNextPage() {
    if (this.contentCurrentPage < this.contentTotalPages - 1) {
      this.contentGoToPage(this.contentCurrentPage + 1);
    }
  }

  contentPreviousPage() {
    if (this.contentCurrentPage > 0) {
      this.contentGoToPage(this.contentCurrentPage - 1);
    }
  }

  downloadLevelFile(filePath: string, fileName: string) {
    const downloadUrl = `${this.contenuDetailleService['baseUrl'].replace('/contenus-detailles', '')}/contenus-detailles/files/${filePath}`;
    window.open(downloadUrl, '_blank');
  }

  // Helper method for dashboard to get formation type icon
  getTypeIcon(type: string): string {
    const typeMap: { [key: string]: string } = {
      'En_Ligne': '💻',
      'Presentiel': '👥',
      'Hybride': '🔄'
    };
    return typeMap[type] || '📚';
  }
}
