package com.capgroup.dcip.webapi.controllers.reference;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.reference.capital_system.AccountModel;
import com.capgroup.dcip.app.reference.capital_system.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

/**
 * API for retrieving fund information
 */
@RestController
@RequestMapping("api/dcip/reference/accounts")
public class AccountController {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public Stream<AccountModel> find(@RequestParam(value = "matches", required = false) String matches) {
        return accountService.find(matches);
    }

    @GetMapping("/{id}")
    public AccountModel get(@PathVariable(value = "id", required = false) long id) {
        return accountService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account", Long.toString(id)));
    }

}