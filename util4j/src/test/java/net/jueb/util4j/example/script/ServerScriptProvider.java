package net.jueb.util4j.example.script;

import net.jueb.util4j.example.script.IServerScript.Request;
import net.jueb.util4j.example.script.IServerScript.RunMode;
import net.jueb.util4j.example.script.factory.GameScriptProvider;
import net.jueb.util4j.hotSwap.classProvider.IClassProvider;
import net.jueb.util4j.net.JConnection;

/**
 * T尽量使用接口类型
 * @author juebanlin@gmail.com
 * time:2015年6月17日
 * @param <T>
 */
public abstract class ServerScriptProvider<T extends IServerScript> extends GameScriptProvider<T>{
	
	protected ServerScriptProvider(IClassProvider classProvider) {
		super(classProvider);
	}

	public final T buildHandleRequest(int code,JConnection connection,Object msg,Object ...params)
	{
		T script=super.buildInstance(code);
		if(script!=null)
		{
			script.setRunMode(RunMode.HandleRequest);
			script.setRequest(new Request(connection, msg));
			if(params!=null)
			{
				script.setParams(params);
			}
		}
		return script;
	}
	
	public final T buildAction(int code,Object ...params)
	{
		T script=super.buildInstance(code);
		if(script!=null)
		{
			script.setRunMode(RunMode.Action);
			if(params!=null)
			{
				script.setParams(params);
			}
		}
		return script;
	}
	
	public final T buildHandleRequest(String path,JConnection connection,Object msg,Object ...params)
	{
		T script=super.buildInstance(path);
		if(script!=null)
		{
			script.setRunMode(RunMode.HandleRequest);
			script.setRequest(new Request(connection, msg));
			if(params!=null)
			{
				script.setParams(params);
			}
		}
		return script;
	}
}
