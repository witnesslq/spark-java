package com.zi.search.utils;

import com.alibaba.fastjson.JSON;
import com.zi.search.entity.Content;
import com.zi.search.entity.EsAndHbaseData;
import com.zi.search.strategy.enums.BookVersionTypeEnum;
import com.zi.search.strategy.enums.DifficultyLevelTypeEnum;
import com.zi.search.strategy.enums.GradeTypeEnum;
import com.zi.search.strategy.enums.TermTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mysql操作
 *
 * @author 刘星
 */
public class MysqlUtils {
    private static final String URL = PropertyUtils.getSystemProperties("mysql.url");
    private static final String USER = PropertyUtils.getSystemProperties("mysql.user");
    private static final String PASSWORD = PropertyUtils.getSystemProperties("mysql.password");
    private static ThreadLocal<Connection> th = new ThreadLocal<Connection>();

    static {
        try {
            Class.forName(PropertyUtils.getSystemProperties("mysql.driver"));
        } catch (ClassNotFoundException e) {
            System.out.println("加载Mysql数据库驱动失败！");
        }
    }

    /**
     * 获取Connection
     *
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("resource")
	public static Connection getConnection() throws SQLException {
        Connection conn = th.get();
        try {
            if (conn == null || conn.isClosed() || !conn.isValid(0)) {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                while(!conn.isValid(0)){
                    conn = DriverManager.getConnection(URL, USER, PASSWORD);
                }
                th.set(conn);
            }
        } catch (SQLException e) {
            System.out.println("获取数据库连接失败！");
            throw e;
        }
        return conn;
    }

    /**
     * 关闭ResultSet
     *
     * @param rs
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * 关闭Statement
     *
     * @param stmt
     */
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * 关闭ResultSet、Statement
     *
     * @param rs
     * @param stmt
     */
    public static void closeStatement(ResultSet rs, Statement stmt) {
        closeResultSet(rs);
        closeStatement(stmt);
    }

    /**
     * 关闭PreparedStatement
     *
     * @param pstmt
     * @throws SQLException
     */
    public static void fastcloseStmt(PreparedStatement pstmt) throws SQLException {
        pstmt.close();
    }

    /**
     * 关闭ResultSet、PreparedStatement
     *
     * @param rs
     * @param pstmt
     * @throws SQLException
     */
    public static void fastcloseStmt(ResultSet rs, PreparedStatement pstmt) throws SQLException {
        rs.close();
        pstmt.close();
    }

    /**
     * 关闭ResultSet、Statement、Connection
     *
     * @param rs
     * @param stmt
     * @param con
     */
    public static void closeConnection(ResultSet rs, Statement stmt, Connection con) {
        closeResultSet(rs);
        closeStatement(stmt);
        closeConnection(con);
    }

    /**
     * 关闭Statement、Connection
     *
     * @param stmt
     * @param con
     */
    public static void closeConnection(Statement stmt, Connection con) {
        closeStatement(stmt);
        closeConnection(con);
    }

    /**
     * 关闭Connection
     *
     * @param con
     */
    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * 获取nysql中的数据
     *
     * @param start 开始位置
     * @param count 行数
     * @return
     */
    public static EsAndHbaseData getData(Integer start, Integer count) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        //es和hbase集合实体
        EsAndHbaseData esAndHbaseData = new EsAndHbaseData();
        //存放索引数据
        Map<String, String> dataMap = null;
        //存放hbase数据
        Map<String, List<String>> hbaseMap = null;
        try {
            dataMap = new HashMap<String, String>();
            hbaseMap = new HashMap<String, List<String>>();
            conn = MysqlUtils.getConnection();
            stmt = conn.createStatement();
            //查询的列
            String queryColnum = PropertyUtils.getSystemProperties("mysql.colnum");
            //查询的表
            String sql = "SELECT a.*,i.book_name FROM " +
                    "(SELECT q.question_id, q.create_time, q.update_time, q.question_tag, q.content, q.question_answer, q.subject, q.type, " +
                    "q.question_head, q.grade_id,q.term_id, q.knowledge_id, q.difficulty, q.options, q.old_id, q.book_id, k.name knowledge " +
                    "FROM question_bank q LEFT JOIN knowledge_tree k ON q.knowledge_id = k.id LIMIT " + start + "," + count + ") a LEFT JOIN book_info i ON a.book_id=i.book_id ";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                //1.生成rowkey
                String rowKey = RowkeyUtils.productRowkey();

                //2.es索引数据集合
                StringBuffer all = new StringBuffer();

                //3.将所有的数据放入到hbase数据集合
                List<String> clonums = new ArrayList<String>();
                //  3.1将字段分割
                String[] queryClonums = queryColnum.split(",");
                for (int i = 0; i < queryClonums.length; i++) {
                    try {
                        //	3.2获取每个字段对应的值
                        clonums.add(rs.getString(queryClonums[i]));
                    } catch (Exception e1) {
                        //e1.printStackTrace();
                        clonums.add("0000-00-00 00:00:00");
                    }
                }
                String book_name = rs.getString("book_name");
                if (!StringUtils.isBlank(book_name)) {
                    all.append(book_name + ",");
                }
                String book_version = rs.getString("subject");
                try {
                    if (!StringUtils.isBlank(book_version)) {
                        //System.out.println(rs.getString("question_id") + "  =====>  " + BookVersionTypeEnum.valueOf(Integer.parseInt(book_version.trim())).value());
                        all.append(BookVersionTypeEnum.valueOf(Integer.parseInt(book_version.trim())).value() + ",");
                    }
                } catch (Exception ii) {
                    //ii.printStackTrace();
                }
                String knowledge = rs.getString("knowledge");
                if (!StringUtils.isBlank(knowledge)) {
                    all.append(knowledge + ",");
                }
                try {
                    String difficultyLevel = rs.getString("difficulty");
                    if (!StringUtils.isBlank(difficultyLevel)) {
                        all.append(DifficultyLevelTypeEnum.valueOf(Integer.parseInt(difficultyLevel.trim())).value() + ",");
                    }
                } catch (Exception ii) {
                    //ii.printStackTrace();
                }
                String grade = rs.getString("grade_id");
                try {
                    if (!StringUtils.isBlank(grade)) {
                        all.append(GradeTypeEnum.valueOf(Integer.parseInt(grade.trim())).value() + ",");
                    }
                } catch (Exception ii) {
                    //ii.printStackTrace();
                }
                String term = rs.getString("term_id");
                try {
                    if (!StringUtils.isBlank(term)) {
                        all.append(TermTypeEnum.valueOf(Integer.parseInt(term.trim())).value() + ",");
                    }
                } catch (Exception ii) {
                    //ii.printStackTrace();
                }

                //4.索引字段组拼
                String question_tag = rs.getString("question_tag");
                question_tag = FilterUtils.filter(question_tag);
                if (!StringUtils.isBlank(question_tag)) {
                    all.append(question_tag + ",");
                }

                String content = rs.getString("content");

                try {
                    Content c = JSON.parseObject(content, Content.class);
                    if (!StringUtils.isBlank(c.getQuestion_body())) {
                        all.append(c.getQuestion_body() + ",");
                    }
                    if (!StringUtils.isBlank(c.getQuestion_body_html())) {
                        all.append(c.getQuestion_body_html() + ",");
                    }
                    if (!StringUtils.isBlank(c.getAnswer_analysis())) {
                        all.append(c.getAnswer_analysis() + ",");
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    all.append(content + ",");
                }
                String question_answer = rs.getString("question_answer");
                if (!StringUtils.isBlank(question_answer)) {
                    all.append(question_answer + ",");
                }

                String type = rs.getString("type");
                if ("1".equals(type)) {
                    type = "选择题";
                } else if ("2".equals(type)) {
                    type = "解答题";
                }
                if (!StringUtils.isBlank(type)) {
                    all.append(type + ",");
                }

                String question_head = rs.getString("question_head");
                if (!StringUtils.isBlank(question_head)) {
                    all.append(question_head);
                }

                all = new StringBuffer(FilterUtils.filter(all.toString()));
                XContentBuilder jsonBuilder = XContentFactory.jsonBuilder();
                jsonBuilder.startObject().field("content", all).endObject();
                jsonBuilder.close();

                //5.每次一条记录
                dataMap.put(rowKey, jsonBuilder.string());
                hbaseMap.put(rowKey, clonums);
            }
            esAndHbaseData.setDataMap(dataMap);
            esAndHbaseData.setHbaseMap(hbaseMap);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return esAndHbaseData;
    }

    /**
     * 获取总的记录数
     *
     * @return
     */
    public static Integer getCount() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = MysqlUtils.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT count(*) c FROM question_bank";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt("c");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
}
