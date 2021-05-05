import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { io } from 'socket.io-client';
import { ApicallService } from 'src/app/services/apicall.service';

@Component({
    selector: 'app-model-ml',
    templateUrl: './model-ml.component.html',
    styleUrls: ['./model-ml.component.css']
})
export class ModelMLComponent implements OnInit {


    private socket: any
    model: any = {
        algorithm: "Gaussian Naive Bayes",
        algo_type: "Classification",
        parameters: {}
    }
    decision_tree_classifier_params = {
        criterion: "Gini"
    }
    random_forest_classifier_params = {
        trees: 10,
        criterion: "Gini"
    }
    utilities: any = {
        validation_split: 70,
        output_coulmn: "Select Column"
    }
    project_id: any = ""
    columns: any = []
    traininglogs: any = ""
    classificationValue: any = "Gaussian Naive Bayes"
    regressionValue: any = "Linear Regression"
    clusteringValue: any = "K-means"

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
        });
        this.socket.on('logs', (data: any) => {
            this.traininglogs += data + "<br>"
        });
    }

    train() {
        if (this.model.algorithm == 'Random Forest' && this.model.algo_type == 'Classification'){
            this.model.parameters = this.random_forest_classifier_params
        } else if (this.model.algorithm == 'Decision Tree Classifier'){
            this.model.parameters = this.decision_tree_classifier_params
        }
        this.model.parameters['output_coulmn'] = this.utilities.output_coulmn
        this.model.parameters['validation_split'] = this.utilities.validation_split
        this.traininglogs = ""
        let url = "http://localhost:3000/generatemodelml?project=" + this.project_id
        this.apiCall.postData(url, this.model).subscribe((response) => {
            console.log(response)
        })
    }

    onClassify() {
        this.model.algo_type = "Classification"
        this.model.algorithm = "Gaussian Naive Bayes"
    }
    onRegress() {
        this.model.algo_type = "Regression"
        this.model.algorithm = "Linear Regression"
    }
    onCluster() {
        this.model.algo_type = "Clustering"
        this.model.algorithm = "K-means"
    }
}
