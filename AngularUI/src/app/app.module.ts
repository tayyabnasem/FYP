import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FileUploadModule } from 'ng2-file-upload';
import { SocialLoginModule, SocialAuthServiceConfig, GoogleLoginProvider } from 'angularx-social-login';
import { NgCircleProgressModule } from 'ng-circle-progress';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SigninComponent } from './components/signin/signin.component';
import { SignupComponent } from './components/signup/signup.component';
import { PreprocessComponent } from './components/preprocess/preprocess.component';
import { ChartsComponent } from './components/charts/charts.component';
import { ShareDataService } from './services/share-data.service';
import { DescriptionComponent } from './components/description/description.component';
import { HomeComponent } from './components/home/home.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ProjectlistComponent } from './components/projectlist/projectlist.component';
import { ApicallService } from './services/apicall.service';
import { ProfileComponent } from './profile/profile.component';
import { ModelComponent } from './components/model/model.component';
import { SortableModule } from '@progress/kendo-angular-sortable';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NavbarComponent } from './components/navbar/navbar.component';
import { ModelDLComponent } from './components/model-dl/model-dl.component';
import { ModelMLComponent } from './components/model-ml/model-ml.component';
import { SearchComponent } from './components/search/search.component';




@NgModule({
    declarations: [
        AppComponent,
        SigninComponent,
        SignupComponent,
        PreprocessComponent,
        ChartsComponent,
        DescriptionComponent,
        HomeComponent,
        DashboardComponent,
        ProjectlistComponent,
        ProfileComponent,
        ModelComponent,
        NavbarComponent,
        ModelDLComponent,
        ModelMLComponent,
        SearchComponent,
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        HttpClientModule,
        FileUploadModule,
        RouterModule,
        SocialLoginModule,
        SortableModule,
        BrowserAnimationsModule,
        NgCircleProgressModule.forRoot({
                outerStrokeWidth: 10,
                outerStrokeColor: "#0c7ce6",
                innerStrokeColor: "#ffffff",
                animationDuration: 500})
    ],
    providers: [
        ShareDataService,
        ApicallService,
        {
            provide: 'SocialAuthServiceConfig',
            useValue: {
                autoLogin: false,
                providers: [
                    {
                        id: GoogleLoginProvider.PROVIDER_ID,
                        provider: new GoogleLoginProvider(
                            '345283664483-6m14ajh4ik9dvm99fsjgm1o3epp53dkn.apps.googleusercontent.com'
                        )
                    }
                ]
            } as SocialAuthServiceConfig,
        }
    ],
    bootstrap: [AppComponent]
})
export class AppModule { }
