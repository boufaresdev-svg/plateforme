import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

import { Formation, NiveauFormation, TypeFormation } from '../../../models/formation/formation.model';
import { PlanFormation, StatutFormation } from '../../../models/formation/PlanFormation.model';
import { Apprenant, StatusInscription } from '../../../models/formation/Apprenant.model';
import { ContenuGlobal } from '../../../models/formation/ContenuGlobal.model';
import { ObjectifSpecifique } from '../../../models/formation/ObjectifSpecifique.model';
import { ContenuJourFormation } from '../../../models/formation/ContenuJourFormation.model';
import { Evaluation } from '../../../models/formation/Evaluation.model';

import { FormationService } from '../../../services/formation_managment/formation.service';
import { PlanFormationService } from '../../../services/formation_managment/PlanFormation.service';
import { ApprenantService } from '../../../services/formation_managment/Apprenants.service';
import { ContenuGlobalService } from '../../../services/formation_managment/ContenuGlobal.service';
import { ObjectifSpecifiqueService } from '../../../services/formation_managment/ObjectifSpecifique.service';
import { ContenuJourFormationService } from '../../../services/formation_managment/ContenuJourFormation.service';
import { EvaluationService } from '../../../services/formation_managment/Evaluation.service';
import { PdfGeneratorService } from '../../../services/formation_managment/pdf-generator.service';
import { FormateurService } from '../../../services/formation_managment/Formateur.service';
import { ContenuDetailleService } from '../../../services/contenu-detaille.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-formations',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './formations.component.html',
  styleUrls: ['./formations.component.css']
})
export class FormationsComponent implements OnInit {
  @Input() showOnlyPlanifiees: boolean = false;
  
  formations: Formation[] = [];
  selectedFormation: Formation | null = null;
  isEditMode = false;
  isLoading = false;
  isLoadingFormateurs = false;

  // Pagination state
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalItems = 0;
  usePagination = true; // Always use pagination by default

  // Plan Formation
  planFormData: PlanFormation = {
    titre: '',
    description: '',
    dateDebut: '',
    dateFin: '',
    dateLancement: '',
    dateFinReel: '',
    statusFormation: StatutFormation.PLANIFIEE,
    formation: undefined,
    formateur: undefined,
    idFormation: undefined,
    idFormateur: undefined
  };
  selectedPlan: PlanFormation | null = null;

  // Formation Form Data
  formationFormData: Formation = {
    theme: '',
    descriptionTheme: '',
    objectifGlobal: '',
    nombreHeures: undefined,
    prix: undefined,
    nombreMax: undefined,
    populationCible: '',
    typeFormation: TypeFormation.En_Ligne,
    niveau: NiveauFormation.Debutant,
    idDomaine: undefined,
    idType: undefined,
    idCategorie: undefined,
    idSousCategorie: undefined
  };

  // Maps and Arrays
  formationPlansMap: { [idFormation: number]: PlanFormation[] } = {};
  formationPlansVisible: { [idFormation: number]: boolean } = {};
  planApprenantsMap: { [idPlanFormation: number]: Apprenant[] } = {};

  contenusByFormation: { [idFormation: number]: ContenuGlobal[] } = {};
  objectifsByContenu: { [idContenu: number]: ObjectifSpecifique[] } = {};
  contenuJourByObjectif: { [idObjectif: number]: ContenuJourFormation[] } = {};
  evaluationsByContenuJour: { [idContenuJour: number]: Evaluation[] } = {};

  // Additional fields
  objectifSpecifique: string = '';
  
  // Objectifs Spécifiques Globaux (single global objective)
  objectifSpecifiqueGlobal: string = '';
  
  // Programme détaillé
  objectifsSpecifiques: Array<{
    titre: string;
    jours: Array<{
      numeroJour: number;
      contenus: Array<{
        titre?: string;
        contenusCles: Array<string>;
        methodesPedagogiques: string;
        dureeTheorique: number;
        dureePratique: number;
        levels?: Array<{
          levelNumber: number;
          theorieContent: string;
          pratiqueContent?: string;
          dureeTheorique?: number;
          dureePratique?: number;
        }>;
      }>;
    }>;
  }> = [];

  // Form Data
  apprenantFormData: Apprenant = {
    nom: '',
    prenom: '',
    adresse: '',
    telephone: '',
    email: '',
    prerequis: '',
    statusInscription: StatusInscription.INSCRIT,
    planFormation: undefined
  };
  selectedApprenant: Apprenant | null = null;
  selectedPlanForApprenant: PlanFormation | null = null;

  // Modal control
  showModal = false;
  modalType: 'formation' | 'plan' | 'apprenant' | 'contenu' | 'objectif' | 'contenuJour' | 'evaluation' | 'details' | null = null;
  modalTitle = '';
  viewOnlyFormation: Formation | null = null;

  // Content Manager
  showContentManager = false;
  selectedFormationForContent: Formation | null = null;
  selectedContent: any = null;
  contentSearchQuery = '';
  selectedProgrammeDetaile: any = null;  // Selected programme
  selectedJour: any = null;              // Selected jour
  selectedContenuDetaille: any = null;   // Selected contenu/day content
  // Index of selected jour/section for UI highlighting
  selectedSectionIndex: { jour: number; section: number } | null = null;
  
  // Content Library (from parent component)
  contentLibrary: Array<{
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
  }> = [];

  // Filter
  filter = {
    categorie: '',
    populationCible: '',
    niveau: '',
    dateDebut: ''
  };

  validationErrors: string[] = [];
  invalidFields: Set<string> = new Set<string>();

  // Formateurs
  formateurs: any[] = [];

  // Enums
  statuts = Object.values(StatutFormation);
  statusInscriptions = Object.values(StatusInscription);

  constructor(
    private formationService: FormationService,
    private planFormationService: PlanFormationService,
    private apprenantService: ApprenantService,
    private contenuGlobalService: ContenuGlobalService,
    private objectifSpecifiqueService: ObjectifSpecifiqueService,
    private contenuJourService: ContenuJourFormationService,
    private evaluationService: EvaluationService,
    private pdfGeneratorService: PdfGeneratorService,
    private formateurService: FormateurService,
    private contenuDetailleService: ContenuDetailleService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadFormations();
    this.loadFormateurs();
  }

  loadFormations() {
    if (this.usePagination) {
      console.log('📄 Loading paginated formations...');
      this.loadFormationsPaginated();
    } else {
      console.log('📋 Loading all formations...');
      this.loadAllFormations();
    }
  }

  loadAllFormations() {
    this.isLoading = true;
    this.formationService.getAllFormations().subscribe({
      next: (data: Formation[]) => {
        this.formations = data;
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Error loading formations:', err);
        this.isLoading = false;
      }
    });
  }

  loadFormationsPaginated() {
    this.isLoading = true;
    this.formationService.getFormationsPaginated({
      page: this.currentPage,
      size: this.pageSize,
      sortBy: 'idFormation',
      sortDirection: 'ASC'
    }).subscribe({
      next: (data: any) => {
        console.log('📄 Paginated Response:', data);
        this.formations = data.content || [];
        this.currentPage = data.page !== undefined ? data.page : this.currentPage;
        this.pageSize = data.size || this.pageSize;
        this.totalItems = data.total || 0;
        this.totalPages = data.totalPages || 1;
        console.log('✅ Pagination State:', { currentPage: this.currentPage, totalPages: this.totalPages, totalItems: this.totalItems });
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Error loading formations (paginated):', err);
        this.isLoading = false;
      }
    });
  }

  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadFormationsPaginated();
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.goToPage(this.currentPage + 1);
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.goToPage(this.currentPage - 1);
    }
  }

  togglePaginationMode() {
    console.log('❌ Button clicked! Current usePagination:', this.usePagination);
    this.usePagination = !this.usePagination;
    this.currentPage = 0;
    console.log('✅ Pagination Mode Toggled to:', this.usePagination);
    console.log('🔄 Full state:', { usePagination: this.usePagination, currentPage: this.currentPage });
    this.loadFormations();
  }

  loadFormateurs() {
    this.isLoadingFormateurs = true;
    
    this.formateurService.getAllFormateurs().subscribe({
      next: (data: any) => {
        this.formateurs = data;
        this.isLoadingFormateurs = false;
      },
      error: (err: any) => {
        console.error('Error loading formateurs:', err);
        this.isLoadingFormateurs = false;
      }
    });
  }

  goToCreateV2() {
    this.router.navigate(['/formation/manage']);
  }

  goToContentManager() {
    this.router.navigate(['/formation/contenu']);
  }

  editFormationV2(formation: Formation) {
    // Navigate to manage form with the formation ID for editing
    this.router.navigate(['/formation/manage'], { queryParams: { id: formation.idFormation } });
  }

  loadFormationPlans() {
    this.formationPlansMap = {};
    this.planFormationService.getAllPlans().subscribe({
      next: (data: PlanFormation[]) => {
        data.forEach((plan) => {
          if (!plan.idFormation) return;

          if (!this.formationPlansMap[plan.idFormation]) {
            this.formationPlansMap[plan.idFormation] = [];
          }
          this.formationPlansMap[plan.idFormation].push(plan);

          if (plan.idPlanFormation) {
            this.loadApprenantsForPlan(plan.idPlanFormation);
          }
        });
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des plans:', error);
      }
    });
  }

  refreshPlansForFormation(idFormation: number) {
    this.planFormationService.getPlansByFormation(idFormation).subscribe({
      next: (plans) => {
        this.formationPlansMap[idFormation] = plans;
        plans.forEach((plan) => {
          if (plan.idPlanFormation) {
            this.loadApprenantsForPlan(plan.idPlanFormation);
          }
        });
      },
      error: (err) => console.error('Erreur lors du rafraîchissement des plans:', err)
    });
  }

  loadApprenantsForPlan(idPlanFormation: number) {
    this.apprenantService.getApprenantsByPlanFormation(idPlanFormation).subscribe({
      next: (data: any) => {
        this.planApprenantsMap[idPlanFormation] = data;
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des apprenants:', error);
      }
    });
  }

  loadContenus() {
    this.contenuGlobalService.getAllContenus().subscribe({
      next: (data: any) => {
        data.forEach((contenu: any) => {
          if (contenu.idFormation) {
            if (!this.contenusByFormation[contenu.idFormation]) {
              this.contenusByFormation[contenu.idFormation] = [];
            }
            this.contenusByFormation[contenu.idFormation].push(contenu);
            if (contenu.idContenuGlobal) {
              this.loadObjectifs(contenu.idContenuGlobal);
            }
          }
        });
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des contenus:', error);
      }
    });
  }

  loadObjectifs(idContenuGlobal: number) {
    this.objectifSpecifiqueService.getObjectifsByContenu(idContenuGlobal).subscribe({
      next: (data: any) => {
        this.objectifsByContenu[idContenuGlobal] = data;
        data.forEach((objectif: any) => {
          if (objectif.idObjectifSpecifique) {
            this.loadContenuJour(objectif.idObjectifSpecifique);
          }
        });
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des objectifs:', error);
      }
    });
  }

  loadContenuJour(idObjectif: number) {
    this.contenuJourService.getContenusByObjectif(idObjectif).subscribe({
      next: (data: any) => {
        this.contenuJourByObjectif[idObjectif] = data;
        data.forEach((contenuJour: any) => {
          if (contenuJour.idContenuJourFormation) {
            this.loadEvaluations(contenuJour.idContenuJourFormation);
          }
        });
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement du contenu jour:', error);
      }
    });
  }

  loadEvaluations(idContenuJour: number) {
    this.evaluationService.getEvaluations().subscribe({
      next: (data: any) => {
        this.evaluationsByContenuJour[idContenuJour] = data.filter(
          (e: any) => e.contenuJourFormation?.idContenuJourFormation === idContenuJour
        );
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des evaluations:', error);
      }
    });
  }

  filteredFormations(): Formation[] {
    return this.formations.filter((f) => {
      const matchCategorie = !this.filter.categorie || 
        f.categorie?.nom?.toLowerCase().includes(this.filter.categorie.toLowerCase());
      const matchPopulation = !this.filter.populationCible || 
        f.populationCible?.toLowerCase().includes(this.filter.populationCible.toLowerCase());
      const matchNiveau = !this.filter.niveau || 
        f.niveau?.toLowerCase().includes(this.filter.niveau.toLowerCase());
      return matchCategorie && matchPopulation && matchNiveau;
    });
  }

  hasError(key: string): boolean {
    return this.invalidFields.has(key);
  }

  private clearValidation() {
    this.validationErrors = [];
    this.invalidFields = new Set<string>();
  }

  private addError(key: string, message: string, errors: string[]) {
    errors.push(message);
    this.invalidFields.add(key);
  }

  private validateFormationForm(): string[] {
    const errors: string[] = [];

    if (!this.formationFormData.theme || !this.formationFormData.theme.trim()) {
      this.addError('formation.theme', 'Le thème est requis.', errors);
    }

    if (this.formationFormData.nombreHeures == null || this.formationFormData.nombreHeures <= 0) {
      this.addError('formation.nombreHeures', 'Le nombre d\'heures doit être supérieur à 0.', errors);
    }

    if (this.formationFormData.prix == null || this.formationFormData.prix < 0) {
      this.addError('formation.prix', 'Le prix doit être positif (0 si gratuit).', errors);
    }

    if (this.formationFormData.nombreMax != null && this.formationFormData.nombreMax <= 0) {
      this.addError('formation.nombreMax', 'Le nombre maximum doit être supérieur à 0.', errors);
    }

    return errors;
  }

  private validatePlanForm(): string[] {
    const errors: string[] = [];

    if (!this.planFormData.titre || !this.planFormData.titre.trim()) {
      this.addError('plan.titre', 'Le titre du plan est requis.', errors);
    }

    if (!this.planFormData.dateDebut) {
      this.addError('plan.dateDebut', 'La date de début est requise.', errors);
    }

    if (!this.planFormData.dateFin) {
      this.addError('plan.dateFin', 'La date de fin est requise.', errors);
    }

    if (this.planFormData.dateDebut && this.planFormData.dateFin) {
      const debut = new Date(this.planFormData.dateDebut);
      const fin = new Date(this.planFormData.dateFin);
      if (debut > fin) {
        this.addError('plan.dateFin', 'La date de fin doit être après la date de début.', errors);
      }
    }

    return errors;
  }

  private validateApprenantForm(): string[] {
    const errors: string[] = [];
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const phoneRegex = /^\+?[0-9\s-]{6,}$/;

    if (!this.apprenantFormData.nom || !this.apprenantFormData.nom.trim()) {
      this.addError('apprenant.nom', 'Le nom est requis.', errors);
    }

    if (!this.apprenantFormData.prenom || !this.apprenantFormData.prenom.trim()) {
      this.addError('apprenant.prenom', 'Le prénom est requis.', errors);
    }

    if (!this.apprenantFormData.email || !emailRegex.test(this.apprenantFormData.email)) {
      this.addError('apprenant.email', 'Un email valide est requis.', errors);
    }

    if (!this.apprenantFormData.telephone || !phoneRegex.test(this.apprenantFormData.telephone)) {
      this.addError('apprenant.telephone', 'Un numéro de téléphone valide est requis.', errors);
    }

    return errors;
  }

  viewFormationDetails(formation: Formation) {
    this.viewOnlyFormation = formation;
    this.modalTitle = 'Détails de la Formation';
    this.modalType = 'details';
    this.showModal = true;
  }

  openModal(type: string) {
    this.clearValidation();
    this.modalType = type as any;
    this.showModal = true;
    
    if (type === 'formation') {
      this.modalTitle = this.isEditMode ? 'Modifier une formation' : 'Nouvelle formation';
    } else if (type === 'plan') {
      this.modalTitle = this.selectedPlan ? 'Modifier le plan de formation' : 'Créer un nouveau plan de formation';
    } else if (type === 'apprenant') {
      this.modalTitle = 'Ajouter un apprenant';
    }
  }

  closeModal() {
    this.showModal = false;
    this.modalType = null;
    this.clearValidation();
    this.resetForms();
  }

  resetForms() {
    this.clearValidation();
    this.formationFormData = {
      theme: '',
      descriptionTheme: '',
      objectifGlobal: '',
      nombreHeures: undefined,
      prix: undefined,
      nombreMax: undefined,
      populationCible: '',
      typeFormation: TypeFormation.En_Ligne,
      niveau: NiveauFormation.Debutant,
      idDomaine: undefined,
      idType: undefined,
      idCategorie: undefined,
      idSousCategorie: undefined
    };
    this.objectifSpecifiqueGlobal = '';
    this.objectifsSpecifiques = [];
    this.isEditMode = false;
    this.selectedFormation = null;

    this.planFormData = {
      titre: '',
      description: '',
      dateDebut: '',
      dateFin: '',
      dateLancement: '',
      dateFinReel: '',
      statusFormation: StatutFormation.PLANIFIEE,
      formation: undefined,
      formateur: undefined,
      idFormation: undefined,
      idFormateur: undefined
    };
    this.selectedPlan = null;

    this.apprenantFormData = {
      nom: '',
      prenom: '',
      adresse: '',
      telephone: '',
      email: '',
      prerequis: '',
      statusInscription: StatusInscription.INSCRIT,
      planFormation: undefined
    };
    this.selectedApprenant = null;
    this.selectedPlanForApprenant = null;
  }

  duplicateFormation(formation: Formation): void {
    if (!formation.idFormation) return;

    Swal.fire({
      title: 'Dupliquer la formation?',
      text: `Voulez-vous dupliquer "${formation.theme}" avec tous ses contenus?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Oui, dupliquer',
      cancelButtonText: 'Annuler',
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33'
    }).then((result) => {
      if (result.isConfirmed) {
        // Show loading indicator
        Swal.fire({
          title: 'Duplication en cours...',
          html: 'Copie de tous les objectifs et contenus...',
          allowOutsideClick: false,
          didOpen: () => {
            Swal.showLoading();
          }
        });

        // Call the backend to perform deep duplication
        this.formationService.duplicateFormation(formation.idFormation!).subscribe({
          next: (response) => {
            // Handle both array and object responses
            const result = Array.isArray(response) ? response[0] : response;
            
            Swal.fire({
              icon: 'success',
              title: 'Formation dupliquée',
              text: result?.message || `Formation dupliquée avec succès`,
              timer: 3000,
              showConfirmButton: true
            });
            this.loadFormations();
          },
          error: (err) => {
            console.error('Erreur duplication formation:', err);
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: err.error?.message || 'Impossible de dupliquer la formation.'
            });
          }
        });
      }
    });
  }

  generateTrainingPlan(formation: Formation): void {
    if (!formation) {
      Swal.fire({
        icon: 'warning',
        title: 'Aucune formation',
        text: 'Impossible de générer le plan pour une formation invalide.'
      });
      return;
    }

    // Prepare the training plan content
    let planContent = `PLAN DE FORMATION\n`;
    planContent += `===================\n\n`;
    planContent += `Formation: ${formation.theme || 'Non défini'}\n`;
    planContent += `Type: ${formation.typeFormation || 'Non défini'}\n`;
    planContent += `Niveau: ${formation.niveau || 'Non défini'}\n`;
    planContent += `Durée totale: ${formation.nombreHeures || '—'}h\n`;
    planContent += `Prix: ${formation.prix || 0} DT\n`;
    planContent += `Nombre maximum: ${formation.nombreMax || '—'}\n`;
    planContent += `Population cible: ${formation.populationCible || 'Non défini'}\n`;
    planContent += `Catégorie: ${formation.categorie?.nom || formation.type?.nom || '—'}\n\n`;

    // Add description
    if (formation.descriptionTheme) {
      planContent += `DESCRIPTION\n`;
      planContent += `===========\n`;
      planContent += `${formation.descriptionTheme}\n\n`;
    }

    // Add global objectives
    if (formation.objectifGlobal) {
      planContent += `OBJECTIFS\n`;
      planContent += `=========\n`;
      planContent += `${formation.objectifGlobal}\n\n`;
    }

    // Add contenus if available
    const contenus = this.contenusByFormation[formation.idFormation!];
    if (contenus && contenus.length > 0) {
      planContent += `SUPPORT PÉDAGOGIQUE\n`;
      planContent += `================\n\n`;
      
      contenus.forEach((contenu, index) => {
        planContent += `${index + 1}. ${contenu.titre || 'Contenu sans titre'}\n`;
        
        if (contenu.description) {
          planContent += `   Description: ${contenu.description}\n`;
        }
        
        // Add specific objectives for this content
        const objectifs = this.objectifsByContenu[contenu.idContenuGlobal!];
        if (objectifs && objectifs.length > 0) {
          planContent += `   Objectifs spécifiques:\n`;
          objectifs.forEach(obj => {
            planContent += `     - ${obj.description}\n`;
            
            // Add daily content for this objective
            const contenuJours = this.contenuJourByObjectif[obj.idObjectifSpec!];
            if (contenuJours && contenuJours.length > 0) {
              contenuJours.forEach(cj => {
                planContent += `       Contenu: ${cj.contenu}\n`;
                if (cj.nbHeuresTheoriques || cj.nbHeuresPratiques) {
                  planContent += `       Durée: ${cj.nbHeuresTheoriques || 0}h théorie, ${cj.nbHeuresPratiques || 0}h pratique\n`;
                }
              });
            }
          });
        }
        
        planContent += `\n`;
      });
    }

    // Add plans information if available
    const plans = this.formationPlansMap[formation.idFormation!];
    if (plans && plans.length > 0) {
      planContent += `SESSIONS PLANIFIÉES\n`;
      planContent += `===================\n\n`;
      
      plans.forEach((plan, index) => {
        planContent += `${index + 1}. ${plan.titre}\n`;
        planContent += `   Description: ${plan.description || 'N/A'}\n`;
        planContent += `   Date de début: ${plan.dateDebut ? new Date(plan.dateDebut).toLocaleDateString('fr-FR') : 'N/A'}\n`;
        planContent += `   Date de fin: ${plan.dateFin ? new Date(plan.dateFin).toLocaleDateString('fr-FR') : 'N/A'}\n`;
        planContent += `   Statut: ${plan.statusFormation}\n`;
        
        // Add participants info
        const apprenants = this.planApprenantsMap[plan.idPlanFormation!];
        if (apprenants && apprenants.length > 0) {
          planContent += `   Nombre de participants: ${apprenants.length}\n`;
        }
        planContent += `\n`;
      });
    }

    // Create a downloadable text file
    const blob = new Blob([planContent], { type: 'text/plain;charset=utf-8' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `plan_formation_${formation.theme?.replace(/\s+/g, '_') || 'formation'}_${new Date().toISOString().split('T')[0]}.txt`;
    link.click();
    window.URL.revokeObjectURL(url);

    Swal.fire({
      icon: 'success',
      title: 'Plan généré',
      text: 'Le plan de formation a été téléchargé avec succès.',
      timer: 2000,
      showConfirmButton: false
    });
  }

  deleteFormation(formation: Formation) {
    const formationName = formation.theme || 'cette formation';
    
    Swal.fire({
      title: 'Confirmer la suppression',
      html: `
        <p>Pour supprimer définitivement cette formation, veuillez entrer son nom exact :</p>
        <p style="font-weight: bold; color: #d33; margin: 10px 0;">${formationName}</p>
        <input type="text" id="confirmationInput" class="swal2-input" placeholder="Entrez le nom de la formation">
      `,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Supprimer',
      cancelButtonText: 'Annuler',
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      preConfirm: () => {
        const input = (document.getElementById('confirmationInput') as HTMLInputElement)?.value;
        if (!input) {
          Swal.showValidationMessage('Veuillez entrer le nom de la formation');
          return false;
        }
        if (input.trim() !== formationName.trim()) {
          Swal.showValidationMessage('Le nom ne correspond pas. Vérifiez l\'orthographe.');
          return false;
        }
        return true;
      }
    }).then((result: any) => {
      if (result.isConfirmed && formation.idFormation) {
        this.formationService.deleteFormation(formation.idFormation).subscribe({
          next: (data: any) => {
            Swal.fire({
              icon: 'success',
              title: 'Supprimée',
              text: 'Formation supprimée avec succès',
              timer: 2000,
              showConfirmButton: false
            });
            this.loadFormations();
          },
          error: (error: any) => {
            console.error('Erreur lors de la suppression:', error);
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Erreur lors de la suppression de la formation'
            });
          }
        });
      }
    });
  }

  togglePlansForFormation(formation: Formation) {
    if (formation.idFormation) {
      this.formationPlansVisible[formation.idFormation] = !this.formationPlansVisible[formation.idFormation];
    }
  }

  openPlanModal(formation: Formation) {
    this.planFormData.idFormation = formation.idFormation;
    this.planFormData.formation = formation;
    this.openModal('plan');
  }

  savePlan() {
    
    this.clearValidation();
    this.validationErrors = this.validatePlanForm();

    if (this.validationErrors.length) {
      Swal.fire('Validation requise', this.validationErrors[0], 'warning');
      return;
    }

    if (!this.planFormData.idFormation && this.planFormData.formation?.idFormation) {
      this.planFormData.idFormation = this.planFormData.formation.idFormation;
    }

    if (!this.planFormData.idFormation) {
      Swal.fire('Erreur', 'Formation non trouvée', 'error');
      return;
    }

    const isUpdate = !!this.planFormData.idPlanFormation;
    
    const targetFormationId = this.planFormData.idFormation;
    const payload: PlanFormation = {
      ...this.planFormData,
      idFormation: targetFormationId,
      idFormateur: this.planFormData.idFormateur || this.planFormData.formateur?.idFormateur
    };
    

    const request$ = isUpdate
      ? this.planFormationService.updatePlan(this.planFormData.idPlanFormation!, payload)
      : this.planFormationService.createPlan(payload);
      

    request$.subscribe({
      next: (res: any) => {
        Swal.fire('Succès', isUpdate ? 'Plan mis à jour' : 'Plan créé', 'success');
        this.closeModal();
        this.refreshPlansForFormation(targetFormationId);
      },
      error: (err) => {
        console.error('Erreur lors de la sauvegarde du plan:', err);
        Swal.fire('Erreur', 'Impossible de sauvegarder le plan', 'error');
      }
    });
  }

  editPlan(plan: PlanFormation) {
    const formation = plan.idFormation
      ? this.formations.find(f => f.idFormation === plan.idFormation)
      : plan.formation;

    this.planFormData = {
      ...plan,
      formation,
      idFormation: plan.idFormation || formation?.idFormation
    };
    this.selectedPlan = plan;
    this.openModal('plan');
  }

  createNewPlan(formation: Formation) {
    if (!formation.idFormation) return;

    // Reset form for new plan
    this.planFormData = {
      titre: '',
      description: '',
      dateDebut: '',
      dateFin: '',
      dateLancement: '',
      dateFinReel: '',
      statusFormation: StatutFormation.PLANIFIEE,
      formation: formation,
      formateur: undefined,
      idFormation: formation.idFormation,
      idFormateur: undefined,
      idPlanFormation: undefined // Ensure it's undefined for new plans (triggers POST, not PUT)
    };
    
    this.selectedPlan = null;
    this.openModal('plan');
  }

  deletePlan(plan: PlanFormation) {
    if (!plan.idPlanFormation) return;

    Swal.fire({
      title: 'Confirmer la suppression',
      text: 'Êtes-vous sûr de vouloir supprimer ce plan?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Oui, supprimer',
      cancelButtonText: 'Annuler'
    }).then((result: any) => {
      if (result.isConfirmed) {
        this.planFormationService.deletePlan(plan.idPlanFormation!).subscribe({
          next: (data: any) => {
            Swal.fire('Supprimé', 'Plan supprimé avec succès', 'success');
            if (plan.idFormation) {
              this.refreshPlansForFormation(plan.idFormation);
            } else {
              this.loadFormationPlans();
            }
          },
          error: (error: any) => {
            console.error('Erreur:', error);
            Swal.fire('Erreur', 'Erreur lors de la suppression', 'error');
          }
        });
      }
    });
  }

  openApprenantModal(plan: PlanFormation, apprenant?: Apprenant) {
    this.selectedPlanForApprenant = plan;
    if (apprenant) {
      this.apprenantFormData = { ...apprenant };
      this.selectedApprenant = apprenant;
    } else {
      this.apprenantFormData = {
        nom: '',
        prenom: '',
        adresse: '',
        telephone: '',
        email: '',
        prerequis: '',
        statusInscription: StatusInscription.INSCRIT,
        planFormation: plan
      };
    }
    this.openModal('apprenant');
  }

  saveApprenant() {
    this.clearValidation();
    this.validationErrors = this.validateApprenantForm();

    if (this.validationErrors.length) {
      Swal.fire('Validation requise', this.validationErrors[0], 'warning');
      return;
    }

    if (this.selectedApprenant?.idApprenant) {
      this.apprenantService.updateApprenant(this.selectedApprenant.idApprenant, this.apprenantFormData).subscribe({
        next: () => {
          Swal.fire('Succès', 'Apprenant mis à jour', 'success');
          this.closeModal();
          this.loadFormations();
        },
        error: (error: any) => {
          console.error('Erreur:', error);
          Swal.fire('Erreur', 'Erreur lors de la mise à jour', 'error');
        }
      });
    } else {
      this.apprenantService.addApprenant(this.apprenantFormData).subscribe({
        next: () => {
          Swal.fire('Succès', 'Apprenant ajouté avec succès', 'success');
          this.closeModal();
          this.loadFormations();
        },
        error: (error: any) => {
          console.error('Erreur:', error);
          Swal.fire('Erreur', 'Erreur lors de l\'ajout', 'error');
        }
      });
    }
  }

  deleteApprenant(apprenant: Apprenant, plan: PlanFormation) {
    if (!apprenant.idApprenant) return;

    Swal.fire({
      title: 'Confirmer la suppression',
      text: 'Êtes-vous sûr?',
      icon: 'warning',
      showCancelButton: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.apprenantService.deleteApprenant(apprenant.idApprenant!).subscribe({
          next: () => {
            Swal.fire('Supprimé', 'Apprenant supprimé', 'success');
            this.loadFormations();
          },
          error: (error: any) => {
            console.error('Erreur:', error);
            Swal.fire('Erreur', 'Erreur lors de la suppression', 'error');
          }
        });
      }
    });
  }

  openContenuModal(formation: Formation) {
    this.openModal('contenu');
  }

  openObjectifModal(contenu: ContenuGlobal) {
    this.openModal('objectif');
  }

  downloadFicheFormation(formationPlanifiee: any) {
    // Get the formation data
    const formation = formationPlanifiee.formation || this.formations.find(f => f.idFormation === formationPlanifiee.id);
    
    if (!formation) {
      Swal.fire('Erreur', 'Formation non trouvée', 'error');
      return;
    }

    // Create a mock plan object with the data we have
    const mockPlan: any = {
      titre: formationPlanifiee.titre,
      dateDebut: formationPlanifiee.dateDebut,
      dateFin: formationPlanifiee.dateFin,
      statusFormation: formationPlanifiee.status
    };

    // Generate the PDF using the PdfGeneratorService
    this.pdfGeneratorService.generateFormationProgramPdf(formation, mockPlan);
  }

  // ==================== CONTENT MANAGER METHODS ====================
  
  openContentManager(formation: Formation) {
    this.selectedFormationForContent = formation;
    this.showContentManager = true;
    this.loadContentLibrary();
    // Auto-select first programme/jour/section for better UX
    const programmes = this.selectedFormationForContent?.programmesDetailes;
    if (programmes && programmes.length > 0) {
      this.selectedProgrammeDetaile = programmes[0];
      const jours = this.selectedProgrammeDetaile?.jours;
      if (jours && jours.length > 0) {
        this.selectedJour = jours[0];
        const contenus = this.selectedJour?.contenus;
        if (contenus && contenus.length > 0) {
          this.selectedContenuDetaille = contenus[0];
          this.selectedSectionIndex = { jour: 0, section: 0 };
        }
      }
    } else {
      this.selectedProgrammeDetaile = null;
      this.selectedJour = null;
      this.selectedContenuDetaille = null;
      this.selectedSectionIndex = null;
    }
  }

  // Open content manager for the first available formation (for quick access from header pill)
  openContentManagerForFirst() {
    const formation = this.filteredFormations()[0] || this.formations[0];
    if (!formation) {
      Swal.fire({ icon: 'info', title: 'Aucune formation', text: 'Ajoutez une formation pour gérer le contenu.' });
      return;
    }
    this.openContentManager(formation);
  }

  closeContentManager() {
    this.showContentManager = false;
    this.selectedFormationForContent = null;
    this.selectedContent = null;
    this.contentSearchQuery = '';
  }

  loadContentLibrary() {
    // Load content from backend for the selected formation
    if (!this.selectedFormationForContent?.idFormation) {
      console.warn('No formation selected for content library');
      this.contentLibrary = [];
      return;
    }

    // Fetch content details that are related to this specific formation
    this.contenuDetailleService.getContenuDetailleByFormationId(this.selectedFormationForContent.idFormation).subscribe({
      next: (response: any) => {
        // Handle both array and single object responses
        const contenus = Array.isArray(response) ? response : (response?.data || []);
        
        this.contentLibrary = contenus.map((contenu: any) => ({
          id: contenu.id || contenu.idContenuDetaille,
          title: contenu.title || contenu.nom || 'Sans titre',
          description: contenu.description || '',
          type: this.getContentType(contenu),
          size: this.formatFileSize(contenu.fileSize || 0),
          uploadDate: this.formatDate(contenu.uploadDate || contenu.dateCreation),
          formation: this.selectedFormationForContent?.theme || 'Formation',
          fileName: contenu.fileName || 'fichier',
          fileUrl: contenu.filePath || '',
          videoUrl: contenu.videoUrl || '',
          levels: contenu.levels || []
        }));
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement du contenu:', error);
        this.contentLibrary = [];
      }
    });
  }

  private getContentType(contenu: any): string {
    if (contenu.fileType) {
      const type = contenu.fileType.toLowerCase();
      if (type.includes('video') || type.includes('mp4')) return 'video';
      if (type.includes('pdf')) return 'pdf';
      if (type.includes('presentation') || type.includes('pptx')) return 'presentation';
      if (type.includes('image') || type.includes('jpg') || type.includes('png')) return 'image';
      return 'document';
    }
    return 'document';
  }

  private formatFileSize(bytes: number): string {
    if (!bytes) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 10) / 10 + ' ' + sizes[i];
  }

  private formatDate(dateStr: any): string {
    if (!dateStr) return new Date().toISOString().split('T')[0];
    const date = new Date(dateStr);
    return date.toISOString().split('T')[0];
  }

  selectContent(content: any) {
    this.selectedContent = content;
  }

  getFilteredContent() {
    if (!this.contentSearchQuery) {
      return this.contentLibrary;
    }
    const query = this.contentSearchQuery.toLowerCase();
    return this.contentLibrary.filter(c => 
      c.title.toLowerCase().includes(query) || 
      c.type.toLowerCase().includes(query) ||
      c.description.toLowerCase().includes(query)
    );
  }

  appendContentToFormation(content: any, event?: Event) {
    if (event) {
      event.stopPropagation();
    }
    
    if (!this.selectedContenuDetaille) {
      Swal.fire({
        icon: 'warning',
        title: 'Sélection incomplète',
        text: 'Veuillez d\'abord sélectionner un jour et une section pour ajouter le contenu',
        timer: 2000,
        showConfirmButton: false
      });
      return;
    }
    
    // Initialize contenusCles array if not exists
    if (!this.selectedContenuDetaille.contenusCles) {
      this.selectedContenuDetaille.contenusCles = [];
    }
    
    // Check if already added
    if (this.isContentAlreadyAdded(content)) {
      Swal.fire({
        icon: 'info',
        title: 'Déjà ajouté',
        text: 'Ce contenu est déjà dans les clés de cette section',
        timer: 2000,
        showConfirmButton: false
      });
      return;
    }
    
    // Add content title to contenusCles
    this.selectedContenuDetaille.contenusCles.push(`[${content.type.toUpperCase()}] ${content.title}`);
    
    Swal.fire({
      icon: 'success',
      title: 'Contenu ajouté',
      text: `"${content.title}" a été ajouté aux contenus clés`,
      timer: 1500,
      showConfirmButton: false
    });
  }

  removeContentFromFormation(contentTitle: string, contenu?: any) {
    const target = contenu || this.selectedContenuDetaille;
    if (!target || !target.contenusCles) return;

    target.contenusCles = target.contenusCles.filter((c: string) => c !== contentTitle);

    Swal.fire({
      icon: 'success',
      title: 'Contenu retiré',
      timer: 1500,
      showConfirmButton: false
    });
  }

  isContentAlreadyAdded(content: any): boolean {
    if (!this.selectedContenuDetaille || !this.selectedContenuDetaille.contenusCles) return false;
    
    const contentTitle = `[${content.type.toUpperCase()}] ${content.title}`;
    return this.selectedContenuDetaille.contenusCles.includes(contentTitle);
  }

  getFormationContent() {
    if (!this.selectedContenuDetaille) return [];
    
    return this.selectedContenuDetaille.contenusCles || [];
  }

  getFormationContentCount(): number {
    return this.getFormationContent().length;
  }

  saveFormationContent() {
    if (!this.selectedFormationForContent || !this.selectedContenuDetaille) return;
    
    const contentCount = this.selectedContenuDetaille.contenusCles?.length || 0;
    const programmeTitle = this.selectedProgrammeDetaile?.titre || 'Programme';
    const dayNumber = this.selectedJour?.numeroJour || 'N/A';
    
    Swal.fire({
      icon: 'success',
      title: 'Modifications enregistrées',
      text: `${contentCount} contenu(s) clé(s) ajouté(s) à Jour ${dayNumber} du programme "${programmeTitle}"`,
      timer: 2000,
      showConfirmButton: false
    });
    
    this.closeContentManager();
  }

  downloadContentFromManager(content: any) {
    Swal.fire({
      icon: 'info',
      title: 'Téléchargement',
      text: `Téléchargement de ${content.fileName}...`,
      timer: 2000,
      showConfirmButton: false
    });
  }

  getFormationContentForView(formationId: number): any[] {
    const formation = this.formations.find(f => f.idFormation === formationId);
    const allContenusCles: string[] = [];
    
    // Collect all contenusCles from all programmes and jours
    formation?.programmesDetailes?.forEach(programme => {
      programme.jours?.forEach(jour => {
        jour.contenus?.forEach(contenu => {
          contenu.contenusCles?.forEach(cle => {
            if (!allContenusCles.includes(cle)) {
              allContenusCles.push(cle);
            }
          });
        });
      });
    });
    
    return allContenusCles;
  }

  // Drag & Drop Methods
  draggedContent: any = null;

  onDragStart(event: DragEvent, content: any) {
    this.draggedContent = content;
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'move';
      event.dataTransfer.setData('application/json', JSON.stringify(content));
    }
  }

  // Explicit selection helpers for easier workflow
  selectJour(jour: any, jourIndex: number) {
    this.selectedJour = jour;
    const first = jour?.contenus?.[0] ?? null;
    this.selectedContenuDetaille = first;
    this.selectedSectionIndex = first ? { jour: jourIndex, section: 0 } : null;
  }

  selectSection(contenu: any, jourIndex: number, sectionIndex: number) {
    this.selectedContenuDetaille = contenu;
    this.selectedSectionIndex = { jour: jourIndex, section: sectionIndex };
  }

  // Allow click-to-assign materials to currently selected section
  onMaterialClick(content: any) {
    if (!this.selectedContenuDetaille) {
      Swal.fire({
        icon: 'warning',
        title: 'Sélection requise',
        text: 'Sélectionnez un jour et une section avant d\'assigner.',
        timer: 1800,
        showConfirmButton: false
      });
      return;
    }
    if (!this.selectedContenuDetaille.contenusCles) {
      this.selectedContenuDetaille.contenusCles = [];
    }
    const title = `[${content.type.toUpperCase()}] ${content.title}`;
    if (this.selectedContenuDetaille.contenusCles.includes(title)) {
      Swal.fire({
        icon: 'info',
        title: 'Déjà ajouté',
        text: 'Ce contenu est déjà assigné à la section sélectionnée.',
        timer: 1200,
        showConfirmButton: false
      });
      return;
    }
    this.selectedContenuDetaille.contenusCles.push(title);
    Swal.fire({
      icon: 'success',
      title: 'Contenu assigné',
      text: 'Matériau ajouté à la section sélectionnée.',
      timer: 1000,
      showConfirmButton: false
    });
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    if (event.dataTransfer) {
      event.dataTransfer.dropEffect = 'move';
    }
    // Add visual feedback
    const target = event.currentTarget as HTMLElement;
    if (target) {
      target.classList.add('drag-over');
    }
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    const target = event.currentTarget as HTMLElement;
    if (target) {
      target.classList.remove('drag-over');
    }
  }

  onDropContent(event: DragEvent, contenu: any) {
    event.preventDefault();
    event.stopPropagation();

    // Remove visual feedback
    const target = event.currentTarget as HTMLElement;
    if (target) {
      target.classList.remove('drag-over');
    }

    // Get the dragged content
    let droppedContent = this.draggedContent;
    
    // Try to get from data transfer if draggedContent not available
    if (!droppedContent && event.dataTransfer?.getData('application/json')) {
      try {
        droppedContent = JSON.parse(event.dataTransfer.getData('application/json'));
      } catch (e) {
        console.error('Error parsing dropped data:', e);
      }
    }

    if (!droppedContent) {
      Swal.fire({
        icon: 'warning',
        title: 'Erreur',
        text: 'Aucun contenu à déposer',
        timer: 2000,
        showConfirmButton: false
      });
      return;
    }

    // Initialize contenusCles if not exists
    if (!contenu.contenusCles) {
      contenu.contenusCles = [];
    }

    // Check if already added
    const contentTitle = `[${droppedContent.type.toUpperCase()}] ${droppedContent.title}`;
    if (contenu.contenusCles.includes(contentTitle)) {
      Swal.fire({
        icon: 'info',
        title: 'Déjà ajouté',
        text: 'Ce matériau est déjà dans cette section',
        timer: 1500,
        showConfirmButton: false
      });
      return;
    }

    // Add content to contenusCles
    contenu.contenusCles.push(contentTitle);

    Swal.fire({
      icon: 'success',
      title: 'Matériau ajouté',
      text: `"${droppedContent.title}" ajouté à cette section`,
      timer: 1000,
      showConfirmButton: false
    });

    this.draggedContent = null;
  }
}
