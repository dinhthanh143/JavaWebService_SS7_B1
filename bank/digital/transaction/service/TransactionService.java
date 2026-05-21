package com.bank.digital.transaction.service;

import org.springframework.stereotype.Service;



@Service
public class TransactionService {

    public boolean processPayment(String accountNumber, double amount) {

        try { Thread.sleep(150); } catch (InterruptedException e) {}

        return true;

    }

}