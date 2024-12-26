package com.loxx3450.hw_19_12_24.task2;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {
	private final Language from;
	private final Language to;
	private final HashMap<String, ArrayList<String>> translations;

	// To keep HashMap sorted in alphabetic order
	private final ArrayList<String> sortedKeys = new ArrayList<>();
	private boolean areKeysSorted = true;

	// To count popularity of every word
	private final ArrayList<SimpleEntry<String, Integer>> popularityCounters = new ArrayList<>();
	private boolean areCountersSorted = true;

	public Dictionary(Language from, Language to) {
		this.from = from;
		this.to = to;
		this.translations = new HashMap<>();
	}

	public Dictionary(Language from, Language to, HashMap<String, ArrayList<String>> translations) {
		this.from = from;
		this.to = to;
		this.translations = translations;
	}

	public void addTranslation(String word, String translation) {
		if (translations.containsKey(word)) {
			translations.get(word).add(translation);
		} else {
			translations.put(word, new ArrayList<String>() {{ add(translation); }});

			popularityCounters.add(new SimpleEntry<>(word, 0));

			sortedKeys.add(word);
			areKeysSorted = false;
		}
	}

	public void addTranslations(String word, ArrayList<String> translationsList) {
		if (translations.containsKey(word)) {
			translations.get(word).addAll(translationsList);
		} else {
			translations.put(word, translationsList);

			popularityCounters.add(new SimpleEntry<>(word, 0));

			sortedKeys.add(word);
			areKeysSorted = false;
		}
	}

	public void replaceTranslation(String word, String oldTranslation, String newTranslation) {
		if (translations.containsKey(word)) {
			translations.get(word).remove(oldTranslation);
			translations.get(word).add(newTranslation);
		}
	}

	public void replaceTranslations(String word, ArrayList<String> oldTranslations, ArrayList<String> newTranslations) {
		if (translations.containsKey(word)) {
			translations.get(word).removeAll(oldTranslations);
			translations.get(word).addAll(newTranslations);
		}
	}

	public void deleteTranslation(String word, String translation) {
		if (translations.containsKey(word)) {
			translations.get(word).remove(translation);

			// No need in word, if there is no translation for it
			if (translations.get(word).isEmpty()) {
				popularityCounters.removeIf(counter -> counter.getKey().equals(word));
				translations.remove(word);
			}
		}
	}

	public void deleteTranslations(String word, ArrayList<String> translationsList) {
		if (translations.containsKey(word)) {
			translations.get(word).removeAll(translationsList);

			// No need in word, if there is no translation for it
			if (translations.get(word).isEmpty()) {
				popularityCounters.removeIf(counter -> counter.getKey().equals(word));
				translations.remove(word);
			}
		}
	}

	public void deleteAllTranslations(String word) {
		if (translations.containsKey(word)) {
			popularityCounters.removeIf(counter -> counter.getKey().equals(word));
			translations.remove(word);
		}
	}

	public void printTranslations(String word) {
		if (!translations.containsKey(word)) {
			System.out.println("No translations found for " + word);
			return;
		}

		var counter = popularityCounters
			.stream()
			.filter(c -> c.getKey().equals(word))
			.findFirst()
			.get();				// There is a check if the word is in the HashMap: no chance for null

		// Increment Popularity by 1
		counter.setValue(counter.getValue() + 1);
		areCountersSorted = false;

		System.out.println(from.toString() + ": " + word);
		System.out.println(to.toString() + ": " + String.join(", ", translations.get(word)));

		System.out.println();
	}

	public void printTranslations() {
		if (!areKeysSorted) {
			Collections.sort(sortedKeys);
			areKeysSorted = true;
		}

		sortedKeys.forEach((word) -> {
			System.out.println(from.toString() + ": " + word);
			System.out.println(to.toString() + ": " + String.join(", ", translations.get(word)));
		});
	}

	public void printTheMostPopularTranslations(int count) {
		if (!areCountersSorted) {
			popularityCounters.sort(Map.Entry.comparingByValue());			// Sorting by values
			areCountersSorted = true;
		}

		int maxIterations = Math.min(count, popularityCounters.size());

		int maxIndex = popularityCounters.size() - 1;

		System.out.println("The most popular translations are:");
		for (int i = 0; i < maxIterations; ++i) {
			System.out.println((i + 1) + ". " + popularityCounters.get(maxIndex - i));		// Desc order
		}
	}

	public void printTheMostUnpopularTranslations(int count) {
		if (!areCountersSorted) {
			popularityCounters.sort(Map.Entry.comparingByValue());

			areCountersSorted = true;
		}

		int maxIterations = Math.min(count, popularityCounters.size());

		System.out.println("The most unpopular translations are:");
		for (int i = 0; i < maxIterations; ++i) {
			System.out.println((i + 1) + ". " + popularityCounters.get(i));
		}
	}
}
