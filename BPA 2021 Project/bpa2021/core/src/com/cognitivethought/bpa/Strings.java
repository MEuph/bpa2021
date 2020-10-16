package com.cognitivethought.bpa;

import com.badlogic.gdx.Gdx;

public class Strings {
	
	public static final String APP_ID = "56E69610-EBDD-52D4-FFF7-EB3E7A675500";
	public static final String SECRET_KEY = "6FADE322-B1A0-430D-9EE5-5EE9152EA4C3";
	
	public static String USER_ID = "";

	public static final String EMPTY = "";
	
	public static final String NAME = "Nuclear War";
	
	public static final String MUI_TITLE = NAME;
	public static final String MUI_START = "Start";
	public static final String MUI_HELP = "Help";
	public static final String MUI_QUIT = "Quit";
	
	public static final String LNUI_TITLE = NAME;
	public static final String LNUI_EMAIL = "[Email]";
	public static final String LNUI_PASSWORD = "[Password]";
	public static final String LNUI_USERNAME = "[Username]";
	public static final String LNUI_TEMP_PASSWORD = "[Temp Password]";
	public static final String LNUI_NEW_PASSWORD = "[New Password]";
	public static final String LNUI_VERIFY_PASSWORD = "[Verify Password]";
	public static final String LNUI_CREATE_ACCOUNT = "Create Account";
	public static final String LNUI_LOGIN = "Login";
	public static final String LNUI_RESET_PASSWORD = "Reset Password";
	public static final String LNUI_FORGOT_PASSWORD = "Forgot Password";
	public static final String LNUI_BACK = "Back";
	public static final String LNUI_LAUNCH = "Launch";
	public static final String LNUI_CONTINUE = "Continue";
	
	public static final String ERROR_BE3033 = "Sorry, an account with that username has already been created!";
	public static final String ERROR_BE3003 = "Invalid username or password";

	public static final String ERROR_BPA0001 = "Temporary password does not match password sent to email address";
	public static final String ERROR_BPA0002 = "Verification password does not match new password";
	
	public static final String URL_LOCATOR = Gdx.files.getLocalStoragePath();
	public static final String URL_UBUNTU_REGULAR = URL_LOCATOR + "assets/fonts/used/ubuntu/Ubuntu-Regular.ttf";
	public static final String URL_ARIAL_UNICODE = URL_LOCATOR + "assets/fonts/used/arial/arial-unicode-ms.ttf";
	public static final String URL_SKINS_DEFAULT_FILE = URL_LOCATOR + "assets/skins/default/skin/uiskin.json";
	public static final String URL_SKINS_DEFAULT_ATLAS = URL_LOCATOR + "assets/skins/default/skin/uiskin.atlas";
}