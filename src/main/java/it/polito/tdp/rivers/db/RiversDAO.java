package it.polito.tdp.rivers.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.rivers.model.Flow;
import it.polito.tdp.rivers.model.River;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RiversDAO {
	Map<Integer,River> rivers;
	public Map <Integer, River> getAllRivers() {
		
		final String sql = "SELECT r.id as idr, r.name as nameR "
				+ "FROM river r";

		rivers = new HashMap<Integer,River>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				River r= new River(res.getInt("idr"), res.getString("nameR"));
				rivers.put(r.getId(), r);
			}

			conn.close();
			
		} catch (SQLException e) {
			//e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return rivers;
	}
	
	public void getInformazioni(River r) {
		
		final String sql = "SELECT MAX(f.day) AS max, MIN(f.day) AS MIN, COUNT(*) AS misure, AVG(f.flow) AS av "
				+ "FROM river r, flow f "
				+ "WHERE f.river=r.id AND r.name=? "
				+ "";

		
		
		try {
			
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, r.getName());
			ResultSet res = st.executeQuery();

			while (res.next()) {
				if(rivers.containsKey(r.getId())) {
				
				r.setFlowAvg(res.getDouble("av"));
				r.setNumeroM(res.getInt("misure"));
				r.setMaxDate(res.getDate("max"));
				r.setMinDate(res.getDate("min"));
				}
			}

			conn.close();
			
		} catch (SQLException e) {
			//e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		
	}
	
	public List <Flow> getAllFlows(River r){
		List <Flow> flows= new ArrayList<>();
		String sql="SELECT f.id,f.day,f.flow,f.river "
				+ "FROM flow f, river r "
				+ "WHERE f.river=r.id AND r.id=?";
		
		
		try {
			
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, r.getId());
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Flow f= new Flow( res.getDate("day").toLocalDate(),res.getDouble("flow"),r);
				flows.add(f);
			}
			Collections.sort(flows);
			r.setFlows(flows);
			conn.close();
			
			
			
		} catch (SQLException e) {
			//e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return flows;
		
		
	}
}
