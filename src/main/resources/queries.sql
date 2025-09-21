CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username     VARCHAR(50) NOT NULL UNIQUE,
    email        VARCHAR(100) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
	status		 VARCHAR(50)
);

CREATE TABLE departments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  department_name VARCHAR(100) NOT NULL,
  location VARCHAR(100),
  manager VARCHAR(100),
  budget DECIMAL(12,2),
  created_by BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  
  CONSTRAINT fk_dept_createdby FOREIGN KEY (created_by) REFERENCES users(id)
);


CREATE TABLE employees (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  first_name VARCHAR(50),
  last_name VARCHAR(50),
  email VARCHAR(100) UNIQUE,
  hire_date DATE,
  department_id BIGINT,
  salary DECIMAL(10,2),
  created_by BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  
  CONSTRAINT fk_emp_dept FOREIGN KEY (department_id) REFERENCES departments(id),
  CONSTRAINT fk_emp_createdby FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE projects (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_name VARCHAR(100) NOT NULL,
  category VARCHAR(100),
  description VARCHAR(100),
  start_date DATE,
  end_date DATE,
  created_by BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  
  CONSTRAINT fk_proj_createdby FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE employee_projects (
  employee_id BIGINT,
  project_id BIGINT,
  role VARCHAR(50),
  status VARCHAR(20),
  
  CONSTRAINT pk PRIMARY KEY (employee_id, project_id),
  CONSTRAINT fk_empproj_emp FOREIGN KEY (employee_id) REFERENCES employees(id),
  CONSTRAINT fk_empproj_proj FOREIGN KEY (project_id) REFERENCES projects(id)
);


INSERT INTO users (username, email, password, status) VALUES
('raj_k', 'raj.kumar@example.com', 'hashed_pw1', 'ACTIVE'),
('anita_s', 'anita.sharma@example.com', 'hashed_pw2', 'ACTIVE'),
('vivek_r', 'vivek.rao@example.com', 'hashed_pw3', 'INACTIVE'),
('meena_p', 'meena.patel@example.com', 'hashed_pw4', 'ACTIVE'),
('arjun_m', 'arjun.mehta@example.com', 'hashed_pw5', 'ACTIVE'),
('kavita_j', 'kavita.joshi@example.com', 'hashed_pw6', 'ACTIVE');


INSERT INTO departments (department_name, location, manager, budget, created_by, created_at) VALUES
('Human Resources', 'Mumbai', 'Raj Kumar', 500000.00, 1, '2025-01-10 09:15:00'),
('Finance', 'Delhi', 'Anita Sharma', 1200000.00, 2, '2025-02-05 10:30:00'),
('IT', 'Bengaluru', 'Vivek Rao', 2000000.00, 3, '2025-03-12 14:00:00'),
('Marketing', 'Chennai', 'Meena Patel', 800000.00, 4, '2025-04-18 11:45:00'),
('Operations', 'Hyderabad', 'Arjun Mehta', 1500000.00, 5, '2025-05-21 16:20:00'),
('Sales', 'Pune', 'Kavita Joshi', 1000000.00, 6, '2025-06-25 08:50:00');


INSERT INTO employees (first_name, last_name, email, hire_date, department_id, salary, created_by, created_at) VALUES
('Amit', 'Verma', 'amit.verma@example.com', '2024-05-15', 1, 45000.00, 1, '2025-01-11 09:20:00'),
('Priya', 'Nair', 'priya.nair@example.com', '2023-11-20', 2, 60000.00, 2, '2025-02-06 11:00:00'),
('Rohan', 'Singh', 'rohan.singh@example.com', '2022-07-01', 3, 75000.00, 3, '2025-03-13 14:10:00'),
('Sneha', 'Menon', 'sneha.menon@example.com', '2021-03-25', 4, 50000.00, 4, '2025-04-19 12:00:00'),
('Deepak', 'Shah', 'deepak.shah@example.com', '2020-09-10', 5, 90000.00, 5, '2025-05-22 16:45:00'),
('Neha', 'Kapoor', 'neha.kapoor@example.com', '2024-02-18', 6, 55000.00, 6, '2025-06-26 09:00:00');


INSERT INTO projects (project_name, category, description, start_date, end_date, created_by, created_at) VALUES
('Payroll System', 'HR', 'Automation of payroll and attendance tracking', '2025-01-01', '2025-06-30', 1, '2025-01-15 09:30:00'),
('Audit Management', 'Finance', 'Tool for audit compliance and reporting', '2025-02-01', '2025-08-31', 2, '2025-02-10 10:45:00'),
('Cloud Migration', 'IT', 'Migrating infrastructure to cloud', '2025-03-01', '2025-09-30', 3, '2025-03-15 14:20:00'),
('Ad Campaign', 'Marketing', 'Digital campaign for product launch', '2025-04-01', '2025-07-15', 4, '2025-04-20 11:30:00'),
('Logistics System', 'Operations', 'Supply chain and warehouse management', '2025-05-01', '2025-10-31', 5, '2025-05-25 16:10:00'),
('CRM Upgrade', 'Sales', 'Upgrade CRM system for better lead tracking', '2025-06-01', '2025-12-31', 6, '2025-06-28 08:40:00');


INSERT INTO employee_projects (employee_id, project_id, role, status) VALUES
(1, 1, 'Analyst', 'Active'),
(2, 2, 'Lead', 'Active'),
(3, 3, 'Engineer', 'Active'),
(4, 4, 'Coordinator', 'Completed'),
(5, 5, 'Manager', 'Active'),
(6, 6, 'Support', 'Active');


