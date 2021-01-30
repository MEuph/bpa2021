package com.cognitivethought.bpa.launcher;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.gamestages.GameStage;
import com.cognitivethought.bpa.gamestages.MainGameStage;
import com.cognitivethought.bpa.multiplayer.HostServerStage;
import com.cognitivethought.bpa.multiplayer.JoinServerStage;
import com.cognitivethought.bpa.multiplayer.MultiplayerQueueStage;
import com.cognitivethought.bpa.multiplayer.NuclearWarServer;
import com.cognitivethought.bpa.sound.Sounds;
import com.cognitivethought.bpa.tidiness.Strings;
import com.cognitivethought.bpa.uistages.ForgotPasswordStage;
import com.cognitivethought.bpa.uistages.GameMenuStage;
import com.cognitivethought.bpa.uistages.LauncherStage;
import com.cognitivethought.bpa.uistages.LoginStage;
import com.cognitivethought.bpa.uistages.MainLauncherStage;
import com.cognitivethought.bpa.uistages.NewAccountStage;
import com.cognitivethought.bpa.uistages.SettingsStage;
import com.cognitivethought.bpa.uistages.UIStage;

public class Launcher extends ApplicationAdapter {
	
	public ArrayList<Stage> stages = new ArrayList<Stage>();
	
	public static LauncherStage na_stage;
	public static LauncherStage login_stage;
	public static LauncherStage fp_stage;
	public static LauncherStage main_stage;

	public static Stage currentStage;
	public static Stage previousStage;

	public static UIStage game_menu_stage;
	public static UIStage js_stage;
	public static UIStage hs_stage;
	public static UIStage mq_stage;
	public static UIStage settings_stage;
	
	public static GameStage game_stage;
	
	public static BackendlessUser currentUser = null;
	
	public OrthographicCamera camera;
	
	public Viewport vp;
	
	public static boolean isFullscreen = false;
	
	static File logFile;
	static PrintStream err;
	
	public static void log() {
		err.println("Please email the following log file to bpamisd@gmail.com: " + logFile.getAbsolutePath());
	}
	
	@Override
	public void create() {
		Backendless.initApp(Strings.APP_ID, Strings.SECRET_KEY);
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
		File f = new File(Strings.URL_LOCATOR + "Logs/" + dtf.format(LocalDateTime.now()) + ".txt");
		
		try {
			f.createNewFile();

			logFile = f;
			
			err = System.err;
			
			System.setOut(new PrintStream(new FileOutputStream(f)));
			System.setErr(new PrintStream(new FileOutputStream(f)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		Sounds.load();
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0, 0, -100);
		camera.near = 0;
		camera.far = 1000000;
		
		vp = new ScalingViewport(Scaling.none, Toolkit.getDefaultToolkit().getScreenSize().width,
				Toolkit.getDefaultToolkit().getScreenSize().height, camera);

		na_stage = new NewAccountStage(vp);
		login_stage = new LoginStage(vp);
		fp_stage = new ForgotPasswordStage(vp);
		main_stage = new MainLauncherStage(vp);
		game_menu_stage = new GameMenuStage(vp);
		game_stage = new MainGameStage(vp);
		js_stage = new JoinServerStage(vp);
		hs_stage = new HostServerStage(vp);
		mq_stage = new MultiplayerQueueStage(vp);
		settings_stage = new SettingsStage(vp);
		
		na_stage.getViewport().apply();
		login_stage.getViewport().apply();
		fp_stage.getViewport().apply();
		main_stage.getViewport().apply();
		game_menu_stage.getViewport().apply();
		game_stage.getViewport().apply();
		js_stage.getViewport().apply();
		hs_stage.getViewport().apply();
		mq_stage.getViewport().apply();
		settings_stage.getViewport().apply();
		
		
		stages.add(na_stage);
		stages.add(login_stage);
		stages.add(fp_stage);
		stages.add(main_stage);
		stages.add(game_menu_stage);
		stages.add(game_stage);
		stages.add(js_stage);
		stages.add(hs_stage);
		stages.add(mq_stage);
		stages.add(settings_stage);
		
		setStage(login_stage);
		
//		populateUI();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		for (Stage stage : stages) {
			stage.getViewport().update(width, height);
			stage.getCamera().position.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		}
	}
	
	public static void setStage(Stage s) {
		if (currentStage != null)
			currentStage.unfocusAll();
		previousStage = currentStage;
		currentStage = s;
		
		if (previousStage != null) {
			if (previousStage instanceof LauncherStage)
				((LauncherStage) previousStage).clearFields();
			previousStage.clear();
		}
		
		if (currentStage instanceof LauncherStage)
			((LauncherStage) currentStage).populate();
		else if (currentStage instanceof UIStage)
			((UIStage) currentStage).populate();
		else if (currentStage instanceof GameStage)
			((GameStage) currentStage).populate();
		
		Gdx.input.setInputProcessor(s);
	}

	public void update() {
		if (currentStage != null) {
			currentStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		currentStage.getCamera().update();
		currentStage.getBatch().setProjectionMatrix(camera.combined);
		
		update();
		
		currentStage.draw();
	}

	@Override
	public void dispose() {
		if (NuclearWarServer.server != null) {
			NuclearWarServer.server.sendToAllTCP("@disconnect");
		}
		
		if (NuclearWarServer.client != null) {
			NuclearWarServer.disconnectClient();
		}
		
		na_stage.dispose();
		fp_stage.dispose();
		login_stage.dispose();
		main_stage.dispose();
		game_menu_stage.dispose();
		game_stage.dispose();
		js_stage.dispose();
		hs_stage.dispose();
		settings_stage.dispose();
	}
}