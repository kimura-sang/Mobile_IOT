package com.chug.north_outlet.utils;

import android.content.Context;
import android.util.Log;

import com.chug.north_outlet.App;
import com.chug.north_outlet.Config;
import com.chug.north_outlet.R;
import com.chug.north_outlet.bean.ControlDevice;
import com.chug.north_outlet.bean.EFDevice;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.dao.DaoUtil;

import org.xutils.db.sqlite.WhereBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Sean.guo
 * @date 2015-8-25 上午11:16:24
 * @filename ByteUtils.java
 */
public class ByteUtils {

    /**
     * @param @return 设定文件
     * @return byte[] 返回类型
     * @throws
     * @Title: getPhonenumberBytes
     * @Description: TODO(得到手机号为20个字节的数组)
     */
    public static byte[] getPhonenumberBytes() {

//        String p = PreferenceHelper.readString(Config.USER_PHONE, "");
        String p =  Constant.userIdFromServer;

        byte[] pho = p.getBytes();
        byte[] phoneData = new byte[20];
        System.arraycopy(pho, 0, phoneData, 0, pho.length);
        return phoneData;
    }

    //<<<<mars_add_20190826
    private String readFromFile(Context context) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("config.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
//                showToast(ret);
            }
        }
        catch (FileNotFoundException e) {
            Log.e("SigninActivity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("SigninActivity", "Can not read file: " + e.toString());
        }
        return ret;
    }
    //>>>>



    /**
     * @param @param  hexString
     * @param @return 设定文件
     * @return byte[] 返回类型
     * @throws
     * @Title: hexStringToBytes
     * @Description: TODO(hex字符串转byte数组)
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * @param @param  b
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: bytesToHexString
     * @Description: TODO(byte数组转hex字符串)
     */
    public static String bytesToHexString(byte[] b) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF).toUpperCase();
            if (hex.length() == 1) {
                str.append('0');
            }
            str.append(hex);
        }
        return str.toString().toUpperCase();
    }
    //设备ip地址
    public static String getDeviecIP(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(bytes[0]&0xff);
        stringBuilder.append('.');
        stringBuilder.append(bytes[1]&0xff);
        stringBuilder.append('.');
        stringBuilder.append(bytes[2]&0xff);
        stringBuilder.append('.');
        stringBuilder.append(bytes[3]&0xff);
        String deviceIP = stringBuilder.toString();
        return deviceIP;
    }

    /**
     * @param @param  bytes
     * @param @param  offset
     * @param @param  len
     * @param @return 设定文件
     * @return byte[] 返回类型
     * @throws
     * @Title: arrayCopyBytes
     * @Description: TODO(数组截取)
     */
    public static byte[] arrayCopyBytes(byte[] bytes, int offset, int len) {
        byte[] b = new byte[len];
        System.arraycopy(bytes, offset, b, 0, len);
        return b;
    }

    /**
     * @param @param  fromBytes
     * @param @param  offset
     * @param @param  copylen
     * @param @param  tolen
     * @param @return 设定文件
     * @return byte[] 返回类型
     * @throws
     * @Title: arrayCopyBytes
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public static byte[] arrayCopyBytes(byte[] fromBytes, int offset,
                                        int copylen, int tolen) {
        byte[] b = new byte[tolen];
        System.arraycopy(fromBytes, offset, b, 0, copylen);
        return b;
    }
    //发送系统时间
    public static byte[] getSystemTimeBytes(Long l) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (l>>4);
        bytes[1] = (byte) (l>>12);
        bytes[2] = (byte) (l>>20);
        bytes[3] = (byte) (l>>28);
        return bytes;
    }

    public static byte[] getOnTimeBytes(Long l) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (l>>4);
        bytes[1] = (byte) (l>>12);
        return bytes;
    }
    /**
     * @param @param  bytes
     * @param @return 设定文件
     * @return byte[] 返回类型
     * @throws
     * @Title: getCheckCode
     * @Description: TODO(获取校验数)
     */
    public static byte[] getCheckCode(byte[] bytes) {
        byte[] b = arrayCopyBytes(bytes, 78, 2);
        return b;
    }

    /**
     * @param
     * @param bs
     * @return
     */
    public static byte[] appendAuto(byte[]... bs) {
        int size = 0;
        for (byte[] bs2 : bs) {
            if (bs2 != null) {
                size = size + bs2.length;
            }
        }
        byte[] newByte = new byte[size];
        int tempLenght = 0;
        for (int i = 0; i < bs.length; i++) {
            if (bs[i] == null) {
                continue;
            }
            System.arraycopy(bs[i], 0, newByte, tempLenght, bs[i].length);
            tempLenght += bs[i].length;
        }
        return newByte;
    }

    public static Date shiftTimeZone(Date date, TimeZone sourceTimeZone, TimeZone targetTimeZone) {
        Calendar sourceCalendar = Calendar.getInstance();
        sourceCalendar.setTime(date);
        sourceCalendar.setTimeZone(sourceTimeZone);

        Calendar targetCalendar = Calendar.getInstance();
        for (int field : new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND}) {
            targetCalendar.set(field, sourceCalendar.get(field));
        }
        targetCalendar.setTimeZone(targetTimeZone);

        return targetCalendar.getTime();
    }

    public static long convertLocalTimeToBeijingTime(long localTimeStr){
        Calendar currentdate = Calendar.getInstance();
        Date date = new Date(localTimeStr);
        currentdate.setTime(date);
        java.text.DateFormat formatter = new SimpleDateFormat("hh:mm");
        TimeZone obj = TimeZone.getTimeZone("Asia/Shanghai");
        formatter.setTimeZone(obj);
        date = currentdate.getTime();
        date.getHours();
        System.out.println("Local:: " +currentdate.getTime());
        System.out.println("china:: "+ formatter.format(currentdate.getTime()));
        return date.getTime();
    }

    /**
     * @param @param  size
     * @param @param  bs
     * @param @return 设定文件
     * @return byte[] 返回类型
     * @throws
     * @Title: append
     * @Description: TODO(byte数组拼接)
     */
    public static byte[] append(int size, byte[]... bs) {
        byte[] newByte = new byte[size];
        int tempLenght = 0;
        for (int i = 0; i < bs.length; i++) {
            System.arraycopy(bs[i], 0, newByte, tempLenght, bs[i].length);
            tempLenght += bs[i].length;
        }
        return newByte;
    }



    /**
     * 解析单个插座
     *
     * @param data
     * @return
     */
    public static EFDeviceOutlet decodeSigOutlet(byte[] data) {
        EFDeviceOutlet outlet = null;
        byte[] temp=null;
        byte b1=data[1];
        byte b2=data[2];
        if (b1 == 00 && (b2 == 24||b2==25||b2==28)) {
            temp = arrayCopyBytes(data, 7, 17);
        } else {

            temp = arrayCopyBytes(data, 0, 20);
        }
        String phone = new String(temp).trim();
        if (!phone.equals(PreferenceHelper.readString(Config.USER_PHONE))) {// Config.USER_PHONE
            return outlet;
        }
        //判断是否是乐鑫插排
        if (b1 == 00 && (b2== 24||b2==25)) {
            int deviceType=0;
            if (b2 == 24) {

                deviceType = EFDevice.TYPE_WIFI_PAI_OUTLET;
            } else {
                deviceType=EFDevice.TYPE_WIF_OUTLET_LEXIN;
            }
            outlet = new EFDeviceOutlet();

            outlet.setDeviceType(deviceType);
            outlet.setDeviceState(data[44]);
            outlet.setLimits(data[33]);
            if (data[33] == 3) {
                outlet.setHaveAuthority(true);
            } else {
                outlet.setHaveAuthority(false);
            }
            String deviceMac = bytesToHexString(arrayCopyBytes(data, 37, 6));
            outlet.setDeviceMac(deviceMac);
            outlet.setLock(data[45] & 0xff);
            StringBuilder builder = new StringBuilder();
            builder.append(data[34] & 0xff);
            builder.append(".");
            builder.append(data[35] & 0xff);
            builder.append(".");
            builder.append(data[36] & 0xff);
            String fwVersion = builder.toString();
            outlet.setFirmwareVersion(fwVersion);
        } else {
            int deviceType = b2==28?data[52]:(data[20] & 0xff);
            if (deviceType == EFDevice.TYPE_ZIG_OUTLET) {
                outlet = new EFDeviceOutlet();
                outlet.setDeviceType(deviceType);
                outlet.setDeviceState(b2==28?data[53]:(data[21] & 0xff));
                String deviceMac = bytesToHexString(arrayCopyBytes(data, b2==28?54:22, 8));
                if (b2 == 28) {
                    outlet.setFirmwareVersion(bytesToHexString(arrayCopyBytes(data, 46, 3)));
                }
                outlet.setDeviceMac(deviceMac);
            } else if (deviceType == EFDevice.TYPE_WIFI_OUTLET||deviceType == EFDevice.TYPE_WIF_OUTLET_LEXIN
                    || deviceType == EFDevice.TYPE_WIFI_POWER_STRIP) {
                int int_32 = data[32] & 0xff;
                if (int_32 == 255) {
                    //                XlinkUtils.shortTips(R.string.no_permission_control);
                }
                outlet = new EFDeviceOutlet();
                outlet.setDeviceType(deviceType);
                outlet.setDeviceState(data[21] & 0xff);
                String deviceMac = bytesToHexString(arrayCopyBytes(data, 22, 6));
                outlet.setDeviceMac(deviceMac);
                outlet.setLock(data[28] & 0xff);
                StringBuilder builder = new StringBuilder();
                builder.append(data[29] & 0xff);
                builder.append(".");
                builder.append(data[30] & 0xff);
                builder.append(".");
                builder.append(data[31] & 0xff);
                String fwVersion = builder.toString();
                outlet.setFirmwareVersion(fwVersion);
            }
        }

        return outlet;
    }


    /**
     * @param @param b 设定文件
     * @return void 返回类型
     * @throws
     * @Title: chaeckMatchResult
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public static int chaeckMatchResult(byte[] b) {
        String suc = new String(arrayCopyBytes(b, 1, 13));
        String fail = new String(arrayCopyBytes(b, 1, 10));
        String pho = new String(arrayCopyBytes(b, 1, 8));
        if ("match success".equals(suc)) {
            return 2;
        }
        if ("phonenum".equals(pho.trim())) {
            return 1;
        }
        if ("match fail".equals(fail)) {
            return 0;
        }
        return -1;
    }

    public static byte[] int2OneByte(int num) {
        byte[] data = new byte[1];
        data[0] = (byte) (num & 0x000000ff);
        return data;
    }

    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }



    /**
     * updateOutlet
     *
     * @param ot
     */
    public static void updateOutlet(EFDeviceOutlet ot) {
        if (ot == null) {
            return;
        }
        // 查询数据库是否存在
        EFDeviceOutlet outlet = DaoUtil.queryOne(EFDeviceOutlet.class,
                WhereBuilder.b("deviceMac", "=", ot.getDeviceMac()));
        // 如果存在则更新状态与固件版本即可
        if (outlet != null) {
            outlet.setDeviceState(ot.getDeviceState());
            outlet.setFirmwareVersion(ot.getFirmwareVersion());
            outlet.setParentMac(ot.getParentMac());
            DaoUtil.saveOrUpdate(outlet);
        } else {
            // 否则存入数据库
            DaoUtil.saveOrUpdate(ot);
        }
    }

    /**





    /**
     * @param deviceMac
     * @param deviceType
     */
    private static void updateWifiMacs(String deviceMac, int deviceType) {
        if (deviceType == EFDevice.TYPE_WIFI_OUTLET||deviceType == EFDevice.TYPE_WIF_OUTLET_LEXIN) {// wifi插座
            String macs = PreferenceHelper.readString(Config.KEY_WIFI_OUTLET,
                    "");
            if (macs != null && macs.contains(deviceMac)) {
                return;
            }
            StringBuilder builder = null;
            if (!"".equals(macs)) {
                builder = new StringBuilder(macs);
                builder.append(",");
            } else {
                builder = new StringBuilder();
            }
            builder.append(deviceMac);
            PreferenceHelper.write(Config.KEY_WIFI_OUTLET, builder.toString());
        } else if (deviceType == EFDevice.TYPE_WIFI_POWER_STRIP) {// wifi插排
            String macs = PreferenceHelper.readString(Config.KEY_POWER_STRIP,
                    "");
            if (macs != null && macs.contains(deviceMac)) {
                return;
            }
            StringBuilder builder = null;
            if (!"".equals(macs)) {
                builder = new StringBuilder(macs);
                builder.append(",");
            } else {
                builder = new StringBuilder();
            }
            builder.append(deviceMac);
            PreferenceHelper.write(Config.KEY_POWER_STRIP, builder.toString());
        } else if (deviceType == EFDevice.TYPE_WIFI_PAI_OUTLET) {// wifi乐鑫插排
            String macs = PreferenceHelper.readString(Config.KEY_PAI_OUTLET,
                                                      "");
            if (macs != null && macs.contains(deviceMac)) {
                return;
            }
            StringBuilder builder = null;
            if (!"".equals(macs)) {
                builder = new StringBuilder(macs);
                builder.append(",");
            } else {
                builder = new StringBuilder();
            }
            builder.append(deviceMac);
            PreferenceHelper.write(Config.KEY_PAI_OUTLET, builder.toString());
        }else if (deviceType == EFDevice.TYPE_WIFI_LIGHT) {// wifi灯
            String macs = PreferenceHelper
                    .readString(Config.KEY_WIFI_LIGHT, "");
            if (macs != null && macs.contains(deviceMac)) {
                return;
            }
            StringBuilder builder = null;
            if (!"".equals(macs)) {
                builder = new StringBuilder(macs);
                builder.append(",");
            } else {
                builder = new StringBuilder();
            }
            builder.append(deviceMac);
            PreferenceHelper.write(Config.KEY_WIFI_LIGHT, builder.toString());
        }
    }

    /**
     * @param deviceMac
     */
    private static void updateTeleMacs(String deviceMac) {
        String macStr = PreferenceHelper.readString(Config.KEY_TELE_DEVICE, "");
        if (macStr.contains(deviceMac)) {
            return;
        }
        StringBuilder builder = null;
        if (!"".equals(macStr)) {
            builder = new StringBuilder(macStr);
            builder.append(",");
        } else {
            builder = new StringBuilder();
        }
        builder.append(deviceMac);
        PreferenceHelper.write(Config.KEY_TELE_DEVICE, builder.toString());
    }

    /**
     * @param deviceMac
     */
    @SuppressWarnings("unused")
    private static void updateCompositeMacs(String deviceMac) {
        String macStr = PreferenceHelper.readString(
                Config.KEY_COMPOSITE_DEVICE, "");
        if (macStr.contains(deviceMac)) {
            return;
        }
        StringBuilder builder = null;
        if (!"".equals(macStr)) {
            builder = new StringBuilder(macStr);
            builder.append(",");
        } else {
            builder = new StringBuilder();
        }
        builder.append(deviceMac);
        PreferenceHelper.write(Config.KEY_COMPOSITE_DEVICE, builder.toString());
    }

    //比较唯一标识
    public static boolean isSameTime(byte[] bytes1,byte[] bytes2) {
        if (bytes1[0] == bytes2[0] && bytes1[1] == bytes2[1] && bytes1[2] == bytes2[2] && bytes1[3] == bytes2[3]) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param @param  bs1
     * @param @return 设定文件
     * @return boolean 返回类型
     * @throws
     * @Title: doCheckCode
     * @Description: TODO(校验算法)
     */
    public static boolean doCheckCode(byte[] bs1) {
        byte[] code = getCheckCode(bs1);

        int[] bs = new int[bs1.length];

        for (int i = 0; i < bs.length; i++) {
            bs[i] = bs1[i] & 0xff;
        }
        int cksum;
        int tmp = 0;
        long sum = 0;
        for (int i = 0; i < bs.length - 2; i += 2) {
            tmp = (bs[i] << 8) + bs[i + 1];
            sum += (tmp >> 1) ^ 0x8408;
        }

        cksum = (int) ((sum & 0xffff) ^ (sum >> 16));
        bs[bs1.length - 2] = (byte) ((cksum >> 8) & 0xff);
        bs[bs1.length - 1] = (byte) (cksum & 0xff);

        if (((byte) (bs[bs1.length - 1]) == code[1])
                && ((byte) (bs[bs1.length - 2] & 0xff) == (code[0]))) {
            return true;
        }
        return false;
    }



    public static double decodePowerData(byte[] data, String curMac) {
        double result = 0f;
        byte[] temp = null;
        if (data[1] == 00 && data[2] == 24) {
            temp = arrayCopyBytes(data, 7, 17);
        } else {

            temp = arrayCopyBytes(data, 0, 20);
        }
        String phone = new String(temp).trim();
        if (!phone.equals(PreferenceHelper.readString(Config.USER_PHONE))) {// Config.USER_PHONE
            return result;
        }
        if (data[1] == 00 && data[2] == 24) {
            int deviceType = EFDevice.TYPE_WIFI_PAI_OUTLET;
            String deviceMac = bytesToHexString(arrayCopyBytes(data,27, 6));
            if (!deviceMac.equals(curMac)) {
                return result;
            }
            double int_1 = (data[46] << 24) + (data[47] <<16)+ (data[48] <<8)+data[49];
            result = int_1;
        } else {
            int deviceType = data[20] & 0xff;
            if (deviceType != EFDevice.TYPE_WIFI_POWER_STRIP) {
                return result;
            }
            String deviceMac = bytesToHexString(arrayCopyBytes(data, 21, 6));
            if (!deviceMac.equals(curMac)) {
                return result;
            }
            double int_1 = (data[27] << 8) + (data[28] & 0xff);
            double int_2 = (data[29] << 8) + (data[30] & 0xff);
            result = int_1 + int_2 % 10000D / 10000D;
        }
        return result;
    }

    public static int getTypeIcon(int type) {

            switch (type) {
                case ControlDevice.NORMAL_DEVICE:
                case ControlDevice.WIFI_BRIDGE:
                    return R.drawable.bridge_net;//桥接器
                case ControlDevice.WIFI_DEVICE_LIGHT:
                    return R.drawable.ic_light;//wifi灯
                case ControlDevice.WIFI_DEVICE_OUTLET:
                    return R.drawable.single_outlet_icon;//wifi插座
                case ControlDevice.WIFI_POWER_STRIP:
                case ControlDevice.TYPE_WIFI_PAI_OUTLET:
                    return R.drawable.icon_strip_on;//wifi插排
                case ControlDevice.TELECONTROLLER_DEVICE:
                    return R.drawable.tele_add;//红外设备
            }
        return 0;
    }
}
