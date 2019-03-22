import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.css']
})
export class VideoComponent implements OnInit {

  courseId: String;
  
  constructor(
    private route: ActivatedRoute,
    private location: Location
  ) {}

  ngOnInit() {
    this.getCourseId();
  }

  getCourseId():void{
    const id = this.route.snapshot.paramMap.get('courseId');
    this.courseId = id ;
    console.log("video component course ID fetched here: " + this.courseId);

  }



  
  
}
