import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsClawMachineUpdateComponent } from './settings-claw-machine-update.component';

describe('SettingsClawMachineUpdateComponent', () => {
  let component: SettingsClawMachineUpdateComponent;
  let fixture: ComponentFixture<SettingsClawMachineUpdateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SettingsClawMachineUpdateComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsClawMachineUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
