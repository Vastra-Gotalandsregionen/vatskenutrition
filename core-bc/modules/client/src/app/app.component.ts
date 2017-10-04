import {Component, OnInit} from '@angular/core';
import {YearService} from "./service/year.service";
import {Observable} from "rxjs/Observable";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  selectedYear: Observable<string>;
  includeDrafts: Observable<string>;

  constructor(public yearService: YearService,
              private route: ActivatedRoute) { }

  ngOnInit() {
    this.selectedYear = this.yearService.selectedYear;
    this.includeDrafts = this.yearService.includeDrafts;

    this.route.queryParams.subscribe(params => {

      console.log('set selectedYear in yearService...');

      if (params.selectedYear) {
        let selectedYear = params.selectedYear;

        if (selectedYear) {
          this.yearService.setSelectedYear(selectedYear);
        } else {
          this.yearService.setSelectedYear(null);
        }

      } else {
        this.yearService.setSelectedYear(null);
      }

      let includeDrafts = params.includeDrafts;
      if (includeDrafts && includeDrafts === 'true') {
        this.yearService.setIncludeDrafts('true')
      } else {
        this.yearService.setIncludeDrafts('false');
      }
    });
  }

  selectedYearIsSameAsDefaultYear() {
    return this.yearService.selectedYearIsSameAsDefaultYear()
  }

}
