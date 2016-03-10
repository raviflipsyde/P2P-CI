package com.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import com.server.TCPServerThread;

public class TCPClient {

	public static void main(String argv[]) throws Exception {
		String sentence;
		String modifiedSentence;

		// BufferedReader input = new BufferedReader(new
		// InputStreamReader(System.in));

		InetAddress localAddr = InetAddress.getLocalHost();
		ServerSocket uploadSocket = new ServerSocket(0, 20, localAddr);
		String hostName = uploadSocket.getInetAddress().getCanonicalHostName();
		int localPort = uploadSocket.getLocalPort();
		sentence = hostName + ":::" + localPort;

		new Thread(new UploadServer(uploadSocket, localPort)).start();
		Socket clientSocket = new Socket("localhost", 7734);
		// Socket clientSocket = new Socket("localhost", 7734, localAddr, 0);

		DataOutputStream outToServer = null;
		BufferedReader inFromServer = null;
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		outToServer.writeBytes(sentence + '\n');

		System.out.println(inFromServer.readLine());
		// 1st send the port no info to server
		// modifiedSentence = sendToServer(clientSocket, sentence);
		// System.out.println("SERVER recievd port no " + modifiedSentence);
		Scanner input = new Scanner(System.in);
		whileLoop: while (true) {
			System.out.println("INPUT: 0-EXIT 1-GET RFC 2-ADD RFC 3-LOOKUP 4-LIST");
			int i = input.nextInt();

			switch (i) {
			case 0:
				break whileLoop;
			case 1: {
				// GET RFC
				// GET RFC 1234 P2P-CI/1.0
				// Host: somehost.csc.ncsu.edu
				// OS: Mac OS 10.4.1

				Scanner in = new Scanner(System.in);
				System.out.print("Input RFC #:");
				int rfcNo = in.nextInt();
				System.out.print("Input Host:");
				String host1 = in.next(); //"rpatel16-Lenovo-Y40-80"
//				host1 = "rpatel16-Lenovo-Y40-80";
				System.out.print("Input Port #:");
				int portNo = in.nextInt(); 

				Socket peerSocket = new Socket(host1, portNo);


				System.out.println(getFromServer(peerSocket,host1, rfcNo));

				break;
			}
			case 2: {
				Scanner in = new Scanner(System.in);
				System.out.print("Input RFC #:");
				int rfcNo = in.nextInt();
				System.out.print("Input RFC Title:");
				String rfcTitle = in.next();

				//				ADD RFC 123 P2P-CI/1.0
				//				Host: thishost.csc.ncsu.edu
				//				Port: 5678
				//				Title: A Proferred Official ICP 
				outToServer.writeBytes("ADD RFC "+rfcNo+" P2P-CI/1.0 " + '\n');
				outToServer.writeBytes("Host: "+hostName + '\n');
				outToServer.writeBytes("Port: "+localPort + '\n');
				outToServer.writeBytes("Title: "+rfcTitle + '\n');
				String response = inFromServer.readLine();
				System.out.println(response);
				break;
			}
			case 3: {
				// RFC Lookup
				//				LOOKUP RFC 3457 P2P-CI/1.0
				//				Host: thishost.csc.ncsu.edu
				//				Port: 5678
				//				Title: Requirements for IPsec Remote Access Scenarios
				Scanner in = new Scanner(System.in);
				System.out.print("Input RFC #:");
				int rfcNo = in.nextInt();
				System.out.print("Input RFC Title:");
				String rfcTitle = in.next();

				outToServer.writeBytes("LOOKUP RFC "+rfcNo+" P2P-CI/1.0 " + '\n');
				outToServer.writeBytes("Host: "+hostName + '\n');
				outToServer.writeBytes("Port: "+localPort + '\n');
				outToServer.writeBytes("Title: "+rfcTitle + '\n');
				
				String response = inFromServer.readLine();
				System.out.println(response);
				while(true){
					response = inFromServer.readLine();
					if(response.equals("EOF")) break;
					System.out.println(response);
				}
				break;
			}
			case 4: {
				// LIST
				//				LIST ALL P2P-CI/1.0
				//				Host: thishost.csc.ncsu.edu
				//				Port: 5678 

				outToServer.writeBytes("LIST ALL P2P-CI/1.0 " + '\n');
				outToServer.writeBytes("Host: "+hostName + '\n');
				outToServer.writeBytes("Port: "+localPort + '\n');

				String response = inFromServer.readLine();
				System.out.println(response);
				while(true){
					response = inFromServer.readLine();
					if(response.equals("EOF")) break;
					System.out.println(response);
				}
				
				break;
			}
			default: {
				System.out.println("Wrong INPUT. Pls enter again");
				break;
			}
			}

		}

		Thread.sleep(4000);
		// to end the server thread
		sendToServer(clientSocket, "end");
		clientSocket.close();
	}

	private static String sendToServer(Socket clientSocket, String sentence) {

		DataOutputStream outToServer;
		BufferedReader inFromServer = null;
		try {
			outToServer = new DataOutputStream(clientSocket.getOutputStream());

			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			outToServer.writeBytes(sentence + '\n');

			return (inFromServer.readLine());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "-ERROR-";
		}

	}

	private static String getFromServer(Socket clientSocket, String hostName, int rfcNo) {

		DataOutputStream outToServer;
		BufferedReader inFromServer = null;
		try {
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			InputStream inStream = clientSocket.getInputStream();
			InputStreamReader inReader = new InputStreamReader(inStream);

			inFromServer = new BufferedReader(inReader);

			String sentence = "GET RFC "+ rfcNo +" P2P-CI/1.0";
			outToServer.writeBytes(sentence + '\n');
			outToServer.writeBytes("Host: "+hostName + '\n');
			outToServer.writeBytes("OS: " +System.getProperty("os.name") +"\n");


			// return (inFromServer.readLine());
			byte[] b = new byte[1024];
			inStream.read(b);
			
			FileOutputStream fos = new FileOutputStream(".//files//temp"+rfcNo+".txt");
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			int bytesRead = inStream.read(b, 0, b.length);

			do {
				baos.write(b);
				bytesRead = inStream.read(b);
			} while (bytesRead != -1);

			bos.write(baos.toByteArray());
			bos.flush();
			bos.close();
			return "File created";

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "-ERROR-";
		}

	}

}
