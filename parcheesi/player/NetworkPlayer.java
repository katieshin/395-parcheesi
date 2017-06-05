package parcheesi.player;

import java.net.Socket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import parcheesi.Board;
import parcheesi.player.Player;
import parcheesi.serializer.xml.Node;
import parcheesi.serializer.xml.BasicXMLSerializer;
import parcheesi.serializer.xml.BasicXMLDeserializer;

import static parcheesi.serializer.xml.Element.*;

public class NetworkPlayer {
	Socket socket;
	Player player;

	BufferedReader input;
	DataOutputStream output;

	public NetworkPlayer(Socket socket, Player player) {
		this.socket = socket;
		this.player = player;

		BasicXMLDeserializer deserializer = new BasicXMLDeserializer();

		try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				 DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
			this.input = input;
			this.output = output;

			String message = this.input.readLine();
			Node messageXML;

			while (message != null) {
				System.out.println(message);
				messageXML = deserializer.parse(message);

				String out = Name().child("my Name Is What").toString();
				System.out.println(out);

				output.write(out.getBytes());

				message = this.input.readLine();
			}
		} catch (Exception e) {
			System.out.println("Failed to create input/output streams");
			System.out.println(e);
			e.printStackTrace();
		};

		System.out.println("Server closed connection.");
	}

	public static void main(String[] args) {
		try (Socket sock = new Socket("localhost", 8000)) {
			Player p = new SimplePlayer();

			new NetworkPlayer(sock, p);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
