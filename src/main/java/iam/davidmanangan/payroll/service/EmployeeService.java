package iam.davidmanangan.payroll.service;

import java.io.InputStream;
import java.util.List;

import iam.davidmanangan.payroll.model.Employee;
import iam.davidmanangan.payroll.model.EmployeeQueryParams;

public interface EmployeeService {

	public int processFile(InputStream file);
	
	public List<Employee> getEmployees(EmployeeQueryParams queryParameters);
	
}
