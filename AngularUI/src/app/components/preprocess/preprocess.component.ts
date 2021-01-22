import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
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
  public uploader: FileUploader = new FileUploader({
    url: 'http://localhost:3000/api/uploadFile',
    itemAlias: 'file',
  });
  signupService: SignupServiceService
  isOpen: boolean
  columnsData: any
  model: any
  progress: number = 0;
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
      console.log(response)
      this.columnsData = JSON.parse(response);
      for (let i = 0; i < this.columnsData.length; i++) {
        this.model[this.columnsData[i].name] = [false, 'mean', 'None']
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
    if (this.statsToDisplay.type === "string") {
      this.isInteger = false
    } else {
      this.isInteger = true
    }
  }

  onFilterData() {
    let filterDataUrl = "http://localhost:3000/api/filterData"
    console.log("Model", this.model)
    let dataToSend = {}
    this.signupService.postData(filterDataUrl, this.model).subscribe((data) => {
      console.log("Data", data)
      dataToSend = data
      this.sharingService.setData(dataToSend)
      this.router.navigateByUrl('visualize')
    }) 
  }

  onClick() {
    this.isOpen = !this.isOpen
  }

}

