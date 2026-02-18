import { Injectable } from '@angular/core';
import { CommentaireTache } from '../../models/projet/CommentaireTache.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environement';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CommentaireService {

  private apiUrl = `${environment.apiUrl}/commentaireTaches`;

  constructor(private http: HttpClient) { }

  getCommentaires(): Observable<CommentaireTache[]> {
    return this.http.get<CommentaireTache[]>(this.apiUrl);
  }

  getCommentaireById(id: string): Observable<CommentaireTache> {
    return this.http.get<CommentaireTache>(`${this.apiUrl}/${id}`);
  }

  getCommentairesByTache(tacheId: string): Observable<CommentaireTache[]> {
    return this.http.get<CommentaireTache[]>(`${this.apiUrl}/taches/tache${tacheId}`);
  }

  addCommentaire(commentaire: Partial<CommentaireTache>): Observable<CommentaireTache> {
    return this.http.post<CommentaireTache>(this.apiUrl, commentaire);
  }

  updateCommentaire(id: string, updates: Partial<CommentaireTache>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, updates);
  }

  deleteCommentaire(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
