import { NgModule, Component } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import {CreateCourseInfoComponent} from './instructor/create-course-info/create-course-info.component';
import { EnrolledCoursesComponent } from './student/enrolled-courses/enrolled-courses.component';

const routes: Routes = [
 
  {
    path: 'courseinfo', 
    component:CreateCourseInfoComponent
  },
  { 
    path: 'courses', 
    component: EnrolledCoursesComponent 
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
//export const routingComponents = [AppComponent]
