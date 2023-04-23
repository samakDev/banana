import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Sprint } from '../models/sprint';
import { Observable } from 'rxjs/Rx';
import { AbstractRestClientService } from './abstract-rest-client.service';

@Injectable()
export class SprintService extends AbstractRestClientService<Sprint>  {

  public static readonly EMBEDDED_NAME = 'sprints';

  constructor(private http: Http) {
    super(http, SprintService.EMBEDDED_NAME);
  }

  public getAllByPage(): Observable<Sprint> {
    return this._getAllByPage().map(o => Sprint.create(o));
  }
  public getAll(): Observable<Sprint> {
    return this._getAll().map(o => Sprint.create(o));
  }
  public getOne(id: string): Observable<Sprint> {
    return this._getOne(id).map(o => Sprint.create(o));
  }
  public save(sprint: Sprint): Observable<Sprint> {
    return this._save(Sprint.create(sprint));
  }

}
