package me.unibike.unilock.data.lock;

/**
 * @author LuoLiangchen
 * @since 16/9/28
 *
 */
public class Instruction {
    public static final String NONE = "none";
    public static final String DETECT_POWER = "detectPower";
    public static final String UNLOCK = "unlock";
    public static final String GET_TOKEN = "getToken";
    public static final String LED = "led";
    public static final String BUZZER = "buzzer";
    //public static final String CHANGE_AES_SECRET = "changeAesSecret";
    public static final String GET_LOCK_STATUS = "getLockStatus";

    String raw;

    String instruction;

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
//    public byte[] ota(){
//        return string2byte("A0003344000000000000000000000000");
//    }
    public byte[] cmd() {
        return string2byte(instruction);
    }
    private byte[] string2byte(String response) {
        byte[] result = new byte[16];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (Integer.parseInt(response.substring(i * 2, i * 2 + 2), 16) & 0xff);
        }
        return result;
    }
}
