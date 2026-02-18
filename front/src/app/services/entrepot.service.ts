import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { 
  Entrepot, 
  AddEntrepotCommand, 
  UpdateEntrepotCommand, 
  EntrepotQuery 
} from '../models/stock.model';
import { environment } from '../../environment/environement';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class EntrepotService {
  private readonly API_URL = `${environment.apiUrl}/stock/entrepot`;
  private entrepotsSubject = new BehaviorSubject<Entrepot[]>([]);
  public entrepots$ = this.entrepotsSubject.asObservable();

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {}

  getEntrepots(query?: EntrepotQuery): Observable<Entrepot[]> {
    let params = new HttpParams();
    
    if (query) {
      if (query.id) params = params.set('id', query.id);
      if (query.searchTerm) params = params.set('searchTerm', query.searchTerm);
      if (query.ville) params = params.set('ville', query.ville);
      if (query.statut) params = params.set('statut', query.statut);
      if (query.estActif !== undefined) params = params.set('estActif', query.estActif.toString());
      if (query.page !== undefined) params = params.set('page', query.page.toString());
      if (query.size !== undefined) params = params.set('size', query.size.toString());
      if (query.sortBy) params = params.set('sortBy', query.sortBy);
      if (query.sortDirection) params = params.set('sortDirection', query.sortDirection);
    }

    return this.http.get<Entrepot[]>(this.API_URL, { params }).pipe(
      tap(entrepots => this.entrepotsSubject.next(entrepots)),
      catchError(error => {
        console.error('Erreur lors du chargement des entrepôts:', error);
        this.notificationService.error('Erreur lors du chargement des entrepôts');
        return throwError(() => error);
      })
    );
  }

  getEntrepotsPaginated(query?: EntrepotQuery): Observable<{content: Entrepot[], totalElements: number, totalPages: number}> {
    let params = new HttpParams();
    
    if (query) {
      if (query.searchTerm) params = params.set('searchTerm', query.searchTerm);
      if (query.ville) params = params.set('ville', query.ville);
      if (query.statut) params = params.set('statut', query.statut);
      if (query.estActif !== undefined) params = params.set('estActif', query.estActif.toString());
      if (query.page !== undefined) params = params.set('page', query.page.toString());
      if (query.size !== undefined) params = params.set('size', query.size.toString());
      if (query.sortBy) params = params.set('sortBy', query.sortBy);
      if (query.sortDirection) params = params.set('sortDirection', query.sortDirection);
    }

    return this.http.get<any>(`${this.API_URL}/paginated`, { params }).pipe(
      catchError(error => {
        console.error('Erreur lors du chargement des entrepôts:', error);
        this.notificationService.error('Erreur lors du chargement des entrepôts');
        return throwError(() => error);
      })
    );
  }

  getEntrepotById(id: string): Observable<Entrepot> {
    return this.http.get<Entrepot>(`${this.API_URL}/${id}`)
      .pipe(
        catchError(error => {
          console.error('Erreur lors du chargement de l\'entrepôt:', error);
          this.notificationService.error('Impossible de charger les détails de l\'entrepôt');
          return throwError(() => error);
        })
      );
  }

  addEntrepot(command: AddEntrepotCommand): Observable<Entrepot> {
    return this.http.post<Entrepot>(this.API_URL, command).pipe(
      tap(() => {
        this.notificationService.success('Entrepôt ajouté avec succès');
        this.refreshEntrepots();
      }),
      catchError(error => {
        console.error('Erreur lors de l\'ajout de l\'entrepôt:', error);
        this.notificationService.error('Erreur lors de l\'ajout de l\'entrepôt');
        return throwError(() => error);
      })
    );
  }

  updateEntrepot(id: string, command: UpdateEntrepotCommand): Observable<Entrepot> {
    command.id = id;
    return this.http.put<Entrepot>(`${this.API_URL}/${id}`, command).pipe(
      tap(() => {
        this.notificationService.success('Entrepôt modifié avec succès');
        this.refreshEntrepots();
      }),
      catchError(error => {
        console.error('Erreur lors de la modification de l\'entrepôt:', error);
        this.notificationService.error('Erreur lors de la modification de l\'entrepôt');
        return throwError(() => error);
      })
    );
  }

  deleteEntrepot(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`).pipe(
      tap(() => {
        this.notificationService.success('Entrepôt supprimé avec succès');
        this.refreshEntrepots();
      }),
      catchError(error => {
        console.error('Erreur lors de la suppression de l\'entrepôt:', error);
        this.notificationService.error('Erreur lors de la suppression de l\'entrepôt');
        return throwError(() => error);
      })
    );
  }

  private refreshEntrepots(): void {
    this.getEntrepots().subscribe();
  }

  // Helper methods for dropdowns
  getActiveEntrepots(): Observable<Entrepot[]> {
    return this.getEntrepots({ estActif: true });
  }

  getEntrepotsByVille(ville: string): Observable<Entrepot[]> {
    return this.getEntrepots({ ville });
  }

  searchEntrepots(searchTerm: string): Observable<Entrepot[]> {
    return this.getEntrepots({ searchTerm });
  }
}
