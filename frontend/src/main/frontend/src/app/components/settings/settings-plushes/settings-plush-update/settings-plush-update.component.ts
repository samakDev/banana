import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PlushService} from "../../../../services/plush.service";
import {concatMap, interval, map} from "rxjs";
import {PlushModel} from "../../../../models/plush.model";
import {CurrentStatePlushEvent, PlushEvent} from 'dto/target/ts/message_pb';
import {Plush} from 'dto/target/ts/model_pb';

@Component({
  selector: 'app-settings-plush-update',
  templateUrl: './settings-plush-update.component.html',
  styleUrls: ['./settings-plush-update.component.css']
})
export class SettingsPlushUpdateComponent implements OnInit {
  responseSuccess: boolean = undefined;
  responseText = undefined;

  plushes: PlushModel[] = [];
  currentPlushIdEditing: string = null;
  clawMachineId: string;

  constructor(private route: ActivatedRoute, private plushService: PlushService) {
  }

  isEditingMode(plushId: string): boolean {
    return this.currentPlushIdEditing === plushId;
  }

  setInEditionMode(plushId: string): void {
    this.currentPlushIdEditing = this.isEditingMode(plushId)
      ? undefined
      : plushId;
  }

  sendUpdatePlush(plushToSend: PlushModel): void {
    this.plushService.updatePlush(plushToSend)
      .subscribe({
        next: any => {
          this.responseSuccess = true;
          this.responseText = "settings-plush-update_updated-success"
        },
        error: e => {
          this.responseSuccess = false;
          this.responseText = "settings-plush-update_updated-error"
          console.error('error while sending post request : ', e);
        }
      });
  }

  sendDeletePlush(plushId: string): void {
    this.plushService.deletePlush(this.clawMachineId, plushId)
      .subscribe({
        next: any => {
          this.responseSuccess = true;
          this.responseText = "settings-plush-update_deleted-success"
        },
        error: e => {
          this.responseSuccess = false;
          this.responseText = "settings-plush-update_deleted-error"
          console.error('error while sending post request : ', e);
        }
      });
  }

  ngOnInit(): void {
    this.route.parent.params
      .pipe(map(params => {
        const clawMachineId: string = params["id"];

        return clawMachineId;
      }))
      .pipe(map(clawMachineId => {
        this.plushes = [];
        this.clawMachineId = clawMachineId;

        return clawMachineId;
      }))
      .pipe(concatMap(clawMachineId => interval(200)
        .pipe(map(any => clawMachineId))))
      .pipe(concatMap(clawMachineId => this.plushService.getPlusheEventInit(clawMachineId)))
      .subscribe({
        next: plushEvent => this.plushEventsHandler(plushEvent),
        error: error => console.error("error when getting param id on ClawMachineComponent", error)
      });
  }

  private plushEventsHandler(plushEvent: PlushEvent): void {
    switch (plushEvent.getEventCase()) {
      case PlushEvent.EventCase.EVENT_NOT_SET:
        throw new Error("event not set");
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
    }
  }

  private init(currentStatePlushEvent: CurrentStatePlushEvent): void {
    currentStatePlushEvent.getPlushesList()
      .forEach(plush => this.addOrUpdate(plush))
  }

  private addOrUpdate(plush: Plush): void {
    this.removePlush(plush.getId());

    const plushModel = PlushService.convertPlushDtoToPlushModel(plush);

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
}
