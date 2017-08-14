package email;

import java.io.Serializable;
import java.util.GregorianCalendar;

public class Email implements Serializable {
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String body;
    
    private GregorianCalendar timestamp;

    public Email() {
        this.timestamp = new GregorianCalendar();
    }

    public Email(String to, String cc, String bcc, String subject, String body) {
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.body = body;
        this.timestamp = new GregorianCalendar();
    }

    @Override
    public String toString() {
        return String.format("To: %s\n"
                + "CC: %s\n"
                + "BCC: %s\n"
                + "Subject: %s\n"
                + "%s", to, cc, bcc, subject, body);
    }
    
    

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public GregorianCalendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(GregorianCalendar timestamp) {
        this.timestamp = timestamp;
    }
    
    
}
