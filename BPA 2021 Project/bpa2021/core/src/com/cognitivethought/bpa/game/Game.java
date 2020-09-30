package com.cognitivethought.bpa.game;

import java.util.Scanner;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cognitivethought.bpa.Strings;

public class Game extends ApplicationAdapter {
	
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		Backendless.initApp(Strings.APP_ID, Strings.SECRET_KEY);
		
		BackendlessUser createUser = new BackendlessUser();
		
		Scanner sc = new Scanner (System.in);
		
		System.out.println("Welcome to [GAME], please input your email here: ");
		String email = sc.nextLine();
		System.out.println("Please input your password here: ");
		String password = sc.nextLine();
		
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
		
		sc.close();
		
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
