package com.ht.controller;

import com.ht.api.CommonResult;
import com.ht.api.ObSendRecType;
import com.ht.api.ResultCode;
import com.ht.constants.Constants;
import com.ht.entity.InventoryTakeDownEntity;
import com.ht.entity.PCBAInventoryEntity1;
import com.ht.entity.PCBAInventoryExample1;
import com.ht.mapper.InventoryTakeDownMapper;
import com.ht.mapper.PCBAInventoryMapper1;
import com.ht.service.PcbaInventoryService;
import com.ht.util.*;
import com.ht.vo.SendRecDataVo;
import com.ht.vo.TotalVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * PCBA 移交流程处理模块
 *
 * @author 张越
 */
@Slf4j
@CrossOrigin
@RestController
@Api("JavaAPI接口")
@RequestMapping(value = "/api")
public class PcbaInventoryController {

    @Autowired
    PcbaInventoryService PcbaService;
    @Autowired
    PCBAInventoryMapper1 inventoryMapper;
    @Autowired
    InventoryTakeDownMapper inventoryTakeDownMapper;
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test() throws ParseException {
        Map res = restTemplate.getForObject("http://172.31.2.184:5001/api/SysParameters/Get?paramName=MonthEndStartTime", Map.class);
        String start = (String) res.get("data");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = format.parse(start);
        System.out.println(startDate);
    }

    /**
     * 外挂系统重新过账
     *
     * @param Lot  交接单
     * @param Type 过账类型
     */
    @ApiOperation(value = "Lot号重新过账")
    @RequestMapping(value = "/retryPosting", method = RequestMethod.GET)
    public void retryPosting(
            @RequestParam(name = "Lot", defaultValue = "") @ApiParam("交接单") String Lot,
            @RequestParam(name = "Type", defaultValue = "") @ApiParam("过账类型") String Type) {
        String BatchId = "", ItemId = "";
        List<Map<String, String>> list = PcbaService.GetBatchId(Lot, Type);
        for (Map<String, String> map : list) {
            BatchId = map.get("BatchId");
            ItemId = String.valueOf(map.get("ItemId"));
        }
        if (!BatchId.isEmpty() && !ItemId.isEmpty()) {
            log.info("update");
            PcbaService.retryPosting(BatchId, ItemId);
        }
    }

    /**
     * 下载看板数据
     *
     * @param response  HttpServletResponse
     * @param StartTime 开始时间
     * @param EndTime   结束时间
     * @return 返回信息
     */
    @ApiOperation(value = "获取库存报表")
    @RequestMapping(value = "/downloadData", method = RequestMethod.GET)
    public CommonResult<String> downloadData(
            HttpServletResponse response,
            @RequestParam(name = "StartTime", defaultValue = "") @ApiParam("开始时间") String StartTime,
            @RequestParam(name = "EndTime", defaultValue = "") @ApiParam("结束时间") String EndTime) {
        String information;
        try {
            information = PcbaService
                    .downloadData(response, StartTime, EndTime);
            if (information.equals("success")) {
                System.out.println("下载成功！");
            } else {
                System.out.println("下载失败！");
            }
        } catch (SQLException e) {
            log.error(String.valueOf(e));
        }
        return null;
    }

    /**
     * 查型号
     *
     * @param Pn
     * @return
     */
    @ApiOperation(value = "查型号")
    @RequestMapping(value = "/fuzzypn", method = RequestMethod.GET)
    public CommonResult<List<Map<String, Object>>> FuzzyPn(
            @RequestParam(name = "Pn", defaultValue = "") @ApiParam("型号") String Pn,
            @RequestParam(name = "node", defaultValue = "") @ApiParam("节点") String node,
            @RequestParam(name = "factory", defaultValue = "") @ApiParam("工厂") String factory) {
        String state = (node.equals("smt") ? "1" : node.equals("cob") ? "2"
                : node.equals("mi") ? "3" : node.equals("casing") ? "4" : "0");
        List<Map<String, Object>> list = PcbaService
                .FuzzyPn(Pn, factory, state);
        if (!list.isEmpty()) {
            return CommonResult.success(list);
        } else {
            return CommonResult.failed("没有查询到这个型号");
        }
    }

    /**
     * 查询SAP库位信息
     *
     * @param Lot          交接单
     * @param user         用户
     * @param node         制程
     * @param ProductModel 型号
     * @param factory      工厂
     * @return SAP 查出的发出库位
     */
    @ApiOperation(value = "查询发出库位")
    @RequestMapping(value = "/saploc", method = RequestMethod.GET)
    public CommonResult<SendRecDataVo> getSAPPN(
            @RequestParam(name = "Lot", defaultValue = "") @ApiParam("交接单") String Lot,
            @RequestParam(name = "user", defaultValue = "") @ApiParam("用户") String user,
            @RequestParam(name = "node", defaultValue = "") @ApiParam("制程") String node,
            @RequestParam(name = "ProductModel", defaultValue = "") @ApiParam("型号") String ProductModel,
            @RequestParam(name = "factory", defaultValue = "") @ApiParam("工厂") String factory) {
        SAPServiceUtil sapUtil = new SAPServiceUtil();
        SendRecDataVo sap = null;
        if (node.equals("REC")) {
            sap = PcbaService.SelFactory(Lot, "0");
            try {
                if (checkObjFieldIsNotNull(sap)) {
                    sap.setRecLocation(sapUtil.getSapPnSendStock(sap.getPn(),
                            sap.getFactory()));
                    sap.setBatch(sap.getBatch().replaceAll("^0*", ""));
                } else {
                    return CommonResult.failed("没有查询到该Lot号收料数据！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return CommonResult.success(sap);
        } else {
            //判断是否为特殊用户
            if (Constants.isNames(user)) {
                if (!Lot.equals("")) {
                    //Lot不为空则查询Lot数据
                    sap = null;
                    sap = PcbaService.SelFactory(Lot, "1");
                    try {
                        if (checkObjFieldIsNotNull(sap)) {
                            sap.setRecLocation(sapUtil.getSapPnSendStock(sap.getPn(),
                                    sap.getFactory()));
                            sap.setBatch(sap.getBatch().replaceAll("^0*", ""));
                        } else {
                            return CommonResult.failed("没有查询到该Lot号发料数据2！");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (!ProductModel.equals("") || !ProductModel.equals(null)) {
                    sap = null;
                    sap = PcbaService.PcbaFIFO(ProductModel, factory);
                }
                if (checkObjFieldIsNotNull(sap)) {
                    if (!sap.getUID().equals(Lot)) {
                        if (!Lot.equals("")
                                && PcbaService.UidState(Lot).equals("0")) {
                            return CommonResult.failed("此Lot号已扫描，现在应发:"
                                    + sap.getUID() + ",该Lot在:" + sap.getLocation()
                                    + "位置！");
                        } else {
                            return CommonResult.failed(sap.getPn() + "型号应该先发:"
                                    + sap.getUID() + ",该批次在:" + sap.getLocation()
                                    + "位置！");
                        }
                    } else {
                        sap = null;
                        sap = PcbaService.SelFactory(Lot, "1");
                        try {
                            if (checkObjFieldIsNotNull(sap)) {
                                sap.setRecLocation(sapUtil.getSapPnSendStock(sap.getPn(),
                                        sap.getFactory()));
                                sap.setBatch(sap.getBatch().replaceAll("^0*", ""));
                            } else {
                                return CommonResult.failed("没有查询到该Lot号发料数据1！");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return CommonResult.success(sap);
                } else {
                    sap = null;
                    sap = PcbaService.SelFactory(Lot, "1");
                    try {
                        if (checkObjFieldIsNotNull(sap)) {
                            sap.setRecLocation(sapUtil.getSapPnSendStock(sap.getPn(),
                                    sap.getFactory()));
                            sap.setBatch(sap.getBatch().replaceAll("^0*", ""));
                        } else {
                            return CommonResult.failed("没有查询到该Lot号发料数据2！");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return CommonResult.success(sap);
        }
    }

    /**
     * 翻页查询数据
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "翻页查询数据")
    @RequestMapping(value = "/newdata", method = RequestMethod.GET)
    public CommonResult<List<Map<String, Object>>> PCBAInventoryData(
            @RequestParam(name = "pageIndex", defaultValue = "") @ApiParam("当前页") int pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "") @ApiParam("每页条数") int pageSize,
            @RequestParam(name = "plant", defaultValue = "") @ApiParam("工厂") String plant,
            @RequestParam(name = "workcenter", defaultValue = "") @ApiParam("工作中心") String workcenter,
            @RequestParam(name = "wo", defaultValue = "") @ApiParam("工单") String wo,
            @RequestParam(name = "partnumber", defaultValue = "") @ApiParam("PN") String partnumber) {
        String[] symbol = {"//", "[", "]", "\""};
        List<String> plant1;
        List<String> workcenter1;
        List<String> wo1;
        List<String> partnumber1;
        plant = getSubString(plant, symbol);
        workcenter = getSubString(workcenter, symbol);
        wo = getSubString(wo, symbol);
        partnumber = getSubString(partnumber, symbol);
        if (plant.equals("null") || plant.equals("")) {
            plant1 = Arrays.asList();
        } else {
            plant1 = Arrays.asList(plant.split(","));
        }
        if (workcenter.equals("null") || workcenter.equals("")) {
            workcenter1 = Arrays.asList();
        } else {
            workcenter1 = Arrays.asList(workcenter.split(","));
        }
        if (wo.equals("null") || wo.equals("")) {
            wo1 = Arrays.asList();
        } else {
            wo1 = Arrays.asList(wo.split(","));
        }
        if (partnumber.equals("null") || partnumber.equals("")) {
            partnumber1 = Arrays.asList();
        } else {
            partnumber1 = Arrays.asList(partnumber.split(","));
        }
        TotalVo totalVo = PcbaService.Total();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        pageIndex = pageIndex == 0 ? 1 : pageIndex;
        List<Map<String, Object>> data = PcbaService.PCBAInventoryData(
                pageIndex, pageSize, plant1, workcenter1, wo1, partnumber1);
        for (Map<String, Object> map : data) {
            String nowMs = format.format(map.get("createtime"));
            map.put("createtime", nowMs);
        }

        return CommonResult.success(totalVo.getTotal(), totalVo.getSmttotal(),
                totalVo.getCobtotal(), totalVo.getMitotal(), data);
    }

    /**
     * 入库 101 过账
     *
     * @param Lot      交接单
     * @param Location 库位
     * @param User     用户
     * @param node     制程
     * @param factory  工厂
     * @return 101 过账结果
     */
    @ApiOperation(value = "101入库")
    @RequestMapping(value = "/Pcba101", method = RequestMethod.GET)
    public CommonResult<String> Pcba101(
            @RequestParam(name = "Lot", defaultValue = "") @ApiParam("交接单") String Lot,
            @RequestParam(name = "Location", defaultValue = "") @ApiParam("库位") String Location,
            @RequestParam(name = "user", defaultValue = "") @ApiParam("用户") String User,
            @RequestParam(name = "node", defaultValue = "") @ApiParam("制程") String node,
            @RequestParam(name = "factory", defaultValue = "") @ApiParam("工厂") String factory) {
        Con72DB con72db = new Con72DB();
        Con75DB con75db = new Con75DB();
        Con51DB con51db = new Con51DB();
        Con182HR con182HR = new Con182HR();
        int state1;
        int state2;
        int state3;
        String Pn;
        ResultSet rs;
        List list = new ArrayList<>();
        if (node.equals("smt")) {
            if (factory.equals("B1")) {
                // Orbit打印数据
                SendRecDataVo data = new SendRecDataVo();
                try {
                    rs = con72db.executeQuery(SqlApi.SelLotData(Lot));
                    if (rs.next()) {
                        if (!rs.getString("Pn").startsWith("620")) {
                            return CommonResult.failed("请用对应账号做101入库");
                        }
                        data.setUID(rs.getString("Batch"));
                        data.setQty(rs.getString("Qty"));
                        data.setWo(rs.getString("Wo"));
                        data.setWoQty(rs.getString("WoQty"));
                        if (rs.getString("Pn").endsWith("00DR3") || rs.getString("Pn").contains("00DR1")) { //以00DR3结尾的只能入BS51仓
                            if (Location.equals("BS51")) {
                                data.setSendLocation(Location);
                            } else {
                                return CommonResult.failed("5000工厂的型号只能入BS51仓");
                            }
                        } else {
                            if (Location.equals("BS81")) {
                                data.setSendLocation(Location);
                            } else {
                                return CommonResult.failed("B1 1100工厂的型号只能入BS81仓");
                            }
                        }
                        data.setRecLocation(rs.getString("RecLocation").trim());
                        data.setPn(rs.getString("Pn"));
                        data.setWorkcenter("1");
                        data.setUser(User);
                        data.setBatch(rs
                                .getString("Batch")
                                .subSequence(13, rs.getString("Batch").length())
                                .toString());
                        data.setFactory(data.getPn().contains("00DR1") || data.getPn().contains("00DR3") ? "5000" : "1100");
                        synchronized (this) {
                            state1 = PcbaService.SendSmtplugin101(data);
                        }
                        if (state1 > 0) {
                            log.info(data.getUID() + "put success!");
                            rs = con72db.executeQuery(SqlApi.SelSmtSnData(Lot));
                            // 获取列数
                            int count = rs.getMetaData().getColumnCount();
                            long start = System.currentTimeMillis();
                            while (rs.next()) {
                                Map map = new HashMap<>();
                                for (int i = 1; i <= count; i++) {
                                    Object value = rs.getObject(i);
                                    Object key = rs.getMetaData()
                                            .getColumnName(i);
                                    map.put(key, value);
                                }
                                list.add(map);
                            }
                            // 循环生成 SQL 后一次性提交
                            String batchSql = "";
                            log.info(String.valueOf(list.size()));
                            for (Object o : list) {
                                String b = o.toString();
                                batchSql += SqlApi.insSn(
                                        b.substring(29, b.length() - 1),
                                        b.substring(5, 24),
                                        data.getQty(),
                                        data.getWo(),
                                        data.getFactory(),
                                        User);
                            }
                            if (!batchSql.isEmpty()) {
                                int isInserted = con182HR.executeUpdate(batchSql);
                                if (isInserted != 0) {
                                    log.info(Lot + " 批量插入 65 SN表 成功");
                                } else {
                                    log.info(Lot + " 批量插入 65 SN表 失败！！！");
                                }
                            } else {
                                log.info(Lot + " Orbit 无 SN 数据！！！");
                            }
                            con72db.close();
                            long end = System.currentTimeMillis();
                            log.info("循环插入SN表耗时: " + (end - start) + "ms");
                            return CommonResult.success("SMT101入库成功");
                        } else {
                            con72db.close();
                            return CommonResult.failed("该Lot号已做过101入库(Smt测试)！");
                        }
                    } else {
                        return CommonResult.failed("没有查询到对应Lot号数据");
                    }
                } catch (Exception e) {
                    con72db.close();
                    log.error(String.valueOf(e));
                }
            } else {
                SendRecDataVo data = new SendRecDataVo();
                try {
                    rs = con51db.executeQuery(SqlApi.SelLotData(Lot));
                    if (rs.next()) {
                        if (!rs.getString("Pn").startsWith("620")) {
                            return CommonResult.failed("请用对应账号做101入库");
                        }
                        data.setUID(rs.getString("Batch"));
                        data.setQty(rs.getString("Qty"));
                        data.setWo(rs.getString("Wo"));
                        data.setWoQty(rs.getString("WoQty"));
                        /*
                         * 以00DR3、00DR1结尾的只能入BS51仓
                         * 以00R3、00R1结尾的只能入BS87仓
                         */
                        if (rs.getString("Pn").endsWith("00DR3") || rs.getString("Pn").contains("00DR1")) {
                            if (Location.equals("BS51")) {
                                data.setSendLocation(Location);
                            } else {
                                return CommonResult.failed("5000工厂的型号只能入BS51仓");
                            }
                        } else if (rs.getString("Pn").endsWith("00R3") || rs.getString("Pn").endsWith("00R1")) {
                            if (Location.equals("BS87")) {
                                data.setSendLocation(Location);
                            } else {
                                return CommonResult.failed("B2 1100工厂的型号只能入BS87仓");
                            }
                        } else {
                            data.setSendLocation((!Location.isEmpty() ? Location : rs.getString("sendLocation").trim()));
                        }
                        data.setRecLocation(rs.getString("RecLocation").trim());
                        data.setPn(rs.getString("Pn"));
                        data.setWorkcenter("1");
                        data.setUser(User);
                        data.setBatch(rs
                                .getString("Batch")
                                .subSequence(13, rs.getString("Batch").length())
                                .toString());
                        data.setFactory(data.getPn().contains("00DR1") || data.getPn().contains("00DR3") ? "5000" : "1100");
                        synchronized (this) {
                            state1 = PcbaService.SendSmtplugin101(data);
                        }
                        if (state1 > 0) {
                            log.info(data.getUID() + "put success!");
                            rs = con51db.executeQuery(SqlApi.SelSmtSnData(Lot));
                            // 获取列数
                            int count = rs.getMetaData().getColumnCount();
                            while (rs.next()) {
                                Map map = new HashMap<>();
                                for (int i = 1; i <= count; i++) {
                                    Object value = rs.getObject(i);
                                    Object key = rs.getMetaData()
                                            .getColumnName(i);
                                    map.put(key, value);
                                }
                                list.add(map);
                            }
                            for (Object o : list) {
                                String b = o.toString();
                                PcbaService.InsertSN(
                                        b.substring(29, b.length() - 1),
                                        b.substring(5, 24), data.getQty(),
                                        data.getWo(), data.getFactory(), User);
                            }
                            con51db.close();
                            return CommonResult.success("SMT101入库成功");
                        } else {
                            con51db.close();
                            return CommonResult.failed("该Lot号已做过101入库(Smt)！");
                        }
                    } else {
                        return CommonResult.failed("没有查询到对应Lot号数据");
                    }
                } catch (Exception e) {
                    con51db.close();
                    log.error(String.valueOf(e));
                }
            }
        } else if (node.equals("cob")) {
            SendRecDataVo data = new SendRecDataVo();
            try {
                if (factory.equals("B1")) {
                    rs = con72db.executeQuery(SqlApi.SelLotData(Lot));
                } else {
                    rs = con51db.executeQuery(SqlApi.SelLotData(Lot));
                }
                if (rs.next()) {
                    if (!rs.getString("Pn").startsWith("610")) {
                        return CommonResult.failed("请用对应账号做101入库");
                    }
                    data.setUID(rs.getString("Batch"));
                    data.setQty(rs.getString("Qty"));
                    data.setWo(rs.getString("Wo"));
                    data.setWoQty(rs.getString("WoQty"));
                    data.setFactory(rs.getString("Factory"));
                    data.setSendLocation((!Location.isEmpty() ? Location : rs.getString("sendLocation").trim()));
                    data.setRecLocation(rs.getString("RecLocation").trim());
                    data.setPn(rs.getString("Pn"));
                    data.setWorkcenter("2");
                    data.setUser(User);
                    data.setPlant(factory);
                    data.setBatch(rs.getString("Batch")
                            .subSequence(13, rs.getString("Batch").length())
                            .toString());
                    String flag = Fifo101(Lot, data.getWo(), "PCBA CNC分板下线",
                            "", factory);
                    if (flag.equals("true")) {
                        synchronized (this) {
                            state1 = PcbaService.SendSmtplugin101(data);
                        }
                        if (state1 > 0) {
                            log.info(data.getUID() + "put success!");
                            state2 = PcbaService.PcbaStorage(data);
                            if (state2 > 0) {
                                rs = con72db.executeQuery(SqlApi
                                        .SelCobSnData(Lot));
                                int count = rs.getMetaData().getColumnCount();// 获取列数
                                while (rs.next()) {
                                    Map map = new HashMap<>();
                                    for (int i = 1; i <= count; i++) {
                                        Object value = rs.getObject(i);
                                        Object key = rs.getMetaData()
                                                .getColumnName(i);
                                        map.put(key, value);
                                    }
                                    list.add(map);
                                }
                                for (Object o : list) {
                                    String b = o.toString();
                                    PcbaService.InsertSN(
                                            b.substring(29, b.length() - 1),
                                            b.substring(5, 24), data.getQty(),
                                            data.getWo(), data.getFactory(),
                                            User);
                                }
                                con72db.close();
                                return CommonResult.success("Cob101入库成功");
                            }
                        } else {
                            con72db.close();
                            con51db.close();
                            return CommonResult.failed("该Lot号已做过101入库(Cob)！");
                        }
                    } else {
                        return CommonResult.failed(flag);
                    }
                } else {
                    return CommonResult.failed("没有查询到对应Lot号数据");
                }
            } catch (Exception e) {
                con72db.close();
                con51db.close();
                log.error(String.valueOf(e));
            }
        } else if (node.equals("mi")) {
            SendRecDataVo data = new SendRecDataVo();
            try {
                if (factory.equals("B1")) {
                    rs = con72db.executeQuery(SqlApi.SelLotData(Lot));
                    if (!rs.isBeforeFirst()) {
                        rs = con75db.executeQuery(SqlApi.SelLotData(Lot));
                    }
                } else {
                    rs = con51db.executeQuery(SqlApi.SelLotData(Lot));
                    if (!rs.isBeforeFirst()) {
                        rs = con75db.executeQuery(SqlApi.SelLotData(Lot));
                    }
                }
                if (rs.next()) {
                    if (!rs.getString("Pn").startsWith("64")) {
                        return CommonResult.failed("请用对应账号做101入库");
                    }
                    data.setUID(rs.getString("Batch"));
                    data.setQty(rs.getString("Qty"));
                    data.setWo(rs.getString("Wo"));
                    data.setWoQty(rs.getString("WoQty"));
                    data.setFactory(rs.getString("Factory"));
                    data.setSendLocation(Location);
                    data.setRecLocation(rs.getString("RecLocation").trim());
                    data.setPn(rs.getString("Pn"));
                    data.setWorkcenter("3");
                    data.setUser(User);
                    data.setPlant(factory);
                    data.setBatch(rs.getString("Batch")
                            .subSequence(13, rs.getString("Batch").length())
                            .toString());
                    String flag = Fifo101(Lot, data.getWo(), "", "", factory);
                    if (flag.equals("true")) {
                        Pn = PcbaService
                                .specialPn(data.getPn().substring(0, 8));
                        if (Pn != null) {
                            System.err.println("特殊PN：" + Lot);
                            // 做101入库(插入过账表)
                            synchronized (this) {
                                state1 = PcbaService.SendSmtplugin101(data);
                            }
                            if (state1 > 0) {
                                log.info(data.getUID() + "put success!");
                                state2 = PcbaService.SendMiInsertSpecial(data);
                                state3 = PcbaService.PcbaStorageSpecial(data);
                                if (state2 > 0 && state3 > 0) {
                                    if (factory.equals("B1")) {
                                        rs = con72db.executeQuery(SqlApi
                                                .SelMiSnData(Lot));
                                        if (!rs.isBeforeFirst()) {
                                            rs = con75db.executeQuery(SqlApi
                                                    .SelMiSnData(Lot));
                                        }
                                    } else {
                                        rs = con51db.executeQuery(SqlApi
                                                .SelMiSnData(Lot));
                                    }
                                    int count = rs.getMetaData()
                                            .getColumnCount();// 获取列数
                                    while (rs.next()) {
                                        Map map = new HashMap<>();
                                        for (int i = 1; i <= count; i++) {
                                            Object value = rs.getObject(i);
                                            Object key = rs.getMetaData()
                                                    .getColumnName(i);
                                            map.put(key, value);
                                        }
                                        list.add(map);
                                    }
                                    for (Object o : list) {
                                        String b = o.toString();
                                        PcbaService
                                                .InsertSN(
                                                        b.substring(29,
                                                                b.length() - 1),
                                                        b.substring(5, 24),
                                                        data.getQty(),
                                                        data.getWo(),
                                                        data.getFactory(), User);
                                    }
                                    con72db.close();
                                    con51db.close();
                                    con75db.close();
                                    return CommonResult.success("MI101入库成功");
                                }
                            } else {
                                return CommonResult
                                        .failed("该Lot号已做过101入库(Mi)！");
                            }

                        } else {
                            synchronized (this) {
                                state1 = PcbaService.SendSmtplugin101(data);
                            }
                            if (state1 > 0) {
                                log.info(data.getUID() + "put success!");
                                state2 = PcbaService.PcbaStorage(data);
                                if (state2 > 0) {
                                    if (factory.equals("B1")) {
                                        rs = con72db.executeQuery(SqlApi
                                                .SelMiSnData(Lot));
                                    } else {
                                        rs = con51db.executeQuery(SqlApi
                                                .SelMiSnData(Lot));
                                    }
                                    int count = rs.getMetaData()
                                            .getColumnCount();// 获取列数
                                    while (rs.next()) {
                                        Map map = new HashMap<>();
                                        for (int i = 1; i <= count; i++) {
                                            Object value = rs.getObject(i);
                                            Object key = rs.getMetaData()
                                                    .getColumnName(i);
                                            map.put(key, value);
                                        }
                                        list.add(map);
                                    }
                                    for (Object o : list) {
                                        String b = o.toString();
                                        PcbaService
                                                .InsertSN(
                                                        b.substring(29,
                                                                b.length() - 1),
                                                        b.substring(5, 24),
                                                        data.getQty(),
                                                        data.getWo(),
                                                        data.getFactory(), User);
                                    }
                                    con72db.close();
                                    con51db.close();
                                    return CommonResult.success("MI101入库成功");
                                }
                            } else {
                                return CommonResult
                                        .failed("该Lot号已做过101入库(Mi)！");
                            }
                        }
                    } else {
                        return CommonResult.failed(flag);
                    }
                } else {
                    return CommonResult.failed("没有查询到对应Lot号数据");
                }
            } catch (Exception e) {
                con72db.close();
                con51db.close();
                log.error(String.valueOf(e));
            }
        } else if (node.equals("casing")) {
            SendRecDataVo data = new SendRecDataVo();
            try {
                if (factory.equals("B1")) {
                    rs = con72db.executeQuery(SqlApi.SelLotData(Lot));
                } else {
                    rs = con51db.executeQuery(SqlApi.SelLotData(Lot));
                }
                if (rs.next()) {
                    if (!rs.getString("Pn").startsWith("65")) {
                        return CommonResult.failed("请用对应账号做101入库");
                    }
                    data.setUID(rs.getString("Batch"));
                    data.setQty(rs.getString("Qty"));
                    data.setWo(rs.getString("Wo"));
                    data.setWoQty(rs.getString("WoQty"));
                    data.setFactory(rs.getString("Factory"));
                    data.setSendLocation(Location);
                    data.setRecLocation(rs.getString("RecLocation").trim());
                    data.setPn(rs.getString("Pn"));
                    data.setWorkcenter("4");
                    data.setUser(User);
                    data.setPlant(factory);
                    data.setBatch(rs.getString("Batch")
                            .subSequence(13, rs.getString("Batch").length())
                            .toString());
                    synchronized (this) {
                        state1 = PcbaService.SendSmtplugin101(data);
                    }
                    if (state1 > 0) {
                        log.info(data.getUID() + "put success!");
                        state2 = PcbaService.PcbaStorage(data);
                        if (state2 > 0) {
                            if (factory.equals("B1")) {
                                rs = con72db.executeQuery(SqlApi
                                        .SelCasingSnData(Lot));
                            } else {
                                rs = con51db.executeQuery(SqlApi
                                        .SelCasingSnData(Lot));
                            }
                            int count = rs.getMetaData().getColumnCount();// 获取列数
                            while (rs.next()) {
                                Map map = new HashMap<>();
                                for (int i = 1; i <= count; i++) {
                                    Object value = rs.getObject(i);
                                    Object key = rs.getMetaData()
                                            .getColumnName(i);
                                    map.put(key, value);
                                }
                                list.add(map);
                            }
                            for (int i = 0; i < list.size(); i++) {
                                String b = list.get(i).toString();
                                PcbaService.InsertSN(
                                        b.substring(29, b.length() - 1),
                                        b.substring(5, 24), data.getQty(),
                                        data.getWo(), data.getFactory(), User);
                            }
                            con72db.close();
                            con51db.close();
                            return CommonResult.success("Casing101入库成功");
                        }
                    } else {
                        return CommonResult.failed("该Lot号已做过101入库(Casing)！");
                    }
                } else {
                    return CommonResult.failed("没有查询到对应Lot号数据");
                }
            } catch (Exception e) {
                con72db.close();
                con51db.close();
                e.printStackTrace();
            }
        }
        return CommonResult.failed("未入库成功！");
    }

    /**
     * 发板 313 过账
     *
     * @param Lot     交接单
     * @param User    用户
     * @param node    制程
     * @param factory 工厂
     * @param Model   模式
     * @return 过账结果
     * @throws Exception
     */
    @ApiOperation(value = "将Pcba板发送到下一工序")
    @RequestMapping(value = "/sendPcba", method = RequestMethod.GET)
    public CommonResult<String> sendPcba(
            @RequestParam(name = "Lot", defaultValue = "") @ApiParam("交接单") String Lot,
            @RequestParam(name = "user", defaultValue = "") @ApiParam("用户") String User,
            @RequestParam(name = "node", defaultValue = "") @ApiParam("制程") String node,
            @RequestParam(name = "factory", defaultValue = "") @ApiParam("工厂") String factory,
            @RequestParam(name = "Model", defaultValue = "") @ApiParam("模式") Boolean Model)
            throws Exception {
        int State1;
        int state2;
        int state3;
        SendRecDataVo SendRecData;
        SAPServiceUtil sapUtil = new SAPServiceUtil();
        // 获取SAP月结时间
        Map sRes = restTemplate.getForObject("http://172.31.2.184:5001/api/SysParameters/Get?paramName=MonthEndStartTime", Map.class);
        Map eRes = restTemplate.getForObject("http://172.31.2.184:5001/api/SysParameters/Get?paramName=MonthEndEndTime", Map.class);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = (String) sRes.get("data");
        String end = (String) eRes.get("data");
        Date startDate = format.parse(start);
        Date endDate = format.parse(end);
        switch (node) {
            case "smt":
                if (Model) {
                    if (getDate(startDate, endDate)) {
                        SendRecData = PcbaService.RxCobData(Lot, "101");
                    } else {
                        SendRecData = PcbaService.Off_RxCobData(Lot, "101");
                    }
                } else {
                    SendRecData = PcbaService.Off_RxCobData(Lot, "101");
                }
                if (checkObjFieldIsNotNull(SendRecData)) {
                    State1 = PcbaService.InventoryStatus(Lot, "0");
                    if (State1 > 0) {
                        SendRecData = PcbaService.BatchData(Lot.substring(0, Lot.indexOf("/")), Lot);
                        if (checkObjFieldIsNotNull(SendRecData)) {
                            SendRecData.setUser(User);
                            SendRecData.setRecLocation(sapUtil.getSapPnSendStock(
                                    SendRecData.getPn(), SendRecData.getFactory()));
                            state2 = PcbaService.SendSmtInsert(SendRecData);
                            synchronized (this) {
                                state3 = PcbaService.SendSmtplugin313(SendRecData);
                            }
                            if (state3 > 0) {
                                log.info(SendRecData.getUID() + "sending success!");
                            }
                            if (state2 > 0 && state3 > 0) {
                                InsertOb(Lot, User, ObSendRecType.SMTSEND.getTypeName(), true);
                                return CommonResult.success("SMT发板成功!");
                            } else {
                                return CommonResult.failed("该Lot号已发过板(Smt)！");
                            }
                        } else {
                            return CommonResult.failed("没有查询到该Lot号发料数据(Smt)1！");
                        }
                    } else {
                        return CommonResult.failed("没有查询到该Lot号发料数据(Smt)2！");
                    }
                } else {
                    return CommonResult.failed("该Lot号101入库SAP过账不成功，请在外挂系统检查原因！");
                }
            case "cob":
                if (Model) {
                    if (getDate(startDate, endDate)) {
                        SendRecData = PcbaService.RxCobData(Lot, "101");
                    } else {
                        SendRecData = PcbaService.Off_RxCobData(Lot, "101");
                    }
                } else {
                    SendRecData = PcbaService.Off_RxCobData(Lot, "101");
                }
                if (checkObjFieldIsNotNull(SendRecData)) {
                    int state = PcbaService.InventoryStatus(Lot, "0");
                    if (state > 0) {
                        SendRecData = PcbaService.BatchData(Lot.substring(0, Lot.indexOf("/")),
                                Lot);
                        if (checkObjFieldIsNotNull(SendRecData)) {
                            SendRecData.setUser(User);
                            SendRecData.setRecLocation(sapUtil.getSapPnSendStock(
                                    SendRecData.getPn(), SendRecData.getFactory()));
                            State1 = PcbaService.SendCobInsert(SendRecData);
                            synchronized (this) {
                                state2 = PcbaService.SendSmtplugin313(SendRecData);
                            }
                            if (state2 > 0) {
                                log.info(SendRecData.getUID() + "sending success!");
                            }
                            if (State1 > 0 && state2 > 0) {
                                InsertOb(Lot, User, ObSendRecType.COBSEND.getTypeName(), true);
                                return CommonResult.success("COB发料成功");
                            } else {
                                return CommonResult.failed("该Lot号已发过板(Cob)！");
                            }
                        } else {
                            return CommonResult.failed("没有查询到该Lot号发料数据(Cob)1！");
                        }
                    } else {
                        return CommonResult.failed("没有查询到该Lot号发料数据(Cob)2！");
                    }
                } else {
                    return CommonResult.failed("该Lot号101入库SAP过账不成功，请在外挂系统检查原因！");
                }
            case "mi":
                if (Model) {
                    if (getDate(startDate, endDate)) {
                        SendRecData = PcbaService.RxCobData(Lot, "101");
                    } else {
                        SendRecData = PcbaService.Off_RxCobData(Lot, "101");
                    }
                } else {
                    SendRecData = PcbaService.Off_RxCobData(Lot, "101");
                }
                if (checkObjFieldIsNotNull(SendRecData)) {
                    int state = PcbaService.InventoryStatus(Lot, "0");
                    if (state > 0) {
                        SendRecData = PcbaService.BatchData(Lot.substring(0, Lot.indexOf("/")),
                                Lot);
                        if (checkObjFieldIsNotNull(SendRecData)) {
                            SendRecData.setUser(User);
                            SendRecData.setRecLocation(sapUtil.getSapPnSendStock(
                                    SendRecData.getPn(), SendRecData.getFactory()));
                            State1 = PcbaService.SendMiInsert(SendRecData);
                            synchronized (this) {
                                state2 = PcbaService.SendSmtplugin313(SendRecData);
                            }
                            if (state2 > 0) {
                                log.info(SendRecData.getUID() + "sending success!");
                            }
                            if (State1 > 0 && state2 > 0) {
                                InsertOb(Lot, User, ObSendRecType.MISEND.getTypeName(), true);
                                return CommonResult.success("MI发料成功");
                            } else {
                                return CommonResult.failed("该Lot号已发过板(Mi)！");
                            }
                        } else {
                            return CommonResult.failed("没有查询到该Lot号发料数据(Mi)1！");
                        }
                    } else {
                        return CommonResult.failed("没有查询到该Lot号发料数据(Mi)2！");
                    }
                } else {
                    return CommonResult.failed("该Lot号101入库SAP过账不成功，请在外挂系统检查原因！");
                }
        }
        return CommonResult.failed("未发板成功！");
    }

    /**
     * 插入库存表信息
     *
     * @param Lot      UID
     * @param Location 库位
     * @param UserName 用户名
     * @param node     制程
     * @param Model    模式
     * @return 返回信息
     */
    @ApiOperation(value = "将Pcba板信息插入库存表")
    @RequestMapping(value = "/insertpcba", method = RequestMethod.GET)
    public CommonResult<String> pcbaStorage(
            @RequestParam(name = "Lot", defaultValue = "") @ApiParam("批号") String Lot,
            @RequestParam(name = "Location", defaultValue = "") @ApiParam("位置") String Location,
            @RequestParam(name = "UserName", defaultValue = "") @ApiParam("用户名") String UserName,
            @RequestParam(name = "node", defaultValue = "") @ApiParam("节点") String node,
            @RequestParam(name = "Model", defaultValue = "") @ApiParam("模式") Boolean Model,
            @RequestParam(name = "factory", defaultValue = "") @ApiParam("工厂") String factory) throws ParseException {
        int state1;
        int state3 = 0;
        SendRecDataVo SendRecData;
        // 获取SAP月结时间
        Map sRes = restTemplate.getForObject("http://172.31.2.184:5001/api/SysParameters/Get?paramName=MonthEndStartTime", Map.class);
        Map eRes = restTemplate.getForObject("http://172.31.2.184:5001/api/SysParameters/Get?paramName=MonthEndEndTime", Map.class);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = (String) sRes.get("data");
        String end = (String) eRes.get("data");
        Date startDate = format.parse(start);
        Date endDate = format.parse(end);
        switch (node) {
            case "smt":
                try {
                    String state = PcbaService.InventoryState(Lot);
                    if (Objects.equals(state, "") || state == null) { // 不在库存中（未经过101入库）
                        if (Constants.isAccount(UserName)) {//判断是否是特殊用户
                            return BindingLocation(Lot, Location, UserName,
                                    Model, factory);
                        } else { // 普通用户
                            String flag = Fifo101(Lot, Lot.substring(0, Lot.indexOf("/")), "", node,
                                    factory);
                            if (flag.equals("true")) {
                                return BindingLocation(Lot, Location, UserName,
                                        Model, factory);
                            } else {
                                return CommonResult.failed(flag);
                            }
                        }
                    } else { // 工单未执行过绑库
                        return BindingLocation(Lot, Location, UserName,
                                Model, factory);
                    }
                } catch (SQLException e) {
                    log.error(String.valueOf(e));
                }
                break;
            case "cob":
                if (Model) {
                    if (getDate(startDate, endDate)) {
                        SendRecData = PcbaService.RxCobData(Lot, "313");
                    } else {
                        SendRecData = PcbaService.Off_RxCobData(Lot, "313");
                    }
                } else {
                    SendRecData = PcbaService.Off_RxCobData(Lot, "313");
                }
                if (checkObjFieldIsNotNull(SendRecData)) {
                    if (SendRecData.getRecLocation().equals("BS80")) {
                        SendRecData.setBatch(SendRecData.getUID()
                                .subSequence(13, SendRecData.getUID().length())
                                .toString());
                        SendRecData.setUser(UserName);
                        synchronized (this) {
                            state1 = PcbaService.RxCobplugin315(SendRecData);
                        }
                        state3 = PcbaService.RxSmtInsert315(SendRecData);
                        if (state1 > 0 && state3 > 0) {
                            PcbaService.UpStatus(Lot);
                            InsertOb(Lot, UserName, ObSendRecType.COBREC.getTypeName(), false);
                            return CommonResult.success("Cob收料成功");
                        } else {
                            return CommonResult.failed("该Lot号已经收料(COB)！");
                        }

                    } else {
                        return CommonResult.failed("COB账号只能收发往COB的型号");
                    }
                } else {
                    return CommonResult.failed("该Lot号313发板SAP过账不成功，请在外挂系统检查原因！");
                }
            case "mi":
                if (Model) {
                    if (getDate(startDate, endDate)) {
                        SendRecData = PcbaService.RxCobData(Lot, "313");
                    } else {
                        SendRecData = PcbaService.Off_RxCobData(Lot, "313");
                    }
                } else {
                    SendRecData = PcbaService.Off_RxCobData(Lot, "313");
                }
                if (checkObjFieldIsNotNull(SendRecData)) {
                    if (SendRecData.getRecLocation().equals("BS82")
                            || SendRecData.getRecLocation().equals("BS8G")
                            || SendRecData.getRecLocation().equals("BS5E")) {
                        SendRecData.setBatch(SendRecData.getUID()
                                .subSequence(13, SendRecData.getUID().length())
                                .toString());
                        SendRecData.setUser(UserName);
                        if (SendRecData.getSendLocation().equals("BS81")
                                || SendRecData.getSendLocation().equals("BS87") || SendRecData.getSendLocation().equals("BS51")) {
                            state3 = PcbaService.RxSmtInsert315(SendRecData);
                        } else if (SendRecData.getSendLocation().equals("BS80")) {
                            state3 = PcbaService.RxCobInsert315(SendRecData);
                        }
                        synchronized (this) {
                            state1 = PcbaService.RxCobplugin315(SendRecData);
                        }
                        if (state1 > 0) {
                            log.info(SendRecData.getUID() + "receiving success!");
                        }
                        if (state1 > 0 && state3 > 0) {
                            PcbaService.UpStatus(Lot);
                            InsertOb(Lot, UserName, ObSendRecType.MIREC.getTypeName(), false);
                            return CommonResult.success("Mi收料成功");
                        } else {
                            return CommonResult.failed("该Lot号已经收料(Mi)！");
                        }
                    } else {
                        return CommonResult.failed("MI账号只能收发往MI的型号");
                    }
                } else {
                    return CommonResult.failed("该Lot号313发板SAP过账不成功，请在外挂系统检查原因！");
                }
            case "casing":
                if (Model) {
                    if (getDate(startDate, endDate)) {
                        SendRecData = PcbaService.RxCobData(Lot, "313");
                    } else {
                        SendRecData = PcbaService.Off_RxCobData(Lot, "313");
                    }
                } else {
                    SendRecData = PcbaService.Off_RxCobData(Lot, "313");
                }
                if (checkObjFieldIsNotNull(SendRecData)) {
                    if (SendRecData.getRecLocation().equals("BS83")
                            || SendRecData.getRecLocation().equals("BS8E")
                            || SendRecData.getRecLocation().equals("BS8A")
                            || SendRecData.getRecLocation().equals("BS8G")
                            || SendRecData.getRecLocation().equals("BS5C")
                            || SendRecData.getRecLocation().equals("BS5D")
                            || SendRecData.getRecLocation().equals("BS5E")
                            || SendRecData.getRecLocation().equals("BS53")) {
                        SendRecData.setBatch(SendRecData.getUID()
                                .subSequence(13, SendRecData.getUID().length())
                                .toString());
                        SendRecData.setUser(UserName);
                        switch (SendRecData.getSendLocation()) {
                            case "BS81":
                            case "BS87":
                            case "BS51":
                                state3 = PcbaService.RxSmtInsert315(SendRecData);
                                break;
                            case "BS80":
                                state3 = PcbaService.RxCobInsert315(SendRecData);
                                break;
                            case "BS82":
                            case "BS8G":
                                state3 = PcbaService.RxMiInsert315(SendRecData);
                                break;
                        }
                        synchronized (this) {
                            state1 = PcbaService.RxCobplugin315(SendRecData);
                        }
                        if (state1 > 0) {
                            log.info(SendRecData.getUID() + "receiving success!");
                        }
                        if (state1 > 0 && state3 > 0) {
                            PcbaService.UpStatus(Lot);
                            InsertOb(Lot, UserName, ObSendRecType.CASINGREC.getTypeName(), false);
                            return CommonResult.success("Casing收料成功");
                        } else {
                            return CommonResult.failed("该Lot号已经收料(Casing)！");
                        }
                    } else {
                        return CommonResult.failed("Casing账号只能收发往Casing的型号");
                    }
                } else {
                    return CommonResult.failed("该Lot号313发板SAP过账不成功，请在外挂系统检查原因！");
                }
            case "MiCasing":
                if (Model) {
                    if (getDate(startDate, endDate)) {
                        SendRecData = PcbaService.RxCobData(Lot, "313");
                    } else {
                        SendRecData = PcbaService.Off_RxCobData(Lot, "313");
                    }
                } else {
                    SendRecData = PcbaService.Off_RxCobData(Lot, "313");
                }
                if (checkObjFieldIsNotNull(SendRecData)) {
                    if (SendRecData.getRecLocation().equals("BS82")
                            || SendRecData.getRecLocation().equals("BS8G")) {
                        SendRecData.setBatch(SendRecData.getUID()
                                .subSequence(13, SendRecData.getUID().length())
                                .toString());
                        SendRecData.setUser(UserName);
                        if (SendRecData.getSendLocation().equals("BS81")
                                || SendRecData.getSendLocation().equals("BS87")) {
                            state3 = PcbaService.RxSmtInsert315(SendRecData);
                        } else if (SendRecData.getSendLocation().equals("BS80")) {
                            state3 = PcbaService.RxCobInsert315(SendRecData);
                        }
                        synchronized (this) {
                            state1 = PcbaService.RxCobplugin315(SendRecData);
                        }
                        if (state1 > 0) {
                            log.info(SendRecData.getUID() + "receiving success!");
                        }
                        if (state1 > 0 && state3 > 0) {
                            PcbaService.UpStatus(Lot);
                            return CommonResult.success("Mi收料成功");
                        } else {
                            return CommonResult.failed("该Lot号已经收料(Mi)！");
                        }
                    } else {
                        return CommonResult.failed("MI账号只能收发往MI的型号");
                    }
                } else {
                    return CommonResult.failed("没有查询到该Lot号收料数据(Mi)！");
                }
        }
        return CommonResult.failed("未收料成功！");
    }

    /**
     * 根据Lot号下架库存
     *
     * @param Lot    交接单
     * @param userID 工号
     * @return 下架结果
     */
    @ApiOperation(value = "库存下架")
    @RequestMapping(value = "/inventoryTakeDown", method = RequestMethod.GET)
    public CommonResult<String> inventoryTakeDown(@RequestParam(name = "Lot", defaultValue = "")
                                                  @ApiParam("交接单") String Lot,
                                                  @RequestParam(name = "userID", defaultValue = "")
                                                  @ApiParam("工号") String userID) {
        List<PCBAInventoryEntity1> inventories;
        // 根据Lot查询库存信息
        PCBAInventoryExample1 inventoryExample = new PCBAInventoryExample1();
        inventoryExample.createCriteria().andUIDEqualTo(Lot).andStateEqualTo(1);
        inventories = inventoryMapper.selectByExample(inventoryExample);
        // 显示在看板上的才能下架
        if (!inventories.isEmpty()) {
            PCBAInventoryEntity1 inventory1 = new PCBAInventoryEntity1();
            inventory1.setState(3);
            PCBAInventoryExample1 inventoryExample1 = new PCBAInventoryExample1();
            inventoryExample1.createCriteria().andUIDEqualTo(Lot).andStateEqualTo(1);
            // 下架库存
            int isUpdated = inventoryMapper.updateByExampleSelective(inventory1, inventoryExample1);
            if (isUpdated != 0) {
                // 查询已下架的Lot（用于新增下架记录）
                PCBAInventoryExample1 inventoryExample2 = new PCBAInventoryExample1();
                inventoryExample2.createCriteria().andUIDEqualTo(Lot).andStateEqualTo(3);
                inventories = inventoryMapper.selectByExample(inventoryExample2);
                /*
                 * 插入下架记录
                 */
                PCBAInventoryEntity1 inventory = inventories.get(0);
                InventoryTakeDownEntity takeDown = new InventoryTakeDownEntity();
                // 把查出来的入库记录直接映射到下架对象中
                BeanUtils.copyProperties(inventory, takeDown);
                Date date = new Date();
                // 获取当前系统时间
                date.setTime(System.currentTimeMillis());
                // 设置成当前下架操作人
                takeDown.setCreateUser(userID);
                // 设置下架时间
                takeDown.setCreateTime(date);
                int isInserted = inventoryTakeDownMapper.insertSelective(takeDown);
                if (isInserted != 0) {
                    return CommonResult.success(ResultCode.TAKEDOWN_SUCCESS);
                } else {
                    return CommonResult.failed("新增库存下架记录失败！");
                }
            } else {
                return CommonResult.failed("库存下架失败！");
            }
        } else {
            return CommonResult.failed("Lot不在库存中！无需下架");
        }
    }

    public static String Fifo101(String Lot, String Wo, String Remark,
                                 String node, String factory) {
        String flag = "";
        Con72DB con72db = new Con72DB();
        Con75DB con75db = new Con75DB();
        Con51DB con51db = new Con51DB();
        Con182HR con182HR = new Con182HR();
        ResultSet rs1, rs2;
        try {
            if (factory.equals("B1")) {
                if (node.equals("smt")) {
                    rs1 = con182HR.executeQuery(SqlApi.findLastBatchByWo(Wo));// 查询入库表
                    /*
                     * 已入库 -> 提示下一个
                     * 未入库 -> 提示工单第一个
                     */
                    if (rs1.next()) {
                        // Aegis打印表数据源
                        /*rs2 = con100hr.executeQuery(SqlApi.SmtObFifo(
                                rs1.getString("UID"), Wo));// 找到按顺序应该入库的下一个SN号
                        if (rs2.next()) {
                            if (Lot.equals(rs2.getString("NewSN"))) {
                                flag = "true";
                            } else {
                                flag = "根据FIFO管控，请先绑定:"
                                        + rs2.getString("NewSN") + "号";
                            }
                        } else {
                        }*/
                        rs1 = con182HR.executeQuery(SqlApi.fifoByBatch(Wo));
                        if (rs1.next()) { // 判断PCBA库存是否有数据
                            rs2 = con72db.executeQuery(SqlApi.obFifo(
                                    rs1.getString("UID"), Wo, Remark));

                            if (!rs2.isBeforeFirst()) {//判断51OB数据是否能查到数据
                                rs2 = con75db.executeQuery(SqlApi.obFifo(
                                        rs1.getString("UID"), Wo, Remark));
                            }

                            if (rs2.next()) {
                                if (Lot.equals(rs2.getString("FGSN"))) {
                                    flag = "true";
                                } else {
                                    flag = "根据FIFO管控，请先入库:" + rs2.getString("FGSN")
                                            + "号";
                                }
                            } else {
                                flag = "此Lot已扫描，占时没有查询到需要101入库的Lot号";
                            }
                        } else {
                            rs1 = con72db.executeQuery(SqlApi.obFirst(Wo, Remark));

                            if (!rs1.isBeforeFirst()) {
                                rs1 = con75db.executeQuery(SqlApi.obFirst(Wo, Remark));
                            }

                            if (rs1.next()) {
                                if (rs1.getString("FGSN").equals(Lot)) {
                                    flag = "true";
                                } else {
                                    flag = "根据FIFO管控，请先入库:" + rs1.getString("FGSN")
                                            + "号";
                                }
                            } else {
                                flag = "Ob中没有查询到此Lot号!2";
                            }
                        }
                    } else {
                        /*rs1 = con182HR.executeQuery(SqlApi.SmtObFirst(Wo));// 查询工单的第一个SN
                        if (rs1.next()) {
                            if (rs1.getString("NewSN").equals(Lot)) {// 根据传入的Lot判断是否是第一个SN
                                flag = "true";
                            } else {
                                flag = "根据FIFO管控，请先绑定:"
                                        + rs1.getString("NewSN") + "号";
                            }
                        } else {
                        }*/
                        rs1 = con182HR.executeQuery(SqlApi.fifoByBatch(Wo));
                        if (rs1.next()) { // 判断PCBA库存是否有数据
                            rs2 = con72db.executeQuery(SqlApi.obFifo(
                                    rs1.getString("UID"), Wo, Remark));

                            if (!rs2.isBeforeFirst()) {//判断72OB数据是否能查到数据
                                rs2 = con75db.executeQuery(SqlApi.obFifo(
                                        rs1.getString("UID"), Wo, Remark));
                            }

                            if (rs2.next()) {
                                if (Lot.equals(rs2.getString("FGSN"))) {
                                    flag = "true";
                                } else {
                                    flag = "根据FIFO管控，请先入库:" + rs2.getString("FGSN")
                                            + "号";
                                }
                            } else {
                                flag = "此Lot已扫描，占时没有查询到需要101入库的Lot号";
                            }
                        } else {
                            rs1 = con72db.executeQuery(SqlApi.obFirst(Wo, Remark));

                            if (!rs1.isBeforeFirst()) {
                                rs1 = con75db.executeQuery(SqlApi.obFirst(Wo, Remark));
                            }

                            if (rs1.next()) {
                                if (rs1.getString("FGSN").equals(Lot)) {
                                    flag = "true";
                                } else {
                                    flag = "根据FIFO管控，请先入库:" + rs1.getString("FGSN")
                                            + "号";
                                }
                            } else {
                                flag = "Ob中没有查询到此Lot号!2";
                            }
                        }
                    }
                } else {
                    rs1 = con182HR.executeQuery(SqlApi.fifoByBatch(Wo));
                    if (rs1.next()) {
                        rs2 = con72db.executeQuery(SqlApi.obFifo(
                                rs1.getString("UID"), Wo, Remark));
                        if (!rs2.isBeforeFirst()) {
                            rs2 = con75db.executeQuery(SqlApi.obFifo(
                                    rs1.getString("UID"), Wo, Remark));
                        }
                        if (rs2.next()) {
                            if (Lot.equals(rs2.getString("FGSN"))) {
                                flag = "true";
                            } else {
                                flag = "根据FIFO管控，请先入库:" + rs2.getString("FGSN")
                                        + "号";
                            }
                        } else {
                            flag = "此Lot已扫描，占时没有查询到需要101入库的Lot号";
                        }
                    } else {
                        rs1 = con72db.executeQuery(SqlApi
                                .obFirst(Wo, Remark));
                        if (!rs1.isBeforeFirst()) {
                            rs1 = con75db.executeQuery(SqlApi
                                    .obFirst(Wo, Remark));
                        }
                        if (rs1.next()) {
                            if (rs1.getString("FGSN").equals(Lot)) {
                                flag = "true";
                            } else {
                                flag = "根据FIFO管控，请先入库:" + rs1.getString("FGSN")
                                        + "号";
                            }
                        } else {
                            flag = "Ob中没有查询到此Lot号!1";
                        }
                    }
                }
            } else { // B2
                rs1 = con182HR.executeQuery(SqlApi.fifoByBatch(Wo));

                if (rs1.next()) { // 判断PCBA库存是否有数据
                    rs2 = con51db.executeQuery(SqlApi.obFifo(
                            rs1.getString("UID"), Wo, Remark));

                    if (!rs2.isBeforeFirst()) {//判断51OB数据是否能查到数据
                        rs2 = con75db.executeQuery(SqlApi.obFifo(
                                rs1.getString("UID"), Wo, Remark));
                    }

                    if (rs2.next()) {
                        if (Lot.equals(rs2.getString("FGSN"))) {
                            flag = "true";
                        } else {
                            flag = "根据FIFO管控，请先入库:" + rs2.getString("FGSN")
                                    + "号";
                        }
                    } else {
                        flag = "此Lot已扫描，占时没有查询到需要101入库的Lot号";
                    }
                } else {
                    rs1 = con51db.executeQuery(SqlApi.obFirst(Wo, Remark));

                    if (!rs1.isBeforeFirst()) {
                        rs1 = con75db.executeQuery(SqlApi.obFirst(Wo, Remark));
                    }

                    if (rs1.next()) {
                        if (rs1.getString("FGSN").equals(Lot)) {
                            flag = "true";
                        } else {
                            flag = "根据FIFO管控，请先入库:" + rs1.getString("FGSN")
                                    + "号";
                        }
                    } else {
                        flag = "Ob中没有查询到此Lot号!2";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

    public CommonResult<String> BindingLocation(String Lot, String Location,
                                                String UserName, Boolean Model, String factory)
            throws SQLException, ParseException {
        int state1;
        int state3;
        String state2;
        ResultSet rs;
        ResultCode result;
        SendRecDataVo SendRecData;
        Con51DB con51db = new Con51DB();
        Con72DB Con72DB = new Con72DB();
        // 获取SAP月结时间
        Map sRes = restTemplate.getForObject("http://172.31.2.184:5001/api/SysParameters/Get?paramName=MonthEndStartTime", Map.class);
        Map eRes = restTemplate.getForObject("http://172.31.2.184:5001/api/SysParameters/Get?paramName=MonthEndEndTime", Map.class);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = (String) sRes.get("data");
        String end = (String) eRes.get("data");
        Date startDate = format.parse(start);
        Date endDate = format.parse(end);
        if (Model) {
            if (getDate(startDate, endDate)) {
                SendRecData = PcbaService.RxCobData(Lot, "101");
                if (!checkObjFieldIsNotNull(SendRecData)) {
                    result = ResultCode.TRANSFERWASUNSUCCESSFUL101;
                    return CommonResult.failed(result);
                }
            } else {
                SendRecData = PcbaService.Off_RxCobData(Lot, "101");
                if (!checkObjFieldIsNotNull(SendRecData)) {
                    result = ResultCode.THEREISNOPOSTING101;
                    return CommonResult.failed(result);
                }
            }
        } else {
            SendRecData = PcbaService.Off_RxCobData(Lot, "101");
            if (!checkObjFieldIsNotNull(SendRecData)) {
                result = ResultCode.THEREISNOPOSTING101;
                return CommonResult.failed(result);
            }
        }
        // 查询批号是否在库存中
        state2 = PcbaService.InventoryState(Lot);
        if (factory.equals("B1")) {
            /*
             * 未绑库 -> 绑库
             * 已绑库 -> 移库
             */
            if (Objects.equals(state2, "") || state2 == null) {
                // B2 测试工单
                rs = Con72DB.executeQuery(SqlApi.SelLotData(Lot));
                SendRecDataVo SendRecData1 = new SendRecDataVo();
                if (rs.next()) {
                    if (!rs.getString("Pn").startsWith("620")) {
                        return CommonResult.failed("SMT账号只能绑SMT的型号");
                    }
                    SendRecData1.setWo(rs.getString("Wo"));
                    SendRecData1.setWoQty(rs.getString("WoQty"));
                    SendRecData1.setPn(rs.getString("Pn"));
                    SendRecData1.setQty(rs.getString("Qty"));
                    SendRecData1.setBatch(rs.getString("Batch")
                            .subSequence(14, rs.getString("Batch").length())
                            .toString());
                    SendRecData1.setUser(UserName);
                    SendRecData1.setFactory(rs.getString("Factory"));
                    SendRecData1.setWorkcenter("1");
                    SendRecData1.setUID(rs.getString("Batch"));
                    SendRecData1.setSendLocation(Location);
                    SendRecData1.setPlant(factory);
                }
                // 此Lot未绑库
                if (Objects.equals(state2, "") || state2 == null) {
                    if (checkObjFieldIsNotNull(SendRecData1)) {
                        SendRecData1.setUser(UserName);
                        // 插入库存
                        state1 = PcbaService.PcbaStorage(SendRecData1);
                        if (state1 > 0) {
                            return CommonResult.success("SMT保存到库存表成功!");
                        } else {
                            return CommonResult.failed("绑库失败！");
                        }
                    } else {
                        return CommonResult.failed("未查询到相关批次数据，请确认批次是否有错！");
                    }
                } else {
                    // 移库记录
                    state3 = PcbaService.InventoryStatus(Lot, "2");
                    if (state3 > 0) {
                        System.out.println(SendRecData1.getUID());
                        if (checkObjFieldIsNotNull(SendRecData1)) {
                            SendRecData1.setUser(UserName);
                            state1 = PcbaService.PcbaStorage(SendRecData1);
                            if (state1 > 0) {
                                return CommonResult.success("SMT保存到库存表成功!");
                            } else {
                                return CommonResult.failed("绑库失败！");
                            }
                        } else {
                            return CommonResult.failed("未查询到相关批次数据，请确认批次是否有错！");
                        }
                    } else {
                        return CommonResult.failed("重新绑库失败！");
                    }
                }
            } else {
                state3 = PcbaService.InventoryStatus(Lot, "2");
                if (state3 > 0) {
                    // B2
                    rs = Con72DB.executeQuery(SqlApi.SelLotData(Lot));
                    SendRecDataVo SendRecData1 = new SendRecDataVo();
                    if (rs.next()) {
                        if (!rs.getString("Pn").startsWith("620")) {
                            return CommonResult.failed("SMT账号只能绑SMT的型号");
                        }
                        SendRecData1.setWo(rs.getString("Wo"));
                        SendRecData1.setWoQty(rs.getString("WoQty"));
                        SendRecData1.setPn(rs.getString("Pn"));
                        SendRecData1.setQty(rs.getString("Qty"));
                        SendRecData1.setBatch(rs.getString("Batch")
                                .subSequence(14, rs.getString("Batch").length())
                                .toString());
                        SendRecData1.setUser(UserName);
                        SendRecData1.setFactory(rs.getString("Factory"));
                        SendRecData1.setWorkcenter("1");
                        SendRecData1.setUID(rs.getString("Batch"));
                        SendRecData1.setSendLocation(Location);
                        SendRecData1.setPlant(factory);
                    }
                    // 此Lot未绑库
                    if (checkObjFieldIsNotNull(SendRecData1)) {
                        SendRecData1.setUser(UserName);
                        state1 = PcbaService.PcbaStorage(SendRecData1);
                        if (state1 > 0) {
                            return CommonResult.success("SMT保存到库存表成功!");
                        } else {
                            return CommonResult.failed("绑库失败！");
                        }
                    } else {
                        return CommonResult.failed("未查询到相关批次数据，请确认批次是否有错！");
                    }
                } else {
                    return CommonResult.failed("重新绑库失败！");
                }
            }
        } else {
            // B2
            rs = con51db.executeQuery(SqlApi.SelLotData(Lot));
            SendRecDataVo SendRecData1 = new SendRecDataVo();
            if (rs.next()) {
                if (!rs.getString("Pn").startsWith("620")) {
                    return CommonResult.failed("SMT账号只能绑SMT的型号");
                }
                SendRecData1.setWo(rs.getString("Wo"));
                SendRecData1.setWoQty(rs.getString("WoQty"));
                SendRecData1.setPn(rs.getString("Pn"));
                SendRecData1.setQty(rs.getString("Qty"));
                SendRecData1.setBatch(rs.getString("Batch")
                        .subSequence(14, rs.getString("Batch").length())
                        .toString());
                SendRecData1.setUser(UserName);
                SendRecData1.setFactory(rs.getString("Factory"));
                SendRecData1.setWorkcenter("1");
                SendRecData1.setUID(rs.getString("Batch"));
                SendRecData1.setSendLocation(Location);
                SendRecData1.setPlant(factory);
            }
            // 此Lot未绑库
            if (Objects.equals(state2, "") || state2 == null) {
                if (checkObjFieldIsNotNull(SendRecData1)) {
                    SendRecData1.setUser(UserName);
                    // 插入库存
                    state1 = PcbaService.PcbaStorage(SendRecData1);
                    if (state1 > 0) {
                        return CommonResult.success("SMT保存到库存表成功!");
                    } else {
                        return CommonResult.failed("绑库失败！");
                    }
                } else {
                    return CommonResult.failed("未查询到相关批次数据，请确认批次是否有错！");
                }
            } else {
                // 移库记录
                state3 = PcbaService.InventoryStatus(Lot, "2");
                if (state3 > 0) {
                    if (checkObjFieldIsNotNull(SendRecData1)) {
                        SendRecData1.setUser(UserName);
                        state1 = PcbaService.PcbaStorage(SendRecData1);
                        if (state1 > 0) {
                            return CommonResult.success("SMT保存到库存表成功!");
                        } else {
                            return CommonResult.failed("绑库失败！");
                        }
                    } else {
                        return CommonResult.failed("未查询到相关批次数据，请确认批次是否有错！");
                    }
                } else {
                    return CommonResult.failed("重新绑库失败！");
                }
            }
        }
    }

    public static void InsertOb(String Lot, String User, String ObType, Boolean state) {
        Con75DB con75db = new Con75DB();
        Con51DB con51db = new Con51DB();
        Con72DB con72db = new Con72DB();
        ResultSet rs = null;
        String Sql = state ? SqlApi.UpObSend(Lot, User, ObType) : SqlApi.UpObRec(Lot, User, ObType);//true：发料 ，false:收料

        try {
            rs = con72db.executeQuery(SqlApi.SelLotData(Lot));

            if (!rs.isBeforeFirst()) {//判断OB是否有数据
                rs = con51db.executeQuery(SqlApi.SelLotData(Lot));

                if (!rs.isBeforeFirst()) {//判断OB是否有数据
                    rs = con75db.executeQuery(SqlApi.SelLotData(Lot));

                    if (rs.isBeforeFirst()) {//判断OB是否有数据
                        con75db.executeUpdate(Sql);
                    }
                } else {
                    con51db.executeUpdate(Sql);
                }
            } else {
                con72db.executeUpdate(Sql);
            }

            con51db.close();
            con72db.close();
            con75db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkObjFieldIsNotNull(Object obj) { // true 不为空 false
        boolean flag = false;
        try {
            for (Field f : obj.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(obj) == null || f.get(obj) == "") {
                } else {
                    flag = true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return flag;
    }

    public static boolean getDate(Date date1, Date date2) {
        Calendar date = Calendar.getInstance();
        boolean flag = true;
        date.setTime(new Date());
        // 获取开始时间
        Calendar begin = Calendar.getInstance();
        begin.setTime(date1);
        // 获取结束时间
        Calendar end = Calendar.getInstance();
        end.setTime(date2);
        if ((date.after(begin) && date.before(end))
                || (date.getTime() == begin.getTime() || date.getTime() == end
                .getTime())) {
            flag = false;
        }
        return flag;
    }

    public static String getSubString(String str1, String[] str2) {
        StringBuffer sb = new StringBuffer(str1);
        for (int i = 0; i < str2.length; i++) {
            while (true) {
                int index = sb.indexOf(str2[i]);
                if (index == -1) {
                    break;
                }
                sb.delete(index, index + 1);
            }
        }

        return sb.toString();
    }
}
