package ghost.library.utility;

import ghost.library.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;

import junit.framework.Assert;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Patterns;

public class StringUtils {
	
	private StringUtils(){}
	
	public static final String CHAR_SET_UTF_8 = "UTF-8";
	public static final String CHAR_SET_US_ASCII = "US-ASCII";
	
	public static String formatHTMLColor(Resources resources, int color)
	{
		return String.format(
						resources.getString(R.string.f_html_font_color), 
						color&0x00FFFFFF);
	}
	
	public static String toPinYin(String str)
	{
		if (TextUtils.isEmpty(str))
		{
			return str;
		}
		StringBuffer sb = new StringBuffer();
		
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		for (int i = 0; i < str.length(); i++)
		{
			char c = str.charAt(i);
			if(String.valueOf(c).matches("[\\u4E00-\\u9FA5]+"))
			{  
				try
				{
					String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, format);
					for (int j = 0; j < pinyins.length; j++)
					{
						sb.append(pinyins[j]);
					}
				}
				catch (BadHanyuPinyinOutputFormatCombination e)
				{
					// TODO: handle exception
					e.printStackTrace();
				}
		    }
			else if('A' <= c && 'Z' >= c)
			{  
				sb.append(c);
			}
			else if('a' <= c && 'z' >= c)
			{  
				sb.append(Character.toUpperCase(c));
			}
		}
		
		if (0 == sb.length())
		{
			sb.append('#');
		}
		
		return sb.toString();
	}
	
	public static String decodeIfPossible(byte[] bytes, String charSet)
	{
		if (null == bytes)
		{
			return null;
		}
		if (TextUtils.isEmpty(charSet))
		{
			return new String(bytes);
		}
		try
		{
			return new String(bytes, charSet);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			return new String(bytes);
		}
	}
	
	public static byte[] encodeIfPossible(String string, String charSet)
	{
		if (TextUtils.isEmpty(string))
		{
			return null;
		}
		if (TextUtils.isEmpty(charSet))
		{
			return string.getBytes();
		}
		try
		{
			return string.getBytes(charSet);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			return string.getBytes();
		}
	}
	
	public static String encodeURLIfPossible(String string, String charSet)
	{
		if (TextUtils.isEmpty(string))
		{
			return null;
		}
		if (TextUtils.isEmpty(charSet))
		{
			charSet = StringUtils.CHAR_SET_UTF_8;
		}
		try
		{
			return URLEncoder.encode(string, charSet);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			return string;
		}
	}

	public static String appenFilePath(String path, String subPath)
	{
		if (TextUtils.isEmpty(path) || TextUtils.isEmpty(subPath))
		{
			return path;
		}
		if (File.separatorChar == path.charAt(path.length() - 1)) 
		{
			if (File.separatorChar == subPath.charAt(0)) 
			{
				return path + subPath.substring(1);
			} 
			else 
			{
				return path + subPath;
			}
		} 
		else 
		{
			if (File.separatorChar == subPath.charAt(0)) 
			{
				return path + subPath;
			} 
			else 
			{
				return path + File.separatorChar + subPath;
			}
		}
	}
	
	public static void appenFilePath(StringBuffer buffer, String subPath)
	{
		if (null == buffer || TextUtils.isEmpty(subPath))
		{
			return;
		}
		if (0 == buffer.length())
		{
			if (File.separatorChar == subPath.charAt(0)) 
			{
				buffer.append(subPath.substring(1));
			}
			else 
			{
				buffer.append(subPath);
			}
			return;
		}
		else if (File.separatorChar == buffer.charAt(buffer.length() - 1)) 
		{
			if (File.separatorChar == subPath.charAt(0)) 
			{
				buffer.append(subPath.substring(1));
			} 
			else 
			{
				buffer.append(subPath);
			}
			return;
		} 
		else 
		{
			if (File.separatorChar == subPath.charAt(0)) 
			{
				buffer.append(subPath);
			} 
			else 
			{
				buffer.append(File.separatorChar);
				buffer.append(subPath);
			}
			return;
		}
	}
	
	public static int calcFullWidthCount(String string)
	{
		if (TextUtils.isEmpty(string))
		{
			return 0;
		}
		try
		{
			byte[] bytes = string.getBytes(CHAR_SET_UTF_8);
			int halfCount = 0;
			for (int i = 0; i < bytes.length;)
			{
				byte c = bytes[i];
				if (0 == (c & 0x80))
				{
					halfCount += 1;
					++i;
				}
				else
				{
					halfCount += 2;
					if (0xFC == (c & 0xFC))
					{
						i += 6;
					}
					else if (0xF8 == (c & 0xF8))
					{
						i += 5;
					}
					else if (0xF0 == (c & 0xF0))
					{
						i += 4;
					}
					else if (0xE0 == (c & 0xE0))
					{
						i += 3;
					}
					else if (0xC0 == (c & 0xC0))
					{
						i += 2;
					}
					else 
					{
						--halfCount;
						++i;
					}
				}
			}
			int count = halfCount / 2;
			if (0 != halfCount % 2)
			{
				++count;
			}
			return count;
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
		}
		return 0;
	}
	
	public static String isolateURL(String string)
	{
		String asciiString = null;
		try
		{
			asciiString = new String(string.getBytes(), CHAR_SET_US_ASCII);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!TextUtils.isEmpty(asciiString))
		{
			Spannable spannable = Spannable.Factory.getInstance().newSpannable(asciiString);
			Matcher m = Patterns.WEB_URL.matcher(spannable);
			if (null != m)
			{
				ArrayList<CharSequence> urls = new ArrayList<CharSequence>();
				while (m.find())
				{
					int start = m.start();
					int end = m.end();
					if (Linkify.sUrlMatchFilter.acceptMatch(spannable, start, end))
					{
						urls.add(spannable.subSequence(start, end));
					}
				}
				if (!urls.isEmpty())
				{
					StringBuffer sb = new StringBuffer(string);
					for (CharSequence url : urls)
					{
						String urlString = url.toString();
						int start = sb.indexOf(urlString);
						int end = start + urlString.length();
						sb.insert(end, ' ');
						sb.insert(start, ' ');
					}
					string = sb.toString();
				}
			}
		}
		return string;
	}
	
	public static CharSequence convertEmoji(String string)
	{
		return string;
	}
	
	public static String byteToTimezoneString(byte timezone)
	{
		if (0 <= timezone)
		{
			return String.format("GMT+%d", timezone);
		}
		else
		{
			return String.format("GMT-%d", -timezone);
		}
	}
	
	public static byte timezoneStringToByte(String timezoneString)
	{
		if (TextUtils.isEmpty(timezoneString) || !(4 < timezoneString.length()))
		{
			return 0;
		}
		String number = timezoneString.substring(3);
		if ('+' == number.charAt(0))
		{
			number = number.substring(1);
		}
		return (byte)(Integer.parseInt(number));
	}
	
	public static String formatRelativeTime(Context context, int interval, int formatResID)
	{
		String formatString = context.getString(formatResID);
		if (0 > interval)
		{
			interval = 0;
		}
		if (60 > interval)
		{
			return String.format(formatString, interval, context.getString(R.string.second));
		}
		interval /= 60;
		if (60 > interval)
		{
			return String.format(formatString, interval, context.getString(R.string.minute));
		}
		interval /= 60;
		if (24 > interval)
		{
			return String.format(formatString, interval, context.getString(R.string.hours));
		}
		interval /= 24;
		if (30 > interval)
		{
			return String.format(formatString, interval, context.getString(R.string.day));
		}
		if (365 > interval)
		{
			return String.format(formatString, interval/30, context.getString(R.string.months));
		}
		interval /= 365;
		return String.format(formatString, interval, context.getString(R.string.months));
	}
	
	public static String formatTimeInDay(Context context, int timeSeconds)
	{
		String formatString = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis((long)timeSeconds * TimeUtils.MILLIS_PER_SECOND);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		if (6 > hours)
		{
			formatString = context.getString(R.string.f_hour_minute_early_in_the_morning);
		}
		else if (12 > hours)
		{
			formatString = context.getString(R.string.f_hour_minute_in_the_morning);
		}
		else if (18 > hours)
		{
			formatString = context.getString(R.string.f_hour_minute_in_the_afternoon);
		}
		else
		{
			formatString = context.getString(R.string.f_hour_minute_in_the_night);
		}
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		return format.format(calendar.getTime());
	}
	
	public static String formatDateInMonth(Context context, int timeSeconds)
	{
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
		int todaySeconds = hours*TimeUtils.SECONDS_PER_HOUR+minutes*TimeUtils.SECONDS_PER_MINUTE+seconds;
		
		int interval = TimeUtils.getDurationSeconds(timeSeconds);
		
		if (todaySeconds > interval)
		{
			return null;
		}
		
		int yesterdaySeconds = TimeUtils.SECONDS_PER_DAY+todaySeconds;
		if (yesterdaySeconds > interval)
		{
			return context.getString(R.string.yesterday);
		}
		
		calendar.setTimeInMillis((long)timeSeconds * TimeUtils.MILLIS_PER_SECOND);
		
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int thisWeekSeconds = todaySeconds;
		if (Calendar.SUNDAY == dayOfWeek)
		{
			thisWeekSeconds += TimeUtils.SECONDS_PER_DAY*6;
		}
		else
		{
			thisWeekSeconds += TimeUtils.SECONDS_PER_DAY*(dayOfWeek-1);
		}
		if (thisWeekSeconds > interval)
		{
			return context.getString(R.string.week)+context.getResources().getStringArray(R.array.week_number)[dayOfWeek-1];
		}
		
		int lastWeekSeconds = thisWeekSeconds+TimeUtils.SECONDS_PER_DAY*7;
		if (lastWeekSeconds > interval)
		{
			return context.getString(R.string.last_week)+context.getResources().getStringArray(R.array.week_number)[dayOfWeek-1];
		}
		
		String formatString = null;
		if (year == calendar.get(Calendar.YEAR))
		{
			formatString = context.getString(R.string.f_month_date);
		}
		else
		{
			formatString = context.getString(R.string.f_year_month_date);
		}
		
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		return format.format(calendar.getTime());
	}
	
	public static String formatTimezoneTime(String formatString, int timeSeconds, String timezone)
	{
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		format.setTimeZone(TimeZone.getTimeZone(timezone));
		return format.format(new Date(timeSeconds));
	}
	
	public static String bytesToHexString(byte[] bytes)
	{  
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < bytes.length; i++)
		{
			int v = bytes[i] & 0xFF;
			String s = Integer.toHexString(v);
			if (2 > s.length())
			{
				sb.append('0');
			}
			sb.append(s);
		}
		return sb.toString();
	}  
	public static byte[] hexStringToBytes(String hexString) 
	{
		Assert.assertTrue(null != hexString && 0 == hexString.length()%2);
		
		int len = hexString.length() / 2;
		byte[] bytes = new byte[len];
		for (int i = 0; i < len; ++i)
		{
			int pos = i * 2;
			bytes[i] = (byte)(charToByte(hexString.charAt(pos)) << 4 | charToByte(hexString.charAt(pos+1)));
		}
	    return bytes;
	}  
	public static byte charToByte(char c) 
	{
		if ('0' <= c && '9' >= c)
		{
			return (byte)(c - '0');
		}
		else if ('a' <= c && 'f' >= c)
		{
			return (byte)(c - 'a' + 10);
		}
		else if ('A' <= c && 'F' >= c)
		{
			return (byte)(c - 'A' + 10);
		}
	    return 0;  
	}
	
}
