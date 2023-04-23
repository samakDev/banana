import {Component, OnInit} from '@angular/core';
import {ContextService} from '../../services/context.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html'
})
export class MenuComponent implements OnInit {

  isCollapsed: Boolean = false;
  isFullScreen: Boolean = false;

  constructor(private contextService: ContextService) {

  }

  ngOnInit() {
    this.contextService.getFullScreenMode()
      .distinctUntilChanged()
      .subscribe(v => this.isFullScreen = v, e => console.error(e));


  }
}
