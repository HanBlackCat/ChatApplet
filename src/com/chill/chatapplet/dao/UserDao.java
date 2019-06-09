package com.chill.chatapplet.dao;

import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.chill.chatapplet.entity.Chatmsg;
import com.chill.chatapplet.entity.User;
import com.chill.chatapplet.action.*;

public class UserDao {
	private static String driver = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost:3306/chatapplet";
	private static String name = "root";
	private static String pwd = "root";
	private static Connection connection = null;

	private static void connectToDB() {
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, name, pwd);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	private static void closeDB() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/** 登录 */
	public User doLogin(User bean) {
		connectToDB();// 连接上数据库
		PreparedStatement statement = null;
		ResultSet rs = null;
		User result = new User();
		try {
			statement = connection.prepareStatement("select id,salt,password from user where name=?;");
			statement.setString(1, bean.getName());
			rs = statement.executeQuery();
			if (rs.next()) {
				result.setUserId(rs.getInt("id"));
				result.setSalt(rs.getString("salt"));
				result.setPassword(rs.getString("password"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (statement != null) {
					statement.close();
				}
				closeDB();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/** 注册 返回用户id */
	public int doRegister(User bean) {
		connectToDB();
		PreparedStatement preparedStatement = null;
		PreparedStatement preparedStatement1 = null;
		ResultSet rs = null;
		int result = 0;
		try {
			preparedStatement = connection
					.prepareStatement("insert into user(name,password,salt,time) values(?,?,?,?);");
			preparedStatement.setString(1, bean.getName());
			preparedStatement.setString(2, bean.getPassword());
			preparedStatement.setString(3, bean.getSalt());
			preparedStatement.setTimestamp(4, bean.getTime());
			preparedStatement.execute();

			preparedStatement1 = connection
					.prepareStatement("select id from user where name=? and password=? and salt=?;");
			preparedStatement1.setString(1, bean.getName());
			preparedStatement1.setString(2, bean.getPassword());
			preparedStatement1.setString(3, bean.getSalt());
			rs = preparedStatement1.executeQuery();
			if (rs.next()) {
				result = rs.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (preparedStatement1 != null) {
					preparedStatement1.close();
				}
				closeDB();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	/**返回用户id**/
	public int getId(String name){
		connectToDB();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			preparedStatement = connection
					.prepareStatement("select id from user where name=? ;");
			preparedStatement.setString(1, name);
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	/** 添加好友 */
	public boolean addFriend(User bean, int fid) {
		connectToDB();
		PreparedStatement preparedStatement = null;
		boolean result = false;
		try {
		
			preparedStatement = connection.prepareStatement("insert into friend(id1,id2) values(?,?);");
			preparedStatement.setInt(1, bean.getUserId());
			preparedStatement.setInt(2, fid);
			preparedStatement.execute();
			preparedStatement.close();
			// 双向添加好友
			preparedStatement = connection.prepareStatement("insert into friend(id1,id2) values(?,?);");
			preparedStatement.setInt(2, bean.getUserId());
			preparedStatement.setInt(1, fid);
			preparedStatement.execute();
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				closeDB();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/** 查询好友 */
	public HashMap<Integer, String> findFriend(User bean) {
		connectToDB();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		HashMap<Integer, String> list = new HashMap<Integer, String>();
		try {
			preparedStatement = connection
					.prepareStatement("select id,name from user where id in(select id2 from friend where id1=?)");
			preparedStatement.setInt(1, bean.getUserId());
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				list.put(rs.getInt("id"), rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				closeDB();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/** 查询某好友聊天记录 */
	public Chatmsg[] chatHistory(User bean, int fid) {
		connectToDB();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		Chatmsg[] list = new Chatmsg[10];
		int i = 0;
		try {
			preparedStatement = connection.prepareStatement(
					"SELECT * FROM (select  * from privatemsg where (sendid=? and recid=?) or (sendid=? and recid=?) order by id  desc limit 10) a order by id");
			preparedStatement.setInt(1, bean.getUserId());
			preparedStatement.setInt(2, fid);
			preparedStatement.setInt(4, bean.getUserId());
			preparedStatement.setInt(3, fid);
			rs = preparedStatement.executeQuery();
			while (rs.next()) { 
				list[i] =new Chatmsg();
				// sendid=userId,这条消息是发出消息
				if (rs.getInt("sendid") == bean.getUserId()) {
					list[i].setType(false);
				} else {
					list[i].setType(true);
				}
				list[i].setId(rs.getInt("sendid"));
				list[i].setChatmsg(rs.getString("msg"));
				list[i].setFileindex(rs.getString("fileindex"));
				list[i].setFilename(rs.getString("filename"));
				list[i].setTime(rs.getTimestamp("time"));
				
			//	System.out.println(rs.getString("msg"));
			//	System.out.println(list[i].getChatmsg());
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				closeDB();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/** 删除好友 */
	public boolean deleteFriend(User bean, int fid) {
		connectToDB();
		PreparedStatement preparedStatement = null;
		boolean result = false;
		try {
			// 双向删除
			preparedStatement = connection
					.prepareStatement("delete from friend where (id1=? and id2=?) or(id1=? and id2=?);");
			preparedStatement.setInt(1, bean.getUserId());
			preparedStatement.setInt(4, bean.getUserId());
			preparedStatement.setInt(2, fid);
			preparedStatement.setInt(3, fid);
			preparedStatement.execute();
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
		} finally {
			closeDB();
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/** 在公共频道聊天 */
	public boolean chat(Chatmsg msg) {
		connectToDB();
		PreparedStatement preparedStatement = null;
		Boolean result = null;
		try {
			preparedStatement = connection.prepareStatement("insert into publicmsg(sendid,msg,time) values(?,?,?);");
			preparedStatement.setInt(1, msg.getId());
			preparedStatement.setString(2, msg.getChatmsg());
			preparedStatement.setTimestamp(3, msg.getTime());
			preparedStatement.execute();
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
		} finally {
			closeDB();
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/** 私聊 */
	public boolean chat(Chatmsg msg, int fid) {
		connectToDB();
		PreparedStatement preparedStatement = null;
		Boolean result = null;
		try {
			preparedStatement = connection.prepareStatement(
					"insert into privatemsg(sendid,recid,msg,fileindex,filename,time) values(?,?,?,?,?,?);");
			preparedStatement.setInt(1, msg.getId());
			preparedStatement.setInt(2, fid);
			preparedStatement.setString(3, msg.getChatmsg());
			preparedStatement.setString(4, msg.getFileindex());
			preparedStatement.setString(5, msg.getFilename());
			preparedStatement.setTimestamp(6, msg.getTime());
			preparedStatement.execute();
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
		} finally {
			closeDB();
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
}
