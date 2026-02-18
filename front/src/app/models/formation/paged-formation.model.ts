import { PagedResponse } from '../pagination.model';

export interface FormationSummary {
  idFormation: number;
  theme: string;
  descriptionTheme?: string;
  nombreHeures?: number;
  prix?: number;
  nombreMax?: number;
  niveau?: string;
  statut?: string;
}

export type PagedFormationResponse = PagedResponse<FormationSummary>;
