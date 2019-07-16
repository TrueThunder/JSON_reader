package com.jdanhel;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

public class App {

    public static void main(String[] args) {


        Integer intMinutes = 5;

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                int runCounter = 0;
                String stringURL = "https://api.zonky.cz/loans/marketplace";


                Integer intMinutes = 5;
                runCounter++;
                startPrintOut(runCounter);

                writeOutJson(returnJson(stringURL));
                endPrintOut(intMinutes, runCounter);
            }
        }, 0, intMinutes * 60 * 1000);


    }

    private static String returnJson(String URL) {
        String result = "";
        try {
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Chyba při volání API na adrese: " + url + " , response code: "
                        + conn.getResponseCode());
            }
            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = IOUtils.toString(in, "UTF-8");
            conn.disconnect();
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void writeOutJson(String result) {

        writeOutNode(result, 0);
    }

    private static void writeOutNode(String string, Integer level) {
        String levelEdit = "";
        for (int i = 0; i <= level; i++) {
            levelEdit = levelEdit + "   ";

        }
        JSONTokener tokener = new JSONTokener(string);

        JSONArray array = new JSONArray(tokener);
        for (int i = 0; i < array.length(); i++) {
            if (level == 0) {
                System.out.println((i + 1) + ". Půjčka");
            }
            JSONObject loan = array.getJSONObject(i);
            String[] keys = JSONObject.getNames(loan);
            for (String key : keys) {

                Object value = loan.get(key);
                if (value instanceof Double || value instanceof Integer || value instanceof Long) {
                    System.out.println(levelEdit + key + ": " + value);


                } else if (value instanceof String) {
                    System.out.println(levelEdit + key + ": " + value);
                    // writeOutNode(String.valueOf(value));


                } else if (value instanceof Boolean) {
                    System.out.println(levelEdit + key + ": " + value);

                } else if (value.hashCode() == 0 || value.equals("[]")) {
                    System.out.println(levelEdit + key + ": " + value);
                } else {

                    if (loan.getJSONArray(key) instanceof JSONArray && loan.getJSONArray(key).isEmpty()) {
                        System.out.println("Prázdný");
                    } else {
                        System.out.println(levelEdit + key);
                        String str = loan.get(key).toString();
                        writeOutNode(str, 1);
                    }
                }


            }
        }
    }


    private static Timestamp getTimeStamp() {


        return new Timestamp(System.currentTimeMillis());

    }

    private static void startPrintOut(Integer runCount) {


        System.out.println("\n" + runCount + ". Spuštění: " + getTimeStamp() + "\n");

    }

    private static void endPrintOut(Integer minutes, Integer runCount) {

        System.out.println("\nDokončení " + runCount + ". spuštění " + getTimeStamp());
        System.out.println("Počet minut do spuštění dalšího volání: " + minutes);
    }
}
