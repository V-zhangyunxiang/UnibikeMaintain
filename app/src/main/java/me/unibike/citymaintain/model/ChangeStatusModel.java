package me.unibike.citymaintain.model;

/**
 * Created by VULCAN on 2017/7/20.
 */

public class ChangeStatusModel {
    private int code;
    private PayLoad payload;

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
        private CityBike city_bike;

        public CityBike getCity_bike() {
            return city_bike;
        }

        public void setCity_bike(CityBike city_bike) {
            this.city_bike = city_bike;
        }
    }
    public class CityBike{
        private String bike_status;

        public String getBike_status() {
            return bike_status;
        }

        public void setBike_status(String bike_status) {
            this.bike_status = bike_status;
        }
    }

}
