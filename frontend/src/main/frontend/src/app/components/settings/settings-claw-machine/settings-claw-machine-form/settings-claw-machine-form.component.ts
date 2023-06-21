import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {NgForm} from "@angular/forms";
import {ClawMachineModel} from "../../../../models/claw.machine.model";
import {Router} from "@angular/router";

@Component({
  selector: 'app-settings-claw-machine-form',
  templateUrl: './settings-claw-machine-form.component.html',
  styleUrls: ['./settings-claw-machine-form.component.css']
})
export class SettingsClawMachineFormComponent implements OnInit {
  @Input("clawMachine") clawMachineInput: ClawMachineModel;
  @Input("isEditing") isEditing: boolean;
  @Output('formSubmited') formEvent = new EventEmitter<ClawMachineModel>();
  @Output('deleteSubmited') deleteEvent = new EventEmitter<string>();

  buttonLabel: string;

  clawMachineUpdated: ClawMachineModel = {
    id: undefined,
    name: '',
    order: undefined
  };

  @ViewChild("form") form: NgForm;

  constructor(private router: Router) {
  }

  ngOnInit() {
    if (this.clawMachineInput !== undefined) {
      this.clawMachineUpdated.name = this.clawMachineInput.name;
      this.clawMachineUpdated.order = this.clawMachineInput.order;
    }

    this.buttonLabel = this.isEditing
      ? 'settings-claw-machine-form_update-claw-machine-button-label'
      : 'settings-claw-machine-form_create-claw-machine-button-label'
  }

  onSubmit() {
    const clawMachineId = this.clawMachineInput === null || this.clawMachineInput === undefined
      ? undefined
      : this.clawMachineInput.id;

    let clawMachineEdited: ClawMachineModel = new ClawMachineModel(clawMachineId, this.clawMachineUpdated.name, this.clawMachineUpdated.order);

    this.formEvent.emit(clawMachineEdited);
  }

  editPlushes(id: string) {
    this.router.navigate(['/settings/' + id + "/create"]);
  }

  sendDeleteAction(id: string) {
    this.deleteEvent.emit(id);
  }

}
