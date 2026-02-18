import { Injectable } from '@angular/core';
import { Retenue } from '../../models/rh/Retenue.model';
import { catchError, Observable, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class RetenueService {
  
 private apiUrl = `${environment.apiUrl}/Retenue`;

  constructor(private http: HttpClient) { }

  // Récupérer toutes les retenues
  getRetenues(): Observable<Retenue[]> {
    return this.http.get<Retenue[]>(this.apiUrl).pipe(
      catchError(err => {
        console.error('Erreur lors de la récupération des retenues', err);
        return throwError(() => err);
      })
    );
  }

  // Ajouter une nouvelle retenue
  addRetenue(retenue: Partial<Retenue>): Observable<Retenue> {
    return this.http.post<Retenue>(this.apiUrl, retenue).pipe(
      catchError(err => {
        console.error('Erreur lors de l’ajout de la retenue', err);
        return throwError(() => err);
      })
    );
  }

  // Mettre à jour une retenue existante
  updateRetenue(id: string, updates: Partial<Retenue>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates).pipe(
      catchError(err => {
        console.error('Erreur lors de la mise à jour de la retenue', err);
        return throwError(() => err);
      })
    );
  }

  // Supprimer une retenue
  deleteRetenue(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => {
        console.error('Erreur lors de la suppression de la retenue', err);
        return throwError(() => err);
      })
    );
  }

  // Recherche de retenues selon des critères
  searchRetenues(query: any): Observable<Retenue[]> {
    return this.http.post<Retenue[]>(`${this.apiUrl}/search`, query).pipe(
      catchError(err => {
        console.error('Erreur lors de la recherche des retenues', err);
        return throwError(() => err);
      })
    );
  }
}