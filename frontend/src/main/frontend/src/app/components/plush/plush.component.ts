import {Component, OnInit} from '@angular/core';
import {LocalStorageService} from 'angular-2-local-storage';
import {PlushService} from '../../services/plush.service';
import {PlushState} from '../../models/plush-state';
import {Plush} from '../../models/plush';
import {User} from '../../models/user';
import {ContextService} from '../../services/context.service';
import {Constants} from "../../constants";

@Component({
  selector: 'app-plush',
  templateUrl: './plush.component.html',
  styleUrls: ['./plush.component.css']
})
export class PlushComponent implements OnInit {

  memberName: String = '';
  memberId: String = '';
  plushs: Array<PlushState> = [];
  isFullScreen: Boolean = false;
  isValid: Boolean = false;

  constructor(private plushService: PlushService,
              private localStorageService: LocalStorageService,
              private contextService: ContextService) {
    this.memberName = localStorageService.get<string>(Constants.LOCAL_STORAGE_MEMBER_NAME);
    this.memberId = localStorageService.get<string>(Constants.LOCAL_STORAGE_MEMBER_ID);

    if (this.isMemberNameValid()) {
      this.isValid = true;
    }
  }

  private isMemberNameValid() {
    return this.memberName != null && this.memberName !== '';
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

  onMemberNameChange(): void {
    if (this.isMemberNameValid()) {
      this.memberId = this.memberName.trim().toLowerCase();
      this.localStorageService.set(Constants.LOCAL_STORAGE_MEMBER_NAME, this.memberName);
      this.localStorageService.set(Constants.LOCAL_STORAGE_MEMBER_ID, this.memberId);
      this.isValid = true;
    } else {
      this.localStorageService.set(Constants.LOCAL_STORAGE_MEMBER_NAME, null);
      this.localStorageService.set(Constants.LOCAL_STORAGE_MEMBER_ID, null);
      this.isValid = false;
    }
  }

  take(plush: Plush): void {
    if (this.memberId !== null && this.memberId !== '') {
      this.plushService.take(new PlushState(plush, new User(this.memberId.toString(), this.memberName.toString())));
    }
  }

  release(plush: Plush): void {
    if (this.memberId !== null && this.memberId !== '') {
      this.plushService.release(new PlushState(plush, new User(this.memberId.toString(), this.memberName.toString())));
    }
  }
}
