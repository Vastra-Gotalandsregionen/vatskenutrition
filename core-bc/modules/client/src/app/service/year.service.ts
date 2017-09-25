import {Injectable} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {Http} from "@angular/http";
import "rxjs/add/operator/share";
import {ReplaySubject} from "rxjs/ReplaySubject";
import {Subject} from "rxjs/Subject";
import "rxjs/add/operator/publishReplay";

@Injectable()
export class YearService {

  private _selectedYear = new ReplaySubject<string>(1);// = new BehaviorSubject()<string>();
  _defaultYear: string;

  constructor(private http: Http) {

    this.http.get('/api/year/currentYear').map(response => response.text())
      .subscribe(currentYear => {
        console.log('set defaultYear ' + currentYear);
        this._defaultYear = currentYear;

        let selectedYear = <string> sessionStorage.getItem("selectedYear");
        if (selectedYear) {
          this._selectedYear.next(selectedYear);
        } else {
          this._selectedYear.next(null);
        }
      });
  }

  get defaultYear() {
    return this._defaultYear;
  }

  get selectedYear(): Observable<string> {
    return this._selectedYear.asObservable();
  }

  setSelectedYear(selectedYear: string) {
    console.log("setSelectedYear");
    if (selectedYear) {
      sessionStorage.setItem("selectedYear", selectedYear);
    } else {
      sessionStorage.removeItem("selectedYear");
    }
    this._selectedYear.next(selectedYear);
  }

  resetYear() {
    sessionStorage.removeItem("selectedYear");
    this._selectedYear.next(null);
  }
}
