package zad1;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
    public GUI(){
        setTitle("Weather Thing");
        setSize(800,800);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3,1));

        JPanel countryInputPanel = new JPanel();
        JLabel countryLabel = new JLabel("Country:");
        JLabel cityLabel = new JLabel("City:");
        JTextField jtfCountry = new JTextField();
        JTextField jtfCity = new JTextField();
        JButton country_city = new JButton("Check");
        JLabel weatherLabel = new JLabel();
        JFXPanel jfxPanel = new JFXPanel();
        country_city.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!jtfCity.getText().equals("") && !jtfCountry.getText().equals("")){
                    try {
                        String country = jtfCountry.getText();
                        String city = jtfCity.getText();
                        Service s = new Service(country);
                        String weather = s.getWeather(city);

                        JsonParser parser = new JsonParser();
                        JsonElement data = parser.parse(weather);
                        JsonObject dataObject = data.getAsJsonObject();
                        JsonObject coord = dataObject.get("coord").getAsJsonObject();
                        JsonObject main = dataObject.get("main").getAsJsonObject();
                        weatherLabel.setText("Lon: " + coord.get("lon").getAsString() + "  ");
                        weatherLabel.setText(weatherLabel.getText() + "Lat: " + coord.get("lat").getAsString() + "  ");
                        weatherLabel.setText(weatherLabel.getText() + "Temp: " + main.get("temp").getAsString() + "  ");
                        weatherLabel.setText(weatherLabel.getText() + "Feels like: " + main.get("feels_like").getAsString() + "  ");
                        weatherLabel.setText(weatherLabel.getText() + "Temp min: " + main.get("temp_min").getAsString() + "  ");
                        weatherLabel.setText(weatherLabel.getText() + "Temp max: " + main.get("temp_max").getAsString() + "  ");
                        weatherLabel.setText(weatherLabel.getText() + "Pressure: " + main.get("pressure").getAsString() + "  ");
                        weatherLabel.setText(weatherLabel.getText() + "Humidity: " + main.get("humidity").getAsString() + "  ");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                WebView webView = new WebView();
                                WebEngine webEngine = webView.getEngine();
                                webEngine.load("https://en.wikipedia.org/wiki/" + city);
                                VBox vBox = new VBox(webView);
                                Scene scene = new Scene(vBox, 800, 800);
                                jfxPanel.setScene(scene);
                            }
                        });
                    } catch (Exception exc){
                        weatherLabel.setText("Wrong data - try again.");
                    }
                    revalidate();
                } else if(jtfCity.getText().equals("") || jtfCountry.getText().equals("")){
                    weatherLabel.setText("Wrong input - try again");
                }
            }
        });
        jtfCountry.setPreferredSize(new Dimension(100,20));
        jtfCity.setPreferredSize(new Dimension(100,20));
        countryInputPanel.add(countryLabel);
        countryInputPanel.add(jtfCountry);
        countryInputPanel.add(cityLabel);
        countryInputPanel.add(jtfCity);
        countryInputPanel.add(country_city);
        countryInputPanel.add(weatherLabel);
        add(countryInputPanel);

        JPanel exchangePanel = new JPanel();
        JLabel currLabel = new JLabel("Kod waluty kraju ktorego chcesz sprawdzic: ");
        JTextField jtfCurrency = new JTextField();
        jtfCurrency.setPreferredSize(new Dimension(50,20));
        JButton exchange = new JButton("Check");
        JLabel rateLabel = new JLabel();
        JLabel NBPRateLabel = new JLabel();
        exchange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!jtfCity.getText().equals("") && !jtfCountry.getText().equals("") && !jtfCurrency.getText().equals("")) {
                    try {
                        String country = jtfCountry.getText();
                        Service s = new Service(country);
                        double rate = s.getRateFor(jtfCurrency.getText());
                        rateLabel.setText("1 " + s.getCurrencyCode() + " = " + rate + " " + jtfCurrency.getText());
                        NBPRateLabel.setText("Kurs NBP: " + s.getNBPRate());
                    } catch (Exception exc){
                        rateLabel.setText("Wrong data - try again.");
                    }
                }
            }
        });

        exchangePanel.add(currLabel);
        exchangePanel.add(jtfCurrency);
        exchangePanel.add(exchange);
        exchangePanel.add(rateLabel);
        exchangePanel.add(NBPRateLabel);
        add(exchangePanel);

        add(jfxPanel);
        revalidate();
        setVisible(true);
    }
}
