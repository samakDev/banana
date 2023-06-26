import {Component, OnDestroy, OnInit} from '@angular/core';
import {ContextService} from '../../services/context.service';
import {distinctUntilChanged, filter, Subscription} from "rxjs";
import {ClawMachineService} from "../../services/claw.machine.service";
import {ClawMachineModel} from "../../models/claw.machine.model";
import {ClawMachineEvent} from 'dto/target/ts/message_pb';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html'
})
export class MenuComponent implements OnInit, OnDestroy {
  isCollapsed: boolean = false;
  isFullScreen: boolean = false;

  clawMachines: ClawMachineModel[] = [];

  private fullScreenSubscription: Subscription;
  private clawEventSubscription: Subscription;

  constructor(private contextService: ContextService, private clawMachineService: ClawMachineService) {
  }

  ngOnInit() {
    this.fullScreenSubscription = this.contextService.getFullScreenMode()
      .pipe(filter(value => !(value === null || value === undefined)))
      .pipe(distinctUntilChanged())
      .subscribe({
        next: (v) => this.isFullScreen = v.valueOf(),
        error: (e) => console.error(e)
      });

    this.clawEventSubscription = this.clawMachineService.getClawMachineEvents()
      .subscribe({
        next: this.clawMachineEventListener(),
        error: e => console.log('e : ', e)
      });
  }

  ngOnDestroy(): void {
    this.fullScreenSubscription.unsubscribe();
    this.clawEventSubscription.unsubscribe();
  }

  private clawMachineEventListener() {
    return clawMachineEvent => {
      switch (clawMachineEvent.getEventCase()) {
        default:
        case ClawMachineEvent.EventCase.EVENT_NOT_SET:
          throw new Error("event not set");
        case ClawMachineEvent.EventCase.CURRENTSTATECLAWMACHINEEVENT:
          clawMachineEvent.getCurrentstateclawmachineevent()
            .getClawmachinesList()
            .map(clawMachine => ClawMachineService.convertClawMachineDtoToClawMachineModel(clawMachine))
            .forEach(clawMachine => this.addOrUpdate(clawMachine))
          break;
        case ClawMachineEvent.EventCase.CREATECLAWMACHINEEVENT:
          const createdEvent = clawMachineEvent.getCreateclawmachineevent();
          if (createdEvent.hasClawmachine()) {
            const model = ClawMachineService.convertClawMachineDtoToClawMachineModel(createdEvent.getClawmachine());
            this.addOrUpdate(model);
          }
          break;
        case ClawMachineEvent.EventCase.UPDATEDCLAWMACHINEEVENT:
          const updatedEvent = clawMachineEvent.getUpdatedclawmachineevent();
          if (updatedEvent.hasClawmachine()) {
            const model = ClawMachineService.convertClawMachineDtoToClawMachineModel(updatedEvent.getClawmachine());
            this.addOrUpdate(model);
          }
          break;
        case ClawMachineEvent.EventCase.DELETEDCLAWMACHINEEVENT:
          const deletedEvent = clawMachineEvent.getDeletedclawmachineevent();

          this.removeClawMachine(deletedEvent.getClawmachineid());
          break;
      }
    };
  }

  private addOrUpdate(clawMachineModel: ClawMachineModel) {
    const index = this.clawMachines.findIndex(model => model.id === clawMachineModel.id);

    if (index !== -1) {
      this.removeClawMachine(clawMachineModel.id);
    }

    this.clawMachines.push(clawMachineModel);

    this.clawMachines = this.clawMachines
      .sort((model1, model2) => model1.order >= model2.order ? 0 : -1);
  }

  private removeClawMachine(clawMachineId: string) {
    const index = this.clawMachines.findIndex(clawMachine => clawMachine.id === clawMachineId);

    if (index > -1) {
      this.clawMachines.splice(index, 1);
    }
  }
}
