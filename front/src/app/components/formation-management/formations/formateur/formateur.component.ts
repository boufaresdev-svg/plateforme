import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlanFormation, StatutFormation } from '../../../../models/formation/PlanFormation.model';
import { Formation } from '../../../../models/formation/formation.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-formation-formateur',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './formateur.component.html',
  styleUrls: ['./formateur.component.css']
})
export class FormationFormateurComponent implements OnInit {
  @Input() planGeneratorForm: any = {
    titre: '',
    description: '',
    dateDebut: '',
    dateFin: '',
    dateLancement: '',
    dateFinReel: '',
    formateurId: undefined,
    status: 'PLANIFIEE'
  };
  @Input() editingPlanId: number | null = null;
  @Input() formateurs: any[] = [];
  @Input() form!: Formation;
  @Input() objectifsGlobauxExemples: any[] = [];
  @Input() objectifsSpecifiquesExemples: any[] = [];
  @Input() contentItems: any[] = [];
  @Input() saving: boolean = false;
  
  @Output() close = new EventEmitter<void>();
  @Output() generatePdf = new EventEmitter<void>();
  @Output() savePlan = new EventEmitter<void>();

  statutOptions = Object.values(StatutFormation);

  constructor() {}

  ngOnInit(): void {
    // Component receives all data from parent
  }

  get contentDays(): number[] {
    const days = new Set(this.contentItems.map((item: any) => item.day).filter((d: any) => d));
    return Array.from(days).sort((a, b) => a - b);
  }

  // Calculate days from date range (dateDebut to dateFin)
  get calculatedDays(): number {
    if (!this.planGeneratorForm.dateDebut || !this.planGeneratorForm.dateFin) {
      return 0;
    }
    const start = new Date(this.planGeneratorForm.dateDebut);
    const end = new Date(this.planGeneratorForm.dateFin);
    const diffTime = end.getTime() - start.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1; // +1 to include both start and end day
    return diffDays > 0 ? diffDays : 0;
  }

  // Number of days with content activities
  get contentDaysCount(): number {
    return this.contentDays.length;
  }

  // The minimum required days (whichever is greater: calculated from dates or from content)
  get requiredDays(): number {
    return Math.max(this.calculatedDays, this.contentDaysCount);
  }

  // Check if user-set days is less than the required days
  get hasDaysMismatch(): boolean {
    if (!this.planGeneratorForm.nombreJours || this.planGeneratorForm.nombreJours <= 0) {
      return false;
    }
    return this.planGeneratorForm.nombreJours < this.requiredDays;
  }

  // Check if calculated days from dates is less than content days (date range too short)
  get hasDateRangeMismatch(): boolean {
    return this.calculatedDays > 0 && this.calculatedDays < this.contentDaysCount;
  }

  totalHoursOverall(): number {
    let total = 0;
    for (const item of this.contentItems) {
      total += (item.hoursPractical || 0) + (item.hoursTheoretical || 0);
    }
    return total;
  }

  getSelectedCount(items: any[]): number {
    return items?.filter((item: any) => item.selected).length || 0;
  }

  onGeneratePdf(): void {
    this.generatePdf.emit();
  }

  onSavePlan(): void {
    this.savePlan.emit();
  }

  onClose(): void {
    this.close.emit();
  }
}
