import {Component, OnDestroy, OnInit} from '@angular/core';
import {Chapter} from "../../model/chapter";
import {Http} from "@angular/http";
import {Article} from "../../model/article";
import "rxjs/add/operator/map";
import {ActivatedRoute} from "@angular/router";
import {Field} from "../../model/field";
import {YearService} from "../../service/year.service";
import "rxjs/add/operator/mergeMap";
import "rxjs/add/operator/takeLast";
import "rxjs/add/operator/publishLast";
import "rxjs/add/operator/take";
import {Subscription} from "rxjs/Subscription";
import {DomSanitizer} from "@angular/platform-browser";
import {Observable} from "rxjs/Observable";
import "rxjs/add/observable/of";
import 'rxjs/add/operator/combineLatest';

@Component({
  selector: 'app-content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.scss']
})
export class ContentComponent implements OnInit, OnDestroy {
  private subscription: Subscription;

  chapters: Chapter[];
  article: Article;
  notReviewedText: string;
  selectedYear: Observable<string>;

  constructor(private http: Http,
              private route: ActivatedRoute,
              private _sanitizer: DomSanitizer,
              private yearService: YearService) {
    this.notReviewedText = '<span class="not-reviewed">UTKAST - ej granskat</span>';

    let n = 0;
    while (n < 10) {
      this.notReviewedText = this.notReviewedText + this.notReviewedText;
      n++;
    }

  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  ngOnInit(): void {
    let includeDrafts = this.yearService.includeDrafts;
    let selectedYear = this.yearService.selectedYear;

    includeDrafts.subscribe(a => console.log(a));
    selectedYear.subscribe(a => console.log(a));

    this.subscription = includeDrafts.combineLatest(selectedYear).mergeMap(result => {
      let includeDrafts = result[0];
      let year = result[1];

      if (!year) {
        year = 'currentYear';
      }

      let options;
      if (includeDrafts === 'true') {
        options = {params: {includeDrafts: true}};
      } else {
        options = {};
      }

      return this.http.get('/api/article/year/' + year, options)
        .map(response => {
          return response.json().reduce(function (map, article) {
            let path = article['path'][1];
            if (!map.get(path)) {
              map.set(path, []);// = [];
            }
            map.get(path).push(article);
            return map;
          }, new Map());
        })
    }).subscribe((chapters: Map<string, Article[]>) => {
      this.chapters = [];

      // this.chapterEntries = chapters.entries();
      let next;
      let entries = chapters.entries();

      while (!(next = entries.next()).done) {
        this.chapters.push({heading: next.value[0], articles: next.value[1]});
      }
    });

    this.route.queryParams
      .subscribe(params => {

        if (params.article) {
          this.http.get('/api/article/' + params.article)
            .map(response => response.json())
            .subscribe(article => this.article = article);
        } else {
          this.article = null;
        }
      });

    this.selectedYear = this.yearService.selectedYear;
  }

  getRichContent(field: Field) {
    return this._sanitizer.bypassSecurityTrustHtml(this.getChildValue(field, 'rich_content'));
  }

  getImage(field: Field): string {
    return this.getChildValue(field, 'image');
  }

  getStyleOption(field: Field) {
    return this.getChildValue(field, "style-option");
  }

  private getChildValue(field: Field, fieldName: string) {
    let result = null;
    field.children.forEach(child => {
      if (child.name === fieldName) {
        result = child.value;
      }
    });
    return result;
  }

  watermarkText(): Observable<string> {
    return this.yearService.selectedYear.mergeMap(year => {
      return Observable.of(year && year !== this.yearService.defaultYear ? this.notReviewedText : null);
    });
  }

}
