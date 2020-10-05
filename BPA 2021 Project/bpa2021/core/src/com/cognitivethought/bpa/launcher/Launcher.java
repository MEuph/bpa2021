package com.cognitivethought.bpa.launcher;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.cognitivethought.bpa.Strings;

public class Launcher extends ApplicationAdapter {

	public static LauncherScreen na_stage;
	public static LauncherScreen login_stage;
	public static LauncherScreen fp_stage;
	public static LauncherScreen currentStage;
	public static LauncherScreen previousStage;

	public static BackendlessUser currentUser = null;
	
	@Override
	public void create() {
		Backendless.initApp(Strings.APP_ID, Strings.SECRET_KEY);

		na_stage = new NewAccountScreen();
		login_stage = new LoginScreen();
		fp_stage = new ForgotPasswordScreen();

		currentStage = login_stage;
		Gdx.input.setInputProcessor(login_stage);
		
		na_stage.populate();
		login_stage.populate();
		fp_stage.populate();
		
//		populateUI();
	}

	public static void setStage(LauncherScreen s) {
		if (currentStage != null)
			currentStage.unfocusAll();
		previousStage = currentStage;
		currentStage = s;
		previousStage.clearFields();
		Gdx.input.setInputProcessor(s);
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