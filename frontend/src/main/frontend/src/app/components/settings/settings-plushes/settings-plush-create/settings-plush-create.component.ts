import {Component, OnInit} from '@angular/core';
import {PlushModel} from "../../../../models/plush.model";
import {ActivatedRoute} from "@angular/router";
import {PlushService} from "../../../../services/plush.service";
import {map} from "rxjs";

@Component({
  selector: 'app-settings-plush-create',
  templateUrl: './settings-plush-create.component.html',
  styleUrls: ['./settings-plush-create.component.css']
})
export class SettingsPlushCreateComponent implements OnInit {
  responseSuccess: boolean = undefined;
  responseText = undefined;

  clawMachineId: string;

  constructor(private route: ActivatedRoute, private plushService: PlushService) {
  }

  ngOnInit(): void {
    this.route.parent.params
      .pipe(map(params => {
        const clawMachineId: string = params["id"];

        return clawMachineId;
      }))
      .subscribe({
        next: clawMachineId => this.clawMachineId = clawMachineId,
        error: error => console.error("error when getting param id on ClawMachineComponent", error)
      });
  }

  sendCreatePlush(plushEdited: PlushModel): void {
    this.plushService.createPlush(plushEdited)
      .subscribe({
        next: identifier => {
          this.responseSuccess = true;
          this.responseText = "settings-plush-create_created-success"
        },
        error: e => {
          this.responseSuccess = false;
          this.responseText = "settings-plush-create_created-error"
          console.error('error while sending post request : ', e);
        }
      })
  }

}
