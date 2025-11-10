import java.util.*;

public class TuringMachine {
    private String regex;          
    private String estadoActual;  
    private char[] cinta;          
    private int cabeza;            
    private boolean aceptada;      

    // Constructor que inicializa la máquina con una expresión y una entrada
    public TuringMachine(String regex, String entrada) {
        this.regex = regex;
        this.estadoActual = "q0";
        this.cinta = new char[entrada.length() + 10];
        Arrays.fill(cinta, '_'); // Llena la cinta con espacios vacíos
        for (int i = 0; i < entrada.length(); i++) {
            cinta[i + 5] = entrada.charAt(i);
        }
        this.cabeza = 5;
        this.aceptada = false;
    }

    public boolean paso() {
        char actual = cinta[cabeza];

        switch (regex) {

            // (01)* → Acepta secuencias alternadas de 0 y 1, como 01, 0101, etc.
            case "(01)*":
                if (estadoActual.equals("q0")) {
                    if (actual == '0') { cabeza++; estadoActual = "q1"; }
                    else if (actual == '_') { estadoActual = "qf"; aceptada = true; }
                    else estadoActual = "qr";
                } else if (estadoActual.equals("q1")) {
                    if (actual == '1') { cabeza++; estadoActual = "q0"; }
                    else estadoActual = "qr";
                }
                break;

            // 1*0+1* → Acepta cadenas con al menos un 0 rodeado por 1's opcionales
            case "1*0+1*":
                if (estadoActual.equals("q0")) {
                    if (actual == '1') cabeza++;
                    else if (actual == '0') { cabeza++; estadoActual = "q1"; }
                    else estadoActual = "qr";
                } else if (estadoActual.equals("q1")) {
                    if (actual == '0') cabeza++;
                    else if (actual == '1' || actual == '_') { estadoActual = "qf"; aceptada = true; }
                }
                break;

            // a*b* → Acepta cualquier cantidad de a’s seguidas de b’s
            case "a*b*":
                if (estadoActual.equals("q0")) {
                    if (actual == 'a') cabeza++;
                    else if (actual == 'b') estadoActual = "q1";
                    else if (actual == '_') { estadoActual = "qf"; aceptada = true; }
                } else if (estadoActual.equals("q1")) {
                    if (actual == 'b') cabeza++;
                    else if (actual == '_') { estadoActual = "qf"; aceptada = true; }
                    else estadoActual = "qr";
                }
                break;

            // (a|b)*ba → Acepta cadenas que terminan con “ba”
            case "(a|b)*ba":
                if (estadoActual.equals("q0")) {
                    if (actual == 'a' || actual == 'b') cabeza++;
                    else if (actual == '_') { cabeza--; estadoActual = "q1"; }
                } else if (estadoActual.equals("q1")) {
                    if (cinta[cabeza] == 'a' && cinta[cabeza - 1] == 'b') {
                        estadoActual = "qf";
                        aceptada = true;
                    } else estadoActual = "qr";
                }
                break;

            // (0|1)*11(0|1)* → Acepta cadenas que contienen “11” en cualquier parte
            case "(0|1)*11(0|1)*":
                if (estadoActual.equals("q0")) {
                    if (actual == '1') { cabeza++; estadoActual = "q1"; }
                    else if (actual == '0') cabeza++;
                    else if (actual == '_') estadoActual = "qr";
                } else if (estadoActual.equals("q1")) {
                    if (actual == '1') { estadoActual = "qf"; aceptada = true; }
                    else if (actual == '0') { estadoActual = "q0"; cabeza++; }
                }
                break;
        }

        return estadoActual.equals("qf") || estadoActual.equals("qr");
    }

    public boolean esAceptada() { return aceptada; }

    public boolean finalizada() { return estadoActual.equals("qf") || estadoActual.equals("qr"); }

    public String getEstado() { return estadoActual; }

    public String mostrarCinta() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cinta.length; i++) {
            if (i == cabeza) sb.append("[").append(cinta[i]).append("]");
            else sb.append(" ").append(cinta[i]).append(" ");
        }
        return sb.toString();
    }
}
