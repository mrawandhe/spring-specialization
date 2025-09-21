package com.example.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.dto.DepartmentDto;
import com.example.dto.EmployeeDto;
import com.example.dto.EmployeeResponse;
import com.example.dto.PageMetadata;
import com.example.dto.SearchFilter;
import com.example.entity.Department;
import com.example.entity.Employee;
import com.example.repository.DepartmentRepository;
import com.example.repository.EmployeeRepository;
import com.example.specification.DepartmentSpecification;
import com.example.specification.EmployeeSpecification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommonService {
	
	
    private final EmployeeRepository empRepo;
    private final DepartmentRepository deptRepo;

    public CommonService(EmployeeRepository empRepo,
    		DepartmentRepository deptRepo) {
        this.empRepo = empRepo;
        this.deptRepo = deptRepo;
    }
    
    /** Approach 1: Creating filed based specification and adding 'AND'/'OR' as needed.
     */
    @SuppressWarnings("removal")
	public List<DepartmentDto> fetchDepartments(String location, BigDecimal budget, String createdBy,
    		LocalDateTime startDate, LocalDateTime endDate){
    	log.info("Start of fetchDepartments with params: location {}, budget {}, createdBy {},"
    			+ " startDate {}, endDate {}", location, budget, createdBy, startDate, endDate);
    	
    	Specification<Department> locationSpec = DepartmentSpecification.hasLocation(location);
    	Specification<Department> createdBySpec = DepartmentSpecification.createdByLike(createdBy);
    	Specification<Department> budgetSpec = DepartmentSpecification.budgetGreaterThen(budget);
    	Specification<Department> createdBetweenSpec = DepartmentSpecification
    			.createdBetweenDates(startDate, endDate);
    	
    	Specification<Department> combinedSpec = Specification
    			.where(locationSpec)
    			.and(createdBySpec)
    			.and(budgetSpec)
    			.and(createdBetweenSpec); // only add or clause of specification is present
    	List<Department> departments = deptRepo.findAll(combinedSpec);
    	if(!CollectionUtils.isEmpty(departments)) {
    		log.info("Total records fetched: {}", departments.size());
    		return departments.stream().map(this::covertToDepartmentDto).collect(Collectors.toList());
    	}
    	return null;
    }
    
    
    /** Approach 2: Creating a specification across multiple fields.
     */
    public List<EmployeeDto> searchEmployees(String searchText) {
    	log.info("Start of searchEmployees, searchText {} ", searchText);
    	Specification<Employee> spec = EmployeeSpecification.searchByText(searchText);
    	List<Employee> employees = empRepo.findAll(spec);
    	if(!CollectionUtils.isEmpty(employees)) {
    		log.info("Total records fetched: {}", employees.size());
    		return employees.stream().map(this::covertToEmployeeDto).collect(Collectors.toList());
    	}
    	return null;
    }
    
    public List<EmployeeDto> getAllEmployeeByDepartmentCreatedBy(String username) {
    	log.info("Start of getAllEmployeeByDepartmentCreatedBy, username {} ", username);
    	Specification<Employee> spec = EmployeeSpecification.empByDeptCreatedBy(username);
    	List<Employee> employees = empRepo.findAll(spec);
    	if(!CollectionUtils.isEmpty(employees)) {
    		log.info("Total records fetched: {}", employees.size());
    		return employees.stream().map(this::covertToEmployeeDto).collect(Collectors.toList());
    	}
    	return null;
    }    
    
    /** Approach 3: Creating a dynamic specification based on fields and values.
     */    
	public List<EmployeeDto> getEmployeesByFilter(List<SearchFilter> filters) {
    	log.info("Start of getEmployeesByFilter");
    	Specification<Employee> spec = EmployeeSpecification.byFilters(filters);
    	List<Employee> employees = empRepo.findAll(spec);
    	if(!CollectionUtils.isEmpty(employees)) {
    		log.info("Total records fetched: {}", employees.size());
    		return employees.stream().map(this::covertToEmployeeDto).collect(Collectors.toList());
    	}
    	return null;
	}    
    
    /** Example of specification with pagination and sorting.
     */ 	
    public EmployeeResponse getEmployeesBySearch(String searchText, Integer page, Integer size, String sortBy, String sortOrder) {
    	log.info("Start of getEmployeesBySearch, searchText {}, pageNo {}, pageSize {}, sortBy {}, sortOrder {} ",
    			searchText, page, size, sortBy, sortOrder);
    	
    	// Client sends page=1 for the first page, but JPA uses 0-based indexing (page=0 is first).
    	int pageNo = Objects.nonNull(page) ? page.intValue()-1 : 0;
    			
    	// Setting the default page size to 10
    	int pageSize = Objects.nonNull(size) ? size.intValue() : 10;
    	
    	Sort sort = sortOrder.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
    	
    	PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
    	
    	Specification<Employee> spec = EmployeeSpecification.searchByText(searchText);
    	
    	Page<Employee> employees = empRepo.findAll(spec, pageRequest);
		log.info("Total records fetched: {}", employees.getNumberOfElements());
		return prepareEmployeeResponse(employees);
    }
	
	private EmployeeResponse prepareEmployeeResponse(Page<Employee> entities) {
		List<EmployeeDto> emps = entities.stream().map(this::covertToEmployeeDto).toList();
		
		PageMetadata pageMetadata = PageMetadata.builder()
				.pageNumber(entities.getNumber() +1)
				.pageSize(entities.getSize())
				.noOfElements(entities.getNumberOfElements())
				.totalElements(entities.getTotalPages())
				.totalPages(entities.getTotalPages())
				.build();
		
		return EmployeeResponse.builder()
				.data(emps)
				.pageMetadata(pageMetadata)
				.build();
	}    
	
	
    private EmployeeDto covertToEmployeeDto(Employee emp) {
    	return EmployeeDto.builder()
    	.id(emp.getId())
    	.createdAt(emp.getCreatedAt())
    	.createdBy(emp.getCreatedBy().getUsername())
    	.createdById(emp.getCreatedBy().getId())
    	.departmentId(emp.getDepartment().getId())
    	.departmentName(emp.getDepartment().getDepartmentName())
    	.email(emp.getEmail())
    	.firstName(emp.getFirstName())
    	.hireDate(emp.getHireDate())
    	.lastName(emp.getLastName())
    	.salary(emp.getSalary())
    	.build();
    }
    
    private DepartmentDto covertToDepartmentDto(Department emp) {
    	return DepartmentDto.builder()
    	.id(emp.getId())
    	.departmentName(emp.getDepartmentName())
    	.location(emp.getLocation())
    	.manager(emp.getManager())
    	.budget(emp.getBudget())
    	.createdAt(emp.getCreatedAt())
    	.createdBy(emp.getCreatedBy().getUsername())
    	.build();
    }



	
}
