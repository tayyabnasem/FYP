import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { SignupServiceService } from 'src/app/services/signup-service.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
  exportAs: 'signUpForm'
})
export class SignupComponent implements OnInit {

  signUpService: SignupServiceService
  model:any = {
    fullName: '', 
    userName: '',
    email: '',
    password: '',
    confPassword: ''
  };
  
  constructor(http: HttpClient) {
    this.signUpService = new SignupServiceService(http);
  }

  ngOnInit(): void {
  }

  onSubmit() {
    console.log(this.model)
    let signupUrl = "http://localhost:3000/signup"
    this.signUpService.postData(signupUrl, this.model).subscribe((data) => {
      console.log(data)
    });
  }

}
