import { NgModule, Component } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { CreateCourseInfoComponent } from './instructor/create-course-info/create-course-info.component';
import { EnrolledCoursesComponent } from './student/enrolled-courses/enrolled-courses.component';
import { PlaylistComponent } from './student/playlist/playlist.component';
import { VideoComponent } from './student/video/video.component';

const routes: Routes = [
  {
    path: 'home', 
    component: HomeComponent
  },
  {
    path: 'courseinfo', 
    component: CreateCourseInfoComponent
  },
  { 
    path: 'courses/:userId', 
    component: EnrolledCoursesComponent 
  },
  {
    path: 'playlist', 
    component: PlaylistComponent
  },
  {
    path: 'video/:courseId', 
    component: VideoComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
//export const routingComponents = [AppComponent]
