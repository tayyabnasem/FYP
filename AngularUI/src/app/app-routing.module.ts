import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ChartsComponent } from './charts/charts.component';
import { DescriptionComponent } from './components/description/description.component';
import { PreprocessComponent } from './components/preprocess/preprocess.component';
import { SigninComponent } from './components/signin/signin.component';

const routes: Routes = [
  { path: '', component: SigninComponent },
  {path: 'description', component: DescriptionComponent},
  { path: 'visualize', component: ChartsComponent },
  {path: 'preprocess', component: PreprocessComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
