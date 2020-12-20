import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FileUploadModule } from 'ng2-file-upload';
import { ChartsModule } from 'ng2-charts';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SigninComponent } from './components/signin/signin.component';
import { SignupComponent } from './components/signup/signup.component';
import { PreprocessComponent } from './components/preprocess/preprocess.component';
import { ChartsComponent } from './charts/charts.component';
import { ShareDataService } from './services/share-data.service';
import { DescriptionComponent } from './components/description/description.component';


@NgModule({
  declarations: [
    AppComponent,
    SigninComponent,
    SignupComponent,
    PreprocessComponent,
    ChartsComponent,
    DescriptionComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule, 
    FileUploadModule,
    ChartsModule,
    RouterModule,
  ],
  providers: [ShareDataService],
  bootstrap: [AppComponent]
})
export class AppModule { }
