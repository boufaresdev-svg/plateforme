import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Certificat } from '../../models/formation/Certificat.model';

@Injectable({
  providedIn: 'root'
})
export class CertificatService {

  private baseUrl = 'http://localhost:8080/api/certificats';

  constructor(private http: HttpClient) {}

  getAllCertificats(): Observable<Certificat[]> {
    return this.http.get<Certificat[][]>(`${this.baseUrl}`).pipe(
      map(arrays => arrays.flat())
    );  
  }

  getCertificatById(id: number): Observable<Certificat> {
    return this.http.get<Certificat>(`${this.baseUrl}/${id}`);
  }

  createCertificat(data: Certificat): Observable<Certificat> {
    return this.http.post<Certificat>(`${this.baseUrl}`, data);
  }

  updateCertificat(id: number, data: Certificat): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/${id}`, data);
  }

  deleteCertificat(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

getCertificatByExamen(idExamen: number): Observable<Certificat[]> {
  return this.http.get<Certificat[][]>(`${this.baseUrl}/by-examen/${idExamen}`).pipe(
    map(arrays => arrays.flat())
  );    
}

  getCertificatByApprenant(idApprenant: number): Observable<Certificat[]> {
    return this.http.get<Certificat[]>(`${this.baseUrl}/by-apprenant/${idApprenant}`);
  }

}
