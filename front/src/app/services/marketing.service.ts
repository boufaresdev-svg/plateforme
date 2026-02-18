import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { 
  CampagneMarketing, 
  KPICampagne, 
  ContenuMarketing, 
  ResultatCampagne, 
  TypeCampagne, 
  CanalMarketing, 
  StatutCampagne, 
  TypeContenu, 
  MarketingStats 
} from '../models/marketing.model';

@Injectable({
  providedIn: 'root'
})
export class MarketingService {
  private campagnesSubject = new BehaviorSubject<CampagneMarketing[]>([]);
  public campagnes$ = this.campagnesSubject.asObservable();

  private contenusSubject = new BehaviorSubject<ContenuMarketing[]>([]);
  public contenus$ = this.contenusSubject.asObservable();

  private resultatsSubject = new BehaviorSubject<ResultatCampagne[]>([]);
  public resultats$ = this.resultatsSubject.asObservable();

  constructor() {
    this.loadMockData();
  }

  private loadMockData(): void {
    const mockCampagnes: CampagneMarketing[] = [
      {
        id: '1',
        nom: 'Campagne Lancement Produit',
        description: 'Campagne de lancement du nouveau service SMS2i',
        type: TypeCampagne.AWARENESS,
        canal: CanalMarketing.FACEBOOK,
        statut: StatutCampagne.EN_COURS,
        budget: 5000,
        coutReel: 2800,
        dateDebut: new Date('2024-01-15'),
        dateFin: new Date('2024-02-15'),
        cible: 'Entreprises 50-200 employés',
        objectifs: ['Notoriété', 'Génération de leads', 'Trafic site web'],
        kpis: [
          { id: '1', nom: 'Impressions', objectif: 100000, actuel: 75000, unite: 'vues' },
          { id: '2', nom: 'Clics', objectif: 2000, actuel: 1200, unite: 'clics' },
          { id: '3', nom: 'Leads', objectif: 50, actuel: 28, unite: 'leads' }
        ],
        contenu: [],
        resultats: [],
        responsable: 'Fatma Trabelsi',
        createdAt: new Date('2024-01-10'),
        updatedAt: new Date('2024-01-20')
      },
      {
        id: '2',
        nom: 'SEO Optimisation',
        description: 'Amélioration du référencement naturel',
        type: TypeCampagne.LEAD_GENERATION,
        canal: CanalMarketing.SEO,
        statut: StatutCampagne.PLANIFIEE,
        budget: 3000,
        dateDebut: new Date('2024-02-01'),
        dateFin: new Date('2024-05-01'),
        cible: 'Recherches organiques',
        objectifs: ['Améliorer le ranking', 'Augmenter le trafic organique'],
        kpis: [
          { id: '4', nom: 'Position moyenne', objectif: 5, actuel: 12, unite: 'position' },
          { id: '5', nom: 'Trafic organique', objectif: 1000, actuel: 450, unite: 'visiteurs/mois' }
        ],
        contenu: [],
        resultats: [],
        responsable: 'Mohamed Ben Ahmed',
        createdAt: new Date('2024-01-25'),
        updatedAt: new Date('2024-01-25')
      }
    ];

    const mockContenus: ContenuMarketing[] = [
      {
        id: '1',
        campagneId: '1',
        titre: 'Vidéo présentation service',
        type: TypeContenu.VIDEO,
        url: 'https://example.com/video1',
        datePublication: new Date('2024-01-16'),
        vues: 15000,
        interactions: 450
      },
      {
        id: '2',
        campagneId: '1',
        titre: 'Article blog fonctionnalités',
        type: TypeContenu.ARTICLE,
        url: 'https://example.com/article1',
        datePublication: new Date('2024-01-18'),
        vues: 2500,
        interactions: 85
      }
    ];

    const mockResultats: ResultatCampagne[] = [
      {
        id: '1',
        campagneId: '1',
        date: new Date('2024-01-20'),
        impressions: 25000,
        clics: 400,
        conversions: 12,
        cout: 800,
        revenus: 2400
      },
      {
        id: '2',
        campagneId: '1',
        date: new Date('2024-01-25'),
        impressions: 30000,
        clics: 500,
        conversions: 16,
        cout: 1000,
        revenus: 3200
      }
    ];

    this.campagnesSubject.next(mockCampagnes);
    this.contenusSubject.next(mockContenus);
    this.resultatsSubject.next(mockResultats);
  }

  getCampagnes(): Observable<CampagneMarketing[]> {
    return this.campagnes$;
  }

  getCampagneById(id: string): CampagneMarketing | undefined {
    return this.campagnesSubject.value.find(c => c.id === id);
  }

  addCampagne(campagne: Omit<CampagneMarketing, 'id' | 'createdAt' | 'updatedAt' | 'contenu' | 'resultats'>): void {
    const newCampagne: CampagneMarketing = {
      ...campagne,
      id: Date.now().toString(),
      contenu: [],
      resultats: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const currentCampagnes = this.campagnesSubject.value;
    this.campagnesSubject.next([...currentCampagnes, newCampagne]);
  }

  updateCampagne(id: string, updates: Partial<CampagneMarketing>): void {
    const currentCampagnes = this.campagnesSubject.value;
    const updatedCampagnes = currentCampagnes.map(campagne => 
      campagne.id === id 
        ? { ...campagne, ...updates, updatedAt: new Date() }
        : campagne
    );
    this.campagnesSubject.next(updatedCampagnes);
  }

  deleteCampagne(id: string): void {
    const currentCampagnes = this.campagnesSubject.value;
    const filteredCampagnes = currentCampagnes.filter(c => c.id !== id);
    this.campagnesSubject.next(filteredCampagnes);

    // Supprimer les contenus et résultats associés
    const currentContenus = this.contenusSubject.value;
    const filteredContenus = currentContenus.filter(c => c.campagneId !== id);
    this.contenusSubject.next(filteredContenus);

    const currentResultats = this.resultatsSubject.value;
    const filteredResultats = currentResultats.filter(r => r.campagneId !== id);
    this.resultatsSubject.next(filteredResultats);
  }

  getContenusByCampagneId(campagneId: string): ContenuMarketing[] {
    return this.contenusSubject.value.filter(c => c.campagneId === campagneId);
  }

  addContenu(contenu: Omit<ContenuMarketing, 'id'>): void {
    const newContenu: ContenuMarketing = {
      ...contenu,
      id: Date.now().toString()
    };

    const currentContenus = this.contenusSubject.value;
    this.contenusSubject.next([...currentContenus, newContenu]);
  }

  getResultatsByCampagneId(campagneId: string): ResultatCampagne[] {
    return this.resultatsSubject.value.filter(r => r.campagneId === campagneId);
  }

  addResultat(resultat: Omit<ResultatCampagne, 'id'>): void {
    const newResultat: ResultatCampagne = {
      ...resultat,
      id: Date.now().toString()
    };

    const currentResultats = this.resultatsSubject.value;
    this.resultatsSubject.next([...currentResultats, newResultat]);
  }

  getMarketingStats(): MarketingStats {
    const campagnes = this.campagnesSubject.value;
    const resultats = this.resultatsSubject.value;

    const campagnesActives = campagnes.filter(c => c.statut === StatutCampagne.EN_COURS).length;
    const budgetTotal = campagnes.reduce((total, c) => total + c.budget, 0);

    const impressionsTotal = resultats.reduce((total, r) => total + r.impressions, 0);
    const clicsTotal = resultats.reduce((total, r) => total + r.clics, 0);
    const conversionsTotal = resultats.reduce((total, r) => total + r.conversions, 0);
    const coutTotal = resultats.reduce((total, r) => total + r.cout, 0);
    const revenusTotal = resultats.reduce((total, r) => total + r.revenus, 0);

    const tauxConversion = clicsTotal > 0 ? (conversionsTotal / clicsTotal) * 100 : 0;
    const roiMoyen = coutTotal > 0 ? ((revenusTotal - coutTotal) / coutTotal) * 100 : 0;
    const coutParLead = conversionsTotal > 0 ? coutTotal / conversionsTotal : 0;

    return {
      totalCampagnes: campagnes.length,
      campagnesActives,
      budgetTotal,
      impressionsTotal,
      tauxConversion: Math.round(tauxConversion * 100) / 100,
      roiMoyen: Math.round(roiMoyen),
      leadsGeneres: conversionsTotal,
      coutParLead: Math.round(coutParLead)
    };
  }
}