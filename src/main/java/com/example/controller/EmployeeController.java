package com.example.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.DepartmentDto;
import com.example.dto.EmployeeDto;
import com.example.dto.EmployeeResponse;
import com.example.dto.SearchFilter;
import com.example.service.CommonService;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    private final CommonService employeeService;

    public EmployeeController(CommonService employeeService) {
        this.employeeService = employeeService;
    }
    
    /** Sample request url: 
    http://localhost:8080/api/dept?location=Hyderabad
    http://localhost:8080/api/dept?budget=800000
    http://localhost:8080/api/dept?location=Mumbai&budget=800000&createdBy=raj
    http://localhost:8080/api/dept?startDate=2025-02-01T00:00:00&endDate2025-06-00T00:00:00
     */
    @GetMapping("/dept")
    public ResponseEntity<List<DepartmentDto>> getDepartments(
    		@RequestParam(required = false) String location,
    		@RequestParam(required = false) BigDecimal budget,
    		@RequestParam(required = false) String createdBy,
    		@RequestParam(required = false) LocalDateTime startDate,
    		@RequestParam(required = false) LocalDateTime endDate) {
    	
    	
    	List<DepartmentDto> empList = employeeService.fetchDepartments(location,
    			budget, createdBy, startDate, startDate);
        return ResponseEntity.ok(empList);
    }
    
    
    /** GET http://localhost:8080/api/employees?searchText=rohan */
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDto>> getEmployees(@RequestParam(required = false)
    		String searchText) {
    	List<EmployeeDto> empList = employeeService.searchEmployees(searchText);
        return ResponseEntity.ok(empList);
    }
    
    @GetMapping("/employees/dept-username")
    public ResponseEntity<List<EmployeeDto>> getEmployeesDeptCreatedBy(@RequestParam(required = false)
    		String username) {
    	List<EmployeeDto> empList = employeeService.getAllEmployeeByDepartmentCreatedBy(username);
        return ResponseEntity.ok(empList);
    }
    
    
    /** Sample request:
	[
	  {
	    "columnName": "firstName",
	    "value": "Priya",
	    "operation": "like"
	  },
	  {
	    "columnName": "email",
	    "value": "priya.nair@example.com",
	    "operation": "equal"
	  }
	]
     */
    @PostMapping("/employees/filter")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByFilter(@RequestBody List<SearchFilter> filters) {
    	List<EmployeeDto> empList = employeeService.getEmployeesByFilter(filters);
        return ResponseEntity.ok(empList);
    }
    
    /** Sample request url: 
     * http://localhost:8080/api/employees/search?page=1&size=15&sortBy=id&sortOrder=desc&searchText=Neha
     */
    @GetMapping("/employees/search")
    public ResponseEntity<EmployeeResponse> getPagebleEmployee(
    		@RequestParam(required = false) Integer page,
    		@RequestParam(required = false) Integer size,
    		@RequestParam(defaultValue = "id") String sortBy,
    		@RequestParam(defaultValue = "asc") String sortOrder,
    		@RequestParam(required = false) String searchText
    		) {
    	EmployeeResponse empList = employeeService.getEmployeesBySearch(searchText, page,
    			size, sortBy, sortOrder);
        return ResponseEntity.ok(empList);
    }
    
    
}
