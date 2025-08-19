package com.ht.utils;

public class SqlApi {

    /**
     * 插入 SN
     *
     * @param sn      SN
     * @param lot     批次
     * @param lotQty  批次数量
     * @param wo      工单
     * @param factory 厂区
     * @param user    操作人
     * @return SQL
     */
    public static String insSn(String sn, String lot, String lotQty, String wo, String factory, String user) {
        return "INSERT INTO [dbo].[DL_Lot-SN] (ID,SN,Lot,LotQty,WO,WERKS,CreateUser,CreateTime) " +
                "VALUES (NEWID(),'" + sn + "','" + lot + "','" + lotQty + "','" + wo + "','" + factory + "','" + user + "',GETDATE())";
    }

    /**
     * LOT号OB数据
     *
     * @param Lot UID
     * @return SQL
     */
    public static String SelLotData(String Lot) {
        return "SELECT Handover.FGSN as Batch,Handover.FGQTY as Qty,MO.MOName as Wo,MO.MOQtyRequired as WoQty,MO.FactoryId as Factory,MO.SendStorage as sendLocation,MO.ReceiveStorage as RecLocation,ProductRoot.ProductName as Pn FROM Handover left JOIN "
                + " MO ON Handover.MoId = MO.MOId left JOIN "
                + " SpecificationRoot ON Handover.SendSpecificationId = SpecificationRoot.DefaultSpecificationId LEFT OUTER JOIN "
                + " SpecificationRoot AS SpecificationRoot_1 ON Handover.ReceiveSpecificationId = SpecificationRoot_1.DefaultSpecificationId left JOIN "
                + " ProductRoot ON MO.ProductId = ProductRoot.DefaultProductId "
                + " where Handover.FGSN = '" + Lot + "' ";
    }

    /**
     * PCBA超市库存
     *
     * @return SQL
     */
    public static String downloadData() {
        return "select WO,WOQuantity,UID,PartNumber,Factory,Location,AvailableBatch,AvailableQuantity,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant,CreateUser,CreateTime  from DL_PCBAInventory where State =1";
    }

    /**
     * 101入库记录
     *
     * @param StartTime 开始时间
     * @param EndTime   结束时间
     * @return SQL
     */
    public static String download101Data(String StartTime, String EndTime) {
        if (StartTime.isEmpty() || EndTime.isEmpty()) {
            return "select WO,WOQuantity,AvailableBatch,AvailableQuantity,PartNumber,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,Location,plant,CreateUser,CreateTime from DL_PCBAInventory where (State = 0 or State =1)";
        } else {
            return "select WO,WOQuantity,AvailableBatch,AvailableQuantity,PartNumber,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,Location,plant,CreateUser,CreateTime from DL_PCBAInventory where (State = 0 or State =1) and CreateTime >='" + StartTime + " 00:00:01' and CreateTime<='" + EndTime + " 23:59:59'";
        }
    }

    /**
     * 313_315收发记录
     *
     * @param StartTime 开始时间
     * @param EndTime   结束时间
     * @return SQL
     */
    public static String download313_315Data(String StartTime, String EndTime) {
        if (StartTime.isEmpty() || EndTime.isEmpty()) {
            return "select PS.WO,PS.WOQTY,PS.PartNumber,SendingBatch,SendingBatchQTY,SendingUser,SendingTime,SendLocation,(CASE WHEN PS.Status = '1' THEN '已发送'else '已接收' END)as Status,ReceiveUser,ReceiveTime,RecLocation,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant " +
                    " from DL_PCBAInventory PIT right join DL_PCBASMT PS on PIT.UID = PS.UID " +
                    " where (PIT.State = 0 or PIT.State =1)" +
                    " union all" +
                    " select PC.WO,PC.WOQTY,PC.PartNumber,SendingBatch,SendingBatchQTY,SendingUser,SendingTime,SendLocation,(CASE WHEN PC.Status = '1' THEN '已发送'else '已接收' END)as Status,ReceiveUser,ReceiveTime,RecLocation,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant " +
                    " from DL_PCBAInventory PIT right join DL_PCBACOB PC on PIT.UID = PC.UID " +
                    " where (PIT.State = 0 or PIT.State =1)" +
                    " union all" +
                    " select PM.WO,PM.WOQTY,PM.PartNumber,SendingBatch,SendingBatchQTY,SendingUser,SendingTime,SendLocation,(CASE WHEN PM.Status = '1' THEN '已发送'else '已接收' END)as Status,ReceiveUser,ReceiveTime,RecLocation,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant " +
                    " from DL_PCBAInventory PIT right join DL_PCBAMI PM on PIT.UID = PM.UID " +
                    " where (PIT.State = 0 or PIT.State =1)";
        } else {
            return "select PS.WO,PS.WOQTY,PS.PartNumber,SendingBatch,SendingBatchQTY,SendingUser,SendingTime,SendLocation,(CASE WHEN PS.Status = '1' THEN '已发送'else '已接收' END)as Status,ReceiveUser,ReceiveTime,RecLocation,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant " +
                    " from DL_PCBAInventory PIT right join DL_PCBASMT PS on PIT.UID = PS.UID " +
                    " where (PIT.State = 0 or PIT.State =1)and PS.CreateTime >='" + StartTime + " 00:00:01' and PS.CreateTime<='" + EndTime + " 23:59:59'" +
                    " union all" +
                    " select PC.WO,PC.WOQTY,PC.PartNumber,SendingBatch,SendingBatchQTY,SendingUser,SendingTime,SendLocation,(CASE WHEN PC.Status = '1' THEN '已发送'else '已接收' END)as Status,ReceiveUser,ReceiveTime,RecLocation,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant " +
                    " from DL_PCBAInventory PIT right join DL_PCBACOB PC on PIT.UID = PC.UID " +
                    " where (PIT.State = 0 or PIT.State =1)and PC.CreateTime >='" + StartTime + " 00:00:01' and PC.CreateTime<='" + EndTime + " 23:59:59'" +
                    " union all" +
                    " select PM.WO,PM.WOQTY,PM.PartNumber,SendingBatch,SendingBatchQTY,SendingUser,SendingTime,SendLocation,(CASE WHEN PM.Status = '1' THEN '已发送'else '已接收' END)as Status,ReceiveUser,ReceiveTime,RecLocation,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant " +
                    " from DL_PCBAInventory PIT right join DL_PCBAMI PM on PIT.UID = PM.UID " +
                    " where (PIT.State = 0 or PIT.State =1)and PM.CreateTime >='" + StartTime + " 00:00:01' and PM.CreateTime<='" + EndTime + " 23:59:59'";
        }
    }

    /**
     * LOT号SN明细 SMT
     *
     * @param Lot 批次
     * @return SQL
     */
    public static String SelSmtSnData(String Lot) {
        return "select LotSN as SN,SMTLot as Lot from Lot where SMTLot= '" + Lot + "' ";
    }

    /**
     * LOT号SN明细 COB
     *
     * @param Lot 批次
     * @return SQL
     */
    public static String SelCobSnData(String Lot) {
        return "select LotSN as SN,BondingLot as Lot from Lot where BondingLot= '" + Lot + "' ";
    }

    /**
     * LOT号SN明细 MI 640
     *
     * @param Lot 批次
     * @return SQL
     */
    public static String SelMiSnData(String Lot) {
        return "select LotSN as SN,DIPElot as Lot from Lot where DIPElot = '" + Lot + "' ";
    }

    /**
     * 根据工单取批次号最大的UID
     *
     * @param Wo 工单
     * @return 批次号最大的UID
     */
    public static String fifoByBatch(String Wo) {
        return "select TOP 1 * from DL_PCBAInventory where WO = '" + Wo + "' order by CAST(AvailableBatch as int) DESC";
    }

    /**
     * Orbit fifo(根据交接单打印时间管控)
     *
     * @param Lot    交接单
     * @param Wo     工单
     * @param Remark 描述
     * @return 下一个需要入或绑的 UID
     */
    public static String obFifo(String Lot, String Wo, String Remark) {
        if (Remark.isEmpty()) {
            return "select top 1 FGSN from Handover where CreateDate >(select CreateDate from Handover where FGSN = '" + Lot +
                    "') and  FGSN like '" + Wo + "%' and CHARINDEX('-', FGSN) = 0  order by CreateDate ";
        } else {
            return "select top 1 FGSN from Handover where CreateDate >(select CreateDate from Handover where FGSN = '" + Lot +
                    "'and Remark = '" + Remark + "') and  FGSN like '" + Wo + "%' and CHARINDEX('-', FGSN) = 0 and Remark = '" + Remark +
                    "' order by CreateDate ";
        }
    }

    /**
     * Orbit fifo（工单第一个批次号）
     *
     * @param Wo     工单
     * @param Remark 描述
     * @return 该工单的第一个 UID
     */
    public static String obFirst(String Wo, String Remark) {
        if (Remark.isEmpty()) {
            return "select top 1 FGSN from Handover where FGSN like '" + Wo + "%' and CHARINDEX('-', FGSN) = 0 order by CreateDate ";
        } else {
            return "select top 1 FGSN from Handover where FGSN like '" + Wo + "%' and CHARINDEX('-', FGSN) = 0 and Remark = '" + Remark +
                    "' order by CreateDate ";
        }
    }

    /**
     * 根据工单取批次号最大UID
     *
     * @param Wo 工单
     * @return SQL
     */
    public static String findLastBatchByWo(String Wo) {
        return "select top 1 UID from [DL_PCBAInventory] where WO = '" + Wo + "' order by cast(AvailableBatch as int) desc";
    }

    /**
     * 修改OB发料状态
     *
     * @param Lot    交接单
     * @param User   用户
     * @param ObType 收发类型
     * @return SQL
     */
    public static String UpObSend(String Lot, String User, String ObType) {
        return "update OrBitX.dbo.Handover set SendUserId = '" + User + "',SendSpecificationId = '" + ObType + "',FGStatus = '0',SendTime = GETDATE() where FGSN = '" + Lot + "'";
    }

    /**
     * 修改OB收料状态
     *
     * @param Lot    交接单
     * @param User   用户
     * @param ObType 收发类型
     * @return SQL
     */
    public static String UpObRec(String Lot, String User, String ObType) {
        return "update OrBitX.dbo.Handover set ReceiveUserId = '" + User + "',ReceiveSpecificationId = '" + ObType + "',FGStatus = '1',ReceiveTime = GETDATE() where FGSN='" + Lot + "'";
    }

}
