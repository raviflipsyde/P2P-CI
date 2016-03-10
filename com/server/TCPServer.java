package com.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.util.Index;
import com.util.Peer;

public class TCPServer {

	static final int counter = 7734;
	static ArrayList<Peer> peerList;
	static ArrayList<Index> rfcList;
	
	
	public static void main(String args[]) throws IOException, InterruptedException {
		peerList = new ArrayList<Peer>();
		rfcList = new ArrayList<Index>();
		
		ServerSocket conn0 = new ServerSocket(counter);
		while (true) {
			Socket sckt0 = conn0.accept();
			new Thread(new TCPServerThread(sckt0, counter)).start();
			Thread.sleep(100);

			// InputStreamReader inputReader = new
			// InputStreamReader(sckt0.getInputStream());
			// BufferedReader bufferReader = new BufferedReader(inputReader);
//			DataOutputStream outputStream = new DataOutputStream(sckt0.getOutputStream());
			// client_input = bufferReader.readLine();
			// System.out.println("Received: " + client_input);
//			outputStream.writeBytes(counter + "\n");
			
		}

	}
	
	public static void addPeer(Peer peer){
		peerList.add(peer);
	}
	
	public static void addRFC(Index index){
		rfcList.add(index);
	}
	
	public static ArrayList<Peer> findRFC(int  rfcNo){
		
		ArrayList<Peer> results = new ArrayList<Peer>();
		for(Index i:rfcList){
			if(i.getRfcNo() == rfcNo)
				results.add(i.getPeer());
		}
		return results;
	}
	
	public static ArrayList<Index> getAllRFCs(){
		return rfcList;
	}
}
