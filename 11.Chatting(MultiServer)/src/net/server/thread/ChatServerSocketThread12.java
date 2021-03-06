package net.server.thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.List;

public class ChatServerSocketThread12 extends Thread {

	///Field
	private BufferedReader fromClient;
	private PrintWriter toClient;
	private Socket socket;
	private List<ChatServerSocketThread12> list;
	private boolean loopFlag;
	private SocketAddress socketAddress;
	private String clientName;
		
	public ChatServerSocketThread12() {
	}

	public ChatServerSocketThread12(Socket socket, List<ChatServerSocketThread12> list) {
		this.socket = socket;
		this.socketAddress = socket.getRemoteSocketAddress();
		this.list = list;
		
		try {
			fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			toClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			
			loopFlag = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	///Method
	public void run() {
		
		System.out.println("[ChatServerSocketThread] (" + socketAddress + ") : ] : data를 수신 송신 Loop Start");
		String fromClientData = null;
		
		while(loopFlag) {
			try {
				if((fromClientData = fromClient.readLine()) != null) {
					System.out.println("\n[ChatServerSocketThread "+ socketAddress +")] : Client 전송받은 Data ==> " + fromClientData);
					
					//toClient.println(fromClientData);
					//toAllClient(fromClientData);
					
					execute(fromClientData.substring(0, 3), fromClientData.substring(4));
					
				}else {
					System.out.println("\n[ChatServerSocketThred " + socketAddress + " ] : Client 접속종료로  Thread 종료함");
					break;
				}
				
			} catch (SocketException se) {
				se.printStackTrace();
				loopFlag = false;
			} catch (Exception e) {
				e.printStackTrace();
				loopFlag = false;
			}
		}// end of while
		
		System.out.println("\n[ServerSocketThread (" + socketAddress + " ) : ] : data를 수신, 송신 Loop End");
		this.close();
	}// end of run()
	
	public synchronized void toAllClient(String message) {
		for(ChatServerSocketThread12 chatServerSocketThread : list) {
			chatServerSocketThread.getWriter().println(message);
		}
	}
	
	public PrintWriter getWriter() {
		return toClient;
	}
	
	public void execute(String protocol, String message) {
		
		if(protocol.equals("100")) {
			this.clientName = message;
			this.toAllClient("[" + message + " ]님 입장");
			
		}else if(protocol.equals("200")) {
			
			this.toAllClient("[" + clientName + "] : " + message );
			
		}else if(protocol.equals("400")) {
			
			this.toAllClient("[" + clientName + "] 님 퇴실");
		}
	}
	
	public void close() {
		
		System.out.println(":: " + socketAddress + " close() start...");
		
		try {
			if(toClient != null) {
				toClient.close();
				toClient = null;
			}
			
			if(fromClient != null) {
				fromClient.close();
				fromClient = null;
			}
			if(socket != null) {
				socket.close();
				socket = null;
			}
			
			list.remove(this);
			
			System.out.println("접속자수 : " + list.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(":: close() end...");
		
	}

	public void setLoopFlag(boolean loopFlag) {
		this.loopFlag = loopFlag;
	}
	
}// end of class