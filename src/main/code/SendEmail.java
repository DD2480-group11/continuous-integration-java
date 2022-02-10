package main.code;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail
{
    // To read text from a txt file
    static String readFile(String file){
        StringBuilder data = new StringBuilder();
        try{
            URL path = SendEmail.class.getResource(file);
            assert path != null;
            File text = new File(path.getFile());
            Scanner readText = new Scanner(text);
            while (readText.hasNextLine()) {
                data.append("\n").append(readText.nextLine());
            }
            readText.close();
        } catch (
                FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data.toString();
    }

    static void sendFromServer(String recipient, String text){

        String sender = "ciserverupdate@gmail.com";
        String password = "skickamail1!";

        // setting up gmail smtp
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); //TLS

        // authenticate to gmail
        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender,password);
                    }
                });
        try
        {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Result from CI Server");
            message.setText(text);
            Transport.send(message);
        }
        catch (MessagingException mex)
        {
            mex.printStackTrace();
        }

    }

    public static void main(String [] args)
    {
        sendFromServer("beab@kth.se", readFile("content.txt"));
    }

}