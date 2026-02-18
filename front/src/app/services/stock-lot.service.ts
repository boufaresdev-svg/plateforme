import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StockLot, StockLotMouvement, StockLotQuery, StockLotMouvementQuery } from '../models/stock.model';
import { environment } from '../../environment/environement';

@Injectable({
  providedIn: 'root'
})
export class StockLotService {
  private apiUrl = `${environment.apiUrl}/stock/lots`;

  constructor(private http: HttpClient) {}

  /**
   * Get all stock lots with optional filters
   */
  getStockLots(query?: StockLotQuery): Observable<StockLot[]> {
    let params = new HttpParams();
    
    if (query) {
      if (query.articleId) params = params.set('articleId', query.articleId);
      if (query.entrepotId) params = params.set('entrepotId', query.entrepotId);
      if (query.estActif !== undefined) params = params.set('estActif', query.estActif.toString());
      if (query.availableOnly) params = params.set('availableOnly', 'true');
      if (query.expiringBefore) {
        const date = query.expiringBefore instanceof Date 
          ? query.expiringBefore.toISOString().split('T')[0]
          : query.expiringBefore;
        params = params.set('expiringBefore', date);
      }
    }

    return this.http.get<StockLot[]>(this.apiUrl, { params });
  }

  /**
   * Get stock lot by ID
   */
  getStockLotById(id: string): Observable<StockLot> {
    return this.http.get<StockLot>(`${this.apiUrl}/${id}`);
  }

  /**
   * Get stock lots for a specific article
   */
  getStockLotsByArticle(articleId: string): Observable<StockLot[]> {
    return this.http.get<StockLot[]>(`${this.apiUrl}/article/${articleId}`);
  }

  /**
   * Get available stock lots for article in warehouse (for stock exit selection)
   */
  getAvailableStockLots(articleId: string, entrepotId: string): Observable<StockLot[]> {
    return this.http.get<StockLot[]>(`${this.apiUrl}/article/${articleId}/entrepot/${entrepotId}`);
  }

  /**
   * Get expiring stock lots before a specific date
   */
  getExpiringLots(beforeDate: Date): Observable<StockLot[]> {
    const dateStr = beforeDate.toISOString().split('T')[0];
    return this.http.get<StockLot[]>(`${this.apiUrl}/expiring?beforeDate=${dateStr}`);
  }

  /**
   * Get movement history for a specific stock lot
   */
  getStockLotHistory(lotId: string): Observable<StockLotMouvement[]> {
    return this.http.get<StockLotMouvement[]>(`${this.apiUrl}/${lotId}/history`);
  }

  /**
   * Get all lot movements for an article
   */
  getArticleLotHistory(articleId: string): Observable<StockLotMouvement[]> {
    return this.http.get<StockLotMouvement[]>(`${this.apiUrl}/history/article/${articleId}`);
  }

  /**
   * Calculate total quantity for article in warehouse from all batches
   */
  getTotalQuantity(lots: StockLot[]): number {
    return lots.reduce((total, lot) => total + lot.quantiteActuelle, 0);
  }

  /**
   * Calculate total available quantity (not reserved)
   */
  getTotalAvailableQuantity(lots: StockLot[]): number {
    return lots.reduce((total, lot) => total + lot.quantiteDisponible, 0);
  }

  /**
   * Calculate total value of all lots
   */
  getTotalValue(lots: StockLot[]): number {
    return lots.reduce((total, lot) => total + lot.valeurTotale, 0);
  }

  /**
   * Get average purchase price across all lots
   */
  getAveragePurchasePrice(lots: StockLot[]): number {
    if (lots.length === 0) return 0;
    const totalQuantity = this.getTotalQuantity(lots);
    if (totalQuantity === 0) return 0;
    const totalValue = lots.reduce((total, lot) => 
      total + (lot.quantiteActuelle * lot.prixAchatUnitaire), 0);
    return totalValue / totalQuantity;
  }

  /**
   * Format lot display name for dropdown
   */
  formatLotDisplayName(lot: StockLot): string {
    const date = new Date(lot.dateAchat).toLocaleDateString('fr-FR');
    return `${lot.numeroLot} - ${date} (${lot.quantiteDisponible} disponibles) - ${lot.prixVenteUnitaire} DT`;
  }

  /**
   * Upload invoice file for stock lot
   * @param file The file to upload (PDF, JPG, or PNG)
   * @returns Observable with uploaded file URL
   */
  uploadInvoice(file: File): Observable<{url: string, filename: string, originalFilename: string}> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<{url: string, filename: string, originalFilename: string}>(
      `${this.apiUrl}/upload-invoice`, 
      formData
    );
  }

  /**
   * Get full download URL for an invoice
   * @param relativeUrl The relative URL returned from upload (e.g., /api/stock/lots/invoices/filename.pdf)
   * @returns Full URL for downloading the invoice
   */
  getInvoiceDownloadUrl(relativeUrl: string): string {
    if (relativeUrl.startsWith('http')) {
      return relativeUrl;
    }
    return `${environment.apiUrl.replace('/api', '')}${relativeUrl}`;
  }
}
