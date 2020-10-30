package com.cognitivethought.bpa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class PopulationCard extends Widget {
	
	Label disp;
	Pixmap pm;
	
	public PopulationCard(int popSize) {
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_PIXEL_FONT_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 20;
		
		LabelStyle style = new LabelStyle();
		style.font = gen.generateFont(param);
		
		disp = new Label(Integer.toString(popSize) + "M", style);
		disp.setColor(Color.BLACK);
		
		pm = new Pixmap((int)getWidth(), (int)getHeight(), Pixmap.Format.RGBA8888);
	}
	
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		
		pm = new Pixmap((int)getWidth(), (int)getHeight(), Pixmap.Format.RGBA8888);
	}
	@Override
	public void setScale(float scaleXY) {
		super.setScale(scaleXY);
		
		pm = new Pixmap((int)getWidth(), (int)getHeight(), Pixmap.Format.RGBA8888);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		disp.setPosition(getX() + (getWidth() / 2) - (disp.getGlyphLayout().width / 2), getY() + (getHeight() / 2));
		
		pm.setColor(Color.CLEAR);
		pm.fill();
		
		int rad = 10;
		
		pm.setColor(Color.WHITE);
		pm.fillCircle(rad, pm.getHeight() - rad, rad);
		pm.fillCircle(pm.getWidth() - rad, pm.getHeight() - rad, rad);
		pm.fillCircle(pm.getWidth() - rad, rad, rad);
		pm.fillCircle(rad, rad, rad);
		pm.setColor(Color.RED);
		pm.drawCircle(rad, pm.getHeight() - rad, rad);
		pm.drawCircle(pm.getWidth() - rad, pm.getHeight() - rad, rad);
		pm.drawCircle(pm.getWidth() - rad, rad, rad);
		pm.drawCircle(rad, rad, rad);
		pm.fillRectangle(rad, 0, (pm.getWidth()) - (rad * 2) + 1, pm.getHeight());
		pm.fillRectangle(0, rad, pm.getWidth(), pm.getHeight() - (rad * 2));
		pm.setColor(Color.WHITE);
		pm.fillRectangle(rad, 1, (pm.getWidth()) - (rad * 2) + 1, pm.getHeight() - 2);
		pm.fillRectangle(1, rad, pm.getWidth() - 2, pm.getHeight() - (rad * 2));
		
		batch.draw(new Texture(pm), getX(), getY(), getWidth(), getHeight());
		disp.draw(batch, parentAlpha);
	}
}