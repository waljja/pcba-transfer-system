package com.ht.api;

import lombok.Getter;

/**
 * Orbit收发类型编码枚举
 */
@Getter
public enum ObSendRecType {
	SMT_SEND("SPE100000060"),
    COB_SEND("SPE100000063"),
    COB_REC("SPE100000061"),
	MI_SEND("SPE10000006B"),
	MI_REC("SPE10000006C"),
	CASING_REC("SPE100000066");

    private final String typeName;

	ObSendRecType(String typeName) {
    	this.typeName = typeName; 
    }

}
