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
        criterion: "Gini",
        splitter: "Best"
    }
    random_forest_classifier_params = {
        trees: 100,
        criterion: "Gini"
    }
    linear_regression_params = {
        normalize: 'False',
        fit_intercept: 'True'
    }
    logistic_regression_params = {
        penalty: 'L2',
        max_iter: 100,
        multi_class: 'Auto',
        solver: 'LBFGS'
    }
    decision_tree_regressor_params = {
        criterion: 'Gini',
        splitter: 'Best'
    }

    k_means_clustering_params = {
        clusters: 2,
        initial_points: 'Random',
        max_iter: 300,
        algorithm: 'Auto'
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
            //console.log(data)
            this.traininglogs += data + "<br>"
        });
    }

    train() {
        if (this.model.algorithm == 'Random Forest' && this.model.algo_type == 'Classification') {
            this.model.parameters = this.random_forest_classifier_params
        } else if (this.model.algorithm == 'Decision Tree Classifier') {
            this.model.parameters = this.decision_tree_classifier_params
        } else if (this.model.algorithm == 'Linear Regression') {
            if (this.linear_regression_params.normalize == 'True') {
                this.model.parameters['normalize'] = true
            } else {
                this.model.parameters['normalize'] = false
            }

            if (this.linear_regression_params.fit_intercept == 'True') {
                this.model.parameters['fit_intercept'] = true
            } else {
                this.model.parameters['fit_intercept'] = false
            }
        } else if (this.model.algorithm == 'Logistic Regression'){
            this.model.parameters = this.logistic_regression_params
        } else if (this.model.algorithm == 'Decision Tree Regressor'){
            this.model.parameters = this.decision_tree_regressor_params
        } else if (this.model.algorithm == 'K-means'){
            this.model.parameters = this.k_means_clustering_params
        }
        this.model.parameters['output_coulmn'] = this.utilities.output_coulmn
        this.model.parameters['validation_split'] = this.utilities.validation_split
        this.traininglogs = "Starting Training....<br>"
        let url = "http://localhost:3000/generatemodelml?project=" + this.project_id
        this.apiCall.postData(url, this.model).subscribe((response) => {
            console.log(response)
        })
    }

    checkMaxIterationsLogReg(){
        this.logistic_regression_params.max_iter < 1 ? this.logistic_regression_params.max_iter = 1 : this.logistic_regression_params.max_iter
    }

    checkSplitRatio() {
        this.utilities.validation_split < 50 ? this.utilities.validation_split = 50 : this.utilities.validation_split
        this.utilities.validation_split > 90 ? this.utilities.validation_split = 90 : this.utilities.validation_split
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
