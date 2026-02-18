import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { StockTrace, StockActionType, StockTraceFilter, StockTraceStats } from '../models/stock-trace.model';

@Injectable({
  providedIn: 'root'
})
export class StockTraceService {
  private apiUrl = 'http://localhost:8080/api/stock/tracabilite';
  private stockTracesSubject = new BehaviorSubject<StockTrace[]>([]);
  public stockTraces$ = this.stockTracesSubject.asObservable();

  constructor(private http: HttpClient) {}

  getStockTraces(filter?: StockTraceFilter): Observable<StockTrace[]> {
    let params = new HttpParams();
    
    if (filter) {
      if (filter.stockId) params = params.set('articleId', filter.stockId);
      if (filter.dateDebut) {
        const dateDebut = typeof filter.dateDebut === 'string' ? new Date(filter.dateDebut) : filter.dateDebut;
        params = params.set('startDate', dateDebut.toISOString());
      }
      if (filter.dateFin) {
        const dateFin = typeof filter.dateFin === 'string' ? new Date(filter.dateFin) : filter.dateFin;
        params = params.set('endDate', dateFin.toISOString());
      }
      if (filter.actionType) params = params.set('typeMouvement', this.mapActionTypeToBackend(filter.actionType));
      if (filter.entrepot) params = params.set('entrepotId', filter.entrepot);
      if (filter.utilisateur) params = params.set('utilisateurId', filter.utilisateur);
    }

    return this.http.get<any[]>(this.apiUrl, { params }).pipe(
      map(mouvements => mouvements.map(m => this.mapBackendToTrace(m))),
      catchError(error => {
        console.error('Error loading stock traces:', error);
        return of([]);
      })
    );
  }

  private mapActionTypeToBackend(actionType: StockActionType): string {
    const mapping: { [key in StockActionType]: string } = {
      [StockActionType.ENTREE]: 'ENTREE',
      [StockActionType.SORTIE]: 'SORTIE',
      [StockActionType.TRANSFERT]: 'TRANSFERT',
      [StockActionType.AJUSTEMENT]: 'AJUSTEMENT',
      [StockActionType.AUGMENTATION]: 'ENTREE',
      [StockActionType.DIMINUTION]: 'SORTIE',
      [StockActionType.INVENTAIRE]: 'AJUSTEMENT',
      [StockActionType.CORRECTION]: 'AJUSTEMENT'
    };
    return mapping[actionType] || 'ENTREE';
  }

  private mapBackendToActionType(typeMouvement: string): StockActionType {
    const mapping: { [key: string]: StockActionType } = {
      'ENTREE': StockActionType.ENTREE,
      'SORTIE': StockActionType.SORTIE,
      'TRANSFERT': StockActionType.TRANSFERT,
      'AJUSTEMENT': StockActionType.AJUSTEMENT,
      'RETOUR_ENTREE': StockActionType.ENTREE,
      'RETOUR_SORTIE': StockActionType.SORTIE
    };
    return mapping[typeMouvement] || StockActionType.ENTREE;
  }

  private mapBackendToTrace(mouvement: any): StockTrace {
    return {
      id: mouvement.id,
      stockId: mouvement.articleId,
      articleNom: mouvement.articleNom || 'Article inconnu',
      stockBatch: mouvement.reference,
      entrepot: mouvement.entrepotNom || mouvement.sourceEntrepotNom || mouvement.destinationEntrepotNom || 'N/A',
      quantiteChanged: mouvement.typeMouvement === 'SORTIE' ? -mouvement.quantite : mouvement.quantite,
      previousQuantity: mouvement.quantitePrecedente || 0,
      userPerformedBy: mouvement.utilisateurNom || 'Système',
      dateReason: new Date(mouvement.dateMouvement),
      unitPrice: mouvement.prixUnitaire || 0,
      performedAt: new Date(mouvement.dateMouvement),
      actionType: this.mapBackendToActionType(mouvement.typeMouvement),
      motif: mouvement.motif || mouvement.commentaire || 'N/A',
      commentaire: mouvement.commentaire || `Réf: ${mouvement.reference || 'N/A'}`
    };
  }

  private getMotifFromMouvement(mouvement: any): string {
    if (mouvement.typeEntree) {
      return `${mouvement.typeEntree} - ${mouvement.numeroBonReception || ''}`;
    }
    if (mouvement.typeSortie) {
      return `${mouvement.typeSortie} - ${mouvement.numeroBonSortie || ''}`;
    }
    if (mouvement.typeMouvement === 'TRANSFERT') {
      return `Transfert: ${mouvement.sourceEntrepotNom} → ${mouvement.destinationEntrepotNom}`;
    }
    return mouvement.typeMouvement || 'Mouvement de stock';
  }

  private oldGetStockTraces(filter?: StockTraceFilter): Observable<StockTrace[]> {
    return this.stockTraces$.pipe(
      map(traces => {
        if (!filter) return traces;

        return traces.filter(trace => {
          // Filter by stock ID
          if (filter.stockId && trace.stockId !== filter.stockId) {
            return false;
          }

          // Filter by date range
          if (filter.dateDebut && new Date(trace.performedAt) < filter.dateDebut) {
            return false;
          }

          if (filter.dateFin && new Date(trace.performedAt) > filter.dateFin) {
            return false;
          }

          // Filter by action type
          if (filter.actionType && trace.actionType !== filter.actionType) {
            return false;
          }

          // Filter by user
          if (filter.utilisateur && 
              !trace.userPerformedBy.toLowerCase().includes(filter.utilisateur.toLowerCase())) {
            return false;
          }

          // Filter by warehouse
          if (filter.entrepot && 
              !trace.entrepot.toLowerCase().includes(filter.entrepot.toLowerCase())) {
            return false;
          }

          return true;
        });
      })
    );
  }

  getStockTraceById(id: string): Observable<StockTrace | undefined> {
    const params = new HttpParams().set('id', id);
    return this.http.get<any[]>(this.apiUrl, { params }).pipe(
      map(mouvements => {
        if (mouvements.length > 0) {
          return this.mapBackendToTrace(mouvements[0]);
        }
        return undefined;
      }),
      catchError(error => {
        console.error('Error loading stock trace by id:', error);
        return of(undefined);
      })
    );
  }

  getStockTracesByStockId(stockId: string): Observable<StockTrace[]> {
    return this.getStockTraces({ stockId });
  }

  addStockTrace(trace: Omit<StockTrace, 'id' | 'performedAt'>): void {
    const newTrace: StockTrace = {
      ...trace,
      id: Date.now().toString(),
      performedAt: new Date()
    };

    const currentTraces = this.stockTracesSubject.value;
    this.stockTracesSubject.next([...currentTraces, newTrace]);
  }

  getStockTraceStats(filter?: StockTraceFilter): Observable<StockTraceStats> {
    let params = new HttpParams();
    
    if (filter) {
      if (filter.stockId) params = params.set('articleId', filter.stockId);
      if (filter.dateDebut) {
        const dateDebut = typeof filter.dateDebut === 'string' ? new Date(filter.dateDebut) : filter.dateDebut;
        params = params.set('startDate', dateDebut.toISOString());
      }
      if (filter.dateFin) {
        const dateFin = typeof filter.dateFin === 'string' ? new Date(filter.dateFin) : filter.dateFin;
        params = params.set('endDate', dateFin.toISOString());
      }
      if (filter.actionType) params = params.set('typeMouvement', this.mapActionTypeToBackend(filter.actionType));
      if (filter.entrepot) params = params.set('entrepotId', filter.entrepot);
      if (filter.utilisateur) params = params.set('utilisateurId', filter.utilisateur);
    }

    return this.http.get<any>(`${this.apiUrl}/statistics`, { params }).pipe(
      map(backendStats => {
        const stats: StockTraceStats = {
          totalActions: backendStats.totalActions || 0,
          actionsParType: {},
          actionsParUtilisateur: {},
          quantiteTotaleModifiee: backendStats.quantiteTotaleModifiee || 0,
          valeurTotaleModifiee: backendStats.valeurTotaleModifiee || 0
        };

        // Convert backend type keys to frontend display format
        if (backendStats.actionsParType) {
          Object.keys(backendStats.actionsParType).forEach(key => {
            const actionType = this.mapBackendToActionType(key);
            stats.actionsParType[actionType] = backendStats.actionsParType[key];
          });
        }

        // Copy user stats
        if (backendStats.actionsParUtilisateur) {
          stats.actionsParUtilisateur = backendStats.actionsParUtilisateur;
        }

        return stats;
      }),
      catchError(error => {
        console.error('Error loading statistics:', error);
        return of({
          totalActions: 0,
          actionsParType: {},
          actionsParUtilisateur: {},
          quantiteTotaleModifiee: 0,
          valeurTotaleModifiee: 0
        });
      })
    );
  }

  // Method to record a stock action
  recordStockAction(
    stockId: string,
    actionType: StockActionType,
    quantiteChanged: number,
    previousQuantity: number,
    userPerformedBy: string,
    motif: string,
    unitPrice: number,
    entrepot: string,
    articleNom?: string,
    stockBatch?: string,
    commentaire?: string
  ): void {
    this.addStockTrace({
      stockId,
      articleNom: articleNom || `Article ${stockId}`,
      stockBatch,
      entrepot,
      quantiteChanged,
      previousQuantity,
      userPerformedBy,
      dateReason: new Date(),
      unitPrice,
      actionType,
      motif,
      commentaire
    });
  }

  // Methods for specific stock actions
  augmenterQuantite(
    stockId: string,
    quantite: number,
    previousQuantity: number,
    userPerformedBy: string,
    motif: string,
    unitPrice: number,
    entrepot: string,
    articleNom?: string,
    stockBatch?: string
  ): void {
    this.recordStockAction(
      stockId,
      StockActionType.AUGMENTATION,
      quantite,
      previousQuantity,
      userPerformedBy,
      motif,
      unitPrice,
      entrepot,
      articleNom,
      stockBatch,
      `Augmentation de ${quantite} unités`
    );
  }

  diminuerQuantite(
    stockId: string,
    quantite: number,
    previousQuantity: number,
    userPerformedBy: string,
    motif: string,
    unitPrice: number,
    entrepot: string,
    articleNom?: string,
    stockBatch?: string
  ): void {
    this.recordStockAction(
      stockId,
      StockActionType.DIMINUTION,
      -quantite,
      previousQuantity,
      userPerformedBy,
      motif,
      unitPrice,
      entrepot,
      articleNom,
      stockBatch,
      `Diminution de ${quantite} unités`
    );
  }

  ajusterQuantite(
    stockId: string,
    nouvelleQuantite: number,
    previousQuantity: number,
    userPerformedBy: string,
    motif: string,
    unitPrice: number,
    entrepot: string,
    articleNom?: string,
    stockBatch?: string
  ): void {
    const quantiteChanged = nouvelleQuantite - previousQuantity;
    this.recordStockAction(
      stockId,
      StockActionType.AJUSTEMENT,
      quantiteChanged,
      previousQuantity,
      userPerformedBy,
      motif,
      unitPrice,
      entrepot,
      articleNom,
      stockBatch,
      `Ajustement de ${previousQuantity} à ${nouvelleQuantite} unités`
    );
  }

  obtenirStockTheorique(stockId: string): Observable<number> {
    return this.getStockTracesByStockId(stockId).pipe(
      map(traces => {
        if (traces.length === 0) return 0;
        
        // Sort by date
        traces.sort((a, b) => new Date(a.performedAt).getTime() - new Date(b.performedAt).getTime());
        
        // Calculate theoretical stock
        let theoreticalStock = 0;
        traces.forEach(trace => {
          if (trace.actionType === StockActionType.INVENTAIRE) {
            theoreticalStock = trace.previousQuantity + trace.quantiteChanged;
          } else {
            theoreticalStock += trace.quantiteChanged;
          }
        });
        
        return theoreticalStock;
      })
    );
  }

  obtenirStockPhysique(stockId: string): Observable<number> {
    // This would typically come from the main stock service
    // For now, return theoretical stock
    return this.obtenirStockTheorique(stockId);
  }

  // Export functionality
  exportTracesToCSV(traces: StockTrace[]): string {
    const headers = [
      'ID', 'Article ID', 'Lot', 'Action', 'Quantité Modifiée', 
      'Stock Précédent', 'Stock Actuel', 'Utilisateur', 'Motif', 
      'Prix Unitaire', 'Entrepôt', 'Date/Heure', 'Commentaire'
    ];

    const csvContent = [
      headers.join(','),
      ...traces.map(trace => [
        trace.id,
        trace.stockId,
        trace.stockBatch || '',
        trace.actionType,
        trace.quantiteChanged,
        trace.previousQuantity,
        trace.previousQuantity + trace.quantiteChanged,
        trace.userPerformedBy,
        `"${trace.motif}"`,
        trace.unitPrice,
        trace.entrepot,
        trace.performedAt.toISOString(),
        `"${trace.commentaire || ''}"`
      ].join(','))
    ].join('\n');

    return csvContent;
  }
}