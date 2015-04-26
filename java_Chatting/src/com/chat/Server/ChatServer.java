package com.chat.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import com.chat.Data.ChatConstant;

public class ChatServer implements Runnable { // extends 나 implements 나 아무거나 쓸것

	ServerSocket ss; // 서버 소켓
	Vector<ChatClient> vc = new Vector<ChatClient>();

	public ChatServer() {
		try {
			ss = new ServerSocket(ChatConstant.PORT);
			System.out.println("접속 대기중");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket s = ss.accept();
				System.out.println("클라이언트 접속 성공" + s); // 접속한 클라의 ip,port
				new ChatClient(s).start(); // 소켓 보내주기 //아래 chatclient 의 run 실행
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	class ChatClient extends Thread { // inner 클래스,, 메소드 같은 클래스
		BufferedReader in;
		OutputStream out;
		String name;
		Socket s; //끊을때 필요하기 때문
		
		public ChatClient(Socket s) {
			this.s = s; //소켓 
			try {
				in = new BufferedReader(new InputStreamReader(
						this.s.getInputStream()));
				out = this.s.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			boolean flag = true;
			while (flag) {
				try {
					String msg = in.readLine(); // 메세지를 받음
					System.out.println("서버가 받은 메세지 : " + msg);
					StringTokenizer st = new StringTokenizer(msg, "||"); // ||를
																			// 기준으로
																			// 자름
					int protocol = Integer.parseInt(st.nextToken()); // 프로토콜 확인

					switch (protocol) {
					case ChatConstant.CCONNECT: { // 100||아이디
						name = st.nextToken();
						// System.out.println("접속자 대화명 : "+name);
						multicast(ChatConstant.SCONNECT + "||" + name); // 모든
																		// 사람에게
																		// 접속자
																		// 대화면
																		// 전달
						// 접속자에게 기존 접속자의 대화명 표시
						vc.add(this); // 내 이름도 추가
						int size = vc.size();
						for (int i = 0; i < size; i++) {
							ChatClient cc = vc.get(i);
							unicast(ChatConstant.SCONNECT + "||" + cc.name);

						}
					}
						break;
					case ChatConstant.CMESSAGE_ALL: { // 200||메세지
						String tmp = st.nextToken(); // 메세지 부분
						multicast(ChatConstant.SMESSAGE_ALL + "||" + "[" + name
								+ "] : " + tmp); // multicast 가 for 문 뒤져서 전부에게
													// 전송
					}
						break;

					case ChatConstant.CMESSAGE_TO: {
						String to = st.nextToken(); // 귓말 받을 사람
						String tmp = st.nextToken(); // 보낼 메세지
						int size = vc.size();
						for (int i = 0; i < size; i++) {
							ChatClient cc = vc.get(i);
							if (to.equals(cc.name)) {
								cc.unicast(ChatConstant.SMESSAGE_TO + "||"
										+ name + "||" + tmp);// 보낸사람과 메세지 전송
								break;
							}
						}
					}
						break;
					case ChatConstant.CPAPER: {
						String to = st.nextToken(); // 쪽지 받을 사람
						String tmp = st.nextToken(); // 보낼 메세지
						int size = vc.size();
						for (int i = 0; i < size; i++) {
							ChatClient cc = vc.get(i);
							if (to.equals(cc.name)) {
								cc.unicast(ChatConstant.SPAPER + "||" + name
										+ "||" + tmp);// 보낸사람과 메세지 전송
								break;
							}
						}
					}
						break;

					case ChatConstant.CRENAME: {
						// String on;
						String nn = st.nextToken(); // 변경후 이름

						multicast(ChatConstant.CRENAME + "||" + name + "||"
								+ nn);
						name = nn;

					}
						break;

					case ChatConstant.CDISCONNECT: {
						multicast(ChatConstant.SDISCONNECT+"||"+name); //나간 사람 이름
						vc.remove(this); //찾을것 없이 바로 자신을 뺌
						in.close();
						out.close();
						s.close();
					}
						break;

					}

				} catch (IOException e) {
					//e.printStackTrace(); //exception 내용 출력.. 저장될경우 x 됨 //완성시 지울것
					return;
				}
			}
		}

		private void multicast(String msg) { // 모든 사람에게
			int size = vc.size();
			for (int i = 0; i < size; i++) {
				ChatClient cc = vc.get(i);
				cc.unicast(msg);
			}
		}

		private void unicast(String msg) { // 귓속말
			try {
				out.write((msg + "\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		ChatServer cs = new ChatServer(); // 포트를 열어라
		new Thread(cs).start(); // 쓰래드를 만들어서 시작해라 / 클라이언트 접속 처리.
	}

}
