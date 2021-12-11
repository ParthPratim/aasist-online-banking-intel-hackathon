package intel.oneman.aassist;

public class netbAccount {
    public String  userName , passwd;
    public int bankid ;
    public netbAccount(String bankname , String username ,String passwd){
        setBankid(bankname);
        setUserName(username);
        setPasswd(passwd);
    }
    public void setBankid(String bankname){
        if(bankname.compareTo("State Bank Of India") == 0)
        {
            this.bankid = 2018001;
        }
        else if(bankname.compareTo("Axis Bank") == 0)
        {
            this.bankid = 2018002;
        }else {
            this.bankid = 0;
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
    public String getUserName(){
        return  this.userName;
    }
    public String getPasswd(){
        return this.passwd;
    }
    public void clear(){
        setBankid("");
        setUserName("");
        setPasswd("");
    }
}
