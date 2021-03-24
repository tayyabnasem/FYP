import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApicallService } from 'src/app/services/apicall.service';

@Component({
	selector: 'app-model',
	templateUrl: './model.component.html',
	styleUrls: ['./model.component.css']
})
export class ModelComponent implements OnInit {

	newLayerData: any = {
		layerName: "Dense",
		Dense: {
			units: 1,
			activationFunction: "Default",
		},
		Dropout:{
			dropoutRate: 0.2
		}
	}
	selectedLayerData: any = {
		layerName: "Dense",
		Dense: {
			units: 1,
			activationFunction: "Default",
		},
		Dropout:{
			dropoutRate: 0.2
		}
	}
	trainingData: any = {
		epoch: 50,
		learningRate: 0.01,
		lossFunction: "Binary Crossentropy",
		batchSize: 16,
		optimizer: "Adam",
		validation_split: 70
	}
	selectedLayerIndex: number = 0
	layerModel: any[] = []
	project_id: any


	constructor(private apiCall: ApicallService, private route: ActivatedRoute) { }

	ngOnInit(): void {
		this.route.queryParams.subscribe(params => {
			this.project_id = params.project
			let url = 'http://localhost:3000/getModelInfo?project=' + this.project_id
			this.apiCall.getData(url).subscribe((response: any) => {
				this.layerModel = response.data.layers
				//console.log(Object.keys(response.data.hyperparameters).length)
				if (Object.keys(response.data.hyperparameters).length !== 0) {
					this.trainingData = response.data.hyperparameters
				}
				// console.log(response)
				// console.log("Hyper", this.trainingData)
			})
		});
	}

	onSpinUp() { this.newLayerData.Dense.units += 1; }

	onSpinDown() { if (this.newLayerData.Dense.units > 1) this.newLayerData.Dense.units -= 1; }

	onSpinUpUpdate() { this.selectedLayerData.Dense.units += 1; }

	onSpinDownUpdate() { if (this.selectedLayerData.Dense.units > 1) this.selectedLayerData.Dense.units -= 1; }

	onEpochUp() { this.trainingData.epoch += 1; }

	onEpochDown() { if (this.trainingData.epoch > 1) this.trainingData.epoch -= 1; }

	onLearningRateUp() {
		if (this.trainingData.learningRate < 1) {
			this.trainingData.learningRate = parseFloat(this.trainingData.learningRate) + 0.01;
			this.trainingData.learningRate = this.trainingData.learningRate.toFixed(2);
		}
	}

	onLearningRateDown() {
		if (this.trainingData.learningRate > 0.01) {
			this.trainingData.learningRate = parseFloat(this.trainingData.learningRate) - 0.01;
			this.trainingData.learningRate = this.trainingData.learningRate.toFixed(2);
		}
	}

	onDropoutRateUp() {
		if (this.newLayerData.Dropout.dropoutRate < 1) {
			this.newLayerData.Dropout.dropoutRate = parseFloat(this.newLayerData.Dropout.dropoutRate) + 0.01;
			this.newLayerData.Dropout.dropoutRate = this.newLayerData.Dropout.dropoutRate.toFixed(2);
		}
	}

	onDropoutRateDown() {
		if (this.newLayerData.Dropout.dropoutRate > 0.01) {
			this.newLayerData.Dropout.dropoutRate = parseFloat(this.newLayerData.Dropout.dropoutRate) - 0.01;
			this.newLayerData.Dropout.dropoutRate = this.newLayerData.Dropout.dropoutRate.toFixed(2);
		}
	}

	onDropoutRateUpUpdate() {
		if (this.selectedLayerData.Dropout.dropoutRate < 1) {
			this.selectedLayerData.Dropout.dropoutRate = parseFloat(this.selectedLayerData.Dropout.dropoutRate) + 0.01;
			this.selectedLayerData.Dropout.dropoutRate = this.selectedLayerData.Dropout.dropoutRate.toFixed(2);
		}
	}

	onDropoutRateDownUpdate() {
		if (this.selectedLayerData.Dropout.dropoutRate > 0.01) {
			this.selectedLayerData.Dropout.dropoutRate = parseFloat(this.selectedLayerData.Dropout.dropoutRate) - 0.01;
			this.selectedLayerData.Dropout.dropoutRate = this.selectedLayerData.Dropout.dropoutRate.toFixed(2);
		}
	}

	checkLearningRateChange(){
		this.trainingData.learningRate < 0.01 ? this.trainingData.learningRate = 0.01 : this.trainingData.learningRate; 
		this.trainingData.learningRate > 1.00 ? this.trainingData.learningRate = 1.00.toFixed(2) : this.trainingData.learningRate;
	}

	checkEpochChange(){
		this.trainingData.epoch < 1 ? this.trainingData.epoch = 1 : this.trainingData.epoch; 
	}

	addLayer() {
		this.layerModel = [...this.layerModel, this.newLayerData]
		this.newLayerData = {
			layerName: "Dense",
			Dense: {
				units: 1,
				activationFunction: "Default",
			},
			Dropout:{
				dropoutRate: 0.2
			}
		}
		//console.log(this.layerModel)
	}

	selectLayer(item: any) {
		this.selectedLayerIndex = this.layerModel.indexOf(item)
		//console.log("Item", item)
		this.selectedLayerData = item
	}

	saveModel() {
		let url = "http://localhost:3000/saveModel?project=" + this.project_id
		this.apiCall.postData(url, this.layerModel).subscribe((response) => {
			console.log(response)
		})
	}

	generateModel() {
		console.log(this.layerModel)
		let url = "http://localhost:3000/generatemodel"
		let dataToSend = { layers: this.layerModel, hyperparameters: this.trainingData }
		this.apiCall.postData(url, dataToSend).subscribe((response) => {
			console.log(response)
		})
	}

	saveHyperparameters() {
		let url = "http://localhost:3000/saveHyperparameter?project=" + this.project_id
		this.apiCall.postData(url, this.trainingData).subscribe((response) => {
			console.log(response)
		})
	}
}
