import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApicallService } from 'src/app/services/apicall.service';
import * as Prism from 'prismjs'
import { io } from 'socket.io-client';
import 'prismjs/components/prism-python'

@Component({
	selector: 'app-model-dl',
	templateUrl: './model-dl.component.html',
	styleUrls: ['./model-dl.component.css']
})
export class ModelDLComponent implements OnInit {

	private socket: any

	newLayerData: any = {
		layerName: "Dense",
		Dense: {
			units: 1,
			activationFunction: "Default",
		},
		Dropout: {
			dropoutRate: 0.2
		},
		LSTM: {
			units: 1,
			activationFunction: "Default",
			recurrentActivation: "Default",
			dropout: 0,
			returnSequence: "False"
		}
	}
	selectedLayerData: any = {
		layerName: "Dense",
		Dense: {
			units: 1,
			activationFunction: "Default",
		},
		Dropout: {
			dropoutRate: 0.2
		}
	}
	trainingData: any = {
		epoch: 50,
		learningRate: 0.01,
		lossFunction: "Binary Crossentropy",
		batchSize: 16,
		optimizer: "Adam",
		validation_split: 70,
		output_coulmn: "Select Column"
	}
	selectedLayerIndex: number = 0
	layerModel: any[] = []
	columns: any[] = []
	project_id: any
	code: any = "[i for i in range(10)]"
	traininglogs: any = ""


	constructor(private apiCall: ApicallService, private route: ActivatedRoute) {
		this.socket = io('http://localhost:3000');
	}

	ngOnInit(): void {
		this.route.queryParams.subscribe(params => {
			this.project_id = params.project
			let url = "http://localhost:3000/getColumns?project=" + params.project
			this.apiCall.getData(url).subscribe((data: any) => {
				this.columns = data.columns
			})
			url = 'http://localhost:3000/getModelInfo?project=' + this.project_id
			this.apiCall.getData(url).subscribe((response: any) => {
				this.layerModel = response.data.layers
				console.log(this.layerModel)
				if (Object.keys(response.data.hyperparameters).length !== 0) {
					this.trainingData = response.data.hyperparameters
				}
			})
		});
		this.socket.on('logs', (data: any) => {
			this.traininglogs += data + "<br>"
		});
		this.code = Prism.highlight(this.code, Prism.languages['python'])
	}

	onSpinUp() { this.newLayerData.Dense.units += 1; }

	onSpinDown() { if (this.newLayerData.Dense.units > 1) this.newLayerData.Dense.units -= 1; }

	onSpinUpUpdate() { this.selectedLayerData.units += 1; }

	onSpinDownUpdate() { if (this.selectedLayerData.units > 1) this.selectedLayerData.units -= 1; }

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
		if (this.selectedLayerData.dropoutRate < 1) {
			this.selectedLayerData.dropoutRate = parseFloat(this.selectedLayerData.dropoutRate) + 0.01;
			this.selectedLayerData.dropoutRate = this.selectedLayerData.dropoutRate.toFixed(2);
		}
	}

	onDropoutRateDownUpdate() {
		if (this.selectedLayerData.dropoutRate > 0.01) {
			this.selectedLayerData.dropoutRate = parseFloat(this.selectedLayerData.dropoutRate) - 0.01;
			this.selectedLayerData.dropoutRate = this.selectedLayerData.dropoutRate.toFixed(2);
		}
	}

	checkLearningRateChange() {
		this.trainingData.learningRate < 0.01 ? this.trainingData.learningRate = 0.01 : this.trainingData.learningRate;
		this.trainingData.learningRate > 1.00 ? this.trainingData.learningRate = 1.00.toFixed(2) : this.trainingData.learningRate;
	}

	checkEpochChange() {
		this.trainingData.epoch < 1 ? this.trainingData.epoch = 1 : this.trainingData.epoch;
	}

	addLayer() {
		var temp: any = { layerName: this.newLayerData.layerName }
		for (var prop in this.newLayerData[this.newLayerData.layerName]) {
			temp[prop] = this.newLayerData[this.newLayerData.layerName][prop]
		}
		this.layerModel = [...this.layerModel, temp]
		this.newLayerData = {
			layerName: "Dense",
			Dense: {
				units: 1,
				activationFunction: "Default",
			},
			Dropout: {
				dropoutRate: 0.2
			},
			LSTM: {
				units: 1,
				activationFunction: "Default",
				recurrentActivation: "Default",
				dropout: 0,
				returnSequence: "False"
			}
		}
		console.log(this.layerModel)
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
		this.traininglogs = ""
		let url = "http://localhost:3000/generatemodel?project=" + this.project_id
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
