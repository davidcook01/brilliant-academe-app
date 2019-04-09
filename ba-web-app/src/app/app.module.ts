import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule,ReactiveFormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { AuthService } from './services/auth-service';
import { LoggerService } from './services/logger.service';
import { TrainingService} from './services/training.service';
import { AuthGuardService} from './services/auth-guard.service';
import {HttpClientModule} from '@angular/common/http';
import { CreateCourseInfoComponent } from './instructor/create-course-info/create-course-info.component';
import { EnrolledCoursesComponent } from './student/enrolled-courses/enrolled-courses.component';
import { HomeComponent } from './home/home.component';
import { VideoComponent } from './student/video/video.component';
import { VgCoreModule } from 'videogular2/core';
import { VgControlsModule } from 'videogular2/controls';
import { VgBufferingModule } from 'videogular2/buffering';
import { VgOverlayPlayModule } from 'videogular2/overlay-play';
import { VgStreamingModule } from 'videogular2/streaming';
import { CookieService } from 'ngx-cookie-service';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { TokenInterceptor } from './services/token.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    CreateCourseInfoComponent,
    EnrolledCoursesComponent,
    HomeComponent,
    VideoComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    HttpClientModule,
    VgCoreModule,
    VgControlsModule,
    VgBufferingModule,
    VgOverlayPlayModule,
    VgStreamingModule
  ],
  providers: [ AuthService, AuthGuardService, LoggerService, TrainingService, CookieService, {
    provide: HTTP_INTERCEPTORS,
    useClass: TokenInterceptor,
    multi: true
  } ],
  bootstrap: [AppComponent]
})
export class AppModule { }
