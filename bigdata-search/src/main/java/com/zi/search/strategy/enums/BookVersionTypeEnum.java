package com.zi.search.strategy.enums;

/**
 * Created by twt on 2016/5/13.
 */
public enum BookVersionTypeEnum {
    BookVersionTypeEnum_1(1, "教材信息", "苏科版,华师大版,沪科版,沪教版,北京课改版,陕教版,人教版,人教新版,冀教版,北师大版,苏教版,浙教版,湘教版,青岛版"),
    BookVersionTypeEnum_2(2, "教材信息", "粤教版,教科版,华师大版,鄂教版,语文版,鲁教版,沪教版,河大版,北京课改版,鲁人版,语文s版,长春版,语文A版,人教版,冀教版,北师大版,苏教版,湘教版,吉林大学出版社"),
    BookVersionTypeEnum_3(3, "教材信息", "外研英语,冀教版一年级起,粤教版,教科版,冀教版三年级起,牛津译林版,牛津版,仁爱版,沪教版,北京课改版,译林版,沪教牛津版,外研版三年级起,北师大版一年级起,外研版一年级起,人教版,人教新目标,冀教版,北师大版,牛津广州版,苏教版,闽教版三年级起," +
            "北京版二年级起,牛津沈阳版,湘少版三年级起,湘教版,深圳版,广州版,新世纪版,重大版,牛津上海版,剑桥青少版,American English In Mind,外研版（天津专用）"),
    BookVersionTypeEnum_4(4, "教材信息", "粤教版,人民版,教科版,沪教版,鲁人版,陕教版,人教版,北师大版,苏教版,湘教版"),
    BookVersionTypeEnum_5(5, "教材信息", "人民版,华师大版,川教版,中图版,北京课改版,中华书局版,冀人版,人教版,冀教版,北师大版,岳麓版"),
    BookVersionTypeEnum_6(6, "教材信息", "商务星球版,粤教版,仁爱版,鲁教版,中图版,北京课改版,晋教版,人教版,浙教版,湘教版,中图版（中华地图）,北斗区域地理"),
    BookVersionTypeEnum_7(7, "教材信息", "粤教版,苏科版,教科版,沪科版,粤教沪科版,北京课改版,人教版,北师大版,鲁科版,元本物理"),
    BookVersionTypeEnum_8(8, "教材信息", "沪科版,仁爱版,鲁教版,沪教版,北京课改版,科粤版,人教版,苏教版,鲁科版"),
    BookVersionTypeEnum_9(9, "教材信息", "苏科版,沪科版,中图版,浙科版,济南版,北京课改版,冀少版,人教版,北师大版,苏教版");
    private int type;
    private String description;
    private String value;

    private BookVersionTypeEnum(int type, String description, String value) {
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

    public static BookVersionTypeEnum valueOf(int type) {
        for (BookVersionTypeEnum bvenum : BookVersionTypeEnum.values()) {
            if (bvenum.type() == type)
                return bvenum;
        }
        return null;
    }
}
