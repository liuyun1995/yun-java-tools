package com.liuyun.github.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * @Author: liuyun18
 * @Date: 2018/8/30 上午11:43
 */
public class JdbcUtils {

    /**
     * 获取数据库连接
     * @param driver
     * @param url
     * @param username
     * @param password
     * @return
     */
    public static Connection getConnection(String driver, String url, String username, String password) {
        if (driver == null || url == null || username == null || password == null) {
            throw new IllegalArgumentException("The parameters must not be null");
        }
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行方法
     * @param conn
     * @param sql
     * @param params
     */
    public static void execute(Connection conn, String sql, Object... params) {
        if(conn == null || sql == null) {
            throw new IllegalArgumentException("Connection and SQL must not be null");
        }
        PreparedStatement pst = null;
        try {
            conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    pst.setObject(i + 1, params[i]);
                }
            }
            pst.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(pst, conn);
        }
    }

    /**
     * 查询方法
     * @param conn
     * @param sql
     * @param params
     * @return
     */
    public static List<Map<String, Object>> query(Connection conn, String sql, Object... params) {
        if(conn == null || sql == null) {
            throw new IllegalArgumentException("Connection and SQL must not be null");
        }
        List<Map<String, Object>> retList = Lists.newArrayList();
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    pst.setObject(i + 1, params[i]);
                }
            }
            rs = pst.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> map = Maps.newHashMap();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String fieldName = rsmd.getColumnName(i);
                    Object fieldValue = rs.getObject(fieldName);
                    map.put(fieldName.toUpperCase(), fieldValue);
                }
                retList.add(map);
            }
            return retList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(rs, pst, conn);
        }
    }

    /**
     * 关闭方法
     * @param csList
     */
    public static void close(AutoCloseable... csList) {
        try{
            for (AutoCloseable c : csList) {
                if(c != null) {c.close();}
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
