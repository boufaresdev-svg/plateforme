import { Apprenant } from "./Apprenant.model";
import { ContenuJourFormation } from "./ContenuJourFormation.model";
import { PlanFormation } from "./PlanFormation.model";

export interface Evaluation {

  idEvaluation?: number;
  type: string;
  date: Date | string;   
  score: number;    
  description?: string;
  apprenantDTO?: Apprenant;
  planFormation?: PlanFormation;
  contenuJourFormation?: ContenuJourFormation;
  idApprenant: number;
  idPlanFormation?: number;
  idContenuJourFormation: number;
}
