import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsPlushesComponent } from './settings-plushes.component';

describe('SettingsPlushesComponent', () => {
  let component: SettingsPlushesComponent;
  let fixture: ComponentFixture<SettingsPlushesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SettingsPlushesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsPlushesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
