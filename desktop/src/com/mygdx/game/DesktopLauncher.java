package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;


public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Probotector");
		config.setWindowedMode(MyGdxGame.WIDTH, MyGdxGame.HEIGHT);
		new Lwjgl3Application(new MyGdxGame(), config);
	}
}
