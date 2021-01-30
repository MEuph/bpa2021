package com.cognitivethought.bpa.prefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cognitivethought.bpa.tidiness.Strings;

public class Spinner extends Actor {
	
	float rotation = 0f;
	boolean spinning = false;

	public Texture arm;
	public Sprite armSprite;
	
	float damp;
	float peak;
	
	Texture tex;
	
	float downY, upY;
	
	Pixmap arrow;
	Texture aTex;
	public ImageButton clickArrow;
	
	boolean isDown;
	
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		
		tex = new Texture(Strings.URL_SPINNER_BACKGROUND);
		
		arrow = new Pixmap(new FileHandle(Strings.URL_SPINNER_ARROW));
		
		aTex = new Texture(arrow);
		Image i = new Image(aTex);
		clickArrow = new ImageButton(i.getDrawable());
		
		clickArrow.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				isDown = !isDown;
			}
		});

		armSprite = new Sprite(new Texture(Strings.URL_SPINNER_ARM));
		armSprite.setPosition(getX() + (getWidth() / 2) - (armSprite.getWidth() / 2), getY() + (getHeight() / 2) - (armSprite.getHeight() / 2));
		armSprite.setOriginCenter();
		armSprite.setOrigin(armSprite.getOriginX(), armSprite.getOriginY() + (armSprite.getHeight() / 2));
		armSprite.setScale(1.5f * 2f);
		
		clickArrow.setPosition(getX() + (getWidth() / 2), getY() - clickArrow.getHeight() + 5);
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		armSprite.setPosition(getX() + (getWidth() / 2) - (armSprite.getWidth() / 2), getY() + (getHeight() / 2) - (armSprite.getHeight() / 2));
		armSprite.setOriginCenter();
		
		if (!isDown && downY == upY) {
			downY = getY();
			setPosition(getX(), getY() + getHeight());
			upY = getY();
		}
	}
	
	public void spin() {
		spinning = true;
		damp = 0f;
		peak = 10f + (float)(Math.random() * 10);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
//		if (!MainGameStage.warInitiated) return;
		
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
		
		if (isDown && getY() >= downY + 2) {
			Vector2 newPos = new Vector2(getX(), getY()).
					lerp(new Vector2(0, downY), Gdx.graphics.getDeltaTime() * 5f);
			setPosition(getX(), newPos.y);
			clickArrow.setPosition(getX() + (getWidth() / 2), getY() - clickArrow.getHeight() + 5);
		} else if (isDown && getY() < downY + 2) {
			setPosition(getX(), downY);
			clickArrow.setPosition(getX() + (getWidth() / 2), getY() - clickArrow.getHeight() + 5);
		}
		
		if (!isDown && getY() <= upY - 2) {
			Vector2 newPos = new Vector2(getX(), getY()).
					lerp(new Vector2(0, upY), Gdx.graphics.getDeltaTime() * 5f);
			setPosition(getX(), newPos.y);
			clickArrow.setPosition(getX() + (getWidth() / 2), getY() - clickArrow.getHeight() + 5);
		} else if (!isDown && getY() > upY - 2) {
			setPosition(getX(), upY);
			clickArrow.setPosition(getX() + (getWidth() / 2), getY() - clickArrow.getHeight() + 5);
		}
	}
}