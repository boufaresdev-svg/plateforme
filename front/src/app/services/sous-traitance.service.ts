import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { 
  SousTraitance, 
  InterventionSousTraitance, 
  TypeSousTraitance, 
  PrioriteSousTraitance, 
  StatutSousTraitance, 
  StatutIntervention, 
  SousTraitanceStats 
} from '../models/sous-traitance.model';

@Injectable({
  providedIn: 'root'
})
export class SousTraitanceService {
  private sousTraitancesSubject = new BehaviorSubject<SousTraitance[]>([]);
  public sousTraitances$ = this.sousTraitancesSubject.asObservable();

  private interventionsSubject = new BehaviorSubject<InterventionSousTraitance[]>([]);
  public interventions$ = this.interventionsSubject.asObservable();

  constructor() {
    this.loadMockData();
  }

  private loadMockData(): void {
    const mockSousTraitances: SousTraitance[] = [
      {
        id: '1',
        titre: 'Maintenance informatique serveurs',
        description: 'Maintenance préventive des serveurs de production',
        type: TypeSousTraitance.INFORMATIQUE,
        priorite: PrioriteSousTraitance.HAUTE,
        statut: StatutSousTraitance.PLANIFIEE,
        sousTraitant: 'TechnoServ SARL',
        contactSousTraitant: 'Ahmed Benali',
        telephoneSousTraitant: '+216 71 123 456',
        emailSousTraitant: 'contact@technoserv.tn',
        domaine: 'Informatique',
        emplacement: 'Salle serveur - Rack 1',
        demandeur: 'admin',
        dateCreation: new Date('2024-01-15'),
        dateDebutPrevue: new Date('2024-02-01'),
        dateFinPrevue: new Date('2024-02-01'),
        coutEstime: 1500,
        prestations: [
          {
            id: '1',
            nom: 'Maintenance serveur',
            quantite: 1,
            prixUnitaire: 1500,
            sousTraitant: 'TechnoServ SARL',
            reference: 'MAINT-SERV-001'
          }
        ],
        interventions: []
      },
      {
        id: '2',
        titre: 'Nettoyage bureaux',
        description: 'Nettoyage hebdomadaire des bureaux',
        type: TypeSousTraitance.NETTOYAGE,
        priorite: PrioriteSousTraitance.NORMALE,
        statut: StatutSousTraitance.EN_COURS,
        sousTraitant: 'CleanPro Services',
        contactSousTraitant: 'Fatma Khelifi',
        telephoneSousTraitant: '+216 20 987 654',
        emailSousTraitant: 'info@cleanpro.tn',
        domaine: 'Nettoyage',
        emplacement: 'Tous les bureaux',
        demandeur: 'RH Manager',
        dateCreation: new Date('2024-01-10'),
        dateDebutPrevue: new Date('2024-01-15'),
        dateFinPrevue: new Date('2024-12-31'),
        dateDebutEffective: new Date('2024-01-15'),
        coutEstime: 800,
        prestations: [],
        interventions: []
      }
    ];

    const mockInterventions: InterventionSousTraitance[] = [
      {
        id: '1',
        sousTraitanceId: '2',
        technicien: 'Équipe CleanPro',
        dateIntervention: new Date('2024-01-22'),
        duree: 4,
        description: 'Nettoyage complet des bureaux étage 1 et 2',
        statut: StatutIntervention.TERMINEE,
        observations: 'Travail satisfaisant'
      }
    ];

    this.sousTraitancesSubject.next(mockSousTraitances);
    this.interventionsSubject.next(mockInterventions);
  }

  getSousTraitances(): Observable<SousTraitance[]> {
    return this.sousTraitances$;
  }

  getSousTraitanceById(id: string): SousTraitance | undefined {
    return this.sousTraitancesSubject.value.find(s => s.id === id);
  }

  addSousTraitance(sousTraitance: Omit<SousTraitance, 'id' | 'dateCreation' | 'interventions'>): void {
    const newSousTraitance: SousTraitance = {
      ...sousTraitance,
      id: Date.now().toString(),
      dateCreation: new Date(),
      interventions: []
    };

    const currentSousTraitances = this.sousTraitancesSubject.value;
    this.sousTraitancesSubject.next([...currentSousTraitances, newSousTraitance]);
  }

  updateSousTraitance(id: string, updates: Partial<SousTraitance>): void {
    const currentSousTraitances = this.sousTraitancesSubject.value;
    const updatedSousTraitances = currentSousTraitances.map(sousTraitance => 
      sousTraitance.id === id ? { ...sousTraitance, ...updates } : sousTraitance
    );
    this.sousTraitancesSubject.next(updatedSousTraitances);
  }

  deleteSousTraitance(id: string): void {
    const currentSousTraitances = this.sousTraitancesSubject.value;
    const filteredSousTraitances = currentSousTraitances.filter(s => s.id !== id);
    this.sousTraitancesSubject.next(filteredSousTraitances);

    // Supprimer les interventions associées
    const currentInterventions = this.interventionsSubject.value;
    const filteredInterventions = currentInterventions.filter(i => i.sousTraitanceId !== id);
    this.interventionsSubject.next(filteredInterventions);
  }

  addIntervention(intervention: Omit<InterventionSousTraitance, 'id'>): void {
    const newIntervention: InterventionSousTraitance = {
      ...intervention,
      id: Date.now().toString()
    };

    const currentInterventions = this.interventionsSubject.value;
    this.interventionsSubject.next([...currentInterventions, newIntervention]);
  }

  getInterventionsBySousTraitanceId(sousTraitanceId: string): InterventionSousTraitance[] {
    return this.interventionsSubject.value.filter(i => i.sousTraitanceId === sousTraitanceId);
  }

  getSousTraitanceStats(): SousTraitanceStats {
    const sousTraitances = this.sousTraitancesSubject.value;
    const interventions = this.interventionsSubject.value;

    const sousTraitancesEnCours = sousTraitances.filter(s => s.statut === StatutSousTraitance.EN_COURS).length;
    const sousTraitancesPlanifiees = sousTraitances.filter(s => s.statut === StatutSousTraitance.PLANIFIEE).length;
    
    const coutTotalSousTraitance = sousTraitances.reduce((total, s) => {
      const coutPrestations = s.prestations.reduce((sum, p) => sum + (p.quantite * p.prixUnitaire), 0);
      return total + (s.coutReel || s.coutEstime) + coutPrestations;
    }, 0);

    const sousTraitantsUniques = new Set(sousTraitances.map(s => s.sousTraitant));
    const nombreSousTraitants = sousTraitantsUniques.size;

    const tauxSatisfaction = 85; // À calculer selon la logique métier

    return {
      totalSousTraitances: sousTraitances.length,
      sousTraitancesEnCours,
      sousTraitancesPlanifiees,
      coutTotalSousTraitance,
      nombreSousTraitants,
      tauxSatisfaction
    };
  }
}