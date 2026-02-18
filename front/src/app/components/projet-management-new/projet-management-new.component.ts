import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProjetManagementService } from '../../services/projet-management.service';
import { Projet } from '../../models/projet/Projet.model';
import { Mission } from '../../models/projet/Mission.model';
import { Tache } from '../../models/projet/Tache.model';
import { CommentaireTache } from '../../models/projet/CommentaireTache.model';
import { PrioriteMission, PrioriteTache, ProjetStats, RoleProjet, StatutAffectation, StatutMission, StatutTache, TypeCommentaire } from '../../models/projet/enum.model';
import { PrioriteProjet, StatutProjet, TypeProjet, StatutPhase } from '../../models/projet/enum.model';
import { EmployeAffecte } from '../../models/projet/EmployeAffecte.model';
import { Charge } from '../../models/projet/Charge.model';
import { Phase } from '../../models/projet/Phase.model';
import Swal from 'sweetalert2';
import { ProjetService } from '../../services/projet_managment/projet-service';
import { ClientService } from '../../services/client_managment/client-service';
import { Client } from '../../models/client/client.model';
import { PhaseService } from '../../services/projet_managment/phase-service';
import { Observable } from 'rxjs';
import { MissionService } from '../../services/projet_managment/mission-service';
import { Router } from '@angular/router';
import { TacheService } from '../../services/projet_managment/tache-service';
import { ChargeService } from '../../services/projet_managment/charge-service';
import { EmployeeAffecteService } from '../../services/projet_managment/employee-affecte-service';
import { EmployeeService } from '../../services/rh_managment/employee-service';
import { Employee } from '../../models/rh/Employee.model';
import { CommentaireService } from '../../services/projet_managment/commentaire-service';
import { LivrableTache } from '../../models/projet/LivrableTache.model';
import { LivrableTacheService } from '../../services/projet_managment/livrable-tache-service';
import { ProblemeTache } from '../../models/projet/ProblemeTache.model';
import { ProblemeTacheService } from '../../services/projet_managment/probleme-tache-service';
//import { ProjetService } from '../../services/projet.service';

interface MissionFormValue {
  projetId: string;
  phaseId: string;
  nom: string;
  description: string;
  objectif: string;
  statut: StatutMission;
  priorite: PrioriteMission;
  dateDebut: string;
  dateFin: string;
  progression: number;
  budget: number;
  employesAffectes: EmployeAffecte[];
  dependances: string[];
  livrables: string[];
}

interface TacheFormValue {
  missionId: string;
  nom: string;
  description: string;
  statut: StatutTache;
  priorite: PrioriteTache;
  dateDebut: string;
  dateFin: string;
  dureeEstimee: number;
  progression: number;
  dependances: string[];
  fichiers: string[];
}

@Component({
  selector: 'app-projet-management-new',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './projet-management-new.component.html',
  styleUrls: ['./projet-management-new.component.css']
})
export class ProjetManagementNewComponent implements OnInit {
  projets: Projet[] = [];
  filteredProjets: Projet[] = [];
  missions: Mission[] = [];
  taches: Tache[] = [];
  commentaires: CommentaireTache[] = [];

  totalProjet: number = 0;
  budgetTotal: number = 0;
  planifie: number = 0;
  encours: number = 0;
  termine: number = 0;
  projetsEnRetard: number = 0;

  currentView: 'dashboard' | 'projets' | 'phases' | 'missions' | 'taches' | 'charges' | 'details' | 'phase-details' | 'mission-details' | 'tache-details' | 'form' | 'phase-form' | 'mission-form' | 'tache-form' | 'charge-form' = 'dashboard';
  selectedProjet: Projet | null = null;
  selectedMission: Mission | null = null;
  selectedTache: Tache | null = null;
  selectedPhase: Phase | null = null;
  isEditMode = false;
  isEditModeMission = false;
  isEditModeTache = false;
  searchTerm = '';
  searchTermMission = '';
  searchTermTache = '';
  searchTermPhase = '';

  // Filtres pour projets
  filterClient = '';
  filterStatut = '';

  // Filtres pour phases
  phases: Phase[] = [];
  filteredPhases: Phase[] = [];
  filterProjetIdPhase = '';
  filterStatutPhase = '';

  // Filtres pour missions
  filterProjetIdMission = '';
  filterPhaseIdMission = '';
  filterStatutMission = '';
  filteredMissions: Mission[] = [];

  // Filtres pour tâches
  filterMissionIdTache = '';
  filterStatutTache = '';
  filteredTaches: Tache[] = [];

  // Charges
  charges: Charge[] = [];
  filteredCharges: Charge[] = [];
  searchTermCharge = '';
  filterTacheIdCharge = '';
  filterEmployeIdCharge = '';

  // Formulaires
  projetFormData: any = {};
  phaseFormData: any = {};
  missionFormData: any = {};
  tacheFormData: any = {};
  chargeFormData: any = {};
  commentaireFormData: any = {};
  affectationFormData: any = {};

  // Popups
  showProjetForm = false;
  showPhaseForm = false;
  showMissionForm = false;
  showTacheForm = false;
  showChargeForm = false;
  showCommentaireForm = false;
  showAffectationForm = false;
  affectationType: 'mission' | 'tache' = 'mission';
  showProjetDetails = false;
  showPhaseDetails = false;
  showMissionDetails = false;
  showTacheDetails = false;
  submitted = false;
  submittedPhase = false;
  submittedMission = false;
  submittedTache = false;
  submittedCharge = false;
  submittedCommentaire = false;
  submittedAffectation = false;
  submittedProbleme = false;
  submittedLivrable = false;

  // FormGroups
  projetForm!: FormGroup;
  phaseForm!: FormGroup;
  missionForm!: FormGroup;
  tacheForm!: FormGroup;
  chargeForm!: FormGroup;
  commentaireForm!: FormGroup;
  livrableForm!: FormGroup;
  problemeForm!: FormGroup;
  affectationForm!: FormGroup;
  showProblemeForm = false;
  showLivrableForm = false;
  ProblemeFormData: any = {};


  // Énumérations
  typesProjet = Object.values(TypeProjet);
  statutsProjets = Object.values(StatutProjet);
  prioritesProjets = Object.values(PrioriteProjet);
  statutsMissions = Object.values(StatutMission);
  prioritesMissions = Object.values(PrioriteMission);
  statutsTaches = Object.values(StatutTache);
  prioritesTaches = Object.values(PrioriteTache);
  rolesProjets = Object.values(RoleProjet);
  typesCommentaires = Object.values(TypeCommentaire);
  clients: Client[] = [];
  selectedProjetPhases: Phase[] = [];
  selectedPhaseMission: Mission[] = []
  selectedMissionTaches: Tache[] = []
  selectedMissionEmployee: EmployeAffecte[] = []
  employees: Employee[] = [];
  idTache: string = '';
  showSousCategorie = false;
  sousCategories: string[] = [];
  constructor(
    private fb: FormBuilder,
    private projetService: ProjetService,
    private clientService: ClientService,
    private phaseService: PhaseService,
    private missionService: MissionService,
    private tacheService: TacheService,
    private chargeService: ChargeService,
    private employeeAffecteService: EmployeeAffecteService,
    private employeeService: EmployeeService,
    private CommentaireService: CommentaireService,
    private LivrableService: LivrableTacheService,
    private ProblemeService: ProblemeTacheService,
    private router: Router
  ) { }

  goBackToMenu(): void {
    this.router.navigate(['/menu']);
  }

  ngOnInit(): void {
    this.initForms();
    this.loadProjet();
    this.loadClient();
    this.loadPhase();
    this.loadMission();
    this.loadEmployees();

    this.applyPhaseFilters();
    this.applyMissionFilters();
    this.applyTacheFilters();
    this.applyChargeFilters();
  }

  loadClient() {
    this.clientService.getClients().subscribe(clients => {
      this.clients = clients;
    })
  }

  private initForms(): void {
    this.initProjetForm();
    this.initMissionForm();
    this.initTacheForm();
    this.initCommentaireForm();
    this.initAffectationForm();
    this.initPhaseForm();
    this.initChargeForm();
    this.initproblemeForm();
    this.initlivrableForm();
  }

  loadProjet() {
    this.projetService.getProjets().subscribe(projets => {
      this.projets = projets ?? [];
      this.filteredProjets = projets ?? [];
      this.getProjetStats(this.projets);
    });
  }

  loadPhase() {
    this.phaseService.getPhases().subscribe(phases => {
      this.phases = phases ?? [];
      this.filteredPhases = phases ?? [];
    });
  }

  loadMission() {
    this.missionService.getMissions().subscribe(missions => {
      this.missions = missions ?? [];
      this.filteredMissions = missions ?? [];
    });
  }

  private loadEmployees(): void {
    this.employeeService.getEmployees().subscribe(employees => {
      this.employees = employees;
    });
  }

  initproblemeForm(): void {
    this.problemeForm = this.fb.group({
      id: [''],
      tacheId: ['', Validators.required],
      nom: ['', Validators.required],
      description: ['']
    });

    this.livrableForm = this.fb.group({
      id: [''],
      tacheId: ['', Validators.required],
      nom: ['', Validators.required],
      description: ['']
    });
  }

  initlivrableForm(): void {


    this.livrableForm = this.fb.group({
      id: [''],
      tacheId: ['', Validators.required],
      nom: ['', Validators.required],
      description: ['']
    });
  }

  private initPhaseForm(): void {
    this.phaseForm = this.fb.group({
      projetId: ['', Validators.required],
      nom: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      ordre: [1, [Validators.required, Validators.min(1), Validators.max(5)]],
      statut: ['PLANIFIEE', Validators.required],
      dateDebut: ['', Validators.required],
      dateFin: ['', Validators.required],
      progression: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
      budget: [0, [Validators.min(0)]],
    });
  }

  private initProjetForm(): void {
    this.projetForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      type: [TypeProjet.DEVELOPPEMENT, Validators.required],
      statut: [StatutProjet.PLANIFIE, Validators.required],
      priorite: [PrioriteProjet.NORMALE, Validators.required],
      chefProjet: ['', Validators.required],
      client: ['', Validators.required],
      dateDebut: ['', Validators.required],
      dateFinPrevue: ['', Validators.required],
      budget: [0, [Validators.required, Validators.min(0)]],
      documents: [[]],
    });
  }

  private initMissionForm(): void {
    this.missionForm = this.fb.group({
      projetId: ['', Validators.required],
      phaseId: ['', Validators.required],
      nom: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      objectif: ['', [Validators.required, Validators.minLength(10)]],
      statut: [StatutMission.PLANIFIEE, Validators.required],
      priorite: [PrioriteMission.NORMALE, Validators.required],
      dateDebut: ['', Validators.required],
      dateFin: ['', Validators.required],
      progression: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
      budget: [0, [Validators.min(0)]],
      employesAffectes: [[]],
      dependances: [[]],
      livrables: [[]]
    });
  }

  private initTacheForm(): void {
    this.tacheForm = this.fb.group({
      missionId: ['', Validators.required],
      nom: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      statut: [StatutTache.A_FAIRE, Validators.required],
      priorite: [PrioriteTache.NORMALE, Validators.required],
      dateDebut: ['', Validators.required],
      dateFin: ['', Validators.required],
      dureeEstimee: [0, [Validators.required, Validators.min(0)]],
      progression: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
      dependances: [[]],
      fichiers: [[]],
      livrables: [[]], problemes: [[]],
    });
  }

  private initChargeForm(): void {
    this.chargeForm = this.fb.group({
      tacheId: [''],
      nom: ['', Validators.required],
      employeId: ['', Validators.required],
      montant: [0, [Validators.required, Validators.min(1)]],
      categorie: ['', Validators.required],
      sousCategorie: [''],
      description: ['']
    });
  }

  private initCommentaireForm(): void {
    this.commentaireForm = this.fb.group({
      tacheId: ['', Validators.required],
      auteur: ['', Validators.required],
      contenu: ['', [Validators.required, Validators.minLength(5)]],
      type: [TypeCommentaire.GENERAL, Validators.required]
    });
  }

  private initAffectationForm(): void {
    this.affectationForm = this.fb.group({
      targetId: ['', Validators.required],
      employeId: ['', Validators.required],
      role: [RoleProjet.DEVELOPPEUR, Validators.required],
      tauxHoraire: [0, [Validators.min(0)]],
      heuresAllouees: [0, [Validators.min(0)]]
    });
  }

  private showToast(message: string, icon: 'success' | 'error' | 'info' = 'info'): void {
    Swal.fire({
      toast: true,
      position: 'top-end',
      icon: icon,
      title: message,
      showConfirmButton: false,
      timer: 2000,
      timerProgressBar: true
    });
  }

  // Navigation
  setView(view: 'dashboard' | 'projets' | 'phases' | 'missions' | 'taches' | 'charges' | 'details' | 'phase-details' | 'mission-details' | 'tache-details' | 'form' | 'phase-form' | 'mission-form' | 'tache-form' | 'charge-form'): void {
    this.currentView = view;

  }

  goBack(): void {
    if (this.currentView === 'mission-details') {
      this.currentView = 'details';
      this.selectedMission = null;
    } else if (this.currentView === 'tache-details') {
      this.currentView = 'mission-details';
      this.selectedTache = null;
    } else {
      this.currentView = 'projets';
      this.selectedProjet = null;
      this.selectedMission = null;
      this.selectedTache = null;
    }
  }

  //------------------------------------------------------------------- Gestion des projets----------------------------------------------------------//

  viewProjetDetailsPopup(projet: Projet): void {
    this.selectedProjet = projet;
    this.showProjetDetails = true;
  }

  closeProjetDetails(): void {
    this.showProjetDetails = false;
    this.selectedProjet = null;
  }

  projetTree: any;
  viewProjetDetails(projet: Projet): void {
    this.selectedProjet = projet;
    this.currentView = 'details';
    this.getProjetPhases(projet.id);

  }

  showAddProjetForm(): void {
    this.isEditMode = false;
    this.selectedProjet = null;
    this.projetForm.reset({
      nom: '',
      description: '',
      type: TypeProjet.DEVELOPPEMENT,
      statut: StatutProjet.PLANIFIE,
      priorite: PrioriteProjet.NORMALE,
      chefProjet: '',
      client: '',
      dateDebut: this.formatDateForInput(new Date()),
      dateFin: this.formatDateForInput(this.getDefaultDateFin()),
      dateFinPrevue: this.formatDateForInput(this.getDefaultDateFin()),
      budget: 0,
      progression: 0,
      documents: [],
      tags: []
    });
    this.showProjetForm = true;
  }

  editProjet(projet: Projet): void {
    this.isEditMode = true;
    this.selectedProjet = projet;
    this.projetForm.setValue({
      nom: projet.nom,
      description: projet.description,
      type: projet.type,
      statut: projet.statut,
      priorite: projet.priorite,
      chefProjet: projet.chefProjet,
      client: projet.client.id,
      dateDebut: this.formatDateForInput(projet.dateDebut),
      dateFinPrevue: this.formatDateForInput(projet.dateFinPrevue),
      budget: projet.budget,
      documents: projet.documents
    });
    this.showProjetForm = true;
  }

  saveProjet(): void {
    this.submitted = true;

    if (this.projetForm.invalid) {
      this.projetForm.markAllAsTouched();
      return; // ne pas soumettre si formulaire invalide
    }

    const formValue = this.projetForm.getRawValue();
    const projetData = {
      nom: formValue.nom!,
      description: formValue.description!,
      type: formValue.type!,
      statut: formValue.statut!,
      priorite: formValue.priorite!,
      chefProjet: formValue.chefProjet!,
      client: formValue.client!,
      ...formValue,
      dateDebut: new Date(formValue.dateDebut),
      dateFinPrevue: new Date(formValue.dateFinPrevue)
    } as Omit<Projet, "missions" | "id" | "createdAt" | "updatedAt">;

    if (this.isEditMode && this.selectedProjet) {
      this.projetService.updateProjet(this.selectedProjet.id, projetData)
        .subscribe(() => {
          this.showToast('Projet modifié avec succès', 'success');
          this.closeProjetForm();
          this.loadProjet();
          this.initProjetForm();
          this.submitted = false;
        });
    } else {
      this.projetService.addProjet(projetData)
        .subscribe(() => {
          this.showToast('Projet ajouté avec succès', 'success');
          this.closeProjetForm();
          this.loadProjet();
          this.initProjetForm();
          this.submitted = false;
        });
    }
  }

  deleteProjet(id: string): void {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: "Cette action supprimera aussi toutes les missions et tâches associées !",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Oui, supprimer !',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.projetService.deleteProjet(id).subscribe({
          next: () => {
            Swal.fire('Supprimé !', 'Le projet a été supprimé.', 'success');
            this.loadProjet(); // Assure-toi d'avoir une méthode pour recharger la liste des projets
          },
          error: (err) => {
            Swal.fire('Erreur', 'Impossible de supprimer le projet.', 'error');
            console.error(err);
          }
        });
      }
    });
  }

  closeProjetForm(): void {
    this.showProjetForm = false;
    this.isEditMode = false;
    this.selectedProjet = null;
    this.submitted = false;
    this.projetForm.reset();
    this.setView('projets')
  }

  // ------------------------------------------------------------------------ Gestion des phases ----------------------------------------------------------//

  viewPhaseDetails(phase: Phase): void {
    // this.selectedPhase = phase;
    this.currentView = 'phase-details';
    this.getPhaseMissions(phase.id)

  }

  showAddPhaseForm(): void {
    this.submittedPhase = false;
    this.selectedPhase = null;
    this.phaseForm.reset({
      projetId: this.selectedProjet?.id || '',
      nom: '',
      description: '',
      ordre: 1,
      statut: 'PLANIFIEE',
      dateDebut: this.formatDateForInput(new Date()),
      dateFin: this.formatDateForInput(this.getDefaultDateFin()),
      progression: 0,
      budget: 0,
      livrables: []
    });
    this.showPhaseForm = true;
  }

  editPhase(phase: Phase): void {
    this.selectedPhase = phase;
    this.phaseForm.setValue({
      projetId: phase.projet.id,
      nom: phase.nom,
      description: phase.description,
      ordre: phase.ordre,
      statut: phase.statut,
      dateDebut: this.formatDateForInput(phase.dateDebut),
      dateFin: this.formatDateForInput(phase.dateFin),
      progression: phase.progression,
      budget: phase.budget || 0,

    });
    this.showPhaseForm = true;
  }

  savePhase(): void {
    this.submittedPhase = true;
    if (this.phaseForm.invalid) {
      this.phaseForm.markAllAsTouched();
      return; // ne pas soumettre si formulaire invalide
    }

    const formValue = this.phaseForm.getRawValue();
    const phaseData = {
      nom: formValue.nom!,
      description: formValue.description!,
      ordre: Number(formValue.ordre)!,
      statut: formValue.statut!,
      dateDebut: new Date(formValue.dateDebut),
      dateFin: new Date(formValue.dateFin),
      progression: formValue.progression!,
      budget: formValue.budget!,
      projet: formValue.projetId! // Associer la phase au projet sélectionné
    };

    if (this.selectedPhase) {
      let idprojet = this.selectedPhase.projet!.id
      this.phaseService.updatePhase(this.selectedPhase.id, phaseData)
        .subscribe(() => {
          this.showToast('Phase modifiée avec succès', 'success');
          this.closePhaseForm();
          this.loadPhase();
          this.getProjetPhases(idprojet);
          this.initPhaseForm();
          this.submittedPhase = false;
        });
    } else {
      this.phaseService.addPhase(phaseData)
        .subscribe(() => {
          this.showToast('Phase ajoutée avec succès', 'success');
          this.closePhaseForm();
          this.loadPhase();
          this.getProjetPhases(this.selectedProjet!.id);
          this.initPhaseForm();
          this.submittedPhase = false;
        });
    }
  }

  closePhaseForm(): void {
    this.showPhaseForm = false;
    this.selectedPhase = null;
    this.submittedPhase = false;
    this.phaseForm.reset();
    this.setView('phases')

  }

  deletePhase(id: string): void {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: "Cette action supprimera aussi toutes les tâches associées à cette phase !",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Oui, supprimer !',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.phaseService.deletePhase(id).subscribe({
          next: () => {
            Swal.fire('Supprimé !', 'La phase a été supprimée.', 'success');

            this.loadPhase();
          },
          error: (err) => {
            Swal.fire('Erreur', 'Impossible de supprimer la phase.', 'error');
            console.error(err);
          }
        });
      }
    });
  }

  //----------------------------------------------------------------- Gestion des charges---------------------------------------------------------//

  showAddChargeForm(): void {
    this.submittedCharge = false;
    this.chargeForm.reset({
      tacheId: this.selectedTache?.id ?? '',
      nom: '',
      employeId: '',
      montant: 0,
      description: '',
      categorie: '',
      sousCategorie: ''
    });
    this.showSousCategorie = false;
    this.showChargeForm = true;

  }

  addCharge(tache: Tache): void {
    this.selectedTache = tache;
    this.showAddChargeForm();
  }

  onCategorieChange(event: any) {
    const value = event.target.value;
    if (value === 'inclus' || value === 'non_inclus') {
      this.showSousCategorie = true;
      this.sousCategories = [
        'Électrique',
        'Mécanique',
        'Pneumatique',
        'Inox',
        'Fabrication',
        'Composant électrique'
      ];
      this.chargeForm.get('sousCategorie')?.setValidators([Validators.required]);
    } else {
      this.showSousCategorie = false;
      this.chargeForm.get('sousCategorie')?.clearValidators();
      this.chargeForm.get('sousCategorie')?.setValue('');
    }
    this.chargeForm.get('sousCategorie')?.updateValueAndValidity();
  }

  saveCharge(): void {
    this.submittedCharge = true;
    if (this.chargeForm.invalid || !this.selectedTache) {
      this.chargeForm.markAllAsTouched();
      return;
    }
    const formValue = this.chargeForm.getRawValue();
    const chargeData = {
      nom: formValue.nom!,
      tacheId: this.idTache,
      employeId: formValue.employeId!,
      montant: Number(formValue.montant)!,
      description: formValue.description ?? '',
      categorie: formValue.categorie!,
      sousCategorie: formValue.sousCategorie ?? ''
    };

    this.chargeService.addCharge(chargeData).subscribe(() => {
      this.showToast('Charge ajoutée avec succès', 'success');
      this.closeChargeForm();
      this.getTacheDetail(this.idTache);
      this.initChargeForm();
      this.submittedCharge = false;
    });
  }

  editCharge(charge: Charge): void {
    this.selectedTache = this.taches.find(t => t.id === charge.tacheId) || null;

    this.chargeForm.setValue({
      tacheId: charge.tacheId,
      employeId: charge.employeId,
      montant: charge.montant,
      description: charge.description || '',
      categorie: charge.categorie || '',
      sousCategorie: charge.sousCategorie || ''

    });

    this.showChargeForm = true;
  }

  closeChargeForm(): void {
    this.showChargeForm = false;
    this.selectedTache = null;
    this.submittedCharge = false;
    this.chargeForm.reset();
  }

  deleteCharge(id: string): void {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: "Cette action est irréversible !",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Oui, supprimer !',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.chargeService.deleteCharge(id).subscribe(() => {
          Swal.fire('Supprimée !', 'La charge a été supprimée.', 'success');
          this.getTacheDetail(this.idTache);
          this.initChargeForm();
        });
        Swal.fire('Supprimée !', 'La charge a été supprimée.', 'success');
      }
    });
  }



  //---------------------------------------------------------------- Gestion des missions ------------------------------------------------

  viewMissionDetailsPopup(mission: Mission): void {
    this.selectedMission = mission;
    this.showMissionDetails = true;
  }

  closeMissionDetails(): void {
    this.showMissionDetails = false;
    this.selectedMission = null;
  }

  viewMissionDetails(mission: Mission): void {

    this.currentView = 'mission-details';
    this.getMissionTaches(mission.id);
    if (mission.phase?.id) { this.getPhaseMissions(mission.phase?.id) }

  }

  showAddMissionForm(): void {
    this.isEditModeMission = false;
    this.selectedMission = null;
    // const phases = this.selectedProjet ? this.projetService.getPhasesByProjetId(this.selectedProjet.id) : [];
    //  const firstPhaseId = phases.length > 0 ? phases[0].id : '';
    const firstPhaseId = 1
    this.missionForm.reset({
      projetId: this.selectedProjet?.id || '',
      phaseId: firstPhaseId,
      nom: '',
      description: '',
      objectif: '',
      statut: StatutMission.PLANIFIEE,
      priorite: PrioriteMission.NORMALE,
      dateDebut: this.formatDateForInput(new Date()),
      dateFin: this.formatDateForInput(this.getDefaultDateFin()),
      progression: 0,
      budget: 0,
      employesAffectes: [],
      dependances: [],
      livrables: []
    });
    this.showMissionForm = true;
  }

  editMission(mission: Mission): void {

    this.isEditModeMission = true;
    this.selectedMission = mission;
    this.missionForm.patchValue({
      projetId: mission.projet.id,
      phaseId: mission.phase.id,
      nom: mission.nom,
      description: mission.description,
      objectif: mission.objectif,
      statut: mission.statut,
      priorite: mission.priorite,
      dateDebut: this.formatDateForInput(mission.dateDebut),
      dateFin: this.formatDateForInput(mission.dateFin),
      progression: mission.progression,
      budget: mission.budget || 0,
      dependances: mission.dependances,
      livrables: mission.livrables
    });

    this.showMissionForm = true;
  }

  onProjetChange(event: Event) {
    const id = (event.target as HTMLSelectElement).value;
    const projetId = id.split(":")[1].trim();

    if (!projetId) {
      this.phases = [];
      this.missionForm.patchValue({ phaseId: '' });
      return;
    }

    this.projetService.getProjetPhases(projetId).subscribe({
      next: (data) => {

        this.phases = data.phases;

      },
      error: (err) => console.error('Erreur chargement projet + phases', err)
    });



  }

  saveMission(): void {

    this.submittedMission = true;

    // Validation du formulaire
    if (this.missionForm.invalid) {
      this.missionForm.markAllAsTouched();
      return;
    }

    const formValue = this.missionForm.getRawValue();

    // Validation dateFin >= dateDebut
    if (new Date(formValue.dateFin) < new Date(formValue.dateDebut)) {
      this.showToast("La date de fin doit être supérieure ou égale à la date de début", "error");
      return;
    }

    const missionData = {
      nom: formValue.nom!,
      description: formValue.description!,
      objectif: formValue.objectif!,
      statut: formValue.statut!,
      priorite: formValue.priorite!,
      dateDebut: new Date(formValue.dateDebut),
      dateFin: new Date(formValue.dateFin),
      progression: formValue.progression!,
      budget: formValue.budget!,
      projet: formValue.projetId!,
      phase: formValue.phaseId!,

    };

    if (this.selectedMission) {
      // Modifier mission

      let idphase = this.selectedMission.phase.id
      this.missionService.updateMission(this.selectedMission.id, missionData).subscribe({
        next: () => {
          this.showToast('Mission modifiée avec succès', 'success');
          this.closeMissionForm();
          this.getPhaseMissions(idphase);
          this.loadMission();
          this.initMissionForm();
          this.submittedMission = false;
        },
        error: () => this.showToast('Erreur lors de la modification de la mission', 'error')
      });
    } else {
      // Ajouter mission
      this.missionService.addMission(missionData).subscribe({
        next: () => {
          this.showToast('Mission ajoutée avec succès', 'success');
          this.closeMissionForm();
          this.getPhaseMissions(this.selectedPhase?.id!);
          this.loadMission();
          this.initMissionForm();
          this.submittedMission = false;
        },
        error: () => this.showToast('Erreur lors de l\'ajout de la mission', 'error')
      });
    }
  }

  closeMissionForm(): void {
    this.showMissionForm = false;
    this.isEditModeMission = false;
    this.selectedMission = null;
    this.submittedMission = false;
    this.missionForm.reset();
    this.setView('missions')

  }

  deleteMission(id: string): void {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: "Cette action supprimera aussi toutes les tâches associées !",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Oui, supprimer !',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.missionService.deleteMission(id).subscribe({
          next: () => {
            Swal.fire('Supprimée !', 'La mission a été supprimée.', 'success');
            this.loadMission(); // recharge la liste des missions
          },
          error: (err) => {
            Swal.fire('Erreur', 'Impossible de supprimer la mission.', 'error');
            console.error(err);
          }
        });
      }
    });
  }

  // -------------------------------------------------------------------------Gestion des tâches -----------------------------------------------------------------------//

  viewTacheDetailsPopup(tache: Tache): void {
    this.selectedTache = tache;
    this.showTacheDetails = true;
  }

  closeTacheDetails(): void {
    this.showTacheDetails = false;
    this.selectedTache = null;
  }

  viewTacheDetails(tache: Tache): void {
    this.selectedTache = tache;
    this.currentView = 'tache-details';
    this.getTacheDetail(tache.id)
    this.idTache = tache.id
  }

  showAddTacheForm(): void {
    this.isEditModeTache = false;
    this.selectedTache = null;
    this.tacheForm.reset({
      missionId: this.selectedMission?.id || '',
      nom: '',
      description: '',
      statut: StatutTache.A_FAIRE,
      priorite: PrioriteTache.NORMALE,
      dateDebut: this.formatDateForInput(new Date()),
      dateFin: this.formatDateForInput(this.getDefaultDateFin()),
      dureeEstimee: 0,
      progression: 0,
      dependances: [],
      fichiers: []
    });
    this.showTacheForm = true;
  }

  editTache(tache: Tache): void {
    this.isEditModeTache = true;
    this.selectedTache = tache;
    this.tacheForm.setValue({
      missionId: tache.mission?.id,
      nom: tache.nom,
      description: tache.description,
      statut: tache.statut,
      priorite: tache.priorite,
      dateDebut: this.formatDateForInput(tache.dateDebut),
      dateFin: this.formatDateForInput(tache.dateFin),
      dureeEstimee: tache.dureeEstimee,
      progression: tache.progression,
      dependances: tache.dependances || [],
      fichiers: tache.fichiers || [],
      problemes: tache.problemes || [],
      livrables: tache.livrables || []
    });
    this.showTacheForm = true;
  }

  saveTache(): void {
    this.submittedTache = true;

    // Validation du formulaire
    if (this.tacheForm.invalid) {
      this.tacheForm.markAllAsTouched();
      return;
    }

    const formValue = this.tacheForm.getRawValue() as TacheFormValue;

    // Validation dateFin >= dateDebut
    if (new Date(formValue.dateFin) < new Date(formValue.dateDebut)) {
      this.showToast("La date de fin doit être supérieure ou égale à la date de début", "error");
      return;
    }

    const tacheData = {
      nom: formValue.nom!,
      description: formValue.description!,
      priorite: formValue.priorite!,
      statut: formValue.statut!,
      dateDebut: new Date(formValue.dateDebut),
      dateFin: new Date(formValue.dateFin),
      progression: formValue.progression || 0,
      dureeEstimee: formValue.dureeEstimee || 0,
      missionId: this.selectedMission!.id
    };

    if (this.isEditModeTache && this.selectedTache) {
      // Modifier tâche
      this.tacheService.updateTache(this.selectedTache.id, tacheData).subscribe({
        next: () => {
          this.showToast('Tâche modifiée avec succès', 'success');
          this.closeTacheForm();
          if (this.selectedMission?.id) {
            this.getMissionTaches(this.selectedMission.id);
          }
          this.initTacheForm();
          this.submittedTache = false;
        },
        error: () => this.showToast('Erreur lors de la modification de la tâche', 'error')
      });
    } else {
      // Ajouter tâche
      this.tacheService.addTache(tacheData).subscribe({
        next: () => {
          this.showToast('Tâche ajoutée avec succès', 'success');
          this.closeTacheForm();
          if (this.selectedMission?.id) {
            this.getMissionTaches(this.selectedMission.id);
          }
          this.initTacheForm();
          this.submittedTache = false;
        },
        error: () => this.showToast('Erreur lors de l\'ajout de la tâche', 'error')
      });
    }
  }

  closeTacheForm(): void {
    this.showTacheForm = false;
    this.isEditModeTache = false;
    this.selectedTache = null;
    this.submittedTache = false;
    this.tacheForm.reset();
  }

  deleteTache(id: string): void {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: "Cette action supprimera définitivement la tâche !",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Oui, supprimer !',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.tacheService.deleteTache(id).subscribe({
          next: () => {
            Swal.fire('Supprimée !', 'La tâche a été supprimée.', 'success');
            if (this.selectedMission?.id) {
              this.getMissionTaches(this.selectedMission.id);
            }
          },
          error: (err) => {
            Swal.fire('Erreur', 'Impossible de supprimer la tâche.', 'error');
            console.error(err);
          }
        });
      }
    });
  }

  getTacheDetail(id: string) {
    this.tacheService.getTacheById(id).subscribe((tache: any) => {
      this.selectedTache = tache[0];
    });
  }

  // ---------------------------------------------------------------------- Gestion des affectations ---------------------------------------------------------//
  openAffectationForm(type: 'mission' | 'tache', targetId: string): void {
    this.affectationType = type;
    this.affectationForm.reset({
      targetId: targetId,
      employeId: '',
      role: RoleProjet.DEVELOPPEUR,
      tauxHoraire: 0,
      heuresAllouees: 0
    });
    this.showAffectationForm = true;
  }

  saveAffectation(): void {
    this.submittedAffectation = true;

    // Validation du formulaire
    if (this.affectationForm.invalid) {
      this.affectationForm.markAllAsTouched();
      return;
    }
    const formValue = this.affectationForm.getRawValue();
    const affectationData: EmployeAffecte = {
      id: "",
      employeId: formValue.employeId,
      role: formValue.role,
      dateAffectation: new Date(),
      tauxHoraire: formValue.tauxHoraire,
      heuresAllouees: formValue.heuresAllouees,
      heuresRealisees: formValue.heuresRealisees,
      statut: StatutAffectation.ACTIF,
      missionId: this.selectedMission?.id,
      tacheId: this.selectedTache?.id

    };

    this.employeeAffecteService.add(affectationData).subscribe({
      next: () => {
        this.showToast('Employé affecté avec succès', 'success');
        this.closeAffectationForm();
        if (this.selectedMission?.id) {
          this.getMissionTaches(this.selectedMission.id);
        }
        this.initAffectationForm();
        this.submittedAffectation = false;
      },
      error: () => this.showToast('Erreur lors de l\'ajout de l\'affectation', 'error')
    });

  }

  saveAffectationTaches(): void {
    this.submittedAffectation = true;

    // Validation du formulaire
    if (this.affectationForm.invalid) {
      this.affectationForm.markAllAsTouched();
      return;
    }
    const formValue = this.affectationForm.getRawValue();
    const affectationData: EmployeAffecte = {
      id: "",
      employeId: formValue.employeId,
      role: formValue.role,
      dateAffectation: new Date(),
      tauxHoraire: formValue.tauxHoraire,
      heuresAllouees: formValue.heuresAllouees,
      heuresRealisees: formValue.heuresRealisees,
      statut: StatutAffectation.ACTIF,
      missionId: this.selectedMission?.id,
      tacheId: this.selectedTache?.id

    };

    this.employeeAffecteService.add(affectationData).subscribe({
      next: () => {
        this.showToast('Employé affecté avec succès', 'success');
        this.closeAffectationForm();
        if (this.selectedMission?.id) {
          this.getMissionTaches(this.selectedMission.id);
        }
        this.initAffectationForm();
        this.submittedAffectation = false;
      },
      error: () => this.showToast('Erreur lors de l\'ajout de l\'affectation', 'error')
    });

  }

  closeAffectationForm(): void {
    this.showAffectationForm = false;
    this.submittedAffectation = false;
    this.affectationForm.reset();
  }

  desaffecterEmploye(id: string): void {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: "Cet employé sera désaffecté de cette mission !",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Oui, désaffecter !',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.employeeAffecteService.delete(id).subscribe({
          next: () => {
            Swal.fire('Désaffecté !', 'L’employé a été désaffecté avec succès.', 'success');

            if (this.selectedMission?.id) {
              this.getMissionTaches(this.selectedMission.id);
            }
          },
          error: (err) => {
            Swal.fire('Erreur', 'Impossible de désaffecter cet employé.', 'error');
            console.error(err);
          }
        });
      }
    });
  }

  // ------------------------------------------------------------------------Gestion des commentaires ----------------------------------------------------------//
  showAddCommentaireForm(): void {
    this.commentaireForm.reset({
      tacheId: this.selectedTache?.id || '',
      auteur: 'Utilisateur actuel',
      contenu: '',
      type: TypeCommentaire.GENERAL
    });
    this.showCommentaireForm = true;
  }

  saveCommentaire(): void {
    this.submittedCommentaire = true;

    if (this.commentaireForm.invalid) {
      this.commentaireForm.markAllAsTouched();
      return;
    }

    const formValue = this.commentaireForm.getRawValue();

    const commentaireData: Omit<CommentaireTache, 'id'> = {
      tacheId: formValue.tacheId,
      auteur: formValue.auteur,
      contenu: formValue.contenu,
      date: new Date(),
      type: formValue.type
    };

    this.CommentaireService.addCommentaire(commentaireData).subscribe({
      next: () => {
        this.showToast('Commentaire ajouté avec succès', 'success');
        this.closeCommentaireForm();
        this.getTacheDetail(this.idTache); // rafraîchir la liste des commentaires
        this.commentaireForm.reset();      // reset formulaire
        this.submittedCommentaire = false;
      },
      error: (err) => {
        console.error(err);
        this.showToast('Erreur lors de l\'ajout du commentaire', 'error');
        this.submittedCommentaire = false;
      }
    });
  }

  closeCommentaireForm(): void {
    this.showCommentaireForm = false;
    this.submittedCommentaire = false;
    this.commentaireForm.reset();
    this.commentaireForm.reset();
  }

  deleteCommentaire(id: string): void {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: "Cette action supprimera le commentaire !",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Oui, supprimer !',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.CommentaireService.deleteCommentaire(id).subscribe({
          next: () => {
            Swal.fire('Supprimé !', 'Le commentaire a été supprimé.', 'success');
            // 🔄 Actualiser la liste des commentaires après suppression
            this.getTacheDetail(this.idTache); // rafraîchir la liste des commentaires
          },
          error: (err) => {
            Swal.fire('Erreur', 'Impossible de supprimer le commentaire.', 'error');
            console.error(err);
          }
        });
      }
    });
  }

  // ======================= LIVRABLES =======================
  // ===== LIVRABLE =====


  showAddLivrableForm() {
    this.livrableForm.reset({
      tacheId: this.selectedTache?.id,
      dateLivraison: new Date()
    });
    this.showLivrableForm = true;
  }

  saveLivrable() {
    this.submittedLivrable = true;
    if (this.livrableForm.invalid) return;

    const data = this.livrableForm.value;
    this.LivrableService.addLivrable(data).subscribe(() => {
      this.showToast("Livrable ajouté ✅", "success");
      this.closeLivrableForm();
      this.getTacheDetail(this.idTache);
    });
  }

  closeLivrableForm() {
    this.showLivrableForm = false;
    this.submittedLivrable = false;
    this.livrableForm.reset();
  }

  deleteLivrable(id: string): void {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: 'Ce livrable sera définitivement supprimé !',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Oui, supprimer !',
      cancelButtonText: 'Annuler'
    }).then(result => {
      if (result.isConfirmed) {
        this.LivrableService.deleteLivrable(id).subscribe({
          next: () => {
            Swal.fire('Supprimé !', 'Le livrable a été supprimé.', 'success');
            this.getTacheDetail(this.idTache); // 🔄 mise à jour des données
          },
          error: () => {
            Swal.fire('Erreur', 'Impossible de supprimer le livrable.', 'error');
          }
        });
      }
    });
  }


  // ===== PROBLEME =====


  showAddProblemeForm() {
    this.problemeForm.reset({
      tacheId: this.selectedTache?.id
    });
    this.showProblemeForm = true;
  }

  saveProbleme() {
    this.submittedProbleme = true;
    if (this.problemeForm.invalid) return;

    const data = this.problemeForm.value;
    this.ProblemeService.addProbleme(data).subscribe(() => {
      this.showToast("Problème ajouté ✅", "success");
      this.closeProblemeForm();
      this.getTacheDetail(this.idTache);
    });
  }

  closeProblemeForm() {
    this.showProblemeForm = false;
    this.submittedProbleme = false;
    this.problemeForm.reset();
  }

  deleteProbleme(id: string): void {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: 'Ce problème sera définitivement supprimé !',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Oui, supprimer !',
      cancelButtonText: 'Annuler'
    }).then(result => {
      if (result.isConfirmed) {
        this.ProblemeService.deleteProbleme(id).subscribe({
          next: () => {
            Swal.fire('Supprimé !', 'Le problème a été supprimé.', 'success');
            this.getTacheDetail(this.idTache); // 🔄 rafraîchir les données
          },
          error: () => {
            Swal.fire('Erreur', 'Impossible de supprimer le problème.', 'error');
          }
        });
      }
    });
  }



  //------------------------------------------------------------------------------------ Detail ----------------------------------------------------------------//

  // getProjetMissions(projetId: string): Mission[] {
  //   return [] // this.projetService.getMissionsByProjetId(projetId);
  // }

  getMissionTaches(missionId: string) {
    this.missionService.getTachesByMissionId(missionId).subscribe(({ mission, taches, employes }) => {
      this.selectedMission = mission;
      this.selectedMissionEmployee = employes;
      this.selectedMissionTaches = taches;
    });
  }


  getEmployesDisponibles(): EmployeAffecte[] {
    return [] //this.projetService.getEmployesDisponibles();
  }

  getEmployeById(employeId: string): EmployeAffecte | undefined {
    return this.getEmployesDisponibles().find(e => e.employeId === employeId);
  }

  updateProgression(type: 'projet' | 'mission' | 'tache', id: string, progression: number): void {
    if (type === 'projet') {
      this.projetService.updateProjet(id, { progression });
    } else if (type === 'mission') {
      // this.projetService.updateMission(id, { progression });
    } else {
      // this.projetService.updateTache(id, { progression });
    }
  }

  // Filtres et recherche
  get searchTerm_() {
    return this.searchTerm;
  }

  set searchTerm_(value: string) {
    this.searchTerm = value;
    this.filterProjets();
  }

  filterProjets(): void {
    if (!this.searchTerm) {
      this.filteredProjets = this.projets;
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredProjets = this.projets.filter(projet =>
        projet.nom.toLowerCase().includes(term) ||
        projet.description.toLowerCase().includes(term) ||
        projet.chefProjet.toLowerCase().includes(term)
        //  projet.client.toLowerCase().includes(term)
      );
    }
    debugger
  }

  // Classes CSS
  getTypeClass(type: TypeProjet): string {
    return type.toLowerCase().replace(/[^a-z]/g, '');
  }

  getStatusClass(statut: StatutProjet | StatutMission | StatutTache | StatutPhase): string {

    return statut.toLowerCase().replace(/[^a-z]/g, '-');
  }

  getPriorityClass(priorite: PrioriteProjet | PrioriteMission | PrioriteTache): string {
    return priorite.toLowerCase().replace(/[^a-z]/g, '');
  }

  getRoleClass(role: RoleProjet): string {
    return role.toLowerCase().replace(/[^a-z]/g, '-');
  }

  // Vérifications
  isProjetEnRetard(projet: Projet): boolean {
    return projet.statut === StatutProjet.EN_COURS && new Date(projet.dateFinPrevue) < new Date();
  }

  isMissionEnRetard(mission: Mission): boolean {
    return mission.statut === StatutMission.EN_COURS && new Date(mission.dateFin) < new Date();
  }

  isTacheEnRetard(tache: Tache): boolean {
    return tache.statut === StatutTache.EN_COURS && new Date(tache.dateFin) < new Date();
  }

  // Calculs
  calculateMissionProgression(missionId: string): number {
    return 0 //this.projetService.calculateMissionProgression(missionId);
  }

  calculateProjetProgression(projetId: string): number {
    return 0//this.projetService.calculateProjetProgression(projetId);
  }

  getTotalHeuresAllouees(employes: EmployeAffecte[]): number {
    return employes.reduce((total, e) => total + (e.heuresAllouees || 0), 0);
  }

  getTotalHeuresRealisees(employes: EmployeAffecte[]): number {
    return employes.reduce((total, e) => total + (e.heuresRealisees || 0), 0);
  }

  getEmployeInitials(employe: EmployeAffecte): string {
    let initials = '';
    if (employe.employee && employe.employee.prenom && employe.employee.nom) {
      initials = `${employe.employee.prenom.charAt(0)}${employe.employee.nom.charAt(0)}`.toUpperCase();
    }
    return initials
  }

  // Utilitaires de date
  private formatDateForInput(date: Date): string {
    return new Date(date).toISOString().split('T')[0];
  }

  private getDefaultDateFin(): Date {
    const date = new Date();
    date.setMonth(date.getMonth() + 3); // 3 mois par défaut
    return date;
  }

  // Méthode pour appliquer les filtres
  // applyPhaseFilters(): void {
  //   let filtered = [...this.phases];

  //   // Filtrer par terme de recherche
  //   if (this.searchTermPhase) {
  //     const term = this.searchTermPhase.toLowerCase();
  //     filtered = filtered.filter(phase =>
  //       phase.nom.toLowerCase().includes(term) ||
  //       (phase.description?.toLowerCase().includes(term) ?? false)
  //     );
  //   }

  //   // Filtrer par projet
  //   if (this.filterProjetIdPhase) {
  //     const projetId = this.filterProjetIdPhase.toString();
  //     filtered = filtered.filter(phase => phase.projet.id.toString() === projetId);
  //   }

  //   // Filtrer par statut
  //   if (this.filterStatutPhase) {
  //     filtered = filtered.filter(phase => phase.statut === this.filterStatutPhase);
  //   }

  //   this.filteredPhases = filtered;
  // }

  // Réinitialiser les filtres
  // clearPhaseFilters(): void {
  //   this.searchTermPhase = '';
  //   this.filterProjetIdPhase = '';
  //   this.filterStatutPhase = '';
  //   this.filteredPhases = [...this.phases];
  //   this.applyPhaseFilters();

  // }

  // Variables
  // searchTermPhase: string = '';
  // filterProjetIdPhase: string = '';
  // filterStatutPhase: string = '';
  // phases: Phase[] = [];
  // filteredPhases: Phase[] = [];

  // Méthode pour appliquer les filtres
  applyPhaseFilters(): void {
    let filtered = [...this.phases];

    // Filtrer par terme de recherche
    if (this.searchTermPhase) {
      const term = this.searchTermPhase.toLowerCase();
      filtered = filtered.filter(phase =>
        phase.nom.toLowerCase().includes(term) ||
        (phase.description?.toLowerCase().includes(term) ?? false)
      );
    }

    // Filtrer par projet
    if (this.filterProjetIdPhase) {
      const projetId = this.filterProjetIdPhase.toString();
      filtered = filtered.filter(phase => phase.projet.id.toString() === projetId);
    }

    // Filtrer par statut
    if (this.filterStatutPhase) {
      filtered = filtered.filter(phase => phase.statut === this.filterStatutPhase);
    }

    this.filteredPhases = filtered;
  }

  // Réinitialiser les filtres
  clearPhaseFilters(): void {
    this.searchTermPhase = '';
    this.filterProjetIdPhase = '';
    this.filterStatutPhase = '';
    this.filteredPhases = [...this.phases];
  }


  applyMissionFilters(): void {
    let filtered = [...this.missions];
    const term = this.searchTermMission?.toLowerCase() ?? '';
    
    // Filtrer par terme de recherche
    if (term) {
      filtered = filtered.filter(mission =>
        mission.nom?.toLowerCase().includes(term) ||
        (mission.description?.toLowerCase().includes(term) ?? false) ||
        (mission.objectif?.toLowerCase().includes(term) ?? false)
      );
    }
    // Filtrer par projet
    if (this.filterProjetIdMission) {
      const projetId = this.filterProjetIdMission.toString();
      filtered = filtered.filter(
        mission => mission.projet.id != null && mission.projet.id.toString() === projetId
      );
    }
     // Filtrer par phase
    if (this.filterPhaseIdMission) {
      const phaseId = this.filterPhaseIdMission.toString();
      filtered = filtered.filter(
        mission => mission.phase.id != null && mission.phase.id.toString() === phaseId
      );
    }
     // Filtrer par statut
    if (this.filterStatutMission) {
      filtered = filtered.filter(
        mission => mission.statut === this.filterStatutMission
      );
    }

    this.filteredMissions = filtered;
  }

  // Réinitialiser les filtres



  // Réinitialiser les filtres
  clearMissionFilters(): void {
    this.searchTermMission = '';
    this.filterProjetIdMission = '';
    this.filterPhaseIdMission = '';
    this.filterStatutMission = '';
    this.filteredMissions = [...this.missions];
    this.applyMissionFilters();

  }


  applyTacheFilters(): void {
    let filtered = [...this.taches];

    // Filtrer par terme de recherche
    if (this.searchTermTache) {
      const term = this.searchTermTache.toLowerCase();
      filtered = filtered.filter(tache =>
        tache.nom.toLowerCase().includes(term) ||
        tache.description.toLowerCase().includes(term)
      );
    }

    // Filtrer par mission
    if (this.filterMissionIdTache) {
      filtered = filtered.filter(tache => tache.missionId === this.filterMissionIdTache);
    }

    // Filtrer par statut
    if (this.filterStatutTache) {
      filtered = filtered.filter(tache => tache.statut === this.filterStatutTache);
    }

    this.filteredTaches = filtered;
  }

  applyChargeFilters(): void {
    let filtered = [...this.charges];

    // Filtrer par terme de recherche
    if (this.searchTermCharge) {
      const term = this.searchTermCharge.toLowerCase();
      // filtered = filtered.filter(charge =>
      //  // charge.nom.toLowerCase().includes(term) ||
      //  // charge.prenom.toLowerCase().includes(term) ||
      //  // charge.poste.toLowerCase().includes(term)
      // );
    }

    // Filtrer par tâche
    if (this.filterTacheIdCharge) {
      filtered = filtered.filter(charge => charge.tacheId === this.filterTacheIdCharge);
    }

    // Filtrer par employé
    if (this.filterEmployeIdCharge) {
      filtered = filtered.filter(charge => charge.employeId === this.filterEmployeIdCharge);
    }

    this.filteredCharges = filtered;
  }



  // clearMissionFilters(): void {
  //   this.searchTermMission = '';
  //   this.filterPhaseIdMission = '';
  //   this.filterStatutMission = '';
  //   this.applyMissionFilters();
  // }

  clearTacheFilters(): void {
    this.searchTermTache = '';
    this.filterMissionIdTache = '';
    this.filterStatutTache = '';
    this.applyTacheFilters();
  }

  clearChargeFilters(): void {
    this.searchTermCharge = '';
    this.filterTacheIdCharge = '';
    this.filterEmployeIdCharge = '';
    this.applyChargeFilters();
  }

  // Validation personnalisée
  get f() { return this.projetForm.controls; }
  get fph() { return this.phaseForm.controls; }
  get fm() { return this.missionForm.controls; }
  get ft() { return this.tacheForm.controls; }
  get fch() { return this.chargeForm.controls; }
  get fc() { return this.commentaireForm.controls; }
  get fa() { return this.affectationForm.controls; }
  get fp() { return this.problemeForm.controls; }
  get fl() { return this.livrableForm.controls; }

  getViewTitle(): string {
    switch (this.currentView) {
      case 'projets': return 'Liste des Projets';
      case 'phases': return 'Gestion des Phases';
      case 'missions': return 'Gestion des Missions';
      case 'taches': return 'Gestion des Tâches';
      case 'charges': return 'Gestion des Charges';
      case 'details': return this.selectedProjet?.nom || 'Détails du Projet';
      case 'phase-details': return this.selectedPhase?.nom || 'Détails de la Phase';
      case 'mission-details': return this.selectedMission?.nom || 'Détails de la Mission';
      case 'tache-details': return this.selectedTache?.nom || 'Détails de la Tâche';
      default: return 'Gestion des Projets';
    }
  }

  getProjetPhases(projetId: string) {
    this.projetService.getProjetPhases(projetId).subscribe({
      next: (data) => {
        this.projetTree = data.projet;  // Projet complet
        this.selectedProjetPhases = data.phases;      // Liste des phases

      },
      error: (err) => console.error('Erreur chargement projet + phases', err)
    });

  }

  getPhaseName(phaseId: string): string {
    const phase = this.phases.find(p => p.id === phaseId);
    return phase ? phase.nom : 'Phase inconnue';
  }

  getProjetName = (projetId: string): string => {
    const projet = this.projets.find(p => p.id === projetId);
    return projet ? projet.nom : 'Projet inconnu';
  }

  getMissionName = (missionId: string): string => {
    const mission = this.missions.find(m => m.id === missionId);
    return mission ? mission.nom : 'Mission inconnue';
  }

  getTacheName = (tacheId: string): string => {
    const tache = this.taches.find(t => t.id === tacheId);
    return tache ? tache.nom : 'Tâche inconnue';
  }

  getChargeEmployeeInitials(charge: Charge): string {
    return "--"// `${charge.prenom.charAt(0)}${charge.nom.charAt(0)}`.toUpperCase();
  }


  getPhaseMissions(phaseId: string) {
    this.phaseService.getPhaseMissions(phaseId).subscribe(({ phase, missions }) => {
      this.selectedPhase = phase;
      this.selectedPhaseMission = missions;

    });
  }

  getTacheCharges(tacheId: string): Charge[] {
    return [] //this.projetService.getChargesByTacheId(tacheId);
  }


  // Méthodes pour les recherches avec getter/setter
  get searchTermPhase_() {
    return this.searchTermPhase;
  }

  set searchTermPhase_(value: string) {
    this.searchTermPhase = value;
    this.applyPhaseFilters();
  }

  get searchTermMission_() {
    return this.searchTermMission;
  }

  set searchTermMission_(value: string) {
    this.searchTermMission = value;
    this.applyMissionFilters();
  }

  get searchTermTache_() {
    return this.searchTermTache;
  }

  set searchTermTache_(value: string) {
    this.searchTermTache = value;
    this.applyTacheFilters();
  }

  get searchTermCharge_() {
    return this.searchTermCharge;
  }

  set searchTermCharge_(value: string) {
    this.searchTermCharge = value;
    this.applyChargeFilters();
  }

  getProjetStats(projets: Projet[]) {

    this.totalProjet = projets.length
    projets.forEach(p => {
      this.budgetTotal += p.budget || 0;
      switch (p.statut?.toUpperCase()) {
        case "PLANIFIE":
          this.planifie++;
          break;
        case "EN_COURS":
          this.encours++;
          break;
        case "TERMINE":
          this.termine++;
          break;
      }
    });
  }

  PDF(id: string) {
    this.projetService.getProjetById(id).subscribe((projets: any) => {
      const projet = projets[0][0]; // ou projets[0] selon le retour exact
      if (!projet) return;

      const printWindow = window.open('', '_blank');
      if (!printWindow) return;

      const html = `
<html>
<head>
  <title>Rapport - Projet ${projet.nom}</title>
  <style>
    body { font-family: 'Segoe UI', Arial, sans-serif; margin: 40px; color: #1C1C1C; background-color: #fff; line-height: 1.6; }
    h1, h2, h3, h4, h5 { margin-bottom: 8px; font-weight: 600; }
    h1 { color: #0B3C5D; border-bottom: 3px solid #0B3C5D; padding-bottom: 8px; }
    h2 { color: #1E8449; margin-top: 25px; border-left: 5px solid #117A65; padding-left: 10px; }
    h3 { color: #0E6655; margin-top: 20px; }
    h4 { color: #154360; margin-top: 15px; }
    h5 { color: #7B241C; margin-top: 12px; }
    p, li { font-size: 14px; color: #2C3E50; }
    table { width: 100%; border-collapse: collapse; margin-top: 10px; margin-bottom: 25px; display:block; overflow-x:auto; }
    th, td { border: 1px solid #BFC9CA; padding: 6px 8px; text-align: left; font-size: 13px; }
    th { background-color: #EAF2F8; color: #0B3C5D; }
    tr:nth-child(even) { background-color: #F8F9F9; }
    .section { margin-top: 25px; }
    .mission, .phase { margin-left: 20px; }
    .header-info { margin-bottom: 20px; background: #F4F6F7; padding: 15px; border-left: 5px solid #0B3C5D; border-radius: 6px; }
    .client { background: #F8F9F9; padding: 12px; border-radius: 6px; margin-top: 10px; }
    .signature { text-align: right; margin-top: 60px; font-style: italic; color: #7B7D7D; }

    /* Styles simplifiés pour charges, livrables, problèmes et commentaires */
    .charge, .livrable, .probleme, .commentaire {
      padding: 8px 12px;
      margin: 5px 0;
      border-left: 4px solid #0B3C5D;
      background-color: #F4F6F7;
      border-radius: 4px;
      font-size: 13px;
      color: #1C1C1C;
    }
    .charge strong, .livrable strong, .probleme strong, .commentaire strong {
      color: #0B3C5D;
    }
    .commentaire ul {
      padding-left: 20px;
      margin: 5px 0;
      list-style-type: disc;
    }
  </style>
</head>
<body>
  <h1>Rapport du Projet : ${projet.nom}</h1>

  <div class="header-info">
    <p><strong>Description :</strong> ${projet.description || '-'}</p>
    <p><strong>Type :</strong> ${projet.type || '-'} |
       <strong>Statut :</strong> ${projet.statut || '-'} |
       <strong>Priorité :</strong> ${projet.priorite || '-'}</p>
    <p><strong>Chef de projet :</strong> ${projet.chefProjet || '-'}</p>
  </div>

  <div class="section">
    <h2>Client</h2>
    <div class="client">
      <p><strong>Nom :</strong> ${projet.client?.nom || '-'}<br>
      <strong>Email :</strong> ${projet.client?.email || '-'}<br>
      <strong>Téléphone :</strong> ${projet.client?.telephone || '-'}</p>
    </div>
  </div>

  <div class="section">
    <h2>Phases et Missions</h2>
    ${projet.phases?.map((phase: any) => `
      <div class="phase">
        <h3>Phase : ${phase.nom} <small>[${phase.statut}]</small></h3>
        <p><em>${phase.description || ''}</em></p>
        <p><strong>Ordre :</strong> ${phase.ordre} |
           <strong>Progression :</strong> ${phase.progression}% |
           <strong>Budget :</strong> ${phase.budget} DT</p>

        ${phase.missions?.map((mission: any) => `
          <div class="mission">
            <h4>Mission : ${mission.nom} <small>[${mission.statut}]</small></h4>
            <p>${mission.description || ''}</p>
            <p><strong>Objectif :</strong> ${mission.objectif || '-'} |
               <strong>Priorité :</strong> ${mission.priorite || '-'}</p>

            ${mission.taches?.map((t: any) => t.nom ? `
              <h5>Tâche : ${t.nom}</h5>
              <table>
                <thead>
                  <tr>
                    <th>Nom</th>
                    <th>Description</th>
                    <th>Statut</th>
                    <th>Progression</th>
                    <th>Durée Estimée</th>
                    <th>Durée Réelle</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>${t.nom}</td>
                    <td>${t.description}</td>
                    <td>${t.statut}</td>
                    <td>${t.progression}%</td>
                    <td>${t.dureeEstimee || '-'}</td>
                    <td>${t.dureeReelle || '-'}</td>
                  </tr>
                </tbody>
              </table>

              ${t.commentaires?.length ? `
                <h5>Commentaires</h5>
                <div class="commentaire">
                  <ul>
                    ${t.commentaires.map((c: any) => `<li><strong>${c.auteur}</strong> : ${c.contenu}</li>`).join('')}
                  </ul>
                </div>
              ` : ''}

              ${t.charges?.length ? `
                <h5>Charges</h5>
                ${t.charges.map((ch: any) => `
                  <div class="charge">
                    <p><strong>${ch.nom}</strong> — ${ch.montant} DT</p>
                    <p><strong>Catégorie :</strong> ${ch.categorie || '-'} |
                       <strong>Sous-catégorie :</strong> ${ch.sousCategorie || '-'}</p>
                    <p><em>${ch.description || ''}</em></p>
                  </div>
                `).join('')}
              ` : ''}

              ${t.livrable?.length ? `
                <h5>Livrables</h5>
                ${t.livrable.map((l: any) => `
                  <div class="livrable">
                    <p><strong>${l.nom}</strong> — Livraison : ${l.dateLivraison || '-'}</p>
                    <p>${l.description || '-'}</p>
                  </div>
                `).join('')}
              ` : ''}

              ${t.probleme?.length ? `
                <h5>Problèmes</h5>
                ${t.probleme.map((p: any) => `
                  <div class="probleme">
                    <p><strong>${p.nom}</strong> — Détection : ${p.dateDetection || '-'}</p>
                    <p>${p.description || '-'}</p>
                  </div>
                `).join('')}
              ` : ''}

            ` : '').join('')}
          </div>
        `).join('')}
      </div>
    `).join('')}
  </div>

  <div class="signature">
    <p>Généré automatiquement le ${new Date().toLocaleDateString()}</p>
    <p>_________________________<br>
    <strong>${projet.chefProjet || 'Chef de projet'}</strong></p>
  </div>
</body>
</html>
`;

      printWindow.document.write(html);
      printWindow.document.close();
      printWindow.print();
    });
  }

  PDFMission(id: string) {
    this.missionService.getMissionById(id).subscribe((missions: any) => {
      const mission = missions[0][0]; // Ajuste selon ton retour exact
      if (!mission) return;

      const printWindow = window.open('', '_blank');
      if (!printWindow) return;

      const html = `
<html>
<head>
  <title>Rapport - Mission ${mission.nom}</title>
  <style>
    body { font-family: 'Segoe UI', Arial, sans-serif; margin: 40px; color: #1C1C1C; background-color: #fff; line-height: 1.6; }
    h1, h2, h3, h4, h5 { margin-bottom: 8px; font-weight: 600; }
    h1 { color: #0B3C5D; border-bottom: 3px solid #0B3C5D; padding-bottom: 8px; }
    h2 { color: #1E8449; margin-top: 25px; border-left: 5px solid #117A65; padding-left: 10px; }
    h3 { color: #0E6655; margin-top: 20px; }
    h4 { color: #154360; margin-top: 15px; }
    h5 { color: #7B241C; margin-top: 12px; }
    p, li { font-size: 14px; color: #2C3E50; }
    table { width: 100%; border-collapse: collapse; margin-top: 10px; margin-bottom: 25px; display:block; overflow-x:auto; }
    th, td { border: 1px solid #BFC9CA; padding: 6px 8px; text-align: left; font-size: 13px; }
    th { background-color: #EAF2F8; color: #0B3C5D; }
    tr:nth-child(even) { background-color: #F8F9F9; }
    .section { margin-top: 25px; }
    .commentaire, .charge, .livrable, .probleme {
      padding: 8px 12px; margin: 5px 0; border-left: 4px solid #0B3C5D;
      background-color: #F4F6F7; border-radius: 4px; font-size: 13px; color: #1C1C1C;
    }
    .signature { text-align: right; margin-top: 60px; font-style: italic; color: #7B7D7D; }
  </style>
</head>
<body>
  <h1>Rapport de la Mission : ${mission.nom}</h1>

  <div class="section">
    <h2>Informations Mission</h2>
    <p><strong>Description:</strong> ${mission.description || '-'}</p>
    <p><strong>Objectif:</strong> ${mission.objectif || '-'}</p>
    <p><strong>Statut:</strong> ${mission.statut || '-'}</p>
    <p><strong>Priorité:</strong> ${mission.priorite || '-'}</p>
  </div>

  <div class="section">
    <h2>Tâches de la Mission</h2>
    ${mission.taches?.map((t: any) => `
      <div class="tache">
        <h4>Tâche : ${t.nom} <small>[${t.statut}]</small></h4>
        <p>${t.description || '-'}</p>

        <table>
          <thead>
            <tr>
              <th>Nom</th>
              <th>Statut</th>
              <th>Progression</th>
              <th>Durée Estimée</th>
              <th>Durée Réelle</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>${t.nom}</td>
              <td>${t.statut}</td>
              <td>${t.progression || 0}%</td>
              <td>${t.dureeEstimee || '-'}</td>
              <td>${t.dureeReelle || '-'}</td>
            </tr>
          </tbody>
        </table>

        ${t.commentaires?.length ? `
          <h5>Commentaires</h5>
          ${t.commentaires.map((c: any) => `
            <div class="commentaire"><strong>${c.auteur}</strong> : ${c.contenu}</div>
          `).join('')}
        ` : ''}

        ${t.charges?.length ? `
          <h5>Charges</h5>
          ${t.charges.map((ch: any) => `
            <div class="charge"><strong>${ch.nom}</strong> — ${ch.montant} DT<br>${ch.description || ''}</div>
          `).join('')}
        ` : ''}

        ${t.livrable?.length ? `
          <h5>Livrables</h5>
          ${t.livrable.map((l: any) => `
            <div class="livrable"><strong>${l.nom}</strong> — ${l.dateLivraison || '-'}</div>
          `).join('')}
        ` : ''}

        ${t.probleme?.length ? `
          <h5>Problèmes</h5>
          ${t.probleme.map((p: any) => `
            <div class="probleme"><strong>${p.nom}</strong> — ${p.dateDetection || '-'}</div>
          `).join('')}
        ` : ''}
      </div>
    `).join('')}
  </div>

  <div class="signature">
    <p>Généré le ${new Date().toLocaleDateString()}</p>
    <p>Signature Chef Projet : _________________________</p>
  </div>

</body>
</html>
`;

      printWindow.document.write(html);
      printWindow.document.close();
      printWindow.print();
    });
  }

}






