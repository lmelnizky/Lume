package org.andengine.scene.OnlineScenes.ServerScene;

import org.andengine.entity.scene.Scene;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedList;

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
        } catch (JSONException e) {e.printStackTrace();}
        return o;
    }
    public static String getRequestFromData(Object ... args){
        String returnValue = "";
        JSONObject o = (JSONObject) args[0];
        try {returnValue = o.getString("fromID");}
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
        Object[] returnValue = new Object[2];
        JSONObject o = (JSONObject) args[0];
        try {
            returnValue[0] = o.getBoolean("angenommen");
            returnValue[1] = o.getString("fromID");
        }catch(JSONException e){e.printStackTrace();}
        return returnValue;
    }
    public static JSONObject getAnswerRequestData(boolean angenommen, String toID, String fromID){
        JSONObject returnValue = new JSONObject();
        try {
            returnValue.put("angenommen", angenommen);
            returnValue.put("toID", toID);
            returnValue.put("fromID", fromID);
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
}
