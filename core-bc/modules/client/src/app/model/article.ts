import {ContentField} from './contentField';

export class Article {
  title: string;
  uuid: string;
  contentFields: ContentField[];
  path: string[];
}
