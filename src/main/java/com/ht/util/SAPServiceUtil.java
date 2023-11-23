package com.ht.util;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

@Slf4j
public class SAPServiceUtil {

    private static final String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL_LEDLIGHT";

    public SAPServiceUtil() {
        try {
            Properties connectProperties = new Properties();
            // SAP服务器
            connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "172.31.2.3");
            // SAP系统编号
            connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "00");
            // SAP集团
            connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "800");
            // SAP用户名
            connectProperties.setProperty(DestinationDataProvider.JCO_USER, "ituser");
            // SAP密码
            connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "itqaz2345");
            // SAP登录语言
            connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "ZH");
            // 最大连接数
            connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "10");
            // 最大连接线程
            connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "20");
            // SAP ROUTER
            // connectProperties.setProperty(DestinationDataProvider.JCO_SAPROUTER, sapConn.getJCO_SAPROUTER());
            createDataFile(connectProperties);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private void createDataFile(Properties properties) {
        File cfg = new File(SAPServiceUtil.ABAP_AS_POOLED + ".jcoDestination");
        if (!cfg.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(cfg, false);
                properties.store(fos, "for tests only !");
                fos.close();
            } catch (Exception e) {
                log.error(e.toString());
                throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
            }
        }
    }

    /**
     * 根据PN，工厂获取工单发板上游库位
     *
     * @param pn   零部件号
     * @param area 厂区
     * @return 工单发板上游库位
     */
    public String getSapPnSendStock(String pn, String area) {
        try {
            String sendStock;
            // 获取连接池
            JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
            // 获取功能函数
            JCoFunction function = destination.getRepository().getFunction("Z_DC_GET_MATERIAL");
            if (function == null) throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
            // 给功能函数输入参数
            JCoParameterList input = function.getImportParameterList();
            input.setValue("I_MATNR", pn);
            input.setValue("I_WERKS", area);
            // 函数执行
            function.execute(destination);
            sendStock = function.toXML();
            sendStock = (String) sendStock.subSequence(sendStock.indexOf("<ZISSLOC>") + 9, sendStock.indexOf("</ZISSLOC>"));
            return sendStock;
        } catch (JCoException | RuntimeException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据PN，工厂获取发出库位
     *
     * @param pn 零部件号
     * @param area 厂区
     * @return 工单发板下游库位
     */
    public String getSapPnRec(String pn, String area) {
        try {
            String partNumber;
            // 获取连接池
            JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
            // 获取功能函数
            JCoFunction function = destination.getRepository().getFunction("Z_DC_GET_MATERIAL");
            if (function == null) throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
            // 给功能函数输入参数
            JCoParameterList input = function.getImportParameterList();
            input.setValue("I_MATNR", pn);
            input.setValue("I_WERKS", area);
            // 函数执行
            function.execute(destination);
            partNumber = function.toXML();
            partNumber = (String) partNumber.subSequence(partNumber.indexOf("<LGPRO>") + 7, partNumber.indexOf("</LGPRO>"));
            return partNumber;
        } catch (JCoException | RuntimeException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }

}
