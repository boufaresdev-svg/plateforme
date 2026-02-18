import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { ContenuJourFormation } from '../../models/formation/ContenuJourFormation.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class ContenuJourFormationService {

  private apiUrl = `${environment.formationUrl}/contenusjour`;  

  constructor(private http: HttpClient) {}

getContenusByObjectif(idObjectifSpec: number): Observable<ContenuJourFormation[]> {
  return this.http.get<ContenuJourFormation[][]>(
    `${this.apiUrl}/by-objectif/${idObjectifSpec}`
  ).pipe(
    map(response => response[0] || [])
  );
}
  getContenuById(id: number): Observable<ContenuJourFormation> {
    return this.http.get<ContenuJourFormation>(`${this.apiUrl}/${id}`);
  }

  addContenuJour(payload: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, payload);
  }

  updateContenuJour(id: number, payload: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, payload);
  }

  deleteContenuJour(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  // Assign contenus détaillés to a contenu jour with optional niveau
  assignContenus(idContenuJour: number, idContenusDetailles: number[], niveau?: number, niveauLabel?: string): Observable<any> {
    const payload: any = { idContenusDetailles };
    if (niveau !== undefined) {
      payload.niveau = niveau;
      payload.niveauLabel = niveauLabel;
    }
    return this.http.post(`${this.apiUrl}/${idContenuJour}/assign-contenus`, payload);
  }

  // Get assigned contenus détaillés for a contenu jour
  getAssignedContenus(idContenuJour: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${idContenuJour}/assigned-contenus`);
  }

  // Update the ordre of a contenu jour
  updateContenuOrdre(idContenuJour: number, ordre: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${idContenuJour}/ordre?ordre=${ordre}`, {});
  }
}
