package com.zi.search;

import com.zi.search.utils.MysqlUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by twt on 2016/5/13.
 */
public class MysqlTest {
    @Test
    public void testIntegerIsNull() throws SQLException {

        Connection conn  = MysqlUtils.getConnection();
        String sql = "select * from question_bank where question_id limit 0,500";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next())
//            System.out.println(BookVersionTypeEnum.valueOf(rs.getString("subject").trim()).value());
            System.out.println(rs.getString("subject").trim());
    }
}
