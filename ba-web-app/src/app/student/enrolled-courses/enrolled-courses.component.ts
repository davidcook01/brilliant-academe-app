import { Component, OnInit } from '@angular/core';
import axios from 'axios';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CourseService } from '../../course.service';
import { ReturnStatement } from '@angular/compiler';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';


export interface Course {
  courseDescription: string;
  courseId: string;
  courseName: string;
  coverImage: string;
  instructorId: string;
  instructorName: string;
  percentageCompleted: number;
}

export interface CourseArr{
  courses: Course[];
}

@Component({
  selector: 'app-enrolled-courses',
  templateUrl: './enrolled-courses.component.html',
  styleUrls: ['./enrolled-courses.component.css']
})
export class EnrolledCoursesComponent implements OnInit {

  public courses: Course[];
  public userID:String;

  constructor( private route: ActivatedRoute,
    private location: Location, private router: Router , private courseService:CourseService, private http: HttpClient) {
    this.courses = [];
  }

  public loadCourseVideos(courseId: String) {
    console.log(courseId);
    const url = '/video/'+courseId;
    this.router.navigateByUrl(url);
    console.log(this.courses[0]);
  }

  // public async getCourses() {
  //   try {
  //     return await axios.get('https://cors-anywhere.herokuapp.com/https://dxumyaeyh4.execute-api.us-east-1.amazonaws.com/ba-api/user/afff130b-0e99-4234-9b2d-db85224c1280/enrollment')
  //   } catch (error) {
  //     console.error(error)
  //   }
  // }
  // public async showCourses() {
  //   const courses = await this.getCourses();
  //   if (courses) {
  //     this.courses.push(courses.data.courses); 
  //     // console.log(this.courses[0][0].coverImage);
  //   }
  // }

 

  ngOnInit() {
     this.getCourses2();
  }

  getCourses2(): void {
    const id = this.route.snapshot.paramMap.get('userId');
    this.userID = id ;
    console.log("Enrolled courses" + this.userID);
   this.courseService.getCourses(this.userID).subscribe( res=>{this.courses= res.courses, console.log("is has printed"+ this.courses)} );
    }
   

}
