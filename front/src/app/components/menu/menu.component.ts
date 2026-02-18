import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Subscription, forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { UserManagementService } from '../../services/user-management.service';
import { FormationService } from '../../services/formation_managment/formation.service';
import { StockTraceService } from '../../services/stock-trace.service';
import { EmployeeService } from '../../services/employee_management/employee.service';
import { ClientService } from '../../services/client.service';
import { FournisseurService } from '../../services/fournisseur.service';
import { UserStats } from '../../models/user-management.model';
import { FormationStatistics } from '../../models/formation/formation.model';
import { StockTraceStats } from '../../models/stock-trace.model';
import { EmployeeStats } from '../../models/employee/employee.model';
import { ClientStats } from '../../models/client/ClientStats.model';
import { FournisseurStats } from '../../models/fournisseur/fournisseur.model';
import { VehicleManagementComponent } from '../vehicle-management/vehicle-management.component';
import { HRManagementComponent } from '../hr-management/hr-management.component';
import { FormationManagementComponent } from '../formation-management/formation-management.component';
import { EmployeeManagementComponent } from '../employee-management/employee-management.component';
import { ProjetManagementNewComponent } from '../projet-management-new/projet-management-new.component';
import { StockManagementComponent } from '../stock-management/stock-management.component';
import { PretManagementComponent } from '../pret-management/pret-management.component';
import { SousTraitanceManagementComponent } from '../sous-traitance-management/sous-traitance-management.component';
import { DossierManagementComponent } from '../dossier-management/dossier-management.component';
import { SOPManagementComponent } from '../sop-management/sop-management.component';
import { FournisseurManagementComponent } from '../fournisseur-management/fournisseur-management.component';
import { ClientManagementComponent } from '../client-management/client-management.component';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    VehicleManagementComponent, 
    HRManagementComponent, 
    FormationManagementComponent, 
    EmployeeManagementComponent, 
    ProjetManagementNewComponent, 
    StockManagementComponent, 
    PretManagementComponent, 
    SousTraitanceManagementComponent, 
    DossierManagementComponent, 
    SOPManagementComponent, 
    FournisseurManagementComponent, 
    ClientManagementComponent
  ],
  template: `
    <div class="app-layout">
      <!-- Header fixe -->
      <header class="app-header">
        <div class="header-content">
          <div class="header-left">
            <h1 class="app-title" (click)="goToMainMenu()">SMS2i</h1>
            <span class="app-subtitle">Système de Management Intégré</span>
          </div>
          <div class="header-right">
            <div class="user-info">
              <span class="user-icon">👤</span>
              <div class="user-details">
                <span class="user-name">{{ getUserDisplayName() }}</span>
                <span class="user-status connected">Connecté</span>
              </div>
            </div>
            <button class="btn-logout" (click)="logout()">
              <span class="logout-icon">🚪</span>
              Déconnexion
            </button>
          </div>
        </div>
      </header>

      <!-- Sidebar -->
      <nav class="sidebar">
        
        <div class="sidebar-menu">
          <a 
            *ngFor="let item of menuItems" 
            (click)="selectMenuItem(item)"
            class="sidebar-item"
            [class.active]="selectedModule === item.route"
          >
            <span class="sidebar-icon">{{ item.icon }}</span>
            <span>{{ item.name }}</span>
          </a>
        </div>
        
      </nav>

      <!-- Main Content -->
      <main class="main-content">
        <!-- Dashboard View -->
        <div *ngIf="selectedModule === null">
        <section class="dashboard-hero">
          <div>
            <p class="eyebrow">Vue globale</p>
            <h2>Tableau de bord multi-modules</h2>
            <p class="muted">Statistiques complètes issues de tous les modules de l'application</p>
          </div>
          <button class="cta" (click)="refreshStats()">↻ Rafraîchir</button>
        </section>

        <div *ngIf="!statsLoading; else loadingBlock">
          <!-- Module Formations -->
          <section class="module-section">
            <h3 class="module-title">🎓 Module Formations</h3>
            <div class="stat-grid">
              <div class="stat-card primary">
                <div class="label">📚 Formations</div>
                <div class="value">{{ stats.formations.totalFormations || 0 }}</div>
                <div class="sub">En cours {{ stats.formations.formationsEnCours || 0 }} · Planifiées {{ stats.formations.formationsPlanifiees || 0 }}</div>
              </div>

              <div class="stat-card">
                <div class="label">👥 Participants</div>
                <div class="value">{{ stats.formations.totalParticipants || 0 }}</div>
                <div class="sub">Actifs {{ stats.formations.participantsActifs || 0 }}</div>
              </div>

              <div class="stat-card">
                <div class="label">🎓 Formateurs</div>
                <div class="value">{{ stats.formations.totalFormateurs || 0 }}</div>
                <div class="sub">Contenus {{ stats.formations.totalContenus || 0 }}</div>
              </div>

              <div class="stat-card">
                <div class="label">📜 Certifications</div>
                <div class="value">{{ stats.formations.certificationsDelivrees || 0 }}</div>
                <div class="sub">Ce mois {{ stats.formations.certificationsThisMonth || 0 }}</div>
              </div>

              <div class="stat-card accent">
                <div class="label">💰 Revenus</div>
                <div class="value">{{ formatCurrency(stats.formations.totalRevenue) }}</div>
                <div class="sub">Prix moyen {{ formatCurrency(stats.formations.averageFormationPrice) }}</div>
              </div>

              <div class="stat-card accent">
                <div class="label">📚 Domaines</div>
                <div class="value">{{ stats.formations.totalDomaines || 0 }}</div>
                <div class="sub">Catégories {{ stats.formations.totalCategories || 0 }} · Sous-cat {{ stats.formations.totalSousCategories || 0 }}</div>
              </div>
            </div>
          </section>

          <!-- Module Utilisateurs -->
          <section class="module-section">
            <h3 class="module-title">👤 Module Utilisateurs</h3>
            <div class="stat-grid">
              <div class="stat-card primary">
                <div class="label">👥 Utilisateurs</div>
                <div class="value">{{ stats.users.totalUsers || 0 }}</div>
                <div class="sub">Actifs {{ stats.users.activeUsers || 0 }}</div>
              </div>

              <div class="stat-card">
                <div class="label">✅ Statuts</div>
                <div class="value">{{ stats.users.activeUsers || 0 }}</div>
                <div class="sub">Inactifs {{ stats.users.inactiveUsers || 0 }} · Suspendus {{ stats.users.suspendedUsers || 0 }}</div>
              </div>

              <div class="stat-card">
                <div class="label">⏳ En attente</div>
                <div class="value">{{ stats.users.pendingUsers || 0 }}</div>
                <div class="sub">Utilisateurs en validation</div>
              </div>
            </div>
          </section>

          <!-- Module Employés / RH -->
          <section class="module-section">
            <h3 class="module-title">👥 Module RH & Employés</h3>
            <div class="stat-grid">
              <div class="stat-card primary">
                <div class="label">👤 Employés</div>
                <div class="value">{{ stats.employees.totalEmployees || 0 }}</div>
                <div class="sub">Actifs {{ stats.employees.activeEmployees || 0 }}</div>
              </div>

              <div class="stat-card">
                <div class="label">📊 Statuts</div>
                <div class="value">{{ stats.employees.activeEmployees || 0 }}</div>
                <div class="sub">Inactifs {{ stats.employees.inactiveEmployees || 0 }} · Suspendus {{ stats.employees.suspendedEmployees || 0 }}</div>
              </div>

              <div class="stat-card">
                <div class="label">🔑 Rôles</div>
                <div class="value">{{ stats.employees.totalRoles || 0 }}</div>
                <div class="sub">Permissions {{ stats.employees.totalPermissions || 0 }}</div>
              </div>

              <div class="stat-card accent">
                <div class="label">⏳ En attente</div>
                <div class="value">{{ stats.employees.pendingEmployees || 0 }}</div>
                <div class="sub">Employés en validation</div>
              </div>
            </div>
          </section>

          <!-- Module Stock -->
          <section class="module-section">
            <h3 class="module-title">📦 Module Stock</h3>
            <div class="stat-grid">
              <div class="stat-card primary">
                <div class="label">📦 Actions Stock</div>
                <div class="value">{{ stats.stock.totalActions || 0 }}</div>
                <div class="sub">Mouvements enregistrés</div>
              </div>

              <div class="stat-card">
                <div class="label">📊 Quantité</div>
                <div class="value">{{ stats.stock.quantiteTotaleModifiee || 0 }}</div>
                <div class="sub">Quantité totale modifiée</div>
              </div>

              <div class="stat-card accent">
                <div class="label">💰 Valeur</div>
                <div class="value">{{ formatCurrency(stats.stock.valeurTotaleModifiee) }}</div>
                <div class="sub">Valeur totale modifiée</div>
              </div>
            </div>
          </section>

          <!-- Module Clients -->
          <section class="module-section">
            <h3 class="module-title">👤 Module Clients</h3>
            <div class="stat-grid">
              <div class="stat-card primary">
                <div class="label">👥 Clients</div>
                <div class="value">{{ stats.clients.totalClients || 0 }}</div>
                <div class="sub">Actifs {{ stats.clients.clientsActifs || 0 }}</div>
              </div>

              <div class="stat-card">
                <div class="label">🎯 Prospects</div>
                <div class="value">{{ stats.clients.prospects || 0 }}</div>
                <div class="sub">Taux de conversion {{ stats.clients.tauxConversion || 0 }}%</div>
              </div>

              <div class="stat-card">
                <div class="label">💰 CA Total</div>
                <div class="value">{{ formatCurrency(stats.clients.chiffreAffairesTotal) }}</div>
                <div class="sub">Valeur moyenne {{ formatCurrency(stats.clients.valeurMoyenneClient) }}</div>
              </div>

              <div class="stat-card accent">
                <div class="label">📄 Factures</div>
                <div class="value">{{ stats.clients.facturenEnAttente || 0 }}</div>
                <div class="sub">En attente de paiement</div>
              </div>
            </div>
          </section>

          <!-- Module Fournisseurs -->
          <section class="module-section">
            <h3 class="module-title">🏭 Module Fournisseurs</h3>
            <div class="stat-grid">
              <div class="stat-card primary">
                <div class="label">🏭 Fournisseurs</div>
                <div class="value">{{ stats.fournisseurs.totalFournisseurs || 0 }}</div>
                <div class="sub">Actifs {{ stats.fournisseurs.fournisseursActifs || 0 }}</div>
              </div>

              <div class="stat-card">
                <div class="label">💳 Dettes</div>
                <div class="value">{{ stats.fournisseurs.totalDettes || 0 }}</div>
                <div class="sub">Non payées {{ stats.fournisseurs.dettesNonPayees || 0 }}</div>
              </div>

              <div class="stat-card">
                <div class="label">⚠️ En retard</div>
                <div class="value">{{ stats.fournisseurs.dettesEnRetard || 0 }}</div>
                <div class="sub">Dettes en retard</div>
              </div>

              <div class="stat-card accent">
                <div class="label">💰 Montant</div>
                <div class="value">{{ formatCurrency(stats.fournisseurs.montantDettesNonPayees) }}</div>
                <div class="sub">Dettes non payées</div>
              </div>
            </div>
          </section>
        </div>

        <ng-template #loadingBlock>
          <div class="loading">⏳ Chargement des statistiques...</div>
        </ng-template>
        </div>

        <!-- Vehicle Management View -->
        <div *ngIf="selectedModule === '/vehicules'" class="module-view">
          <app-vehicle-management></app-vehicle-management>
        </div>

        <!-- HR Management View -->
        <div *ngIf="selectedModule === '/rh'" class="module-view">
          <app-hr-management></app-hr-management>
        </div>

        <!-- Formation Management View (includes students/participants) -->
        <div *ngIf="selectedModule === '/formation'" class="module-view">
          <app-formation-management></app-formation-management>
        </div>

        <!-- User Management View -->
        <div *ngIf="selectedModule === '/employees'" class="module-view">
          <app-employee-management></app-employee-management>
        </div>

        <!-- Project Management View -->
        <div *ngIf="selectedModule === '/projets'" class="module-view">
          <app-projet-management-new></app-projet-management-new>
        </div>

        <!-- Stock Management View -->
        <div *ngIf="selectedModule === '/stock'" class="module-view">
          <app-stock-management></app-stock-management>
        </div>

        <!-- Pret Management View -->
        <div *ngIf="selectedModule === '/prets'" class="module-view">
          <app-pret-management></app-pret-management>
        </div>

        <!-- Sous-Traitance Management View -->
        <div *ngIf="selectedModule === '/sous-traitance'" class="module-view">
          <app-sous-traitance-management></app-sous-traitance-management>
        </div>

        <!-- Dossier Management View -->
        <div *ngIf="selectedModule === '/dossiers'" class="module-view">
          <app-dossier-management></app-dossier-management>
        </div>

        <!-- SOP Management View -->
        <div *ngIf="selectedModule === '/sop'" class="module-view">
          <app-sop-management></app-sop-management>
        </div>

        <!-- Fournisseur Management View -->
        <div *ngIf="selectedModule === '/fournisseurs'" class="module-view">
          <app-fournisseur-management></app-fournisseur-management>
        </div>

        <!-- Client Management View -->
        <div *ngIf="selectedModule === '/clients'" class="module-view">
          <app-client-management></app-client-management>
        </div>

        <!-- Other modules placeholder -->
        <div *ngIf="selectedModule && selectedModule !== '/vehicules' && selectedModule !== '/rh' && selectedModule !== '/formation' && selectedModule !== '/employees' && selectedModule !== '/projets' && selectedModule !== '/stock' && selectedModule !== '/prets' && selectedModule !== '/sous-traitance' && selectedModule !== '/dossiers' && selectedModule !== '/sop' && selectedModule !== '/fournisseurs' && selectedModule !== '/clients'" class="module-placeholder">
          <div class="placeholder-content">
            <div class="placeholder-icon">🚧</div>
            <h2>Module en cours de chargement</h2>
            <p>{{ getModuleName(selectedModule) }}</p>
            <button class="btn-back-dashboard" (click)="backToDashboard()">
              ← Retour au tableau de bord
            </button>
          </div>
        </div>

      </main>
    </div>
  `,
  styles: [`
    * {
      box-sizing: border-box;
    }

    .app-layout {
      display: flex;
      min-height: 100vh;
      background: #f8fafc;
    }

    .app-header {
      position: fixed;
      top: 0;
      left: 280px;
      right: 0;
      height: 70px;
      background: white;
      border-bottom: 1px solid #e5e7eb;
      z-index: 100;
      box-shadow: 0 2px 10px rgba(0,0,0,0.03);
    }

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 100%;
      padding: 0 2rem;
    }

    .header-left {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .app-title {
      font-size: 1.8rem;
      font-weight: 700;
      color: #2563eb;
      margin: 0;
      cursor: pointer;
    }

    .app-subtitle {
      color: #6b7280;
      font-size: 0.9rem;
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 1.5rem;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      padding: 0.5rem 1rem;
      background: #f8fafc;
      border-radius: 10px;
    }

    .user-icon {
      font-size: 1.5rem;
    }

    .user-details {
      display: flex;
      flex-direction: column;
    }

    .user-name {
      font-weight: 600;
      color: #1f2937;
      font-size: 0.95rem;
    }

    .user-status {
      font-size: 0.75rem;
      color: #6b7280;
    }

    .user-status.connected {
      color: #10b981;
    }

    .btn-logout {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.6rem 1.2rem;
      background: #ef4444;
      color: white;
      border: none;
      border-radius: 10px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-logout:hover {
      background: #dc2626;
      box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
    }

    .logout-icon {
      font-size: 1.1rem;
    }

    .sidebar {
      position: fixed;
      top: 0;
      left: 0;
      width: 280px;
      height: 100vh;
      background: white;
      border-right: 1px solid #e5e7eb;
      overflow-y: auto;
      z-index: 101;
    }

    .sidebar-header {
      padding: 2rem 1.5rem;
      border-bottom: 1px solid #e5e7eb;
      text-align: center;
    }

    .sidebar-header h2 {
      font-size: 1.8rem;
      font-weight: 700;
      color: #2563eb;
      margin-bottom: 0.5rem;
    }

    .sidebar-header p {
      color: #6b7280;
      font-size: 0.9rem;
    }

    .user-info-sidebar {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
      padding: 0.75rem 1rem;
      background: #f3f4f6;
      border-radius: 8px;
      margin-top: 1rem;
    }

    .user-info-sidebar .user-icon {
      font-size: 1.2rem;
    }

    .user-info-sidebar .user-name {
      font-weight: 500;
      color: #374151;
    }

    .sidebar-menu {
      flex: 1;
      padding: 1rem 0;
      padding-top: 2rem;
    }

    .sidebar-item {
      display: flex;
      align-items: center;
      width: 100%;
      padding: 1rem 1.5rem;
      color: #6b7280;
      text-decoration: none;
      transition: all 0.3s ease;
      border-left: 3px solid transparent;
      font-weight: 500;
    }

    .sidebar-item:hover {
      background: #f8fafc;
      color: #2563eb;
      border-left-color: #2563eb;
    }

    .sidebar-item.active {
      background: #eff6ff;
      color: #2563eb;
      border-left-color: #2563eb;
    }

    .sidebar-icon {
      margin-right: 0.75rem;
      font-size: 1.1rem;
      width: 20px;
      text-align: left;
    }

    .main-content {
      margin-left: 280px;
      margin-top: 70px;
      padding: 2rem;
      width: calc(100% - 280px);
      min-height: calc(100vh - 70px);
      background: #f8fafc;
    }

    .dashboard-hero {
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: white;
      border: 1px solid #e5e7eb;
      padding: 2rem;
      border-radius: 16px;
      margin-bottom: 2rem;
      box-shadow: 0 2px 10px rgba(0,0,0,0.03);
    }

    .eyebrow {
      text-transform: uppercase;
      letter-spacing: 0.08em;
      font-size: 0.75rem;
      color: #6b7280;
      margin: 0;
    }

    .dashboard-hero h2 {
      margin: 0.2rem 0;
      font-size: 1.5rem;
    }

    .muted {
      color: #6b7280;
      margin: 0;
    }

    .cta {
      background: #2563eb;
      color: white;
      border: none;
      border-radius: 10px;
      padding: 0.65rem 1rem;
      font-weight: 600;
      cursor: pointer;
      box-shadow: 0 10px 30px rgba(37, 99, 235, 0.15);
    }

    .module-section {
      margin-bottom: 3rem;
      background: white;
      padding: 2rem;
      border-radius: 16px;
      border: 1px solid #e5e7eb;
      box-shadow: 0 2px 10px rgba(0,0,0,0.03);
    }

    .module-title {
      font-size: 1.5rem;
      font-weight: 700;
      color: #1f2937;
      margin: 0 0 1.5rem 0;
      padding-bottom: 0.75rem;
      border-bottom: 3px solid #2563eb;
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }

    .stat-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
      gap: 1.25rem;
    }

    .stat-card {
      border: 2px solid #e5e7eb;
      border-radius: 14px;
      padding: 1.5rem;
      background: #fafbfc;
      transition: all 0.3s ease;
    }

    .stat-card:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0,0,0,0.08);
      border-color: #2563eb;
    }

    .stat-card.primary {
      background: linear-gradient(135deg, #2563eb, #1d4ed8);
      color: white;
      border: none;
      box-shadow: 0 12px 30px rgba(37,99,235,0.25);
    }

    .stat-card.accent {
      background: linear-gradient(135deg, #f8fafc, #ffffff);
      border: 1px solid #e0e7ff;
    }

    .label {
      font-size: 0.9rem;
      color: #6b7280;
      margin-bottom: 0.3rem;
    }

    .stat-card.primary .label {
      color: rgba(255,255,255,0.8);
    }

    .value {
      font-size: 2rem;
      font-weight: 700;
      margin: 0;
    }

    .sub {
      margin-top: 0.35rem;
      color: #6b7280;
      font-size: 0.9rem;
    }

    .stat-card.primary .sub {
      color: rgba(255,255,255,0.85);
    }

    .loading {
      padding: 3rem;
      color: #6b7280;
      text-align: center;
      font-size: 1.2rem;
      background: white;
      border-radius: 16px;
      margin: 2rem 0;
    }

    .module-view {
      width: 100%;
    }

    .module-placeholder {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 60vh;
    }

    .placeholder-content {
      text-align: center;
      background: white;
      padding: 3rem;
      border-radius: 16px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.05);
    }

    .placeholder-icon {
      font-size: 4rem;
      margin-bottom: 1rem;
    }

    .placeholder-content h2 {
      font-size: 1.8rem;
      color: #1f2937;
      margin-bottom: 0.5rem;
    }

    .placeholder-content p {
      color: #6b7280;
      margin-bottom: 2rem;
    }

    .btn-back-dashboard {
      padding: 0.75rem 1.5rem;
      background: #2563eb;
      color: white;
      border: none;
      border-radius: 10px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-back-dashboard:hover {
      background: #1d4ed8;
      box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
    }

    @media (max-width: 1400px) {
      .stat-grid {
        grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
      }
    }

    @media (max-width: 1024px) {
      .sidebar {
        width: 240px;
      }

      .app-header {
        left: 240px;
      }

      .main-content {
        margin-left: 240px;
        width: calc(100% - 240px);
        padding: 1.5rem;
      }

      .stat-grid {
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 1rem;
      }

      .module-section {
        padding: 1.5rem;
      }
    }

    @media (max-width: 768px) {
      .sidebar {
        transform: translateX(-100%);
      }

      .app-header {
        left: 0;
      }

      .main-content {
        margin-left: 0;
        width: 100%;
        padding: 1rem;
      }

      .stat-grid {
        grid-template-columns: 1fr;
      }

      .dashboard-hero {
        flex-direction: column;
        gap: 1rem;
        text-align: center;
      }

      .header-content {
        padding: 0 1rem;
      }

      .app-subtitle {
        display: none;
      }
    }
  `]
})
export class MenuComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  private userSubscription: Subscription = new Subscription();
  selectedItem: string | null = null;
  selectedModule: string | null = null;
  statsLoading = true;
  stats: { 
    users: UserStats; 
    formations: FormationStatistics; 
    stock: StockTraceStats;
    employees: EmployeeStats;
    clients: ClientStats;
    fournisseurs: FournisseurStats;
  } = {
    users: { totalUsers: 0, activeUsers: 0, inactiveUsers: 0, suspendedUsers: 0, pendingUsers: 0 },
    formations: {
      totalFormations: 0,
      formationsEnCours: 0,
      formationsPlanifiees: 0,
      formationsTerminees: 0,
      formationsAnnulees: 0,
      formationsReportees: 0,
      totalParticipants: 0,
      participantsActifs: 0,
      certificationsDelivrees: 0,
      totalContenus: 0,
      totalFormateurs: 0,
      formationsByType: {},
      formationsByLevel: {},
      formationsThisMonth: 0,
      certificationsThisMonth: 0,
      totalRevenue: 0,
      averageFormationPrice: 0,
      totalDomaines: 0,
      totalCategories: 0,
      totalSousCategories: 0,
      totalPlans: 0,
      activePlans: 0
    },
    stock: {
      totalActions: 0,
      actionsParType: {},
      actionsParUtilisateur: {},
      quantiteTotaleModifiee: 0,
      valeurTotaleModifiee: 0
    },
    employees: {
      totalEmployees: 0,
      activeEmployees: 0,
      inactiveEmployees: 0,
      suspendedEmployees: 0,
      pendingEmployees: 0,
      totalRoles: 0,
      totalPermissions: 0
    },
    clients: {
      totalClients: 0,
      clientsActifs: 0,
      prospects: 0,
      chiffreAffairesTotal: 0,
      facturenEnAttente: 0,
      tauxConversion: 0,
      valeurMoyenneClient: 0,
      interactionsRecentes: 0
    },
    fournisseurs: {
      totalFournisseurs: 0,
      fournisseursActifs: 0,
      totalDettes: 0,
      dettesNonPayees: 0,
      dettesEnRetard: 0,
      montantDettesNonPayees: 0
    }
  };
  menuItems = [
    {
      route: '/vehicules',
      name: 'Gestion Véhicules',
      icon: '🚗',
      description: 'Gérer le parc automobile de l\'entreprise',
      features: [
        'Inventaire des véhicules',
        'Maintenance et réparations',
        'Assurances et contrôles techniques',
        'Suivi des consommations',
        'Planning des réservations'
      ]
    },
    {
      route: '/rh',
      name: 'Gestion RH',
      icon: '👥',
      description: 'Gestion des ressources humaines',
      features: [
        'Gestion des employés',
        'Paie et avantages',
        'Congés et absences',
        'Évaluations de performance',
        'Formation et développement'
      ]
    },
    {
      route: '/employees',
      name: 'Gestion Utilisateurs',
      icon: '👤',
      description: 'Gestion des utilisateurs, rôles et permissions',
      features: [
        'Gestion des utilisateurs',
        'Gestion des rôles',
        'Gestion des permissions',
        'Attribution des accès',
        'Suivi des statuts'
      ]
    },
    {
      route: '/projets',
      name: 'Gestion Projets',
      icon: '📊',
      description: 'Gestion avancée des projets avec missions et tâches',
      features: [
        'Projets avec missions structurées',
        'Tâches détaillées par mission',
        'Affectation d\'équipes par mission/tâche',
        'Suivi des heures et budgets',
        'Commentaires et collaboration'
      ]
    },
    {
      route: '/formation',
      name: 'Formation',
      icon: '🎓',
      description: 'Gestion de la formation et développement des compétences',
      features: [
        'Catalogue de formations',
        'Planification des sessions',
        'Suivi des certifications',
        'Évaluation des compétences',
        'Rapports de progression'
      ]
    },
    
    {
      route: '/stock',
      name: 'Gestion du Stock',
      icon: '📦',
      description: 'Gestion de l\'inventaire et des stocks',
      features: [
        'Inventaire des articles',
        'Mouvements de stock',
        'Alertes de rupture',
        'Valorisation du stock',
        'Rapports d\'inventaire'
      ]
    },
    {
      route: '/prets',
      name: 'Gestion des Prêts',
      icon: '📋',
      description: 'Gestion des prêts de matériel',
      features: [
        'Prêts de matériel',
        'Suivi des retours',
        'Historique des prêts',
        'État du matériel',
        'Relances automatiques'
      ]
    },
    {
      route: '/sous-traitance',
      name: 'Gestion Sous-Traitance',
      icon: '🤝',
      description: 'Gestion des prestataires et contrats de sous-traitance',
      features: [
        'Gestion des prestataires',
        'Contrats de sous-traitance',
        'Suivi des prestations',
        'Évaluation des performances',
        'Facturation et paiements'
      ]
    },
    {
      route: '/dossiers',
      name: 'Gestion des Dossiers',
      icon: '📁',
      description: 'Gestion documentaire et archivage',
      features: [
        'Classement des documents',
        'Archivage numérique',
        'Contrôle d\'accès',
        'Versioning des fichiers',
        'Recherche avancée'
      ]
    },
    {
      route: '/sop',
      name: 'SOP - Processus Standard',
      icon: '💼',
      description: 'Gestion des procédures opérationnelles standardisées',
      features: [
        'Création de procédures SOP',
        'Workflow de validation',
        'Versioning des processus',
        'Formation aux procédures',
        'Audit et conformité'
      ]
    },
    // {
    //   route: '/facturation',
    //   name: 'Facturation',
    //   icon: '🧾',
    //   description: 'Gestion de la facturation et des devis',
    //   features: [
    //     'Création de devis',
    //     'Facturation automatisée',
    //     'Suivi des paiements',
    //     'Relances clients',
    //     'Rapports financiers'
    //   ]
    // },
    // {
    //   route: '/achats',
    //   name: 'Achats',
    //   icon: '🛒',
    //   description: 'Gestion des achats et approvisionnements',
    //   features: [
    //     'Demandes d\'achat',
    //     'Gestion des fournisseurs',
    //     'Bons de commande',
    //     'Réception marchandises',
    //     'Contrôle budgétaire'
    //   ]
    // },
    // {
    //   route: '/marketing',
    //   name: 'Marketing Digital',
    //   icon: '📱',
    //   description: 'Gestion du marketing digital et communication',
    //   features: [
    //     'Campagnes publicitaires',
    //     'Réseaux sociaux',
    //     'Email marketing',
    //     'Analytics et reporting',
    //     'Gestion de contenu'
    //   ]
    // },
    // {
    //   route: '/outillage',
    //   name: 'Gestion des Outils',
    //   icon: '🔧',
    //   description: 'Gestion de l\'outillage et équipements',
    //   features: [
    //     'Inventaire des outils',
    //     'Suivi des prêts et retours',
    //     'Maintenance préventive',
    //     'État et disponibilité',
    //     'Historique d\'utilisation'
    //   ]
    // },
    // {
    //   route: '/immobilier',
    //   name: 'Gestion Immobilier',
    //   icon: '🏢',
    //   description: 'Gestion des biens immobiliers et locations',
    //   features: [
    //     'Portefeuille immobilier',
    //     'Contrats de location',
    //     'Suivi des loyers',
    //     'Maintenance des biens',
    //     'Rapports financiers'
    //   ]
    // },
    {
      route: '/fournisseurs',
      name: 'Gestion Fournisseurs',
      icon: '🏭',
      description: 'Gestion des fournisseurs et relations commerciales',
      features: [
        'Base de données fournisseurs',
        'Évaluation des performances',
        'Contrats et négociations',
        'Suivi des commandes',
        'Analyse des coûts'
      ]
    },
    {
      route: '/clients',
      name: 'Gestion Clients',
      icon: '👤',
      description: 'Gestion de la relation client (CRM)',
      features: [
        'Base de données clients',
        'Historique des interactions',
        'Suivi des opportunités',
        'Gestion des contrats',
        'Analyse de satisfaction'
      ]
    }
  ];

  constructor(
    private authService: AuthService,
    private router: Router,
    private userService: UserManagementService,
    private formationService: FormationService,
    private stockTraceService: StockTraceService,
    private employeeService: EmployeeService,
    private clientService: ClientService,
    private fournisseurService: FournisseurService
  ) {}

  ngOnInit(): void {
    // Subscribe to current user changes
    this.userSubscription = this.authService.currentUser$.subscribe(
      user => {
        this.currentUser = user;
      }
    );

    this.loadStats();
  }

  ngOnDestroy(): void {
    this.userSubscription.unsubscribe();
  }

  getUserDisplayName(): string {
    if (!this.currentUser) return 'Utilisateur';
    
    if (this.currentUser.prenom && this.currentUser.nom) {
      return `${this.currentUser.prenom} ${this.currentUser.nom}`;
    }
    
    return this.currentUser.nomUtilisateur || 'Utilisateur';
  }

  selectMenuItem(item: any): void {
    this.selectedItem = item.route;
    this.selectedModule = item.route;
  }

  backToDashboard(): void {
    this.selectedModule = null;
    this.selectedItem = null;
  }

  getModuleName(route: string): string {
    const item = this.menuItems.find(m => m.route === route);
    return item ? item.name : route;
  }

  refreshStats(): void {
    this.loadStats();
  }

  private loadStats(): void {
    this.statsLoading = true;

    forkJoin({
      users: this.userService.getUserStats().pipe(catchError(() => of(this.stats.users))),
      formations: this.formationService.getStatistics().pipe(catchError(() => of(this.stats.formations))),
      stock: this.stockTraceService.getStockTraceStats().pipe(catchError(() => of(this.stats.stock))),
      employees: this.employeeService.getEmployeeStats().pipe(catchError(() => of(this.stats.employees))),
      clients: of(this.clientService.getClientStats()),
      fournisseurs: this.fournisseurService.getStats().pipe(catchError(() => of(this.stats.fournisseurs)))
    }).subscribe(result => {
      this.stats = result;
      this.statsLoading = false;
    });
  }

  formatCurrency(value: number | undefined): string {
    if (!value) return '0';
    return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 }).format(value);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  goToMainMenu(): void {
    this.selectedItem = null;
    this.selectedModule = null;
  }
}