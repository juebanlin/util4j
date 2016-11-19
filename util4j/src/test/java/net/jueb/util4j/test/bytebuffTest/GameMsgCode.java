package net.jueb.util4j.test.bytebuffTest;


public interface GameMsgCode {

	public static final short Heart_Req = 1001;
	public static final short Heart_Rsp = 1002;
	public static final short Sys_ServerClose = 1003;
	public static final short Sys_Time24 = 1004;
	public static final short Sys_Debug = 1005;
	public static final short HttpRequest = 1006;
	public static final short Rpc_Request = 1007;
	
	/**
	 * 大厅消息编号
	 */
	public static final short Center_GameReg = 2001;//大厅游戏服注�?
	public static final short Center_GameUnReg = 2002;//大厅游戏服注�?
	public static final short Center_GateReg = 2003;//大厅网关注册
	public static final short Center_GateUnReg = 2004;//大厅网关注销
	public static final short Center_GameRoleExit = 2005;//玩家离开游戏�?
	public static final short Center_UserLogin = 2006;//用户登录
	public static final short Center_CreateRole = 2007;//用户创建
	public static final short Center_MessageArrived = 2008;
	public static final short Center_ConnectionClosed = 2009;
	public static final short Center_RoleFlushDb = 2010;//角色刷新到数据库
	public static final short Center_RoleLockUpdate = 2011;//角色锁状态更�?
	
	//玩家操作请求
	public static final short Center_EntryGame = 2501;//进入游戏
	public static final short RoleHallDataRefresh = 2502;//刷新大厅玩家数据
	
	/**
	 * 游戏服消息编�?
	 */
	public static final short Game_GateReg = 3001;
	public static final short Game_GateUnReg = 3002;
	public static final short Game_RoleEntry = 3003;
	public static final short Game_RoleDataUpdate = 3004;
	public static final short Game_MessageArrived = 3005;
	public static final short Game_ConnectionClosed = 3006;
	public static final short Game_RoomEvent = 3007;
	
	//游戏服玩家交互消�?
	public static final short Game_RoleEnv = 3501;
	public static final short Game_createTable = 3502;

	/**
	 * 网关消息编号
	 */
	
	public static final short Gate_MessageArrived = 4001;
	public static final short Gate_ConnectionClosed = 4002;
	public static final short Gate_UserLogin = 4003;//用户登录
	public static final short Gate_RoleDisConnect = 4004;//用户登录
	public static final short Gate_LoginOut = 4005;//用户登录
	public static final short Gate_GameConnectionUpdate = 4006;//游戏服连接更�?
	public static final short Gate_RoleGameMessage = 4007;//玩家游戏消息
	public static final short Gate_ErrorInfo = 4008;
	public static final short Gate_OtherLogin = 4009;
	public static final short Gate_Broadcast = 4010;//广播消息
	public static final short Gate_RoleGateEvent = 4011;
}
