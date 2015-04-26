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
		name.setText("  대 화 명  : ");
		ipTF.setNextFocusableComponent(nameTF);
		ipTF.setBounds(new Rectangle(78, 3, 163, 27));
		nameTF.setNextFocusableComponent(ok);
		nameTF.setBounds(new Rectangle(78, 0, 163, 27));
		cancel.setFont(new java.awt.Font("SansSerif", 0, 12));
		cancel.setBorder(BorderFactory.createRaisedBevelBorder());
		cancel.setText("취 소");
		cancel.setBounds(new Rectangle(126, 2, 67, 26));
		ok.setBounds(new Rectangle(48, 2, 67, 26));
		ok.setFont(new java.awt.Font("SansSerif", 0, 12));
		ok.setBorder(BorderFactory.createRaisedBevelBorder());
		ok.setNextFocusableComponent(cancel);
		ok.setText("확 인");
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
		jl1.setText("자신한테 쪽지라니 쩝..");
		jl2.setFont(f);
		jl2.setText("자신한테 귓말을 보내는 사람두 인나용 ㅡ.ㅡ++");

		setBounds(200, 200, 259, 146);
		setVisible(true);

		addWindowListener(new WindowAdapter() { // x 눌러서 닫기
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// login창 이벤트 등록
		nameTF.addActionListener(this);
		ok.addActionListener(this);
		cancel.addActionListener(this);

		// chat창 이벤트 등록
		chat.globalsend.addActionListener(this);
		chat.whomsend.addActionListener(this);
		chat.paper.addActionListener(this);
		chat.rename.addActionListener(this);
		chat.close.addActionListener(this);

		// paper창 이벤트 등록
		paper.ok.addActionListener(this);
		paper.cancel.addActionListener(this);
		paper.answer.addActionListener(this);

		// rename창 이벤트 등록
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
			connectProcess(); // 접속 처리를 위한 메소드
		} else if (ob == cancel) {
			System.exit(0);
		} else if (ob == chat.globalsend) { // 이름 같은 변수가 이곳에 존재하므로
			sendAllProcess();
		} else if (ob == chat.whomsend) {
			sendToProcess();
		} else if (ob == chat.paper) {
			String to = chat.whom.getText();
			if (to.equals(myid)) {
				JOptionPane.showMessageDialog(this, "대상이 자신입니다.", "아무도 없냐 오류",
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
			paper.letter.append("\n-------------------[답장]\n");
			paper.to.setText(paper.from.getText());
			paper.from.setText(myid);
			paper.card.show(paper.south1, "ok");
		} else if (ob == nameRe.ok) {
			renameProcess();
		} else if (ob == nameRe.cancel) {

		}

	}

	private void closeProcess() {
		send(ChatConstant.CDISCONNECT+"||"); //보낼건 없지만, 토큰 분류를 위해 ||
		chat.setVisible(false);
	}

	private void renameProcess() {
		nameRe.oldname.setText(myid);
		String mm = nameRe.newname.getText().trim();
		if (mm.length() < 3 || myid.length() > 8) {
			JOptionPane.showMessageDialog(this, "대화명은 3자이상, 8자 이하 입니다.",
					"대화명 오류", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (mm.equals(myid)) {
			JOptionPane.showMessageDialog(this, "이름이 같습니다", "손가락 오류",
					JOptionPane.WARNING_MESSAGE);
		}
		myid = mm;
		send(ChatConstant.CRENAME + "||" + mm);

	}

	private void sendPaper() {
		String to = chat.whom.getText();
		String msg = paper.letter.getText().replaceAll("\n", "<n>");
		// \n 기준으로 자르기 때문에 > <n> 로 임시 변경해준다
		send(ChatConstant.CPAPER + "||" + to + "||" + msg);

		paper.to.setText("");
		paper.from.setText("");
		paper.letter.setText("");

		paper.setVisible(false);
	}

	private void sendToProcess() { // 귓말
		String to = chat.whom.getText();
		String msg = chat.whomsend.getText().trim();
		chat.whomsend.setText("");
		if (msg.isEmpty()) {
			return;
		}
		if (to.isEmpty()) {
			JOptionPane.showMessageDialog(this, "귓속말 받을 사람을 선택하세요",
					"귓속말 대상자 오류", JOptionPane.WARNING_MESSAGE);
		}
		if (to.equals(myid)) {
			JOptionPane.showMessageDialog(this, "대상이 자신입니다.", "친구가 없냐 오류",
					JOptionPane.WARNING_MESSAGE);
		}
		send(ChatConstant.CMESSAGE_TO + "||" + to + "||" + msg);

		chat.area.append("《" + to + "》" + msg + "\n");

	}

	// 1.보낼 메세지 얻기(유효성 검사 필수(빈문자열 , globalsend 지우기
	// 2.1의 메세지를 server 로 전송
	private void sendAllProcess() {
		String msg = chat.globalsend.getText().trim(); // 공백 정리용 trim
		chat.globalsend.setText("");
		if (msg.isEmpty()) { // 메세지가 비어있다면
			return;
		}
		send(ChatConstant.CMESSAGE_ALL + "||" + msg); // send 메소드가 쪼개서 \b 붙여서
														// 서버로 보냄

	}

	// 1. server ip 대화명 얻기 (유효성검사)
	// 2. socket 생성
	// 3. 로그인창 닫고, 체팅창 열기
	// 4. in, out 생성
	// 5. 내 대화명을 서버로 전달
	private void connectProcess() {
		String host = ipTF.getText().trim(); // tf걸 받아옴
		myid = nameTF.getText().trim(); // 대화명 반드시 3자 이상, 8자 이하

		if (myid.length() < 3 || myid.length() > 8) {
			JOptionPane.showMessageDialog(this, "대화명은 3자이상, 8자 이하 입니다.",
					"대화명 오류", JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			s = new Socket(host, ChatConstant.PORT);
			this.setVisible(false);
			chat.setVisible(true);

			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = s.getOutputStream();

			send(ChatConstant.CCONNECT + "||" + myid); // \n 까지 읽는것이므로 같이
														// 보내줘야한다. //getbytes
														// 바이트 배열로 만듬

			new Thread(this).start(); // run 실행

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
				String msg = in.readLine(); // 메세지를 받음
				System.out.println("클라이언트가 서버가 받은 메세지 : " + msg);
				StringTokenizer st = new StringTokenizer(msg, "||"); // ||를 기준으로
																		// 자름
				int protocol = Integer.parseInt(st.nextToken()); // 프로토콜 확인

				switch (protocol) {
				case ChatConstant.SCONNECT: { // 100||아이디
					String tmp = st.nextToken();
					chat.area.append("[알림] " + tmp + " 님이 입장 하셨습니다.\n");
					chat.list.add(tmp);
				}
					break;
				case ChatConstant.SMESSAGE_ALL: {
					String tmp = st.nextToken(); // 서버가 보내준 메세지를 받음
					chat.area.append(tmp + "\n");
				}
					break;
				case ChatConstant.SMESSAGE_TO: { // 250 || 보낸사람 || 메세지
					String from = st.nextToken(); // 보낸사람
					String tmp = st.nextToken(); // 메세지
					chat.area.append("《" + from + "》" + tmp + "\n");
					chat.whom.setText(from);
				}
					break;
				case ChatConstant.SPAPER: {
					String from = st.nextToken(); // 보낸사람
					String tmp = st.nextToken(); // 메세지
					paper.from.setText(from);
					paper.to.setText(myid);

					paper.letter.setText(tmp.replaceAll("<n>", "\n")); // 다시 변경

					paper.card.show(paper.south1, "answer");

					paper.setVisible(true);

				}
					break;
				case ChatConstant.SRENAME: {
					String oldname = st.nextToken();
					String newname = st.nextToken();
					chat.area.append("변경 전 이름 : " + oldname + ">>"
							+ "변경 후 이름 : " + newname + "\n");

					int len = chat.list.getRows(); // 행의 길이
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
					String outid = st.nextToken();//나가는 사람 이름
					if(myid.equals(outid)){
						in.close();
						out.close();
						s.close(); // 소켓을끄면 in,out 이 꺼지긴 하지만 강제 종료..
						System.exit(0); //종료
					}else{
						chat.area.append("[알림] "+outid+"님이 나갔습니다.");
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
