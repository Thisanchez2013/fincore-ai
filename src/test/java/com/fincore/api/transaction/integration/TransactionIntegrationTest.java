
package com.fincore.api.transaction.integration;

import com.fincore.api.account.model.Account;
import com.fincore.api.account.model.AccountType;
import com.fincore.api.account.repository.AccountRepository;
import com.fincore.api.transaction.model.Transaction;
import com.fincore.api.transaction.model.TransactionType;
import com.fincore.api.transaction.repository.TransactionRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class TransactionIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void shouldConnectToPostgresContainer() {

        Integer result = jdbcTemplate.queryForObject(
                "SELECT 1",
                Integer.class
        );

        assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldSaveAccountInPostgresContainer() {

        Account account = new Account(
                UUID.randomUUID(),
                "Conta Teste Integração",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        Account savedAccount = accountRepository.save(account);

        assertThat(savedAccount.getId()).isNotNull();

        Account foundAccount = accountRepository
                .findById(savedAccount.getId())
                .orElseThrow();

        assertThat(foundAccount.getName())
                .isEqualTo("Conta Teste Integração");

        assertThat(foundAccount.getBalance())
                .isEqualByComparingTo("1000.00");
    }

    @Test
    void shouldSaveTransactionInPostgresContainer() {

        Account account = new Account(
                UUID.randomUUID(),
                "Conta para Transação",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "Pagamento de supermercado",
                new BigDecimal("150.00"),
                TransactionType.EXPENSE,
                LocalDateTime.now(),
                savedAccount
        );

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        assertThat(savedTransaction.getId()).isNotNull();

        Transaction foundTransaction = transactionRepository
                .findById(savedTransaction.getId())
                .orElseThrow();

        assertThat(foundTransaction.getDescription())
                .isEqualTo("Pagamento de supermercado");

        assertThat(foundTransaction.getAmount())
                .isEqualByComparingTo("150.00");

        assertThat(foundTransaction.getType())
                .isEqualTo(TransactionType.EXPENSE);

        assertThat(foundTransaction.getAccount().getId())
                .isEqualTo(savedAccount.getId());
    }
}
