package com.ht.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;

import com.ht.vo.SapClosingTime;
import com.ht.vo.SendRecDataVo;
import com.ht.vo.TotalVo;

/**
 * @Author 张越
 * @Date 2022
 */
public interface PcbaInventoryService {
	String specialPn(String pn);
	int retryPosting(@Param("BatchId")String BatchId,@Param("ItemId")String ItemId);
	List<Map<String,String>> GetBatchId(@Param("Lot")String Lot,@Param("Type")String Type);
	List<Map<String,Object>> PCBAInventoryData(int pageIndex,int pageSize,List<String> plant1,List<String> workcenter1,List<String> wo1,List<String> partnumber1);
	/**
	 * 查询Smt工序批次数据
	 */
//	SendRecDataVo QRYSmtData(String Lot);
//	SendRecDataVo QRYSmtDataNo(String Lot);
	/**
	 * 把数据插入PCBA库存表
	 */
	int PcbaStorage(SendRecDataVo SendRecData);
	/**
	 * 查询Pcba库存信息
	 */
	SendRecDataVo BatchData(String Wo, String Lot);
	/**
	 * 101插入数据
	 */
	int SendSmtplugin101(SendRecDataVo SendRecData);
	/**
	 * 查询批次是否在库存中
	 */
	String InventoryState(String Lot);
	/**
	 * 更改库存表收料状态
	 */
	int UpStatus(String Lot);
	/**
	 * 获取SN明细
	 */
//	List<Map<String,Object>>QRYSmtDataMap(String Lot);
	/**
	 * 存入313过账表
	 */
	int SendSmtplugin313(SendRecDataVo SendRecData);
	/**
	 * 存入smt发料库存表
	 */
	int SendSmtInsert(SendRecDataVo SendRecData);
	/**
	 * 更新批次在库存中状态
	 */
	int InventoryStatus(String Lot,String model);
	/**
	 * 查询收发料信息
	 */
	SendRecDataVo SelFactory(String Lot,String model);
	/**
	 * 先进先出
	 */
	SendRecDataVo PcbaFIFO(String Lot,String Plant);
	/**
	 * COB 收料
	 */
	SendRecDataVo RxCobData(String Lot,String Type);
	SendRecDataVo Off_RxCobData(String Lot,String Type);
	int RxCobplugin315(SendRecDataVo SendRecData);
	int RxSmtInsert315(SendRecDataVo SendRecData);
	int SendCobInsert(SendRecDataVo SendRecData);
	/**
	 * Mi收料
	 */
	int RxCobInsert315(SendRecDataVo SendRecData);
	int SendMiInsert(SendRecDataVo SendRecData);
	int SendMiInsertSpecial(SendRecDataVo SendRecData);
	int PcbaStorageSpecial(SendRecDataVo SendRecData);
	/**
	 * Casing收料
	 */
	int RxMiInsert315(SendRecDataVo SendRecData);
	int SendCasingInsert(SendRecDataVo SendRecData);
	/**
	 * 插入SN明细
	 */
	int InsertSN(String Sn,String Lot,String LotQty,String WO,String WERKS,String CreateUser);
	List<Map<String,Object>> FuzzyPn(String Pn,String plant,String workcenter);
	TotalVo Total();
	/**
	 * 下载看板数据
	 */
	String downloadData(HttpServletResponse response,String StartTime,String EndTime) throws SQLException;
	String UidState(String Lot);
	SapClosingTime SapSuspended();
}

