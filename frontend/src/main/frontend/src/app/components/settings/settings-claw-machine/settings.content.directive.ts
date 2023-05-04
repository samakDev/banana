import {Directive, ViewContainerRef} from "@angular/core";

@Directive({
  selector: '[settingsContent]',
})
export class SettingsContentDirective {
  constructor(public viewContainerRef: ViewContainerRef) {
  }
}
