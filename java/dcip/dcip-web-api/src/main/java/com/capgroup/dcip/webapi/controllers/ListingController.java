package com.capgroup.dcip.webapi.controllers;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.reference.listing.ListingModel;
import com.capgroup.dcip.app.reference.listing.ListingService;

@RestController
@RequestMapping("api/dcip/listings")
//("isAuthenticated()")
public class ListingController {
	
	private ListingService listingService;
	
	@Autowired
	public ListingController(ListingService listingService) {
		this.listingService = listingService;
	}
	
	@GetMapping
	public Stream<ListingModel> find(@RequestParam("matches") String matches){
		return listingService.find(matches);
	}
	
	@GetMapping("/{id}")
	public ListingModel get(@PathVariable("id") String id){
		return listingService.findById(id).orElseThrow(()->new ResourceNotFoundException("Listing", id));
	}
}
