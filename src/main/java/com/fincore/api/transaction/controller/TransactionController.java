package com.fincore.api.transaction.controller;

import com.fincore.api.transaction.dto.CreateTransactionRequest;
import com.fincore.api.transaction.dto.TransactionResponse;
import com.fincore.api.transaction.model.Transaction;
import com.fincore.api.transaction.model.TransactionType;
import com.fincore.api.transaction.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(
        name = "Transações",
        description = "Gerenciamento de transações financeiras do FinCore AI"
)
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    // Criar uma transacao financeira
    @Operation(
            summary = "Criar uma transação financeira",
            description = "Registra uma receita ou despesa e atualiza "
                    + "o saldo da conta associada, respeitando "
                    + "as regras de negócio financeiras."
    )
    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @Valid @RequestBody CreateTransactionRequest request
    ) {
        Transaction transaction = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TransactionResponse.from(transaction));
    }

    // Listar transacoes com filtros e paginacao
    @Operation(
            summary = "Listar transações financeiras",
            description = "Consulta transações com filtros opcionais "
                    + "por conta e tipo, paginação e ordenação "
                    + "pela data de criação, da mais recente "
                    + "para a mais antiga."
    )
    @GetMapping
    public Page<TransactionResponse> findAll(

            @Parameter(
                    description = "UUID da conta utilizada para filtrar as transações"
            )
            @RequestParam(required = false) UUID accountId,

            @Parameter(
                    description = "Tipo da transação: INCOME (receita) ou EXPENSE (despesa)"
            )
            @RequestParam(required = false) TransactionType type,

            @Parameter(
                    description = "Número da página, começando em zero",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = "Quantidade de registros por página, entre 1 e 100",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") int size
    ) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Pagina ou tamanho invalido"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return service.findAll(accountId, type, pageable)
                .map(TransactionResponse::from);
    }

    // Buscar uma transacao pelo ID
    @Operation(
            summary = "Buscar transação por ID",
            description = "Consulta os detalhes de uma transação "
                    + "financeira utilizando seu identificador UUID."
    )
    @GetMapping("/{id}")
    public TransactionResponse findById(

            @Parameter(
                    description = "UUID da transação financeira"
            )
            @PathVariable UUID id
    ) {
        return TransactionResponse.from(
                service.findById(id)
        );
    }
}
