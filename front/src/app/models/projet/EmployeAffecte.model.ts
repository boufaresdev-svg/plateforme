import { Employee } from "../rh/Employee.model";
import { RoleProjet, StatutAffectation } from "./enum.model";
import { Mission } from "./Mission.model";
import { Tache } from "./Tache.model";

export interface EmployeAffecte {
  id: string;
  employeId: string;   
  role: RoleProjet;
  dateAffectation: Date;
  tauxHoraire?: number;
  heuresAllouees?: number;
  heuresRealisees?: number;
  statut: StatutAffectation;
  employee?:Employee;
  mission ?: Mission;
  tache ?:Tache;
  missionId?:string;
  tacheId?: string;
}
