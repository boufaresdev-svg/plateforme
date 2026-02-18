import { Client } from "../client/client.model";
import { PrioriteProjet, StatutProjet, TypeProjet } from "./enum.model";
import { Phase } from "./Phase.model";
import { TacheProjet } from "./TacheProjet.model";

export interface Projet {
  id: string;
  nom: string;
  description: string;
  type: TypeProjet;
  statut: StatutProjet;
  priorite: PrioriteProjet;
  chefProjet: string;
  equipe: string[];
  client: Client;
  dateDebut: Date;
  dateFin: Date;
  dateFinPrevue: Date;
  budget: number;
  coutReel?: number;
  progression: number; // 0-100
  phases: Phase[];
  documents: string[];
  taches: TacheProjet[];
  tags: string[];
  createdAt: Date;
  updatedAt: Date;
}
