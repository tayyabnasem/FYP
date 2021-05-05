import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApicallService } from 'src/app/services/apicall.service';


@Component({
	selector: 'app-model',
	templateUrl: './model.component.html',
	styleUrls: ['./model.component.css'],
})
export class ModelComponent implements OnInit {
	
	domain: any = "None"

	constructor(private apicall: ApicallService, private route: ActivatedRoute){
		
	}

	ngOnInit(): void{
		this.route.queryParams.subscribe(params =>{
			let url = "http://localhost:3000/getProjectDomain?project=" + params.project
			this.apicall.getData(url).subscribe((data: any) =>{
				console.log(data)
				this.domain = data.domain
			})
		})
		
	}

	
}
