import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ContentComponent} from "./component/content/content.component";
import {AdminComponent} from "./component/admin/admin.component";

const routes: Routes = [
  {
    path: '',
    component: ContentComponent/*,
    children: [
      {
        path: ':articleUuid',
        // component: ContentComponent,
        children: []
      }
    ]*/
  },
  {
    path: 'admin',
    component: AdminComponent
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
