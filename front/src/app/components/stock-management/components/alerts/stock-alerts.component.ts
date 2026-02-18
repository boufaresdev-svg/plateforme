import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { 
  Alerte, 
  AlertType, 
  AlertPriority, 
  AlertStatus, 
  AlertFilter, 
  AlertStats 
} from '../../../../models/alert.model';
import { AlertService } from '../../../../services/alert.service';
import { NotificationComponent } from '../../../shared/notification/notification.component';

@Component({
  selector: 'app-stock-alerts',
  standalone: true,
  imports: [CommonModule, FormsModule, NotificationComponent],
  templateUrl: './stock-alerts.component.html',
  styleUrl: './stock-alerts.component.css'
})
export class StockAlertsComponent implements OnInit, OnDestroy {
  @Input() showHeader = true;
  @Input() maxDisplayed = 10;
  @Input() showFilters = true;
  @Input() compactMode = false;
  @Output() alertClicked = new EventEmitter<Alerte>();
  @Output() actionExecuted = new EventEmitter<{alerte: Alerte, actionId: string}>();



  alertes: Alerte[] = [];
  filteredAlertes: Alerte[] = [];
  stats: AlertStats | null = null;
  isLoading = false;
  showArchived = false;

  // Details modal
  showDetailsModal = false;
  selectedAlert: Alerte | null = null;

  // Filter properties
  filter: AlertFilter = {};
  searchTerm = '';
  selectedType: AlertType | '' = '';
  selectedPriority: AlertPriority | '' = '';
  selectedStatus: AlertStatus | '' = '';

  // Enums for template
  AlertType = AlertType;
  AlertPriority = AlertPriority;
  AlertStatus = AlertStatus;

  // Pagination
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];

  // Make Math available in template
  Math = Math;

  private destroy$ = new Subject<void>();

  constructor(private alertService: AlertService) {}

  ngOnInit(): void {
    this.applyFilters();
    this.loadStats();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadAlertes(): void {
    this.isLoading = true;
    
    this.alertService.getAlertes(this.filter)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: any) => {
          this.alertes = response.content || [];
          this.filteredAlertes = this.alertes;
          this.totalElements = response.totalElements || 0;
          this.totalPages = response.totalPages || 0;
          this.currentPage = response.number || 0;
          this.isLoading = false;
        },
        error: (error: any) => {
          console.error('Erreur lors du chargement des alertes:', error);
          this.isLoading = false;
        }
      });
  }

  loadStats(): void {
    this.alertService.getAlertStats()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (stats: AlertStats) => {
          this.stats = stats;
        },
        error: (error: any) => {
          console.error('Erreur lors du chargement des statistiques:', error);
        }
      });
  }

  applyFilters(): void {
    this.filter = {
      page: this.currentPage,
      size: this.pageSize,
      isArchived: this.showArchived
    };

    if (this.searchTerm.trim()) {
      this.filter.searchTerm = this.searchTerm.trim();
    }

    if (this.selectedType) {
      this.filter.type = [this.selectedType];
    }

    if (this.selectedPriority) {
      this.filter.priority = [this.selectedPriority];
    }

    if (this.selectedStatus) {
      this.filter.status = [this.selectedStatus];
    }

    this.loadAlertes();
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.filter.page = page;
      this.loadAlertes();
    }
  }

  onPageSizeChange(): void {
    this.pageSize = this.pageSize;
    this.currentPage = 0;
    this.filter.size = this.pageSize;
    this.filter.page = 0;
    this.loadAlertes();
  }

  get pages(): number[] {
    const pages: number[] = [];
    for (let i = 0; i < this.totalPages; i++) {
      pages.push(i);
    }
    return pages;
  }

  getPageNumbersOld(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    let startPage = Math.max(1, this.currentPage - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(this.totalPages, startPage + maxPagesToShow - 1);
    
    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(1, endPage - maxPagesToShow + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }

  onSearchChange(): void {
    this.currentPage = 0;
    this.applyFilters();
  }

  onFilterChange(): void {
    this.currentPage = 1;
    this.applyFilters();
  }

  toggleArchived(): void {
    this.showArchived = !this.showArchived;
    this.currentPage = 1;
    this.applyFilters();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedType = '';
    this.selectedPriority = '';
    this.selectedStatus = '';
    this.showArchived = false;
    this.filter = { isArchived: false };
    this.loadAlertes();
  }

  onAlertClick(alerte: Alerte): void {
    this.alertClicked.emit(alerte);
    
    // Mark as read if not already read
    if (!alerte.isRead) {
      this.markAsRead(alerte.id);
    }
  }

  markAsRead(id: string): void {
    this.alertService.markAsRead(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (success: boolean) => {
          if (success) {
            this.loadAlertes();
          }
        },
        error: (error: any) => {
          console.error('Erreur lors du marquage comme lu:', error);
        }
      });
  }

  markAsUnread(id: string): void {
    this.alertService.markAsUnread(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (success: boolean) => {
          if (success) {
            this.loadAlertes();
          }
        },
        error: (error: any) => {
          console.error('Erreur lors du marquage comme non lu:', error);
        }
      });
  }

  acknowledgeAlert(id: string): void {
    this.alertService.acknowledgeAlerte(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (success: boolean) => {
          if (success) {
            this.loadAlertes();
          }
        },
        error: (error: any) => {
          console.error('Erreur lors de l\'accusé de réception:', error);
        }
      });
  }

  resolveAlert(id: string): void {
    this.alertService.resolveAlerte(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (success: boolean) => {
          if (success) {
            this.loadAlertes();
          }
        },
        error: (error: any) => {
          console.error('Erreur lors de la résolution:', error);
        }
      });
  }

  dismissAlert(id: string): void {
    this.alertService.dismissAlerte(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (success: boolean) => {
          if (success) {
            this.loadAlertes();
          }
        },
        error: (error: any) => {
          console.error('Erreur lors du rejet:', error);
        }
      });
  }

  archiveAlert(id: string): void {
    this.alertService.archiveAlerte(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (success: boolean) => {
          if (success) {
            this.loadAlertes();
          }
        },
        error: (error: any) => {
          console.error('Erreur lors de l\'archivage:', error);
        }
      });
  }

  executeAction(alerte: Alerte, actionId: string): void {
    this.alertService.executeAction(alerte.id, actionId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (success: boolean) => {
          if (success) {
            this.actionExecuted.emit({ alerte, actionId });
            this.loadAlertes();
          }
        },
        error: (error: any) => {
          console.error('Erreur lors de l\'exécution de l\'action:', error);
        }
      });
  }

  refresh(): void {
    this.loadAlertes();
    this.loadStats();
  }

  getAlertIcon(type: AlertType): string {
    switch (type) {
      case AlertType.RUPTURE:
        return '❌';
      case AlertType.STOCK_FAIBLE:
        return '⚠️';
      case AlertType.EXPIRATION:
        return '📅';
      case AlertType.SEUIL_CRITIQUE:
        return '🚨';
      case AlertType.MOUVEMENT_SUSPECT:
        return '🔍';
      case AlertType.SUCCESS:
        return '✅';
      case AlertType.INFO:
        return 'ℹ️';
      case AlertType.WARNING:
        return '⚠️';
      default:
        return '📢';
    }
  }

  getAlertClass(alerte: Alerte): string {
    const classes = ['alert-item'];
    
    classes.push(`alert-${alerte.priority}`);
    classes.push(`alert-${alerte.status}`);
    classes.push(`alert-${alerte.type.replace('_', '-')}`);
    
    if (!alerte.isRead) {
      classes.push('alert-unread');
    }
    
    if (this.compactMode) {
      classes.push('alert-compact');
    }
    
    return classes.join(' ');
  }

  getPriorityLabel(priority: AlertPriority): string {
    switch (priority) {
      case AlertPriority.CRITICAL:
        return 'Critique';
      case AlertPriority.HIGH:
        return 'Élevée';
      case AlertPriority.MEDIUM:
        return 'Moyenne';
      case AlertPriority.LOW:
        return 'Faible';
      default:
        return 'Inconnue';
    }
  }

  getStatusLabel(status: AlertStatus): string {
    switch (status) {
      case AlertStatus.ACTIVE:
        return 'Active';
      case AlertStatus.ACKNOWLEDGED:
        return 'Accusée';
      case AlertStatus.RESOLVED:
        return 'Résolue';
      case AlertStatus.DISMISSED:
        return 'Rejetée';
      default:
        return 'Inconnue';
    }
  }

  getTypeLabel(type: AlertType): string {
    switch (type) {
      case AlertType.RUPTURE:
        return 'Rupture';
      case AlertType.STOCK_FAIBLE:
        return 'Stock faible';
      case AlertType.STOCK_ELEVE:
        return 'Stock élevé';
      case AlertType.EXPIRATION:
        return 'Expiration';
      case AlertType.SEUIL_CRITIQUE:
        return 'Seuil critique';
      case AlertType.MOUVEMENT_SUSPECT:
        return 'Mouvement suspect';
      case AlertType.SUCCESS:
        return 'Succès';
      case AlertType.INFO:
        return 'Information';
      case AlertType.WARNING:
        return 'Avertissement';
      case AlertType.SYSTEM:
        return 'Système';
      default:
        return 'Inconnue';
    }
  }

  formatRelativeTime(date: Date): string {
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / (1000 * 60));
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

    if (diffMins < 1) {
      return 'À l\'instant';
    } else if (diffMins < 60) {
      return `Il y a ${diffMins} min`;
    } else if (diffHours < 24) {
      return `Il y a ${diffHours}h`;
    } else if (diffDays === 1) {
      return 'Hier';
    } else if (diffDays < 7) {
      return `Il y a ${diffDays} jours`;
    } else {
      return date.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      });
    }
  }

  trackByAlertId(index: number, alerte: Alerte): string {
    return alerte.id;
  }

  // New methods for the clean component
  getTypeBadgeClass(type: AlertType): string {
    return `type-${type.toLowerCase().replace('_', '-')}`;
  }

  getQuantityClass(alerte: Alerte): string {
    if (alerte.quantiteActuelle === undefined) return '';
    
    if (alerte.seuilCritique && alerte.quantiteActuelle <= alerte.seuilCritique) {
      return 'critical';
    } else if (alerte.seuilMinimum && alerte.quantiteActuelle <= alerte.seuilMinimum) {
      return 'low';
    }
    return 'normal';
  }

  viewAlert(alerte: Alerte): void {
    this.selectedAlert = alerte;
    this.showDetailsModal = true;
    this.alertClicked.emit(alerte);
    
    // Mark as read if not already read
    if (!alerte.isRead) {
      this.markAsRead(alerte.id);
    }
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedAlert = null;
  }
}