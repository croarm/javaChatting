package com.samsungsds.chat.Client;

import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.samsungsds.chat.Data.ChatConstant;

public class Login extends JFrame implements ActionListener, Runnable {

	String myid;
	BufferedReader in;
	OutputStream out;
	Socket s;

	Chat chat = new Chat();
	ReName nameRe = new ReName();
	Paper paper = new Paper();

	JPanel global = new JPanel();
	GridLayout gridLayout1 = new GridLayout();
	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JLabel ip = new JLabel();
	JLabel name = new JLabel();
	JTextField ipTF = new JTextField();
	JTextField nameTF = new JTextField();
	JButton cancel = new JButton();
	JButton ok = new JButton();
	Font f = new Font("SansSerif", 0, 12);
	JLabel jl1 = new JLabel();
	JLabel jl2 = new JLabel();

	public Login() {
		super("Login!!");
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		this.getContentPane().setBackground(new Color(249, 255, 255));
		this.getContentPane().setLayout(null);
		global.setBorder(BorderFactory.createEtchedBorder());
		global.setOpaque(false);
		global.setBounds(new Rectangle(3, 3, 246, 114));
		global.setLayout(gridLayout1);
		gridLayout1.setRows(3);
		gridLayout1.setColumns(1);
		gridLayout1.setVgap(5);
		jPanel3.setBorder(BorderFactory.createEtchedBorder());
		jPanel3.setOpaque(false);
		jPanel3.setLayout(null);
		jPanel2.setOpaque(false);
		jPanel2.setLayout(null);
		jPanel1.setOpaque(false);
		jPanel1.setLayout(null);
		ip.setFont(new java.awt.Font("SansSerif", 0, 12));
		ip.setText("   I        P   : ");
		ip.setBounds(new Rectangle(6, 3, 66, 27));
		name.setBounds(new Rectangle(6, 0, 66, 27));
		name.setFont(new java.awt.Font("SansSerif", 0, 12));
		name.setText("  �� ȭ ��  : ");
		ipTF.setNextFocusableComponent(nameTF);
		ipTF.setBounds(new Rectangle(78, 3, 163, 27));
		nameTF.setNextFocusableComponent(ok);
		nameTF.setBounds(new Rectangle(78, 0, 163, 27));
		cancel.setFont(new java.awt.Font("SansSerif", 0, 12));
		cancel.setBorder(BorderFactory.createRaisedBevelBorder());
		cancel.setText("�� ��");
		cancel.setBounds(new Rectangle(126, 2, 67, 26));
		ok.setBounds(new Rectangle(48, 2, 67, 26));
		ok.setFont(new java.awt.Font("SansSerif", 0, 12));
		ok.setBorder(BorderFactory.createRaisedBevelBorder());
		ok.setNextFocusableComponent(cancel);
		ok.setText("Ȯ ��");
		this.getContentPane().add(global, null);
		global.add(jPanel1, null);
		jPanel1.add(ip, null);
		jPanel1.add(ipTF, null);
		global.add(jPanel2, null);
		jPanel2.add(name, null);
		jPanel2.add(nameTF, null);
		global.add(jPanel3, null);
		jPanel3.add(cancel, null);
		jPanel3.add(ok, null);

		jl1.setFont(f);
		jl1.setText("�ڽ����� ������� ��..");
		jl2.setFont(f);
		jl2.setText("�ڽ����� �Ӹ��� ������ ����� �γ��� ��.��++");

		setBounds(200, 200, 259, 146);
		setVisible(true);

		addWindowListener(new WindowAdapter() { // x ������ �ݱ�
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// loginâ �̺�Ʈ ���
		nameTF.addActionListener(this);
		ok.addActionListener(this);
		cancel.addActionListener(this);

		// chatâ �̺�Ʈ ���
		chat.globalsend.addActionListener(this);
		chat.whomsend.addActionListener(this);
		chat.paper.addActionListener(this);
		chat.rename.addActionListener(this);
		chat.close.addActionListener(this);

		// paperâ �̺�Ʈ ���
		paper.ok.addActionListener(this);
		paper.cancel.addActionListener(this);
		paper.answer.addActionListener(this);

		// renameâ �̺�Ʈ ���
		nameRe.ok.addActionListener(this);
		nameRe.cancel.addActionListener(this);

	}

	public static void main(String[] args) {
		new Login();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object ob = e.getSource();

		if (ob == nameTF || ob == ok) {
			connectProcess(); // ���� ó���� ���� �޼ҵ�
		} else if (ob == cancel) {
			System.exit(0);
		} else if (ob == chat.globalsend) { // �̸� ���� ������ �̰��� �����ϹǷ�
			sendAllProcess();
		} else if (ob == chat.whomsend) {
			sendToProcess();
		} else if (ob == chat.paper) {
			String to = chat.whom.getText();
			if (to.equals(myid)) {
				JOptionPane.showMessageDialog(this, "����� �ڽ��Դϴ�.", "�ƹ��� ���� ����",
						JOptionPane.WARNING_MESSAGE);
			}
			paper.to.setText(to);
			paper.from.setText(myid);
			paper.setVisible(true);
		} else if (ob == chat.rename) {
			nameRe.oldname.setText(myid);
			nameRe.setVisible(true);

		} else if (ob == chat.close) {
			closeProcess();
		} else if (ob == paper.ok) {
			sendPaper();
		} else if (ob == paper.cancel) {
			paper.setVisible(false);
		} else if (ob == paper.answer) {
			paper.letter.append("\n-------------------[����]\n");
			paper.to.setText(paper.from.getText());
			paper.from.setText(myid);
			paper.card.show(paper.south1, "ok");
		} else if (ob == nameRe.ok) {
			renameProcess();
		} else if (ob == nameRe.cancel) {

		}

	}

	private void closeProcess() {
		send(ChatConstant.CDISCONNECT+"||"); //������ ������, ��ū �з��� ���� ||
		chat.setVisible(false);
	}

	private void renameProcess() {
		nameRe.oldname.setText(myid);
		String mm = nameRe.newname.getText().trim();
		if (mm.length() < 3 || myid.length() > 8) {
			JOptionPane.showMessageDialog(this, "��ȭ���� 3���̻�, 8�� ���� �Դϴ�.",
					"��ȭ�� ����", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (mm.equals(myid)) {
			JOptionPane.showMessageDialog(this, "�̸��� �����ϴ�", "�հ��� ����",
					JOptionPane.WARNING_MESSAGE);
		}
		myid = mm;
		send(ChatConstant.CRENAME + "||" + mm);

	}

	private void sendPaper() {
		String to = chat.whom.getText();
		String msg = paper.letter.getText().replaceAll("\n", "<n>");
		// \n �������� �ڸ��� ������ > <n> �� �ӽ� �������ش�
		send(ChatConstant.CPAPER + "||" + to + "||" + msg);

		paper.to.setText("");
		paper.from.setText("");
		paper.letter.setText("");

		paper.setVisible(false);
	}

	private void sendToProcess() { // �Ӹ�
		String to = chat.whom.getText();
		String msg = chat.whomsend.getText().trim();
		chat.whomsend.setText("");
		if (msg.isEmpty()) {
			return;
		}
		if (to.isEmpty()) {
			JOptionPane.showMessageDialog(this, "�ӼӸ� ���� ����� �����ϼ���",
					"�ӼӸ� ����� ����", JOptionPane.WARNING_MESSAGE);
		}
		if (to.equals(myid)) {
			JOptionPane.showMessageDialog(this, "����� �ڽ��Դϴ�.", "ģ���� ���� ����",
					JOptionPane.WARNING_MESSAGE);
		}
		send(ChatConstant.CMESSAGE_TO + "||" + to + "||" + msg);

		chat.area.append("��" + to + "��" + msg + "\n");

	}

	// 1.���� �޼��� ���(��ȿ�� �˻� �ʼ�(���ڿ� , globalsend �����
	// 2.1�� �޼����� server �� ����
	private void sendAllProcess() {
		String msg = chat.globalsend.getText().trim(); // ���� ������ trim
		chat.globalsend.setText("");
		if (msg.isEmpty()) { // �޼����� ����ִٸ�
			return;
		}
		send(ChatConstant.CMESSAGE_ALL + "||" + msg); // send �޼ҵ尡 �ɰ��� \b �ٿ���
														// ������ ����

	}

	// 1. server ip ��ȭ�� ��� (��ȿ���˻�)
	// 2. socket ����
	// 3. �α���â �ݰ�, ü��â ����
	// 4. in, out ����
	// 5. �� ��ȭ���� ������ ����
	private void connectProcess() {
		String host = ipTF.getText().trim(); // tf�� �޾ƿ�
		myid = nameTF.getText().trim(); // ��ȭ�� �ݵ�� 3�� �̻�, 8�� ����

		if (myid.length() < 3 || myid.length() > 8) {
			JOptionPane.showMessageDialog(this, "��ȭ���� 3���̻�, 8�� ���� �Դϴ�.",
					"��ȭ�� ����", JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			s = new Socket(host, ChatConstant.PORT);
			this.setVisible(false);
			chat.setVisible(true);

			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = s.getOutputStream();

			send(ChatConstant.CCONNECT + "||" + myid); // \n ���� �д°��̹Ƿ� ����
														// ��������Ѵ�. //getbytes
														// ����Ʈ �迭�� ����

			new Thread(this).start(); // run ����

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send(String msg) {
		try {
			out.write((msg + "\n").getBytes());
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
				System.out.println("Ŭ���̾�Ʈ�� ������ ���� �޼��� : " + msg);
				StringTokenizer st = new StringTokenizer(msg, "||"); // ||�� ��������
																		// �ڸ�
				int protocol = Integer.parseInt(st.nextToken()); // �������� Ȯ��

				switch (protocol) {
				case ChatConstant.SCONNECT: { // 100||���̵�
					String tmp = st.nextToken();
					chat.area.append("[�˸�] " + tmp + " ���� ���� �ϼ̽��ϴ�.\n");
					chat.list.add(tmp);
				}
					break;
				case ChatConstant.SMESSAGE_ALL: {
					String tmp = st.nextToken(); // ������ ������ �޼����� ����
					chat.area.append(tmp + "\n");
				}
					break;
				case ChatConstant.SMESSAGE_TO: { // 250 || ������� || �޼���
					String from = st.nextToken(); // �������
					String tmp = st.nextToken(); // �޼���
					chat.area.append("��" + from + "��" + tmp + "\n");
					chat.whom.setText(from);
				}
					break;
				case ChatConstant.SPAPER: {
					String from = st.nextToken(); // �������
					String tmp = st.nextToken(); // �޼���
					paper.from.setText(from);
					paper.to.setText(myid);

					paper.letter.setText(tmp.replaceAll("<n>", "\n")); // �ٽ� ����

					paper.card.show(paper.south1, "answer");

					paper.setVisible(true);

				}
					break;
				case ChatConstant.SRENAME: {
					String oldname = st.nextToken();
					String newname = st.nextToken();
					chat.area.append("���� �� �̸� : " + oldname + ">>"
							+ "���� �� �̸� : " + newname + "\n");

					int len = chat.list.getRows(); // ���� ����
					for (int i = 0; i < len; i++) {
						String name = chat.list.getItem(i);
						if (name.equals(oldname)) {
							chat.list.remove(oldname);
							chat.list.add(newname);
							break;
						}
					}
					// chat.list.remove(myid);
					// chat.list.add(newname);
					// myid = newname;
					nameRe.setVisible(false);
				}
					break;
				case ChatConstant.SDISCONNECT: {
					String outid = st.nextToken();//������ ��� �̸�
					if(myid.equals(outid)){
						in.close();
						out.close();
						s.close(); // ���������� in,out �� ������ ������ ���� ����..
						System.exit(0); //����
					}else{
						chat.area.append("[�˸�] "+outid+"���� �������ϴ�.");
						chat.list.remove(outid);
					}
				}
					break;
				}

			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}

}
