import {Component} from '@angular/core';
import {ClawMachineService} from "../../../../services/claw.machine.service";
import {ClawMachineModel} from "../../../../models/Claw.machine.model";

@Component({
  selector: 'app-settings-claw-machine-created',
  templateUrl: './settings-claw-machine-create.component.html',
  styleUrls: ['./settings-claw-machine-create.component.css']
})
export class SettingsClawMachineCreateComponent {
  responseSuccess: Boolean = undefined;
  responseText: String;

  constructor(private clawMachineService: ClawMachineService) {
  }

  sendCreateClawMachine(clawMachinedEdited: ClawMachineModel): void {
    this.responseSuccess = undefined;
    this.clawMachineService.sendCreateClawMachineCmd(clawMachinedEdited)
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
      })
  }
}
