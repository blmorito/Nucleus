package com.example.nucleus;

public class Globals{
	   private static Globals instance;
	 
	   // Global variable
	   private String myIp = "192.168.8.120";
	 
	   // Restrict the constructor from being instantiated
	   private Globals(){}
	 
	   
	   public String getMyIp(){
	     return this.myIp;
	   }
	 
	   public static synchronized Globals getInstance(){
	     if(instance==null){
	       instance=new Globals();
	     }
	     return instance;
	   }
	}
