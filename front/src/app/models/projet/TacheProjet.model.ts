import { StatutTache } from "./enum.model";

export interface TacheProjet {
  id: string;
  projetId: string;
  nom: string;
  description: string;
  assignee: string;
  statut: StatutTache;
  dateDebut: Date;
  dateFin: Date;
  progression: number;
  dependances: string[];
}