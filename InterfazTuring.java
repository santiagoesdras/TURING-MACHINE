import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InterfazTuring extends JFrame {
    private JComboBox<String> expresionesCombo;
    private JTextField entradaField;
    private JTextArea cintaArea, estadoArea, descripcionArea;
    private JButton pasoBtn, autoBtn, reiniciarBtn, iniciarBtn;
    private Timer timer;
    private TuringMachine maquina;

    // Expresiones regulares
    private final String[][] expresiones = {
        {"(01)*", "Cadenas formadas por repeticiones del patrón '01'."},
        {"1*0+1*", "Cadenas con al menos un '0' rodeado por unos opcionales."},
        {"a*b*", "Cadenas con varias 'a' seguidas de varias 'b'."},
        {"(a|b)*ba", "Cadenas que terminan con 'ba'."},
        {"(0|1)*11(0|1)*", "Cadenas que contienen el patrón '11' en cualquier parte."}
    };

    public InterfazTuring() {
        // Configuración general de la ventana
        setTitle("Máquina de Turing");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        Color fondo = new Color(230, 240, 255);
        Color areaColor = new Color(245, 247, 250);
        Color botonAzul = new Color(80, 130, 200);
        Color botonCeleste = new Color(120, 170, 230);
        Color botonGris = new Color(200, 210, 220);
        Color textoNegro = Color.BLACK;
        Color textoBlanco = Color.WHITE;
        Color azulOscuro = new Color(25, 25, 112);

        getContentPane().setBackground(fondo);

        // Panel superior
        JPanel top = new JPanel(new GridLayout(3, 1, 5, 5));
        top.setBackground(fondo);

        // Selección de expresión
        JPanel fila1 = new JPanel();
        fila1.setBackground(fondo);
        JLabel labelRegex = new JLabel("Expresión regular:");
        labelRegex.setForeground(azulOscuro);
        fila1.add(labelRegex);
        String[] regexList = new String[expresiones.length];
        for (int i = 0; i < expresiones.length; i++) regexList[i] = expresiones[i][0];
        expresionesCombo = new JComboBox<>(regexList);
        fila1.add(expresionesCombo);
        top.add(fila1);

        // Descripción de la expresión seleccionada
        descripcionArea = new JTextArea(2, 60);
        descripcionArea.setEditable(false);
        descripcionArea.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        descripcionArea.setBackground(fondo);
        descripcionArea.setForeground(azulOscuro);
        descripcionArea.setWrapStyleWord(true);
        descripcionArea.setLineWrap(true);
        descripcionArea.setText(expresiones[0][1]);
        top.add(descripcionArea);

        // Ingresar la cadena
        JPanel fila2 = new JPanel();
        fila2.setBackground(fondo);
        JLabel labelEntrada = new JLabel("Cadena de entrada:");
        labelEntrada.setForeground(azulOscuro);
        fila2.add(labelEntrada);
        entradaField = new JTextField(20);
        iniciarBtn = new JButton("Iniciar");
        fila2.add(entradaField);
        fila2.add(iniciarBtn);
        top.add(fila2);

        add(top, BorderLayout.NORTH);

        // Actualiza la descripción 
        expresionesCombo.addActionListener(e -> {
            int idx = expresionesCombo.getSelectedIndex();
            descripcionArea.setText(expresiones[idx][1]);
        });

        // Muestra la cinta y el estado
        cintaArea = new JTextArea(4, 60);
        cintaArea.setFont(new Font("Consolas", Font.BOLD, 18));
        cintaArea.setEditable(false);
        cintaArea.setBackground(Color.WHITE);
        cintaArea.setForeground(azulOscuro);

        estadoArea = new JTextArea(2, 60);
        estadoArea.setFont(new Font("Segoe UI", Font.BOLD, 14));
        estadoArea.setEditable(false);
        estadoArea.setBackground(areaColor);
        estadoArea.setForeground(azulOscuro);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(fondo);
        center.add(new JScrollPane(cintaArea), BorderLayout.CENTER);
        center.add(estadoArea, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        // Botones de control
        JPanel bottom = new JPanel();
        bottom.setBackground(fondo);

        pasoBtn = crearBoton("Paso a Paso", botonAzul, textoBlanco);
        autoBtn = crearBoton("Automático", botonCeleste, textoBlanco);
        reiniciarBtn = crearBoton("Reiniciar", botonGris, textoNegro);

        bottom.add(pasoBtn);
        bottom.add(autoBtn);
        bottom.add(reiniciarBtn);
        add(bottom, BorderLayout.SOUTH);

        // Acciones de los botones
        iniciarBtn.addActionListener(e -> iniciar());
        pasoBtn.addActionListener(e -> paso());
        autoBtn.addActionListener(e -> auto());
        reiniciarBtn.addActionListener(e -> reiniciar());
    }

    private JButton crearBoton(String texto, Color fondo, Color textoColor) {
        JButton boton = new JButton(texto);
        boton.setBackground(fondo);
        boton.setForeground(textoColor);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setPreferredSize(new Dimension(130, 35));
        boton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    // Inicia la simulación con la cadena ingresada
    private void iniciar() {
        String entrada = entradaField.getText().trim();
        if (entrada.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresa una cadena");
            return;
        }

        String regex = (String) expresionesCombo.getSelectedItem();
        maquina = new TuringMachine(regex, entrada);
        cintaArea.setText(maquina.mostrarCinta());
        estadoArea.setText("Estado actual: q0");
    }

    private void paso() {
        if (maquina == null) return;
        maquina.paso();
        cintaArea.setText(maquina.mostrarCinta());
        estadoArea.setText("Estado actual: " + maquina.getEstado());

        if (maquina.finalizada()) mostrarResultado();
    }

    // Ejecuta automáticamente
    private void auto() {
        if (maquina == null) return;
        timer = new Timer(600, e -> {
            if (!maquina.finalizada()) paso();
            else ((Timer) e.getSource()).stop();
        });
        timer.start();
    }

    // Reinicia la simulación
    private void reiniciar() {
        if (timer != null) timer.stop();
        cintaArea.setText("");
        estadoArea.setText("");
        entradaField.setText("");
        maquina = null;
    }

    private void mostrarResultado() {
        String mensaje = maquina.esAceptada() ? "Cadena ACEPTADA" : "Cadena RECHAZADA";
        JOptionPane.showMessageDialog(this, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazTuring().setVisible(true));
    }
}
