package com.ht.service.impl;

import com.ht.mapper.PcbaInventoryMapper;
import com.ht.service.PcbaInventoryService;
import com.ht.util.Con182HR;
import com.ht.util.SqlApi;
import com.ht.vo.SapClosingTime;
import com.ht.vo.SendRecDataVo;
import com.ht.vo.TotalVo;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class PcbaInventoryServiceimpl implements PcbaInventoryService{
	@Autowired
	PcbaInventoryMapper mapper;

	/**
	 * 查询Smt工序批次数据
	 * @see com.ht.service.PcbaInventoryService#QRYSmtData(java.lang.String)
	 */
	/*@Override
	public SendRecDataVo QRYSmtData(String Lot) {
		// TODO Auto-generated method stub
		return mapper.QRYSmtData(Lot);
	}*/
	
	/**
	 * 查询Smt工序批次数据
	 * @see com.ht.service.PcbaInventoryService#QRYSmtData(java.lang.String)
	 */
//	@Override
	/*public SendRecDataVo QRYSmtDataNo(String Lot) {
		// TODO Auto-generated method stub
		return mapper.QRYSmtDataNo(Lot);
	}*/

	/**
	 * 把数据插入PCBA库存表
	 * @see com.ht.service.PcbaInventoryService#PcbaStorage(com.ht.vo.SendRecDataVo)
	 */
	@Override
	public int PcbaStorage(SendRecDataVo SendRecData) {
		// TODO Auto-generated method stub
		return mapper.PcbaStorage(SendRecData);
	}

	/**
	 * 查询Pcba库存信息
	 * @see com.ht.service.PcbaInventoryService#BatchData(java.lang.String, java.lang.String)
	 */
	@Override
	public SendRecDataVo BatchData(String Wo, String Lot) {
		// TODO Auto-generated method stub
		return mapper.BatchData(Wo, Lot);
	}

	/**
	 * 101插入数据
	 * @see com.ht.service.PcbaInventoryService#SendSmtplugin101(com.ht.vo.SendRecDataVo)
	 */
	@Override
	public int SendSmtplugin101(SendRecDataVo SendRecData) {
		// TODO Auto-generated method stub
		return mapper.SendSmtplugin101(SendRecData);
	}

	@Override
	public String InventoryState(String Lot) {
		// TODO Auto-generated method stub
		return mapper.InventoryState(Lot);
	}

	@Override
	public int SendSmtplugin313(SendRecDataVo SendRecData) {
		// TODO Auto-generated method stub
		return mapper.SendSmtplugin313(SendRecData);
	}

	@Override
	public int SendSmtInsert(SendRecDataVo SendRecData) {
		// TODO Auto-generated method stub
		return mapper.SendSmtInsert(SendRecData);
	}

	@Override
	public int InventoryStatus(String Lot,String model) {
		// TODO Auto-generated method stub
		return mapper.InventoryStatus(Lot,model);
	}

	@Override
	public SendRecDataVo SelFactory(String Lot,String model) {
		// TODO Auto-generated method stub
		return mapper.SelFactory(Lot,model);
	}

	@Override
	public SendRecDataVo PcbaFIFO(String Lot,String Plant) {
		// TODO Auto-generated method stub
		return mapper.PcbaFIFO(Lot,Plant);
	}

	@Override
	public SendRecDataVo RxCobData(String Lot,String Type) {
		// TODO Auto-generated method stub
		return mapper.RxCobData(Lot,Type);
	}

	@Override
	public SendRecDataVo Off_RxCobData(String Lot,String Type) {
		// TODO Auto-generated method stub
		return mapper.Off_RxCobData(Lot,Type);
	}

	@Override
	public int RxCobplugin315(SendRecDataVo SendRecData) {
		// TODO Auto-generated method stub
		return mapper.RxCobplugin315(SendRecData);
	}

	@Override
	public int RxCobInsert315(SendRecDataVo SendRecData) {
		// TODO Auto-generated method stub
		return mapper.RxCobInsert315(SendRecData);
	}

	@Override
	public int SendCobInsert(SendRecDataVo SendRecData) {
		// TODO Auto-generated method stub
		return mapper.SendCobInsert(SendRecData);
	}

	@Override
	public int RxSmtInsert315(SendRecDataVo SendRecData) {
		// TODO Auto-generated method stub
		return mapper.RxSmtInsert315(SendRecData);
	}

	@Override
	public int SendMiInsert(SendRecDataVo SendRecData) {
		// TODO Auto-generated method stub
		return mapper.SendMiInsert(SendRecData);
	}

	@Override
	public int RxMiInsert315(SendRecDataVo SendRecData) {
		// TODO Auto-generated method stub
		return mapper.RxMiInsert315(SendRecData);
	}

	@Override
	public int SendCasingInsert(SendRecDataVo SendRecData) {
		// TODO Auto-generated method stub
		return SendCasingInsert(SendRecData);
	}

	@Override
	public int UpStatus(String Lot) {
		// TODO Auto-generated method stub
		return mapper.UpStatus(Lot);
	}

	/*@Override
	public List<Map<String, Object>> QRYSmtDataMap(String Lot) {
		// TODO Auto-generated method stub
		return mapper.QRYSmtDataMap(Lot);
	}*/

	@Override
	public int InsertSN(String Sn, String Lot, String LotQty, String WO,
			String WERKS, String CreateUser) {
		// TODO Auto-generated method stub
		return mapper.InsertSN(Sn, Lot, LotQty, WO, WERKS, CreateUser);
	}

	@Override
	public List<Map<String,Object>> PCBAInventoryData(int pageIndex,int pageSize,List<String> plant1,List<String> workcenter1,List<String> wo1,List<String> partnumber1) {
		// TODO Auto-generated method stub
		return mapper.PCBAInventoryData(pageIndex,pageSize,plant1,workcenter1,wo1,partnumber1);
	}

	@Override
	public List<Map<String,Object>> FuzzyPn(String Pn,String plant,String workcenter) {
		// TODO Auto-generated method stub
		return mapper.FuzzyPn(Pn,plant,workcenter);
	}

	@Override
	public TotalVo Total() {
		// TODO Auto-generated method stub
		return mapper.Total();
	}

	@Override
	public String downloadData(HttpServletResponse response,String StartTime,String EndTime) throws SQLException {
		javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView.getFileSystemView();
		String filepath = "C:\\PCBA报表"; 
		String filename = "PCBA超市库存.xls";
		Con182HR con100hr = new Con182HR();
		ResultSet rs = con100hr.executeQuery(SqlApi.downloadData(StartTime,EndTime));
		ResultSet rs1 = con100hr.executeQuery(SqlApi.download101Data(StartTime,EndTime));
		ResultSet rs2 = con100hr.executeQuery(SqlApi.download313_315Data(StartTime,EndTime));
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
			titleFont.setFontHeightInPoints((short)10);
			titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
			titleFont.setFontName("新細明體");
			
			//内容字体
			HSSFFont commonFont = wb.createFont();
			commonFont.setFontHeightInPoints((short)9);
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
            HSSFDataFormat format= wb.createDataFormat();
            dateStyle.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm:ss"));

			//表头
			String header[] = {"序号","工单","工单数量","Lot号","PN","库位","批次","批次数量","工作中心","工厂","存放人","存放时间"};
			String header1[] = {"序号","工单","工单数量","Lot号","Lot数量","PN","库位","工作中心","工厂","入库人员","入库时间"};
			String header2[] = {"序号","工单","工单数量","PN","Lot号","Lot数量","发板人","发板时间","发出仓位","发送状态","收板人","收板时间","接收仓位","工作中心","工厂"};
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
					sum+=1;
					 if (sum<=65534) {
							rowNum = rowNum + 1;//行数+1
							int cellindex=0;
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
				sum1+=1;
				 if (sum1<=65534) {
						rowNum1 = rowNum1 + 1;//行数+1
						int cellindex=0;
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
				sum2+=1;
				 if (sum2<=65534) {
						rowNum2 = rowNum2 + 1;//行数+1
						int cellindex=0;
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
			for(int i=0;i<10;i++){
				sheet.autoSizeColumn((short)i); //自动调整宽度
				sheet1.autoSizeColumn((short)i); //自动调整宽度
				sheet2.autoSizeColumn((short)i); //自动调整宽度
			}
			
		//输出Excel文件
        OutputStream output;
		try {
			filename = URLEncoder.encode(filename, "UTF-8");
			output = response.getOutputStream();
			response.reset();
			//设置响应头，
			        response.setHeader("Content-disposition", "attachment; filename="+filename);
			        response.setContentType("application/msexcel");       
			        wb.write(output);
			        output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
 
	
	public static void isChartPathExist(String dirpath){
		File file = new File(dirpath);
		if(!file.exists()){
		file.mkdirs();
	}
	}

	@Override
	public String UidState(String Lot) {
		// TODO Auto-generated method stub
		return mapper.UidState(Lot);
	}

	@Override
	public SapClosingTime SapSuspended() {
		// TODO Auto-generated method stub
		return mapper.SapSuspended();
	}

	@Override
	public List<Map<String, String>> GetBatchId(String Lot, String Type) {
		// TODO Auto-generated method stub
		return mapper.GetBatchId(Lot, Type);
	}

	@Override
	public int retryPosting(String BatchId, String ItemId) {
		// TODO Auto-generated method stub
		return mapper.retryPosting(BatchId, ItemId);
	}

	@Override
	public String specialPn(String pn) {
		// TODO Auto-generated method stub
		return mapper.specialPn(pn);
	}

	@Override
	public int SendMiInsertSpecial(SendRecDataVo SendRecData) {
		// TODO Auto-generated method stub
		return mapper.SendMiInsertSpecial(SendRecData);
	}

	@Override
	public int PcbaStorageSpecial(SendRecDataVo SendRecData) {
		// TODO Auto-generated method stub
		return mapper.PcbaStorageSpecial(SendRecData);
	}
 

}
