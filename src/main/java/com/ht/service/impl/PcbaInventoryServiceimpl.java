package com.ht.service.impl;

import com.ht.api.CommonResult;
import com.ht.entity.LotSn;
import com.ht.mapper.PcbaInventoryMapper;
import com.ht.service.PcbaInventoryService;
import com.ht.service.TransactionService;
import com.ht.utils.*;
import com.ht.vo.SendRecDataVo;
import com.ht.vo.TotalVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Service
public class PcbaInventoryServiceimpl implements PcbaInventoryService {

    private final TransactionService transactionService;
    private final PcbaInventoryMapper mapper;

    public PcbaInventoryServiceimpl(TransactionService transactionService, PcbaInventoryMapper mapper) {
        this.transactionService = transactionService;
        this.mapper = mapper;
    }

    /**
     * 把数据插入PCBA库存表
     */
    @Override
    public int PcbaStorage(SendRecDataVo SendRecData) {
        return mapper.PcbaStorage(SendRecData);
    }

    /**
     * 查询Pcba库存信息
     */
    @Override
    public SendRecDataVo BatchData(String Wo, String Lot) {
        return mapper.BatchData(Wo, Lot);
    }

    @Override
    public CommonResult<String> workReport(String lot, String location, String user, String node, String factory) {
        Con72DB con72db = new Con72DB();
        Con75DB con75db = new Con75DB();
        Con51DB con51db = new Con51DB();
        try {
            SendRecDataVo data = new SendRecDataVo();
            String batch = lot.subSequence(13, lot.length()).toString();
            List<LotSn> lotSnList;
            data.setUID(lot);
            data.setUser(user);
            data.setBatch(batch);
            data.setPlant(factory);
            switch (node) {
                case "smt":
                    data.setWorkcenter("1");
                    if (factory.equals("B1")) {
                        try (ResultSet rs = con72db.executeQuery(SqlApi.SelLotData(lot))) {
                            if (rs.next()) {
                                if (!rs.getString("Pn").startsWith("620")) {
                                    return CommonResult.failed("请用对应账号做101入库");
                                }
                                data.setQty(rs.getString("Qty"));
                                data.setWo(rs.getString("Wo"));
                                data.setWoQty(rs.getString("WoQty"));
                                // 以00DR3结尾的只能入BS51仓
                                if (rs.getString("Pn").endsWith("00DR3") || rs.getString("Pn").contains("00DR1")) {
                                    if (location.equals("BS51")) {
                                        data.setSendLocation(location);
                                    } else {
                                        return CommonResult.failed("5000工厂的型号只能入BS51仓");
                                    }
                                } else {
                                    if (location.equals("BS81")) {
                                        data.setSendLocation(location);
                                    } else {
                                        return CommonResult.failed("B1 1100工厂的型号只能入BS81仓");
                                    }
                                }
                                data.setRecLocation(rs.getString("RecLocation").trim());
                                data.setPn(rs.getString("Pn"));
                                data.setFactory(data.getPn().contains("00DR1") || data.getPn().contains("00DR3") ? "5000" : "1100");
                                try (ResultSet rs1 = con72db.executeQuery(SqlApi.SelSmtSnData(lot))) {
                                    lotSnList = extractSnRecords(rs1, data);
                                }
                                boolean isWorkReportSuccess = transactionService.workReportTransaction(data, lotSnList);
                                if (isWorkReportSuccess) {
                                    return CommonResult.success("SMT101入库成功");
                                } else {
                                    return CommonResult.failed("该Lot号已做过101入库(Smt)！");
                                }
                            } else {
                                return CommonResult.failed("没有查询到对应Lot号数据");
                            }
                        }
                    } else {
                        try (ResultSet rs = con51db.executeQuery(SqlApi.SelLotData(lot))) {
                            if (rs.next()) {
                                if (!rs.getString("Pn").startsWith("620")) {
                                    return CommonResult.failed("请用对应账号做101入库");
                                }
                                data.setQty(rs.getString("Qty"));
                                data.setWo(rs.getString("Wo"));
                                data.setWoQty(rs.getString("WoQty"));
                                /*
                                 * 以00DR3、00DR1结尾的只能入BS51仓
                                 * 以00R3、00R1结尾的只能入BS87仓
                                 */
                                if (rs.getString("Pn").endsWith("00DR3") || rs.getString("Pn").contains("00DR1")) {
                                    if (location.equals("BS51")) {
                                        data.setSendLocation(location);
                                    } else {
                                        return CommonResult.failed("5000工厂的型号只能入BS51仓");
                                    }
                                } else if (rs.getString("Pn").endsWith("00R3") || rs.getString("Pn").endsWith("00R1")) {
                                    if (location.equals("BS87")) {
                                        data.setSendLocation(location);
                                    } else {
                                        return CommonResult.failed("B2 1100工厂的型号只能入BS87仓");
                                    }
                                } else {
                                    data.setSendLocation((!location.isEmpty() ? location : rs.getString("sendLocation").trim()));
                                }
                                data.setRecLocation(rs.getString("RecLocation").trim());
                                data.setPn(rs.getString("Pn"));
                                data.setFactory(data.getPn().contains("00DR1") || data.getPn().contains("00DR3") ? "5000" : "1100");
                                try (ResultSet rs1 = con51db.executeQuery(SqlApi.SelSmtSnData(lot))) {
                                    lotSnList = extractSnRecords(rs1, data);
                                }
                                boolean isWorkReportSuccess = transactionService.workReportTransaction(data, lotSnList);
                                if (isWorkReportSuccess) {
                                    return CommonResult.success("SMT101入库成功");
                                } else {
                                    return CommonResult.failed("该Lot号已做过101入库(Smt)！");
                                }
                            } else {
                                return CommonResult.failed("没有查询到对应Lot号数据");
                            }
                        }
                    }
                case "cob":
                    data.setWorkcenter("2");
                    try (ResultSet rs = factory.equals("B1") ? con72db.executeQuery(SqlApi.SelLotData(lot)) :
                            con51db.executeQuery(SqlApi.SelLotData(lot))) {
                        if (rs.next()) {
                            if (!rs.getString("Pn").startsWith("610")) {
                                return CommonResult.failed("请用对应账号做101入库");
                            }
                            data.setQty(rs.getString("Qty"));
                            data.setWo(rs.getString("Wo"));
                            data.setWoQty(rs.getString("WoQty"));
                            data.setFactory(rs.getString("Factory"));
                            data.setSendLocation((!location.isEmpty() ? location : rs.getString("sendLocation").trim()));
                            data.setRecLocation(rs.getString("RecLocation").trim());
                            data.setPn(rs.getString("Pn"));
                            String flag = Fifo101(lot, data.getWo(), "PCBA CNC分板下线",
                                    "", factory);
                            if (flag.equals("true")) {
                                try (ResultSet rs1 = con72db.executeQuery(SqlApi.SelCobSnData(lot))) {
                                    lotSnList = extractSnRecords(rs1, data);
                                }
                                boolean isWorkReportSuccess = transactionService.workReportAndInventoryIn(data, lotSnList);
                                if (isWorkReportSuccess) {
                                    return CommonResult.success("COB101入库成功");
                                } else {
                                    return CommonResult.failed("该Lot号已做过101入库(Cob)！");
                                }
                            } else {
                                return CommonResult.failed(flag);
                            }
                        } else {
                            return CommonResult.failed("没有查询到对应Lot号数据");
                        }
                    }
                case "mi":
                    data.setWorkcenter("3");
                    Map<String, Object> lotDetail;
                    if (factory.equals("B1")) {
                        // 尝试从 con72db 查询
                        try (ResultSet rsTemp = con72db.executeQuery(SqlApi.SelLotData(lot))) {
                            if (rsTemp != null && rsTemp.next()) {
                                lotDetail = extractRow(rsTemp);
                            } else {
                                // 第一次无数据，尝试从 con75db 查询
                                try (ResultSet rsTemp1 = con75db.executeQuery(SqlApi.SelLotData(lot))) {
                                    if (rsTemp1 != null && rsTemp1.next()) {
                                        lotDetail = extractRow(rsTemp1);
                                    } else {
                                        return CommonResult.failed("没有查询到对应Lot号数据");
                                    }
                                }
                            }
                        }
                    } else {
                        // 尝试从 con51db 查询
                        try (ResultSet rsTemp = con51db.executeQuery(SqlApi.SelLotData(lot))) {
                            if (rsTemp != null && rsTemp.next()) {
                                lotDetail = extractRow(rsTemp);
                            } else {
                                // 第一次无数据，尝试从 con75db 查询
                                try (ResultSet rsTemp1 = con75db.executeQuery(SqlApi.SelLotData(lot))) {
                                    if (rsTemp1 != null && rsTemp1.next()) {
                                        lotDetail = extractRow(rsTemp1);
                                    } else {
                                        return CommonResult.failed("没有查询到对应Lot号数据");
                                    }
                                }
                            }
                        }
                    }
                    if (!CollectionUtils.isEmpty(lotDetail)) {
                        if (!lotDetail.get("Pn").toString().startsWith("64")) {
                            return CommonResult.failed("请用对应账号做101入库");
                        }
                        data.setQty(String.valueOf(lotDetail.get("Qty")));
                        data.setWo(String.valueOf(lotDetail.get("Wo")));
                        data.setWoQty(String.valueOf(lotDetail.get("WoQty")));
                        data.setFactory(String.valueOf(lotDetail.get("Factory")));
                        data.setSendLocation(location);
                        data.setPn(String.valueOf(lotDetail.get("Pn")));
                        String flag = Fifo101(lot, data.getWo(), "", "", factory);
                        if (flag.equals("true")) {
                            String pn;
                            pn = specialPn(data.getPn().substring(0, 8));
                            if (pn != null) {
                                log.info("特殊PN：" + lot);
                                List<Map<String, Object>> snDataList = new ArrayList<>();
                                if (factory.equals("B1")) {
                                    try (ResultSet rs1 = con72db.executeQuery(SqlApi.SelMiSnData(lot))) {
                                        if (rs1 != null && rs1.next()) {
                                            do {
                                                snDataList.add(extractRow(rs1));
                                            } while (rs1.next());
                                        } else {
                                            // 尝试从 con75db 查询
                                            try (ResultSet rsTemp = con75db.executeQuery(SqlApi.SelMiSnData(lot))) {
                                                if (rsTemp != null && rsTemp.next()) {
                                                    do {
                                                        snDataList.add(extractRow(rsTemp));
                                                    } while (rsTemp.next());
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    try (ResultSet rs1 = con51db.executeQuery(SqlApi.SelMiSnData(lot))) {
                                        if (rs1 != null && rs1.next()) {
                                            do {
                                                snDataList.add(extractRow(rs1));
                                            } while (rs1.next());
                                        }
                                    }
                                }
                                lotSnList = convertToLotSnList(snDataList, data);
                                boolean isWorkReportSuccess = transactionService.workReportAndInventoryInSpecial(data, lotSnList);
                                if (isWorkReportSuccess) {
                                    return CommonResult.success("MI101入库成功");
                                } else {
                                    return CommonResult.failed("该Lot号已做过101入库(MI)！");
                                }
                            } else {
                                List<LotSn> lotSnList1 = new ArrayList<>();
                                if (factory.equals("B1")) {
                                    try (ResultSet rs1 = con72db.executeQuery(SqlApi.SelMiSnData(lot))) {
                                        if (rs1 != null && rs1.next()) {
                                            lotSnList1= extractSnRecords(rs1, data);
                                        } else {
                                            // 尝试从 con75db 查询
                                            try (ResultSet rsTemp = con75db.executeQuery(SqlApi.SelMiSnData(lot))) {
                                                if (rsTemp != null && rsTemp.next()) {
                                                    lotSnList1 = extractSnRecords(rsTemp, data);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    try (ResultSet rs1 = con51db.executeQuery(SqlApi.SelMiSnData(lot))) {
                                        if (rs1 != null && rs1.next()) {
                                            lotSnList1 = extractSnRecords(rs1, data);
                                        }
                                    }
                                }
                                boolean isWorkReportSuccess = transactionService.workReportAndInventoryIn(data, lotSnList1);
                                if (isWorkReportSuccess) {
                                    return CommonResult.success("MI101入库成功");
                                } else {
                                    return CommonResult.failed("该Lot号已做过101入库(MI)！");
                                }
                            }
                        } else {
                            return CommonResult.failed(flag);
                        }
                    } else {
                        return CommonResult.failed("没有查询到对应Lot号数据");
                    }
            }
        } catch (Exception e) {
            log.error("{} 报工异常：", lot);
            log.error(e.toString());
            log.error(String.valueOf(e.getCause()));
            log.error(e.getLocalizedMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        } finally {
            con51db.close();
            con72db.close();
            con75db.close();
        }
        return CommonResult.failed("未入库成功！");
    }

    /**
     * 101插入数据
     */
    @Override
    public int SendSmtplugin101(SendRecDataVo SendRecData) {
        return mapper.SendSmtplugin101(SendRecData);
    }

    @Override
    public String InventoryState(String Lot) {
        return mapper.InventoryState(Lot);
    }

    @Override
    public int SendSmtplugin313(SendRecDataVo SendRecData) {
        return mapper.SendSmtplugin313(SendRecData);
    }

    @Override
    public int SendSmtInsert(SendRecDataVo SendRecData) {
        return mapper.SendSmtInsert(SendRecData);
    }

    @Override
    public int InventoryStatus(String Lot, String model) {
        return mapper.InventoryStatus(Lot, model);
    }

    @Override
    public SendRecDataVo SelFactory(String Lot, String model) {
        return mapper.SelFactory(Lot, model);
    }

    @Override
    public SendRecDataVo PcbaFIFO(String Lot, String Plant) {
        return mapper.PcbaFIFO(Lot, Plant);
    }

    @Override
    public SendRecDataVo RxCobData(String Lot, String Type) {
        return mapper.RxCobData(Lot, Type);
    }

    @Override
    public SendRecDataVo Off_RxCobData(String Lot, String Type) {
        return mapper.Off_RxCobData(Lot, Type);
    }

    @Override
    public int RxCobplugin315(SendRecDataVo SendRecData) {
        return mapper.RxCobplugin315(SendRecData);
    }

    @Override
    public int RxCobInsert315(SendRecDataVo SendRecData) {
        return mapper.RxCobInsert315(SendRecData);
    }

    @Override
    public int SendCobInsert(SendRecDataVo SendRecData) {
        return mapper.SendCobInsert(SendRecData);
    }

    @Override
    public int RxSmtInsert315(SendRecDataVo SendRecData) {
        return mapper.RxSmtInsert315(SendRecData);
    }

    @Override
    public int SendMiInsert(SendRecDataVo SendRecData) {
        return mapper.SendMiInsert(SendRecData);
    }

    @Override
    public int RxMiInsert315(SendRecDataVo SendRecData) {
        return mapper.RxMiInsert315(SendRecData);
    }

    @Override
    public int UpStatus(String Lot) {
        return mapper.UpStatus(Lot);
    }

    @Override
    public int InsertSN(String Sn, String Lot, String LotQty, String WO,
                        String WERKS, String CreateUser) {
        return mapper.InsertSN(Sn, Lot, LotQty, WO, WERKS, CreateUser);
    }

    @Override
    public List<Map<String, Object>> PCBAInventoryData(int pageIndex, int pageSize, List<String> plant1, List<String> workcenter1, List<String> wo1, List<String> partnumber1) {
        return mapper.PCBAInventoryData(pageIndex, pageSize, plant1, workcenter1, wo1, partnumber1);
    }

    @Override
    public List<Map<String, Object>> FuzzyPn(String Pn, String plant, String workcenter) {
        return mapper.FuzzyPn(Pn, plant, workcenter);
    }

    @Override
    public TotalVo Total() {
        return mapper.Total();
    }

    @Override
    public String downloadData(HttpServletResponse response, String StartTime, String EndTime) throws SQLException {
        javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView.getFileSystemView();
        String filename = "PCBA超市库存.xls";
        Con182HR con100hr = new Con182HR();
        ResultSet rs = con100hr.executeQuery(SqlApi.downloadData());
        ResultSet rs1 = con100hr.executeQuery(SqlApi.download101Data(StartTime, EndTime));
        ResultSet rs2 = con100hr.executeQuery(SqlApi.download313_315Data(StartTime, EndTime));
        HSSFWorkbook wb = new HSSFWorkbook();
        int sum = 0;
        int sum1 = 0;
        int sum2 = 0;
        HSSFSheet sheet = wb.createSheet("PCBA超市库存");
        HSSFSheet sheet1 = wb.createSheet("PCBA超市101入库记录");
        HSSFSheet sheet2 = wb.createSheet("PCBA超市收发板记录");
        HSSFRow row = null;
        HSSFRow row1 = null;
        HSSFRow row2 = null;
        HSSFCell cell = null;

        //标题字体
        HSSFFont titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 10);
        titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
        titleFont.setFontName("新細明體");

        //内容字体
        HSSFFont commonFont = wb.createFont();
        commonFont.setFontHeightInPoints((short) 9);
        commonFont.setFontName("新細明體");

        //表头样式
        HSSFCellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFont(titleFont);
        headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框实线
        headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框实线
        headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框实线
        headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);//下边框实线
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平居中
        headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
        headerStyle.setFillForegroundColor((short) 5);// 设置背景色
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setWrapText(true);

        //内容文字样式
        HSSFCellStyle textStyle = wb.createCellStyle();
        textStyle.setFont(commonFont);
        textStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框实线
        textStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框实线
        textStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框实线
        textStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);//下边框实线
        textStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中

        //内容数字样式
        HSSFCellStyle numberStyle = wb.createCellStyle();
        numberStyle.setFont(commonFont);
        numberStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//右边对齐

        //内容日期样式
        HSSFCellStyle dateStyle = wb.createCellStyle();
        dateStyle.setFont(commonFont);
        dateStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框实线
        dateStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框实线
        dateStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框实线
        dateStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);//下边框实线
        dateStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
        HSSFDataFormat format = wb.createDataFormat();
        dateStyle.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm:ss"));

        //表头
        String[] header = {"序号", "工单", "工单数量", "Lot号", "PN", "库位", "批次", "批次数量", "工作中心", "工厂", "存放人", "存放时间"};
        String[] header1 = {"序号", "工单", "工单数量", "Lot号", "Lot数量", "PN", "库位", "工作中心", "工厂", "入库人员", "入库时间"};
        String[] header2 = {"序号", "工单", "工单数量", "PN", "Lot号", "Lot数量", "发板人", "发板时间", "发出仓位", "发送状态", "收板人", "收板时间", "接收仓位", "工作中心", "工厂"};
        row = sheet.createRow(0);
        for (int j = 0; j < header.length; j++) {
            cell = row.createCell(j);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(header[j]);
        }
        row1 = sheet1.createRow(0);
        for (int j = 0; j < header1.length; j++) {
            cell = row1.createCell(j);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(header1[j]);
        }
        row2 = sheet2.createRow(0);
        for (int j = 0; j < header2.length; j++) {
            cell = row2.createCell(j);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(header2[j]);
        }
        int rowNum = 0;
        int rowNum1 = 0;
        int rowNum2 = 0;
        while (rs.next()) {
            sum += 1;
            if (sum <= 65534) {
                rowNum = rowNum + 1;//行数+1
                int cellindex = 0;
                row = sheet.createRow(rowNum);
                cellindex = 0;

                cell = row.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(sum);

                cell = row.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs.getString("WO"));

                cell = row.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs.getString("WOQuantity"));

                cell = row.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs.getString("UID"));

                cell = row.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs.getString("PartNumber"));

                cell = row.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs.getString("Location"));

                cell = row.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs.getString("AvailableBatch"));

                cell = row.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs.getString("AvailableQuantity"));

                cell = row.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs.getString("workcenter"));

                cell = row.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs.getString("plant"));

                cell = row.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs.getString("CreateUser"));

                cell = row.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs.getString("CreateTime"));

            }
        }
        while (rs1.next()) {
            sum1 += 1;
            if (sum1 <= 65534) {
                rowNum1 = rowNum1 + 1;//行数+1
                int cellindex = 0;
                row1 = sheet1.createRow(rowNum1);
                cellindex = 0;

                cell = row1.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(sum1);

                cell = row1.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs1.getString("WO"));

                cell = row1.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs1.getString("WOQuantity"));

                cell = row1.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs1.getString("AvailableBatch"));

                cell = row1.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs1.getString("AvailableQuantity"));

                cell = row1.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs1.getString("PartNumber"));

                cell = row1.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs1.getString("Location"));

                cell = row1.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs1.getString("workcenter"));

                cell = row1.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs1.getString("plant"));

                cell = row1.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs1.getString("CreateUser"));

                cell = row1.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs1.getString("CreateTime"));
            }
        }
        while (rs2.next()) {
            sum2 += 1;
            if (sum2 <= 65534) {
                rowNum2 = rowNum2 + 1;//行数+1
                int cellindex = 0;
                row2 = sheet2.createRow(rowNum2);
                cellindex = 0;

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(sum2);

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("WO"));

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("WOQTY"));

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("PartNumber"));

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("SendingBatch"));

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("SendingBatchQTY"));

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("SendingUser"));

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("SendingTime"));

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("SendLocation"));

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("Status"));

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("ReceiveUser"));

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("ReceiveTime"));

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("RecLocation"));

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("workcenter"));

                cell = row2.createCell(cellindex++);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(textStyle);
                cell.setCellValue(rs2.getString("plant"));
            }
        }
        for (int i = 0; i < 10; i++) {
            sheet.autoSizeColumn((short) i); //自动调整宽度
            sheet1.autoSizeColumn((short) i); //自动调整宽度
            sheet2.autoSizeColumn((short) i); //自动调整宽度
        }

        //输出Excel文件
        OutputStream output;
        try {
            filename = URLEncoder.encode(filename, "UTF-8");
            output = response.getOutputStream();
            response.reset();
            //设置响应头
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            response.setContentType("application/msexcel");
            wb.write(output);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

    @Override
    public String UidState(String Lot) {
        return mapper.UidState(Lot);
    }

    @Override
    public List<String> selPrivilegedUsers(int type) {
        return mapper.selPrivilegedUsers(type);
    }

    @Override
    public boolean isValidLocation(String location) {
        return mapper.checkLocationCodeExists(location) != null;
    }

    @Override
    public List<Map<String, String>> GetBatchId(String Lot, String Type) {
        return mapper.GetBatchId(Lot, Type);
    }

    @Override
    public int retryPosting(String BatchId, String ItemId) {
        return mapper.retryPosting(BatchId, ItemId);
    }

    @Override
    public String specialPn(String pn) {
        return mapper.specialPn(pn);
    }

    @Override
    public int SendMiInsertSpecial(SendRecDataVo SendRecData) {
        return mapper.SendMiInsertSpecial(SendRecData);
    }

    @Override
    public int PcbaStorageSpecial(SendRecDataVo SendRecData) {
        return mapper.PcbaStorageSpecial(SendRecData);
    }

    @Override
    public String Fifo101(String Lot, String Wo, String Remark, String node, String factory) {
        String flag = "";
        Con72DB con72db = new Con72DB();
        Con75DB con75db = new Con75DB();
        Con51DB con51db = new Con51DB();
        Con182HR con182HR = new Con182HR();
        ResultSet rs1, rs2;
        try {
            if (factory.equals("B1")) {
                if (node.equals("smt")) {
                    rs1 = con182HR.executeQuery(SqlApi.findLastBatchByWo(Wo));
                    /*
                     * 已入库 -> 提示下一个
                     * 未入库 -> 提示工单第一个
                     */
                    if (rs1.next()) {
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
                                flag = "此Lot已扫描，暂时没有查询到需要101入库的Lot号";
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
                                flag = "此Lot已扫描，暂时没有查询到需要101入库的Lot号";
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
                            flag = "此Lot已扫描，暂时没有查询到需要101入库的Lot号";
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
                        flag = "此Lot已扫描，暂时没有查询到需要101入库的Lot号";
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

    /**
     * 提取批次SN集合
     *
     * @param rs   结果集
     * @param data 报工信息
     * @return 批次SN集合
     * @throws SQLException SQL异常
     */
    private List<LotSn> extractSnRecords(ResultSet rs, SendRecDataVo data) throws SQLException {
        String qty = data.getQty();
        String wo = data.getWo();
        String factory = data.getFactory();
        String user = data.getUser();
        List<LotSn> list = new ArrayList<>();
        // 获取列数
        int count = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 1; i <= count; i++) {
                Object value = rs.getObject(i);
                String key = rs.getMetaData().getColumnName(i);
                map.put(key, value);
            }
            String b = map.toString();
            String sn = b.substring(29, b.length() - 1);
            String lot = b.substring(5, 24);
            list.add(new LotSn(sn, lot, qty, wo, factory, user));
        }
        return list;
    }

    private Map<String, Object> extractRow(ResultSet rs) throws SQLException {
        Map<String, Object> row = new HashMap<>();
        int columnCount = rs.getMetaData().getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            row.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
        }
        return row;
    }

    private List<LotSn> convertToLotSnList(List<Map<String, Object>> dataList, SendRecDataVo data) {
        List<LotSn> list = new ArrayList<>();
        String qty = data.getQty();
        String wo = data.getWo();
        String factory = data.getFactory();
        String user = data.getUser();

        for (Map<String, Object> map : dataList) {
            String sn = map.get("SN") != null ? map.get("SN").toString() : "";
            String lot = map.get("Lot") != null ? map.get("Lot").toString() : "";
            list.add(new LotSn(sn, lot, qty, wo, factory, user));
        }

        return list;
    }

}
