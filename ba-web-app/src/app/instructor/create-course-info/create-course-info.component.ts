import { Component, OnInit } from '@angular/core';
import {FormGroup,Validators,FormControl} from '@angular/forms';
import {InstructorService} from '../../services/instructor.service';

@Component({
  selector: 'app-create-course-info',
  templateUrl: './create-course-info.component.html',
  styleUrls: ['./create-course-info.component.css']
})
export class CreateCourseInfoComponent implements OnInit {

  courseInfoForm : FormGroup; //courseInfoForm
  levels=['Beginner','Intermediate','Expert','All']
  categories = []


  initForm() {
    this.courseInfoForm = new FormGroup({
      title: new FormControl('', Validators.required),
      description: new FormControl('', Validators.required),
      
      level: new FormControl('', Validators.required),
      category: new FormControl('', Validators.required)
    });
  }
 
  private  categoryList:  Array<object> = [];

  constructor(private instructorservice:InstructorService) { }

  ngOnInit() {
    this.initForm();
    this.getCategoryList();
  }

  public  getCategoryList(){
    this.instructorservice.getCategoryList().subscribe((data:  Array<object>) => {
        this.categories  =  data;
        console.log(data);
    });
}



  onSubmit(){
    const title = this.courseInfoForm.value.title;
    const description = this.courseInfoForm.value.description;
    const level = this.courseInfoForm.value.level;
    const category = this.courseInfoForm.value.category;
    console.log(title,level);
    console.log(this.courseInfoForm.value)
  }

}
