import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs/Observable";
import "rxjs/add/operator/map";
import {YearService} from "../../service/year.service";
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {StateService} from "../../service/state/state.service";
import {AuthStateService} from "../../service/auth/auth-state.service";

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {

  availableYears: Observable<string[]>;
  selectedYear: string;

  constructor(private http: HttpClient,
              private router: Router,
              public yearService: YearService,
              private stateService: StateService,
              private authStateService: AuthStateService) { }

  ngOnInit() {
    this.availableYears = this.yearService.availableYears;

    this.yearService.selectedYear.subscribe(year => this.selectedYear = year);
  }

  saveSelectedYear() {
    this.yearService.setSelectedYear(this.selectedYear);

    let queryParams = {
      'selectedYear': this.selectedYear
    };

    this.router.navigate(['/admin'], { queryParams: queryParams });
  }

  saveIncludeDrafts() {
    let queryParams = {
      'selectedYear': this.selectedYear
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

  get loggedIn(): boolean {
    return this.authStateService.isAuthenticated();
  }
}
