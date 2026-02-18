import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import { Fournisseur, DettesFournisseur, FournisseurStats, DetteUpdateRequest, TranchePaiement, TranchePaiementQuery, PageResponse, PaginationParams } from '../models/fournisseur/fournisseur.model';
import { RapportDettesEntreprise, RapportFilterParams } from '../models/fournisseur/rapport-dettes.model';
import { environment } from '../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class FournisseurService {
  private baseUrl = environment.apiUrl;
  private fournisseursSubject = new BehaviorSubject<Fournisseur[]>([]);
  public fournisseurs$ = this.fournisseursSubject.asObservable();

  private dettesSubject = new BehaviorSubject<DettesFournisseur[]>([]);
  public dettes$ = this.dettesSubject.asObservable();

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) {
    this.loadFournisseurs();
    this.loadDettes();
  }

  // Fournisseur CRUD operations
  getAllFournisseurs(): Observable<Fournisseur[]> {
    return this.http.post<any>(`${this.baseUrl}/fournisseur/search`, {}, this.httpOptions).pipe(
      map((response: any) => {
        // Handle array containing paginated response [{ content: [...], pageNumber: 0, ... }]
        if (Array.isArray(response) && response.length > 0 && response[0].content && Array.isArray(response[0].content)) {
          this.fournisseursSubject.next(response[0].content);
          return response[0].content;
        }
        // Handle direct paginated response { content: [...], pageNumber: 0, ... }
        if (response && response.content && Array.isArray(response.content)) {
          this.fournisseursSubject.next(response.content);
          return response.content;
        }
        // Handle nested array response [[...]]
        if (Array.isArray(response) && response.length > 0 && Array.isArray(response[0])) {
          this.fournisseursSubject.next(response[0]);
          return response[0];
        }
        // Handle direct array response [...]
        if (Array.isArray(response)) {
          this.fournisseursSubject.next(response);
          return response;
        }
        console.warn('Unexpected fournisseur response format:', response);
        this.fournisseursSubject.next([]);
        return [];
      }),
      catchError((error) => {
        console.error('Error fetching fournisseurs:', error);
        return this.handleError(error);
      })
    );
  }

  // Paginated version of getAllFournisseurs
  getFournisseursPaginated(params?: PaginationParams): Observable<PageResponse<Fournisseur>> {
    const searchBody: any = {};
    
    if (params?.page !== undefined) {
      searchBody.page = params.page;
    }
    if (params?.size !== undefined) {
      searchBody.size = params.size;
    }
    if (params?.sortBy) {
      searchBody.sortBy = params.sortBy;
    }
    if (params?.sortDirection) {
      searchBody.sortDirection = params.sortDirection;
    }

    return this.http.post<any>(`${this.baseUrl}/fournisseur/search`, searchBody, this.httpOptions).pipe(
      map(response => {
        // Handle if response is wrapped in an array
        const pageResponse = Array.isArray(response) && response.length > 0 ? response[0] : response;
        
        // Map pageNumber to page for consistency
        return {
          content: pageResponse.content || [],
          page: pageResponse.pageNumber ?? pageResponse.page ?? 0,
          size: pageResponse.pageSize ?? pageResponse.size ?? 10,
          totalElements: pageResponse.totalElements ?? 0,
          totalPages: pageResponse.totalPages ?? 0,
          last: pageResponse.last ?? false,
          first: pageResponse.first ?? false,
          empty: pageResponse.empty ?? true,
          sortBy: params?.sortBy,
          sortDirection: params?.sortDirection
        } as PageResponse<Fournisseur>;
      }),
      tap(response => {
        // Update the subject with the paginated content
        if (response.content) {
          this.fournisseursSubject.next(response.content);
        }
      }),
      catchError(this.handleError)
    );
  }

  getFournisseurById(id: string): Observable<Fournisseur> {
    return this.http.get<Fournisseur>(`${this.baseUrl}/fournisseur/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  createFournisseur(fournisseur: Omit<Fournisseur, 'id'>): Observable<Fournisseur> {
    return this.http.post<Fournisseur>(`${this.baseUrl}/fournisseur`, fournisseur, this.httpOptions).pipe(
      tap(() => this.loadFournisseurs()),
      catchError(this.handleError)
    );
  }

  updateFournisseur(id: string, fournisseur: Partial<Fournisseur>): Observable<Fournisseur> {
    return this.http.put<Fournisseur>(`${this.baseUrl}/fournisseur/${id}`, fournisseur, this.httpOptions).pipe(
      tap(() => this.loadFournisseurs()),
      catchError(this.handleError)
    );
  }

  deleteFournisseur(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/fournisseur/${id}`).pipe(
      tap(() => this.loadFournisseurs()),
      catchError(this.handleError)
    );
  }

  searchFournisseurs(criteria: any): Observable<Fournisseur[]> {
    return this.http.post<Fournisseur[]>(`${this.baseUrl}/fournisseur/search`, criteria, this.httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  // Paginated search for fournisseurs
  searchFournisseursPaginated(criteria: any, params?: PaginationParams): Observable<PageResponse<Fournisseur>> {
    const searchBody = {
      ...criteria,
      page: params?.page,
      size: params?.size,
      sortBy: params?.sortBy,
      sortDirection: params?.sortDirection
    };

    return this.http.post<any>(`${this.baseUrl}/fournisseur/search`, searchBody, this.httpOptions).pipe(
      map(response => {
        // Handle if response is wrapped in an array
        const pageResponse = Array.isArray(response) && response.length > 0 ? response[0] : response;
        
        // Map pageNumber to page for consistency
        return {
          content: pageResponse.content || [],
          page: pageResponse.pageNumber ?? pageResponse.page ?? 0,
          size: pageResponse.pageSize ?? pageResponse.size ?? 10,
          totalElements: pageResponse.totalElements ?? 0,
          totalPages: pageResponse.totalPages ?? 0,
          last: pageResponse.last ?? false,
          first: pageResponse.first ?? false,
          empty: pageResponse.empty ?? true,
          sortBy: params?.sortBy,
          sortDirection: params?.sortDirection
        } as PageResponse<Fournisseur>;
      }),
      tap(response => {
        // Update the subject with the paginated content
        if (response.content) {
          this.fournisseursSubject.next(response.content);
        }
      }),
      catchError(this.handleError)
    );
  }

  // Dettes CRUD operations
  getAllDettes(): Observable<DettesFournisseur[]> {
    return this.http.post<any>(`${this.baseUrl}/dettes-fournisseur/search`, {}, this.httpOptions).pipe(
      map(response => {
        let dettes = [];
        if (Array.isArray(response)) {
          if (response.length > 0 && Array.isArray(response[0])) {
            dettes = response[0];
          } else {
            dettes = response;
          }
        } else {
          dettes = [];
        }
        
        return dettes as DettesFournisseur[];
      }),
      tap(dettes => {
        this.dettesSubject.next(dettes);
      }),
      catchError(this.handleError)
    );
  }

  // Paginated version of getAllDettes
  getDettesPaginated(params?: PaginationParams): Observable<PageResponse<DettesFournisseur>> {
    const searchBody: any = {};
    
    if (params?.page !== undefined) {
      searchBody.page = params.page;
    }
    if (params?.size !== undefined) {
      searchBody.size = params.size;
    }
    if (params?.sortBy) {
      searchBody.sortBy = params.sortBy;
    }
    if (params?.sortDirection) {
      searchBody.sortDirection = params.sortDirection;
    }

    return this.http.post<any>(`${this.baseUrl}/dettes-fournisseur/search`, searchBody, this.httpOptions).pipe(
      map(response => {
        // Handle if response is wrapped in an array
        const pageResponse = Array.isArray(response) && response.length > 0 ? response[0] : response;
        
        // Map pageNumber to page for consistency
        return {
          content: pageResponse.content || [],
          page: pageResponse.pageNumber ?? pageResponse.page ?? 0,
          size: pageResponse.pageSize ?? pageResponse.size ?? 10,
          totalElements: pageResponse.totalElements ?? 0,
          totalPages: pageResponse.totalPages ?? 0,
          last: pageResponse.last ?? false,
          first: pageResponse.first ?? false,
          empty: pageResponse.empty ?? true,
          sortBy: params?.sortBy,
          sortDirection: params?.sortDirection
        } as PageResponse<DettesFournisseur>;
      }),
      tap(response => {
        // Update the subject with the paginated content
        if (response.content) {
          this.dettesSubject.next(response.content);
        }
      }),
      catchError(this.handleError)
    );
  }

  getDetteById(id: string): Observable<DettesFournisseur> {
    return this.http.get<DettesFournisseur>(`${this.baseUrl}/dettes-fournisseur/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  getDettesByFournisseurId(fournisseurId: string): Observable<DettesFournisseur[]> {
    const query = { fournisseurId: fournisseurId };
    return this.http.post<DettesFournisseur[]>(`${this.baseUrl}/dettes-fournisseur/search`, query, this.httpOptions).pipe(
      map(response => {
        // Handle if response is wrapped in an array
        if (Array.isArray(response) && response.length > 0 && Array.isArray(response[0])) {
          return response[0];
        }
        return Array.isArray(response) ? response : [];
      }),
      catchError(this.handleError)
    );
  }

  // Paginated version of getDettesByFournisseurId
  getDettesByFournisseurIdPaginated(fournisseurId: string, params?: PaginationParams): Observable<PageResponse<DettesFournisseur>> {
    const searchBody: any = {
      fournisseurId: fournisseurId
    };
    
    if (params?.page !== undefined) {
      searchBody.page = params.page;
    }
    if (params?.size !== undefined) {
      searchBody.size = params.size;
    }
    if (params?.sortBy) {
      searchBody.sortBy = params.sortBy;
    }
    if (params?.sortDirection) {
      searchBody.sortDirection = params.sortDirection;
    }

    return this.http.post<any>(`${this.baseUrl}/dettes-fournisseur/search`, searchBody, this.httpOptions).pipe(
      map(response => {
        // Handle if response is wrapped in an array
        const pageResponse = Array.isArray(response) && response.length > 0 ? response[0] : response;
        
        return {
          content: pageResponse.content || [],
          page: pageResponse.pageNumber ?? pageResponse.page ?? 0,
          size: pageResponse.pageSize ?? pageResponse.size ?? 10,
          totalElements: pageResponse.totalElements ?? 0,
          totalPages: pageResponse.totalPages ?? 0,
          last: pageResponse.last ?? false,
          first: pageResponse.first ?? false,
          empty: pageResponse.empty ?? true,
          sortBy: params?.sortBy,
          sortDirection: params?.sortDirection
        } as PageResponse<DettesFournisseur>;
      }),
      catchError(this.handleError)
    );
  }

  createDette(dette: Omit<DettesFournisseur, 'id'>): Observable<DettesFournisseur> {
    return this.http.post<any>(`${this.baseUrl}/dettes-fournisseur`, dette, this.httpOptions).pipe(
      map(response => {
        // Handle if response is wrapped in an array
        return Array.isArray(response) && response.length > 0 ? response[0] : response;
      }),
      tap(() => {
        this.loadDettes();
        this.loadFournisseurs();
      }),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 409) {
          // Duplicate numeroFacture
          return throwError(() => ({
            status: 409,
            message: error.error?.message || `Le numéro de facture ${dette.numeroFacture} existe déjà`,
            error: error.error
          }));
        }
        return this.handleError(error);
      })
    );
  }

  updateDette(id: string, detteUpdate: DetteUpdateRequest): Observable<DettesFournisseur> {
    const url = `${this.baseUrl}/dettes-fournisseur/${id}`;
    
    return this.http.put<DettesFournisseur>(url, detteUpdate, this.httpOptions).pipe(
      tap(() => {
        this.loadDettes();
        this.loadFournisseurs();
      }),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 409) {
          // Duplicate numeroFacture
          return throwError(() => ({
            status: 409,
            message: error.error?.message || `Le numéro de facture ${detteUpdate.numeroFacture} existe déjà`,
            error: error.error
          }));
        }
        // Try PATCH if PUT fails
        return this.http.patch<DettesFournisseur>(url, detteUpdate, this.httpOptions).pipe(
          tap(() => {
            this.loadDettes();
            this.loadFournisseurs();
          }),
          catchError((patchError: HttpErrorResponse) => {
            if (patchError.status === 409) {
              return throwError(() => ({
                status: 409,
                message: patchError.error?.message || `Le numéro de facture ${detteUpdate.numeroFacture} existe déjà`,
                error: patchError.error
              }));
            }
            return this.handleError(patchError);
          })
        );
      })
    );
  }

  deleteDette(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/dettes-fournisseur/${id}`).pipe(
      tap(() => {
        this.loadDettes();
        this.loadFournisseurs();
      }),
      catchError(this.handleError)
    );
  }

  // Search dettes with filters including type
  searchDettes(searchParams: {
    typeDette?: string;
    estPaye?: boolean;
    fournisseurId?: string;
    dateDebut?: string;
    dateFin?: string;
    page?: number;
    size?: number;
  }): Observable<PageResponse<DettesFournisseur>> {
    let httpParams = new HttpParams();
    
    if (searchParams.typeDette) {
      httpParams = httpParams.set('typeDette', searchParams.typeDette);
    }
    if (searchParams.estPaye !== undefined) {
      httpParams = httpParams.set('estPaye', searchParams.estPaye.toString());
    }
    if (searchParams.fournisseurId) {
      httpParams = httpParams.set('fournisseurId', searchParams.fournisseurId);
    }
    if (searchParams.dateDebut) {
      httpParams = httpParams.set('dateDebut', searchParams.dateDebut);
    }
    if (searchParams.dateFin) {
      httpParams = httpParams.set('dateFin', searchParams.dateFin);
    }
    if (searchParams.page !== undefined) {
      httpParams = httpParams.set('page', searchParams.page.toString());
    }
    if (searchParams.size !== undefined) {
      httpParams = httpParams.set('size', searchParams.size.toString());
    }

    return this.http.get<PageResponse<DettesFournisseur>>(`${this.baseUrl}/dettes-fournisseur/search`, { 
      params: httpParams 
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Search dettes using POST method (legacy)
  searchDettesPost(criteria: any): Observable<DettesFournisseur[]> {
    return this.http.post<DettesFournisseur[]>(`${this.baseUrl}/dettes-fournisseur/search`, criteria, this.httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  // Paginated search for dettes
  searchDettesPaginated(criteria: any, params?: PaginationParams): Observable<PageResponse<DettesFournisseur>> {
    const searchBody = {
      ...criteria,
      page: params?.page,
      size: params?.size,
      sortBy: params?.sortBy,
      sortDirection: params?.sortDirection
    };

    return this.http.post<any>(`${this.baseUrl}/dettes-fournisseur/search`, searchBody, this.httpOptions).pipe(
      map(response => {
        // Handle if response is wrapped in an array
        const pageResponse = Array.isArray(response) && response.length > 0 ? response[0] : response;
        
        // Map pageNumber to page for consistency
        return {
          content: pageResponse.content || [],
          page: pageResponse.pageNumber ?? pageResponse.page ?? 0,
          size: pageResponse.pageSize ?? pageResponse.size ?? 10,
          totalElements: pageResponse.totalElements ?? 0,
          totalPages: pageResponse.totalPages ?? 0,
          last: pageResponse.last ?? false,
          first: pageResponse.first ?? false,
          empty: pageResponse.empty ?? true,
          sortBy: params?.sortBy,
          sortDirection: params?.sortDirection
        } as PageResponse<DettesFournisseur>;
      }),
      tap(response => {
        // Update the subject with the paginated content
        if (response.content) {
          this.dettesSubject.next(response.content);
        }
      }),
      catchError(this.handleError)
    );
  }

  // Check if numeroFacture already exists (for validation)
  checkNumeroFactureExists(numeroFacture: string, excludeDetteId?: string): Observable<boolean> {
    // Search for dettes with this numeroFacture
    return this.getAllDettes().pipe(
      map(dettes => {
        const exists = dettes.some(d => 
          d.numeroFacture === numeroFacture && 
          (!excludeDetteId || d.id !== excludeDetteId)
        );
        return exists;
      }),
      catchError(() => {
        // On error, assume it doesn't exist to allow form submission
        // The backend will still validate
        return new Observable<boolean>(observer => {
          observer.next(false);
          observer.complete();
        });
      })
    );
  }

  // Tranche Paiement operations
  createTranche(tranche: Omit<TranchePaiement, 'id'>): Observable<TranchePaiement> {
    return this.http.post<TranchePaiement>(`${this.baseUrl}/tranches-paiement`, tranche, this.httpOptions).pipe(
      tap(() => this.loadDettes()),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 400) {
          // Tranche exceeds montantTotal
          return throwError(() => ({
            status: 400,
            message: error.error?.message || 'Le total des tranches dépasse le montant total de la dette',
            error: error.error
          }));
        }
        return this.handleError(error);
      })
    );
  }

  getTranchesById(detteId: string): Observable<TranchePaiement[]> {
    const query = { dettesFournisseurId: detteId };
    return this.http.post<any>(`${this.baseUrl}/tranches-paiement/search`, query, this.httpOptions).pipe(
      map(response => {
        // Handle different response formats from backend
        if (Array.isArray(response)) {
          // If response is directly an array
          if (response.length > 0 && Array.isArray(response[0])) {
            // If nested array like [[...]]
            return response[0] as TranchePaiement[];
          }
          return response as TranchePaiement[];
        } else if (response && response.data) {
          // If response has data property
          return Array.isArray(response.data) ? response.data : [];
        }
        return [];
      }),
      catchError(error => {
        return this.handleError(error);
      })
    );
  }

  // Get tranche by ID
  getTrancheById(id: string): Observable<TranchePaiement> {
    return this.http.get<any>(`${this.baseUrl}/tranches-paiement/${id}`).pipe(
      map(response => {
        if (Array.isArray(response) && response.length > 0) {
          return Array.isArray(response[0]) ? response[0][0] : response[0];
        }
        return response;
      }),
      catchError(this.handleError)
    );
  }

  // Search/filter tranches with multiple criteria
  searchTranches(query: TranchePaiementQuery): Observable<TranchePaiement[]> {
    return this.http.post<any>(`${this.baseUrl}/tranches-paiement/search`, query, this.httpOptions).pipe(
      map(response => {
        if (Array.isArray(response)) {
          if (response.length > 0 && Array.isArray(response[0])) {
            return response[0] as TranchePaiement[];
          }
          return response as TranchePaiement[];
        }
        return [];
      }),
      catchError(this.handleError)
    );
  }

  // Mark tranche as paid
  payTranche(trancheId: string): Observable<TranchePaiement> {
    return this.http.put<TranchePaiement>(`${this.baseUrl}/tranches-paiement/${trancheId}/payer`, {}, this.httpOptions).pipe(
      tap(() => this.loadDettes()),
      catchError(this.handleError)
    );
  }

  updateTranche(id: string, tranche: Partial<TranchePaiement>): Observable<TranchePaiement> {
    return this.http.put<TranchePaiement>(`${this.baseUrl}/tranches-paiement/${id}`, tranche, this.httpOptions).pipe(
      tap(() => this.loadDettes()),
      catchError(this.handleError)
    );
  }

  deleteTranche(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/tranches-paiement/${id}`).pipe(
      tap(() => this.loadDettes()),
      catchError(this.handleError)
    );
  }

  // Calculate available amount for new tranches
  calculateAvailableAmount(detteId: string, montantTotal: number): Observable<number> {
    return this.getTranchesById(detteId).pipe(
      map(tranches => {
        const totalTranches = tranches.reduce((sum, t) => sum + t.montant, 0);
        return Math.max(0, montantTotal - totalTranches);
      }),
      catchError(() => {
        // If error fetching tranches, return full montantTotal as available
        return new Observable<number>(observer => {
          observer.next(montantTotal);
          observer.complete();
        });
      })
    );
  }

  // Utility methods
  private loadFournisseurs(): void {
    this.getAllFournisseurs().subscribe();
  }

  private loadDettes(): void {
    this.getAllDettes().subscribe();
  }

  getStats(): Observable<FournisseurStats> {
    const fournisseurs = this.fournisseursSubject.value;
    const dettes = this.dettesSubject.value;
    
    const stats: FournisseurStats = {
      totalFournisseurs: fournisseurs.length,
      fournisseursActifs: fournisseurs.filter(f => f.totalDettes && f.totalDettes.parsedValue > 0).length,
      totalDettes: dettes.length,
      dettesNonPayees: dettes.filter(d => !d.estPaye).length,
      dettesEnRetard: dettes.filter(d => !d.estPaye && new Date(d.datePaiementPrevue) < new Date()).length,
      montantDettesNonPayees: dettes.filter(d => !d.estPaye).reduce((sum, d) => sum + d.montantDu, 0)
    };

    return new Observable(observer => {
      observer.next(stats);
      observer.complete();
    });
  }

  // Debt Report - Get comprehensive debt analysis
  getRapportDettes(params?: RapportFilterParams): Observable<RapportDettesEntreprise> {
    let httpParams = new HttpParams();
    
    if (params?.dateDebut) {
      httpParams = httpParams.set('dateDebut', params.dateDebut);
    }
    if (params?.dateFin) {
      httpParams = httpParams.set('dateFin', params.dateFin);
    }
    if (params?.inclureDettesPayees !== undefined) {
      httpParams = httpParams.set('inclureDettesPayees', params.inclureDettesPayees.toString());
    }
    if (params?.fournisseurIds && params.fournisseurIds.length > 0) {
      // Send the first fournisseur ID (single selection)
      httpParams = httpParams.set('fournisseurId', params.fournisseurIds[0]);
    }

    return this.http.get<RapportDettesEntreprise>(`${this.baseUrl}/dettes-fournisseur/rapport`, { 
      params: httpParams 
    }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: any) {
    return throwError(() => error);
  }
}
