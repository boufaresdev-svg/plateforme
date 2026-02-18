import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environement';
import { HttpClient } from '@angular/common/http';
import { EmployeAffecte } from '../../models/projet/EmployeAffecte.model';
import { map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmployeeAffecteService {


  private apiUrl = `${environment.apiUrl}/employeAffectes`;

  constructor(private http: HttpClient) { }


  getAll(): Observable<EmployeAffecte[]> {
    return this.http.get<EmployeAffecte[][]>(this.apiUrl).pipe(
      map(arr => arr[0])
    );
  }

  getById(id: string): Observable<EmployeAffecte> {
    return this.http.get<EmployeAffecte>(`${this.apiUrl}/employeAffecte/${id}`);
  }

  getByTache(tacheId: string): Observable<EmployeAffecte[]> {
    return this.http.get<EmployeAffecte[]>(`${this.apiUrl}/tache/${tacheId}`);
  }


  add(employee: Partial<EmployeAffecte>): Observable<EmployeAffecte> {
    return this.http.post<EmployeAffecte>(this.apiUrl, employee);
  }


  update(id: string, updates: Partial<EmployeAffecte>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }
  
  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
