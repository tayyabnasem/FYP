import { Component, OnInit } from '@angular/core';
import { ApicallService } from 'src/app/services/apicall.service';

@Component({
	selector: 'app-model',
	templateUrl: './model.component.html',
	styleUrls: ['./model.component.css']
})
export class ModelComponent implements OnInit {

	newLayerData: any = {
		layerName: "Dense",
		units: 1,
		activationFunction: "None"
	}
	selectedLayerData: any = {
		layerName: "",
		units: 1,
		activationFunction: "",
	}
	selectedLayerIndex: number = 0
	layerModel: any[] = []


	constructor(private apiCall: ApicallService) { }

	ngOnInit(): void { }

	onSpinUp() { this.newLayerData.units += 1; }

	onSpinDown() { if (this.newLayerData.units > 1) this.newLayerData.units -= 1; }

	onSpinUpUpdate() { this.selectedLayerData.units += 1; }

	onSpinDownUpdate() { if (this.selectedLayerData.units > 1) this.selectedLayerData.units -= 1; }

	addLayer() {
		this.layerModel = [...this.layerModel, this.newLayerData]
		this.newLayerData = {
			layerName: "Dense",
			units: 1,
			activationFunction: "None"
		}
		console.log(this.layerModel)
	}

	selectLayer(item: any) {
		this.selectedLayerIndex = this.layerModel.indexOf(item)
		console.log("Item", item)
		this.selectedLayerData = JSON.parse(JSON.stringify(item));
		console.log(this.selectedLayerIndex)
	}

	updateLayerData() {
		this.layerModel[this.selectedLayerIndex] = this.selectedLayerData
		this.layerModel = [... this.layerModel]
		console.log(this.layerModel)
	}

	generateModel() {
		let url = "http://localhost:3000/generatemodel"
		this.apiCall.postData(url, this.layerModel).subscribe((response)=>{
			console.log(response)
		})
	}

}
