import {Component, DoCheck, Input, OnDestroy, OnInit} from '@angular/core';
import {PlushService} from '../../services/plush.service';
import {PlushModel} from "../../models/plush.model";
import {NameService} from "../../services/name.service";
import {Observable, Subscription} from "rxjs";

@Component({
  selector: 'plush-display',
  templateUrl: './plush.component.html',
  styleUrls: ['./plush.component.css']
})
export class PlushComponent implements OnInit, DoCheck, OnDestroy {
  @Input("plushModel") plushModel: PlushModel;
  @Input("currentNameEvent") currentNameEvent: Observable<string>;

  currentName: string;
  canLock: boolean;
  canUnLock: boolean;
  lockerName: string;
  imageToShow: string | ArrayBuffer;

  private oldLockerName = undefined;
  private imageSubscription: Subscription;
  private currentNameSubscription: Subscription;
  private plushLockSubscription: Subscription;
  private plushUnLockSubscription: Subscription;

  constructor(private nameService: NameService,
              private plushService: PlushService) {
  }


  ngOnInit(): void {
    this.oldLockerName = this.plushModel.locker;
    this.updateLockState();

    this.imageSubscription = this.plushService.getImage(this.plushModel.clawMachineId, this.plushModel.id)
      .subscribe({
        next: blob => {
          let reader = new FileReader();
          reader.addEventListener("load", () => {
            this.imageToShow = reader.result;
          }, false);

          reader.readAsDataURL(blob);
        },
        error: e => console.log('error while loading image : ', e)
      });

    this.currentNameSubscription = this.currentNameEvent.subscribe({
      next: currentName => {
        this.currentName = currentName;
        this.updateLockState();
      }
    });
  }

  ngDoCheck(): void {
    if (this.oldLockerName !== this.plushModel.locker) {
      this.updateLockState();
      this.oldLockerName = this.plushModel.locker;
    }
  }

  ngOnDestroy() {
    this.imageSubscription.unsubscribe();
    this.currentNameSubscription.unsubscribe();

    if (this.plushLockSubscription !== undefined) {
      this.plushLockSubscription.unsubscribe();
    }

    if (this.plushUnLockSubscription !== undefined) {
      this.plushUnLockSubscription.unsubscribe();
    }
  }

  private updateLockState() {
    this.lockerName = this.plushModel.locker;
    this.canUnLock = this.canUnLockFn();
    this.canLock = this.canLockFn() && !this.canUnLock;
  }

  private canLockFn(): boolean {
    return this.hasValidMemberName() && this.lockerName === undefined;
  }

  private canUnLockFn(): boolean {
    return this.hasValidMemberName()
      && this.lockerName !== undefined
      && (this.currentName === 'admin'
        || this.lockerName === this.currentName);
  }

  private hasValidMemberName() {
    return this.currentName !== null && this.currentName !== undefined;
  }

  lock(): void {
    this.plushLockSubscription = this.plushService.lock(this.plushModel, this.currentName)
      .subscribe();
  }

  unlock(): void {
    this.plushUnLockSubscription = this.plushService.unLock(this.plushModel, this.currentName)
      .subscribe();
  }
}
