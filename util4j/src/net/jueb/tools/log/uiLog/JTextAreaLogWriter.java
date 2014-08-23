package net.jueb.tools.log.uiLog;

import java.io.IOException;
import java.io.Writer;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class JTextAreaLogWriter extends Writer{
	
	private JTextArea textArea;
    private JScrollPane scroll;
    
    
	public JTextAreaLogWriter(JTextArea textArea, JScrollPane scroll) {
		super();
		this.textArea = textArea;
		this.scroll = scroll;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		if(textArea != null)
		{
			textArea.append(new String(cbuf, off, len));
		}
		if(scroll!=null)
        {//使垂直滚动条自动向下滚动
        	scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
        }
	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public JScrollPane getScroll() {
		return scroll;
	}

}
