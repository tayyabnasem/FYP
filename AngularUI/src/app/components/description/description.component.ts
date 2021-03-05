import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { ApicallService } from 'src/app/services/apicall.service';

@Component({
  selector: 'app-description',
  templateUrl: './description.component.html',
  styleUrls: ['./description.component.css']
})
export class DescriptionComponent implements OnInit {

  @Output()
  showPreprocess = new EventEmitter();

  isOpen: boolean
  model: any
  isValid: boolean
  isVisible: boolean
  domainML: boolean
  name: any

  deeplearningicon: any = 'deep-learning'
  machinelearningicon: any = 'machine-learning-active'

  constructor(private router: Router, private apiCall: ApicallService) {
    this.isOpen = false;
    this.model = { name: '', description: '', domain: 'Machine Learning' }
    this.isValid = false;
    this.isVisible = false;
    this.domainML = true;
  }

  ngOnInit(): void {
  }

  onClickML() {
    this.domainML = true
    this.machinelearningicon = 'machine-learning-active'
    this.deeplearningicon = 'deep-learning'
    this.model.domain = "Machine Learning"
  }
  onClickDL() {
    this.domainML = false
    this.deeplearningicon = 'deep-learning-active'
    this.machinelearningicon = 'machine-learning'
    this.model.domain = "Deep Learning"
  }
  next() {
    this.showPreprocess.emit()
  }

  createProject() {
    let url = "http://localhost:3000/createProject"

    this.apiCall.postData(url, this.model).subscribe((data) => {
      console.log("Data", data)
      if (data.text == "Project Created"){
        this.router.navigate(['preprocess'], { queryParams: {project: data.projectID}, replaceUrl: true })
      }
    })
  }

  onClick() {
    console.log(this.isOpen)
    this.isOpen = !this.isOpen
  }
}
