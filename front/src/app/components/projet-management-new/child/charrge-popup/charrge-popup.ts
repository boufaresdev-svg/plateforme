import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-charge-popup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './charge-popup.html',
  styleUrls: ['./charge-popup.css']
})
export class ChargePopup {
  @Input() show: boolean = false;
  @Input() employees: any[] = [];
 
  @Output() close = new EventEmitter<void>();
  @Output() chargeData = new EventEmitter<any>();

  chargeForm!: FormGroup;
  submitted: boolean = false;
  showSousCategorie: boolean = false;
   sousCategories: string[] =  
 [
        'Électrique',
        'Mécanique',
        'Pneumatique',
        'Inox',
        'Fabrication',
        'Composant électrique'
      ];

  constructor(private fb: FormBuilder) {
    this.initForm();
  }

  initForm() {
    this.chargeForm = this.fb.group({
      employeId: ['', Validators.required],
      nom: ['', Validators.required],
      montant: [0, [Validators.required, Validators.min(0)]],
      categorie: ['', Validators.required],
      sousCategorie: [''],
      description: ['']
    });
  }

  get fch() { return this.chargeForm.controls; }

  onCategorieChange(event: any) {
    const cat = event.target.value;
    this.showSousCategorie = cat === 'inclus' || cat === 'non_inclus';
  }

  showForm() {
    this.submitted = false;
    this.chargeForm.reset({ montant: 0 });
    this.showSousCategorie = false;
  }

  save() {
    this.submitted = true;
    if (this.chargeForm.invalid) return;

    this.chargeData.emit(this.chargeForm.value);
    this.closePopup();
  }

  closePopup() {
    this.submitted = false;
    this.chargeForm.reset({ montant: 0 });
    this.showSousCategorie = false;
    this.close.emit();
  }
}
