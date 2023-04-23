import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, Subject} from 'rxjs/Rx';
import {LocalStorageService} from 'angular-2-local-storage';
import {NavigationEnd, Router} from '@angular/router';
import {Hotkey, HotkeysService} from 'angular2-hotkeys';

@Injectable()
export class ContextService {

  private fullScreenMode: Subject<Boolean> = new BehaviorSubject(false);
  private showWeekend: Subject<Boolean> = new BehaviorSubject(false);
  private isFullScreen: Boolean = false;
  private viewSelected: Observable<string>;

  constructor(private localStorageService: LocalStorageService,
              private router: Router,
              private hotkeysService: HotkeysService) {
    this.viewSelected = router.events
      .filter(evt => evt instanceof NavigationEnd)
      .map((evt: NavigationEnd) => evt.url)
      .map(url => url.substring(1))
      .map(url => {
        const index = url.indexOf('/');
        if (index !== -1) {
          return url.substring(0, index);
        }
        return url;
      }).shareReplay(1);
    this.setFullScreenMode(this.localStorageService.get<Boolean>('isFullScreen'));
    this.hotkeysService.add(new Hotkey('ctrl+shift+f', evt => {
      this.setFullScreenMode(!this.isFullScreen);
      return false;
    }));
    this.setShowWeekend(this.localStorageService.get<Boolean>('showWeekend'));
  }

  public getViewSelected(): Observable<string> {
    return this.viewSelected;
  }

  public getFullScreenMode(): Observable<Boolean> {
    return this.fullScreenMode;
  }

  public setFullScreenMode(checked: Boolean) {
    this.isFullScreen = checked;
    this.localStorageService.set('isFullScreen', checked);
    this.fullScreenMode.next(checked);
  }

  public getShowWeekend(): Observable<Boolean> {
    return this.showWeekend;
  }

  public setShowWeekend(show: Boolean) {
    this.localStorageService.set('showWeekend', show);
    this.showWeekend.next(show);
  }


}
