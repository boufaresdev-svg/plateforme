import { EmployeAffecte } from "./EmployeAffecte.model";
import { PrioriteMission, StatutMission } from "./enum.model";
import { Phase } from "./Phase.model";
import { Projet } from "./Projet.model";
import { Tache } from "./Tache.model";

export interface Mission {
  id: string;
  phaseId: string;
  projetId: string;
  nom: string;
  projet:Projet;
  phase:Phase;
  description: string;
  objectif: string;
  statut: StatutMission;
  priorite: PrioriteMission;
  dateDebut: Date;
  dateFin: Date;
  progression: number; // 0-100
  budget?: number;
  employesAffectes: EmployeAffecte[];
  taches: Tache[];
  dependances: string[]; // IDs des missions dépendantes
  livrables: string[];
  createdAt: Date;
  updatedAt: Date;
}
