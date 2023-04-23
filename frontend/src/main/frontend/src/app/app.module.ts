import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChartModule } from 'angular2-highcharts';
import { HttpModule, Http } from '@angular/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AppComponent } from './app.component';
import { MenuComponent } from './components/menu/menu.component';
import { GraphComponent } from './components/graph/graph.component';
import { RouterModule, Routes } from '@angular/router';
import { TextfieldCellComponent } from './ui/textfield-cell/textfield-cell.component';
import { ComboboxCellComponent } from './ui/combobox-cell/combobox-cell.component';
import { DatepickerCellComponent } from './ui/datepicker-cell/datepicker-cell.component';
import { ActionCellComponent } from './ui/action-cell/action-cell.component';
import { DatepickerInputComponent } from './ui/datepicker-input/datepicker-input.component';
import { ReleaseService } from './services/release.service';
import { ReleasesComponent } from './components/releases/releases.component';
import { NewReleaseComponent } from './components/new-release/new-release.component';
import { SprintComponent } from './components/sprint/sprint.component';
import { SprintsComponent } from './components/sprints/sprints.component';
import { NewSprintComponent } from './components/new-sprint/new-sprint.component';
import { SprintService } from './services/sprint.service';
import { ContextService } from './services/context.service';
import { PlushService } from './services/plush.service';
import { HighchartsStatic } from 'angular2-highcharts/dist/HighchartsService';
import { PlushComponent } from './components/plush/plush.component';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { LocalStorageModule, LocalStorageService } from 'angular-2-local-storage';
import { HotkeyModule } from 'angular2-hotkeys';
import { SettingsComponent } from './components/settings/settings.component';
import { JiraProjectComponent } from './components/jira/project/jira-project.component';
import { JiraSprintComponent } from './components/jira/sprint/jira-sprint.component';
import { StompConfig, StompService } from '@stomp/ng2-stompjs';
import {HttpClientModule, HttpClient} from '@angular/common/http';
declare var require: any;

const appRoutes: Routes = [
  { path: 'releases', component: ReleasesComponent },
  { path: 'sprints', component: SprintsComponent },
  { path: 'sprint/:id', component: SprintComponent },
  { path: 'graph', component: GraphComponent },
  { path: 'plush', component: PlushComponent },
  { path: 'settings', component: SettingsComponent },
  { path: 'jira/project', component: JiraProjectComponent },
  { path: 'jira/sprint/:id', component: JiraSprintComponent },
  { path: '*', redirectTo: '/plush', pathMatch: 'full' },
];


export function getStompConfig() {
  let protocol = 'ws:';
  if (window.location.protocol === 'https:') {
    protocol = 'wss:';
  }
  const url = protocol + '//' + window.location.host + '/websocket';
  const stompConfig: StompConfig = {
    // Which server?
    url: url,

    // Headers
    headers: {
    },

    // How often to heartbeat?
    // Interval in milliseconds, set to 0 to disable
    heartbeat_in: 0, // Typical value 0 - disabled
    heartbeat_out: 20000, // Typical value 20000 - every 20 seconds

    // Wait in milliseconds before attempting auto reconnect
    // Set to 0 to disable
    // Typical value 5000 (5 seconds)
    reconnect_delay: 5000,

    // Will log diagnostics on console
    debug: true
  };
  return stompConfig;
}


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
    AppComponent,
    MenuComponent,
    GraphComponent,
    TextfieldCellComponent,
    ComboboxCellComponent,
    DatepickerCellComponent,
    ActionCellComponent,
    DatepickerInputComponent,
    NewReleaseComponent,
    ReleasesComponent,
    SprintComponent,
    SprintsComponent,
    NewSprintComponent,
    PlushComponent,
    SettingsComponent,
    JiraProjectComponent,
    JiraSprintComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    HttpClientModule,
    NgbModule.forRoot(),
    RouterModule.forRoot(appRoutes, { useHash: true }),
    HotkeyModule.forRoot(),
    ChartModule,
    LocalStorageModule.withConfig({
      prefix: 'banana',
      storageType: 'localStorage'
    }),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    })

  ],
  providers: [//
    ReleaseService, //
    SprintService, //
    PlushService, //
    ContextService, //
    {
      provide: HighchartsStatic,
      useFactory: highchartsFactory
    }, //
    StompService,
    {
      provide: StompConfig,
      useFactory: getStompConfig
    },
    LocalStorageService
  ],
  entryComponents: [//
    NewReleaseComponent, //
    NewSprintComponent//
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
