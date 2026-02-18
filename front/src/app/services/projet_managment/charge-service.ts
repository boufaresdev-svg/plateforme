import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
 import { map, Observable } from 'rxjs';
import { environment } from '../../../environment/environement';
import { Tache } from '../../models/projet/Tache.model';
import { Charge } from '../../models/projet/Charge.model';

@Injectable({
  providedIn: 'root'
})
export class ChargeService {
 
 private apiUrl = `${environment.apiUrl}/charges`;

  constructor(private http: HttpClient) {}

  // 🔹 Récupérer toutes les charges
  getCharges(): Observable<Charge[]> {
    return this.http.get<Charge[][]>(this.apiUrl).pipe(
      map(arr => arr[0])
    );
  }

  // 🔹 Récupérer une charge par ID
  getChargeById(id: string): Observable<Charge> {
    return this.http.get<Charge>(`${this.apiUrl}/${id}`);
  }

  // 🔹 Récupérer les charges d’une tâche
  getChargesByTache(tacheId: string): Observable<Charge[]> {
    return this.http.get<Charge[]>(`${this.apiUrl}/taches/tache/${tacheId}`);
  }

  // 🔹 Ajouter une charge
  addCharge(charge: Partial<Charge>): Observable<Charge> {
    return this.http.post<Charge>(this.apiUrl, charge);
  }

  // 🔹 Mettre à jour une charge
  updateCharge(id: string, updates: Partial<Charge>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  // 🔹 Supprimer une charge
  deleteCharge(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
