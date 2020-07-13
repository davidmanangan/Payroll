package iam.davidmanangan.payroll.model;

import java.util.List;

public class EmployeeQueryResponse {

	List<Employee> results;

	public EmployeeQueryResponse(List<Employee> results) {
		this.results = results;
	}

	public List<Employee> getResults() {
		return results;
	}

	public void setResults(List<Employee> results) {
		this.results = results;
	}
	
	
}
