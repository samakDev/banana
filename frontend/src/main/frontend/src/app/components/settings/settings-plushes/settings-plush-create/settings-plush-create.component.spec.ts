import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsPlushCreateComponent } from './settings-plush-create.component';

describe('SettingsPlushCreateComponent', () => {
  let component: SettingsPlushCreateComponent;
  let fixture: ComponentFixture<SettingsPlushCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SettingsPlushCreateComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsPlushCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
