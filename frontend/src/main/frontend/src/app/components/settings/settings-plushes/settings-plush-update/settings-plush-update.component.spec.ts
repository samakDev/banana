import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsPlushUpdateComponent } from './settings-plush-update.component';

describe('SettingsPlushUpdateComponent', () => {
  let component: SettingsPlushUpdateComponent;
  let fixture: ComponentFixture<SettingsPlushUpdateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SettingsPlushUpdateComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsPlushUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
