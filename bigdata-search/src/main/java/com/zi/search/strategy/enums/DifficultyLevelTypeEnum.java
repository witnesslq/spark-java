package com.zi.search.strategy.enums;

/**
 * Created by twt on 2016/5/13.
 */
public enum DifficultyLevelTypeEnum {
    DifficultyLevelTypeEnum_0(0,"难度等级","容易"),
    DifficultyLevelTypeEnum_1(1,"难度等级","容易"),
    DifficultyLevelTypeEnum_2(2,"难度等级","一般"),
    DifficultyLevelTypeEnum_3(3,"难度等级","中等"),
    DifficultyLevelTypeEnum_4(4,"难度等级","较难"),
    DifficultyLevelTypeEnum_5(1,"学期信息","很难");
    private int type;
    private String description;
    private String value;
    private DifficultyLevelTypeEnum(int type, String description, String value){
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

    public static DifficultyLevelTypeEnum valueOf(int type){
        for(DifficultyLevelTypeEnum bvenum : DifficultyLevelTypeEnum.values()){
            if(bvenum.type() == type)
                return bvenum;
        }
        return null;
    }
}
