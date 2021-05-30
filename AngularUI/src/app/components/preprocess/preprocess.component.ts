import { Component, EventEmitter, OnInit, Output, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FileUploader, FileItem, FileLikeObject } from 'ng2-file-upload';
import { ApicallService } from 'src/app/services/apicall.service';
import { ShareDataService } from 'src/app/services/share-data.service';

@Component({
	selector: 'app-preprocess',
	templateUrl: './preprocess.component.html',
	styleUrls: ['./preprocess.component.css'],
	exportAs: "columnSelectForm"
})
export class PreprocessComponent implements OnInit {

	@Output()
	showVisualization = new EventEmitter()

	@ViewChild('hiddenBtn', { static: false }) myHiddenBtn: any;

	public uploader: FileUploader = new FileUploader({
		url: 'http://localhost:3000/uploadFile',
		itemAlias: 'file',
		allowedMimeType: ['text/csv', 'application/vnd.ms-excel']
	});
	disable: boolean
	isOpen: boolean
	columnsData: any
	model: any
	progress: number = 0;
	selected_column: number = 0
	statsToDisplay: any
	isInteger: boolean = false
	data_error: undefined
	file_name: String = ""
	project_id: String = ""
	over_all_dataset_options: any = {
		drop_rows: true
	}
	columns_error: Array<any> = []


	constructor(private apicall: ApicallService, private router: Router, private sharingService: ShareDataService, private route: ActivatedRoute) {
		this.isOpen = false;
		this.disable = false;
		this.columnsData = []
		this.model = {}
		this.statsToDisplay = {
			name: '',
			type: '',
			missing: '',
			unique: '',
			mean: '',
			std: '',
			min: '',
			max: '',
			labels: []
		}
	}

	getDatasetStistics() {
		let url = "http://localhost:3000/getDatasetStaictics?project=" + this.project_id

		this.apicall.getData(url).subscribe((res: any) => {
			console.log(res)
			if (res.error != 'None')
				this.data_error = res.error
			else {
				let renew_model = true
				if (res.preprocessing_options.column_wise_options) {
					this.model = res.preprocessing_options.column_wise_options
					this.over_all_dataset_options = res.preprocessing_options.over_all_dataset_options
					renew_model = false
				}
				this.columnsData = res.data_statistics
				for (let i = 0; i < this.columnsData.length; i++) {
					this.columns_error.push({
						replacement_error: ''
					})
					if (renew_model) {
						this.model[this.columnsData[i].name] = {
							include: true,
							impute_int_with: 'mean',
							impute_str_with: 'Blank',
							type: this.columnsData[i].type,
							replacevalue: "",
							replacewith: ""
						}
					}
				};

			}
		})
	}

	ngOnInit() {

		this.route.queryParams.subscribe(params => {
			this.project_id = params.project
			this.uploader.setOptions({ url: 'http://localhost:3000/uploadFile?project=' + this.project_id });
			console.log(this.project_id)
			let url = "http://localhost:3000/disableFields?project=" + this.project_id
			console.log(url)
			this.apicall.getData(url).subscribe((response: any) => {
				console.log(response)
				if (response.data == false) {
					this.disable = !response.data;
				}
			})
		});

		this.getDatasetStistics()

		this.uploader.onAfterAddingFile = (file) => {
			file.withCredentials = true;
			this.data_error = undefined
			this.progress = 0
			this.file_name = file._file.name
			this.uploader.options.additionalParameter = {
				project: this.project_id,
			};
		};

		this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
			console.log('File Uploaded');
			console.log(JSON.parse(response))
			let res = JSON.parse(response)
			if (res.error != 'None')
				this.data_error = res.error
			else {
				this.model = {}
				this.columns_error = []
				this.columnsData = res.data
				for (let i = 0; i < this.columnsData.length; i++) {
					this.columns_error.push({
						replacement_error: ''
					})
					this.model[this.columnsData[i].name] = {
						include: true,
						impute_int_with: 'mean',
						impute_str_with: 'Blank',
						type: this.columnsData[i].type,
						replacevalue: "",
						replacewith: ""
					}
				};
			}
			this.uploader.queue.pop()
		};

		this.uploader.onWhenAddingFileFailed = (item: FileLikeObject, filter: any, options: any) => {
			console.log(item);
			console.log(filter);
			console.log(options);
			this.myHiddenBtn.nativeElement.click();
			// todo: show alert that you tried uploading wrong files
		};

		this.uploader.onProgressItem = (item: FileItem, progress: any) => {
			console.log(progress)
			this.progress = progress
		}
	}

	onColumnSelect(item: any, index: number) {
		this.model[item].include = !this.model[item].include
	}

	onColumnClick(index: number) {
		this.statsToDisplay = {
			name: this.columnsData[index].name,
			type: this.columnsData[index].type,
			missing: this.columnsData[index].missing,
			unique: this.columnsData[index].unique,
			mean: this.columnsData[index].mean,
			std: this.columnsData[index].std,
			min: this.columnsData[index].min,
			max: this.columnsData[index].max,
			labels: this.columnsData[index].labels
		}
		this.selected_column = index
		console.log(this.selected_column)
		if (this.statsToDisplay.type === "String") {
			this.isInteger = false
		} else {
			this.isInteger = true
		}
	}

	onFilterData() {
		let filterDataUrl = "http://localhost:3000/filterData?project=" + this.project_id
		let dataToSend: Array<any> = []
		console.log("Model", this.model)
		console.log("OverAll", this.over_all_dataset_options)

		for (let i = 0; i < this.columnsData.length; i++) {
			if (this.model[this.columnsData[i].name].include) {
				dataToSend.push(this.columnsData[i].name)
			}

		};
		console.log(this.columns_error)
		// console.log(dataToSend)
		// this.sharingService.setData(dataToSend)

		let filter_opt_data = { over_all_dataset_options: this.over_all_dataset_options, column_wise_options: this.model }

		this.apicall.postData(filterDataUrl, filter_opt_data).subscribe((data) => {
			console.log("Data", data)
			this.router.navigate(['/visualize'], { queryParamsHandling: 'preserve' })
		})
	}

	onClick() {
		this.isOpen = !this.isOpen
	}

	logModel() {
		console.log(this.model)
	}
	onImport() {
		let url = "http://localhost:3000/importprojectroute?project=" + this.project_id
		console.log(url)
		this.apicall.getData(url).subscribe((response: any) => {
			console.log(response)
		})
	}

}

