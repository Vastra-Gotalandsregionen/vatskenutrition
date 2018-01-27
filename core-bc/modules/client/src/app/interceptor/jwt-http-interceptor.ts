import {Observable} from 'rxjs/Observable';
import {Injectable} from '@angular/core';
import {Location} from '@angular/common';
import {AuthStateService} from '../service/auth/auth-state.service';
import {ErrorHandler} from "../service/errorHandler/error-handler";
import {StateService} from "../service/state/state.service";
import {Subscription} from "rxjs/Subscription";
import 'rxjs/add/observable/throw';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";

@Injectable()
export class JwtHttpInterceptor implements HttpInterceptor {

  authService: AuthStateService;
  errorHandler: ErrorHandler;
  stateService: StateService;

  constructor(authService: AuthStateService,
              errorHandler: ErrorHandler,
              stateService: StateService) {
    this.authService = authService;
    this.errorHandler = errorHandler;
    this.stateService = stateService;
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    const timerSubscription: Subscription = Observable.timer(250) // Delay when progress indicator is shown.
      .take(1)
      .subscribe(undefined, undefined, () => {
        this.stateService.startShowProgress();
      });

    return next.handle(this.addToken(req))
      .catch(error => {
        this.errorHandler.notifyError(error);
        return Observable.throw(error);
      })
      .finally(() => {
        timerSubscription.unsubscribe();
        this.stateService.stopShowProgress()
      });
  }

  addToken(req: HttpRequest<any>): HttpRequest<any> {

    const token = this.authService.jwt;

    if (this.authService.isTokenExpired()) {
      // todo Use refresh token to get new access token
    }


    if (token) {
      return req.clone({ setHeaders: {Authorization: `Bearer ${token}`}});
    } else {
      return req;
    }
  }
}
