import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environment/environement';
import { Formation, FormationStatistics } from '../../models/formation/formation.model';
import { Domaine } from '../../models/formation/Domaine.model';
import { TypeFormations } from '../../models/formation/TypeFormations.model';
import { Categorie } from '../../models/formation/Categorie.model';
import { SousCategorie } from '../../models/formation/SousCategorie.model';
import { PaginationParams, PagedResponse } from '../../models/pagination.model';
import { PagedFormationResponse } from '../../models/formation/paged-formation.model';
@Injectable({
  providedIn: 'root'
})
export class FormationService {

  private baseUrl = `${environment.formationUrl}/formations`;
  private categoriesUrl = `${environment.formationUrl}/categories`;
  private domainesUrl = `${environment.formationUrl}/domaines`;
  private typesUrl = `${environment.formationUrl}/types`;
  private sousCategoriesUrl = `${environment.formationUrl}/souscategories`;
  private objectifsGlobauxUrl = `${environment.formationUrl}/objectifsglobaux`;
  private objectifsSpecifiquesUrl = `${environment.formationUrl}/objectifsspecifiques`;
  private contenusJourUrl = `${environment.formationUrl}/contenusjour`;

  constructor(private http: HttpClient) {}

  // Normalize API responses that may be either T[] or T[][]
  private normalizeList<T>(response: any): T[] {
    if (Array.isArray(response)) {
      if (response.length && Array.isArray(response[0])) {
        return response[0] as T[];
      }
      return response as T[];
    }
    return [] as T[];
  }

  getAllFormations(): Observable<Formation[]> {
    return this.http.get<any>(this.baseUrl).pipe(
      map(res => this.normalizeList<Formation>(res))
    );
  }

  getStatistics(): Observable<FormationStatistics> {
    return this.http.get<FormationStatistics>(`${this.baseUrl}/statistics`);
  }

  getFormationsPaginated(params: PaginationParams): Observable<PagedFormationResponse> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString());
    
    if (params.sortBy) {
      httpParams = httpParams.set('sortBy', params.sortBy);
    }
    if (params.sortDirection) {
      httpParams = httpParams.set('sortDirection', params.sortDirection);
    }
    
    return this.http.get<PagedFormationResponse>(`${this.baseUrl}/paginated`, { params: httpParams });
  }

  getFormationById(id: number): Observable<Formation> {
    return this.http.get<any>(`${this.baseUrl}/${id}`).pipe(
      map(res => {
        // API returns [[{...}]] - unwrap nested arrays
        let unwrapped: Formation;
        if (Array.isArray(res)) {
          if (res.length && Array.isArray(res[0])) {
            unwrapped = res[0][0] as Formation;
          } else {
            unwrapped = res[0] as Formation;
          }
        } else {
          unwrapped = res as Formation;
        }
        return unwrapped;
      })
    );
  }

  createFormation(formation: Formation): Observable<Formation> {
    return this.http.post<Formation>(this.baseUrl, formation);
  }

  updateFormation(id: number, formation: Formation): Observable<Formation> {
    return this.http.put<Formation>(`${this.baseUrl}/${id}`, formation);
  }

  deleteFormation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  duplicateFormation(id: number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/${id}/duplicate`, {});
  }

  searchFormations(keyword: string): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.baseUrl}/search?keyword=${keyword}`);
  }

  getAllDomaines(): Observable<Domaine[]> {
    return this.http.get<any>(this.domainesUrl).pipe(
      map(res => this.normalizeList<Domaine>(res))
    );
  }

  getAllTypes(): Observable<TypeFormations[]> {
    return this.http.get<TypeFormations[]>(this.typesUrl);
  }

  getTypesByDomaine(idDomaine: number): Observable<TypeFormations[]> {
    return this.http.get<any>(`${this.typesUrl}/by-domaine/${idDomaine}`).pipe(
      map(res => this.normalizeList<TypeFormations>(res))
    );
  }

  getAllCategories(): Observable<Categorie[]> {
    return this.http.get<Categorie[]>(this.categoriesUrl);
  }

  getCategoriesByType(idType: number): Observable<Categorie[]> {
    return this.http.get<any>(`${this.categoriesUrl}/by-type/${idType}`).pipe(
      map(res => this.normalizeList<Categorie>(res))
    );
  }

  getAllSousCategories(): Observable<SousCategorie[]> {
    return this.http.get<SousCategorie[]>(this.sousCategoriesUrl);
  }

  getSousCategoriesByCategorie(idCategorie: number): Observable<SousCategorie[]> {
    return this.http.get<any>(`${this.sousCategoriesUrl}/by-categorie/${idCategorie}`).pipe(
      map(res => this.normalizeList<SousCategorie>(res))
    );
  }

  // Objectifs globaux
  getAllObjectifsGlobaux(): Observable<any[]> {
    return this.http.get<any>(this.objectifsGlobauxUrl).pipe(
      map(res => this.normalizeList<any>(res))
    );
  }

  searchObjectifsGlobaux(keyword: string): Observable<any[]> {
    return this.http.get<any>(`${this.objectifsGlobauxUrl}/search`, {
      params: { keyword }
    }).pipe(
      map(res => this.normalizeList<any>(res))
    );
  }

  searchObjectifsNotLinkedToFormation(formationId: number, keyword: string): Observable<any[]> {
    return this.http.get<any>(`${this.objectifsGlobauxUrl}/search-not-linked/${formationId}`, {
      params: { keyword }
    }).pipe(
      map(res => this.normalizeList<any>(res))
    );
  }

  getObjectifsNotLinkedToFormation(formationId: number): Observable<any[]> {
    return this.http.get<any>(`${this.objectifsGlobauxUrl}/not-linked/${formationId}`).pipe(
      map(res => this.normalizeList<any>(res))
    );
  }

  createObjectifGlobal(payload: { formationId: number; libelle: string; description?: string; tags?: string; }): Observable<any> {
    return this.http.post<any>(this.objectifsGlobauxUrl, payload);
  }

  updateObjectifGlobal(id: number, payload: { libelle: string; description?: string; tags?: string; }): Observable<any> {
    return this.http.put<any>(`${this.objectifsGlobauxUrl}/${id}`, payload);
  }

  deleteObjectifGlobal(id: number): Observable<void> {
    return this.http.delete<void>(`${this.objectifsGlobauxUrl}/${id}`);
  }

  // Objectifs spécifiques
  getAllObjectifsSpecifiques(): Observable<any[]> {
    return this.http.get<any>(this.objectifsSpecifiquesUrl).pipe(
      map(res => this.normalizeList<any>(res))
    );
  }

  searchObjectifsSpecifiques(keyword: string): Observable<any[]> {
    return this.http.get<any>(`${this.objectifsSpecifiquesUrl}/search`, {
      params: { titre: keyword }
    }).pipe(
      map(res => this.normalizeList<any>(res))
    );
  }

  copyAndLinkObjectifSpecifique(objectifSpecifiqueId: number, objectifGlobalId: number, formationId: number): Observable<void> {
    return this.http.post<void>(`${this.objectifsSpecifiquesUrl}/copy-and-link`, {
      objectifSpecifiqueId,
      objectifGlobalId,
      formationId
    });
  }

  createObjectifSpecifique(payload: { formationId: number; titre: string; description?: string; idObjectifGlobal?: number | null }): Observable<any> {
    return this.http.post<any>(this.objectifsSpecifiquesUrl, payload);
  }

  updateObjectifSpecifique(id: number, payload: { titre: string; description?: string; idObjectifGlobal?: number | null }): Observable<any> {
    return this.http.put<any>(`${this.objectifsSpecifiquesUrl}/${id}`, payload);
  }

  deleteObjectifSpecifique(id: number): Observable<void> {
    return this.http.delete<void>(`${this.objectifsSpecifiquesUrl}/${id}`);
  }

  // Contenus Jour Formation CRUD
  getAllContenusJour(): Observable<any[]> {
    return this.http.get<any>(this.contenusJourUrl).pipe(
      map(res => this.normalizeList<any>(res))
    );
  }

  getContenuJourById(id: number): Observable<any> {
    return this.http.get<any>(`${this.contenusJourUrl}/${id}`);
  }

  getContenusByObjectifSpecifique(idObjectifSpec: number): Observable<any[]> {
    return this.http.get<any>(`${this.contenusJourUrl}/by-objectif/${idObjectifSpec}`).pipe(
      map(res => this.normalizeList<any>(res))
    );
  }

  createContenuJour(payload: { contenu: string; moyenPedagogique?: string; supportPedagogique?: string; nbHeuresTheoriques: number; nbHeuresPratiques: number; idObjectifSpecifique: number; idPlanFormation?: number | null; numeroJour?: number; staff?: string; niveau?: string }): Observable<any> {
    return this.http.post<any>(this.contenusJourUrl, payload);
  }

  updateContenuJour(id: number, payload: { contenu: string; moyenPedagogique?: string; supportPedagogique?: string; nbHeuresTheoriques: number; nbHeuresPratiques: number; idObjectifSpecifique?: number; idPlanFormation?: number | null; numeroJour?: number; staff?: string; niveau?: string }): Observable<any> {
    return this.http.put<any>(`${this.contenusJourUrl}/${id}`, payload);
  }

  deleteContenuJour(id: number): Observable<void> {
    return this.http.delete<void>(`${this.contenusJourUrl}/${id}`);
  }

  searchContenusJour(params: { contenu?: string; idObjectifSpecifique?: number; isCopied?: boolean }): Observable<any[]> {
    let httpParams: any = {};
    if (params.contenu) httpParams.contenu = params.contenu;
    if (params.idObjectifSpecifique) httpParams.idObjectifSpecifique = params.idObjectifSpecifique.toString();
    if (params.isCopied !== undefined) httpParams.isCopied = params.isCopied.toString();
    
    return this.http.get<any>(`${this.contenusJourUrl}/search`, {
      params: httpParams
    }).pipe(
      map(res => this.normalizeList<any>(res))
    );
  }

  copyAndLinkContenuJour(contenuJourId: number, objectifSpecifiqueId: number, formationId: number, planFormationId: number | null, niveau?: number, niveauLabel?: string): Observable<void> {
    return this.http.post<void>(`${this.contenusJourUrl}/copy-and-link`, {
      contenuJourId,
      objectifSpecifiqueId,
      formationId,
      planFormationId,
      ...(niveau !== undefined && { niveau }),
      ...(niveauLabel !== undefined && { niveauLabel })
    });
  }

  // Link/Unlink Objectif Global to Formation
  linkObjectifGlobalToFormation(formationId: number, objectifGlobalId: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${formationId}/link-objectif-global/${objectifGlobalId}`, {});
  }

  unlinkObjectifGlobalFromFormation(formationId: number, objectifGlobalId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${formationId}/unlink-objectif-global/${objectifGlobalId}`);
  }

  // Link/Unlink Objectif Specifique to Formation
  linkObjectifSpecifiqueToFormation(formationId: number, objectifSpecifiqueId: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${formationId}/link-objectif-specifique/${objectifSpecifiqueId}`, {});
  }

  unlinkObjectifSpecifiqueFromFormation(formationId: number, objectifSpecifiqueId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${formationId}/unlink-objectif-specifique/${objectifSpecifiqueId}`);
  }

  // Upload formation image
  uploadFormationImage(formationId: number, file: File): Observable<{ imageUrl: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ imageUrl: string }>(`${this.baseUrl}/${formationId}/upload-image`, formData);
  }
}

