import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class FormateurService {

  private baseUrl = `${environment.formationUrl}/formateurs`;

  constructor(private http: HttpClient) {}

  getAllFormateurs(): Observable<any[]> {
    return this.http.get<any[][]>(`${this.baseUrl}`).pipe(
      map(response => response[0] || [])
    );
  }

  getFormateurById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

addFormateur(formateur: any, file?: File): Observable<any> {
  const formData = new FormData();
  formData.append('nom', formateur.nom);
  formData.append('prenom', formateur.prenom);
  formData.append('specialite', formateur.specialite);
  formData.append('contact', formateur.contact);
  formData.append('experience', formateur.experience);

  if (file) {
    formData.append('document', file);
  }

  return this.http.post(this.baseUrl, formData);
}

updateFormateurWithFile(id: number, formData: FormData): Observable<void> {
  return this.http.put<void>(`${this.baseUrl}/${id}/with-file`, formData);
}

updateFormateur(id: number, formateur: any): Observable<void> {
  return this.http.put<void>(`${this.baseUrl}/${id}`, formateur);
}


  deleteFormateur(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
