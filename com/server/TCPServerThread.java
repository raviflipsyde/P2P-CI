package com.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.print.attribute.ResolutionSyntax;

import com.client.TCPClient;
import com.util.Index;
import com.util.Peer;

public class TCPServerThread implements Runnable {

	private Socket clientSocket;
	private int port;

	public TCPServerThread(Socket clientSocket, int port) {

		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {

		try {

			Socket sckt1 = clientSocket;

			InputStreamReader inputReader = new InputStreamReader(sckt1.getInputStream());
			BufferedReader bufferReader = new BufferedReader(inputReader);
			DataOutputStream outputStream = new DataOutputStream(sckt1.getOutputStream());

			String client_input = bufferReader.readLine();
			if (client_input.contains(":::")) {
				String[] arr = client_input.split(":::");
				String hostname = arr[0];
				String sPort = arr[1];
				int port = Integer.parseInt(sPort);

				System.out.println("Connected to:" + hostname + ":" + port);
				outputStream.writeBytes("HOST ADDED SUCCESSFULLY" + "\n");
				TCPServer.addPeer(new Peer(hostname, port));
				client_input = null;
				while (true) {
					client_input = bufferReader.readLine();
					if (client_input != null) {
						// // Process client input according to the request
						// command

						if (client_input.equalsIgnoreCase("END")) {
							sckt1.close();
							break;
						} else if (client_input.startsWith("ADD")) {
							String inpts[] = client_input.split(" ");
							int rfcNo = Integer.parseInt(inpts[2]);
							String hostData = bufferReader.readLine();
							String portData = bufferReader.readLine();
							String titleData = bufferReader.readLine();
							String hostName = hostData.substring(6);
							String sPortNo = portData.substring(6).trim();
							int portNo = Integer.parseInt(sPortNo);
							String rfcTitle = titleData.substring(7);
							Peer p = new Peer(hostName, portNo);
							Index in = new Index();
							in.setRfcNo(rfcNo);
							in.setRfcTitle(rfcTitle);
							in.setPeer(p);
							TCPServer.addRFC(in);
							outputStream.writeBytes("RFC ADDED SUCCESSFULLY" + "\n");
						} 
						else if (client_input.startsWith("LOOKUP")) {
							String inpts[] = client_input.split(" ");
							int rfcNo = Integer.parseInt(inpts[2]);
							String hostData = bufferReader.readLine();
							String portData = bufferReader.readLine();
							String titleData = bufferReader.readLine();
							titleData = titleData.substring(6);
							ArrayList<Peer> resList = TCPServer.findRFC(rfcNo);
							
							outputStream.writeBytes("P2P-CI/1.0 200 OK" + "\n");
							outputStream.flush();
							StringBuilder strB = new StringBuilder("");
							for(Peer p:resList){
//								outputStream.writeBytes("200 OK" + "\n");
								outputStream.writeBytes(rfcNo+" "+ titleData+ " "+p.getHostname()+" "+p.getPort() + "\n");
								
							}
							outputStream.flush();
							outputStream.writeBytes("EOF"+ '\n');
							outputStream.flush();
						} 
						else if (client_input.startsWith("LIST")) {
							
							System.out.println("Received LIST command");
							String hostData = bufferReader.readLine();
							String portData = bufferReader.readLine();
							
							ArrayList<Index> resList = TCPServer.getAllRFCs();

							outputStream.writeBytes("P2P-CI/1.0 200 OK" + "\n");
							outputStream.writeBytes("\n");
							outputStream.flush();
							for(Index in:resList){
//								outputStream.writeBytes("200 OK" + "\n");
								outputStream.writeBytes(in.getRfcNo()+" "+ in.getRfcTitle()+ " "+ in.getPeer().getHostname()+" "+in.getPeer().getPort() + "\n");
								
							}
							
							outputStream.flush();
							outputStream.writeBytes("EOF"+ '\n');
							outputStream.flush();
							
						}

					}
				}

			}
		} catch (IOException e1) {

			e1.printStackTrace();
		}
	}

}

// String hostAddr = clientSocket.getInetAddress().getHostAddress();
// String hostName = clientSocket.getInetAddress().getHostName();
// String canHostName = clientSocket.getInetAddress().getCanonicalHostName();
//
// System.out.println(" Addr:"+hostAddr+"\n");
// System.out.println(" Name:"+hostName+"\n");
// System.out.println("Can Name:"+canHostName+"\n");
//