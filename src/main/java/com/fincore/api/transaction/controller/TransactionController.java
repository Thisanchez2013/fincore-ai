
package com.fincore.api.transaction.controller;

import com.fincore.api.transaction.dto.CreateTransactionRequest;
import com.fincore.api.transaction.dto.TransactionResponse;
import com.fincore.api.transaction.model.Transaction;
import com.fincore.api.transaction.service.TransactionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    // Criar uma transacao financeira
    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @RequestBody CreateTransactionRequest request
    ) {
        Transaction transaction = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TransactionResponse.from(transaction));
    }

    // Listar todas as transacoes
    @GetMapping
    public List<TransactionResponse> findAll() {
        return service.findAll()
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    // Buscar uma transacao pelo ID
    @GetMapping("/{id}")
    public TransactionResponse findById(@PathVariable UUID id) {
        return TransactionResponse.from(
                service.findById(id)
        );
    }
}
