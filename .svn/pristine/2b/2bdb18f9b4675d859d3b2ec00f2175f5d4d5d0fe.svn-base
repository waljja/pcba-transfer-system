package test2;

import static org.junit.Assert.*;

import java.sql.ResultSet;

import org.junit.Test;

import com.ht.util.Con100HR;

public class D {

	@Test
	public void test() {
		Con100HR con100hr = new Con100HR();
		int sum = 0;
		ResultSet rs = con100hr.executeQuery("SELECT B.ToStock,B.TransactionTime,B.TransactionUser,A.SendingBatch,A.SendingBatchQTY,A.UID " +
  "FROM [HT_InterfaceExchange].[dbo].[DL_PCBASMT]A left join xTend_MaterialTransactions B " +
  "on A.UID = B.UID " +
  "where A.UID in( " +
  "select UID from DL_PCBAInventory where [313_Status] = '已发送'and [315_Status]is null and plant = 'B2' " +
  ") and B.TransactionType = '315'");
		
		try {
			while (rs.next()) {
				sum+=1;
				con100hr.executeUpdate("update DL_PCBASMT set ReceiveBatch = '"+rs.getString("SendingBatch")+"' ,RecLocation = '"+rs.getString("ToStock")+"' ,ReceiveUser = '"+rs.getString("TransactionUser")+"' ,ReceiveBatchQTY = '"+rs.getString("SendingBatchQTY")+"' ,ReceiveTime = '"+rs.getString("TransactionTime")+"'  where UID = '"+rs.getString("UID")+"' ");
			}
			System.out.println(sum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
