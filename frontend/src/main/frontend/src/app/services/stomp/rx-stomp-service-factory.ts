import {RxStompService} from './rx-stomp.service';
// import {StompConfig, StompService} from '@stomp/ng2-stompjs';
import {getStompConfig} from './rx.stomp.config';


export function rxStompServiceFactory() {
  const rxStomp = new RxStompService();
  rxStomp.configure(getStompConfig());
  rxStomp.activate();
  return rxStomp;
}



