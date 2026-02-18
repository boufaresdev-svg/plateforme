import { Employee } from "./Employee.model";
import { FichePaie } from "./FichePaie.model";

export interface Prime {
  id: string;
  libelle: string;
  montant: number;
  nombrePoints: number;
  description: string;
  fichePaie: FichePaie;
  employee: Employee;
  createdAt: Date;
  updatedAt: Date;
}