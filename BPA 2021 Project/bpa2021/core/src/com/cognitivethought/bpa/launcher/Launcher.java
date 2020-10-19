package com.cognitivethought.bpa.launcher;

import java.awt.Toolkit;
import java.util.ArrayList;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.Card;
import com.cognitivethought.bpa.Strings;
import com.cognitivethought.bpa.uistages.ForgotPasswordStage;
import com.cognitivethought.bpa.uistages.GameMenuStage;
import com.cognitivethought.bpa.uistages.LauncherStage;
import com.cognitivethought.bpa.uistages.LoginStage;
import com.cognitivethought.bpa.uistages.MainLauncherStage;
import com.cognitivethought.bpa.uistages.NewAccountStage;
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

	public static BackendlessUser currentUser = null;
	
	public OrthographicCamera camera;
	
	public Viewport vp;
	
	public static boolean isFullscreen = false;
	
	@Override
	public void create() {
		Backendless.initApp(Strings.APP_ID, Strings.SECRET_KEY);

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0, 0, 0);
		
		vp = new ScalingViewport(Scaling.none, Toolkit.getDefaultToolkit().getScreenSize().width,
				Toolkit.getDefaultToolkit().getScreenSize().height, camera);
		
		// -- DEBUGGING TOOL FOR UPDATING CARD DATABASE -- //
		
		Card.loadCards();
		
//		JSONObject jo;
//		try {
//			File f = new File(Strings.URL_LOCATOR + "assets\\json\\TestJSON.json");
//			jo = (JSONObject) new JSONParser().parse(new FileReader(f));
//
//			Map<String, String> card = new HashMap<String, String>();
//
//			for (Object o : jo.keySet()) {
//				card.put(o.toString(), jo.get(o).toString());
//			}
//
//			Backendless.Data.of("Cards").save(card);
//		} catch (IOException | ParseException e) {
//			e.printStackTrace();
//		}

		na_stage = new NewAccountStage(vp);
		login_stage = new LoginStage(vp);
		fp_stage = new ForgotPasswordStage(vp);
		main_stage = new MainLauncherStage(vp);
		game_menu_stage = new GameMenuStage(vp);
		
		na_stage.getViewport().apply();
		login_stage.getViewport().apply();
		fp_stage.getViewport().apply();
		main_stage.getViewport().apply();
		game_menu_stage.getViewport().apply();
		
		stages.add(na_stage);
		stages.add(login_stage);
		stages.add(fp_stage);
		stages.add(main_stage);
		stages.add(game_menu_stage);
		
		setStage(login_stage);
		
		for (Stage s : stages) {
			s.addListener(new InputListener() {
				@Override
				public boolean keyTyped(InputEvent event, char character) {
					if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
						Backendless.UserService.login("MEuph", "603Euph_", new AsyncCallback<BackendlessUser>() {
							
							@Override
							public void handleResponse(BackendlessUser response) {
								System.out.println("Worked");
							}
							
							@Override
							public void handleFault(BackendlessFault fault) {
								System.err.println("Failed");
							}
						});
						
						Launcher.setStage(main_stage);
					}
					
					return super.keyTyped(event, character);
				}
			});
		}
		
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
		
		Gdx.input.setInputProcessor(s);
	}

	public void update() {
		
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		currentStage.getCamera().update();
		
		update();
		
		currentStage.draw();
	}

	@Override
	public void dispose() {
		na_stage.dispose();
		fp_stage.dispose();
		login_stage.dispose();
		main_stage.dispose();
		game_menu_stage.dispose();
	}
}