import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SousTraitanceService } from '../../services/sous-traitance.service';
import { PrestataireService } from '../../services/prestataire.service';
import { 
  SousTraitance, 
  InterventionSousTraitance, 
  TypeSousTraitance, 
  PrioriteSousTraitance, 
  StatutSousTraitance, 
  StatutIntervention,
  SousTraitanceStats 
} from '../../models/sous-traitance.model';
import { 
  Prestataire, 
  ContactPrestataire, 
  EvaluationPrestataire, 
  ContratPrestataire, 
  SecteurPrestataire, 
  TypePrestataire, 
  StatutPrestataire, 
  TypeContratPrestataire, 
  StatutContratPrestataire, 
  PrestataireStats 
} from '../../models/prestataire.model';

@Component({
  selector: 'app-sous-traitance-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sous-traitance-management.component.html',
  styleUrls: ['./sous-traitance-management.component.css']
})
export class SousTraitanceManagementComponent implements OnInit {
  sousTraitances: SousTraitance[] = [];
  filteredSousTraitances: SousTraitance[] = [];
  interventions: InterventionSousTraitance[] = [];
  filteredInterventions: InterventionSousTraitance[] = [];
  prestataires: Prestataire[] = [];
  filteredPrestataires: Prestataire[] = [];
  prestataireStats: PrestataireStats = {
    totalPrestataires: 0,
    prestatairesActifs: 0,
    contratsActifs: 0,
    montantContrats: 0,
    noteMoyenne: 0,
    specialitesUniques: 0,
    tauxSatisfaction: 0,
    interventionsRecentes: 0
  };
  stats: SousTraitanceStats = {
    totalSousTraitances: 0,
    sousTraitancesEnCours: 0,
    sousTraitancesPlanifiees: 0,
    coutTotalSousTraitance: 0,
    nombreSousTraitants: 0,
    tauxSatisfaction: 0
  };

  currentView: 'dashboard' | 'sous-traitances' | 'interventions' | 'prestataires' = 'dashboard';
  selectedSousTraitance: SousTraitance | null = null;
  selectedIntervention: InterventionSousTraitance | null = null;
  searchTerm = '';
  searchTermIntervention = '';
  filterType = '';
  filterStatut = '';
  filterPrestataire = '';
  filterSousTraitanceId = '';
  filterStatutIntervention = '';
  searchTermPrestataire = '';
  filterSecteurPrestataire = '';
  filterStatutPrestataire = '';
  filterTypePrestataire = '';

  // Popups et formulaires
  showSousTraitanceForm = false;
  showInterventionForm = false;
  showSousTraitanceDetails = false;
  showInterventionDetails = false;
  showPrestataireForm = false;
  showPrestataireDetails = false;
  showEvaluationForm = false;
  isEditMode = false;
  isEditModeIntervention = false;
  isEditModePrestataire = false;
  selectedPrestataire: Prestataire | null = null;

  // Données des formulaires
  sousTraitanceFormData: any = {};
  interventionFormData: any = {};
  prestataireFormData: any = {};
  evaluationFormData: any = {};

  // Énumérations
  typesSousTraitance = Object.values(TypeSousTraitance);
  prioritesSousTraitance = Object.values(PrioriteSousTraitance);
  statutsSousTraitance = Object.values(StatutSousTraitance);
  statutsIntervention = Object.values(StatutIntervention);
  secteursPrestataire = Object.values(SecteurPrestataire);
  typesPrestataire = Object.values(TypePrestataire);
  statutsPrestataire = Object.values(StatutPrestataire);
  typesContratPrestataire = Object.values(TypeContratPrestataire);
  statutsContratPrestataire = Object.values(StatutContratPrestataire);
  
  // Exposition des enums pour le template
  StatutSousTraitance = StatutSousTraitance;

  constructor(
    private sousTraitanceService: SousTraitanceService,
    private prestataireService: PrestataireService,
    private router: Router
  ) {}

  goBackToMenu(): void {
    this.router.navigate(['/menu']);
  }

  ngOnInit(): void {
    this.sousTraitanceService.getSousTraitances().subscribe(sousTraitances => {
      this.sousTraitances = sousTraitances;
      this.filteredSousTraitances = sousTraitances;
      this.stats = this.sousTraitanceService.getSousTraitanceStats();
    });

    this.sousTraitanceService.interventions$.subscribe(interventions => {
      this.interventions = interventions;
      this.filteredInterventions = interventions;
    });

    this.prestataireService.getPrestataires().subscribe(prestataires => {
      this.prestataires = prestataires;
      this.filteredPrestataires = prestataires;
      this.prestataireStats = this.prestataireService.getPrestataireStats();
    });
  }

  // Navigation
  setView(view: 'dashboard' | 'sous-traitances' | 'interventions' | 'prestataires'): void {
    this.currentView = view;
  }

  // Gestion des sous-traitances
  viewSousTraitanceDetails(sousTraitance: SousTraitance): void {
    this.selectedSousTraitance = sousTraitance;
    this.showSousTraitanceDetails = true;
  }

  closeSousTraitanceDetails(): void {
    this.showSousTraitanceDetails = false;
    this.selectedSousTraitance = null;
  }

  showAddSousTraitanceForm(): void {
    this.isEditMode = false;
    this.selectedSousTraitance = null;
    this.sousTraitanceFormData = {
      titre: '',
      description: '',
      type: TypeSousTraitance.INFORMATIQUE,
      priorite: PrioriteSousTraitance.NORMALE,
      statut: StatutSousTraitance.PLANIFIEE,
      sousTraitant: '',
      contactSousTraitant: '',
      telephoneSousTraitant: '',
      emailSousTraitant: '',
      domaine: '',
      emplacement: '',
      demandeur: 'admin',
      dateDebutPrevue: this.formatDateForInput(new Date()),
      dateFinPrevue: this.formatDateForInput(this.getDefaultDateFin()),
      coutEstime: 0,
      prestations: []
    };
    this.showSousTraitanceForm = true;
  }

  editSousTraitance(sousTraitance: SousTraitance): void {
    this.isEditMode = true;
    this.selectedSousTraitance = sousTraitance;
    this.sousTraitanceFormData = {
      ...sousTraitance,
      dateDebutPrevue: this.formatDateForInput(sousTraitance.dateDebutPrevue),
      dateFinPrevue: this.formatDateForInput(sousTraitance.dateFinPrevue),
      dateDebutEffective: sousTraitance.dateDebutEffective ? this.formatDateForInput(sousTraitance.dateDebutEffective) : '',
      dateFinEffective: sousTraitance.dateFinEffective ? this.formatDateForInput(sousTraitance.dateFinEffective) : ''
    };
    this.showSousTraitanceForm = true;
  }

  saveSousTraitance(): void {
    const sousTraitanceData = {
      ...this.sousTraitanceFormData,
      dateDebutPrevue: new Date(this.sousTraitanceFormData.dateDebutPrevue),
      dateFinPrevue: new Date(this.sousTraitanceFormData.dateFinPrevue),
      dateDebutEffective: this.sousTraitanceFormData.dateDebutEffective ? new Date(this.sousTraitanceFormData.dateDebutEffective) : undefined,
      dateFinEffective: this.sousTraitanceFormData.dateFinEffective ? new Date(this.sousTraitanceFormData.dateFinEffective) : undefined
    };

    if (this.isEditMode && this.selectedSousTraitance) {
      this.sousTraitanceService.updateSousTraitance(this.selectedSousTraitance.id, sousTraitanceData);
    } else {
      this.sousTraitanceService.addSousTraitance(sousTraitanceData);
    }

    this.closeSousTraitanceForm();
  }

  closeSousTraitanceForm(): void {
    this.showSousTraitanceForm = false;
    this.isEditMode = false;
    this.selectedSousTraitance = null;
    this.sousTraitanceFormData = {};
  }

  deleteSousTraitance(id: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette sous-traitance ?')) {
      this.sousTraitanceService.deleteSousTraitance(id);
    }
  }

  // Gestion des interventions
  viewInterventionDetails(intervention: InterventionSousTraitance): void {
    this.selectedIntervention = intervention;
    this.showInterventionDetails = true;
  }

  closeInterventionDetails(): void {
    this.showInterventionDetails = false;
    this.selectedIntervention = null;
  }

  showAddInterventionForm(): void {
    this.isEditModeIntervention = false;
    this.selectedIntervention = null;
    this.interventionFormData = {
      sousTraitanceId: this.selectedSousTraitance?.id || '',
      technicien: '',
      dateIntervention: this.formatDateForInput(new Date()),
      duree: 0,
      description: '',
      statut: StatutIntervention.PREVUE,
      observations: ''
    };
    this.showInterventionForm = true;
  }

  addIntervention(sousTraitance: SousTraitance): void {
    this.selectedSousTraitance = sousTraitance;
    this.showAddInterventionForm();
  }

  editIntervention(intervention: InterventionSousTraitance): void {
    this.isEditModeIntervention = true;
    this.selectedIntervention = intervention;
    this.interventionFormData = {
      ...intervention,
      dateIntervention: this.formatDateForInput(intervention.dateIntervention)
    };
    this.showInterventionForm = true;
  }

  saveIntervention(): void {
    const interventionData = {
      ...this.interventionFormData,
      dateIntervention: new Date(this.interventionFormData.dateIntervention)
    };

    if (this.isEditModeIntervention && this.selectedIntervention) {
      // Mise à jour via le service (à implémenter)
      console.log('Mise à jour intervention:', interventionData);
    } else {
      this.sousTraitanceService.addIntervention(interventionData);
    }

    this.closeInterventionForm();
  }

  closeInterventionForm(): void {
    this.showInterventionForm = false;
    this.isEditModeIntervention = false;
    this.selectedIntervention = null;
    this.interventionFormData = {};
  }

  deleteIntervention(id: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette intervention ?')) {
      // Suppression via le service (à implémenter)
      console.log('Supprimer intervention:', id);
    }
  }

  // Filtres et recherche
  get searchTerm_() {
    return this.searchTerm;
  }

  set searchTerm_(value: string) {
    this.searchTerm = value;
    this.filterSousTraitances();
  }

  filterSousTraitances(): void {
    let filtered = [...this.sousTraitances];

    // Filtre par terme de recherche
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(sousTraitance =>
        sousTraitance.titre.toLowerCase().includes(term) ||
        sousTraitance.sousTraitant.toLowerCase().includes(term) ||
        sousTraitance.domaine.toLowerCase().includes(term)
      );
    }

    // Filtre par type
    if (this.filterType) {
      filtered = filtered.filter(sousTraitance => sousTraitance.type === this.filterType);
    }

    // Filtre par statut
    if (this.filterStatut) {
      filtered = filtered.filter(sousTraitance => sousTraitance.statut === this.filterStatut);
    }

    // Filtre par prestataire
    if (this.filterPrestataire) {
      const term = this.filterPrestataire.toLowerCase();
      filtered = filtered.filter(sousTraitance =>
        sousTraitance.sousTraitant.toLowerCase().includes(term)
      );
    }

    this.filteredSousTraitances = filtered;
  }

  applyInterventionFilters(): void {
    let filtered = [...this.interventions];

    // Filtre par terme de recherche
    if (this.searchTermIntervention) {
      const term = this.searchTermIntervention.toLowerCase();
      filtered = filtered.filter(intervention =>
        intervention.technicien.toLowerCase().includes(term) ||
        intervention.description.toLowerCase().includes(term)
      );
    }

    // Filtre par sous-traitance
    if (this.filterSousTraitanceId) {
      filtered = filtered.filter(intervention => intervention.sousTraitanceId === this.filterSousTraitanceId);
    }

    // Filtre par statut
    if (this.filterStatutIntervention) {
      filtered = filtered.filter(intervention => intervention.statut === this.filterStatutIntervention);
    }

    this.filteredInterventions = filtered;
  }

  clearSousTraitanceFilters(): void {
    this.searchTerm = '';
    this.filterType = '';
    this.filterStatut = '';
    this.filterPrestataire = '';
    this.filterSousTraitances();
  }

  clearInterventionFilters(): void {
    this.searchTermIntervention = '';
    this.filterSousTraitanceId = '';
    this.filterStatutIntervention = '';
    this.applyInterventionFilters();
  }

  // Méthodes utilitaires
  getSousTraitanceInterventions(sousTraitanceId: string): InterventionSousTraitance[] {
    return this.sousTraitanceService.getInterventionsBySousTraitanceId(sousTraitanceId);
  }

  getSousTraitanceName(sousTraitanceId: string): string {
    const sousTraitance = this.sousTraitances.find(s => s.id === sousTraitanceId);
    return sousTraitance ? sousTraitance.titre : 'Sous-traitance inconnue';
  }

  getPrestataires(): string[] {
    const prestataires = [...new Set(this.sousTraitances.map(s => s.sousTraitant))];
    return prestataires.sort();
  }

  getTypeClass(type: TypeSousTraitance): string {
    return type.toLowerCase().replace(/[^a-z]/g, '');
  }

  getPriorityClass(priorite: PrioriteSousTraitance): string {
    return priorite.toLowerCase().replace(/[^a-z]/g, '');
  }

  getStatusClass(statut: StatutSousTraitance | StatutIntervention): string {
    return statut.toLowerCase().replace(/[^a-z]/g, '-');
  }

  isOverdue(sousTraitance: SousTraitance): boolean {
    return sousTraitance.statut === StatutSousTraitance.EN_COURS && 
           new Date(sousTraitance.dateFinPrevue) < new Date();
  }

  getTotalCost(sousTraitance: SousTraitance): number {
    const coutPrestations = sousTraitance.prestations.reduce((total, p) => 
      total + (p.quantite * p.prixUnitaire), 0);
    return (sousTraitance.coutReel || sousTraitance.coutEstime) + coutPrestations;
  }

  getInitials(nom: string): string {
    const words = nom.split(' ');
    if (words.length >= 2) {
      return `${words[0].charAt(0)}${words[1].charAt(0)}`.toUpperCase();
    }
    return nom.substring(0, 2).toUpperCase();
  }

  private formatDateForInput(date: Date): string {
    return new Date(date).toISOString().split('T')[0];
  }

  private getDefaultDateFin(): Date {
    const date = new Date();
    date.setMonth(date.getMonth() + 1); // 1 mois par défaut
    return date;
  }

  getViewTitle(): string {
    switch (this.currentView) {
      case 'sous-traitances': return 'Gestion des Contrats';
      case 'interventions': return 'Gestion des Interventions';
      case 'prestataires': return 'Gestion des Prestataires';
      default: return 'Gestion Sous-Traitance';
    }
  }

  getViewFeatures(): string[] {
    switch (this.currentView) {
      case 'sous-traitances':
        return [
          'Création de contrats',
          'Suivi des prestations',
          'Gestion des échéances',
          'Évaluation des performances',
          'Facturation et paiements'
        ];
      case 'interventions':
        return [
          'Planification des interventions',
          'Suivi en temps réel',
          'Rapports d\'intervention',
          'Gestion des techniciens',
          'Historique complet'
        ];
      case 'prestataires':
        return [
          'Base de données prestataires',
          'Évaluation des fournisseurs',
          'Historique des collaborations',
          'Certifications et qualifications',
          'Contacts et coordonnées'
        ];
      default:
        return [];
    }
  }

  getPrestataireActiveContractsCount(prestataire: string): number {
    return this.sousTraitances.filter(s => 
      s.sousTraitant === prestataire && s.statut === StatutSousTraitance.EN_COURS
    ).length;
  }

  getPrestataireTotalContractsCount(prestataire: string): number {
    return this.sousTraitances.filter(s => s.sousTraitant === prestataire).length;
  }

  // Gestion des prestataires
  viewPrestataireDetails(prestataire: Prestataire): void {
    this.selectedPrestataire = prestataire;
    this.showPrestataireDetails = true;
  }

  closePrestataireDetails(): void {
    this.showPrestataireDetails = false;
    this.selectedPrestataire = null;
  }

  showAddPrestataireForm(): void {
    this.isEditModePrestataire = false;
    this.selectedPrestataire = null;
    this.prestataireFormData = {
      nom: '',
      raisonSociale: '',
      siret: '',
      secteur: SecteurPrestataire.INFORMATIQUE,
      type: TypePrestataire.ENTREPRISE,
      statut: StatutPrestataire.ACTIF,
      adresse: '',
      ville: '',
      codePostal: '',
      pays: 'Tunisie',
      telephone: '',
      email: '',
      siteWeb: '',
      contactPrincipal: {
        id: '',
        nom: '',
        prenom: '',
        poste: '',
        telephone: '',
        email: '',
        principal: true
      },
      specialites: [],
      certifications: [],
      conditions: {
        delaiPaiement: 30,
        remise: 0,
        fraisDeplacementKm: 0.5,
        garantie: 12,
        disponibiliteUrgence: false
      },
      documents: []
    };
    this.showPrestataireForm = true;
  }

  editPrestataire(prestataire: Prestataire): void {
    this.isEditModePrestataire = true;
    this.selectedPrestataire = prestataire;
    this.prestataireFormData = { ...prestataire };
    this.showPrestataireForm = true;
  }

  savePrestataire(): void {
    if (this.isEditModePrestataire && this.selectedPrestataire) {
      this.prestataireService.updatePrestataire(this.selectedPrestataire.id, this.prestataireFormData);
    } else {
      this.prestataireService.addPrestataire(this.prestataireFormData);
    }

    this.closePrestataireForm();
  }

  closePrestataireForm(): void {
    this.showPrestataireForm = false;
    this.isEditModePrestataire = false;
    this.selectedPrestataire = null;
    this.prestataireFormData = {};
  }

  deletePrestataire(id: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce prestataire ?')) {
      this.prestataireService.deletePrestataire(id);
    }
  }

  addEvaluationPrestataire(prestataire: Prestataire): void {
    this.selectedPrestataire = prestataire;
    this.showAddEvaluationForm();
  }

  showAddEvaluationForm(): void {
    this.showEvaluationForm = true;
    this.evaluationFormData = {
      prestataireId: this.selectedPrestataire?.id || '',
      date: this.formatDateForInput(new Date()),
      qualite: 5,
      delais: 5,
      service: 5,
      prix: 5,
      noteGlobale: 5,
      commentaires: '',
      evaluateur: ''
    };
  }

  saveEvaluation(): void {
    const evaluationData: Omit<EvaluationPrestataire, 'id'> = {
      prestataireId: this.evaluationFormData.prestataireId,
      date: new Date(this.evaluationFormData.date),
      qualite: this.evaluationFormData.qualite,
      delais: this.evaluationFormData.delais,
      service: this.evaluationFormData.service,
      prix: this.evaluationFormData.prix,
      noteGlobale: this.calculateNoteGlobale(),
      commentaires: this.evaluationFormData.commentaires,
      evaluateur: this.evaluationFormData.evaluateur
    };

    this.prestataireService.addEvaluation(evaluationData);
    this.closeEvaluationForm();
  }

  closeEvaluationForm(): void {
    this.showEvaluationForm = false;
    this.evaluationFormData = {};
  }

  calculateNoteGlobale(): number {
    const { qualite, delais, service, prix } = this.evaluationFormData;
    return (qualite + delais + service + prix) / 4;
  }

  updateNoteGlobale(): void {
    this.evaluationFormData.noteGlobale = this.calculateNoteGlobale();
  }

  // Filtres pour prestataires
  get searchTermPrestataire_() {
    return this.searchTermPrestataire;
  }

  set searchTermPrestataire_(value: string) {
    this.searchTermPrestataire = value;
    this.filterPrestataires();
  }

  filterPrestataires(): void {
    let filtered = [...this.prestataires];

    // Filtre par terme de recherche
    if (this.searchTermPrestataire) {
      const term = this.searchTermPrestataire.toLowerCase();
      filtered = filtered.filter(prestataire =>
        prestataire.nom.toLowerCase().includes(term) ||
        prestataire.raisonSociale.toLowerCase().includes(term) ||
        prestataire.email.toLowerCase().includes(term) ||
        prestataire.contactPrincipal.nom.toLowerCase().includes(term)
      );
    }

    // Filtre par secteur
    if (this.filterSecteurPrestataire) {
      filtered = filtered.filter(prestataire => prestataire.secteur === this.filterSecteurPrestataire);
    }

    // Filtre par statut
    if (this.filterStatutPrestataire) {
      filtered = filtered.filter(prestataire => prestataire.statut === this.filterStatutPrestataire);
    }

    // Filtre par type
    if (this.filterTypePrestataire) {
      filtered = filtered.filter(prestataire => prestataire.type === this.filterTypePrestataire);
    }

    this.filteredPrestataires = filtered;
  }

  applyPrestataireFilters(): void {
    this.filterPrestataires();
  }

  clearPrestataireFilters(): void {
    this.searchTermPrestataire = '';
    this.filterSecteurPrestataire = '';
    this.filterStatutPrestataire = '';
    this.filterTypePrestataire = '';
    this.filterPrestataires();
  }

  // Méthodes utilitaires pour prestataires
  getPrestataireInitials(prestataire: Prestataire): string {
    const name = prestataire.raisonSociale || prestataire.nom;
    const words = name.split(' ');
    if (words.length >= 2) {
      return `${words[0].charAt(0)}${words[1].charAt(0)}`.toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  getPrestataireNote(prestataireId: string): number {
    const evaluations = this.prestataireService.getEvaluationsByPrestataireId(prestataireId);
    if (evaluations.length === 0) return 0;
    const total = evaluations.reduce((sum, evaluation) => sum + evaluation.noteGlobale, 0);
    return Math.round((total / evaluations.length) * 10) / 10;
  }

  getPrestataireEvaluations(prestataireId: string): EvaluationPrestataire[] {
    return this.prestataireService.getEvaluationsByPrestataireId(prestataireId);
  }

  getPrestataireContrats(prestataireId: string): ContratPrestataire[] {
    return this.prestataireService.getContratsByPrestataireId(prestataireId);
  }

  getSecteurClass(secteur: SecteurPrestataire): string {
    return secteur.toLowerCase().replace(/[^a-z]/g, '');
  }

  getPrestataireTypeClass(type: TypePrestataire): string {
    return type.toLowerCase().replace(/[^a-z]/g, '');
  }

  getPrestataireStatusClass(statut: StatutPrestataire): string {
    return statut.toLowerCase().replace(/[^a-z]/g, '');
  }

  getContratStatusClass(statut: StatutContratPrestataire): string {
    return statut.toLowerCase().replace(/[^a-z]/g, '-');
  }

  getContratTypeClass(type: TypeContratPrestataire): string {
    return type.toLowerCase().replace(/[^a-z]/g, '');
  }

  isContratExpired(contrat: ContratPrestataire): boolean {
    const today = new Date();
    const dateFin = new Date(contrat.dateFin);
    return dateFin < today;
  }
}