package net.jueb.util4j.beta.tools.ui.frame;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class FrameStart {
	
	public void start(final JFrame frame)
	{
		if(frame!=null)
		{
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						final JFrame ui=frame;
						ui.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
	}
}
