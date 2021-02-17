import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';

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

  constructor(private router: Router) {
    this.isOpen = false;
    this.model = { name: 'Deep Learning Studio', description: 'NN models' }
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
  }
  onClickDL() {
    this.domainML = false
    this.deeplearningicon = 'deep-learning-active'
    this.machinelearningicon = 'machine-learning'
  }
  next(){
    this.showPreprocess.emit()
  }

  onClick() {
    console.log(this.isOpen)
    this.isOpen = !this.isOpen
  }
}
