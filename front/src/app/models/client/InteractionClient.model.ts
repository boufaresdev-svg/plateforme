import { TypeInteraction } from "./enum.model";

export interface InteractionClient {
  id: string;
  clientId: string;
  type: TypeInteraction;
  sujet: string;
  description: string;
  date: Date;
  responsable: string;
  suiviRequis: boolean;
  dateSuivi?: Date;
}