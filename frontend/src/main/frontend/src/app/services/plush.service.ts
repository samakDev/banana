import {Injectable} from '@angular/core';
import {concat, from, map, Observable, share} from 'rxjs';
import {PlushState} from '../models/plush-state';
import {RxStompService} from "./stomp/rx-stomp.service";
import {Constants} from "../constants";
import {RxStompServiceFactory} from "./stomp/rx-stomp-service-factory";
import {HttpClient, HttpHeaders} from "@angular/common/http";

@Injectable()
export class PlushService {

  private plushObs: Observable<PlushState>;
  private readonly plushStates: Array<PlushState>;
  private stompService: RxStompService;

  constructor(rxStompServiceFactory: RxStompServiceFactory, private httpClient: HttpClient) {
    this.plushStates = [];

    this.stompService = rxStompServiceFactory.getRxStompService()
  }

  public getPlushs(): Observable<PlushState> {
    if (this.plushObs == null) {
      this.plushObs = this.stompService.watch(Constants.QUEUE_BROKER_STATE_NAME)
        .pipe(map(message => JSON.parse(message.body)))
        .pipe(map(obj => PlushState.create(obj)))
        .pipe(share())

      this.plushObs.subscribe({
        next: (plushState) => this.addOrUpdate(plushState),
        error: (e) => console.error(e)
      });
    }

    return concat(from(this.plushStates), this.plushObs)
  }

  private addOrUpdate(plush: PlushState):
    void {
    const index = this.plushStates.findIndex(s => s.plush.id === plush.plush.id);

    if (index !== -1
    ) {
      this.plushStates[index] = plush;
    } else {
      this.plushStates.push(plush);
    }
  }

  public sendCreatePlushCmd(plushName: string, imgPath: File) {
    console.log("plushService sendCreatePlushCmd")
    console.log('plushName : ', plushName);
    console.log('imgPath : ', imgPath);
    const body = JSON.stringify({
      plushName: plushName,
      imagePath: imgPath
    });

    const data = new FormData();
    data.append("plushName", plushName);
    data.append("imagePath", imgPath);

    const headers = new HttpHeaders({
      'Access-Control-Allow-Origin': '*'
    });

    this.httpClient.post("http://127.0.0.1:9000/api/plush/create", data, {
      headers: headers
    })
      .subscribe({
        next: response => console.log('response : ', response),
        error: e => console.error('e : ', e)
      });
  }

  public take(plush: PlushState) {
    const body = JSON.stringify(plush);

    this.stompService.publish({
      destination: '/plush/take',
      body: body
    });
  }

  public release(plush: PlushState) {
    const body = JSON.stringify(plush);

    this.stompService.publish({
      destination: '/plush/release',
      body: body
    });
  }

}
