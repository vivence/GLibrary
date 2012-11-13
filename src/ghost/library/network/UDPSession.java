package ghost.library.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import junit.framework.Assert;
import android.text.TextUtils;

public class UDPSession {
public static final int DEFAULT_TIMEOUT = 20*1000; // seconds
	
	private String host_;
	private int port_;
	private DatagramSocket socket_;

	public UDPSession(String host, int port)
	{
		Assert.assertTrue(!TextUtils.isEmpty(host));
		host_ = host;
		port_ = port;
	}
	
	public DatagramSocket getSocket()
	{
		return socket_;
	}
	
	public boolean connect()
	{
		return connect(DEFAULT_TIMEOUT);
	}
	
	public boolean connect(int timeout)
	{
		try
		{
			if (null == socket_)
			{
				socket_ = new DatagramSocket();
			}
			if (socket_.isConnected())
			{
				return true;
			}
			InetSocketAddress address = new InetSocketAddress(host_, port_);
			socket_.connect(address);
			socket_.setSoTimeout(DEFAULT_TIMEOUT);
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
		close();
		return false;
	}
	
	public void close()
	{
		if (null != socket_ && !socket_.isClosed())
		{
			socket_.close();
			socket_ = null;
		}
	}
	
	public boolean isConnected()
	{
		return null != socket_ ? socket_.isConnected() : false;
	}
	
	public boolean send(byte[] data)
	{
		if (null == data || 0 == data.length)
		{
			return false;
		}
		if (null == socket_)
		{
			return false;
		}
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try
		{
			socket_.send(packet);
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
		if (null == socket_ || !socket_.isConnected())
		{
			if (!connect())
			{
				return false;
			}
		}
		DatagramPacket packet = new DatagramPacket(buffer, offset, length);
		try
		{
			socket_.receive(packet);
		}
		catch (IOException e)
		{
			close();
		}
		return false;
	}
}
