package com.alphadelete.sandbox.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

public class BoundsComponent implements Component {
	public final Rectangle bounds = new Rectangle();
}