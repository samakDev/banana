import {Component, OnInit, ViewChild} from '@angular/core';
import {SettingsContentDirective} from "./settings.content.directive";
import {
  SettingsClawMachineCreateComponent
} from "./settings-claw-machine-create/settings-claw-machine-create.component";
import {
  SettingsClawMachineUpdateComponent
} from "./settings-claw-machine-update/settings-claw-machine-update.component";

@Component({
  selector: 'app-settings-claw-machine',
  templateUrl: './settings-claw-machine.component.html',
  styleUrls: ['./settings-claw-machine.component.css']
})
export class SettingsClawMachineComponent implements OnInit {
  @ViewChild(SettingsContentDirective, {static: true}) settingsContent!: SettingsContentDirective;

  currentTab: String = 'create';

  ngOnInit(): void {
    this.settingsContent.viewContainerRef.createComponent<any>(SettingsClawMachineCreateComponent);
  }

  setActiveTab(newTab: string) {
    this.currentTab = newTab;

    const viewContainerRef = this.settingsContent.viewContainerRef;
    viewContainerRef.clear();

    const component = (newTab === 'create')
      ? SettingsClawMachineCreateComponent
      : SettingsClawMachineUpdateComponent;

    viewContainerRef.createComponent<any>(component);
  }
}
