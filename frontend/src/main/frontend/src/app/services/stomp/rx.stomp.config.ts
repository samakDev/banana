import {RxStompConfig} from "@stomp/rx-stomp";

export class RxStompConfigProvider {

  public getStompConfig(): RxStompConfig {
    let protocol = 'ws:';

    if (window.location.protocol === 'https:') {
      protocol = 'wss:';
    }

    const hostname = window.location.hostname;
    const port = '9000';
    const url = protocol + '//' + hostname + ':' + port + '/websocket';

    return {
      // Which server?
      brokerURL: url,

      // Headers
      connectHeaders: {},

      // How often to heartbeat?
      // Interval in milliseconds, set to 0 to disable
      heartbeatIncoming: 0, // Typical value 0 - disabled
      heartbeatOutgoing: 20000, // Typical value 20000 - every 20 seconds

      // Wait in milliseconds before attempting auto reconnect
      // Set to 0 to disable
      // Typical value 5000 (5 seconds)
      reconnectDelay: 2000,

      // Will log diagnostics on console
      debug: (message) => {
        console.debug('stomp message received');
        console.debug(message);
      }
    };
  }
}
