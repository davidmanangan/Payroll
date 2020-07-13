package iam.davidmanangan.payroll;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.logging.Logger;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import iam.davidmanangan.payroll.controller.EmployeePayrollController;

public class PayrollAppTest extends JerseyTest
{

	EmployeePayrollController uploadController;
	
	@Override
    public ResourceConfig configure() {
        return new ResourceConfig(EmployeePayrollController.class)
                .register(MultiPartFeature.class)
                .register(new LoggingFilter(Logger.getAnonymousLogger(), true));
    }
    
    @Override
    public void configureClient(ClientConfig config) {
        config.register(MultiPartFeature.class);
    }
	
	@Before
	public void mock_controller(){
		uploadController = new EmployeePayrollController();
	}

	
	@Test
	public void test_employee_records_csv_upload() {
		
		assertResponseObject( 200,  "RowCount:4" , uploadCSV("/employee-records-test-file.csv") );

	}

	@Test
	public void test_employee_records_with_comments_csv_upload() {
		
		assertResponseObject( 200,  "RowCount:3" , uploadCSV("/employee-records-with-comments-test-file.csv") );
	
	}
	
	@Test
	public void test_empty_file_upload() {
		
		
		assertResponseObject( 400, "File Content is not valid" , uploadCSV("/empty-test-file.csv") );
	
	}
	
	@Test
	public void test_employee_records_upload_with_only_the_header_information() {
		
		
		assertResponseObject( 400, "File Content is not valid" , uploadCSV("/employee-records-with-headers-only-test-file.csv") );
	
	}
	
	@Test
	public void test_employee_records_with_incomplete_columns() {
		
		assertResponseObject( 400,  "File Content is not valid" , uploadCSV("/employee-records-with-incomplete-columns-test-file.csv") );
	
	}

	@Test
	public void test_employee_records_with_incorrect_salary_format() {
		
		assertResponseObject( 400,  "File Content is not valid" , uploadCSV("/employee-records-with-incorrect-salary-format.csv") );
	
	}
	
	@Test
	public void test_employee_records_with_negative_salary() {
		
		assertResponseObject( 400,  "File Content is not valid" , uploadCSV("/employee-records-with-negative-salary.csv") );
	
	}
	

	
	private void assertResponseObject(int status, String responseText, Response response) {
		
		assertEquals(status, response.getStatus());
        
		response.close();
	}
	
	
	
	
	private Response uploadCSV(String fileName) {
		
		FileDataBodyPart filePart = new FileDataBodyPart("file", new File(System.getProperty("user.dir")+fileName));
		
		filePart.setContentDisposition(
                FormDataContentDisposition.name("file").fileName(fileName).build());
		
		filePart.setMediaType(MediaType.valueOf("text/csv"));

		MultiPart multiPartFileUpload = new FormDataMultiPart()
                .bodyPart(filePart);
		
		return target("/upload").request()
                .post(Entity.entity(multiPartFileUpload, MediaType.MULTIPART_FORM_DATA));
	}
	
}
