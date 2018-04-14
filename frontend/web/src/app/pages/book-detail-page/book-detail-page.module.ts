import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BookDetailPageComponent} from './book-detail-page.component';
import {MatTabsModule} from '@angular/material/tabs';
import {MatGridListModule} from '@angular/material/grid-list';
import {SharedModule} from '../../shared/shared.module';
import {MatDividerModule} from '@angular/material/divider';
import {MatListModule} from '@angular/material/list';
@NgModule({
  imports: [
    CommonModule,
    MatTabsModule,
    MatGridListModule,
    SharedModule,
    MatDividerModule,
    MatListModule
  ],
  declarations: [BookDetailPageComponent]
})
export class BookDetailPageModule {
}
