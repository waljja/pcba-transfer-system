package com.ht.service;

import com.ht.api.CommonResult;
import com.ht.vo.SendRecDataVo;
import com.ht.vo.TotalVo;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @Author 张越
 * @Date 2022
 */
public interface PcbaInventoryService {

    String specialPn(String pn);

    int retryPosting(@Param("BatchId") String BatchId, @Param("ItemId") String ItemId);

    List<Map<String, String>> GetBatchId(@Param("Lot") String Lot, @Param("Type") String Type);

    List<Map<String, Object>> PCBAInventoryData(int pageIndex, int pageSize, List<String> plant1, List<String> workcenter1, List<String> wo1, List<String> partnumber1);

    /**
     * 把数据插入PCBA库存表
     */
    int PcbaStorage(SendRecDataVo SendRecData);

    /**
     * 查询Pcba库存信息
     */
    SendRecDataVo BatchData(String Wo, String Lot);

    /**
     * PCBA报工
     *
     * @param lot      交接单
     * @param location 库位
     * @param user     用户
     * @param node     制程
     * @param factory  工厂
     * @return 结果
     */
    CommonResult<String> workReport(String lot, String location, String user, String node, String factory);

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
    int InventoryStatus(String Lot, String model);

    /**
     * 查询收发料信息
     */
    SendRecDataVo SelFactory(String Lot, String model);

    /**
     * 先进先出
     */
    SendRecDataVo PcbaFIFO(String Lot, String Plant);

    /**
     * COB 收料
     */
    SendRecDataVo RxCobData(String Lot, String Type);

    SendRecDataVo Off_RxCobData(String Lot, String Type);

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

    /**
     * 插入SN明细
     */
    int InsertSN(String Sn, String Lot, String LotQty, String WO, String WERKS, String CreateUser);

    List<Map<String, Object>> FuzzyPn(String Pn, String plant, String workcenter);

    TotalVo Total();

    /**
     * 下载看板数据
     */
    String downloadData(HttpServletResponse response, String StartTime, String EndTime) throws SQLException;

    String UidState(String Lot);

    /**
     * 根据类型查找特权用户
     *
     * @param type 1-发板特权用户 2-绑库特权用户
     * @return 特权用户
     */
    List<String> selPrivilegedUsers(int type);

    /**
     * 校验库位是否合法
     *
     * @param location 库存位置
     * @return true-合法 false-不合法
     */
    boolean isValidLocation(String location);

    String Fifo101(String Lot, String Wo, String Remark,
                                 String node, String factory);

}

