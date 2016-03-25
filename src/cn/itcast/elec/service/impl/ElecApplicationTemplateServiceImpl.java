package cn.itcast.elec.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.elec.dao.IElecApplicationTemplateDao;
import cn.itcast.elec.domain.ElecApplicationTemplate;
import cn.itcast.elec.service.IElecApplicationTemplateService;
import cn.itcast.elec.util.UploadUtils;


@Service(IElecApplicationTemplateService.SERVICE_NAME)
@Transactional(readOnly=true)
public class ElecApplicationTemplateServiceImpl implements IElecApplicationTemplateService {

	@Resource(name=IElecApplicationTemplateDao.SERVICE_NAME)
	private IElecApplicationTemplateDao elecApplicationTemplateDao;

	/**  
	* @Name: findApplicationTemplateList
	* @Description: 查询所有申请模板信息
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: 无
	* @Return: List<ElecApplicationTemplate>：申请模板列表
	*/
	public List<ElecApplicationTemplate> findApplicationTemplateList() {
		List<ElecApplicationTemplate> list = elecApplicationTemplateDao.findCollectionByConditionNoPage("", null, null);
		return list;
	}

	/**  
	* @Name: saveApplicationTemplate
	* @Description: 保存申请模板数据，同时上传申请模板附件
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: ElecApplicationTemplate：保存参数
	* @Return: 无
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void saveApplicationTemplate(
			ElecApplicationTemplate elecApplicationTemplate) {
		//在UploadUtils类中完成文件上传，并返回对应的path
		File upload = elecApplicationTemplate.getUpload();
		String path = UploadUtils.uploadReturnPath(upload);
		//保存数据库
		elecApplicationTemplate.setPath(path);
		//执行保存
		elecApplicationTemplateDao.save(elecApplicationTemplate);
		
	}

	/**  
	* @Name: findApplicationTemplateByID
	* @Description: 使用ID获取申请模板的详细信息
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: ElecApplicationTemplate：存放申请模板ID
	* @Return: ElecApplicationTemplate：申请模板的详细信息
	*/
	public ElecApplicationTemplate findApplicationTemplateByID(
			ElecApplicationTemplate elecApplicationTemplate) {
		Long id = elecApplicationTemplate.getId();
		elecApplicationTemplate = elecApplicationTemplateDao.findObjectByID(id);
		return elecApplicationTemplate;
	}

	/**  
	* @Name: updateApplicationTemplate
	* @Description: 使用ID对申请模板信息进行修改，同时判断是否上传了新的模板文件
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: ElecApplicationTemplate：存放保存参数
	* @Return: 无
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void updateApplicationTemplate(
			ElecApplicationTemplate elecApplicationTemplate) {
		//已上传文件的路径
		String oldPath = elecApplicationTemplate.getPath();
		//使用upload判断是否上传了新的模板文件
		File upload = elecApplicationTemplate.getUpload();
		if(upload!=null){//说明上传了新的模板文件
			//删除旧的文件
			File oldFile = new File(oldPath);
			if(oldFile.exists()){
				oldFile.delete();
			}
			//上传新的文件
			String path = UploadUtils.uploadReturnPath(upload);
			//在保存的对象中添加新的路径path
			elecApplicationTemplate.setPath(path);
		}
		else{//说明没有上传新的模板文件
			
		}
		//按照ID更新数据库
		elecApplicationTemplateDao.update(elecApplicationTemplate);
	}

	/**  
	* @Name: deleteApplicationTemplateByID
	* @Description: 使用ID对删除申请模板信息数据，同时删除之前上传的文件
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: ElecApplicationTemplate：存放申请模板ID
	* @Return: 无
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void deleteApplicationTemplateByID(
			ElecApplicationTemplate elecApplicationTemplate) {
		//获取申请模板ID
		Long id = elecApplicationTemplate.getId();
		//使用id获取申请模板的详细信息
		ElecApplicationTemplate applicationTemplate = elecApplicationTemplateDao.findObjectByID(id);
		//获取路径path，用删除文件
		String path = applicationTemplate.getPath();
		//删除文件
		File file = new File(path);
		if(file.exists()){
			file.delete();
		}
		//使用ID删除数据库的信息
		elecApplicationTemplateDao.deleteObjectByIDs(id);
	}

	/**  
	* @Name: findInputStreamByPath
	* @Description: 使用ID获取文件的路径，按照文件的路径将文件放置到InputStream中
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2012-09-18 （创建日期）
	* @Parameters: ElecApplicationTemplate：存放申请模板ID
	* @Return: InputStream：存放文件
	*/
	public InputStream findInputStreamByPath(
			ElecApplicationTemplate elecApplicationTemplate) {
		//获取申请模板ID
		Long id = elecApplicationTemplate.getId();
		//使用id获取申请模板的详细信息
		ElecApplicationTemplate applicationTemplate = elecApplicationTemplateDao.findObjectByID(id);
		InputStream inputStream = null;
		try {
			//获取路径path
			String path = applicationTemplate.getPath();
			//获取模板的名称用作下载文件的名称
			String filename = applicationTemplate.getName();
			//将filename转成iso8859-1的形式，用作在xml
			filename = new String(filename.getBytes("GBK"),"iso8859-1");
			//将filename使用request传递给struts.xml，用来对下载文件的命名
			ServletActionContext.getRequest().setAttribute("fileName", filename);
			//使用路径path找到对应的文件
			File file = new File(path);
			inputStream = new FileInputStream(file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return inputStream;
	}
	
}
