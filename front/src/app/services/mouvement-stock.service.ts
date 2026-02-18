import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import {
  EntreeStockCommand,
  SortieStockCommand,
  TransfertStockCommand,
  RetourStockCommand,
  MouvementStockQuery,
  MouvementStockResponse,
  CommandResponse,
  PageResponse
} from '../models/stock.model';
import { environment } from '../../environment/environement';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class MouvementStockService {
  private apiUrl = `${environment.apiUrl}/stock/mouvement`;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {}

  // Create stock entry (Entrée)
  createEntree(command: EntreeStockCommand): Observable<CommandResponse> {
    console.log('Creating entrée with command:', command);
    return this.http.post<CommandResponse>(`${this.apiUrl}/entree`, command)
      .pipe(
        catchError(error => {
          console.error('Erreur lors de la création de l\'entrée:', error);
          const errorMsg = this.extractErrorMessage(error);
          this.notificationService.error(errorMsg);
          return throwError(() => error);
        })
      );
  }

  // Create stock exit (Sortie)
  createSortie(command: SortieStockCommand): Observable<CommandResponse> {
    console.log('Creating sortie with command:', command);
    return this.http.post<CommandResponse>(`${this.apiUrl}/sortie`, command)
      .pipe(
        catchError(error => {
          console.error('Erreur complète de la sortie:', error);
          console.error('error.error:', error.error);
          const errorMsg = this.extractErrorMessage(error);
          console.log('Message extrait:', errorMsg);
          this.notificationService.error(errorMsg);
          return throwError(() => error);
        })
      );
  }

  // Create stock transfer (Transfert)
  createTransfert(command: TransfertStockCommand): Observable<CommandResponse> {
    console.log('Creating transfert with command:', command);
    return this.http.post<CommandResponse>(`${this.apiUrl}/transfert`, command)
      .pipe(
        catchError(error => {
          console.error('Erreur lors de la création du transfert:', error);
          const errorMsg = this.extractErrorMessage(error);
          this.notificationService.error(errorMsg);
          return throwError(() => error);
        })
      );
  }

  // Create stock return (Retour)
  createRetour(command: RetourStockCommand): Observable<CommandResponse> {
    console.log('Creating retour with command:', command);
    return this.http.post<CommandResponse>(`${this.apiUrl}/retour`, command)
      .pipe(
        catchError(error => {
          console.error('Erreur lors de la création du retour:', error);
          const errorMsg = this.extractErrorMessage(error);
          this.notificationService.error(errorMsg);
          return throwError(() => error);
        })
      );
  }

  // Get all mouvements with optional filters
  getMouvements(query?: MouvementStockQuery): Observable<PageResponse<MouvementStockResponse>> {
    let params = new HttpParams();
    
    if (query) {
      if (query.id) params = params.set('id', query.id);
      if (query.articleId) params = params.set('articleId', query.articleId);
      if (query.entrepotId) params = params.set('entrepotId', query.entrepotId);
      if (query.utilisateurId) params = params.set('utilisateurId', query.utilisateurId);
      if (query.categorieId) params = params.set('categorieId', query.categorieId);
      if (query.marqueId) params = params.set('marqueId', query.marqueId);
      if (query.typeMouvement) params = params.set('typeMouvement', query.typeMouvement);
      if (query.statut) params = params.set('statut', query.statut);
      if (query.reference) params = params.set('reference', query.reference);
      if (query.startDate) params = params.set('startDate', query.startDate.toISOString());
      if (query.endDate) params = params.set('endDate', query.endDate.toISOString());
      if (query.page !== undefined) params = params.set('page', query.page.toString());
      if (query.size) params = params.set('size', query.size.toString());
      if (query.sortBy) params = params.set('sortBy', query.sortBy);
      if (query.sortDirection) params = params.set('sortDirection', query.sortDirection);
    }

    return this.http.get<MouvementStockResponse[]>(this.apiUrl, { params })
      .pipe(
        map((mouvements: MouvementStockResponse[]) => {
          // Backend returns plain array, convert to PageResponse
          const page = query?.page || 0;
          const size = query?.size || 15;
          
          console.log('Raw mouvements from backend:', mouvements);
          console.log('Mouvements length:', mouvements?.length);
          
          // Create PageResponse structure
          const pageResponse: PageResponse<MouvementStockResponse> = {
            content: mouvements || [],
            totalElements: mouvements?.length || 0,
            totalPages: mouvements?.length ? Math.ceil(mouvements.length / size) : 0,
            size: size,
            number: page,
            first: page === 0,
            last: true,
            numberOfElements: mouvements?.length || 0,
            empty: !mouvements || mouvements.length === 0
          };
          
          console.log('Mapped PageResponse:', pageResponse);
          return pageResponse;
        }),
        catchError(error => {
          console.error('Error getting mouvements:', error);
          this.notificationService.error('Erreur lors du chargement des mouvements. Veuillez réessayer.');
          return throwError(error);
        })
      );
  }

  // Get mouvement by ID
  getMouvementById(id: string): Observable<MouvementStockResponse> {
    return this.http.get<MouvementStockResponse>(`${this.apiUrl}/${id}`);
  }

  // Validate mouvement
  validateMouvement(id: string): Observable<CommandResponse> {
    return this.http.put<CommandResponse>(`${this.apiUrl}/${id}/validate`, {});
  }

  // Cancel mouvement
  cancelMouvement(id: string): Observable<CommandResponse> {
    return this.http.put<CommandResponse>(`${this.apiUrl}/${id}/cancel`, {});
  }

  // Get mouvements by article ID
  getMouvementsByArticleId(articleId: string): Observable<MouvementStockResponse[]> {
    return this.getMouvements({ articleId }).pipe(
      map(response => response.content)
    );
  }

  // Get mouvements by entrepot ID
  getMouvementsByEntrepotId(entrepotId: string): Observable<MouvementStockResponse[]> {
    return this.getMouvements({ entrepotId }).pipe(
      map(response => response.content)
    );
  }

  // Get mouvements by date range
  getMouvementsByDateRange(startDate: Date, endDate: Date): Observable<MouvementStockResponse[]> {
    return this.getMouvements({ startDate, endDate }).pipe(
      map(response => response.content)
    );
  }

  // Export mouvements to CSV
  exportToCsv(mouvements: MouvementStockResponse[]): void {
    const headers = [
      'ID',
      'Type',
      'Article',
      'Entrepôt Source',
      'Entrepôt Destination',
      'Quantité',
      'Date',
      'Utilisateur',
      'Statut',
      'Référence'
    ];

    const csvData = mouvements.map(m => [
      m.id,
      m.typeMouvement,
      m.articleNom || '',
      m.sourceEntrepotNom || '',
      m.destinationEntrepotNom || '',
      m.quantite.toString(),
      new Date(m.dateMouvement).toLocaleString(),
      m.utilisateurNom || '',
      m.statut,
      m.reference || ''
    ]);

    const csv = [headers, ...csvData]
      .map(row => row.map(cell => `"${cell}"`).join(','))
      .join('\n');

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    
    link.setAttribute('href', url);
    link.setAttribute('download', `mouvements_stock_${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  /**
   * Extract meaningful error message from HTTP error response
   */
  private extractErrorMessage(error: any): string {
    console.log('Extracting error message from:', error);
    
    // Check if error has a structured response
    if (error.error) {
      // Backend sometimes returns an array with error object
      if (Array.isArray(error.error) && error.error.length > 0) {
        const firstError = error.error[0];
        if (firstError.message) {
          let message = firstError.message;
          // Clean up "Validation errors:" prefix
          if (message.startsWith('Validation errors:')) {
            message = message.replace('Validation errors:', '').trim();
          }
          if (message.startsWith('Erreur de validation:')) {
            message = message.replace('Erreur de validation:', '').trim();
          }
          return message;
        }
      }
      
      // Backend returns { success: false, message: "..." }
      if (error.error.message) {
        let message = error.error.message;
        // Clean up "Validation errors:" prefix
        if (message.startsWith('Validation errors:')) {
          message = message.replace('Validation errors:', '').trim();
        }
        // Clean up "Erreur de validation:" prefix if present
        if (message.startsWith('Erreur de validation:')) {
          message = message.replace('Erreur de validation:', '').trim();
        }
        return message;
      }
      // Sometimes error is just a string
      if (typeof error.error === 'string') {
        let message = error.error;
        if (message.startsWith('Validation errors:')) {
          message = message.replace('Validation errors:', '').trim();
        }
        return message;
      }
      // Check for other common error formats
      if (error.error.error) {
        return error.error.error;
      }
    }
    
    // Fallback to error message
    if (error.message) {
      return error.message;
    }
    
    // Default message
    return 'Une erreur est survenue lors de l\'opération';
  }
}
