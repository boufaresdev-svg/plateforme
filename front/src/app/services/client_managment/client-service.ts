import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environement';
import { map, Observable } from 'rxjs';
import { Client } from '../../models/client/client.model';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private apiUrl = `${environment.apiUrl}/client`;

  constructor(private http: HttpClient) { }

  getClients(): Observable<Client[]> {
    return this.http.get<Client[][]>(`${this.apiUrl}`).pipe(
      map(arr => arr[0])
    );
  }

  getClientById(id: string): Observable<Client> {
    return this.http.get<Client>(`${this.apiUrl}/${id}`);
  }

  addClient(client: Partial<Client>): Observable<Client> {
    return this.http.post<Client>(`${this.apiUrl}`, client);
  }

  updateClient(id: string, updates: Partial<Client>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  deleteClient(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchClients(query: any): Observable<Client[]> {
    return this.http.post<Client[]>(`${this.apiUrl}/search`, query);
  }
}
