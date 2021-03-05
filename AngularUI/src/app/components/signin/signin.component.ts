import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SocialAuthService, GoogleLoginProvider } from "angularx-social-login";
import { SignupServiceService } from 'src/app/services/signup-service.service';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css']
})
export class SigninComponent implements OnInit {

  model: any
  flags = {
    fieldTextType: false
  }

  signinService: SignupServiceService
  constructor(http: HttpClient, private router: Router, private authService: SocialAuthService) {
    this.model = {
      email: "",
      password: ""
    }
    this.signinService = new SignupServiceService(http)
  }

  ngOnInit(): void {
    let signinUrl = "http://localhost:3000/signin"
    let signupUrl = "http://localhost:3000/signup"
    let signinWithGoogleUrl = "http://localhost:3000/signinWithGoogle"

    this.signinService.postData(signinUrl, this.model).subscribe((data: any) => {
      console.log(data)
      if (data.text == "Already Logged in") {
        this.router.navigateByUrl('dashboard')
      }
    })
    this.authService.authState.subscribe((user) => {
      console.log(user)
      let dataToSend = {
        email: user.email,
        fullName: user.name,
        provider: 'Google'
      }

      this.signinService.postData(signupUrl, dataToSend).subscribe((response) => {
        console.log("Sign up Call in Google: ",response)
        if (response.text === "Username already exists") {
          this.signinService.postData(signinWithGoogleUrl, dataToSend).subscribe((response) => {
            console.log("Sign in with Google Response: ",response)
            if (response.text === "Logged in") {
              this.router.navigate(['dashboard'], {replaceUrl: true})
              //this.router.navigateByUrl('dashboard')
            }
          })
        } else if (response.text === "OK") {
          this.router.navigate(['dashboard'], {replaceUrl: true})
        }
      })
    })
  }

  signInWithGoogle(): void {
    this.authService.signIn(GoogleLoginProvider.PROVIDER_ID);
  }

  onSubmit() {
    console.log(this.model)
    let signinUrl = "http://localhost:3000/signin"
    this.signinService.postData(signinUrl, this.model).subscribe((data: any) => {
      console.log(data)
      if (data.text === "Logged in") {
        this.router.navigate(['dashboard'], {replaceUrl: true})
      }
    })
  }

  toggleFieldTextType() {
    this.flags.fieldTextType = !this.flags.fieldTextType
  }
}
