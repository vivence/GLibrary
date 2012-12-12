package ghost.library.concurrent;

import ghost.library.error.Error;
import ghost.library.error.ExceptionError;
import ghost.library.error.GenericError;
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
import org.apache.http.NameValuePair;
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
			GET_STRING{
				@Override
				public HttpResponse fetch(HTTPSession session, Request request) throws ExceptionError
				{
					// TODO Auto-generated method stub
					return session.get(request.getURLString(), request.getHeaders());
				}
			},
			GET_BYTES{
				@Override
				public HttpResponse fetch(HTTPSession session, Request request) throws ExceptionError
				{
					// TODO Auto-generated method stub
					return session.get(request.getURLString(), request.getHeaders());
				}
			},
			GET_PROGRESS_BYTES{
				@Override
				public HttpResponse fetch(HTTPSession session, Request request) throws ExceptionError
				{
					// TODO Auto-generated method stub
					return session.get(request.getURLString(), request.getHeaders());
				}
			},
			POST_STRING{
				@Override
				public HttpResponse fetch(HTTPSession session, Request request) throws ExceptionError
				{
					// TODO Auto-generated method stub
					return session.post(request.getURLString(), request.getHeaders(), request.getParams());
				}
			},
			POST_BYTES{
				@Override
				public HttpResponse fetch(HTTPSession session, Request request) throws ExceptionError
				{
					// TODO Auto-generated method stub
					return session.post(request.getURLString(), request.getHeaders(), request.getParams());
				}
			},
			POST_PROGRESS_BYTES{
				@Override
				public HttpResponse fetch(HTTPSession session, Request request) throws ExceptionError
				{
					// TODO Auto-generated method stub
					return session.post(request.getURLString(), request.getHeaders(), request.getParams());
				}
			},
			UPLOAD,
			DOWNLOAD;
			
			public HttpResponse fetch(HTTPSession session, Request request) throws ExceptionError
			{
				return null;
			}
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
			switch (getMethod())
			{
			case GET_STRING:
			case GET_BYTES:
			case GET_PROGRESS_BYTES:
				{
					String params = buildParamsString(getParams());
					if (!TextUtils.isEmpty(params))
					{
						sb.append('?');
						sb.append(params);
					}
				}
				break;
			default:
				break;
			}
			return sb.toString();
		}
		
		protected String buildParamsString(List<NameValuePair> params)
		{
			if (null != params && !params.isEmpty())
			{
				StringBuffer sb = new StringBuffer();
				for (NameValuePair param : params)
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
		public List<NameValuePair> getParams()
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
		
		public String getFilePath()
		{
			return null;
		}
		
		public String getFilePath2()
		{
			return null;
		}

		public Error handleResponse(String string)
		{
			return null;
		}
		
		public Error handleResponse(byte[] bytes)
		{
			return null;
		}
		
		public Error handleResponse(HTTPSession session)
		{
			return null;
		}
		
	}
	
	public static final int ERROR_CODE_NULL_RESPONSE = 1;
	public static final int ERROR_CODE_HTTP_STATUS_CODE = 2;
	public static final int ERROR_CODE_NULL_ENTITY = 3;
	public static final int ERROR_CODE_EMPTY_ENTITY = 4;
	public static final int ERROR_CODE_READ = 5;
	public static final int ERROR_CODE_ARGUMENTS = 6;
	public static final int ERROR_CODE_NULL_OUTPUT_STREAM = 7;
	public static final int ERROR_CODE_FILE_SIZE_NOT_EQUAL = 8;
	public static final int ERROR_CODE_RENAME_FILE = 9;
	
	public static final Error E_NULL_RESPONSE = new GenericError(ERROR_CODE_NULL_RESPONSE, "null response");
	public static final Error E_NULL_ENTITY = new GenericError(ERROR_CODE_NULL_ENTITY, "null entity");
	public static final Error E_EMPTY_ENTITY = new GenericError(ERROR_CODE_EMPTY_ENTITY, "empty entity");
	public static final Error E_READ = new GenericError(ERROR_CODE_EMPTY_ENTITY, "read error");
	public static final Error E_NULL_OUTPUT_STREAM = new GenericError(ERROR_CODE_NULL_OUTPUT_STREAM, "null output stream");
	public static final Error E_FILE_SIZE_NOT_EQUAL = new GenericError(ERROR_CODE_FILE_SIZE_NOT_EQUAL, "file size not equal");
	public static final Error E_RENAME_FILE = new GenericError(ERROR_CODE_RENAME_FILE, "rename file error");
	
	public static final ThreadLocal<HTTPSession> TLS_HTTP_SESSION = new ThreadLocal<HTTPSession>();
	
	private Request request_;
	
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
	
	private void fetchString(HTTPSession session, Request.Method method) throws InterruptedException
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
					if (null == request_.handleResponse(object))
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
		HttpResponse response = null;
		try
		{
			response = method.fetch(session, request_);
		}
		catch (ExceptionError e)
		{
			// TODO: handle exception
			setError(e);
			return;
		}
		
		if (null == response)
		{
			setError(E_NULL_RESPONSE);
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
				setError(new GenericError(ERROR_CODE_HTTP_STATUS_CODE, String.valueOf(statusCode)));
				return;
			}
		}
		HttpEntity entity = response.getEntity();
		if (null == entity)
		{
			setError(E_NULL_ENTITY);
			return;
		}
		try
		{
			String object = EntityUtils.toString(entity, StringUtils.CHAR_SET_UTF_8);
			if (TextUtils.isEmpty(object))
			{
				setError(E_EMPTY_ENTITY);
				return;
			}
			if (!TextUtils.isEmpty(cachePath))
			{
				FileUtils.objectToFile(cachePath, object);
			}
			setError(request_.handleResponse(object));
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			setError(new ExceptionError(e));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			setError(new ExceptionError(e));
		}
	}
	
	private void fetchBytes(HTTPSession session, Request.Method method) throws InterruptedException
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
				if (null == request_.handleResponse(bytes))
				{
					return;
				}
				new File(cachePath).delete();
			}
		}
		HttpResponse response = null;
		try
		{
			response = method.fetch(session, request_);
		}
		catch (ExceptionError e)
		{
			// TODO Auto-generated catch block
			setError(e);
			return;
		}
		if (null == response)
		{
			setError(E_NULL_RESPONSE);
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
				setError(new GenericError(ERROR_CODE_HTTP_STATUS_CODE, String.valueOf(statusCode)));
				return;
			}
		}
		HttpEntity entity = response.getEntity();
		if (null == entity)
		{
			setError(E_NULL_ENTITY);
			return;
		}
		try
		{
			byte[] bytes = EntityUtils.toByteArray(entity);
			if (null == bytes)
			{
				setError(E_EMPTY_ENTITY);
				return;
			}
			if (!TextUtils.isEmpty(cachePath))
			{
				FileUtils.bytesToFile(cachePath, bytes);
			}
			setError(request_.handleResponse(bytes));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			setError(new ExceptionError(e));
		}
	}
	
	private void fetchProgressBytes(HTTPSession session, Request.Method method) throws InterruptedException
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
				if (null == request_.handleResponse(bytes))
				{
					return;
				}
				new File(cachePath).delete();
			}
		}
		setProgress(0, -1);
		HttpResponse response = null;
		try
		{
			response = method.fetch(session, request_);
		}
		catch (ExceptionError e)
		{
			// TODO: handle exception
			setError(e);
			return;
		}
		if (null == response)
		{
			setError(E_NULL_RESPONSE);
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
				setError(new GenericError(ERROR_CODE_HTTP_STATUS_CODE, String.valueOf(statusCode)));
				return;
			}
		}
		HttpEntity entity = response.getEntity();
		if (null == entity)
		{
			setError(E_NULL_ENTITY);
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
				setError(request_.handleResponse(bytes));
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
						setError(E_READ);
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
				setError(request_.handleResponse(buffer));
			}
		}
		catch (IllegalStateException e)
		{
			// TODO Auto-generated catch block
			setError(new ExceptionError(e));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			setError(new ExceptionError(e));
		}
	}
	
	private void upload(HTTPSession session) throws InterruptedException
	{
		String filePath = request_.getFilePath();
		if (TextUtils.isEmpty(filePath))
		{
			setError(new GenericError(ERROR_CODE_ARGUMENTS, "upload without file path"));
			return;
		}
		File file = new File(filePath);
		if (!file.exists())
		{
			setError(new GenericError(ERROR_CODE_ARGUMENTS, "upload file isn't exist"));
			return;
		}
		int contentLen = (int)file.length();
		if (0 >= contentLen)
		{
			setError(new GenericError(ERROR_CODE_ARGUMENTS, "upload file size is 0"));
			return;
		}
		setProgress(0, contentLen);
		OutputStream outputStream = null;
		try
		{
			outputStream = session.upload(
					request_.getURLString(), 
					request_.getHeaders(),
					request_.getParams(),
					file);
		}
		catch (ExceptionError e)
		{
			// TODO: handle exception
			setError(e);
			return;
		}
		
		if (null == outputStream)
		{
			setError(E_NULL_OUTPUT_STREAM);
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
					setError(E_READ);
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
			setError(request_.handleResponse(session));
		}
		catch (IllegalStateException e)
		{
			// TODO: handle exception
			setError(new ExceptionError(e));
		}
		catch (FileNotFoundException e)
		{
			setError(new ExceptionError(e));
		}
		catch (IOException e) 
		{
			// TODO: handle exception
			setError(new ExceptionError(e));
		}
		catch (ExceptionError e)
		{
			// TODO Auto-generated catch block
			setError(e);
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
			setError(new GenericError(ERROR_CODE_ARGUMENTS, "download without file path"));
			return;
		}
		File cacheFile = new File(cachePath);
		if (cacheFile.exists())
		{
			if (null == request_.handleResponse(cachePath))
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
				if (null == request_.handleResponse(cachePath2))
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
		HttpResponse response = null;
		try
		{
			response = session.get(request_.getURLString(), request_.getHeaders());
		}
		catch (ExceptionError e)
		{
			// TODO: handle exception
			setError(e);
			return;
		}
		
		if (null == response)
		{
			setError(E_NULL_RESPONSE);
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
				setError(new GenericError(ERROR_CODE_HTTP_STATUS_CODE, String.valueOf(statusCode)));
				return;
			}
		}
		HttpEntity entity = response.getEntity();
		if (null == entity)
		{
			setError(E_NULL_ENTITY);
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
				setError(E_FILE_SIZE_NOT_EQUAL);
				return;
			}
			if (!tempFile.renameTo(cacheFile))
			{
				setError(E_RENAME_FILE);
				return;
			}
			setError(request_.handleResponse(cachePath));
		}
		catch (IllegalStateException e)
		{
			// TODO Auto-generated catch block
			setError(new ExceptionError(e));
		}
		catch (FileNotFoundException e) 
		{
			// TODO: handle exception
			setError(new ExceptionError(e));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			setError(new ExceptionError(e));
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
				case POST_STRING:
					fetchString(httpSession, request_.getMethod());
					break;
				case GET_BYTES:
				case POST_BYTES:
					fetchBytes(httpSession, request_.getMethod());
					break;
				case GET_PROGRESS_BYTES:
				case POST_PROGRESS_BYTES:
					fetchProgressBytes(httpSession, request_.getMethod());
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
