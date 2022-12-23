import {Component, OnInit} from '@angular/core';
import {YearService} from "./service/year.service";
import {Observable} from "rxjs/Observable";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {AuthStateService} from "./service/auth/auth-state.service";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  selectedYear: string;

  constructor(public yearService: YearService,
              private http: HttpClient,
              private router: Router,
              private route: ActivatedRoute,
              private authStateService: AuthStateService) {
  }

  ngOnInit() {
    this.yearService.selectedYear.subscribe(selectedYear => {
      this.selectedYear = selectedYear;
    });

    let currentYearObservable = this.http.get('/api/year/currentYear', {responseType: 'text'});
    let paramsObservable = this.route.queryParams;
    const additionalHeadingTextObservable = this.http.get('/api/year/additionalHeadingText', {responseType: 'text'});

    currentYearObservable.subscribe(year => {
      this.yearService.setSelectedYear(year);
      this.yearService.defaultYear = year;
    });

    additionalHeadingTextObservable.subscribe(text => {
      this.yearService.setAdditionalHeadingText(text);
    });
  }

  get loggedIn(): boolean {
    return this.authStateService.isAuthenticated();
  }

  get displayName() {
    return this.authStateService.getLoggedInDisplayName();
  }

  get availableYears() {
    return this.yearService.availableYears;
  }

}
