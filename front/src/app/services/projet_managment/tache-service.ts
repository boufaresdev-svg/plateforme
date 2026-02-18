import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environement';
import { map, Observable } from 'rxjs';
import { Tache } from '../../models/projet/Tache.model';

@Injectable({
  providedIn: 'root'
})
export class TacheService {

  private apiUrl = `${environment.apiUrl}/taches`;

  constructor(private http: HttpClient) { }

  getTaches(): Observable<Tache[]> {
    return this.http.get<Tache[][]>(this.apiUrl).pipe(
      map(arr => arr[0])
    );
  }

  getTacheById(id: string): Observable<Tache> {
    return this.http.get<Tache>(`${this.apiUrl}/tache/${id}`) ;
  }


  addTache(tache: Partial<Tache>): Observable<Tache> {
    return this.http.post<Tache>(this.apiUrl, tache);
  }


  updateTache(id: string, updates: Partial<Tache>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  // Supprimer une tâche
  deleteTache(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
