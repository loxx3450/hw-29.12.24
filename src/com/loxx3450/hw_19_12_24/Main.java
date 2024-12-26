package com.loxx3450.hw_19_12_24;

import com.loxx3450.hw_19_12_24.task1.MotorboatDock;
import com.loxx3450.hw_19_12_24.task1.TrafficFlow;
import com.loxx3450.hw_19_12_24.task2.Dictionary;
import com.loxx3450.hw_19_12_24.task2.Language;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Task 1
//        ArrayList<TrafficFlow> trafficFlows = new ArrayList();
//        trafficFlows.add(new TrafficFlow(LocalTime.of(12, 0, 0), 1.0f, 15.0f));
//        trafficFlows.add(new TrafficFlow(LocalTime.of(17, 0, 0), 0.4f, 10.0f));
//        trafficFlows.add(new TrafficFlow(LocalTime.of(22, 0, 0), 2f, 20.0f));
//
//        MotorboatDock dock = new MotorboatDock(trafficFlows, false);
//
//        dock.printTrafficFlows();
//
//        System.out.println("Average waiting time of passenger: " + dock.getAveragePassengerWaitingTime());
//
//        System.out.println("\n\n\n");
//
//        printHashMap(dock.optimizeTrafficFlow(100));

        // Task 2
        Dictionary dict = new Dictionary(Language.EN, Language.DE);
        String word = "possibility";

        dict.addTranslations(word, new ArrayList<>() {{ add("Gelegenheit"); add("Moglichkeit"); add("Chance"); }});
        dict.addTranslation("dog", "Hund");
        dict.addTranslation("mobile phone", "Handy");
        dict.addTranslation("justice", "Gerechtigkeit");
        dict.printTranslations(word);

        dict.replaceTranslation(word, "Moglichkeit", "Moeglichkeit");
        dict.printTranslations(word);

        dict.printTranslations("justice");

        dict.printTheMostPopularTranslations(10);

        System.out.println('\n');

        dict.printTheMostUnpopularTranslations(10);
    }

    // Development method-helper
    public static void printHashMap(HashMap<LocalTime, Float> map) {
        if (map == null || map.isEmpty()) {
            System.out.println("The map is empty or null.");
            return;
        }

        System.out.println("HashMap Contents:");
        for (Map.Entry<LocalTime, Float> entry : map.entrySet()) {
            System.out.printf("Time: %s, Interval: %.2f minutes%n", entry.getKey(), entry.getValue());
        }
    }
}
