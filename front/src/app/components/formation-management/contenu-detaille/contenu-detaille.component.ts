import { Component, OnInit, OnChanges, SimpleChanges, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { QuillModule } from 'ngx-quill';
import Swal from 'sweetalert2';
import { ContenuDetailleService, ContenuDetailleDto, ContentLevel } from '../../../services/contenu-detaille.service';
import { environment } from '../../../../environment/environement';

interface LevelFile {
  fileName: string;
  filePath: string;
  fileType: string;
  fileSize: number;
  uploadDate?: string;
  description?: string;
}

interface LevelData {
  level: string;
  levelNumber: number;
  content: string;
  dureeTheorique: number;
  dureePratique: number;
  // Files to be uploaded (new selections)
  newFiles: File[];
  // Already uploaded files (from backend)
  uploadedFiles: LevelFile[];
}

@Component({
  selector: 'app-contenu-detaille',
  standalone: true,
  imports: [CommonModule, FormsModule, QuillModule],
  templateUrl: './contenu-detaille.component.html',
  styleUrls: ['./contenu-detaille.component.css']
})
export class ContenuDetailleComponent implements OnInit, OnChanges {
  @Input() contentData: any = null;
  @Input() editMode: boolean = false;
  @Output() onClose = new EventEmitter<void>();
  @Output() onSaved = new EventEmitter<void>();

  // Form data
  titre: string = '';
  type: string = 'document';
  idJourFormation: number | undefined;
  description: string = '';
  tags: string = '';
  tagsList: string[] = [];
  selectedLevel: string = 'debutant';
  isSaving: boolean = false;
  isLoading: boolean = false;
  isUploading: boolean = false;
  
  // Flag to track if component was accessed via route (for PDF link navigation)
  private isRoutedAccess: boolean = false;
  
  // Skill levels configuration
  skillLevels = [
    { id: 'debutant', label: 'Débutant', levelNumber: 1, icon: '🌱' },
    { id: 'intermediaire', label: 'Intermédiaire', levelNumber: 2, icon: '📈' },
    { id: 'avance', label: 'Avancé', levelNumber: 3, icon: '🚀' }
  ];
  
  levels: LevelData[] = [
    { level: 'debutant', levelNumber: 1, content: '', dureeTheorique: 0, dureePratique: 0, newFiles: [], uploadedFiles: [] },
    { level: 'intermediaire', levelNumber: 2, content: '', dureeTheorique: 0, dureePratique: 0, newFiles: [], uploadedFiles: [] },
    { level: 'avance', levelNumber: 3, content: '', dureeTheorique: 0, dureePratique: 0, newFiles: [], uploadedFiles: [] }
  ];

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
    private route: ActivatedRoute,
    private contenuDetailleService: ContenuDetailleService
  ) {}

  ngOnChanges(changes: SimpleChanges) {
    // Detect when contentData input changes and reload the form
    if (changes['contentData'] && changes['contentData'].currentValue && this.editMode) {
      console.log('🔄 contentData input changed, reloading form data');
      this.resetLevels();
      this.loadContentData();
    }
  }

  private resetLevels() {
    // Reset all levels to default state before loading new data
    this.levels = [
      { level: 'debutant', levelNumber: 1, content: '', dureeTheorique: 0, dureePratique: 0, newFiles: [], uploadedFiles: [] },
      { level: 'intermediaire', levelNumber: 2, content: '', dureeTheorique: 0, dureePratique: 0, newFiles: [], uploadedFiles: [] },
      { level: 'avance', levelNumber: 3, content: '', dureeTheorique: 0, dureePratique: 0, newFiles: [], uploadedFiles: [] }
    ];
  }

  ngOnInit() {
    // Check for route param ID (from PDF link navigation)
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isRoutedAccess = true;
        const contentId = +params['id'];
        console.log('📄 Loading content via route, ID:', contentId);
        this.loadContentById(contentId);
      } else if (this.contentData && this.editMode) {
        this.loadContentData();
      }
    });
  }

  // Load content by ID (for route-based navigation from PDF links)
  private loadContentById(id: number) {
    this.isLoading = true;
    console.log('🔄 Starting API call to load content ID:', id);
    
    this.contenuDetailleService.getById(id).subscribe({
      next: (response) => {
        console.log('✅ Content loaded successfully:', JSON.stringify(response, null, 2));
        this.isLoading = false;
        
        // Handle both array and object responses
        const content = Array.isArray(response) ? response[0] : response;
        
        if (!content) {
          console.error('❌ No content found in response');
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: 'Contenu non trouvé.'
          }).then(() => {
            this.router.navigate(['/formation']);
          });
          return;
        }
        
        this.contentData = content;
        this.editMode = true;
        this.loadContentData();
      },
      error: (err) => {
        console.error('❌ Error loading content:', err);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de charger le contenu détaillé. Vérifiez que vous êtes connecté.'
        }).then(() => {
          this.router.navigate(['/formation']);
        });
      }
    });
  }

  loadContentData() {
    console.log('📋 Loading content data into form:', JSON.stringify(this.contentData, null, 2));
    
    this.titre = this.contentData.title || this.contentData.titre || '';
    this.type = this.contentData.type || 'document';
    this.description = this.contentData.description || this.contentData.methodesPedagogiques || '';
    this.tags = '';
    this.tagsList = this.contentData.tags ? this.contentData.tags.split(';').filter((t: string) => t.trim()) : [];
    this.idJourFormation = this.contentData.idJourFormation;
    
    // Check if levels exist in the response
    if (this.contentData.levels && Array.isArray(this.contentData.levels)) {
      console.log('📚 Processing levels from backend:', JSON.stringify(this.contentData.levels, null, 2));
      
      this.levels.forEach(localLevel => {
        // Find matching level in backend data
        const backendLevel = this.contentData.levels.find((l: any) => {
            // value which might be number (1) or string ("DEBUTANT")
            const rawLevel = l.level || l.levelNumber || l.level_number;
            
            console.log(`🔍 Checking level match: rawLevel=${rawLevel}, localLevel.levelNumber=${localLevel.levelNumber}`);
            
            // Direct number match
            if (rawLevel == localLevel.levelNumber) return true;
            
            // String mapping
            if (typeof rawLevel === 'string') {
                const upper = rawLevel.toUpperCase();
                if (localLevel.levelNumber === 1 && (upper.includes('DEBU') || upper.includes('BEGIN'))) return true;
                if (localLevel.levelNumber === 2 && (upper.includes('INTER') || upper.includes('MOYEN'))) return true;
                if (localLevel.levelNumber === 3 && (upper.includes('AVAN') || upper.includes('ADVANC'))) return true;
            }
            
            return false;
        });

        if (backendLevel) {
            console.log(`✅ Found backend data for ${localLevel.level} (Level ${localLevel.levelNumber}):`, JSON.stringify(backendLevel, null, 2));
            
            // Map Content
            localLevel.content = backendLevel.content || backendLevel.theorieContent || backendLevel.theorie_content || '';

            // Map Hours - log the values being mapped
            console.log(`⏱️ Hours for ${localLevel.level}: dureeTheorique=${backendLevel.dureeTheorique}, dureePratique=${backendLevel.dureePratique}`);
            localLevel.dureeTheorique = Number(backendLevel.dureeTheorique) || 0;
            localLevel.dureePratique = Number(backendLevel.dureePratique) || 0;
            console.log(`⏱️ Mapped hours: dureeTheorique=${localLevel.dureeTheorique}, dureePratique=${localLevel.dureePratique}`);
            
            // Map Files
            let files = [];
            if (Array.isArray(backendLevel.files)) {
                files = backendLevel.files;
            } else if (Array.isArray(backendLevel.levelFiles)) {
                files = backendLevel.levelFiles;
            } else if (Array.isArray(backendLevel.uploadedFiles)) {
                files = backendLevel.uploadedFiles;
            }
            
            // Ensure files have necessary properties for the display template
            localLevel.uploadedFiles = files.map((f: any) => ({
                fileName: f.fileName || f.name || 'document',
                filePath: f.filePath || f.path || f.url || '',
                fileType: f.fileType || f.type || 'application/octet-stream',
                fileSize: f.fileSize || f.size || 0,
                // ensure we keep original object properties just in case
                ...f
            }));
            
            console.log(`📁 Files for ${localLevel.level}: ${files.length} found, ${localLevel.uploadedFiles.length} mapped.`);
            
        } else {
             console.log(`⚠️ No backend data for ${localLevel.level} (Level ${localLevel.levelNumber}) using rawLevel search.`);
        }
      });
      
      console.log('✅ Final Levels State after loading:', this.levels);
    } else {
        console.warn('⚠️ No levels array found in contentData', this.contentData);
    }
  }

  getLevelIdFromNumber(levelNumber: number): string {
    const level = this.skillLevels.find(l => l.levelNumber === levelNumber);
    return level?.id || 'debutant';
  }

  getLevelLabel(levelId: string): string {
    const level = this.skillLevels.find(l => l.id === levelId);
    return level?.label || 'Débutant';
  }

  getLevelById(levelId: string): LevelData | undefined {
    return this.levels.find(l => l.level === levelId);
  }

  getTotalHours(levelId: string): number {
    const level = this.getLevelById(levelId);
    if (!level) return 0;
    return (level.dureeTheorique || 0) + (level.dureePratique || 0);
  }

  getCurrentLevel(): LevelData {
    return this.levels.find(l => l.level === this.selectedLevel) || this.levels[0];
  }

  hasLevelContent(levelId: string): boolean {
    const level = this.levels.find(l => l.level === levelId);
    return !!(level && (level.content || level.uploadedFiles.length > 0 || level.newFiles.length > 0));
  }

  selectLevel(levelId: string) {
    this.selectedLevel = levelId;
  }

  getCurrentLevelContent(): string {
    const level = this.levels.find(l => l.level === this.selectedLevel);
    return level?.content || '';
  }

  setCurrentLevelContent(content: string) {
    const level = this.levels.find(l => l.level === this.selectedLevel);
    if (level) level.content = content;
  }

  addTagOnEnter(event: KeyboardEvent) {
    event.preventDefault();
    const tag = this.tags.trim();
    if (tag && !this.tagsList.includes(tag)) {
      this.tagsList.push(tag);
      this.tags = '';
    }
  }

  removeTag(tag: string) {
    this.tagsList = this.tagsList.filter(t => t !== tag);
  }

  getTagsString(): string {
    return this.tagsList.join(';');
  }

  onLevelFileSelected(event: any) {
    const files = Array.from(event.target.files) as File[];
    if (files.length > 0) {
      const level = this.levels.find(l => l.level === this.selectedLevel);
      if (level) {
        // Add new files to the list
        level.newFiles = [...level.newFiles, ...files];
        
        Swal.fire({
          icon: 'success',
          title: 'Fichiers ajoutés',
          text: `${files.length} fichier(s) ajouté(s) au niveau ${this.getLevelLabel(this.selectedLevel)}`,
          timer: 2000,
          showConfirmButton: false
        });
        
        // Reset input
        event.target.value = '';
      }
    }
  }

  removeNewFile(levelId: string, index: number) {
    const level = this.levels.find(l => l.level === levelId);
    if (level) {
      level.newFiles.splice(index, 1);
    }
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

  deleteExistingFile(levelId: string, filePath: string) {
    Swal.fire({
      title: 'Confirmer la suppression',
      text: 'Voulez-vous vraiment supprimer ce fichier ?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Oui, supprimer',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        const level = this.levels.find(l => l.level === levelId);
        if (level && this.contentData?.id) {
          // Call backend to delete the file
          this.isLoading = true;
          
          this.contenuDetailleService.deleteLevelFile(this.contentData.id, level.levelNumber, filePath).subscribe({
            next: () => {
              // Remove from uploadedFiles array
              level.uploadedFiles = level.uploadedFiles.filter(f => f.filePath !== filePath);
              
              this.isLoading = false;
              Swal.fire({
                icon: 'success',
                title: 'Fichier supprimé',
                text: 'Le fichier a été supprimé avec succès',
                timer: 2000,
                showConfirmButton: false
              });
            },
            error: (error) => {
              console.error('Error deleting file:', error);
              this.isLoading = false;
              Swal.fire({
                icon: 'error',
                title: 'Erreur',
                text: 'Impossible de supprimer le fichier: ' + (error.message || 'Erreur inconnue')
              });
            }
          });
        }
      }
    });
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
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

  async uploadLevelFiles(contentId: number) {
    this.isUploading = true;
    const uploadPromises: Promise<any>[] = [];
    
    for (const level of this.levels) {
      // Upload new files for this level
      if (level.newFiles.length > 0) {
        const uploadPromise = this.contenuDetailleService
          .uploadLevelFiles(contentId, level.levelNumber, level.newFiles)
          .toPromise()
          .then(response => {
            
            // Add uploaded files to the uploadedFiles array
            if (response.files && Array.isArray(response.files)) {
              level.uploadedFiles = [...level.uploadedFiles, ...response.files];
            }
            
            // Clear newFiles after successful upload
            level.newFiles = [];
            
            return response;
          })
          .catch(error => {
            console.error(`❌ Error uploading files for level ${level.levelNumber}:`, error);
            throw error;
          });
        
        uploadPromises.push(uploadPromise);
      }
    }
    
    
    if (uploadPromises.length > 0) {
      try {
        await Promise.all(uploadPromises);
        this.isUploading = false;
      } catch (error) {
        console.error('❌ Error uploading some files:', error);
        this.isUploading = false;
        throw error;
      }
    } else {
      this.isUploading = false;
    }
  }

  async saveContent() {
    if (!this.titre) {
      Swal.fire({
        icon: 'warning',
        title: 'Champs requis',
        text: 'Veuillez remplir le titre'
      });
      return;
    }

    this.isSaving = true;
    Swal.fire({
      title: 'Enregistrement en cours...',
      html: 'Veuillez patienter...',
      icon: 'info',
      allowOutsideClick: false,
      allowEscapeKey: false,
      didOpen: (modal) => {
        Swal.showLoading();
      }
    });

    const payload: ContenuDetailleDto = {
      titre: this.titre,
      contenusCles: [],
      methodesPedagogiques: this.description || '',
      tags: this.getTagsString(),
      dureeTheorique: undefined,
      dureePratique: undefined,
      levels: this.levels.map(lvl => ({
        levelNumber: lvl.levelNumber,
        theorieContent: lvl.content || '',
        pratiqueContent: '',
        dureeTheorique: lvl.dureeTheorique ?? null,
        dureePratique: lvl.dureePratique ?? null
      } as ContentLevel))
      // idJourFormation removed - not needed for content creation
    };

    
    if (this.editMode && this.contentData?.id) {
      // Update existing content - First save content/levels, then upload files
      this.contenuDetailleService.updateContenuDetaille(this.contentData.id, payload).subscribe({
        next: async (response) => {
          
          // Now upload any new files after levels are saved
          try {
            await this.uploadLevelFiles(this.contentData.id);
            
            this.isSaving = false;
            Swal.fire({
              icon: 'success',
              title: 'Support modifié',
              text: 'Le support a été modifié avec succès',
              timer: 2000,
              showConfirmButton: false
            });
            this.onSaved.emit();
          } catch (fileError) {
            console.error('File upload error:', fileError);
            this.isSaving = false;
            Swal.fire({
              icon: 'warning',
              title: 'Support modifié',
              text: 'Le support a été modifié mais certains fichiers n\'ont pas pu être uploadés',
              timer: 3000,
              showConfirmButton: false
            });
            this.onSaved.emit();
          }
        },
        error: (error: any) => {
          console.error('Erreur lors de la modification:', error);
          this.isSaving = false;
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: 'Impossible de modifier le support: ' + (error.error?.message || error.message || 'Erreur inconnue')
          });
        }
      });
    } else {
      // Add new content
      this.contenuDetailleService.addContenuDetaille(payload).subscribe({
        next: async (response: any) => {
          
          const contentId = response[0]?.idContenuDetaille || response?.idContenuDetaille;
          
          if (contentId) {
            try {
              // Upload level files
              await this.uploadLevelFiles(contentId);
              
              this.isSaving = false;
              Swal.fire({
                icon: 'success',
                title: 'Support ajouté',
                text: 'Le support a été ajouté avec succès',
                timer: 2000,
                showConfirmButton: false
              });
            } catch (fileError) {
              console.error('File upload error:', fileError);
              this.isSaving = false;
              Swal.fire({
                icon: 'warning',
                title: 'Support ajouté',
                text: 'Le support a été ajouté mais certains fichiers n\'ont pas pu être uploadés',
                timer: 3000,
                showConfirmButton: false
              });
            }
          } else {
            this.isSaving = false;
            Swal.fire({
              icon: 'success',
              title: 'Support ajouté',
              text: 'Le support a été ajouté avec succès',
              timer: 2000,
              showConfirmButton: false
            });
          }
          
          this.onSaved.emit();
        },
        error: (error: any) => {
          console.error('Erreur lors de l\'ajout:', error);
          this.isSaving = false;
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: 'Impossible d\'ajouter le support: ' + (error.error?.message || error.message || 'Erreur inconnue')
          });
        }
      });
    }
  }

  close() {
    if (this.isRoutedAccess) {
      // Navigate back to menu/dashboard when accessed via route
      this.router.navigate(['/menu']);
    } else {
      // Emit close event when used as child component
      this.onClose.emit();
    }
  }

  resetForm() {
    this.titre = '';
    this.type = 'document';
    this.description = '';
    this.idJourFormation = undefined;
    this.selectedLevel = 'debutant';
    this.levels = [
      { level: 'debutant', levelNumber: 1, content: '', dureeTheorique: 0, dureePratique: 0, newFiles: [], uploadedFiles: [] },
      { level: 'intermediaire', levelNumber: 2, content: '', dureeTheorique: 0, dureePratique: 0, newFiles: [], uploadedFiles: [] },
      { level: 'avance', levelNumber: 3, content: '', dureeTheorique: 0, dureePratique: 0, newFiles: [], uploadedFiles: [] }
    ];
  }
}
