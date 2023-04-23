import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {LocalStorageService} from 'angular-2-local-storage';
import {Hotkey, HotkeysService} from 'angular2-hotkeys';

@Injectable()
export class ContextService {

  private fullScreenMode: Subject<Boolean> = new BehaviorSubject(false);
  private showWeekend: Subject<Boolean> = new BehaviorSubject(false);
  private isFullScreen: Boolean = false;

  constructor(private localStorageService: LocalStorageService,
              private hotkeysService: HotkeysService) {
    this.setFullScreenMode(this.localStorageService.get<Boolean>('isFullScreen'));

    this.hotkeysService.add(new Hotkey('ctrl+shift+f', evt => {
      this.setFullScreenMode(!this.isFullScreen);
      return false;
    }));

    this.setShowWeekend(this.localStorageService.get<Boolean>('showWeekend'));
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
