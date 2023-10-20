import {Component, Input, OnInit} from '@angular/core';
import {Article} from '../../model/article';
import {ContentField} from '../../model/contentField';
import {DomSanitizer} from '@angular/platform-browser';
import {Image} from '../../model/image';

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

  getHeadingContent(field: ContentField) {
    return this.getChildValue(field, 'Rubrik1');
  }

  getRichContent(field: ContentField) {
    return this._sanitizer.bypassSecurityTrustHtml(this.getChildValue(field, 'rich_content') as string);
  }

  getImage(field: ContentField): Image {
    return this.getChildValue(field, 'image') as Image;
  }

  getStyleOption(field: ContentField) {
    return this.getChildValue(field, 'styleoption');
  }

  filterField(fieldName: string, fields: ContentField[]): ContentField[] {
    let tryFind = [];

    let tries = 0;
    while ((tryFind = fields.filter(ncf2 => ncf2.name === fieldName)).length === 0) {
      fields = this.flatMap(fields, (f: ContentField) => f.nestedContentFields);
      if (tries++ >= 5) {
        throw new Error('Cound\'t find sought field: ' + fieldName);
      }
    }

    return tryFind;
  }

  flatMap(array, mapFunc) {
    return array.reduce(function(accumulator, currentValue) {
      return accumulator.concat(mapFunc(currentValue));
    }, []);
  }

  private getChildValue(field: ContentField, fieldName: string): string | Image | null {

    if (!field.nestedContentFields) {
      return null;
    }

    const foundField = this.findRecursively(field, fieldName);

    if (foundField) {
      return foundField.contentFieldValue.data || foundField.contentFieldValue.image;
    } else {
      return null;
    }
  }

  private findRecursively(field: ContentField, fieldName: string): ContentField {
    if (!field.nestedContentFields || field.nestedContentFields.length === 0) {
      return null;
    }

    for (const nestedField of field.nestedContentFields) {
      if (nestedField.name === fieldName) {
        return nestedField;
      } else {
        const foundDeeper = this.findRecursively(nestedField, fieldName);

        if (foundDeeper) {
          return foundDeeper;
        }
      }
    }
  }
}
