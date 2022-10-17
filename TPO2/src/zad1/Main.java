/**
 *
 *  @author Murawski Dinhchidung S22825
 *
 */

package zad1;


public class Main {
  public static void main(String[] args) {
    Service s = new Service("Afghanistan");
    String weatherJson = s.getWeather("London");
    System.out.println(weatherJson);
    Double rate1 = s.getRateFor("USD");
    System.out.println(rate1);
    Double rate2 = s.getNBPRate();
    System.out.println(rate2);
    // ...
    // część uruchamiająca GUI
    GUI gui = new GUI();
  }
}
