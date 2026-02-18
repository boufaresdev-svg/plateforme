import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Reparation } from '../../models/vehicule-mangment/reparation.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class ReparationService {

 
  private apiUrl = `${environment.apiUrl}/reparation`;
 
  constructor(private http: HttpClient) { }

  getReparations(): Observable<Reparation[]> {
    return this.http.get<Reparation[][]>(`${this.apiUrl}`).pipe(
      map(arr => arr[0])  
    );
  }

  addReparation(rep: Partial<Reparation>): Observable<Reparation> {
    return this.http.post<Reparation>(`${this.apiUrl}`, rep);
  }

  updateReparation(id: string, updates: Partial<Reparation>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  deleteReparation(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchReparations(query: any): Observable<Reparation[]> {
    return this.http.post<Reparation[]>(`${this.apiUrl}/search`, query);
  }
}
