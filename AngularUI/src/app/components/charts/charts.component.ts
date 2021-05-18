import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApicallService } from 'src/app/services/apicall.service';
import { ShareDataService } from '../../services/share-data.service';
import { SignupServiceService } from '../../services/signup-service.service';

@Component({
	selector: 'app-charts',
	templateUrl: './charts.component.html',
	styleUrls: ['./charts.component.css']
})
export class ChartsComponent implements OnInit {
	isOpen: boolean
	isBGVisible: boolean
	isSPVisible: boolean
	isLGVisible: boolean
	disable: boolean = false
	columns: Array<String> = []
	project_id: any
	model: any = {
		x: 'Select Column',
		y: 'Select Column',
		plottype: 'bar'
	}
	image: any = undefined

	constructor(private apicall: ApicallService, private route: ActivatedRoute, private router: Router) {
		this.isOpen = false
		this.isBGVisible = true
		this.isSPVisible = false
		this.isLGVisible = false
	}

	ngOnInit(): void {

		this.route.queryParams.subscribe(params => {
			this.project_id = params.project
			let url = "http://localhost:3000/getColumns?project=" + params.project
			this.apicall.getData(url).subscribe((data: any) => {
				console.log(data)
				this.columns = data.columns
			})
			url = "http://localhost:3000/disableFields?project=" + this.project_id
			console.log(url)
			this.apicall.getData(url).subscribe((response:any) => {
				console.log(response)
				if (response.data==false){
					this.disable=!response.data;
				}
			})
		});

	}

	createPlot() {
		if (this.image) {
			this.image = undefined
		}
		let URL = 'http://localhost:3000/plotData'
		this.apicall.postData(URL, this.model).subscribe((data) => {
			console.log(data)
			this.image = 'http://localhost:3000/' + data.imagePath
		})
	}

	onClick() {
		this.isOpen = !this.isOpen
	}
	onBGClick() {
		this.model.plottype = 'bar'
		this.isBGVisible = true
		this.isLGVisible = false
		this.isSPVisible = false
	}
	onSPClick() {
		this.model.plottype = 'scatter'
		this.isBGVisible = false
		this.isLGVisible = false
		this.isSPVisible = true
	}
	onLGClick() {
		this.model.plottype = 'line'
		this.isBGVisible = false
		this.isLGVisible = true
		this.isSPVisible = false
	}

	onImport() {
		let url = "http://localhost:3000/importprojectroute?project=" + this.project_id
		console.log(url)
		this.apicall.getData(url).subscribe((response: any) => {
			console.log(response)
		})
	}

}
