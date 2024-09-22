package com.emp.employee_management_system.Services.Impl;

import net.sf.jasperreports.engine.JRException;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;

public interface ReportServiceImpl {

    ResponseEntity<Object> exportReport(String reportFormat) throws FileNotFoundException, JRException;
}
