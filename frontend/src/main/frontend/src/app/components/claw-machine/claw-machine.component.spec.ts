import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClawMachineComponent } from './claw-machine.component';

describe('ClawMachineComponent', () => {
  let component: ClawMachineComponent;
  let fixture: ComponentFixture<ClawMachineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClawMachineComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClawMachineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
