import {Injectable} from '@angular/core';
import {User} from "../../model/user";

@Injectable()
export class StateService {

  // private _loggedInUser: User;
  // private _showContentEdit: boolean;
  // private _showDebug: boolean;
  // private _showSidenav: boolean;
  showProgress: boolean;

  constructor() {}

  /*get loggedInUser(): User {
    return this._loggedInUser;
  }

  set loggedInUser(value: User) {
    this._loggedInUser = value;
  }

  get showContentEdit(): boolean {
    return this._showContentEdit;
  }

  set showContentEdit(value : boolean) {
    this._showContentEdit = value;
  }


  get showDebug(): boolean {
    return this._showDebug;
  }

  set showDebug(value : boolean) {
    this._showDebug = value;
  }

  get showSidenav(): boolean {
    return this._showSidenav;
  }

  set showSidenav(value : boolean) {
    this._showSidenav = value;
  }

  toggleSidenav() {
    this._showSidenav = !this._showSidenav;
  }*/

  startShowProgress() {
    this.showProgress = true;
  }

  stopShowProgress() {
    this.showProgress = false;
  }
}
