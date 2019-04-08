import { Component, OnInit } from '@angular/core';
import axios from 'axios';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';

interface Course {
  courseDescription: string;
  courseId: string;
  courseName: string;
  coverImage: string;
  instructorId: string;
  instructorName: string;
  percentageCompleted: number;
}

@Component({
  selector: 'app-enrolled-courses',
  templateUrl: './enrolled-courses.component.html',
  styleUrls: ['./enrolled-courses.component.css']
})
export class EnrolledCoursesComponent implements OnInit {

  public courses: Course[];

  constructor(private router: Router, private http: HttpClient) {
    this.courses = [];
  }

  public async loadCourseVideos(courseId: String) {
    console.log(courseId);
    const url = '/video/' + courseId;
    this.router.navigateByUrl(url);
  }

  public async getCourses() {
    try {
      return await axios.get(
        'https://cors-anywhere.herokuapp.com/https://hvgo3a7lrc.execute-api.us-east-1.amazonaws.com/ba-api/enrollment'
        // {
        //   headers:  { 'Authorization': `Bearer <my token>` }
        // }
      );
    } catch (error) {
      console.error(error)
    }
  }
  public async showCourses() {
  //   let header = new HttpHeaders();
  // header = header.append('Access-Control-Expose-Headers', 'Accept, application/x-www-form-urlencoded');
  // console.log(header.get('Accept'));
    const courses = await this.getCourses();
    if (courses) {
      this.courses.push(courses.data.courses);
      console.log(this.courses);

    }
  }

  ngOnInit() {
    this.showCourses();
  //   this.http.get<any>('/courses', {observe: 'response'})
  // .subscribe(resp => {
  //   console.log(resp.headers.get('X-Token'));
  // });
  // this.http.get('/courses', {observe: 'response'})
  //   .subscribe(resp => console.log(resp.headers))

    // const myHeaders = new Headers();
    // const acceptHeader =  myHeaders.get('Cache-Control');
    // console.log(acceptHeader);
  }



}
