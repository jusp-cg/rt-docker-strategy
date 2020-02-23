package com.capgroup.dcip.webapi.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/dcip/ping")
public class PingController {
	@GetMapping
	public boolean get() {
		return true;
	}
}
