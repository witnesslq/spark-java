package com.zi.search.strategy.enums;

/**
 * Created by twt on 2016/5/13.
 */
public enum TermTypeEnum {
    TermTypeEnum_1(1,"学期信息","上学期"),
    TermTypeEnum_2(2,"学期信息","下学期");
    private int type;
    private String description;
    private String value;
    private TermTypeEnum(int type, String description, String value){
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

    public static TermTypeEnum valueOf(int type){
        for(TermTypeEnum bvenum : TermTypeEnum.values()){
            if(bvenum.type() == type)
                return bvenum;
        }
        return null;
    }
}
