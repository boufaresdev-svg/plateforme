import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environement';
import { HttpClient } from '@angular/common/http';
import { FichePaie } from '../../models/rh/FichePaie.model';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FichePaieService {
  
private apiUrl = `${environment.apiUrl}/FichePaie`;

  constructor(private http: HttpClient) { }

  // Récupérer toutes les fiches de paie
  getFichesPaie(): Observable<FichePaie[]> {
    return this.http.get<FichePaie[]>(this.apiUrl).pipe(
      catchError(err => {
        console.error('Erreur lors de la récupération des fiches de paie', err);
        return throwError(() => err);
      })
    );
  }

  // Ajouter une nouvelle fiche de paie
  addFichePaie(fiche: Partial<FichePaie>): Observable<FichePaie> {
    return this.http.post<FichePaie>(this.apiUrl, fiche).pipe(
      catchError(err => {
        console.error('Erreur lors de l’ajout de la fiche de paie', err);
        return throwError(() => err);
      })
    );
  }

  // Mettre à jour une fiche de paie existante
  updateFichePaie(id: string, updates: Partial<FichePaie>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates).pipe(
      catchError(err => {
        console.error('Erreur lors de la mise à jour de la fiche de paie', err);
        return throwError(() => err);
      })
    );
  }

  // Supprimer une fiche de paie
  deleteFichePaie(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => {
        console.error('Erreur lors de la suppression de la fiche de paie', err);
        return throwError(() => err);
      })
    );
  }

  // Recherche de fiches de paie selon des critères
  searchFichesPaie(query: any): Observable<FichePaie[]> {
    return this.http.post<FichePaie[]>(`${this.apiUrl}/search`, query).pipe(
      catchError(err => {
        console.error('Erreur lors de la recherche des fiches de paie', err);
        return throwError(() => err);
      })
    );
  }
}

