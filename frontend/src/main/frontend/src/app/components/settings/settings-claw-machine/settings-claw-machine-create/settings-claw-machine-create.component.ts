import {Component} from '@angular/core';
import {ClawMachineService} from "../../../../services/claw.machine.service";

@Component({
  selector: 'app-settings-claw-machine-created',
  templateUrl: './settings-claw-machine-create.component.html',
  styleUrls: ['./settings-claw-machine-create.component.css']
})
export class SettingsClawMachineCreateComponent {
  clawMachineName: string;
  clawMachineOrder: number;
  responseSuccess: Boolean = undefined;
  responseText: String;

  constructor(private clawMachineService: ClawMachineService) {
  }

  sendCreateClawMachine(event: any) {
    this.responseSuccess = undefined;
    this.clawMachineService.sendCreateClawMachineCmd(this.clawMachineName, this.clawMachineOrder)
      .subscribe({
        next: identifier => {
          this.responseSuccess = true;
          this.responseText = "settings-claw-machine_new-claw-machine-created-success"
        },
        error: e => {
          this.responseSuccess = false;
          this.responseText = "settings-claw-machine_new-claw-machine-created-error"
          console.error('error while sending post request : ', e);
        }
      })
  }

}
