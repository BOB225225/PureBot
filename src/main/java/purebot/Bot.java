package purebot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        
        System.out.println("Starting PureBot...");

        JDABuilder.createLight(System.getenv().get("TOKEN").toString(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Bot())
                .setActivity(Activity.playing("Purity Vanilla"))
                .build();
    }
    public void onReady(@NotNull ReadyEvent event) {

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        // GRABS SYSTEM TIME OF DEVICE

        ZonedDateTime nextFirstLesson = now.withHour(19).withMinute(30).withSecond(0).withNano(00);
        // THIS IS THE TIME IT WILL POST, AND OBVIOUSLY THE BOT NEEDS TO BE RUNNING WHEN THIS HAPPENS

        if (now.compareTo(nextFirstLesson) > 0) {
            nextFirstLesson = nextFirstLesson.plusDays(1);
        }
        Duration durationUntilFirstLesson = Duration.between(now, nextFirstLesson);
        // in seconds
        long initialDelayFirstLesson = durationUntilFirstLesson.getSeconds();
        // schedules the reminder at a fixed rate of one day


        //Random Article Selection
        ScheduledExecutorService schedulerFirstLesson = Executors.newScheduledThreadPool(1);
        schedulerFirstLesson.scheduleAtFixedRate(() -> {

            //YouTuber of the week
                    if(now.getDayOfWeek() == DayOfWeek.of(5)){
                        MessageChannel bot = event.getJDA().getTextChannelById("958051113964302336"); //Talking channel
                        MessageChannel listbot = event.getJDA().getTextChannelById("955932368802312253");//API channel
                        MessageHistory history = MessageHistory.getHistoryFromBeginning(listbot).complete();//API history
                        List<Message> mess = history.getRetrievedHistory();

                        OffsetDateTime lastMessage = history.getRetrievedHistory().get(1).getTimeCreated();
                        if(lastMessage.getDayOfWeek() == now.getDayOfWeek()){
                        } else{
                        Random rand = new Random(); // RANDOMISATION
                        int randomNum; // RANDOMISATION
                        randomNum = 0 + rand.nextInt((mess.size() - 0) + 1); // RANDOMISATION
                        String username = mess.get(randomNum).getContentRaw().split("=")[0]; //Get YouTube channel username from API
                        String channelLink = mess.get(randomNum).getContentRaw().replaceAll(".+=", ""); //Get YouTube channel link from API
                        EmbedBuilder test = new EmbedBuilder();
                        test.setTitle(":desktop: **The Creator of the week is... "+username+"!** :partying_face:");
                        test.addField("Help support " + username + " by watching, liking, and commenting on their videos!", ":point_down: You can find their channel link below! :point_down:", true);
                        test.setColor(Color.green);
                        bot.sendMessageEmbeds(test.build()).queue(); //Send embed message
                        bot.sendMessage(channelLink).queue(); //Send link message
                             }
                    }
            //Wiki of the day

                        try {
                            URL url = new URL("https://purity.fandom.com/wiki/Special:Random");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            ArrayList<String> list = new ArrayList<String>();
                            String inputLine;
                            MessageChannel channel = event.getJDA().getTextChannelById("954789778572836936");
                            MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();
                            OffsetDateTime lastMessage = history.getRetrievedHistory().get(1).getTimeCreated();
                           

                           
                                while ((inputLine = in.readLine()) != null)
                                    if (true) {
                                        list.add(inputLine);
                                        if (inputLine.contains("<title>")) {
                                            String strippedText = inputLine.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
                                            String spaceRem = strippedText.replaceAll(" ", "_");
                                            String finalmsg = spaceRem.split("\\|")[0].substring(0, spaceRem.split("\\|")[0].length() - 1).substring(1);
                                            EmbedBuilder test = new EmbedBuilder();
                                            if (finalmsg.replaceAll("_", " ").equals("Glaceonguy") || finalmsg.replaceAll("_", " ").equals("RobertDonald")) {
                                                test.setTitle(":book: **Todays wiki page is on " + finalmsg.replaceAll("_", " ") + ".** :nerd: :books:");
                                                    //makes these nerds have a nerd emoji in the message
                                            } else {
                                                test.setTitle(":book: **Todays wiki page is on " + finalmsg.replaceAll("_", " ") + ".** :books:");
                                            }
                                            test.addField("A new random article from the Purity Vanilla wiki each day.", "All data is from https://purity.fandom.com/wiki/Purity_Wiki", true);
                                            test.setColor(Color.green);
                                            channel.sendMessageEmbeds(test.build()).queue();
                                            channel.sendMessage("https://purity.fandom.com/wiki/" + finalmsg).queue();
                                        }
                                    }
                          
                            in.close();
                        }

                        catch (MalformedURLException e) {
                            System.out.print(e);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
 
                },
                initialDelayFirstLesson,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);
    }
}