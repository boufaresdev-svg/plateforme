import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SOPService } from '../../services/sop.service';
import { 
  SOP, 
  EtapeSOP, 
  DocumentSOP, 
  CategorieSOP, 
  StatutSOP, 
  TypeDocument, 
  SOPStats 
} from '../../models/sop.model';

@Component({
  selector: 'app-sop-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="sop-management">
      <!-- Header moderne -->
      <div class="header-section">
        <div class="header-content">
          <div class="header-title">
            <h1>💼 Gestion SOP</h1>
            <p class="subtitle">Gérez vos procédures opérationnelles standardisées</p>
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
              [class.active]="currentView === 'sops'"
              (click)="setView('sops')"
            >
              <span class="icon">💼</span>
              SOPs
            </button>
            <button 
              class="nav-pill" 
              [class.active]="currentView === 'etapes'"
              (click)="setView('etapes')"
            >
              <span class="icon">✅</span>
              Étapes
            </button>
            <button 
              class="btn-primary"
              (click)="showAddSOPForm()"
              *ngIf="currentView === 'sops'"
            >
              <span class="icon">➕</span>
              Nouvelle SOP
            </button>
          </div>
        </div>
      </div>

      <!-- Dashboard -->
      <div class="dashboard-view" *ngIf="currentView === 'dashboard'">
        <div class="stats-container">
          <div class="stat-card primary">
            <div class="stat-icon">💼</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalSOPs }}</div>
              <div class="stat-label">Total SOPs</div>
            </div>
          </div>

          <div class="stat-card success">
            <div class="stat-icon">✅</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.sopsActifs }}</div>
              <div class="stat-label">SOPs Actifs</div>
            </div>
          </div>

          <div class="stat-card warning">
            <div class="stat-icon">📝</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.sopsEnRevision }}</div>
              <div class="stat-label">En Révision</div>
            </div>
          </div>

          <div class="stat-card info">
            <div class="stat-icon">📄</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.documentsAttaches }}</div>
              <div class="stat-label">Documents</div>
            </div>
          </div>

          <div class="stat-card danger" *ngIf="stats.revisionsEnRetard > 0">
            <div class="stat-icon">⚠️</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.revisionsEnRetard }}</div>
              <div class="stat-label">Révisions en Retard</div>
            </div>
          </div>
        </div>

        <!-- SOPs récentes -->
        <div class="recent-section">
          <div class="section-header">
            <h2>SOPs Récentes</h2>
            <button class="btn-outline" (click)="setView('sops')">
              Voir tout
              <span class="icon">→</span>
            </button>
          </div>
          
          <div class="sops-grid">
            <div 
              *ngFor="let sop of sops.slice(0, 6)" 
              class="sop-card modern"
              (click)="viewSOPDetails(sop)"
            >
              <div class="card-header">
                <div class="sop-info">
                  <h3>{{ sop.titre }}</h3>
                  <span class="sop-code">{{ sop.code }}</span>
                </div>
                <div class="status-indicator">
                  <span 
                    class="status-dot"
                    [class]="getStatusClass(sop.statut)"
                  ></span>
                </div>
              </div>
              
              <div class="card-content">
                <div class="sop-mission">{{ sop.description }}</div>
                <div class="sop-departement">{{ sop.departement }}</div>
                <div class="sop-version">Version {{ sop.version }}</div>
                <div class="sop-etapes">
                  {{ getSopEtapes(sop.id).length }} étape(s)
                </div>
                <div class="sop-revision">
                  Révision: {{ sop.prochainRevision | date:'dd/MM/yyyy' }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Liste des SOPs -->
      <div class="sops-view" *ngIf="currentView === 'sops'">
        <div class="table-container">
          <div class="table-header">
            <div class="search-bar">
              <span class="search-icon">🔍</span>
              <input 
                type="text" 
                placeholder="Rechercher une SOP..."
                [(ngModel)]="searchTerm"
                class="search-input"
              >
            </div>
            <div class="table-actions">
              <button class="btn-outline">
                <span class="icon">📊</span>
                Exporter
              </button>
            </div>
          </div>

          <div class="modern-table">
            <div class="table-row header">
              <div class="table-cell">SOP</div>
              <div class="table-cell">Département</div>
              <div class="table-cell">Version</div>
              <div class="table-cell">Statut</div>
              <div class="table-cell">Révision</div>
              <div class="table-cell">Étapes</div>
              <div class="table-cell">Actions</div>
            </div>

            <div 
              *ngFor="let sop of filteredSOPs" 
              class="table-row clickable"
              (click)="viewSOPDetails(sop)"
            >
              <div class="table-cell sop-info">
                <div class="sop-avatar">💼</div>
                <div class="sop-details">
                  <div class="sop-name">{{ sop.titre }}</div>
                  <div class="sop-code">{{ sop.code }}</div>
                </div>
              </div>
              
              <div class="table-cell">{{ sop.departement }}</div>
              
              <div class="table-cell">
                <span class="version">v{{ sop.version }}</span>
              </div>
              
              <div class="table-cell">
                <span 
                  class="status-badge"
                  [class]="getStatusClass(sop.statut)"
                >
                  {{ sop.statut }}
                </span>
              </div>
              
              <div class="table-cell">
                <span 
                  class="revision-date"
                  [class.overdue]="isRevisionOverdue(sop)"
                >
                  {{ sop.prochainRevision | date:'dd/MM/yyyy' }}
                </span>
              </div>
              
              <div class="table-cell">
                <span class="etapes-count">{{ getSopEtapes(sop.id).length }}</span>
              </div>
              
              <div class="table-cell actions" (click)="$event.stopPropagation()">
                <div class="action-buttons">
                  <button class="action-btn" (click)="editSOP(sop)" title="Modifier">
                    ✏️
                  </button>
                  <button class="action-btn" (click)="manageEtapes(sop)" title="Étapes">
                    ✅
                  </button>
                  <button class="action-btn danger" (click)="deleteSOP(sop.id)" title="Supprimer">
                    🗑️
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Détails SOP -->
      <div class="details-view" *ngIf="currentView === 'details' && selectedSOP">
        <div class="details-header">
          <button class="btn-back" (click)="goBack()">
            <span class="icon">←</span>
            Retour
          </button>
          <div class="sop-title">
            <h1>{{ selectedSOP.titre }}</h1>
            <span class="sop-code-large">{{ selectedSOP.code }} - v{{ selectedSOP.version }}</span>
          </div>
          <button class="btn-primary" (click)="editSOP(selectedSOP)">
            <span class="icon">✏️</span>
            Modifier
          </button>
        </div>

        <div class="details-grid">
          <!-- Informations générales -->
          <div class="info-card">
            <h3>Informations Générales</h3>
            <div class="info-list">
              <div class="info-item">
                <span class="info-label">Mission</span>
                <span class="info-value">{{ selectedSOP.description }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Département</span>
                <span class="info-value">{{ selectedSOP.departement }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Processus</span>
                <span class="info-value">{{ selectedSOP.processus }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Auteur</span>
                <span class="info-value">{{ selectedSOP.auteur }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Approbateur</span>
                <span class="info-value">{{ selectedSOP.approbateur }}</span>
              </div>
            </div>
          </div>

          <!-- Statut et révisions -->
          <div class="status-card">
            <h3>Statut et Révisions</h3>
            <div class="status-list">
              <div class="status-item">
                <div class="status-icon" [class]="getStatusClass(selectedSOP.statut)">
                  📋
                </div>
                <div class="status-content">
                  <span class="status-title">Statut actuel</span>
                  <span class="status-value">{{ selectedSOP.statut }}</span>
                </div>
              </div>
              
              <div class="status-item">
                <div class="status-icon">📅</div>
                <div class="status-content">
                  <span class="status-title">Prochaine révision</span>
                  <span 
                    class="status-value"
                    [class.overdue]="isRevisionOverdue(selectedSOP)"
                  >
                    {{ selectedSOP.prochainRevision | date:'dd/MM/yyyy' }}
                  </span>
                </div>
              </div>
              
              <div class="status-item" *ngIf="selectedSOP.dateApprobation">
                <div class="status-icon">✅</div>
                <div class="status-content">
                  <span class="status-title">Date d'approbation</span>
                  <span class="status-value">{{ selectedSOP.dateApprobation | date:'dd/MM/yyyy' }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Checklist des étapes -->
          <div class="etapes-card">
            <div class="card-header-with-action">
              <h3>Checklist des Étapes</h3>
              <button class="btn-primary" (click)="showAddEtapeForm()">
                <span class="icon">➕</span>
                Ajouter
              </button>
            </div>
            
            <div class="etapes-list">
              <div 
                *ngFor="let etape of getSopEtapes(selectedSOP.id); let i = index" 
                class="etape-item"
              >
                <div class="etape-checkbox">
                  <input 
                    type="checkbox" 
                    [id]="'etape-' + etape.id"
                    [(ngModel)]="etape.completed"
                    (change)="toggleEtapeCompletion(etape)"
                  >
                  <label [for]="'etape-' + etape.id" class="checkbox-label"></label>
                </div>
                <div class="etape-content">
                  <div class="etape-number">{{ etape.numero }}</div>
                  <div class="etape-details">
                    <div class="etape-title">{{ etape.titre }}</div>
                    <div class="etape-description">{{ etape.description }}</div>
                    <div class="etape-meta">
                      <span class="etape-responsable">{{ etape.responsable }}</span>
                      <span class="etape-duree">{{ etape.dureeEstimee }} min</span>
                    </div>
                  </div>
                </div>
                <div class="etape-actions">
                  <button class="action-btn" (click)="editEtape(etape)" title="Modifier">
                    ✏️
                  </button>
                </div>
              </div>
              
              <div *ngIf="getSopEtapes(selectedSOP.id).length === 0" class="empty-state">
                <div class="empty-icon">✅</div>
                <p>Aucune étape définie</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Formulaire SOP -->
      <div class="form-view" *ngIf="currentView === 'form'">
        <div class="form-container">
          <div class="form-header">
            <button class="btn-back" (click)="cancelForm()">
              <span class="icon">←</span>
              Retour
            </button>
            <h1>{{ isEditMode ? 'Modifier' : 'Nouvelle' }} SOP</h1>
          </div>

          <form (ngSubmit)="saveSOP()" #sopForm="ngForm" class="modern-form">
            <div class="form-section">
              <h3>Informations Générales</h3>
              <div class="form-grid">
                <div class="form-group">
                  <label class="form-label">Titre de la SOP</label>
                  <input 
                    type="text" 
                    class="form-input"
                    [(ngModel)]="sopFormData.titre"
                    name="titre"
                    required
                    placeholder="Procédure de sauvegarde des données"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Code SOP</label>
                  <input 
                    type="text" 
                    class="form-input"
                    [(ngModel)]="sopFormData.code"
                    name="code"
                    required
                    placeholder="SOP-001"
                    [readonly]="isEditMode"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Version</label>
                  <input 
                    type="text" 
                    class="form-input"
                    [(ngModel)]="sopFormData.version"
                    name="version"
                    required
                    placeholder="1.0"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Catégorie</label>
                  <select 
                    class="form-input"
                    [(ngModel)]="sopFormData.categorie"
                    name="categorie"
                    required
                  >
                    <option value="">Sélectionner</option>
                    <option *ngFor="let categorie of categoriesSOPs" [value]="categorie">
                      {{ categorie }}
                    </option>
                  </select>
                </div>

                <div class="form-group">
                  <label class="form-label">Département</label>
                  <input 
                    type="text" 
                    class="form-input"
                    [(ngModel)]="sopFormData.departement"
                    name="departement"
                    required
                    placeholder="IT"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Processus</label>
                  <input 
                    type="text" 
                    class="form-input"
                    [(ngModel)]="sopFormData.processus"
                    name="processus"
                    required
                    placeholder="Sauvegarde"
                  />
                </div>
              </div>
            </div>

            <div class="form-section">
              <h3>Mission et Description</h3>
              <div class="form-group">
                <label class="form-label">Mission / Description</label>
                <textarea 
                  class="form-input"
                  [(ngModel)]="sopFormData.description"
                  name="description"
                  required
                  rows="4"
                  placeholder="Décrivez la mission et l'objectif de cette procédure..."
                ></textarea>
              </div>
            </div>

            <div class="form-section">
              <h3>Responsabilités</h3>
              <div class="form-grid">
                <div class="form-group">
                  <label class="form-label">Auteur</label>
                  <input 
                    type="text" 
                    class="form-input"
                    [(ngModel)]="sopFormData.auteur"
                    name="auteur"
                    required
                    placeholder="Mohamed Ben Ahmed"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Approbateur</label>
                  <input 
                    type="text" 
                    class="form-input"
                    [(ngModel)]="sopFormData.approbateur"
                    name="approbateur"
                    required
                    placeholder="Directeur IT"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Prochaine révision</label>
                  <input 
                    type="date" 
                    class="form-input"
                    [(ngModel)]="sopFormData.prochainRevision"
                    name="prochainRevision"
                    required
                  />
                </div>
              </div>
            </div>

            <div class="form-actions">
              <button type="button" class="btn-outline" (click)="cancelForm()">
                Annuler
              </button>
              <button type="submit" class="btn-primary" [disabled]="sopForm.invalid">
                <span class="icon">💾</span>
                {{ isEditMode ? 'Modifier' : 'Créer' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .sop-management {
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

    .btn-back {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.5rem 1rem;
      background: #f3f4f6;
      color: #374151;
      border: none;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-back:hover {
      background: #e5e7eb;
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

    .sops-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
      gap: 1.5rem;
    }

    .sop-card.modern {
      background: white;
      border-radius: 16px;
      padding: 1.5rem;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      cursor: pointer;
      transition: all 0.3s ease;
      border: 2px solid transparent;
    }

    .sop-card.modern:hover {
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

    .sop-info h3 {
      font-size: 1.2rem;
      font-weight: 600;
      color: #1f2937;
      margin: 0 0 0.25rem 0;
    }

    .sop-code {
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

    .status-dot.brouillon { background: #6b7280; }
    .status-dot.en-revision { background: #f59e0b; }
    .status-dot.approuve { background: #3b82f6; }
    .status-dot.actif { background: #10b981; }
    .status-dot.obsolete { background: #ef4444; }
    .status-dot.archive { background: #9ca3af; }

    .card-content {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .sop-mission {
      color: #6b7280;
      font-size: 0.9rem;
      line-height: 1.4;
    }

    .sop-departement, .sop-version, .sop-etapes, .sop-revision {
      color: #6b7280;
      font-size: 0.8rem;
    }

    /* Tables */
    .sops-view {
      width: 100%;
      padding: 0 2rem;
    }

    .table-container {
      background: white;
      border-radius: 16px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      overflow: hidden;
    }

    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1.5rem 2rem;
      border-bottom: 1px solid #f3f4f6;
    }

    .search-bar {
      position: relative;
      flex: 1;
      max-width: 400px;
    }

    .search-icon {
      position: absolute;
      left: 1rem;
      top: 50%;
      transform: translateY(-50%);
      color: #9ca3af;
    }

    .search-input {
      width: 100%;
      padding: 0.75rem 1rem 0.75rem 3rem;
      border: 2px solid #f3f4f6;
      border-radius: 12px;
      font-size: 0.9rem;
      transition: all 0.3s ease;
    }

    .search-input:focus {
      outline: none;
      border-color: #667eea;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }

    .table-actions {
      display: flex;
      gap: 1rem;
    }

    .modern-table {
      display: flex;
      flex-direction: column;
    }

    .table-row {
      display: grid;
      grid-template-columns: 2fr 1.2fr 1fr 1fr 1.2fr 1fr 1fr;
      align-items: center;
      padding: 1.5rem 2rem;
      border-bottom: 1px solid #f3f4f6;
      transition: all 0.3s ease;
    }

    .table-row.header {
      background: #f8fafc;
      font-weight: 600;
      color: #374151;
      border-bottom: 2px solid #e5e7eb;
    }

    .table-row.clickable:hover {
      background: #f8fafc;
      cursor: pointer;
    }

    .table-cell {
      display: flex;
      align-items: center;
    }

    .sop-info {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .sop-avatar {
      width: 40px;
      height: 40px;
      background: #f3f4f6;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.2rem;
    }

    .sop-details {
      display: flex;
      flex-direction: column;
    }

    .sop-name {
      font-weight: 600;
      color: #1f2937;
    }

    .version {
      font-weight: 600;
      color: #1f2937;
    }

    .status-badge {
      padding: 0.5rem 1rem;
      border-radius: 20px;
      font-size: 0.8rem;
      font-weight: 500;
    }

    .status-badge.brouillon { background: #f3f4f6; color: #374151; }
    .status-badge.en-revision { background: #fef3c7; color: #92400e; }
    .status-badge.approuve { background: #dbeafe; color: #1e40af; }
    .status-badge.actif { background: #d1fae5; color: #065f46; }
    .status-badge.obsolete { background: #fee2e2; color: #991b1b; }
    .status-badge.archive { background: #f3f4f6; color: #6b7280; }

    .revision-date {
      font-weight: 500;
      color: #1f2937;
    }

    .revision-date.overdue {
      color: #ef4444;
      font-weight: 600;
    }

    .etapes-count {
      font-weight: 600;
      color: #1f2937;
    }

    .action-buttons {
      display: flex;
      gap: 0.5rem;
    }

    .action-btn {
      width: 32px;
      height: 32px;
      border: none;
      border-radius: 8px;
      background: #f3f4f6;
      cursor: pointer;
      transition: all 0.3s ease;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .action-btn:hover {
      background: #e5e7eb;
      transform: scale(1.1);
    }

    .action-btn.danger:hover {
      background: #fee2e2;
    }

    /* Détails SOP */
    .details-view {
      width: 100%;
      padding: 0 2rem;
    }

    .details-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    .sop-title h1 {
      font-size: 2rem;
      font-weight: 700;
      color: #1f2937;
      margin: 0;
    }

    .sop-code-large {
      color: #6b7280;
      font-size: 1.1rem;
    }

    .details-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
      gap: 2rem;
    }

    .info-card, .status-card, .etapes-card {
      background: white;
      border-radius: 16px;
      padding: 2rem;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    }

    .info-card h3, .status-card h3, .etapes-card h3 {
      font-size: 1.3rem;
      font-weight: 600;
      color: #1f2937;
      margin: 0 0 1.5rem 0;
    }

    .info-list {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem;
      background: #f8fafc;
      border-radius: 12px;
    }

    .info-label {
      color: #6b7280;
      font-weight: 500;
    }

    .info-value {
      font-weight: 600;
      color: #1f2937;
    }

    .status-list {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .status-item {
      display: flex;
      align-items: center;
      gap: 1rem;
      padding: 1rem;
      background: #f8fafc;
      border-radius: 12px;
    }

    .status-icon {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      background: #e5e7eb;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.2rem;
    }

    .status-icon.actif { background: #d1fae5; }
    .status-icon.en-revision { background: #fef3c7; }
    .status-icon.brouillon { background: #f3f4f6; }

    .status-content {
      flex: 1;
      display: flex;
      flex-direction: column;
    }

    .status-title {
      font-weight: 600;
      color: #1f2937;
    }

    .status-value {
      color: #6b7280;
      font-size: 0.9rem;
    }

    .status-value.overdue {
      color: #dc2626;
      font-weight: 600;
    }

    .card-header-with-action {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
    }

    .etapes-list {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .etape-item {
      display: flex;
      align-items: flex-start;
      gap: 1rem;
      padding: 1rem;
      background: #f8fafc;
      border-radius: 12px;
      transition: all 0.3s ease;
    }

    .etape-item:hover {
      background: #f1f5f9;
    }

    .etape-checkbox {
      position: relative;
      margin-top: 0.25rem;
    }

    .etape-checkbox input[type="checkbox"] {
      opacity: 0;
      position: absolute;
      width: 20px;
      height: 20px;
      cursor: pointer;
    }

    .checkbox-label {
      display: block;
      width: 20px;
      height: 20px;
      border: 2px solid #d1d5db;
      border-radius: 4px;
      background: white;
      cursor: pointer;
      transition: all 0.3s ease;
      position: relative;
    }

    .etape-checkbox input[type="checkbox"]:checked + .checkbox-label {
      background: #10b981;
      border-color: #10b981;
    }

    .etape-checkbox input[type="checkbox"]:checked + .checkbox-label:after {
      content: "✓";
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      color: white;
      font-size: 12px;
      font-weight: bold;
    }

    .etape-number {
      width: 30px;
      height: 30px;
      border-radius: 50%;
      background: #667eea;
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 600;
      font-size: 0.9rem;
      flex-shrink: 0;
    }

    .etape-details {
      flex: 1;
    }

    .etape-title {
      font-weight: 600;
      color: #1f2937;
      margin-bottom: 0.25rem;
    }

    .etape-description {
      color: #6b7280;
      font-size: 0.9rem;
      margin-bottom: 0.5rem;
    }

    .etape-meta {
      display: flex;
      gap: 1rem;
      font-size: 0.8rem;
      color: #6b7280;
    }

    .etape-responsable {
      font-weight: 500;
    }

    .etape-actions {
      display: flex;
      gap: 0.5rem;
    }

    .empty-state {
      text-align: center;
      padding: 2rem;
      color: #6b7280;
    }

    .empty-icon {
      font-size: 3rem;
      margin-bottom: 1rem;
      opacity: 0.5;
    }

    /* Formulaire */
    .form-view {
      width: 100%;
      max-width: 800px;
      margin: 0 auto;
      padding: 0 2rem;
    }

    .form-container {
      background: white;
      border-radius: 16px;
      padding: 2rem;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    }

    .form-header {
      display: flex;
      align-items: center;
      gap: 1rem;
      margin-bottom: 2rem;
    }

    .form-header h1 {
      font-size: 1.8rem;
      font-weight: 700;
      color: #1f2937;
      margin: 0;
    }

    .modern-form {
      display: flex;
      flex-direction: column;
      gap: 2rem;
    }

    .form-section {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
    }

    .form-section h3 {
      font-size: 1.2rem;
      font-weight: 600;
      color: #1f2937;
      margin: 0;
      padding-bottom: 0.5rem;
      border-bottom: 2px solid #f3f4f6;
    }

    .form-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 1.5rem;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .form-label {
      font-weight: 600;
      color: #374151;
      font-size: 0.9rem;
    }

    .form-input {
      padding: 0.75rem 1rem;
      border: 2px solid #f3f4f6;
      border-radius: 12px;
      font-size: 0.9rem;
      transition: all 0.3s ease;
    }

    .form-input:focus {
      outline: none;
      border-color: #667eea;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }

    .form-actions {
      display: flex;
      justify-content: flex-end;
      gap: 1rem;
      padding-top: 1rem;
      border-top: 1px solid #f3f4f6;
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

      .dashboard-view, .sops-view, .details-view {
        padding: 0 1rem;
      }

      .table-row {
        grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1fr 80px;
        padding: 1rem;
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

      .sops-grid {
        grid-template-columns: 1fr;
      }

      .details-grid {
        grid-template-columns: 1fr;
        gap: 1rem;
      }

      .table-header {
        flex-direction: column;
        gap: 1rem;
        align-items: stretch;
      }

      .form-grid {
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

      .sop-card.modern {
        padding: 1rem;
      }

      .form-view {
        padding: 0 0.5rem;
      }
    }
  `]
})
export class SOPManagementComponent implements OnInit {
  sops: SOP[] = [];
  filteredSOPs: SOP[] = [];
  etapes: EtapeSOP[] = [];
  documents: DocumentSOP[] = [];
  stats: SOPStats = {
    totalSOPs: 0,
    sopsActifs: 0,
    sopsEnRevision: 0,
    sopsObsoletes: 0,
    formationsLiees: 0,
    tauxConformite: 0,
    revisionsEnRetard: 0,
    documentsAttaches: 0
  };

  currentView: 'dashboard' | 'sops' | 'etapes' | 'details' | 'form' = 'dashboard';
  selectedSOP: SOP | null = null;
  isEditMode = false;
  searchTerm = '';

  // Formulaires
  sopFormData: any = {};

  // Énumérations
  categoriesSOPs = Object.values(CategorieSOP);
  statutsSOPs = Object.values(StatutSOP);

  constructor(private sopService: SOPService) {}

  goBackToMenu(): void {
    window.location.href = '/menu';
  }

  ngOnInit(): void {
    this.sopService.getSOPs().subscribe(sops => {
      this.sops = sops;
      this.filteredSOPs = sops;
      this.stats = this.sopService.getSOPStats();
    });

    this.sopService.etapes$.subscribe(etapes => {
      this.etapes = etapes;
    });

    this.sopService.documents$.subscribe(documents => {
      this.documents = documents;
    });
  }

  get searchTerm_() {
    return this.searchTerm;
  }

  set searchTerm_(value: string) {
    this.searchTerm = value;
    this.filterSOPs();
  }

  filterSOPs(): void {
    if (!this.searchTerm) {
      this.filteredSOPs = this.sops;
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredSOPs = this.sops.filter(sop =>
        sop.titre.toLowerCase().includes(term) ||
        sop.code.toLowerCase().includes(term) ||
        sop.departement.toLowerCase().includes(term)
      );
    }
  }

  setView(view: 'dashboard' | 'sops' | 'etapes' | 'details' | 'form'): void {
    this.currentView = view;
  }

  goBack(): void {
    this.currentView = 'sops';
    this.selectedSOP = null;
  }

  viewSOPDetails(sop: SOP): void {
    this.selectedSOP = sop;
    this.currentView = 'details';
  }

  showAddSOPForm(): void {
    this.isEditMode = false;
    this.sopFormData = {
      titre: '',
      description: '',
      code: this.sopService.generateSOPCode(),
      version: '1.0',
      categorie: CategorieSOP.QUALITE,
      departement: '',
      processus: '',
      statut: StatutSOP.BROUILLON,
      auteur: '',
      approbateur: '',
      prochainRevision: '',
      formations: [],
      tags: []
    };
    this.currentView = 'form';
  }

  editSOP(sop: SOP): void {
    this.isEditMode = true;
    this.selectedSOP = sop;
    this.sopFormData = {
      ...sop,
      prochainRevision: this.formatDateForInput(sop.prochainRevision)
    };
    this.currentView = 'form';
  }

  saveSOP(): void {
    const sopData = {
      ...this.sopFormData,
      prochainRevision: new Date(this.sopFormData.prochainRevision)
    };

    if (this.isEditMode && this.selectedSOP) {
      this.sopService.updateSOP(this.selectedSOP.id, sopData);
    } else {
      this.sopService.addSOP(sopData);
    }

    this.currentView = 'sops';
  }

  cancelForm(): void {
    this.currentView = 'sops';
    this.selectedSOP = null;
    this.isEditMode = false;
  }

  deleteSOP(id: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette SOP ?')) {
      this.sopService.deleteSOP(id);
    }
  }

  manageEtapes(sop: SOP): void {
    this.selectedSOP = sop;
    this.currentView = 'details';
  }

  showAddEtapeForm(): void {
    // À implémenter
  }

  editEtape(etape: EtapeSOP): void {
    // À implémenter
  }

  toggleEtapeCompletion(etape: any): void {
    // Logique pour marquer une étape comme complétée
    console.log('Étape complétée:', etape);
  }

  getSopEtapes(sopId: string): EtapeSOP[] {
    return this.sopService.getEtapesBySOPId(sopId);
  }

  getStatusClass(statut: StatutSOP): string {
    return statut.toLowerCase().replace(/[^a-z]/g, '-');
  }

  isRevisionOverdue(sop: SOP): boolean {
    return new Date(sop.prochainRevision) < new Date();
  }

  private formatDateForInput(date: Date): string {
    return new Date(date).toISOString().split('T')[0];
  }
}