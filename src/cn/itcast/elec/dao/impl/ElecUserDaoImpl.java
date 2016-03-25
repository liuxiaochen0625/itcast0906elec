package cn.itcast.elec.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import cn.itcast.elec.dao.IElecUserDao;
import cn.itcast.elec.domain.ElecUser;


@Repository(IElecUserDao.SERVICE_NAME)
public class ElecUserDaoImpl extends CommonDaoImpl<ElecUser> implements IElecUserDao {

	/**  
	* @Name: findRoleByLogonName
	* @Description: 使用登录名作为条件，获取登录登录名所具有的角色的集合
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-14 （创建日期）
	* @Parameters: String 登录名
	* @Return: List<Object[]> 角色集合
	*/
	public List<Object[]> findRoleByLogonName(final String name) {
		final String sql = "SELECT b.ddlCode,b.ddlName FROM elec_user_role a " +
					 " LEFT OUTER JOIN elec_systemddl b ON a.roleID = b.ddlCode AND b.keyword = '角色类型' " +
					 " JOIN elec_user c ON a.userID = c.userID " +
					 " AND c.logonName = ? " +
					 " WHERE c.isDuty = '1'";
		List<Object[]> list = (List<Object[]>) this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createSQLQuery(sql);
				query.setParameter(0, name);
				return query.list();
			}
			
		});
		return list;
	}

	/**  
	* @Name: findPopedomByLogonName
	* @Description: 使用登录名作为条件，获取登录名所具有的权限的集合
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-14 （创建日期）
	* @Parameters: String 登录名
	* @Return: List<Object>：存放系统权限集合
	*/
	public List<Object> findPopedomByLogonName(final String name) {
		final String sql = "SELECT a.popedomcode FROM elec_role_popedom a " +
					 " LEFT OUTER JOIN elec_user_role b ON a.roleID = b.roleID " +
					 " JOIN elec_user c ON b.userID = c.userID " +
					 " AND c.logonName = ?" +
					 " WHERE c.isDuty = '1'";
		List<Object> list = (List<Object>) this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createSQLQuery(sql);
				query.setParameter(0, name);
				return query.list();
			}
			
		});
		return list;
	}

	/**  
	* @Name: findChartDataSet
	* @Description: 获取人员统计的数据集合（按照所属单位进行统计）
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-22 （创建日期）
	* @Parameters: 无
	* @Return: List<Object>：数据集合
	*/
	public List<Object[]> findChartDataSet() {
		final String sql = "SELECT b.keyword,b.ddlName,COUNT(a.jctID) FROM elec_user a " +
					 " LEFT OUTER JOIN elec_systemddl b ON a.jctID = b.ddlCode AND b.keyword = '所属单位'" +
					 " WHERE a.isduty = '1'" +
					 " GROUP BY a.jctID";
		List<Object[]> list = (List<Object[]>) this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createSQLQuery(sql);
				return query.list();
			}
			
		});
		return list;
	}
	
}
