import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule,ReactiveFormsModule } from '@angular/forms'; 
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { AuthService } from './services/auth-service';
import { LoggerService } from './services/logger.service';
import { TrainingService} from './services/training.service'
import { AuthGuardService} from './services/auth-guard.service'
import {HttpClientModule} from '@angular/common/http';
import { CreateCourseInfoComponent } from './instructor/create-course-info/create-course-info.component';
import { EnrolledCoursesComponent } from './student/enrolled-courses/enrolled-courses.component';
import { HomeComponent } from './home/home.component';
import { PlaylistComponent } from './student/playlist/playlist.component';
import { VideoComponent } from './student/video/video.component'

@NgModule({
  declarations: [
    AppComponent,
    CreateCourseInfoComponent,
    EnrolledCoursesComponent,
    HomeComponent,
    PlaylistComponent,
    VideoComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [ AuthService, AuthGuardService, LoggerService, TrainingService ],
  bootstrap: [AppComponent]
})
export class AppModule { }
