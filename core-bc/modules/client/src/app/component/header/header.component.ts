import { Component, OnInit } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {YearService} from '../../service/year.service';
import {AuthStateService} from '../../service/auth/auth-state.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  additionalHeadingText: Observable<string>;

  showBackLink: boolean;

  constructor(private yearService: YearService,
              private authStateService: AuthStateService,
              private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.additionalHeadingText = this.yearService.additionalHeadingText;

    this.route.queryParams.subscribe(params => {
      if (params.article) {
        // An article is showing. We show back button
        this.showBackLink = true;
      } else {
        this.showBackLink = false;
      }
    });
  }

}
