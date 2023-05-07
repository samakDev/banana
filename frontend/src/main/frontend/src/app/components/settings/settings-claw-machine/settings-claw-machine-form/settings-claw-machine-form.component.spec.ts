import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsClawMachineFormComponent } from './settings-claw-machine-form.component';

describe('SettingsClawMachineFormComponent', () => {
  let component: SettingsClawMachineFormComponent;
  let fixture: ComponentFixture<SettingsClawMachineFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SettingsClawMachineFormComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsClawMachineFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
