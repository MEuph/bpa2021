package com.cognitivethought.bpa.prefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cognitivethought.bpa.game.Player;
import com.cognitivethought.bpa.gamestages.MainGameStage;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.multiplayer.NuclearWarServer;
import com.cognitivethought.bpa.multiplayer.StringPacket;

public class Country extends Actor {

	public boolean selected = false;

	Sprite s;
	
	float pmScaleX = 1.0f, pmScaleY = 1.0f;
	float offsX, offsY;
	final Pixmap pm;
	
	int id = GameMap.ID_NONE;
	
	Player assignedPlayer = null;

	public boolean isClicked;
	
	public Country(int id, Texture t) {
		s = new Sprite(t);

		this.id = id;
		
		t.getTextureData().prepare();
		pm = t.getTextureData().consumePixmap();
		
		final Country c = this;
		addListener(new ClickListener() {
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				super.enter(event, x, y, pointer, fromActor);
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				super.exit(event, x, y, pointer, toActor);

				selected = false;
			}

			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				float imgX = (x / pmScaleX) + (offsX / pmScaleX);
				float imgY = pm.getHeight() - ((y + offsY) / pmScaleY);
				int pixel = pm.getPixel((int)imgX, (int)imgY);
				
				selected = (pixel & 0x000000ff) != 0 && (pixel & 0x000000ff) != -1;

				return super.mouseMoved(event, x, y);
			}
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (selected)
					c.clicked(x, y);
				return super.touchDown(event, x, y, pointer, button);
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (selected)
					isClicked = false;
				super.touchUp(event, x, y, pointer, button);
			}
		});
		
		addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
					setBounds();
				}

				return super.keyTyped(event, character);
			}
		});

		setBounds();
	}
	
	public void assignPlayer(Player p) {
		assignedPlayer = p;
	}
	
	public void setBounds() {
		int minX = pm.getWidth();
		int minY = pm.getHeight();
		int maxX = 0;
		int maxY = 0;

		for (int x = 0; x < pm.getWidth(); x++) {
			for (int y = 0; y < pm.getHeight(); y++) {
				if ((pm.getPixel(x, y) & 0x000000ff) != 0) {
					minX = Math.min(minX, x);
					minY = Math.min(minY, y);
					maxX = Math.max(maxX, x);
					maxY = Math.max(maxY, y);
				} else {
					continue;
				}
			}
		}
		
		float x = s.getX() + (minX * pmScaleX) * (s.getWidth() / pm.getWidth());
		float y = s.getY() - (maxY * pmScaleY) * (s.getHeight() / pm.getHeight()) + s.getHeight();
		float w = (maxX - minX) * (s.getWidth() / pm.getWidth());
		float h = (maxY - minY) * (s.getHeight() / pm.getHeight());
		
		// Edge cases
		if (id == GameMap.ID_POPULA) {
			w /= 2;
			h /= 2;
			x += w / 2;
			y += h / 2;
		}
		
		if (id == GameMap.ID_NIPPYO) {
			w -= 40;
			x -= 20;
		}
		
		// CHANGES THE SCALE OF THE BOUNDS
		float scaleDown = 1f;
		w /= scaleDown;
		h /= scaleDown;
		
		offsX = x - getX();
		offsY = y - getY();
		
		setBounds(x, y, w, h);
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		s.setSize(width, height);
		setBounds();
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		s.setSize(width, s.getHeight());
		setBounds();
	}

	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		s.setSize(s.getWidth(), height);
		setBounds();
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		s.setPosition(x, y);
		setBounds();
	}

	public void update() {
		pmScaleX = s.getWidth() / (float) pm.getWidth();
		pmScaleY = s.getHeight() / (float) pm.getHeight();
	}
	
	public void clicked(float x, float y) {
		if (((MainGameStage)Launcher.game_stage).enableDark) {
			Thread[] tarray = new Thread[Thread.activeCount()];
			Thread.enumerate(tarray);
			for (Thread thread : tarray) {
				if (thread.getName().equals("selectTarget")) {
					String data = "#clickedCountry;" + ((MainGameStage)Launcher.game_stage).clientPlayer.username + ";" + this.id;
					StringPacket countryClickedPacket = new StringPacket(data);
					NuclearWarServer.client.sendTCP(countryClickedPacket);
					thread.destroy();
					break;
				}
			}
		}
		isClicked = true;
		// TODO: Pull up mini placemat of the country's player
	}

	public Player getAssignedPlayer() {
		return assignedPlayer;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		update();
		
		
		if (assignedPlayer != null) {
			if (assignedPlayer.equals(((MainGameStage)Launcher.game_stage).clientPlayer)) {
				batch.setColor(0.1f, 1.0f, 0.1f, 1.0f);
			}
			
			if (selected) {
				batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		} else {
			batch.setColor(0f, 0f, 0f, 1.0f);
		}


		batch.draw(s, s.getX(), s.getY(), s.getWidth(), s.getHeight());
	}
}