import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environement';
import { Mission } from '../../models/projet/Mission.model';
import { map, Observable } from 'rxjs';
import { EmployeAffecte } from '../../models/projet/EmployeAffecte.model';
import { Tache } from '../../models/projet/Tache.model';

@Injectable({
  providedIn: 'root'
})
export class MissionService {
  private apiUrl = `${environment.apiUrl}/missions`;

  constructor(private http: HttpClient) { }


  getMissions(): Observable<Mission[]> {
    return this.http.get<Mission[][]>(this.apiUrl).pipe(
      map(arr => arr[0])
    );
  }

  getMissionById(id: string): Observable<Mission> {
    return this.http.get<Mission>(`${this.apiUrl}/mission/${id}`);
  }


  getMissionsByProjet(projetId: string): Observable<Mission[]> {
    return this.http.get<Mission[]>(`${this.apiUrl}/projets/projet/${projetId}`);
  }

  addMission(mission: Partial<Mission>): Observable<Mission> {
    return this.http.post<Mission>(this.apiUrl, mission);
  }


  updateMission(id: string, updates: Partial<Mission>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  deleteMission(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }


  getTachesByMissionId(missionId: string): Observable<{ mission: Mission, taches: Tache[], employes: EmployeAffecte[] }> {
    return this.getMissionById(missionId).pipe(
      map((missions: any) => {
        const mission = missions?.[0]?.[0];
        const taches = mission?.taches || [];
        const employes = mission?.employesAffectes || [];
        return { mission, taches, employes };
      })
    );
  }

}