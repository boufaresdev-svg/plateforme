import { StatutProjetClient } from "./enum.model";

export interface ProjetClient {
  id: string;
  clientId: string;
  nom: string;
  description: string;
  montant: number;
  dateDebut: Date;
  dateFin?: Date;
  statut: StatutProjetClient;
}