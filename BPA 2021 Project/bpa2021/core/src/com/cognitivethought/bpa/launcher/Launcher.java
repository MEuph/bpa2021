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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.cognitivethought.bpa.Strings;

public class Launcher extends ApplicationAdapter {

	Stage newAccountStage;
	Stage loginStage;
	Stage forgotPasswordStage;
	Stage currentStage;
	Stage previousStage;

	BackendlessUser currentUser = null;

	VerticalGroup na_elements;

	boolean na_password_focused = false;
	TextField na_username, na_email, na_password;
	TextButton na_createUser, na_forgotPassword;

	VerticalGroup login_elements;

	boolean login_password_focused = false;
	TextField login_username, login_password;
	TextButton login_submit, login_newuser, login_forgotPassword;

	Label title;

	VerticalGroup fp_elements;

	boolean fp_email_focused = false;
	TextField fp_username, fp_email, fp_tempPass, fp_newPass, fp_verifyNewPass;
	TextButton fp_submit, fp_back;
	
	Label errorMessages;

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
		
		newAccountStage = new Stage();
		loginStage = new Stage();
		forgotPasswordStage = new Stage();

		newAccountStage.addActor(na_title);
		loginStage.addActor(loginTitle);
		forgotPasswordStage.addActor(fp_title);
		
		currentStage = loginStage;
		Gdx.input.setInputProcessor(loginStage);
		
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
		Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleFault(BackendlessFault fault) {
				switch (fault.getCode()) {
				case "3003":
					System.err.println(Strings.ERROR_BE3003);
					break;
				default:
					System.err.println("Error logging in! " + fault.getMessage());
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
		// If the email the user submitted does not match the email of the account,
		// don't do anything
		boolean isTruthful = false;
		String userId = "";

		try {
			String whereClause = "email = " + "\'" + email + "\'";
			DataQueryBuilder queryBuilder = DataQueryBuilder.create();
			queryBuilder.setWhereClause(whereClause);
			List<BackendlessUser> result = Backendless.Data.of(BackendlessUser.class).find(queryBuilder);
			if (result.size() > 0) {
				userId = result.get(0).getObjectId();
			}
			isTruthful = true;
		} catch (BackendlessException e) {
			System.err.println("User with that email username combo does not exist");
		}

		System.out.println(userId);

		if (!isTruthful) {
			return; // TODO: Notify user
		} else {
			Backendless.UserService.restorePassword(username, new AsyncCallback<Void>() {
				@Override
				public void handleFault(BackendlessFault fault) {
					System.err.println(fault.getMessage());
				}

				@Override
				public void handleResponse(Void response) {
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

		Backendless.UserService.register(createUser, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleFault(BackendlessFault fault) {
				switch (fault.getCode()) {
				case "3033":
					System.err.println(Strings.ERROR_BE3033);
					break;
				default:
					System.err.println(fault.getCode());
					break;
				}
			}

			@Override
			public void handleResponse(BackendlessUser response) {
				System.out.println("Successfully created new user, " + response.getProperty("name"));
				System.out.println("Email sent to new user at address " + response.getEmail());
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
		textStyle.background = new Image(new Texture(bgColor)).getDrawable();;
		textStyle.background.setLeftWidth(textStyle.background.getLeftWidth());

		TextButtonStyle buttonStyle = new TextButtonStyle();
		bgColor = new Pixmap(300, (int) textStyle.font.getLineHeight(), Pixmap.Format.RGB888);
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

		// -- NEW ACCOUNT UI -- //
		na_email = new TextField(Strings.LNUI_EMAIL, new TextFieldStyle(textStyle));
		na_password = new TextField(Strings.LNUI_PASSWORD, new TextFieldStyle(textStyle));
		na_username = new TextField(Strings.LNUI_USERNAME, new TextFieldStyle(textStyle));

		na_createUser = new TextButton(Strings.LNUI_CREATE_ACCOUNT, buttonStyle);
		na_createUser.setBackground(textStyle.background);
		na_createUser.setSize(250, font.getLineHeight() + 10);
		na_createUser.align(Align.center);

		na_forgotPassword = new TextButton(Strings.LNUI_FORGOT_PASSWORD, noBackgroundButton);
		na_forgotPassword.setSize(100, font.getLineHeight() + 10);
		na_forgotPassword.align(Align.center);

		na_forgotPassword = new TextButton(Strings.LNUI_FORGOT_PASSWORD, buttonStyle);

		na_forgotPassword.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setStage(forgotPasswordStage);
			}
		});

		na_createUser.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				createUser(na_username.getText(), na_email.getText(), na_password.getText());
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
				na_password_focused = focused;
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

		newAccountStage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isClickingOnUIElement(na_elements, x, y, na_email, na_password, na_username, na_createUser)) {
					newAccountStage.unfocusAll();
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
		na_elements.addActor(na_username);
		na_elements.addActor(na_email);
		na_elements.addActor(na_password);
		na_elements.addActor(na_createUser);

		// -- LOGIN UI -- //
		login_password = new TextField(Strings.LNUI_PASSWORD, new TextFieldStyle(textStyle));
		login_username = new TextField(Strings.LNUI_USERNAME, new TextFieldStyle(textStyle));

		login_submit = new TextButton(Strings.LNUI_LOGIN, buttonStyle);
		login_submit.setBackground(textStyle.background);
		login_submit.setSize(250, font.getLineHeight() + 10);
		login_submit.align(Align.center);

		login_forgotPassword = new TextButton(Strings.LNUI_FORGOT_PASSWORD, noBackgroundButton);
		login_forgotPassword.setSize(100, font.getLineHeight() + 10);
		login_forgotPassword.align(Align.center);

		login_forgotPassword = new TextButton(Strings.LNUI_FORGOT_PASSWORD, buttonStyle);

		login_forgotPassword.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setStage(forgotPasswordStage);
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
				login_password_focused = focused;
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

		loginStage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isClickingOnUIElement(login_elements, x, y, login_password, login_username, login_submit)) {
					loginStage.unfocusAll();
				}
				super.clicked(event, x, y);
			}
		});

		login_username.setSize(250, textStyle.font.getLineHeight() + 10);
		login_password.setSize(250, textStyle.font.getLineHeight() + 10);

		login_username.setAlignment(Align.center);
		login_password.setAlignment(Align.center);

		login_elements.addActor(login_forgotPassword);
		login_elements.addActor(login_username);
		login_elements.addActor(login_password);
		login_elements.addActor(login_submit);

		// -- FORGOT PASSWORD -- //
		fp_username = new TextField(Strings.LNUI_USERNAME, new TextFieldStyle(textStyle));
		fp_email = new TextField(Strings.LNUI_EMAIL, new TextFieldStyle(textStyle));
		fp_tempPass = new TextField(Strings.LNUI_TEMP_PASSWORD, new TextFieldStyle(textStyle));
		fp_newPass = new TextField(Strings.LNUI_NEW_PASSWORD, new TextFieldStyle(textStyle));
		fp_verifyNewPass = new TextField(Strings.LNUI_VERIFY_PASSWORD, new TextFieldStyle(textStyle));

		fp_tempPass.setVisible(false);
		fp_newPass.setVisible(false);
		fp_verifyNewPass.setVisible(false);

		fp_submit = new TextButton(Strings.LNUI_SUBMIT, buttonStyle);
		fp_submit.setBackground(textStyle.background);
		fp_submit.setSize(250, font.getLineHeight() + 10);
		fp_submit.align(Align.center);

		fp_back = new TextButton(Strings.LNUI_BACK, buttonStyle);
		fp_back.setBackground(textStyle.background);
		fp_back.setSize(250, font.getLineHeight() + 10);
		fp_submit.align(Align.center);

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
				fp_email_focused = focused;
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
				if (!fp_verifyNewPass.isVisible()) {
					submitPasswordReset(fp_username.getText(), fp_email.getText());
				} else {
					if (fp_verifyNewPass.getText().equals(fp_newPass.getText())) {
						// TODO: Enforce Password Requirements
					} else {
						// TODO: Inform user
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

		forgotPasswordStage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (fp_verifyNewPass.isVisible()) {
					if (!isClickingOnUIElement(fp_elements, x, y, fp_email, fp_submit, fp_username, fp_verifyNewPass,
							fp_newPass, fp_tempPass)) {
						forgotPasswordStage.unfocusAll();
					}
				} else {
					if (!isClickingOnUIElement(fp_elements, x, y, fp_email, fp_submit, fp_username)) {
						forgotPasswordStage.unfocusAll();
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
		
		fp_elements.addActor(fp_back);
		fp_elements.addActor(fp_username);
		fp_elements.addActor(fp_email);
		fp_elements.addActor(fp_submit);

		// -- FINALIZING -- //
		loginStage.addActor(login_elements);
		newAccountStage.addActor(na_elements);
		forgotPasswordStage.addActor(fp_elements);
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
		newAccountStage.dispose();
		forgotPasswordStage.dispose();
		loginStage.dispose();
	}
}