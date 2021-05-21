package it.polito.tdp.rivers.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import it.polito.tdp.rivers.db.RiversDAO;

public class Model {
	
	RiversDAO dao;
	//parametri input
	
	Collection<River> rivers;
	
	//Coda degli eventi
	PriorityQueue<Flow> queue;
	
	//parametri di simulazione
	
	
	//Parametri di output
	int giorniErrore;
	double occupazioneMedia;
	
	
	
	
	public Model() {
		dao= new RiversDAO();
		rivers = dao.getAllRivers().values();
		for (River river : rivers) {
			dao.getAllFlows(river);
		}
		
	}
	
	public SimulationResult run(River r, double k) {
		queue= new PriorityQueue <Flow>();
		giorniErrore=0;
		
		
		queue.addAll(r.getFlows()); //ci aggiungo tutte le misurazioni del fiume
		//Ci servono poi le capacita
		List <Double> capacity= new ArrayList <Double>();//creo la lista.
		//ho una capacita max
		double Q= this.capienzaMassima(k, r)*60*60*24; //per passare in m3 al giorno
		//ho un'occupazione iniziale
		double C=Q/2;
		//devo avere un flusso minimo
		double fOutMin= 60*60*24*(0.8*r.getFlowAvg());
		
		System.out.print("Q: "+Q);
		
		Flow flow;
		
		while((flow=this.queue.poll())!=null) {//fin quando ho qualcosa ho degli eventi --> simulazione non finita
			//giusto per debug mi faccio una print
			System.out.println("Date: "+flow.getDay());
			// inizialmente so che fOut=fOutMin
			double fOut= fOutMin;
			
			//so che esiste il 5% di probabilità che fOut sia 10 volte fOutMin
			//dobbiamo dotare di una certa randomicità la simulazione
			
			if(Math.random()>0.95) {//esprimo così il 5% di prob
				fOut=10*fOutMin;
				System.out.println("10xfOutMin");
				
			}
			
			//parto da C=Q/2 ma poi questo C incrementa
			C=C+ flow.getFlow()*60*60*24;
			//prima situazione --> C>Q non va bene!!
			if(C>Q) {
			//evento trancimazione--> la quantità in più va via, C=Q, più di così non può andare
			}
			
			if(C<fOut) {
				//secondo caso--> non riesco a garantire la qtà minima
				//numeroGiorniErrore++
				this.giorniErrore++;
				C=0; //più di 0 non può scendere
				
			}
			else {
				//caso normale
				//faccio uscire la quantità giornaliera
				C=C-fOut; //faccio uscire la qta che serve perchè è disponibile
				
			}
			//aggiungo C alla lista
			capacity.add(C);
			
			
		}
		
		//mi serve ore la media delle C
		double CAvg=0;
		for(Double d:capacity) {
			CAvg+=d;
			
		}
		CAvg=CAvg/capacity.size();
		//devi ritornare un oggetto di tipo SimulationResult
		return new SimulationResult(CAvg, this.giorniErrore);
		
		
		}
	
	
	public Map <Integer, River> getAllRivers() {
		return this.dao.getAllRivers();
	}
	
	public void getInformazioni(River r) {
		
	}
	public RiversDAO getDao() {
		return dao;
	}
	
	
	public double capienzaMassima (double k, River r) {
		
		return k*r.getFlowAvg()*30;
	}
	
	
	
	
	
	public List <Flow> getAllFlows(River r){
		return dao.getAllFlows(r);
	}
}
