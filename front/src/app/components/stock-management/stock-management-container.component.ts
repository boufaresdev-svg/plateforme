import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-stock-management',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule
  ],
  template: `
    <div class="stock-management">
      <!-- Header moderne -->
      <div class="header-section" style="margin-top: 4%;">
        <div class="header-content">
          <button class="btn-back-menu" (click)="goBackToMenu()">
            <span class="icon">🏠</span>
            Menu Principal
          </button>
          <div class="header-title">
            <h1>📦 Gestion du Stock</h1>
            <p class="subtitle">Gérez votre inventaire, catégories, marques et entrepôts</p>
          </div>
          <div class="header-actions">
            <button class="nav-pill" [class.active]="currentView === 'dashboard'" (click)="setView('dashboard')">
              <span class="icon">📊</span>
              Dashboard
            </button>
            <button class="nav-pill" [class.active]="currentView === 'categories'" (click)="setView('categories')">
              <span class="icon">🏷️</span>
              Catégories
            </button>
            <button class="nav-pill" [class.active]="currentView === 'marques'" (click)="setView('marques')">
              <span class="icon">🏭</span>
              Marques
            </button>
            <button class="nav-pill" [class.active]="currentView === 'articles'" (click)="setView('articles')">
              <span class="icon">📦</span>
              Articles
            </button>
            <button class="nav-pill" [class.active]="currentView === 'stock'" (click)="setView('stock')">
              <span class="icon">📊</span>
              Stock
            </button>
            <button class="nav-pill" [class.active]="currentView === 'entrepots'" (click)="setView('entrepots')">
              <span class="icon">🏢</span>
              Entrepôts
            </button>
            <button class="nav-pill" [class.active]="currentView === 'mouvements'" (click)="setView('mouvements')">
              <span class="icon">📋</span>
              Mouvements
            </button>
          </div>
        </div>
      </div>

      <!-- Dynamic Content Based on Current View -->
      <div class="content-container">
        <div *ngIf="currentView === 'dashboard'" class="placeholder-content">
          <h3>📊 Dashboard du Stock</h3>
          <p>Vue d'ensemble des statistiques et métriques de stock</p>
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon">📦</div>
              <div class="stat-value">1,234</div>
              <div class="stat-label">Articles</div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">🏷️</div>
              <div class="stat-value">45</div>
              <div class="stat-label">Catégories</div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">🏢</div>
              <div class="stat-value">8</div>
              <div class="stat-label">Entrepôts</div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">⚠️</div>
              <div class="stat-value">12</div>
              <div class="stat-label">Alertes</div>
            </div>
          </div>
        </div>
        
        <div *ngIf="currentView === 'categories'" class="placeholder-content">
          <h3>🏷️ Gestion des Catégories</h3>
          <p>Organisez vos articles par catégories</p>
          <button class="btn-primary">+ Nouvelle Catégorie</button>
        </div>
        
        <div *ngIf="currentView === 'marques'" class="placeholder-content">
          <h3>🏭 Gestion des Marques</h3>
          <p>Gérez les marques de vos produits</p>
          <button class="btn-primary">+ Nouvelle Marque</button>
        </div>
        
        <div *ngIf="currentView === 'articles'" class="placeholder-content">
          <h3>📦 Gestion des Articles</h3>
          <p>Créez et gérez vos articles</p>
          <button class="btn-primary">+ Nouvel Article</button>
        </div>
        
        <div *ngIf="currentView === 'stock'" class="placeholder-content">
          <h3>📊 Vue des Stocks</h3>
          <p>Consultez les niveaux de stock par entrepôt</p>
          <button class="btn-primary">⚖️ Ajustement de Stock</button>
        </div>
        
        <div *ngIf="currentView === 'entrepots'" class="placeholder-content">
          <h3>🏢 Gestion des Entrepôts</h3>
          <p>Gérez vos entrepôts et zones de stockage</p>
          <button class="btn-primary">+ Nouvel Entrepôt</button>
        </div>
        
        <div *ngIf="currentView === 'mouvements'" class="placeholder-content">
          <h3>📋 Mouvements de Stock</h3>
          <p>Historique des entrées et sorties de stock</p>
          <button class="btn-primary">📥 Nouveau Mouvement</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .stock-management {
      min-height: 100vh;
      background: #f8fafc;
    }

    .header-section {
      background: white;
      border-bottom: 1px solid #e5e7eb;
      padding: 2rem 0;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }

    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 2rem;
      display: flex;
      align-items: center;
      justify-content: space-between;
      flex-wrap: wrap;
      gap: 2rem;
    }

    .btn-back-menu {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.75rem 1.5rem;
      background: #f3f4f6;
      color: #374151;
      border: none;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.3s ease;
      font-weight: 500;
    }

    .btn-back-menu:hover {
      background: #e5e7eb;
      transform: translateY(-2px);
    }

    .header-title h1 {
      margin: 0 0 0.5rem;
      color: #1f2937;
      font-size: 2rem;
      font-weight: bold;
    }

    .subtitle {
      margin: 0;
      color: #6b7280;
      font-size: 1.1rem;
    }

    .header-actions {
      display: flex;
      gap: 0.5rem;
      flex-wrap: wrap;
    }

    .nav-pill {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.75rem 1rem;
      background: #f9fafb;
      color: #6b7280;
      border: none;
      border-radius: 25px;
      cursor: pointer;
      transition: all 0.3s ease;
      font-weight: 500;
      font-size: 0.9rem;
    }

    .nav-pill:hover {
      background: #e5e7eb;
      color: #374151;
    }

    .nav-pill.active {
      background: #667eea;
      color: white;
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
    }

    .nav-pill .icon {
      font-size: 1.1rem;
    }

    .content-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 2rem;
    }

    .placeholder-content {
      background: white;
      border-radius: 16px;
      padding: 3rem;
      text-align: center;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    }

    .placeholder-content h3 {
      margin: 0 0 1rem;
      color: #1f2937;
      font-size: 1.8rem;
      font-weight: bold;
    }

    .placeholder-content p {
      margin: 0 0 2rem;
      color: #6b7280;
      font-size: 1.1rem;
    }

    .btn-primary {
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      padding: 1rem 2rem;
      background: #10b981;
      color: white;
      border: none;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.3s ease;
      font-weight: 600;
      font-size: 1rem;
    }

    .btn-primary:hover {
      background: #059669;
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(16, 185, 129, 0.3);
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1.5rem;
      margin-top: 2rem;
    }

    .stat-card {
      background: #f9fafb;
      border-radius: 12px;
      padding: 1.5rem;
      text-align: center;
      border: 2px solid #f3f4f6;
      transition: all 0.3s ease;
    }

    .stat-card:hover {
      border-color: #667eea;
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    }

    .stat-icon {
      font-size: 2.5rem;
      margin-bottom: 0.5rem;
    }

    .stat-value {
      font-size: 2rem;
      font-weight: bold;
      color: #1f2937;
      margin-bottom: 0.25rem;
    }

    .stat-label {
      color: #6b7280;
      font-weight: 500;
    }

    @media (max-width: 768px) {
      .header-content {
        flex-direction: column;
        text-align: center;
      }

      .header-actions {
        justify-content: center;
      }

      .nav-pill {
        padding: 0.5rem 0.75rem;
        font-size: 0.8rem;
      }

      .content-container {
        padding: 1rem;
      }

      .placeholder-content {
        padding: 2rem 1rem;
      }

      .stats-grid {
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
        gap: 1rem;
      }
    }
  `]
})
export class StockManagementComponent implements OnInit {
  currentView: string = 'dashboard';

  constructor(private router: Router) {}

  ngOnInit(): void {}

  setView(view: string): void {
    this.currentView = view;
  }

  goBackToMenu(): void {
    this.router.navigate(['/menu']);
  }
}