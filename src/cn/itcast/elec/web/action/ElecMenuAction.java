package cn.itcast.elec.web.action;

import java.util.Hashtable;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.elec.domain.ElecCommonMsg;
import cn.itcast.elec.domain.ElecUser;
import cn.itcast.elec.service.IElecCommonMsgService;
import cn.itcast.elec.service.IElecUserService;
import cn.itcast.elec.util.LogonUtils;
import cn.itcast.elec.util.MD5keyBean;

import com.opensymphony.xwork2.ActionContext;

@Controller("elecMenuAction")
@Scope("prototype")
@SuppressWarnings("serial")
public class ElecMenuAction extends BaseAction {
	
	@Resource(name=IElecCommonMsgService.SERVICE_NAME)
	private IElecCommonMsgService elecCommonMsgService;
	
	@Resource(name=IElecUserService.SERVICE_NAME)
	private IElecUserService elecUserService;
	
	/**  
	* @Name: home
	* @Description: 处理系统登录
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-14 （创建日期）
	* @Parameters: 无
	* @Return: String 
	*       如果登录成功，跳转到home.jsp
	*       如果登录不成功，返回到index.jsp
	*/
	public String home(){
		/**
		 * 验证页面的验证码输入是否正确
		 * boolean flag:
		 *      * flag=true:表示验证码输入无误
		 *      * flag=false：表示验证码输入错误，返回首页面，提示验证码输入有误
		 * */
		boolean flag = LogonUtils.checkNumber(request);
		if(!flag){
			this.addFieldError("error", "您输入的验证码不正确！");
			return "error";
		}
		/**验证用户名和密码*/
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		//以登录名作为查询条件，查询用户信息表，获取对应的ElecUser对象
		ElecUser elecUser = elecUserService.findElecUserByLogonName(name);
		//返回登录页面，提示错误信息
		if(elecUser == null){
			this.addFieldError("error", "您输入的登录名不正确！");
			return "error";
		}
		if(StringUtils.isBlank(password)){
			this.addFieldError("error", "您输入的密码不能为空！");
			return "error";
		}
		//使用md5对密码进行加密
		MD5keyBean bean = new MD5keyBean();
		String md5Password = bean.getkeyBeanofStr(password);
		if(!elecUser.getLogonPwd().equals(md5Password)){
			this.addFieldError("error", "您输入的密码不正确！");
			return "error";
		}
		/**使用登录名获取系统中的角色ID和角色名称，存放到Hashtable中*/
		Hashtable<String, String> ht = elecUserService.findRoleByLogonName(name);
		if(ht==null){
			this.addFieldError("error", "您当前登录名没有分配角色！");
			return "error";
		}
		/**使用登录名获取系统中的权限，权限存放到String中*/
		String popedom = elecUserService.findPopedomByLogonName(name);
		if(StringUtils.isBlank(popedom)){
			this.addFieldError("error", "您当前登录名没有分配权限！");
			return "error";
		}
		/**添加记住我功能，将登录名和密码存放到Cookie中*/
		LogonUtils.rememberMe(request,response);
		request.getSession().setAttribute("globle_user", elecUser);
		request.getSession().setAttribute("globle_role", ht);
		request.getSession().setAttribute("globle_popedom", popedom);
		return "home";
	}
	
	public String title(){
		return "title";
	}
	
	public String left(){
		return "left";
	}
	
	public String change1(){
		return "change1";
	}
	
	public String loading(){
		return "loading";
	}
	
	public String alermXZ(){
		return "alermXZ";
	}
	
	public String alermJX(){
		return "alermJX";
	}
	
	/**  
	* @Name: alermZD
	* @Description: 显示站点运行情况
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-07 （创建日期）
	* @Parameters: 无
	* @Return: String 跳转到alermZD.jsp
	*/
	public String alermZD(){
		this.initCommonMsg();
		return "alermZD";
	}
	
	

	/**  
	* @Name: alermSB
	* @Description: 显示设备运行情况
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-07 （创建日期）
	* @Parameters: 无
	* @Return: String 跳转到alermSB.jsp
	*/
	public String alermSB(){
		this.initCommonMsg();
		return "alermSB";
	}
	
	/**加载代办事宜站点运行情况和设备运行情况信息*/
	private void initCommonMsg() {
		ElecCommonMsg elecCommonMsg = elecCommonMsgService.findCommonMsg();
		//放入到栈顶
		ActionContext.getContext().getValueStack().pop();
		ActionContext.getContext().getValueStack().push(elecCommonMsg);
	}
	
	public String alermYS(){
		return "alermYS";
	}
	
	/**  
	* @Name: logout
	* @Description: 重新登录（清空session）
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-14 （创建日期）
	* @Parameters: 无
	* @Return: String 跳转到index.jsp
	*/
	public String logout(){
		//清空session
		//第一种：按照指定的名称清空session
		//request.getSession().removeAttribute("globle_user");
		//第二种：清空系统中所有的session
		request.getSession().invalidate();
		return "logout";
	}
	
	/**  
	* @Name: logouterror
	* @Description: 错误操作，系统在5秒钟内调转到登录页面（清空session）
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-14 （创建日期）
	* @Parameters: 无
	* @Return: String 跳转到index.jsp
	*/
	public String logouterror(){
		request.getSession().invalidate();
		return "logout";
	}
}
