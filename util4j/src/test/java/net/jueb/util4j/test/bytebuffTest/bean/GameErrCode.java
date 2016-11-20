package net.jueb.util4j.test.bytebuffTest.bean;

public enum GameErrCode {

	/**
	 * 未知错误
	 */
	UnknownError(0,"未知错误"),
	/**
	 * 成功
	 */
	Succeed (1,"成功"),
	/**
	 * 参数错误
	 */
	ArgsError (2,"参数错误"),
	/**
	 * 登录token错误�?
	 */
	GateTokenError (3,"登录token错误"),
	/**
	 * 重复服务ID注册
	 */
	RepeatServerIdRegError (4,"重复服务ID注册"),
	/**
	 * 房间号错�?
	 */
	RoomNumberError (5,"房间号错�?"),
	/**
	 * 系统繁忙
	 */
	ServiceBusy (6,"系统繁忙"),
	/**
	 * 处理超时
	 */
	TimeOut (7,"处理超时"),
	/**
	 * 角色游戏锁定
	 */
	RoleGameLockError (8,"角色游戏锁定"),
	/**
	 * 角色不存�?
	 */
	RoleNotFound (9,"处理超时"),
	/**
	 * 不支持的操作
	 */
	UnSupportOperation (10,"不支持的操作"),
	/**
	 * 服务部可�?
	 */
	ServiceNotAvailable(11,"服务不可�?"),
	/**
	 * 游戏服务不可�?
	 */
	GameServiceNotAvailable(12,"游戏服务不可�?"),
	/**
	 * 大厅服务不可�?
	 */
	HallServiceNotAvailable(13,"大厅服务不可�?"),
	/**
	 * 服务器维护中
	 */
	SystemReapir(14,"服务器维护中"),
	/**
	 * webtoken错误
	 */
	WebTokenError (15,"webtoken错误"),
	;
	
	private int value;
	private final String msg;
	private GameErrCode(int value,String msg) {
		this.value=value;
		this.msg=msg;
	}
	public int value()
	{
		return this.value;
	}
	
	public String getMsg() {
		return msg;
	}
	public static GameErrCode valueOf(int value)
	{
		for(GameErrCode gt:values())
		{
			if(gt.value==value)
			{
				return gt;
			}
		}
		return UnknownError;
	}
}
