package cn.itcast.elec.web.action;


import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.elec.domain.ElecApplication;
import cn.itcast.elec.domain.ElecApplicationTemplate;
import cn.itcast.elec.domain.ElecApproveInfo;
import cn.itcast.elec.domain.ElecSystemDDL;
import cn.itcast.elec.service.IElecApplicationService;
import cn.itcast.elec.service.IElecApplicationTemplateService;
import cn.itcast.elec.service.IElecSystemDDLService;

import com.opensymphony.xwork2.ModelDriven;


@Controller("elecApplicationAction")
@Scope(value="prototype")
@SuppressWarnings("serial")
public class ElecApplicationAction extends BaseAction implements ModelDriven<ElecApplication> {

	private ElecApplication elecApplication = new ElecApplication();
	
	public ElecApplication getModel() {
		return elecApplication;
	}
	
	@Resource(name=IElecApplicationService.SERVICE_NAME)
	private IElecApplicationService elecApplicationService;
	
	@Resource(name=IElecApplicationTemplateService.SERVICE_NAME)
	private IElecApplicationTemplateService elecApplicationTemplateService;
	
	@Resource(name=IElecSystemDDLService.SERVICE_NAME)
	private IElecSystemDDLService elecSystemDDLService;
	/**  
	* @Name: home
	* @Description: 跳转到起草申请的首页面（列表）
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-20 （创建日期）
	* @Parameters: 无
	* @Return: String,跳转到workflow/flowTemplateList.jsp
	*/
	public String templateHome(){
		List<ElecApplicationTemplate> list = elecApplicationTemplateService.findApplicationTemplateList();
		request.setAttribute("templateList", list);
		return "templateHome";
	}
	
	/**  
	* @Name: flowSubmitApplication
	* @Description: 跳转到提交申请的页面（列表）
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-20 （创建日期）
	* @Parameters: 无
	* @Return: String,跳转到workflow/flowSubmitApplication.jsp
	*/
	public String flowSubmitApplication(){	
		return "flowSubmitApplication";
	}
	
	/**  
	* @Name: saveApplication
	* @Description: 提交申请
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-20 （创建日期）
	* @Parameters: 无
	* @Return: String,重定向到我的申请查询，workflow/flowMyApplicationList.jsp
	*/
	public String saveApplication(){
		elecApplicationService.saveApplication(elecApplication);
		return "saveApplication";
	}
	
	/**  
	* @Name: myApplicationHome
	* @Description: 跳转到我的申请查询首页面
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-20 （创建日期）
	* @Parameters: 无
	* @Return: String,重定向到我的申请查询，workflow/flowMyApplicationList.jsp
	*/
	public String myApplicationHome(){
		//初始化页面的申请模板列表
		List<ElecApplicationTemplate> temList = elecApplicationTemplateService.findApplicationTemplateList();
		request.setAttribute("templateList", temList);
		//初始化页面的审核状态列表
		ElecSystemDDL elecSystemDDL = new ElecSystemDDL();
		elecSystemDDL.setKeyword("审核状态");
		List<ElecSystemDDL> sysList = elecSystemDDLService.findSystemDDLList(elecSystemDDL);
		request.setAttribute("systemList", sysList);
		//获取申请信息表的相关信息（根据当前人进行查询）
		List<ElecApplication> appList = elecApplicationService.findApplicationList(elecApplication);
		request.setAttribute("applicationList", appList);
		return "myApplicationHome";
	}
	
	/**  
	* @Name: myTaskHome
	* @Description: 跳转到待我审批的首页面
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-20 （创建日期）
	* @Parameters: 无
	* @Return: String,跳转到workflow/flowMyTaskList.jsp
	*/
	public String myTaskHome(){
		List<ElecApplication> list = elecApplicationService.findApplicationVariable();
		request.setAttribute("applicationList",list);
		return "myTaskHome";
	}
	
	/**  
	* @Name: flowApprove
	* @Description: 跳转到审批处理页面
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-20 （创建日期）
	* @Parameters: 无
	* @Return: String,跳转到workflow/flowApprove.jsp
	*/
	public String flowApprove(){
		Collection<String> collection = elecApplicationService.getOutComeTransition(elecApplication);
		request.setAttribute("collection", collection);
		return "flowApprove";
	}
	
	/**  
	* @Name: download
	* @Description: 下载申请文件
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-20 （创建日期）
	* @Parameters: 无
	* @Return: 使用struts提供的下载type=stream
	*/
	public String download(){
		InputStream inputStream = elecApplicationService.findInputStreamByPath(elecApplication);
		//放置到模型驱动中
		elecApplication.setInputStream(inputStream);
		return "download";
	}
	
	/**  
	* @Name: approve
	* @Description: 审批处理
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-20 （创建日期）
	* @Parameters: 无
	* @Return: 使用重定向到待我审批的首页面workflow/flowMyTaskList
	*/
	public String approve(){
		elecApplicationService.approveInfo(elecApplication);
		return "approve";
	}
	
	/**  
	* @Name: flowApprovedHistory
	* @Description: 获取审核的历史流程数据
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-20 （创建日期）
	* @Parameters: 无
	* @Return: 跳转的页面workflow/flowApprovedHistory.jsp
	*/
	public String flowApprovedHistory(){
		List<ElecApproveInfo> list = elecApplicationService.findApproveInfoListByApplicationID(elecApplication);
		request.setAttribute("approveList", list);
		return "flowApprovedHistory";
	}
	
}
