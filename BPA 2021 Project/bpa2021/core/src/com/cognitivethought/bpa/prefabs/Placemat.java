package com.cognitivethought.bpa.prefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cognitivethought.bpa.external.GifDecoder;
import com.cognitivethought.bpa.tidiness.Strings;

public class Placemat extends Actor {

	// 83 x 46 CARD SIZE

	public static float cardWidth = 33;
	public static float cardHeight = 49;
	public static float matWidth = 440, matHeight = 224;
	
	float downY, upY;
	
	Card left, right, bottom, top, center;
	Pixmap mat;
	Texture tex;
	Pixmap arrow;
	Texture aTex;
	public ImageButton clickArrow;
	Texture outline;
	Animation<TextureRegion> background;
	float elapsed;
	
	boolean isDown = false;
	
	public Placemat() {
		mat = new Pixmap(new FileHandle(Strings.URL_PLACEMAT_SPOTS));
		
		tex = new Texture(mat);
		
		// NOTE: GifDecoder written by Johannes Borchardt and converted to the latest LibGDX version by Anton Persson
		background = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, new FileHandle(Strings.URL_PLACEMAT_BACKGROUND).read());
		
		arrow = new Pixmap(new FileHandle(Strings.URL_PLACEMAT_ARROW));
		
		aTex = new Texture(arrow);
		Image i = new Image(aTex);
		clickArrow = new ImageButton(i.getDrawable());
		
		outline = new Texture(new Pixmap(new FileHandle(Strings.URL_PLACEMAT_OUTLINE)));
		
		clickArrow.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				isDown = !isDown;
			}
		});

		left = new Card(Card.BLANK);
		right = new Card(Card.BLANK);
		bottom = new Card(Card.BLANK);
		top = new Card(Card.BLANK);
		center = new Card(Card.BLANK);
	}
	
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);

		if (!isDown) {
			downY = getY();
			setPosition(getX(), getY() + getHeight());
			upY = getY();
		}		
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		
		left.setPosition(getX() + pixelToRelative(64, 131).x, getY() + pixelToRelative(64, 131).y);
		left.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
		
		right.setPosition(getX() + pixelToRelative(305, 131).x, getY() + pixelToRelative(305, 131).y);
		right.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
		
		top.setPosition(getX() + pixelToRelative(185, 64).x, getY() + pixelToRelative(185, 64).y);
		top.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
		
		center.setPosition(getX() + pixelToRelative(185, 131).x, getY() + pixelToRelative(185, 131).y);
		center.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
		
		bottom.setPosition(getX() + pixelToRelative(185, 198).x, getY() + pixelToRelative(185, 198).y);
		bottom.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
	}
	
	public Card getLeft() {
		return left;
	}

	public void setLeft(Card left) {
		this.left = left;
	}

	public Card getRightCard() {
		return right;
	}

	public void setRight(Card right) {
		this.right = right;
	}

	public Card getTopCard() {
		return bottom;
	}

	public void setTop(Card top) {
		this.bottom = top;
	}

	public Card getBottom() {
		return top;
	}

	public void setBottom(Card bottom) {
		this.top = bottom;
	}

	public Card getCenter() {
		return center;
	}

	public void setCenter(Card center) {
		this.center = center;
	}

	public Vector2 pixelToRelative(float xPix, float yPix) {
		float retX = 0;
		float retY = 0;
		
		retX = (xPix * getWidth()) / mat.getWidth();
		retY = ((matHeight - yPix) * getHeight()) / mat.getHeight();

		return new Vector2(retX, retY);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		elapsed += Gdx.graphics.getDeltaTime() * 5;
		batch.draw(background.getKeyFrame(elapsed), getX(), getY(), getWidth(), getHeight());
		batch.draw(tex, getX(), getY(), getWidth(), getHeight());
		clickArrow.draw(batch, parentAlpha);
		
		left.draw(batch, parentAlpha);
		bottom.draw(batch, parentAlpha);
		center.draw(batch, parentAlpha);
		top.draw(batch, parentAlpha);
		right.draw(batch, parentAlpha);
		
		left.isOnPlacemat = true;
		bottom.isOnPlacemat = true;
		center.isOnPlacemat = true;
		top.isOnPlacemat = true;
		right.isOnPlacemat = true;
		
		if (isDown && getY() >= downY + 2) {
			Vector2 newPos = new Vector2(getX(), getY()).
					lerp(new Vector2(0, downY), Gdx.graphics.getDeltaTime() * 5f);
			setPosition(getX(), newPos.y);
			clickArrow.setPosition(getX() + (getWidth() / 2) - (clickArrow.getWidth()), getY() - clickArrow.getHeight() + 5);
		} else if (isDown && getY() < downY + 2) {
			setPosition(getX(), downY);
			clickArrow.setPosition(getX() + (getWidth() / 2) - (clickArrow.getWidth()), getY() - clickArrow.getHeight() + 5);
		}
		
		if (!isDown && getY() <= upY - 2) {
			Vector2 newPos = new Vector2(getX(), getY()).
					lerp(new Vector2(0, upY), Gdx.graphics.getDeltaTime() * 5f);
			setPosition(getX(), newPos.y);
			clickArrow.setPosition(getX() + (getWidth() / 2) - (clickArrow.getWidth()), getY() - clickArrow.getHeight() + 5);
		} else if (!isDown && getY() > upY - 2) {
			setPosition(getX(), upY);
			clickArrow.setPosition(getX() + (getWidth() / 2) - (clickArrow.getWidth()), getY() - clickArrow.getHeight() + 5);
		}
		
		batch.draw(outline, getX(), getY(), getWidth(), getHeight());
	}

	public void advance() {
		top.play();
		top = center;
		center = bottom;
		bottom = Card.BLANK;
	}
}