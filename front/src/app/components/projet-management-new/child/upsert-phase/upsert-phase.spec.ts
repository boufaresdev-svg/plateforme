import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpsertPhase } from './upsert-phase';

describe('UpsertPhase', () => {
  let component: UpsertPhase;
  let fixture: ComponentFixture<UpsertPhase>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpsertPhase]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpsertPhase);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
