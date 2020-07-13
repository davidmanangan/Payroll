import { Injectable } from '@angular/core';
import { HttpClient,HttpHeaders } from '@angular/common/http';

const httpOptions = {
  headers: new HttpHeaders({
    'Access-Control-Allow-Origin':  '*'
  })
}
@Injectable({
  providedIn: 'root'
})
export class PayrollService {

  constructor(private http:HttpClient) { }

  public async getEmployees(input:any){
    return await this.http.get('http://localhost:9080/users?minSalary='+input['minSalary']+'&maxSalary='+input['maxSalary']+'&offset='+input['offset']+'&limit='+input['limit']+'&sort='+input['sort']).toPromise();
  }
}
