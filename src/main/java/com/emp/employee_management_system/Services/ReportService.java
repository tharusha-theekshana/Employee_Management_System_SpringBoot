package com.emp.employee_management_system.Services;

import com.emp.employee_management_system.Entity.Employee;
import com.emp.employee_management_system.Repository.EmployeeRepo;
import com.emp.employee_management_system.Services.Impl.ReportServiceImpl;
import com.emp.employee_management_system.Utils.Response.ResponseHandler;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService implements ReportServiceImpl{

    @Autowired
    private EmployeeRepo employeeRepo;

    @Override
    public ResponseEntity<Object> exportReport(String reportFormat) throws FileNotFoundException, JRException {
        try {
            List<Employee> employees = employeeRepo.findAll();

            InputStream jrxmlInput = new ClassPathResource("employees.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlInput);
            JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(employees);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("createdBy", "Tharusha Theekshana");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);

            if (reportFormat != null && reportFormat.equalsIgnoreCase("pdf")) {
                String outputPath = "C:/Users/Lenovo/Documents/employee_report.pdf";
                JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            }

            return ResponseHandler.generateResponse(
                    "Report exported successfully",
                    HttpStatus.OK,
                    "Report saved at: C:/Users/Lenovo/Documents/employee_report.pdf"
            );
        } catch (Exception e) {
            return ResponseHandler.generateResponse(
                    "Something went wrong: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
            );
        }

    }
}
