package org.rundeck.plugins.notification;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import org.jibble.pircbot.PircBot;

import java.util.Map;

/**
 * IRC Notification Plugin
 */
@Plugin(service = "Notification", name = "IRC")
@PluginDescription(title = "IRC Notification Plugin", description = "Send job notifications to an IRC channel.")
public class IrcNotification implements NotificationPlugin {

    @PluginProperty(name = "server", title = "Server host", description = "Chat server (eg, irc.freenode.net)")
    private String server;

    @PluginProperty(name = "channel", title = "IRC channel", description = "The IRC channel to join")
    private String channel;
    
    @PluginProperty(name = "chankey", title = "Channel Key", description = "The IRC channel key")
    private String chankey;

    public IrcNotification() {

    }

    @Override
    public boolean postNotification(String trigger, Map executionData, Map config) {

        IrcBot bot = new IrcBot();
        bot.setVerbose(true);
        try {

            bot.connect(server);
            bot.setAutoNickChange(true);
            bot.joinChannel(channel, chankey);
            bot.sendPrivMessage(trigger, executionData);

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return false;
        } finally {
            bot.disconnect();
        }

        return true;
    }

    /**
     * Format the message to send
     * @param trigger Job trigger event
     * @param executionData    Job execution data
     * @return String formatted with job data
     */
    private String generateMessage(String trigger, Map executionData) {
        Object job = executionData.get("job");
        Map jobdata = (Map) job;
        Object groupPath = jobdata.get("group");
        Object jobname = jobdata.get("name");
        String jobdesc = (!isBlank(groupPath.toString()) ? groupPath + "/" : "") + jobname;

        return "[" + trigger.toUpperCase() + "] \"" + jobdesc + "\" run by " + executionData.get("user") + ": " +
                executionData.get("href");
    }

    private boolean isBlank(String string) {
        return null == string || "".equals(string);
    }

    /**
     * The RundeckBot
     */
    public class IrcBot extends PircBot {
        public IrcBot() {
            setName("RundeckBot");
        }

        void sendPrivMessage(String trigger, Map executionData) {
            sendRawLine("PRIVMSG " + channel + " :" + generateMessage(trigger, executionData));
        }

    }

}
