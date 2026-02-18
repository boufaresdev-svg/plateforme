import { Injectable } from '@angular/core';
 import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environement';
import { Observable } from 'rxjs';
import { LivrableTache } from '../../models/projet/LivrableTache.model';

@Injectable({
  providedIn: 'root'
})
export class LivrableTacheService {

  private apiUrl = `${environment.apiUrl}/livrableTaches`;

  constructor(private http: HttpClient) { }

  // Récupérer tous les livrables
  getLivrables(): Observable<LivrableTache[]> {
    return this.http.get<LivrableTache[]>(this.apiUrl);
  }

  // Récupérer un livrable par ID
  getLivrableById(id: string): Observable<LivrableTache> {
    return this.http.get<LivrableTache>(`${this.apiUrl}/${id}`);
  }

  // Récupérer les livrables d'une tâche spécifique
  getLivrablesByTache(tacheId: string): Observable<LivrableTache[]> {
    return this.http.get<LivrableTache[]>(`${this.apiUrl}/taches/${tacheId}`);
  }

  // Ajouter un livrable
  addLivrable(livrable: Partial<LivrableTache>): Observable<LivrableTache> {
    return this.http.post<LivrableTache>(this.apiUrl, livrable);
  }

  // Mettre à jour un livrable
  updateLivrable(id: string, updates: Partial<LivrableTache>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  // Supprimer un livrable
  deleteLivrable(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
