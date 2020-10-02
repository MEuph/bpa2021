package com.cognitivethought.bpa.launcher;

import java.util.List;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.cognitivethought.bpa.Strings;

public class Launcher extends ApplicationAdapter {

	Stage na_stage;
	Stage login_stage;
	Stage fp_stage;
	Stage currentStage;
	Stage previousStage;

	BackendlessUser currentUser = null;

	VerticalGroup na_elements;
	
	
	
	TextField na_username, na_email, na_password;
	TextButton na_createUser, na_forgotPassword, na_back;
	Label na_errors;

	VerticalGroup login_elements;

	TextField login_username, login_password;
	TextButton login_submit, login_forgotPassword, login_newAccount;
	Label login_errors;

	Label title;

	VerticalGroup fp_elements;

	TextField fp_username, fp_email, fp_tempPass, fp_newPass, fp_verifyNewPass;
	TextButton fp_submit, fp_back, fp_resetPass;
	CheckBox fp_alreadyHasTempPass;
	Label fp_errors;

	BitmapFont font;

	@Override
	public void create() {
		Backendless.initApp(Strings.APP_ID, Strings.SECRET_KEY);

		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_UBUNTU_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 15;
		font = gen.generateFont(param);

		na_elements = new VerticalGroup();
		login_elements = new VerticalGroup();
		fp_elements = new VerticalGroup();

		na_elements.align(Align.center);
		login_elements.align(Align.center);
		fp_elements.align(Align.center);

		na_elements.space(20f);
		login_elements.space(20f);
		fp_elements.space(20f);

		na_elements.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		login_elements.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		fp_elements.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

		param.size = 50;
		BitmapFont titleFont = gen.generateFont(param);

		LabelStyle style = new LabelStyle();
		style.font = titleFont;
		style.fontColor = Color.RED;

		title = new Label(Strings.LNUI_TITLE, style);
		title.setAlignment(Align.center);
		title.setPosition(na_elements.getX() - (title.getWidth() / 2), na_elements.getY() + 150);

		Label loginTitle;
		Label na_title;
		Label fp_title;

		loginTitle = new Label(Strings.LNUI_TITLE, style);
		na_title = new Label(Strings.LNUI_TITLE, style);
		fp_title = new Label(Strings.LNUI_TITLE, style);

		loginTitle.setAlignment(Align.center);
		na_title.setAlignment(Align.center);
		fp_title.setAlignment(Align.center);

		loginTitle.setPosition(login_elements.getX() - (loginTitle.getWidth() / 2), login_elements.getY() + 150);
		na_title.setPosition(na_elements.getX() - (na_title.getWidth() / 2), na_elements.getY() + 150);
		fp_title.setPosition(fp_elements.getX() - (fp_title.getWidth() / 2), fp_elements.getY() + 150);

		gen.dispose();

		na_stage = new Stage();
		login_stage = new Stage();
		fp_stage = new Stage();

		na_stage.addActor(na_title);
		login_stage.addActor(loginTitle);
		fp_stage.addActor(fp_title);

		currentStage = login_stage;
		Gdx.input.setInputProcessor(login_stage);

		populateUI();
	}

	public void setStage(Stage s) {
		if (currentStage != null)
			currentStage.unfocusAll();
		previousStage = currentStage;
		currentStage = s;
		Gdx.input.setInputProcessor(s);
	}

	public void login(String email, String password) {
		login_errors.setText("Loading...");
		Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleFault(BackendlessFault fault) {
				switch (fault.getCode()) {
				case "3003":
					login_errors.setText(Strings.ERROR_BE3003 + " Code BE3003");
					break;
				default:
					login_errors.setText("Error logging in! " + fault.getMessage() + " Code BE" + fault.getCode());
					break;
				}
			}

			@Override
			public void handleResponse(BackendlessUser response) {
				System.out.println("Successfully logged in as " + response.getProperty("name"));
			}
		});
	}

	public void submitPasswordReset(String username, String email) {
		boolean isTruthful = false;
		
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
			fp_errors.setText("User with that email username combo does not exist");
		}

		System.out.println(Strings.USER_ID);

		if (!isTruthful) {
			fp_errors.setText("User with that email username combo does not exist");
			return;
		} else {
			Backendless.UserService.restorePassword(username, new AsyncCallback<Void>() {
				@Override
				public void handleFault(BackendlessFault fault) {
					fp_errors.setText(fault.getMessage());
				}

				@Override
				public void handleResponse(Void response) {
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

	public void createUser(String username, String email, String password) {
		BackendlessUser createUser = new BackendlessUser();

		createUser.setEmail(email);
		createUser.setPassword(password);
		createUser.setProperty("name", username);
		
		na_errors.setText("Loading...");
		
		Backendless.UserService.register(createUser, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleFault(BackendlessFault fault) {
				switch (fault.getCode()) {
				case "3033":
					na_errors.setText(Strings.ERROR_BE3033 + " Code BE3033");
					break;
				default:
					na_errors.setText(fault.getMessage() + " Code BE" + fault.getCode());
					break;
				}
			}

			@Override
			public void handleResponse(BackendlessUser response) {
				na_errors.setText("Check your email to verify your email address, " + response.getEmail());
			}
		});
	}

	@SuppressWarnings("unused")
	private boolean isClickingOnUIElement(float mouseX, float mouseY, Actor... actors) {
		for (Actor a : actors) {
			if (new Rectangle(a.getX(), a.getY(), a.getWidth(), a.getHeight()).contains(mouseX, mouseY)) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	private boolean isClickingOnUIElement(Actor container, float mouseX, float mouseY, Actor... actors) {
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

	private void populateUI() {
		// -- GENERAL -- //
		TextFieldStyle textStyle = new TextFieldStyle();
		textStyle.font = font;
		Pixmap bgColor = new Pixmap(250, (int) textStyle.font.getLineHeight(), Pixmap.Format.RGB888);
		bgColor.setColor(Color.WHITE);
		bgColor.fill();
		textStyle.fontColor = Color.GRAY;
		textStyle.background = new Image(new Texture(bgColor)).getDrawable();
		textStyle.background.setLeftWidth(textStyle.background.getLeftWidth());

		TextButtonStyle buttonStyle = new TextButtonStyle();
		bgColor = new Pixmap(100, (int) textStyle.font.getLineHeight(), Pixmap.Format.RGB888);
		bgColor.setColor(Color.DARK_GRAY);
		bgColor.fill();
		buttonStyle.down = new Image(new Texture(bgColor)).getDrawable();
		buttonStyle.up = new Image(new Texture(bgColor)).getDrawable();
		buttonStyle.font = font;
		buttonStyle.fontColor = Color.WHITE;
		buttonStyle.overFontColor = Color.RED;
		buttonStyle.downFontColor = Color.GRAY;

		TextButtonStyle noBackgroundButton = new TextButtonStyle();
		noBackgroundButton.font = font;
		noBackgroundButton.fontColor = Color.WHITE;
		noBackgroundButton.overFontColor = Color.RED;
		noBackgroundButton.downFontColor = Color.GRAY;

		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_UBUNTU_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 12;

		Pixmap labelBg = new Pixmap(200, 200, Pixmap.Format.RGB888);
		labelBg.setColor(Color.BLACK);
		labelBg.fill();

		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = gen.generateFont(param);
		labelStyle.fontColor = Color.RED;
		labelStyle.background = new Image(new Texture(labelBg)).getDrawable();

		// -- NEW ACCOUNT UI -- //
		na_email = new TextField(Strings.LNUI_EMAIL, new TextFieldStyle(textStyle));
		na_password = new TextField(Strings.LNUI_PASSWORD, new TextFieldStyle(textStyle));
		na_username = new TextField(Strings.LNUI_USERNAME, new TextFieldStyle(textStyle));

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
				setStage(previousStage);
			}
		});

		na_forgotPassword.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setStage(fp_stage);
			}
		});

		na_createUser.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				int min_length = 8;
				
				na_errors.setText("Loading");
				
				String password = na_password.getText();
				
				boolean lengthReq = password.length() >= min_length;
				boolean specialCharReq = !password.matches("[a-zA-Z0-9]");
				boolean numReq = password.matches(".*\\d.*");

				boolean strongEnough = lengthReq && specialCharReq && numReq;

				if (strongEnough) {
					createUser(na_username.getText(), na_email.getText(), na_password.getText());
				} else {
					String errorMessage = "";
					na_errors.setText(errorMessage);
					if (!lengthReq)
						errorMessage += "Password must be at least 8 characters; ";
					if (!specialCharReq)
						errorMessage += "Password must contain a special character; ";
					if (!numReq)
						errorMessage += "Password must contain a number";
					na_errors.setText(errorMessage);
				}
			}
		});

		na_email.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					na_email.getStyle().fontColor = Color.RED;
					if (na_email.getText().equals(Strings.LNUI_EMAIL)) {
						na_email.setText("");
					}
				} else if (!focused) {
					na_email.getStyle().fontColor = Color.BLACK;
					if (na_email.getText().equals("")) {
						na_email.setText(Strings.LNUI_EMAIL);
						na_email.getStyle().fontColor = Color.GRAY;
					}
				}
			}
		});

		na_username.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					na_username.getStyle().fontColor = Color.RED;
					if (na_username.getText().equals(Strings.LNUI_USERNAME)) {
						na_username.setText("");
					}
				} else if (!focused) {
					na_username.getStyle().fontColor = Color.BLACK;
					if (na_username.getText().equals("")) {
						na_username.setText(Strings.LNUI_USERNAME);
						na_username.getStyle().fontColor = Color.GRAY;
					}
				}
			}
		});

		na_password.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					na_password.getStyle().fontColor = Color.RED;
					if (na_password.getText().equals(Strings.LNUI_PASSWORD)) {
						na_password.setText("");
					}
				} else if (!focused) {
					na_password.getStyle().fontColor = Color.BLACK;
					if (na_password.getText().equals("")) {
						na_password.setText(Strings.LNUI_PASSWORD);
						na_password.getStyle().fontColor = Color.GRAY;
					}
				}
			}
		});

		na_stage.addListener(new ClickListener() {
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

		// -- LOGIN UI -- //
		login_password = new TextField(Strings.LNUI_PASSWORD, new TextFieldStyle(textStyle));
		login_username = new TextField(Strings.LNUI_USERNAME, new TextFieldStyle(textStyle));

		login_submit = new TextButton(Strings.LNUI_LOGIN, buttonStyle);
		login_submit.setSize(250, buttonStyle.font.getLineHeight() + 10);
		login_submit.align(Align.center);

		login_errors = new Label("", labelStyle);

		login_forgotPassword = new TextButton(Strings.LNUI_FORGOT_PASSWORD, noBackgroundButton);
		login_forgotPassword.setSize(100, noBackgroundButton.font.getLineHeight() + 10);
		login_forgotPassword.align(Align.center);

		login_newAccount = new TextButton(Strings.LNUI_CREATE_ACCOUNT, noBackgroundButton);
		login_newAccount.setSize(200, noBackgroundButton.font.getLineHeight() + 10);
		login_newAccount.align(Align.center);

		login_newAccount.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setStage(na_stage);
			}
		});

		login_forgotPassword.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setStage(fp_stage);
			}
		});

		login_submit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				login(login_username.getText(), login_password.getText());
			}
		});

		login_username.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					login_username.getStyle().fontColor = Color.RED;
					if (login_username.getText().equals(Strings.LNUI_USERNAME)) {
						login_username.setText("");
					}
				} else if (!focused) {
					login_username.getStyle().fontColor = Color.BLACK;
					if (login_username.getText().equals("")) {
						login_username.setText(Strings.LNUI_USERNAME);
						login_username.getStyle().fontColor = Color.GRAY;
					}
				}
			}
		});

		login_password.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					login_password.getStyle().fontColor = Color.RED;
					if (login_password.getText().equals(Strings.LNUI_PASSWORD)) {
						login_password.setText("");
					}
				} else if (!focused) {
					login_password.getStyle().fontColor = Color.BLACK;
					if (login_password.getText().equals("")) {
						login_password.setText(Strings.LNUI_PASSWORD);
						login_password.getStyle().fontColor = Color.GRAY;
					}
				}
			}
		});

		login_stage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isClickingOnUIElement(login_elements, x, y, login_password, login_username, login_submit, login_newAccount)) {
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

		// -- FORGOT PASSWORD -- //
		fp_username = new TextField(Strings.LNUI_USERNAME, new TextFieldStyle(textStyle));
		fp_email = new TextField(Strings.LNUI_EMAIL, new TextFieldStyle(textStyle));
		fp_tempPass = new TextField(Strings.LNUI_TEMP_PASSWORD, new TextFieldStyle(textStyle));
		fp_newPass = new TextField(Strings.LNUI_NEW_PASSWORD, new TextFieldStyle(textStyle));
		fp_verifyNewPass = new TextField(Strings.LNUI_VERIFY_PASSWORD, new TextFieldStyle(textStyle));

		fp_tempPass.setVisible(false);
		fp_newPass.setVisible(false);
		fp_verifyNewPass.setVisible(false);

		fp_errors = new Label("", labelStyle);

		fp_submit = new TextButton(Strings.LNUI_RESET_PASSWORD, buttonStyle);
		fp_submit.setBackground(textStyle.background);
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

		fp_resetPass = new TextButton(Strings.LNUI_RESET_PASSWORD, buttonStyle);
		fp_resetPass.setBackground(textStyle.background);
		fp_resetPass.setSize(250, buttonStyle.font.getLineHeight() + 10);
		fp_resetPass.align(Align.center);

		fp_resetPass.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				resetPassword(fp_newPass.getText(), fp_verifyNewPass.getText(), fp_tempPass.getText());
			}
		});

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
					fp_verifyNewPass.getStyle().fontColor = Color.RED;
					if (fp_verifyNewPass.getText().equals(Strings.LNUI_VERIFY_PASSWORD)) {
						fp_verifyNewPass.setText("");
					}
				} else if (!focused) {
					fp_verifyNewPass.getStyle().fontColor = Color.BLACK;
					if (fp_verifyNewPass.getText().equals("")) {
						fp_verifyNewPass.setText(Strings.LNUI_VERIFY_PASSWORD);
						fp_verifyNewPass.getStyle().fontColor = Color.GRAY;
					}
				}
			}
		});

		fp_newPass.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					fp_newPass.getStyle().fontColor = Color.RED;
					if (fp_newPass.getText().equals(Strings.LNUI_NEW_PASSWORD)) {
						fp_newPass.setText("");
					}
				} else if (!focused) {
					fp_newPass.getStyle().fontColor = Color.BLACK;
					if (fp_newPass.getText().equals("")) {
						fp_newPass.setText(Strings.LNUI_NEW_PASSWORD);
						fp_newPass.getStyle().fontColor = Color.GRAY;
					}
				}
			}
		});

		fp_tempPass.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					fp_tempPass.getStyle().fontColor = Color.RED;
					if (fp_tempPass.getText().equals(Strings.LNUI_TEMP_PASSWORD)) {
						fp_tempPass.setText("");
					}
				} else if (!focused) {
					fp_tempPass.getStyle().fontColor = Color.BLACK;
					if (fp_tempPass.getText().equals("")) {
						fp_tempPass.setText(Strings.LNUI_TEMP_PASSWORD);
						fp_tempPass.getStyle().fontColor = Color.GRAY;
					}
				}
			}
		});

		fp_username.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					fp_username.getStyle().fontColor = Color.RED;
					if (fp_username.getText().equals(Strings.LNUI_USERNAME)) {
						fp_username.setText("");
					}
				} else if (!focused) {
					fp_username.getStyle().fontColor = Color.BLACK;
					if (fp_username.getText().equals("")) {
						fp_username.setText(Strings.LNUI_USERNAME);
						fp_username.getStyle().fontColor = Color.GRAY;
					}
				}
			}
		});

		fp_email.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					fp_email.getStyle().fontColor = Color.RED;
					if (fp_email.getText().equals(Strings.LNUI_EMAIL)) {
						fp_email.setText("");
					}
				} else if (!focused) {
					fp_email.getStyle().fontColor = Color.BLACK;
					if (fp_email.getText().equals("")) {
						fp_email.setText(Strings.LNUI_EMAIL);
						fp_email.getStyle().fontColor = Color.GRAY;
					}
				}
			}
		});

		fp_submit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
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
					fp_errors.setText("User with that email username combo does not exist");
					return;
				}
				if (!fp_verifyNewPass.isVisible()) {
					submitPasswordReset(fp_username.getText(), fp_email.getText());
				} else {
					try {
						if (fp_verifyNewPass.getText().equals(fp_newPass.getText())) {
							try {
								currentUser = Backendless.UserService.login(fp_username.getText(), fp_tempPass.getText());
							} catch (BackendlessException e) {
								System.err.println("line 706");
								return;
							}

							if (currentUser.getUserId().equals(Strings.USER_ID)) {

								String password = fp_newPass.getText();

								int min_length = 8;

								boolean lengthReq = password.length() >= min_length;
								boolean specialCharReq = !password.matches("[a-zA-Z0-9]");
								boolean numReq = password.matches(".*\\d.*");

								boolean strongEnough = lengthReq && specialCharReq && numReq;

								if (strongEnough) {
									currentUser.setPassword(password);
									Backendless.Data.of(BackendlessUser.class).save(currentUser, new AsyncCallback<BackendlessUser>() {
										
										@Override
										public void handleResponse(BackendlessUser response) {
											System.out.println("Set password to " + fp_newPass.getText());
										}
										
										@Override
										public void handleFault(BackendlessFault fault) {
											System.err.println(fault.getMessage());
										}
									});
								} else {
									String errorMessage = "";
									fp_errors.setText(errorMessage);
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
								System.out.println(currentUser.getUserId());
								System.out.println(Strings.USER_ID);
								System.err.println("Invalid login");
							}
						} else {
							fp_errors.setText(Strings.ERROR_BPA0002);
						}

					} catch (BackendlessException e) {
						fp_errors.setText("User with that email username combo does not exist");
						return;
					}
				}
			}
		});

		fp_back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setStage(previousStage);
			}
		});

		fp_stage.addListener(new ClickListener() {
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

		// -- FINALIZING -- //
		login_stage.addActor(login_elements);
		na_stage.addActor(na_elements);
		fp_stage.addActor(fp_elements);
	}

	private void resetPassword(String newPass, String verifyNewPass, String tempPass) {

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

	public void update() {

	}

	@Override
	public void render() {
		update();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		currentStage.draw();
	}

	@Override
	public void dispose() {
		na_stage.dispose();
		fp_stage.dispose();
		login_stage.dispose();
	}
}