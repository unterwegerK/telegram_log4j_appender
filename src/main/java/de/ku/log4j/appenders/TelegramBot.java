package de.ku.log4j.appenders;

import java.util.concurrent.ConcurrentHashMap;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBot extends TelegramLongPollingBot implements IMessageSink
{
    private final String botUserName;
    private final String botToken;
    private final ConcurrentHashMap<Long, String> knownUsers;
	private final String password;
    
    public TelegramBot(final String botUserName, final String botToken, final String password) throws TelegramApiException {
        this.password = password;
		this.knownUsers = new ConcurrentHashMap<Long, String>();
        this.botUserName = botUserName;
        this.botToken = botToken;
        final TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot((LongPollingBot)this);
    }
    
    public void handleMessage(final String formattedMessage) {
        for (final long chatId : knownUsers.keySet()) {
            sendMessage(formattedMessage, chatId);
        }
    }
    
    public void onUpdateReceived(final Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
        	if(update.getMessage().getText().trim() == password) {
	            final long chatId = update.getMessage().getChatId();
	            final String userName = update.getMessage().getChat().getUserName();
	            this.knownUsers.put(chatId, userName);
	            
	            sendMessage("Registered for logging notifications.", chatId);
        	}
        }
    }

	private void sendMessage(final String formattedMessage, final long chatId) {
		final SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(Long.valueOf(chatId));
		sendMessage.setText(formattedMessage);
		try {
		    execute(sendMessage);
		}
		catch (TelegramApiException e) {
		    e.printStackTrace();
		}
	}
    
    public String getBotUsername() {
        return this.botUserName;
    }
    
    public String getBotToken() {
        return this.botToken;
    }
}