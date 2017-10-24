import { Component, OnInit } from '@angular/core';
import {Http} from "@angular/http";
import {Observable} from "rxjs/Observable";
import "rxjs/add/operator/map";
import {YearService} from "../../service/year.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {

  availableYears: Observable<string[]>;
  selectedYear: string;
  includeDrafts: string;

  constructor(private http: Http,
              private router: Router,
              public yearService: YearService) { }

  ngOnInit() {
    this.availableYears = this.http.get('/api/year/availableYears').map(response => response.json());

    this.yearService.selectedYear.subscribe(year => this.selectedYear = year);
    this.yearService.includeDrafts.subscribe(includeDrafts => this.includeDrafts = includeDrafts);
  }

  saveSelectedYear() {
    this.yearService.setSelectedYear(this.selectedYear);

    let queryParams = {
      'selectedYear': this.selectedYear,
      'includeDrafts': this.includeDrafts
    };

    this.router.navigate(['/admin'], { queryParams: queryParams });
  }

  saveIncludeDrafts() {
    this.yearService.setIncludeDrafts(this.includeDrafts);

    let queryParams = {
      'selectedYear': this.selectedYear,
      'includeDrafts': this.includeDrafts
    };

    this.router.navigate(['/admin'], { queryParams: queryParams });
  }

  resetYear() {
    this.yearService.resetYear();
    this.router.navigate(['/admin']);
  }

  get defaultYear() {
    return this.yearService.defaultYear;
  }
}
