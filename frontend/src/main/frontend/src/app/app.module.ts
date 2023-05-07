import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ChartModule} from 'angular2-highcharts';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {HighchartsStatic} from 'angular2-highcharts/dist/HighchartsService';
import {RouterModule, Routes} from '@angular/router';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {LocalStorageModule, LocalStorageService} from 'angular-2-local-storage';
import {HotkeyModule} from 'angular2-hotkeys';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {ContextService} from './services/context.service';
import {PlushService} from './services/plush.service';
import {RxStompService} from "./services/stomp/rx-stomp.service";
import {RxStompServiceFactory} from "./services/stomp/rx-stomp-service-factory";
import {RxStompConfigProvider} from "./services/stomp/rx.stomp.config";
import {NgOptimizedImage} from "@angular/common";
import {HttpBananaClawMachineSenderService} from "./services/http-banana-claw-machine-sender.service";
import {ClawMachineService} from "./services/claw.machine.service";
import {AppComponent} from './app.component';
import {MenuComponent} from './components/menu/menu.component';
import {PlushComponent} from './components/plush/plush.component';
import {SettingsComponent} from './components/settings/settings.component';
import {
  SettingsClawMachineComponent
} from './components/settings/settings-claw-machine/settings-claw-machine.component';
import {
  SettingsClawMachineCreateComponent
} from './components/settings/settings-claw-machine/settings-claw-machine-create/settings-claw-machine-create.component';
import {
  SettingsClawMachineUpdateComponent
} from './components/settings/settings-claw-machine/settings-claw-machine-update/settings-claw-machine-update.component';
import {SettingsContentDirective} from "./components/settings/settings-claw-machine/settings.content.directive";
import {
  SettingsClawMachineFormComponent
} from './components/settings/settings-claw-machine/settings-claw-machine-form/settings-claw-machine-form.component';

declare var require: any;

const appRoutes: Routes = [
  {path: 'plush', component: PlushComponent},
  {path: 'settings', component: SettingsComponent},
  {path: '*', redirectTo: '/plush', pathMatch: 'full'},
];


// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, '/assets/i18n/', '.json');
}

export function highchartsFactory() {
  const hc = require('highcharts');
  const ba = require('highcharts/modules/broken-axis');
  ba(hc);
  return hc;
}

@NgModule({
  declarations: [
    SettingsContentDirective,
    AppComponent,
    MenuComponent,
    PlushComponent,
    SettingsComponent,
    SettingsClawMachineComponent,
    SettingsClawMachineCreateComponent,
    SettingsClawMachineUpdateComponent,
    SettingsClawMachineFormComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    NgbModule,
    RouterModule.forRoot(appRoutes, {useHash: true}),
    HotkeyModule.forRoot(),
    ChartModule,
    LocalStorageModule.forRoot({
      prefix: 'banana',
      storageType: 'localStorage'
    }),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    NgOptimizedImage

  ],
  providers: [
    HttpBananaClawMachineSenderService,
    ContextService,
    ClawMachineService,
    PlushService,
    {
      provide: HighchartsStatic,
      useFactory: highchartsFactory
    },
    RxStompConfigProvider,
    RxStompServiceFactory,
    RxStompService,
    LocalStorageService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
