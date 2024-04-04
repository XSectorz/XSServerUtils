package net.xsapi.panat.xsserverutilsbungee.objects;

public class XSBanplayers {

    public String uuid;
    public int idRef;
    public String reason;
    public double creation_date;
    public double end_date;
    public String banner;

    public XSBanplayers(String uuid,int idRef,String reason,double creation_date,double end_date,String banner) {
        this.uuid = uuid;
        this.idRef = idRef;
        this.reason = reason;
        this.creation_date = creation_date;
        this.end_date = end_date;
        this.banner = banner;
    }

    public double getCreation_date() {
        return creation_date;
    }

    public double getEnd_date() {
        return end_date;
    }

    public String getBanner() {
        return banner;
    }

    public String getReason() {
        return reason;
    }

    public String getUuid() {
        return uuid;
    }

    public int getIdRef() {
        return idRef;
    }
}
