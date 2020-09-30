package com.cognitivethought.bpa.launcher;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
	Stage currentStage;

	BackendlessUser currentUser = null;

	VerticalGroup na_elements;

	TextField na_username, na_email, na_password;
	TextButton na_createUser;

	VerticalGroup login_elements;

	TextField login_username, login_password;
	TextButton login_submit;
	
	Label title;
	
	BitmapFont font;

	@Override
	public void create() {
		Backendless.initApp(Strings.APP_ID, Strings.SECRET_KEY);

		font = new BitmapFont();

		na_elements = new VerticalGroup();
		login_elements = new VerticalGroup();

		na_elements.align(Align.center);
		login_elements.align(Align.center);

		na_elements.space(20f);
		login_elements.space(20f);

		na_elements.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		login_elements.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		
		newAccountStage = new Stage();
		loginStage = new Stage();

		setStage(loginStage);

		populateUI();
	}

	public void setStage(Stage s) {
		if (currentStage != null)
			currentStage.unfocusAll();
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

	private boolean isClickingOnUIElement(Actor relativeTo, float mouseX, float mouseY, Actor... actors) {
		for (Actor a : actors) {
			if (new Rectangle(a.getX() + relativeTo.getX(), a.getY() + relativeTo.getY(), a.getWidth(), a.getHeight())
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
		Pixmap bgColor = new Pixmap(200, (int) textStyle.font.getLineHeight(), Pixmap.Format.RGB888);
		bgColor.setColor(Color.WHITE);
		bgColor.fill();
		textStyle.fontColor = Color.GRAY;
		textStyle.background = new Image(new Texture(bgColor)).getDrawable();
		textStyle.background.setLeftWidth(textStyle.background.getLeftWidth() + 5);
		
		TextButtonStyle buttonStyle = new TextButtonStyle();
		bgColor.setColor(Color.DARK_GRAY);
		bgColor.fill();
		buttonStyle.down = new Image(new Texture(bgColor)).getDrawable();
		buttonStyle.up = new Image(new Texture(bgColor)).getDrawable();
		buttonStyle.font = font;
		buttonStyle.fontColor = Color.WHITE;
		buttonStyle.overFontColor = Color.RED;
		buttonStyle.downFontColor = Color.GRAY;
		
		LabelStyle titleStyle = new LabelStyle();
		titleStyle.font = font;
		titleStyle.fontColor = Color.WHITE;
		bgColor.setColor(Color.WHITE);
		titleStyle.background = new Image(new Texture(bgColor)).getDrawable();
		title = new Label(Strings.LNUI_TITLE, titleStyle);
		title.setSize(200, 20);
		title.setPosition(100, 200);

		// -- NEW ACCOUNT UI -- //
		na_email = new TextField(Strings.LNUI_EMAIL, new TextFieldStyle(textStyle));
		na_password = new TextField(Strings.LNUI_PASSWORD, new TextFieldStyle(textStyle));
		na_username = new TextField(Strings.LNUI_USERNAME, new TextFieldStyle(textStyle));
		
		na_createUser = new TextButton(Strings.LNUI_CREATE_ACCOUNT, buttonStyle);
		na_createUser.setBackground(textStyle.background);
		na_createUser.setSize(200, font.getLineHeight() + 10);
		na_createUser.setPosition(100, 145);
		na_createUser.align(Align.center);
		
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

		na_username.setSize(200, textStyle.font.getLineHeight() + 10);
		na_email.setSize(200, textStyle.font.getLineHeight() + 10);
		na_password.setSize(200, textStyle.font.getLineHeight() + 10);
		
		na_username.setPosition(100, 220);
		na_email.setPosition(100, 200);
		na_password.setPosition(100, 180);
		
		na_username.setAlignment(Align.center);
		na_email.setAlignment(Align.center);
		na_password.setAlignment(Align.center);
		
		na_elements.addActor(na_username);
		na_elements.addActor(na_email);
		na_elements.addActor(na_password);
		na_elements.addActor(na_createUser);

		// -- LOGIN UI -- //
		login_password = new TextField(Strings.LNUI_PASSWORD, new TextFieldStyle(textStyle));
		login_username = new TextField(Strings.LNUI_USERNAME, new TextFieldStyle(textStyle));

		login_submit = new TextButton(Strings.LNUI_LOGIN, buttonStyle);
		login_submit.setBackground(textStyle.background);
		login_submit.setSize(200, font.getLineHeight() + 10);
		login_submit.setPosition(100, 145);
		login_submit.align(Align.center);
		
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

		loginStage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isClickingOnUIElement(login_elements, x, y, login_password, login_username, login_submit)) {
					loginStage.unfocusAll();
				}
				super.clicked(event, x, y);
			}
		});

		login_username.setSize(200, textStyle.font.getLineHeight() + 10);
		login_password.setSize(200, textStyle.font.getLineHeight() + 10);

		login_username.setPosition(100, 220);
		login_password.setPosition(100, 200);

		login_username.setAlignment(Align.center);
		login_password.setAlignment(Align.center);
		
		login_elements.addActor(login_username);
		login_elements.addActor(login_password);
		login_elements.addActor(login_submit);
		
		// -- FINALIZING -- //
		loginStage.addActor(login_elements);
		newAccountStage.addActor(na_elements);
		loginStage.addActor(title);
		newAccountStage.addActor(title);
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
		loginStage.dispose();
	}
}