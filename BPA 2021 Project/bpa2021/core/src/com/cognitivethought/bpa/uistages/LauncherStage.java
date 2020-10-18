package com.cognitivethought.bpa.uistages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.Colors;
import com.cognitivethought.bpa.Strings;
import com.cognitivethought.bpa.launcher.Launcher;

public abstract class LauncherStage extends Stage {

	BitmapFont font;
	TextFieldStyle textStyle;
	Pixmap bgColor;
	TextButtonStyle buttonStyle;
	TextButtonStyle noBackgroundButton;
	Pixmap labelBg;
	LabelStyle labelStyle;
	
	HorizontalGroup bar;
	
	TextButton close, fullscreen;
	
	public LauncherStage(Viewport vp) {
		super(vp);
	}
	
	public void repopulate() {
		clear();
		populate();
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
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_ARIAL_UNICODE));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

		Pixmap barBG_pm = new Pixmap(Gdx.graphics.getWidth(), 20, Pixmap.Format.RGB888);
		barBG_pm.setColor(Color.GRAY);
		barBG_pm.fill();
		Image barBG = new Image(new Texture(barBG_pm));
		barBG.setFillParent(true);
		
		Group windowBar = new Group();
		windowBar.setSize(Gdx.graphics.getWidth(), 20);
		windowBar.setWidth(Gdx.graphics.getWidth());
		
		bar = new HorizontalGroup();
		bar.setPosition(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() * 1.5f) - 25);
		bar.align(Align.right);
		bar.setFillParent(true);
		
		TextButtonStyle closeStyle = new TextButtonStyle();
		Pixmap closeBg = new Pixmap(48, 32, Pixmap.Format.RGB888);
		closeBg.setColor(Color.FIREBRICK);
		closeBg.fill();
		closeStyle.down = new Image(new Texture(closeBg)).getDrawable();
		closeBg.setColor(Color.RED);
		closeBg.fill();
		closeStyle.up = new Image(new Texture(closeBg)).getDrawable();
		closeStyle.fontColor = Color.WHITE;
		parameter.size = 20;
		closeStyle.font = gen.generateFont(parameter);
		
		TextButtonStyle barButtonStyle = new TextButtonStyle();
		Pixmap barButtonBg = new Pixmap(48, 32, Pixmap.Format.RGB888);
		barButtonBg.setColor(Color.GRAY);
		barButtonBg.fill();
		barButtonStyle.down = new Image(new Texture(barButtonBg)).getDrawable();
		barButtonBg.setColor(Color.BLACK);
		barButtonBg.fill();
		barButtonStyle.up = new Image(new Texture(barButtonBg)).getDrawable();
		barButtonStyle.over = new Image(new Texture(barButtonBg)).getDrawable();
		barButtonStyle.fontColor = Color.WHITE;
		barButtonStyle.font = gen.generateFont(parameter);
		
		close = new TextButton("X", closeStyle);
		fullscreen = new TextButton("±", barButtonStyle);
		
		close.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		fullscreen.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				clear();
				
				Launcher.isFullscreen = !Launcher.isFullscreen;
				
				if (Launcher.isFullscreen) {
					Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
				} else {
					Gdx.graphics.setWindowedMode(640, 480);
				}
				populate();
			}
		});
		
		bar.addActor(fullscreen);
		bar.addActor(close);
		
		windowBar.addActor(barBG);
		windowBar.addActor(bar);
		
		gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_UBUNTU_REGULAR));
		parameter.size = 15;
		font = gen.generateFont(parameter);
		
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
		bgColor.setColor(Color.GRAY);
		bgColor.fill();
		buttonStyle.down = new Image(new Texture(bgColor)).getDrawable();
		bgColor.setColor(Color.DARK_GRAY);
		bgColor.fill();
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

		parameter.size = 12;

		labelBg = new Pixmap(200, 200, Pixmap.Format.RGB888);
		labelBg.setColor(Color.BLACK);
		labelBg.fill();

		labelStyle = new LabelStyle();
		labelStyle.font = gen.generateFont(parameter);
		labelStyle.fontColor = Color.RED;
		labelStyle.background = new Image(new Texture(labelBg)).getDrawable();
		
		addActor(windowBar);
		
		gen.dispose();
	}

	public abstract void clearFields();
	
}