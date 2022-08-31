package de.ku.log4j.appenders;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.Property;
import java.io.Serializable;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.appender.AbstractAppender;

@Plugin(name = "TelegramAppender", category = "Core", elementType = "appender")
public class TelegramAppender extends AbstractAppender
{
    private final IMessageSink messageSink;
    
    TelegramAppender(
    		String name, 
    		String telegramBotUserName, 
    		String telegramBotToken,
    		String password,
    		Filter filter, 
    		Layout<? extends Serializable> layout, 
    		Property[] properties) {
        super(name, filter, (Layout<? extends Serializable>)layout, true, properties);

        this.messageSink = MessageSinkLocator.getMessageSink(telegramBotUserName, telegramBotToken, password);
    }
    
    @PluginFactory
    public static TelegramAppender createAppender(
    		@PluginAttribute("name") String name, 
    		@PluginAttribute("telegramBotUserName") String telegramBotUserName, 
    		@PluginAttribute("telegramBotToken") String telegramBotToken,
    		@PluginAttribute("password") String password,
    		@PluginElement("Filter") Filter filter,
    		@PluginElement("layout") Layout<? extends Serializable> layout) {
    	
        return new TelegramAppender(name, telegramBotUserName, telegramBotToken, password, filter, layout, null);
    }
    
    public void append(final LogEvent event) {
        final byte[] bytes = this.getLayout().toByteArray(event);
        this.messageSink.handleMessage(new String(bytes));
    }
}