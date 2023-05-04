import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SettingsClawMachineCreateComponent} from './settings-claw-machine-create.component';

describe('SettingsClawMachineCreatedComponent', () => {
  let component: SettingsClawMachineCreateComponent;
  let fixture: ComponentFixture<SettingsClawMachineCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SettingsClawMachineCreateComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SettingsClawMachineCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
