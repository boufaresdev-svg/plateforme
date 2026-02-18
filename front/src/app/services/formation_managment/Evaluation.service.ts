import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Evaluation } from '../../models/formation/Evaluation.model';
import { environment } from '../../../environment/environement';


@Injectable({
  providedIn: 'root'
})
export class EvaluationService {

  private http = inject(HttpClient);

  private baseUrl =  `${environment.formationUrl}/evaluations`;

  getEvaluations(): Observable<Evaluation[]> {
    return this.http.get<Evaluation[]>(`${this.baseUrl}`);
  }

  getEvaluationById(id: number): Observable<Evaluation> {
    return this.http.get<Evaluation>(`${this.baseUrl}/${id}`);
  }

  createEvaluation(evalData: Evaluation): Observable<Evaluation> {
    return this.http.post<Evaluation>(`${this.baseUrl}`, evalData);
  }

  updateEvaluation(id: number, evalData: Evaluation): Observable<Evaluation> {
    return this.http.put<Evaluation>(`${this.baseUrl}/${id}`, evalData);
  }

  deleteEvaluation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getEvaluationsByApprenant(apprenantId: number): Observable<Evaluation[]> {
    return this.http.get<Evaluation[][]>(`${this.baseUrl}/apprenant/${apprenantId}`).pipe(
      map(Response => Response.flat())
    );
  }

  getEvaluationsByPlan(planId: number): Observable<Evaluation[]> {
    return this.http.get<Evaluation[][]>(`${this.baseUrl}/plan/${planId}`).pipe(
      map(Response => Response.flat())
    );
  }

  getEvaluationsByContenuJour(idContenuJour: number): Observable<Evaluation[]> {
    return this.http.get<Evaluation[][]>(`${this.baseUrl}/contenu/${idContenuJour}`).pipe(
      map(Response => Response.flat())
    );  
   

  }

}
