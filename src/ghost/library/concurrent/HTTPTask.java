package ghost.library.concurrent;

import ghost.library.network.HTTPSession;
import ghost.library.utility.FileUtils;
import ghost.library.utility.Range;
import ghost.library.utility.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;

public final class HTTPTask extends Task implements Runnable {
	
	public static final int BUFFER_SIZE = 1024;
	public static final String TEMP_FILE_SUFFIX = ".tmp";
	
	public static abstract class Request {
		
		public static interface ResponseReceiver{
			public void onResponse(Request request, Object response, Object tag);
		}
		private ResponseReceiver responseReceiver_;
		private Object responseTag_;
		
		public final boolean setResponseReceiver(ResponseReceiver receiver, Object tag)
		{
			if (null != responseReceiver_)
			{
				return false;
			}
			responseReceiver_ = receiver;
			responseTag_ = tag;
			return true;
		}
		
		protected void notifyResponseReceiver(Object response)
		{
			if (null != response && null != responseReceiver_)
			{
				responseReceiver_.onResponse(this, response, responseTag_);
				responseReceiver_ = null;
				responseTag_ = null;
			}
		}
		
		public static enum Method{
			GET_STRING,
			GET_BYTES,
			GET_PROGRESS_BYTES,
			UPLOAD,
			DOWNLOAD
		}
		
		private Range range_;
		
		public abstract Method getMethod();
		
		public void setRange(Range range) 
		{
			range_ = range;
		}

		public void setRange(int b, int e) 
		{
			if (null == range_) 
			{
				range_ = new Range(b, e);
			} 
			else 
			{
				range_.begin = b;
				range_.end = e;
			}
		}

		public void setRangeBegin(int b) 
		{
			if (null == range_) 
			{
				range_ = new Range();
			}
			range_.begin = b;
		}

		public void setRangeEnd(int e) 
		{
			if (null == range_) 
			{
				range_ = new Range();
			}
			range_.end = e;
		}
		
		public final String getURLString()
		{
			StringBuffer sb = new StringBuffer(getHost());
			String params = buildParamsString(getURLParams());
			if (!TextUtils.isEmpty(params))
			{
				sb.append('?');
				sb.append(params);
			}
			return sb.toString();
		}
		
		protected String buildParamsString(List<Header> params)
		{
			if (null != params && !params.isEmpty())
			{
				StringBuffer sb = new StringBuffer();
				for (Header param : params)
				{
					if (0 < sb.length())
					{
						sb.append('&');
					}
					sb.append(param.getName());
					sb.append('=');
					sb.append(StringUtils.encodeURLIfPossible(param.getValue(), StringUtils.CHAR_SET_UTF_8));
				}
				return sb.toString();
			}
			return null;
		}
		
		public abstract String getHost();
		public List<Header> getURLParams()
		{
			return null;
		}
		
		public String getUserAgent()
		{
			return null;
		}
		public List<Header> getHeaders() 
		{
			List<Header> headers = new LinkedList<Header>();
			String userAgent = getUserAgent();
			if (!TextUtils.isEmpty(userAgent))
			{
				headers.add(new BasicHeader("User-Agent", userAgent));
			}
			
			if (null != range_ && 0 < range_.begin) 
			{
				StringBuffer sb = new StringBuffer();
				sb.append(range_.begin);
				if (range_.begin < range_.end) 
				{
					sb.append("-");
					sb.append(range_.end);
				}
				headers.add(new BasicHeader("Range", sb.toString()));
			}
			return headers;
		}
		
		public List<Header> getParams()
		{
			return null;
		}
		
		public String getFilePath()
		{
			return null;
		}
		
		public String getFilePath2()
		{
			return null;
		}

		public boolean handleResponse(String string)
		{
			notifyResponseReceiver(string);
			return true;
		}
		
		public boolean handleResponse(byte[] bytes)
		{
			notifyResponseReceiver(bytes);
			return true;
		}
		
		public boolean handleResponse(HTTPSession session)
		{
			return true;
		}
		
	}
	
	public static class Result{
		public static enum Error{
			NETWORK,
			HTTP,
			NO_ENTITY,
			PARSE,
			OTHER
		}
		public final Error error;
		public final int code;
		
		public Result(Error error, int code)
		{
			// TODO Auto-generated constructor stub
			this.error = error;
			this.code = code;
		}
	}
	
	public static final ThreadLocal<HTTPSession> TLS_HTTP_SESSION = new ThreadLocal<HTTPSession>();
	
	private Request request_;
	private Result result_;
	
	public HTTPTask(Request request)
	{
		// TODO Auto-generated constructor stub
		Assert.assertNotNull(request);
		request_ = request;
	}
	
	public void reset(Request request)
	{
		Assert.assertNotNull(request);
		request_ = request;
		reset();
	}
	
	@Override
	public synchronized boolean reset()
	{
		// TODO Auto-generated method stub
		if (super.reset())
		{
			result_ = null;
			return true;
		}
		return false;
	}
	
	public Result getResult()
	{
		return result_;
	}
	
	public HTTPSession getTLSHttpSession()
	{
		HTTPSession httpSession = TLS_HTTP_SESSION.get();
		if (null == httpSession)
		{
			httpSession = new HTTPSession();
			TLS_HTTP_SESSION.set(httpSession);
		}
		return httpSession;
	}
	
	private void getString(HTTPSession session) throws InterruptedException
	{
		String cachePath = request_.getFilePath();
		if (TextUtils.isEmpty(cachePath))
		{
			cachePath = request_.getFilePath2();
		}
		if (!TextUtils.isEmpty(cachePath))
		{
			try
			{
				String object = (String)FileUtils.objectFromFile(cachePath);
				if (null != object)
				{
					if (request_.handleResponse(object))
					{
						return;
					}
				}
			}
			catch (ClassCastException e)
			{
				// TODO: handle exception
			}
			new File(cachePath).delete();
		}
		HttpResponse response = session.get(request_.getURLString(), request_.getHeaders());
		if (null == response)
		{
			result_ = new Result(Result.Error.NETWORK, 0);
			return;
		}
		else
		{
			int statusCode = response.getStatusLine().getStatusCode();
			switch (statusCode)
			{
			case HttpStatus.SC_OK:
				break;
			default:
				result_ = new Result(Result.Error.HTTP, statusCode);
				return;
			}
		}
		HttpEntity entity = response.getEntity();
		if (null == entity)
		{
			result_ = new Result(Result.Error.NO_ENTITY, 0);
			return;
		}
		try
		{
			String object = EntityUtils.toString(entity, StringUtils.CHAR_SET_UTF_8);
			if (TextUtils.isEmpty(object))
			{
				result_ = new Result(Result.Error.NO_ENTITY, 0);
				return;
			}
			if (!TextUtils.isEmpty(cachePath))
			{
				FileUtils.objectToFile(cachePath, object);
			}
			request_.handleResponse(object);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			result_ = new Result(Result.Error.PARSE, 0);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			result_ = new Result(Result.Error.NETWORK, 0);
		}
	}
	
	private void getBytes(HTTPSession session) throws InterruptedException
	{
		String cachePath = request_.getFilePath();
		if (TextUtils.isEmpty(cachePath))
		{
			cachePath = request_.getFilePath2();
		}
		if (!TextUtils.isEmpty(cachePath))
		{
			byte[] bytes = FileUtils.bytesFromFile(cachePath);
			if (null != bytes)
			{
				if (request_.handleResponse(bytes))
				{
					return;
				}
				new File(cachePath).delete();
			}
		}
		HttpResponse response = session.get(request_.getURLString(), request_.getHeaders());
		if (null == response)
		{
			result_ = new Result(Result.Error.NETWORK, 0);
			return;
		}
		else
		{
			int statusCode = response.getStatusLine().getStatusCode();
			switch (statusCode)
			{
			case HttpStatus.SC_OK:
				break;
			default:
				result_ = new Result(Result.Error.HTTP, statusCode);
				return;
			}
		}
		HttpEntity entity = response.getEntity();
		if (null == entity)
		{
			result_ = new Result(Result.Error.NO_ENTITY, 0);
			return;
		}
		try
		{
			byte[] bytes = EntityUtils.toByteArray(entity);
			if (null == bytes)
			{
				result_ = new Result(Result.Error.NO_ENTITY, 0);
				return;
			}
			if (!TextUtils.isEmpty(cachePath))
			{
				FileUtils.bytesToFile(cachePath, bytes);
			}
			request_.handleResponse(bytes);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			result_ = new Result(Result.Error.NETWORK, 0);
		}
	}
	
	private void getProgressBytes(HTTPSession session) throws InterruptedException
	{
		String cachePath = request_.getFilePath();
		if (TextUtils.isEmpty(cachePath))
		{
			cachePath = request_.getFilePath2();
		}
		if (!TextUtils.isEmpty(cachePath))
		{
			byte[] bytes = FileUtils.bytesFromFile(cachePath);
			if (null != bytes)
			{
				if (request_.handleResponse(bytes))
				{
					return;
				}
				new File(cachePath).delete();
			}
		}
		setProgress(0, -1);
		HttpResponse response = session.get(request_.getURLString(), request_.getHeaders());
		if (null == response)
		{
			result_ = new Result(Result.Error.NETWORK, 0);
			return;
		}
		else
		{
			int statusCode = response.getStatusLine().getStatusCode();
			switch (statusCode)
			{
			case HttpStatus.SC_OK:
				break;
			default:
				result_ = new Result(Result.Error.HTTP, statusCode);
				return;
			}
		}
		HttpEntity entity = response.getEntity();
		if (null == entity)
		{
			result_ = new Result(Result.Error.NO_ENTITY, 0);
			return;
		}
		try
		{
			InputStream inputStream = entity.getContent();
			Header header = response.getLastHeader("Content-Length");
			int contentLen = -1;
			if (null != header)
			{
				contentLen = Integer.parseInt(header.getValue());
			}
			if (-1 == contentLen)
			{
				byte[] buffer = new byte[BUFFER_SIZE];
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				int readLen = 0;
				while (-1 != (readLen = inputStream.read(buffer)))
				{
					if (isCanceled() || isAborted())
					{
						return;
					}
					outputStream.write(buffer, 0, readLen);
					outputStream.flush();
					setProgress(outputStream.size(), -1);
				}
				byte[] bytes = outputStream.toByteArray();
				if (!TextUtils.isEmpty(cachePath))
				{
					FileUtils.bytesToFile(cachePath, bytes);
				}
				request_.handleResponse(bytes);
			}
			else
			{
				byte[] buffer = new byte[contentLen];
				int offset = 0;
				int restLen = contentLen;
				setProgress(0, contentLen);
				while (0 < restLen)
				{
					int readLen = inputStream.read(buffer, offset, restLen);
					if (isCanceled() || isAborted())
					{
						return;
					}
					if (-1 == readLen)
					{
						result_ = new Result(Result.Error.OTHER, 0);
						return;
					}
					offset += readLen;
					restLen -= readLen;
					setProgress(offset, contentLen);
				}
				if (!TextUtils.isEmpty(cachePath))
				{
					FileUtils.bytesToFile(cachePath, buffer);
				}
				request_.handleResponse(buffer);
			}
		}
		catch (IllegalStateException e)
		{
			// TODO Auto-generated catch block
			result_ = new Result(Result.Error.OTHER, 0);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			result_ = new Result(Result.Error.NETWORK, 0);
		}
	}
	
	private void upload(HTTPSession session) throws InterruptedException
	{
		String filePath = request_.getFilePath();
		if (TextUtils.isEmpty(filePath))
		{
			result_ = new Result(Result.Error.OTHER, 0);
			return;
		}
		File file = new File(filePath);
		if (!file.exists())
		{
			result_ = new Result(Result.Error.OTHER, 0);
			return;
		}
		int contentLen = (int)file.length();
		if (0 >= contentLen)
		{
			result_ = new Result(Result.Error.OTHER, 0);
			return;
		}
		setProgress(0, contentLen);
		OutputStream outputStream = session.upload(
				request_.getURLString(), 
				request_.getHeaders(),
				request_.getParams(),
				file);
		if (null == outputStream)
		{
			result_ = new Result(Result.Error.NETWORK, 0);
			return;
		}
		FileInputStream fileInputStream = null;
		try
		{
			fileInputStream = new FileInputStream(file);
			
			byte[] buffer = new byte[BUFFER_SIZE];
			int offset = 0;
			int restLen = contentLen;
			while (0 < restLen)
			{
				int readLen = fileInputStream.read(buffer);
				if (isCanceled() || isAborted())
				{
					return;
				}
				if (-1 == readLen)
				{
					result_ = new Result(Result.Error.OTHER, 0);
					return;
				}
				outputStream.write(buffer, 0, readLen);
				outputStream.flush();
				if (isCanceled() || isAborted())
				{
					return;
				}
				offset += readLen;
				restLen -= readLen;
				setProgress(offset, contentLen);
			}
			fileInputStream.close();
			fileInputStream = null;
			
			session.endUpload(outputStream);
			outputStream.close();
			outputStream = null;
			request_.handleResponse(session);
		}
		catch (IllegalStateException e)
		{
			// TODO: handle exception
			result_ = new Result(Result.Error.OTHER, 0);
		}
		catch (FileNotFoundException e)
		{
			result_ = new Result(Result.Error.OTHER, 0);
		}
		catch (IOException e) 
		{
			// TODO: handle exception
			result_ = new Result(Result.Error.NETWORK, 0);
		}
		finally
		{
			if (null != outputStream)
			{
				try
				{
					outputStream.close();
				}
				catch (IOException e)
				{
					// TODO: handle exception
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
					// TODO: handle exception
				}
			}
			if (null != session)
			{
				session.closeUpload();
			}
		}
	}
	
	private void download(HTTPSession session) throws InterruptedException
	{
		String cachePath = request_.getFilePath();
		if (TextUtils.isEmpty(cachePath))
		{
			result_ = new Result(Result.Error.OTHER, 0);
			return;
		}
		File cacheFile = new File(cachePath);
		if (cacheFile.exists())
		{
			if (request_.handleResponse(cachePath))
			{
				return;
			}
			cacheFile.delete();
		}
		else
		{
			String cachePath2 = request_.getFilePath2();
			File cacheFile2 = new File(cachePath2);
			if (cacheFile2.exists())
			{
				if (request_.handleResponse(cachePath2))
				{
					return;
				}
				cacheFile2.delete();
			}
		}
		int downloadLen = 0;
		String tempPath = cachePath+TEMP_FILE_SUFFIX;
		File tempFile = new File(tempPath);
		if (tempFile.exists())
		{
			downloadLen += (int)tempFile.length();
			request_.setRangeBegin(downloadLen);
		}
		setProgress(0, -1);
		HttpResponse response = session.get(request_.getURLString(), request_.getHeaders());
		if (null == response)
		{
			result_ = new Result(Result.Error.NETWORK, 0);
			return;
		}
		else
		{
			int statusCode = response.getStatusLine().getStatusCode();
			switch (statusCode)
			{
			case HttpStatus.SC_OK:
			case HttpStatus.SC_PARTIAL_CONTENT:
				break;
			default:
				result_ = new Result(Result.Error.HTTP, statusCode);
				return;
			}
		}
		HttpEntity entity = response.getEntity();
		if (null == entity)
		{
			result_ = new Result(Result.Error.OTHER, 0);
			return;
		}
		FileOutputStream outputStream = null;
		try
		{
			InputStream inputStream = entity.getContent();
			Header header = response.getLastHeader("Content-Length");
			int contentLen = -1;
			if (null != header)
			{
				contentLen = Integer.parseInt(header.getValue());
			}
			if (0 <= contentLen)
			{
				contentLen += downloadLen;
			}
			outputStream = new FileOutputStream(tempFile, 0 < downloadLen);
			byte[] buffer = new byte[BUFFER_SIZE];
			int readLen = 0;
			while (-1 != (readLen = inputStream.read(buffer)))
			{
				if (isCanceled() || isAborted())
				{
					return;
				}
				outputStream.write(buffer, 0, readLen);
				outputStream.flush();
				downloadLen += readLen;
				setProgress(downloadLen, contentLen);
			}
			if (0 <= contentLen && downloadLen != contentLen)
			{
				result_ = new Result(Result.Error.OTHER, 0);
				return;
			}
			if (!tempFile.renameTo(cacheFile))
			{
				result_ = new Result(Result.Error.OTHER, 0);
				return;
			}
			request_.handleResponse(cachePath);
		}
		catch (IllegalStateException e)
		{
			// TODO Auto-generated catch block
			result_ = new Result(Result.Error.OTHER, 0);
		}
		catch (FileNotFoundException e) 
		{
			// TODO: handle exception
			result_ = new Result(Result.Error.OTHER, 0);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			result_ = new Result(Result.Error.NETWORK, 0);
		}
		finally
		{
			if (null != outputStream)
			{
				try
				{
					outputStream.close();
				}
				catch (IOException e)
				{
					// TODO: handle exception
				}
				outputStream = null;
			}
		}
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		final HTTPSession httpSession = getTLSHttpSession();
		final Thread thread = Thread.currentThread();
		if (execute(new Context() {
			
			@Override
			public void interrupt()
			{
				// TODO Auto-generated method stub
				httpSession.abort();
				thread.interrupt();
			}
		}))
		{
			try
			{
				switch (request_.getMethod())
				{
				case GET_STRING:
					getString(httpSession);
					break;
				case GET_BYTES:
					getBytes(httpSession);
					break;
				case GET_PROGRESS_BYTES:
					getProgressBytes(httpSession);
					break;
				case UPLOAD:
					upload(httpSession);
					break;
				case DOWNLOAD:
					download(httpSession);
					break;
				default:
					break;
				}
			}
			catch (InterruptedException e)
			{
				// TODO: handle exception
			}
			finally
			{
				finish();
			}
		}
	}

}
