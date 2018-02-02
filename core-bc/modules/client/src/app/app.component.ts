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

    currentYearObservable.combineLatest(paramsObservable, (currentYear, params) => [currentYear, params])
      .subscribe(array => {

        let defaultYear = array[0];
        let params = array[1];

        this.yearService.defaultYear = <string>defaultYear;

        let selectedYearFromParams = (<Params>params).selectedYear;
        if (selectedYearFromParams) {
          let selectedYear = selectedYearFromParams;

            this.yearService.setSelectedYear(selectedYear);
        } else {
          this.yearService.setSelectedYear(<string>defaultYear);
        }
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
