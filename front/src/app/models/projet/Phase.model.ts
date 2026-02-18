import { Mission } from "./Mission.model";
import { Projet } from "./Projet.model";
import { StatutPhase } from "./enum.model";

export interface Phase {
  id: string;
  projetId: string;
  projet: Projet;
  nom: string;
  description: string;
  ordre: number; // 1, 2, 3, 4, 5 pour les 5 phases
  statut: StatutPhase;
  dateDebut: Date;
  dateFin: Date;
  progression: number; // 0-100
  budget?: number;
  missions: Mission[];
  livrables: string[];
  createdAt: Date;
  updatedAt: Date;
}

export enum PhaseType {
  ETUDE_ANALYSE = 'Étude/Analyse',
  DEVELOPPEMENT_REALISATION = 'Développement/Réalisation',
  MISE_EN_SERVICE_DEPLOIEMENT = 'Mise en service/Déploiement',
  SUPERVISION_CONTROLE_SUIVI = 'Supervision/Contrôle/Suivi',
  CLOTURE = 'Clôture'
}