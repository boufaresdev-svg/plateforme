import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpsertTache } from './upsert-tache';

describe('UpsertTache', () => {
  let component: UpsertTache;
  let fixture: ComponentFixture<UpsertTache>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpsertTache]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpsertTache);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
