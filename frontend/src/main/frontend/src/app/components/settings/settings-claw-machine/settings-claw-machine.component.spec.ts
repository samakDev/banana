import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SettingsClawMachineComponent} from './settings-claw-machine.component';

describe('SettingsClawmachineComponent', () => {
  let component: SettingsClawMachineComponent;
  let fixture: ComponentFixture<SettingsClawMachineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SettingsClawMachineComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SettingsClawMachineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
