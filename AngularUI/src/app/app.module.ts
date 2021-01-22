import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FileUploadModule } from 'ng2-file-upload';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SigninComponent } from './components/signin/signin.component';
import { SignupComponent } from './components/signup/signup.component';
import { PreprocessComponent } from './components/preprocess/preprocess.component';
import { ChartsComponent } from './components/charts/charts.component';
import { ShareDataService } from './services/share-data.service';
import { DescriptionComponent } from './components/description/description.component';
import { HomeComponent } from './components/home/home.component';

@NgModule({
  declarations: [
    AppComponent,
    SigninComponent,
    SignupComponent,
    PreprocessComponent,
    ChartsComponent,
    DescriptionComponent,
    HomeComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule, 
    FileUploadModule,
    RouterModule,
  ],
  providers: [ShareDataService],
  bootstrap: [AppComponent]
})
export class AppModule { }
