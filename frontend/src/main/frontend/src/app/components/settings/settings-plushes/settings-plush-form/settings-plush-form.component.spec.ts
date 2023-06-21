import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsPlushFormComponent } from './settings-plush-form.component';

describe('SettingsPlushFormComponent', () => {
  let component: SettingsPlushFormComponent;
  let fixture: ComponentFixture<SettingsPlushFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SettingsPlushFormComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsPlushFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
