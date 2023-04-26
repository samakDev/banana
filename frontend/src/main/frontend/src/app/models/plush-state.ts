import {User} from './user';
import {Plush} from './plush';

export class PlushState {

  public static create(object: any): PlushState {
    if (object == null) {
      return null;
    }

    return new PlushState(object.plush, object.owner);
  }

  constructor(public plush: Plush, public owner: User) {
  }

}
