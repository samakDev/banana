import {Injectable} from '@angular/core';
import {Sprint} from '../models/sprint';
import {ContextService} from './context.service';
import {SprintService} from './sprint.service';
import {DateUtils} from './date.service';
import {BehaviorSubject, Observable, Subject} from 'rxjs/Rx';

@Injectable()
export class GraphService {


  private currentSprint: Sprint;

  private reloadSub: Subject<boolean>;

  constructor(private contextService: ContextService, private sprintService: SprintService) {
    this.reloadSub = new BehaviorSubject(true);
  }


  public getTitle(): Observable<string> {
    return this.getSprint()//
      .map(sprint => sprint.name);
  }

  public getDates(): Observable<Date> {
    return this.getSprint()//
      .flatMap((sprint: Sprint) => sprint.getDates());
  }

  public getSprint(): Observable<Sprint> {
    return Observable.combineLatest(this.contextService.getSelectedSprintId(), this.reloadSub, (id, r) => id)//
      .switchMap(id => this.sprintService.getOne(id));
  }

  public reload() {
    this.reloadSub.next(true);
  }

  public getXAxis(): Observable<any> {
    return this.getSprint()//
      .flatMap(sprint => sprint.getDates()//
        .bufferCount(2, 1)//
        .filter(dates => dates[0] != null && dates[1] != null)//
        .reduce((acc, dates) => {
          const firstIsWeekend: boolean = DateUtils.isWeekend(dates[0]);
          const secondIsWeekend: boolean = DateUtils.isWeekend(dates[1]);
          if (!firstIsWeekend && secondIsWeekend) {
            acc.push(Break.create(dates[1], dates[1]));
          } else if (firstIsWeekend && secondIsWeekend && acc.length === 0) {
            acc.push(Break.create(dates[0], dates[1]));
          } else if (firstIsWeekend && secondIsWeekend && acc.length >= 0) {
            acc[acc.length - 1].to = dates[1].getTime();
          } else if (firstIsWeekend && !secondIsWeekend && acc.length === 0) {
            acc.push(Break.create(dates[0], dates[1]));
          } else if (firstIsWeekend && !secondIsWeekend && acc.length >= 0) {
            acc[acc.length - 1].to = dates[1].getTime();
          }
          return acc;
        }, Array<Break>())//
        .withLatestFrom(this.contextService.getShowWeekend(), (breaks, showWeekend) => {
          if (showWeekend) {
            return [];
          }
          return breaks;
        })
        .map(breaks => {
          return {
            min: sprint.start.getTime(),
            max: sprint.end.getTime(),
            type: 'datetime',
            breaks: breaks
          };
        }));
  }

  public getComplexities(): Observable<Array<Point>> {
    return this.getSprint().switchMap(sprint => this.getFilteredDate(sprint)
      .filter(date => date.getTime() <= DateUtils.getToday().getTime())
      .map(date => {
        return new Point(date.getTime());
      }).toArray());

  }

  public getBonusComplexities(): Observable<Array<Point>> {
    return this.getSprint().switchMap(sprint => this.getFilteredDate(sprint)//
      .filter(date => date.getTime() <= DateUtils.getToday().getTime())//
      .map(date => {
        return new Point(date.getTime());
      }).toArray());
  }

  public getIdealComplexities(): Observable<Array<Point>> {
    return this.getSprint().switchMap(sprint =>
      sprint.getComplexityPerDay()//
        .flatMap(complexityPerDay => this.getFilteredDate(sprint)//
          .reduce((acc, date) => {
            if (acc.length === 0) {
              acc.push(new Point(date.getTime()));
              return acc;
            }
            const lastComplexity = acc[acc.length - 1].y;
            if (DateUtils.isWeekend(date)) {
              acc.push(new Point(date.getTime()));
            } else {
              acc.push(new Point(date.getTime()));
            }
            return acc;
          }, new Array<Point>())//
        )
    )
  }


  private getFilteredDate(sprint: Sprint): Observable<Date> {
    return sprint.getDates()//
      .withLatestFrom(this.contextService.getShowWeekend(), (date, showWeekend) => {
        const tuple = {'date': date, 'showWeekend': showWeekend};
        return tuple;
      })
      .filter(tuple => <boolean>tuple.showWeekend || !DateUtils.isWeekend(tuple.date))//
      .map(tuple => tuple.date);
  }

}


export class Break {

  public from: number;
  public to: number;

  public static create(from: Date, to: Date): Break {
    return new Break(from.getTime(), to.getTime(), 0);
  }


  constructor(from: number, to: number, public breakSize: number) {
    this.from = from + 1000 * 60 * 60 * 2;
    this.to = to + 1000 * 60 * 60 * 2;
  }

}

export class Point {

  public x: number;

  constructor(x: number) {
    this.x = x + 1000 * 60 * 60 * 2;
  }
}

export class Label {
  public enabled: any = false;

  constructor(public format: string) {
    if (format !== '') {
      this.enabled = true;
    }
  }

}
