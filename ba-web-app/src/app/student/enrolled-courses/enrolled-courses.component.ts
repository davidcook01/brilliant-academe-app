import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

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

  constructor(private router: Router, public http: HttpClient) {
    this.courses = [];
  }

  public async loadCourseVideos(courseId: String) {
    console.log(courseId);
    const url = '/api/courses/video/' + courseId;
    this.router.navigateByUrl(url);
  }

  public async getCourses() {
    try {
      await this.http.get('https://cors-anywhere.herokuapp.com/https://hvgo3a7lrc.execute-api.us-east-1.amazonaws.com/ba-api/enrollment')
      .subscribe(
        data => this.courses.push(data.courses),
        err => console.log(err)
      );
      console.log(this.courses);
    } catch (error) {
      console.error(error)
    }
  }

  ngOnInit() {
    this.getCourses();
  }
}
