package org.lume.scene.OnlineScenes.ServerScene;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

public class ServerDataFactory {
    public static JSONObject getCreatePlayerData(String username, String id){
        JSONObject returnValue = new JSONObject();
        try {
            returnValue.put("name", username);
            returnValue.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnValue;
    }
    public static Collection<Player> getPLayersFromData(Object ... args){
        JSONArray array = (JSONArray) args[0];
        LinkedList<Player> returnValue = new LinkedList();
        for(int i = 0; i < array.length(); i++){
            try {returnValue.add(new Player(array.getJSONObject(i).getString("id"), array.getJSONObject(i).getString("name")));}
            catch (JSONException e) {e.printStackTrace();}
        }
        return returnValue;
    }
    public static Player getPlayerFromData(Object ... args){
        JSONObject o = (JSONObject) args[0];
        Player returnValue = null;
        try { returnValue = new Player(o.getString("id"), o.getString("name")); }
        catch (JSONException e) {e.printStackTrace();}
        return returnValue;
    }
    public static JSONObject getRequestData(String toID, String fromID){
        JSONObject o = new JSONObject();
        try {
            o.put("toID", toID);
            o.put("fromID", fromID);
            byte[] array = new byte[7]; // length is bounded by 7
            new Random().nextBytes(array);
            String s = randomRoom();
            o.put("room",s);
        } catch (JSONException e) {e.printStackTrace();}
        return o;
    }
    public static String[] getRequestFromData(Object ... args){
        String[] returnValue = new String[2];
        JSONObject o = (JSONObject) args[0];
        try {returnValue[0] = o.getString("fromID");
            returnValue[1] = o.getString("room");}
        catch (JSONException e) {e.printStackTrace();}
        return returnValue;
    }
    public static String getIdFromData(Object ... args){
        String returnValue = "";
        JSONObject o = (JSONObject) args[0];
        try {returnValue = o.getString("id"); }
        catch (JSONException e) {e.printStackTrace();}
        return returnValue;
    }
    public static Object[] getAnswerFromRequestData(Object ... args){
        Object[] returnValue = new Object[3];
        JSONObject o = (JSONObject) args[0];
        try {
            returnValue[0] = o.getBoolean("angenommen");
            returnValue[1] = o.getString("fromID");
            returnValue[2] = o.getString("room");
        }catch(JSONException e){e.printStackTrace();}
        return returnValue;
    }
    public static JSONObject getAnswerRequestData(boolean angenommen, String toID, String fromID, String room){
        JSONObject returnValue = new JSONObject();
        try {
            returnValue.put("angenommen", angenommen);
            returnValue.put("toID", toID);
            returnValue.put("fromID", fromID);
            returnValue.put("room", room);
        } catch (JSONException e) {e.printStackTrace();}
        return returnValue;
    }
    public static JSONObject getCreateGameData(String[] IDs, String room){
        JSONObject returnValue = new JSONObject();
        String addValue = "";
        for(int i = 0; i < IDs.length; i++) {
            if (i == IDs.length - 1) addValue += IDs[i];
            else addValue += IDs[i] + ";";
        }
        try {
            returnValue.put("IDs", addValue);
            returnValue.put("room", room);
        } catch (JSONException e){e.printStackTrace();}
        return returnValue;
    }
    public static Object[] getStartGameFromData(Object ... args){
        Object[] returnValue = new Object[2];
        JSONObject o = (JSONObject) args[0];
        try {
            returnValue[0] = o.getString("IDs").split(";");
            returnValue[1] = o.getString("referee");
        } catch (JSONException e) {e.printStackTrace();}
        return returnValue;
    }
    private static String randomRoom(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 15;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        System.out.println("ROOOOOOOM: " + buffer.toString());
        return buffer.toString();
    }
    public static String getLostLifeIDFromData(Object ... args){
        JSONObject o = (JSONObject) args[0];
        try {
            return o.getString("ID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
