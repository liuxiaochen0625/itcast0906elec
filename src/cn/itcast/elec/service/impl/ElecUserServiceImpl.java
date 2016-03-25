package cn.itcast.elec.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.elec.dao.IElecSystemDDLDao;
import cn.itcast.elec.dao.IElecUserDao;
import cn.itcast.elec.domain.ElecUser;
import cn.itcast.elec.service.IElecUserService;
import cn.itcast.elec.util.MD5keyBean;
import cn.itcast.elec.util.PageInfo;


@Service(IElecUserService.SERVICE_NAME)
@Transactional(readOnly=true)
public class ElecUserServiceImpl implements IElecUserService {

	@Resource(name=IElecUserDao.SERVICE_NAME)
	private IElecUserDao elecUserDao;
	
	@Resource(name=IElecSystemDDLDao.SERVICE_NAME)
	private IElecSystemDDLDao elecSystemDDLDao;

	/**  
	* @Name: findElecUserList
	* @Description: 按照用户姓名组织条件查询用户列表
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-11 （创建日期）
	* @Parameters: ElecUser 存放用户姓名
	* @Return: List<ElecUser> 用户列表数据
	*/
	public List<ElecUser> findElecUserList(ElecUser elecUser) {
		String userName = elecUser.getUserName();
		//组织查询条件
		String condition = "";
		List<String> paramsList = new ArrayList<String>();
		if(StringUtils.isNotBlank(userName)){
			condition += " and o.userName like ?";
			paramsList.add("%"+userName+"%");
		}
		Object [] params = paramsList.toArray();
		LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
		orderby.put("o.onDutyDate", "asc");
		/**添加分页逻辑2012-9-21 begin*/
		HttpServletRequest request = ServletActionContext.getRequest();
		PageInfo pageInfo = new PageInfo(request);//处理分页
		//List<ElecUser> list = elecUserDao.findCollectionByConditionNoPage(condition, params, orderby);
		List<ElecUser> list = elecUserDao.findCollectionByConditionWithPage(condition, params, orderby,pageInfo);
		request.setAttribute("page",pageInfo.getPageBean());
		/**end*/
		//将数据字典项进行转换，才能显示到页面上
		this.userListConverntSystemDDL(list);
		return list;
	}

	/**值转换，将数据字典项转换成页面显示的值*/
	private void userListConverntSystemDDL(List<ElecUser> list) {
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();i++){
				ElecUser elecUser = list.get(i);
				//性别
				elecUser.setSexID(elecUser.getSexID()!=null && !elecUser.getSexID().equals("")?elecSystemDDLDao.findDdlNameByCondition("性别",elecUser.getSexID()):"");
				//是否在职
				elecUser.setIsDuty(elecUser.getIsDuty()!=null && !elecUser.getIsDuty().equals("")?elecSystemDDLDao.findDdlNameByCondition("是否在职",elecUser.getIsDuty()):"");
			}
		}
	}

	/**  
	* @Name: saveElecUser
	* @Description: 保存用户信息
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-11 （创建日期）
	* @Parameters: ElecUser 存放页面传递的用户信息
	* @Return: List<ElecUser> 用户列表数据
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void saveElecUser(ElecUser elecUser) {
		String userID = elecUser.getUserID();
		this.elecUserLogonPwdByMd5(elecUser);
		//如果userID不为null，执行更新操作
		if(userID!=null && !"".equals(userID)){
			elecUserDao.update(elecUser);
		}
		//否则执行保存操作
		else{
			elecUserDao.save(elecUser);
		}
		
	}
	/**将ElecUser对象中logonPwd的字段使用md5进行加密*/
	private void elecUserLogonPwdByMd5(ElecUser elecUser) {
		String logonPwd = elecUser.getLogonPwd();
		String md5LogonPwd = "";
		//添加密码的默认值是000000
		if(logonPwd==null || "".equals(logonPwd)){
			logonPwd = "000000";
		}
		//获取判断是否修改密码的标识
		String md5flag = elecUser.getMd5flag();
		//此时表示没有修改密码，此时不需要进行密码加密
		if(md5flag!=null && md5flag.equals("1")){
			md5LogonPwd = logonPwd;
		}
		//否则都需要进行密码加密
		else{
			MD5keyBean bean = new MD5keyBean();
			md5LogonPwd = bean.getkeyBeanofStr(logonPwd);
		}
		elecUser.setLogonPwd(md5LogonPwd);
	}

	/**  
	* @Name: findElecUserByID
	* @Description: 按照userID获取该用户的详细信息
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-11 （创建日期）
	* @Parameters: ElecUser 存放userID
	* @Return: ElecUser 用户详细信息
	*/
	public ElecUser findElecUserByID(ElecUser elecUser) {
		elecUser = elecUserDao.findObjectByID(elecUser.getUserID());
		return elecUser;
	}

	/**  
	* @Name: deleteElecUserByID
	* @Description: 按照userID删除用户信息
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-11 （创建日期）
	* @Parameters: ElecUser 存放userID
	* @Return: 无
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void deleteElecUserByID(ElecUser elecUser) {
		String userID = elecUser.getUserID();
		elecUserDao.deleteObjectByIDs(userID);
	}

	/**  
	* @Name: checkUser
	* @Description: 使用登录名作为条件，获取用户信息，判断当前用户名是否存在
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-11 （创建日期）
	* @Parameters: String 登录名
	* @Return: String：判断用户名是否出现重复的标识
					 *    如果flag=1：此时表明用户存在
					 *    如果flag=2：此时表明用户不存在
	*/
	public String checkUser(String logonName) {
		String condition = " and o.logonName=?";
		Object [] params = {logonName};
		List<ElecUser> list = elecUserDao.findCollectionByConditionNoPage(condition, params, null);
		String flag = "";
		if(list!=null && list.size()>0){
			flag = "1";
		}
		else{
			flag = "2";
		}
		return flag;
	}

	/**  
	* @Name: findElecUserByLogonName
	* @Description: 使用登录名作为条件，获取ElecUser对象，作为登录的验证
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-14 （创建日期）
	* @Parameters: String 登录名
	* @Return: ElecUser：存放用户信息
	*/
	public ElecUser findElecUserByLogonName(String name) {
		String condition = " and o.logonName = ?";
		Object [] params = {name};
		List<ElecUser> list = elecUserDao.findCollectionByConditionNoPage(condition, params, null);
		ElecUser elecUser = null;
		if(list!=null && list.size()>0){
			elecUser = list.get(0);
		}
		return elecUser;
	}

	/**  
	* @Name: findRoleByLogonName
	* @Description: 使用登录名作为条件，获取登录登录名所具有的角色的集合
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-14 （创建日期）
	* @Parameters: String 登录名
	* @Return: Hashtable<String, String> 角色集合，集合的形式key：角色ID，
	*                                                     value：角色名称
	*/
	public Hashtable<String, String> findRoleByLogonName(String name) {
		List<Object[]> list = elecUserDao.findRoleByLogonName(name);
		Hashtable<String, String> ht = null;
		if(list!=null && list.size()>0){
			ht = new Hashtable<String, String>();
			for(int i=0;i<list.size();i++){
				Object [] objects = list.get(i);
				ht.put(objects[0].toString(), objects[1].toString());
			}
		}
		return ht;
	}

	/**  
	* @Name: findPopedomByLogonName
	* @Description: 使用登录名作为条件，获取登录名所具有的权限的集合
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-14 （创建日期）
	* @Parameters: String 登录名
	* @Return: String：存放系统权限
	*/
	public String findPopedomByLogonName(String name) {
		List<Object> list = elecUserDao.findPopedomByLogonName(name);
		StringBuffer buffer = null;
		if(list!=null && list.size()>0){
			buffer = new StringBuffer("");
			for(int i=0;i<list.size();i++){
				Object object = list.get(i);
				buffer.append(object.toString());
			}
		}
		return buffer.toString();
	}

	/**  
	* @Name: excelFildName
	* @Description: 构造excel的标题数据集合
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-21 （创建日期）
	* @Parameters: 无
	* @Return: ArrayList<String>：标题数据集合
	*/
	public ArrayList<String> excelFildName() {
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("登录名");
		fieldName.add("用户姓名");
		fieldName.add("性别");
		fieldName.add("联系电话");
		fieldName.add("是否在职");
		return fieldName;
	}

	/**  
	* @Name: excelFildData
	* @Description: 构造excel的数据数据集合
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-21 （创建日期）
	* @Parameters: 无
	* @Return: ArrayList<String>：数据数据集合
	*/
	public ArrayList<ArrayList<String>> excelFildData(ElecUser elecUser) {
		String userName = elecUser.getUserName();
		try {
			//方式一
			//userName = new String(userName.getBytes("iso-8859-1"),"UTF-8");
			//方式二
			userName = URLDecoder.decode(userName,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//组织查询条件
		String condition = "";
		List<String> paramsList = new ArrayList<String>();
		if(StringUtils.isNotBlank(userName)){
			condition += " and o.userName like ?";
			paramsList.add("%"+userName+"%");
		}
		Object [] params = paramsList.toArray();
		LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
		orderby.put("o.onDutyDate", "asc");
		List<ElecUser> list = elecUserDao.findCollectionByConditionNoPage(condition, params, orderby);
		//将数据字典项进行转换，才能显示到页面上
		this.userListConverntSystemDDL(list);
		//将list集合转换导出的数据格式
		ArrayList<ArrayList<String>> fieldDate = new ArrayList<ArrayList<String>>();
		for(int i=0;list!=null && i<list.size();i++){
			ArrayList<String> field = new ArrayList<String>();
			ElecUser user = list.get(i);
			//登录名 用户姓名 性别 联系电话 是否在职 
			field.add(user.getLogonName());
			field.add(user.getUserName());
			field.add(user.getSexID());
			field.add(user.getContactTel());
			field.add(user.getIsDuty());
			fieldDate.add(field);
		}
		return fieldDate;
	}

	/**  
	* @Name: excelImportData
	* @Description: 从excel读取的数据导入到数据库中
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-21 （创建日期）
	* @Parameters: ArrayList<String[]>：存放excel读取后的数据
	* @Return: 无
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void excelImportData(ArrayList<String[]> arrayList) {
		MD5keyBean bean = new MD5keyBean();
		for(int i=0;arrayList!=null && i<arrayList.size();i++){
			String [] str = arrayList.get(i);
			//实例化ElecUser，进行保存
			/**
			 * 数组中存放的顺序：
			 * 登录名	密码	  用户姓名	性别	  所属单位	联系地址	  是否在职
			 * yk	     123  杨康	    1	   2	    吉林	       1
			 */
			ElecUser elecUser = new ElecUser();
			elecUser.setLogonName(str[0]);
			elecUser.setLogonPwd(bean.getkeyBeanofStr(str[1]));//密码要加密
			elecUser.setUserName(str[2]);
			elecUser.setSexID(str[3]);//如果是真实项目，需要进行数据字典转换
			elecUser.setJctID(str[4]);//如果是真实项目，需要进行数据字典转换
			elecUser.setAddress(str[5]);
			elecUser.setIsDuty(str[6]);//如果是真实项目，需要进行数据字典转换
			elecUserDao.save(elecUser);
		}
		
	}

	/**  
	* @Name: findChartDataSet
	* @Description: 获取人员按照所属单位统计的数据集合
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-22 （创建日期）
	* @Parameters: 无
	* @Return: List<Object[]>：数据集合
	*/
	public List<Object[]> findChartDataSet() {
		List<Object[]> list = elecUserDao.findChartDataSet();
		return list;
	}

	
}
