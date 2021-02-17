import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ApicallService {

  constructor(private http: HttpClient) { }

  getData(url: string){
    return this.http.get(url, {withCredentials: true})
  }

  postData(url: string, data: any){
    return this.http.post<any>(url, data, {withCredentials: true});
  }
}
