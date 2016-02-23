package com.alphadelete.sandbox.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureComponent implements Component {
	public TextureRegion region = null;
	public Color color = null;
}