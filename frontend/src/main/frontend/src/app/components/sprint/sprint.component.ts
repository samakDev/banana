import {Component, OnInit} from '@angular/core';
import {Sprint} from '../../models/sprint';
import {SprintService} from '../../services/sprint.service';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-sprint',
  templateUrl: './sprint.component.html',
  styleUrls: ['./sprint.component.css'],
})
export class SprintComponent implements OnInit {

  sprint: Sprint;
  complexities: number[];
  types: string[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private sprintService: SprintService) {
    this.sprint = Sprint.create();
    this.complexities = [0, 0.5, 1, 2, 3, 5, 8, 13];
    this.types = ['USER_STORY', 'BUG_STORY', 'TECHNICAL_STORY'];
  }

  ngOnInit() {
    this.route.params
      .switchMap((params: Params) => this.sprintService.getOne(params['id']))//
      .subscribe((sprint: Sprint) => this.sprint = sprint, e => console.error(e))//
  }

}
