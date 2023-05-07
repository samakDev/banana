import {Component, OnInit} from '@angular/core';
import {ContextService} from '../../services/context.service';
import {distinctUntilChanged, filter} from "rxjs";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html'
})
export class MenuComponent implements OnInit {

  isCollapsed: boolean = false;
  isFullScreen: boolean = false;

  constructor(private contextService: ContextService) {
  }

  ngOnInit() {
    this.contextService.getFullScreenMode()
      .pipe(filter(value => !(value === null || value === undefined)))
      .pipe(distinctUntilChanged())
      .subscribe({
        next: (v) => this.isFullScreen = v.valueOf(),
        error: (e) => console.error(e)
      });
  }
}
