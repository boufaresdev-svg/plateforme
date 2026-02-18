import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, interval } from 'rxjs';
import { map, delay, switchMap, catchError } from 'rxjs/operators';
import { HttpClient, HttpParams } from '@angular/common/http';
import { 
  Alerte, 
  AlertType, 
  AlertPriority, 
  AlertStatus, 
  AlertFilter, 
  AlertStats,
  AlertAction 
} from '../models/alert.model';
import { environment } from '../../environment/environement';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private alertesSubject = new BehaviorSubject<Alerte[]>([]);
  public alertes$ = this.alertesSubject.asObservable();
  private apiUrl = `http://localhost:8080/api/stock/alertes`;
  
  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {
    // Auto-refresh alerts every 2 minutes - removed to prevent duplicate calls
    // The component will handle loading and refreshing
  }
  
  private loadAlertes(filter?: AlertFilter): Observable<any> {
    let params = new HttpParams();
    
    if (filter?.type && filter.type.length > 0) {
      params = params.set('type', filter.type[0]);
    }
    if (filter?.priority && filter.priority.length > 0) {
      params = params.set('priorite', filter.priority[0]);
    }
    if (filter?.status && filter.status.length > 0) {
      params = params.set('statut', filter.status[0]);
    }
    if (filter?.isRead !== undefined) {
      params = params.set('isRead', filter.isRead.toString());
    }
    if (filter?.page !== undefined) {
      params = params.set('page', filter.page.toString());
    }
    if (filter?.size !== undefined) {
      params = params.set('size', filter.size.toString());
    }
    if (filter?.searchTerm) {
      params = params.set('searchTerm', filter.searchTerm);
    }
    
    return this.http.get<any>(this.apiUrl, { params }).pipe(
      map(response => {
        // Handle paginated response
        if (response.content) {
          const alertes = response.content.map((dto: any) => this.mapDtoToAlerte(dto));
          this.alertesSubject.next(alertes);
          return {
            content: alertes,
            totalElements: response.totalElements,
            totalPages: response.totalPages,
            number: response.number,
            size: response.size
          };
        }
        // Handle legacy response
        const alertes = response.alertes.map((dto: any) => this.mapDtoToAlerte(dto));
        this.alertesSubject.next(alertes);
        return { content: alertes, totalElements: alertes.length, totalPages: 1, number: 0, size: alertes.length };
      }),
      catchError(error => {
        console.error('Error loading alertes:', error);
        this.notificationService.error('Erreur lors du chargement des alertes.');
        return of({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 0 });
      })
    );
  }
  
  private mapDtoToAlerte(dto: any): Alerte {
    return {
      id: dto.id,
      type: dto.type as AlertType,
      priority: dto.priorite as AlertPriority,
      status: dto.statut as AlertStatus,
      titre: dto.titre,
      message: dto.message,
      description: dto.description,
      articleId: dto.articleId,
      articleNom: dto.articleNom,
      entrepotId: dto.entrepotId,
      entrepotNom: dto.entrepotNom,
      quantiteActuelle: dto.quantiteActuelle,
      seuilMinimum: dto.seuilMinimum,
      seuilCritique: dto.seuilCritique,
      dateCreation: new Date(dto.dateCreation),
      dateModification: dto.dateModification ? new Date(dto.dateModification) : undefined,
      isRead: dto.isRead,
      isArchived: dto.isArchived,
      actions: this.generateActionsForAlerte(dto)
    };
  }
  
  private generateActionsForAlerte(dto: any): AlertAction[] {
    const actions: AlertAction[] = [];
    
    if (!dto.isRead) {
      actions.push({
        id: 'read',
        label: 'Marquer comme lu',
        action: 'mark_read',
        icon: '✓',
        color: 'success'
      });
    }
    
    if (dto.type === 'RUPTURE' || dto.type === 'STOCK_FAIBLE') {
      actions.push({
        id: 'restock',
        label: 'Réapprovisionner',
        action: 'restock',
        icon: '📦',
        color: 'primary',
        primary: true
      });
    }
    
    if (dto.statut === 'ACTIVE') {
      actions.push({
        id: 'acknowledge',
        label: 'Accuser réception',
        action: 'acknowledge',
        icon: '👍',
        color: 'info'
      });
      actions.push({
        id: 'resolve',
        label: 'Résoudre',
        action: 'resolve',
        icon: '✔',
        color: 'success'
      });
    }
    
    return actions;
  }

  getAlertes(filter?: AlertFilter): Observable<any> {
    return this.loadAlertes(filter);
  }

  getAlerteById(id: string): Observable<Alerte | undefined> {
    return this.alertes$.pipe(
      map(alertes => alertes.find(alerte => alerte.id === id))
    );
  }

  getAlertStats(filter?: AlertFilter): Observable<AlertStats> {
    // Backend uses /statistics not /stats
    const statsUrl = `${this.apiUrl}/statistics`;
    let params = new HttpParams();
    
    if (filter?.type && filter.type.length > 0) {
      params = params.set('type', filter.type[0]);
    }
    if (filter?.isArchived !== undefined) {
      params = params.set('isArchived', filter.isArchived.toString());
    }
    
    return this.http.get<{ totalActive: number, unread: number }>(statsUrl, { params }).pipe(
      map(backendStats => {
        // Map backend response to frontend AlertStats model
        const stats: AlertStats = {
          total: backendStats.totalActive || 0,
          active: backendStats.totalActive || 0,
          acknowledged: 0,
          resolved: 0,
          critical: 0,
          high: 0,
          medium: 0,
          low: 0,
          recent: backendStats.unread || 0,
          byType: {
            [AlertType.STOCK_FAIBLE]: 0,
            [AlertType.RUPTURE]: 0,
            [AlertType.STOCK_ELEVE]: 0,
            [AlertType.EXPIRATION]: 0,
            [AlertType.SEUIL_CRITIQUE]: 0,
            [AlertType.MOUVEMENT_SUSPECT]: 0,
            [AlertType.SYSTEM]: 0,
            [AlertType.WARNING]: 0,
            [AlertType.INFO]: 0,
            [AlertType.SUCCESS]: 0
          }
        };
        return stats;
      }),
      catchError((error) => {
        console.error('Error fetching alert stats:', error);
        // If stats endpoint fails, return empty stats
        const emptyStats: AlertStats = {
          total: 0,
          critical: 0,
          high: 0,
          medium: 0,
          low: 0,
          active: 0,
          acknowledged: 0,
          resolved: 0,
          recent: 0,
          byType: {
            [AlertType.STOCK_FAIBLE]: 0,
            [AlertType.RUPTURE]: 0,
            [AlertType.STOCK_ELEVE]: 0,
            [AlertType.EXPIRATION]: 0,
            [AlertType.SEUIL_CRITIQUE]: 0,
            [AlertType.MOUVEMENT_SUSPECT]: 0,
            [AlertType.SYSTEM]: 0,
            [AlertType.WARNING]: 0,
            [AlertType.INFO]: 0,
            [AlertType.SUCCESS]: 0
          }
        };
        return of(emptyStats);
      })
    );
  }

  getActiveAlertes(): Observable<Alerte[]> {
    return this.getAlertes({ status: [AlertStatus.ACTIVE], page: 0, size: 100 }).pipe(
      map(response => response.content || [])
    );
  }

  getCriticalAlertes(): Observable<Alerte[]> {
    return this.getAlertes({ 
      priority: [AlertPriority.CRITICAL], 
      status: [AlertStatus.ACTIVE],
      page: 0,
      size: 100
    }).pipe(
      map(response => response.content || [])
    );
  }

  getUnreadAlertes(): Observable<Alerte[]> {
    return this.getAlertes({ isRead: false, page: 0, size: 100 }).pipe(
      map(response => response.content || [])
    );
  }

  createAlerte(alerte: Partial<Alerte>): Observable<Alerte> {
    // This would call backend API to create alerte if needed
    // For now, just reload alertes
    return this.loadAlertes().pipe(
      map(() => alerte as Alerte)
    );
  }

  updateAlerte(id: string, updates: Partial<Alerte>): Observable<Alerte | null> {
    // This would call backend API
    // For now, just update local state temporarily
    const currentAlertes = this.alertesSubject.value;
    const alerteIndex = currentAlertes.findIndex(a => a.id === id);
    
    if (alerteIndex === -1) {
      return of(null);
    }

    const updatedAlerte = {
      ...currentAlertes[alerteIndex],
      ...updates,
      dateModification: new Date()
    };

    const updatedAlertes = [...currentAlertes];
    updatedAlertes[alerteIndex] = updatedAlerte;
    this.alertesSubject.next(updatedAlertes);

    return of(updatedAlerte).pipe(delay(300));
  }

  markAsRead(id: string): Observable<boolean> {
    return this.http.post<any>(`${this.apiUrl}/${id}/read`, {}).pipe(
      map(response => {
        if (response.success) {
          this.loadAlertes().subscribe();
          return true;
        }
        return false;
      }),
      catchError(error => {
        console.error('Error marking alert as read:', error);
        return of(false);
      })
    );
  }

  markAsUnread(id: string): Observable<boolean> {
    return this.updateAlerte(id, { isRead: false }).pipe(
      map(alerte => alerte !== null)
    );
  }

  acknowledgeAlerte(id: string): Observable<boolean> {
    return this.updateAlerteStatus(id, AlertStatus.ACKNOWLEDGED);
  }

  resolveAlerte(id: string): Observable<boolean> {
    return this.updateAlerteStatus(id, AlertStatus.RESOLVED);
  }

  dismissAlerte(id: string): Observable<boolean> {
    return this.updateAlerteStatus(id, AlertStatus.DISMISSED);
  }
  
  private updateAlerteStatus(id: string, status: AlertStatus): Observable<boolean> {
    return this.http.put<any>(`${this.apiUrl}/${id}/status`, { statut: status }).pipe(
      map(response => {
        if (response.success) {
          this.loadAlertes().subscribe();
          return true;
        }
        return false;
      }),
      catchError(error => {
        console.error('Error updating alert status:', error);
        return of(false);
      })
    );
  }

  archiveAlerte(id: string): Observable<boolean> {
    // This would need a backend endpoint, for now mark as resolved
    return this.resolveAlerte(id);
  }

  deleteAlerte(id: string): Observable<boolean> {
    // This would need a backend endpoint
    return of(false);
  }

  executeAction(alerteId: string, actionId: string): Observable<boolean> {
    console.log(`Executing action ${actionId} for alert ${alerteId}`);
    
    switch (actionId) {
      case 'read':
        return this.markAsRead(alerteId);
      case 'acknowledge':
        return this.acknowledgeAlerte(alerteId);
      case 'resolve':
        return this.resolveAlerte(alerteId);
      case 'restock':
        return this.acknowledgeAlerte(alerteId);
      default:
        return of(true).pipe(delay(500));
    }
  }
  
  refreshAlertes(): Observable<Alerte[]> {
    return this.loadAlertes();
  }
  
  generateAlertes(): Observable<any> {
    return this.http.post(`${this.apiUrl}/generate`, {}).pipe(
      map(() => {
        this.loadAlertes().subscribe();
        return { success: true };
      }),
      catchError(error => {
        console.error('Error generating alerts:', error);
        return of({ success: false });
      })
    );
  }

  private calculateStats(alertes: Alerte[]): AlertStats {
    const stats: AlertStats = {
      total: alertes.length,
      active: 0,
      acknowledged: 0,
      resolved: 0,
      critical: 0,
      high: 0,
      medium: 0,
      low: 0,
      byType: {
        [AlertType.STOCK_FAIBLE]: 0,
        [AlertType.RUPTURE]: 0,
        [AlertType.STOCK_ELEVE]: 0,
        [AlertType.EXPIRATION]: 0,
        [AlertType.SEUIL_CRITIQUE]: 0,
        [AlertType.MOUVEMENT_SUSPECT]: 0,
        [AlertType.SYSTEM]: 0,
        [AlertType.WARNING]: 0,
        [AlertType.INFO]: 0,
        [AlertType.SUCCESS]: 0
      },
      recent: 0
    };

    const oneDayAgo = new Date(Date.now() - 24 * 60 * 60 * 1000);

    alertes.forEach(alerte => {
      // Status counts
      switch (alerte.status) {
        case AlertStatus.ACTIVE:
          stats.active++;
          break;
        case AlertStatus.ACKNOWLEDGED:
          stats.acknowledged++;
          break;
        case AlertStatus.RESOLVED:
          stats.resolved++;
          break;
      }

      // Priority counts
      switch (alerte.priority) {
        case AlertPriority.CRITICAL:
          stats.critical++;
          break;
        case AlertPriority.HIGH:
          stats.high++;
          break;
        case AlertPriority.MEDIUM:
          stats.medium++;
          break;
        case AlertPriority.LOW:
          stats.low++;
          break;
      }

      // Type counts
      stats.byType[alerte.type]++;

      // Recent alerts (last 24 hours)
      if (alerte.dateCreation > oneDayAgo) {
        stats.recent++;
      }
    });

    return stats;
  }

  private getActionById(alerteId: string, actionId: string): AlertAction | undefined {
    const alerte = this.alertesSubject.value.find(a => a.id === alerteId);
    return alerte?.actions?.find(action => action.id === actionId);
  }
}
