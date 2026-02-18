import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-probleme-popup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './probleme-popup.html',
  styleUrl: './probleme-popup.css'
})
export class ProblemePopup {
    
  @Input() show = false;
  @Input() tacheId: string | null = null;

  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<any>();

  submitted = false;
  problemeForm!: FormGroup;

  constructor(private fb: FormBuilder) {
    this.initForm();
  }

  initForm() {
    this.problemeForm = this.fb.group({
      nom: ['', Validators.required],
      description: ['']
    });
  }

  handleSave() {
    this.submitted = true;
    if (this.problemeForm.invalid || !this.tacheId) return;

    const payload = {
      tacheId: this.tacheId,
      ...this.problemeForm.value
    };

    this.save.emit(payload);
    this.resetAndClose();
  }

  resetAndClose() {
    this.submitted = false;
    this.problemeForm.reset();
    this.close.emit();
  }
}
