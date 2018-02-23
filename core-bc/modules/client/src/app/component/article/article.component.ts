import {Component, Input, OnInit} from '@angular/core';
import {Article} from "../../model/article";
import {Field} from "../../model/field";
import {DomSanitizer} from "@angular/platform-browser";

@Component({
  selector: 'app-article',
  templateUrl: './article.component.html',
  styleUrls: ['./article.component.scss']
})
export class ArticleComponent implements OnInit {

  @Input('article')
  article: Article;

  @Input('showTitle')
  showTitle: boolean;

  constructor(
    private _sanitizer: DomSanitizer
  ) { }

  ngOnInit() {
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

  filterField(fieldName: string, fields: Field[]) {
    return fields.filter(field => field.name === fieldName);
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
}
