package com.chat.Data;

public class ChatConstant {
	public static final int PORT = 9876;
	
	//chat protocol ���� // CLIENT >>>>SERVER
	public static final int CCONNECT = 100;//���ӿ�						CONNECT||��ȭ��
	public static final int CMESSAGE_ALL= 200;//��ü �޼��� ���� 			MESSAGE_ALL||�޼�������
	public static final int CMESSAGE_TO= 250;//Ư���ο��� �޼��� ����		MESSAGE_TO||�ӼӸ��������||�޼�������
	public static final int CPAPER = 300; // ����						PAPER||�޴»��||��������
	public static final int CRENAME = 400; //�̸� ����					RENAME||�����Ҵ�ȭ��
	public static final int CDISCONNECT = 900; // ���� ����				DISCONNECT||
	
	//chat protocol ���� //  SERVER>>>>CLIENT
	public static final int SCONNECT = 100;//���ӿ�						CONNECT||�����ڴ�ȭ��
	public static final int SMESSAGE_ALL= 200;//��ü �޼��� ���� 			MESSAGE_ALL||��ȭ��] �޼�������
	public static final int SMESSAGE_TO= 250;//Ư���ο��� �޼��� ���� 		MESSAGE_TO||�ӼӸ��������||�޼�������
	public static final int SPAPER = 300; // ����						PAPER||�������||��������
	public static final int SRENAME = 400; //�̸� ����					RENAME||��������ȭ��||�����Ҵ�ȭ��
	public static final int SDISCONNECT = 900; // ���� ����				DISCONNECT||�����»����ȭ��

	
}
