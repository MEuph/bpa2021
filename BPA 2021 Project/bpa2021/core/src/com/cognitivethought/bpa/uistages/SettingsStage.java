package com.cognitivethought.bpa.uistages;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.tidiness.Colors;
import com.cognitivethought.bpa.tidiness.Settings;
import com.cognitivethought.bpa.tidiness.Strings;

public class SettingsStage extends UIStage {
	
	Label ss_volume, ss_errors;
	TextButton save, back;
	VerticalGroup ss_elements;
	Slider ss_slider;
	
	public SettingsStage(Viewport vp) {
		super(vp);
	}
	
	@Override
	public void populate() {
		super.populate();
		
		int scale = ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768));
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_PIXEL_FONT_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 15 * (1 + scale);

		//	buttonStyle.font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		buttonStyle.font = gen.generateFont(param);
		
		ss_elements = new VerticalGroup();
		ss_elements.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ss_elements.space(50);
		
//		gm_elements.scaleBy(scale);

		param.size = (int)labelStyle.font.getLineHeight() * (1 + scale);
		LabelStyle style = new LabelStyle();
		style.font = gen.generateFont(param);
		style.fontColor = Color.RED;

		ss_slider = new Slider(0, 100, 1, false, new Skin(new FileHandle(Strings.URL_SKINS_DEFAULT_FILE),
						new TextureAtlas(new FileHandle(Strings.URL_SKINS_DEFAULT_ATLAS))));
		ss_slider.setSize(Gdx.graphics.getWidth() / 2, 50);
		
		ss_errors = new Label(Strings.EMPTY, labelStyle);
		ss_errors.getStyle().fontColor = Colors.TEXT_INFO;
		
		ss_volume = new Label(Strings.EMPTY, labelStyle);
		ss_volume.getStyle().fontColor = Color.WHITE;
		
		save = new TextButton(Strings.SS_SAVE, buttonStyle);
		back = new TextButton(Strings.SS_BACK, buttonStyle);
		
		save.align(Align.center);
		back.align(Align.center);
		
		ss_slider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ss_volume.setText((int)(ss_slider.getVisualPercent() * 100) + "%");
			}
		});
		
		save.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println((int)(ss_slider.getVisualPercent() * 100));
				Backendless.UserService.CurrentUser().setProperty("nw_volume", (int)(ss_slider.getVisualPercent() * 100));
				Backendless.Data.of(BackendlessUser.class).save(Backendless.UserService.CurrentUser(), new AsyncCallback<BackendlessUser>() {
					
					@Override
					public void handleResponse(BackendlessUser response) {
						ss_errors.setColor(Colors.TEXT_INFO);
						ss_errors.setText("Successfully saved volume for " + Launcher.currentUser.getProperty("name").toString());
					}
					
					@Override
					public void handleFault(BackendlessFault fault) {
						ss_errors.setColor(Colors.TEXT_ERROR);
						ss_errors.setText("Could not save volume for this user. BE" + fault.getCode());
					}
				});
			}
		});
		
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.game_menu_stage);
			}
		});
		
		final SettingsStage mui_stage = this;
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isClickingOnUIElement(ss_elements, x, y)) {
					mui_stage.unfocusAll();
				}
				super.clicked(event, x, y);
			}
		});
		
		ss_elements.addActor(ss_volume);
		ss_elements.addActor(ss_slider);
		ss_elements.addActor(save);
		ss_elements.addActor(back);
		ss_elements.addActor(ss_errors);
		
		ss_elements.align(Align.center);
		
		addActor(ss_elements);
		
		ss_volume.setText(Backendless.UserService.CurrentUser().getProperty("nw_volume").toString() + "%");
		ss_slider.setValue(Integer.parseInt(Backendless.UserService.CurrentUser().getProperty("nw_volume").toString()));
		
		Settings.VOLUME = (int)ss_slider.getValue();
	}
	
	@Override
	public void clearFields() {
		
	}
}