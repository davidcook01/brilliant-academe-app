import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject, from, of} from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import Amplify, { Auth } from 'aws-amplify';
import { environment } from '../../environments/environment';
import { CookieService } from 'ngx-cookie-service';

@Injectable()
export class AuthService {

  public loggedIn: BehaviorSubject<boolean>;

  constructor(
    private router: Router,
    private cookieService: CookieService
  ) {
    Amplify.configure(environment.amplify);
    this.loggedIn = new BehaviorSubject<boolean>(false);
  }

  public forgotPassword(email): Observable<any>{
    console.log('Forgot password service called');
    console.log(email);
    return from(Auth.forgotPassword(email));
  }

  public onsubmitforgotPassword(email, code, newPassword): Observable<any> {
    console.log('Password saved service call');
    console.log(email);
    return from(Auth.forgotPasswordSubmit(email, code, newPassword)
      .then(data => console.log(data))
        .catch(err => console.log(err))
    );
  }

  public signUp(email, password): Observable<any> {
    return from(Auth.signUp(email, password));
  }

  public confirmSignUp(email, code): Observable<any> {
    console.log('Auth service new user sign up:' + email + ' ' +  code);
    return from(Auth.confirmSignUp(email, code, {
      forceAliasCreation: true
    })
      .then(data => console.log(data))
        .catch(err => console.log(err))
    );
  }

  public resendSignUp(email): Observable<any> {
    return from(Auth.resendSignUp(email));
  }

  public signIn(email, password): Observable<any> {
    return from(Auth.signIn(email, password))
    .pipe(
      tap(() => {
        this.loggedIn.next(true);
        return true;
      })
    );
  }

  public isAuthenticated(): Observable<boolean> {
    return from(Auth.currentAuthenticatedUser())
      .pipe(
        map(() => {
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

  public signOut() {
    from(Auth.signOut())
      .subscribe(
        result => {
          this.loggedIn.next(false);
          this.router.navigate(['/']);
        },
        error => console.log(error)
      );
  }

}
