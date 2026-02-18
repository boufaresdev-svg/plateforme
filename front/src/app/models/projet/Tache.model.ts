import { CommentaireTache } from "./CommentaireTache.model";
import { EmployeAffecte } from "./EmployeAffecte.model";
import { PrioriteTache, StatutTache } from "./enum.model";
import { Charge } from "./Charge.model";
import { Mission } from "./Mission.model";
import { Employee } from "../rh/Employee.model";


interface Livrable {
  id: string;
  nom: string;
  description?: string;
  dateLivraison?: Date;
}

interface Probleme {
  id: string;
  nom: string;
  description?: string;
  dateDetection?: Date;
}

export interface Tache {
  id: string;
  missionId: string;
  nom: string;
  description: string;
  statut: StatutTache;
  priorite: PrioriteTache;
  dateDebut: Date;
  dateFin: Date;
  dureeEstimee: number; // en heures
  dureeReelle?: number; // en heures
  progression: number; // 0-100
  employesAffectes: EmployeAffecte[];
  charges: Charge[];
  dependances: string[]; // IDs des tâches dépendantes
  commentaires: CommentaireTache[];
  fichiers: string[];
  problemes: Probleme[];
  livrables: Livrable[];
  createdAt: Date;
  updatedAt: Date;
  mission?:Mission;
  employee?:Employee;
}
