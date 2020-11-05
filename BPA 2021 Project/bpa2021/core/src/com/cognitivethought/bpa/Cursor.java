package com.cognitivethought.bpa;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Cursor extends Actor {
	
	Pixmap pm;
	Texture tex;
	
	int size = 16;
	
	public Cursor() {
		pm = new Pixmap(size, size, Pixmap.Format.RGB888);
		pm.setColor(Color.CYAN);
		pm.fill();
		setSize(size, size);
		tex = new Texture(pm);
	}
	
	public void update() {
//		setPosition();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(tex, getX(), getY(), size, size);
	}
}