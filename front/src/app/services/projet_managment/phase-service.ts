import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Phase } from '../../models/projet/Phase.model';
import { environment } from '../../../environment/environement';
import { Mission } from '../../models/projet/Mission.model';


@Injectable({
  providedIn: 'root'
})
export class PhaseService {

  private apiUrl = `${environment.apiUrl}/phases`;

  constructor(private http: HttpClient) { }

  // Récupérer toutes les phases
  getPhases(): Observable<Phase[]> {
    return this.http.get<Phase[][]>(this.apiUrl).pipe(
      map(arr => arr[0])
    );
  }

  // Récupérer une phase par son ID
  getPhaseById(id: string): Observable<Phase> {
    return this.http.get<Phase>(`${this.apiUrl}/phase/${id}`);
  }

  // Ajouter une nouvelle phase
  addPhase(phase: Partial<Phase>): Observable<Phase> {
    return this.http.post<Phase>(this.apiUrl, phase);
  }

  // Mettre à jour une phase existante
  updatePhase(id: string, updates: Partial<Phase>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  // Supprimer une phase
  deletePhase(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getPhaseMissions(phaseId: string): Observable<{ phase: Phase, missions: Mission[] }> {
  return this.getPhaseById(phaseId).pipe(
    map((phases: any) => {    
      const phase = phases?.[0]?.[0]; // même principe
      const missions = phase?.missions || [];    
      return { phase, missions };            // renvoie un objet avec phase + missions
    })
  );
}
    

  
}
