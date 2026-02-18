import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockAlertsComponent } from '../alerts/stock-alerts.component';
import { Alerte } from '../../../../models/alert.model';

@Component({
  selector: 'app-alerts-demo',
  standalone: true,
  imports: [CommonModule, StockAlertsComponent],
  template: `
    <div class="alerts-demo">
      <h2>Démo des Alertes Stock</h2>
      
      <!-- Full alerts component -->
      <div class="demo-section">
        <h3>Vue complète des alertes</h3>
        <app-stock-alerts 
          [showHeader]="true"
          [showFilters]="true"
          [maxDisplayed]="10"
          (alertClicked)="onAlertClicked($event)"
          (actionExecuted)="onActionExecuted($event)">
        </app-stock-alerts>
      </div>
      
      <!-- Compact alerts component -->
      <div class="demo-section">
        <h3>Vue compacte des alertes</h3>
        <app-stock-alerts 
          [showHeader]="false"
          [showFilters]="false"
          [compactMode]="true"
          [maxDisplayed]="5"
          (alertClicked)="onAlertClicked($event)">
        </app-stock-alerts>
      </div>
    </div>
  `,
  styles: [`
    .alerts-demo {
      padding: 2rem;
      background: #f8fafc;
      min-height: 100vh;
    }
    
    .demo-section {
      margin-bottom: 3rem;
      background: white;
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    }
    
    .demo-section h3 {
      margin: 0 0 1.5rem 0;
      color: #1f2937;
      font-size: 1.2rem;
      font-weight: 600;
    }
    
    h2 {
      text-align: center;
      color: #1f2937;
      margin-bottom: 2rem;
      font-size: 2rem;
      font-weight: 700;
    }
  `]
})
export class AlertsDemoComponent {
  
  onAlertClicked(alerte: Alerte): void {
    // Handle alert click - could navigate to details, show modal, etc.
  }
  
  onActionExecuted(event: {alerte: Alerte, actionId: string}): void {
    // Handle action execution - could show success message, refresh data, etc.
  }
}