import { Component, OnInit } from '@angular/core';
import axios from 'axios';

@Component({
  selector: 'app-playlist',
  templateUrl: './playlist.component.html',
  styleUrls: ['./playlist.component.css'],
  // template: `
  //   <div class="col-lg-3 col-md-4 col-sm-6 col-6">
  //     <div class="card play-list-crd"> 
  //       <a href="video.html">
  //         <img class=" card-img-top  img-fluid" src="assets/img/play.png" alt="Card image">
  //       </a>
  //       <div class="card-body text-left">
  //         <h4 class="enrolled-card">
  //           <a href="video.html">Introduction to Java</a>
  //         </h4>
  //         <div class="progress" style="height:15px">
  //           <div class="progress-bar" style="width:40%;height:15px">35%</div>
  //             <a href="#"><i class="fa fa-close font-close"></i></a>
  //           </div>
  //         </div>
  //       </div>	
  //     </div>
  //   </div>
  //   `
  })

export class PlaylistComponent implements OnInit {

  constructor() { }

  ngOnInit() {
    // https://www.bennadel.com/blog/3444-proof-of-concept-using-axios-as-your-http-client-in-angular-6-0-0.htm
    const getCourses = async () => {
      try {
          return await axios.get('https://cors-anywhere.herokuapp.com/https://dxumyaeyh4.execute-api.us-east-1.amazonaws.com/ba-api/user/afff130b-0e99-4234-9b2d-db85224c1280/enrollment')
        //  https://ir3v0f4teh.execute-api.us-east-1.amazonaws.com/ba-api/course/53d93c0a-4656-4e43-bd72-852d04a5e1fe
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
