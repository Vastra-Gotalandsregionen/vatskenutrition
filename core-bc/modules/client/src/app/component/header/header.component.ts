import { Component, OnInit } from '@angular/core';
import {Observable} from "rxjs/Observable";
import {YearService} from "../../service/year.service";
import {AuthStateService} from "../../service/auth/auth-state.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  selectedYear: Observable<string>;

  constructor(private yearService: YearService,
              private authStateService: AuthStateService) { }

  ngOnInit() {
    this.selectedYear = this.yearService.selectedYear;
  }

}
