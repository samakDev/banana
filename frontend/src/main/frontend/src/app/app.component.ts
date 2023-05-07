import {Component} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(private translateService: TranslateService) {
    const usableLanguages = ['en', 'fr'];
    translateService.addLangs(usableLanguages);

    const browserLang = translateService.getBrowserLang();
    const defaultLang = usableLanguages.includes(browserLang) ? browserLang : 'fr'

    translateService.use('fr');
  }

}
