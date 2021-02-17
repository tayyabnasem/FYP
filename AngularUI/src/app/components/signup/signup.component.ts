import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SocialAuthService, GoogleLoginProvider } from 'angularx-social-login';
import { SignupServiceService } from 'src/app/services/signup-service.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
  exportAs: 'signUpForm'
})
export class SignupComponent implements OnInit {

  signUpService: SignupServiceService
  model: any = {
    fullName: '',
    userName: '',
    email: '',
    password: '',
    provider: 'DLS'
  };
  flags: any = {
    passStrength: 'Short',
    fieldTextType: false,
    userExists: false
  }

  constructor(http: HttpClient, private router: Router, private authService: SocialAuthService) {
    this.signUpService = new SignupServiceService(http);
  }

  ngOnInit(): void {
    let signupUrl = "http://localhost:3000/signup"
    let signinWithGoogleUrl = "http://localhost:3000/signinWithGoogle"

    this.authService.authState.subscribe((user) => {
      console.log(user)
      let dataToSend = {
        email: user.email,
        fullName: user.name,
        provider: 'Google'
      }

      this.signUpService.postData(signupUrl, dataToSend).subscribe((response) => {
        console.log(response)
        if (response.text === "Username already exists") {
          this, this.signUpService.postData(signinWithGoogleUrl, dataToSend).subscribe((response) => {
            console.log(response)
            if (response.text === "Logged in") {
              this.router.navigate(["description"], {replaceUrl:true, skipLocationChange: true});
            }
          })
        } else if (response.text === "OK") {
          this.router.navigate(["description"], {replaceUrl:true, skipLocationChange: true});
          //this.router.navigateByUrl('description', { skipLocationChange: true })
        }
      })
    })
  }

  onSubmit() {
    let signupUrl = "http://localhost:3000/signup"
    this.signUpService.postData(signupUrl, this.model).subscribe((data) => {
      console.log(data)
      if (data.text === "Username already exists") {
        this.flags.userExists = true
      } else if (data.text === "OK") {
        this.router.navigateByUrl('signin')
      }
    });
  }

  signInWithGoogle(): void {
    this.authService.signIn(GoogleLoginProvider.PROVIDER_ID);
  }

  toggleFieldTextType() {
    this.flags.fieldTextType = !this.flags.fieldTextType
    console.log(this.flags.fieldTextType)
  }

  public isPasswordCommon(password: string): boolean {
    const commonPasswordPatterns = /passw.*|12345.*|09876.*|qwert.*|asdfg.*|zxcvb.*|footb.*|baseb.*|drago.*/;
    return commonPasswordPatterns.test(password);
  }

  public checkPasswordStrength(password: string) {
    console.log(this.model.password)

    // Build up the strenth of our password
    let numberOfElements = 0;
    numberOfElements = /.*[a-z].*/.test(password) ? ++numberOfElements : numberOfElements;      // Lowercase letters
    numberOfElements = /.*[A-Z].*/.test(password) ? ++numberOfElements : numberOfElements;      // Uppercase letters
    numberOfElements = /.*[0-9].*/.test(password) ? ++numberOfElements : numberOfElements;      // Numbers
    numberOfElements = /[^a-zA-Z0-9]/.test(password) ? ++numberOfElements : numberOfElements;   // Special characters (inc. space)

    // Assume we have a poor password already
    let currentPasswordStrength = 'Short';

    // Check then strenth of this password using some simple rules
    if (password === null || password.length < 5) {
      currentPasswordStrength = 'Short';
    } else if (this.isPasswordCommon(password) === true) {
      currentPasswordStrength = 'Common';
    } else if (numberOfElements === 0 || numberOfElements === 1 || numberOfElements === 2) {
      currentPasswordStrength = 'Weak';
    } else if (numberOfElements === 3) {
      currentPasswordStrength = 'Ok';
    } else {
      currentPasswordStrength = 'Strong';
    }

    // Return the strength of this password
    this.flags.passStrength = currentPasswordStrength;
  }

}
