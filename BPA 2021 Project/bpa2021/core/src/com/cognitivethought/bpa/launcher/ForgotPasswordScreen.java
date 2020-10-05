package com.cognitivethought.bpa.launcher;

import java.util.List;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.cognitivethought.bpa.Colors;
import com.cognitivethought.bpa.Strings;

public class ForgotPasswordScreen extends LauncherScreen {
	
	VerticalGroup fp_elements;

	TextField fp_username, fp_email, fp_tempPass, fp_newPass, fp_verifyNewPass;
	TextButton fp_submit, fp_back;
	CheckBox fp_alreadyHasTempPass;
	Label fp_errors, fp_title;
	
	public ForgotPasswordScreen() {
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_UBUNTU_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 50;
		BitmapFont titleFont = gen.generateFont(param);
		
		LabelStyle style = new LabelStyle();
		style.font = titleFont;
		style.fontColor = Colors.LNUI_TITLE;
		
		fp_elements = new VerticalGroup();
		fp_elements.align(Align.center);
		fp_elements.space(20f);
		fp_elements.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		
		fp_title = new Label(Strings.LNUI_TITLE, style);
		fp_title.setAlignment(Align.center);
		fp_title.setPosition(fp_elements.getX() - (fp_title.getWidth() / 2), fp_elements.getY() + 150);
		
		addActor(fp_title);
	}
	
	@Override
	public void populate() {
		fp_username = new TextField(Strings.EMPTY, new TextFieldStyle(textStyle));
		fp_email = new TextField(Strings.EMPTY, new TextFieldStyle(textStyle));
		fp_tempPass = new TextField(Strings.EMPTY, new TextFieldStyle(textStyle));
		fp_newPass = new TextField(Strings.EMPTY, new TextFieldStyle(textStyle));
		fp_verifyNewPass = new TextField(Strings.EMPTY, new TextFieldStyle(textStyle));
		
		fp_username.setMessageText(Strings.LNUI_USERNAME);
		fp_email.setMessageText(Strings.LNUI_EMAIL);
		fp_tempPass.setMessageText(Strings.LNUI_TEMP_PASSWORD);
		fp_newPass.setMessageText(Strings.LNUI_NEW_PASSWORD);
		fp_verifyNewPass.setMessageText(Strings.LNUI_VERIFY_PASSWORD);
		
		fp_newPass.setPasswordMode(true);
		fp_verifyNewPass.setPasswordMode(true);
		
		fp_tempPass.setVisible(false);
		fp_newPass.setVisible(false);
		fp_verifyNewPass.setVisible(false);

		fp_errors = new Label("", labelStyle);

		fp_submit = new TextButton(Strings.LNUI_RESET_PASSWORD, noBackgroundButton);
		fp_submit.setSize(250, buttonStyle.font.getLineHeight() + 10);
		fp_submit.align(Align.center);

		fp_back = new TextButton(Strings.LNUI_BACK, buttonStyle);
		fp_back.setBackground(textStyle.background);
		fp_back.setSize(250, buttonStyle.font.getLineHeight() + 10);
		fp_back.align(Align.center);

		fp_alreadyHasTempPass = new CheckBox("Check here if you have already received a temporary password",
				new Skin(new FileHandle(Strings.URL_SKINS_DEFAULT_FILE),
						new TextureAtlas(new FileHandle(Strings.URL_SKINS_DEFAULT_ATLAS))));
		fp_alreadyHasTempPass.setSize(32, 32);
		fp_alreadyHasTempPass.setClip(false);

		fp_alreadyHasTempPass.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (fp_alreadyHasTempPass.isChecked()) {
					showAdditionalFPUI();
				} else {
					hideAdditionalFPUI();
				}
			}
		});

		fp_verifyNewPass.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					fp_verifyNewPass.getStyle().fontColor = Colors.TEXT_DEFAULT;
					if (fp_verifyNewPass.getText().equals(Strings.LNUI_VERIFY_PASSWORD)) {
						fp_verifyNewPass.setText("");
					}
				}
			}
		});

		fp_newPass.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					fp_newPass.getStyle().fontColor = Colors.TEXT_DEFAULT;
					if (fp_newPass.getText().equals(Strings.LNUI_NEW_PASSWORD)) {
						fp_newPass.setText("");
					}
				}
			}
		});

		fp_tempPass.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					fp_tempPass.getStyle().fontColor = Colors.TEXT_DEFAULT;
					if (fp_tempPass.getText().equals(Strings.LNUI_TEMP_PASSWORD)) {
						fp_tempPass.setText("");
					}
				}
			}
		});

		fp_username.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					fp_username.getStyle().fontColor = Colors.TEXT_DEFAULT;
					if (fp_username.getText().equals(Strings.LNUI_USERNAME)) {
						fp_username.setText("");
					}
				}
			}
		});

		fp_email.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					fp_email.getStyle().fontColor = Colors.TEXT_DEFAULT;
					if (fp_email.getText().equals(Strings.LNUI_EMAIL)) {
						fp_email.setText("");
					}
				}
			}
		});

		fp_submit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				fp_errors.getStyle().fontColor = Colors.TEXT_INFO;
				fp_errors.setText("Loading...");
				try {
					String whereClause = "email = " + "\'" + fp_email.getText() + "\'";
					DataQueryBuilder queryBuilder = DataQueryBuilder.create();
					queryBuilder.setWhereClause(whereClause);
					List<BackendlessUser> result = Backendless.Data.of(BackendlessUser.class).find(queryBuilder);
					if (result.size() > 0) {
						Strings.USER_ID = result.get(0).getObjectId();
					}
				} catch (BackendlessException e) {
					fp_errors.getStyle().fontColor = Colors.TEXT_ERROR;
					fp_errors.setText("User with that email username combo does not exist");
					return;
				}
				if (!fp_verifyNewPass.isVisible()) {
					submitPasswordReset(fp_username.getText(), fp_email.getText());
				} else {
					try {
						if (fp_verifyNewPass.getText().equals(fp_newPass.getText())) {
							try {
								Launcher.currentUser = Backendless.UserService.login(fp_username.getText(), fp_tempPass.getText());
							} catch (BackendlessException e) {
								System.err.println("line 706");
								return;
							}

							if (Launcher.currentUser.getUserId().equals(Strings.USER_ID)) {

								String password = fp_newPass.getText();

								int min_length = 8;

								boolean lengthReq = password.length() >= min_length;
								boolean specialCharReq = !password.matches("[a-zA-Z0-9]");
								boolean numReq = password.matches(".*\\d.*");

								boolean strongEnough = lengthReq && specialCharReq && numReq;

								if (strongEnough) {
									Launcher.currentUser.setPassword(password);
									Backendless.Data.of(BackendlessUser.class).save(Launcher.currentUser, new AsyncCallback<BackendlessUser>() {
										
										@Override
										public void handleResponse(BackendlessUser response) {
											fp_errors.getStyle().fontColor = Colors.TEXT_INFO;
											fp_errors.setText("Successfully changed password");
										}
										
										@Override
										public void handleFault(BackendlessFault fault) {
											fp_errors.getStyle().fontColor = Colors.TEXT_ERROR;
											fp_errors.setText(fault.getMessage());
										}
									});
								} else {
									String errorMessage = "";
									fp_errors.getStyle().fontColor = Colors.TEXT_ERROR;
									fp_errors.setText(Strings.EMPTY);
									if (!lengthReq)
										errorMessage += "Password must be at least 8 characters; ";
									if (!specialCharReq)
										errorMessage += "Password must contain a special character; ";
									if (!numReq)
										errorMessage += "Password must contain a number";
									fp_errors.setText(errorMessage);
								}
							} else {
								fp_errors.setText("");
								System.out.println(Launcher.currentUser.getUserId());
								System.out.println(Strings.USER_ID);
								System.err.println("Invalid login");
							}
						} else {
							fp_errors.getStyle().fontColor = Colors.TEXT_ERROR;
							fp_errors.setText(Strings.ERROR_BPA0002);
						}

					} catch (BackendlessException e) {
						fp_errors.getStyle().fontColor = Colors.TEXT_ERROR;
						fp_errors.setText("User with that email username combo does not exist");
						return;
					}
				}
			}
		});

		fp_back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.previousStage);
			}
		});
		
		final ForgotPasswordScreen fp_stage = this;
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (fp_verifyNewPass.isVisible()) {
					if (!isClickingOnUIElement(fp_elements, x, y, fp_email, fp_submit, fp_username, fp_verifyNewPass,
							fp_newPass, fp_tempPass, fp_back)) {
						fp_stage.unfocusAll();
					}
				} else {
					if (!isClickingOnUIElement(fp_elements, x, y, fp_email, fp_submit, fp_username, fp_back)) {
						fp_stage.unfocusAll();
					}
				}
				super.clicked(event, x, y);
			}
		});

		fp_username.setAlignment(Align.center);
		fp_email.setAlignment(Align.center);
		fp_newPass.setAlignment(Align.center);
		fp_verifyNewPass.setAlignment(Align.center);
		fp_tempPass.setAlignment(Align.center);
		fp_errors.setAlignment(Align.center);
		fp_alreadyHasTempPass.align(Align.center);

		fp_elements.addActor(fp_back);
		fp_elements.addActor(fp_username);
		fp_elements.addActor(fp_email);
		fp_elements.addActor(fp_submit);
		fp_elements.addActor(fp_errors);
		fp_elements.addActor(fp_alreadyHasTempPass);
		
		addActor(fp_elements);
	}
	
	public void hideAdditionalFPUI() {
		fp_elements.moveBy(0, 50);
		fp_tempPass.setVisible(false);
		fp_newPass.setVisible(false);
		fp_verifyNewPass.setVisible(false);
		fp_elements.removeActor(fp_tempPass);
		fp_elements.removeActor(fp_newPass);
		fp_elements.removeActor(fp_verifyNewPass);
		fp_elements.act(Gdx.graphics.getDeltaTime());
	}

	public void showAdditionalFPUI() {
		fp_elements.moveBy(0, -50);
		fp_tempPass.setVisible(true);
		fp_newPass.setVisible(true);
		fp_verifyNewPass.setVisible(true);
		fp_elements.removeActor(fp_alreadyHasTempPass);
		fp_elements.removeActor(fp_submit);
		fp_elements.addActor(fp_tempPass);
		fp_elements.addActor(fp_newPass);
		fp_elements.addActor(fp_verifyNewPass);
		fp_elements.addActor(fp_submit);
		fp_elements.addActor(fp_alreadyHasTempPass);
		fp_elements.act(Gdx.graphics.getDeltaTime());
	}
	
	public void submitPasswordReset(String username, String email) {
		boolean isTruthful = false;

		fp_errors.getStyle().fontColor = Colors.TEXT_INFO;
		fp_errors.setText("Loading...");
		
		try {
			String whereClause = "email = " + "\'" + email + "\'";
			DataQueryBuilder queryBuilder = DataQueryBuilder.create();
			queryBuilder.setWhereClause(whereClause);
			List<BackendlessUser> result = Backendless.Data.of(BackendlessUser.class).find(queryBuilder);
			if (result.size() > 0) {
				Strings.USER_ID = result.get(0).getObjectId();
			}
			isTruthful = true;
		} catch (BackendlessException e) {
			fp_errors.getStyle().fontColor = Colors.TEXT_ERROR;
			fp_errors.setText("User with that email username combo does not exist");
		}

		System.out.println(Strings.USER_ID);

		if (!isTruthful) {
			fp_errors.getStyle().fontColor = Colors.TEXT_ERROR;
			fp_errors.setText("User with that email username combo does not exist");
			return;
		} else {
			Backendless.UserService.restorePassword(username, new AsyncCallback<Void>() {
				@Override
				public void handleFault(BackendlessFault fault) {
					fp_errors.getStyle().fontColor = Colors.TEXT_ERROR;
					fp_errors.setText(fault.getMessage());
				}

				@Override
				public void handleResponse(Void response) {
					fp_errors.getStyle().fontColor = Colors.TEXT_INFO;
					fp_errors.setText(
							"Please input the password sent to you in the " + Strings.LNUI_TEMP_PASSWORD + " box");
					fp_tempPass.setVisible(true);
					fp_newPass.setVisible(true);
					fp_verifyNewPass.setVisible(true);
					fp_elements.removeActor(fp_submit);
					fp_elements.addActor(fp_tempPass);
					fp_elements.addActor(fp_newPass);
					fp_elements.addActor(fp_verifyNewPass);
					fp_elements.addActor(fp_submit);
					fp_elements.act(Gdx.graphics.getDeltaTime());
					System.out.println("Sent temp password to " + fp_email.getText());
				}
			});
		}
	}
	
	@Override
	public void clearFields() {
		for (Actor a : fp_elements.getChildren()) {
			if (a instanceof TextField) {
				((TextField) a).setText(Strings.EMPTY);
			} else if (a instanceof CheckBox) {
				((CheckBox) a).setChecked(false);
			} else if (a instanceof Label) {
				((Label) a).setText(Strings.EMPTY);
			}
		}
	}
}