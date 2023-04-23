import {Injectable} from '@angular/core';
import {BehaviorSubject, Subject} from 'rxjs/Rx';

@Injectable()
export class GraphService {
  private reloadSub: Subject<boolean>;

  constructor() {
    this.reloadSub = new BehaviorSubject(true);
  }

  public reload() {
    this.reloadSub.next(true);
  }
}
