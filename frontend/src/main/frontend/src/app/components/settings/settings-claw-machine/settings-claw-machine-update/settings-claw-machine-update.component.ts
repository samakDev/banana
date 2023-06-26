import {Component, OnDestroy} from '@angular/core';
import {ClawMachineService} from "../../../../services/claw.machine.service";
import {ClawMachineModel} from "../../../../models/claw.machine.model";
import {ClawMachineEvent} from 'dto/target/ts/message_pb';
import {HttpBananaClawMachineSenderService} from "../../../../services/http-banana-claw-machine-sender.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-settings-claw-machine-update',
  templateUrl: './settings-claw-machine-update.component.html',
  styleUrls: ['./settings-claw-machine-update.component.css']
})
export class SettingsClawMachineUpdateComponent implements OnDestroy {
  clawMachines: ClawMachineModel[] = [];
  currentClawMachineIdEditing: string;
  responseSuccess: boolean = undefined;
  responseText: string;

  private clawMachineSubscription: Subscription;
  private updateCmdSubscription: Subscription;
  private deleteCmdSubscription: Subscription;

  constructor(private clawMachineService: ClawMachineService, private senderService: HttpBananaClawMachineSenderService) {
    this.clawMachineSubscription = this.clawMachineService.getClawMachineEvents()
      .subscribe({
        next: this.clawMachineEventListener(),
        error: e => console.log('e : ', e)
      });
  }

  ngOnDestroy() {
    this.clawMachineSubscription.unsubscribe();

    if (this.updateCmdSubscription !== undefined) {
      this.updateCmdSubscription.unsubscribe();
    }

    if (this.deleteCmdSubscription !== undefined) {
      this.deleteCmdSubscription.unsubscribe();
    }
  }

  private clawMachineEventListener() {
    return clawMachineEvent => {
      switch (clawMachineEvent.getEventCase()) {
        default:
        case ClawMachineEvent.EventCase.EVENT_NOT_SET:
          throw new Error("event not set");
        case ClawMachineEvent.EventCase.CURRENTSTATECLAWMACHINEEVENT:
          clawMachineEvent.getCurrentstateclawmachineevent()
            .getClawmachinesList()
            .map(clawMachine => ClawMachineService.convertClawMachineDtoToClawMachineModel(clawMachine))
            .forEach(clawMachine => this.addOrUpdate(clawMachine))
          break;
        case ClawMachineEvent.EventCase.CREATECLAWMACHINEEVENT:
          const createdEvent = clawMachineEvent.getCreateclawmachineevent();
          if (createdEvent.hasClawmachine()) {
            const model = ClawMachineService.convertClawMachineDtoToClawMachineModel(createdEvent.getClawmachine());
            this.addOrUpdate(model);
          }
          break;
        case ClawMachineEvent.EventCase.UPDATEDCLAWMACHINEEVENT:
          const updatedEvent = clawMachineEvent.getUpdatedclawmachineevent();
          if (updatedEvent.hasClawmachine()) {
            const model = ClawMachineService.convertClawMachineDtoToClawMachineModel(updatedEvent.getClawmachine());
            this.addOrUpdate(model);
          }
          break;
        case ClawMachineEvent.EventCase.DELETEDCLAWMACHINEEVENT:
          const deletedEvent = clawMachineEvent.getDeletedclawmachineevent();

          this.removeClawMachine(deletedEvent.getClawmachineid());
          break;
      }
    };
  }

  private addOrUpdate(clawMachineModel: ClawMachineModel): void {
    const index = this.clawMachines.findIndex(model => model.id === clawMachineModel.id);

    if (index !== -1) {
      this.removeClawMachine(clawMachineModel.id);
    }

    this.clawMachines.push(clawMachineModel);

    this.clawMachines = this.clawMachines
      .sort((model1, model2) => model1.order >= model2.order ? 0 : -1);
  }

  private removeClawMachine(clawMachineId: string) {
    const index = this.clawMachines.findIndex(clawMachine => clawMachine.id === clawMachineId);

    if (index > -1) {
      this.clawMachines.splice(index, 1);
    }
  }

  isEditingMode(clawMachineId: string): boolean {
    return this.currentClawMachineIdEditing === clawMachineId;
  }

  setNewClawMachineInEditionMode(clawMachineId: string): void {
    if (this.isEditingMode(clawMachineId)) {
      this.currentClawMachineIdEditing = null;
    } else {
      this.currentClawMachineIdEditing = clawMachineId;
    }
  }

  sendUpdateClawMachine(clawMachineEdited: ClawMachineModel) {
    this.responseSuccess = undefined;
    this.updateCmdSubscription = this.senderService.sendUpdateClawMachineCmd(clawMachineEdited.id, clawMachineEdited)
      .subscribe({
        next: any => {
          this.responseSuccess = true;
          this.responseText = "settings-claw-machine-update_updated-success"
        },
        error: e => {
          this.responseSuccess = false;
          this.responseText = "settings-claw-machine-update_updated-error"
          console.error("can't update", e);
        }
      });
  }

  sendDeleteClawMachine(clawMachineId: string) {
    this.responseSuccess = undefined;
    this.deleteCmdSubscription = this.senderService.sendDeleteCmd(clawMachineId)
      .subscribe({
        next: any => {
          console.log("delete done");
          this.responseSuccess = true;
          this.responseText = "settings-claw-machine-update_deleted-success"
        },
        error: e => {
          this.responseSuccess = false;
          this.responseText = "settings-claw-machine-update_deleted-error"
          console.error("can't delete" + clawMachineId + " error :", e);
        }
      });
  }
}
