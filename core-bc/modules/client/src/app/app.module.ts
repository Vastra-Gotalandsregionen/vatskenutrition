import { BrowserModule } from '@angular/platform-browser';
import {Injector, NgModule} from '@angular/core';

import { AppRoutingModule } from './app-routing.module';

import { ServiceWorkerModule } from '@angular/service-worker';
import { AppComponent } from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { HeaderComponent } from './component/header/header.component';
import { ContentComponent } from './component/content/content.component';
import { AdminComponent } from './component/admin/admin.component';
import {FormsModule} from "@angular/forms";
import {YearService} from "./service/year.service";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { BackLinkComponent } from './component/back-link/back-link.component';

import { environment } from '../environments/environment';
import { LoginComponent } from './component/login/login.component';

// import 'rxjs/add/operator/timeout';
import 'rxjs/add/operator/combineLatest';
// import 'rxjs/add/operator/mergeMap';
// import 'rxjs/add/operator/switchMap';
// import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/filter';
// import 'rxjs/add/operator/startWith';
// import 'rxjs/add/operator/concatMap';
// import 'rxjs/add/operator/take';
// import 'rxjs/add/operator/skip';
// import 'rxjs/add/operator/toArray';
// import 'rxjs/add/operator/debounceTime';
// import 'rxjs/add/operator/delay';
// import 'rxjs/add/operator/distinct';
import 'rxjs/add/operator/finally';
// import 'rxjs/add/operator/share';
import 'rxjs/add/operator/do';
// import 'rxjs/add/observable/forkJoin';
// import 'rxjs/add/observable/of';
// import 'rxjs/add/observable/from';
import 'rxjs/add/observable/timer';
import {JwtHttpInterceptor} from "./interceptor/jwt-http-interceptor";
import {StateService} from "./service/state/state.service";
import {ErrorHandler} from "./service/errorHandler/error-handler";
import {AuthStateService} from "./service/auth/auth-state.service";
import {JwtHelper} from "angular2-jwt";
import {ErrorDialogComponent} from "./component/error-dialog/error-dialog.component";
import {LoginService} from "./service/login/login.service";
import {AuthGuard} from "./guard/auth.guard";
import { LoggedInHeaderComponent } from './component/logged-in-header/logged-in-header.component';
import {Router} from "@angular/router";
import 'rxjs/add/operator/retry';
// import 'rxjs/add/operator/retryWhen';
// import 'rxjs/add/observable/interval';
import 'rxjs/add/observable/empty';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    ContentComponent,
    AdminComponent,
    BackLinkComponent,
    LoginComponent,
    ErrorDialogComponent,
    LoggedInHeaderComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ServiceWorkerModule.register('/ngsw-worker.js', { enabled: environment.production }),
    HttpClientModule,
    FormsModule,
    BrowserAnimationsModule,
    // AppMaterialModule
  ],
  providers: [
    AuthStateService,
    ErrorHandler,
    JwtHelper,
    LoginService,
    StateService,
    YearService,
    {
      provide: HTTP_INTERCEPTORS,
      useFactory: JwtHttpInterceptorFactory,
      deps: [AuthStateService, ErrorHandler, StateService, Injector, Router],
      multi: true,
    },
    AuthGuard
  ],
  entryComponents: [
    ErrorDialogComponent,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

export function JwtHttpInterceptorFactory(
                               authService: AuthStateService,
                               errorHandler: ErrorHandler,
                               stateService: StateService,
                               injector: Injector,
                               router: Router) {
  return new JwtHttpInterceptor(authService, errorHandler, stateService, injector, router);
}
