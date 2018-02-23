package net.jueb.util4j.example.script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServerScript implements IServerScript{

	protected final Logger _log=LoggerFactory.getLogger(getClass());
	private Object[] params=null;
	private Request request;
	private RunMode mode;
	
	@Override
	public final void setRunMode(RunMode mode) {
		this.mode=mode;
	}

	@Override
	public final RunMode getRunMode() {
		return mode;
	}

	@Override
	public void setParams(Object... params) {
		this.params=params;
	}
	
	@Override
	public Object[] getParams() {
		return params;
	}
	
	@Override
	public final Request getRequest() {
		return request;
	}
	
	@Override
	public final void setRequest(Request request) {
		this.request=request;
	}
	
	@Override
	public final void run() {
		runBefore();
		try {
			switch (getRunMode()) {
			case Action:
				action();
				break;
			case HandleRequest:
				handleRequest(getRequest());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
			onException(e);
		}
		runAfter();
	}
	
	protected void runBefore(){
		
	}
	protected void runAfter() {
		
	}
	protected void onException(Exception e){
		
	}
}
