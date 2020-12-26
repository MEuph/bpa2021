package com.cognitivethought.bpa.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.tidiness.Strings;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false;
		config.title = Strings.NAME;
//		config.width = (int)(1280 / 1.5);
//		config.height = (int)(720 / 1.5);
		// TODO: Makes window have no decoration
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		new LwjglApplication(new Launcher(), config);
	}
}
