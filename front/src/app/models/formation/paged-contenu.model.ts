import { PagedResponse } from '../pagination.model';

export interface ContenuSummary {
  idContenuDetaille: number;
  titre: string;
  methodePedagogique?: string;
  dureeTheorique?: number;
  dureePratique?: number;
  levelCount: number;
}

export type PagedContenuDetailleResponse = PagedResponse<ContenuSummary>;
