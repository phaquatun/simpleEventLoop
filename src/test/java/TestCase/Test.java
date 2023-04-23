package TestCase;

import FormObserver.FormatObserver;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Test {

    static int countMap = 0;

    public static void main(String[] args) {
        Map<String, FormatObserver> map = new HashMap<>();

        map.put("1", new FormatObserver().setTaskFree());
        map.put("2", new FormatObserver().setTaskFree());
        map.put("3", new FormatObserver().setTaskSkip());
        map.put("4", new FormatObserver().setTaskFree());
        
      

        var check = map.entrySet().stream().allMatch((t) -> {
            var fo = t.getValue();

            if (fo.isTaskFree() | fo.isTaskStart()) {
                return true;
            }
            return false;
        });

        var firstGetProxy = map.entrySet().stream().allMatch((t) -> {
            var fo = t.getValue();
            var fwp = fo.getFormWebProxy();
            var keyApi = fwp.getKeyApi();
            if (keyApi == null) {
                return true;
            }
//            if(t.getValue().get)
            return false;
        });

        var listStr = map.entrySet().stream()
                .filter((t) -> {
                    if (t.getValue().isTaskFree()) {
                        return true;
                    }
                    return false;
                })
                .map((t) -> {
                    if (t.getValue().isTaskFree()) {
                        return t.getKey();
                    }
                    return null;
                })
                .collect(Collectors.toList());

        var countFree = map.entrySet().stream().filter((t) -> {
            var fo = t.getValue();

            if (fo.isTaskFree() | fo.isTaskStart()) {
                return true;
            }
            return false;

        }).count();

//        var key = map.entrySet().stream().filter((t) -> {
//            var fo = t.getValue();
//            if (!fo.isTaskFree()) {
//                return true;
//            }
//            return false;
//        }).findFirst().get().getKey();
        System.out.println(firstGetProxy);
    }

}
