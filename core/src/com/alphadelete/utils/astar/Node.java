package com.alphadelete.utils.astar;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;

public class Node implements IndexedNode<Node> {

	public final int x;
	public final int y;
	public boolean isWall;
	private int index;
	private final Array<Connection<Node>> connections;

	public Node(int x, int y, int index) {
		this.x = x;
		this.y = y;
		this.index = index;
		this.isWall = false;
		this.connections = new Array<Connection<Node>>();
	}

	@Override
	public int getIndex() {
		return this.index;
	}

	@Override
	public Array<Connection<Node>> getConnections() {
		return this.connections;
	}

	@Override
	public String toString() {
		return "Index:" + String.valueOf(this.index) +
				" X: " + String.valueOf(this.x) + 
				" Y: " + String.valueOf(this.y) + 
				" isWall: " + String.valueOf(this.isWall);
	}
}