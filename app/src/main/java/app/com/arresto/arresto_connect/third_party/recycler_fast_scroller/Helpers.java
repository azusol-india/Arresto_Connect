package app.com.arresto.arresto_connect.third_party.recycler_fast_scroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Helpers {
    public static HashMap<Integer, Integer> sectionsHelper(List<String> sections, ArrayList<String> test) {
        HashMap<Integer, Integer> mapOfSections = new HashMap<>();
        int lastFound = 0;

        for (int i = 0; i < test.size(); i++) {
            String s = test.get(i);
            if (sections.contains(s)) {
                int pos = sections.indexOf(s);
                mapOfSections.put(i, lastFound);
                lastFound = pos;
            } else {
                mapOfSections.put(i, lastFound);
            }


        }

        return mapOfSections;
    }
}
