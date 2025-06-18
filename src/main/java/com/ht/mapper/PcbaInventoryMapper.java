package com.ht.mapper;

import com.ht.entity.LotSn;
import com.ht.vo.SendRecDataVo;
import com.ht.vo.TotalVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PcbaInventoryMapper {

    /**
     * 查找存放位置编码
     *
     * @param locationCode 存放位置编码
     * @return 存放位置编码
     */
    Integer checkLocationCodeExists(String locationCode);

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

    /**
     * 绑库（插入库存表）
     *
     * @param SendRecData
     * @return
     */
    int PcbaStorage(SendRecDataVo SendRecData);

    SendRecDataVo BatchData(@Param("Wo") String Wo, @Param("Lot") String Lot);

    /**
     * 插入101过账数据
     *
     * @param SendRecData
     * @return
     */
    int SendSmtplugin101(SendRecDataVo SendRecData);

    /**
     * 查询批次是否在库存中
     *
     * @param Lot 交接单号
     * @return
     */
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

    SendRecDataVo RxCobData(@Param("Lot") String Lot, @Param("Type") String Type);

    SendRecDataVo Off_RxCobData(@Param("Lot") String Lot, @Param("Type") String Type);

    int RxCobplugin315(SendRecDataVo SendRecData);

    int RxSmtInsert315(SendRecDataVo SendRecData);

    int SendCobInsert(SendRecDataVo SendRecData);

    int RxCobInsert315(SendRecDataVo SendRecData);

    int SendMiInsert(SendRecDataVo SendRecData);

    int SendMiInsertSpecial(SendRecDataVo SendRecData);

    int PcbaStorageSpecial(SendRecDataVo SendRecData);

    /**
     * 批量插入SN记录
     *
     * @param lotSn 批次SN记录
     * @return 插入数量
     */
    int batchInsSnRecords(@Param("lotSnList") List<LotSn> lotSnList);

    int RxMiInsert315(SendRecDataVo SendRecData);

    int SendSmtInsert(SendRecDataVo SendRecData);

    int InsertSN(@Param("Sn") String Sn, @Param("Lot") String Lot, @Param("LotQty") String LotQty, @Param("WO") String WO, @Param("WERKS") String WERKS, @Param("CreateUser") String CreateUser);

    List<Map<String, Object>> FuzzyPn(@Param("Pn") String Pn, @Param("plant") String plant, @Param("workcenter") String workcenter);

    TotalVo Total();

    String UidState(String Lot);

    /**
     * 根据类型查找特权用户
     *
     * @param type 1-发板特权用户 2-绑库特权用户
     * @return 特权用户
     */
    List<String> selPrivilegedUsers(@Param("type") int type);

}
