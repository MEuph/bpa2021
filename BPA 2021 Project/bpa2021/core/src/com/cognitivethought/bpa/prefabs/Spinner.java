package com.cognitivethought.bpa.prefabs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.cognitivethought.bpa.Strings;

public class Spinner extends Actor {
	
	float rotation = 0f;
	boolean spinning = false;

	public Texture arm;
	public Sprite armSprite;
	
	float damp;
	float peak;
	
	Texture tex;
	
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		
		tex = new Texture(Strings.URL_SPINNER_BACKGROUND);
		
		armSprite = new Sprite(new Texture(Strings.URL_SPINNER_ARM));
		armSprite.setPosition(getX() + (getWidth() / 2) - (armSprite.getWidth() / 2), getY() + (getHeight() / 2) - (armSprite.getHeight() / 2));
		armSprite.setOriginCenter();
		armSprite.setOrigin(armSprite.getOriginX(), armSprite.getOriginY() + (armSprite.getHeight() / 2));
		armSprite.setScale(1.5f);
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		armSprite.setPosition(getX() + (getWidth() / 2) - (armSprite.getWidth() / 2), getY() + (getHeight() / 2) - (armSprite.getHeight() / 2));
		armSprite.setOriginCenter();
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