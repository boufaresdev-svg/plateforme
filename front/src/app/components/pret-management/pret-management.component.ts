import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PretService } from '../../services/pret.service';
import { 
  Pret, 
  ArticlePret, 
  StatutPret, 
  EtatArticle, 
  PretStats 
} from '../../models/pret.model';

@Component({
  selector: 'app-pret-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="pret-management">
      <!-- Header moderne -->
      <div class="header-section">
        <div class="header-content">
          <div class="header-title">
            <h1>📋 Gestion des Prêts</h1>
            <p class="subtitle">Gérez les prêts de matériel et suivez les retours</p>
          </div>
          <div class="header-actions">
            <button 
              class="nav-pill" 
              [class.active]="currentView === 'dashboard'"
              (click)="setView('dashboard')"
            >
              <span class="icon">📊</span>
              Dashboard
            </button>
            <button 
              class="nav-pill" 
              [class.active]="currentView === 'prets'"
              (click)="setView('prets')"
            >
              <span class="icon">🤝</span>
              Prêts
            </button>
            <button 
              class="nav-pill" 
              [class.active]="currentView === 'retours'"
              (click)="setView('retours')"
            >
              <span class="icon">↩️</span>
              Retours
            </button>
            <button 
              class="btn-primary"
              (click)="showAddPretForm()"
              *ngIf="currentView === 'prets'"
            >
              <span class="icon">➕</span>
              Nouveau Prêt
            </button>
          </div>
        </div>
      </div>

      <!-- Dashboard -->
      <div class="dashboard-view" *ngIf="currentView === 'dashboard'">
        <div class="stats-container">
          <div class="stat-card primary">
            <div class="stat-icon">🤝</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalPrets }}</div>
              <div class="stat-label">Total Prêts</div>
            </div>
          </div>

          <div class="stat-card warning">
            <div class="stat-icon">⏳</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.pretsEnCours }}</div>
              <div class="stat-label">Prêts en Cours</div>
            </div>
          </div>

          <div class="stat-card danger" *ngIf="stats.pretsEnRetard > 0">
            <div class="stat-icon">⚠️</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.pretsEnRetard }}</div>
              <div class="stat-label">Prêts en Retard</div>
            </div>
          </div>

          <div class="stat-card info">
            <div class="stat-icon">📦</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.articlesPretes }}</div>
              <div class="stat-label">Articles Prêtés</div>
            </div>
          </div>

          <div class="stat-card success">
            <div class="stat-icon">✅</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.tauxRetour }}%</div>
              <div class="stat-label">Taux de Retour</div>
            </div>
          </div>
        </div>

        <!-- Prêts récents -->
        <div class="recent-section">
          <div class="section-header">
            <h2>Prêts Récents</h2>
            <button class="btn-outline" (click)="setView('prets')">
              Voir tout
              <span class="icon">→</span>
            </button>
          </div>
          
          <div class="prets-grid">
            <div 
              *ngFor="let pret of prets.slice(0, 6)" 
              class="pret-card modern"
              (click)="viewPretDetails(pret)"
            >
              <div class="card-header">
                <div class="pret-info">
                  <h3>{{ pret.emprunteur }}</h3>
                  <span class="pret-departement">{{ pret.departement }}</span>
                </div>
                <div class="status-indicator">
                  <span 
                    class="status-dot"
                    [class]="getStatusClass(pret.statut)"
                  ></span>
                </div>
              </div>
              
              <div class="card-content">
                <div class="pret-motif">{{ pret.motif }}</div>
                <div class="pret-dates">
                  <span class="date-label">Retour prévu:</span>
                  <span class="date-value" [class.overdue]="isOverdue(pret)">
                    {{ pret.dateRetourPrevue | date:'dd/MM/yyyy' }}
                  </span>
                </div>
                <div class="pret-articles">
                  {{ pret.articles.length }} article(s) prêté(s)
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Vue en développement pour les autres sections -->
      <div *ngIf="currentView !== 'dashboard'" class="development-view">
        <div class="development-card">
          <div class="development-icon">🚧</div>
          <h2>{{ getViewTitle() }}</h2>
          <p>Cette section est en cours de développement</p>
          <div class="development-features">
            <h4>Fonctionnalités prévues:</h4>
            <ul>
              <li *ngFor="let feature of getViewFeatures()">{{ feature }}</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .pret-management {
      width: 100%;
      background: #f8fafc;
      padding: 0;
    }

    /* Styles identiques à stock-management */
    .header-section {
      background: #2057e0;
      color: white;
      padding: 2rem;
      margin-bottom: 2rem;
      width: 100%;
      border-radius: 16px;
    }

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;
      max-width: none;
    }

    .header-title h1 {
      font-size: 2.5rem;
      font-weight: 700;
      margin: 0 0 0.5rem 0;
    }

    .subtitle {
      opacity: 0.9;
      font-size: 1.1rem;
      margin: 0;
    }

    .header-actions {
      display: flex;
      gap: 1rem;
      align-items: center;
      flex-wrap: wrap;
    }

    .nav-pill {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.75rem 1.5rem;
      background: rgba(255, 255, 255, 0.1);
      border: 2px solid rgba(255, 255, 255, 0.2);
      border-radius: 50px;
      color: white;
      cursor: pointer;
      transition: all 0.3s ease;
      font-weight: 500;
    }

    .nav-pill:hover {
      background: rgba(255, 255, 255, 0.2);
      border-color: rgba(255, 255, 255, 0.4);
    }

    .nav-pill.active {
      background: white;
      color: #667eea;
      border-color: white;
    }

    .btn-primary {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.75rem 1.5rem;
      background: #10b981;
      color: white;
      border: none;
      border-radius: 12px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-primary:hover {
      background: #059669;
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(16, 185, 129, 0.3);
    }

    .btn-outline {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.75rem 1.5rem;
      background: transparent;
      color: #6b7280;
      border: 2px solid #e5e7eb;
      border-radius: 12px;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-outline:hover {
      border-color: #667eea;
      color: #667eea;
    }

    .icon {
      font-size: 1rem;
    }

    .dashboard-view {
      width: 100%;
      padding: 0 2rem;
    }

    .stats-container {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 1.5rem;
      margin-bottom: 3rem;
    }

    .stat-card {
      background: white;
      border-radius: 16px;
      padding: 2rem;
      display: flex;
      align-items: center;
      gap: 1.5rem;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      transition: all 0.3s ease;
      border-left: 4px solid transparent;
    }

    .stat-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
    }

    .stat-card.primary { border-left-color: #667eea; }
    .stat-card.success { border-left-color: #10b981; }
    .stat-card.warning { border-left-color: #f59e0b; }
    .stat-card.danger { border-left-color: #ef4444; }
    .stat-card.info { border-left-color: #3b82f6; }

    .stat-icon {
      font-size: 3rem;
      opacity: 0.8;
    }

    .stat-number {
      font-size: 2.5rem;
      font-weight: 700;
      color: #1f2937;
      line-height: 1;
    }

    .stat-label {
      color: #6b7280;
      font-size: 0.9rem;
      font-weight: 500;
      margin-top: 0.25rem;
    }

    .recent-section {
      margin-bottom: 3rem;
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    .section-header h2 {
      font-size: 1.8rem;
      font-weight: 700;
      color: #1f2937;
      margin: 0;
    }

    .prets-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
      gap: 1.5rem;
    }

    .pret-card.modern {
      background: white;
      border-radius: 16px;
      padding: 1.5rem;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      cursor: pointer;
      transition: all 0.3s ease;
      border: 2px solid transparent;
    }

    .pret-card.modern:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
      border-color: #667eea;
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 1rem;
    }

    .pret-info h3 {
      font-size: 1.2rem;
      font-weight: 600;
      color: #1f2937;
      margin: 0 0 0.25rem 0;
    }

    .pret-departement {
      background: #f3f4f6;
      color: #6b7280;
      padding: 0.25rem 0.75rem;
      border-radius: 6px;
      font-size: 0.8rem;
      font-weight: 500;
    }

    .status-dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;
      background: #e5e7eb;
    }

    .status-dot.en-cours { background: #f59e0b; }
    .status-dot.retourne { background: #10b981; }
    .status-dot.retard { background: #ef4444; }
    .status-dot.partiellement-retourne { background: #3b82f6; }
    .status-dot.annule { background: #6b7280; }

    .card-content {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
    }

    .pret-motif {
      color: #6b7280;
      font-size: 0.9rem;
      font-style: italic;
    }

    .pret-dates {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .date-label {
      color: #6b7280;
      font-size: 0.8rem;
    }

    .date-value {
      font-weight: 600;
      color: #1f2937;
    }

    .date-value.overdue {
      color: #ef4444;
    }

    .pret-articles {
      color: #6b7280;
      font-size: 0.8rem;
      text-align: center;
      padding: 0.5rem;
      background: #f8fafc;
      border-radius: 8px;
    }

    .development-view {
      width: 100%;
      padding: 0 2rem;
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 60vh;
    }

    .development-card {
      background: white;
      border-radius: 16px;
      padding: 3rem;
      text-align: center;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      max-width: 600px;
      width: 100%;
    }

    .development-icon {
      font-size: 4rem;
      margin-bottom: 1.5rem;
    }

    .development-card h2 {
      font-size: 2rem;
      font-weight: 700;
      color: #1f2937;
      margin-bottom: 1rem;
    }

    .development-card p {
      color: #6b7280;
      font-size: 1.1rem;
      margin-bottom: 2rem;
    }

    .development-features {
      text-align: left;
    }

    .development-features h4 {
      color: #374151;
      margin-bottom: 1rem;
      font-size: 1.1rem;
    }

    .development-features ul {
      list-style: none;
      padding: 0;
    }

    .development-features li {
      padding: 0.5rem 0;
      border-bottom: 1px solid #f3f4f6;
      color: #6b7280;
    }

    .development-features li:before {
      content: "✓";
      color: #059669;
      font-weight: bold;
      margin-right: 0.5rem;
    }

    /* Responsive */
    @media (max-width: 1024px) {
      .header-content {
        flex-direction: column;
        gap: 1.5rem;
        text-align: center;
      }

      .header-actions {
        justify-content: center;
      }

      .dashboard-view {
        padding: 0 1rem;
      }
    }

    @media (max-width: 768px) {
      .header-section {
        padding: 1.5rem 1rem;
      }

      .stats-container {
        grid-template-columns: 1fr;
        gap: 1rem;
      }

      .prets-grid {
        grid-template-columns: 1fr;
      }
    }

    @media (max-width: 480px) {
      .header-title h1 {
        font-size: 2rem;
      }

      .nav-pill, .btn-primary, .btn-outline {
        padding: 0.5rem 1rem;
        font-size: 0.9rem;
      }

      .stat-card {
        padding: 1.5rem;
      }

      .stat-number {
        font-size: 2rem;
      }

      .pret-card.modern {
        padding: 1rem;
      }

      .development-view {
        padding: 0 0.5rem;
      }

      .development-card {
        padding: 2rem;
      }
    }
  `]
})
export class PretManagementComponent implements OnInit {
  prets: Pret[] = [];
  stats: PretStats = {
    totalPrets: 0,
    pretsEnCours: 0,
    pretsEnRetard: 0,
    articlesPretes: 0,
    tauxRetour: 0,
    pretsParMois: 0
  };

  currentView: 'dashboard' | 'prets' | 'retours' = 'dashboard';

  constructor(private pretService: PretService) {}

  goBackToMenu(): void {
    window.location.href = '/menu';
  }

  ngOnInit(): void {
    this.pretService.getPrets().subscribe(prets => {
      this.prets = prets;
      this.stats = this.pretService.getPretStats();
    });
  }

  setView(view: 'dashboard' | 'prets' | 'retours'): void {
    this.currentView = view;
  }

  viewPretDetails(pret: Pret): void {
    // À implémenter
  }

  showAddPretForm(): void {
    // À implémenter
  }

  getStatusClass(statut: StatutPret): string {
    return statut.toLowerCase().replace(/[^a-z]/g, '-');
  }

  isOverdue(pret: Pret): boolean {
    return pret.statut === StatutPret.EN_COURS && new Date(pret.dateRetourPrevue) < new Date();
  }

  getViewTitle(): string {
    switch (this.currentView) {
      case 'prets': return 'Gestion des Prêts';
      case 'retours': return 'Gestion des Retours';
      default: return 'Gestion des Prêts';
    }
  }

  getViewFeatures(): string[] {
    switch (this.currentView) {
      case 'prets':
        return [
          'Liste complète des prêts',
          'Création de nouveaux prêts',
          'Suivi des échéances',
          'Gestion des emprunteurs',
          'Historique des prêts'
        ];
      case 'retours':
        return [
          'Traitement des retours',
          'Vérification de l\'état',
          'Retours partiels',
          'Pénalités de retard',
          'Rapports de retours'
        ];
      default:
        return [];
    }
  }
}