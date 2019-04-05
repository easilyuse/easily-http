
package com.github.easilyuse.exception;

/**
 * 
 * <p>
 * Title:EasyHttpException
 * </p>
 * <p>
 * Description: 自定义异常
 * </p>
 * 
 * @author linyb
 */
public class EasyHttpException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String msg;
	private Integer code;;

	public EasyHttpException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public EasyHttpException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}

	public EasyHttpException(String msg, Integer code) {
		super(msg);
		this.msg = msg;
		this.code = code;
	}

	public EasyHttpException(String msg, Integer code, Throwable e) {
		super(msg, e);
		this.msg = msg;
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

}
