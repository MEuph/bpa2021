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
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.cognitivethought.bpa.Strings;

public class Launcher extends ApplicationAdapter {
	
	Stage stage;
	
	TextField email, password;
	TextButton submit;
	
	BitmapFont font;
	
	@Override
	public void create () {
		Backendless.initApp(Strings.APP_ID, Strings.SECRET_KEY);
		
		font = new BitmapFont();
		
		stage = new Stage();
		
		Gdx.input.setInputProcessor(stage);
		
		populateUI();
	}
	
	public void createUser(String email, String password) {
		BackendlessUser createUser = new BackendlessUser();
		
		createUser.setEmail(email);
		createUser.setPassword(password);
		
		Backendless.UserService.register(createUser, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleFault(BackendlessFault fault) {
				System.out.printf("\n", fault.getCode(), "\n", fault.getDetail(), "\n", fault.getMessage());
			}
			
			@Override
			public void handleResponse(BackendlessUser response) {
				System.out.println("Successfully created new user, " + response.getEmail());
			}
		});
	}
	
	private boolean isClickingOnUIElement(float mouseX, float mouseY, Actor ... actors) {
		for (Actor a : actors) {
			if (new Rectangle(a.getX(), a.getY(), a.getWidth(), a.getHeight()).contains(mouseX, mouseY)) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}
	
	private void populateUI() {		
		TextFieldStyle textStyle = new TextFieldStyle();
		textStyle.fontColor = Color.GRAY;
		textStyle.font = font;
		
		TextButtonStyle buttonStyle = new TextButtonStyle();
		
		Pixmap bgColor = new Pixmap(200, (int)textStyle.font.getLineHeight(), Pixmap.Format.RGB888);
		bgColor.setColor(Color.WHITE);
		bgColor.fill();
		
		textStyle.background = new Image(new Texture(bgColor)).getDrawable();
		
		email = new TextField(Strings.LNUI_EMAIL, new TextFieldStyle(textStyle));
		password = new TextField(Strings.LNUI_PASSWORD, new TextFieldStyle(textStyle));
		
		submit = new TextButton(Strings.LNUI_SUBMIT, buttonStyle);
		submit.setBackground(textStyle.background);
		submit.setSize(200, font.getLineHeight() + 10);
		submit.setPosition(100, 145);
		
		email.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					email.getStyle().fontColor = Color.RED;
					if (email.getText().equals(Strings.LNUI_EMAIL)) {
						email.setText("");
					}
				} else if (!focused) {
					email.getStyle().fontColor = Color.BLACK;
					if (email.getText().equals("")) {
						email.setText(Strings.LNUI_EMAIL);
						email.getStyle().fontColor = Color.GRAY;
					}
				}
			}
		});
		
		password.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					password.getStyle().fontColor = Color.RED;
					if (password.getText().equals(Strings.LNUI_PASSWORD)) {
						password.setText("");
					}
				} else if (!focused) {
					password.getStyle().fontColor = Color.BLACK;
					if (password.getText().equals("")) {
						password.setText(Strings.LNUI_PASSWORD);
						password.getStyle().fontColor = Color.GRAY;
					}
				}
			}
		});
		
		stage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isClickingOnUIElement(x, y, email, password, submit)) {
					stage.unfocusAll();
				}
				super.clicked(event, x, y);
			}
		});
		
		email.setSize(200, textStyle.font.getLineHeight());
		password.setSize(200, textStyle.font.getLineHeight());
		
		email.setPosition(100, 200);
		password.setPosition(100, 180);
		
		stage.addActor(email);
		stage.addActor(password);
		stage.addActor(submit);
	}
	
	
	
	public void update() {
		
	}
	
	@Override
	public void render () {
		update();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.draw();
		
	}
	
	@Override
	public void dispose () {
		stage.dispose();
	}
}
