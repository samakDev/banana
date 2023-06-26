import {Component, OnDestroy} from '@angular/core';
import {ClawMachineService} from "../../../../services/claw.machine.service";
import {ClawMachineModel} from "../../../../models/Claw.machine.model";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-settings-claw-machine-created',
  templateUrl: './settings-claw-machine-create.component.html',
  styleUrls: ['./settings-claw-machine-create.component.css']
})
export class SettingsClawMachineCreateComponent implements OnDestroy {
  responseSuccess: boolean = undefined;
  responseText: string;
  private sendClawMachineCmdSubscription: Subscription;

  constructor(private clawMachineService: ClawMachineService) {
  }

  ngOnDestroy() {
    if (this.sendClawMachineCmdSubscription !== undefined) {
      this.sendClawMachineCmdSubscription.unsubscribe();
    }
  }

  sendCreateClawMachine(clawMachinedEdited: ClawMachineModel): void {
    this.responseSuccess = undefined;
    this.sendClawMachineCmdSubscription = this.clawMachineService.sendCreateClawMachineCmd(clawMachinedEdited)
      .subscribe({
        next: identifier => {
          this.responseSuccess = true;
          this.responseText = "settings-claw-machine-create_created-success"
        },
        error: e => {
          this.responseSuccess = false;
          this.responseText = "settings-claw-machine-create_created-error"
          console.error('error while sending post request : ', e);
        }
      });
  }
}
