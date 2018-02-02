import {Observable} from 'rxjs/Observable';
import {Injectable, Injector} from '@angular/core';
import {AuthStateService} from '../service/auth/auth-state.service';
import {ErrorHandler} from "../service/errorHandler/error-handler";
import {StateService} from "../service/state/state.service";
import {Subscription} from "rxjs/Subscription";
import 'rxjs/add/observable/throw';
import {HttpClient, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {TokenResponse} from "../model/token-response";

@Injectable()
export class JwtHttpInterceptor implements HttpInterceptor {

  authService: AuthStateService;
  errorHandler: ErrorHandler;
  stateService: StateService;

  constructor(authService: AuthStateService,
              errorHandler: ErrorHandler,
              stateService: StateService,
              private injector: Injector) {
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

    // Don't intercept login calls
    if (req.url == '/api/login') {
      return next.handle(req);
    }

    if (this.authService.isTokenExpired() && req.url !== '/api/auth/token') {
      let http = this.injector.get(HttpClient);
      const refreshToken = this.authService.refreshToken;

      if (refreshToken && !this.authService.isRefreshTokenExpired()) {
        // Send a prior request to get new access token (and new refresh token).
        return http.get<TokenResponse>('/api/auth/token', {headers: {Authorization: `Bearer ${refreshToken}`}})
          .do(tokens => {
            this.authService.jwt = tokens.accessToken;
            this.authService.refreshToken = tokens.refreshToken;
          })
          .mergeMap(() => {
            // Continue with the first request. Now a fresh jwt should be present and will thus be added.
            return this.addTokenAndContinueRequest(next, req, timerSubscription);
          });
      } else {
        this.authService.resetAuth();
      }
    } else if (this.authService.isTokenExpired()) {
      // Just send request without access token.
      return next.handle(req)
        .finally(() => {
          timerSubscription.unsubscribe();
          this.stateService.stopShowProgress()
        });
    } else {
      // Valid access token is present and thus added to request.
      return this.addTokenAndContinueRequest(next, req, timerSubscription);
    }

  }

  addTokenAndContinueRequest(next: HttpHandler, req: HttpRequest<any>, timerSubscription): Observable<HttpEvent<any>> {
    return next.handle(this.addAccessToken(req))
      .catch(error => {
        this.errorHandler.notifyError(error);
        return Observable.throw(error);
      })
      .finally(() => {
        timerSubscription.unsubscribe();
        this.stateService.stopShowProgress()
      });
  }

  addAccessToken(req: HttpRequest<any>): HttpRequest<any> {

    const token = this.authService.jwt;

    if (token) {
      return req.clone({setHeaders: {Authorization: `Bearer ${token}`}});
    } else {
      return req;
    }
  }
}
