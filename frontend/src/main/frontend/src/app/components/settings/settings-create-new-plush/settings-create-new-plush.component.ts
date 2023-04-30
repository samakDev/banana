import {Component} from '@angular/core';
import {PlushService} from "../../../services/plush.service";

@Component({
  selector: 'app-settings-create-new-plush',
  templateUrl: './settings-create-new-plush.component.html',
  styleUrls: ['./settings-create-new-plush.component.css']
})
export class SettingsCreateNewPlushComponent {

  plushName: string;
  plushImg: File;

  public constructor(private plushService: PlushService) {
  }

  public sendCreatePlush(event: any) {
    this.plushService.sendCreatePlushCmd(this.plushName, this.plushImg)
  }

}
