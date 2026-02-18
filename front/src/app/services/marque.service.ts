import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { 
  Marque, 
  AddMarqueCommand, 
  UpdateMarqueCommand, 
  MarqueQuery 
} from '../models/stock.model';
import { environment } from '../../environment/environement';
import { NotificationService } from './notification.service';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export interface MarqueResponse {
  id: string;
}

export interface MarquePageResponse {
  content: Marque[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class MarqueService {
  private readonly API_URL = `${environment.apiUrl}/stock/marque`;
  
  private marquesSubject = new BehaviorSubject<Marque[]>([]);
  public marques$ = this.marquesSubject.asObservable();

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {
    this.loadMarques();
  }

  // Get all marques
  getMarques(query?: MarqueQuery): Observable<Marque[]> {
    let params = new HttpParams();
    
    if (query) {
      if (query.nom) params = params.set('nom', query.nom);
      if (query.codeMarque) params = params.set('codeMarque', query.codeMarque);
      if (query.pays) params = params.set('pays', query.pays);
      if (query.estActif !== undefined) params = params.set('estActif', query.estActif.toString());
      if (query.page !== undefined) params = params.set('page', query.page.toString());
      if (query.size !== undefined) params = params.set('size', query.size.toString());
      if (query.sortBy) params = params.set('sortBy', query.sortBy);
      if (query.sortDirection) params = params.set('sortDirection', query.sortDirection);
    }

    return this.http.get<Marque[]>(this.API_URL, { params }).pipe(
      tap(marques => this.marquesSubject.next(marques))
    );
  }

  // Get marques with pagination
  getMarquesPaginated(query?: MarqueQuery): Observable<MarquePageResponse> {
    let params = new HttpParams();
    
    if (query) {
      if (query.nom) params = params.set('nom', query.nom);
      if (query.codeMarque) params = params.set('codeMarque', query.codeMarque);
      if (query.pays) params = params.set('pays', query.pays);
      if (query.estActif !== undefined) params = params.set('estActif', query.estActif.toString());
      if (query.page !== undefined) params = params.set('page', query.page.toString());
      if (query.size !== undefined) params = params.set('size', query.size.toString());
      if (query.sortBy) params = params.set('sortBy', query.sortBy);
      if (query.sortDirection) params = params.set('sortDirection', query.sortDirection);
    }

    return this.http.get<MarquePageResponse>(`${this.API_URL}/paginated`, { params });
  }

  // Get marque by ID
  getMarqueById(id: string): Observable<Marque> {
    return this.http.get<Marque>(`${this.API_URL}/${id}`);
  }

  // Add new marque
  addMarque(command: AddMarqueCommand): Observable<MarqueResponse> {
    return this.http.post<MarqueResponse>(this.API_URL, command).pipe(
      tap(() => {
        this.loadMarques();
        this.notificationService.success('Marque ajoutée avec succès');
      }),
      catchError(error => {
        this.notificationService.error('Erreur lors de l\'ajout de la marque.');
        return throwError(() => error);
      })
    );
  }

  // Update marque
  updateMarque(id: string, command: UpdateMarqueCommand): Observable<MarqueResponse> {
    return this.http.put<MarqueResponse>(`${this.API_URL}/${id}`, command).pipe(
      tap(() => {
        this.loadMarques();
        this.notificationService.success('Marque modifiée avec succès');
      }),
      catchError(error => {
        this.notificationService.error('Erreur lors de la modification de la marque.');
        return throwError(() => error);
      })
    );
  }

  // Delete marque
  deleteMarque(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`).pipe(
      tap(() => {
        this.loadMarques();
        this.notificationService.success('Marque supprimée avec succès');
      }),
      catchError(error => {
        this.notificationService.error('Erreur lors de la suppression de la marque.');
        return throwError(() => error);
      })
    );
  }

  // Load marques into subject
  private loadMarques(): void {
    this.getMarques().subscribe();
  }
}
