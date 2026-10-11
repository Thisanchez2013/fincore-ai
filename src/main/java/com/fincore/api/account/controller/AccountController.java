
package com.fincore.api.account.controller;

import com.fincore.api.account.dto.CreateAccountRequest;
import com.fincore.api.account.dto.UpdateAccountRequest;
import com.fincore.api.account.model.Account;
import com.fincore.api.account.service.AccountService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    // Criar uma nova conta
    @PostMapping
    public ResponseEntity<Account> create(
            @RequestBody CreateAccountRequest request
    ) {
        Account account = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(account);
    }

    // Listar todas as contas
    @GetMapping
    public List<Account> findAll() {
        return service.findAll();
    }

    // Buscar uma conta pelo ID
    @GetMapping("/{id}")
    public Account findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    // Atualizar uma conta existente
    @PutMapping("/{id}")
    public ResponseEntity<Account> update(
            @PathVariable UUID id,
            @RequestBody UpdateAccountRequest request
    ) {
        Account updatedAccount = service.update(id, request);

        return ResponseEntity.ok(updatedAccount);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

}
