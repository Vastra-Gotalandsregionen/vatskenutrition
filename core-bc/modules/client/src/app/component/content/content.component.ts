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

@Component({
  selector: 'app-content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.scss']
})
export class ContentComponent implements OnInit, OnDestroy {
  private subscription: Subscription;

  private chapters: Chapter[];
  private article: Article;
  private notReviewedText: string;
  // private chapters: {path: string, articles: Article[]}[];
  // private chapters: Map<string, Article>;
  // private chapterEntries: IterableIterator<[string, Article[]]>;

  // private articles: Observable<any>;

  constructor(private http: Http,
              private route: ActivatedRoute,
              private _sanitizer:DomSanitizer,
              private yearServcie: YearService) {
    this.notReviewedText = 'Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat Ej_granskat ';
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  ngOnInit(): void {
    this.subscription = this.yearServcie.selectedYear.mergeMap(year => {
      if (!year) {
        year = 'currentYear';
      }
      console.log('flag1');
      return this.http.get('/api/article/year/' + year).map(response => {
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
      console.log('flag2');

      this.chapters = [];

      // this.chapterEntries = chapters.entries();
      let next;
      let entries = chapters.entries();
      while (!(next = entries.next()).done) {
        this.chapters.push({heading: next.value[0], articles: next.value[1]});
      }
      /*chapters.entries().next().value.forEach(entry => {
        debugger;
        console.log(entry);
      })*/
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

    // .subscribe(articles => this.articles = articles);
  }

  getRichContent(field: Field) {
    // debugger;
    let fieldName = 'rich_content';
    /*let result = null;
    field.children.forEach(child => {
      if (child.name === fieldName) { // todo make constant
        result = child.value;
      }
    });
    return result;*/
    return this._sanitizer.bypassSecurityTrustHtml(this.getChildValue(field, fieldName));
  }

  getImage(field: Field): string {
    // debugger;
    let fieldName = 'image';
    return this.getChildValue(field, fieldName);
  }

  getStyleOption(field: Field) {
    return this.getChildValue(field, "style-option");
  }

  private getChildValue(field: Field, fieldName: string) {
    let result = null;
    field.children.forEach(child => {
      if (child.name === fieldName) { // todo make constant
        result = child.value;
      }
    });
    return result;
  }

  /*isReviewed(): Observable<boolean> {
    return this.yearServcie.selectedYear.mergeMap(year => {
      return Observable.of(year === this.yearServcie.defaultYear);
    });
  }*/

  watermarkText(): Observable<string> {
    return this.yearServcie.selectedYear.mergeMap(year => {
      return Observable.of(year === this.yearServcie.defaultYear ? null : this.notReviewedText);
    });
  }

}
