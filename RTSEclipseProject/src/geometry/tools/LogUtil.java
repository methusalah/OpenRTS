package geometry.tools;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class LogUtil {

	public static Logger logger = Logger.getLogger("Tests benoit");

	public static void init() {
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
		Handler ch = new MyHandler(System.out, new MyFormatter());
		logger.addHandler(ch);
	}

	static class MyHandler extends StreamHandler {

		public MyHandler(PrintStream out, MyFormatter formatter) {
			super(out, formatter);
		}

		@Override
		public void publish(LogRecord record) {
			super.publish(record);
			flush();
		}

	}

	static class MyFormatter extends Formatter {

		private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

		@Override
		public String format(LogRecord record) {
			StringBuilder res = new StringBuilder();
			res.append(dateFormat.format(new Date(record.getMillis())));
			res.append(" - ");
			res.append(record.getLevel());
			res.append(" - ");
			res.append(record.getSourceClassName());
			res.append(".");
			res.append(record.getSourceMethodName());
			res.append(" - ");
			res.append(formatMessage(record));
			res.append("\n");
			if (record.getThrown() != null) {
				res.append("    " + record.getThrown().toString());
				res.append("\n");
				StackTraceElement[] els = record.getThrown().getStackTrace();
				for (StackTraceElement e : els) {
					res.append("    " + e.toString());
					res.append("\n");
				}
				res.append("\n");
			}
			return res.toString();
		}

	}
}
