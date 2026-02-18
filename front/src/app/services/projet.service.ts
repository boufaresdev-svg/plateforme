import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs'; 
import { PrioriteProjet, StatutProjet, StatutTache, TypeProjet } from '../models/projet/enum.model';
import { ProjetStats } from '../models/projet/enum.model';
import { TacheProjet } from '../models/projet/TacheProjet.model';
import { Projet } from '../models/projet/Projet.model';
 
 
@Injectable({
  providedIn: 'root'
})
export class ProjetService {
  private projetsSubject = new BehaviorSubject<Projet[]>([]);
  public projets$ = this.projetsSubject.asObservable();

  private tachesSubject = new BehaviorSubject<TacheProjet[]>([]);
  public taches$ = this.tachesSubject.asObservable();

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
  //   chefProjet: 'Mohamed Ben Ahmed',   // renommé
  //   equipe: ['Fatma Trabelsi', 'Ahmed Khelifi'],
  //   client: undefined,
  //   dateDebut: new Date('2024-01-15'),
  //   dateFin: new Date('2024-03-15'),
  //   dateFinPrevue: new Date('2024-03-15'),
  //   budget: 15000,
  //   coutReel: 8500,
  //   progression: 65,
  //   phases: [],         // ajouté
  //   taches: [],
  //   tags: [],           // ajouté
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
  //   chefProjet: 'Ahmed Khelifi',      // renommé
  //   equipe: ['Mohamed Ben Ahmed'],
  //   client: 'SMS2i',
  //   dateDebut: new Date('2024-02-01'),
  //   dateFin: new Date('2024-04-01'),
  //   dateFinPrevue: new Date('2024-04-01'),
  //   budget: 25000,
  //   progression: 0,
  //   phases: [],         // ajouté
  //   taches: [],
  //   tags: [],           // ajouté
  //   documents: ['architecture.pdf'],
  //   createdAt: new Date('2024-01-20'),
  //   updatedAt: new Date('2024-01-20')
  // }
];

    const mockTaches: TacheProjet[] = [
      {
        id: '1',
        projetId: '1',
        nom: 'Analyse des besoins',
        description: 'Analyser les besoins fonctionnels',
        assignee: 'Fatma Trabelsi',
        statut: StatutTache.TERMINEE,
        dateDebut: new Date('2024-01-15'),
        dateFin: new Date('2024-01-20'),
        progression: 100,
        dependances: []
      },
      {
        id: '2',
        projetId: '1',
        nom: 'Développement frontend',
        description: 'Développer l\'interface utilisateur',
        assignee: 'Mohamed Ben Ahmed',
        statut: StatutTache.EN_COURS,
        dateDebut: new Date('2024-01-21'),
        dateFin: new Date('2024-02-15'),
        progression: 70,
        dependances: ['1']
      }
    ];

    this.projetsSubject.next(mockProjets);
    this.tachesSubject.next(mockTaches);
  }

  getProjets(): Observable<Projet[]> {
    return this.projets$;
  }

  getProjetById(id: string): Projet | undefined {
    return this.projetsSubject.value.find(p => p.id === id);
  }

  addProjet(projet: Omit<Projet, 'id' | 'createdAt' | 'updatedAt' | 'taches'>): void {
    const newProjet: Projet = {
      ...projet,
      id: Date.now().toString(),
      taches: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentProjets = this.projetsSubject.value;
    this.projetsSubject.next([...currentProjets, newProjet]);
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

    // Supprimer les tâches associées
    const currentTaches = this.tachesSubject.value;
    const filteredTaches = currentTaches.filter(t => t.projetId !== id);
    this.tachesSubject.next(filteredTaches);
  }

  getTachesByProjetId(projetId: string): TacheProjet[] {
    return this.tachesSubject.value.filter(t => t.projetId === projetId);
  }

  addTache(tache: Omit<TacheProjet, 'id'>): void {
    const newTache: TacheProjet = {
      ...tache,
      id: Date.now().toString()
    };

    const currentTaches = this.tachesSubject.value;
    this.tachesSubject.next([...currentTaches, newTache]);
  }

  updateTache(id: string, updates: Partial<TacheProjet>): void {
    const currentTaches = this.tachesSubject.value;
    const updatedTaches = currentTaches.map(tache =>
      tache.id === id ? { ...tache, ...updates } : tache
    );
    this.tachesSubject.next(updatedTaches);
  }

  getProjetStats(): ProjetStats {
    const projets = this.projetsSubject.value;
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
      p.statut === StatutProjet.EN_COURS && p.dateFinPrevue < today
    ).length;

    const tachesEnCours = taches.filter(t => t.statut === StatutTache.EN_COURS).length;

    return {
      totalProjets: projets.length,
      projetsEnCours,
      projetsPlanifies,
      projetsTermines,
      budgetTotal,
      progressionMoyenne: Math.round(progressionMoyenne),
      projetsEnRetard,
      tachesEnCours,

      // Champs manquants
      totalMissions: 0,       // ou calcul réel si tu as une liste de missions
      totalTaches: taches.length,
      employesAffectes: 0,    // ou calcul réel
      heuresAllouees: 0,      // ou calcul réel
      heuresRealisees: 0      // ou calcul réel
    };

  }
}