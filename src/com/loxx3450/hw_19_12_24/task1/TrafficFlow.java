package com.loxx3450.hw_19_12_24.task1;

import java.time.LocalTime;

public class TrafficFlow {
	private LocalTime time;
	private float averageTimeBetweenPassengers;
	private float averageTimeBetweenBoats;

	public TrafficFlow(LocalTime time, float averageTimeBetweenPassengers, float averageTimeBetweenBoats) {
		this.time = time;

		if (averageTimeBetweenBoats <= 0) {
		  	throw new IllegalArgumentException("Average time between boats must be positive.");
		}
		this.averageTimeBetweenPassengers = averageTimeBetweenPassengers;

		if (averageTimeBetweenBoats <= 0) {
		  	throw new IllegalArgumentException("Average time between boats must be positive.");
		}
		this.averageTimeBetweenBoats = averageTimeBetweenBoats;
	}

	public LocalTime getTime() {
		return time;
	}

	public float getAverageTimeBetweenBoats() {
		return averageTimeBetweenBoats;
	}

	public float getAverageTimeBetweenPassengers() {
		return averageTimeBetweenPassengers;
	}

	@Override
	public String toString() {
		return "TrafficFlow{" +
			"time=" + time +
			", averageTimeBetweenPassengers=" + averageTimeBetweenPassengers +
			", averageTimeBetweenBoats=" + averageTimeBetweenBoats +
			'}';
	}
}