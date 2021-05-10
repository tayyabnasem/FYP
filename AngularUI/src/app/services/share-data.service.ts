import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ShareDataService {

  private code: any = undefined
  
  constructor() { }

  setCode(data: any) {
    this.code = data;
  }

  getCode(): any {
    return this.code;
  }
}
