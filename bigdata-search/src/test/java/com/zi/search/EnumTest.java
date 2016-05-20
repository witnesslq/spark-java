package com.zi.search;

import com.zi.search.strategy.enums.BookVersionTypeEnum;
import org.junit.Test;

/**
 * Created by twt on 2016/5/13.
 */
public class EnumTest {
    @Test
    public void testBookVersion(){
        System.out.println(BookVersionTypeEnum.valueOf(30).value());
    }
}
