package com.cognitivethought.bpa.multiplayer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

import com.backendless.Backendless;
import com.cognitivethought.bpa.game.Player;
import com.cognitivethought.bpa.gamestages.MainGameStage;
import com.cognitivethought.bpa.launcher.Launcher;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class NuclearWarServer {
	
	public static Server server;
	public static Client client;
	public static int code = 0;
	
	public static String errorCode;
	
	/**
	 * Returns true if server is successfully opened
	 * @param code
	 * @return
	 * @throws IOException
	 */
	public static void openServer(int code) throws IOException {
		if (code > 3000 && code < 10000) {
			server = new Server();
			new Thread(server).start();
			server.bind(code - 100, code);
			
			Kryo kryo = server.getKryo();
			kryo.register(TurnPacket.class);
			kryo.register(StringPacket.class);
			
			NuclearWarServer.code = code;
			
			System.out.println("HOSTED SERVER ON " + code);
			
			server.addListener(new Listener() {
				@Override
				public void received(Connection conn, Object req) {
					System.out.println("CONNECTION " + conn.getRemoteAddressTCP() + " SENT DATA " + req.toString());
					if (req instanceof TurnPacket) {
						TurnPacket request = (TurnPacket)req;
						System.out.println("SERVER RECEIVED TP " + req.toString());
						server.sendToAllTCP(request);
					} else if (req instanceof StringPacket) {
						if (((StringPacket) req).data.equals("updateNames")) {
							System.out.println("CONECTION " + conn.getRemoteAddressTCP() + " REQUESTED AN UPDATED LIST OF PLAYERS");
							String names = "packet_updatedNames;";
							for (int i = 0; i < ((MultiplayerQueueStage)Launcher.mq_stage).player_names.size(); i++) {
								names += ((MultiplayerQueueStage)Launcher.mq_stage).player_names.get(i) + ";";
							}
							StringPacket names_packet = new StringPacket(names);
							System.out.println("SENDING NAMES PACKET " + names_packet.data);
							server.sendToAllTCP(names_packet);
						} else if (((StringPacket)req).toString().contains("@remove$")) {
							String player_id = ((StringPacket)req).toString().substring(8);
							System.out.println("SERVER RECEIVED REQUEST TO REMOVE PLAYER " + player_id);
							System.out.println("SERVER SENDING REMOVE PLAYER REQUEST TO ALL CLIENTS");
							server.sendToAllTCP((StringPacket)req);
							conn.close();
						}  else if (req.toString().charAt(0) == '?') {
							if (req.toString().substring(1, 6).contains("ready")) {
								System.out.println("SERVER RECEIVED UPDATED REQUEST ON WHETHER OR NOT PLAYER IS READY TO PLAY");
								server.sendToAllTCP(req);
							}
						} else if (req.toString().contains("%change%")) {
							String[] data = ((StringPacket) req).data.split(";");
							String player_name = data[1];
							int country_id = Integer.parseInt(data[2]);
							StringPacket packet = new StringPacket("%change%;" + player_name + ";" + country_id);
							server.sendToAllTCP(packet);
						} else {
							System.out.println("SERVER RECEIVED PDP " + req.toString());
							StringPacket pdp = (StringPacket)req;
							System.out.println(pdp.data);
							server.sendToAllTCP(pdp);
						}
					} else if (req instanceof FrameworkMessage.KeepAlive) {
						if (server.getConnections().size() != ((MultiplayerQueueStage)Launcher.mq_stage).player_names.size()) {
							System.out.println("SENDING OUT UPDATE PACKET\nNUM CONNECTIONS: " + server.getConnections().size()
									+ "\nNUM SUPPOSED PLAYERS: " + ((MultiplayerQueueStage)Launcher.mq_stage).player_names.size());
							StringPacket update = new StringPacket("?update");
							server.sendToAllTCP(update);
						}
					}
				}
				
				@Override
				public void connected(Connection conn) {
					System.out.println("CONNECTION AT " + conn.getRemoteAddressTCP());
				}
				
				@Override
				public void disconnected(Connection conn) {
					System.out.println("USER " + conn.getRemoteAddressTCP() + " DISCONNECTED");
				}
			});

			joinServer(code);
		}
	}
	
	public static void closeServer() {
		StringPacket exit = new StringPacket("@disconnect");
		server.sendToAllTCP(exit);
		server = null;
	}
	
	public static void joinServer(int code) throws IOException {
		if (code > 3000 && code < 10000) {
			client = new Client();

			Kryo kryo = client.getKryo();
			kryo.register(TurnPacket.class);
			kryo.register(StringPacket.class);
			
			client.addListener(new Listener() {
				@Override
				public void connected(Connection conn) {
					System.out.println("CLIENT CONNECTED TO SERVER AS " + conn.getRemoteAddressTCP());
					
					StringPacket pdp = new StringPacket((String)Backendless.UserService.CurrentUser().getProperty("name"));
					client.sendTCP(pdp);
					StringPacket nameRequests = new StringPacket("updateNames");
					System.out.println("SENT UPDATE NAMES REQUEST");
					client.sendTCP(nameRequests);
					System.out.println("SENT PDP AS TCP");
				}
				
				@Override
				public void received(Connection conn, Object dat) {
					if (dat instanceof TurnPacket) {
						TurnPacket data = (TurnPacket)dat;
						if (((MainGameStage)Launcher.game_stage).players.containsKey(data.getIssuer())) {
							System.out.println("RECEIVED TURN PACKET " + data.data);
							((MainGameStage)Launcher.game_stage).executeTurn(data);
						} else {
							System.err.println("Invalid username, possible hacking attempt");
						}
					} else if (dat instanceof StringPacket) {
						StringPacket data = (StringPacket)dat;
						System.out.println("CLIENT RECEIVED PDP");
						if(data.data.regionMatches(true, 0, "@start@" + Integer.toString(NuclearWarServer.code), 0, 7)) {
							((MultiplayerQueueStage)Launcher.mq_stage).start = true;
						} else if (data.data.regionMatches(true, 0, "packet_updatedNames;", 0, 20)) {
							String[] names = data.data.split(";");
							System.out.println("CLIENT RECEIVED UPDATED NAMES\nBEGINNING DEBUG");
							System.out.println(Arrays.toString(names));
							for (int i = 1; i < names.length; i++) {
								if (!((MultiplayerQueueStage)Launcher.mq_stage).player_names.contains(names[i])) {
									System.out.println(names[i] + " IS NOT CONTAINED IN PLAYER_NAMES");
									((MainGameStage)Launcher.game_stage).players.put(names[i], new Player());
									((MultiplayerQueueStage)Launcher.mq_stage).player_names.add(names[i]);
								} else {
									System.out.println(names[i] + " IS CONTAINED IN PLAYER_NAMES");
								}
							}
						} else if (data.data.regionMatches(true, 0, "@remove$", 0, 8)) {
							String player_id = ((StringPacket)dat).toString().substring(8);
							System.out.println("CLIENT RECEIVED REQUEST TO REMOVE PLAYER " + player_id);
							((MainGameStage)Launcher.game_stage).players.remove(player_id);
							((MultiplayerQueueStage)Launcher.mq_stage).player_names.remove(player_id);
							if (((MultiplayerQueueStage)Launcher.mq_stage).players != null)
								((MultiplayerQueueStage)Launcher.mq_stage).refreshList();
							if (((MultiplayerQueueStage)Launcher.mq_stage).player_names.size() <= 0) {
								server.close();
								server.stop();
								server = null;
							}
						} else if (data.toString().equals("@disconnect")) {
							disconnectClient();
						} else if (data.toString().charAt(0) == '?') {
							if (data.toString().substring(1, 6).contains("ready")) {
								boolean ready = Boolean.parseBoolean(data.toString().split(":")[1]);
								String user = data.toString().split(":")[2];
								((MainGameStage)Launcher.game_stage).players.get(user).ready = ready;
								if (((MultiplayerQueueStage)Launcher.mq_stage).players != null)
									((MultiplayerQueueStage)Launcher.mq_stage).refreshList();
							} else if (data.toString().substring(1, 7).contains("update")) {
								((MultiplayerQueueStage)Launcher.mq_stage).refreshList();
							}
						}  else if (data.toString().contains("%change%")) {
							String[] change_data = ((StringPacket) dat).data.split(";");
							String player_name = change_data[1];
							int country_id = Integer.parseInt(change_data[2]);
//							if (player_name.equals((String)Launcher.currentUser.getProperty("name"))) return;
//							else {
								((MainGameStage)Launcher.game_stage).players.get(player_name).country_id = country_id;
								if (((MultiplayerQueueStage)Launcher.mq_stage).players != null)
									((MultiplayerQueueStage)Launcher.mq_stage).refreshList();
//							}
						} else {
							((MainGameStage)Launcher.game_stage).players.put(data.data, new Player());
							((MultiplayerQueueStage)Launcher.mq_stage).player_names.add(data.data);
							if (((MultiplayerQueueStage)Launcher.mq_stage).players != null)
								((MultiplayerQueueStage)Launcher.mq_stage).refreshList();
						}
					}
				}
			});
			
			client.start();
			InetAddress address = client.discoverHost(code, 5000);
			try {
				client.connect(5000, address, code - 100, code);
			} catch (Exception e) {
				System.err.println("SERVER DOES NOT EXIST");
			}
		}
	}
	
	public static void disconnectClient() {
		server = null;
		
		System.out.println("CLIENT DISCONNECTED");
		
		StringPacket removePlayer = new StringPacket("@remove$" + (String)Backendless.UserService.CurrentUser().getProperty("name"));
		System.out.println("SENDING REMOVE PLAYER CODE " + removePlayer.toString());
		client.sendTCP(removePlayer);
		
		((MultiplayerQueueStage)Launcher.mq_stage).player_names.clear();
		if (((MultiplayerQueueStage)Launcher.mq_stage).players != null)
			((MultiplayerQueueStage)Launcher.mq_stage).refreshList();
	}
}