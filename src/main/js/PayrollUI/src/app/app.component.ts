import { Component, Input, ViewChild,OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatTableDataSource } from '@angular/material';
import {PayrollService} from './service/payroll.service'

import {Employee} from './model/employee'
import {EmployeeDataColumn} from './model/employee-data-column'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{
  title = 'PayrollUI';

  SERVER_URL = "http://localhost:9080/users/upload";
  uploadForm: FormGroup;  

  @ViewChild('fileInput', {static: false})
  fileInput;
  
  @ViewChild('submitInput', {static: false})
  submitInput;

  @Input()
  minSalary:number = 0.0;
  
  @Input()
  maxSalary:number= 6000.0;

  @Input()
  orderBy:string ="+";

  @Input()
  sortBy:string = "name";

  currentPage:number = 0;  
  pageLimit:number = 30;
  pageOffset:number =0;

  file: File | null = null;

  columnNames: EmployeeDataColumn[] = [
    {value: 'id', viewValue: 'ID'},
    {value: 'login', viewValue: 'Login'},
    {value: 'name', viewValue: 'Name'},
    {value: 'salary', viewValue: 'Salary'}
  ];
  sortOrder: EmployeeDataColumn[] = [
    {value: '+', viewValue: 'Ascending'},
    {value: '-', viewValue: 'Descending'}
  ];
  displayedColumns: string[] = ['id','login','name','salary'];

  employeeArray:Array<Employee> = [];

  dataSource = new MatTableDataSource<Employee>(this.employeeArray);  

  constructor(
    private formBuilder: FormBuilder, 
    private httpClient: HttpClient,
    private payrollService: PayrollService 
    ){}

  ngOnInit() {
    this.uploadForm = this.formBuilder.group({
      profile: ['']
    });
    this.initTable();
  }

  tableEvent() {
    this.initTable();
  }
  onChooseFileClick(){
    this.fileInput.nativeElement.click();
  }

  onSubmitInput(){
    this.submitInput.nativeElement.click();
  }

  onFileInputChange(): void {
    const files: { [key: string]: File } = this.fileInput.nativeElement.files;
    this.file = files[0];
    this.uploadForm.get('profile').setValue(this.file);
  }

  async onSubmit() {
    const formData = new FormData();
    formData.append('file', this.uploadForm.get('profile').value);

    this.httpClient.post<any>(this.SERVER_URL, formData)
      .toPromise()
        .then(
          res =>{
            this.initTable();
          });

  }

  async initTable(){
    let employeeQueryParam = {
      minSalary:this.minSalary,
      maxSalary:this.maxSalary,
      offset:this.pageOffset,
      limit:this.pageLimit,
      sort: this.orderBy+this.sortBy
    };

    await this.payrollService.getEmployees(employeeQueryParam)
    .then( (res:any )=> {
        
        this.employeeArray = [];
        
        for(var i=0; i<res['results'].length; i++){

          var row = res['results'][i];

          this.employeeArray.push(new Employee(row['id'],row['login'],row['name'],row['salary']));
        }

        this.dataSource.data = this.employeeArray;

    } );
  }

  async onNextPageClick(){
    
    this.currentPage += 1;

    this.pageOffset = this.currentPage*this.pageLimit;

    await this.initTable();

  }


  async onPreviousPageClick(){
    
    this.currentPage -= 1;    
    
    if(this.currentPage > 0){
      this.pageOffset = this.currentPage*this.pageLimit;
    }else{
      this.pageOffset =0;
    }

    await this.initTable();

  }
}
