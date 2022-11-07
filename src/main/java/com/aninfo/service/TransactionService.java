package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.InsufficientFundsException;
import com.aninfo.exceptions.InvalidTransactionTypeException;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.model.TransactionType;
import com.aninfo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Transaction createTransaction(Long cbu, TransactionType type, Double sum) {

        Transaction transaction = new Transaction(cbu, type, sum);
        Account acount = accountService.findById(cbu).get();

        if (transaction.getType() == TransactionType.DEPOSIT) {

            transaction.setSum(this.applyPromo(transaction.getSum()));
            acount.setBalance(acount.getBalance() + transaction.getSum());

        } else if (transaction.getType() == TransactionType.WITHDRAWAL) {
            acount.setBalance(acount.getBalance() - transaction.getSum());
        } else {
            throw new InvalidTransactionTypeException("Invalid transaction type");
        }

        return transactionRepository.save(transaction);
    }

    private Double applyPromo(Double sum){
        if (sum >= 2000){
            Double promotional = sum * 0.1;
            sum += (promotional <= 500) ? promotional : 500.00;
        }
        return sum;
    }

    public Collection<Transaction> getTransactions(long cbu) {
        return transactionRepository.getTransactionsByCbu(cbu);
    }

    public Optional<Transaction> getTransaction(Long transactionId){
        return transactionRepository.getTransactionById(transactionId);
    }

    public void deleteTransaction(Long transactionId){

        transactionRepository.deleteById(transactionId);
    }


}