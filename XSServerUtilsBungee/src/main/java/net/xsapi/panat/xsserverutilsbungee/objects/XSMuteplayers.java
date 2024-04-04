package net.xsapi.panat.xsserverutilsbungee.objects;

public class XSMuteplayers {

    public String uuid;
    public int idRef;
    public String reason;
    public double creation_date;
    public double end_date;
    public String muter;

    public XSMuteplayers(String uuid, int idRef, String reason, double creation_date, double end_date, String muter) {
        this.uuid = uuid;
        this.idRef = idRef;
        this.reason = reason;
        this.creation_date = creation_date;
        this.end_date = end_date;
        this.muter = muter;
    }

    public double getCreation_date() {
        return creation_date;
    }

    public double getEnd_date() {
        return end_date;
    }

    public String getMuter() {
        return muter;
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
