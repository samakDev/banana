import {Constants} from "../constants";
import {LocalStorageService} from "angular-2-local-storage";
import {Injectable} from "@angular/core";
import {BehaviorSubject, defer, map, Observable} from "rxjs";

class NameHolder {
  clawMachineId: string;
  currentName: string;
}

@Injectable()
export class NameService {
  private currentNameSubject: BehaviorSubject<NameHolder[]> = new BehaviorSubject([]);

  constructor(private localStorageService: LocalStorageService) {
  }

  public updateName(clawMachineId: string, newName: string = undefined): void {
    const nameHolderArray = this.currentNameSubject.getValue();

    if (newName === undefined || newName === null || newName.length === 0) {
      this.remove(nameHolderArray, clawMachineId);
    } else {
      this.update(nameHolderArray, clawMachineId, newName);
    }
  }

  private remove(nameHolderArray: NameHolder[], clawMachineId: string) {
    const key = this.storeServiceKeyGenerator(clawMachineId);
    this.localStorageService.remove(key);

    this.removeInArray(nameHolderArray, clawMachineId);

    this.currentNameSubject.next(nameHolderArray);
  }

  private update(nameHolderArray: NameHolder[], clawMachineId: string, newName: string) {
    const currentName = this.stripName(newName);

    const key = this.storeServiceKeyGenerator(clawMachineId);
    this.localStorageService.set(key, currentName);

    this.removeInArray(nameHolderArray, clawMachineId);

    nameHolderArray.push({
      clawMachineId: clawMachineId,
      currentName: currentName
    });
    this.currentNameSubject.next(nameHolderArray);
  }

  private stripName(newName: string) {
    if (newName === null || newName === undefined) {
      newName = undefined;
    }

    return newName.trim();
  }

  private removeInArray(nameHolderArray: NameHolder[], clawMachineId: string): void {
    const index = nameHolderArray.findIndex(nameHolder => nameHolder.clawMachineId === clawMachineId);
    if (index > -1) {
      nameHolderArray.splice(index, 1);
    }
  }

  private storeServiceKeyGenerator(clawMachineId: string): string {
    return Constants.LOCAL_STORAGE_MEMBER_NAME + "_" + clawMachineId;
  }

  public getNameObs(clawMachineId: string): Observable<string> {
    return defer(() => {
      const currentName = this.localStorageService.get<string>(this.storeServiceKeyGenerator(clawMachineId));

      this.updateName(clawMachineId, currentName);

      return this.currentNameSubject
        .pipe(map(currentArray => {
          const index = currentArray.findIndex(nameHolder => nameHolder.clawMachineId === clawMachineId);

          if (index > -1) {
            return currentArray[index].currentName;
          } else {
            return undefined;
          }
        }));
    });
  }
}
