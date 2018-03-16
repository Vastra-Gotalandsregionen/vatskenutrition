import {Field} from "./field";

export class Article {
  articleId: string;
  uuid: string;
  title: string;
  fields: Field[];
  path: string[];
  status: number;
  version: string;
}
