import {Component, OnInit} from '@angular/core';
import {LocalStorageService} from 'angular-2-local-storage';
import {PlushService} from '../../services/plush.service';
import {PlushState} from '../../models/plush-state';
import {Plush} from '../../models/plush';
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

  plushs: Array<PlushState> = [];

  constructor(private plushService: PlushService,
              private localStorageService: LocalStorageService,
              private contextService: ContextService) {
  }

  ngOnInit() {
    this.plushService.getPlushs()
      .subscribe({
        next: (plush) => this.addOrUpdate(plush),
        error: (e) => console.error(e)
      });

    this.contextService.getFullScreenMode()
      .subscribe({
        next: (v) => this.isFullScreen = v,
        error: (e) => console.error(e)
      });
  }

  private addOrUpdate(plush: PlushState): void {
    const index = this.plushs.findIndex(s => s.plush.id === plush.plush.id);

    if (index !== -1) {
      this.plushs[index] = plush;
    } else {
      this.plushs.push(plush);
    }
  }

  take(plush: Plush): void {
    // if (this.memberId !== null && this.memberId !== '') {
    //   this.plushService.take(new PlushState(plush, new User(this.memberId.toString(), this.memberName.toString())));
    // }
  }

  release(plush: Plush): void {
    // if (this.memberId !== null && this.memberId !== '') {
    //   this.plushService.release(new PlushState(plush, new User(this.memberId.toString(), this.memberName.toString())));
    // }
  }
}
