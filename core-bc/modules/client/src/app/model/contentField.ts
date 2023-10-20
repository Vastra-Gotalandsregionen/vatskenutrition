export class ContentField {
  name: string;
  label: string;
  contentFieldValue: { data?: string, image?: string };
  dataType: 'integer' | 'string' | 'image';
  nestedContentFields: ContentField[];

  public getHeading(): string {
    return this.contentFieldValue.data;
  }


}
