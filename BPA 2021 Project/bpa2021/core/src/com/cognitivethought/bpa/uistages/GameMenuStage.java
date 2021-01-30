package com.cognitivethought.bpa.uistages;

import com.backendless.Backendless;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.sound.Sounds;
import com.cognitivethought.bpa.tidiness.Colors;
import com.cognitivethought.bpa.tidiness.Strings;

public class GameMenuStage extends UIStage {
	
	Label mui_errors;
	Label credits;
	TextButton start, settings, quit, host_server;
	VerticalGroup gm_elements;
	
	Texture ctm, studio;
	
	public GameMenuStage(Viewport vp) {
		super(vp);
	}
	
	@Override
	public void populate() {
		super.populate();
		
		ctm = new Texture(new Pixmap(new FileHandle(Strings.URL_CTM)));
		studio = new Texture(new Pixmap(new FileHandle(Strings.URL_GRS)));
		
		int vol_i = (int) Backendless.UserService.CurrentUser().getProperty("nw_volume");
		float vol = (float)(vol_i) / 100f;
		Sounds.music_queue.stop();
		Sounds.music_intro.stop();
		Sounds.music_war.stop();
		Sounds.music_intro_id = Sounds.music_intro.play(vol);
		
		int scale = ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768));
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_PIXEL_FONT_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 15 * (1 + scale);

		//	buttonStyle.font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		buttonStyle.font = gen.generateFont(param);
		
		gm_elements = new VerticalGroup();
		gm_elements.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gm_elements.space(50);
		
//		gm_elements.scaleBy(scale);

		param.size = (int)labelStyle.font.getLineHeight() * (1 + scale);
		LabelStyle style = new LabelStyle();
		style.font = gen.generateFont(param);
		style.fontColor = Color.RED;

				
		mui_errors = new Label(Strings.EMPTY, labelStyle);
		mui_errors.getStyle().fontColor = Colors.TEXT_INFO;
		
		credits = new Label("Created by Grass Roots Studios\n\nChristopher Harris\n\nMark White\n\nAlister White\n\nCaleb Pruitt\n\nFor BPA Software Engineering 2021", new LabelStyle(labelStyle));
		credits.getStyle().background = new Image(new Texture(new Pixmap(10, 10, Pixmap.Format.RGBA8888))).getDrawable();
		credits.getStyle().fontColor = Color.WHITE;
		credits.setPosition(Gdx.graphics.getWidth() / 2 + 25, Gdx.graphics.getHeight() / 2 + 25);
		
		start = new TextButton(Strings.MUI_START, buttonStyle);
		settings = new TextButton(Strings.MUI_SETTINGS, buttonStyle);
		quit = new TextButton(Strings.MUI_QUIT, buttonStyle);
		host_server = new TextButton(Strings.MUI_HOST, buttonStyle);
		
		start.align(Align.center);
		settings.align(Align.center);
		quit.align(Align.center);
		host_server.align(Align.center);
		
		start.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.js_stage);
			}
		});
		
		settings.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.settings_stage);
			}
		});
		
		quit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		host_server.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.hs_stage);
			}
		});
		
		final GameMenuStage mui_stage = this;
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isClickingOnUIElement(gm_elements, x, y, host_server)) {
					mui_stage.unfocusAll();
				}
				super.clicked(event, x, y);
			}
		});
		
		gm_elements.addActor(start);
		gm_elements.addActor(settings);
		gm_elements.addActor(host_server);
		gm_elements.addActor(quit);
		gm_elements.addActor(mui_errors);
		
		gm_elements.align(Align.center);
		
		addActor(gm_elements);
	}
	
	float elapsed = 0;
	
	@Override
	public void draw() {
		super.draw();
		
		if (elapsed < 350) {
			gm_elements.setVisible(false);
			elapsed += Gdx.graphics.getDeltaTime() * 50f;
			
			if (!getBatch().isDrawing()) getBatch().begin();
			getBatch().draw(elapsed < 150 ? ctm : studio, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			getBatch().end();
		} else {
			gm_elements.setVisible(true);
			
			if (!getBatch().isDrawing()) getBatch().begin();
			credits.draw(getBatch(), 1);
			getBatch().end();
		}
	}
	
	@Override
	public void clearFields() {
		
	}
}