package com.example.bigspenderapk;

import java.util.Date;

public class RegularTransaction extends Transaction {
    int day;
    RegularTransaction(int _day, float _value, Transaction.transactionType _type, String _currency, String _transactionTarget, Date _transactionDate){
        super(_value,_type,_currency,_transactionTarget,_transactionDate);
        this.day=_day;
    }

}
