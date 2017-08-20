package me.unibike.unilock.data.lock;

/**
 * @author LuoLiangchen
 * @since 16/9/28
 *
 */
public class Response {
//    private String resDecrypted;
//    private int insNum;
//    private int insStatus;
//    private boolean canReturn;
//    private int battery;
//    private PayLoad payload;
      private String token;
      private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    //    public PayLoad getPayload() {
//        return payload;
//    }
//
//    public void setPayload(PayLoad payload) {
//        this.payload = payload;
//    }

//    public String getResDecrypted() {
//        return resDecrypted;
//    }
//
//    public void setResDecrypted(String resDecrypted) {
//        this.resDecrypted = resDecrypted;
//    }
//
//    public int getInsNum() {
//        return insNum;
//    }
//
//    public void setInsNum(int insNum) {
//        this.insNum = insNum;
//    }
//
//    public int getInsStatus() {
//        return insStatus;
//    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

//    public void setInsStatus(int insStatus) {
//        this.insStatus = insStatus;
//    }
//
//    public boolean isCanReturn() {
//        return canReturn;
//    }
//
//    public void setCanReturn(boolean canReturn) {
//        this.canReturn = canReturn;
//    }
//
//    public int getBattery() {
//        return battery;
//    }
//
//    public void setBattery(int battery) {
//        this.battery = battery;
//    }
//    public class PayLoad{
//        private String token;
//
//        public String getToken() {
//            return token;
//        }
//
//        public void setToken(String token) {
//            this.token = token;
//        }
//    }
}
