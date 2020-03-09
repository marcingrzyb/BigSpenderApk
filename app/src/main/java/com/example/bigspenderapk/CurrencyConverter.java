package com.example.bigspenderapk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class CurrencyConverter {
    static String getResult(BufferedReader _in) throws IOException {
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = _in.readLine()) != null) {
            content.append(inputLine);
        }
        _in.close();
        String result = content.toString();
        return result;
    }
    static float getCurrencyExchangeRate(String from, String to) throws IOException, connectionException {
        String key = "8bac2c5370c2196a1a12";
        URL url = new URL("https://free.currconv.com/api/v7/convert?q=" + from + "_" + to + "&compact=ultra&apiKey=" + key);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        int status = con.getResponseCode();
        Reader streamReader = null;
        BufferedReader in;
        float wynik = 0;
        if (status > 299) {
            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            String result=getResult(in);
            return 0;
        } else {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String result = getResult(in);
            wynik = Float.parseFloat(result.substring(11, result.length() - 1));
            return wynik;
        }
    }
    static boolean netIsAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }
}