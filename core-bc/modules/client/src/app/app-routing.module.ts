import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ContentComponent} from "./component/content/content.component";
import {AdminComponent} from "./component/admin/admin.component";
import {AuthGuard} from "./guard/auth.guard";
import {LoginComponent} from "./component/login/login.component";

const routes: Routes = [
  {
    path: '',
    component: ContentComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'login',
    component: LoginComponent
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
