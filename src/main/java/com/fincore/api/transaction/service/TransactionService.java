
package com.fincore.api.transaction.service;

import com.fincore.api.account.model.Account;
import com.fincore.api.account.service.AccountService;
import com.fincore.api.transaction.dto.CreateTransactionRequest;
import com.fincore.api.transaction.model.Transaction;
import com.fincore.api.transaction.repository.TransactionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final AccountService accountService;

    public TransactionService(
            TransactionRepository repository,
            AccountService accountService
    ) {
        this.repository = repository;
        this.accountService = accountService;
    }

    // Criar uma transacao financeira
    @Transactional
    public Transaction create(CreateTransactionRequest request) {

        // Validar os dados recebidos
        if (request == null
                || request.description() == null
                || request.description().isBlank()
                || request.amount() == null
                || request.amount().compareTo(BigDecimal.ZERO) <= 0
                || request.type() == null
                || request.accountId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dados da transacao invalidos"
            );
        }

        // Buscar a conta vinculada a transacao
        Account account = accountService.findById(
                request.accountId()
        );

        BigDecimal currentBalance = account.getBalance();
        BigDecimal newBalance;

        // Calcular o novo saldo
        switch (request.type()) {

            case INCOME -> {
                newBalance = currentBalance.add(
                        request.amount()
                );
            }

            case EXPENSE -> {

                // Impedir despesas maiores que o saldo
                if (currentBalance.compareTo(request.amount()) < 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Saldo insuficiente"
                    );
                }

                newBalance = currentBalance.subtract(
                        request.amount()
                );
            }

            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de transacao invalido"
            );
        }

        // Atualizar o saldo da conta
        account.updateBalance(newBalance);

        // Criar a transacao financeira
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                request.description().trim(),
                request.amount(),
                request.type(),
                LocalDateTime.now(),
                account
        );

        // Salvar a transacao no PostgreSQL
        // O Hibernate tambem persiste a alteracao do saldo
        return repository.save(transaction);
    }

    // Listar todas as transacoes
    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        return repository.findAll();
    }

    // Buscar uma transacao pelo ID
    @Transactional(readOnly = true)
    public Transaction findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transacao nao encontrada"
                ));
    }
}
