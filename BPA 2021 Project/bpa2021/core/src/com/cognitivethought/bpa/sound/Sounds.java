package com.cognitivethought.bpa.sound;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.cognitivethought.bpa.tidiness.Strings;

/**
 * 
 * @author christopherm_harris
 * 
 * The class that holds all of the sounds that are used in the game, that way they are only loaded once
 * 
 */
public class Sounds {
	
	public static long music_intro_id = 0;
	public static long music_queue_id = 0;
	public static long music_war_id = 0;
	
	public static Sound music_intro;
	public static Sound music_queue;
	public static Sound music_war;
	
	public static Sound attack_alert;
	public static Sound boom;
	public static Sound country_clicked;
	public static Sound card_placed;
	public static Sound placemat_pulled_down;
	public static Sound siren;
	public static Sound war_declared;
	
	public static void load() {
		music_intro = Gdx.audio.newSound(new FileHandle(new File(Strings.URL_MUSIC_INTRO)));
		music_queue = Gdx.audio.newSound(new FileHandle(new File(Strings.URL_MUSIC_QUEUE)));
		music_war = Gdx.audio.newSound(new FileHandle(new File(Strings.URL_MUSIC_WAR)));
		
		attack_alert = Gdx.audio.newSound(new FileHandle(new File(Strings.URL_SOUND_ATTACK)));
		boom = Gdx.audio.newSound(new FileHandle(new File(Strings.URL_SOUND_BOOM)));
		country_clicked = Gdx.audio.newSound(new FileHandle(new File(Strings.URL_SOUND_COUNTRY_CLICKED)));
		card_placed = Gdx.audio.newSound(new FileHandle(new File(Strings.URL_SOUND_CARD_PLACED)));
		placemat_pulled_down = Gdx.audio.newSound(new FileHandle(new File(Strings.URL_SOUND_PLACEMAT_PULLED_DOWN)));
		siren = Gdx.audio.newSound(new FileHandle(new File(Strings.URL_SOUND_SIREN)));
		war_declared = Gdx.audio.newSound(new FileHandle(new File(Strings.URL_SOUND_WAR_DECLARED)));
	}
}