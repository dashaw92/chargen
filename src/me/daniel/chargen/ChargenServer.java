package me.daniel.chargen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChargenServer implements Runnable {
	
	private boolean running = true;
	//Connected clients. Cleaned of disconnected clients by run() 
	private List<ClientThread> clients = new ArrayList<>(); 
	
	public static void main(String[] args) throws IOException {
		int port = 7777;
		if(args.length > 0) {
			try {
				port = Integer.parseInt(args[0].trim());
			} catch(NumberFormatException ignored) {}
		}
		
		new ChargenServer().launch(port);
	}
	
	//Starts the multithreaded chargen server
	public void launch(int port) throws IOException {
		ServerSocket server = new ServerSocket(port);
		System.out.println("Chargen: Accepting connections on port " + port);	
		//See run()
		new Thread(this).start();

		while(!server.isClosed()) {
			Socket socket = server.accept();
			clients.add(new ClientThread(socket));
			System.out.println("\t[+] Accepted new connection");
		}
		
		server.close();
		running = false;
	}
	
	//Used as a disconnected client cleanup thread
	public void run() {
		while(running) {
			//Collect the disconnected clients into one list so they
			//can be removed from the clients list and ultimately be
			//gc'ed
			//Done this way to ConcurrentModificationException
			List<ClientThread> dead = clients.stream()
											 .filter(ClientThread::done)
											 .collect(Collectors.toList());
			dead.forEach(clients::remove);
			
			if(dead.size() > 0) {
				System.out.printf("\t[-] %d client(s) disconnected.\n", dead.size());
			}
			
			try { //don't eat cpu time
				Thread.sleep(100);
			} catch (InterruptedException ignored) {}
		}
	}
}
