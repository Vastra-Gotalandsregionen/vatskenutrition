import {Child} from "./child";

export class Field {
  name: string;
  value: string;
  children: Child[];

  public getHeading(): string {
    return this.value;
  }


}
