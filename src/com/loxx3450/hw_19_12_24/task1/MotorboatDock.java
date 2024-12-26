package com.loxx3450.hw_19_12_24.task1;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MotorboatDock {

	private final ArrayList<TrafficFlow> trafficFlowList;
	private final boolean finalStop;

	private LinkedList<LocalTime> passengersArrivalTimesQueue = new LinkedList<>();
	private boolean isListSorted = false;
	private static final int MAX_BOAT_CAPACITY = 200;

	public MotorboatDock(boolean finalStop) {
		this.finalStop = finalStop;
		this.trafficFlowList = new ArrayList<>();
	}

	public MotorboatDock(ArrayList<TrafficFlow> trafficFlowList, boolean finalStop) {
		this.finalStop = finalStop;
		this.trafficFlowList = trafficFlowList;
	}

	public void addTrafficFlow(TrafficFlow trafficFlow) {
		trafficFlowList.add(trafficFlow);

		isListSorted = false;
	}

	public void removeTrafficFlow(TrafficFlow trafficFlow) {
		trafficFlowList.remove(trafficFlow);
	}

	public void removeTrafficFlow(LocalTime time) {
		trafficFlowList.removeIf(trafficFlow -> trafficFlow.getTime().equals(time));
	}

	public float getAveragePassengerWaitingTime() {
		if (trafficFlowList.isEmpty()) {
			return 0.0f;
		}

		// Traffic flows should be sorted by the field "Time"
		if (!isListSorted) {
			trafficFlowList.sort((a, b) -> a.getTime().compareTo(b.getTime()));
			isListSorted = true;
		}

		AtomicInteger totalAmountOfPassengers = new AtomicInteger(0);
		AtomicReference<Float> totalWaitingTimeInMinutes = new AtomicReference(0.0f);

		for (int i = 0; i < trafficFlowList.size(); i++) {
			var trafficFlow = trafficFlowList.get(i);
			LocalTime currentTime = trafficFlow.getTime();

			int minutesUntilChange = getMinutesUntilNextChange(i, currentTime);

			fillPassengersArrivalTimesQueue(trafficFlow, minutesUntilChange, totalAmountOfPassengers, currentTime);

			imitateBoatsArrival(trafficFlow, minutesUntilChange, currentTime, totalWaitingTimeInMinutes);
		}

		addWaitingTimeOfRemainingPassengers(totalWaitingTimeInMinutes);

		// Development outputs
		System.out.println("Total waiting time in minutes: " + totalWaitingTimeInMinutes);
		System.out.println("Total amount of passengers: " + totalAmountOfPassengers);

		if (totalAmountOfPassengers.get() == 0) {
			return 0.0f;
		}

		return totalWaitingTimeInMinutes.get() / totalAmountOfPassengers.get();
	}

	public HashMap<LocalTime, Float> optimizeTrafficFlow(int maxAmountOfPassengers) {
		var result = new HashMap<LocalTime, Float>();

		if (trafficFlowList.isEmpty()) {
			return result;
		}

		float avgFreeSeatsCount = getAverageFreeSeatsCount();

		for (int i = 0; i < trafficFlowList.size(); i++) {
			var trafficFlow = trafficFlowList.get(i);
			LocalTime currentTime = trafficFlow.getTime();

			// How long info from traffic flow is up-to-date
			int minutesUntilChange = getMinutesUntilNextChange(i, currentTime);

			// How many passengers would be in the dock during the period
			float totalAmountOfPassengers = minutesUntilChange / trafficFlow.getAverageTimeBetweenPassengers();

			float neededBoatsCount = getNeededBoatsCount(avgFreeSeatsCount, maxAmountOfPassengers, totalAmountOfPassengers);

			float intervalBetweenBoats = minutesUntilChange / neededBoatsCount;

			// Saving result in Map for every traffic flow
			result.put(trafficFlow.getTime(), intervalBetweenBoats);
		}

		return result;
	}

	// Test method: for development
	public void printTrafficFlows() {
		for (var trafficFlow : trafficFlowList) {
			System.out.println(trafficFlow);
		}
	}

	private float getAverageFreeSeatsCount() {
		if (finalStop) {
			// On final stop all passengers from previous stop are going out
			// All seats will be free
			return MAX_BOAT_CAPACITY;
		} else {
			// Assume 50% capacity as an average
			return MAX_BOAT_CAPACITY / 2.0f;
		}
	}

	private float getNeededBoatsCount(float avgFreeSeatsCount, int maxAmountOfPassengers, float totalAmountOfPassengers) {
		if (maxAmountOfPassengers == 0 || avgFreeSeatsCount == 0 || totalAmountOfPassengers == 0) {
			return 0.0f;
		}

		if (avgFreeSeatsCount > maxAmountOfPassengers) {
			// Even if boat has more capacity,
			// boats need to come more often to not let passengers wait
			return (float)Math.ceil(totalAmountOfPassengers / maxAmountOfPassengers);
		}
		else {
			// Boats should arrive as often as possible to meet the condition
			return (float)Math.ceil(totalAmountOfPassengers / avgFreeSeatsCount);
		}
	}

	private int getMinutesUntilNextChange(int currentTrafficFlowIndex, LocalTime currentTime) {
		LocalTime nextChange;

		// Calculating time of next change of conditions:
		// LocalTime.MAX if this is last traffic flow
		// otherwise time of next traffic flow
		if (currentTrafficFlowIndex + 1 == trafficFlowList.size()) {
			nextChange = LocalTime.MAX;
		} else {
			nextChange = trafficFlowList.get(currentTrafficFlowIndex + 1).getTime();
		}

		return (int)Duration.between(currentTime, nextChange).toMinutes();
	}

	private void fillPassengersArrivalTimesQueue(TrafficFlow trafficFlow, int minutesUntilChange, AtomicInteger totalAmountOfPassengers, LocalTime currentTime) {
		float avgTimeBetweenPassengers = trafficFlow.getAverageTimeBetweenPassengers();
		for (float j = avgTimeBetweenPassengers; j <= minutesUntilChange; j += avgTimeBetweenPassengers) {
			// Increment
			totalAmountOfPassengers.set(totalAmountOfPassengers.get() + 1);
			passengersArrivalTimesQueue.add(currentTime.plusMinutes((long)j));
		}
	}

	private void imitateBoatsArrival(TrafficFlow trafficFlow, int minutesUntilChange, LocalTime currentTime, AtomicReference<Float> totalWaitingTimeInMinutes) {
		Random random = new Random();

		float avgTimeBetweenBoats = trafficFlow.getAverageTimeBetweenBoats();
		for (float j = avgTimeBetweenBoats; j <= minutesUntilChange; j += avgTimeBetweenBoats) {

			int freeSeatsCount = finalStop ? MAX_BOAT_CAPACITY : random.nextInt(MAX_BOAT_CAPACITY + 1);

			// Updating currentTime
			currentTime = currentTime.plusMinutes((long)j);

			removePassengersFromQueue(freeSeatsCount, currentTime, totalWaitingTimeInMinutes);
		}
	}

	private void removePassengersFromQueue(int freeSeatsCount, LocalTime currentTime, AtomicReference<Float> totalWaitingTimeInMinutes) {
		for (int g = 0; g < freeSeatsCount; g++) {
			if (!passengersArrivalTimesQueue.isEmpty()) {
				// If boat arrived before passengers
				// If there are no passengers waiting for boat
				if (passengersArrivalTimesQueue.getFirst().compareTo(currentTime) > 0) {
					return;
				}

				LocalTime passengerArrivalTime = passengersArrivalTimesQueue.remove();

				long passengerWaitingTimeInMinutes = Duration.between(passengerArrivalTime, currentTime).toMinutes();
				totalWaitingTimeInMinutes.set(totalWaitingTimeInMinutes.get() + passengerWaitingTimeInMinutes);
			}
		}
	}

	private void addWaitingTimeOfRemainingPassengers(AtomicReference<Float> totalWaitingTimeInMinutes) {
		for (var passengerArrivalTime : passengersArrivalTimesQueue) {
			long passengerWaitingTimeInMinutes = Duration.between(passengerArrivalTime, LocalTime.MAX).toMinutes();
			totalWaitingTimeInMinutes.set(totalWaitingTimeInMinutes.get() + passengerWaitingTimeInMinutes);
		}
	}
}
