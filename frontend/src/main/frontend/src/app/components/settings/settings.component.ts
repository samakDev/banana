import {Component, OnInit} from '@angular/core';
import {ContextService} from '../../services/context.service';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

  isFullScreen: Boolean = false;
  currentLang: string;
  langs: Array<string>;

  constructor(private contextService: ContextService, private translateService: TranslateService) {
    this.contextService.getFullScreenMode()
      .subscribe({
        next: (v) => this.isFullScreen = v,
        error: (e) => console.error(e)
      });

    // this.currentLang = this.translateService.currentLang;
    this.currentLang = "fr";
    this.translateService.use(this.currentLang);
    this.langs = this.translateService.getLangs();
  }

  ngOnInit() {
  }

  onFullScreenChange(checked: Boolean) {
    this.contextService.setFullScreenMode(checked);
  }

  onCurrentLangChange(currentLang: string) {
    console.log('currentLang : ', currentLang);
    this.translateService.use(currentLang);
  }
}
