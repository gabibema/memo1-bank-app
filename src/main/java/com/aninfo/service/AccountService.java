package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.InsufficientFundsException;
import com.aninfo.exceptions.InvalidTransactionTypeException;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.model.TransactionType;
import com.aninfo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Collection<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long cbu) {
        return accountRepository.findById(cbu);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public void deleteById(Long cbu) {
        accountRepository.deleteById(cbu);
    }

    @Transactional
    public Account withdraw(Long cbu, Double sum) {
        Account account = accountRepository.findAccountByCbu(cbu);

        if (account.getBalance() < sum) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        transactionService.createTransaction(cbu, TransactionType.WITHDRAWAL, sum);
        return account;
    }

    @Transactional
    public Account deposit(Long cbu, Double sum) {
        Account account = accountRepository.findAccountByCbu(cbu);
        if (sum <= 0) {
            throw new DepositNegativeSumException("Cannot deposit negative sums");
        }

        transactionService.createTransaction(cbu, TransactionType.DEPOSIT, sum);
        return account;
    }

    public Optional<Transaction> getTransaction(Long transactionId) {
        return transactionService.getTransaction(transactionId);
    }

    public void deleteTransaction(Long transactionId) {
        Transaction transaction = transactionService.getTransaction(transactionId).get();
        Account account = accountRepository.findAccountByCbu(transaction.getCbu());

        Double newBalance = account.getBalance() - transaction.getSum();
        if (newBalance < 0) {
            throw new InvalidTransactionTypeException("Cannot delete transaction");
        }

        account.setBalance(newBalance);
        accountRepository.save(account);

        transactionService.deleteTransaction(transactionId);

    }
}