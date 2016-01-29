package com.alphadelete.sandbox.components;

import com.badlogic.ashley.core.Component;

public class AttackComponent implements Component {
	public long damage;

	public AttackComponent(long damage) {
		this.damage = damage;
	}
}