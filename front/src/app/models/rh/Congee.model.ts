import { StatutConge, TypeConge } from "./enum.model";
import { Employee } from "./Employee.model";

export interface Congee {
  id: string;
  type: TypeConge;
  dateDebut: Date;
  dateFin: Date;
  nombreJours: number;
  motif: string;
  statut: StatutConge;
  dateValidation?: Date;
  validePar?: string;
  employee: Employee;
}