import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { 
  Pret, 
  ArticlePret, 
  StatutPret, 
  EtatArticle, 
  PretStats 
} from '../models/pret.model';

@Injectable({
  providedIn: 'root'
})
export class PretService {
  private pretsSubject = new BehaviorSubject<Pret[]>([]);
  public prets$ = this.pretsSubject.asObservable();

  constructor() {
    this.loadMockData();
  }

  private loadMockData(): void {
    const mockPrets: Pret[] = [
      {
        id: '1',
        emprunteur: 'Mohamed Ben Ahmed',
        emailEmprunteur: 'mohamed@sms2i.com',
        telephoneEmprunteur: '+216 20 123 456',
        departement: 'IT',
        articles: [
          {
            id: '1',
            stockId: '1',
            nomArticle: 'Ordinateur Portable Dell',
            quantitePretee: 1,
            quantiteRetournee: 0,
            etatDepart: EtatArticle.BON_ETAT
          }
        ],
        datePret: new Date('2024-01-15'),
        dateRetourPrevue: new Date('2024-02-15'),
        statut: StatutPret.EN_COURS,
        motif: 'Télétravail',
        validePar: 'admin',
        dateValidation: new Date('2024-01-15')
      },
      {
        id: '2',
        emprunteur: 'Fatma Trabelsi',
        emailEmprunteur: 'fatma@sms2i.com',
        telephoneEmprunteur: '+216 25 987 654',
        departement: 'Management',
        articles: [
          {
            id: '2',
            stockId: '2',
            nomArticle: 'Projecteur',
            quantitePretee: 1,
            quantiteRetournee: 1,
            etatDepart: EtatArticle.BON_ETAT,
            etatRetour: EtatArticle.BON_ETAT
          }
        ],
        datePret: new Date('2024-01-10'),
        dateRetourPrevue: new Date('2024-01-12'),
        dateRetourEffective: new Date('2024-01-12'),
        statut: StatutPret.RETOURNE,
        motif: 'Présentation client',
        validePar: 'admin',
        dateValidation: new Date('2024-01-10')
      }
    ];

    this.pretsSubject.next(mockPrets);
  }

  getPrets(): Observable<Pret[]> {
    return this.prets$;
  }

  getPretById(id: string): Pret | undefined {
    return this.pretsSubject.value.find(p => p.id === id);
  }

  addPret(pret: Omit<Pret, 'id' | 'dateValidation'>): void {
    const newPret: Pret = {
      ...pret,
      id: Date.now().toString(),
      dateValidation: new Date()
    };

    const currentPrets = this.pretsSubject.value;
    this.pretsSubject.next([...currentPrets, newPret]);
  }

  updatePret(id: string, updates: Partial<Pret>): void {
    const currentPrets = this.pretsSubject.value;
    const updatedPrets = currentPrets.map(pret => 
      pret.id === id ? { ...pret, ...updates } : pret
    );
    this.pretsSubject.next(updatedPrets);
  }

  deletePret(id: string): void {
    const currentPrets = this.pretsSubject.value;
    const filteredPrets = currentPrets.filter(p => p.id !== id);
    this.pretsSubject.next(filteredPrets);
  }

  retournerPret(id: string, articlesRetournes: ArticlePret[]): void {
    const pret = this.getPretById(id);
    if (!pret) return;

    const articlesUpdates = pret.articles.map(article => {
      const articleRetourne = articlesRetournes.find(ar => ar.id === article.id);
      return articleRetourne ? { ...article, ...articleRetourne } : article;
    });

    const toutRetourne = articlesUpdates.every(a => a.quantiteRetournee >= a.quantitePretee);
    const partiellementRetourne = articlesUpdates.some(a => a.quantiteRetournee > 0 && a.quantiteRetournee < a.quantitePretee);

    let nouveauStatut = StatutPret.EN_COURS;
    if (toutRetourne) {
      nouveauStatut = StatutPret.RETOURNE;
    } else if (partiellementRetourne) {
      nouveauStatut = StatutPret.PARTIELLEMENT_RETOURNE;
    }

    this.updatePret(id, {
      articles: articlesUpdates,
      statut: nouveauStatut,
      dateRetourEffective: toutRetourne ? new Date() : undefined
    });
  }

  getPretStats(): PretStats {
    const prets = this.pretsSubject.value;
    const today = new Date();

    const pretsEnCours = prets.filter(p => p.statut === StatutPret.EN_COURS).length;
    const pretsEnRetard = prets.filter(p => 
      p.statut === StatutPret.EN_COURS && p.dateRetourPrevue < today
    ).length;

    const articlesPretes = prets
      .filter(p => p.statut === StatutPret.EN_COURS)
      .reduce((total, p) => total + p.articles.reduce((sum, a) => sum + a.quantitePretee, 0), 0);

    const pretsRetournes = prets.filter(p => p.statut === StatutPret.RETOURNE).length;
    const tauxRetour = prets.length > 0 ? (pretsRetournes / prets.length) * 100 : 0;

    const currentMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    const pretsParMois = prets.filter(p => p.datePret >= currentMonth).length;

    return {
      totalPrets: prets.length,
      pretsEnCours,
      pretsEnRetard,
      articlesPretes,
      tauxRetour: Math.round(tauxRetour),
      pretsParMois
    };
  }
}