package com.ht.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;


@Component
public class SAPConnUtils {

    private static String ABAP_AS_POOLED;


    /**
     * 创建SAP接口属性文件。
     * @param name  ABAP管道名称
     * @param suffix    属性文件后缀
     * @param properties    属性文件内容
     */
    private static void createDataFile(String name, String suffix, Properties properties){
        File cfg = new File(name+"."+suffix);
        if(cfg.exists()){
            cfg.deleteOnExit();
        }
        try{
            FileOutputStream fos = new FileOutputStream(cfg, false);
            properties.store(fos, "for tests only !");
            fos.close();
        }catch (Exception e){
            System.out.println("Create Data file fault, error msg: " + e.toString());
            throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
        }
    }

    /**
     * 初始化SAP连接
     */

    private void initProperties() {


        Properties connectProperties = new Properties();
        // SAP服务器
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "172.31.2.3");
        // SAP系统编号
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  "00");
        // SAP集团
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "800");
        // SAP用户名
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   "ituser");
        // SAP密码
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "itqaz2345");
        // SAP登录语言
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "ZH");
        // 最大连接数
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "10");
        // 最大连接线程
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "20");
        // SAP ROUTER
        //connectProperties.setProperty(DestinationDataProvider.JCO_SAPROUTER, sapConn.getJCO_SAPROUTER());

        createDataFile(ABAP_AS_POOLED, "jcoDestination", connectProperties);
    }

    /**
     * 获取SAP连接
     * @return  SAP连接对象
     */
    public JCoDestination connect(){
        System.out.println("正在连接至SAP...");
        JCoDestination destination = null;
        System.out.println("1");
        initProperties();
        System.out.println("2");
        try {
            destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
            System.out.println("进来");
            destination.ping();
            System.out.println("已成功建立sap的连接");
        } catch (JCoException e) {
            System.out.println("Connect SAP fault, error msg: " + e.toString());
        }
        return destination;
    }

}

