
package com.fincore.api.account.controller;

import com.fincore.api.account.dto.CreateAccountRequest;
import com.fincore.api.account.dto.UpdateAccountRequest;
import com.fincore.api.account.model.Account;
import com.fincore.api.account.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(
        name = "Contas",
        description = "Gerenciamento de contas financeiras do FinCore AI"
)
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    // Criar uma nova conta
    @Operation(
            summary = "Criar uma conta",
            description = "Cadastra uma nova conta financeira no sistema."
    )
    @PostMapping
    public ResponseEntity<Account> create(
            @Valid @RequestBody CreateAccountRequest request
    ) {
        Account account = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(account);
    }

    // Listar todas as contas
    @Operation(
            summary = "Listar contas",
            description = "Retorna todas as contas financeiras cadastradas."
    )
    @GetMapping
    public List<Account> findAll() {
        return service.findAll();
    }

    // Buscar uma conta pelo ID
    @Operation(
            summary = "Buscar conta por ID",
            description = "Consulta uma conta financeira utilizando seu identificador UUID."
    )
    @GetMapping("/{id}")
    public Account findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    // Atualizar uma conta existente
    @Operation(
            summary = "Atualizar uma conta",
            description = "Atualiza os dados de uma conta financeira existente."
    )
    @PutMapping("/{id}")
    public ResponseEntity<Account> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountRequest request
    ) {
        Account updatedAccount = service.update(id, request);

        return ResponseEntity.ok(updatedAccount);
    }

    // Excluir uma conta
    @Operation(
            summary = "Excluir uma conta",
            description = "Remove uma conta financeira existente pelo seu UUID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
