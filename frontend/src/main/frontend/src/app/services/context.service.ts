import { Injectable } from '@angular/core';
import { SprintService } from './sprint.service';
import { Observable, Subject, BehaviorSubject } from 'rxjs/Rx';
import { LocalStorageService } from 'angular-2-local-storage';
import { Router, NavigationEnd } from '@angular/router';
import { HotkeysService, Hotkey } from 'angular2-hotkeys';

@Injectable()
export class ContextService {

    private sprintSelected: Subject<string> = new BehaviorSubject(null);
    private fullScreenMode: Subject<Boolean> = new BehaviorSubject(false);
    private showWeekend: Subject<Boolean> = new BehaviorSubject(false);
    private isFullScreen: Boolean = false;
    private viewSelected: Observable<string>;

    constructor(private localStorageService: LocalStorageService,
        private sprintService: SprintService,
        private router: Router,
        private hotkeysService: HotkeysService) {
        const selectedSprintId = this.localStorageService.get<string>('selectedSprintId');
        if (selectedSprintId != null) {
            this.sprintSelected.next(selectedSprintId);
        }
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

    public getSelectedSprintId(): Observable<string> {
        return this.sprintSelected.asObservable()//
            .filter(sprint => sprint !== null)//
            .distinctUntilChanged((a, b) => a === b);
    }

    public setSelectedSprint(sprint: string) {
        this.sprintSelected.next(sprint);
        if (sprint != null) {
            this.localStorageService.set('selectedSprintId', sprint);
        }
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
