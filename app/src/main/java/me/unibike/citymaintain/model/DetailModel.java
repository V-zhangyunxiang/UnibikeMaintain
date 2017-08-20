package me.unibike.citymaintain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by VULCAN on 2017/7/13.
 */

public class DetailModel implements Parcelable {
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


    public static class PayLoad implements Parcelable {
        private int id;
        private String password;
        private String bike_status;
        private LockInfo lock_info;
        private LastRide last_ride;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getBike_status() {
            return bike_status;
        }

        public void setBike_status(String bike_status) {
            this.bike_status = bike_status;
        }

        public LockInfo getLock_info() {
            return lock_info;
        }

        public void setLock_info(LockInfo lock_info) {
            this.lock_info = lock_info;
        }

        public LastRide getLast_ride() {
            return last_ride;
        }

        public void setLast_ride(LastRide last_ride) {
            this.last_ride = last_ride;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.password);
            dest.writeString(this.bike_status);
            dest.writeParcelable(this.lock_info, flags);
            dest.writeParcelable(this.last_ride, flags);
        }

        public PayLoad() {
        }

        protected PayLoad(Parcel in) {
            this.id = in.readInt();
            this.password = in.readString();
            this.bike_status = in.readString();
            this.lock_info = in.readParcelable(LockInfo.class.getClassLoader());
            this.last_ride = in.readParcelable(LastRide.class.getClassLoader());
        }

        public static final Creator<PayLoad> CREATOR = new Creator<PayLoad>() {
            @Override
            public PayLoad createFromParcel(Parcel source) {
                return new PayLoad(source);
            }

            @Override
            public PayLoad[] newArray(int size) {
                return new PayLoad[size];
            }
        };
    }

    public static class LockInfo implements Parcelable {
        private int id;
        private int voltage;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getVoltage() {
            return voltage;
        }

        public void setVoltage(int voltage) {
            this.voltage = voltage;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeInt(this.voltage);
        }

        public LockInfo() {
        }

        protected LockInfo(Parcel in) {
            this.id = in.readInt();
            this.voltage = in.readInt();
        }

        public static final Creator<LockInfo> CREATOR = new Creator<LockInfo>() {
            @Override
            public LockInfo createFromParcel(Parcel source) {
                return new LockInfo(source);
            }

            @Override
            public LockInfo[] newArray(int size) {
                return new LockInfo[size];
            }
        };
    }

    public static class LastRide implements Parcelable {
        private int order_id;
        private int created_at;
        private int finished_at;
        private int cost;
        private int used_time;

        public int getOrder_id() {
            return order_id;
        }

        public void setOrder_id(int order_id) {
            this.order_id = order_id;
        }

        public int getCreated_at() {
            return created_at;
        }

        public void setCreated_at(int created_at) {
            this.created_at = created_at;
        }

        public int getFinished_at() {
            return finished_at;
        }

        public void setFinished_at(int finished_at) {
            this.finished_at = finished_at;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public int getUsed_time() {
            return used_time;
        }

        public void setUsed_time(int used_time) {
            this.used_time = used_time;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.order_id);
            dest.writeInt(this.created_at);
            dest.writeInt(this.finished_at);
            dest.writeInt(this.cost);
            dest.writeInt(this.used_time);
        }

        public LastRide() {
        }

        protected LastRide(Parcel in) {
            this.order_id = in.readInt();
            this.created_at = in.readInt();
            this.finished_at = in.readInt();
            this.cost = in.readInt();
            this.used_time = in.readInt();
        }

        public static final Creator<LastRide> CREATOR = new Creator<LastRide>() {
            @Override
            public LastRide createFromParcel(Parcel source) {
                return new LastRide(source);
            }

            @Override
            public LastRide[] newArray(int size) {
                return new LastRide[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeParcelable(this.payload, flags);
    }

    public DetailModel() {
    }

    protected DetailModel(Parcel in) {
        this.code = in.readInt();
        this.payload = in.readParcelable(PayLoad.class.getClassLoader());
    }

    public static final Creator<DetailModel> CREATOR = new Creator<DetailModel>() {
        @Override
        public DetailModel createFromParcel(Parcel source) {
            return new DetailModel(source);
        }

        @Override
        public DetailModel[] newArray(int size) {
            return new DetailModel[size];
        }
    };
}
