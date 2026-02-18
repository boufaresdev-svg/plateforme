import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, tap } from 'rxjs';
import { Projet } from '../../models/projet/Projet.model';
import { environment } from '../../../environment/environement';
import { Phase } from '../../models/projet/Phase.model';

@Injectable({
  providedIn: 'root'
})
export class ProjetService {

  private apiUrl = `${environment.apiUrl}/projets`;

  constructor(private http: HttpClient) { }

  getProjets(): Observable<Projet[]> {
    return this.http.get<Projet[][]>(this.apiUrl).pipe(
      map(arr => arr[0])
    );
  }

  getProjetById(id: string): Observable<Projet> {
    return this.http.get<Projet>(`${this.apiUrl}/projet/${id}`);
  }

  getProjetPhases(projetId: string): Observable<{ projet: any, phases: any[] }> {
  return this.getProjetById(projetId).pipe(
    map((projets: any) => {
      // Si ton backend retourne un tableau imbriqué, on sécurise l’accès
      const projet = projets?.[0]?.[0] || projets?.[0] || projets;
      const phases = projet?.phases || [];
      return { projet, phases };
    })
  );
}

   

  addProjet(projet: Partial<Projet>): Observable<Projet> {
    return this.http.post<Projet>(this.apiUrl, projet);
  }

  updateProjet(id: string, updates: Partial<Projet>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  deleteProjet(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
