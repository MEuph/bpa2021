package com.cognitivethought.bpa;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Placemat extends Actor {

	// 83 x 46 CARD SIZE

	float cardWidth = 95, cardHeight = 46;
	float matWidth = 440, matHeight = 224;

	Card left, right, top, bottom, center;
	Pixmap mat;
	Texture tex;

	public Placemat() {
		mat = new Pixmap(new FileHandle(Strings.URL_PLACEMAT));
		
		tex = new Texture(mat);
		
		left = new Card(Card.BLANK);
		right = new Card(Card.BLANK);
		top = new Card(Card.BLANK);
		bottom = new Card(Card.BLANK);
		center = new Card(Card.BLANK);
	}
	
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);

		left.setPosition(getX() + pixelToRelative(40, 88).x, getY() + pixelToRelative(40, 88).y);
		left.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
		
		top.setPosition(getX() + pixelToRelative(159, 24).x, getY() + pixelToRelative(159, 24).y);
		top.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
		
		center.setPosition(getX() + pixelToRelative(159, 88).x, getY() + pixelToRelative(159, 88).y);
		center.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
		
		bottom.setPosition(getX() + pixelToRelative(159, 152).x, getY() + pixelToRelative(159, 152).y);
		bottom.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
		
		right.setPosition(getX() + pixelToRelative(278, 88).x, getY() + pixelToRelative(278, 88).y);
		right.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
//		
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
		return top;
	}

	public void setTop(Card top) {
		this.top = top;
	}

	public Card getBottom() {
		return bottom;
	}

	public void setBottom(Card bottom) {
		this.bottom = bottom;
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
		retY = (yPix * getHeight()) / mat.getHeight();

		return new Vector2(retX, retY);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		batch.draw(tex, getX(), getY(), getWidth(), getHeight());

		left.draw(batch, parentAlpha);
		top.draw(batch, parentAlpha);
		center.draw(batch, parentAlpha);
		bottom.draw(batch, parentAlpha);
		right.draw(batch, parentAlpha);
	}

}