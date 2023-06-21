import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {ClawMachineModel} from "../models/claw.machine.model";
import {ClawMachine, ClawMachineUpdater} from 'dto/target/ts/model_pb';

@Injectable()
export class HttpBananaClawMachineSenderService {
  // API endpoint
  private readonly BASE_API_URL = "http://127.0.0.1:9000/";
  private readonly BASE_API_ENDPOINT = "api/";

  // ClawMachine endpoints
  private readonly CLAW_MACHINE_BASE_ENDPOINT = this.BASE_API_URL + this.BASE_API_ENDPOINT + "claw-machine/";
  private readonly CREATE_CLAW_MACHINE_ENDPOINT = this.CLAW_MACHINE_BASE_ENDPOINT + "create";
  private readonly UPDATE_CLAW_MACHINE_FN = (clawMachineId) => this.CLAW_MACHINE_BASE_ENDPOINT + `${clawMachineId}`;
  private readonly DELETE_CLAW_MACHINE_FN = (clawMachineId) => this.CLAW_MACHINE_BASE_ENDPOINT + `${clawMachineId}`;
  private readonly GET_CLAW_MACHINE_FN = (clawMachineId) => this.CLAW_MACHINE_BASE_ENDPOINT + `${clawMachineId}`;

  constructor(private httpClient: HttpClient) {
  }

  public sendCreateClawMachineCmd(clawMachineModel: ClawMachineModel): Observable<Object> {
    const clawMachine: ClawMachine.AsObject = {
      id: clawMachineModel.id,
      name: clawMachineModel.name,
      order: clawMachineModel.order
    };

    const clawMachineJson = this.jsonifyMessage(clawMachine);

    const headers = new HttpHeaders({
      "Accept": "application/json",
      "Content-Type": "application/json"
    });

    return this.httpClient.post(this.CREATE_CLAW_MACHINE_ENDPOINT, clawMachineJson, {headers: headers})
  }

  public sendUpdateClawMachineCmd(clawMachineId: string, clawMachineModel: ClawMachineModel): Observable<Object> {
    const updater: ClawMachineUpdater.AsObject = {
      name: clawMachineModel.name,
      order: clawMachineModel.order
    }

    const updateJson = this.jsonifyMessage(updater);

    const headers = new HttpHeaders({
      "Accept": "application/json",
      "Content-Type": "application/json"
    });

    const url = this.UPDATE_CLAW_MACHINE_FN(clawMachineId);

    return this.httpClient.patch(url, updateJson, {headers: headers})
  }

  public sendDeleteCmd(clawMachineId: string): Observable<Object> {
    const headers = new HttpHeaders({
      "Content-Type": "application/json"
    });

    const url = this.DELETE_CLAW_MACHINE_FN(clawMachineId);

    return this.httpClient.delete(url, {headers: headers})
  }

  public getClawMachine(clawMachineId: string): Observable<ClawMachine.AsObject> {
    const url = this.GET_CLAW_MACHINE_FN(clawMachineId);

    const headers = new HttpHeaders({
      "Content-Type": "application/json"
    });

    return this.httpClient.get(url, {headers: headers})
      .pipe(map(response => <ClawMachine.AsObject>response));
  }

  private jsonifyMessage(protoMessage: any): string {
    return JSON.stringify(protoMessage);
  }
}
