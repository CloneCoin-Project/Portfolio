package com.cloneCoin.portfolio.client;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@Component
public class BithumbOpenApi {
    public Double TickerApi(String order_currency) {
        try {
            URL url = new URL("https://api.bithumb.com/public/ticker/" + order_currency + "_KRW");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            System.out.println("Response status: " + status);
            System.out.println(content);

            JSONObject jsonObject = new JSONObject(content.toString());

            JSONObject dataObject =  jsonObject.getJSONObject("data");

            return dataObject.getDouble("closing_price");

        }catch (Exception e){
            e.printStackTrace();
        }
        return 0.0;
    }
}
