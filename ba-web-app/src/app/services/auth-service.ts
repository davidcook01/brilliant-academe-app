import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject, from, of} from 'rxjs';
//import { BehaviorSubject } from 'rxjs/BehaviorSubject';
//import { fromPromise } from 'rxjs/observable/fromPromise';
import { map, tap, catchError } from 'rxjs/operators';
//import { of } from 'rxjs/observable/of';
import Amplify, { Auth } from 'aws-amplify';
import { environment } from '../../environments/environment';
import { CookieService } from 'ngx-cookie-service';
import { AuthenticationDetails, CognitoUser, CognitoUserSession } from "amazon-cognito-identity-js";
import * as AWS from 'aws-sdk';
@Injectable()
export class AuthService {

public loggedIn: BehaviorSubject<boolean>;

constructor(private router: Router, private cookieService: CookieService) {
    Amplify.configure(environment.amplify);
    this.loggedIn = new BehaviorSubject<boolean>(false);
}

public forgotPassword(email):Observable<any>{
         console.log("this service even called")
         console.log(email)
         return from(Auth.forgotPassword(email));

}


public onsubmitforgotPassword(email,code,new_password):Observable<any>{
  console.log(" on forgot password with new password is service even called")
  console.log(email)
  return from(Auth.forgotPasswordSubmit(email, code, new_password).then(data => console.log(data))
  .catch(err => console.log(err))
);

}

  /** signup */
  public signUp(email, password): Observable<any> {
    return from(Auth.signUp(email, password));
  }

  /** confirm code */
  public confirmSignUp(email, code): Observable<any> {
    console.log("auth service got called" + email + "  " +  code);
    return from(Auth.confirmSignUp(email, code, {
      // Optional. Force user confirmation irrespective of existing alias. By default set to True.
      forceAliasCreation: true
  }).then(data => console.log(data))
    .catch(err => console.log(err))
  );
  }


  public resendSignUp(email): Observable<any> {
    return from(Auth.resendSignUp(email));
  }

  /** signin */
  public signIn(email, password): Observable<any> {
    return from(Auth.signIn(email, password))
      .pipe(
        tap(() => {
          this.loggedIn.next(true);
          return true;

          })
      );
  }

  /** get authenticat state */
  public isAuthenticated(): Observable<boolean> {
    return from(Auth.currentAuthenticatedUser())
      .pipe(
        map(()=> {
          this.loggedIn.next(true);
          return true;
        }),
        catchError(error => {
          this.loggedIn.next(false);
          return of(false);
        })
      );
  }

  public getToken(): string {
    return this.cookieService.get('SESSIONID');
  }

  /** signout */
  public signOut() {
    from(Auth.signOut())
      .subscribe(
        result => {
          this.loggedIn.next(false);
          this.router.navigate(['/login']);
        },
        error => console.log(error)
      );
  }
}
