package com.cognitivethought.bpa.multiplayer;

public class StringPacket {
	
	String data;
	
	public StringPacket() {
		
	}
	
	public StringPacket(String data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return data;
	}
}