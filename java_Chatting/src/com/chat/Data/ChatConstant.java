package com.chat.Data;

public class ChatConstant {
	public static final int PORT = 9876;
	
	//chat protocol 정의 // CLIENT >>>>SERVER
	public static final int CCONNECT = 100;//접속용						CONNECT||대화명
	public static final int CMESSAGE_ALL= 200;//전체 메세지 전달 			MESSAGE_ALL||메세지내용
	public static final int CMESSAGE_TO= 250;//특정인에게 메세지 전달		MESSAGE_TO||귓속말받을사람||메세지내용
	public static final int CPAPER = 300; // 쪽지						PAPER||받는사람||쪽지내용
	public static final int CRENAME = 400; //이름 변경					RENAME||변경할대화명
	public static final int CDISCONNECT = 900; // 접속 종료				DISCONNECT||
	
	//chat protocol 정의 //  SERVER>>>>CLIENT
	public static final int SCONNECT = 100;//접속용						CONNECT||접속자대화명
	public static final int SMESSAGE_ALL= 200;//전체 메세지 전달 			MESSAGE_ALL||대화명] 메세지내용
	public static final int SMESSAGE_TO= 250;//특정인에게 메세지 전달 		MESSAGE_TO||귓속말보낸사람||메세지내용
	public static final int SPAPER = 300; // 쪽지						PAPER||보낸사람||쪽지내용
	public static final int SRENAME = 400; //이름 변경					RENAME||변경전대화명||변경할대화명
	public static final int SDISCONNECT = 900; // 접속 종료				DISCONNECT||나가는사람대화명

	
}
