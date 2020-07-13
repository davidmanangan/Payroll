package iam.davidmanangan.payroll.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;
import javax.json.Json;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;

import iam.davidmanangan.payroll.model.Employee;
import iam.davidmanangan.payroll.model.EmployeeQueryParams;
import iam.davidmanangan.payroll.model.EmployeeQueryResponse;
import iam.davidmanangan.payroll.service.EmployeeService;
import iam.davidmanangan.payroll.service.impl.EmployeeCSVServiceImpl;

@Path("")
@Singleton
public class EmployeePayrollController {

	
	private Map<String,Employee> employeeRepository;
	
	EmployeeService employeeService;
	
	
	public EmployeePayrollController() {
		
		this.employeeRepository = new ConcurrentHashMap<>();
		
		this.employeeService  = new EmployeeCSVServiceImpl(employeeRepository);
	}
	

	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadCSV( 
			@FormDataParam("file") InputStream file) throws IOException {
		
		int numberOfRowsInEmployeeRecords = employeeService.processFile(file);
		
		
		if(numberOfRowsInEmployeeRecords > 0) {
			
			return Response.ok().build();
			
		}
		
		return Response.status(Response.Status.BAD_REQUEST).entity("File Content is not valid").build();
		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllEmployee(
			@QueryParam("minSalary") Double minSalary,
			@QueryParam("maxSalary") Double maxSalary,
			@QueryParam("offset") Integer offset,
			@QueryParam("limit") Integer limit,
			@QueryParam("sort") String sort
			){
		
		if(minSalary == null
			|| maxSalary == null
			|| offset == null
			|| limit == null
			|| sort == null) {
			
			return Response.status(Response.Status.BAD_REQUEST).build();
			
		}
		
		EmployeeQueryParams queryParameters = new EmployeeQueryParams(minSalary, maxSalary, offset, limit, sort);
		
		return Response.ok().entity(new EmployeeQueryResponse(employeeService.getEmployees(queryParameters))).build();
		
	}
	
	
}
