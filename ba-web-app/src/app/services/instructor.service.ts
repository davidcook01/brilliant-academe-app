import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { throwError } from 'rxjs';
import { environment } from '../../environments/environment'; 

@Injectable({
  providedIn: 'root'
})
export class InstructorService {
  constructor(private httpClient: HttpClient) { }
  private categoryurl = environment.categoriesUrl; 

  getCategoryList()  {
         return this.httpClient.get(environment.categoriesUrl);
      }
}