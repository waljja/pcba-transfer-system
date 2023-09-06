package com.ht.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import com.ht.vo.LocationInfoVo;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class SAPServiceUtil {
	private static  String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL_LEDLIGHT";
	
	public SAPServiceUtil() {
	      	
	  	try{
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
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void createDataFile(String name,String suffix,Properties properties) {
		File cfg = new File(name + "." + suffix);
		if (!cfg.exists()) {
			try {
				FileOutputStream fos = new FileOutputStream(cfg, false);
				properties.store(fos, "for tests only !");
				fos.close();
			} catch (Exception e) {
				throw new RuntimeException("Unable to create the destination file "+ cfg.getName(), e);
			}
		}
	}
	
	//根据PN，工厂获取发出库位 
	public String getSAPPN(String PN,String Factory) throws Exception {		
		String PartNumber = "";
		//获取连接池
        JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
        //获取功能函数
        JCoFunction function = destination.getRepository().getFunction("Z_DC_GET_MATERIAL");
        if(function == null) throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
        //给功能函数输入参数
        JCoParameterList input = function.getImportParameterList();
        input.setValue("I_MATNR",PN);	 
        input.setValue("I_WERKS",Factory);
        function.execute(destination); //函数执行 
        PartNumber = function.toXML();
        PartNumber = (String) PartNumber.subSequence(PartNumber.indexOf("<ZISSLOC>")+9, PartNumber.indexOf("</ZISSLOC>"));
        return PartNumber;
	}
	
		//根据PN，工厂获取发出库位 
		public String getSAPPNRec(String PN,String Factory) throws Exception {		
			String PartNumber = "";
			//获取连接池
	        JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
	        //获取功能函数
	        JCoFunction function = destination.getRepository().getFunction("Z_DC_GET_MATERIAL");
	        if(function == null) throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
	        //给功能函数输入参数
	        JCoParameterList input = function.getImportParameterList();
	        input.setValue("I_MATNR",PN);	 
	        input.setValue("I_WERKS",Factory);
	        function.execute(destination); //函数执行 
	        PartNumber = function.toXML();
	        PartNumber = (String) PartNumber.subSequence(PartNumber.indexOf("<LGPRO>")+7, PartNumber.indexOf("</LGPRO>"));
	        return PartNumber;
		}
    
}
