package com.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadServer implements Runnable {

	private ServerSocket uploadSocket;
	private int port;

	public UploadServer(ServerSocket uploadSocket, int port) {

		this.uploadSocket = uploadSocket;
		this.port = port;
	}

	@Override
	public void run() {

		System.out.println("Starting Upload server at:" + uploadSocket.getInetAddress().getHostName() + ":" + port);
		Socket socket;

		while (true) {
			try {
				System.out.println("Waiting for the request..");
				socket = uploadSocket.accept();
				System.out.println("request recieved");
				InputStreamReader inputReader = new InputStreamReader(socket.getInputStream());
				BufferedReader bufferReader = new BufferedReader(inputReader);
				DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
				String client_input = bufferReader.readLine();
				System.out.println("Received:" + client_input);

				if (client_input != null) {
					// String client_in1 = bufferReader.readLine();
					// String client_in2 = bufferReader.readLine();
					String inpt[] = client_input.split(" ");
					System.out.println(inpt[0]);
					System.out.println(inpt[1]);
					System.out.println(inpt[2]);
					System.out.println(inpt[3]);

					if (inpt[0].equals("GET") && inpt[1].equals("RFC") && inpt[3].equals("P2P-CI/1.0")) {
						// System.out.println("REQUEST OK");
						try {

							File file = new File(".//files//" + inpt[2] + ".txt");

							byte[] bytes = new byte[(int) file.length()];
							FileInputStream fis = new FileInputStream(file);
							BufferedInputStream bis = new BufferedInputStream(fis);
							bis.read(bytes, 0, bytes.length);
							System.out.println("Sending...");
							
							DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
							Date date = new Date();
							System.out.println(dateFormat.format(date));
							
							outputStream.writeBytes("P2P-CI/1.0 200 OK" + "\n");							
							outputStream.writeBytes("Date: "+dateFormat.format(date) + "\n");
							outputStream.writeBytes("OS: " +System.getProperty("os.name") +"\n");
							outputStream.writeBytes("Last-Modified: " +dateFormat.format(new Date(file.lastModified())) +"\n");
							outputStream.writeBytes("Content-Length: " +(int) file.length()+ "\n");
							outputStream.writeBytes("Content-Type: text/plain" + "\n");

							outputStream.write(bytes);
							outputStream.flush();
							socket.close();
						} catch (IOException e) {
							outputStream.writeBytes("P2P-CI/1.0 404 NOT FOUND" + "\n");
							
						}
					}

					else if (!inpt[3].equals("P2P-CI/1.0"))
						outputStream.writeBytes("P2P-CI/1.0 505 P2P-CI Version Not Supported" + "\n");
						
					else
						outputStream.writeBytes("P2P-CI/1.0 400 BAD REQUEST" + "\n");
						

				} else {
					Thread.sleep(5000);
				}

			} catch (IOException e) {

				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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