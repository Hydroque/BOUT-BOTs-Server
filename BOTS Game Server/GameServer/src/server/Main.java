package server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import server.event.account.server.AccountPath;
import server.event.account.server.ChannelPath;
import server.event.account.server.RoomPath;
import server.event.gameserver.server.GamePath;
import server.gui.ServerGui;
import shared.ConfigStore;
import static shared.ConfigStore.PropertyStructure;
import shared.Logger;
import shared.SQLDatabase;

public class Main {

	public static final String SESSION_LOG_DIR = "log_login";
	public static Logger logger;
	
	public static ServerGui gui;
	
	public static AccountPath accountpath;
	public static ChannelPath channelpath;
	public static RoomPath roomserver;
	
	public static Vector<GamePath> gamepaths;
	
	public static File createSessionLog() throws IOException {
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		final File f = new File(SESSION_LOG_DIR + "\\" + format.format(new Date()) + ".log");
		final boolean created = f.createNewFile();
		if(!created) {
			System.out.println("Session log already exists! Check your time!");
			System.exit(1);
		}
		return f;
	}
	
	public static void main(String[] args) {
		gamepaths = new Vector<GamePath>();
		try {
			final PropertyStructure mysql = ConfigStore.loadProperties("configs/mysql.cfg");
			final PropertyStructure channels = ConfigStore.loadProperties("configs/channels.cfg");
			
			gui = new ServerGui();
			gui.setLocationRelativeTo(null);
			gui.setVisible(true);
			
			logger = gui.getLogger(gui.addTab("Account Server: 0"));
			
			accountpath = new AccountPath(11000, 5000);
			channelpath = new ChannelPath(11010, 5000);
			roomserver = new RoomPath(11011, 5000);
			
			gui.startUpdateTimer();
			
			SQLDatabase.loadconfig(mysql);
			SQLDatabase.start();
			accountpath.start();
			roomserver.start();
			channelpath.start();
			
			for (int i=0; i<Integer.valueOf(channels.getProperty("channels")); i++) {
				final Logger logger = gui.getLogger(gui.addTab(channels.getProperty("name_"+i)));
				
				//TODO: Connect logger
				final GamePath channelserver = new GamePath(logger, Integer.valueOf(channels.getProperty("port_"+i)), Integer.valueOf(channels.getProperty("timeout_"+i)));
				channelserver.start();
				gamepaths.add(channelserver);
				logger.log("Main", "Started new channel server");
			}
			
			logger.log("Main", "Login server started!");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void invokeShutdown() {
		gui.dispose();
		for (int i=0; i<gamepaths.size(); i++)
			gamepaths.get(i).stopThread();
		logger.log("Main", "games closed");
		accountpath.stopThread();
		logger.log("Main", "login closed");
		roomserver.stopThread();
		logger.log("Main", "room closed");
		channelpath.stopThread();
		logger.log("Main", "channel closed");
		try {
			accountpath.join();
			roomserver.join();
			channelpath.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		SQLDatabase.close();
		logger.log("Main", "SQL closed");
		try {
			ConfigStore.saveProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.log("Main", "Properties saved");
		logger.flushAll();
		logger.closeAll();
	}
	
}
