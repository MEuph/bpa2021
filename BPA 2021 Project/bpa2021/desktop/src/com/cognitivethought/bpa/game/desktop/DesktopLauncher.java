package com.cognitivethought.bpa.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cognitivethought.bpa.Strings;
import com.cognitivethought.bpa.launcher.Launcher;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false;
		config.title = Strings.NAME;
		new LwjglApplication(new Launcher(), config);
	}
}
