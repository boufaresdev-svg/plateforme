import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import {
  Stock,
  StockLegacy,
  MouvementStock,
  CategorieStock,
  StatutStock,
  TypeMouvement,
  StatutMouvement,
  StockStats,
  AddStockCommand,
  UpdateStockCommand,
  DeleteStockCommand,
  StockQuery,
  StockResponse,
  PageResponse
} from '../models/stock.model';
import { StockTraceService } from './stock-trace.service';
import { StockActionType } from '../models/stock-trace.model';
import { environment } from '../../environment/environement';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class StockService {
  private apiUrl = `${environment.apiUrl}/stock`;

  private stocksSubject = new BehaviorSubject<Stock[]>([]);
  public stocks$ = this.stocksSubject.asObservable();

  private mouvementsSubject = new BehaviorSubject<MouvementStock[]>([]);
  public mouvements$ = this.mouvementsSubject.asObservable();

  constructor(
    private http: HttpClient,
    private stockTraceService: StockTraceService,
    private notificationService: NotificationService
  ) {
    this.loadMockData(); // Keep for compatibility, will be removed later
  }

  private loadMockData(): void {
    const mockStocks: Stock[] = [
      {
        id: '1',
        articleId: '1',
        entrepotId: '1',
        quantite: 15,
        dateDexpiration: new Date('2025-12-31'),
        createdBy: 'admin',
        updatedBy: 'admin',
        createdAt: new Date('2024-01-15'),
        updatedAt: new Date('2024-01-20'),
        article: {
          id: '1',
          nom: 'Ordinateur Portable Dell',
          description: 'Dell Latitude 5520, Intel i5, 8GB RAM, 256GB SSD',
          categorie: CategorieStock.MATERIEL_INFORMATIQUE,
          prixVente: 1200,
          prixUnitaire: 1200,
          stockMinimum: 5,
          stockMaximum: 30,
          quantiteMinimale: 5,
          quantiteMaximale: 30,
          fournisseurPrincipal: 'Dell Technologies',
          codeBarres: '1234567890123',
          estActif: true
        }
      },
      {
        id: '2',
        articleId: '2',
        entrepotId: '1',
        quantite: 2,
        createdBy: 'admin',
        updatedAt: new Date('2024-01-25'),
        createdAt: new Date('2024-01-10'),
        article: {
          id: '2',
          nom: 'Papier A4',
          description: 'Ramettes de papier A4 80g/m²',
          categorie: CategorieStock.FOURNITURES_BUREAU,
          prixVente: 5,
          prixUnitaire: 5,
          stockMinimum: 10,
          stockMaximum: 100,
          quantiteMinimale: 10,
          quantiteMaximale: 100,
          fournisseurPrincipal: 'Bureau Plus',
          estActif: true
        }
      }
    ];

    const mockMouvements: MouvementStock[] = [
      {
        id: '1',
        articleId: '1',
        entrepotId: '1',
        stockId: '1',
        typeMouvement: TypeMouvement.ENTREE,
        quantite: 10,
        utilisateurId: 'admin',
        dateMouvement: new Date('2024-01-15'),
        date: new Date('2024-01-15'),
        statut: StatutMouvement.VALIDE
      },
      {
        id: '2',
        articleId: '1',
        entrepotId: '1',
        stockId: '1',
        typeMouvement: TypeMouvement.SORTIE,
        quantite: 2,
        utilisateurId: 'admin',
        dateMouvement: new Date('2024-01-20'),
        date: new Date('2024-01-20'),
        statut: StatutMouvement.VALIDE
      }
    ];

    this.stocksSubject.next(mockStocks);
    this.mouvementsSubject.next(mockMouvements);
  }

  getStocks(): Observable<Stock[]> {
    return this.stocks$;
  }

  getStockByIdLegacy(id: string): Stock | StockLegacy | undefined {
    return this.stocksSubject.value.find(s => s.id === id);
  }

  addStockLegacy(stock: Omit<StockLegacy, 'id' | 'dateAjout' | 'dateModification' | 'mouvements'>): void {
    const newStock: Stock = {
      ...stock,
      id: Date.now().toString(),
      dateAjout: new Date(),
      dateModification: new Date(),
      mouvements: []
    } as any;

    const currentStocks = this.stocksSubject.value;
    this.stocksSubject.next([...currentStocks, newStock]);
  }

  updateStockLegacy(id: string, updates: Partial<Stock | StockLegacy>, userPerformedBy: string = 'Système', motif: string = 'Mise à jour stock'): void {
    const currentStocks = this.stocksSubject.value;
    const originalStock = currentStocks.find(s => s.id === id);

    if (!originalStock) return;

    const updatedStocks = currentStocks.map(stock => {
      if (stock.id === id) {
        const updatedStock = { ...stock, ...updates } as Stock | StockLegacy;
        if (this.isLegacyStock(stock)) {
          (updatedStock as StockLegacy).dateModification = new Date();
        } else {
          (updatedStock as Stock).updatedAt = new Date();
        }
        return updatedStock;
      }
      return stock;
    });

    // Record stock trace if quantity changed
    const oldQuantity = this.isLegacyStock(originalStock) ? originalStock.quantiteStock : originalStock.quantite;
    let newQuantity = oldQuantity;

    if ('quantiteStock' in updates && updates.quantiteStock !== undefined) {
      newQuantity = updates.quantiteStock;
    } else if ('quantite' in updates && updates.quantite !== undefined) {
      newQuantity = updates.quantite;
    }

    if (newQuantity !== oldQuantity) {
      const quantiteChanged = newQuantity - oldQuantity;
      let actionType: StockActionType;

      if (quantiteChanged > 0) {
        actionType = StockActionType.AUGMENTATION;
      } else if (quantiteChanged < 0) {
        actionType = StockActionType.DIMINUTION;
      } else {
        actionType = StockActionType.AJUSTEMENT;
      }

      this.stockTraceService.recordStockAction(
        id,
        actionType,
        quantiteChanged,
        oldQuantity,
        userPerformedBy,
        motif,
        this.isLegacyStock(originalStock) ? originalStock.prixUnitaire : (originalStock.article?.prixUnitaire || 0),
        this.isLegacyStock(originalStock) ? originalStock.emplacement : '',
        this.isLegacyStock(originalStock) ? originalStock.codeBarres : '',
        `Quantité modifiée de ${oldQuantity} à ${newQuantity}`
      );
    }

    this.stocksSubject.next(updatedStocks as Stock[]);
  }

  // Helper method to check if stock is legacy
  isLegacyStock(stock: Stock | StockLegacy): stock is StockLegacy {
    return 'nom' in stock && 'quantiteStock' in stock;
  }

  deleteStockLegacy(id: string): void {
    const currentStocks = this.stocksSubject.value;
    const filteredStocks = currentStocks.filter(s => s.id !== id);
    this.stocksSubject.next(filteredStocks);
  }

  // New methods for stock quantity operations with tracing
  augmenterStock(
    stockId: string,
    quantite: number,
    userPerformedBy: string,
    motif: string
  ): boolean {
    const stock = this.getStockByIdLegacy(stockId);
    if (!stock) return false;

    const currentQuantity = this.isLegacyStock(stock) ? stock.quantiteStock : stock.quantite;
    const nouvelleQuantite = currentQuantity + quantite;

    if (this.isLegacyStock(stock)) {
      this.updateStockLegacy(stockId, { quantiteStock: nouvelleQuantite }, userPerformedBy, motif);
    } else {
      this.updateStockLegacy(stockId, { quantite: nouvelleQuantite }, userPerformedBy, motif);
    }
    return true;
  }

  diminuerStock(
    stockId: string,
    quantite: number,
    userPerformedBy: string,
    motif: string
  ): boolean {
    const stock = this.getStockByIdLegacy(stockId);
    if (!stock) return false;

    const currentQuantity = this.isLegacyStock(stock) ? stock.quantiteStock : stock.quantite;
    if (currentQuantity < quantite) return false;

    const nouvelleQuantite = currentQuantity - quantite;

    if (this.isLegacyStock(stock)) {
      this.updateStockLegacy(stockId, { quantiteStock: nouvelleQuantite }, userPerformedBy, motif);
    } else {
      this.updateStockLegacy(stockId, { quantite: nouvelleQuantite }, userPerformedBy, motif);
    }
    return true;
  }

  ajusterStock(
    stockId: string,
    nouvelleQuantite: number,
    userPerformedBy: string,
    motif: string
  ): boolean {
    const stock = this.getStockByIdLegacy(stockId);
    if (!stock) return false;

    if (this.isLegacyStock(stock)) {
      this.updateStockLegacy(stockId, { quantiteStock: nouvelleQuantite }, userPerformedBy, motif);
    } else {
      this.updateStockLegacy(stockId, { quantite: nouvelleQuantite }, userPerformedBy, motif);
    }
    return true;
  }

  transfererStock(
    stockIdSource: string,
    stockIdDestination: string,
    quantite: number,
    userPerformedBy: string,
    motif: string
  ): boolean {
    const stockSource = this.getStockByIdLegacy(stockIdSource);
    const stockDestination = this.getStockByIdLegacy(stockIdDestination);

    if (!stockSource || !stockDestination) {
      return false;
    }

    const sourceQuantity = this.isLegacyStock(stockSource) ? stockSource.quantiteStock : stockSource.quantite;
    if (sourceQuantity < quantite) {
      return false;
    }

    const destName = this.isLegacyStock(stockDestination) ? stockDestination.nom : (stockDestination.article?.nom || 'Article');
    const sourceName = this.isLegacyStock(stockSource) ? stockSource.nom : (stockSource.article?.nom || 'Article');

    // Diminuer le stock source
    this.diminuerStock(stockIdSource, quantite, userPerformedBy, `Transfert vers ${destName}: ${motif}`);

    // Augmenter le stock destination
    this.augmenterStock(stockIdDestination, quantite, userPerformedBy, `Transfert depuis ${sourceName}: ${motif}`);

    return true;
  }

  addMouvement(mouvement: Omit<MouvementStock, 'id'>): void {
    const newMouvement: MouvementStock = {
      ...mouvement,
      id: Date.now().toString()
    };

    const currentMouvements = this.mouvementsSubject.value;
    this.mouvementsSubject.next([...currentMouvements, newMouvement]);

    // Mettre à jour le stock
    if (mouvement.stockId) {
      const stock = this.getStockByIdLegacy(mouvement.stockId);
      if (stock) {
        let nouvelleQuantite = this.isLegacyStock(stock) ? stock.quantiteStock : stock.quantite;

        switch (mouvement.type) {
          case TypeMouvement.ENTREE:
          case TypeMouvement.RETOUR:
            nouvelleQuantite += mouvement.quantite;
            break;
          case TypeMouvement.SORTIE:
            nouvelleQuantite -= mouvement.quantite;
            break;
          case TypeMouvement.AJUSTEMENT:
            nouvelleQuantite = mouvement.quantite;
            break;
        }

        const minQty = this.isLegacyStock(stock) ? stock.quantiteMinimale : (stock.article?.stockMinimum || 0);
        const nouveauStatut = nouvelleQuantite <= 0
          ? StatutStock.RUPTURE
          : nouvelleQuantite <= minQty
            ? StatutStock.DISPONIBLE
            : StatutStock.DISPONIBLE;

        if (this.isLegacyStock(stock)) {
          this.updateStockLegacy(mouvement.stockId, {
            quantiteStock: nouvelleQuantite,
            statut: nouveauStatut
          });
        } else {
          this.updateStockLegacy(mouvement.stockId, {
            quantite: nouvelleQuantite
          });
        }
      }
    }
  }

  getMouvementsByStockId(stockId: string): MouvementStock[] {
    return this.mouvementsSubject.value.filter(m => m.stockId === stockId);
  }

  getStockStats(): StockStats {
    const stocks = this.stocksSubject.value;
    const mouvements = this.mouvementsSubject.value;

    const articlesDisponibles = stocks.filter(s => (s as any).statut === StatutStock.DISPONIBLE).length;
    const articlesRupture = stocks.filter(s => (s as any).statut === StatutStock.RUPTURE).length;
    const valeurTotaleStock = stocks.reduce((total, s) => total + ((s as any).quantiteStock * ((s as any).prixUnitaire || 0)), 0);
    const alertesStock = stocks.filter(s => (s as any).quantiteStock <= (s as any).quantiteMinimale).length;

    const today = new Date();
    const lastMonth = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
    const mouvementsRecents = mouvements.filter(m => (m.date || m.dateMouvement) >= lastMonth).length;

    return {
      totalArticles: stocks.length,
      articlesDisponibles,
      articlesRupture,
      valeurTotaleStock,
      mouvementsRecents,
      alertesStock,
      totalEntrepots: 0,
      totalCategories: 0,
      totalMarques: 0,
      articlesParCategorie: {},
      articlesParMarque: {},
      stockParEntrepot: {}
    };
  }

  // ==================== NEW API METHODS ====================

  /**
   * Add new stock
   */
  addStock(command: AddStockCommand): Observable<StockResponse> {
    return this.http.post<StockResponse>(this.apiUrl, command)
      .pipe(
        catchError(error => {
          console.error('Error adding stock:', error);
          this.notificationService.error('Erreur lors de l\'ajout du stock. Vérifiez les données saisies.');
          return throwError(error);
        })
      );
  }

  /**
   * Update existing stock
   */
  updateStock(id: string, command: UpdateStockCommand): Observable<StockResponse> {
    return this.http.put<StockResponse>(`${this.apiUrl}/${id}`, command)
      .pipe(
        catchError(error => {
          console.error('Error updating stock:', error);
          this.notificationService.error('Erreur lors de la modification du stock. Veuillez réessayer.');
          return throwError(error);
        })
      );
  }

  /**
   * Delete stock
   */
  deleteStock(id: string): Observable<{ success: boolean, message: string }> {
    return this.http.delete<{ success: boolean, message: string }>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(error => {
          console.error('Error deleting stock:', error);
          this.notificationService.error('Erreur lors de la suppression du stock. Veuillez réessayer.');
          return throwError(error);
        })
      );
  }

  /**
   * Get stock by ID
   */
  getStockById(id: string): Observable<StockResponse> {
    return this.http.get<StockResponse>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(error => {
          console.error('Error getting stock by ID:', error);
          this.notificationService.error('Impossible de charger les détails du stock.');
          return throwError(error);
        })
      );
  }

  /**
   * Get all stocks with optional filters
   */
  getAllStocks(query?: StockQuery): Observable<PageResponse<StockResponse>> {
    let params = new HttpParams();

    if (query?.articleId) {
      params = params.set('articleId', query.articleId);
    }
    if (query?.entrepotId) {
      params = params.set('entrepotId', query.entrepotId);
    }
    if (query?.categorieId) {
      params = params.set('categorieId', query.categorieId);
    }
    if (query?.marqueId) {
      params = params.set('marqueId', query.marqueId);
    }
    if (query?.fournisseurId) {
      params = params.set('fournisseurId', query.fournisseurId);
    }
    if (query?.statut) {
      params = params.set('statut', query.statut);
    }
    if (query?.searchTerm) {
      params = params.set('searchTerm', query.searchTerm);
    }
    if (query?.page !== undefined) {
      params = params.set('page', query.page.toString());
    }
    if (query?.size !== undefined) {
      params = params.set('size', query.size.toString());
    }

    return this.http.get<StockResponse[]>(this.apiUrl, { params })
      .pipe(
        map((stocks: StockResponse[]) => {
          // Backend returns plain array, convert to PageResponse
          const page = query?.page || 0;
          const size = query?.size || 15;
          
          // Since backend doesn't return pagination info, create a mock PageResponse
          const pageResponse: PageResponse<StockResponse> = {
            content: stocks,
            totalElements: stocks.length,
            totalPages: 1,
            size: size,
            number: page,
            first: page === 0,
            last: true,
            numberOfElements: stocks.length,
            empty: stocks.length === 0
          };
          return pageResponse;
        }),
        catchError(error => {
          console.error('Error getting all stocks:', error);
          this.notificationService.error('Erreur lors du chargement des stocks. Veuillez réessayer.');
          return throwError(error);
        })
      );
  }

  /**
   * Get stocks by article ID
   */
  getStocksByArticleId(articleId: string): Observable<StockResponse[]> {
    return this.http.get<StockResponse[]>(`${this.apiUrl}/article/${articleId}`)
      .pipe(
        catchError(error => {
          console.error('Error getting stocks by article ID:', error);
          this.notificationService.error('Erreur lors du chargement des stocks de l\'article.');
          return throwError(error);
        })
      );
  }

  /**
   * Get stocks by entrepot ID
   */
  getStocksByEntrepotId(entrepotId: string): Observable<StockResponse[]> {
    return this.http.get<StockResponse[]>(`${this.apiUrl}/entrepot/${entrepotId}`)
      .pipe(
        catchError(error => {
          console.error('Error getting stocks by entrepot ID:', error);
          this.notificationService.error('Erreur lors du chargement des stocks de l\'entrepôt.');
          return throwError(error);
        })
      );
  }

  /**
   * Get stock by article ID and entrepot ID
   */
  getStockByArticleAndEntrepot(articleId: string, entrepotId: string): Observable<StockResponse> {
    return this.http.get<StockResponse>(`${this.apiUrl}/article/${articleId}/entrepot/${entrepotId}`)
      .pipe(
        catchError(error => {
          console.error('Error getting stock by article and entrepot:', error);
          this.notificationService.error('Impossible de trouver le stock pour cet article et entrepôt.');
          return throwError(error);
        })
      );
  }

  /**
   * Convert StockResponse to Stock interface for compatibility
   */
  mapStockResponseToStock(response: StockResponse): Stock {
    // Use lot-based pricing if available, otherwise fallback to article pricing
    const prixVente = response.prixUnitaireMoyenVente || response.articlePrixVente || 0;
    const prixAchat = response.prixUnitaireMoyenAchat || response.articlePrixAchat || 0;
    
    return {
      id: response.id,
      articleId: response.articleId,
      article_id: response.articleId,
      entrepotId: response.entrepotId,
      entrepot_id: response.entrepotId,
      fournisseurId: response.fournisseurId,
      fournisseur_id: response.fournisseurId,
      fournisseurNom: response.fournisseurNom,
      // Create fournisseur object if data exists
      fournisseur: response.fournisseurId ? {
        id: response.fournisseurId,
        nom: response.fournisseurNom || 'Fournisseur inconnu',
        infoContact: undefined
      } : undefined,
      quantite: response.quantite,
      dateDexpiration: response.dateDexpiration,
      date_dexpiration: response.dateDexpiration,
      createdBy: response.createdBy,
      created_by: response.createdBy,
      updatedBy: response.updatedBy,
      updated_by: response.updatedBy,
      createdAt: response.createdAt,
      created_at: response.createdAt,
      updatedAt: response.updatedAt,
      updated_at: response.updatedAt,
      // Lot-based pricing values
      prixUnitaireMoyenAchat: response.prixUnitaireMoyenAchat,
      prixUnitaireMoyenVente: response.prixUnitaireMoyenVente,
      valeurTotaleStock: response.valeurTotaleStock,
      nombreLots: response.nombreLots,
      // Create complete article object from response data
      article: response.articleNom ? {
        id: response.articleId,
        nom: response.articleNom,
        description: response.articleDescription || '',
        categorie: this.mapCategorieFromName(response.categorieNom) || CategorieStock.AUTRE,
        categorieId: response.categorieId,
        categorieNom: response.categorieNom || 'Autre',
        prixVente: prixVente,
        prixUnitaire: prixVente,
        prixAchat: prixAchat,
        stockMinimum: response.articleStockMinimum || 0,
        stockMaximum: response.articleStockMaximum || 0,
        quantiteMinimale: response.articleStockMinimum || 0,
        quantiteMaximale: response.articleStockMaximum || 0,
        codeBarres: response.articleCodebare || '',
        codebare: response.articleCodebare || '',
        marqueId: response.marqueId,
        marque: response.marqueNom || '',
        fournisseurPrincipal: '',
        estActif: true
      } : undefined,
      // Create complete entrepot object from response data  
      entrepot: response.entrepotNom ? {
        id: response.entrepotId,
        nom: response.entrepotNom,
        description: '',
        adresse: response.entrepotAdresse || '',
        ville: '',
        estActif: true,
        createdAt: new Date(),
        updatedAt: new Date()
      } : undefined
    };
  }

  /**
   * Map category name to CategorieStock enum
   */
  private mapCategorieFromName(categorieNom?: string): CategorieStock | undefined {
    if (!categorieNom) return undefined;
    
    const categoryMapping: { [key: string]: CategorieStock } = {
      'Matériel Informatique': CategorieStock.MATERIEL_INFORMATIQUE,
      'Fournitures de Bureau': CategorieStock.FOURNITURES_BUREAU,
      'Équipement de Sécurité': CategorieStock.EQUIPEMENT_SECURITE,
      'Pièces Détachées': CategorieStock.PIECES_DETACHEES,
      'Consommables': CategorieStock.CONSOMMABLES,
      'Autre': CategorieStock.AUTRE
    };
    
    return categoryMapping[categorieNom] || CategorieStock.AUTRE;
  }

  /**
   * Convert Stock to AddStockCommand
   */
  mapStockToAddCommand(stock: Stock): AddStockCommand {
    return {
      articleId: stock.articleId || stock.article_id || '',
      entrepotId: stock.entrepotId || stock.entrepot_id || '',
      quantite: stock.quantite,
      dateDexpiration: stock.dateDexpiration || stock.date_dexpiration,
      createdBy: stock.createdBy || stock.created_by
    };
  }

  /**
   * Convert Stock to UpdateStockCommand
   */
  mapStockToUpdateCommand(stock: Stock): UpdateStockCommand {
    return {
      id: stock.id,
      quantite: stock.quantite,
      dateDexpiration: stock.dateDexpiration || stock.date_dexpiration,
      updatedBy: stock.updatedBy || stock.updated_by
    };
  }
}