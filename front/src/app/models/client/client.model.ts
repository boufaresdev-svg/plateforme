import { ContactClient } from "./ContactClient.model";
import { PrioriteClient, SecteurClient, StatutClient, TypeClient } from "./enum.model";
import { FactureClient } from "./FactureClient.model";
import { InteractionClient } from "./InteractionClient.model";
import { PaiementClient } from "./PaiementClient.model";
import { PreferencesClient } from "./PreferencesClient.model";
import { ProjetClient } from "./ProjetClient.model";

export interface Client {
  id: string;
  nom: string;
  prenom?: string;
  raisonSociale?: string;
  type: TypeClient;
  secteur: SecteurClient;
  statut: StatutClient;
  priorite: PrioriteClient;
  siret?: string;
  adresse: string;
  ville: string;
  codePostal: string;
  pays: string;
  telephone: string;
  email: string;
  siteWeb?: string;
  contactPrincipal?: ContactClient;
  localisation: string;
  identifiantFiscal: string;
  rib: string;
  pointsFidelite: number;
  contacts: ContactClient[];
  projets: ProjetClient[];
  factures: FactureClient[];
  paiements: PaiementClient[];
  interactions: InteractionClient[];
  preferences: PreferencesClient;
  documents: string[];
  tags: string[];
  dateCreation: Date;
  derniereInteraction: Date;
  chiffreAffaires: number;
  createdAt: Date;
  updatedAt: Date;
}











