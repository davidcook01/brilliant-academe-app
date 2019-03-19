import { Component, OnInit } from '@angular/core';
import axios from 'axios';

@Component({
  selector: 'app-playlist',
  templateUrl: './playlist.component.html',
  styleUrls: ['./playlist.component.css']
})

export class PlaylistComponent implements OnInit {

  constructor() { }

  ngOnInit() {
    // https://www.bennadel.com/blog/3444-proof-of-concept-using-axios-as-your-http-client-in-angular-6-0-0.htm
    const getCourses = async () => {
      try {
          return await axios.get('https://cors-anywhere.herokuapp.com/https://dxumyaeyh4.execute-api.us-east-1.amazonaws.com/ba-api/user/afff130b-0e99-4234-9b2d-db85224c1280/enrollment')
      } catch (error) {
          console.error(error)
      }
    }
    
    const showCourses = async () => {
      const courses = await getCourses();
    
      if (courses) {
          console.log(courses.data.courses);
      }
    }
    
    showCourses();
  }

}
