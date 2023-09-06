package com.ht.api;


public enum ObSendRecType {
	SMTSEND("SPE100000060"),  
    COBSEND("SPE100000063"),  
    COBREC("SPE100000061"),
	MISEND("SPE10000006B"),
	MIREC("SPE10000006C"),
	CASINGREC("SPE100000066");

    private String typeName;  

    public String getTypeName() {
		return typeName;
	}

	ObSendRecType(String typeName) {
    	this.typeName = typeName; 
    }
    
    ObSendRecType() {  
     
    }  
}
