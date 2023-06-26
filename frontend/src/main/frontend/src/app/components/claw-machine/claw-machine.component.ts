import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PlushModel} from "../../models/plush.model";
import {BehaviorSubject, interval, map, share, Subscription, switchMap, take} from "rxjs";
import {PlushService} from "../../services/plush.service";
import {CurrentStatePlushEvent, PlushEvent} from 'dto/target/ts/message_pb';
import {Plush} from 'dto/target/ts/model_pb';
import {NameService} from "../../services/name.service";

@Component({
  selector: 'app-claw-machine',
  templateUrl: './claw-machine.component.html',
  styleUrls: ['./claw-machine.component.css']
})
export class ClawMachineComponent implements OnInit, OnDestroy {
  plushes: PlushModel[] = [];
  currentName: string = undefined;
  currentNameEvent: BehaviorSubject<string> = new BehaviorSubject(undefined);

  private clawMachineId: string;
  private plushEventSubscriton: Subscription;
  private currentNaneSubscription: Subscription;

  constructor(private route: ActivatedRoute, private plushService: PlushService, private nameService: NameService) {
  }


  ngOnInit(): void {
    const currentMachineIdObs = this.route.params
      .pipe(map(params => {
        const clawMachineId: string = params["id"];

        return clawMachineId;
      }))
      .pipe(map(clawMachineId => {
        this.plushes = [];
        this.clawMachineId = clawMachineId;

        return clawMachineId;
      }))
      .pipe(switchMap(clawMachineId => interval(200)
        .pipe(take(1))
        .pipe(map(any => clawMachineId))))
      .pipe(share());

    this.plushEventSubscriton = currentMachineIdObs
      .pipe(switchMap(clawMachineId => this.plushService.getPlusheEventInit(clawMachineId)))
      .subscribe({
        next: plushEvent => this.plushEventsHandler(plushEvent),
        error: error => console.error("error when getting param id on ClawMachineComponent", error)
      });

    this.currentNaneSubscription = currentMachineIdObs
      .pipe(switchMap(clawMachineId => this.nameService.getNameObs(clawMachineId)))
      .subscribe({
        next: currentName => {
          this.currentName = currentName;
          this.currentNameEvent.next(currentName);
        },
        error: error => console.error("error when getting param id on ClawMachineComponent", error)
      });
  }

  ngOnDestroy(): void {
    this.plushEventSubscriton.unsubscribe();
    this.currentNaneSubscription.unsubscribe();
  }

  private plushEventsHandler(plushEvent: PlushEvent): void {
    switch (plushEvent.getEventCase()) {
      case PlushEvent.EventCase.CURRENTSTATEPLUSHEVENT:
        this.init(plushEvent.getCurrentstateplushevent());
        break;
      case PlushEvent.EventCase.CREATEPLUSHEVENT:
        this.addOrUpdate(plushEvent.getCreateplushevent().getPlush())
        break;
      case PlushEvent.EventCase.UPDATEDPLUSHEVENT:
        this.addOrUpdate(plushEvent.getUpdatedplushevent().getPlush())
        break;
      case PlushEvent.EventCase.DELETEDPLUSHEVENT:
        this.removePlush(plushEvent.getDeletedplushevent().getPlushid())
        break;
      case PlushEvent.EventCase.LOCKPLUSHEVENT:
        this.updateLockState(plushEvent.getLockplushevent().getPlushid(), plushEvent.getLockplushevent().getPlushlocker().getName());
        break
      case PlushEvent.EventCase.UNLOCKPLUSHEVENT:
        this.updateLockState(plushEvent.getUnlockplushevent().getPlushid(), undefined);
        break
      case PlushEvent.EventCase.EVENT_NOT_SET:
      default:
        throw new Error("event not set or treated");
    }
  }

  private init(currentStatePlushEvent: CurrentStatePlushEvent): void {
    currentStatePlushEvent.getPlushesList()
      .forEach(plush => this.addOrUpdate(plush))
  }

  private addOrUpdate(plush: Plush): void {
    this.removePlush(plush.getId());

    const plushModel = PlushService.convertPlushDtoToPlushModel(plush);

    this.addOrUpdateModel(plushModel);
  }

  private addOrUpdateModel(plushModel: PlushModel) {
    this.plushes.push(plushModel);

    this.plushes = [...this.plushes]
      .sort((model1, model2) => model1.order >= model2.order ? 0 : -1);
  }

  private removePlush(plushId: string): void {
    const index = this.plushes.findIndex(plushModel => plushModel.id === plushId);

    if (index > -1) {
      this.plushes.splice(index, 1);
    }
  }

  private updateLockState(plushId: string, lockerName: string): void {
    const index = this.plushes.findIndex(plushModel => plushModel.id === plushId);

    if (index > -1) {
      const savedPlush = this.plushes[index];
      this.plushes.splice(index, 1);

      savedPlush.locker = lockerName;

      this.addOrUpdateModel(savedPlush);
    }
  }

  onMemberNameChange(): void {
    this.nameService.updateName(this.clawMachineId, this.currentName.toLowerCase());
  }
}
