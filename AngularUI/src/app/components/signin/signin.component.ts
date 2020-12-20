import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SignupServiceService } from 'src/app/services/signup-service.service';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css']
})
export class SigninComponent implements OnInit {

  model: any
  signinService: SignupServiceService
  constructor(http: HttpClient, private router: Router) { 
    this.model = {}
    this.signinService = new SignupServiceService(http)
  }

  ngOnInit(): void {
  }

  onSubmit(){
    console.log(this.model)
    let signinUrl = "http://localhost:3000/signin"
    this.signinService.postData(signinUrl, this.model).subscribe((data: any)=>{
      console.log(data)
      if (data.msg === "Logged in" || data.msg == "Already Logged in"){
        this.router.navigateByUrl('description')
      }
    })
  }
}
