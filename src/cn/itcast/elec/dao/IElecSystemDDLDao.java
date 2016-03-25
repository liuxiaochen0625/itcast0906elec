package cn.itcast.elec.dao;

import java.util.List;

import cn.itcast.elec.domain.ElecSystemDDL;

public interface IElecSystemDDLDao extends ICommonDao<ElecSystemDDL> {
	public static final String SERVICE_NAME = "cn.itcast.elec.dao.impl.ElecSystemDDLDaoImpl";

	List<Object[]> findKeywordList();

	String findDdlNameByCondition(String keyword, String ddlCode);

	

}
