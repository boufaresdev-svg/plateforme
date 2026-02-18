import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Projet } from '../models/projet/Projet.model';
import { Phase, PhaseType } from '../models/projet/Phase.model';
import { Mission } from '../models/projet/Mission.model';
import { Tache } from '../models/projet/Tache.model';
import { Charge } from '../models/projet/Charge.model';
import { CommentaireTache } from '../models/projet/CommentaireTache.model';
import { PrioriteMission, PrioriteProjet, PrioriteTache, ProjetStats, RoleProjet, StatutAffectation, StatutMission, StatutPhase, StatutProjet, StatutTache, TypeCommentaire, TypeProjet } from '../models/projet/enum.model';
import { EmployeAffecte } from '../models/projet/EmployeAffecte.model';
 

@Injectable({
  providedIn: 'root'
})
export class ProjetManagementService {
  private projetsSubject = new BehaviorSubject<Projet[]>([]);
  public projets$ = this.projetsSubject.asObservable();

  private phasesSubject = new BehaviorSubject<Phase[]>([]);
  public phases$ = this.phasesSubject.asObservable();

  private missionsSubject = new BehaviorSubject<Mission[]>([]);
  public missions$ = this.missionsSubject.asObservable();

  private tachesSubject = new BehaviorSubject<Tache[]>([]);
  public taches$ = this.tachesSubject.asObservable();

  private chargesSubject = new BehaviorSubject<Charge[]>([]);
  public charges$ = this.chargesSubject.asObservable();

  private commentairesSubject = new BehaviorSubject<CommentaireTache[]>([]);
  public commentaires$ = this.commentairesSubject.asObservable();

  constructor() {
    this.loadMockData();
  }

  private loadMockData(): void {
 const mockProjets: Projet[] = [
  // {
  //   id: '1',
  //   nom: 'Refonte Site Web',
  //   description: 'Refonte complète du site web de l\'entreprise',
  //   type: TypeProjet.DEVELOPPEMENT,
  //   statut: StatutProjet.EN_COURS,
  //   priorite: PrioriteProjet.HAUTE,
  //   chefProjet: 'Mohamed Ben Ahmed',
  //   equipe: [],               // <--- obligatoire
  //   client: 'SMS2i',
  //   dateDebut: new Date('2024-01-15'),
  //   dateFin: new Date('2024-03-15'),
  //   dateFinPrevue: new Date('2024-03-15'),
  //   budget: 15000,
  //   coutReel: 8500,
  //   progression: 65,
  //   phases: [],
  //   taches: [],               // <--- obligatoire
  //   tags: [],
  //   documents: ['cahier_charges.pdf', 'maquettes.fig'],
  //   createdAt: new Date('2024-01-10'),
  //   updatedAt: new Date('2024-01-20')
  // },
  // {
  //   id: '2',
  //   nom: 'Migration Infrastructure',
  //   description: 'Migration vers le cloud AWS',
  //   type: TypeProjet.INFRASTRUCTURE,
  //   statut: StatutProjet.PLANIFIE,
  //   priorite: PrioriteProjet.NORMALE,
  //   chefProjet: 'Ahmed Khelifi',
  //   equipe: [],               // <--- obligatoire
  //   client: 'SMS2i',
  //   dateDebut: new Date('2024-02-01'),
  //   dateFin: new Date('2024-04-01'),
  //   dateFinPrevue: new Date('2024-04-01'),
  //   budget: 25000,
  //   progression: 0,
  //   phases: [],
  //   taches: [],               // <--- obligatoire
  //   tags: [],
  //   documents: ['architecture.pdf'],
  //   createdAt: new Date('2024-01-20'),
  //   updatedAt: new Date('2024-01-20')
  // }
];


    const mockPhases: Phase[] = [
      // {
      //   id: '1',
      //   projetId: '1',
      //   nom: PhaseType.ETUDE_ANALYSE,
      //   description: 'Phase d\'étude et d\'analyse des besoins',
      //   ordre: 1,
      //   statut: StatutPhase.TERMINEE,
      //   dateDebut: new Date('2024-01-15'),
      //   dateFin: new Date('2024-02-15'),
      //   progression: 100,
      //   budget: 5000,
      //   missions: [],
      //   livrables: ['cahier_charges.pdf', 'analyse_besoins.pdf'],
      //   createdAt: new Date('2024-01-15'),
      //   updatedAt: new Date('2024-02-15')
      // },
      // {
      //   id: '2',
      //   projetId: '1',
      //   nom: PhaseType.DEVELOPPEMENT_REALISATION,
      //   description: 'Phase de développement et réalisation',
      //   ordre: 2,
      //   statut: StatutPhase.EN_COURS,
      //   dateDebut: new Date('2024-02-16'),
      //   dateFin: new Date('2024-03-30'),
      //   progression: 65,
      //   budget: 15000,
      //   missions: [],
      //   livrables: ['application.zip', 'documentation.pdf'],
      //   createdAt: new Date('2024-02-16'),
      //   updatedAt: new Date('2024-03-10')
      // }
    ];
    const mockMissions: Mission[] = [
      // {
      //   id: '1',
      //   phaseId: '1',
      //   projetId: '1',
      //   nom: 'Analyse et Conception',
      //   description: 'Analyse des besoins et conception de l\'architecture',
      //   objectif: 'Définir l\'architecture technique et fonctionnelle',
      //   statut: StatutMission.TERMINEE,
      //   priorite: PrioriteMission.HAUTE,
      //   dateDebut: new Date('2024-01-15'),
      //   dateFin: new Date('2024-02-15'),
      //   progression: 100,
      //   budget: 8000,
      //   employesAffectes: [
      //     {
      //       id: '1',
      //       employeId: '1',
      //       nom: 'Ben Ahmed',
      //       prenom: 'Mohamed',
      //       poste: 'Architecte Logiciel',
      //       email: 'mohamed@sms2i.com',
      //       role: RoleProjet.ARCHITECTE,
      //       dateAffectation: new Date('2024-01-15'),
      //       tauxHoraire: 50,
      //       heuresAllouees: 160,
      //       heuresRealisees: 155,
      //       statut: StatutAffectation.TERMINE
      //     }
      //   ],
      //   taches: [],
      //   dependances: [],
      //   livrables: ['architecture.pdf', 'specifications.docx'],
      //   createdAt: new Date('2024-01-15'),
      //   updatedAt: new Date('2024-02-15')
      // },
      // {
      //   id: '2',
      //   phaseId: '2',
      //   projetId: '1',
      //   nom: 'Développement Frontend',
      //   description: 'Développement de l\'interface utilisateur',
      //   objectif: 'Créer une interface moderne et responsive',
      //   statut: StatutMission.EN_COURS,
      //   priorite: PrioriteMission.HAUTE,
      //   dateDebut: new Date('2024-02-16'),
      //   dateFin: new Date('2024-03-30'),
      //   progression: 65,
      //   budget: 12000,
      //   employesAffectes: [
      //     {
      //       id: '2',
      //       employeId: '2',
      //       nom: 'Trabelsi',
      //       prenom: 'Fatma',
      //       poste: 'Développeur Frontend',
      //       email: 'fatma@sms2i.com',
      //       role: RoleProjet.DEVELOPPEUR,
      //       dateAffectation: new Date('2024-02-16'),
      //       tauxHoraire: 40,
      //       heuresAllouees: 300,
      //       heuresRealisees: 195,
      //       statut: StatutAffectation.ACTIF
      //     }
      //   ],
      //   taches: [],
      //   dependances: [],
      //   livrables: ['interface.html', 'styles.css'],
      //   createdAt: new Date('2024-02-16'),
      //   updatedAt: new Date('2024-03-10')
      // }
    ];

    const mockTaches: Tache[] = [
      // {
      //   id: '1',
      //   missionId: '1',
      //   nom: 'Analyse des besoins fonctionnels',
      //   description: 'Analyser et documenter les besoins fonctionnels',
      //   statut: StatutTache.TERMINEE,
      //   priorite: PrioriteTache.HAUTE,
      //   dateDebut: new Date('2024-01-15'),
      //   dateFin: new Date('2024-01-25'),
      //   dureeEstimee: 80,
      //   dureeReelle: 75,
      //   progression: 100,
      //   employesAffectes: [
      //     {
      //       id: '1',
      //       employeId: '1',
      //       nom: 'Ben Ahmed',
      //       prenom: 'Mohamed',
      //       poste: 'Architecte Logiciel',
      //       email: 'mohamed@sms2i.com',
      //       role: RoleProjet.ANALYSTE,
      //       dateAffectation: new Date('2024-01-15'),
      //       tauxHoraire: 50,
      //       heuresAllouees: 80,
      //       heuresRealisees: 75,
      //       statut: StatutAffectation.TERMINE
      //     }
      //   ],
      //   charges: [],
      //   dependances: [],
      //   commentaires: [],
      //   fichiers: ['besoins_fonctionnels.pdf'],
      //   createdAt: new Date('2024-01-15'),
      //   updatedAt: new Date('2024-01-25')
      // },
      // {
      //   id: '2',
      //   missionId: '2',
      //   nom: 'Création des maquettes',
      //   description: 'Créer les maquettes de l\'interface utilisateur',
      //   statut: StatutTache.EN_COURS,
      //   priorite: PrioriteTache.NORMALE,
      //   dateDebut: new Date('2024-02-20'),
      //   dateFin: new Date('2024-03-05'),
      //   dureeEstimee: 60,
      //   progression: 70,
      //   employesAffectes: [
      //     {
      //       id: '2',
      //       employeId: '2',
      //       nom: 'Trabelsi',
      //       prenom: 'Fatma',
      //       poste: 'Développeur Frontend',
      //       email: 'fatma@sms2i.com',
      //       role: RoleProjet.DESIGNER,
      //       dateAffectation: new Date('2024-02-20'),
      //       tauxHoraire: 40,
      //       heuresAllouees: 60,
      //       heuresRealisees: 42,
      //       statut: StatutAffectation.ACTIF
      //     }
      //   ],
      //   charges: [],
      //   dependances: [],
      //   commentaires: [],
      //   fichiers: ['maquettes.fig'],
      //   createdAt: new Date('2024-02-20'),
      //   updatedAt: new Date('2024-03-01')
      // }
    ];

    const mockCharges: Charge[] = [
      // {
      //   id: '1',
      //   tacheId: '1',
      //   employeId: '1',
      //   nom: 'Ben Ahmed',
      //   prenom: 'Mohamed',
      //   poste: 'Architecte Logiciel',
      //   email: 'mohamed@sms2i.com',
      //   tauxHoraire: 50,
      //   heuresAllouees: 80,
      //   heuresRealisees: 75,
      //   dateDebut: new Date('2024-01-15'),
      //   dateFin: new Date('2024-01-25'),
      //   description: 'Analyse des besoins fonctionnels',
      //   createdAt: new Date('2024-01-15'),
      //   updatedAt: new Date('2024-01-25')
      // },
      // {
      //   id: '2',
      //   tacheId: '2',
      //   employeId: '2',
      //   nom: 'Trabelsi',
      //   prenom: 'Fatma',
      //   poste: 'Développeur Frontend',
      //   email: 'fatma@sms2i.com',
      //   tauxHoraire: 40,
      //   heuresAllouees: 60,
      //   heuresRealisees: 42,
      //   dateDebut: new Date('2024-02-20'),
      //   dateFin: new Date('2024-03-05'),
      //   description: 'Création des maquettes UI',
      //   createdAt: new Date('2024-02-20'),
      //   updatedAt: new Date('2024-03-01')
      // }
    ];
    const mockCommentaires: CommentaireTache[] = [
      {
        id: '1',
        tacheId: '2',
        auteur: 'Mohamed Ben Ahmed',
        contenu: 'Les maquettes sont bien avancées, bon travail !',
        date: new Date('2024-03-01'),
        type: TypeCommentaire.GENERAL
      }
    ];

    this.projetsSubject.next(mockProjets);
    this.phasesSubject.next(mockPhases);
    this.missionsSubject.next(mockMissions);
    this.tachesSubject.next(mockTaches);
    this.chargesSubject.next(mockCharges);
    this.commentairesSubject.next(mockCommentaires);
  }

  // Gestion des projets
  getProjets(): Observable<Projet[]> {
    return this.projets$;
  }

  getProjetById(id: string): Projet | undefined {
    return this.projetsSubject.value.find(p => p.id === id);
  }

  addProjet(projet: Omit<Projet, 'id' | 'createdAt' | 'updatedAt' | 'missions'>): void {
    const newProjet: Projet = {
      ...projet,
      id: Date.now().toString(),
      phases: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentProjets = this.projetsSubject.value;
    this.projetsSubject.next([...currentProjets, newProjet]);

    // Créer automatiquement les 5 phases standard
    this.createStandardPhases(newProjet.id);
  }

  private createStandardPhases(projetId: string): void {
    // const standardPhases: Phase[] = [
    //   {
    //     id: `${projetId}-phase-1`,
    //     projetId,
    //     nom: PhaseType.ETUDE_ANALYSE,
    //     description: 'Phase d\'étude et d\'analyse des besoins',
    //     ordre: 1,
    //     statut: StatutPhase.PLANIFIEE,
    //     dateDebut: new Date(),
    //     dateFin: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000), // +30 jours
    //     progression: 0,
    //     budget: 0,
    //     missions: [],
    //     livrables: [],
    //     createdAt: new Date(),
    //     updatedAt: new Date()
    //   },
    //   {
    //     id: `${projetId}-phase-2`,
    //     projetId,
    //     nom: PhaseType.DEVELOPPEMENT_REALISATION,
    //     description: 'Phase de développement et réalisation',
    //     ordre: 2,
    //     statut: StatutPhase.PLANIFIEE,
    //     dateDebut: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000), // +30 jours
    //     dateFin: new Date(Date.now() + 90 * 24 * 60 * 60 * 1000), // +90 jours
    //     progression: 0,
    //     budget: 0,
    //     missions: [],
    //     livrables: [],
    //     createdAt: new Date(),
    //     updatedAt: new Date()
    //   },
    //   {
    //     id: `${projetId}-phase-3`,
    //     projetId,
    //     nom: PhaseType.MISE_EN_SERVICE_DEPLOIEMENT,
    //     description: 'Phase de mise en service et déploiement',
    //     ordre: 3,
    //     statut: StatutPhase.PLANIFIEE,
    //     dateDebut: new Date(Date.now() + 90 * 24 * 60 * 60 * 1000), // +90 jours
    //     dateFin: new Date(Date.now() + 105 * 24 * 60 * 60 * 1000), // +105 jours
    //     progression: 0,
    //     budget: 0,
    //     missions: [],
    //     livrables: [],
    //     createdAt: new Date(),
    //     updatedAt: new Date()
    //   },
    //   {
    //     id: `${projetId}-phase-4`,
    //     projetId,
    //     nom: PhaseType.SUPERVISION_CONTROLE_SUIVI,
    //     description: 'Phase de supervision, contrôle et suivi',
    //     ordre: 4,
    //     statut: StatutPhase.PLANIFIEE,
    //     dateDebut: new Date(Date.now() + 105 * 24 * 60 * 60 * 1000), // +105 jours
    //     dateFin: new Date(Date.now() + 120 * 24 * 60 * 60 * 1000), // +120 jours
    //     progression: 0,
    //     budget: 0,
    //     missions: [],
    //     livrables: [],
    //     createdAt: new Date(),
    //     updatedAt: new Date()
    //   },
    //   {
    //     id: `${projetId}-phase-5`,
    //     projetId,
    //     nom: PhaseType.CLOTURE,
    //     description: 'Phase de clôture du projet',
    //     ordre: 5,
    //     statut: StatutPhase.PLANIFIEE,
    //     dateDebut: new Date(Date.now() + 120 * 24 * 60 * 60 * 1000), // +120 jours
    //     dateFin: new Date(Date.now() + 135 * 24 * 60 * 60 * 1000), // +135 jours
    //     progression: 0,
    //     budget: 0,
    //     missions: [],
    //     livrables: [],
    //     createdAt: new Date(),
    //     updatedAt: new Date()
    //   }
    // ];

    const currentPhases = this.phasesSubject.value;
   // this.phasesSubject.next([...currentPhases, ...standardPhases]);
  }

  updateProjet(id: string, updates: Partial<Projet>): void {
    const currentProjets = this.projetsSubject.value;
    const updatedProjets = currentProjets.map(projet => 
      projet.id === id 
        ? { ...projet, ...updates, updatedAt: new Date() }
        : projet
    );
    this.projetsSubject.next(updatedProjets);
  }

  deleteProjet(id: string): void {
    const currentProjets = this.projetsSubject.value;
    const filteredProjets = currentProjets.filter(p => p.id !== id);
    this.projetsSubject.next(filteredProjets);

    // Supprimer les phases, missions et tâches associées
    const currentPhases = this.phasesSubject.value;
    const phasesToDelete = currentPhases.filter(p => p.projetId === id);
    const filteredPhases = currentPhases.filter(p => p.projetId !== id);
    this.phasesSubject.next(filteredPhases);

    const currentMissions = this.missionsSubject.value;
    const phaseIds = phasesToDelete.map(p => p.id);
    const missionsToDelete = currentMissions.filter(m => phaseIds.includes(m.phaseId));
    const filteredMissions = currentMissions.filter(m => !phaseIds.includes(m.phaseId));
    this.missionsSubject.next(filteredMissions);

    // Supprimer les tâches des missions supprimées
    const currentTaches = this.tachesSubject.value;
    const missionIds = missionsToDelete.map(m => m.id);
    const filteredTaches = currentTaches.filter(t => !missionIds.includes(t.missionId));
    this.tachesSubject.next(filteredTaches);

    // Supprimer les charges des tâches supprimées
    const currentCharges = this.chargesSubject.value;
    const tacheIds = missionsToDelete.flatMap(m => this.getTachesByMissionId(m.id).map(t => t.id));
    const filteredCharges = currentCharges.filter(c => !tacheIds.includes(c.tacheId));
    this.chargesSubject.next(filteredCharges);
  }

  // Gestion des phases
  getPhases(): Observable<Phase[]> {
    return this.phases$;
  }

  getPhaseById(id: string): Phase | undefined {
    return this.phasesSubject.value.find(p => p.id === id);
  }

  addPhase(phase: Omit<Phase, 'id' | 'createdAt' | 'updatedAt' | 'missions'>): void {
    const newPhase: Phase = {
      ...phase,
      id: Date.now().toString(),
      missions: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentPhases = this.phasesSubject.value;
    this.phasesSubject.next([...currentPhases, newPhase]);
  }

  updatePhase(id: string, updates: Partial<Phase>): void {
    const currentPhases = this.phasesSubject.value;
    const updatedPhases = currentPhases.map(phase => 
      phase.id === id 
        ? { ...phase, ...updates, updatedAt: new Date() }
        : phase
    );
    this.phasesSubject.next(updatedPhases);
  }

  deletePhase(id: string): void {
    const currentPhases = this.phasesSubject.value;
    const filteredPhases = currentPhases.filter(p => p.id !== id);
    this.phasesSubject.next(filteredPhases);

    // Supprimer les missions associées
    const currentMissions = this.missionsSubject.value;
    const missionsToDelete = currentMissions.filter(m => m.phaseId === id);
    const filteredMissions = currentMissions.filter(m => m.phaseId !== id);
    this.missionsSubject.next(filteredMissions);

    // Supprimer les tâches des missions supprimées
    const currentTaches = this.tachesSubject.value;
    const missionIds = missionsToDelete.map(m => m.id);
    const filteredTaches = currentTaches.filter(t => !missionIds.includes(t.missionId));
    this.tachesSubject.next(filteredTaches);
  }

  // Gestion des charges
  getCharges(): Observable<Charge[]> {
    return this.charges$;
  }

  getChargesByTacheId(tacheId: string): Charge[] {
    return this.chargesSubject.value.filter(c => c.tacheId === tacheId);
  }

  addCharge(charge: Omit<Charge, 'id' | 'createdAt' | 'updatedAt'>): void {
    const newCharge: Charge = {
      ...charge,
      id: Date.now().toString(),
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentCharges = this.chargesSubject.value;
    this.chargesSubject.next([...currentCharges, newCharge]);
  }

  updateCharge(id: string, updates: Partial<Charge>): void {
    const currentCharges = this.chargesSubject.value;
    const updatedCharges = currentCharges.map(charge => 
      charge.id === id 
        ? { ...charge, ...updates, updatedAt: new Date() }
        : charge
    );
    this.chargesSubject.next(updatedCharges);
  }

  deleteCharge(id: string): void {
    const currentCharges = this.chargesSubject.value;
    const filteredCharges = currentCharges.filter(c => c.id !== id);
    this.chargesSubject.next(filteredCharges);
  }

  // Gestion des missions
  getMissions(): Observable<Mission[]> {
    return this.missions$;
  }

  getMissionsByProjetId(projetId: string): Mission[] {
    return this.missionsSubject.value.filter(m => m.projetId === projetId);
  }

  getMissionsByPhaseId(phaseId: string): Mission[] {
    return this.missionsSubject.value.filter(m => m.phaseId === phaseId);
  }

  getPhasesByProjetId(projetId: string): Phase[] {
    return this.phasesSubject.value.filter(p => p.projetId === projetId);
  }

  addMission(mission: Omit<Mission, 'id' | 'createdAt' | 'updatedAt' | 'taches'>): void {
    const newMission: Mission = {
      ...mission,
      id: Date.now().toString(),
      taches: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentMissions = this.missionsSubject.value;
    this.missionsSubject.next([...currentMissions, newMission]);
  }

  updateMission(id: string, updates: Partial<Mission>): void {
    const currentMissions = this.missionsSubject.value;
    const updatedMissions = currentMissions.map(mission => 
      mission.id === id 
        ? { ...mission, ...updates, updatedAt: new Date() }
        : mission
    );
    this.missionsSubject.next(updatedMissions);
  }

  deleteMission(id: string): void {
    const currentMissions = this.missionsSubject.value;
    const filteredMissions = currentMissions.filter(m => m.id !== id);
    this.missionsSubject.next(filteredMissions);

    // Supprimer les tâches associées
    const currentTaches = this.tachesSubject.value;
    const filteredTaches = currentTaches.filter(t => t.missionId !== id);
    this.tachesSubject.next(filteredTaches);
  }

  // Gestion des tâches
  getTaches(): Observable<Tache[]> {
    return this.taches$;
  }

  getTachesByMissionId(missionId: string): Tache[] {
    return this.tachesSubject.value.filter(t => t.missionId === missionId);
  }

  addTache(tache: Omit<Tache, 'id' | 'createdAt' | 'updatedAt' | 'commentaires'>): void {
    const newTache: Tache = {
      ...tache,
      id: Date.now().toString(),
      commentaires: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentTaches = this.tachesSubject.value;
    this.tachesSubject.next([...currentTaches, newTache]);
  }

  updateTache(id: string, updates: Partial<Tache>): void {
    const currentTaches = this.tachesSubject.value;
    const updatedTaches = currentTaches.map(tache => 
      tache.id === id 
        ? { ...tache, ...updates, updatedAt: new Date() }
        : tache
    );
    this.tachesSubject.next(updatedTaches);
  }

  deleteTache(id: string): void {
    const currentTaches = this.tachesSubject.value;
    const filteredTaches = currentTaches.filter(t => t.id !== id);
    this.tachesSubject.next(filteredTaches);

    // Supprimer les commentaires associés
    const currentCommentaires = this.commentairesSubject.value;
    const filteredCommentaires = currentCommentaires.filter(c => c.tacheId !== id);
    this.commentairesSubject.next(filteredCommentaires);
  }

  // Gestion des commentaires
  getCommentairesByTacheId(tacheId: string): CommentaireTache[] {
    return this.commentairesSubject.value.filter(c => c.tacheId === tacheId);
  }

  addCommentaire(commentaire: Omit<CommentaireTache, 'id'>): void {
    const newCommentaire: CommentaireTache = {
      ...commentaire,
      id: Date.now().toString()
    };

    const currentCommentaires = this.commentairesSubject.value;
    this.commentairesSubject.next([...currentCommentaires, newCommentaire]);
  }

  // Gestion des affectations
  affecterEmployeAMission(missionId: string, employe: EmployeAffecte): void {
    const mission = this.missionsSubject.value.find(m => m.id === missionId);
    if (mission) {
      const employesAffectes = [...mission.employesAffectes, employe];
      this.updateMission(missionId, { employesAffectes });
    }
  }

  desaffecterEmployeDeMission(missionId: string, employeId: string): void {
    const mission = this.missionsSubject.value.find(m => m.id === missionId);
    if (mission) {
      const employesAffectes = mission.employesAffectes.filter(e => e.employeId !== employeId);
      this.updateMission(missionId, { employesAffectes });
    }
  }

  affecterEmployeATache(tacheId: string, employe: EmployeAffecte): void {
    const tache = this.tachesSubject.value.find(t => t.id === tacheId);
    if (tache) {
      const employesAffectes = [...tache.employesAffectes, employe];
      this.updateTache(tacheId, { employesAffectes });
    }
  }

  desaffecterEmployeDeTache(tacheId: string, employeId: string): void {
    const tache = this.tachesSubject.value.find(t => t.id === tacheId);
    if (tache) {
      const employesAffectes = tache.employesAffectes.filter(e => e.employeId !== employeId);
      this.updateTache(tacheId, { employesAffectes });
    }
  }

  // Statistiques
  getProjetStats(): ProjetStats {
    const projets = this.projetsSubject.value;
    const missions = this.missionsSubject.value;
    const taches = this.tachesSubject.value;

    const projetsEnCours = projets.filter(p => p.statut === StatutProjet.EN_COURS).length;
    const projetsPlanifies = projets.filter(p => p.statut === StatutProjet.PLANIFIE).length;
    const projetsTermines = projets.filter(p => p.statut === StatutProjet.TERMINE).length;

    const budgetTotal = projets.reduce((total, p) => total + p.budget, 0);
    const progressionMoyenne = projets.length > 0 
      ? projets.reduce((total, p) => total + p.progression, 0) / projets.length 
      : 0;

    const today = new Date();
    const projetsEnRetard = projets.filter(p => 
      p.statut === StatutProjet.EN_COURS && new Date(p.dateFinPrevue) < today
    ).length;

    const tachesEnCours = taches.filter(t => t.statut === StatutTache.EN_COURS).length;

    // Calculer les employés affectés uniques
    const employesAffectesSet = new Set<string>();
    missions.forEach(m => {
      m.employesAffectes.forEach(e => employesAffectesSet.add(e.employeId));
    });
    taches.forEach(t => {
      t.employesAffectes.forEach(e => employesAffectesSet.add(e.employeId));
    });

    const heuresAllouees = missions.reduce((total, m) => 
      total + m.employesAffectes.reduce((sum, e) => sum + (e.heuresAllouees || 0), 0), 0
    ) + taches.reduce((total, t) => 
      total + t.employesAffectes.reduce((sum, e) => sum + (e.heuresAllouees || 0), 0), 0
    );

    const heuresRealisees = missions.reduce((total, m) => 
      total + m.employesAffectes.reduce((sum, e) => sum + (e.heuresRealisees || 0), 0), 0
    ) + taches.reduce((total, t) => 
      total + t.employesAffectes.reduce((sum, e) => sum + (e.heuresRealisees || 0), 0), 0
    );

    return {
      totalProjets: projets.length,
      projetsEnCours,
      projetsPlanifies,
      projetsTermines,
      budgetTotal,
      progressionMoyenne: Math.round(progressionMoyenne),
      projetsEnRetard,
      totalMissions: missions.length,
      totalTaches: taches.length,
      tachesEnCours,
      employesAffectes: employesAffectesSet.size,
      heuresAllouees,
      heuresRealisees
    };
  }

  // Utilitaires
  calculateMissionProgression(missionId: string): number {
    const taches = this.getTachesByMissionId(missionId);
    if (taches.length === 0) return 0;
    
    const totalProgression = taches.reduce((sum, t) => sum + t.progression, 0);
    return Math.round(totalProgression / taches.length);
  }

  calculatePhaseProgression(phaseId: string): number {
    const missions = this.getMissionsByPhaseId(phaseId);
    if (missions.length === 0) return 0;
    
    const totalProgression = missions.reduce((sum, m) => sum + m.progression, 0);
    return Math.round(totalProgression / missions.length);
  }

  calculateProjetProgression(projetId: string): number {
    const phases = this.getPhasesByProjetId(projetId);
    if (phases.length === 0) return 0;
    
    const totalProgression = phases.reduce((sum, p) => sum + p.progression, 0);
    return Math.round(totalProgression / phases.length);
  }

  getEmployesDisponibles(): EmployeAffecte[] {
    // Cette méthode devrait récupérer les employés depuis le service HR
    // Pour l'instant, on retourne une liste mock
    return [
      // {
      //   id: '1',
      //   employeId: '1',
      //   nom: 'Ben Ahmed',
      //   prenom: 'Mohamed',
      //   poste: 'Développeur Senior',
      //   email: 'mohamed@sms2i.com',
      //   role: RoleProjet.DEVELOPPEUR,
      //   dateAffectation: new Date(),
      //   statut: StatutAffectation.ACTIF
      // },
      // {
      //   id: '2',
      //   employeId: '2',
      //   nom: 'Trabelsi',
      //   prenom: 'Fatma',
      //   poste: 'Chef de Projet',
      //   email: 'fatma@sms2i.com',
      //   role: RoleProjet.CHEF_PROJET,
      //   dateAffectation: new Date(),
      //   statut: StatutAffectation.ACTIF
      // },
      // {
      //   id: '3',
      //   employeId: '3',
      //   nom: 'Khelifi',
      //   prenom: 'Ahmed',
      //   poste: 'Analyste',
      //   email: 'ahmed@sms2i.com',
      //   role: RoleProjet.ANALYSTE,
      //   dateAffectation: new Date(),
      //   statut: StatutAffectation.ACTIF
      // }
    ];
  }
}