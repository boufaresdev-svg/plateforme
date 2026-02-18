import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { ContenuGlobal } from '../../models/formation/ContenuGlobal.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class ContenuGlobalService {

  private apiUrl = `${environment.formationUrl}/contenusglobaux`;

  constructor(private http: HttpClient) {}

  getAllContenus(): Observable<ContenuGlobal[]> {
    return this.http.get<ContenuGlobal[][]>(`${this.apiUrl}`).pipe(
      map(response => response[0] || [])
    );  
  }

  getContenuById(id: number): Observable<ContenuGlobal> {
    return this.http.get<ContenuGlobal>(`${this.apiUrl}/${id}`);
  }

  addContenu(contenu: any): Observable<ContenuGlobal> {
    return this.http.post<ContenuGlobal>(`${this.apiUrl}`, contenu);
  }

  updateContenu(id: number, contenu: any): Observable<ContenuGlobal> {
    return this.http.put<ContenuGlobal>(`${this.apiUrl}/${id}`, contenu);
  }

  deleteContenu(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

getContenusByFormation(idFormation: number): Observable<ContenuGlobal[]> {
  return this.http.get<ContenuGlobal[][]>(`${this.apiUrl}/by-formation/${idFormation}`).pipe(
    map(response => response[0] || [])
  );



}
}
