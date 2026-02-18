import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Classe, ClasseSearchResponse, ClasseStats } from '../../models/formation/Classe.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class ClasseService {

  private readonly apiUrl = `${environment.formationUrl}/classes`;

  constructor(private http: HttpClient) {}

  // ==================== CRUD Operations ====================

  getAllClasses(): Observable<Classe[]> {
    return this.http.get<Classe[]>(this.apiUrl);
  }

  getClasseById(id: number): Observable<Classe> {
    return this.http.get<Classe>(`${this.apiUrl}/${id}`);
  }

  createClasse(classe: Partial<Classe>): Observable<Classe> {
    return this.http.post<Classe>(this.apiUrl, classe);
  }

  updateClasse(id: number, classe: Partial<Classe>): Observable<Classe> {
    return this.http.put<Classe>(`${this.apiUrl}/${id}`, classe);
  }

  deleteClasse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // ==================== Search & Filter ====================

  searchClasses(
    search: string = '',
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    sortDirection: string = 'desc'
  ): Observable<ClasseSearchResponse> {
    const params = new HttpParams()
      .set('search', search)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);

    return this.http.get<ClasseSearchResponse>(`${this.apiUrl}/search`, { params });
  }

  getClassesByFormation(formationId: number): Observable<Classe[]> {
    return this.http.get<Classe[]>(`${this.apiUrl}/formation/${formationId}`);
  }

  getClassesByPlanFormation(planFormationId: number): Observable<Classe[]> {
    return this.http.get<Classe[]>(`${this.apiUrl}/plan-formation/${planFormationId}`);
  }

  getActiveClasses(): Observable<Classe[]> {
    return this.http.get<Classe[]>(`${this.apiUrl}/active`);
  }

  getClassesWithAvailableSpots(): Observable<Classe[]> {
    return this.http.get<Classe[]>(`${this.apiUrl}/available`);
  }

  // ==================== Class Status ====================

  toggleActive(id: number): Observable<Classe> {
    return this.http.patch<Classe>(`${this.apiUrl}/${id}/toggle-active`, {});
  }

  // ==================== Apprenant Management ====================

  addApprenantToClasse(classeId: number, apprenantId: number): Observable<Classe> {
    return this.http.post<Classe>(`${this.apiUrl}/${classeId}/apprenants/${apprenantId}`, {});
  }

  removeApprenantFromClasse(classeId: number, apprenantId: number): Observable<Classe> {
    return this.http.delete<Classe>(`${this.apiUrl}/${classeId}/apprenants/${apprenantId}`);
  }

  addMultipleApprenants(classeId: number, apprenantIds: number[]): Observable<Classe> {
    return this.http.post<Classe>(`${this.apiUrl}/${classeId}/apprenants`, { apprenantIds });
  }

  getClassesByApprenant(apprenantId: number): Observable<Classe[]> {
    return this.http.get<Classe[]>(`${this.apiUrl}/apprenant/${apprenantId}`);
  }

  // ==================== Statistics ====================

  getStats(): Observable<ClasseStats> {
    return this.http.get<ClasseStats>(`${this.apiUrl}/stats`);
  }
}
