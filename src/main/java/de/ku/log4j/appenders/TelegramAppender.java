package de.ku.log4j.appenders;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
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
    		Property[] properties, 
    		IMessageSink messageSink) {
        super(name, filter, (Layout)layout, true, properties);
        if (messageSink == null) {
            try {
                messageSink = (IMessageSink)new TelegramBot(telegramBotUserName, telegramBotToken, password);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        this.messageSink = messageSink;
    }
    
    @PluginFactory
    public static TelegramAppender createAppender(
    		@PluginAttribute("name") String name, 
    		@PluginAttribute("telegramBotUserName") String telegramBotUserName, 
    		@PluginAttribute("telegramBotToken") String telegramBotToken,
    		@PluginAttribute("password") String password,
    		@PluginElement("Filter") Filter filter, 
    		Layout<? extends Serializable> layout) {
        return new TelegramAppender(name, telegramBotUserName, telegramBotToken, password, filter, layout, null, null);
    }
    
    public void append(final LogEvent event) {
        final byte[] bytes = this.getLayout().toByteArray(event);
        this.messageSink.handleMessage(new String(bytes));
    }
}