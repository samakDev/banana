import {RxStompService} from './rx-stomp.service';
import {RxStompConfigProvider} from './rx.stomp.config';
import {Injectable} from "@angular/core";

@Injectable()
export class RxStompServiceFactory {

  rxStompService: RxStompService = null;

  constructor(private provider: RxStompConfigProvider) {
  }

  public getRxStompService(): RxStompService {
    if (this.rxStompService === null) {
      this.rxStompService = new RxStompService();
      this.rxStompService.configure(this.provider.getStompConfig());
      this.rxStompService.activate();
      this.rxStompService.connected();
    }

    return this.rxStompService;
  }
}
