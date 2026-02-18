import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { SousCategorie } from '../../models/formation/SousCategorie.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class SousCategorieService {

  private apiUrl = `${environment.formationUrl}/souscategories`;

  constructor(private http: HttpClient) {}

  getAllSousCategories(): Observable<SousCategorie[]> {
    return this.http.get<SousCategorie[][]>(`${this.apiUrl}`).pipe(
      map(response => response[0] || [])
    );  
  }

  getSousCategorieById(id: number): Observable<SousCategorie> {
    return this.http.get<SousCategorie>(`${this.apiUrl}/${id}`);
  }

  getSousCategoriesByCategorie(idCategorie: number): Observable<SousCategorie[]> {
    return this.http.get<SousCategorie[][]>(`${this.apiUrl}/by-categorie/${idCategorie}`).pipe(
      map(response => response[0] || [])
    );  
  }

  addSousCategorie(payload: any): Observable<SousCategorie> {
    return this.http.post<SousCategorie>(`${this.apiUrl}`, payload);
  }

  updateSousCategorie(id: number, payload: any): Observable<SousCategorie> {
    return this.http.put<SousCategorie>(`${this.apiUrl}/${id}`, payload);
  }


  deleteSousCategorie(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
