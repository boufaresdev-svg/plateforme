import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environement';
import { HttpClient } from '@angular/common/http';
import { Prime } from '../../models/rh/Prime.model';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PrimeService {

  private apiUrl = `${environment.apiUrl}/Prime`;

  constructor(private http: HttpClient) { }

  getPrimes(): Observable<Prime[]> {
    return this.http.get<Prime[]>(this.apiUrl).pipe(
      catchError(err => {
        console.error('Erreur lors de la récupération des primes', err);
        return throwError(() => err);
      })
    );
  }

  addPrime(prime: Partial<Prime>): Observable<Prime> {
    return this.http.post<Prime>(this.apiUrl, prime).pipe(
      catchError(err => {
        console.error('Erreur lors de l’ajout de la prime', err);
        return throwError(() => err);
      })
    );
  }

  updatePrime(id: string, updates: Partial<Prime>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates).pipe(
      catchError(err => {
        console.error('Erreur lors de la mise à jour de la prime', err);
        return throwError(() => err);
      })
    );
  }

  deletePrime(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => {
        console.error('Erreur lors de la suppression de la prime', err);
        return throwError(() => err);
      })
    );
  }

  searchPrimes(query: any): Observable<Prime[]> {
    return this.http.post<Prime[]>(`${this.apiUrl}/search`, query).pipe(
      catchError(err => {
        console.error('Erreur lors de la recherche des primes', err);
        return throwError(() => err);
      })
    );
  }
}

