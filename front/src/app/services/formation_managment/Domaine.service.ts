import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Domaine } from '../../models/formation/Domaine.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class DomaineService {

  private apiUrl = `${environment.formationUrl}/domaines`; 

  constructor(private http: HttpClient) {}

  getAllDomaines(): Observable<Domaine[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(res => Array.isArray(res) ? (Array.isArray(res[0]) ? (res[0] as Domaine[]) : (res as Domaine[])) : [])
    );  
  }

  getDomaineById(id: number): Observable<Domaine> {
    return this.http.get<Domaine>(`${this.apiUrl}/${id}`);
  }

  createDomaine(domaine: Domaine): Observable<Domaine> {
    return this.http.post<Domaine>(this.apiUrl, domaine);
  }

  updateDomaine(id: number, domaine: Domaine): Observable<Domaine> {
    return this.http.put<Domaine>(`${this.apiUrl}/${id}`, domaine);
  }

  deleteDomaine(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getTypesByDomaine(idDomaine: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${idDomaine}/types`);
  }
}
