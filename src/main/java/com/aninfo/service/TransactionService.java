package com.aninfo.service;

import com.aninfo.model.Transaction;
import com.aninfo.model.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionService {


    @Autowired
    private AccountService accountService;

    @Transactional
    public Transaction createTransaction(Transaction transaction) {

        if (transaction.getType() == TransactionType.DEPOSIT) {
            accountService.deposit(transaction.getCbu(), transaction.getSum());

        } else if (transaction.getType() == TransactionType.WITHDRAWAL) {
            accountService.withdraw(transaction.getCbu(), transaction.getSum());
        }
        
        return transaction;
    }

}