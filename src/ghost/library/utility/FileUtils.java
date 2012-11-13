package ghost.library.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;

import android.text.TextUtils;

public class FileUtils {
	
	public static final int BUFFER_SIZE = 1024*100;
	
	private FileUtils(){}
	
	public static boolean copyFile(InputStream inputStream, OutputStream outputStream)
	{
		byte[] buffer = new byte[BUFFER_SIZE];
		int readLen = 0;
		try
		{
			while (-1 != (readLen = inputStream.read(buffer)))
			{
				outputStream.write(buffer, 0, readLen);
			}
			return true;
		}
		catch (IOException e)
		{
			// TODO: handle exception
		}
		return false;
	}
	
	public static boolean objectToFile(String path, Object obj)
	{
		File file = new File(path);
		if (!file.exists())
		{
			file.mkdirs();
		}
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(path);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(obj);
			objectOutputStream.flush();
			fileOutputStream.flush();
			return true;
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
		} 
		finally 
		{
			if (null != fileOutputStream) 
			{

				try 
				{
					fileOutputStream.close();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}
			}
			if (null != objectOutputStream) 
			{
				try 
				{
					objectOutputStream.close();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}
			}
		}
		return false;
	}
	
	public static Object objectFromFile(String path) 
	{
		if (TextUtils.isEmpty(path)) 
		{
			return null;
		}
		FileInputStream fileInputStream = null;
		ObjectInputStream inputStream = null;
		try 
		{
			fileInputStream = new FileInputStream(path);
			inputStream = new ObjectInputStream(fileInputStream);
			Object object = inputStream.readObject();
			return object;
		} 
		catch (StreamCorruptedException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (ClassNotFoundException e) 
		{
			// TODO Auto-generated catch block
		} 
		finally 
		{
			if (null != inputStream) 
			{
				try 
				{
					inputStream.close();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}
			}
			if (null != fileInputStream) 
			{

				try 
				{
					fileInputStream.close();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}
			}
		}
		return null;
	}
	
	public static boolean bytesToFile(String path, byte[] bytes)
	{
		File file = new File(path);
		if (!file.exists())
		{
			file.mkdirs();
		}
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(path);
			fileOutputStream.write(bytes);
			fileOutputStream.flush();
			return true;
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
		} 
		finally 
		{
			if (null != fileOutputStream) 
			{

				try 
				{
					fileOutputStream.close();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}
			}
		}
		return false;
	}
	
	public static byte[] bytesFromFile(String path) 
	{
		if (TextUtils.isEmpty(path)) 
		{
			return null;
		}
		File file = new File(path);
		if (!file.exists())
		{
			return null;
		}
		FileInputStream fileInputStream = null;
		try 
		{
			byte[] buffer = new byte[(int)file.length()];
			fileInputStream = new FileInputStream(path);
			int offset = 0;
			int restLen = buffer.length;
			while (0 < restLen)
			{
				int readLen = fileInputStream.read(buffer, offset, restLen);
				if (-1 == readLen)
				{
					return null;
				}
				offset += readLen;
				restLen -= readLen;
			}
			return buffer;
		} 
		catch (StreamCorruptedException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
		}
		finally 
		{
			if (null != fileInputStream) 
			{

				try 
				{
					fileInputStream.close();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}
			}
		}
		return null;
	}

}
