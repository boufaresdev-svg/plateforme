import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-livrable-popup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './livrable-popup.html',
  styleUrls: ['./livrable-popup.css']
})
export class LivrablePopup {
  @Input() show: boolean = false;
  @Input() tacheId!: string; // injecté par le parent

  @Output() close = new EventEmitter<void>();
  @Output() livrableData = new EventEmitter<{ tacheId: string; nom: string; description: string }>();

  livrableForm!: FormGroup;
  submitted: boolean = false;

  constructor(private fb: FormBuilder) {
    this.initForm();
  }

  initForm() {
    this.livrableForm = this.fb.group({
      tacheId: ['', Validators.required],
      nom: ['', Validators.required],
      description: ['']
    });
  }

  get f() { return this.livrableForm.controls; }

  showForm() {
    this.submitted = false;
    this.livrableForm.reset({ tacheId: this.tacheId });
  }

  save() {
    this.submitted = true;
    if (this.livrableForm.invalid) return;

    const data = this.livrableForm.value;
    this.livrableData.emit(data); // on envoie au parent
    this.closePopup();
  }

  closePopup() {
    this.submitted = false;
    this.livrableForm.reset();
    this.close.emit();
  }
}
