package com.ht.api;

public enum ResultCode implements IErrorCode{
    SUCCESS(200, "ture"),
    FAILED(500, "false"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "没有查询到相关数据!"),
    TRANSFERWASUNSUCCESSFUL101(401, "101过账未成功，不能进行绑库！"),
    THEREISNOPOSTING101(401, "请先做101收料移库在进行绑库"),
    FORBIDDEN(403, "没有相关权限"),
    LOGINGFAILED(401,"登入失败"),
    DATAINSERTIONFAILURE(401, "数据已存在！"),
    TAKEDOWN_SUCCESS(300,"库存下架成功"),
    EXISTING(401, "已经在库存中!");
 
    /** 定义状态码 */
    private long code;
 
    /** 定义返回信息 */
    private String message;
 
    ResultCode() {
    }
 
    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }
 
    @Override
    public long getCode() {
        return code;
    }
 
    @Override
    public String getMessage() {
        return message;
    }
}

