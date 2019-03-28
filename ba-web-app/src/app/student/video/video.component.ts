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

  constructor(
    private route: ActivatedRoute,
    private location: Location
  ) {
    this.videos = [];
  }

  public async getVideos() {
    const proxyurl = 'https://cors-anywhere.herokuapp.com/';
    const url = 'https://ir3v0f4teh.execute-api.us-east-1.amazonaws.com/ba-api/course/';
    try {
      return await axios.get(proxyurl + url + this.courseId)
    } catch (error) {
      console.error(error)
    }
  }
  public async showVideos() {
    const videos = await this.getVideos();
    if (videos) {
      this.videos.push(videos.data.courseSection);
    }
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
