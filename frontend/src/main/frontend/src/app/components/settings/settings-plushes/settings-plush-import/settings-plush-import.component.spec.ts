import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsPlushImportComponent } from './settings-plush-import.component';

describe('SettingsPlushImportComponent', () => {
  let component: SettingsPlushImportComponent;
  let fixture: ComponentFixture<SettingsPlushImportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SettingsPlushImportComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsPlushImportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
