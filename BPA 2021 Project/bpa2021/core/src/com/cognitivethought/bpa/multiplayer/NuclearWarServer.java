package com.cognitivethought.bpa.multiplayer;

import java.io.IOException;

import com.cognitivethought.bpa.gamestages.MainGameStage;
import com.cognitivethought.bpa.launcher.Launcher;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class NuclearWarServer {
	
	public static Server server;
	public static int code = 0;
	
	/**
	 * Returns true if server is successfully opened
	 * @param code
	 * @return
	 * @throws IOException
	 */
	public static void openServer(int code) throws IOException {
		if (code > 3000 && code < 10000) {
			server = new Server();
			server.start();
			server.bind(code - 100, code);
			
			NuclearWarServer.code = code;
			
			server.addListener(new Listener() {
				@Override
				public void received(Connection conn, Object req) {
					if (req instanceof TurnPacket) {
						TurnPacket request = (TurnPacket)req;
						if (((MainGameStage)Launcher.dev_stage).players.containsKey(request.getIssuer())) {
							((MainGameStage)Launcher.dev_stage).executeTurn(request);
						} else {
							System.err.println("Invalid username, possible hacking attempt");
						}
					}
				}
				
				@Override
				public void connected(Connection conn) {
					
				}
			});
		}
	}
	
}