package com.emp.employee_management_system.Controllers;

import com.emp.employee_management_system.Services.ReportService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("api/v1/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    //Jasper report generate
    @GetMapping("/{format}")
    public ResponseEntity<Object> generateReport(@PathVariable String format) throws JRException, FileNotFoundException {
        return reportService.exportReport(format);
    }

}
