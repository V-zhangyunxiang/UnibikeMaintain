package me.unibike.citymaintain.model;

/**
 * Created by VULCAN on 2017/6/24.
 */

public class LoginModel {
    private int code;
    private  PayLoad payload;
    public PayLoad getPayload() {
        return payload;
    }

    public void setPayload(PayLoad payload) {
        this.payload = payload;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    //--------------------------
    public class PayLoad{
        private String token;
        private UniAccount uni_account;
        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }
        public UniAccount getUni_account() {
            return uni_account;
        }

        public void setUni_account(UniAccount uni_account) {
            this.uni_account = uni_account;
        }
    }
    ///------------------------------------
    public class UniAccount{
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
