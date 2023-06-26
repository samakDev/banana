import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Plush, PlushImport, PlushLocker, PlushUnLocker, PlushUpdater} from 'dto/target/ts/model_pb';
import {PlushModel} from "../models/plush.model";

@Injectable()
export class HttpBananaPlushSenderService {
  // API endpoint
  private readonly BASE_API_URL = "http://127.0.0.1:9000/";
  private readonly BASE_API_ENDPOINT = "api/";

  // ClawMachine endpoints
  private readonly PLUSH_BASE_ENDPOINT_FN = (clawMachineId: string) => this.BASE_API_URL + this.BASE_API_ENDPOINT + `claw-machine/${clawMachineId}/plushes`;
  private readonly CREATE_PLUSH_ENDPOINT_FN = (clawMachineId: string) => this.PLUSH_BASE_ENDPOINT_FN(clawMachineId) + "/create";
  private readonly UPDATE_PLUSH_ENDPOINT_FN = (clawMachineId: string, plushId: string) => this.PLUSH_BASE_ENDPOINT_FN(clawMachineId) + `/${plushId}`;
  private readonly DELETE_PLUSH_ENDPOINT_FN = (clawMachineId: string, plushId: string) => this.PLUSH_BASE_ENDPOINT_FN(clawMachineId) + `/${plushId}`
  private readonly IMPORT_PLUSH_ENDPOINT_FN = (clawMachineId: string) => this.PLUSH_BASE_ENDPOINT_FN(clawMachineId) + `/import`
  private readonly LOCK_PLUSH_ENDPOINT_FN = (clawMachineId: string, plushId: string) => this.PLUSH_BASE_ENDPOINT_FN(clawMachineId) + `/${plushId}/lock`;
  private readonly UNLOCK_PLUSH_ENDPOINT_FN = (clawMachineId: string, plushId: string) => this.PLUSH_BASE_ENDPOINT_FN(clawMachineId) + `/${plushId}/unlock`;
  private readonly GET_PLUSH_IMG_ENDPOINT_FN = (clawMachineId: string, plushId: string) => this.PLUSH_BASE_ENDPOINT_FN(clawMachineId) + `/${plushId}`;

  constructor(private httpClient: HttpClient) {
  }

  public sendCreatePlushCmd(plushModel: PlushModel): Observable<Object> {
    const plush: Plush.AsObject = {
      id: plushModel.id,
      clawMachineId: plushModel.clawMachineId,
      name: plushModel.name,
      order: plushModel.order,
      imageAbsolutePath: undefined,
      state: undefined
    };

    const headers = new HttpHeaders({
      "Accept": "application/json"
    });

    const url = this.CREATE_PLUSH_ENDPOINT_FN(plushModel.clawMachineId);

    const formData: any = new FormData();
    formData.append("metadata", this.jsonifyMessage(plush));
    formData.append("plushImg ", plushModel.newImg);

    return this.httpClient.post(url, formData, {headers: headers})
  }

  public sendUpdatePlushCmd(plushModel: PlushModel): Observable<Object> {
    const plush: PlushUpdater.AsObject = {
      name: plushModel.name,
      order: plushModel.order
    };

    const headers = new HttpHeaders({
      "Accept": "application/json"
    });

    const url = this.UPDATE_PLUSH_ENDPOINT_FN(plushModel.clawMachineId, plushModel.id);

    const formData: any = new FormData();
    formData.append("metadata", this.jsonifyMessage(plush));
    formData.append("plushImg ", plushModel.newImg);

    return this.httpClient.patch(url, formData, {headers: headers})
  }

  public sendDeletePlushCmd(clawMachineId: string, plushId: string): Observable<Object> {
    const headers = new HttpHeaders({
      "Content-Type": "application/json"
    });

    const url = this.DELETE_PLUSH_ENDPOINT_FN(clawMachineId, plushId);

    return this.httpClient.delete(url, {headers: headers})
  }

  public importPlushCmd(clawMachineId: string, importFile: File, homeDirectory: string): Observable<Object> {
    const url = this.IMPORT_PLUSH_ENDPOINT_FN(clawMachineId);

    const metadata: PlushImport.AsObject = {
      homeDirectory: homeDirectory
    };

    const formData: any = new FormData();
    formData.append("metadata", this.jsonifyMessage(metadata))
    formData.append("bananaConfigFile", importFile);

    const headers = new HttpHeaders({
      "Accept": "application/json"
    });

    return this.httpClient.post(url, formData, {headers: headers})
  }

  public sendLockCmd(clawMachineId: string, plushId: string, memberName: string): Observable<Object> {
    const plushLocker: PlushLocker.AsObject = {
      name: memberName
    };

    const url = this.LOCK_PLUSH_ENDPOINT_FN(clawMachineId, plushId);

    const headers = new HttpHeaders({
      "Content-Type": "application/json"
    });

    return this.httpClient.post(url, this.jsonifyMessage(plushLocker), {headers: headers})
  }

  public unLock(clawMachineId: string, plushId: string, memberName: string): Observable<Object> {
    const plushUnLocker: PlushUnLocker.AsObject = {
      name: memberName
    };

    const url = this.UNLOCK_PLUSH_ENDPOINT_FN(clawMachineId, plushId);

    const headers = new HttpHeaders({
      "Content-Type": "application/json"
    });

    return this.httpClient.post(url, this.jsonifyMessage(plushUnLocker), {headers: headers})
  }

  public getImage(clawMachineId: string, plushId: string): Observable<Blob> {
    const url = this.GET_PLUSH_IMG_ENDPOINT_FN(clawMachineId, plushId)

    const headers = new HttpHeaders({
      "Accept": "application/octet-stream"
    });

    return this.httpClient.get(url, {
      headers: headers,
      responseType: 'blob'
    });
  }

  private jsonifyMessage(protoMessage: any): string {
    return JSON.stringify(protoMessage);
  }
}
