import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { PlanFormation, StatutFormation } from '../../models/formation/PlanFormation.model';
import { environment } from '../../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class PlanFormationService {

  private baseUrl = `${environment.formationUrl}/planformations`; 
  

  constructor(private http: HttpClient) {}

  getAllPlans(): Observable<PlanFormation[]> {
    return this.http.get<any[]>(this.baseUrl).pipe(map((items) => items.map(p => this.mapPlan(p))));
  }

  getPlanById(id: number): Observable<PlanFormation> {
    return this.http.get<any>(`${this.baseUrl}/${id}`).pipe(map((data) => this.mapSingleOrFirst(data)));
  }

  createPlan(plan: PlanFormation): Observable<PlanFormation> {
    return this.http.post<any>(this.baseUrl, this.toCommand(plan)).pipe(map((res) => ({ ...plan, idPlanFormation: res?.idPlanFormation || res?.id })));
  }

  updatePlan(id: number, plan: PlanFormation): Observable<PlanFormation> {
    return this.http.put<any>(`${this.baseUrl}/${id}`, this.toCommand(plan)).pipe(
      map(() => ({ ...plan, idPlanFormation: id } as PlanFormation))
    );
  }

  deletePlan(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getPlansByFormation(idFormation: number): Observable<PlanFormation[]> {
    return this.http.get<any>(`${this.baseUrl}/formation/${idFormation}`).pipe(
      map((response) => {
        console.log('RAW HTTP response from getPlansByFormation:', response);
        console.log('Type of response:', typeof response, 'IsArray:', Array.isArray(response));
        
        // The mediator wraps results in an extra array, so we need to unwrap
        let items = response;
        if (Array.isArray(response) && response.length > 0 && Array.isArray(response[0])) {
          console.log('Unwrapping nested array');
          items = response[0]; // Get the first element which is the actual array
        }
        
        const mapped = items.map((p: any) => this.mapPlan({ ...p, idFormation }));
        console.log('After mapping in service:', mapped);
        return mapped;
      })
    );
  }

  private mapSingleOrFirst(data: any): PlanFormation {
    const arr = Array.isArray(data) ? data : [];
    const first = Array.isArray(data) ? data[0] : data;
    if (arr.length > 0) {
      return this.mapPlan(first);
    }
    return this.mapPlan(first);
  }

  private mapPlan(plan: any): PlanFormation {
    const status = plan?.statusFormation as StatutFormation | string | undefined;
    return {
      idPlanFormation: plan?.idPlanFormation ?? plan?.id,
      titre: plan?.titre,
      description: plan?.description,
      dateLancement: plan?.dateLancement || plan?.date_lancement,
      dateFinReel: plan?.dateFinReel || plan?.date_fin_reel,
      dateDebut: plan?.dateDebut || plan?.date_debut,
      dateFin: plan?.dateFin || plan?.date_fin,
      statusFormation: status as StatutFormation,
      idFormation: plan?.formationId || plan?.idFormation || plan?.formation?.idFormation,
      idFormateur: plan?.formateurId || plan?.idFormateur || plan?.formateur?.idFormateur,
      formation: plan?.formation,
      formateur: plan?.formateur,
      apprenants: plan?.apprenants,
      evaluations: plan?.evaluations,
      nombreJours: plan?.nombreJours
    } as PlanFormation;
  }

  private toCommand(plan: PlanFormation) {
    return {
      titre: plan.titre,
      description: plan.description,
      dateLancement: plan.dateLancement,
      dateDebut: plan.dateDebut,
      dateFin: plan.dateFin,
      dateFinReel: plan.dateFinReel,
      statusFormation: plan.statusFormation,
      idFormation: plan.idFormation ?? plan.formation?.idFormation,
      idFormateur: plan.idFormateur ?? plan.formateur?.idFormateur,
      nombreJours: plan.nombreJours
    };
  }



  
}
