package com.company;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

class TelegramBot
{
    public String bot_url;
    private String api_telegram_org = "https://api.telegram.org/bot";

    public TelegramBot(String url)
    {
        bot_url = url;
    }

    public String getUpdates()
    {
        return getHttp(api_telegram_org + bot_url + "/getUpdates");
    }

    public Boolean writeAllData(String s1)
    {
        Boolean good = false;
        ArrayList<String> res1 = new ArrayList<>();
        for (int i = 0; i < s1.length(); i++)
        {
            if (i < s1.length() - 9)
            {
                if (s1.substring(i, i + 9).equals("update_id"))
                {
                    if (s1.indexOf("update_id", i + 9) != -1)
                    {
                        String cur = s1.substring(i, s1.indexOf("update_id", i + 9));
                        cur = cur.substring(0, cur.length()-4);
                        if (!res1.contains(cur))
                            res1.add(cur);
                    }
                    else
                    {
                        String cur = s1.substring(i);
                        res1.add(cur);
                    }
                }
            }
        }
        File f1 = new File("../alldata.txt");
        try
        {
            FileWriter fileWriter = new FileWriter(f1, true);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            for (int i = 0; i < res1.size(); i++)
            {
                bw.write(res1.get(i));
                bw.newLine();
            }
            bw.close();
            good = true;
        }
        catch (Exception e){}
        return good;
}

    public ArrayList<String> findChatID(String s1)
    {
        ArrayList<String> res1 = new ArrayList<>();
        for (int i = 0; i < s1.length(); i++)
        {
           if (i < s1.length() - 4)
           {
               String cur = "";
                if (s1.substring(i, i + 4).equals("chat"))
                {
                    for (int j = i + 12; j < s1.length(); j++)
                    {
                        if (s1.charAt(j) != ',')
                            cur += s1.charAt(j);
                        else
                        {
                            if (!res1.contains(cur))
                                res1.add(cur);
                            i = j;
                            break;
                        }
                    }
                }
            }
        }
        return res1;
    }

    public ArrayList<String> findText(String updates)
    {
        ArrayList<String> res1 = new ArrayList<>();
        for (int i = 0; i < updates.length(); i++)
        {
            if (i < updates.length() - 4)
            {
                String cur = "";
                if (updates.substring(i, i + 4).equals("text"))
                {
                    for (int j = i + 7; j < updates.length(); j++)
                    {
                        if (updates.charAt(j) != '"')
                            cur += updates.charAt(j);
                        else
                        {
                            res1.add(cur);
                            i = j;
                            break;
                        }
                    }
                }
            }
        }
        return res1;
    }

    public ArrayList<String> readChatIDsFromFile(File f1)
    {
        ArrayList<String> res1 = new ArrayList<>();
        String sub = "";
        try
        {
            BufferedReader read1 = new BufferedReader(new FileReader(f1));
            while ((sub = read1.readLine()) != null)
                res1.add(sub);
        }
        catch (Exception e) {}
        return res1;
    }

    public void writeChatIDsInFile(ArrayList<String> s1)
    {
        //if you want to save chat ids, then write here a full path for .txt file
                                             //for example :
        File f1 = new File("../chatIDs.txt");
        ArrayList<String> entries = readChatIDsFromFile(f1);
        try
        {
            FileWriter fileWriter = new FileWriter(f1, true);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            for (int i = 0; i < s1.size(); i++)
            {
                if (!entries.contains(s1.get(i)+"&"))
                {
                    bw.write(s1.get(i) + "&");
                    bw.newLine();
                }
            }
            bw.close();
        }
        catch (Exception e) {}
    }

    public String fromUEStoUTF(String ues)
    {
        String res1 = "";
        for (int i = 0; i < ues.length(); i++)
        {
            if (i < ues.length() - 1)
            {
                if (ues.substring(i, i + 2).equals("\\u"))
                {
                    char cur = (char) Integer.parseInt(ues.substring(i + 2, i + 6), 16);
                    i += 5;
                    res1 += cur;
                }
                else
                    res1 += ues.charAt(i);
            }
        }
        return res1;
    }

    public String getHttp(String http_url)
    {
        String res1 = "";
        try
        {
            URL bot_url = new URL(http_url);
            URLConnection connection = bot_url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                res1 += inputLine;
            in.close();
        }
        catch(Exception e){}
        return res1;
    }

    public void sendText(String chatID, String text)
    {
        try
        {
            final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET().uri(URI.create(api_telegram_org + bot_url + "/sendMessage?chat_id=" + chatID + "&text=" + text)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(api_telegram_org + bot_url + "/sendMessage?chat_id=" + chatID + "&text=" + text);
            //System.out.println(response.body());
        }
        catch(Exception e){}
    }

    public void sendPhoto(String chatID, String photo_url)
    {
        try
        {
            final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET().uri(URI.create(api_telegram_org + bot_url + "/sendPhoto?chat_id=" + chatID + "&photo=" + photo_url)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(api_telegram_org + bot_url + "/sendMessage?chat_id=" + chatID + "&text=" + text);
            //System.out.println(response.body());
        }
        catch(Exception e){}
    }

    public String lastUpdID(String updates)
    {
        String res1 = "";
        int idx = updates.lastIndexOf("update_id");
        for (int i = idx + 11; i < updates.length(); i++)
        {
            if (updates.charAt(i) != ',')
            {
                res1 += updates.charAt(i);
            }
            else
                break;
        }
        return res1;
    }

    public void clearUpdates(String lastUpd)
    {
        try
        {
            final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET().uri(URI.create("https://api.telegram.org/bot" + bot_url + "/getUpdates?offset=" + (Long.valueOf(lastUpd)+1))).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch(Exception e){}
    }

    static String fromUTFtoUES(String s1)
    {
        String res1 = "";
        for (int i = 0; i < s1.length(); i++)
        {
            if ((s1.charAt(i) >= 'a') && (s1.charAt(i) <= 'я') || (s1.charAt(i) >= 'А') && (s1.charAt(i) <= 'Я'))
                res1 += s1.charAt(i);
            if (s1.charAt(i) == ' ')
                res1 += "%20";
            if (i < s1.length() - 1 && s1.substring(i, i + 2).equals("\\n"))
            {
                res1 += "%0A";
                i += 1;
            }
        }
            return res1;
    }
}

public class Main
{
    static String botUrl = "YOUR_TOKEN";
    public static void main(String[] args)
    {
        TelegramBot myBot = new TelegramBot(botUrl);
        while (true)
        {
            try
            {
                String updates = (myBot.getUpdates());
                ArrayList<String> ids = myBot.findChatID(updates);
                ArrayList<String> texts = myBot.findText(updates);
                System.out.println(updates);
                //myBot.clearUpdates(myBot.lastUpdID(updates));
                //Thread.sleep(2000);
            }
            catch (Exception e) {}
        }
    }
}
