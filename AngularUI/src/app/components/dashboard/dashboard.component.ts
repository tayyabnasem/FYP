import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApicallService } from 'src/app/services/apicall.service';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

    userName: String = "Tayyab Naseem Minhas"

    flags: any = {
        showProjects: true,
        showPreprocess: false,
        showDescription: false,
        showVisualization: false
    }

    constructor(private apiCall: ApicallService, private router: Router) {

    }

    ngOnInit(): void {

    }

    logout(): void{
        let url = "http://localhost:3000/signout"
        this.apiCall.getData(url).subscribe((data)=>{
            console.log(data)
            this.router.navigate([''])
        })
    }

    closeProjectsComponent() {
        this.flags.showProjects = false
        this.flags.showDescription = true
    }

    closeDescription() {
        this.flags.showDescription = false
        this.flags.showPreprocess = true
    }

    closePreprocessing() {
        this.flags.showPreprocess = false
        this.flags.showVisualization = true
    }

}
