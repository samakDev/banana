import {Component, OnInit} from '@angular/core';
import {LocalStorageService} from 'angular-2-local-storage';
import {PlushService} from '../../services/plush.service';
import {ContextService} from '../../services/context.service';

@Component({
  selector: 'app-plush',
  templateUrl: './plush.component.html',
  styleUrls: ['./plush.component.css']
})
export class PlushComponent implements OnInit {

  memberId: string = undefined;
  isFullScreen: Boolean = false;
  isValid: Boolean = false;

  plushs: Array<any> = [];

  constructor(private plushService: PlushService,
              private localStorageService: LocalStorageService,
              private contextService: ContextService) {
  }

  ngOnInit() {
    this.contextService.getFullScreenMode()
      .subscribe({
        next: (v) => this.isFullScreen = v,
        error: (e) => console.error(e)
      });
  }

  take(plush: any): void {
  }

  release(plush: any): void {
  }
}
