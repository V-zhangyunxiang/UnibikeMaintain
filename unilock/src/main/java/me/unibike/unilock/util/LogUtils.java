package me.unibike.unilock.util;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


/**
 * @author LuoLiangchen
 * @since 16/9/28
 */

public class LogUtils {

    public final static SparseArray<String> motionEventMap;

    static {
        if (AppUtils.debugging()) {
            motionEventMap = new SparseArray<String>();
            Class<MotionEvent> clazz = MotionEvent.class;
            Field[] fields = clazz.getFields();
            String name;
            try {
                for (Field f : fields) {
                    name = f.getName();
                    if (name.contains("ACTION")) motionEventMap.put((Integer) f.get(clazz), name);

                }
            } catch (Exception e) {
                LogUtils.ex(e);
            }
        } else {
            motionEventMap = null;
        }
    }

    public static String motionType(int key) {
        if (motionEventMap == null) return StringUtils.EMPTY;
        else return motionEventMap.get(key);
    }


    private static final DateFormat timeStamp = new SimpleDateFormat("HH:mm ===> ss:SSS", Locale.US);

    public static void log(String tag, Object... objects) {
        if (!AppUtils.debugging()) return;
        StringBuilder sb = new StringBuilder("===>").append(tag).append("<===")
                .append("\t")
                .append("log time")
                .append(" : ")
                .append(timeStamp.format(System.currentTimeMillis()))
                .append("\n");
        if (objects.length == 1) {
            sb.append("--->");
            if (objects[0] == null) {
                sb.append("null");
            } else {
                sb.append(objects[0].getClass().getSimpleName())
                        .append(":")
                        .append(objects[0].toString());
            }
            sb.append("<---");
        } else {
            for (int i = 0; i < objects.length; i++) {
                if (i % 2 == 0) {
                    if (i > 0) sb.append("\n");
                    sb.append(objects[i])
                            .append("===>");
                } else {
                    sb.append(objects[i] == null ? "null" : (objects[i].getClass().getSimpleName() + " : " + objects[i]));
                }
            }
        }
        d(tag, sb.toString());
    }

    public static void tableLog(String tag, String tableName, Object... things) {
        if (!AppUtils.debugging()) return;
        StringBuilder sb = new StringBuilder(tableName)
                .append(" : ")
                .append(timeStamp.format(System.currentTimeMillis()));
        int rows = things.length / 2;
        String keys[] = new String[rows];
        String clazz[] = new String[rows];
        String values[] = new String[rows];
        int i;
        int j;
        int maxKeyLength = -1;
        int maxValueLength = -1;
        int maxClassLengtht = -1;
        for (i = 0, j = 0; j < rows; j++, i += 2) {
            keys[j] = things[i].toString();
            if (keys[j].length() > maxKeyLength) maxKeyLength = keys[j].length();
            values[j] = things[i + 1] == null ? "null" : things[i + 1].toString();
            if (values[j].length() > maxValueLength) maxValueLength = values[j].length();
            clazz[j] = things[i + 1] == null ? "null obj" : things[i + 1].getClass().getSimpleName();
            if (clazz[j].length() > maxClassLengtht) maxClassLengtht = clazz[j].length();
        }
        String title = sb.toString();
        int titleLength = title.length() + 2;
        int totalLength = maxKeyLength + maxValueLength + maxClassLengtht + 4;
        if (titleLength > totalLength) totalLength = titleLength;

        if (totalLength > (maxKeyLength + maxValueLength + maxClassLengtht + 4))
            maxValueLength = totalLength - (maxKeyLength + maxClassLengtht + 4);

        titleLength -= 2;
        sb.delete(0, sb.length());
        sb.append("┌");
        for (i = 0; i < totalLength - 2; i++) {
            sb.append("─");
        }
        sb.append("┐");
        sb.append("\n");
        sb.append("│");
        int spaceNum = totalLength - 2 - titleLength;
        for (i = 0; i < spaceNum / 2; i++) {
            sb.append(" ");
        }
        sb.append(title);
        //奇数会错位
        for (i = 0; i < (totalLength - 2 - titleLength) / 2 + spaceNum % 2; i++) {
            sb.append(" ");
        }
        sb.append("│");
        sb.append("\n");

        sb.append("├");
        for (i = 0; i < maxKeyLength; i++) {
            sb.append("─");
        }
        sb.append("┬");
        for (i = 0; i < maxClassLengtht; i++) {
            sb.append("─");
        }
        sb.append("┬");
        for (i = 0; i < maxValueLength; i++) {
            sb.append("─");
        }
        sb.append("┤")
                .append("\n");
        for (i = 0; i < rows; i++) {
            sb.append("│").append(keys[i]);
            for (j = 0; j < maxKeyLength - keys[i].length(); j++) {
                sb.append(" ");
            }
            sb.append("│").append(clazz[i]);
            for (j = 0; j < maxClassLengtht - clazz[i].length(); j++) {
                sb.append(" ");
            }
            sb.append("│").append(values[i]);
            for (j = 0; j < maxValueLength - values[i].length(); j++) {
                sb.append(" ");
            }
            sb.append("│").append("\n");

            if (i < rows - 1) {
                sb.append("├");
                for (j = 0; j < maxKeyLength; j++) {
                    sb.append("─");
                }
                sb.append("┼");
                for (j = 0; j < maxClassLengtht; j++) {
                    sb.append("─");
                }
                sb.append("┼");
                for (j = 0; j < maxValueLength; j++) {
                    sb.append("─");
                }
                sb.append("┤")
                        .append("\n");
            } else {
                sb.append("└");
                for (j = 0; j < maxKeyLength; j++) {
                    sb.append("─");
                }
                sb.append("┴");
                for (j = 0; j < maxClassLengtht; j++) {
                    sb.append("─");
                }
                sb.append("┴");
                for (j = 0; j < maxValueLength; j++) {
                    sb.append("─");
                }
                sb.append("┘")
                        .append("\n");
            }

        }
        d(tag, sb.toString());
    }

    public static void logJavaFile(String tag, Object o) {
        if (!AppUtils.debugging()) return;
        d("java", StringUtils.splice(tag, "\n", o.getClass().getName(), "(", o.getClass().getSimpleName(), ".java:1)"));
    }

    public static void d(String tag, Object content) {
        if (!AppUtils.debugging()) return;
        Log.d(tag, ObjectUtils.toString(content, "null"));
    }

    public static void i(String tag, Object content) {
        if (!AppUtils.debugging()) return;
        Log.i(tag, ObjectUtils.toString(content, "null"));
    }

    public static void w(String tag, Object content) {
        if (!AppUtils.debugging()) return;
        Log.w(tag, ObjectUtils.toString(content, "null"));
    }

    public static void e(String tag, Object content) {
        if (!AppUtils.debugging()) return;
        Log.e(tag, ObjectUtils.toString(content, "null"));
    }

    public static void ex(Throwable t) {
        if (!AppUtils.debugging()) return;
        Log.w("catch!", t);
    }

    public static void ex(String tag, Throwable t) {
        if (!AppUtils.debugging()) return;
        Log.e("catch!", tag, t);
    }

    public static String toString(Map<?, ?> map) {
        if (CollectionUtils.isEmpty(map)) return "[size=0]";
        StringBuilder sb = new StringBuilder("[size=")
                .append(map.size())
                .append(";");
        Iterator<? extends Map.Entry<?, ?>> it = map.entrySet().iterator();
        Map.Entry<?, ?> e;
        for (int i = 0; it.hasNext(); i++) {
            e = it.next();
            sb.append(i)
                    .append(":")
                    .append(ObjectUtils.toString(e.getKey(), "null"))
                    .append("->")
                    .append(ObjectUtils.toString(e.getValue(), "null"))
                    .append(";");
        }
        return sb.append("]").toString();
    }

    public static void printActivityJumpTrace(Intent intent, Object obj, StackTraceElement[] callers) {
        if (AppUtils.debugging()) {
            StringBuilder sb = new StringBuilder();
            ComponentName c = intent.getComponent();
            String targetClassName = c.getClassName();
            sb.append("from")
                    .append(" : ")
                    .append(obj.getClass().getSimpleName())
                    .append(" ===> ")
                    .append("to")
                    .append(" : ")
                    .append("\n")
                    .append(targetClassName)
                    .append("(")
                    .append(targetClassName.substring(targetClassName.lastIndexOf(".") + 1))
                    .append(".java:1")
                    .append(")");

            sb.append("\n");
            sb.append("at\t")
                    .append(callers[4].toString());
            log("activity", "activity jump track", sb.toString());
        }
    }

    public static void throwe(Throwable e) {
        if (AppUtils.debugging()) throw new RuntimeException(e);
    }
}
