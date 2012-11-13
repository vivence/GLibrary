package ghost.library.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import junit.framework.Assert;
import android.text.TextUtils;

public class TCPSession {

	public static final int DEFAULT_TIMEOUT = 20*1000; // seconds
	
	private String host_;
	private int port_;
	private Socket socket_;

	public TCPSession(String host, int port)
	{
		Assert.assertTrue(!TextUtils.isEmpty(host));
		host_ = host;
		port_ = port;
	}
	
	public Socket getSocket()
	{
		if (null == socket_)
		{
			socket_ = new Socket();
		}
		else if (socket_.isClosed())
		{
			socket_ = new Socket();
		}
		return socket_;
	}
	
	public boolean connect()
	{
		return connect(DEFAULT_TIMEOUT);
	}
	
	public boolean connect(int timeout)
	{
		if (isConnected())
		{
			return true;
		}
		InetAddress host = null;
		try
		{
			host = InetAddress.getByName(host_);
		}
		catch (UnknownHostException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		InetSocketAddress address = new InetSocketAddress(host, port_);
		try
		{
			Socket socket = getSocket();
			socket.connect(address, timeout);
			socket.setSoTimeout(DEFAULT_TIMEOUT);
			return true;
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		close();
		return false;
	}
	
	public void close()
	{
		if (isConnected())
		{
			try
			{
				socket_.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			socket_ = null;
		}
	}
	
	public boolean isConnected()
	{
		return null != socket_ && socket_.isConnected() && !socket_.isClosed();
	}
	
	public boolean send(byte[] data)
	{
		if (null == data || 0 == data.length)
		{
			return false;
		}
		if (!isConnected())
		{
			if (!connect())
			{
				return false;
			}
		}
		try
		{
			OutputStream outputStream = getSocket().getOutputStream();
			outputStream.write(data);
			outputStream.flush();
			return true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			close();
		}
		return false;
	}
	
	public boolean recv(byte[] buffer)
	{
		return recv(buffer, 0, buffer.length);
	}
	
	public boolean recv(byte[] buffer, int offset)
	{
		return recv(buffer, offset, buffer.length-offset);
	}
	
	public boolean recv(byte[] buffer, int offset, int length)
	{
		if (null == buffer)
		{
			return false;
		}
		try
		{
			InputStream inputStream = getSocket().getInputStream();
			int readLength = 0;
			while (0 < length)
			{
				readLength = inputStream.read(buffer, offset, length);
				if (-1 == readLength)
				{
					break;
				}
				offset += readLength;
				length -= readLength;
			}
			return 0 == length;
		}
		catch (IndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			close();
		}
		return false;
	}
	
}
