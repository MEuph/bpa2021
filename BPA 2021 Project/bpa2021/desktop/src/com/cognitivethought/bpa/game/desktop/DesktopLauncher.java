package com.cognitivethought.bpa.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.tidiness.Strings;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false;
		config.width = 640;
		config.height = 480;
		config.title = Strings.NAME;
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		new LwjglApplication(new Launcher(), config);
	}
}
