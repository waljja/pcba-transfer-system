package com.ht.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ht.vo.SapClosingTime;
import com.ht.vo.SendRecDataVo;
import com.ht.vo.TotalVo;

public interface PcbaInventoryMapper {

    /**
     * 特殊型号
     *
     * @param pn
     * @return
     */
    String specialPn(String pn);

    /**
     * 重新过账
     *
     * @param BatchId
     * @param ItemId
     * @return
     */
    int retryPosting(@Param("BatchId") String BatchId, @Param("ItemId") String ItemId);

    List<Map<String, String>> GetBatchId(@Param("Lot") String Lot, @Param("Type") String Type);

    List<Map<String, Object>> PCBAInventoryData(@Param("pageIndex") int pageIndex, @Param("pageSize") int pageSize, @Param("plant1") List<String> plant1, @Param("workcenter1") List<String> workcenter1, @Param("wo1") List<String> wo1, @Param("partnumber1") List<String> partnumber1);

    //查询Smt工序批次数据
//    SendRecDataVo QRYSmtData(String Lot);

    //查询Smt工序批次数据
//    SendRecDataVo QRYSmtDataNo(String Lot);

    /**
     * 绑库（插入库存表）
     *
     * @param SendRecData
     * @return
     */
    int PcbaStorage(SendRecDataVo SendRecData);

    //查询Pcba库存信息
    SendRecDataVo BatchData(@Param("Wo") String Wo, @Param("Lot") String Lot);

    /**
     * 插入101过账数据
     *
     * @param SendRecData
     * @return
     */
    int SendSmtplugin101(SendRecDataVo SendRecData);

    //查询批次是否在库存中
    String InventoryState(String Lot);

    /**
     * 插入313过账数据
     *
     * @param SendRecData
     * @return
     */
    int SendSmtplugin313(SendRecDataVo SendRecData);

    /**
     * 更新批次库存状态
     *
     * @param Lot
     * @param model
     * @return
     */
    int InventoryStatus(@Param("Lot") String Lot, @Param("model") String model);

    //查询收发料信息
    SendRecDataVo SelFactory(@Param("Lot") String Lot, @Param("model") String model);

    /**
     * 先进先出
     *
     * @param Lot
     * @param Plant
     * @return
     */
    SendRecDataVo PcbaFIFO(@Param("Lot") String Lot, @Param("Plant") String Plant);

    /**
     * 更改库存表收、发料状态
     *
     * @param Lot
     * @return
     */
    int UpStatus(String Lot);

//    List<Map<String, Object>> QRYSmtDataMap(String Lot);

    SendRecDataVo RxCobData(@Param("Lot") String Lot, @Param("Type") String Type);

    SendRecDataVo Off_RxCobData(@Param("Lot") String Lot, @Param("Type") String Type);

    int RxCobplugin315(SendRecDataVo SendRecData);

    int RxSmtInsert315(SendRecDataVo SendRecData);

    int SendCobInsert(SendRecDataVo SendRecData);
    //COB 收料

    //Mi收料
    int RxCobInsert315(SendRecDataVo SendRecData);

    int SendMiInsert(SendRecDataVo SendRecData);

    int SendMiInsertSpecial(SendRecDataVo SendRecData);

    int PcbaStorageSpecial(SendRecDataVo SendRecData);
    //Mi收料

    //Casing收料
    int RxMiInsert315(SendRecDataVo SendRecData);

    int SendCasingInsert(SendRecDataVo SendRecData);
    //Casing收料

    //smt发料库存表
    int SendSmtInsert(SendRecDataVo SendRecData);

    //插入SN明细
    int InsertSN(@Param("Sn") String Sn, @Param("Lot") String Lot, @Param("LotQty") String LotQty, @Param("WO") String WO, @Param("WERKS") String WERKS, @Param("CreateUser") String CreateUser);

    List<Map<String, Object>> FuzzyPn(@Param("Pn") String Pn, @Param("plant") String plant, @Param("workcenter") String workcenter);

    TotalVo Total();

    String UidState(String Lot);

    SapClosingTime SapSuspended();

}
