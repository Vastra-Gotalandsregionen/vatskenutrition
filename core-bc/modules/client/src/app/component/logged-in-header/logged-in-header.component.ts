import {Component, Input, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {YearService} from "../../service/year.service";
import {AuthStateService} from "../../service/auth/auth-state.service";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/Observable";

@Component({
  selector: 'app-logged-in-header',
  templateUrl: './logged-in-header.component.html',
  styleUrls: ['./logged-in-header.component.scss']
})
export class LoggedInHeaderComponent implements OnInit {

  @Input('selectedYear') selectedYear: string;
  @Input('availableYears') availableYears: string[];
  @Input('displayName') displayName: string[];

  adminUrl: Observable<string>;

  constructor(
    private router: Router,
    private http: HttpClient,
    private yearService: YearService,
    private authStateService: AuthStateService
  ) {
    this.adminUrl = http.get<any>('/api/article/admin/url').map(response => response.url);
  }

  ngOnInit() {
  }

  saveSelectedYear() {
    let queryParams = {
      'selectedYear': this.selectedYear
    };

    this.router.navigate([''], {queryParams: queryParams});
  }

  selectedYearIsSameAsDefaultYear() {
    return this.yearService.selectedYearIsSameAsDefaultYear()
  }

  logout() {
    this.authStateService.resetAuth();
  }

}
