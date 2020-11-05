package com.cognitivethought.bpa.prefabs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Spinner extends Actor {
	
	float rotation = 0f;
	boolean spinning = false;

	public Pixmap arm;
	public Sprite armSprite;
	
	float damp;
	float peak;
	
	Pixmap pm;
	
	Texture tex;
	
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		
		pm = new Pixmap((int)width, (int)height, Pixmap.Format.RGBA8888);
		pm.setColor(Color.RED);
		pm.fillRectangle((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
		
		tex = new Texture(pm);
		
		arm = new Pixmap(5, (int)(getHeight() / 2) - 10, Pixmap.Format.RGB888);
		arm.setColor(Color.BLACK);
		arm.fill();
		
		armSprite = new Sprite(new Texture(arm));
		armSprite.setPosition(getX() + (getWidth() / 2), getY());
		armSprite.setOriginCenter();
		armSprite.setOrigin(armSprite.getOriginX(), armSprite.getOriginY() + (armSprite.getHeight() / 2));
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		armSprite.setPosition(getX() + (getWidth() / 2), getY());
		armSprite.setOriginCenter();
		armSprite.setOrigin(armSprite.getOriginX(), armSprite.getOriginY() + (armSprite.getHeight() / 2));
	}
	
	public void spin() {
		spinning = true;
		damp = 0f;
		peak = 10f + (float)(Math.random() * 10);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (spinning) {
			
			damp += 0.1f;
			
			rotation += peak - damp;
			armSprite.rotate(damp - peak);
			
			if (damp >= peak) {
				spinning = false;
			}
		}
		
		super.draw(batch, parentAlpha);
		batch.draw(tex, getX(), getY(), getWidth(), getHeight());
		armSprite.draw(batch);
	}
}