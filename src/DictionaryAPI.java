package com.vic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DictionaryAPI {
    private final static String baseUrl = "https://www.dictionaryapi.com/api/v3/references/spanish/json/%s?key=";
    private String url;
    private static HttpURLConnection connection;

    public DictionaryAPI(String key) {
        url = baseUrl + key;
    }

    public String define(String word) {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();

        try {
            String lookupUrl = String.format(url, word);
            URL baseUrl = new URL(lookupUrl);
            connection = (HttpURLConnection) baseUrl.openConnection();
            // setup
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }

            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

            Merriam result = parse(responseContent.toString());
            return (result != null) ? result.getDefinition() : "NA";

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return "NA";

    }

    private Merriam parse(String responseBody) {


        Merriam merriamResult = null;
        try {
            JSONArray results = new JSONArray(responseBody);

            if (results.length() > 0 &&
                    results.get(0) instanceof JSONObject) {

                merriamResult = new Merriam();

                JSONObject result = results.getJSONObject(0);
                // type
                String type = result.has("fl") ? result.getString("fl") : "NA";
                merriamResult.setType(type);
                // definition
                JSONArray defs = result.getJSONArray("shortdef");
                merriamResult.setDefinition(defs.length() > 0 ? defs.get(0).toString() : "");

                // meta
                JSONObject meta = result.getJSONObject("meta");
                merriamResult.setLanguage(meta.getString("lang"));
                merriamResult.setSource(meta.getString("src"));
                merriamResult.setOffensive(meta.getBoolean("offensive"));
            }

        } catch (org.json.JSONException jex) {
            System.out.println("Error:" + jex.getMessage() + " - " + responseBody);
        }

        return merriamResult;
    }
}