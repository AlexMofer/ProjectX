package com.am.print;

import android.os.Parcel;
import android.os.Parcelable;

import com.am.utils.BigDecimalUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * 打印内容
 * Created by Alex on 2016/4/19.
 */
public class PrinterData implements Parcelable {
    public static final int PAY_TYPE_ONLINE = 1;
    public static final int PAY_TYPE_OFFLINE = 2;
    private String storeName;// 门店名称
    private String storeInfo;// 门店简介
    private String storePhone;// 客服电话
    private long orderNo;// 订单号
    private long deliveryTime;// 预计送达时间
    private int serialNumber;// 订单流水号
    private String address;// 配送地址
    private String contacts;//联系人姓名
    private String phone;//联系人电话
    private String remark;//订单备注（留言信息 ，可能为空）
    private int totalFee;//总费用
    private int payType;//支付类型：1-线上支付；2-现金支付
    private ArrayList<PrinterDishData> dishes;// 菜品
    private int deliveryFee;// 配送费用
    private String deliveryFeeStr;// 配送费用文字

    public String getStoreName() {
        return storeName == null ? "" : storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreInfo() {
        return storeInfo == null ? "" : storeInfo;
    }

    public void setStoreInfo(String storeInfo) {
        this.storeInfo = storeInfo;
    }

    public String getStorePhone() {
        return storePhone == null ? "" : storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public String getOrderNoStr() {
        return String.format(Locale.getDefault(), "%d", orderNo);
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }

    public String getDeliveryTime() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                .format(new Date(deliveryTime));
    }

    public void setDeliveryTime(long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getSerialNumber() {
        return String.format(Locale.getDefault(), "#%d", serialNumber);
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getAddress() {
        return address == null ? "" : address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContacts() {
        return contacts == null ? "" : contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getPhone() {
        return phone == null ? "" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemark() {
        return remark == null ? "无" : remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTotalFee() {
        return String.format(Locale.getDefault(),"￥%1$.2f",
                BigDecimalUtils.mul(totalFee, 0.01d));
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public ArrayList<PrinterDishData> getDishes() {
        return dishes == null ? new ArrayList<PrinterDishData>() : dishes;
    }

    public void setDishes(ArrayList<PrinterDishData> dishes) {
        this.dishes = dishes;
    }

    public void addDishes(PrinterDishData dish) {
        if (dish == null)
            return;
        if (dishes == null)
            dishes = new ArrayList<>();
        dishes.add(dish);
    }

    public String getDeliveryFee() {
        switch (deliveryFee) {
            case 0:
                return deliveryFeeStr == null ? "" : deliveryFeeStr;
            default:
                return String.format(Locale.getDefault(),"￥%1$.2f",
                        BigDecimalUtils.mul(deliveryFee, 0.01d));
        }
    }

    public void setDeliveryFee(int deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public void setDeliveryFeeStr(String deliveryFeeStr) {
        this.deliveryFeeStr = deliveryFeeStr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.storeName);
        dest.writeString(this.storeInfo);
        dest.writeString(this.storePhone);
        dest.writeLong(this.orderNo);
        dest.writeLong(this.deliveryTime);
        dest.writeInt(this.serialNumber);
        dest.writeString(this.address);
        dest.writeString(this.contacts);
        dest.writeString(this.phone);
        dest.writeString(this.remark);
        dest.writeInt(this.totalFee);
        dest.writeInt(this.payType);
        dest.writeTypedList(dishes);
        dest.writeInt(this.deliveryFee);
        dest.writeString(this.deliveryFeeStr);
    }

    public PrinterData() {
    }

    protected PrinterData(Parcel in) {
        this.storeName = in.readString();
        this.storeInfo = in.readString();
        this.storePhone = in.readString();
        this.orderNo = in.readLong();
        this.deliveryTime = in.readLong();
        this.serialNumber = in.readInt();
        this.address = in.readString();
        this.contacts = in.readString();
        this.phone = in.readString();
        this.remark = in.readString();
        this.totalFee = in.readInt();
        this.payType = in.readInt();
        this.dishes = in.createTypedArrayList(PrinterDishData.CREATOR);
        this.deliveryFee = in.readInt();
        this.deliveryFeeStr = in.readString();
    }

    public static final Creator<PrinterData> CREATOR = new Creator<PrinterData>() {
        @Override
        public PrinterData createFromParcel(Parcel source) {
            return new PrinterData(source);
        }

        @Override
        public PrinterData[] newArray(int size) {
            return new PrinterData[size];
        }
    };

    public static class PrinterDishData implements Parcelable {
        private String dishName;// 菜品名称
        private String dishSpec;// 菜品规格
        private int dishFee;// 菜品价格
        private int dishCount;// 菜品数量
        private String dishRemark;// 菜品备注
        private int serialNumber;// 订单流水号
        private long deliveryTime;// 预计送达时间

        public void setDishName(String dishName) {
            this.dishName = dishName;
        }

        public void setDishSpec(String dishSpec) {
            this.dishSpec = dishSpec;
        }

        public void setDishFee(int dishFee) {
            this.dishFee = dishFee;
        }

        public void setDishCount(int dishCount) {
            this.dishCount = dishCount;
        }

        public void setDishRemark(String dishRemark) {
            this.dishRemark = dishRemark;
        }

        public String getDishName() {
            return dishName == null ? "" : dishName;
        }

        public String getDishFee() {

            return String.format(Locale.getDefault(),"￥%1$.2f",
                    BigDecimalUtils.mul(dishFee, 0.01d));
        }

        public String getDishCount() {
            return String.format(Locale.getDefault(), "×%d", dishCount);
        }

        public String getDishRemark() {
            return dishRemark == null ? "" : dishRemark;
        }

        public boolean hasDishSpec() {
            return dishSpec != null;
        }

        public String getDishSpec() {
            return dishSpec == null ? "" : dishSpec;
        }

        public String getSerialNumber() {
            return String.format(Locale.getDefault(), "#%d", serialNumber);
        }

        public void setSerialNumber(int serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getDeliveryTime() {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                    .format(new Date(deliveryTime));
        }

        public void setDeliveryTime(long deliveryTime) {
            this.deliveryTime = deliveryTime;
        }

        public static PrinterDishData getTest() {
            PrinterDishData dish = new PrinterDishData();
            dish.setDishName("米星美食");
            dish.setDishSpec("豪华版");
            dish.setDishFee(8888);
            dish.setDishCount(1);
            dish.setDishRemark("星级美食体验，顶级美食送货上门，随时随地享受美食！");
            dish.setSerialNumber(8);
            dish.setDeliveryTime(System.currentTimeMillis());
            return dish;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.dishName);
            dest.writeString(this.dishSpec);
            dest.writeInt(this.dishFee);
            dest.writeInt(this.dishCount);
            dest.writeString(this.dishRemark);
            dest.writeInt(this.serialNumber);
            dest.writeLong(this.deliveryTime);
        }

        public PrinterDishData() {
        }

        protected PrinterDishData(Parcel in) {
            this.dishName = in.readString();
            this.dishSpec = in.readString();
            this.dishFee = in.readInt();
            this.dishCount = in.readInt();
            this.dishRemark = in.readString();
            this.serialNumber = in.readInt();
            this.deliveryTime = in.readLong();
        }

        public static final Creator<PrinterDishData> CREATOR = new Creator<PrinterDishData>() {
            @Override
            public PrinterDishData createFromParcel(Parcel source) {
                return new PrinterDishData(source);
            }

            @Override
            public PrinterDishData[] newArray(int size) {
                return new PrinterDishData[size];
            }
        };
    }

    public static PrinterData getTest() {
        PrinterData data = new PrinterData();
        data.setStoreName("米星餐厅");
        data.setStoreInfo("时尚粤菜大城小菜明星饭堂");
        data.setStorePhone("400-8888888");
        data.setOrderNo(8888888888888888L);
        data.setDeliveryTime(System.currentTimeMillis());
        data.setSerialNumber(8);
        data.setPayType(PAY_TYPE_ONLINE);
        data.setAddress("天河区海乐路12号合景睿峰·L7 2201");
        data.setPhone("18888888888");
        data.setContacts("李生");
        data.setRemark("星级美食体验，顶级美食送货上门，随时随地享受美食！");

        PrinterDishData dish1 = new PrinterDishData();
        dish1.setDishName("米星美食");
        dish1.setDishSpec("豪华版");
        dish1.setDishFee(8888);
        dish1.setDishCount(1);
        data.addDishes(dish1);

        PrinterDishData dish2 = new PrinterDishData();
        dish2.setDishName("米星美食");
        dish2.setDishSpec("限量版");
        dish2.setDishFee(88888);
        dish2.setDishCount(1);
        data.addDishes(dish2);

        PrinterDishData dish3 = new PrinterDishData();
        dish3.setDishName("餐具");
        dish3.setDishFee(0);
        dish3.setDishCount(2);
        data.addDishes(dish3);

        data.setTotalFee(97776);
        data.setDeliveryFee(0);
        data.setDeliveryFeeStr("免费");
        return data;
    }
}
