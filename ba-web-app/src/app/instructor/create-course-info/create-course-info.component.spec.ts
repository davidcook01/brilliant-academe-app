import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateCourseInfoComponent } from './create-course-info.component';

describe('CreateCourseInfoComponent', () => {
  let component: CreateCourseInfoComponent;
  let fixture: ComponentFixture<CreateCourseInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateCourseInfoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateCourseInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
