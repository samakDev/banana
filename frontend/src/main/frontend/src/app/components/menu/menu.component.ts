import {Component, OnInit} from '@angular/core';
import {ContextService} from '../../services/context.service';
import {distinctUntilChanged, filter} from "rxjs";
import {NameService} from "../../services/name.service";
import {ClawMachineService} from "../../services/claw.machine.service";
import {ClawMachineModel} from "../../models/claw.machine.model";
import {ClawMachineEvent} from 'dto/target/ts/message_pb';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html'
})
export class MenuComponent implements OnInit {
  isCollapsed: boolean = false;
  isFullScreen: boolean = false;

  memberName: string;
  clawMachines: ClawMachineModel[] = [];

  constructor(private contextService: ContextService, private nameService: NameService, private clawMachineService: ClawMachineService) {
  }

  ngOnInit() {
    this.contextService.getFullScreenMode()
      .pipe(filter(value => !(value === null || value === undefined)))
      .pipe(distinctUntilChanged())
      .subscribe({
        next: (v) => this.isFullScreen = v.valueOf(),
        error: (e) => console.error(e)
      });

    this.listenMemberName();
    this.listenClawMachineService();
  }

  private listenMemberName() {
    this.nameService.getNameObs()
      .pipe(distinctUntilChanged())
      .pipe(filter(value => this.memberName !== value))
      .subscribe({
        next: newName => this.memberName = newName,
        error: e => console.error("error while getting name: ", e)
      })
  }

  private listenClawMachineService() {
    this.clawMachineService.getClawMachineEvents()
      .subscribe({
        next: this.clawMachineEventListener(),
        error: e => console.log('e : ', e)
      })
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

  onMemberNameChange(): void {
    this.nameService.setNewName(this.memberName);
  }

}
