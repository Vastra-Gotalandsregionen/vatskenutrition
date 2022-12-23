import {Injectable} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {HttpClient} from "@angular/common/http";
import "rxjs/add/operator/share";
import {ReplaySubject} from "rxjs/ReplaySubject";
import "rxjs/add/operator/publishReplay";

@Injectable()
export class YearService {

  private _selectedYear = new ReplaySubject<string>(1);
  private _additionalHeadingText = new ReplaySubject<string>(1);
  private _availableYears: Observable<string[]>;
  _defaultYear: string;

  constructor(private http: HttpClient) {
    this.init();
  }

  private init() {
    this._availableYears = this.http.get<string[]>('/api/year/availableYears');
  }

  public reinit() {
    this.init();
    // Push out currentYear to make subscribes redo their stuff.
    this._selectedYear.take(1).subscribe(year => this._selectedYear.next(year));
    this._additionalHeadingText.take(1).subscribe(year => this._additionalHeadingText.next(year));
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

  setAdditionalHeadingText(text: string): void {
    this._additionalHeadingText.next(text);
  }

  get additionalHeadingText(): Observable<string> {
    return this._additionalHeadingText;
  }

  resetYear() {
    this._selectedYear.next(null);
  }

  selectedYearIsSameAsDefaultYear(): Observable<boolean> {
    return this.selectedYear.mergeMap(selectedYear => Observable.of(selectedYear === this.defaultYear));
  }
}
