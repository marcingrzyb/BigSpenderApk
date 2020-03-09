package com.example.bigspenderapk;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class KantorActivity extends AppCompatActivity {

    String currencyFrom;
    String currencyTo;
    float exchangeRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kantor);
        Spinner spinnerFrom = (Spinner) findViewById(R.id.spinnerFrom);
        Spinner spinnerTo = (Spinner) findViewById(R.id.spinnerTo);
        EditText moneyAmmountInput = (EditText) findViewById(R.id.moneyAmmountInput);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerFrom.setSelection(2);
        spinnerTo.setAdapter(adapter);
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                currencyFrom = parent.getSelectedItem().toString();
                new convertCurrency().execute(currencyFrom, currencyTo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currencyFrom = null;
            }
        });
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                currencyTo = parent.getSelectedItem().toString();
                new convertCurrency().execute(currencyFrom, currencyTo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currencyTo = null;
            }
        });
    }

    public void onClickConvert(View view){
        Log.d("from", currencyFrom);
        Log.d("to", currencyTo);
        EditText moneyAmmountInput = (EditText) findViewById(R.id.moneyAmmountInput);
        String tmpInput = moneyAmmountInput.getText().toString();
        Float inputNum = Float.parseFloat(tmpInput);

        Log.d("rate",String.valueOf(exchangeRate));
        if (exchangeRate != 0) {
            float result = inputNum * exchangeRate;
            String finalResult = Float.toString(result);
            TextView resultField = (TextView) findViewById(R.id.ConvertResult);
            resultField.setText(finalResult);
        } else {
            String finalResult = "Something went wrong! Try Again";
            TextView resultField = findViewById(R.id.ConvertResult);
            resultField.setText(finalResult);
        }
    }

    public void setExchangeRate(float setTo) {
        exchangeRate=setTo;
    }

    private class convertCurrency extends AsyncTask<String, Void, Float> {
        @Override
        protected Float doInBackground(String... params) {
            float exchangeRateReturn=0;
            if (params[0] != null && params[1] != null) {
                try {
                    exchangeRateReturn = CurrencyConverter.getCurrencyExchangeRate(params[0], params[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (connectionException e) {
                    e.printStackTrace();
                }
            } else {
                 exchangeRateReturn = 0;
            }
            Log.d("eRR",String.valueOf(exchangeRateReturn));
            return exchangeRateReturn;
        }
        @Override
        protected void onPostExecute(Float result) {
            setExchangeRate(result);
        }
    }
}

