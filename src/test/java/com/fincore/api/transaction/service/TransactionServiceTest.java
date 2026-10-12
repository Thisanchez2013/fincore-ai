
package com.fincore.api.transaction.service;

import com.fincore.api.account.model.Account;
import com.fincore.api.account.model.AccountType;
import com.fincore.api.account.service.AccountService;
import com.fincore.api.transaction.dto.CreateTransactionRequest;
import com.fincore.api.transaction.model.Transaction;
import com.fincore.api.transaction.model.TransactionType;
import com.fincore.api.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;


    // TESTE 1: Receita deve aumentar o saldo
    @Test
    void shouldIncreaseAccountBalanceWhenCreatingIncome() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();

        Account account = new Account(
                accountId,
                "Conta Principal",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        CreateTransactionRequest request = new CreateTransactionRequest(
                "Salario",
                new BigDecimal("500.00"),
                TransactionType.INCOME,
                accountId
        );

        // Simular a busca da conta
        when(accountService.findById(accountId))
                .thenReturn(account);

        // Simular o salvamento da transacao
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT: executar a receita
        transactionService.create(request);

        // ASSERT: verificar o saldo final
        assertEquals(
                0,
                new BigDecimal("1500.00").compareTo(account.getBalance())
        );

        // Verificar se a transacao foi salva
        verify(transactionRepository)
                .save(any(Transaction.class));
    }


    // TESTE 2: Despesa deve diminuir o saldo
    @Test
    void shouldDecreaseAccountBalanceWhenCreatingExpense() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();

        Account account = new Account(
                accountId,
                "Conta Principal",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        CreateTransactionRequest request = new CreateTransactionRequest(
                "Supermercado",
                new BigDecimal("300.00"),
                TransactionType.EXPENSE,
                accountId
        );

        // Simular a busca da conta
        when(accountService.findById(accountId))
                .thenReturn(account);

        // Simular o salvamento da transacao
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT: executar a despesa
        transactionService.create(request);

        // ASSERT: verificar o saldo final
        assertEquals(
                0,
                new BigDecimal("700.00").compareTo(account.getBalance())
        );

        // Verificar se a transacao foi salva
        verify(transactionRepository)
                .save(any(Transaction.class));
    }


    // TESTE 3: Rejeitar despesa com saldo insuficiente
    @Test
    void shouldRejectExpenseWhenAccountHasInsufficientBalance() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();

        Account account = new Account(
                accountId,
                "Conta Principal",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        CreateTransactionRequest request = new CreateTransactionRequest(
                "Compra de notebook",
                new BigDecimal("1500.00"),
                TransactionType.EXPENSE,
                accountId
        );

        when(accountService.findById(accountId))
                .thenReturn(account);

        // ACT: tentar executar uma despesa maior que o saldo
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.create(request)
        );

        // ASSERT: verificar o erro retornado pelo service
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        assertEquals(
                "Saldo insuficiente",
                exception.getReason()
        );

        // O saldo nao pode ser alterado
        assertEquals(
                0,
                new BigDecimal("1000.00").compareTo(account.getBalance())
        );

        // Nenhuma transacao pode ser salva
        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    // TESTE 4: Rejeitar transacao com valor zero
    @Test
    void shouldRejectTransactionWithZeroAmount() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();

        CreateTransactionRequest request = new CreateTransactionRequest(
                "Transacao invalida",
                new BigDecimal("0.00"),
                TransactionType.INCOME,
                accountId
        );

        // ACT: tentar criar uma transacao invalida
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.create(request)
        );

        // ASSERT: verificar o erro
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        // A conta nao deve ser consultada
        verify(accountService, never())
                .findById(any(UUID.class));

        // Nenhuma transacao deve ser salva
        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    // TESTE 5: Rejeitar transacao com valor negativo
    @Test
    void shouldRejectTransactionWithNegativeAmount() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();

        CreateTransactionRequest request = new CreateTransactionRequest(
                "Transacao invalida",
                new BigDecimal("-100.00"),
                TransactionType.EXPENSE,
                accountId
        );

        // ACT: tentar criar uma transacao invalida
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.create(request)
        );

        // ASSERT: verificar o erro
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        // A conta nao deve ser consultada
        verify(accountService, never())
                .findById(any(UUID.class));

        // Nenhuma transacao deve ser salva
        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    // TESTE 6: Rejeitar transacao com descricao vazia
    @Test
    void shouldRejectTransactionWithBlankDescription() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();

        CreateTransactionRequest request = new CreateTransactionRequest(
                "   ",
                new BigDecimal("100.00"),
                TransactionType.INCOME,
                accountId
        );

        // ACT: tentar criar uma transacao invalida
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.create(request)
        );

        // ASSERT: verificar o erro
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        // Nenhuma conta deve ser consultada
        verify(accountService, never())
                .findById(any(UUID.class));

        // Nenhuma transacao deve ser salva
        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    // TESTE 7: Rejeitar transacao sem tipo
    @Test
    void shouldRejectTransactionWithoutType() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();

        CreateTransactionRequest request = new CreateTransactionRequest(
                "Pagamento",
                new BigDecimal("100.00"),
                null,
                accountId
        );

        // ACT: tentar criar uma transacao invalida
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.create(request)
        );

        // ASSERT: verificar o erro
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        // Nenhuma conta deve ser consultada
        verify(accountService, never())
                .findById(any(UUID.class));

        // Nenhuma transacao deve ser salva
        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    // TESTE 8: Rejeitar transacao sem conta
    @Test
    void shouldRejectTransactionWithoutAccountId() {

        // ARRANGE: preparar os dados
        CreateTransactionRequest request = new CreateTransactionRequest(
                "Pagamento",
                new BigDecimal("100.00"),
                TransactionType.EXPENSE,
                null
        );

        // ACT: tentar criar uma transacao invalida
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.create(request)
        );

        // ASSERT: verificar o erro
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        // Nenhuma conta deve ser consultada
        verify(accountService, never())
                .findById(any(UUID.class));

        // Nenhuma transacao deve ser salva
        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    // TESTE 9: Rejeitar transacao sem valor
    @Test
    void shouldRejectTransactionWithoutAmount() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();

        CreateTransactionRequest request = new CreateTransactionRequest(
                "Pagamento",
                null,
                TransactionType.EXPENSE,
                accountId
        );

        // ACT: tentar criar uma transacao invalida
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.create(request)
        );

        // ASSERT: verificar o erro
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        // Nenhuma conta deve ser consultada
        verify(accountService, never())
                .findById(any(UUID.class));

        // Nenhuma transacao deve ser salva
        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    // TESTE 10: Buscar transacao existente pelo ID
    @Test
    void shouldFindTransactionById() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        Account account = new Account(
                accountId,
                "Conta Principal",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        Transaction transaction = new Transaction(
                transactionId,
                "Supermercado",
                new BigDecimal("150.00"),
                TransactionType.EXPENSE,
                LocalDateTime.now(),
                account
        );

        // Simular a busca da transacao
        when(transactionRepository.findById(transactionId))
                .thenReturn(Optional.of(transaction));

        // ACT: buscar a transacao pelo ID
        Transaction result = transactionService.findById(transactionId);

        // ASSERT: verificar o resultado
        assertEquals(transactionId, result.getId());
        assertEquals("Supermercado", result.getDescription());

        // Verificar se o repositorio foi consultado
        verify(transactionRepository).findById(transactionId);
    }


    // TESTE 11: Rejeitar busca de transacao inexistente
    @Test
    void shouldThrowNotFoundWhenTransactionDoesNotExist() {

        // ARRANGE: preparar os dados
        UUID transactionId = UUID.randomUUID();

        // Simular uma busca sem resultado
        when(transactionRepository.findById(transactionId))
                .thenReturn(Optional.empty());

        // ACT: tentar buscar uma transacao inexistente
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.findById(transactionId)
        );

        // ASSERT: verificar o erro
        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        // Verificar se o repositorio foi consultado
        verify(transactionRepository)
                .findById(transactionId);
    }

    // TESTE 12: Listar transacoes com paginacao
    @Test
    void shouldFindAllTransactionsWithPagination() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        Account account = new Account(
                accountId,
                "Conta Principal",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        Transaction transaction = new Transaction(
                transactionId,
                "Supermercado",
                new BigDecimal("150.00"),
                TransactionType.EXPENSE,
                LocalDateTime.now(),
                account
        );

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> transactionPage = new PageImpl<>(
                List.of(transaction),
                pageable,
                1
        );

        // Simular a busca paginada no repositorio
        when(transactionRepository.findAll(pageable))
                .thenReturn(transactionPage);

        // ACT: listar transacoes sem filtros
        Page<Transaction> result = transactionService.findAll(
                null,
                null,
                pageable
        );

        // ASSERT: verificar o resultado
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(transactionId, result.getContent().get(0).getId());

        // Verificar se o repositorio foi consultado
        verify(transactionRepository).findAll(pageable);
    }

    // TESTE 13: Filtrar transacoes por conta
    @Test
    void shouldFindTransactionsByAccountId() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        Account account = new Account(
                accountId,
                "Conta Principal",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        Transaction transaction = new Transaction(
                transactionId,
                "Supermercado",
                new BigDecimal("150.00"),
                TransactionType.EXPENSE,
                LocalDateTime.now(),
                account
        );

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> transactionPage = new PageImpl<>(
                List.of(transaction),
                pageable,
                1
        );

        // Simular a busca filtrada por conta
        when(transactionRepository.findByAccountId(accountId, pageable))
                .thenReturn(transactionPage);

        // ACT: buscar transacoes da conta
        Page<Transaction> result = transactionService.findAll(
                accountId,
                null,
                pageable
        );

        // ASSERT: verificar o resultado
        assertEquals(1, result.getTotalElements());
        assertEquals(transactionId, result.getContent().get(0).getId());
        assertEquals(accountId, result.getContent().get(0).getAccount().getId());

        // Verificar se o filtro foi aplicado
        verify(transactionRepository)
                .findByAccountId(accountId, pageable);
    }

    // TESTE 14: Filtrar transacoes por tipo
    @Test
    void shouldFindTransactionsByType() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        Account account = new Account(
                accountId,
                "Conta Principal",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        Transaction transaction = new Transaction(
                transactionId,
                "Supermercado",
                new BigDecimal("150.00"),
                TransactionType.EXPENSE,
                LocalDateTime.now(),
                account
        );

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> transactionPage = new PageImpl<>(
                List.of(transaction),
                pageable,
                1
        );

        // Simular a busca filtrada por tipo
        when(transactionRepository.findByType(
                TransactionType.EXPENSE,
                pageable
        )).thenReturn(transactionPage);

        // ACT: buscar apenas despesas
        Page<Transaction> result = transactionService.findAll(
                null,
                TransactionType.EXPENSE,
                pageable
        );

        // ASSERT: verificar o resultado
        assertEquals(1, result.getTotalElements());
        assertEquals(transactionId, result.getContent().get(0).getId());
        assertEquals(TransactionType.EXPENSE, result.getContent().get(0).getType());

        // Verificar se o filtro foi aplicado
        verify(transactionRepository)
                .findByType(TransactionType.EXPENSE, pageable);
    }

    // TESTE 15: Filtrar transacoes por conta e tipo
    @Test
    void shouldFindTransactionsByAccountIdAndType() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        Account account = new Account(
                accountId,
                "Conta Principal",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        Transaction transaction = new Transaction(
                transactionId,
                "Supermercado",
                new BigDecimal("150.00"),
                TransactionType.EXPENSE,
                LocalDateTime.now(),
                account
        );

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> transactionPage = new PageImpl<>(
                List.of(transaction),
                pageable,
                1
        );

        // Simular a busca com os dois filtros
        when(transactionRepository.findByAccountIdAndType(
                accountId,
                TransactionType.EXPENSE,
                pageable
        )).thenReturn(transactionPage);

        // ACT: buscar despesas da conta
        Page<Transaction> result = transactionService.findAll(
                accountId,
                TransactionType.EXPENSE,
                pageable
        );

        // ASSERT: verificar o resultado
        assertEquals(1, result.getTotalElements());
        assertEquals(transactionId, result.getContent().get(0).getId());
        assertEquals(accountId, result.getContent().get(0).getAccount().getId());
        assertEquals(TransactionType.EXPENSE, result.getContent().get(0).getType());

        // Verificar se os dois filtros foram aplicados
        verify(transactionRepository).findByAccountIdAndType(
                accountId,
                TransactionType.EXPENSE,
                pageable
        );
    }

    // TESTE 16: Retornar lista vazia de transacoes
    @Test
    void shouldReturnEmptyPageWhenNoTransactionsExist() {

        // ARRANGE: preparar os dados
        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> emptyPage = Page.empty(pageable);

        when(transactionRepository.findAll(pageable))
                .thenReturn(emptyPage);

        // ACT: buscar transacoes
        Page<Transaction> result = transactionService.findAll(
                null,
                null,
                pageable
        );

        // ASSERT: verificar o resultado
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());

        // Verificar se o repositorio foi consultado
        verify(transactionRepository).findAll(pageable);
    }

    // TESTE 17: Verificar dados da transacao consultada
    @Test
    void shouldReturnCorrectTransactionDetails() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        Account account = new Account(
                accountId,
                "Conta Principal",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        Transaction transaction = new Transaction(
                transactionId,
                "Pagamento de energia",
                new BigDecimal("250.00"),
                TransactionType.EXPENSE,
                LocalDateTime.now(),
                account
        );

        when(transactionRepository.findById(transactionId))
                .thenReturn(Optional.of(transaction));

        // ACT: buscar a transacao
        Transaction result = transactionService.findById(transactionId);

        // ASSERT: verificar os dados retornados
        assertEquals(transactionId, result.getId());
        assertEquals("Pagamento de energia", result.getDescription());
        assertEquals(
                0,
                new BigDecimal("250.00").compareTo(result.getAmount())
        );
        assertEquals(TransactionType.EXPENSE, result.getType());
        assertEquals(accountId, result.getAccount().getId());

        // Verificar a consulta
        verify(transactionRepository).findById(transactionId);
    }


    // TESTE 18: Retornar multiplas transacoes paginadas
    @Test
    void shouldReturnMultipleTransactionsWithPagination() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();

        Account account = new Account(
                accountId,
                "Conta Principal",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        Transaction transaction1 = new Transaction(
                UUID.randomUUID(),
                "Salario",
                new BigDecimal("2000.00"),
                TransactionType.INCOME,
                LocalDateTime.now(),
                account
        );

        Transaction transaction2 = new Transaction(
                UUID.randomUUID(),
                "Supermercado",
                new BigDecimal("300.00"),
                TransactionType.EXPENSE,
                LocalDateTime.now(),
                account
        );

        Pageable pageable = PageRequest.of(0, 2);

        Page<Transaction> transactionPage = new PageImpl<>(
                List.of(transaction1, transaction2),
                pageable,
                5
        );

        when(transactionRepository.findAll(pageable))
                .thenReturn(transactionPage);

        // ACT: buscar transacoes paginadas
        Page<Transaction> result = transactionService.findAll(
                null,
                null,
                pageable
        );

        // ASSERT: verificar a paginacao
        assertEquals(5, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertEquals(2, result.getSize());

        // Verificar a consulta
        verify(transactionRepository).findAll(pageable);
    }

    // TESTE 19: Rejeitar requisicao nula
    @Test
    void shouldRejectNullTransactionRequest() {

        // ACT: tentar criar uma transacao sem requisicao
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.create(null)
        );

        // ASSERT: verificar o erro
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        // Nenhum repositorio ou servico deve ser consultado
        verifyNoInteractions(accountService, transactionRepository);
    }


    // TESTE 20: Rejeitar transacao com descricao nula
    @Test
    void shouldRejectTransactionWithNullDescription() {

        // ARRANGE: preparar os dados
        CreateTransactionRequest request = new CreateTransactionRequest(
                null,
                new BigDecimal("100.00"),
                TransactionType.INCOME,
                UUID.randomUUID()
        );

        // ACT: tentar criar uma transacao invalida
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.create(request)
        );

        // ASSERT: verificar o erro
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(accountService, transactionRepository);
    }


    // TESTE 21: Remover espacos da descricao
    @Test
    void shouldTrimTransactionDescriptionWhenCreatingTransaction() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();

        Account account = new Account(
                accountId,
                "Conta Principal",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        CreateTransactionRequest request = new CreateTransactionRequest(
                "  Salario  ",
                new BigDecimal("500.00"),
                TransactionType.INCOME,
                accountId
        );

        when(accountService.findById(accountId))
                .thenReturn(account);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT: criar a transacao
        Transaction result = transactionService.create(request);

        // ASSERT: verificar a descricao
        assertEquals("Salario", result.getDescription());

        verify(transactionRepository)
                .save(any(Transaction.class));
    }


    // TESTE 22: Salvar transacao com dados corretos
    @Test
    void shouldSaveTransactionWithCorrectData() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();

        Account account = new Account(
                accountId,
                "Conta Principal",
                AccountType.CHECKING,
                new BigDecimal("1000.00")
        );

        CreateTransactionRequest request = new CreateTransactionRequest(
                "Freelance",
                new BigDecimal("750.00"),
                TransactionType.INCOME,
                accountId
        );

        when(accountService.findById(accountId))
                .thenReturn(account);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT: criar a transacao
        transactionService.create(request);

        // ASSERT: capturar a transacao enviada ao repositorio
        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction savedTransaction = captor.getValue();

        assertNotNull(savedTransaction.getId());
        assertNotNull(savedTransaction.getCreatedAt());

        assertEquals("Freelance", savedTransaction.getDescription());

        assertEquals(
                0,
                new BigDecimal("750.00").compareTo(savedTransaction.getAmount())
        );

        assertEquals(
                TransactionType.INCOME,
                savedTransaction.getType()
        );

        assertEquals(
                accountId,
                savedTransaction.getAccount().getId()
        );
    }


    // TESTE 23: Retornar pagina vazia ao filtrar por conta
    @Test
    void shouldReturnEmptyPageWhenAccountHasNoTransactions() {

        // ARRANGE: preparar os dados
        UUID accountId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> emptyPage = Page.empty(pageable);

        when(transactionRepository.findByAccountId(accountId, pageable))
                .thenReturn(emptyPage);

        // ACT: buscar transacoes da conta
        Page<Transaction> result = transactionService.findAll(
                accountId,
                null,
                pageable
        );

        // ASSERT: verificar o resultado
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());

        verify(transactionRepository)
                .findByAccountId(accountId, pageable);
    }


    // TESTE 24: Preservar a pagina solicitada
    @Test
    void shouldReturnRequestedPageNumber() {

        // ARRANGE: preparar os dados
        Pageable pageable = PageRequest.of(2, 5);

        Page<Transaction> emptyPage = new PageImpl<>(
                List.of(),
                pageable,
                20
        );

        when(transactionRepository.findAll(pageable))
                .thenReturn(emptyPage);

        // ACT: buscar a terceira pagina
        Page<Transaction> result = transactionService.findAll(
                null,
                null,
                pageable
        );

        // ASSERT: verificar a paginacao
        assertEquals(2, result.getNumber());
        assertEquals(5, result.getSize());
        assertEquals(20, result.getTotalElements());
        assertEquals(4, result.getTotalPages());

        verify(transactionRepository).findAll(pageable);
    }

}
