package intel.oneman.aassist;

public class Feature {
    public static final int INPUT_NONE = 0 , INPUT_KEYBOARD = 1 , INPUT_VOICE = 2, INPUT_SELECT_LIST = 3;
    public String context,name,faprefix,submit_url,fn;
    public int fields,params,format;
    public String[] fnames,fdtypes,cparams,fvalues,fdsispn,ulist;
    public int[] finputm,storein;
    public static final  int ACTION = 1 ,NAME = 2 , FDTYPE = 3 ;
    public Feature(String context , String name , String fn ,String submit_url , int params , int format, String[] cparams , int[] storein ,
                   int fields,String[] fnames,String[] fdispn, String[] fdtypes, int[] finputm){
        this.context=context;
        this.name=name;
        this.params=params;
        this.cparams=cparams;
        this.storein=storein;
        this.fields=fields;
        this.fnames=fnames;
        this.fdsispn=fdispn;
        this.fdtypes=fdtypes;
        this.faprefix=name+"."+name+"-";
        this.finputm=finputm;
        this.format = format;
        this.fvalues=new String[this.fields];
        this.submit_url=submit_url;
        this.fn = fn;
        for(int i =0 ; i < this.fields;i++)
            this.fvalues[i] = "";
    }
    public int getIndex(int stype,String value){
        switch (stype){
            case ACTION:
                for(int i = 0 ; i < fields;i++){
                    if(fnames[i].compareTo(value.substring(0,this.faprefix.length())) == 0)
                        return i;
                }
                break;
            case NAME:
                for(int i = 0 ; i < fields;i++){
                    if(fnames[i].compareTo(value) == 0)
                        return i;
                }
                break;
            case FDTYPE:
                for(int i = 0 ; i < fields;i++){
                    if(fdtypes[i].compareTo(value) == 0)
                        return i;
                }
                break;
        }
        return -1;
    }
    public void truncate(){
        for(int i =0 ; i < this.fields;i++)
            this.fvalues[i] = "";
    }
    public void cleanUp(){
        for(int i = 0 ; i < this.fields;i++){
            this.fvalues[i]="";
        }
    }
}
