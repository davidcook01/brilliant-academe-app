import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TrainingService {
  private addUrl = "https://y14e0f4sfj.execute-api.us-east-1.amazonaws.com/dev/video-metadata";

  constructor(private http: HttpClient) { }

  addTraining(
    title: string, 
    description: string, 
    shortDescription: string, 
    starRating: number)  {
      
      var data = {"title": title, "description": description, "shortDescription": shortDescription, "starRating": starRating}
      // const httpOptions = {
      //   headers: new HttpHeaders({
      //     'Content-Type':  'application/json'
      //   })
      // };

      return this.http.post(this.addUrl, data, {})
        .subscribe(results => {
          alert('success: ' + results);
          console.log("result:", results);
        });
        // .pipe(
        //   catchError(this.handleError)
        // );
  }

  postFile(fileToUpload: File) {
    const endpoint = 'https://nbwjt3qnk6.execute-api.us-east-1.amazonaws.com/dev';
    const formData: FormData = new FormData();
    formData.append('fileKey', fileToUpload, fileToUpload.name);
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'multipart/form-data',
        'Access-Control-Allow-Origin':'*'
      })
    };
    return this.http.post(endpoint, formData, httpOptions)
      .subscribe(results => {
        alert('success: ' + results);
        console.log("result:", results);
        //return Observable(true);
      });
      //.map(() => { return true; })
      //.catch((e) => this.handleError(e));
  }

  private handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);
    }
    // return an ErrorObservable with a user-facing error message
    return throwError(
      'Something bad happened; please try again later.');
  };
}
