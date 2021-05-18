import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApicallService } from 'src/app/services/apicall.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  userName: String = "Tayyab Naseem Minhas"

  constructor(private apiCall: ApicallService, private router: Router) { }

  ngOnInit(): void {
    let url="http://localhost:3000/getUsername"
    this.apiCall.getData(url).subscribe((response: any) => {
      //console.log(response)
      this.userName = response.data
    })
  }

  logout() {
    let url = "http://localhost:3000/signout"
    this.apiCall.getData(url).subscribe((response: any) => {
      //console.log(response)
      this.router.navigate([''])
    })
  }

}
