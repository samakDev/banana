import {Injectable} from '@angular/core';
import {concat, from, map, mergeMap, Observable, share} from 'rxjs';
import {PlushState} from '../models/plush-state';
import {Constants} from '../constants';
import {RxStompService} from "./stomp/rx-stomp.service";

@Injectable()
export class PlushService {

  private plushObs: Observable<PlushState>;
  private plushStates: Array<PlushState>;

  constructor(private stompService: RxStompService) {
    this.plushStates = [];
  }


  public getPlushs(): Observable<PlushState> {
    if (this.plushObs == null) {
      this.plushObs = this.stompService.watch(Constants.QUEUE_BROKER_STATE_NAME)
        .pipe(map(message => JSON.parse(message.body)))
        .pipe(map(obj => PlushState.createFromArray(obj)))
        .pipe(mergeMap(plushStats => from(plushStats)))
        .pipe(share())

      this.plushObs.subscribe({
        next: (plushState) => this.addOrUpdate(plushState),
        error: (e) => console.error(e)
      });
    }

    return concat(from(this.plushStates), this.plushObs)
  }

  private addOrUpdate(plush: PlushState): void {
    const index = this.plushStates.findIndex(s => s.plush.id === plush.plush.id);

    if (index !== -1) {
      this.plushStates[index] = plush;
    } else {
      this.plushStates.push(plush);
    }
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
