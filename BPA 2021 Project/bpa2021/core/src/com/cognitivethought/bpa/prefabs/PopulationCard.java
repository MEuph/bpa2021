package com.cognitivethought.bpa.prefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.cognitivethought.bpa.tidiness.Strings;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class PopulationCard extends Widget {
	
	Label disp;
	Texture tex;
	int quantity;
	
	public PopulationCard(int popSize, int quantity) {
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_PIXEL_FONT_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 15;
		
		LabelStyle style = new LabelStyle();
		style.font = gen.generateFont(param);
		
		String str = Integer.toString(popSize) + "M" + (quantity > 0 ? " x" + Integer.toString(quantity) : "");
		disp = new Label(str, style);
		disp.setColor(Color.BLUE);
		
		switch (popSize) {
		case 1:
			tex = new Texture(Strings.URL_1MIL);
			break;
		case 10:
			tex = new Texture(Strings.URL_10MIL);
			break;
		case 20:
			tex = new Texture(Strings.URL_20MIL);
			break;
		case 50:
			tex = new Texture(Strings.URL_50MIL);
			break;
		case 100:
			tex = new Texture(Strings.URL_100MIL);
			break;
		}
		
		this.quantity = quantity;
		
		gen.dispose();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		disp.setPosition(getX() - 10 - (disp.getGlyphLayout().width), getY() + (getHeight() / 2));
		
		for (int i = 0; i < quantity; i++) {
			batch.draw(tex, getX() + (quantity * 5) - (i * 5), getY(), getWidth(), getHeight());
		}
		
		disp.draw(batch, parentAlpha);
	}
}