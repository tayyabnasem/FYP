import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SignupServiceService {

  constructor(private http: HttpClient) { }

  getData(url: string){
    return this.http.get(url, {withCredentials: true})
  }

  postData(url: string, data: any){
    return this.http.post<any>(url, data, {withCredentials: true});
  }
}
