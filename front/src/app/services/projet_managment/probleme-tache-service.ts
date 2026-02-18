import { Injectable } from '@angular/core';
 import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environement';
import { Observable } from 'rxjs';
import { ProblemeTache } from '../../models/projet/ProblemeTache.model';

@Injectable({
  providedIn: 'root'
})
export class ProblemeTacheService {

  private apiUrl = `${environment.apiUrl}/problemeTaches`;

  constructor(private http: HttpClient) { }

  // Récupérer tous les problèmes
  getProblemes(): Observable<ProblemeTache[]> {
    return this.http.get<ProblemeTache[]>(this.apiUrl);
  }

  // Récupérer un problème par ID
  getProblemeById(id: string): Observable<ProblemeTache> {
    return this.http.get<ProblemeTache>(`${this.apiUrl}/${id}`);
  }

  // Récupérer les problèmes d'une tâche spécifique
  getProblemesByTache(tacheId: string): Observable<ProblemeTache[]> {
    return this.http.get<ProblemeTache[]>(`${this.apiUrl}/taches/${tacheId}`);
  }

  // Ajouter un problème
  addProbleme(probleme: Partial<ProblemeTache>): Observable<ProblemeTache> {
    return this.http.post<ProblemeTache>(this.apiUrl, probleme);
  }

  // Mettre à jour un problème
  updateProbleme(id: string, updates: Partial<ProblemeTache>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  // Supprimer un problème
  deleteProbleme(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
