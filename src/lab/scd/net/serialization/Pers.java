package lab.scd.net.serialization;

import java.io.Serializable;

public class Pers implements Serializable {
	String nume;
	// Exercise: comment the line above and uncomment the one below.
	// A transient field is skipped by serialization, so it is not sent through
	// the socket. The server rebuilds the object with the field's default value:
	//   normal:    "Persoana: Alin vasrta: 14"
	//   transient: "Persoana: null vasrta: 14"
	//transient String nume;
	int varsta;

	Pers(String n, int v) {
		nume = n;
		varsta = v;
	}

	public String toString() {
		return "Persoana: " + nume + " vasrta: " + varsta;
	}
}
