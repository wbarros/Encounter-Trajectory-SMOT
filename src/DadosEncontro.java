public class DadosEncontro {
	
	private int id_stop;
	private int uid;
	private int tid;
	private String descricao;
	private String data_inicio;
	private String start_time;
	private String end_time;
	private String data_fim;
	private int lugares_id;
	private double deltaX;
	private double deltaY;
	private double velocidade;
	
	
	
	public DadosEncontro(int id_stop, int uid, int tid, String descricao, String data_inicio, String start_time,
			String end_time, String data_fim, int lugares_id, double deltaX, double deltaY, double velocidade) {
		this.id_stop = id_stop;
		this.uid = uid;
		this.tid = tid;
		this.descricao = descricao;
		this.data_inicio = data_inicio;
		this.start_time = start_time;
		this.end_time = end_time;
		this.data_fim = data_fim;
		this.lugares_id = lugares_id;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.velocidade = velocidade;
	}
	
	public int getId_stop() {
		return id_stop;
	}
	public void setId_stop(int id_stop) {
		this.id_stop = id_stop;
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
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getData_inicio() {
		return data_inicio;
	}
	public void setData_inicio(String data_inicio) {
		this.data_inicio = data_inicio;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getData_fim() {
		return data_fim;
	}
	public void setData_fim(String data_fim) {
		this.data_fim = data_fim;
	}
	public int getLugares_id() {
		return lugares_id;
	}
	public void setLugares_id(int lugares_id) {
		this.lugares_id = lugares_id;
	}
	public double getDeltaX() {
		return deltaX;
	}
	public void setDeltaX(double deltaX) {
		this.deltaX = deltaX;
	}
	public double getDeltaY() {
		return deltaY;
	}
	public void setDeltaY(double deltaY) {
		this.deltaY = deltaY;
	}
	public double getVelocidade() {
		return velocidade;
	}
	public void setVelocidade(double velocidade) {
		this.velocidade = velocidade;
	}
	
	
	
}
