package org.uvt.m1.patientmanagement.models;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;

public class Model {
    protected static Connection connection = null;
    protected String table = null;
    protected Map<String, Object> properties;

    public Model() {
        properties = new HashMap<>(Map.of());
    }
    public Model(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public BigInteger getId() {
        Object id = properties.getOrDefault("id", null);
        if(id == null) return BigInteger.ZERO;
        if(id instanceof String){
            return BigInteger.valueOf(Integer.parseInt(id.toString()));
        }
        return (BigInteger) properties.getOrDefault("id", BigInteger.ZERO);
    }

    public void setId(BigInteger id) {
        properties.put("id", id);
    }

    public void fill(Map<String, Object> properties){
        for (String key: properties.keySet()){
            this.properties.put(key, properties.get(key));
        }
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public static void initConnection() throws ClassNotFoundException, SQLException {
        if(connection == null){
            String dbUrl = "jdbc:mysql://localhost:3306/PatientManagement", userName="gek", password="superpassword";
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection =  DriverManager.getConnection(dbUrl, userName, password);
        }
    }

    public static void closeConnection(){
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void save(){
        try {
            if(connection == null){
                initConnection();
            }
            StringBuilder query = new StringBuilder();

            Object id = properties.getOrDefault("id", null);
            ArrayList<String> bindings = new ArrayList<>();

            boolean hasId = id != null && !id.toString().isEmpty();
            if(hasId){
                query.append("UPDATE ").append(table).append(" SET ");
                for(String key: properties.keySet()){
                    if(key.equals("id")){
                        continue;
                    }
                    query.append(key).append("= ?,");
                    bindings.add(properties.get(key).toString());
                }
                query.deleteCharAt(query.length() - 1);
                query.append(" WHERE id = ?");
                bindings.add(properties.get("id").toString());
            }
            else{
                query.append("INSERT INTO ").append(table).append("(");
                StringBuilder values = new StringBuilder("VALUES(");
                for(String key: properties.keySet()){
                    Object value = properties.get(key);
                    if(key.equals("id") || value == null){
                        continue;
                    }
                    query.append(key).append(",");
                    values.append("?,");
                    bindings.add(value.toString());
                }
                query.deleteCharAt(query.length() - 1).append(")");
                values.deleteCharAt(values.length() - 1).append(")");
                query.append(values);
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            for (int i = 0, j=bindings.toArray().length; i < j; i++) {
                preparedStatement.setString(i+1, bindings.get(i));
            }
            preparedStatement.execute();
            if(!hasId){
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + this.table + " WHERE id=LAST_INSERT_ID()");
                if(resultSet.next()){
                    for (String key: this.properties.keySet()){
                        try{
                            this.properties.put(key, resultSet.getString(key));
                        }catch (SQLException e){
                            System.out.println("Can't set " + key + " property to model\n" + e.getMessage());
                        }
                    }
                    properties.put("id", BigInteger.valueOf(resultSet.getLong("id")));
                }
            }
            //System.out.println(preparedStatement.toString());
            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Model> all(String table, String[] columns, String whereClause) throws SQLException {
        if(connection == null){
            try {
                initConnection();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        Statement statement = connection.createStatement();
        ArrayList<Model> ls = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table + " " + whereClause);
        while (resultSet.next()){
            ls.add(fromResultSet(resultSet, columns));
        }
        resultSet.close();
        return ls;
    }

    public static ArrayList<Model> select(String query, String[] columns) throws SQLException {
        if(connection == null){
            try {
                initConnection();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        Statement statement = connection.createStatement();
        ArrayList<Model> ls = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()){
            ls.add(fromResultSet(resultSet, columns));
        }
        resultSet.close();
        return ls;
    }

    public static Model find(String table, BigInteger id, String[] columns) throws SQLException {
        if(connection == null){
            try {
                initConnection();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        Statement statement = connection.createStatement();
        Model model = null;
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table + " WHERE id=" + id);
        if (resultSet.next()){
            model = fromResultSet(resultSet, columns);
        }
        resultSet.close();
        return model;
    }

    private static Model fromResultSet(ResultSet resultSet, String[] columns) throws SQLException {
        Map<String, Object> properties = new HashMap<>(Map.of());
        for(String field: columns){
            properties.put(field, resultSet.getString(field));
        }
        return new Model(properties);
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder(this.getClass().getName());
        txt.append("{\n");
        for (String key: properties.keySet()){
            txt.append("\t").append(key).append(" => ");
            Object value = properties.getOrDefault(key, "");
            txt.append(value == null? "": value.toString());
            txt.append("\n");
        }
        txt.append("}");
        return txt.toString();
    }

    public void delete() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM " + this.table + " WHERE id=" + this.getId());
        statement.close();
    }

    public ArrayList<Model> hasMany(String table, String foreignKey, String localKey, String[] selected) throws SQLException {
        if(localKey == null){
            localKey = "id";
        }
        Statement statement = connection.createStatement();
        String query = "SELECT " + table + ".* FROM " + table + " LEFT JOIN " + this.table + " ON " + this.table + "." + localKey + "=" + table + "." + foreignKey + " WHERE " + this.table + ".id = " + this.getId();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<Model> models = new ArrayList<>();
        while (resultSet.next()){
            HashMap<String, Object> properties = new HashMap<>();
            for (String key: selected){
                try{
                    properties.put(key, resultSet.getObject(key));
                }catch (SQLException e){
                    throw new RuntimeException(e);
                }
            }
            Model model = new Model(properties);
            models.add(model);
        }
        statement.close();
        return models;
    }

    public Model hasOne(String table, String foreignKey, String localKey, String[] selected) throws SQLException {
        if(localKey == null){
            localKey = "id";
        }
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT " + table + ".* FROM " + this.table + " LEFT JOIN " + table + " ON " + this.table + "." + localKey + "=" + table + "." + foreignKey + " LIMIT 1");

        if (resultSet.next()){
            HashMap<String, Object> properties = new HashMap<>();
            for (String key: selected){
                try{
                    properties.put(key, resultSet.getObject(key));
                }catch (SQLException e){
                    throw new RuntimeException(e);
                }
            }
            statement.close();
            return new Model(properties);
        }
        statement.close();
        return null;
    }

    public Object get(Object key){
        return this.properties.getOrDefault(key, null);
    }

    public static ArrayList<Model> search(String table, String keyWord, String[] properties) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * FROM " + table + " WHERE ");
        String[] bindings = new String[properties.length];
        if(!keyWord.endsWith("%")){
            keyWord += "%";
        }
        if(!keyWord.startsWith("%")){
            keyWord = "%" + keyWord;
        }
        for (int i = 0; i < properties.length; i++) {
            if(i == 0){
                query.append(properties[i]).append(" LIKE ? ");
            }
            else{
                query.append(" OR ").append(properties[i]).append(" LIKE ? ");
            }
            bindings[i] = keyWord;
        }
        PreparedStatement statement = connection.prepareStatement(query.toString());
        for (int i = 0; i < bindings.length; i++) {
            statement.setString(i + 1, bindings[i]);
        }
        ResultSet resultSet = statement.executeQuery();
        ArrayList<Model> ls = new ArrayList<>();
        while (resultSet.next()){
            ls.add(fromResultSet(resultSet, properties));
        }
        resultSet.close();
        statement.close();
        return ls;
    }
}
