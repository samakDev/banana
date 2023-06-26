export class PlushModel {
  public id: string;
  public clawMachineId: string;
  public name: string;
  public order: number;
  public imagePath: string;
  public newImg: File;
  public locker: string;

  constructor(id: string, clawMachineId: string, name: string, order: number, imagePath: string, newImg: File = undefined, locker: string = undefined) {
    this.id = id;
    this.clawMachineId = clawMachineId;
    this.name = name;
    this.order = order;
    this.imagePath = imagePath;
    this.newImg = newImg;
    this.locker = locker;
  }
}
