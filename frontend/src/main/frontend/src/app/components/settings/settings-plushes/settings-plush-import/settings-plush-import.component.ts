import {Component, OnDestroy, OnInit} from '@angular/core';
import {PlushService} from "../../../../services/plush.service";
import {map, Subscription} from "rxjs";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-settings-plush-import',
  templateUrl: './settings-plush-import.component.html',
  styleUrls: ['./settings-plush-import.component.css']
})
export class SettingsPlushImportComponent implements OnInit, OnDestroy {
  responseSuccess: boolean;
  responseText: string;
  homeDirectory: string;

  private importFile: File;
  private clawMachineId: string;
  private paramSubscription: Subscription;
  private importPlushSubscription: Subscription;

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

    if (this.importPlushSubscription !== undefined) {
      this.importPlushSubscription.unsubscribe();
    }
  }

  public selectedFile(event) {
    this.importFile = event.target.files[0]
  }

  public onSubmit(): void {
    this.importPlushSubscription = this.plushService.importPlush(this.clawMachineId, this.importFile, this.homeDirectory)
      .subscribe({
        next: response => {
          this.responseSuccess = true;
          this.responseText = "settings-plush-import_import-success"
        },
        error: e => {
          this.responseSuccess = false;
          this.responseText = "settings-plush-import_import-error"
          console.error('error while sending post request : ', e);
        }
      });
  }
}
