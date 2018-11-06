package com.shushan.homework101.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * @Class:
 * @Description:
 * @author: liming
 */
public class Utils {

    public static DisplayMetrics getScreenWH(Context context) {
        DisplayMetrics dMetrics = new DisplayMetrics();
        dMetrics = context.getResources().getDisplayMetrics();
        return dMetrics;
    }

    /**
     * count focus and metering region
     *
     * @param focusWidth
     * @param focusHeight
     * @param areaMultiple
     * @param x
     * @param y
     * @param previewleft
     * @param previewRight
     * @param previewTop
     * @param previewBottom
     * @return Rect(left, top, right, bottom)
     */
    public static Rect calculateTapArea(int focusWidth, int focusHeight,
                                        float areaMultiple, float x, float y, int previewleft,
                                        int previewRight, int previewTop, int previewBottom) {
        int areaWidth = (int) (focusWidth * areaMultiple);
        int areaHeight = (int) (focusHeight * areaMultiple);
        int centerX = (previewleft + previewRight) / 2;
        int centerY = (previewTop + previewBottom) / 2;
        double unitx = ((double) previewRight - (double) previewleft) / 2000;
        double unity = ((double) previewBottom - (double) previewTop) / 2000;
        int left = clamp((int) (((x - areaWidth / 2) - centerX) / unitx),
                -1000, 1000);
        int top = clamp((int) (((y - areaHeight / 2) - centerY) / unity),
                -1000, 1000);
        int right = clamp((int) (left + areaWidth / unitx), -1000, 1000);
        int bottom = clamp((int) (top + areaHeight / unity), -1000, 1000);

        return new Rect(left, top, right, bottom);
    }

    public static int clamp(int x, int min, int max) {
        if (x > max)
            return max;
        if (x < min)
            return min;
        return x;
    }

    /**
     * Check if this device has a camera
     *
     * @param context
     * @return
     */
    public static boolean checkCameraHardware(Context context) {
        if (context != null && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * @param context
     * @return app_cache_path/dirName
     */
    public static String getDBDir(Context context) {
        String path = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    File.separator + "bbk" + File.separator + "cloudteacher" + File.separator + "db";
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                path = externalCacheDir.getPath();
            }
        }
        if (path == null) {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                path = cacheDir.getPath();
            }
        }
        return path;
    }

    /**
     * bitmap rotate
     *
     * @param b
     * @param degrees
     * @return
     */
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
            }
        }
        return b;
    }

    public static final int getHeightInPx(Context context) {
        final int height = context.getResources().getDisplayMetrics().heightPixels;
        return height;
    }

    public static final int getWidthInPx(Context context) {
        final int width = context.getResources().getDisplayMetrics().widthPixels;
        return width;
    }

    public static float dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);

    }

    /* ��U�ַ�ת��Ϊ���ʾ���ַ���, �磺 "\\u55\\u5b57\\u7b26" -> "U�ַ�" ;��\\u�ָ����ת��Ϊ��Ӧ�ַ�*/
    public static String UStr_2_Str(String Ustr0) {
        String Ustr = Ustr0;

        int S = 0, E = 0;
        String C = "", Value = "";

        while (Ustr.contains("\\u")) {
            S = Ustr.indexOf("\\u") + "\\u".length();
            E = Ustr.indexOf("\\u", S);
            if (E == -1) E = Ustr.length();

            if (E > S) {
                C = Ustr.substring(S, E);
                if (C.length() > 4) C = C.substring(0, 4);
                Value = (char) Integer.parseInt(C, 16) + "";

                Ustr = Ustr.replace("\\u" + C, Value);
            }
        }

        return Ustr;
    }

    /**
     * ���ַ���ת��MD5ֵ
     *
     * @param string ��Ҫת�����ַ���
     * @return �ַ�����MD5ֵ
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
    // formatType��ʽΪyyyy-MM-dd HH:mm:ss//yyyy��MM��dd�� HHʱmm��ss��
    // data Date���͵�ʱ��
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    // currentTimeҪת����long���͵�ʱ��
    // formatTypeҪת����string���͵�ʱ���ʽ
    public static String longToString(long currentTime, String formatType) throws ParseException {
        Date date = longToDate(currentTime, formatType);
        // long����ת��Date����
        String strTime = dateToString(date, formatType); // date����ת��String
        return strTime;
    }

    // strTimeҪת����string���͵�ʱ�䣬formatTypeҪת���ĸ�ʽyyyy-MM-dd HH:mm:ss
    // yyyy��MM��dd��
    // HHʱmm��ss�룬
    // strTime��ʱ���ʽ����Ҫ��formatType��ʱ���ʽ��ͬ
    public static Date stringToDate(String strTime, String formatType) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // currentTimeҪת����long���͵�ʱ��
    // formatTypeҪת����ʱ���ʽyyyy-MM-dd HH:mm:ss
    // yyyy��MM��dd�� HHʱmm��ss��
    public static Date longToDate(long currentTime, String formatType) throws ParseException {
        Date dateOld = new Date(currentTime);
        // ����long���͵ĺ���������һ��date���͵�ʱ��
        String sDateTime = dateToString(dateOld, formatType);
        // ��date���͵�ʱ��ת��Ϊstring
        Date date = stringToDate(sDateTime, formatType); // ��String����ת��ΪDate����
        return date;
    }

    // strTimeҪת����String���͵�ʱ��
    // formatTypeʱ���ʽ
    // strTime��ʱ���ʽ��formatType��ʱ���ʽ������ͬ
    public static long stringToLong(String strTime, String formatType) throws ParseException {
        Date date = stringToDate(strTime, formatType);
        // String����ת��date����
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date����ת��long����
            return currentTime;
        }
    }

    // dateҪת����date���͵�ʱ��
    public static long dateToLong(Date date) {
        return date.getTime();
    }


    /**
     *
     * ������;: �����д�����������ֶ�����Unicode���С���������ֵ��򣩣���������url������<br>
     * ʵ�ֲ���: <br>
     *
     * @param paraMap   Ҫ�����Map����
     * @param urlEncode   �Ƿ���ҪURLENCODE
     * @param keyToLower    �Ƿ���Ҫ��Keyת��ΪȫСд
     *            true:keyת����Сд��false:��ת��
     * @return
     */
    public static String formatUrlMap(Map<String, String> paraMap, boolean urlEncode, boolean keyToLower)
    {
        String buff = "";
        Map<String, String> tmpMap = paraMap;
        try
        {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());
            // �����д�����������ֶ����� ASCII ���С���������ֵ���
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>()
            {

                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2)
                {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // ����URL ��ֵ�Եĸ�ʽ
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds)
            {
                //if (StringUtils.isNotBlank(item.getKey()))
                if(item.getKey() != null && item.getKey().length() > 0 && item.getKey().trim().length() > 0)
                {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlEncode)
                    {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower)
                    {
                        buf.append(key.toLowerCase() + "=" + val);
                    } else
                    {
                        buf.append(key + "=" + val);
                    }
                    buf.append("&");
                }

            }
            buff = buf.toString();
            if (buff.isEmpty() == false)
            {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e)
        {
            return null;
        }
        return buff;
    }
    /**
     * ��ò�����ʽ���ַ���
     * ���������ֵ�����Сд�ں���
     */
    public static String getFormatParams(Map<String,String> params){
        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(params.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> arg0, Map.Entry<String, String> arg1) {
                return (arg0.getKey()).compareTo(arg1.getKey());
            }
        });
        String ret = "";

        for (Map.Entry<String, String> entry : infoIds) {
            ret += entry.getKey();
            ret += "=";
            ret += entry.getValue();
            ret += "&";
        }
        ret = ret.substring(0, ret.length() - 1);
        return ret;
    }
}
