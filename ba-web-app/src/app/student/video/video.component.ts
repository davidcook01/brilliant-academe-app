import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import axios from 'axios';

interface Video {
  courseSection: [];
}

@Component({
  selector: 'app-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.css']
})
export class VideoComponent implements OnInit {
  courseId: String;
  public videos: Video[];
  public lectures: String[];
  public currentVideoURL: String ;

  constructor(
    private route: ActivatedRoute,
    private location: Location
  ) {
    this.videos = [];
    this.lectures = [];
  }

  public async getVideos() {
    const proxyurl = 'https://cors-anywhere.herokuapp.com/';
    const url = 'https://lzeowpbkpf.execute-api.us-east-1.amazonaws.com/ba-api/course/';
    try {
      return await axios.get(proxyurl + url + this.courseId + '/lectures');
    } catch (error) {
      console.error(error)
    }
  }

  public pushLink(lectureLink) {
    console.log(lectureLink);
    this.lectures.push(lectureLink);

  }
  public async showVideos() {
    const videos = await this.getVideos();
    if (videos) {
      this.videos.push(videos.data.courseSection);
    }
    console.log(this.videos);
  }

  ngOnInit() {
    this.getCourseId();
    this.showVideos();

  }

  getCourseId(): void {
    const id = this.route.snapshot.paramMap.get('courseId');
    this.courseId = id;
  }

}
