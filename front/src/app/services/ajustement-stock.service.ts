import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import {
  AjustementStock,
  AddAjustementStockCommand,
  UpdateAjustementStockCommand,
  AjustementStockQuery,
  CommandResponse,
  PageResponse
} from '../models/stock.model';
import { environment } from '../../environment/environement';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class AjustementStockService {
  private apiUrl = `${environment.apiUrl}/stock/ajustement`;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {}

  // Create adjustment
  createAjustement(command: AddAjustementStockCommand): Observable<CommandResponse> {
    return this.http.post<CommandResponse>(this.apiUrl, command)
      .pipe(
        catchError(error => {
          console.error('Erreur lors de la création de l\'ajustement:', error);
          this.notificationService.error('Erreur lors de la création de l\'ajustement. Veuillez réessayer.');
          return throwError(error);
        })
      );
  }

  // Update adjustment
  updateAjustement(id: string, command: UpdateAjustementStockCommand): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/${id}`, command)
      .pipe(
        catchError(error => {
          console.error('Erreur lors de la modification de l\'ajustement:', error);
          this.notificationService.error('Erreur lors de la modification de l\'ajustement. Veuillez réessayer.');
          return throwError(error);
        })
      );
  }

  // Delete adjustment
  deleteAjustement(id: string): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(error => {
          console.error('Erreur lors de la suppression de l\'ajustement:', error);
          this.notificationService.error('Erreur lors de la suppression de l\'ajustement. Veuillez réessayer.');
          return throwError(error);
        })
      );
  }

  // Get all adjustments
  getAllAjustements(): Observable<PageResponse<AjustementStock>> {
    return this.http.get<AjustementStock[]>(this.apiUrl)
      .pipe(
        map((ajustements: AjustementStock[]) => {
          // Backend returns plain array, convert to PageResponse
          const pageResponse: PageResponse<AjustementStock> = {
            content: ajustements || [],
            totalElements: ajustements?.length || 0,
            totalPages: 1,
            size: ajustements?.length || 0,
            number: 0,
            first: true,
            last: true,
            numberOfElements: ajustements?.length || 0,
            empty: !ajustements || ajustements.length === 0
          };
          return pageResponse;
        }),
        catchError(error => {
          console.error('Erreur lors du chargement des ajustements:', error);
          this.notificationService.error('Erreur lors du chargement des ajustements. Veuillez réessayer.');
          return throwError(error);
        })
      );
  }

  // Get adjustments with filters
  getAjustements(query: AjustementStockQuery): Observable<PageResponse<AjustementStock>> {
    let params = new HttpParams();
    
    if (query.categorieId) {
      params = params.set('categorieId', query.categorieId);
    }
    if (query.marqueId) {
      params = params.set('marqueId', query.marqueId);
    }
    if (query.fournisseurId) {
      params = params.set('fournisseurId', query.fournisseurId);
    }
    if (query.entrepotId) {
      params = params.set('entrepotId', query.entrepotId);
    }
    if (query.articleId) {
      params = params.set('articleId', query.articleId);
    }
    if (query.utilisateurId) {
      params = params.set('utilisateurId', query.utilisateurId);
    }
    if (query.startDate) {
      params = params.set('startDate', query.startDate.toISOString().split('T')[0]);
    }
    if (query.endDate) {
      params = params.set('endDate', query.endDate.toISOString().split('T')[0]);
    }
    
    return this.http.get<AjustementStock[]>(this.apiUrl, { params })
      .pipe(
        map((ajustements: AjustementStock[]) => {
          // Backend returns plain array, convert to PageResponse
          const pageResponse: PageResponse<AjustementStock> = {
            content: ajustements || [],
            totalElements: ajustements?.length || 0,
            totalPages: 1,
            size: ajustements?.length || 0,
            number: 0,
            first: true,
            last: true,
            numberOfElements: ajustements?.length || 0,
            empty: !ajustements || ajustements.length === 0
          };
          return pageResponse;
        }),
        catchError(error => {
          console.error('Erreur lors du chargement des ajustements:', error);
          this.notificationService.error('Erreur lors du chargement des ajustements. Veuillez réessayer.');
          return throwError(error);
        })
      );
  }

  // Get adjustment by ID
  getAjustementById(id: string): Observable<AjustementStock> {
    return this.http.get<AjustementStock>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(error => {
          console.error('Erreur lors du chargement de l\'ajustement:', error);
          this.notificationService.error('Impossible de charger les détails de l\'ajustement.');
          return throwError(error);
        })
      );
  }

  // Get adjustments by article
  getAjustementsByArticle(articleId: string): Observable<AjustementStock[]> {
    return this.http.get<AjustementStock[]>(`${this.apiUrl}/article/${articleId}`);
  }

  // Get adjustments by user
  getAjustementsByUtilisateur(utilisateurId: string): Observable<AjustementStock[]> {
    return this.http.get<AjustementStock[]>(`${this.apiUrl}/utilisateur/${utilisateurId}`);
  }

  // Get adjustments by date range
  getAjustementsByDateRange(startDate: Date, endDate: Date): Observable<AjustementStock[]> {
    let params = new HttpParams()
      .set('startDate', startDate.toISOString().split('T')[0])
      .set('endDate', endDate.toISOString().split('T')[0]);
    
    return this.http.get<AjustementStock[]>(`${this.apiUrl}/periode`, { params });
  }
}
