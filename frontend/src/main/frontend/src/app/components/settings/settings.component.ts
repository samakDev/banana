import {Component, OnDestroy} from '@angular/core';
import {ContextService} from '../../services/context.service';
import {TranslateService} from '@ngx-translate/core';
import {Subscription} from "rxjs";

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnDestroy {

  isFullScreen: Boolean = false;
  currentLang: string;
  langs: Array<string>;

  private fullScreenSubscription: Subscription;

  constructor(private contextService: ContextService, private translateService: TranslateService) {
    this.fullScreenSubscription = this.contextService.getFullScreenMode()
      .subscribe({
        next: (v) => this.isFullScreen = v,
        error: (e) => console.error(e)
      });

    this.currentLang = this.translateService.currentLang;
    this.langs = this.translateService.getLangs();
  }

  ngOnDestroy() {
    this.fullScreenSubscription.unsubscribe();
  }


  onFullScreenChange(checked: Boolean) {
    this.contextService.setFullScreenMode(checked);
  }

  onCurrentLangChange(currentLang: string) {
    this.translateService.use(currentLang);
  }
}
