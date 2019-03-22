import { Component, OnInit } from '@angular/core';
import axios from 'axios';
import { Router } from '@angular/router';

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

  constructor(private router: Router) {
    this.courses = [];
  }

  public async loadCourseVideos() {
    this.router.navigateByUrl('/video');
    console.log(this.courses[0]);
  }

  public async getCourses() {
    try {
      return await axios.get('https://cors-anywhere.herokuapp.com/https://dxumyaeyh4.execute-api.us-east-1.amazonaws.com/ba-api/user/afff130b-0e99-4234-9b2d-db85224c1280/enrollment')
    } catch (error) {
      console.error(error)
    }
  }
  public async showCourses() {
    const courses = await this.getCourses();
    if (courses) {
      this.courses.push(courses.data.courses); 
      // console.log(this.courses[0][0].coverImage);
    }
  }

  ngOnInit() {
    this.showCourses();
  }

}
