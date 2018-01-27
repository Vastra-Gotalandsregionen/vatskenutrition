import { Injectable } from '@angular/core';
import {TokenResponse} from "../../model/token-response";
import {HttpClient} from "@angular/common/http";
import {AuthStateService} from "../auth/auth-state.service";
import {Observable} from "rxjs/Observable";

@Injectable()
export class LoginService {

  constructor(private http: HttpClient, private authStateService: AuthStateService) { }

  login(username: string, password: string): Observable<boolean> {
    return this.http.post<TokenResponse>('/api/login', {username: username, password: password})
      .map(tokenResponse => {
        let accessToken = tokenResponse.accessToken;
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', tokenResponse.refreshToken);

        this.authStateService.jwt = accessToken;

        return true;
      });
  }
}
