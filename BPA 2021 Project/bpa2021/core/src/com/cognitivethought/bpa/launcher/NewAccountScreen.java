package com.cognitivethought.bpa.launcher;

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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.cognitivethought.bpa.Colors;
import com.cognitivethought.bpa.Strings;

public class NewAccountScreen extends LauncherScreen {
	
	VerticalGroup na_elements;
	
	TextField na_username, na_email, na_password;
	TextButton na_createUser, na_forgotPassword, na_back;
	Label na_errors, na_title;
	
	public NewAccountScreen() {
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_UBUNTU_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 50;
		BitmapFont titleFont = gen.generateFont(param);
		
		LabelStyle style = new LabelStyle();
		style.font = titleFont;
		style.fontColor = Colors.LNUI_TITLE;
		
		na_elements = new VerticalGroup();
		na_elements.align(Align.center);
		na_elements.space(20f);
		na_elements.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		na_title = new Label(Strings.LNUI_TITLE, style);
		na_title.setAlignment(Align.center);
		na_title.setPosition(na_elements.getX() - (na_title.getWidth() / 2), na_elements.getY() + 150);
		
		addActor(na_title);
	}
	
	public void createUser(String username, String email, String password) {
		BackendlessUser createUser = new BackendlessUser();

		createUser.setEmail(email);
		createUser.setPassword(password);
		createUser.setProperty("name", username);

		na_errors.getStyle().fontColor = Colors.TEXT_INFO;
		na_errors.setText("Loading...");
		
		Backendless.UserService.register(createUser, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleFault(BackendlessFault fault) {
				switch (fault.getCode()) {
				case "3033":
					na_errors.getStyle().fontColor = Colors.TEXT_ERROR;
					na_errors.setText(Strings.ERROR_BE3033 + " Code BE3033");
					break;
				default:
					na_errors.getStyle().fontColor = Colors.TEXT_ERROR;
					na_errors.setText(fault.getMessage() + " Code BE" + fault.getCode());
					break;
				}
			}

			@Override
			public void handleResponse(BackendlessUser response) {
				na_errors.getStyle().fontColor = Colors.TEXT_INFO;
				na_errors.setText("Check your email to verify your email address, " + response.getEmail());
			}
		});
	}
	
	@Override
	public void populate() {
		na_email = new TextField(Strings.EMPTY, new TextFieldStyle(textStyle));
		na_password = new TextField(Strings.EMPTY, new TextFieldStyle(textStyle));
		na_username = new TextField(Strings.EMPTY, new TextFieldStyle(textStyle));
		
		na_email.setMessageText(Strings.LNUI_EMAIL);
		na_password.setMessageText(Strings.LNUI_PASSWORD);
		na_username.setMessageText(Strings.LNUI_USERNAME);
		
		na_password.setPasswordMode(true);
		
		na_errors = new Label("", labelStyle);

		na_createUser = new TextButton(Strings.LNUI_CREATE_ACCOUNT, buttonStyle);
		na_createUser.setBackground(textStyle.background);
		na_createUser.setSize(250, buttonStyle.font.getLineHeight() + 10);
		na_createUser.align(Align.center);

		na_forgotPassword = new TextButton(Strings.LNUI_FORGOT_PASSWORD, noBackgroundButton);
		na_forgotPassword.setSize(100, noBackgroundButton.font.getLineHeight() + 10);
		na_forgotPassword.align(Align.center);

		na_back = new TextButton(Strings.LNUI_BACK, noBackgroundButton);
		na_back.setSize(100, noBackgroundButton.font.getLineHeight() + 10);
		na_back.align(Align.center);

		na_back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.previousStage);
			}
		});

		na_forgotPassword.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.fp_stage);
			}
		});

		na_createUser.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				int min_length = 8;

				na_errors.getStyle().fontColor = Colors.TEXT_INFO;
				na_errors.setText("Loading");
				
				String password = na_password.getText();
				
				boolean lengthReq = password.length() >= min_length;
				boolean specialCharReq = !password.matches("[a-zA-Z0-9]");
				boolean numReq = password.matches(".*\\d.*");
				boolean noSpaces = password.split("\\s+").length <= 1;
				
				boolean strongEnough = lengthReq && specialCharReq && numReq && noSpaces;

				if (strongEnough) {
					createUser(na_username.getText(), na_email.getText(), na_password.getText());
				} else {
					String errorMessage = "";
					na_errors.setText(Strings.EMPTY);
					if (!lengthReq)
						errorMessage += "Password must be at least 8 characters; ";
					if (!specialCharReq)
						errorMessage += "Password must contain a special character; ";
					if (!numReq)
						errorMessage += "Password must contain a number";
					if (!noSpaces)
						errorMessage += "Password must not contain spaces";
					na_errors.getStyle().fontColor = Colors.TEXT_ERROR;
					na_errors.setText(errorMessage);
				}
			}
		});

		na_email.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					na_email.getStyle().fontColor = Colors.TEXT_DEFAULT;
					if (na_email.getText().equals(Strings.LNUI_EMAIL)) {
						na_email.setText("");
					}
				}
			}
		});

		na_username.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					na_username.getStyle().fontColor = Colors.TEXT_DEFAULT;
					if (na_username.getText().equals(Strings.LNUI_USERNAME)) {
						na_username.setText("");
					}
				}
			}
		});

		na_password.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					na_password.getStyle().fontColor = Colors.TEXT_DEFAULT;
					if (na_password.getText().equals(Strings.LNUI_PASSWORD)) {
						na_password.setText("");
					}
				}
			}
		});

		final NewAccountScreen na_stage = this;
		this.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isClickingOnUIElement(na_elements, x, y, na_email, na_password, na_username, na_createUser, na_back)) {
					na_stage.unfocusAll();
				}
				super.clicked(event, x, y);
			}
		});

		na_username.setSize(250, textStyle.font.getLineHeight() + 10);
		na_email.setSize(250, textStyle.font.getLineHeight() + 10);
		na_password.setSize(250, textStyle.font.getLineHeight() + 10);

		na_username.setAlignment(Align.center);
		na_email.setAlignment(Align.center);
		na_password.setAlignment(Align.center);

		na_elements.addActor(na_forgotPassword);
		na_elements.addActor(na_back);
		na_elements.addActor(na_username);
		na_elements.addActor(na_email);
		na_elements.addActor(na_password);
		na_elements.addActor(na_errors);
		na_elements.addActor(na_createUser);
		
		addActor(na_elements);
	}
	
	@Override
	public void clearFields() {
		for (Actor a : na_elements.getChildren()) {
			if (a instanceof TextField) {
				((TextField) a).setText(Strings.EMPTY);
			} else if (a instanceof Label) {
				((Label) a).setText(Strings.EMPTY);
			}
		}
	}

}
