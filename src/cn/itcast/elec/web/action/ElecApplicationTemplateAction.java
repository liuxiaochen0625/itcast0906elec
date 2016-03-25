package cn.itcast.elec.web.action;


import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;
import org.jbpm.api.ProcessDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.elec.domain.ElecApplicationTemplate;
import cn.itcast.elec.service.IElecApplicationTemplateService;
import cn.itcast.elec.service.IElecProcessDefinitionService;

import com.opensymphony.xwork2.ModelDriven;


@Controller("elecApplicationTemplateAction")
@Scope(value="prototype")
@SuppressWarnings("serial")
public class ElecApplicationTemplateAction extends BaseAction implements ModelDriven<ElecApplicationTemplate> {

	private ElecApplicationTemplate elecApplicationTemplate = new ElecApplicationTemplate();
	
	public ElecApplicationTemplate getModel() {
		return elecApplicationTemplate;
	}
	
	@Resource(name=IElecApplicationTemplateService.SERVICE_NAME)
	private IElecApplicationTemplateService elecApplicationTemplateService;
	
	@Resource(name=IElecProcessDefinitionService.SERVICE_NAME)
	private IElecProcessDefinitionService elecProcessDefinitionService;
	
	/**  
	* @Name: home
	* @Description: 跳转到申请模板管理的首页面（列表）
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: 无
	* @Return: String,跳转到workflow/applicationTemplateList.jsp
	*/
	public String home(){
		List<ElecApplicationTemplate> list = elecApplicationTemplateService.findApplicationTemplateList();
		request.setAttribute("templateList", list);
		return "home";
	}
	
	/**  
	* @Name: add
	* @Description: 跳转到新增申请模板页面
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: 无
	* @Return: String,跳转到workflow/applicationTemplateAdd.jsp
	*/
	public String add(){
		//获取最新版本的流程定义列表，在页面中显示
		List<ProcessDefinition> list = elecProcessDefinitionService.findPDListByLastVersion();
		request.setAttribute("pdList", list);
		return "add";
	}
	
	/**  
	* @Name: save
	* @Description: 保存申请模板
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: 无
	* @Return: String,重定向到workflow/applicationTemplateList.jsp
	*/
	public String save(){
		elecApplicationTemplateService.saveApplicationTemplate(elecApplicationTemplate);
		return "save";
	}
	
	/**  
	* @Name: edit
	* @Description: 跳转到编辑页面
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: 无
	* @Return: String,跳转到workflow/applicationTemplateEdit.jsp
	*/
	public String edit(){
		elecApplicationTemplate = elecApplicationTemplateService.findApplicationTemplateByID(elecApplicationTemplate);
		//将对象elecApplicationTemplate放置栈顶，用于表单回显
		ServletActionContext.getContext().getValueStack().pop();
		ServletActionContext.getContext().getValueStack().push(elecApplicationTemplate);
		//获取最新版本的流程定义列表
		List<ProcessDefinition> list = elecProcessDefinitionService.findPDListByLastVersion();
		request.setAttribute("pdList", list);
		return "edit";
	}
	
	/**  
	* @Name: update
	* @Description: 修改保存申请模板
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: 无
	* @Return: String,重定向到workflow/applicationTemplateList.jsp
	*/
	public String update(){
		elecApplicationTemplateService.updateApplicationTemplate(elecApplicationTemplate);
		return "save";
	}
	
	/**  
	* @Name: delete
	* @Description: 删除申请模板数据
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: 无
	* @Return: String,重定向到workflow/applicationTemplateList.jsp
	*/
	public String delete(){
		elecApplicationTemplateService.deleteApplicationTemplateByID(elecApplicationTemplate);
		return "save";
	}
	
	/**  
	* @Name: download
	* @Description: 文件下载
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: 无
	* @Return: String,使用stream类型完成对文件的下载
	*/
	public String download(){
		InputStream inputStream = elecApplicationTemplateService.findInputStreamByPath(elecApplicationTemplate);
		//放置到模型驱动中
		elecApplicationTemplate.setInputStream(inputStream);
		return "download";
	}
	
}
