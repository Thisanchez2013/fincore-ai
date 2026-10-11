
package com.fincore.api.account.service;

import com.fincore.api.account.dto.CreateAccountRequest;
import com.fincore.api.account.dto.UpdateAccountRequest;
import com.fincore.api.account.model.Account;
import com.fincore.api.account.repository.AccountRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    // Criar uma conta
    public Account create(CreateAccountRequest request) {

        if (request == null
                || request.name() == null
                || request.name().isBlank()
                || request.type() == null
                || request.balance() == null
                || request.balance().compareTo(BigDecimal.ZERO) < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dados da conta invalidos"
            );
        }

        Account account = new Account(
                UUID.randomUUID(),
                request.name().trim(),
                request.type(),
                request.balance()
        );

        return repository.save(account);
    }

    // Atualizar uma conta
    public Account update(UUID id, UpdateAccountRequest request) {

        if (request == null
                || request.name() == null
                || request.name().isBlank()
                || request.type() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dados da conta invalidos"
            );
        }

        Account account = findById(id);

        account.update(
                request.name().trim(),
                request.type()
        );

        return repository.save(account);
    }

    // Excluir uma conta
    public void delete(UUID id) {

        Account account = findById(id);

        repository.delete(account);
    }

    // Listar todas as contas
    public List<Account> findAll() {
        return repository.findAll();
    }

    // Buscar uma conta pelo ID
    public Account findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Conta nao encontrada"
                ));
    }
}
