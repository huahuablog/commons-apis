import java.util.HashMap;
import java.util.Map;

public class TestMain {
    public static void main(String[] args) {
        Map<String,String> map=new HashMap<String, String>();
        map.put("key1","value1");
        map.put("key2","value2");
        String str=map.toString();

        System.out.println("===============");
    }

}
