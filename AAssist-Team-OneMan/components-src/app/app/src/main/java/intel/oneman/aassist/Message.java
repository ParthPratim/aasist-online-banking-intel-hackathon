package intel.oneman.aassist;


public class Message {
    private String sender,message;
    public Message(String m_sender, String m_message){
        this.setSender(m_sender);
        this.setMessage(m_message);
    }
    public void setSender(String m_sender){
        this.sender = m_sender;
    }
    public void setMessage(String m_message){
        this.message = m_message;
    }
    public String getSender() {
        return this.sender;
    }
    public String getMessage(){
        return this.message;
    }

}
