import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
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
} from '../models/prestataire.model';

@Injectable({
  providedIn: 'root'
})
export class PrestataireService {
  private prestatairesSubject = new BehaviorSubject<Prestataire[]>([]);
  public prestataires$ = this.prestatairesSubject.asObservable();

  private evaluationsSubject = new BehaviorSubject<EvaluationPrestataire[]>([]);
  public evaluations$ = this.evaluationsSubject.asObservable();

  private contratsSubject = new BehaviorSubject<ContratPrestataire[]>([]);
  public contrats$ = this.contratsSubject.asObservable();

  constructor() {
    this.loadMockData();
  }

  private loadMockData(): void {
    const mockPrestataires: Prestataire[] = [
      {
        id: '1',
        nom: 'TechnoServ',
        raisonSociale: 'TechnoServ SARL',
        siret: '12345678901234',
        secteur: SecteurPrestataire.INFORMATIQUE,
        type: TypePrestataire.ENTREPRISE,
        statut: StatutPrestataire.ACTIF,
        adresse: '123 Avenue de la Technologie',
        ville: 'Tunis',
        codePostal: '1000',
        pays: 'Tunisie',
        telephone: '+216 71 123 456',
        email: 'contact@technoserv.tn',
        siteWeb: 'https://technoserv.tn',
        contactPrincipal: {
          id: '1',
          nom: 'Benali',
          prenom: 'Ahmed',
          poste: 'Directeur Technique',
          telephone: '+216 20 123 456',
          email: 'ahmed.benali@technoserv.tn',
          principal: true
        },
        contacts: [],
        specialites: ['Maintenance serveurs', 'Réseau', 'Sécurité informatique'],
        certifications: ['ISO 27001', 'Cisco Partner'],
        evaluations: [],
        contrats: [],
        conditions: {
          delaiPaiement: 30,
          remise: 5,
          fraisDeplacementKm: 0.5,
          garantie: 12,
          disponibiliteUrgence: true
        },
        documents: ['kbis.pdf', 'assurance.pdf'],
        createdAt: new Date('2023-06-15'),
        updatedAt: new Date('2024-01-10')
      },
      {
        id: '2',
        nom: 'CleanPro Services',
        raisonSociale: 'CleanPro Services SARL',
        siret: '98765432109876',
        secteur: SecteurPrestataire.NETTOYAGE,
        type: TypePrestataire.ENTREPRISE,
        statut: StatutPrestataire.ACTIF,
        adresse: '456 Rue du Commerce',
        ville: 'Sfax',
        codePostal: '3000',
        pays: 'Tunisie',
        telephone: '+216 74 987 654',
        email: 'info@cleanpro.tn',
        contactPrincipal: {
          id: '2',
          nom: 'Khelifi',
          prenom: 'Fatma',
          poste: 'Responsable Opérations',
          telephone: '+216 25 987 654',
          email: 'fatma.khelifi@cleanpro.tn',
          principal: true
        },
        contacts: [],
        specialites: ['Nettoyage bureaux', 'Désinfection', 'Entretien'],
        certifications: ['ISO 9001'],
        evaluations: [],
        contrats: [],
        conditions: {
          delaiPaiement: 15,
          remise: 10,
          fraisDeplacementKm: 0.3,
          garantie: 6,
          disponibiliteUrgence: false
        },
        documents: ['kbis.pdf'],
        createdAt: new Date('2023-08-20'),
        updatedAt: new Date('2024-01-05')
      }
    ];

    const mockEvaluations: EvaluationPrestataire[] = [
      {
        id: '1',
        prestataireId: '1',
        date: new Date('2024-01-15'),
        qualite: 4,
        delais: 5,
        service: 4,
        prix: 3,
        noteGlobale: 4,
        commentaires: 'Très bon service technique, délais respectés',
        evaluateur: 'Mohamed Ben Ahmed'
      },
      {
        id: '2',
        prestataireId: '2',
        date: new Date('2024-01-20'),
        qualite: 5,
        delais: 4,
        service: 5,
        prix: 4,
        noteGlobale: 4.5,
        commentaires: 'Excellent travail de nettoyage, équipe professionnelle',
        evaluateur: 'Fatma Trabelsi'
      }
    ];

    const mockContrats: ContratPrestataire[] = [
      {
        id: '1',
        prestataireId: '1',
        numero: 'CONT-PREST-2024-001',
        type: TypeContratPrestataire.CADRE,
        dateDebut: new Date('2024-01-01'),
        dateFin: new Date('2024-12-31'),
        montant: 50000,
        statut: StatutContratPrestataire.ACTIF,
        conditions: 'Maintenance informatique annuelle'
      },
      {
        id: '2',
        prestataireId: '2',
        numero: 'CONT-PREST-2024-002',
        type: TypeContratPrestataire.PONCTUEL,
        dateDebut: new Date('2024-01-15'),
        dateFin: new Date('2024-12-31'),
        montant: 9600,
        statut: StatutContratPrestataire.ACTIF,
        conditions: 'Nettoyage hebdomadaire des bureaux'
      }
    ];

    this.prestatairesSubject.next(mockPrestataires);
    this.evaluationsSubject.next(mockEvaluations);
    this.contratsSubject.next(mockContrats);
  }

  getPrestataires(): Observable<Prestataire[]> {
    return this.prestataires$;
  }

  getPrestataireById(id: string): Prestataire | undefined {
    return this.prestatairesSubject.value.find(p => p.id === id);
  }

  addPrestataire(prestataire: Omit<Prestataire, 'id' | 'createdAt' | 'updatedAt' | 'contacts' | 'evaluations' | 'contrats'>): void {
    const newPrestataire: Prestataire = {
      ...prestataire,
      id: Date.now().toString(),
      contacts: [],
      evaluations: [],
      contrats: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentPrestataires = this.prestatairesSubject.value;
    this.prestatairesSubject.next([...currentPrestataires, newPrestataire]);
  }

  updatePrestataire(id: string, updates: Partial<Prestataire>): void {
    const currentPrestataires = this.prestatairesSubject.value;
    const updatedPrestataires = currentPrestataires.map(prestataire => 
      prestataire.id === id 
        ? { ...prestataire, ...updates, updatedAt: new Date() }
        : prestataire
    );
    this.prestatairesSubject.next(updatedPrestataires);
  }

  deletePrestataire(id: string): void {
    const currentPrestataires = this.prestatairesSubject.value;
    const filteredPrestataires = currentPrestataires.filter(p => p.id !== id);
    this.prestatairesSubject.next(filteredPrestataires);

    // Supprimer les éléments associés
    const currentEvaluations = this.evaluationsSubject.value;
    const filteredEvaluations = currentEvaluations.filter(e => e.prestataireId !== id);
    this.evaluationsSubject.next(filteredEvaluations);

    const currentContrats = this.contratsSubject.value;
    const filteredContrats = currentContrats.filter(c => c.prestataireId !== id);
    this.contratsSubject.next(filteredContrats);
  }

  getEvaluationsByPrestataireId(prestataireId: string): EvaluationPrestataire[] {
    return this.evaluationsSubject.value.filter(e => e.prestataireId === prestataireId);
  }

  addEvaluation(evaluation: Omit<EvaluationPrestataire, 'id'>): void {
    const newEvaluation: EvaluationPrestataire = {
      ...evaluation,
      id: Date.now().toString()
    };

    const currentEvaluations = this.evaluationsSubject.value;
    this.evaluationsSubject.next([...currentEvaluations, newEvaluation]);
  }

  getContratsByPrestataireId(prestataireId: string): ContratPrestataire[] {
    return this.contratsSubject.value.filter(c => c.prestataireId === prestataireId);
  }

  addContrat(contrat: Omit<ContratPrestataire, 'id'>): void {
    const newContrat: ContratPrestataire = {
      ...contrat,
      id: Date.now().toString()
    };

    const currentContrats = this.contratsSubject.value;
    this.contratsSubject.next([...currentContrats, newContrat]);
  }

  getPrestataireStats(): PrestataireStats {
    const prestataires = this.prestatairesSubject.value;
    const evaluations = this.evaluationsSubject.value;
    const contrats = this.contratsSubject.value;

    const prestatairesActifs = prestataires.filter(p => p.statut === StatutPrestataire.ACTIF).length;
    const contratsActifs = contrats.filter(c => c.statut === StatutContratPrestataire.ACTIF).length;
    const montantContrats = contrats
      .filter(c => c.statut === StatutContratPrestataire.ACTIF)
      .reduce((total, c) => total + c.montant, 0);

    const noteMoyenne = evaluations.length > 0 
      ? evaluations.reduce((total, e) => total + e.noteGlobale, 0) / evaluations.length 
      : 0;

    const specialitesUniques = new Set(prestataires.flatMap(p => p.specialites)).size;
    const tauxSatisfaction = 92; // À calculer selon la logique métier

    const lastMonth = new Date();
    lastMonth.setMonth(lastMonth.getMonth() - 1);
    const interventionsRecentes = 15; // À calculer selon la logique métier

    return {
      totalPrestataires: prestataires.length,
      prestatairesActifs,
      contratsActifs,
      montantContrats,
      noteMoyenne: Math.round(noteMoyenne * 10) / 10,
      specialitesUniques,
      tauxSatisfaction,
      interventionsRecentes
    };
  }
}