import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ChartsComponent } from './components/charts/charts.component';
import { DescriptionComponent } from './components/description/description.component';
import { HomeComponent } from './components/home/home.component';
import { PreprocessComponent } from './components/preprocess/preprocess.component';
import { SigninComponent } from './components/signin/signin.component';
import { SignupComponent } from './components/signup/signup.component';
import { DashboardComponent } from './components/dashboard/dashboard.component'
import { ModelComponent } from './components/model/model.component';
import { SearchComponent } from './components/search/search.component';
import { ProfileComponent } from './components/profile/profile.component';

const routes: Routes = [
  { path: '', component: SigninComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'home', component: HomeComponent },
  { path: 'newProject', component: DescriptionComponent },
  { path: 'preprocess', component: PreprocessComponent },
  { path: 'visualize', component: ChartsComponent },
  { path: 'model', component: ModelComponent },
  { path: 'search', component: SearchComponent },
  { path: 'profile', component: ProfileComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
