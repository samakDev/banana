import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsCreateNewPlushComponent } from './settings-create-new-plush.component';

describe('SettingsCreateNewPlushComponent', () => {
  let component: SettingsCreateNewPlushComponent;
  let fixture: ComponentFixture<SettingsCreateNewPlushComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SettingsCreateNewPlushComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsCreateNewPlushComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
