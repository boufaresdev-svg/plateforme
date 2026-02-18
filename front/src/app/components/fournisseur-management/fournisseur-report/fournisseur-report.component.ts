import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RapportDettesEntreprise, RapportFilterParams } from '../../../models/fournisseur/rapport-dettes.model';
import { Fournisseur } from '../../../models/fournisseur/fournisseur.model';

@Component({
  selector: 'app-fournisseur-report',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './fournisseur-report.component.html',
  styleUrls: ['./fournisseur-report.component.css']
})
export class FournisseurReportComponent implements OnInit {
  // Expose Math to template
  Math = Math;

  @Input() fournisseurs: Fournisseur[] = [];
  @Input() rapportDettes: RapportDettesEntreprise | null = null;
  @Input() isLoadingRapport = false;
  @Input() rapportDateDebut = '';
  @Input() rapportDateFin = '';
  @Input() rapportInclureDettesPayees = false;
  @Input() selectedFournisseurIds: string[] = [];

  @Output() rapportDateDebutChange = new EventEmitter<string>();
  @Output() rapportDateFinChange = new EventEmitter<string>();
  @Output() rapportInclureDettesPayeesChange = new EventEmitter<boolean>();
  @Output() selectedFournisseurIdsChange = new EventEmitter<string[]>();
  @Output() applyFilter = new EventEmitter<RapportFilterParams>();
  @Output() clearFilter = new EventEmitter<void>();
  @Output() printRapport = new EventEmitter<void>();
  @Output() exportRapport = new EventEmitter<string>(); // 'pdf' | 'excel'

  // Filter options
  showAdvancedFilters = false;
  selectedPeriodType: 'custom' | 'last30days' | 'last3months' | 'last6months' | 'lastyear' = 'custom';
  periodOptions: ('custom' | 'last30days' | 'last3months' | 'last6months' | 'lastyear')[] = [
    'last30days', 'last3months', 'last6months', 'lastyear', 'custom'
  ];

  constructor() {}

  ngOnInit(): void {
    // Initialize with default date range if not set
    if (!this.rapportDateDebut && !this.rapportDateFin) {
      this.setDefaultDateRange();
    }
  }

  /**
   * Set default date range (last 30 days)
   */
  private setDefaultDateRange(): void {
    const today = new Date();
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(today.getDate() - 30);
    
    this.rapportDateDebut = thirtyDaysAgo.toISOString().split('T')[0];
    this.rapportDateFin = today.toISOString().split('T')[0];
    
    this.rapportDateDebutChange.emit(this.rapportDateDebut);
    this.rapportDateFinChange.emit(this.rapportDateFin);
  }

  /**
   * Handle date debut change
   */
  onRapportDateDebutChange(value: string): void {
    this.rapportDateDebut = value;
    this.rapportDateDebutChange.emit(value);
    
    // Validate date range
    if (value && this.rapportDateFin) {
      const dateDebut = new Date(value);
      const dateFin = new Date(this.rapportDateFin);
      
      if (dateDebut > dateFin) {
        // If start date is after end date, adjust end date
        this.rapportDateFin = value;
        this.rapportDateFinChange.emit(value);
      }
      
      // Auto-apply filter when date changes
      setTimeout(() => this.applyRapportFilter(), 300);
    }
  }

  /**
   * Handle date fin change
   */
  onRapportDateFinChange(value: string): void {
    this.rapportDateFin = value;
    this.rapportDateFinChange.emit(value);
    
    // Validate date range
    if (value && this.rapportDateDebut) {
      const dateDebut = new Date(this.rapportDateDebut);
      const dateFin = new Date(value);
      
      if (dateFin < dateDebut) {
        // If end date is before start date, adjust start date
        this.rapportDateDebut = value;
        this.rapportDateDebutChange.emit(value);
      }
      
      // Auto-apply filter when date changes
      setTimeout(() => this.applyRapportFilter(), 300);
    }
  }

  /**
   * Get date range duration in days
   */
  getDateRangeDuration(): number {
    if (!this.rapportDateDebut || !this.rapportDateFin) {
      return 0;
    }
    
    const dateDebut = new Date(this.rapportDateDebut);
    const dateFin = new Date(this.rapportDateFin);
    const diffTime = Math.abs(dateFin.getTime() - dateDebut.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    return diffDays;
  }

  /**
   * Handle include paid debts change
   */
  onRapportInclureDettesPayeesChange(value: boolean): void {
    this.rapportInclureDettesPayees = value;
    this.rapportInclureDettesPayeesChange.emit(value);
  }

  /**
   * Handle period type change
   */
  onPeriodTypeChange(type: 'custom' | 'last30days' | 'last3months' | 'last6months' | 'lastyear'): void {
    this.selectedPeriodType = type;
    
    if (type !== 'custom') {
      const now = new Date();
      const endDate = now.toISOString().split('T')[0];
      
      let startDate: Date;
      switch (type) {
        case 'last30days':
          startDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
          break;
        case 'last3months':
          startDate = new Date(now.getTime() - 90 * 24 * 60 * 60 * 1000);
          break;
        case 'last6months':
          startDate = new Date(now.getTime() - 180 * 24 * 60 * 60 * 1000);
          break;
        case 'lastyear':
          startDate = new Date(now.getTime() - 365 * 24 * 60 * 60 * 1000);
          break;
        default:
          return;
      }
      
      this.onRapportDateDebutChange(startDate.toISOString().split('T')[0]);
      this.onRapportDateFinChange(endDate);
      
      // Auto-apply filter when period changes
      setTimeout(() => this.applyRapportFilter(), 500);
    }
  }

  /**
   * Handle fournisseur selection change (single selection only)
   */
  onFournisseurSelectionChange(fournisseurId: string): void {
    // Replace the entire selection with just this one fournisseur
    this.selectedFournisseurIds = [fournisseurId];
    this.selectedFournisseurIdsChange.emit([fournisseurId]);
  }

  /**
   * Clear selected fournisseur
   */
  clearSelectedFournisseur(): void {
    this.selectedFournisseurIds = [];
    this.selectedFournisseurIdsChange.emit([]);
  }

  /**
   * Select all fournisseurs (DEPRECATED - kept for backward compatibility)
   */
  selectAllFournisseurs(): void {
    // No longer used - single selection only
    const allIds = this.fournisseurs.map(f => f.id);
    this.selectedFournisseurIds = allIds;
    this.selectedFournisseurIdsChange.emit(allIds);
  }

  /**
   * Clear all fournisseur selection (DEPRECATED - use clearSelectedFournisseur instead)
   */
  clearAllFournisseurs(): void {
    this.clearSelectedFournisseur();
  }

  /**
   * Apply rapport filter
   */
  applyRapportFilter(): void {
    const filterParams: RapportFilterParams = {
      dateDebut: this.rapportDateDebut || undefined,
      dateFin: this.rapportDateFin || undefined,
      inclureDettesPayees: this.rapportInclureDettesPayees,
      fournisseurIds: this.selectedFournisseurIds.length > 0 ? this.selectedFournisseurIds : undefined
    };
    
    this.applyFilter.emit(filterParams);
  }

  /**
   * Clear rapport filter
   */
  clearRapportFilter(): void {
    this.rapportDateDebut = '';
    this.rapportDateFin = '';
    this.rapportInclureDettesPayees = false;
    this.selectedFournisseurIds = [];
    this.selectedPeriodType = 'custom';
    
    this.rapportDateDebutChange.emit('');
    this.rapportDateFinChange.emit('');
    this.rapportInclureDettesPayeesChange.emit(false);
    this.selectedFournisseurIdsChange.emit([]);
    
    this.clearFilter.emit();
  }

  /**
   * Print rapport
   */
  onPrintRapport(): void {
    this.printRapport.emit();
  }

  /**
   * Export rapport
   */
  onExportRapport(format: string): void {
    this.exportRapport.emit(format);
  }

  /**
   * Toggle advanced filters
   */
  toggleAdvancedFilters(): void {
    this.showAdvancedFilters = !this.showAdvancedFilters;
  }

  /**
   * Get tendance class for styling
   */
  getTendanceClass(tendance: string): string {
    switch (tendance?.toLowerCase()) {
      case 'amelioration':
        return 'tendance-positive';
      case 'degradation':
        return 'tendance-negative';
      case 'stable':
        return 'tendance-stable';
      default:
        return 'tendance-neutral';
    }
  }

  /**
   * Get tendance icon
   */
  getTendanceIcon(tendance: string): string {
    switch (tendance?.toLowerCase()) {
      case 'amelioration':
        return '📈';
      case 'degradation':
        return '📉';
      case 'stable':
        return '➡️';
      default:
        return '📊';
    }
  }

  /**
   * Get evaluation badge class
   */
  getEvaluationClass(evaluation: string): string {
    switch (evaluation?.toLowerCase()) {
      case 'bon':
        return 'evaluation-bon';
      case 'moyen':
        return 'evaluation-moyen';
      case 'critique':
        return 'evaluation-critique';
      default:
        return 'evaluation-unknown';
    }
  }

  /**
   * Get evaluation icon
   */
  getEvaluationIcon(evaluation: string): string {
    switch (evaluation?.toLowerCase()) {
      case 'bon':
        return '✅';
      case 'moyen':
        return '⚠️';
      case 'critique':
        return '🚨';
      default:
        return '❓';
    }
  }

  /**
   * Get period type label
   */
  getPeriodTypeLabel(type: string): string {
    switch (type) {
      case 'last30days':
        return 'Derniers 30 jours';
      case 'last3months':
        return 'Derniers 3 mois';
      case 'last6months':
        return 'Derniers 6 mois';
      case 'lastyear':
        return 'Dernière année';
      case 'custom':
        return 'Période personnalisée';
      default:
        return type;
    }
  }

  /**
   * Check if fournisseur is selected
   */
  isFournisseurSelected(fournisseurId: string): boolean {
    return this.selectedFournisseurIds.includes(fournisseurId);
  }

  /**
   * Get formatted percentage
   */
  getFormattedPercentage(value: number): string {
    return `${value.toFixed(1)}%`;
  }

  /**
   * Get formatted currency
   */
  getFormattedCurrency(value: number): string {
    return `${value.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} DT`;
  }

  /**
   * Check if rapport has data
   */
  hasRapportData(): boolean {
    return !!(this.rapportDettes && !this.isLoadingRapport);
  }

  /**
   * Get selected fournisseur name
   */
  getSelectedFournisseurName(): string {
    if (this.selectedFournisseurIds.length === 0) {
      return 'Aucun fournisseur sélectionné';
    }
    const selectedId = this.selectedFournisseurIds[0];
    const fournisseur = this.fournisseurs.find(f => f.id === selectedId);
    return fournisseur ? fournisseur.nom : 'Fournisseur inconnu';
  }

  /**
   * Get total selected fournisseurs count (legacy - for backward compatibility)
   */
  getSelectedFournisseursCount(): number {
    return this.selectedFournisseurIds.length;
  }

  /**
   * Check if a fournisseur is selected
   */
  hasSelectedFournisseur(): boolean {
    return this.selectedFournisseurIds.length > 0;
  }

  /**
   * Check if all fournisseurs are selected (DEPRECATED - single selection only)
   */
  areAllFournisseursSelected(): boolean {
    return this.selectedFournisseurIds.length === this.fournisseurs.length && this.fournisseurs.length > 0;
  }

  /**
   * Check if some fournisseurs are selected (DEPRECATED - single selection only)
   */
  areSomeFournisseursSelected(): boolean {
    return this.selectedFournisseurIds.length > 0 && this.selectedFournisseurIds.length < this.fournisseurs.length;
  }
}