package com.alphadelete.sandbox.components;

import com.badlogic.ashley.core.Component;

public class EffectsComponent implements Component {
	public long startTimeMillis;
	public long durationTimeMillis;

	public EffectsComponent(long durationTimeMillis) {
		this.startTimeMillis = System.currentTimeMillis();
		this.durationTimeMillis = durationTimeMillis;
	}
}