import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Congee } from '../../models/rh/Congee.model';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class CongeeService {
 

  private apiUrl = `${environment.apiUrl}/Congee`;

  constructor(private http: HttpClient) { }

  getCongees(): Observable<Congee[]> {
    return this.http.get<Congee[][]>(`${this.apiUrl}`)
      .pipe(
        map(arr => arr[0])
      );
  }

  addCongee(congee: Partial<Congee>): Observable<Congee> {
    return this.http.post<Congee>(`${this.apiUrl}`, congee);
  }

  updateCongee(id: string, updates: Partial<Congee>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }


  deleteCongee(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchCongees(query: any): Observable<Congee[]> {
    return this.http.post<Congee[]>(`${this.apiUrl}/search`, query);
  }

}
