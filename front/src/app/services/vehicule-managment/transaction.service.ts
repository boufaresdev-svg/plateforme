import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { TransactionCarburant } from '../../models/vehicule-mangment/transaction.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
 
  private apiUrl = `${environment.apiUrl}/transaction-carburant`;
  
  constructor(private http: HttpClient) {}

  getTransactions(): Observable<TransactionCarburant[]> {
    return this.http.get<TransactionCarburant[][]>(`${this.apiUrl}`).pipe(
        map(arr => arr[0])  
      );
  }
   
  addTransaction(tx: Partial<TransactionCarburant>): Observable<TransactionCarburant> {
    return this.http.post<TransactionCarburant>(`${this.apiUrl}`, tx);
  }

  updateTransaction(id: string, updates: Partial<TransactionCarburant>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  deleteTransaction(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchTransactions(query: any): Observable<TransactionCarburant[]> {
    return this.http.post<TransactionCarburant[]>(`${this.apiUrl}/search`, query);
  }
}
