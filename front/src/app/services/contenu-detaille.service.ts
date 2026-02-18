import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpEvent, HttpEventType, HttpRequest } from '@angular/common/http';
import { Observable, Subject, from } from 'rxjs';
import { environment } from '../../environment/environement';
import { PaginationParams } from '../models/pagination.model';
import { PagedContenuDetailleResponse } from '../models/formation/paged-contenu.model';

export interface UploadProgress {
  loaded: number;
  total: number;
  percentage: number;
  fileName: string;
}

export interface LevelFile {
  fileName: string;
  filePath: string;
  fileType: string;
  fileSize: number;
  uploadDate?: string;
  description?: string;
}

export interface ContentLevel {
  levelNumber: number;
  theorieContent: string;
  pratiqueContent?: string;
  dureeTheorique?: number;
  dureePratique?: number;
  // Multiple files per level
  files?: LevelFile[];
}

export interface ContenuDetailleFileDto {
  idFile?: number;
  fileName: string;
  filePath: string;
  fileType: string;
  fileSize: number;
  description?: string;
  uploadDate?: string;
}

export interface ContenuDetailleDto {
  idContenuDetaille?: number;
  titre: string;
  contenusCles: string[];
  methodesPedagogiques?: string;
  tags?: string;
  dureeTheorique?: number;
  dureePratique?: number;
  levels: ContentLevel[];
  idJourFormation?: number; // Optional - can be assigned later
}

export interface ContenuWithJourResponse {
  idContenuDetaille: number;
  titre: string;
  methodesPedagogiques?: string;
  tags?: string;
  dureeTheorique?: number;
  dureePratique?: number;
  contenusCles?: string[];
  numeroJour: number;  // From ContenuJourFormation
  idContenuJour: number;  // Reference to ContenuJourFormation
  files: {
    fileName: string;
    filePath: string;
    fileType: string;
    fileSize: number;
    levelNumber: number;
  }[];
}

@Injectable({ providedIn: 'root' })
export class ContenuDetailleService {
  private baseUrl = `${environment.apiUrl}/contenus-detailles`;

  constructor(private http: HttpClient) {}

  // Get ContenuDetaille by ID
  getById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  // Get all ContenuDetaille (independent of jour)
  getAllContenuDetaille(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}`);
  }

  // Get paginated ContenuDetaille
  getContenuDetaillePagedQ(params: PaginationParams, idJourFormation?: number): Observable<PagedContenuDetailleResponse> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString());
    
    if (params.sortBy) {
      httpParams = httpParams.set('sortBy', params.sortBy);
    }
    if (params.sortDirection) {
      httpParams = httpParams.set('sortDirection', params.sortDirection);
    }
    if (idJourFormation) {
      httpParams = httpParams.set('idJourFormation', idJourFormation.toString());
    }
    
    return this.http.get<PagedContenuDetailleResponse>(`${this.baseUrl}/paginated`, { params: httpParams });
  }

  // Search by titre (backend substring, case-insensitive)
  searchContenuDetaille(query: string): Observable<any[]> {
    const params = query ? new HttpParams().set('q', query) : undefined;
    return this.http.get<any[]>(`${this.baseUrl}/search`, { params });
  }

  // Create
  addContenuDetaille(payload: ContenuDetailleDto): Observable<any> {
    return this.http.post(`${this.baseUrl}`, payload);
  }

  // Read by id
  getContenuDetailleById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  // Read by jour
  getAllByJour(idJour: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/by-jour/${idJour}`);
  }

  // Read by formation
  getContenuDetailleByFormationId(idFormation: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/by-formation/${idFormation}`);
  }

  // Read by formation with jour mapping (for PDF generation)
  getContenuDetailleByFormationWithJours(idFormation: number): Observable<ContenuWithJourResponse[]> {
    return this.http.get<ContenuWithJourResponse[]>(`${this.baseUrl}/by-formation/${idFormation}/with-jours`);
  }

  // Update
  updateContenuDetaille(id: number, payload: Partial<ContenuDetailleDto>): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, payload);
  }

  // Delete
  deleteContenuDetaille(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // Upload multiple files for a specific level
  uploadLevelFiles(contentId: number, levelNumber: number, files: File[]): Observable<any> {
    const formData = new FormData();
    files.forEach(file => formData.append('files', file));
    return this.http.post(`${this.baseUrl}/${contentId}/levels/${levelNumber}/upload`, formData);
  }

  // Get all files for a specific level
  getLevelFiles(contentId: number, levelNumber: number): Observable<LevelFile[]> {
    return this.http.get<LevelFile[]>(`${this.baseUrl}/${contentId}/levels/${levelNumber}/files`);
  }

  // Delete a specific file from a level
  deleteLevelFile(contentId: number, levelNumber: number, filePath: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${contentId}/levels/${levelNumber}/files/${filePath}`);
  }

  /**
   * Upload large files using streaming endpoint
   * Uses XMLHttpRequest for better progress tracking and to bypass Angular's default chunking
   * Recommended for files > 100MB
   */
  uploadLargeFile(file: File, contentId: number, levelNumber: number): Observable<UploadProgress> {
    return new Observable<UploadProgress>(observer => {
      const xhr = new XMLHttpRequest();
      const formData = new FormData();
      formData.append('file', file);
      formData.append('contentId', contentId.toString());
      formData.append('levelNumber', levelNumber.toString());

      // Progress tracking
      xhr.upload.addEventListener('progress', (event) => {
        if (event.lengthComputable) {
          const progress: UploadProgress = {
            loaded: event.loaded,
            total: event.total,
            percentage: Math.round((event.loaded / event.total) * 100),
            fileName: file.name
          };
          observer.next(progress);
        }
      });

      // Completion
      xhr.addEventListener('load', () => {
        if (xhr.status >= 200 && xhr.status < 300) {
          observer.next({
            loaded: file.size,
            total: file.size,
            percentage: 100,
            fileName: file.name
          });
          observer.complete();
        } else {
          observer.error(new Error(`Upload failed: ${xhr.status} ${xhr.statusText}`));
        }
      });

      // Error handling
      xhr.addEventListener('error', () => {
        observer.error(new Error('Upload failed: Network error'));
      });

      xhr.addEventListener('abort', () => {
        observer.error(new Error('Upload cancelled'));
      });

      // Use streaming endpoint for large files
      const streamingUrl = `${environment.apiUrl}/streaming/upload`;
      xhr.open('POST', streamingUrl, true);
      
      // Don't set Content-Type header - let browser set it with boundary for FormData
      xhr.send(formData);

      // Return cleanup function
      return () => {
        xhr.abort();
      };
    });
  }

  /**
   * Upload file with progress tracking using HttpClient (for smaller files)
   */
  uploadLevelFilesWithProgress(contentId: number, levelNumber: number, files: File[]): Observable<HttpEvent<any>> {
    const formData = new FormData();
    files.forEach(file => formData.append('files', file));
    
    const req = new HttpRequest(
      'POST',
      `${this.baseUrl}/${contentId}/levels/${levelNumber}/upload`,
      formData,
      { reportProgress: true }
    );
    
    return this.http.request(req);
  }

  /**
   * Smart upload - uses streaming for large files (>100MB), regular for smaller files
   */
  smartUploadFile(file: File, contentId: number, levelNumber: number, threshold: number = 100 * 1024 * 1024): Observable<UploadProgress> {
    if (file.size > threshold) {
      // Use streaming for large files
      return this.uploadLargeFile(file, contentId, levelNumber);
    } else {
      // Use regular upload for smaller files
      return new Observable<UploadProgress>(observer => {
        const formData = new FormData();
        formData.append('files', file);
        
        this.http.post(`${this.baseUrl}/${contentId}/levels/${levelNumber}/upload`, formData).subscribe({
          next: () => {
            observer.next({
              loaded: file.size,
              total: file.size,
              percentage: 100,
              fileName: file.name
            });
            observer.complete();
          },
          error: (err) => observer.error(err)
        });
      });
    }
  }
}
