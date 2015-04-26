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

public class ChatServer implements Runnable { // extends �� implements �� �ƹ��ų� ����

	ServerSocket ss; // ���� ����
	Vector<ChatClient> vc = new Vector<ChatClient>();

	public ChatServer() {
		try {
			ss = new ServerSocket(ChatConstant.PORT);
			System.out.println("���� �����");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket s = ss.accept();
				System.out.println("Ŭ���̾�Ʈ ���� ����" + s); // ������ Ŭ���� ip,port
				new ChatClient(s).start(); // ���� �����ֱ� //�Ʒ� chatclient �� run ����
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	class ChatClient extends Thread { // inner Ŭ����,, �޼ҵ� ���� Ŭ����
		BufferedReader in;
		OutputStream out;
		String name;
		Socket s; //������ �ʿ��ϱ� ����
		
		public ChatClient(Socket s) {
			this.s = s; //���� 
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
					String msg = in.readLine(); // �޼����� ����
					System.out.println("������ ���� �޼��� : " + msg);
					StringTokenizer st = new StringTokenizer(msg, "||"); // ||��
																			// ��������
																			// �ڸ�
					int protocol = Integer.parseInt(st.nextToken()); // �������� Ȯ��

					switch (protocol) {
					case ChatConstant.CCONNECT: { // 100||���̵�
						name = st.nextToken();
						// System.out.println("������ ��ȭ�� : "+name);
						multicast(ChatConstant.SCONNECT + "||" + name); // ���
																		// �������
																		// ������
																		// ��ȭ��
																		// ����
						// �����ڿ��� ���� �������� ��ȭ�� ǥ��
						vc.add(this); // �� �̸��� �߰�
						int size = vc.size();
						for (int i = 0; i < size; i++) {
							ChatClient cc = vc.get(i);
							unicast(ChatConstant.SCONNECT + "||" + cc.name);

						}
					}
						break;
					case ChatConstant.CMESSAGE_ALL: { // 200||�޼���
						String tmp = st.nextToken(); // �޼��� �κ�
						multicast(ChatConstant.SMESSAGE_ALL + "||" + "[" + name
								+ "] : " + tmp); // multicast �� for �� ������ ���ο���
													// ����
					}
						break;

					case ChatConstant.CMESSAGE_TO: {
						String to = st.nextToken(); // �Ӹ� ���� ���
						String tmp = st.nextToken(); // ���� �޼���
						int size = vc.size();
						for (int i = 0; i < size; i++) {
							ChatClient cc = vc.get(i);
							if (to.equals(cc.name)) {
								cc.unicast(ChatConstant.SMESSAGE_TO + "||"
										+ name + "||" + tmp);// ��������� �޼��� ����
								break;
							}
						}
					}
						break;
					case ChatConstant.CPAPER: {
						String to = st.nextToken(); // ���� ���� ���
						String tmp = st.nextToken(); // ���� �޼���
						int size = vc.size();
						for (int i = 0; i < size; i++) {
							ChatClient cc = vc.get(i);
							if (to.equals(cc.name)) {
								cc.unicast(ChatConstant.SPAPER + "||" + name
										+ "||" + tmp);// ��������� �޼��� ����
								break;
							}
						}
					}
						break;

					case ChatConstant.CRENAME: {
						// String on;
						String nn = st.nextToken(); // ������ �̸�

						multicast(ChatConstant.CRENAME + "||" + name + "||"
								+ nn);
						name = nn;

					}
						break;

					case ChatConstant.CDISCONNECT: {
						multicast(ChatConstant.SDISCONNECT+"||"+name); //���� ��� �̸�
						vc.remove(this); //ã���� ���� �ٷ� �ڽ��� ��
						in.close();
						out.close();
						s.close();
					}
						break;

					}

				} catch (IOException e) {
					//e.printStackTrace(); //exception ���� ���.. ����ɰ�� x �� //�ϼ��� �����
					return;
				}
			}
		}

		private void multicast(String msg) { // ��� �������
			int size = vc.size();
			for (int i = 0; i < size; i++) {
				ChatClient cc = vc.get(i);
				cc.unicast(msg);
			}
		}

		private void unicast(String msg) { // �ӼӸ�
			try {
				out.write((msg + "\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		ChatServer cs = new ChatServer(); // ��Ʈ�� �����
		new Thread(cs).start(); // �����带 ���� �����ض� / Ŭ���̾�Ʈ ���� ó��.
	}

}
