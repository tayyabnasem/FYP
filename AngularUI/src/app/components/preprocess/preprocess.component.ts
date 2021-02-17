import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { FileUploadModule, FileUploader, FileItem } from 'ng2-file-upload';
import { ShareDataService } from 'src/app/services/share-data.service';
import { SignupServiceService } from 'src/app/services/signup-service.service';

@Component({
  selector: 'app-preprocess',
  templateUrl: './preprocess.component.html',
  styleUrls: ['./preprocess.component.css'],
  exportAs: "columnSelectForm"
})
export class PreprocessComponent implements OnInit {

  @Output()
  showVisualization = new EventEmitter()

  public uploader: FileUploader = new FileUploader({
    url: 'http://localhost:3000/uploadFile',
    itemAlias: 'file',
  });
  signupService: SignupServiceService
  isOpen: boolean
  columnsData: any
  model: any
  progress: number = 0;
  selected_column: number = 0
  statsToDisplay: any
  isInteger: boolean = false
  private router: Router

  constructor(private http: HttpClient, router: Router, private sharingService: ShareDataService) {
    this.router = router
    this.isOpen = false;
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
    this.signupService = new SignupServiceService(http)
  }

  ngOnInit() {
    this.uploader.onAfterAddingFile = (file) => {
      file.withCredentials = true;
    };

    this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
      console.log('File Uploaded');
      console.log(JSON.parse(response))
      this.columnsData = JSON.parse(response);
      for (let i = 0; i < this.columnsData.length; i++) {
        this.model[this.columnsData[i].name] = [false, 'mean', 'None', {
          replacevalue: "",
          replacewith: ""
        }]
      };
      this.uploader.queue.pop()
    };

    this.uploader.onProgressItem = (item: FileItem, progress: any) => {
      console.log(progress)
      this.progress = progress
    }
  }

  onColumnSelect(item: any, index: number) {
    this.model[item][0] = !this.model[item][0]
    this.model[item][2] = this.columnsData[index].type
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
    if (this.statsToDisplay.type === "String") {
      this.isInteger = false
    } else {
      this.isInteger = true
    }
  }

  onFilterData() {
    let filterDataUrl = "http://localhost:3000/filterData"
    let dataToSend: Array<any> = []

    this.signupService.postData(filterDataUrl, this.model).subscribe((data) => {
      console.log("Data", data)
      this.showVisualization.emit()
      for (let i = 0; i < this.columnsData.length; i++) {
        if (this.model[this.columnsData[i].name][0])
          dataToSend.push(this.columnsData[i].name)
      };
      console.log(dataToSend)
      this.sharingService.setData(dataToSend)
    }) 
  }

  onClick() {
    this.isOpen = !this.isOpen
  }

}

