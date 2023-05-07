export class ClawMachineModel {
  public id: string;
  public name: string;
  public order: number;

  constructor(id: string, name: string, order: number) {
    this.id = id;
    this.name = name;
    this.order = order;
  }
}
