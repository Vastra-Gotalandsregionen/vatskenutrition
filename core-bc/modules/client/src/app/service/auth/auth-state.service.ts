import {Injectable} from '@angular/core';
import {JwtHelper} from 'angular2-jwt/angular2-jwt';
import {Router} from '@angular/router';
import {TokenResponse} from "../../model/token-response";

@Injectable()
export class AuthStateService {

  constructor(private jwtHelper: JwtHelper,
              private router: Router) {

    const localStorageToken = localStorage.getItem('jwtToken');

    if (localStorageToken) {
      this.jwt = localStorageToken;
    }

  }

  isTokenExpired() {
    const token = this.getToken();
    return token && (token.exp - new Date().getTime() / 1000 < 0);
  }

  isRefreshTokenExpired() {
    const token = this.getRefreshToken();
    return token && (token.exp - new Date().getTime() / 1000 < 0);
  }

  getToken(): any {
    const jwtTokenString = this.jwt;
    return jwtTokenString ? this.jwtHelper.decodeToken(jwtTokenString) : null;
  }

  getRefreshToken(): any {
    const jwtTokenString = this.refreshToken;
    return jwtTokenString ? this.jwtHelper.decodeToken(jwtTokenString) : null;
  }

  isAuthenticated(): boolean {
    return this.getRefreshToken() && !this.isRefreshTokenExpired();
  }

  get jwt(): string {
    return localStorage.getItem('accessToken');
  }

  set jwt(value: string) {

    if (value) {

      localStorage.setItem('accessToken', value);

    } else if (this.getToken()) {
      // Logout
      this.router.navigate(['/']);
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }

  }

  get refreshToken(): string {
    return localStorage.getItem('refreshToken');
  }

  set refreshToken(value: string) {
    if (value) {
      localStorage.setItem('refreshToken', value);
    } else if (this.getRefreshToken()) {
      debugger;
    }
  }

  resetAuth() {
    this.jwt = null;
    this.refreshToken = null;
  }

  getLoggedInUserId(): string {
    const token = this.getToken();
    return token ? token.sub : null;
  }

  getLoggedInDisplayName(): string {
    const token = this.getToken();
    return token ? token.context.displayName : null;
  }

}
