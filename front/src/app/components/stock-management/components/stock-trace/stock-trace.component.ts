import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StockTrace, StockActionType, StockTraceFilter, StockTraceStats } from '../../../../models/stock-trace.model';
import { StockTraceService } from '../../../../services/stock-trace.service';
import { NotificationComponent } from '../../../shared/notification/notification.component';

@Component({
  selector: 'app-stock-trace',
  standalone: true,
  imports: [CommonModule, FormsModule, NotificationComponent],
  templateUrl: './stock-trace.component.html',
  styleUrls: ['./stock-trace.component.css']
})
export class StockTraceComponent implements OnInit {
  @Input() stockId?: string; // If provided, filter traces for specific stock item

  traces: StockTrace[] = [];
  filteredTraces: StockTrace[] = [];
  stats?: StockTraceStats;
  
  filter: StockTraceFilter = {};
  actionTypes = Object.values(StockActionType);
  
  // Pagination
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];
  Math = Math;

  constructor(private stockTraceService: StockTraceService) {}

  ngOnInit(): void {
    // Set filter if stockId is provided (for modal view)
    if (this.stockId) {
      this.filter.stockId = this.stockId;
    }
    this.loadTraces();
    // Stats will be loaded by loadTraces(), no need to call twice
  }

  loadTraces(): void {
    this.stockTraceService.getStockTraces(this.filter).subscribe((traces: StockTrace[]) => {
      this.traces = traces;
      this.applyFilters();
    });
    // Reload stats with the current filter
    this.loadStats();
  }

  loadStats(): void {
    this.stockTraceService.getStockTraceStats(this.filter).subscribe((stats: StockTraceStats) => {
      this.stats = stats;
    });
  }

  applyFilters(): void {
    let filtered = [...this.traces];

    // Apply stock-specific filter if provided
    if (this.stockId) {
      filtered = filtered.filter(trace => trace.stockId === this.stockId);
    }

    // Apply date filters
    if (this.filter.dateDebut) {
      filtered = filtered.filter(trace => 
        new Date(trace.performedAt) >= new Date(this.filter.dateDebut!)
      );
    }

    if (this.filter.dateFin) {
      filtered = filtered.filter(trace => 
        new Date(trace.performedAt) <= new Date(this.filter.dateFin!)
      );
    }

    // Apply action type filter
    if (this.filter.actionType) {
      filtered = filtered.filter(trace => trace.actionType === this.filter.actionType);
    }

    // Apply user filter
    if (this.filter.utilisateur) {
      filtered = filtered.filter(trace => 
        trace.userPerformedBy.toLowerCase().includes(this.filter.utilisateur!.toLowerCase())
      );
    }

    // Apply warehouse filter
    if (this.filter.entrepot) {
      filtered = filtered.filter(trace => 
        trace.entrepot.toLowerCase().includes(this.filter.entrepot!.toLowerCase())
      );
    }

    // Sort by date (most recent first)
    filtered.sort((a, b) => 
      new Date(b.performedAt).getTime() - new Date(a.performedAt).getTime()
    );

    this.filteredTraces = filtered;
    this.updatePagination();
  }

  resetFilters(): void {
    this.filter = {};
    if (this.stockId) {
      this.filter.stockId = this.stockId;
    }
    this.loadTraces();
  }

  updatePagination(): void {
    this.totalElements = this.filteredTraces.length;
    this.totalPages = Math.ceil(this.totalElements / this.pageSize);
    if (this.currentPage >= this.totalPages && this.totalPages > 0) {
      this.currentPage = this.totalPages - 1;
    }
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
    }
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.updatePagination();
  }

  get pages(): number[] {
    const pages: number[] = [];
    for (let i = 0; i < this.totalPages; i++) {
      pages.push(i);
    }
    return pages;
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
    }
  }

  get paginatedTraces(): StockTrace[] {
    const startIndex = this.currentPage * this.pageSize;
    return this.filteredTraces.slice(startIndex, startIndex + this.pageSize);
  }

  getActionClass(actionType: StockActionType): string {
    return actionType.toLowerCase().replace(/[^a-z]/g, '');
  }

  getQuantityClass(quantity: number): string {
    return quantity > 0 ? 'positive' : quantity < 0 ? 'negative' : '';
  }

  getStockName(stockId: string): string {
    // This should be replaced with actual stock lookup
    return `Article ${stockId}`;
  }

  exportTraces(): void {
    // Implementation for exporting traces to CSV/Excel
  }
}