package com.alphadelete.sandbox.components;

import com.alphadelete.sandbox.Constants;
import com.alphadelete.utils.astar.Node;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class EnemyComponent implements Component {
	public static final int STATE_IDLE = 0;
	public static final int STATE_WALK = 1;
	public static final int STATE_DIE = 2;
	public static final int STATE_ATTACK = 4;
	public static final float JUMP_VELOCITY = 11;
	public static final float MOVE_VELOCITY = 50;
	public static final float WIDTH = 1f;
	public static final float HEIGHT = 1f;
	
	
	public float scaleSide = Constants.SCALE_RIGHT;
	public Vector2 accel = new Vector2(0,0);
	public Vector2 target = new Vector2();
	public long health = 0;
	public Boolean isAttacking = false;
	public float attackStance = 0f;
	public long knockbackTimeMillis = 0;
	public Node nextNode; // for pursue or escape
	
	public Boolean getEnemyIsMoving() {
		if (this.accel.x == 0f && this.accel.y == 0f) {
			return false;
		}
		return true;
	}

}