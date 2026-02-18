import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DossierService } from '../../services/dossier.service';
import { 
  Dossier, 
  DocumentDossier, 
  CategorieDossier, 
  StatutDossier, 
  TypeDocument, 
  DossierStats 
} from '../../models/dossier.model';

@Component({
  selector: 'app-dossier-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="dossier-management">
      <!-- Header moderne -->
      <div class="header-section">
        <div class="header-content">
          <div class="header-title">
            <h1>📁 Gestion des Dossiers</h1>
            <p class="subtitle">Organisez et gérez vos documents et dossiers</p>
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
              [class.active]="currentView === 'dossiers'"
              (click)="setView('dossiers')"
            >
              <span class="icon">📁</span>
              Dossiers
            </button>
            <button 
              class="nav-pill" 
              [class.active]="currentView === 'documents'"
              (click)="setView('documents')"
            >
              <span class="icon">📄</span>
              Documents
            </button>
            <button 
              class="btn-primary"
              (click)="showAddDossierForm()"
              *ngIf="currentView === 'dossiers'"
            >
              <span class="icon">➕</span>
              Nouveau Dossier
            </button>
          </div>
        </div>
      </div>

      <!-- Dashboard -->
      <div class="dashboard-view" *ngIf="currentView === 'dashboard'">
        <div class="stats-container">
          <div class="stat-card primary">
            <div class="stat-icon">📁</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalDossiers }}</div>
              <div class="stat-label">Total Dossiers</div>
            </div>
          </div>

          <div class="stat-card success">
            <div class="stat-icon">✅</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.dossiersActifs }}</div>
              <div class="stat-label">Dossiers Actifs</div>
            </div>
          </div>

          <div class="stat-card info">
            <div class="stat-icon">📦</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.dossiersArchives }}</div>
              <div class="stat-label">Dossiers Archivés</div>
            </div>
          </div>

          <div class="stat-card warning">
            <div class="stat-icon">📄</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalDocuments }}</div>
              <div class="stat-label">Total Documents</div>
            </div>
          </div>

          <div class="stat-card danger">
            <div class="stat-icon">💾</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.tailleTotal }}</div>
              <div class="stat-label">Taille Total (MB)</div>
            </div>
          </div>
        </div>

        <!-- Dossiers par catégorie -->
        <div class="categories-section">
          <div class="section-header">
            <h2>Dossiers par Catégorie</h2>
          </div>
          
          <div class="categories-grid">
            <div 
              *ngFor="let category of getCategoriesWithCounts()" 
              class="category-card modern"
              (click)="filterByCategory(category.name)"
            >
              <div class="category-icon">{{ getCategoryIcon(category.name) }}</div>
              <div class="category-info">
                <h3>{{ category.name }}</h3>
                <span class="category-count">{{ category.count }} dossier(s)</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Dossiers récents -->
        <div class="recent-section">
          <div class="section-header">
            <h2>Dossiers Récents</h2>
            <button class="btn-outline" (click)="setView('dossiers')">
              Voir tout
              <span class="icon">→</span>
            </button>
          </div>
          
          <div class="dossiers-grid">
            <div 
              *ngFor="let dossier of dossiers.slice(0, 6)" 
              class="dossier-card modern"
              (click)="viewDossierDetails(dossier)"
            >
              <div class="card-header">
                <div class="dossier-info">
                  <h3>{{ dossier.nom }}</h3>
                  <span class="dossier-categorie" [class]="getCategoryClass(dossier.categorie)">
                    {{ dossier.categorie }}
                  </span>
                </div>
                <div class="status-indicator">
                  <span 
                    class="status-dot"
                    [class]="getStatusClass(dossier.statut)"
                  ></span>
                </div>
              </div>
              
              <div class="card-content">
                <div class="dossier-description">{{ dossier.description }}</div>
                <div class="dossier-meta">
                  <span class="meta-item">
                    <span class="meta-label">Documents:</span>
                    <span class="meta-value">{{ dossier.documents.length }}</span>
                  </span>
                  <span class="meta-item">
                    <span class="meta-label">Taille:</span>
                    <span class="meta-value">{{ dossier.taille }} MB</span>
                  </span>
                </div>
                <div class="dossier-date">
                  Modifié le {{ dossier.dateModification | date:'dd/MM/yyyy' }}
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
    .dossier-management {
      width: 100%;
      background: #f8fafc;
      padding: 0;
    }

    /* Styles identiques aux autres composants */
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

    .categories-section, .recent-section {
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

    .categories-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 1.5rem;
      margin-bottom: 3rem;
    }

    .category-card.modern {
      background: white;
      border-radius: 16px;
      padding: 1.5rem;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      cursor: pointer;
      transition: all 0.3s ease;
      border: 2px solid transparent;
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .category-card.modern:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
      border-color: #667eea;
    }

    .category-icon {
      font-size: 2.5rem;
      opacity: 0.8;
    }

    .category-info h3 {
      font-size: 1.1rem;
      font-weight: 600;
      color: #1f2937;
      margin: 0 0 0.25rem 0;
    }

    .category-count {
      color: #6b7280;
      font-size: 0.9rem;
    }

    .dossiers-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
      gap: 1.5rem;
    }

    .dossier-card.modern {
      background: white;
      border-radius: 16px;
      padding: 1.5rem;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      cursor: pointer;
      transition: all 0.3s ease;
      border: 2px solid transparent;
    }

    .dossier-card.modern:hover {
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

    .dossier-info h3 {
      font-size: 1.2rem;
      font-weight: 600;
      color: #1f2937;
      margin: 0 0 0.25rem 0;
    }

    .dossier-categorie {
      padding: 0.25rem 0.75rem;
      border-radius: 6px;
      font-size: 0.8rem;
      font-weight: 500;
    }

    .dossier-categorie.administratif { background: #dbeafe; color: #1e40af; }
    .dossier-categorie.juridique { background: #fee2e2; color: #991b1b; }
    .dossier-categorie.financier { background: #d1fae5; color: #065f46; }
    .dossier-categorie.technique { background: #fef3c7; color: #92400e; }
    .dossier-categorie.rh { background: #e0e7ff; color: #3730a3; }
    .dossier-categorie.commercial { background: #fce7f3; color: #be185d; }
    .dossier-categorie.projet { background: #ecfdf5; color: #047857; }
    .dossier-categorie.autre { background: #f3f4f6; color: #374151; }

    .status-dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;
      background: #e5e7eb;
    }

    .status-dot.actif { background: #10b981; }
    .status-dot.archive { background: #6b7280; }
    .status-dot.brouillon { background: #f59e0b; }
    .status-dot.en-revision { background: #3b82f6; }
    .status-dot.confidentiel { background: #ef4444; }

    .card-content {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
    }

    .dossier-description {
      color: #6b7280;
      font-size: 0.9rem;
      line-height: 1.4;
    }

    .dossier-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .meta-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 0.5rem;
      background: #f8fafc;
      border-radius: 8px;
      flex: 1;
      margin: 0 0.25rem;
    }

    .meta-label {
      color: #6b7280;
      font-size: 0.7rem;
      margin-bottom: 0.25rem;
    }

    .meta-value {
      font-weight: 600;
      color: #1f2937;
      font-size: 0.9rem;
    }

    .dossier-date {
      color: #6b7280;
      font-size: 0.8rem;
      text-align: center;
      font-style: italic;
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

      .categories-grid, .dossiers-grid {
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

      .dossier-card.modern, .category-card.modern {
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
export class DossierManagementComponent implements OnInit {
  dossiers: Dossier[] = [];
  documents: DocumentDossier[] = [];
  stats: DossierStats = {
    totalDossiers: 0,
    dossiersActifs: 0,
    dossiersArchives: 0,
    totalDocuments: 0,
    tailleTotal: 0,
    dossiersParCategorie: {}
  };

  currentView: 'dashboard' | 'dossiers' | 'documents' = 'dashboard';

  constructor(private dossierService: DossierService) {}

  goBackToMenu(): void {
    window.location.href = '/menu';
  }

  ngOnInit(): void {
    this.dossierService.getDossiers().subscribe(dossiers => {
      this.dossiers = dossiers;
      this.stats = this.dossierService.getDossierStats();
    });

    this.dossierService.documents$.subscribe(documents => {
      this.documents = documents;
    });
  }

  setView(view: 'dashboard' | 'dossiers' | 'documents'): void {
    this.currentView = view;
  }

  viewDossierDetails(dossier: Dossier): void {
    // À implémenter
  }

  showAddDossierForm(): void {
    // À implémenter
  }

  filterByCategory(category: string): void {
    // À implémenter
  }

  getCategoriesWithCounts(): { name: string, count: number }[] {
    return Object.entries(this.stats.dossiersParCategorie).map(([name, count]) => ({
      name,
      count
    }));
  }

  getCategoryIcon(category: string): string {
    const icons: { [key: string]: string } = {
      'Administratif': '📋',
      'Juridique': '⚖️',
      'Financier': '💰',
      'Technique': '🔧',
      'Ressources Humaines': '👥',
      'Commercial': '🤝',
      'Projet': '📊',
      'Autre': '📁'
    };
    return icons[category] || '📁';
  }

  getCategoryClass(categorie: CategorieDossier): string {
    return categorie.toLowerCase().replace(/[^a-z]/g, '');
  }

  getStatusClass(statut: StatutDossier): string {
    return statut.toLowerCase().replace(/[^a-z]/g, '-');
  }

  getViewTitle(): string {
    switch (this.currentView) {
      case 'dossiers': return 'Gestion des Dossiers';
      case 'documents': return 'Gestion des Documents';
      default: return 'Gestion des Dossiers';
    }
  }

  getViewFeatures(): string[] {
    switch (this.currentView) {
      case 'dossiers':
        return [
          'Création de dossiers',
          'Organisation par catégories',
          'Gestion des accès',
          'Archivage automatique',
          'Recherche avancée'
        ];
      case 'documents':
        return [
          'Upload de documents',
          'Versioning des fichiers',
          'Prévisualisation',
          'Métadonnées',
          'Contrôle de confidentialité'
        ];
      default:
        return [];
    }
  }
}