import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-claw-machine',
  templateUrl: './claw-machine.component.html',
  styleUrls: ['./claw-machine.component.css']
})
export class ClawMachineComponent implements OnInit {

  constructor(private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.params.subscribe({
      next: params => {
        const clawMachineId: string = params["id"];

        console.log('clawMachineId : ', clawMachineId);
      },
      error: error => {
        console.error("error when getting param id on ClawMachineComponent", error);
      }
    });
  }

}
