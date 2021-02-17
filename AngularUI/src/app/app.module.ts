import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FileUploadModule } from 'ng2-file-upload';
import { SocialLoginModule, SocialAuthServiceConfig, GoogleLoginProvider } from 'angularx-social-login';

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
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        HttpClientModule,
        FileUploadModule,
        RouterModule,
        SocialLoginModule
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
