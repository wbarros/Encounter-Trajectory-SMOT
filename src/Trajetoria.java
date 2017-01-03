public class Trajetoria {
	
	private int id;
	private int uid;
	private int tid;
//	private PGgeometry tgeom; antes JTS
	private String tgeom;
	private String data;
	private String time;
	private float xlat;
	private float ylon;
		
	public Trajetoria(int id, int uid, int tid, String tgeom,String data, String time, float xlat, float ylon) {
		this.id = id;
		this.uid = uid;
		this.tid = tid;
		this.tgeom = tgeom;
		this.data = data;
		this.time = time;
		this.xlat = xlat;
		this.ylon = ylon;
	}
	
	public float getxLat() {
		return xlat;
	}
	public void setxLat(float xlat) {
		this.xlat = xlat;
	}
	public float getyLon() {
		return ylon;
	}
	public void setyLon(float ylon) {
		this.ylon = ylon;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public String getTgeom() {
		return tgeom;
	}
	public void setTgeom(String tgeom) {
		this.tgeom = tgeom;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
public String toString() {
		
		return id+ " " +uid+ " "+tid+ " "+tgeom+ " "+time+"\n";
	}	
}
