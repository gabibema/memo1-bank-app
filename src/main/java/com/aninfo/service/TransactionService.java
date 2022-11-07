package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.InsufficientFundsException;
import com.aninfo.model.Transaction;
import com.aninfo.model.TransactionType;
import com.aninfo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionService {

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Transaction createTransaction(Transaction transaction) {

        if (transaction.getType() == TransactionType.DEPOSIT) {
            if (transaction.getSum() <= 0){
                throw new DepositNegativeSumException("Cannot deposit negative sum");
            }

            transaction.setSum(this.applyPromo(transaction.getSum()));
            accountService.deposit(transaction.getCbu(), transaction.getSum());

        } else if (transaction.getType() == TransactionType.WITHDRAWAL) {
            if (transaction.getSum() > accountService.findById(transaction.getCbu()).get().getBalance()){
                throw new InsufficientFundsException("Cannot withdraw over balance");
            }
            accountService.withdraw(transaction.getCbu(), transaction.getSum());
        }
        
        return transactionRepository.save(transaction);
    }

    private Double applyPromo(Double sum){
        if (sum >= 2000){
            Double promotional = sum * 0.1;
            if (promotional > 500) promotional = 500.00;
            sum += promotional;
        }
        return sum;
    }
    
    public Collection<Transaction> getTransactions(long cbu) {
        return transactionRepository.getTransactionsByCbu(cbu);
    }

}