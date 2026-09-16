package idv.const_x.utils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {

	public static Date getWeekBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayofweek == 0)
			dayofweek = 7;
		c.add(Calendar.DATE, -dayofweek + 1);
		c.set(Calendar.HOUR_OF_DAY,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		return c.getTime();
	}

	/**
	 * 获取一周开始时间（周一零点）
	 * 
	 * @return
	 *
	 * @author const.x
	 * @createDate 2015年1月28日
	 */
	public static Date getNowWeekBegin() {
		Calendar c = Calendar.getInstance();
		int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayofweek == 0)
			dayofweek = 7;
		c.add(Calendar.DATE, -dayofweek + 1);
		c.set(Calendar.HOUR_OF_DAY,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		return c.getTime();
	}

	/**
	 * 获取一周结束时间（周日23点）
	 *
	 * @return
	 *
	 * @author const.x
	 * @createDate 2015年1月28日
	 */
	public static Date getNowWeekEnd() {
		Calendar c = Calendar.getInstance();
		int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayofweek == 0)
			dayofweek = 7;
		c.add(Calendar.DATE, 7 - dayofweek);
		c.set(Calendar.HOUR_OF_DAY,23);
		c.set(Calendar.MINUTE,59);
		c.set(Calendar.SECOND,59);
		return c.getTime();
	}

	public static String toDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	public static String toDateTimeString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}


	/**
	 * 获取当前时间
	 * 
	 * @return yyyy-MM-dd HH:mm:ss
	 *
	 * @author const.x
	 * @createDate 2015年1月29日
	 */
	public static String getNowDateTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	/**
	 * 获取当前日期
	 * 
	 * @return yyMMdd
	 *
	 * @author const.x
	 * @createDate 2015年1月29日
	 */
	public static String getShortNowDate() {
		return new SimpleDateFormat("yyMMdd").format(new Date());
	}

	public static String getShortNowTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		return sdf.format(new Date());
	}

	public static String toDateTimeZonedString(Date date) {
		if (date == null) {
			return "null";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return sdf.format(date);
	}

	/**
	 * 获取当前日期
	 * 
	 * @return yyyy-MM-dd
	 *
	 * @author const.x
	 * @createDate 2015年1月29日
	 */
	public static String getNowDate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}

	/**
	 * 与当前时间比对
	 * 
	 * @param date
	 *            yyyy-MM-dd HH:mm:ss
	 * @return =0 等于 ； <0 小于当前时间 ； >0 大于当前时间
	 * 
	 * @author const.x
	 * @throws ParseException
	 * @createDate 2015年2月9日
	 */
	public static int compareWithNowTime(String date) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = format.parse(date);
		Date now = format.parse(format.format(new Date()));
		return d.compareTo(now);
	}

	/**
	 * 时间比对
	 * 
	 * @param date
	 *            yyyy-MM-dd HH:mm:ss
	 * @return =0 等于 ； <0 小于； >0
	 * 
	 * @author const.x
	 * @throws ParseException
	 * @createDate 2015年2月9日
	 */
	public static int compareTime(String date1, String date2) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d1 = format.parse(date1);
		Date d2 = format.parse(date2);
		return d1.compareTo(d2);
	}

	/**
	 * 与当前日期比对
	 * 
	 * @param date
	 *            yyyy-MM-dd
	 * @return =0 等于 ； <0 小于当前日期 ； >0 大于当前日期
	 * 
	 * @author const.x
	 * @throws ParseException
	 * @createDate 2015年2月9日
	 */
	public static int compareWithNowDate(String date) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date d = format.parse(date);
		Date now = format.parse(format.format(new Date()));
		return d.compareTo(now);
	}


	/**
	 * 获得当天24点时间
	 * 
	 * @return yyyy-MM-dd HH:mm:ss
	 *
	 * @author const.x
	 * @createDate 2015年2月10日
	 */
	public static String getTimesnight() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 24);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date date = cal.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	/**
	 * 获取日期间隔天数
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 *
	 * @author const.x
	 * @createDate 2015年2月10日
	 */
	public static Long getDaysBetween(Date startDate, Date endDate) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(startDate);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(endDate);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		return (toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);
	}

	/**
	 * 获取日期间隔天数
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 *
	 * @author const.x
	 * @throws ParseException
	 * @createDate 2015年2月10日
	 */
	public static Long getDaysBetween(String startDate, String endDate) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(dateFormat.parse(startDate));
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(dateFormat.parse(endDate));
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		return (toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);
	}

	public static String dealDatetimeWithFormat(String dateFormatStr, String datetime) throws ParseException {
		if (datetime == null || datetime.trim().equals("")) {
			return null;
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (datetime.length() <= 10) {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		}
		Date date = dateFormat.parse(datetime);
		dateFormat = new SimpleDateFormat(dateFormatStr);
		String dealDateStr = dateFormat.format(date);
		return dealDateStr;
	}
	
	public static Date formatDate(String datetime){
		if (datetime == null || datetime.trim().equals("")) {
			return null;
		}
		DateFormat dateFormat = null;
		if (datetime.length() == 10) {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		}else if(datetime.length() == 14){
			dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		}else{
			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		Date date = null;
		try {
			date = dateFormat.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获取指定月间隔后的时间
	 * 
	 * @param days
	 *            间隔天数 如果为负数 则获得指定日前的日期
	 * @param datetime
	 * @return
	 * @throws ParseException
	 */
	public static String getDateTimeAfter(int days, String datetime) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = dateFormat.parse(datetime);
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(date);// 把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_YEAR, days); // 设置为前3月
		date = calendar.getTime(); // 得到前3月的时间

		String dealDateStr = dateFormat.format(date);
		return dealDateStr;
	}

	/**
	 * 获取指定月间隔后的时间
	 *
	 * @param days
	 *            间隔天数 如果为负数 则获得指定日前的日期
	 * @param datetime
	 * @return
	 * @throws ParseException
	 */
	public static String getDateTimeAfterNow(int days) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(new Date());// 把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_YEAR, days); // 设置为前3月
		Date date = calendar.getTime(); // 得到前3月的时间

		String dealDateStr = dateFormat.format(date);
		return dealDateStr;
	}

	public static Date getDateAfter(int days, Date date)  {
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(date);// 把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_YEAR, days); // 设置为前3月
		date = calendar.getTime(); // 得到前3月的时间
		return date;
	}

	public static Date getDateAfter(int amount,int field, Date date)  {
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(date);// 把当前时间赋给日历
		calendar.add(field, amount); // 设置为前3月
		date = calendar.getTime(); // 得到前3月的时间
		return date;
	}
	
	/**
	 * 获取指定小时间隔后的时间
	 * 
	 * @param hours
	 *            间隔天数 如果为负数 则获得指定日前的日期
	 * @param datetime
	 * @return
	 * @throws ParseException
	 */
	public static String getDateTimeAfterHours(int hours, String datetime) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = dateFormat.parse(datetime);
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(date);// 把当前时间赋给日历
		calendar.add(Calendar.HOUR, hours); // 设置为前3月
		date = calendar.getTime(); // 得到前3月的时间
		String dealDateStr = dateFormat.format(date);
		return dealDateStr;
	}




	/**
	 * 转换为 xx月xx日 周x 的格式
	 *
	 * @return
	 *
	 * @author const.x
	 * @throws ParseException
	 * @createDate 2015年1月28日
	 */
	public static String getWeek(String date) throws ParseException {
		if (date == null || date.trim().equals("")) {
			return date;
		}
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat format2 = new SimpleDateFormat("MM月dd日 周");
		Date d = format1.parse(date);
		int day = d.getDay();
		String res = format2.format(d);
		switch (day) {
			case 0:
				res += "日";
				break;
			case 1:
				res += "一";
				break;
			case 2:
				res += "二";
				break;
			case 3:
				res += "三";
				break;
			case 4:
				res += "四";
				break;
			case 5:
				res += "五";
				break;
			case 6:
				res += "六";
				break;
		}
		return res;
	}


	/**
	 * 将时间转换为文字描述
	 * @param date
	 * @param level 1 日 2 时 3 分 4 秒
	 * @return
	 */
	public static String getDateDetail(Date date,int level) {
		Date now = new Date();
		int year = date.getYear();
		int curYear = now.getYear();
		StringBuilder day = new StringBuilder();
		if (curYear != year) {
			day.append(year + 1900).append("年").append(date.getMonth() + 1).append("月").append(date.getDate()).append("日");
		} else {
			Calendar today = Calendar.getInstance();
			Calendar target = Calendar.getInstance();
			today.setTime(now);
			today.set(Calendar.HOUR, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			target.setTime(date);
			target.set(Calendar.HOUR, 0);
			target.set(Calendar.MINUTE, 0);
			target.set(Calendar.SECOND, 0);
			long intervalMilli = target.getTimeInMillis() - today.getTimeInMillis();
			int xcts = (int) (intervalMilli / (24 * 60 * 60 * 1000));
			switch (xcts) {
				case 0: {
					day.append("今天");
					break;
				}
				case 1: {
					day.append("明天");
					break;
				}
				case 2: {
					day.append("后天");
					break;
				}
				case -1: {
					day.append("昨天");
					break;
				}
				case -2: {
					day.append("前天");
					break;
				}
				default: {
					day.append(date.getMonth() + 1).append("月").append(date.getDate()).append("日");
				}
			}
		}
		if (level == 1) {
			return day.toString();
		}
		int hour = date.getHours();
		if (hour < 12) {
			day.append(" ").append(date.getHours()).append("点");
		} else if (hour < 18) {
			day.append(" 下午").append(date.getHours() - 12).append("点");
		} else {
			day.append(" 晚上").append(date.getHours() - 12).append("点");
		}

		if (level == 2) {
			return day.toString();
		}

		int minutes = date.getMinutes();
		if (minutes == 0) {
			day.append("整");
		}else{
			day.append(minutes).append("分");
		}

		if (level == 3) {
			return day.toString();
		}
		day.append(date.getSeconds()).append("秒");
		return day.toString();
	}

	public static String getTimeString(Long t){
		StringBuilder sb = new StringBuilder();
		Long h = t/ (60*60*1000);
		t = t%(60*60*1000);
		if(h > 0){
			sb.append(h).append("小时");
		}
		h = t/ (60*1000);
		t = t%(60*1000);
		if(h > 0){
			sb.append(h).append("分");
		}
		if (t > 0) {
			sb.append(new BigDecimal(t).divide(new BigDecimal(1000), 3, BigDecimal.ROUND_HALF_UP).toPlainString()).append("秒");
		}
		return sb.toString();
	}
	
	/**
	 * 获取延期时间 考虑工作时间影响
	 * 
	 * @param date
	 * @param delaytime
	 *            毫秒
	 * @param workBeginHours
	 * @param workBeginMinute
	 * @param workEndHours
	 * @param workEndMinute
	 * @return
	 */
	public static Date getDelayDate(Date date, Long delaytime, int workBeginHours, int workBeginMinute,
			int workEndHours, int workEndMinute) {
		// 计算起始时间
		Date begin = date;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, workBeginHours);
		calendar.set(Calendar.MINUTE, workBeginMinute);
		calendar.set(Calendar.SECOND, 0);
		Date workbegin = calendar.getTime();

		calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, workEndHours);
		calendar.set(Calendar.MINUTE, workEndMinute);
		calendar.set(Calendar.SECOND, 0);
		Date workEnd = calendar.getTime();
		Long workTime = workEnd.getTime() - workbegin.getTime();
		Long restTime = 24 * 60 * 60 * 1000L - workTime;
		if (begin.before(workbegin)) {
			begin = workbegin;
		} else if (begin.after(workEnd)) {
			calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, workBeginHours);
			calendar.set(Calendar.MINUTE, workBeginMinute);
			calendar.set(Calendar.SECOND, 0);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			begin = calendar.getTime();
		}

		if (delaytime > workTime) {
			int s = (int) (delaytime / workTime);
			delaytime += s * restTime;
		}
		Date end = new Date(begin.getTime() + delaytime);

		calendar = Calendar.getInstance();
		calendar.setTime(end);
		calendar.set(Calendar.HOUR_OF_DAY, workBeginHours);
		calendar.set(Calendar.MINUTE, workBeginMinute);
		calendar.set(Calendar.SECOND, 0);
		workbegin = calendar.getTime();

		calendar = Calendar.getInstance();
		calendar.setTime(end);
		calendar.set(Calendar.HOUR_OF_DAY, workEndHours);
		calendar.set(Calendar.MINUTE, workEndMinute);
		calendar.set(Calendar.SECOND, 0);
		workEnd = calendar.getTime();

		if (end.after(workEnd) || end.before(workbegin)) {
			end = new Date(end.getTime() + restTime);
		}

		return end;
	}

	public static Date getDateStart(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		return c.getTime();
	}

	public static Date getDateEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY,23);
		c.set(Calendar.MINUTE,59);
		c.set(Calendar.SECOND,59);
		return c.getTime();
	}

	public static List<Date[]> splitDate(Date begin, Date end, int step, int field, boolean asc){
		List<Date[]> list = new ArrayList<>();
		Date s = null,e = null;
		while (true){
			if (asc) {
				if (s == null) {
					s = begin;
				}else{
					if (e.after(end) || e.equals(end)) {
						//progressBar.completed(null);
						break;
					}
					s = e;
				}
				e = DateUtils.getDateAfter(step, field,s);
				if (e.after(end)) {
					e = end;
				}
				list.add(new Date[]{s,e});
			}else{
				if (e == null) {
					e = end;
				}else{
					if (s.before(begin) || s.equals(begin)) {
						//progressBar.completed(null);
						break;
					}
					e = s;
				}
				s = DateUtils.getDateAfter(-step, field,e);
				if (s.before(begin)) {
					s = begin;
				}
				list.add(new Date[]{s,e});
			}
		}
		return list;
	}

	public static void main(String[] args) throws ParseException {
		Date dateAfter = getDateAfter(-2, new Date());
		System.out.println(getDateDetail(dateAfter,1));
	}

}
