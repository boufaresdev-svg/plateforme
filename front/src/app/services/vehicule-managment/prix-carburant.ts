import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { PrixCarburant } from '../../models/vehicule-mangment/PrixCarburant.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class PrixCarburantService {
 
   
  private apiUrl = `${environment.apiUrl}/prix-carburant`;

  constructor(private http: HttpClient) { }

  get(): Observable<PrixCarburant[]> {
    return this.http.get<PrixCarburant[][]>(`${this.apiUrl}`)
      .pipe(
        map(arr => arr[0])
      );
  }

  update(id: string, prix: PrixCarburant): Observable<any> {
    const command = {
      id: id,
      essence: prix.essence,
      gasoil: prix.gasoil,
      gasoil50: prix.gasoil_50,
      gpl: prix.gpl
    };
     
    return this.http.put(`${this.apiUrl}/${id}`, command);
  }



}
