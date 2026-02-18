import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Fournisseur, DettesFournisseur, FournisseurStats } from '../../../models/fournisseur/fournisseur.model';

@Component({
  selector: 'app-fournisseur-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './fournisseur-dashboard.component.html',
  styleUrls: ['./fournisseur-dashboard.component.css']
})
export class FournisseurDashboardComponent {
  // Expose Math to template
  Math = Math;

  @Input() stats: FournisseurStats = {
    totalFournisseurs: 0,
    fournisseursActifs: 0,
    totalDettes: 0,
    dettesNonPayees: 0,
    dettesEnRetard: 0,
    montantDettesNonPayees: 0
  };

  @Input() fournisseurs: Fournisseur[] = [];
  @Input() dettes: DettesFournisseur[] = [];

  @Output() viewChange = new EventEmitter<'fournisseurs' | 'dettes'>();
  @Output() fournisseurSelect = new EventEmitter<Fournisseur>();

  /**
   * Calculate percentage of active suppliers
   */
  getActivePercentage(): number {
    if (this.stats.totalFournisseurs === 0) return 0;
    return Math.round((this.stats.fournisseursActifs / this.stats.totalFournisseurs) * 100);
  }

  /**
   * Get top fournisseurs by total debt amount
   */
  getTopFournisseurs(): Fournisseur[] {
    return this.fournisseurs
      .sort((a, b) => {
        // Sort by totalDettes
        const aDettes = (a.totalDettes?.parsedValue ?? 0);
        const bDettes = (b.totalDettes?.parsedValue ?? 0);
        return bDettes - aDettes;
      })
      .slice(0, 5);
  }

  /**
   * Get recent dettes (last 10)
   */
  getRecentDettes(): DettesFournisseur[] {
    return this.dettes
      .sort((a, b) => new Date(b.datePaiementPrevue).getTime() - new Date(a.datePaiementPrevue).getTime())
      .slice(0, 10);
  }

  /**
   * Get rank class for top fournisseurs
   */
  getRankClass(index: number): string {
    switch (index) {
      case 0: return 'rank-gold';
      case 1: return 'rank-silver';
      case 2: return 'rank-bronze';
      default: return 'rank-default';
    }
  }

  /**
   * Get dette status CSS class
   */
  getDetteStatusClass(dette: DettesFournisseur): string {
    if (dette.estPaye) {
      return 'status-paid';
    } else if (this.isOverdue(dette)) {
      return 'status-overdue';
    } else {
      return 'status-pending';
    }
  }

  /**
   * Check if a dette is overdue
   */
  isOverdue(dette: DettesFournisseur): boolean {
    if (dette.estPaye) return false;
    const today = new Date();
    const dueDate = new Date(dette.datePaiementPrevue);
    return today > dueDate;
  }

  /**
   * Get fournisseur name by ID
   */
  getFournisseurName(fournisseurId: string): string {
    const fournisseur = this.fournisseurs.find(f => f.id === fournisseurId);
    return fournisseur ? fournisseur.nom : 'Inconnu';
  }

  /**
   * Navigate to fournisseurs view
   */
  goToFournisseurs(): void {
    this.viewChange.emit('fournisseurs');
  }

  /**
   * Navigate to dettes view
   */
  goToDettes(): void {
    this.viewChange.emit('dettes');
  }

  /**
   * View fournisseur details
   */
  viewFournisseurDetails(fournisseur: Fournisseur): void {
    this.fournisseurSelect.emit(fournisseur);
  }
}