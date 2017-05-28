import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {StompConfig, StompConfigService} from '@stomp/ng2-stompjs';

@Injectable()
export class ConfigService extends StompConfigService {

  /** Constructor */
  constructor() {
    super();
  }

  public get(): Observable<StompConfig> {
    const conf: StompConfig = {
      // Which server?
      url: "ws://localhost:9000/websocket",

      // Headers
      // Typical keys: login, passcode, host
      headers: {
      //  login: 'guest',
     //   passcode: 'guest'
      },

      // How often to heartbeat?
      // Interval in milliseconds, set to 0 to disable
      heartbeat_in: 0, // Typical value 0 - disabled
      heartbeat_out: 20000, // Typical value 20000 - every 20 seconds

      // Wait in milliseconds before attempting auto reconnect
      // Set to 0 to disable
      // Typical value 5000 (5 seconds)
      reconnect_delay: 5000,

      // Will log diagnostics on console
      debug: true
    };

    return Observable.of(conf);
  }
}