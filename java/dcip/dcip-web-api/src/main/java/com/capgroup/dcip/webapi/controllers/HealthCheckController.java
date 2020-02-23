package com.capgroup.dcip.webapi.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("healthcheck")
public class HealthCheckController {
	@GetMapping
	public boolean get() {
		return true;
	}
}
