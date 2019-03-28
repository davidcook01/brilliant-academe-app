import { Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl} from '@angular/forms';
import { Router } from '@angular/router';
import { LoggerService } from './services/logger.service';
import {AuthService} from './services/auth-service';
import { HttpClient } from '@angular/common/http';
import * as $ from 'jquery';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit {
  loginForm: FormGroup;
  signUpForm: FormGroup;
  confirmCodeForm: FormGroup;
  resetpassword : FormGroup;
  confirmCodeForm2: FormGroup;
  errorMsgLogin: string;
  errorMsgSignup : string ;
  errorMsgConfirmSignUp: string;
  errorMsg : string ;
  userId : string;


  constructor(
    private fb: FormBuilder,
    private router: Router,
    private auth: AuthService,
    private logger: LoggerService,
    private http: HttpClient
  ) { }

  ngOnInit() {
    this.initForm();
    /* University Logo repeat at the bottom of the page */
    $('.carousel .carousel-item').each(function(){
      console.log('Cloning the carousel item');
      var next = $(this).next();
      if (!next.length) {
      next = $(this).siblings(':first');
      }
      next.children(':first-child').clone().appendTo($(this));
      
      for (var i=0;i<5;i++) {
          next=next.next();
          if (!next.length) {
            next = $(this).siblings(':first');
          }
          
          next.children(':first-child').clone().appendTo($(this));
        }
  });
  }

   ForgetPassword(){
  
    eval("$('#signin').modal('hide')") ;
      var person = prompt("Please enter your emailID");
      this.auth.forgotPassword(person)
      .subscribe(
              result => {
                 
                  console.log("succesfully done")
                  eval("$('#resetpassword').modal('show')") ;          
                  this.router.navigate(['/']);
                },
                error => {
                    this.errorMsg = error.message;
                  
                });    
    
       
   }
   initForm() {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required)
    });
      
    this.signUpForm =  new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required)
    });
      
    this.confirmCodeForm = new FormGroup({
      code: new FormControl('', [Validators.required])
    });

    this.confirmCodeForm2 = new FormGroup({
      code: new FormControl('', [Validators.required])
    });

   

    this.resetpassword = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
      code: new FormControl('', [Validators.required])
    });
  }
 
      
  onSignUp() {
      const email = this.signUpForm.value.email, password = this.signUpForm.value.password;
      this.auth.signUp(email, password).subscribe(
      result => {
          this.logger.log("SignUp retured result:" + result);
          eval("$('#signup').modal('hide')") ;
          this.errorMsg = "";
          eval("$('#confirmcode').modal('show')") ;          
          this.router.navigate(['/']);
        },
        error => {
          this.logger.log(error);
          this.errorMsgSignup = error.message;
          eval("$('#signup').modal('show')") ;
         
        });
    }
   
   
   onConfirmSignUp(){   
      
       console.log(this.signUpForm.value.email);
       console.log(this.confirmCodeForm.value.code);
       this.auth.confirmSignUp(this.signUpForm.value.email, this.confirmCodeForm.value.code)
       .subscribe(
               result => {
                   eval("$('#confirmcode').modal('hide')") ;
                   this.errorMsg = "";
                   console.log(this.errorMsg + "no error");
                 },
                 error => {
                     this.errorMsgConfirmSignUp = error.message;
                     eval("$('#confirmcode').modal('show')") ;
                     console.log(error.message);                   
                 });       
   }

   onConfirmSignUp2(){   
   
    this.auth.confirmSignUp(this.loginForm.value.email, this.confirmCodeForm2.value.code )
    .subscribe(
            result => {
                eval("$('#confirmcode2').modal('hide')") ;
                this.errorMsg = "";
                console.log(this.errorMsg + "no error");
                console.log(result);
              },
              error => {
                  this.errorMsgConfirmSignUp = error.message;
                  eval("$('#confirmcode2').modal('show')") ;
                  console.log(error.message);                   
              });       
}
   
  
    



   onsubmitforgotPassword(){
    
    console.log("serive forgot password is called")
    console.log( "code is:  " + this.resetpassword.value.code)
    this.auth.onsubmitforgotPassword(this.resetpassword.value.email,this.resetpassword.value.code, this.resetpassword.value.password )
    .subscribe(
            result => {
                
                eval("$('#resetpassword').modal('hide')") ;
                this.errorMsg = "";
              },
              error => {
                  this.errorMsg = error.message;
               
                
              });    
   }
      
  onSubmitLogin() {
    const email = this.loginForm.value.email, password = this.loginForm.value.password;
   
 
     this.auth.signIn(email, password)
      .subscribe(
        result => {
          this.userId = result.username ;
          console.log("App component :  "+this.userId);
          eval("$('#signin').modal('hide')") ;
          eval("$('#login_button').modal('hide')") ;
          alert("signIn success...");
          this.router.navigate(['/myhome']);
        },
        error => {
          this.logger.log(error);
          console.log(error);
          if(error.name === "UserNotConfirmedException"){
            eval("$('#signin').modal('hide')") ;
            this.auth.resendSignUp(email).subscribe(
                  result => {
                    eval("$('#login_button').modal('hide')") ;
                  },
                  error =>{
                    this.errorMsgConfirmSignUp = error.message;
                  }

            );
            eval("$('#confirmcode2').modal('show')") ; 
            this.errorMsgConfirmSignUp = "CODE HAS BEEN SEND TO YOUR EMAIL ID"
            

          }
          this.errorMsgLogin = error.message;
         
        });
  }

  google(){
    console.log("google");
    let url = "https://brilliant-academe.auth.us-east-1.amazoncognito.com/login?response_type=code&client_id=6her35acoifrnmia95q8sjvovl&redirect_uri=https://www.amazon.com";
    let result = this.http.get(url);
    window.location.replace(url);
    console.log(result);
    //  const ga;
    //  ga.signIn().then(googleUser => {
    // const { id_token, expires_at } = googleUser.getAuthResponse();
    // const profile = googleUser.getBasicProfile();
    // const user = {
    //      email: profile.getEmail(),
    //      name: profile.getName()
    //  };

  //   return this.auth.federatedSignIn(
  //     // Initiate federated sign-in with Google identity provider 
  //      'google',
  //       { 
  //           // the JWT token
  //            token: id_token, 
  //            // the expiration time
  //          expires_at 
  //        },
  //       // a user object
  //        user
  //    ).then(() => {
  //        // ...
  //    });
  //  });
  }
}