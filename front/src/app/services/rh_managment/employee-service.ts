import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Employee } from '../../models/rh/Employee.model';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  constructor(private http: HttpClient) { }

  private apiUrl = `${environment.apiUrl}/Employee`;
  
  getEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[][]>(`${this.apiUrl}`)
      .pipe(
        map(arr => arr[0])
      );
  }

  addEmployee(employee: Partial<Employee>): Observable<Employee> {
    return this.http.post<Employee>(`${this.apiUrl}`, employee);
  }

  updateEmployee(id: string, updates: Partial<Employee>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  deleteEmployee(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchEmployees(query: any): Observable<Employee[]> {
    return this.http.post<Employee[]>(`${this.apiUrl}/search`, query);
  }

 
}
