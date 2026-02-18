import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpsertMission } from './upsert-mission';

describe('UpsertMission', () => {
  let component: UpsertMission;
  let fixture: ComponentFixture<UpsertMission>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpsertMission]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpsertMission);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
