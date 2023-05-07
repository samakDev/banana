import {Injectable} from '@angular/core';
import {HttpBananaClawMachineSenderService} from "./http-banana-claw-machine-sender.service";
import {ClawMachineModel} from "../models/claw.machine.model";

@Injectable()
export class ClawMachineService {

  constructor(private httpBananaSenderService: HttpBananaClawMachineSenderService) {
  }

  public sendCreateClawMachineCmd(clawMachine: ClawMachineModel): Observable<ClawMachineIdentifier> {
    return this.httpBananaSenderService.sendCreateClawMachineCmd(clawMachine)
      .pipe(map(response => <ClawMachineIdentifier>response));
  }
}
