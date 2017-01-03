import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

import java.util.ArrayList;
import java.util.List;

public class Smot {
	public void executaSmot() throws SQLException, com.vividsolutions.jts.io.ParseException {
		int k = 0, i = 0, j = 0;
		String ultimoStopLeaveTime = null;
		String stop_name1 = null;
		String stop_name2 = null;
		int stop_id1;
		int stop_id2; 
		String enterTime = null;
		String leaveTime = null;
		String minTime = null;
		
		String sql;
		ResultSet rs = null;
		ResultSet it = null;
		ResultSet in = null;
		List<Conexao> conexao = iniciaConexao();
		
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );
		Point ponto;
		Polygon poligono;
		
		List<Trajetoria> listaTraje = new ArrayList<Trajetoria>();
		sql = "INSERT INTO Intersection(\"uid\",\"tid\") SELECT DISTINCT t.uid,t.tid FROM Trajectories as t , Places as s WHERE st_intersects(t.geom, s.geom) ='t'";
		conexao.get(0).getStatmentRoll().executeUpdate(sql); //popula a tabela de intersecao com id de usuario e trajetoria que contem alguma intersecao
		sql = "SELECT min ( min_time ) FROM Places";
		rs = conexao.get(0).getStatmentRoll().executeQuery(sql);
		rs.next();
		minTime = rs.getString(1);
		sql = "SELECT id,description,ST_ASTEXT(geom),min_time from Places";
		it = conexao.get(1).getStatmentRoll().executeQuery(sql); //pontos de stops
		sql = "SELECT uid,tid FROM Intersection";
		in = conexao.get(2).getStatment().executeQuery(sql);		
		while(in.next()){//PEGA UMA TRAJETORIA QUE HOUVE INTERSECAO.
			int iuid = in.getInt(1);
			int itid = in.getInt(2);
			sql = "SELECT id,uid,tid,st_astext(geom),date,time,ST_X(geom),ST_Y(geom) FROM Trajectories WHERE uid = "+iuid+"and tid = "+itid;
			listaTraje = montaTrajetoria(sql, conexao.get(0).getStatmentRoll());
			int ultimo = listaTraje.size()-1;
			i=0;
			int ultimoIdTstop = -1; //ID da trajetoria do ultimo ponto do stop
			stop_id1 = -1;
			stop_name1 = null;
			ultimoStopLeaveTime = "00:00:00";			
			while(i <= listaTraje.size()-1){// TESTA TODOS OS PONTOS DA TRAJETORIA
				if (k > 0) //apontar o registro de stop para a primeira posição. 
					it.beforeFirst();
				while(it.next()) {// TESTA TODOS STOPS EM CADA PONTO DA TRAJETORIA
					int sgid = it.getInt(1);
					String sdescricao = it.getString(2);
					String sgeom = it.getString(3);
					String smin_time = it.getString(4);
					ponto = (Point) reader.read(listaTraje.get(i).getTgeom());
					poligono = (Polygon) reader.read(sgeom);
					if(ponto.intersects(poligono)){//SE O PONTO DA TRAJETORIA INTECEPTO O POLIGONO
						System.out.println("ENTRADA:"+listaTraje.get(i).getData()+" "+listaTraje.get(i).getTime());
//						String entrada = listaTraje.get(i).getData();
						String dataInicio = listaTraje.get(i).getData();
						enterTime = listaTraje.get(i).getTime();// enterTime aqui.
						double lat1 = listaTraje.get(i).getxLat();
						double lon1 = listaTraje.get(i).getyLon();
						i++;
						while(i <= listaTraje.size()-1) { // verificar se os proximos pontos interceptam o mesmo poligono
							ponto = (Point) reader.read(listaTraje.get(i).getTgeom());
							poligono = (Polygon) reader.read(sgeom); 
							if(!ponto.intersects(poligono)) {
								break;
							}
							i++;
						}
						i--;
						System.out.println("SAIDA:"+listaTraje.get(i).getData()+" "+listaTraje.get(i).getTime());
//						String saida = listaTraje.get(i).getData();
//						noEquals(entrada,saida,listaTraje.get(i).getUid(),listaTraje.get(i).getTid(),listaTraje.get(i).getId());
						leaveTime = listaTraje.get(i).getTime();
						String dataTermino = listaTraje.get(i).getData();
						double lat2 = listaTraje.get(i).getxLat();
						double lon2 = listaTraje.get(i).getyLon();
						double deltaX = Math.abs(lat2 - lat1);
						double deltaY = Math.abs(lon2 - lon1);
						if(deltaX > deltaY)
							deltaY = -1;
						else
							deltaX = -1;
						double velocidadeMedia = 0;
						double tempo = tempoHoras(enterTime,leaveTime);
						double distancia = haversine(lat1, lon1, lat2, lon2);
						if (tempo != 0)
							velocidadeMedia = Math.round(distancia/tempo);
						try {
							if(verificaTempo(dataInicio,enterTime,dataTermino,leaveTime,smin_time)) {//  adiciona os pontos de stop e moves no banco de dados.
								System.out.println("Tempo: "+tempo+" Distancia: "+distancia+ " VelocidadeRound: "+velocidadeMedia+ "VelocidadeReal: "+distancia/tempo);
								sql = "INSERT INTO Stop VALUES("+listaTraje.get(i).getUid()+","+listaTraje.get(i).getTid()+","+"DEFAULT"+","+"'"+sdescricao+"'"+ "," + "'"+dataInicio+"'"+ "," +"'" +enterTime+"'" +","+"'"+leaveTime+"'" +","+ "'"+dataTermino+"'"+ ","+listaTraje.get(i).getId()+","+sgid+","+deltaX+","+deltaY+","+velocidadeMedia+")";
								stop_name2 = sdescricao;
								conexao.get(0).getStatment().executeUpdate(sql);
								sql = "SELECT id FROM Stop ORDER BY id DESC LIMIT 1"; //PEGA O ID DO Stop
								rs = conexao.get(0).getStatment().executeQuery(sql);
								rs.next();
								stop_id2 = rs.getInt(1);
								sql = "INSERT INTO Move VALUES("+listaTraje.get(i).getUid()+","+listaTraje.get(i).getTid()+","+"DEFAULT"+","+"'"+stop_name1+"'"+","+stop_id1+","+"'"+stop_name2+"'"+","+stop_id2+","+ "'"+listaTraje.get(i).getData()+"'"+ "," +"'"+ultimoStopLeaveTime+"'"+","+"'"+enterTime+"'"+")";
								conexao.get(0).getStatment().executeUpdate(sql);
								stop_id1 = stop_id2;
								stop_name1 = stop_name2;
								ultimoStopLeaveTime = leaveTime;
								ultimoIdTstop = listaTraje.get(i).getId();
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				k++;
				i++;
				j = 1;
				try {
					while((i + j <= listaTraje.size()-1) && (verificaTempoMinimo(listaTraje,i,j,minTime))) {
						j++;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				it.beforeFirst();
				int existe = 0; 
				while(it.next() && (i + j <= listaTraje.size()-1)) {
					String sgeom = it.getString(3);
					ponto = (Point) reader.read(listaTraje.get(i+j-1).getTgeom());
					poligono = (Polygon) reader.read(sgeom); 
					if(ponto.intersects(poligono)){//SE O PONTO DA TRAJETORIA INTECEPTO O POLIGONO
						existe = 1;
						break;
					}
				}
				if(existe == 0) {
					i = i + j;
				}
			}
			if(ultimoIdTstop != -1){ //SE não teve nenhum stop então não existe move
				boolean pertence = false;
				if(ultimoIdTstop == listaTraje.get(ultimo).getId()) // A trajetoria acabou em um stop
					pertence = true;
				if(pertence == false) {
					sql = "INSERT INTO Move VALUES("+listaTraje.get(ultimo).getUid()+","+listaTraje.get(ultimo).getTid()+","+"DEFAULT"+","+"'"+stop_name1+"'"+","+stop_id1+","+"'null'"+","+"-1"+","+ "'"+listaTraje.get(ultimo).getData()+"'"+ "," +"'"+ultimoStopLeaveTime+"'"+","+"'"+listaTraje.get(ultimo).getTime()+"'"+")";
					conexao.get(0).getStatmentRoll().executeUpdate(sql);
				}
			}
			
		}
		in.close();
		it.close();
		rs.close();
		terminaConexao(conexao);
	}
	public boolean verificaTempo(String data1,String tempo1,String data2, String tempo2,String deltaStop) throws ParseException {
		LocalTime min = new LocalTime(deltaStop);
//		LocalTime start = new LocalTime(enterTime);
//		LocalTime end = new LocalTime(leaveTime);
		LocalDateTime start = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseLocalDateTime(data1+" "+tempo1);
		LocalDateTime end = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseLocalDateTime(data2+" "+tempo2);
		Period period   = new Period(start, end);
//		System.out.println(((end.getHourOfDay()*3600+end.getMinuteOfHour()*60 + end.getSecondOfMinute()) - (start.getHourOfDay()*3600+start.getMinuteOfHour()*60 + start.getSecondOfMinute())) + " STOP: " +(min.getHourOfDay()*3600 + min.getMinuteOfHour()*60+min.getSecondOfMinute()));
//		if (((end.getHourOfDay()*3600+end.getMinuteOfHour()*60 + end.getSecondOfMinute()) - (start.getHourOfDay()*3600+start.getMinuteOfHour()*60 + start.getSecondOfMinute())) >= (min.getHourOfDay()*3600 + min.getMinuteOfHour()*60 + min.getSecondOfMinute())){
		System.out.println((period.getHours()*3600+period.getMinutes()*60+period.getSeconds())+ " STOP: " +(min.getHourOfDay()*3600 + min.getMinuteOfHour()*60+min.getSecondOfMinute()));
		if(data1 == data2) {
			if((period.getHours()*3600+period.getMinutes()*60+period.getSeconds()) >= (min.getHourOfDay()*3600 + min.getMinuteOfHour()*60 + min.getSecondOfMinute())){
				return true;
			}
			else {return false;}
		}
		else if((period.getDays()*86400+period.getHours()*3600+period.getMinutes()*60+period.getSeconds()) >= (min.getHourOfDay()*3600 + min.getMinuteOfHour()*60 + min.getSecondOfMinute())){
			return true;
		}
		else {return false;}
	}
	public List<Trajetoria> montaTrajetoria(String sql,Statement stmt) throws SQLException {
		List<Trajetoria> lista = new ArrayList<Trajetoria>();
		
		ResultSet rs = stmt.executeQuery(sql); //contem todas as trajetorias brutas que tem alguma intersecao
		while(rs.next()){// 
			int tid = rs.getInt(1);
			int tuid  = rs.getInt(2);
			int ttid = rs.getInt(3);
			String tgeom = rs.getString(4);
			String tdata = rs.getString(5);
			String ttime = rs.getString(6);
			float xlat = rs.getFloat(7);
			float ylon = rs.getFloat(8);
			lista.add(new Trajetoria(tid, tuid, ttid, tgeom, tdata, ttime, xlat, ylon));
		}
		return lista;	
	}

	public boolean verificaTempoMinimo(List<Trajetoria> lista,int i,int j,String minTime) throws ParseException {
		LocalTime min = new LocalTime(minTime);
		LocalDateTime start = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseLocalDateTime(lista.get(i).getData()+" "+lista.get(i).getTime());
		LocalDateTime end = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseLocalDateTime(lista.get(i+j).getData()+" "+lista.get(i+j).getTime());
		Period period   = new Period(start, end);
		if((period.getSeconds()+ period.getMinutes()*60 + period.getHours()*3600) < (min.getHourOfDay()*3600 + min.getMinuteOfHour()*60 + min.getSecondOfMinute())) {
			return true;
		}
		else {return false;}
	}
	public List<Conexao> iniciaConexao() {
		List<Conexao> conexao = new ArrayList<Conexao>();
		for (int i = 0; i < 3; i++) {
			conexao.add(new Conexao());
		}
		return conexao;
	}
	public void terminaConexao(List<Conexao> conexao) {
		for (int i = 0; i < 3; i++) {
			conexao.get(i).disconecta();
		}
		
	}
	public static double haversine(double lat1, double lon1, double lat2, double lon2) {
    	double R = 6372.8; //Em  quilômetros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
 }
	public double tempoHoras(String enterTime,String leaveTime) {
		LocalTime start = new LocalTime(enterTime);
		LocalTime end = new LocalTime(leaveTime);
		return Math.abs((end.getHourOfDay() + end.getMinuteOfHour()/60.0 + end.getSecondOfMinute()/3600.0) - (start.getHourOfDay()+start.getMinuteOfHour()/60.0 + start.getSecondOfMinute()/3600.0));
	}
	public void noEquals(String enterTime,String leaveTime,int uid,int tid, int id) {
		LocalDateTime start = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDateTime(enterTime);
		LocalDateTime end = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDateTime(leaveTime);
		if (!start.equals(end)) {
			System.out.println("Entrada: "+enterTime );
			System.out.println("Saída: "+leaveTime + "uid: "+uid +" tid:"+tid+" id:"+id );
		}
			
	}
}
