package com.forkexec.hub.ws;

import java.util.List;

import javax.jws.WebService;

import com.forkexec.hub.domain.*;

import com.forkexec.rst.ws.cli.*;
import com.forkexec.rst.ws.*;
import com.forkexec.pts.ws.cli.*;
import com.forkexec.pts.ws.*;
import com.forkexec.cc.ws.cli.*;
import java.util.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

import java.util.Collections.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

public class PointsManager{

	private HubEndpointManager endpointManager;

	private Map<String, PointsView> cachedAccounts = new ConcurrentHashMap<>();
	private int currentTag=0;

	public PointsManager(HubEndpointManager endpointManager){
		this.endpointManager = endpointManager;		
	}


	public Collection<UDDIRecord> getPointsServers() {
		Collection<UDDIRecord> pointsURLs = null;
		try {
			// verificacao
			pointsURLs = endpointManager.getUddiNaming().listRecords("T06_Points" + "%");

			
		} catch(UDDINamingException e) {
			System.out.println("Failed to contact the UDDI server:"+e.getMessage()+" ("+e.getClass().getName()+")\n");
		}

		return pointsURLs;
	}

	public void spendPoints(String email, int value){

		PointsView view = checkCachedAccount(email);
		synchronized(view){
		

			int v = view.getPoints();

			write(email,v-value);
		}
	}

	public void addPoints(String email, int value){

		PointsView view = checkCachedAccount(email);
		synchronized(view){

			int v = view.getPoints();

			write(email,v+value);

		}
	}

	public int getBalance(String email){
		PointsView view = checkCachedAccount(email);

		return view.getPoints();
	}

	public PointsView read(String email) {
		
		Collection<UDDIRecord> pointsURLs = this.getPointsServers();
		Response<GetPointsResponse> response = null;
		PointsView view = new PointsView();
		int maxTag=-1;
		float Q = 0;
		int pts = 0;
		int contador = 0;
		int value = 0; 

		for(UDDIRecord ptsURL : pointsURLs) {
			try {
				pts++;
				PointsClient client = new PointsClient(ptsURL.getUrl());
				response = client.getPointsAsync(email);
			} catch(Exception e) {
				//throwInvalidUserId("Email already exipts");
			}
			
		}
		
		Q = Math.round((pts/2)+1);
		 ArrayList<PointsView> list = new ArrayList<PointsView>();
         while (contador < Q) {
        	if(response.isDone()) {
        		contador ++;
        		try {
					list.add(response.get().getReturn());
				} catch (Exception e) {
					System.err.println("Caught exception on read: " + e);
				}
        	} 
        	
            try {
				Thread.sleep(50 /* milliseconds */);
			} catch (Exception e) {
				System.err.println("Caught exception on read: " + e);
			}
         }
         
         //verificar a maior tag
         for(int i=0; i < list.size() ; i++) {
        	if(list.get(i).getTag() > maxTag) {
        		maxTag = list.get(i).getTag();
        		value = list.get(i).getPoints();
        	}
         }
        
		//view.setvalue(value);
        try {
			view.setPoints(value);
		} catch (Exception e) {
			System.err.println("Caught exception on read: " + e);
		}
        view.setTag(maxTag); 
         
		return view;
	}

	public void write(String email, int value) {
		
		Collection<UDDIRecord> pointsURLs = this.getPointsServers();
		Response<SetPointsResponse> response = null;
		int contador = 0;
		float Q = 0;
		int pts = 0;
		
		PointsView view = checkCachedAccount(email);
		
		int newTag = view.getTag() + 1;
		view.setTag(newTag);
		view.setPoints(value);
		updateCachedAccount(email,view); 
		for(UDDIRecord ptsURL : pointsURLs) {
			try {
				pts++;
				PointsClient client = new PointsClient(ptsURL.getUrl());
				response = client.setPointsAsync(email,value,newTag);
				System.out.println("USER: " + email+ " Points: "+ value + " values TAG: "+ newTag);
			} catch(Exception e) {
				//throwInvalidUserId("Email already exipts");
			}
			
		}

		Q = Math.round((pts/2)+1);
		while (contador < Q) {
	        if(response.isDone()) {
	        	contador ++;
	        }
	        try {
				Thread.sleep(50 /* milliseconds */);
			} catch (InterruptedException e) {
				System.err.println("Caught exception on write: " + e);
			}
	    }
} 
	private PointsView checkCachedAccount(String email) {
		PointsView view = null;
		//if (email==null) 

		if (cachedAccounts.containsKey(email)) {

			view = cachedAccounts.get(email);
			System.out.println("ContainsCache : "+ email + "  " +  view.getPoints() + "  " + view.getTag());
		}
		if (view==null || view.getTag()<this.currentTag) {
			view = read(email);
			System.out.println("NewOrUpdateCache : "+ email + "  " +  view.getPoints() + "  " + view.getTag());
			updateCachedAccount(email,view);
		}
		return view;
	}

	private void updateCachedAccount(String email, PointsView pv) {
		if (cachedAccounts.containsKey(email)){
			cachedAccounts.replace(email,pv);
			System.out.println("UpdatedCache : "+ email + "  " +  pv.getPoints() + "  " + pv.getTag());
		}
		else{ cachedAccounts.put(email,pv);}
	}

	private void throwInvalidUserId(final String message) throws InvalidUserIdFault_Exception {
		InvalidUserIdFault faultInfo = new InvalidUserIdFault();
		faultInfo.message = message;
		throw new InvalidUserIdFault_Exception(message, faultInfo);
	}
	private void throwNotEnoughPoints(final String message) throws NotEnoughPointsFault_Exception {
		NotEnoughPointsFault faultInfo = new NotEnoughPointsFault();
		faultInfo.message = message;
		throw new NotEnoughPointsFault_Exception(message, faultInfo);
	}
}