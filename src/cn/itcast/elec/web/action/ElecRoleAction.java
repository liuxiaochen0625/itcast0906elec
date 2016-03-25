package cn.itcast.elec.web.action;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.elec.domain.ElecSystemDDL;
import cn.itcast.elec.domain.ElecUser;
import cn.itcast.elec.domain.ElecUserRole;
import cn.itcast.elec.service.IElecRoleService;
import cn.itcast.elec.service.IElecSystemDDLService;
import cn.itcast.elec.web.form.XmlObjectBean;

import com.opensymphony.xwork2.ModelDriven;


@Controller("elecRoleAction")
@Scope(value="prototype")
@SuppressWarnings("serial")
public class ElecRoleAction extends BaseAction implements ModelDriven<ElecUserRole> {

	private ElecUserRole elecUserRole = new ElecUserRole();
	
	public ElecUserRole getModel() {
		return elecUserRole;
	}
	
	@Resource(name=IElecRoleService.SERVICE_NAME)
	private IElecRoleService elecRoleService;
	
	@Resource(name=IElecSystemDDLService.SERVICE_NAME)
	private IElecSystemDDLService elecSystemDDLService;
	
	/**  
	* @Name: home
	* @Description: 跳转到角色管理的首页面（列表）
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-13 （创建日期）
	* @Parameters: 无
	* @Return: String,跳转到system/roleIndex.jsp
	*/
	public String home(){
		//按照角色类型获取对应的数据集
		ElecSystemDDL elecSystemDDL = new ElecSystemDDL();
		elecSystemDDL.setKeyword("角色类型");
		List<ElecSystemDDL> roleList = elecSystemDDLService.findSystemDDLList(elecSystemDDL);
		request.setAttribute("roleList", roleList);
		//获取Function.xml中的权限配置
		List<XmlObjectBean> xmlList = elecRoleService.readFunctionXml();
		request.setAttribute("xmlList", xmlList);
		return "home";
	}
	
	/**  
	* @Name: edit
	* @Description: 跳转到编辑角色、用户、权限的页面
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-13 （创建日期）
	* @Parameters: 无
	* @Return: String,跳转到system/roleEdit.jsp
	*/
	public String edit(){
		//使用选择的角色ID，获取对应的权限信息集合
		List<XmlObjectBean> xmlList = elecRoleService.editFunctionXml(elecUserRole);
		request.setAttribute("xmlList", xmlList);
		//使用选择的角色ID，获取对应的用户信息集合
		List<ElecUser> userList = elecRoleService.findUserList(elecUserRole);
		request.setAttribute("userList", userList);
		return "edit";
	}

	/**  
	* @Name: save
	* @Description: 保存角色与用户、角色与权限的关联关系
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-13 （创建日期）
	* @Parameters: 无
	* @Return: String,重定向到system/roleIndex.jsp
	*/
	public String save(){
		elecRoleService.saveRole(elecUserRole);
		return "save";
	}
}
