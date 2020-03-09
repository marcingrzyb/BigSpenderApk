package com.example.bigspenderapk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.text.InputType.TYPE_CLASS_DATETIME;
import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;

public class TransActivity extends AppCompatActivity {
    Account acc;
    ArrayAdapter<Transaction> adapter;
    private float exchangeRate=0;
    Transaction.transactionType transTypeFinal;
    boolean notAdded=false;
    boolean dateChecked=true;
    public boolean getdateChecked(){
        return dateChecked;
    }
    public void setdateChecked(boolean setTo){
        dateChecked=setTo;
    }
    boolean valueChecked=false;
    public boolean getvalueChecked(){
        return valueChecked;
    }
    public void setvalueChecked(boolean setTo){
        valueChecked=setTo;
    }
    TextView balanceShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);

        balanceShow = findViewById(R.id.showBalance);

        ArrayAdapter<CharSequence> adapterChar = ArrayAdapter.createFromResource(this,
                R.array.Currencies, android.R.layout.simple_spinner_item);
        final Spinner spinnerDefault = new Spinner(this);
        final Spinner spinnerMain = new Spinner(this);

        spinnerDefault.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        spinnerDefault.setAdapter(adapterChar);

        spinnerMain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        spinnerMain.setAdapter(adapterChar);

        final TextView choiceTextFirst=(TextView) new TextView(this);
        choiceTextFirst.setText(R.string.choiceText1);

        final TextView choiceTextSecond=(TextView) new TextView(this);
        choiceTextSecond.setText(R.string.choiceText2);

        final LinearLayout containerCurrenciesSet= new LinearLayout(this);
        containerCurrenciesSet.setOrientation(LinearLayout.VERTICAL);
        containerCurrenciesSet.addView(choiceTextFirst);
        containerCurrenciesSet.addView(spinnerMain);
        containerCurrenciesSet.addView(choiceTextSecond);
        containerCurrenciesSet.addView(spinnerDefault);


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(containerCurrenciesSet);
        builder.setTitle("Wybierz odpowiednie waluty");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String defaultCurrency = spinnerDefault.getSelectedItem().toString();
                Log.d("wybor spinner",defaultCurrency);
                String mainCurrency = spinnerMain.getSelectedItem().toString();
                Log.d("wybor spinner main",defaultCurrency);
                acc=new Account(defaultCurrency,mainCurrency);
                ArrayList<Transaction> list = acc.getTransactions();
                ListView lv = (ListView) findViewById(R.id.transList);
                TextView empty = (TextView) new TextView(getBaseContext());
                empty.setText("No Transactions :(");
                lv.setEmptyView(empty);
                adapter = new ArrayAdapter<Transaction>(getBaseContext(), android.R.layout.simple_list_item_1, list);
                lv.setAdapter(adapter);
                Log.d("list1",Boolean.toString(acc==null));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("back",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //startActivity(new Intent(getBaseContext(),MainActivity.class));
                finish();
            }
        });

        File dataFile =this.getFileStreamPath("AccountValues.ser");
        if(dataFile.exists()) {

            try {
                FileInputStream fileIn = this.openFileInput("AccountValues.ser");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                acc = (Account) in.readObject();
                if(acc!=null) {
                    ArrayList<Transaction> list = acc.getTransactions();
                    ListView lv = (ListView) findViewById(R.id.transList);
                    TextView empty = (TextView) new TextView(getBaseContext());
                    empty.setText("No Transactions :(");
                    lv.setEmptyView(empty);
                    adapter = new ArrayAdapter<Transaction>(getBaseContext(), android.R.layout.simple_list_item_1, list);
                    lv.setAdapter(adapter);
                    Log.d("serializeload", "wczytano z pliku.");
                    balanceShow.setText("Bilans: " + Float.toString(acc.getActualBalance()) + " " + acc.mainCurrency);
                    adapter.notifyDataSetChanged();

                    in.close();
                    fileIn.close();
                }
                else{
                    builder.create().show();
                }

            } catch (IOException i) {
                i.printStackTrace();
                return;
            } catch (ClassNotFoundException c) {
                Log.d("Load", "Account class not found");
                c.printStackTrace();
                return;
            }
        }
        else {
            builder.create().show();
            try {
                if (dataFile.createNewFile())
                {
                    Log.d("file","File is created!");
                } else {
                    Log.d("file","File already exists.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        Log.d("list2",Boolean.toString(acc==null));
    }

    @Override
    protected void onStop() {
        File dataFile =new File(this.getFilesDir().getPath() + "AccountValues.ser");
        try {
            Log.d("path",this.getFilesDir().getPath());
            FileOutputStream fileOut = this.openFileOutput("AccountValues.ser",this.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(acc);
            out.close();
            fileOut.close();
            Log.d("Stop","Serialized data is saved AccountValues.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
        super.onStop();

    }
    int updateResult=-1;
    public void onClickCheckBalance(View view){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    updateResult = acc.updateBalance();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (connectionException e) {
                    e.printStackTrace();
                }
            }
        });

        if(updateResult==-1){
            Toast.makeText(getBaseContext(),"failed to uodate",Toast.LENGTH_SHORT).show();
        }
        else{
            balanceShow.setText("Bilans: " + Float.toString(acc.getActualBalance()) + " " + acc.mainCurrency);
            adapter.notifyDataSetChanged();
        }
    }

    public void onClickAddTrans(View view){
        final Spinner spinnerType = new Spinner(this);
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this,
                R.array.transType, android.R.layout.simple_spinner_item);
        spinnerType.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        spinnerType.setAdapter(adapterType);
        spinnerType.setSelection(1);

        final EditText transValue= (EditText) new EditText(this);
        transValue.setInputType(TYPE_CLASS_NUMBER|TYPE_NUMBER_FLAG_DECIMAL);
        transValue.setHint("Podaj wartość transakcji");

        String defaultCurr=null;
        if(acc!=null){
            defaultCurr=acc.getDefaultCurrency();
        }
        ArrayAdapter<CharSequence> adapterCurrency = ArrayAdapter.createFromResource(this,
                R.array.Currencies, android.R.layout.simple_spinner_item);
        final Spinner spinnerCurrency = new Spinner(this);
        spinnerCurrency.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        spinnerCurrency.setAdapter(adapterCurrency);
        if(defaultCurr!=null) {
            spinnerCurrency.setSelection(adapterCurrency.getPosition(defaultCurr));
        }

        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String currencyFrom;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    new convertCurrency().execute(spinnerCurrency.getSelectedItem().toString(), acc.mainCurrency);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        final EditText transDate=(EditText) new EditText(this);
        transDate.setInputType(TYPE_CLASS_DATETIME);
        transDate.setText(currentDate);

        final EditText transInfo=(EditText) new EditText(this);
        transInfo.setInputType(TYPE_CLASS_TEXT);
        transInfo.setHint("Czego dotyczy transakcja?");

        final TextView warning=(TextView) new TextView(this);
        warning.setTextColor(Color.RED);


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final LinearLayout container= new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);

        container.addView(spinnerType);
        container.addView(transValue);
        container.addView(spinnerCurrency);
        container.addView(transDate);
        container.addView(transInfo);
        container.addView(warning);

        builder.setView(container);
        builder.setTitle("Podaj dane transakcji");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Date transDateFinal=null;
                Log.d("datacheckbefore format",transDate.getText().toString());
                try {
                    transDateFinal=new SimpleDateFormat("dd-MM-yyyy").parse(transDate.getText().toString());
                    Log.d("pozamianie",transDateFinal.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(spinnerType.getSelectedItem().toString().equals("OUTCOME")){
                    transTypeFinal = Transaction.transactionType.OUTCOME;
                }
                else{
                    transTypeFinal = Transaction.transactionType.INCOME;
                }
                Log.d("transactiontype", transTypeFinal.toString());
                if(acc!=null) {
                    Log.d("DataCheck",transDateFinal.toString());
                    acc.newTransaction(new Transaction(Float.parseFloat(transValue.getText().toString()), transTypeFinal, spinnerCurrency.getSelectedItem().toString(), transInfo.getText().toString(), transDateFinal));
                    adapter.notifyDataSetChanged();
                    Log.d("transaction", "dodano");
                }
                Log.d("rate",Float.toString(exchangeRate));

                    if (exchangeRate != 0) {
                        if (transTypeFinal.equals(Transaction.transactionType.INCOME)) {
                            acc.setActualBalance(acc.getActualBalance() + (exchangeRate * Float.parseFloat(transValue.getText().toString())));
                            balanceShow.setText("Bilans: " + Float.toString(acc.getActualBalance()) + " " + acc.mainCurrency);
                        } else {
                            acc.setActualBalance(acc.getActualBalance() - exchangeRate * Float.parseFloat(transValue.getText().toString()));
                            balanceShow.setText("Bilans: " + Float.toString(acc.getActualBalance()) + " " + acc.mainCurrency);
                        }
                    } else {
                        if (acc.getLatest() != null) {
                            if (transTypeFinal.equals(Transaction.transactionType.INCOME)) {
                                if (acc.mainCurrency.equals(acc.getLatest().to) && spinnerCurrency.getSelectedItem().toString().equals(acc.getLatest().from)) {
                                    acc.setActualBalance(acc.getActualBalance() + (acc.getLatest().rate * Float.parseFloat(transValue.getText().toString())));
                                    balanceShow.setText("Bilans: " + Float.toString(acc.getActualBalance()) + " " + acc.mainCurrency);
                                }
                                else{
                                    Log.d("kurs","brak");
                                    balanceShow.setText("Bilans: " + Float.toString(acc.getActualBalance()) + " " + acc.mainCurrency);
                                    acc.getTransactions().get(0).includedInBalance=false;
                                }

                            } else {
                                if (acc.mainCurrency.equals(acc.getLatest().to) && spinnerCurrency.getSelectedItem().toString().equals(acc.getLatest().from)) {
                                    acc.setActualBalance(acc.getActualBalance() - (acc.getLatest().rate * Float.parseFloat(transValue.getText().toString())));
                                    balanceShow.setText("Bilans: " + Float.toString(acc.getActualBalance()) + " " + acc.mainCurrency);
                                }
                                else{
                                    Log.d("kurs","brak");
                                    balanceShow.setText("Bilans: " + Float.toString(acc.getActualBalance()) + " " + acc.mainCurrency);
                                    acc.getTransactions().get(0).includedInBalance=false;
                                }

                            }
                        } else{
                            Log.d("kurs","brak");
                            balanceShow.setText("Bilans: " + Float.toString(acc.getActualBalance()) + " " + acc.mainCurrency);
                            acc.getTransactions().get(0).includedInBalance=false;
                        }
                    }
                setdateChecked(true);
                setvalueChecked(false);
                dialog.dismiss();

            }
        });

        final AlertDialog dialog = builder.create();


        transDate.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(!transDate.getText().toString().matches("^([0-2][0-9]|(3)[0-1])(\\-)(((0)[0-9])|((1)[0-2]))(\\-)\\d{4}$")) {
                    if(!warning.getText().toString().contains("Podaj datę w formacie dd-mm-yyyy")) {
                        warning.setText(warning.getText().toString() + "\n Podaj datę w formacie dd-mm-yyyy");
                    }
                    setdateChecked(false);
                    if (getdateChecked() == true && getvalueChecked() == true) {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    } else {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                }
                else {
                    warning.setText("");
                    setdateChecked(true);
                    if (getdateChecked() == true && getvalueChecked() == true) {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    } else {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                }
            }
        });
        transValue.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(transValue.getText().toString().length()!=0 && (transValue.getText().toString().matches("^[0-9]+\\.[0-9]+$")||transValue.getText().toString().matches("^[0-9]+$"))) {
                    setvalueChecked(true);
                    if (getdateChecked() == true && getvalueChecked() == true) {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    } else {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                }
                else{
                    setvalueChecked(false);
                    if (getdateChecked() == true && getvalueChecked() == true) {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    } else {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                }
            }
        });

        dialog.show();
        dialog.getButton((AlertDialog.BUTTON_POSITIVE)).setEnabled(false);

    }
    public void setExchangeRate(float setTo) {
        exchangeRate=setTo;
    }

    private class convertCurrency extends AsyncTask<String, Void, Float> {
        @Override
        protected Float doInBackground(String... params) {
            float exchangeRateReturn=0;
            if (params[0] != null && params[1] != null &&CurrencyConverter.netIsAvailable()) {
                try {
                    exchangeRateReturn = CurrencyConverter.getCurrencyExchangeRate(params[0], params[1]);
                    acc.setLatest(new LatestExchangeRate(params[0],params[1],exchangeRateReturn));

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (connectionException e) {
                    e.printStackTrace();
                }
            } else {
                exchangeRateReturn = 0;
            }
            return exchangeRateReturn;
        }
        @Override
        protected void onPostExecute(Float result) {
            setExchangeRate(result);
        }
    }
}
