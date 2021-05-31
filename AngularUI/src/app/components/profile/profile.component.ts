import { Component, OnInit } from '@angular/core';
import { ApicallService } from 'src/app/services/apicall.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  projects: any = []
  full_name: any
  email: any
  constructor(private apiCall: ApicallService, private router: Router) { }

  ngOnInit(): void {
    let url = "http://localhost:3000/getProjects"
		this.apiCall.getData(url).subscribe((data: any ) => {
		  this.projects = data.length
		})

    url="http://localhost:3000/getUsername"
    this.apiCall.getData(url).subscribe((response: any) => {
      this.full_name = response.data.fullName
      this.email = response.data.email

    })
  }

}
