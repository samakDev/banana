import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
// @ts-ignore
import {ClawMachine} from 'dto';
import {Observable} from "rxjs";

@Injectable()
export class HttpBananaSenderService {
  // API endpoint
  private readonly BASE_API_URL = "http://127.0.0.1:9000/";
  private readonly BASE_API_ENDPOINT = "api/";

  // ClawMachine endpoints
  private readonly CLAW_MACHINE_BASE_ENDPOINT = this.BASE_API_URL + this.BASE_API_ENDPOINT + "claw-machine/";
  private readonly CREATE_CLAW_MACHINE_ENDPOINT = this.CLAW_MACHINE_BASE_ENDPOINT + "create";

  constructor(private httpClient: HttpClient) {
  }


  public sendJsonCmd(jsonMessage: string): Observable<Object> {
    const headers = new HttpHeaders({
      "Accept": "application/json",
      "Content-Type": "application/json"
    });

    return this.httpClient.post(this.CREATE_CLAW_MACHINE_ENDPOINT, jsonMessage, {headers: headers})
  }
}
