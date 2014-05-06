package Server;

public class ServerMain {
	
	private int port;
	private Server2 server;
	
	public ServerMain(int port){
		this.port = port;
		server = new Server2(port);
	}
	
	public static void main(String[] args){
		int port;
		if(args.length != 1){
			System.err.println("Invalid arguments");
			return;
		}
		
		port = Integer.parseInt(args[0]);
		new ServerMain(port);
	}
}
