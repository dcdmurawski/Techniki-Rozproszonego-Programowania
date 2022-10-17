/**
 *
 *  @author Murawski Dinhchidung S22825
 *
 */

package zad1;


import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tools {
    static Options createOptionsFromYaml(String fileName) throws Exception{
        File fin = new File(fileName);
        Map<String, Object> map = new HashMap<>();
        try(InputStream in = new FileInputStream(fin)){
            Yaml yaml = new Yaml();
            map = yaml.load(in);
        } catch (IOException e){
            e.printStackTrace();
        }
        return new Options(
                map.get("host").toString(),
                (int)map.get("port"),
                (boolean) map.get("concurMode"),
                (boolean)map.get("showSendRes"),
                (LinkedHashMap<String, List<String>>)map.get("clientsMap")
        );
    }
}
