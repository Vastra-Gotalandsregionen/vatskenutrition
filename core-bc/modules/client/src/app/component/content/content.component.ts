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
import {Observable} from "rxjs/Observable";
import "rxjs/add/observable/of";
import 'rxjs/add/operator/combineLatest';
import {HttpClient} from "@angular/common/http";
import {AuthStateService} from "../../service/auth/auth-state.service";
import {StateService} from "../../service/state/state.service";

@Component({
  selector: 'app-content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.scss']
})
export class ContentComponent implements OnInit, OnDestroy {
  private subscription: Subscription;

  chapters: Chapter[];
  article: Article;
  startPageArticle: Article;
  selectedYear: Observable<string>;

  constructor(private httpClient: HttpClient,
              private route: ActivatedRoute,
              private yearService: YearService,
              private stateService: StateService,
              private authStateService: AuthStateService) {
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  ngOnInit(): void {
    let selectedYear = this.yearService.selectedYear;

    selectedYear.mergeMap(year => {
      if (!year || year === this.yearService.defaultYear) {
        year = 'currentYear';
      }

      return this.httpClient.get<Article>('/api/article/startPageArticle/' + year)
    }).subscribe(article => this.startPageArticle = article);

    this.subscription = selectedYear
      .mergeMap(result => {
        let year = result;

        if (!year || year === this.yearService.defaultYear) {
          year = 'currentYear';
        }

        return this.httpClient.get<Article[]>('/api/article/year/' + year)
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
      }).retry(3)
      .subscribe((chapters: Map<string, Article[]>) => {
        this.chapters = [];
        // this.chapterEntries = chapters.entries();
        let next;
        let entries = chapters.entries();

        while (!(next = entries.next()).done) {
          // next: {value: [chapterName: string, articles: Article[]], done: boolean},
          let articles = <Article[]>next.value[1];
          articles.sort((a, b) => this.sort(a, b));

          this.chapters.push({heading: next.value[0], articles: articles});
        }

        this.chapters.sort(((a, b) => a.heading.localeCompare(b.heading)));
      }, error => {
        console.log('error: ' + error);
      });

    this.route.queryParams
      .subscribe(params => {

        if (params.article) {
          const timerSubscription: Subscription = Observable.timer(250) // Delay when progress indicator is shown.
            .take(1)
            .subscribe(() => {
              this.article = null; // Makes view show progress indidator
            });

          this.httpClient.get<Article>('/api/article/' + params.article)
            .finally(() => timerSubscription.unsubscribe())
            .subscribe(article => this.article = article);
        } else {
          this.article = null;
        }
      });

    this.selectedYear = this.yearService.selectedYear;
  }

  private sort(a: Article, b: Article): number {
    let aSortNumber = this.getSortNumber(a);
    let bSortNumber = this.getSortNumber(b);
    if (aSortNumber < bSortNumber) {
      return -1;
    } else if (aSortNumber > bSortNumber) {
      return 1;
    } else {
      return a.title.localeCompare(b.title);
    }
  }

  private getSortNumber(article: Article): number {
    let sortFields = this.filterField('sort-number', article.fields);
    if (sortFields.length === 1) {
      return Number.parseInt(sortFields[0].value);
    } else {
      return 999;
    }
  }

  get loggedIn() {
    return this.authStateService.isAuthenticated();
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

  get showProgress() {
    return this.stateService.showProgress;
  }

  filterField(fieldName: string, fields: Field[]): Field[] {
    return fields.filter(field => field.name === fieldName);
  }
}
