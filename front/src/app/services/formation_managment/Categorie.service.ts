import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Categorie } from '../../models/formation/Categorie.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class CategorieService {

  private apiUrl = `${environment.formationUrl}/categories`;

  constructor(private http: HttpClient) {}

  getAllCategories(): Observable<Categorie[]> {
    return this.http.get<Categorie[]>(`${this.apiUrl}`);
  }

  getCategorieById(idCategorie: number): Observable<Categorie> {
    return this.http.get<Categorie>(`${this.apiUrl}/${idCategorie}`);
  }

getCategoriesByType(idType: number): Observable<Categorie[]> {
  return this.http.get<Categorie[]>(`${this.apiUrl}/by-type/${idType}`);
}
  addCategorie(categorie: Categorie): Observable<Categorie> {
    return this.http.post<Categorie>(`${this.apiUrl}`, categorie);
  }

  updateCategorie(idCategorie: number, categorie: Categorie): Observable<Categorie> {
    return this.http.put<Categorie>(`${this.apiUrl}/${idCategorie}`, categorie);
  }

  deleteCategorie(idCategorie: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${idCategorie}`);
  }
}
