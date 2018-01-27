import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule } from "@angular/material/dialog";
import {MatTooltipModule} from "@angular/material/tooltip";

@NgModule({
  imports: [
    CommonModule,
    MatDialogModule,
    MatTooltipModule
  ],
  declarations: [
  ],
  exports: [
    MatDialogModule,
    MatTooltipModule
  ]
})
export class AppMaterialModule { }
