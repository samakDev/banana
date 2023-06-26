import {Component, OnDestroy, OnInit} from '@angular/core';
import {PlushModel} from "../../../../models/plush.model";
import {ActivatedRoute} from "@angular/router";
import {PlushService} from "../../../../services/plush.service";
import {map, Subscription} from "rxjs";

@Component({
  selector: 'app-settings-plush-create',
  templateUrl: './settings-plush-create.component.html',
  styleUrls: ['./settings-plush-create.component.css']
})
export class SettingsPlushCreateComponent implements OnInit, OnDestroy {
  responseSuccess: boolean = undefined;
  responseText = undefined;

  clawMachineId: string;
  private paramSubscription: Subscription;
  private createPlushSubscription: Subscription;

  constructor(private route: ActivatedRoute, private plushService: PlushService) {
  }

  ngOnInit(): void {
    this.paramSubscription = this.route.parent.params
      .pipe(map(params => {
        const clawMachineId: string = params["id"];

        return clawMachineId;
      }))
      .subscribe({
        next: clawMachineId => this.clawMachineId = clawMachineId,
        error: error => console.error("error when getting param id on ClawMachineComponent", error)
      });
  }

  ngOnDestroy() {
    this.paramSubscription.unsubscribe();

    if (this.createPlushSubscription !== undefined) {
      this.createPlushSubscription.unsubscribe();
    }
  }

  sendCreatePlush(plushEdited: PlushModel): void {
    this.createPlushSubscription = this.plushService.createPlush(plushEdited)
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
      });
  }

}
