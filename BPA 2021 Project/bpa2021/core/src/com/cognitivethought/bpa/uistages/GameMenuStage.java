package com.cognitivethought.bpa.uistages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.Strings;
import com.cognitivethought.bpa.launcher.Launcher;

public class GameMenuStage extends UIStage {
	
	Label title;
	TextButton start, help, quit;
	VerticalGroup gm_elements;
	
	public GameMenuStage(Viewport vp) {
		super(vp);
	}
	
	@Override
	public void populate() {
		super.populate();
		
		int scale = ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768));
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_UBUNTU_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 15 * (1 + scale);

		//	buttonStyle.font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		buttonStyle.font = gen.generateFont(param);
		
		gm_elements = new VerticalGroup();
		gm_elements.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gm_elements.space(50);
		
//		gm_elements.scaleBy(scale);

		param.size = (int)labelStyle.font.getLineHeight() * (1 + scale);
		labelStyle.font = gen.generateFont(param);
		title = new Label(Strings.MUI_TITLE, labelStyle);
//		title.scaleBy(scale);
		title.setAlignment(Align.center);
		title.setPosition(gm_elements.getX() - (title.getWidth() / 2), (int)(getViewport().getScreenHeight() * 1.3));
		
		start = new TextButton(Strings.MUI_START, buttonStyle);
		help = new TextButton(Strings.MUI_HELP, buttonStyle);
		quit = new TextButton(Strings.MUI_QUIT, buttonStyle);
		
		start.align(Align.center);
		help.align(Align.center);
		quit.align(Align.center);
		
		start.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.dev_stage);
			}
		});
		
		quit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		gm_elements.addActor(start);
		gm_elements.addActor(help);
		gm_elements.addActor(quit);
		
		gm_elements.align(Align.center);
		
		addActor(title);
		addActor(gm_elements);
	}
	
	@Override
	public void clearFields() {
		
	}
}