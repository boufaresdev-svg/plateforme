import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environement';
import { HttpClient } from '@angular/common/http';
import { Regle } from '../../models/rh/Regle.model';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RegleService {
  
 private apiUrl = `${environment.apiUrl}/Regle`;

  constructor(private http: HttpClient) { }

  // Récupérer toutes les règles
  getRegles(): Observable<Regle[]> {
    return this.http.get<Regle[]>(this.apiUrl).pipe(
      catchError(err => {
        console.error('Erreur lors de la récupération des règles', err);
        return throwError(() => err);
      })
    );
  }

  // Ajouter une nouvelle règle
  addRegle(regle: Partial<Regle>): Observable<Regle> {
    return this.http.post<Regle>(this.apiUrl, regle).pipe(
      catchError(err => {
        console.error('Erreur lors de l’ajout de la règle', err);
        return throwError(() => err);
      })
    );
  }

  // Mettre à jour une règle existante
  updateRegle(id: string, updates: Partial<Regle>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates).pipe(
      catchError(err => {
        console.error('Erreur lors de la mise à jour de la règle', err);
        return throwError(() => err);
      })
    );
  }

  // Supprimer une règle
  deleteRegle(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => {
        console.error('Erreur lors de la suppression de la règle', err);
        return throwError(() => err);
      })
    );
  }

  // Recherche de règles selon des critères
  searchRegles(query: any): Observable<Regle[]> {
    return this.http.post<Regle[]>(`${this.apiUrl}/search`, query).pipe(
      catchError(err => {
        console.error('Erreur lors de la recherche des règles', err);
        return throwError(() => err);
      })
    );
  }
}
