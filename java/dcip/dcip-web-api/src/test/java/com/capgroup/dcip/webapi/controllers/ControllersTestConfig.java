package com.capgroup.dcip.webapi.controllers;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.capgroup.dcip.webapi.RestResponseEntityExceptionHandler;

@SpringBootTest
@Import(RestResponseEntityExceptionHandler.class)
public class ControllersTestConfig {
	
}
