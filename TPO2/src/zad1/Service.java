/**
 *
 *  @author Murawski Dinhchidung S22825
 *
 */

package zad1;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Currency;
import java.util.Locale;
import java.util.Scanner;

public class Service {
    private String kraj;
    private final String key = "a01726d5e433a08a14c3f5e9361c92bd";
    private Currency currencyCode;

    public Service(String kraj){
        this.kraj=kraj;
        setCurrencyCode();
    }

    //Wypisanie pogody - DONE
    public String getWeather(String miasto) {
        String[] coord = getCoordinates(miasto);
        final String resAPI = "https://api.openweathermap.org/data/2.5/weather?lat=" +
                coord[0] + "&lon="+ coord[1] +
                "&appid=" + this.key;
        StringBuilder get = getSiteToSb(resAPI);
        return get.toString();
    }

    //kurs waluty danego kraju wobec waluty podanej jako argument - DONE
    public double getRateFor(String kod_waluty) {
        //Szukanie waluty kraju (przechodzenie po kazdym kraju i wyszukiwanie odpowiedniego)
        final String currencyCodeURL = "https://api.exchangerate.host/convert?from=" +
                this.currencyCode +
                "&to=" + kod_waluty;
        StringBuilder currencyData = getSiteToSb(currencyCodeURL);
        JsonParser parser = new JsonParser();
        JsonElement currDataArr = parser.parse(String.valueOf(currencyData));
        JsonObject currDataObject = (JsonObject) currDataArr;
        return Double.parseDouble(currDataObject.get("result").toString());
    }

    public double getNBPRate() {
        final String url1 = "https://www.nbp.pl/kursy/kursya.html";
        final String url2 = "https://www.nbp.pl/kursy/kursyb.html";
        double res;
        try{
            if(przeszukanieNBP(url1)!=-1)
                return przeszukanieNBP(url1);
            else if(przeszukanieNBP(url2)!=-1)
                return przeszukanieNBP(url2);
        } catch(Exception e){
            e.printStackTrace();
        }
        if(kraj.equals("Poland"))
            return 1.0;
        return -1;
    }

    public double przeszukanieNBP(String url) throws IOException {
        final Document document = Jsoup.connect(url).get();
        for (Element e : document.select(".nbptable tr")) {
            String pomWaluty = e.select("td.right:nth-of-type(2)").text().toLowerCase();
            if (pomWaluty.equals(""))
                continue;
            String[] kodWaluty = pomWaluty.split(" ");
            if (kodWaluty[1].equals(currencyCode.getCurrencyCode().toLowerCase()))
                return Double.parseDouble(e.select("td.right:nth-of-type(3)").text().replace(',', '.'));
        }
        return -1;
    }

    //Zdobycie currencycode z kraju - DONE
    public void setCurrencyCode() throws IllegalArgumentException{
        final String url = "https://docs.oracle.com/cd/E13214_01/wli/docs92/xref/xqisocodes.html";
        String countryCode="";
        String languageCode="";
        try{
            final Document document = Jsoup.connect(url).get();
            for(Element e : document.select("#wp1250799table1250793.table tr")){
                if(e!=null) {
                    if (e.select("td.table:nth-of-type(1)").text().toLowerCase().equals(kraj.toLowerCase())) {
                        countryCode = e.select("td.table:nth-of-type(2)").text();
                        break;
                    }
                }
            }

            Locale[] all = Locale.getAvailableLocales();
            for (Locale locale : all) {
                String country = locale.getCountry();
                if(country.equalsIgnoreCase(countryCode)){
                    languageCode=locale.getLanguage();
                    break;
                }
            }
            this.currencyCode = Currency.getInstance(new Locale(languageCode, countryCode));
        } catch(Exception e){
            throw new IllegalArgumentException();
        }
    }

    //Pobranie API ze strony do zmiennych JSON'owskich - DONE
    public StringBuilder getSiteToSb(String url){
        StringBuilder get = new StringBuilder();
        try{
            URL locAPI = new URL(url);
            URLConnection conn = locAPI.openConnection();
            conn.connect();
            Scanner read = new Scanner(conn.getInputStream());
            while(read.hasNext())
                get.append(read.nextLine());
        } catch (Exception e){
            e.printStackTrace();
        }
        return get;
    }

    //Zdobycie wspolrzednych miasta - DONE
    public String[] getCoordinates(String miasto){
        String coordAPI = "http://api.openweathermap.org/geo/1.0/direct?q="+
                miasto+ ","+ this.kraj+
                "&appid=" + this.key;
        StringBuilder get = getSiteToSb(coordAPI);
        JsonParser parser = new JsonParser();
        JsonArray data = (JsonArray) parser.parse(String.valueOf(get));
        JsonObject dataObject = (JsonObject) data.get(0);
        String[] res = new String[2];
        res[0] = dataObject.get("lat").getAsString();
        res[1] = dataObject.get("lon").getAsString();
        return res;
    }

    public Currency getCurrencyCode() {
        return currencyCode;
    }
}
