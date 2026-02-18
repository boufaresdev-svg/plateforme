import { Apprenant } from "./Apprenant.model";
import { Certificat } from "./Certificat.model";
import { PlanFormation } from "./PlanFormation.model";

export interface Examen {
  idExamen?: number;
  type: string;
  date: string;
  description?: string;
  score?: number;
  idApprenant?: number;
  idPlanFormation?: number;
  apprenant?: Apprenant;
  planFormation?: PlanFormation
  certificat?: Certificat;

}
