import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import com.vividsolutions.jts.io.ParseException;

public class Encontro {
	public static void main(String[] args) throws SQLException, ParseException, FileNotFoundException {
		String sql; 
		Conexao conexao = new Conexao();
		List<List<DadosEncontro>> listaEncontro = new ArrayList<List<DadosEncontro>>();
		//System.setOut(new PrintStream(new FileOutputStream("output.txt")));
		Smot smot = new Smot();
		try {
			smot.executaSmot();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int qtd = 0;
		int qtd2 = 0;
		sql = "SELECT * FROM Stop WHERE deltax > 0 ORDER BY places_id,start_date";
		listaEncontro = montaMatriz(sql,conexao.getStatmentRoll());
		for(int i=0; i < listaEncontro.size();i++) {
			for (int j = 0; j < listaEncontro.get(i).size()-1; j++) {
				for (int k = j; k < listaEncontro.get(i).size()-1; k++) {
					if(listaEncontro.get(i).get(j).getUid() != listaEncontro.get(i).get(k).getUid() && listaEncontro.get(i).get(j).getVelocidade() == listaEncontro.get(i).get(k).getVelocidade()){
						LocalDateTime start_j = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseLocalDateTime(listaEncontro.get(i).get(j).getData_inicio()+" "+listaEncontro.get(i).get(j).getStart_time());
						LocalDateTime end_j = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseLocalDateTime(listaEncontro.get(i).get(j).getData_fim()+" "+listaEncontro.get(i).get(j).getEnd_time());
						LocalDateTime start_k = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseLocalDateTime(listaEncontro.get(i).get(k).getData_inicio()+" "+listaEncontro.get(i).get(k).getStart_time());
						LocalDateTime end_k = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseLocalDateTime(listaEncontro.get(i).get(k).getData_fim()+" "+listaEncontro.get(i).get(k).getEnd_time());
						boolean intervalo = avaliaIntervaloDataHora(start_j, end_j, start_k, end_k);
						if(intervalo){
							System.out.println("Encontro em "+listaEncontro.get(i).get(j).getDescricao()+" de Uid: "+listaEncontro.get(i).get(j).getUid()+" Tid: "+listaEncontro.get(i).get(j).getTid()+" e " +"Uid: "+listaEncontro.get(i).get(k).getUid()+" Tid: " +listaEncontro.get(i).get(k).getTid());
							qtd++;
						}
					}	
				}
			}
		}
		sql = "SELECT * FROM stop WHERE deltay > 0 ORDER BY places_id,start_date";
		listaEncontro = montaMatriz(sql,conexao.getStatmentRoll());
		for(int i=0; i < listaEncontro.size();i++) {
			for (int j = 0; j < listaEncontro.get(i).size()-1; j++) {
				for (int k = j; k < listaEncontro.get(i).size()-1; k++) {
					if(listaEncontro.get(i).get(j).getUid() != listaEncontro.get(i).get(k).getUid() && listaEncontro.get(i).get(j).getVelocidade() == listaEncontro.get(i).get(k).getVelocidade()){
						LocalDateTime start_j = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseLocalDateTime(listaEncontro.get(i).get(j).getData_inicio()+" "+listaEncontro.get(i).get(j).getStart_time());
						LocalDateTime end_j = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseLocalDateTime(listaEncontro.get(i).get(j).getData_fim()+" "+listaEncontro.get(i).get(j).getEnd_time());
						LocalDateTime start_k = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseLocalDateTime(listaEncontro.get(i).get(k).getData_inicio()+" "+listaEncontro.get(i).get(k).getStart_time());
						LocalDateTime end_k = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseLocalDateTime(listaEncontro.get(i).get(k).getData_fim()+" "+listaEncontro.get(i).get(k).getEnd_time());
						boolean intervalo = avaliaIntervaloDataHora(start_j, end_j, start_k, end_k);
						if(intervalo){
							System.out.println("Encontro em "+listaEncontro.get(i).get(j).getDescricao()+" de Uid: "+listaEncontro.get(i).get(j).getUid()+" Tid: "+listaEncontro.get(i).get(j).getTid()+" e " +"Uid: "+listaEncontro.get(i).get(k).getUid()+" Tid: " +listaEncontro.get(i).get(k).getTid());
							qtd2++;
						}
					}						
				}
			}
		}
		System.out.println(qtd+" Encontros detectados na direção de latitude");
		System.out.println(qtd2+" Encontros detectados na direção de longitude");
		System.out.println("Total de encontros: "+ (qtd+qtd2));
	}
	
	public static List<List<DadosEncontro>> montaMatriz(String sql,Statement stmt) throws SQLException  {
		List<List<DadosEncontro>> matlista = new ArrayList<List<DadosEncontro>>();
		int i=0;
		int lugares_id = 0;
		int lugares_idant = -1;
		ResultSet rs = stmt.executeQuery(sql);
		matlista.add(new ArrayList<DadosEncontro>());
		while(rs.next()) {
			int uid = rs.getInt(1);
			int tid = rs.getInt(2);
			int id_stop = rs.getInt(3);
			String descricao = rs.getString(4);
			String data_inicio = rs.getString(5);
			String start_time = rs.getString(6);
			String end_time = rs.getString(7);
			String data_fim = rs.getString(8);
			lugares_id = rs.getInt(10);
			double deltaX = rs.getDouble(11);
			double deltaY = rs.getDouble(12);
			double velocidade = rs.getDouble(13);
			if (lugares_id != lugares_idant && lugares_idant != -1) {
				i++;
				matlista.add(new ArrayList<DadosEncontro>());
			}
			lugares_idant = lugares_id;
			matlista.get(i).add(new DadosEncontro(id_stop,uid,tid,descricao,data_inicio,start_time,end_time,data_fim,lugares_id,deltaX,deltaY,velocidade));
		}
		rs.close();
		return matlista;
		
	}
	public static boolean avaliaIntervaloDataHora(LocalDateTime start_j,LocalDateTime end_j,LocalDateTime start_k,LocalDateTime end_k) {
		if(start_k.isAfter(start_j) && start_k.isBefore(end_j)){
			return true;
		}
		else if (end_k.isAfter(start_j) && end_k.isBefore(end_j)){ 
			return true;
		}
		else if (start_j.isAfter(start_k) && start_j.isBefore(end_k)) {
			return true;
		}
		else if (end_j.isAfter(start_k) && end_j.isBefore(end_k)){
			return true;
		}
//		else if (end_k.equals(end_j) && start_k.equals(start_k)) { //PARECE Q EXISTEM TRAJETORIAS REPETIDAS EM ISSO MOSTRA ELAS POREM TEM Q COLOCAR O SMOT PARA NÃO ARREDONDAR A VELOCIDADE
//			return true;
//		}
		else 
			return false;
	}

}
