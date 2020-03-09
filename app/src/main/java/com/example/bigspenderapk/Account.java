package com.example.bigspenderapk;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Account implements Serializable {
    private ArrayList<Transaction> transactions;
    private float actualBalance;
    String defaultCurrency;
    String mainCurrency;
    protected LatestExchangeRate latest;

    public void setLatest(LatestExchangeRate latest) {
        this.latest = latest;
    }

    public LatestExchangeRate getLatest() {
        return latest;
    }


    public Account(String _defaultCurrency,String _mainCurrency){
        defaultCurrency=_defaultCurrency;
        mainCurrency=_mainCurrency;
        actualBalance=0;
        latest=null;
        transactions=new ArrayList<Transaction>();
    }
    public int updateBalance() throws IOException, connectionException {
        int result=0;
        for(Transaction a:transactions){
            if(!a.includedInBalance){
                float rateTmp=CurrencyConverter.getCurrencyExchangeRate(a.currency,mainCurrency);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(rateTmp!=0){
                    if(a.type== Transaction.transactionType.INCOME){
                        setActualBalance(getActualBalance()+a.value*rateTmp);
                    }else{
                        setActualBalance(getActualBalance()-a.value*rateTmp);
                    }
                    a.includedInBalance=true;
                }else{
                    result=-1;
                    break;
                }
            }
        }
        return result;
    }

    public float getActualBalance() {
        return actualBalance;
    }

    public void setActualBalance(float actualBalance) {
        this.actualBalance = actualBalance;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getDefaultCurrency(){
        return this.defaultCurrency;
    }
    public void newTransaction(Transaction transaction){
        transactions.add(0,transaction);
    }
}
