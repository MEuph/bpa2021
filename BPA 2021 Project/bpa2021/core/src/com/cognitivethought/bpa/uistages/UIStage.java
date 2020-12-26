package com.cognitivethought.bpa.uistages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.tidiness.Colors;
import com.cognitivethought.bpa.tidiness.Strings;

public abstract class UIStage extends Stage {
	
	public BitmapFont font;
	public TextFieldStyle textStyle;
	public Pixmap bgColor;
	public TextButtonStyle buttonStyle;
	public TextButtonStyle noBackgroundButton;
	public Pixmap labelBg;
	public LabelStyle labelStyle;
	
	float flicker = 0f;
	Sprite background;
	Texture on, off;
	
	public UIStage(Viewport vp) {
		super(vp);
		
		on = new Texture(Strings.URL_TITLE_BACKGROUND_ON);
		off = new Texture(Strings.URL_TITLE_BACKGROUND_OFF);
		background = new Sprite(on);
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_PIXEL_FONT_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 15;
		font = gen.generateFont(param);
		
		textStyle = new TextFieldStyle();
		textStyle.font = font;
		bgColor = new Pixmap(250, (int) textStyle.font.getLineHeight(), Pixmap.Format.RGB888);
		bgColor.setColor(Color.WHITE);
		bgColor.fill();
		textStyle.fontColor = Colors.TEXT_DEFAULT;
		textStyle.messageFontColor = Colors.TEXT_MESSAGE;
		textStyle.background = new Image(new Texture(bgColor)).getDrawable();
		textStyle.background.setLeftWidth(textStyle.background.getLeftWidth());
		
		TextField calib = new TextField("|", textStyle);
		Pixmap cursor = new Pixmap(1, (int)calib.getHeight(), Pixmap.Format.RGB888);
		cursor.setColor(Colors.TEXT_DEFAULT);
		cursor.fill();
		
		textStyle.cursor = new Image(new Texture(cursor)).getDrawable();
		
		buttonStyle = new TextButtonStyle();
		bgColor = new Pixmap(100, (int) textStyle.font.getLineHeight(), Pixmap.Format.RGB888);
		bgColor.setColor(Color.DARK_GRAY);
		bgColor.fill();
		buttonStyle.down = new Image(new Texture(bgColor)).getDrawable();
		buttonStyle.up = new Image(new Texture(bgColor)).getDrawable();
		buttonStyle.font = font;
		buttonStyle.fontColor = Color.WHITE;
		buttonStyle.overFontColor = Color.RED;
		buttonStyle.downFontColor = Color.GRAY;

		noBackgroundButton = new TextButtonStyle();
		noBackgroundButton.font = font;
		noBackgroundButton.fontColor = Color.WHITE;
		noBackgroundButton.overFontColor = Color.RED;
		noBackgroundButton.downFontColor = Color.GRAY;

		param.size = 12;

		labelBg = new Pixmap(200, 200, Pixmap.Format.RGB888);
		labelBg.setColor(Color.BLACK);
		labelBg.fill();

		labelStyle = new LabelStyle();
		param.size = 30;
		labelStyle.font = gen.generateFont(param);
		labelStyle.fontColor = Color.RED;
		labelStyle.background = new Image(new Texture(labelBg)).getDrawable();

		gen.dispose();
	}
	
	public boolean isClickingOnUIElement(Actor container, float mouseX, float mouseY, Actor... actors) {
		for (Actor a : actors) {
			if (new Rectangle(a.getX() + container.getX(), a.getY() + container.getY(), a.getWidth(), a.getHeight())
					.contains(mouseX, mouseY)) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}
	
	public void populate() {
		
	}

	public abstract void clearFields();
	
	@Override
	public void draw() {
		act();
		flicker += ((1f / (Math.random() * Math.random()))) / 5000;
		getBatch().begin();
		background = new Sprite(flicker > 1 ? off : on);
		flicker = flicker > 2 ? 0 : flicker;
		getBatch().draw(background, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		getBatch().end();
		super.draw();
	}
	
}