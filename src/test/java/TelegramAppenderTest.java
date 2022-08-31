import java.io.Serializable;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.filter.BurstFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.ku.log4j.appenders.IMessageSink;
import de.ku.log4j.appenders.MessageSinkLocator;
import de.ku.log4j.appenders.TelegramAppender;

public class TelegramAppenderTest {
	
	class MessageSinkSpy implements IMessageSink {

		private int countOfHandledMessages;
		
		public int getCountOfHandledMessages() {
			return countOfHandledMessages;
		}
		
		public void handleMessage(String message) {
			countOfHandledMessages++;
		}
	}
	
	@AfterEach
	public void TearDown() {
		((LoggerContext)LogManager.getContext(false)).close();
	}
	
	@Test
	public void TestConfiguredAppender() {	
		MessageSinkSpy spy = new MessageSinkSpy();
        MessageSinkLocator.setMessageSink(spy);
	    LoggerContext logContext = (LoggerContext)LogManager.getContext(false);

	    Map<String, LoggerConfig> map = logContext.getConfiguration().getLoggers();

	    Appender appender = map.values().iterator().next().getAppenders().values().iterator().next();
	    
	    Assertions.assertInstanceOf(TelegramAppender.class, appender);
	    
	    TelegramAppender telegramAppender = (TelegramAppender)appender;
	    Filter filter = telegramAppender.getFilter();
	    
	    Assertions.assertInstanceOf(BurstFilter.class, filter);
	    
	    Layout<? extends Serializable> layout = telegramAppender.getLayout();
	    
	    Assertions.assertInstanceOf(PatternLayout.class, layout);
	}

	@Test
	public void TestSingleMessage() {		
		MessageSinkSpy spy = new MessageSinkSpy();
        MessageSinkLocator.setMessageSink(spy);

		Logger logger = LogManager.getLogger("de.ku");

		logger.warn("Test");
		
		Assertions.assertEquals(1, spy.countOfHandledMessages);
	}
	
	@Test
	public void Test100MessagesWithBurstLimitOf10() {
		MessageSinkSpy spy = new MessageSinkSpy();
        MessageSinkLocator.setMessageSink(spy);

        Logger logger = LogManager.getLogger("de.ku");
        
		for(int i = 0; i < 100; i++) {
			logger.error("Test");
        }
		
      Assertions.assertEquals(10, spy.getCountOfHandledMessages());
	}
}
