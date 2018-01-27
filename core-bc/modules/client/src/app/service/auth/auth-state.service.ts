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

    if (this.isTokenExpired()) {
      this.resetAuth();
    }

  }

  isTokenExpired() {
    const token = this.getToken();
    return token && (token.exp - new Date().getTime() / 1000 < 0);
  }

  getToken(): any {
    const jwtTokenString = this.jwt;
    return jwtTokenString ? this.jwtHelper.decodeToken(jwtTokenString) : null;
  }

  isAuthenticated(): boolean {
    return this.getToken() && !this.isTokenExpired();
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

  resetAuth() {
    this.jwt = null;
  }

  getLoggedInUserId(): string {
    const token = this.getToken();
    return token ? token.sub : null;
  }

  getLoggedInDisplayName(): string {
    const token = this.getToken();
    return token ? token.displayName : null;
  }

  /*isAdmin() {
    const token = this.getToken();
    if (token) {
      const roles = <string[]>token.roles;
      return roles.indexOf('ADMIN') > -1;
    }

    return false;
  }*/

}
