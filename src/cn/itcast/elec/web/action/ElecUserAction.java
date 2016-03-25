package cn.itcast.elec.web.action;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.elec.domain.ElecSystemDDL;
import cn.itcast.elec.domain.ElecUser;
import cn.itcast.elec.service.IElecSystemDDLService;
import cn.itcast.elec.service.IElecUserService;
import cn.itcast.elec.util.ChartUtils;
import cn.itcast.elec.util.ExcelFileGenerator;
import cn.itcast.elec.util.GenerateSqlFromExcel;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;


@Controller("elecUserAction")
@Scope(value="prototype")
@SuppressWarnings("serial")
public class ElecUserAction extends BaseAction implements ModelDriven<ElecUser> {

	private ElecUser elecUser = new ElecUser();
	
	public ElecUser getModel() {
		return elecUser;
	}
	
	@Resource(name=IElecUserService.SERVICE_NAME)
	private IElecUserService elecUserService;
	
	@Resource(name=IElecSystemDDLService.SERVICE_NAME)
	private IElecSystemDDLService elecSystemDDLService;
	
	/**  
	* @Name: home
	* @Description: 跳转到用户管理的首页面（列表）
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-11 （创建日期）
	* @Parameters: 无
	* @Return: String,跳转到system/userIndex.jsp
	*/
	public String home(){
		List<ElecUser> list = elecUserService.findElecUserList(elecUser);
		request.setAttribute("userList", list);
		/**
		 * 添加分页， 2012-9-21：begin
		 * 点击用户管理，需要retrun home，此时initflag的值是null
		 * 点击查询、上一页、...，需要return list，测试initflag的值是1
		 * */
		String initflag = request.getParameter("initflag");
		if(initflag!=null && initflag.equals("1")){
			return "list";
		}
		/**end*/
		return "home";
	}
	
	/**  
	* @Name: add
	* @Description: 跳转到添加页面
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-11 （创建日期）
	* @Parameters: 无
	* @Return: String,跳转到system/userAdd.jsp
	*/
	public String add(){
		//初始化数据字典中的项（性别、所属单位、是否在职）
		this.initSystemDDL();
		return "add";
	}
	/**
	 * 初始化用户信息中，数据字典的值
	 */
	private void initSystemDDL() {
		ElecSystemDDL elecSystemDDL = new ElecSystemDDL();
		elecSystemDDL.setKeyword("性别");
		List<ElecSystemDDL> sexList = elecSystemDDLService.findSystemDDLList(elecSystemDDL);
		elecSystemDDL.setKeyword("所属单位");
		List<ElecSystemDDL> jctList = elecSystemDDLService.findSystemDDLList(elecSystemDDL);
		elecSystemDDL.setKeyword("是否在职");
		List<ElecSystemDDL> isDutyList = elecSystemDDLService.findSystemDDLList(elecSystemDDL);
		//将3个值放置到request对象中
		request.setAttribute("sexList", sexList);//性别
		request.setAttribute("jctList", jctList);//所属单位
		request.setAttribute("isDutyList", isDutyList);//是否在职
	}

	/**  
	* @Name: save
	* @Description: 保存用户信息
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-11 （创建日期）
	* @Parameters: 无
	* @Return: String,重定向到system/userIndex.jsp
	*/
	public String save(){
		elecUserService.saveElecUser(elecUser);
		String roleflag = elecUser.getRoleflag();
		if(roleflag!=null && roleflag.equals("1")){
			return edit();
		}
		return "save";
	}
	
	/**  
	* @Name: edit
	* @Description: 跳转到编辑页面
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-11 （创建日期）
	* @Parameters: 无
	* @Return: String,跳转到system/userEdit.jsp
	*/
	public String edit(){
		//获取viewflag的值，作为一个中间变量
		String viewflag = elecUser.getViewflag();
		//获取roleflag的值，作为一个中间变量
		String roleflag = elecUser.getRoleflag();
		elecUser = elecUserService.findElecUserByID(elecUser);
		elecUser.setViewflag(viewflag);//存放viewflag的值
		elecUser.setRoleflag(roleflag);//存放roleflag的值
		//将用户信息放置到栈顶用于显示
		ActionContext.getContext().getValueStack().pop();//删除栈顶信息
		ActionContext.getContext().getValueStack().push(elecUser);//将ElecUser对象放置到栈顶
		//初始化数据字典中的项（性别、所属单位、是否在职）
		this.initSystemDDL();
		//ValueStack stack = ActionContext.getContext().getValueStack();
		return "edit";
	}
	
	/**  
	* @Name: delete
	* @Description: 删除用户信息
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-11 （创建日期）
	* @Parameters: 无
	* @Return: String,重定向到system/userIndex.jsp
	*/
	public String delete(){
		elecUserService.deleteElecUserByID(elecUser);
		return "save";
	}
	
	/**  
	* @Name: checkUser
	* @Description: 使用ajax验证登录名在数据库中是否存在
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-11 （创建日期）
	* @Parameters: 无
	* @Return: 无
	*/
	public String checkUser(){
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			String logonName = elecUser.getLogonName();
			/**
			 * flag作为判断用户名是否出现重复的标识
			 *    如果flag=1：此时表明用户存在
			 *    如果flag=2：此时表明用户不存在
			 */
			String flag = elecUserService.checkUser(logonName);
			PrintWriter out = response.getWriter();
			out.println(flag);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**  
	* @Name: exportExcel
	* @Description: 将用户信息列表中的信息导出成excel
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-21 （创建日期）
	* @Parameters: 无
	* @Return: 无
	*/
	public String exportExcel(){
		/**
		 * ArrayList<String> fieldName:存放的是excel的标题集合
		 *     fieldName.add("登录名");
		 *     fieldName.add("用户姓名");
		 *     ...
		 */
		ArrayList<String> fieldName = elecUserService.excelFildName();
		/**
		 * ArrayList<ArrayList<String>> fieldData:存放的是excel的数据集合
		 *      ArrayList<String> field = new ArrayList<String>();
		 *      field.add("zhaoyun");
		 *      field.add("赵云");
		 *      ...
		 *      
		 *      fieldData.add(field);
		 */
		ArrayList<ArrayList<String>> fieldData = elecUserService.excelFildData(elecUser);
		ExcelFileGenerator generator = new ExcelFileGenerator(fieldName,fieldData);
		try {
			//用response对象获取输出流
			OutputStream os = response.getOutputStream();
			//重置response对象中的缓冲区，该方法可以不写，但是你要保证response缓冲区没有其他数据，否则导出可能会出现问题，建议加上
			response.reset();
			//由于导出格式是excel的文件，设置导出文件的响应头部信息
			response.setContentType("application/vnd.ms-excel");
			//生成excel,传递输出流
			generator.expordExcel(os);
			//清理刷新缓冲区，将缓存中的数据将数据导出excel
			os.flush();
			//关闭os
			if(os!=null){
				os.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**  
	* @Name: importExcelPage
	* @Description: 跳转到导入excel的页面
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-21 （创建日期）
	* @Parameters: 无
	* @Return: 跳转到system/userImport.jsp
	*/
	public String importExcelPage(){
		
		return "importExcel";
	}
	
	/**  
	* @Name: importData
	* @Description: 将excel中存放的数据导入到数据库中
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-21 （创建日期）
	* @Parameters: 无
	* @Return: 跳转到system/userImport.jsp
	*/
	public String importData(){
		//获取导入的excel文件
		File file = elecUser.getFile();
		GenerateSqlFromExcel fromExcel = new GenerateSqlFromExcel();
		try {
			/**
			 * ArrayList<String[]> arrayList:集合就是从excel中读取的数据集合
			 * 针对本例：4条数据，String[]用于存值
			 */
			ArrayList<String[]> arrayList = fromExcel.generateUserSql(file);
			//将arrayList的数据导入到数据库中
			elecUserService.excelImportData(arrayList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "importExcel";
	}
	
	/**  
	* @Name: chartUser
	* @Description: 按照柱状图统计人员
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-22 （创建日期）
	* @Parameters: 无
	* @Return: 跳转到system/userReport.jsp
	*/
	public String chartUser(){
		List<Object[]> list = elecUserService.findChartDataSet();
		//生成图片，同时要返回图片的名称，将图片名称放置到request中
		String filename = ChartUtils.generaterBarChart(list);
		request.setAttribute("filename", filename);
		return "chartUser";
	}
}
