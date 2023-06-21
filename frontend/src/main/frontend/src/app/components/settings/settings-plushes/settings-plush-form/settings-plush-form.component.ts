import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {PlushModel} from "../../../../models/plush.model";

@Component({
  selector: 'app-settings-plush-form',
  templateUrl: './settings-plush-form.component.html',
  styleUrls: ['./settings-plush-form.component.css']
})
export class SettingsPlushFormComponent implements OnInit {
  @Input("plushMachine") plushModelInput: PlushModel;
  @Input("clawMachineId") clawMachineId: string;
  @Output('formSubmited') formEvent = new EventEmitter<PlushModel>();
  @Output('deleteSubmited') deleteEvent = new EventEmitter<string>();

  plushUpdated: PlushModel = new PlushModel(undefined, undefined, '', undefined, undefined);
  isEditing: boolean;

  buttonLabel: string;

  ngOnInit(): void {
    this.isEditing = this.plushModelInput !== null && this.plushModelInput !== undefined;

    this.buttonLabel = this.isEditing
      ? "settings-plush-form_update-plush-button-label"
      : "settings-plush-form_create-plush-button-label";

    if (this.isEditing) {
      this.plushUpdated.name = this.plushModelInput.name;
      this.plushUpdated.order = this.plushModelInput.order;
      this.plushUpdated.imagePath = this.plushModelInput.imagePath;
    }
  }

  selectFile(event): void {
    this.plushUpdated.newImg = event.target.files[0];
  }

  onSubmit(): void {
    const hasPlushModelInput = this.plushModelInput !== null && this.plushModelInput !== undefined;
    const plushId = hasPlushModelInput
      ? this.plushModelInput.id
      : undefined;

    const imagePath = hasPlushModelInput
      ? this.plushModelInput.imagePath
      : undefined;

    const plushEdited: PlushModel = new PlushModel(plushId,
      this.clawMachineId,
      this.plushUpdated.name,
      this.plushUpdated.order,
      imagePath,
      this.plushUpdated.newImg);

    this.formEvent.emit(plushEdited);
  }

  sendDeleteAction(): void {
    this.deleteEvent.emit(this.plushModelInput.id);
  }
}
