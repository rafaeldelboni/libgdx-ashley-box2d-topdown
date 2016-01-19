package com.alphadelete.sandbox.components;

import com.alphadelete.sandbox.Constants;
import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
	public static final int STATE_IDLE = 0;
	public static final int STATE_WALK = 1;
	public static final int STATE_HIT = 2;
	public static final float JUMP_VELOCITY = 11;
	public static final float MOVE_VELOCITY = 50;
	public static final float WIDTH = 1f;
	public static final float HEIGHT = 1f;
	
	public static float SCALE_SIDE = Constants.SCALE_RIGHT;
}