import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { TypeFormations } from '../../models/formation/TypeFormations.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class TypeService {
  private apiUrl = `${environment.formationUrl}/types`; 

  constructor(private http: HttpClient) {}

  getAllTypes(): Observable<TypeFormations[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(res => Array.isArray(res) ? (Array.isArray(res[0]) ? (res[0] as TypeFormations[]) : (res as TypeFormations[])) : [])
    );
  }

  getTypesByDomaine(idDomaine: number): Observable<TypeFormations[]> {
    return this.http.get<any>(`${this.apiUrl}/by-domaine/${idDomaine}`).pipe(
      map(res => Array.isArray(res) ? (Array.isArray(res[0]) ? (res[0] as TypeFormations[]) : (res as TypeFormations[])) : [])
    );
  }

  createType(type: TypeFormations): Observable<TypeFormations> {
    return this.http.post<TypeFormations>(this.apiUrl, type);
  }

  updateType(idType: number, type: TypeFormations): Observable<TypeFormations> {
    return this.http.put<TypeFormations>(`${this.apiUrl}/${idType}`, type);
  }

  deleteType(idType: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${idType}`);
  }

  getTypeById(idType: number): Observable<TypeFormations> {
    return this.http.get<TypeFormations>(`${this.apiUrl}/${idType}`);
  }
}
