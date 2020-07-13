package iam.davidmanangan.payroll.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import iam.davidmanangan.payroll.model.Employee;
import iam.davidmanangan.payroll.model.EmployeeQueryParams;
import iam.davidmanangan.payroll.service.EmployeeService;

public class EmployeeCSVServiceImpl implements EmployeeService {

	
	private Map<String,Employee> employeeRepository;
	
	public EmployeeCSVServiceImpl(Map<String, Employee> employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	@Override
	public int processFile(InputStream file) {
		
		int numberOfRowsInEmployeeRecords = 0;
		
		try {
			
			
			List<String> parsedFileContentInList = new BufferedReader( new InputStreamReader(file)) .lines() .collect(Collectors.toList());
		
			file.close();

			
			if(parsedFileContentInList != null && parsedFileContentInList.size() > 0) {
				
				/**
				 * Remove Unecessary Metadata in MultipartFile
				 */
				parsedFileContentInList.remove(0);
				parsedFileContentInList.remove(0);
				parsedFileContentInList.remove(0);
				parsedFileContentInList.remove(0);
				parsedFileContentInList.remove(0);
				parsedFileContentInList.remove(parsedFileContentInList.size()-1);
				
				
				List<String> employeeRecordsWithoutComments 
					= parsedFileContentInList
						.stream()
						.filter(line -> !line.contains("#"))
						.collect(Collectors.toList());
				
				if(checkIfAllRowsContainsCompleteNumberOfColumns(employeeRecordsWithoutComments) 
					&& checkIfAllSalaryFormatIsCorrect(employeeRecordsWithoutComments)) {
					
					numberOfRowsInEmployeeRecords = saveToEmployeeDatabase(employeeRecordsWithoutComments);
					
				}

			}
			


		} catch (IOException e) {
			e.printStackTrace();
		}
		return numberOfRowsInEmployeeRecords;
	}
	
	private List<Employee> parseRows(List<String> employeeRecords){
		
		List<Employee> employeeList = employeeRecords.stream()
				.map(row -> { 
						String[] employeeRecord = row.split(",");
						
						Employee employee 
							= new Employee(
									employeeRecord[0],
									employeeRecord[1],
									employeeRecord[2],
									Double.parseDouble(employeeRecord[3]));
						
						return employee;
						
					}).collect(Collectors.toList());
		
		return employeeList;
	}
	
	private boolean checkIfAllSalaryFormatIsCorrect(List<String> employeeRecords) {
		
		boolean isAllSalaryFormatCorrect = true;

		int lineIndex = 0;

		while(isAllSalaryFormatCorrect && lineIndex < employeeRecords.size()) {
			
			String line = employeeRecords.get(lineIndex);
			
			String[] columnValue = line.split(",");
			double salary = 0d;
			try {

				salary = Double.parseDouble(columnValue[3]);
			
				if(salary < 0) isAllSalaryFormatCorrect  = false;
				
			}catch(NumberFormatException e) {
				
				isAllSalaryFormatCorrect = false;
				
				e.printStackTrace();
				
			}

			lineIndex++;
		}
		
		return isAllSalaryFormatCorrect;
	}
	
	
	private boolean checkIfAllRowsContainsCompleteNumberOfColumns(List<String> employeeRecords) {
		
		boolean isAllRowsContainsCompleteNumberOfColumn = true;

		int lineIndex = 0;

		while(isAllRowsContainsCompleteNumberOfColumn && lineIndex < employeeRecords.size()) {
			
			String line = employeeRecords.get(lineIndex);
			
			String[] columnValue = line.split(",");
			
			if(columnValue.length  != 4) {
				isAllRowsContainsCompleteNumberOfColumn = false;
			}
			
			lineIndex++;
		}
		
		return isAllRowsContainsCompleteNumberOfColumn;
	}
	
	private int saveToEmployeeDatabase(List<String> employeeRecords) {
		
		List<Employee> employeeList = parseRows(employeeRecords);
		
		
		employeeList.forEach(employee -> employeeRepository.put(employee.getId(),employee));
		
		return (employeeRepository != null && employeeRepository.size() > 0) 
			? employeeRepository.size()
			: 0;
	}

	@Override
	public List<Employee> getEmployees(EmployeeQueryParams queryParameter) {
		 
		List<Employee> allEmployees = employeeRepository
			.values()
			.stream()
			.collect(Collectors.toList());
		

		if(allEmployees !=null && allEmployees.size() > 0 && queryParameter != null) {

			
			if(queryParameter.getSortName() != null && !"".equals(queryParameter.getSortName())) {
				String sortName = queryParameter.getSortName();
				
				String order = sortName.substring(0,1);
				String fieldName = sortName.substring(1, sortName.length()).toLowerCase();
				
				Comparator<Employee> employeeComparator = getComparator(fieldName);
			    
				if("-".equals(order) && employeeComparator!=null) {
					allEmployees.sort(employeeComparator.reversed());
				}else if(employeeComparator!=null){
					allEmployees.sort(employeeComparator);
				}


			}
			
			if(queryParameter.getOffset() != null && queryParameter.getOffset() > 0)
				allEmployees 
					= allEmployees
						.stream()
						.skip(queryParameter.getOffset())
						.collect(Collectors.toList());	

			if(queryParameter.getLimit() != null && queryParameter.getLimit() > 0)
				allEmployees 
					= allEmployees
						.stream()
						.limit(queryParameter.getLimit())
						.collect(Collectors.toList());	
			
			if(queryParameter.getMinSalary() != null && queryParameter.getMinSalary() >= 0.0) 
				allEmployees 
					= allEmployees
						.stream()
						.filter(employee -> employee.getSalary() >= queryParameter.getMinSalary() )
						.collect(Collectors.toList());

			
			if(queryParameter.getMaxSalary() != null && queryParameter.getMaxSalary() >= 0.0) 
				allEmployees 
					= allEmployees
						.stream()
						.filter(employee -> employee.getSalary() <= queryParameter.getMaxSalary() )
						.collect(Collectors.toList());				
			
		}

		
		 return allEmployees;
	}
	
	private Comparator<Employee> getComparator(String fieldName){
	    switch(fieldName) {
	    
	    case "id": 
	    	return new Comparator<Employee>() {
		        @Override
		        public int compare(Employee e1, Employee e2) {
		            return e1.getId().compareTo(e2.getId());
		        }
		    };			    	
	    case "login":
	    	return new Comparator<Employee>() {
		        @Override
		        public int compare(Employee e1, Employee e2) {
		            return e1.getLogin().compareTo(e2.getLogin());
		        }
		    };			    	
	    case "name":
	    	
	    	return new Comparator<Employee>() {
		        @Override
		        public int compare(Employee e1, Employee e2) {
		            return e1.getName().compareTo(e2.getName());
		        }
		    };
		    
	    case "salary":
	    	
	    	return new Comparator<Employee>() {
		        @Override
		        public int compare(Employee e1, Employee e2) {
		            return e1.getSalary().compareTo(e2.getSalary());
		        }
		    };			    	
	    }
	    
		return null;
	}
	
}
