import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {HttpModule} from "@angular/http";
import {MatTooltipModule} from '@angular/material';
import { HeaderComponent } from './component/header/header.component';
import { ContentComponent } from './component/content/content.component';
import { AdminComponent } from './component/admin/admin.component';
import {FormsModule} from "@angular/forms";
import {YearService} from "./service/year.service";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { BackLinkComponent } from './component/back-link/back-link.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    ContentComponent,
    AdminComponent,
    BackLinkComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpModule,
    FormsModule,
    BrowserAnimationsModule,
    MatTooltipModule
  ],
  providers: [
    YearService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
