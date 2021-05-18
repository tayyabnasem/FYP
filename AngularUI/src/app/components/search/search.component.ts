import { Component, OnInit } from '@angular/core';
import { ApicallService } from 'src/app/services/apicall.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  search: string
  projects: any = []
  constructor(private apiCall: ApicallService, private router: Router) {
    this.search = ""
  }

  ngOnInit(): void {

  }
  onSearch(){
    let url = "http://localhost:3000/searchProjects?search="+this.search
    console.log(url)
    this.apiCall.getData(url).subscribe((response) =>{
      console.log(response)
      this.projects=response
    }) 
  }
  openProject(index: number){
		this.router.navigate(['preprocess'], {queryParams: {project: this.projects[index]._id}})
	}


}

