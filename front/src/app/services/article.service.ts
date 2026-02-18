import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, map, catchError } from 'rxjs/operators';
import { 
  Article, 
  AddArticleCommand, 
  UpdateArticleCommand, 
  ArticleQuery,
  PageResponse
} from '../models/stock.model';
import { environment } from '../../environment/environement';
import { NotificationService } from './notification.service';

export interface ArticleResponse {
  id: string;
}

export interface GetArticleQueryResponse {
  articles: Article[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
}

export interface ArticlePageResponse {
  content: Article[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class ArticleService {
  private readonly API_URL = `${environment.apiUrl}/stock/article`;
  
  private articlesSubject = new BehaviorSubject<Article[]>([]);
  public articles$ = this.articlesSubject.asObservable();

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {
    this.loadArticles();
  }

  getArticles(query?: ArticleQuery): Observable<PageResponse<Article>> {
    let params = new HttpParams();
    
    if (query) {
      if (query.searchTerm) params = params.set('searchTerm', query.searchTerm);
      if (query.categorie) params = params.set('categorie', query.categorie);
      if (query.marque) params = params.set('marque', query.marque);
      if (query.estActif !== undefined) params = params.set('estActif', query.estActif.toString());
      if (query.page !== undefined) params = params.set('page', query.page.toString());
      if (query.size !== undefined) params = params.set('size', query.size.toString());
    }

    return this.http.get<GetArticleQueryResponse>(this.API_URL, { params }).pipe(
      map(response => {
        // Map backend response (articles) to frontend PageResponse (content)
        const pageResponse: PageResponse<Article> = {
          content: response.articles || [],
          totalElements: response.totalElements || 0,
          totalPages: response.totalPages || 0,
          size: query?.size || 15,
          number: response.currentPage || 0,
          first: (response.currentPage || 0) === 0,
          last: (response.currentPage || 0) >= (response.totalPages || 1) - 1,
          numberOfElements: response.articles?.length || 0,
          empty: !response.articles || response.articles.length === 0
        };
        return pageResponse;
      }),
      tap(response => {
        if (response && response.content) {
          this.articlesSubject.next(response.content);
        }
      })
    );
  }

  getArticlesPaginated(query?: ArticleQuery): Observable<ArticlePageResponse> {
    let params = new HttpParams();
    
    if (query) {
      if (query.nom) params = params.set('nom', query.nom);
      if (query.sku) params = params.set('sku', query.sku);
      if (query.categorie) params = params.set('categorie', query.categorie);
      if (query.marque) params = params.set('marque', query.marque);
      if (query.estActif !== undefined) params = params.set('estActif', query.estActif.toString());
      if (query.page !== undefined) params = params.set('page', query.page.toString());
      if (query.size !== undefined) params = params.set('size', query.size.toString());
      if (query.sortBy) params = params.set('sortBy', query.sortBy);
      if (query.sortDirection) params = params.set('sortDirection', query.sortDirection);
    }

    return this.http.get<ArticlePageResponse>(`${this.API_URL}/paginated`, { params });
  }

  getArticleById(id: string): Observable<Article> {
    return this.http.get<Article>(`${this.API_URL}/${id}`);
  }

  addArticle(command: AddArticleCommand): Observable<ArticleResponse> {
    return this.http.post<ArticleResponse>(this.API_URL, command).pipe(
      tap(() => {
        this.loadArticles();
        this.notificationService.success('Article ajouté avec succès');
      }),
      catchError(error => {
        console.error('Error adding article:', error);
        this.notificationService.error('Erreur lors de l\'ajout de l\'article. Vérifiez les données.');
        return throwError(() => error);
      })
    );
  }

  updateArticle(id: string, command: UpdateArticleCommand): Observable<ArticleResponse> {
    return this.http.put<ArticleResponse>(`${this.API_URL}/${id}`, command).pipe(
      tap(() => {
        this.loadArticles();
        this.notificationService.success('Article modifié avec succès');
      }),
      catchError(error => {
        console.error('Error updating article:', error);
        this.notificationService.error('Erreur lors de la modification de l\'article.');
        return throwError(() => error);
      })
    );
  }

  deleteArticle(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`).pipe(
      tap(() => {
        this.loadArticles();
        this.notificationService.success('Article supprimé avec succès');
      }),
      catchError(error => {
        console.error('Error deleting article:', error);
        this.notificationService.error('Erreur lors de la suppression de l\'article.');
        return throwError(() => error);
      })
    );
  }

  private loadArticles(): void {
    this.getArticles().subscribe();
  }
}
