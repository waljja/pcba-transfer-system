package com.ht.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class Con51DB {
	private static String strDBDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static String strDBUrl = "jdbc:sqlserver://172.31.2.51;DatabaseName=OrBitX";
	public Connection con = null;
	private ResultSet rs = null;
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	private Statement stmt = null;
	private String user = "sa";
	private String password = "s=a2011!";

	public Con51DB() {
		init();
	}

	public void init() {
		try {
			Class.forName(strDBDriver).newInstance();
			this.con = DriverManager.getConnection(strDBUrl, this.user,
					this.password);
			this.stmt = this.con.createStatement();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String sql) {
		try {
			this.stmt = this.con.createStatement();
			this.rs = this.stmt.executeQuery(sql);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return this.rs;
	}
	
	public List executeQuery1(String sql) {
		try {
			this.stmt = this.con.createStatement();
			this.rs = this.stmt.executeQuery(sql);
			while(this.rs.next()) {
				ResultSetMetaData md = this.rs.getMetaData();//获得结果集结构信息,元数据
				int columnCount = md.getColumnCount();//获得列数
				Map<String,Object> rowData = new HashMap<String,Object>();
				for (int i = 1; i <= columnCount; i++) {
					rowData.put(md.getColumnName(i), this.rs.getObject(i));
				}
				list.add(rowData);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public void executeUpdate(String sql) throws SQLException {
		this.stmt = this.con.createStatement();
		this.stmt.executeUpdate(sql);
	}

	public void close() {
		try {
			if (this.rs != null) {
				this.rs.close();
				this.rs = null;
			}
		} catch (Exception e) {
			System.out.println("dbBean close rs error!");
			try {
				if (this.stmt != null) {
					this.stmt.close();
					this.stmt = null;
				}
			} catch (Exception ex) {
				System.out.println("dbBean close stmt error!");
				try {
					if (this.con == null)
						return;
					this.con.close();
					this.con = null;
				} catch (Exception ee) {
					System.out.println("dbBean close con error!");
				}
			} finally {
				try {
					if (this.con != null) {
						this.con.close();
						this.con = null;
					}
				} catch (Exception ex) {
					System.out.println("dbBean close con error!");
				}
			}
			try {
				if (this.con != null) {
					this.con.close();
					this.con = null;
				}
			} catch (Exception ex) {
				System.out.println("dbBean close con error!");
			}
		} finally {
			try {
				if (this.stmt != null) {
					this.stmt.close();
					this.stmt = null;
				}
			} catch (Exception e) {
				System.out.println("dbBean close stmt error!");
				try {
					if (this.con != null) {
						this.con.close();
						this.con = null;
					}
				} catch (Exception ex) {
					System.out.println("dbBean close con error!");
				}
			} finally {
				try {
					if (this.con != null) {
						this.con.close();
						this.con = null;
					}
				} catch (Exception e) {
					System.out.println("dbBean close con error!");
				}
			}
		}
	}

	public static void main(String[] args) {
		Con51DB c = new Con51DB();
		System.out.println(c.con);
	}

}
