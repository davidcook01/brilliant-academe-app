import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Course } from './student/enrolled-courses/enrolled-courses.component';
import { CourseArr } from './student/enrolled-courses/enrolled-courses.component';

@Injectable({
  providedIn: 'root'
})
export class CourseService {

  courseId: string ; 

  constructor(private http: HttpClient) { }

  getCourses(userId:String): Observable<CourseArr> {
    return this.http.get<CourseArr>("https://cors-anywhere.herokuapp.com/https://dxumyaeyh4.execute-api.us-east-1.amazonaws.com/ba-api/user/"+userId+"/enrollment");
  }
  

};
