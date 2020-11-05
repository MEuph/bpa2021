package com.cognitivethought.bpa.uistages;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.Colors;
import com.cognitivethought.bpa.Strings;
import com.cognitivethought.bpa.launcher.Launcher;

public class LoginStage extends LauncherStage {

	VerticalGroup login_elements;

	TextField login_username, login_password;
	TextButton login_submit, login_forgotPassword, login_newAccount, login_continue;
	Label login_errors, login_title;
	
	public LoginStage(Viewport vp) {
		super(vp);
	}

	public void login(String email, String password) {
		login_errors.getStyle().fontColor = Colors.TEXT_INFO;
		login_errors.setText("Loading...");
		Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleFault(BackendlessFault fault) {
				switch (fault.getCode()) {
				case "3003":
					login_errors.getStyle().fontColor = Colors.TEXT_ERROR;
					login_errors.setText(Strings.ERROR_BE3003 + " Code BE3003");
					break;
				default:
					login_errors.getStyle().fontColor = Colors.TEXT_ERROR;
					login_errors.setText("Error logging in! " + fault.getMessage() + " Code BE" + fault.getCode());
					break;
				}
			}

			@Override
			public void handleResponse(BackendlessUser response) {
				login_errors.getStyle().fontColor = Colors.TEXT_INFO;
				login_errors.setText("Successfully logged in as " + response.getProperty("name"));
				login_elements.removeActor(login_submit);
				login_elements.removeActor(login_errors);
				login_elements.removeActor(login_newAccount);
				login_elements.removeActor(login_forgotPassword);
				login_elements.addActor(login_continue);
				login_elements.addActor(login_errors);
			}
		});
	}

	@Override
	public void populate() {
		super.populate();
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_PIXEL_FONT_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 50;
		BitmapFont titleFont = gen.generateFont(param);

		LabelStyle style = new LabelStyle();
		style.font = titleFont;
		style.fontColor = Colors.LNUI_TITLE;

		login_elements = new VerticalGroup();
		login_elements.align(Align.center);
		login_elements.space(20f);
		login_elements.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		login_title = new Label(Strings.LNUI_TITLE, style);
		login_title.setAlignment(Align.center);
		login_title.setPosition(login_elements.getX() - (login_title.getWidth() / 2), login_elements.getY() + 150);

		addActor(login_title);
		
		login_password = new TextField(Strings.EMPTY, new TextFieldStyle(textStyle));
		login_username = new TextField(Strings.EMPTY, new TextFieldStyle(textStyle));

		login_password.setMessageText(Strings.LNUI_PASSWORD);
		login_username.setMessageText(Strings.LNUI_USERNAME);

		login_password.setPasswordMode(true);
		login_password.setPasswordCharacter('*');

		login_submit = new TextButton(Strings.LNUI_LOGIN, buttonStyle);
		login_submit.setSize(250, buttonStyle.font.getLineHeight() + 10);
		login_submit.align(Align.center);

		login_continue = new TextButton(Strings.LNUI_CONTINUE, buttonStyle);
		login_continue.setSize(200, buttonStyle.font.getLineHeight() + 10);
		login_continue.align(Align.center);

		login_errors = new Label(Strings.EMPTY, labelStyle);

		login_forgotPassword = new TextButton(Strings.LNUI_FORGOT_PASSWORD, noBackgroundButton);
		login_forgotPassword.setSize(100, noBackgroundButton.font.getLineHeight() + 10);
		login_forgotPassword.align(Align.center);

		login_newAccount = new TextButton(Strings.LNUI_CREATE_ACCOUNT, noBackgroundButton);
		login_newAccount.setSize(200, noBackgroundButton.font.getLineHeight() + 10);
		login_newAccount.align(Align.center);

		login_newAccount.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.na_stage);
			}
		});

		login_forgotPassword.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.fp_stage);
			}
		});

		login_submit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				login(login_username.getText(), login_password.getText());
			}
		});

		login_continue.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (Backendless.UserService.isValidLogin()) {
					Launcher.setStage(Launcher.main_stage);
				} else {
					System.out.println("Line 148 in LS");
				}
			}
		});

		login_username.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					login_username.getStyle().fontColor = Colors.TEXT_DEFAULT;
					if (login_username.getText().equals(Strings.LNUI_USERNAME)) {
						login_username.setText("");
					}
				}
			}
		});

		login_password.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					login_password.getStyle().fontColor = Colors.TEXT_DEFAULT;
					if (login_password.getText().equals(Strings.LNUI_PASSWORD)) {
						login_password.setText("");
					}
				}
			}
		});

		final LoginStage login_stage = this;
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isClickingOnUIElement(login_elements, x, y, login_password, login_username, login_submit,
						login_newAccount)) {
					login_stage.unfocusAll();
				}
				super.clicked(event, x, y);
			}
		});

		login_username.setSize(250, textStyle.font.getLineHeight() + 10);
		login_password.setSize(250, textStyle.font.getLineHeight() + 10);

		login_username.setAlignment(Align.center);
		login_password.setAlignment(Align.center);
		login_errors.setAlignment(Align.center);

		login_elements.addActor(login_forgotPassword);
		login_elements.addActor(login_username);
		login_elements.addActor(login_password);
		login_elements.addActor(login_submit);
		login_elements.addActor(login_newAccount);
		login_elements.addActor(login_errors);
		
		addActor(login_elements);
	}

	@Override
	public void clearFields() {
		for (Actor a : login_elements.getChildren()) {
			if (a instanceof TextField) {
				((TextField) a).setText(Strings.EMPTY);
			} else if (a instanceof Label) {
				((Label) a).setText(Strings.EMPTY);
			}
		}
	}
	
	@Override
	public void draw() {
		super.draw();
	}
}