import {Component, OnInit} from '@angular/core';
import {LoginService} from "../../service/login/login.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  username: string;
  password: string;

  returnUrl: string;

  constructor(private loginService: LoginService,
              private route: ActivatedRoute,
              private router: Router) { }

  ngOnInit() {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  login() {
    this.loginService.login(this.username, this.password)
      .subscribe(result => {
        // login successful so redirect to return url
        if (result) {
          this.router.navigateByUrl(this.returnUrl);
        }
      });
  }

}
