import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, map } from 'rxjs/operators';
import { 
  CategorieArticle, 
  AddCategoryCommand, 
  UpdateCategoryCommand, 
  CategoryQuery 
} from '../models/stock.model';
import { environment } from '../../environment/environement';
import { NotificationService } from './notification.service';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export interface CategoryResponse {
  id: string;
}

export interface CategoryPageResponse {
  content: CategorieArticle[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private readonly API_URL = `${environment.apiUrl}/stock/category`;
  
  private categoriesSubject = new BehaviorSubject<CategorieArticle[]>([]);
  public categories$ = this.categoriesSubject.asObservable();

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {
    this.loadCategories();
  }

  // Get all categories
  getCategories(query?: CategoryQuery): Observable<CategorieArticle[]> {
    let params = new HttpParams();
    
    if (query) {
      if (query.nom) params = params.set('nom', query.nom);
      if (query.description) params = params.set('description', query.description);
      if (query.estActif !== undefined) params = params.set('estActif', query.estActif.toString());
      if (query.page !== undefined) params = params.set('page', query.page.toString());
      if (query.size !== undefined) params = params.set('size', query.size.toString());
      if (query.sortBy) params = params.set('sortBy', query.sortBy);
      if (query.sortDirection) params = params.set('sortDirection', query.sortDirection);
    }

    return this.http.get<any[]>(this.API_URL, { params })
      .pipe(
        tap(categories => this.categoriesSubject.next(categories)),
        catchError(error => {
          this.notificationService.error('Erreur lors du chargement des catégories');
          return throwError(() => error);
        })
      );
  }

  // Get categories with pagination
  getCategoriesPaginated(query?: CategoryQuery): Observable<CategoryPageResponse> {
    let params = new HttpParams();
    
    if (query) {
      if (query.nom) params = params.set('nom', query.nom);
      if (query.description) params = params.set('description', query.description);
      if (query.estActif !== undefined) params = params.set('estActif', query.estActif.toString());
      if (query.page !== undefined) params = params.set('page', query.page.toString());
      if (query.size !== undefined) params = params.set('size', query.size.toString());
      if (query.sortBy) params = params.set('sortBy', query.sortBy);
      if (query.sortDirection) params = params.set('sortDirection', query.sortDirection);
    }

    return this.http.get<any>(`${this.API_URL}/paginated`, { params })
      .pipe(
        catchError(error => {
          this.notificationService.error('Erreur lors du chargement des catégories');
          return throwError(() => error);
        })
      );
  }

  // Get category by ID
  getCategoryById(id: string): Observable<CategorieArticle[]> {
    return this.http.get<CategorieArticle[]>(`${this.API_URL}/${id}`)
      .pipe(
        catchError(error => {
          this.notificationService.error('Erreur lors du chargement de la catégorie');
          return throwError(() => error);
        })
      );
  }

  // Add new category
  addCategory(command: AddCategoryCommand): Observable<CategoryResponse[]> {
    return this.http.post<CategoryResponse[]>(this.API_URL, command)
      .pipe(
        tap(() => {
          this.loadCategories(); // Refresh the list after adding
          this.notificationService.success('Catégorie ajoutée avec succès');
        }),
        catchError(error => {
          const message = error.error?.message || error.error || 'Erreur lors de l\'ajout de la catégorie';
          this.notificationService.error(message);
          return throwError(() => error);
        })
      );
  }

  // Update category
  updateCategory(id: string, command: UpdateCategoryCommand): Observable<any> {
    return this.http.put(`${this.API_URL}/${id}`, command)
      .pipe(
        tap(() => {
          this.loadCategories(); // Refresh the list after updating
          this.notificationService.success('Catégorie modifiée avec succès');
        }),
        catchError(error => {
          const message = error.error?.message || error.error || 'Erreur lors de la modification de la catégorie';
          this.notificationService.error(message);
          return throwError(() => error);
        })
      );
  }

  // Delete category
  deleteCategory(id: string): Observable<any> {
    return this.http.delete(`${this.API_URL}/${id}`)
      .pipe(
        tap(() => {
          this.loadCategories(); // Refresh the list after deleting
          this.notificationService.success('Catégorie supprimée avec succès');
        }),
        catchError(error => {
          const message = error.error?.message || error.error || 'Erreur lors de la suppression de la catégorie';
          this.notificationService.error(message);
          return throwError(() => error);
        })
      );
  }

  // Load categories (private method)
  private loadCategories(): void {
    this.getCategories().subscribe();
  }

  // Get current categories value
  getCurrentCategories(): CategorieArticle[] {
    return this.categoriesSubject.value;
  }

  // Refresh categories
  refreshCategories(): void {
    this.loadCategories();
  }
}