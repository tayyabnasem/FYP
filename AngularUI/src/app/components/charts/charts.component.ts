import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ShareDataService } from '../../services/share-data.service';
import { SignupServiceService } from '../../services/signup-service.service';

@Component({
  selector: 'app-charts',
  templateUrl: './charts.component.html',
  styleUrls: ['./charts.component.css']
})
export class ChartsComponent implements OnInit {
  isOpen: boolean
  isBGVisible:boolean
  isSPVisible:boolean
  isLGVisible:boolean
  columns: any = ['None']
  model: any = {
    x: 'Select Column',
    y: 'Select Column',
    plottype:'bar'
  }
  image: any = undefined

  constructor(private sharingService: ShareDataService, private http: HttpClient, private serverservice: SignupServiceService) {
    this.isOpen = false
    this.isBGVisible = true
    this.isSPVisible = false
    this.isLGVisible = false
  }

  ngOnInit(): void {
    this.columns = this.sharingService.getData()
  }

  createPlot() {
    if (this.image) {
      this.image = undefined
    }
    let URL = 'http://localhost:3000/plotData'
    this.serverservice.postData(URL, this.model).subscribe((data) => {
      console.log(data)
      this.image = 'http://localhost:3000/' + data.imagePath
    })
  }

  onClick() {
    this.isOpen = !this.isOpen 
  }
  onBGClick(){
    this.model.plottype = 'bar'
    this.isBGVisible = true
    this.isLGVisible = false
    this.isSPVisible = false
  }
  onSPClick(){
    this.model.plottype = 'scatter'
    this.isBGVisible = false
    this.isLGVisible = false
    this.isSPVisible = true
  }
  onLGClick(){
    this.model.plottype = 'line'
    this.isBGVisible = false
    this.isLGVisible = true
    this.isSPVisible = false
  }

}
