package cn.itcast.elec.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.elec.domain.ElecUser;

public class SystemFilter implements Filter {

	private List<String> list = new ArrayList<String>();
	/**初始化的时候启动init*/
	public void init(FilterConfig arg0) throws ServletException {
		//定义可以进行放行的连接，用List集合存储放行的连接
		list.add("/index.jsp");
		list.add("/image.jsp");
		list.add("/system/elecMenuAction_home.do");
		list.add("/error.jsp");
		list.add("/system/elecMenuAction_logouterror.do");
	}

	/**当访问每一个连接的时候，先执行过滤器的doFilter方法*/
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		//使用当前访问的链接与放行list的连接进行比较，如果匹配成功，就放行
		//当前访问的链接
		String path = request.getServletPath();
		if(list!=null && list.contains(path)){
			chain.doFilter(request, response);
			return;
		}
		//主要验证系统session是否存在，如果存在则放行，不存在就直接跳转到登录页面
		ElecUser elecUser = (ElecUser) request.getSession().getAttribute("globle_user");
		//如果session中存在用户信息，则表示正常访问，此时需要放行
		if(elecUser!=null){
			chain.doFilter(request, response);
			return;
		}
		//重定向到登录页面
		//response.sendRedirect(request.getContextPath()+"/");
		//使用error.jsp实现倒计时功能每隔5秒钟，自动调整到登录页面
		response.sendRedirect(request.getContextPath()+"/error.jsp");
	}

	
	public void destroy() {
		
		
	}

}
