package com.chug.north_outlet.dao;


import com.chug.north_outlet.bean.EntityBase;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * 
 * @ClassName: DaoUtil 
 * @Description: TODO(数据库操作工具类) 
 * @author yinhui 
 * @date 2015-12-22 下午6:27:31 
 *
 */
public class DaoUtil {
	
	/**
	 * 
	 * @Title: saveOrUpdate 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param t    设定文件 
	 * @param @param t    设定文件 add
	 * @return void    返回类型
	 * @throws
	 */
	public static <T extends EntityBase> void saveOrUpdate(T t) {
		try {
			DBHelper.getdbInstance().saveOrUpdate(t);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	public static <T extends EntityBase> void update(T t,String... updateColumnNames) {
		try {
			DBHelper.getdbInstance().update(t,updateColumnNames);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	public static <T extends EntityBase> void update(Class<T> _class, WhereBuilder whereBuilder, KeyValue keyValue) {
		try {
			DBHelper.getdbInstance().update(_class,whereBuilder,keyValue);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title: deleteEntry 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param t    设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public static <T extends EntityBase> void delete(T t) {
		try {
			DBHelper.getdbInstance().delete(t);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @Title: deleteEntry 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param t    设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public static <T extends EntityBase> void delete(Class<T> _class,WhereBuilder whereBuilder) {
		try {
			DBHelper.getdbInstance().delete(_class,whereBuilder);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @Title: deleteById 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param _class
	 * @param @param id    设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public static <T extends EntityBase> void deleteById(Class<T> _class,Object id) {
		try {
			DBHelper.getdbInstance().deleteById(_class, id);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @Title: getAllList 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param _class
	 * @param @return    设定文件 
	 * @return List<T>    返回类型 
	 * @throws
	 */
	public static <T extends EntityBase> List<T> getAllList(Class<T> _class) {
		List<T> list = null;
		try {
			list = DBHelper.getdbInstance().findAll(_class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 
	 * @Title: getAllList 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param _class
	 * @param @param columnName
	 * @param @param op
	 * @param @param value
	 * @param @return    设定文件 
	 * @return List<T>    返回类型 
	 * @throws
	 */
	public static <T extends EntityBase> List<T> getAllList(Class<T> _class,WhereBuilder whereBuilder) {
		List<T> list = null;
		try {
			list = DBHelper.getdbInstance().selector(_class).where(whereBuilder).findAll();
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 
	 * @Title: queryOne 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param _class
	 * @param @param whereBuilder
	 * @param @return    设定文件 
	 * @return T    返回类型 
	 * @throws
	 */
	public static <T extends EntityBase> T queryOne(Class<T> _class,WhereBuilder whereBuilder) {
		T entry = null;
		try {
			entry = DBHelper.getdbInstance().selector(_class).where(whereBuilder).findFirst();
		} catch (DbException e) {
			e.printStackTrace();
		}
		return entry;
	}
	
	/**
	 * 
	 * @param _class
	 */
	public static <T extends EntityBase> void dropTable(Class<T> _class) {
		try {
			DBHelper.getdbInstance().dropTable(_class);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static void dropDb() {
		try {
			DBHelper.getdbInstance().dropDb();
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
}
