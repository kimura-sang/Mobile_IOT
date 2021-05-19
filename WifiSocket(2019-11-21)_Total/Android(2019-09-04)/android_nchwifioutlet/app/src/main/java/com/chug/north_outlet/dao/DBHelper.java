package com.chug.north_outlet.dao;

import com.chug.north_outlet.Config;

import org.xutils.DbManager;
import org.xutils.DbManager.DbOpenListener;
import org.xutils.ex.DbException;
import org.xutils.x;

public class DBHelper {

    private static DbManager db;

    public static synchronized DbManager getdbInstance() {
        if (db == null) {
            DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                    .setDbName(Config.DBNAME)
                    .setDbVersion(1) //设置数据库版本,每次启动应用时将会检查该版本号,
                    // 发现数据库版本低于这里设置的值将进行数据库升级并触发DbUpgradeListener
                    .setDbOpenListener(new DbOpenListener() {

                        @Override
                        public void onDbOpened(DbManager db) {
                            // 开启WAL, 对写入加速提升巨大
                            db.getDatabase().enableWriteAheadLogging();
                        }
                    })//设置数据库创建时的Listener
                    .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager dbm, int oldVersion, int newVersion) {
                            try {
                                dbm.dropDb();
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                    });//设置数据库升级时的Listener,这里可以执行相关数据库表的相关修改,比如alter语句增加字段等
            //.setDbDir(null);//设置数据库.db文件存放的目录,默认为包名下databases目录下
            db = x.getDb(daoConfig);


        }
        return db;
    }

}
