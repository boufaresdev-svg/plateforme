import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpsertAffectation } from './upsert-affectation';

describe('UpsertAffectation', () => {
  let component: UpsertAffectation;
  let fixture: ComponentFixture<UpsertAffectation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpsertAffectation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpsertAffectation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
