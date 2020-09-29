package com.cognitivethought.bpa.game;

import java.util.HashMap;
import java.util.Map;

import com.backendless.Backendless;
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
	
	@SuppressWarnings("rawtypes")
	@Override
	public void create () {
		Backendless.initApp(Strings.APP_ID, Strings.SECRET_KEY);
		
		HashMap<String, String> test = new HashMap<>();
		test.put("first", "second");
		Backendless.Data.of("Test").save(test, new AsyncCallback<Map>() {

			@Override
			public void handleResponse(Map response) {
				System.out.println("Item saved");
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				System.err.println("<ERROR>: Server reported an error: " + fault.getMessage());
			}
			
		});
		
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
