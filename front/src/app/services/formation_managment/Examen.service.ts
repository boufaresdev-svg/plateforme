import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Examen } from '../../models/formation/Examen.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class ExamenService {

  private http = inject(HttpClient);

  private baseUrl = `${environment.formationUrl}/examens`; 

  getAllExamens(): Observable<Examen[]> {
    return this.http.get<Examen[][]>(`${this.baseUrl}`).pipe(
      map(arrays => arrays.flat())
    );
  }

  getExamenById(idExamen: number): Observable<Examen> {
    return this.http.get<Examen>(`${this.baseUrl}/${idExamen}`);
  }

  getExamensByApprenant(idApprenant: number): Observable<Examen[]> {
    return this.http.get<Examen[]>(`${this.baseUrl}/apprenant/${idApprenant}`);
  }

  getExamensByPlan(idPlanFormation: number): Observable<Examen[]> {
    return this.http.get<Examen[][]>(`${this.baseUrl}/plan/${idPlanFormation}`).pipe(
      map(arrays => arrays.flat())
    );
  }

  createExamen(examen: Examen): Observable<Examen> {
    return this.http.post<Examen>(`${this.baseUrl}`, examen);
  }

updateExamen(payload: any): Observable<void> {
  return this.http.put<void>(`${this.baseUrl}/${payload.idExamen}`, payload);
}

  deleteExamen(idExamen: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${idExamen}`);
  }
}
