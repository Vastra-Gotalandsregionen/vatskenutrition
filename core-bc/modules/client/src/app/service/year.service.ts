import {Injectable} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {Http} from "@angular/http";
import "rxjs/add/operator/share";
import {ReplaySubject} from "rxjs/ReplaySubject";
import "rxjs/add/operator/publishReplay";

@Injectable()
export class YearService {

  private _selectedYear = new ReplaySubject<string>(1);// = new BehaviorSubject()<string>();
  private _includeDrafts = new ReplaySubject<string>(1);
  _defaultYear: string;

  constructor(private http: Http) {

    this.http.get('/api/year/currentYear').map(response => response.text())
      .subscribe(currentYear => {
        this._defaultYear = currentYear;
      });
  }

  get defaultYear() {
    return this._defaultYear;
  }

  get selectedYear(): Observable<string> {
    return this._selectedYear.asObservable();
  }

  setSelectedYear(selectedYear: string) {
    this._selectedYear.next(selectedYear);
  }

  get includeDrafts(): Observable<string> {
    return this._includeDrafts;
  }

  setIncludeDrafts(includeDrafts: string) {
    this._includeDrafts.next(includeDrafts);
  }

  resetYear() {
    this._selectedYear.next(null);
  }

  selectedYearIsSameAsDefaultYear(): Observable<boolean> {
    return this.selectedYear.mergeMap(selectedYear => Observable.of(selectedYear === this.defaultYear));
  }
}
