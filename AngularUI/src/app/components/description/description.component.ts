import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-description',
  templateUrl: './description.component.html',
  styleUrls: ['./description.component.css']
})
export class DescriptionComponent implements OnInit {
  isOpen: boolean
  model: any
  isValid: boolean
  selectedRadio: any
  selectedRadioML: any
  selectedRadioDL: any
  isVisible: boolean
  name: any
  constructor(private router: Router) {
    this.isOpen = false;
    this.model = { name: 'Deep Learning Studio', description: 'NN models' }
    this.isValid = false;
    this.isVisible = false;
  }

  ngOnInit(): void {
  }

  onRadioClick() {
    if (this.selectedRadio == "ML") {
      this.isValid = true
    } else {
      this.isValid = false
    }
    this.isVisible = true
  }
  onClickML() {

  }
  onClickDL() {

  }
  next(){
    this.router.navigateByUrl('preprocess')
  }

  onClick() {
    console.log(this.isOpen)
    this.isOpen = !this.isOpen
  }
  onSubmit() {

  }
}
