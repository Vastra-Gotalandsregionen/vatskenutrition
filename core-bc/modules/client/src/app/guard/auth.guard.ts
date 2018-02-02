import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import {AuthStateService} from "../service/auth/auth-state.service";

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private router: Router,
              private authStateService: AuthStateService) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (!(route.queryParams.selectedYear || route.routeConfig.path === 'admin') || this.authStateService.isAuthenticated()) {
      // logged in so return true
      return true;
    }

    // not logged in so redirect to login page with the return url and return false
    this.router.navigate(['login'], { queryParams: { returnUrl: state.url }});
    return false;
  }
}
