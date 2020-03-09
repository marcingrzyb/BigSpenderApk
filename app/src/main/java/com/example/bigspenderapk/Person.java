package com.example.bigspenderapk;

import java.util.Date;

public class Person {
    String firstName;
    String secondName;
    Date dateOfBirth;
    Account acc;
    private String password;
    public Person(String _firstName, String _secondName, Date _dateOfBirth, Account _acc, String _password){
        firstName=_firstName;
        secondName=_secondName;
        dateOfBirth=_dateOfBirth;
        acc=_acc;
        password=_password;
    }
    public Account getAcc(){
        return  acc;
    }
}
