import { Component, OnInit } from '@angular/core';
import {Http} from "@angular/http";
import {Observable} from "rxjs/Observable";
import "rxjs/add/operator/map";
import {YearService} from "../../service/year.service";

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {

  availableYears: Observable<string[]>;
  selectedYear: string;
  // defaultYear: string;

  constructor(private http: Http,
              private yearService: YearService) { }

  ngOnInit() {
    this.availableYears = this.http.get('/api/year/availableYears').map(response => response.json());

    this.yearService.selectedYear.subscribe(year => this.selectedYear = year);
    // this.selectedYear = sessionStorage.getItem("selectedYear");

    /*this.http.get('/api/year/currentYear').map(response => response.text())
      .subscribe(year => {
        // this.selectedYear = year;
        this.defaultYear = year;
      });*/
  }

  saveSelectedYear() {
    this.yearService.setSelectedYear(this.selectedYear);
    console.log("saveSelectedYear");
  }

  resetYear() {
    this.yearService.resetYear();
  }

  get defaultYear() {
    return this.yearService.defaultYear;
  }
}
