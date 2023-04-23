import {Component, OnInit} from '@angular/core';
import {ContextService} from '../../services/context.service';
import {distinctUntilChanged} from "rxjs";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html'
})
export class MenuComponent implements OnInit {

  isCollapsed: boolean = false;
  isFullScreen: boolean = false;

  showSprints: boolean = false;

  constructor(private contextService: ContextService) {

  }

  ngOnInit() {
    this.contextService.getFullScreenMode()
      .pipe(distinctUntilChanged())
      .subscribe({
        next: (v) => this.isFullScreen = v.valueOf(),
        error: (e) => console.error(e)
      });
  }
}
