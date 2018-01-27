import {Injectable} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {HttpClient} from "@angular/common/http";
import "rxjs/add/operator/share";
import {ReplaySubject} from "rxjs/ReplaySubject";
import "rxjs/add/operator/publishReplay";

@Injectable()
export class YearService {

  private _selectedYear = new ReplaySubject<string>(1);
  private _availableYears: Observable<string[]>;
  _defaultYear: string;

  constructor(private http: HttpClient) {
    this._availableYears = this.http.get<string[]>('/api/year/availableYears');
  }

  get defaultYear() {
    return this._defaultYear;
  }

  set defaultYear(defaultYear) {
    this._defaultYear = defaultYear;
  }

  get selectedYear(): Observable<string> {
    return this._selectedYear.asObservable();
  }

  setSelectedYear(selectedYear: string) {
    this._selectedYear.next(selectedYear);
  }

  get availableYears(): Observable<string[]> {
    return this._availableYears;
  }

  resetYear() {
    this._selectedYear.next(null);
  }

  selectedYearIsSameAsDefaultYear(): Observable<boolean> {
    return this.selectedYear.mergeMap(selectedYear => Observable.of(selectedYear === this.defaultYear));
  }
}
