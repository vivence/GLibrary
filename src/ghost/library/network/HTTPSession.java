package ghost.library.network;

import ghost.library.error.ExceptionError;
import ghost.library.utility.Log;
import ghost.library.utility.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.text.TextUtils;

public class HTTPSession {
	
	public static final String LOG_TAG = "http_session";
	
	public static final int DEFAULT_TIMEOUT = 20*1000; // seconds
	
	public static final String PREFIX = "----------------------------";
	public static final String LINEND = "\r\n";
	public static final String SUUID = java.util.UUID.randomUUID().toString();
	public static final String BOUNDARY = PREFIX+SUUID.substring(SUUID.length() - 12);
	public static final String CONTENT_TYPE = "multipart/form-data;boundary="+BOUNDARY;
	public static final String UPLOAD_END_STRING = LINEND + "--" + BOUNDARY + "--";
	public static final byte[] UPLOAD_END_DATA;
	
	static{
		byte[] uploadEndData = null;
		try
		{
			uploadEndData = UPLOAD_END_STRING.getBytes(StringUtils.CHAR_SET_UTF_8);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			uploadEndData = UPLOAD_END_STRING.getBytes();
		}
		UPLOAD_END_DATA = uploadEndData;
	}

	private DefaultHttpClient httpClient_;
	private HttpUriRequest request_;
	private HttpURLConnection connection_;
	
	public HTTPSession()
	{
		// TODO Auto-generated constructor stub
		 HttpParams httpParams = new BasicHttpParams();
		 HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT);
		 HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_TIMEOUT);
		 SchemeRegistry schemeRegistry = new SchemeRegistry();
		 schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		 schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		 httpClient_ = new DefaultHttpClient(
				 new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams);
	}
	
	public CookieStore getCookieStore()
	{
		return httpClient_.getCookieStore();
	}
	
	public void setCookieStore(CookieStore cookieStore)
	{
		httpClient_.setCookieStore(cookieStore);
	}
	
	public void abort()
	{
		if (null != request_)
		{
			request_.abort();
			request_ = null;
		}
		if (null != connection_)
		{
			connection_.disconnect();
			connection_ = null;
		}
	}

	public HttpResponse get(String urlString, List<Header> headers) throws ExceptionError
	{
		Assert.assertTrue(!TextUtils.isEmpty(urlString));
		Log.i(LOG_TAG, "http-get: "+urlString);
		try 
		{
			if (null != request_)
			{
				request_.abort();
			}
//			urlString = URLEncoder.encode(urlString, StringUtils.CHAR_SET_UTF_8);
			request_ = new HttpGet(urlString);
			if (null != headers) 
			{
				for (Header header : headers) 
				{
					request_.addHeader(header);
				}
			}
			HttpResponse response = httpClient_.execute(request_);
			if (null != response)
			{
				Log.d(LOG_TAG, "http-get response: "+response.getStatusLine().getStatusCode());
			}
			return response;
		} 
		catch (IllegalArgumentException e) 
		{
			// TODO: handle exception
			throw new ExceptionError(e);
		} 
		catch (ClientProtocolException e) 
		{
			// TODO Auto-generated catch block
			throw new ExceptionError(e);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ExceptionError(e);
		}
		catch (IOException e) 
		{
			// TODO: handle exception
			throw new ExceptionError(e);
		}
	}
	
	public HttpResponse post(String urlString, List<Header> headers, List<NameValuePair> params) throws ExceptionError
	{
		Assert.assertTrue(!TextUtils.isEmpty(urlString));
		Log.i(LOG_TAG, "http-post: "+urlString);
		try 
		{
			if (null != request_)
			{
				request_.abort();
			}
//			urlString = URLEncoder.encode(urlString, StringUtils.CHAR_SET_UTF_8);
			request_ = new HttpPost(urlString);
			if (null != headers) 
			{
				for (Header header : headers) 
				{
					request_.addHeader(header);
				}
			}
			if (null != params && !params.isEmpty())
			{
				((HttpPost)request_).setEntity(new UrlEncodedFormEntity(params, StringUtils.CHAR_SET_UTF_8));
			}
			HttpResponse response = httpClient_.execute(request_);
			if (null != response)
			{
				Log.d(LOG_TAG, "http-post response: "+response.getStatusLine().getStatusCode());
			}
			return response;
		} 
		catch (IllegalArgumentException e) 
		{
			// TODO: handle exception
			throw new ExceptionError(e);
		} 
		catch (ClientProtocolException e) 
		{
			// TODO Auto-generated catch block
			throw new ExceptionError(e);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ExceptionError(e);
		}
		catch (IOException e) 
		{
			// TODO: handle exception
			throw new ExceptionError(e);
		}
	}
	
	private static <T> void appendParam(StringBuffer sb, String paramName, T paramValue) {
		sb.append("--" + BOUNDARY);
		sb.append(LINEND);
		sb.append("Content-Disposition: form-data; name=\"" + paramName + "\""
				+ LINEND);
		sb.append(LINEND);
		sb.append(paramValue);
		sb.append(LINEND);
	}

	public OutputStream upload(
			String urlString, 
			List<Header> headers, 
			List<NameValuePair> params,
			File file) throws ExceptionError
	{
		Assert.assertTrue(!TextUtils.isEmpty(urlString) && null != file);
		Log.i(LOG_TAG, "http-post: "+urlString);
		if (null == headers)
		{
			headers = new LinkedList<Header>();
		}
		headers.add(new BasicHeader("Expect", "100-continue"));
		headers.add(new BasicHeader("Charset", StringUtils.CHAR_SET_UTF_8));
		headers.add(new BasicHeader("Accept", "*/*"));
		headers.add(new BasicHeader("connection", "keep-Alive"));
		headers.add(new BasicHeader("Accept-Encoding", "identity,deflate,gzip"));
		headers.add(new BasicHeader("Content-Type", CONTENT_TYPE));
		boolean succeed = false;
		try 
		{
			if (null != connection_)
			{
				connection_.disconnect();
			}
//			urlString = URLEncoder.encode(urlString, StringUtils.CHAR_SET_UTF_8);
			URL url = new URL(urlString);
			connection_ = (HttpURLConnection)url.openConnection();
			connection_.setDoInput(true);
			connection_.setDoOutput(true);
			connection_.setUseCaches(false);
			connection_.setRequestMethod(HttpPost.METHOD_NAME);

			if (null != headers) 
			{
				for (Header header : headers) 
				{
					connection_.setRequestProperty(
							header.getName(), 
							header.getValue());
				}
			}
			byte[] paramsData = null;
			if (null != params)
			{
				StringBuffer sb = new StringBuffer();
				for (NameValuePair param : params) 
				{
					appendParam(sb, param.getName(), param.getValue());
				}
				
				sb.append("--" + BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"");
				sb.append(file.getPath());
				sb.append("\"" + LINEND);
				sb.append("Content-Type: application/octet-stream" + LINEND);
				sb.append(LINEND);
				
				String paramsString = sb.toString();
				try
				{
					paramsData = paramsString.getBytes(StringUtils.CHAR_SET_UTF_8);
				}
				catch (UnsupportedEncodingException e)
				{
					// TODO Auto-generated catch block
					paramsData = paramsString.getBytes();
				}
			}
			long fixLength = file.length()+UPLOAD_END_DATA.length;
			if (null != paramsData)
			{
				fixLength += paramsData.length;
			}
			connection_.setFixedLengthStreamingMode((int)fixLength);
			connection_.connect();
			OutputStream outputStream = connection_.getOutputStream();
			if (null != outputStream)
			{
				outputStream.write(paramsData);
			}
			succeed = true;
			return outputStream;
		} 
		catch (ClassCastException e) 
		{
			throw new ExceptionError(e);
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			throw new ExceptionError(e);
		}
		catch (UnsupportedEncodingException e) 
		{
			// TODO: handle exception
			throw new ExceptionError(e);
		}
		catch (IOException e) 
		{
			// TODO: handle exception
			throw new ExceptionError(e);
		}
		finally
		{
			if (!succeed)
			{
				if (null != connection_)
				{
					connection_.disconnect();
					connection_ = null;
				}
			}
		}
	}

	public boolean endUpload(OutputStream outputStream)  throws ExceptionError
	{
		try
		{
			outputStream.write(UPLOAD_END_DATA);
			outputStream.flush();
			return true;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			throw new ExceptionError(e);
		}
	}
	
	public void closeUpload()
	{
		if (null != connection_)
		{
			connection_.disconnect();
			connection_ = null;
		}
	}

}
