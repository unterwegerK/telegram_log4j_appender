package de.ku.log4j.appenders;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageSinkLocator {

	private static IMessageSink messageSink;
	
	public static void setMessageSink(IMessageSink messageSink) {
		MessageSinkLocator.messageSink = messageSink;
	}

	public static IMessageSink getMessageSink(String botUserName, String telegramBotToken, String password) {
        if (messageSink == null) {
            try {
                messageSink = (IMessageSink)new TelegramBot(botUserName, telegramBotToken, password);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        
        return messageSink;
	}
}
