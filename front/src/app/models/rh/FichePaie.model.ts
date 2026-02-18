import { StatutFichePaie } from "./enum.model";
import { Employee } from "./Employee.model";
import { Prime } from "./Prime.model";
import { Retenue } from "./Retenue.model";

export interface FichePaie {
  id: string;
  dateEmission: Date;
  salaireDeBase: number;
  netAPayer: number;
  statut: StatutFichePaie;
  employee: Employee;
  primes: Prime[];
  retenues: Retenue[];
}