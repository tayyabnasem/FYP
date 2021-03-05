import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { ApicallService } from 'src/app/services/apicall.service';

@Component({
	selector: 'app-projectlist',
	templateUrl: './projectlist.component.html',
	styleUrls: ['./projectlist.component.css']
})
export class ProjectlistComponent implements OnInit {

	@Output()
	showProjectsChange = new EventEmitter();

	projects: any = []

	constructor(private apiCall: ApicallService, private router: Router) { }

	ngOnInit(): void {
		let url = "http://localhost:3000/getProjects"
		this.apiCall.getData(url).subscribe((data) => {
		  this.projects = data
		})
	}

	closeProjects(){
		this.showProjectsChange.emit()
	}

	openProject(index: number){
		this.router.navigate(['preprocess'], {queryParams: {project: this.projects[index]._id}})
	}

	createNewProject(): void {
		//this.closeProjects()
		this.router.navigateByUrl('newProject')

	}

}
