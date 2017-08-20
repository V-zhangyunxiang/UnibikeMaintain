package me.unibike.citymaintain.model;

import java.util.ArrayList;

/**
 * Created by VULCAN on 2017/7/11.
 */

public class NearBikeModel {
      private int ts;
      private int code;
      private PayLoad payload;

    public int getTs() {
        return ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public PayLoad getPayload() {
        return payload;
    }

    public void setPayload(PayLoad payload) {
        this.payload = payload;
    }

    public class PayLoad{
        public ArrayList<BikeMsg> city_bikes;

        public ArrayList<BikeMsg> getCity_bikes() {
            return city_bikes;
        }

        public void setCity_bikes(ArrayList<BikeMsg> city_bikes) {
            this.city_bikes = city_bikes;
        }
    }
    public class BikeMsg{
        private int id;
        private String latest_lnglat;
        private String bike_status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLatest_lnglat() {
            return latest_lnglat;
        }

        public void setLatest_lnglat(String latest_lnglat) {
            this.latest_lnglat = latest_lnglat;
        }

        public String getBike_status() {
            return bike_status;
        }

        public void setBike_status(String bike_status) {
            this.bike_status = bike_status;
        }
    }

}
