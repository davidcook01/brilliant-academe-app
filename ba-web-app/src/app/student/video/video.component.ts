import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import axios from 'axios';


@Component({
  selector: 'app-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.css']
})
export class VideoComponent implements OnInit {
  courseId: String;
  public videos: Array<any>;
  public lectures: Object[];
  public currentVideoURL: String;
  public currentVideoTitle: String;

  constructor(
    private route: ActivatedRoute,
    private location: Location
  ) {
    this.videos = [];
    this.lectures = [];
    this.currentVideoURL = 'Java+Programming+1/java_installation_video.mp4';
    this.currentVideoTitle = 'Java Installation';
  }

  public async getVideos() {
    const proxyurl = 'https://cors-anywhere.herokuapp.com/';
    const url = 'https://z2b3utn8c2.execute-api.us-east-1.amazonaws.com/ba-api/course/';
    try {
      return await axios.get(proxyurl + url + 'efe3fe7b-cc80-400b-9080-b03e8b43e7b6' + '/lecture');
    } catch (error) {
      console.error(error);
    }
  }

  public async showVideos() {
    const videos = await this.getVideos();
    if (videos) {
      this.videos.push(videos.data.courseSection);
      this.videos.forEach(course => {
        course.forEach(lecture => {
          lecture.lectures.forEach(lectureObj => {
            this.lectures.push(lectureObj);
          });
        });
      });
    }
    console.log(this.lectures);
    console.log(this.videos[0]);
  }

  ngOnInit() {
    this.getCourseId();
    this.showVideos();

  }

  public async chooseCourseVideo(selectedLink: String, selectedTitle: String) {
    this.currentVideoURL = selectedLink;
    this.currentVideoTitle = selectedTitle;
    console.log('change to component');
  }

  getCourseId(): void {
    const id = this.route.snapshot.paramMap.get('courseId');
    this.courseId = id;
  }
}
