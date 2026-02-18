import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StockLot, StockLotMouvement, Stock } from '../../../../models/stock.model';
import { StockLotService } from '../../../../services/stock-lot.service';
import { NotificationService } from '../../../../services/notification.service';

@Component({
  selector: 'app-stock-lots',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './stock-lots.component.html',
  styleUrls: ['./stock-lots.component.css']
})
export class StockLotsComponent implements OnInit {
  @Input() stock: Stock | null = null;
  
  lots: StockLot[] = [];
  selectedLot: StockLot | null = null;
  lotHistory: StockLotMouvement[] = [];
  isLoading = false;
  isHistoryLoading = false;
  showHistoryModal = false;

  // Filters
  showActiveOnly = true;
  showExpiring = false;

  constructor(
    private stockLotService: StockLotService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadStockLots();
  }

  loadStockLots(): void {
    if (!this.stock) return;

    const articleId = this.stock.articleId || this.stock.article?.id;
    if (!articleId) return;

    this.isLoading = true;

    this.stockLotService.getStockLotsByArticle(articleId).subscribe({
      next: (lots) => {
        this.lots = lots;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des lots:', error);
        this.notificationService.error('Erreur lors du chargement des lots');
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.lots];

    if (this.showActiveOnly) {
      filtered = filtered.filter(lot => lot.estActif && lot.quantiteDisponible > 0);
    }

    if (this.showExpiring) {
      const thirtyDaysFromNow = new Date();
      thirtyDaysFromNow.setDate(thirtyDaysFromNow.getDate() + 30);
      filtered = filtered.filter(lot => 
        lot.dateExpiration && new Date(lot.dateExpiration) <= thirtyDaysFromNow
      );
    }

    this.lots = filtered;
  }

  viewLotHistory(lot: StockLot): void {
    this.selectedLot = lot;
    this.showHistoryModal = true;
    this.isHistoryLoading = true;

    this.stockLotService.getStockLotHistory(lot.id).subscribe({
      next: (history) => {
        this.lotHistory = history;
        this.isHistoryLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement de l\'historique:', error);
        this.notificationService.error('Erreur lors du chargement de l\'historique');
        this.isHistoryLoading = false;
      }
    });
  }

  closeHistoryModal(): void {
    this.showHistoryModal = false;
    this.selectedLot = null;
    this.lotHistory = [];
  }

  getTotalQuantity(): number {
    return this.stockLotService.getTotalQuantity(this.lots);
  }

  getTotalValue(): number {
    return this.stockLotService.getTotalValue(this.lots);
  }

  getAveragePurchasePrice(): number {
    return this.stockLotService.getAveragePurchasePrice(this.lots);
  }

  isExpiringSoon(lot: StockLot): boolean {
    if (!lot.dateExpiration) return false;
    const expirationDate = new Date(lot.dateExpiration);
    const thirtyDaysFromNow = new Date();
    thirtyDaysFromNow.setDate(thirtyDaysFromNow.getDate() + 30);
    return expirationDate <= thirtyDaysFromNow;
  }

  isExpired(lot: StockLot): boolean {
    if (!lot.dateExpiration) return false;
    return new Date(lot.dateExpiration) < new Date();
  }

  getLotStatusClass(lot: StockLot): string {
    if (!lot.estActif) return 'status-inactive';
    if (this.isExpired(lot)) return 'status-expired';
    if (this.isExpiringSoon(lot)) return 'status-expiring';
    if (lot.quantiteDisponible === 0) return 'status-empty';
    return 'status-active';
  }

  getLotStatus(lot: StockLot): string {
    if (!lot.estActif) return 'Inactif';
    if (this.isExpired(lot)) return 'Expiré';
    if (this.isExpiringSoon(lot)) return 'Expire bientôt';
    if (lot.quantiteDisponible === 0) return 'Épuisé';
    return 'Actif';
  }

  getMovementTypeLabel(type: string): string {
    const types: { [key: string]: string } = {
      'ENTREE': 'Entrée',
      'SORTIE': 'Sortie',
      'AJUSTEMENT': 'Ajustement',
      'TRANSFERT': 'Transfert',
      'RESERVATION': 'Réservation',
      'LIBERATION': 'Libération'
    };
    return types[type] || type;
  }

  getMovementTypeClass(type: string): string {
    const classes: { [key: string]: string } = {
      'ENTREE': 'movement-entree',
      'SORTIE': 'movement-sortie',
      'AJUSTEMENT': 'movement-ajustement',
      'TRANSFERT': 'movement-transfert',
      'RESERVATION': 'movement-reservation',
      'LIBERATION': 'movement-liberation'
    };
    return classes[type] || 'movement-default';
  }

  onFilterChange(): void {
    this.loadStockLots();
  }

  downloadInvoice(lot: StockLot): void {
    if (lot.factureUrl) {
      window.open(lot.factureUrl, '_blank');
    }
  }
}
