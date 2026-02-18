import { SituationFamiliale, StatutEmployee, TypePieceIdentite } from "./enum.model";
import { Congee } from "./Congee.model";
import { FichePaie } from "./FichePaie.model";
import { Retenue } from "./Retenue.model";
import { Prime } from "./Prime.model";

export interface Employee {
 id: string;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  poste: string;
  departement: string;
  dateEmbauche: Date;
  salaire: number;
  soldeConges: number;
  congesUtilises: number;
  soldePoints: number;
  pointsDemandesParAn: number;
  adresse: string;
  typePieceIdentite: TypePieceIdentite; // enum
  numeroPieceIdentite: string;
 
  nombreEnfants: number;
  statut: StatutEmployee; // enum
  conges: Congee[];
  fichesPaie: FichePaie[];
  retenues: Retenue[];
  primes: Prime[];
  createdAt: Date;
  updatedAt: Date;
}