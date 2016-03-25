package cn.itcast.elec.util;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.struts2.ServletActionContext;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis3D;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.DefaultCategoryDataset;

public class ChartUtils {

	/**
	 * 使用Jfreechart生成图片
	 * @param list，集合
	 * @return
	 */
	public static String generaterBarChart(List<Object[]> list) {
		//构造数据集合
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(int i=0;list!=null && i<list.size();i++){
			//b.keyword,b.ddlName,COUNT(a.jctID)
			Object [] objects = list.get(i);
			dataset.addValue(Double.parseDouble(objects[2].toString()), objects[0].toString(), objects[1].toString());
		}
		
		JFreeChart chart = ChartFactory.createBarChart3D("用户统计报表（所属单位）",   //图形主标题
									                    "所属单位名称",              //x轴外的标题
									                    "数量",              		//y轴外的标题
									                    dataset, 
									                    PlotOrientation.VERTICAL,  //表示图表显示形式（水平/垂直） 
									                    true,  						//是否显示子标题
									                    true, 						//是否在图形上生成对应的提示工具
									                    true   						//是否生成URL连接地址
									                    ); 
		//处理主标题乱码
		chart.getTitle().setFont(new Font("宋体",Font.BOLD,18));
		//处理子标题的乱码
		chart.getLegend().setItemFont(new Font("宋体",Font.BOLD,15));
		//处理X轴和Y轴的乱码
		//调用图表区域对象(xxxxPlot)
		CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
		//获取X轴的对象
		CategoryAxis3D categoryAxis3D = (CategoryAxis3D) categoryPlot.getDomainAxis();
		//获取Y轴的对象
		NumberAxis3D numberAxis3D = (NumberAxis3D) categoryPlot.getRangeAxis();
		//处理X轴上的乱码
		categoryAxis3D.setTickLabelFont(new Font("宋体",Font.BOLD,15));
		//处理X轴外的乱码
		categoryAxis3D.setLabelFont(new Font("宋体",Font.BOLD,15));
		//处理Y轴上的乱码
		numberAxis3D.setTickLabelFont(new Font("宋体",Font.BOLD,15));
		//处理Y轴外的乱码
		numberAxis3D.setLabelFont(new Font("宋体",Font.BOLD,15));
		
		//设置y轴上的刻度的默认值为1
		numberAxis3D.setAutoTickUnitSelection(false);
		NumberTickUnit unit = new NumberTickUnit(1);
		numberAxis3D.setTickUnit(unit);
		
		//调用绘图区域对象（xxxxRenderer）
		BarRenderer3D barRenderer3D = (BarRenderer3D) categoryPlot.getRenderer();
		//使图形变得苗条
		barRenderer3D.setMaximumBarWidth(0.08);
		//在图形上生成数字
		barRenderer3D.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		barRenderer3D.setBaseItemLabelsVisible(true);
		barRenderer3D.setBaseItemLabelFont(new Font("宋体",Font.BOLD,15));
		
		//要求使图形chart生成图片，在项目指定的chart文件夹下
		//获取项目chart文件夹的目录
		String basePath = ServletActionContext.getServletContext().getRealPath("/chart");
		//按照当前日期的年月日时分秒的形式生成图片名称
		String filename = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")+".png";
		//用Jfreechart生成的图片放置到指定的chart的文件夹下
		File file = new File(basePath+"/"+filename);
		try {
			ChartUtilities.saveChartAsJPEG(file, chart, 800, 600);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filename;
	}

}
