import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { ObjectifSpecifique } from '../../models/formation/ObjectifSpecifique.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class ObjectifSpecifiqueService {

  private apiUrl = `${environment.formationUrl}/objectifsspecifiques`;

  constructor(private http: HttpClient) {}


  addObjectif(payload: any): Observable<ObjectifSpecifique> {
    return this.http.post<ObjectifSpecifique>(`${this.apiUrl}`, payload);
  }

  updateObjectif(id: number, payload: any): Observable<ObjectifSpecifique> {
    return this.http.put<ObjectifSpecifique>(`${this.apiUrl}/${id}`, payload);
  }

  deleteObjectif(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }


  getObjectifsByContenu(idContenu: number): Observable<ObjectifSpecifique[]> {
    return this.http.get<ObjectifSpecifique[][]>(`${this.apiUrl}/by-contenu/${idContenu}`).pipe(
      map(response => response[0] || [])
    );
  }
   

  getObjectifById(id: number): Observable<ObjectifSpecifique> {
    return this.http.get<ObjectifSpecifique>(`${this.apiUrl}/${id}`);
  }
}
