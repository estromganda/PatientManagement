package org.uvt.m1.patientmanagement.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room extends Model{

    private static final ArrayList<Room> rooms = new ArrayList<>();
    public static String[] columns = {"id", "isBusy"};
    public Room(){
        this.table = "Room";
        properties = new HashMap<>(Map.of());
    }

    public boolean isBusy(){
        return properties.get("isBusy").equals(true);
    }
    public void setBusy(boolean value){
        properties.put("isBusy", value);
    }

    public static ArrayList<Room> getRooms() {
        if(rooms.isEmpty()){
            loadRooms();
        }
        return rooms;
    }

    public static void loadRooms(){
        try {
            ArrayList<Model> models = Model.all("Room", Room.columns, "");
            rooms.clear();
            for(Model model: models){
                Room room = new Room();
                room.fill(model.getProperties());
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
