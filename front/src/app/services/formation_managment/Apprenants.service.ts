import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Apprenant, ApprenantClassesResponse, ApprenantEnrollmentResponse, ApprenantSearchResponse, ApprenantStats, CsvImportResponse } from '../../models/formation/Apprenant.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class ApprenantService {

  private readonly apiUrl = `${environment.formationUrl}/apprenants`;

  constructor(private http: HttpClient) {}

  // ==================== CRUD Operations ====================

  getAllApprenants(): Observable<Apprenant[]> {
    return this.http.get<Apprenant[][]>(`${this.apiUrl}`).pipe(
      map(response => response[0] || [])
    );  
  }

  getApprenantById(id: number): Observable<Apprenant> {
    return this.http.get<Apprenant>(`${this.apiUrl}/${id}`);
  }

  addApprenant(apprenant: Apprenant): Observable<Apprenant> {
    return this.http.post<Apprenant>(`${this.apiUrl}`, apprenant);
  }

  updateApprenant(id: number, apprenant: Apprenant): Observable<Apprenant> {
    return this.http.put<Apprenant>(`${this.apiUrl}/${id}`, apprenant);
  }

  deleteApprenant(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // ==================== Search & Filter ====================

  searchApprenants(
    search: string = '',
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    sortDirection: string = 'asc'
  ): Observable<ApprenantSearchResponse> {
    const params = new HttpParams()
      .set('search', search)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);
    
    return this.http.get<ApprenantSearchResponse>(`${this.apiUrl}/search`, { params });
  }

  getApprenantsByPlanFormation(planId: number): Observable<Apprenant[]> {
    return this.http.get<Apprenant[][]>(`${this.apiUrl}/plan/${planId}`).pipe(
      map(response => response[0] || [])
    );
  }

  getApprenantsByFormation(formationId: number): Observable<Apprenant[]> {
    return this.http.get<Apprenant[]>(`${this.apiUrl}/formation/${formationId}`);
  }

  getApprenantsByFormationPaged(
    formationId: number,
    page: number = 0,
    size: number = 10
  ): Observable<ApprenantSearchResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<ApprenantSearchResponse>(`${this.apiUrl}/formation/${formationId}/paged`, { params });
  }

  // ==================== User Management ====================

  blockApprenant(id: number): Observable<Apprenant> {
    return this.http.patch<Apprenant>(`${this.apiUrl}/${id}/block`, {});
  }

  unblockApprenant(id: number): Observable<Apprenant> {
    return this.http.patch<Apprenant>(`${this.apiUrl}/${id}/unblock`, {});
  }

  toggleStaff(id: number): Observable<Apprenant> {
    return this.http.patch<Apprenant>(`${this.apiUrl}/${id}/toggle-staff`, {});
  }

  updatePassword(id: number, password: string): Observable<{ message: string }> {
    return this.http.patch<{ message: string }>(`${this.apiUrl}/${id}/password`, { password });
  }

  generatePassword(id: number): Observable<{ message: string; password: string }> {
    return this.http.post<{ message: string; password: string }>(`${this.apiUrl}/${id}/generate-password`, {});
  }

  // ==================== CSV Import ====================

  importFromCsv(
    file: File,
    planFormationId?: number,
    generatePasswords: boolean = true
  ): Observable<CsvImportResponse> {
    const formData = new FormData();
    formData.append('file', file);
    
    let params = new HttpParams().set('generatePasswords', generatePasswords.toString());
    if (planFormationId) {
      params = params.set('planFormationId', planFormationId.toString());
    }
    
    return this.http.post<CsvImportResponse>(`${this.apiUrl}/import-csv`, formData, { params });
  }

  // ==================== Statistics ====================

  getStats(): Observable<ApprenantStats> {
    return this.http.get<ApprenantStats>(`${this.apiUrl}/stats`);
  }

  // ==================== Verification & Enrollment ====================

  verifyApprenant(params: { email?: string; matricule?: string }): Observable<{ exists: boolean; apprenant?: Apprenant }> {
    let httpParams = new HttpParams();
    if (params.email) httpParams = httpParams.set('email', params.email);
    if (params.matricule) httpParams = httpParams.set('matricule', params.matricule);
    return this.http.get<{ exists: boolean; apprenant?: Apprenant }>(`${this.apiUrl}/verify`, { params: httpParams });
  }

  getApprenantClasses(apprenantId: number): Observable<ApprenantClassesResponse> {
    return this.http.get<ApprenantClassesResponse[]>(`${this.apiUrl}/${apprenantId}/classes`).pipe(
      map(response => response[0])
    );
  }

  checkEnrollment(apprenantId: number, classeId: number): Observable<ApprenantEnrollmentResponse> {
    return this.http.get<ApprenantEnrollmentResponse[]>(`${this.apiUrl}/${apprenantId}/enrolled/${classeId}`).pipe(
      map(response => response[0])
    );
  }
}
