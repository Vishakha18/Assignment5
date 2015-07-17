/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Assignment4;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONArray;

/**
 * REST Web Service
 *
 * @author Vishakha Mehan
 */
@Path("products")
public class Prod {
ProdConnection prodCon=new ProdConnection();
    Connection connect=null;
    @Context
    private UriInfo context;
    
    /**
     * Creates a new instance of ProductResource
     */
    public Prod() {
       connect=prodCon.getConnection();
    }

    /**
     * Retrieves representation of an instance of com.oracle.products.ProductResource
     * @return an instance of java.lang.String
     */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public String getAllProducts() throws SQLException
   {
       if(connect==null)
       {
           return "not connected";
       }
       else {
           String query="Select * from product";
           PreparedStatement preparedst = connect.prepareStatement(query);
           ResultSet rs = preparedst.executeQuery();
           String result="";
           JSONArray productArr = new JSONArray();
           while (rs.next()) {
                Map pMap = new LinkedHashMap();
                pMap.put("productID", rs.getInt("product_id"));
                pMap.put("name", rs.getString("name"));
                pMap.put("description", rs.getString("description"));
                pMap.put("quantity", rs.getInt("quantity"));
                productArr.add(pMap);
           }
            result = productArr.toString();
          return  result.replace("},", "},\n");
        }
       
   }
   
   @GET
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public String getproduct(@PathParam("id") int id) throws SQLException {
   
      if(connect==null)
       {
           return "not connected";
       }
       else {
           String query="Select * from product where product_id = ?";
           PreparedStatement prepstmt = connect.prepareStatement(query);
           prepstmt.setInt(1,id);
           ResultSet rs = prepstmt.executeQuery();
           String result="";
           JSONArray productArr = new JSONArray();
           while (rs.next()) {
                 Map pMap = new LinkedHashMap();
                pMap.put("productID", rs.getInt("product_id"));
                pMap.put("name", rs.getString("name"));
                pMap.put("description", rs.getString("description"));
                pMap.put("quantity", rs.getInt("quantity"));
                productArr.add(pMap);
           }    
                result = productArr.toString();
                
                 return result;
      }
   
   }
   
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_PLAIN)
   public String postProduct(String str) throws SQLException{
       JsonParser parser= Json.createParser(new StringReader(str));
       Map<String,String> pMap = new LinkedHashMap<String,String>();
       String key="";
       String val="";
       
       while(parser.hasNext()){
        JsonParser.Event event=parser.next();
            switch (event){
            case KEY_NAME :
              key = parser.getString();
              break;
            case VALUE_STRING:
              val=parser.getString();
              pMap.put(key, val);
              break;
            case VALUE_NUMBER:     
              val=parser.getString();
              pMap.put(key, val);
              break;  
            default :
              break;  
            }
       }    
       if(connect == null){
           return "Not connected";
       }
       else {
            String query="INSERT INTO product (product_id,name,description,quantity) VALUES (?,?,?,?)";
            PreparedStatement prepstmt=connect.prepareStatement(query);
            prepstmt.setInt(1, Integer.parseInt(pMap.get("product_id")));
            prepstmt.setString(2, pMap.get("name"));
            prepstmt.setString(3, pMap.get("description"));
            prepstmt.setInt(4, Integer.parseInt(pMap.get("quantity")));
            prepstmt.executeUpdate();
            return "row has been inserted into the database";
           }
       
       
   }
   
   
   @PUT
   @Path("{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   public String  putProduct(@PathParam("id")  int id,String str) throws SQLException{
    JsonParser parser= Json.createParser(new StringReader(str));
       Map<String,String> pMap = new LinkedHashMap<>();
       String key="";
       String val="";
       
       while(parser.hasNext()){
        JsonParser.Event event=parser.next();
            switch (event){
            case KEY_NAME :
              key = parser.getString();
              break;
            case VALUE_STRING:
              val=parser.getString();
              pMap.put(key, val);
              break;
            case VALUE_NUMBER:     
              val=parser.getString();
              pMap.put(key, val);
              break;  
            default :
              break;  
            }
       }    
       if(connect == null){
           return "Not connected";
       }
       else {
            String query="UPDATE product SET  name = ?, description = ?, quantity = ? WHERE product_id =?" ;          PreparedStatement prepstmt=connect.prepareStatement(query);
            prepstmt.setString(1, pMap.get("name"));
            prepstmt.setString(2, pMap.get("description"));
            prepstmt.setInt(3, Integer.parseInt(pMap.get("quantity")));
            prepstmt.setInt(4, id);
            prepstmt.executeUpdate();
            return "row has been updated into the database";
           }
   
   }
 
   @DELETE
   @Path("{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_PLAIN)
   public String deleteProduct(@PathParam("id") int id) throws SQLException{
       
        if(connect==null)
        {
           return "not connected";
        }
        else {
           String query="DELETE FROM product WHERE product_id = ?";
           PreparedStatement prepstmt = connect.prepareStatement(query);
           prepstmt.setInt(1,id);
           prepstmt.executeUpdate();
           return "The specified row is deleted";
           
        }
   
    }
}
