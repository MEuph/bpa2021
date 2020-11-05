package com.cognitivethought.bpa;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MouseZone extends Actor {

	Pixmap pm;
	Texture tex;
	
	public MouseZone() {
		pm = new Pixmap(300, 300, Pixmap.Format.RGB888);
		pm.setColor(Color.BLACK);
		pm.fill();
		
		tex = new Texture(pm);
		
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				
				System.exit(0);
			}
		});
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		batch.draw(tex, getX(), getY(), getWidth(), getHeight());
	}
}