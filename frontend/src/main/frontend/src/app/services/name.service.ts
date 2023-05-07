import {Constants} from "../constants";
import {LocalStorageService} from "angular-2-local-storage";
import {Injectable} from "@angular/core";
import {BehaviorSubject, distinctUntilChanged, Observable, Subject} from "rxjs";

@Injectable()
export class NameService {
  private currentNameSubject: Subject<string> = new BehaviorSubject("");

  constructor(private localStorageService: LocalStorageService) {
    this.setNewName(this.localStorageService.get<string>(Constants.LOCAL_STORAGE_MEMBER_NAME));

    this.currentNameSubject
      .pipe(distinctUntilChanged())
      .subscribe({
        next: name => this.localStorageService.set(Constants.LOCAL_STORAGE_MEMBER_NAME, name),
        error: e => console.error(e)
      });
  }

  public setNewName(newName: string) {
    this.currentNameSubject.next(this.stripName(newName));
  }

  private stripName(newName: string) {
    if (newName === null || newName === undefined) {
      newName = "";
    }

    return newName.trim();
  }

  public getNameObs(): Observable<string> {
    return this.currentNameSubject;
  }
}
