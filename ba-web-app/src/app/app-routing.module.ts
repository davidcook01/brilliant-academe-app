import { NgModule, Component } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { CreateCourseInfoComponent } from './instructor/create-course-info/create-course-info.component';
import { EnrolledCoursesComponent } from './student/enrolled-courses/enrolled-courses.component';
import { VideoComponent } from './student/video/video.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'courseinfo',
    component: CreateCourseInfoComponent
  },
  {
    path: 'api/courses',
    component: EnrolledCoursesComponent
  },
  {
    path: 'api/courses/video/:courseId',
    component: VideoComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
//export const routingComponents = [AppComponent]
