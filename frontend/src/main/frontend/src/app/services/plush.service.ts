import {Injectable} from '@angular/core';
import {concat, defer, filter, map, Observable, of, share} from 'rxjs';
import {RxStompService} from "./stomp/rx-stomp.service";
import {Constants} from "../constants";
import {RxStompServiceFactory} from "./stomp/rx-stomp-service-factory";
import {HttpBananaPlushSenderService} from "./http-banana-plush-sender.service";
import {PlushModel} from "../models/plush.model";
import {CurrentStatePlushEvent, PlushEvent} from 'dto/target/ts/message_pb';
import {Plush, PlushIdentifier} from 'dto/target/ts/model_pb';

@Injectable()
export class PlushService {
  private plushes: Plush[] = [];
  private plushEventObs: Observable<PlushEvent>;
  private stompService: RxStompService;

  constructor(rxStompServiceFactory: RxStompServiceFactory, private senderService: HttpBananaPlushSenderService) {
    this.stompService = rxStompServiceFactory.getRxStompService()

    this.initListening();
  }

  private initListening(): void {
    this.plushEventObs = this.stompService.watch(Constants.QUEUE_BROKER_PLUSH_NAME)
      .pipe(map(message => <PlushEvent>PlushEvent.deserializeBinary(message.binaryBody)))
      .pipe(share());

    this.plushEventObs.subscribe({
      next: (plushEvent) => this.plushEventsHandler(plushEvent),
      error: (e) => console.error(e)
    });
  }

  private plushEventsHandler(plushEvent: PlushEvent): void {
    switch (plushEvent.getEventCase()) {
      case PlushEvent.EventCase.EVENT_NOT_SET:
        throw new Error("event not set");
      case PlushEvent.EventCase.CURRENTSTATEPLUSHEVENT:
        this.initState(plushEvent.getCurrentstateplushevent());
        break;
      case PlushEvent.EventCase.CREATEPLUSHEVENT:
        this.addOrUpdate(plushEvent.getCreateplushevent().getPlush())
        break;
      case PlushEvent.EventCase.UPDATEDPLUSHEVENT:
        this.addOrUpdate(plushEvent.getUpdatedplushevent().getPlush())
        break;
      case PlushEvent.EventCase.DELETEDPLUSHEVENT:
        this.removePlush(plushEvent.getDeletedplushevent().getPlushid())
        break;
    }
  }

  private initState(currentStatePlushEvent: CurrentStatePlushEvent): void {
    currentStatePlushEvent.getPlushesList()
      .forEach(plush => this.addOrUpdate(plush))
  }

  private addOrUpdate(plush: Plush): void {
    this.removePlush(plush.getId());

    this.plushes.push(plush);
  }

  private removePlush(plushId: string): void {
    const index = this.plushes.findIndex(plush => plush.getId() === plushId);

    if (index > -1) {
      this.plushes.splice(index, 1);
    }
  }

  public static convertPlushDtoToPlushModel(plush: Plush): PlushModel {
    if (!plush.hasId()) {
      return undefined;
    }

    const plushOrder = plush.hasOrder()
      ? plush.getOrder()
      : undefined;

    return new PlushModel(plush.getId(), plush.getClawMachineId(), plush.getName(), plushOrder, plush.getImageAbsolutePath());
  }

  public getPlusheEventInit(clawMachineId: string): Observable<PlushEvent> {
    return defer(() => {
      const plushesFormClawMachine = this.plushes.filter(plush => plush.getClawMachineId() === clawMachineId);

      const currentState = of(plushesFormClawMachine)
        .pipe(map(plushes => {
          const currentEvent = new CurrentStatePlushEvent();
          currentEvent.setPlushesList(plushes);

          const event = new PlushEvent();
          event.setCurrentstateplushevent(currentEvent);

          return event;
        }));

      const currentEvents = this.plushEventObs
        .pipe(filter(plushEvent => !plushEvent.hasCurrentstateplushevent()));

      return concat(currentState, currentEvents);
    });
  }

  public createPlush(plushModel: PlushModel): Observable<PlushIdentifier> {
    return this.senderService.sendCreatePlushCmd(plushModel)
      .pipe(map(response => <PlushIdentifier>response));
  }

  public updatePlush(plushToSend: PlushModel): Observable<Plush> {
    return this.senderService.sendUpdatePlushCmd(plushToSend)
      .pipe(map(response => <Plush>response));
  }

  public deletePlush(clawMachineId: string, plushId: string): Observable<Object> {
    return this.senderService.sendDeletePlushCmd(clawMachineId, plushId);
  }
}
