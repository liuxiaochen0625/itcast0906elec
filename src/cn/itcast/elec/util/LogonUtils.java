package cn.itcast.elec.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class LogonUtils {

	/**
	 * 验证页面的验证码输入是否正确
	 * boolean flag:
	 *      * flag=true:表示验证码输入无误
	 *      * flag=false：表示验证码输入错误，返回首页面，提示验证码输入有误
	 * */
	public static boolean checkNumber(HttpServletRequest request) {
		//获取页面的验证码
		String checkNumber = request.getParameter("checkNumber");
		if(StringUtils.isBlank(checkNumber)){
			return false;
		}
		//从session中获取给定的验证码
		String CHECK_NUMBER_KEY = (String) request.getSession().getAttribute("CHECK_NUMBER_KEY");
		if(StringUtils.isBlank(CHECK_NUMBER_KEY)){
			return false;
		}
		return checkNumber.equalsIgnoreCase(CHECK_NUMBER_KEY);
	}

	/**
	 * 添加记住我功能，将登录名和密码存放到Cookie中
	 * @param request
	 * @param response
	 */
	public static void rememberMe(HttpServletRequest request,
			HttpServletResponse response) {
		//获得登录名和密码
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		//创建2个Cookie，分别存放登录名和密码
		//处理name的编码格式，处理中文
		try {
			name = URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Cookie cookieName = new Cookie("name",name);
		Cookie cookiePassword = new Cookie("password",password);
		//设置Cookie的有效路径，路径为项目根目录
		cookieName.setPath(request.getContextPath()+"/");
		cookiePassword.setPath(request.getContextPath()+"/");
		//从页面中获取复选框的值，判断复选框是否被选中
		String remeberMe = request.getParameter("remeberMe");
		//说明复选框被选中，此时要设置Cookie的有效时长
		if(remeberMe!=null && remeberMe.equals("yes")){
			cookieName.setMaxAge(7*24*60*60);//一个星期
			cookiePassword.setMaxAge(7*24*60*60);
		}
		//清空Cookie的有效时长
		else{
			cookieName.setMaxAge(0);
			cookiePassword.setMaxAge(0);
		}
		//将Cookie存放到response对象中
		response.addCookie(cookieName);
		response.addCookie(cookiePassword);
	}

}
