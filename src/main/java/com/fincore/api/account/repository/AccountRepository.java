
package com.fincore.api.account.repository;
import com.fincore.api.account.model.Account;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountRepository {

    private final Map<UUID, Account> accounts =
            new ConcurrentHashMap<>();

    public Account save(Account account) {
        accounts.put(account.id(), account);
        return account;
    }

    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }

    public Optional<Account> findById(UUID id) {
        return Optional.ofNullable(accounts.get(id));
    }
}
