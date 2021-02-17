import { Component, EventEmitter, OnInit, Output } from '@angular/core';
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

	constructor(private apiCall: ApicallService) { }

	ngOnInit(): void {
		this.projects = [{
			name: 'Image Segmentation',
			domain: 'Machine Laerning',
			date: '25 Dec 2020'
		},
		{
			name: 'Image Segmentation',
			domain: 'Machine Laerning',
			date: '25 Dec 2020'
		}]
	}

	closeProjects(){
		this.showProjectsChange.emit()
	}

	createNewProject(): void {
		this.closeProjects()

	}

}
