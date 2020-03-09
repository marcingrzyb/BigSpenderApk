package com.example.bigspenderapk;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction implements Serializable {
    public enum transactionType{
        INCOME,OUTCOME;
    }
    transactionType type;
    float value;
    String currency;
    String transactionTarget;
    Date transactionDate;
    Boolean includedInBalance;
    public Transaction(float _value,transactionType _type,String _currency,String _transactionTarget,Date _transactionDate){
        this.type=_type;
        this.value=_value;
        this.currency=_currency;
        this.transactionTarget=_transactionTarget;
        this.transactionDate=_transactionDate;
        this.includedInBalance=true;
    }
    public Transaction(){
        this.currency="PLN";
        this.type=transactionType.INCOME;
        this.value=0;
        this.transactionTarget="";
        this.transactionDate= null;
    }

    @Override
    public String toString() {
        String result=null;
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        if(type== transactionType.INCOME) {
            if (includedInBalance == true) {
                result = "Income of " + value + " " + currency + " from " + transactionTarget + " at " + formatter.format(transactionDate) + "\n";
            }
            else{
                result = "Income of " + value + " " + currency + " from " + transactionTarget + " at " + formatter.format(transactionDate) + "\n" + " Not Included in Balance" + "\n";
            }
        }
        else {
            if (includedInBalance == true) {
                result = "Outcome of " + value + " " + currency + " for " + transactionTarget + " at " + formatter.format(transactionDate) + "\n";
            }
            else{
                result = "Income of " + value + " " + currency + " from " + transactionTarget + " at " + formatter.format(transactionDate) + "\n" + " Not Included in Balance" + "\n";
            }
        }
        return result;
    }
}
