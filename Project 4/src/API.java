import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class API {

    public API() {
    }

    public float getBitcoinPrice() throws IOException{
        float price = 0;
        System.out.println("calling to api (getBitcoinPrice())");
        //If program isn't working, try the link with the API key.
        //String link = "https://api.fxratesapi.com/latest?base=BTC&api_key=...&currencies=USD";
        String link = "https://api.fxratesapi.com/latest?base=BTC&currencies=USD";

        URL urlObj = new URL(link);
        HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        System.out.println(connection.getResponseMessage() + " - Price"); //debugging stuff
        System.out.println(responseCode + " - Price");
        System.out.println(link);
        if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_NOT_MODIFIED) {
            StringBuilder sb = new StringBuilder();
            Scanner sc = new Scanner(connection.getInputStream());
            sb.append(sc.nextLine());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(sb.toString());
            JsonNode ratesNode = rootNode.path("rates");
            price = (float) (ratesNode.path("USD").asDouble());
        }
        return price;
    }

    public TreeMap<LocalDateTime, Float> getBTCPriceHistory(String accuracy, int daysBehind) throws IOException {
        System.out.println("calling to api (getBTCPriceHistory())");
        ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(5); //make sure to sync time, otherwise you'll have to backtrack 5 seconds
        //ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime startTime = endTime.minusDays(daysBehind);
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        //If program isn't working, try the link with the API key.

        //String link = "https://api.fxratesapi.com/timeseries?base=BTC&start_date="+startTime.format(timeFormat)+"&end_date="+endTime.format(timeFormat)+"&currencies=USD&accuracy="+accuracy+"&api_key=...";
        String link = "https://api.fxratesapi.com/timeseries?base=BTC&start_date="+startTime.format(timeFormat)+"&end_date="+endTime.format(timeFormat)+"&currencies=USD&accuracy="+accuracy;


        URL urlObj = new URL(link);
        HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println(connection.getResponseMessage() + " - History"); //debugging stuff
        System.out.println(responseCode + " - History");
        System.out.println(link);
        if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_NOT_MODIFIED) {

            StringBuilder sb = new StringBuilder();
            Scanner sc = new Scanner(connection.getInputStream());
            sb.append(sc.nextLine());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(sb.toString());
            JsonNode ratesNode = rootNode.path("rates");

            Map<String, Object> map = objectMapper.convertValue(ratesNode, new TypeReference<>(){});
            TreeMap<LocalDateTime, Float> btcHistory = new TreeMap<>();
            for(Map.Entry<String, Object> i : map.entrySet()){
                String dateString = i.getKey();
                LocalDateTime date = LocalDateTime.parse(dateString,timeFormat);
                Map<String, Float> map2 = objectMapper.convertValue(i.getValue(), new TypeReference<>() {});
                float bitcoinPrice = map2.get("USD");
                btcHistory.put(date,bitcoinPrice);
            }
            return btcHistory;

        }

        return new TreeMap<>();
    }



}
