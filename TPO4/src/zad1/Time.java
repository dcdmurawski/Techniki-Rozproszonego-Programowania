/**
 *
 *  @author Murawski Dinhchidung S22825
 *
 */

package zad1;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Time {
    public static String passed(String from, String to){
        String res = "";
        try {
            if (from.contains("T")) {
                LocalDateTime ldtFrom = LocalDateTime.parse(from);
                LocalDateTime ldtTo = LocalDateTime.parse(to);
                ZonedDateTime zdtFrom = ZonedDateTime.of(ldtFrom, ZoneId.of("Europe/Warsaw"));
                ZonedDateTime zdtTo = ZonedDateTime.of(ldtTo, ZoneId.of("Europe/Warsaw"));


                long uplywMinut = ChronoUnit.MINUTES.between(zdtFrom, zdtTo);
                long uplywGodzin = Math.round(uplywMinut/60.0);
                long uplywDni = Math.round(uplywGodzin/24.0);

                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                DecimalFormat df = new DecimalFormat("0.00",symbols);
                double uplywTygodni = Double.parseDouble(df.format(uplywDni/7.0));

                res += "Od " + wypisanie(zdtFrom, res) + " do " + wypisanie(zdtTo, res) + "\n- mija: " +
                        uplywDni + (uplywDni==1?" dzień":" dni")+", tygodni " + uplywTygodni + "\n- godzin: " +
                        uplywGodzin + ", minut: " + uplywMinut;
                res = kalendarzowo(zdtFrom.toLocalDate(), zdtTo.toLocalDate(), res);
            } else {
                LocalDate ldFrom = LocalDate.parse(from);
                LocalDate ldTo = LocalDate.parse(to);
                long uplywDni = ChronoUnit.DAYS.between(ldFrom, ldTo);

                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                DecimalFormat df = new DecimalFormat("0.00", symbols);
                double uplywTygodni = Double.parseDouble(df.format(uplywDni/7.0));

                res += "Od " + wypisanie(ldFrom, res) + " do " + wypisanie(ldTo, res) + "\n- mija: " +
                        uplywDni + (uplywDni==1?" dzień":" dni") +", tygodni " + uplywTygodni;
                res = kalendarzowo(ldFrom, ldTo, res);
            }
        } catch (DateTimeException e){
            System.out.print("*** " + e);
        }
        return res;
    }

    public static String wypisanie(ZonedDateTime zdt, String res){
        int dzien = zdt.getDayOfMonth();
        String miesiacNazwa = getMonthPL(zdt.toLocalDate());
        int rok = zdt.getYear();
        int godzinaInt = zdt.getHour();
        int minutaInt = zdt.getMinute();
        String godzina, minuta;
        if(godzinaInt<10)
            godzina="0"+godzinaInt;
        else
            godzina = godzinaInt+"";
        if(minutaInt<10)
            minuta="0"+minutaInt;
        else
            minuta = minutaInt+"";
        String tydzienOd = getWeekPL(zdt.toLocalDate());
        res += dzien + " " + miesiacNazwa + " " + rok + " (" + tydzienOd + ") godz. " + godzina + ":" + minuta;
        return res;
    }

    public static String wypisanie(LocalDate ld, String res){
        int dzien = ld.getDayOfMonth();
        String miesiacNazwa = getMonthPL(ld);
        int rok = ld.getYear();
        String tydzienOd = getWeekPL(ld);
        res += dzien + " " + miesiacNazwa + " " + rok + " (" + tydzienOd + ")";
        return res;
    }


    public static String kalendarzowo(LocalDate ldFrom, LocalDate ldTo, String res){
        long uplywLat = ChronoUnit.YEARS.between(ldFrom, ldTo);
        long uplywMiesiecy = ChronoUnit.MONTHS.between(ldFrom, ldTo);
        long uplywDni = ChronoUnit.DAYS.between(ldFrom, ldTo);
        long uplywMiesiecyPeriod = Period.between(ldFrom,ldTo).getMonths();
        long uplywDniPeriod = Period.between(ldFrom,ldTo).getDays();
        if (uplywDni >= 1) {
            res += "\n- kalendarzowo: " ;
            if (uplywLat >= 1) {
                res+=uplywLat + " ";
                if (uplywLat == 1)
                    res += "rok";
                else if ((uplywLat < 5 || uplywLat > 21) && (uplywLat % 10 == 2 || uplywLat % 10 == 3 || uplywLat % 10 == 4))
                    res += "lata";
                else
                    res += "lat";
                if(uplywMiesiecyPeriod>=1)
                    res+=", ";
            }

            if (uplywMiesiecyPeriod >= 1) {
                res += uplywMiesiecyPeriod + " ";
                if (uplywMiesiecyPeriod == 1)
                    res += "miesiąc";
                else if (uplywMiesiecyPeriod < 5 && uplywMiesiecyPeriod % 10 == 2 )
                    res += "miesiące";
                else
                    res += "miesięcy";
                if(uplywDniPeriod>=1)
                    res+=", ";
            }

            if(uplywDniPeriod>=1){
                res += uplywDniPeriod + " ";
                if (uplywDniPeriod == 1)
                    res += "dzień";
                else
                    res += "dni";
            }
        }
        return res;
    }

    public static String getMonthPL(LocalDate ld){
        switch(ld.getMonth().getValue()){
            case 1:
                return "stycznia";
            case 2:
                return "lutego";
            case 3:
                return "marca";
            case 4:
                return "kwietnia";
            case 5:
                return "maja";
            case 6:
                return "czerwca";
            case 7:
                return "lipca";
            case 8:
                return "sierpnia";
            case 9:
                return "września";
            case 10:
                return "pazdźiernika";
            case 11:
                return "listopada";
            case 12:
                return "grudnia";
            default:
                return "";
        }
    }

    public static String getWeekPL(LocalDate ld){
        switch(ld.getDayOfWeek().toString()){
            case "MONDAY":
                return "poniedziałek";
            case "TUESDAY":
                return "wtorek";
            case "WEDNESDAY":
                return "środa";
            case "THURSDAY":
                return "czwartek";
            case "FRIDAY":
                return "piątek";
            case "SATURDAY":
                return "sobota";
            case "SUNDAY":
                return "niedziela";
            default:
                return "";
        }
    }
}
