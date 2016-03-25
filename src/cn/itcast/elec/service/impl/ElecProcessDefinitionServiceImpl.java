package cn.itcast.elec.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;

import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessDefinitionQuery;
import org.jbpm.api.ProcessEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.elec.service.IElecProcessDefinitionService;
import cn.itcast.elec.web.form.ElecProcessDefinition;


@Service(IElecProcessDefinitionService.SERVICE_NAME)
@Transactional(readOnly=true)
public class ElecProcessDefinitionServiceImpl implements IElecProcessDefinitionService {

	@Resource(name="processEngine")
	private ProcessEngine processEngine;

	/**  
	* @Name: findPDListByLastVersion
	* @Description: 获取最新版本的流程定义列表
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: 无
	* @Return: List<ProcessDefinition> 流程定义列表
	*/
	public List<ProcessDefinition> findPDListByLastVersion() {
		List<ProcessDefinition> list = processEngine.getRepositoryService()//
						            .createProcessDefinitionQuery()//
						            .orderAsc(ProcessDefinitionQuery.PROPERTY_VERSION)//将最新版本的流程放置到后面
						            .list();
		Map<String, ProcessDefinition> map = new HashMap<String, ProcessDefinition>();
		for(ProcessDefinition pd:list){
			//用pd.getKey()作为key的好处：同一个key的版本逐渐增加，最后一次的版本将前面所有的版本都替换
			map.put(pd.getKey(), pd);
		}
		List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>(map.values()); 
		return pdList;
	}

	/**  
	* @Name: deployeProcessDefinition
	* @Description: 部署流程定义
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: ElecProcessDefinition：用来存放流程定义文档的file文件
	* @Return: 无
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void deployeProcessDefinition(
			ElecProcessDefinition elecProcessDefinition) {
		try {
			//获取页面传递的流程定义文件，格式为zip格式
			File upload = elecProcessDefinition.getUpload();
			ZipInputStream zipInputStream  = new ZipInputStream(new FileInputStream(upload));
			//部署流程定义
			processEngine.getRepositoryService()//
			             .createDeployment()//
			             .addResourcesFromZipInputStream(zipInputStream)//
			             .deploy();
			zipInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**  
	* @Name: deleteProcessDefinitionByKey
	* @Description: 按照key删除所有版本的流程定义
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: ElecProcessDefinition：用来存放流程定义的key
	* @Return: 无
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void deleteProcessDefinitionByKey(
			ElecProcessDefinition elecProcessDefinition) {
		//获取页面传递的流程定义的key
		String key = elecProcessDefinition.getKey();
		//处理key的乱码问题
		try {
			key = new String(key.getBytes("iso-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//使用key获取所有版本的流程定义
		List<ProcessDefinition> list = processEngine.getRepositoryService()//
						              .createProcessDefinitionQuery()//
						              .processDefinitionKey(key)//
						              .list();
		//删除流程定义
		for(ProcessDefinition pd:list){
			String deploymentId = pd.getDeploymentId();
			processEngine.getRepositoryService()//
	                  .deleteDeploymentCascade(deploymentId);
		}
	}
	/**  
	* @Name: findImpageInputStreamByID
	* @Description: 按照id获取对应的图片信息，并封装到inputStream中
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: ElecProcessDefinition：用来存放流程定义的id
	* @Return: InputStream：输入流存放图片
	*/
	public InputStream findImpageInputStreamByID(
			ElecProcessDefinition elecProcessDefinition) {
		//获取页面传递的流程定义id
		String processDefinitionId = elecProcessDefinition.getId();
		try {
			processDefinitionId = new String(processDefinitionId.getBytes("iso-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ProcessDefinition pd = processEngine.getRepositoryService()//
				               .createProcessDefinitionQuery()//
				               .processDefinitionId(processDefinitionId)//
				               .uniqueResult();
		String deploymentId = pd.getDeploymentId();
		String resourceName = pd.getImageResourceName();
		InputStream inputStream = processEngine.getRepositoryService()//
		                     .getResourceAsStream(deploymentId, resourceName);
		return inputStream;
	}
}
