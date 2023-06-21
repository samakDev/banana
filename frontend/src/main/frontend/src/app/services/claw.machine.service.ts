import {Injectable} from '@angular/core';
import {HttpBananaClawMachineSenderService} from "./http-banana-claw-machine-sender.service";
import {concat, from, map, Observable, share} from "rxjs";
import {RxStompServiceFactory} from "./stomp/rx-stomp-service-factory";
import {RxStompService} from "./stomp/rx-stomp.service";
import {Constants} from "../constants";
import {ClawMachineModel} from "../models/claw.machine.model";
import {ClawMachineEvent} from 'dto/target/ts/message_pb';
import {ClawMachine, ClawMachineIdentifier} from 'dto/target/ts/model_pb';

@Injectable()
export class ClawMachineService {
  private clawMachineEventObs: Observable<ClawMachineEvent> = null;
  private stompService: RxStompService;
  private clawMachinesEvents: ClawMachineEvent[] = [];

  constructor(private httpBananaSenderService: HttpBananaClawMachineSenderService, rxStompServiceFactory: RxStompServiceFactory) {
    this.stompService = rxStompServiceFactory.getRxStompService()
  }

  public sendCreateClawMachineCmd(clawMachine: ClawMachineModel): Observable<ClawMachineIdentifier> {
    return this.httpBananaSenderService.sendCreateClawMachineCmd(clawMachine)
      .pipe(map(response => <ClawMachineIdentifier>response));
  }

  public getClawMachineEvents(): Observable<ClawMachineEvent> {
    if (this.clawMachineEventObs === null) {
      this.clawMachineEventObs = this.stompService.watch(Constants.QUEUE_BROKER_CLAW_MACHINE_NAME)
        .pipe(map(message => ClawMachineEvent.deserializeBinary(message.binaryBody)))
        .pipe(share())

      this.clawMachineEventObs.subscribe({
        next: (clawMachineEvent) => this.addOrUpdate(clawMachineEvent),
        error: (e) => console.error(e)
      });
    }

    return concat(from(this.clawMachinesEvents), this.clawMachineEventObs)
  }

  private addOrUpdate(clawMachineEvent: ClawMachineEvent) {
    this.clawMachinesEvents.push(clawMachineEvent);
  }

  public getClawMachineName(clawMachineId: string): Observable<string> {
    return this.httpBananaSenderService.getClawMachine(clawMachineId)
      .pipe(map(clawMachine => clawMachine.name))
  }

  public static convertClawMachineDtoToClawMachineModel(clawMachine: ClawMachine): ClawMachineModel {
    if (!clawMachine.hasId()) {
      return undefined;
    }

    const clawMachineOrder = clawMachine.hasOrder()
      ? clawMachine.getOrder()
      : undefined;

    return new ClawMachineModel(clawMachine.getId(), clawMachine.getName(), clawMachineOrder);
  }
}
