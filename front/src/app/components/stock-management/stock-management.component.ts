import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NotificationComponent } from '../shared/notification/notification.component';

// Subcomponents
import { StockDashboardComponent } from './components/dashboard/stock-dashboard.component';
import { CategoriesManagementComponent } from './components/categories/categories-management.component';
import { MarquesManagementComponent } from './components/marques/marques-management.component';
import { ArticlesManagementComponent } from './components/articles/articles-management.component';
import { EntrepotsManagementComponent } from './components/entrepots/entrepots-management.component';
import { StockManagementViewComponent } from './components/stock/stock-management-view.component';
import { MouvementsManagementComponent } from './components/mouvements/mouvements-management.component';
import { AjustementStockComponent } from './components/ajustement-stock/ajustement-stock.component';
import { StockTraceComponent } from './components/stock-trace/stock-trace.component';
import { StockAlertsComponent } from './components/alerts/stock-alerts.component';

@Component({
  selector: 'app-stock-management',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule,
    NotificationComponent,
    StockDashboardComponent,
    CategoriesManagementComponent,
    MarquesManagementComponent,
    ArticlesManagementComponent,
    EntrepotsManagementComponent,
    StockManagementViewComponent,
    MouvementsManagementComponent,
    AjustementStockComponent,
    StockTraceComponent,
    StockAlertsComponent
  ],
  templateUrl: './stock-management.component.html',
  styleUrls: ['./stock-management.component.css']
})
export class StockManagementComponent implements OnInit {
  currentView: 'dashboard' | 'categories' | 'marques' | 'articles' | 'entrepots' | 'stock' | 'mouvements' | 'ajustements' | 'tracabilite' | 'alertes' = 'dashboard';

  constructor(private router: Router) {}

  ngOnInit(): void {}

  setView(view: 'dashboard' | 'categories' | 'marques' | 'articles' | 'entrepots' | 'stock' | 'mouvements' | 'ajustements' | 'tracabilite' | 'alertes'): void {
    this.currentView = view;
  }

  goBackToMenu(): void {
    this.router.navigate(['/menu']);
  }
}