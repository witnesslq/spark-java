package com.zi.search.strategy.enums;

/**
 * Created by twt on 2016/5/13.
 */
public enum GradeTypeEnum {
    GradeTypeEnum_1(1,"年级信息","一年级"),
    GradeTypeEnum_2(2,"年级信息","二年级"),
    GradeTypeEnum_3(3,"年级信息","三年级"),
    GradeTypeEnum_4(4,"年级信息","四年级"),
    GradeTypeEnum_5(5,"年级信息","五年级"),
    GradeTypeEnum_6(6,"年级信息","六年级"),
    GradeTypeEnum_7(7,"年级信息","初一"),
    GradeTypeEnum_8(8,"年级信息","初二"),
    GradeTypeEnum_9(9,"年级信息","初三"),
    GradeTypeEnum_10(10,"年级信息","高一"),
    GradeTypeEnum_11(11,"年级信息","高二"),
    GradeTypeEnum_12(12,"年级信息","高三");
    private int type;
    private String description;
    private String value;
    private GradeTypeEnum(int type, String description, String value){
        this.type = type;
        this.description = description;
        this.value = value;
    }

    public int type() {
        return type;
    }

    public String description() {
        return description;
    }

    public String value() {
        return value;
    }

    public static GradeTypeEnum valueOf(int type){
        for(GradeTypeEnum bvenum : GradeTypeEnum.values()){
            if(bvenum.type() == type)
                return bvenum;
        }
        return null;
    }
}
