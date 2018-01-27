import {Component, OnDestroy, OnInit} from '@angular/core';
import {Chapter} from "../../model/chapter";
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
import {HttpClient} from "@angular/common/http";
import {AuthStateService} from "../../service/auth/auth-state.service";

@Component({
  selector: 'app-content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.scss']
})
export class ContentComponent implements OnInit, OnDestroy {
  private subscription: Subscription;

  chapters: Chapter[];
  article: Article;
  selectedYear: Observable<string>;

  constructor(private httpClient: HttpClient,
              private route: ActivatedRoute,
              private _sanitizer: DomSanitizer,
              private yearService: YearService,
              private authStateService: AuthStateService) {
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  ngOnInit(): void {
    let selectedYear = this.yearService.selectedYear;

    selectedYear.subscribe(a => console.log(a));

    this.subscription = selectedYear.mergeMap(result => {
      let year = result;

      if (!year || year === this.yearService.defaultYear) {
        year = 'currentYear';
      }

      return this.httpClient.get<string[]>('/api/article/year/' + year)
        .map(response => {
          return response.reduce(function (map, article) {
            let path = article['path'][1];
            if (!map.get(path)) {
              map.set(path, []);// = [];
            }
            map.get(path).push(article);
            return map;
          }, new Map());
        });
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
          this.httpClient.get<Article>('/api/article/' + params.article)
            .subscribe(article => this.article = article);
        } else {
          this.article = null;
        }
      });

    this.selectedYear = this.yearService.selectedYear;
  }

  get loggedIn() {
    return this.authStateService.isAuthenticated();
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

  scrollToSubChapter(subChapter: string) {
    try {
      let subChapterElement = document.querySelector('a[name="' + subChapter + '"]');
      subChapterElement.scrollIntoView();
      subChapterElement.closest('.scroll-container').scrollTop -= 12;
    } catch (err) {
      console.log(err);
    }
  }

  scrollToTop() {
    document.querySelector('.col-8').scrollTop = 0;
  }

  watermarkText(): Observable<boolean> {
    return this.yearService.selectedYear.mergeMap(year => {
      return Observable.of(year && year !== this.yearService.defaultYear);
    });
  }

}
