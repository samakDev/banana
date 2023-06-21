import {Component, OnInit} from '@angular/core';
import {map, mergeMap} from "rxjs";
import {ActivatedRoute} from "@angular/router";
import {ClawMachineService} from "../../../services/claw.machine.service";

@Component({
  selector: 'app-settings-plushes',
  templateUrl: './settings-plushes.component.html',
  styleUrls: ['./settings-plushes.component.css']
})
export class SettingsPlushesComponent implements OnInit {
  clawMachineId: string;
  clawMachineName: string;

  constructor(private route: ActivatedRoute, private clawMachineService: ClawMachineService) {
  }

  ngOnInit(): void {
    this.route.params
      .pipe(map(params => {
        const clawMachineId: string = params["id"];

        this.clawMachineId = clawMachineId;

        return clawMachineId;
      }))
      .pipe(mergeMap(clawMachineId => this.clawMachineService.getClawMachineName(clawMachineId)))
      .subscribe({
        next: clawMachineName => this.clawMachineName = clawMachineName,
        error: error => console.error("error when getting param id on SettingsPlushesComponent or getting ClawMachineName: ", error)
      });
  }
}
