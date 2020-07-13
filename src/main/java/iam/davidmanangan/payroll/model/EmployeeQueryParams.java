package iam.davidmanangan.payroll.model;

import javax.ws.rs.QueryParam;

public class EmployeeQueryParams {
	
	Double minSalary;
	Double maxSalary;
	Integer offset;
	Integer limit;
	String sortName;
	
	public EmployeeQueryParams(Double minSalary, Double maxSalary, Integer offset, Integer limit, String sortName) {
		this.minSalary = minSalary;
		this.maxSalary = maxSalary;
		this.offset = offset;
		this.limit = limit;
		this.sortName = sortName;
	}

	public Double getMinSalary() {
		return minSalary;
	}

	public void setMinSalary(Double minSalary) {
		this.minSalary = minSalary;
	}

	public Double getMaxSalary() {
		return maxSalary;
	}

	public void setMaxSalary(Double maxSalary) {
		this.maxSalary = maxSalary;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	
}
