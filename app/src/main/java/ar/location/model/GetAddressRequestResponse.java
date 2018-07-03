package ar.location.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetAddressRequestResponse implements Serializable {

    @SerializedName("recordsets")
    @Expose
    private ArrayList<Center> recordsets;

    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    class Center{
        @SerializedName("CenterID")
        @Expose
        private int CenterID;

        public int getCenterID() {
            return CenterID;
        }

        public void setCenterID(int centerID) {
            CenterID = centerID;
        }
    }
}