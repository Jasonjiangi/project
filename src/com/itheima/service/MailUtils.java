package com.itheima.service;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MailUtils {

    public static final String SMTPSERVER = "smtp.qq.com";
    public static final String SMTPPORT = "465";
    public static final String ACCOUT = "1090423582@qq.com";
    public static final String PWD = "qacjyqtwyagejeii";


    public void testSendEmail() throws Exception {

        // �����ʼ�����
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp"); // ʹ�õ�Э�飨JavaMail�淶Ҫ��
        props.setProperty("mail.smtp.host", SMTPSERVER); // �����˵������ SMTP ��������ַ
        props.setProperty("mail.smtp.port", SMTPPORT); 
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.auth", "true"); // ��Ҫ������֤
        props.setProperty("mail.smtp.ssl.enable", "true");// ����ssl


        // �����ʼ����ô����Ự��ע��session�𵼴��
        Session session = Session.getDefaultInstance(props);
        // ����debugģʽ�����Կ���������ϸ��������־
        session.setDebug(true);
        //�����ʼ�
        MimeMessage message = createEmail(session);
        //��ȡ����ͨ��
        Transport transport = session.getTransport();
        transport.connect(SMTPSERVER,ACCOUT, PWD);
        //���ӣ��������ʼ�
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();

    }


    public MimeMessage createEmail(Session session) throws Exception {
        // ���ݻỰ�����ʼ�
        MimeMessage msg = new MimeMessage(session);
        // address�ʼ���ַ, personal�ʼ��ǳ�, charset���뷽ʽ
        InternetAddress fromAddress = new InternetAddress(ACCOUT,
                "kimi", "utf-8");
        // ���÷����ʼ���
        msg.setFrom(fromAddress);
        InternetAddress receiveAddress = new InternetAddress(
                "********@qq.com", "test", "utf-8");
        // �����ʼ����շ�
        msg.setRecipient(RecipientType.TO, receiveAddress);
        // �����ʼ�����
        msg.setSubject("���Ա���", "utf-8");
        msg.setText("���Ǹ�����Ա��һ��������·��һ�ߺ�ˮһ�߿������� ��ʱһ����ؤ���ұ��������ˣ���ʼҪ�����Ҿ��ÿ������͸�����1��Ǯ�� Ȼ����ŵ��Գ������������ⲻ�ã������ĵĿ������ڸ�ʲô��Ȼ�����һ�ᣬ��������ָ���ҵ���Ļ˵���������˸��ֺ�");
        // ������ʾ�ķ���ʱ��
        msg.setSentDate(new Date());
        // ��������
        msg.saveChanges();
        return msg;
    }
}
