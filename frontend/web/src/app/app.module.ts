import {NgModule} from '@angular/core';


import {AppComponent} from './app.component';
import {RouterModule} from '@angular/router';
import {ATHENA_ROUTES} from './routes';
import {CoreModule} from './core/core.module';
import {HomePageModule} from './pages/home-page/home-page.module';
import {SharedModule} from './shared/shared.module';
import {BrowserModule} from '@angular/platform-browser';
import {APP_BASE_HREF_VALUE, BASE_URL, BASE_URL_VALUE, REST_URL, REST_URL_VALUE} from './config';
import {PublicationPageModule} from "./pages/publication-page/publication-page.module";
import {APP_BASE_HREF} from '@angular/common';
import {BookDetailPageModule} from './pages/book-detail-page/book-detail-page.module';
import {UserPageModule} from './pages/user-page/user-page.module';
import {LoggedInGuard} from './core/guard/logged-in.guard';
import {LoginPageModule} from './pages/login-page/login-page.module';


@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    RouterModule.forRoot(ATHENA_ROUTES),
    BrowserModule,
    CoreModule,
    SharedModule,
    HomePageModule,
    PublicationPageModule,
    BookDetailPageModule,
    UserPageModule,
    LoginPageModule,
  ],
  providers: [
    {
      provide: REST_URL,
      useFactory: (base_url) => {
        for (const description in REST_URL_VALUE) {
          if (REST_URL_VALUE.hasOwnProperty(description)) {
            REST_URL_VALUE[description].url = base_url + REST_URL_VALUE[description].url;
          }
        }
        return REST_URL_VALUE;
      },
      deps: [BASE_URL]
    },
    {provide: BASE_URL, useValue: BASE_URL_VALUE},
    {provide: APP_BASE_HREF, useValue: APP_BASE_HREF_VALUE},
    LoggedInGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
