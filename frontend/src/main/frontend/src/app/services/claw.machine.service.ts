import {Injectable} from '@angular/core';
// @ts-ignore
import {ClawMachine, ClawMachineIdentifier} from 'dto';
import {HttpBananaSenderService} from "./http.banana.sender.service";
import {map, Observable} from "rxjs";

@Injectable()
export class ClawMachineService {

  constructor(private httpBananaSenderService: HttpBananaSenderService) {
  }

  public sendCreateClawMachineCmd(clawMachineName: string, order: number): Observable<ClawMachineIdentifier> {
    const clawMachine: ClawMachine = {
      name: clawMachineName,
      order: order
    };

    return this.httpBananaSenderService.sendJsonCmd(JSON.stringify(clawMachine))
      .pipe(map(response => <ClawMachineIdentifier>response));
  }
}
