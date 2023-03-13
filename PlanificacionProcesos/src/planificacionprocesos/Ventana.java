package planificacionprocesos;
//--------------------- lIBRERIAS ------------------------//
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.*;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.jfree.ui.RefineryUtilities;
//------------------------------------------------------//
public class Ventana extends JFrame implements ActionListener{
    //____________________________________________________________________________________________________________________________________________________  
    Proceso arregloProcesos[];                                       //arreglo donde se instanciaran los procesos entrantes
    int historial[];          //IMPORTANTE: historial es un arreglo que tendrá el proceso que se ejecuto en un instante i
                               //Ej: historial[4]= 2 ... quiere decir que en el instante 4 se ejecuto el proceso 2.
    int count=0;                                                     //contador para ubicar procesos en arreglo unidimensional
    int NumProcI;                                                    //numero de procesos entrantes
    public JPanel Panel1, Panel2;                                    //paneles para la ventana
    public JLabel etiqueta13,etiqueta19,etiqueta20,etiqueta21;       //etiqueta para mejorar el diseño
    public JTextField numeroDeProcesos,instanteLlegada,                 //cajas de texto para ingresar datos de procs.
            rafagaInicial, rafagaES, 
            rafagaFinal, prioridad,  quantum; 
    public JButton boton1, boton2, boton3, boton4, boton5, boton6;   //botones para la gestion de datos entrantes
    public JComboBox caja1,caja2;                                    //caja con opciones de seleccion de algortimos no apropiativos
    public String[] lista1 = {"Algoritmo First Come First Served"    //algoritmos de politica no apropiativa
            ,"Algoritmo Shortest-Job-First"
            ,"Algoritmo High Response Next Time"};
    public String[] lista2 = {"Aplicar Penalización y Recompensa", "No Aplicar Penalización y Recompensa"};
    public boolean panel=false;                                      //si es false, quiere decir que estamos en el panel1 y si no estamos en el panel 2
    //_____________________________________________________________________________________________________________________________________________________                             
    Ventana(){ //CONSTRUCTOR
        setTitle("Algoritmo de Planificación de Colas Multiples");
        this.setSize(900,700);                                    
        setLocationRelativeTo(null);                              
        
        //COMPONENTES:
        Paneles(); 
        Etiquetas();
        Botones();
        CamposTextuales();
        camposDeOpcion();
        //Operación para dejar de ejecutar el programa una vez cerrado la ventana.
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);                 
    }
    //Paneles:
    private void Paneles(){                              //funcion que crea los paneles de la ventana.
        Panel1 =new JPanel();                            //panel para el ingreso de información.
        Panel2 =new JPanel();                            //panel para la tabulacion de los resultados
        this.getContentPane().add(Panel1);               // añadimos el panel al objeto ventana.
        Panel1.setBackground(new Color(255, 253, 189));  //establece un color para el panel.
        Panel1.setLayout(null);                          //desactivamos el diseño estandar de posicionamiento de la GUI.
    }
    //Etiquetas:
    private void Etiquetas(){ 
        //INFORMACION TEXTUAL DEL PROGRAMA, NO ES RELEVANTE SU LECTURA----------------------------------------------------------------------------------
        JLabel etiqueta1= new JLabel("<html><body>Simulador de Algoritmo de Planificación"
                + "<br>de Colas Multiples con Retroalimentación</body></html>");  
        
        JLabel etiqueta2= new JLabel("<html><body><b>1)</b> "
                + "Para la siguiente simulación puedes ingresar los siguientes datos <b>(Opción b.)</b> o generarlos de"
                + " forma aleatoria <b>(Opción a.)</b>:</body></html> :");
        JLabel etiqueta3= new JLabel("<html><body><b>a)</b> Si desea generar la información de forma aleatoria tenga en cuenta que el número de"
                + " procesos será 10 en cualquier caso.</body></html> :");
        JLabel etiqueta4= new JLabel("<html><body><b>b)</b> <br> <font color=\"red\"><b>*</b></font>Número de Procesos:</body></html>)");
        JLabel etiqueta5= new JLabel("Instante de llegada:");
        JLabel etiqueta6= new JLabel("Rafaga de CPU Inicial:");
        JLabel etiqueta7= new JLabel("Rafaga de E/S:");
        JLabel etiqueta8= new JLabel("Rafaga de CPU Final:");
        JLabel etiqueta9= new JLabel("Prioridad:");
        JLabel etiqueta10= new JLabel("<html><body><font color=\"red\"><b>*</b></font>Información del proceso: (digite los datos"
                + " de un proceso,ingresalos y repite<br> hasta haber registrado los de tu caso).</body></html>)");
        JLabel etiqueta11= new JLabel("<html><body><b>2)</b>Nuestra aplicación tiene 2 Colas, la <b>primer cola</b> de mayor prioridad tiene una <b>política "
                + "apropiativa</b>, mientras <br> que para la <b>segunda cola</b>, se requiere que escoga entre los siguientes algoritmos de <b>política no apriopiativa</b>:</body></html>");
        
        JLabel etiqueta12= new JLabel("<html><body> *Las <b>rafagas de CPU Iniciales</b> entrarán a la <b>Cola 1</b> y <br> "
                + " *las <b>rafagas de CPU finales</b> entrarán a la <b>Cola 2</b></body></html>");
        etiqueta13= new JLabel(": Restantes");
        JLabel etiqueta14= new JLabel("<html><body><b>*Cola 1: Round Robbin, Quantum: </b></body></html> :");
        JLabel etiqueta15= new JLabel("<html><body><b> Penalización</b>: Para procesos que estén en la Cola 1 y ejecuten una unidad de tiempo (no de Quantum)"
                + " se les pregunta si su Prioridad es menor a la Prioridad del proceso anteriormente ejecutado(En la Cola 1); "
                + "En caso de ser cierto, estos descienden a la Cola 2.</body></html>");
        JLabel etiqueta16= new JLabel("<html><body><b> Recompensa</b>: Para procesos que estén en la Cola 2 se les pregunta antes de ejecutarse"
                + " si su tiempo de espera actual es mayor o igual a la suma de sus rafagas de CPU; En caso de ser cierto, estos ascienden "
                + "a la Cola 1.</body></html>");
        
        JLabel etiqueta17= new JLabel("<html><body>RESULTADOS</body></html>");
        JLabel etiqueta18= new JLabel("<html><body> 3) </body></html>");
        JLabel etiqueta19= new JLabel("Datos ingresados: ");
        JLabel etiquetaL= new JLabel("<html><body><b>INFORMACIÓN LIBRERÍA USADA PARA GRAFICAR:</b><br><br>"
                + "JFreeChart : a free chart library for the Java(tm) platform<br><br>"
                + "(C) Copyright 2000-2004, by Object Refinery Limited and Contributors.<br><br>"
                + "Project Info:  http://www.jfree.org/jfreechart/index.html</body></html>");
        //------------------------------------------------------------------------------------------------------------------------------------------------
        //ponemos la info. en el panel
        Panel1.add(etiqueta1); 
        Panel1.add(etiqueta2);
        Panel1.add(etiqueta3);
        Panel1.add(etiqueta4);
        Panel1.add(etiqueta5);
        Panel1.add(etiqueta6);
        Panel1.add(etiqueta7);
        Panel1.add(etiqueta8);
        Panel1.add(etiqueta9);
        Panel1.add(etiqueta10);
        Panel1.add(etiqueta11);
        Panel1.add(etiqueta12);
        Panel1.add(etiqueta13);
        Panel1.add(etiqueta14);
        Panel1.add(etiqueta15);
        Panel1.add(etiqueta16);
        Panel2.add(etiqueta17);
        Panel1.add(etiqueta18);
        Panel2.add(etiqueta19);
        Panel2.add(etiquetaL);
        
        //setBounds(posicion x en panel, posicion y en panel, espacio en x, espacio en y que se le reservara al texto).------------------------------------
        etiqueta1.setBounds(210,10,500,50);  
        etiqueta2.setBounds(50,90,800,60);
        etiqueta3.setBounds(50,160,240,100);
        etiqueta4.setBounds(320,130,600,80);
        etiqueta5.setBounds(380,215,600,80);
        etiqueta6.setBounds(545,215,600,80);
        etiqueta7.setBounds(360,255,600,80);
        etiqueta8.setBounds(500,255,600,80);
        etiqueta9.setBounds(670,255,600,80);
        etiqueta10.setBounds(320,175,600,80);
        etiqueta11.setBounds(50,375,800,60);
        etiqueta12.setBounds(500,490,400,50);
        etiqueta13.setBounds(515,138,600,80);
        etiqueta14.setBounds(400,440,400,20);
        etiqueta15.setBounds(50,540,600,50);
        etiqueta16.setBounds(50,585,600,60);
        etiqueta17.setBounds(350,10,500,50);
        etiqueta18.setBounds(50,490,50,50);
        etiqueta19.setBounds(50,308,120,15);
        etiquetaL.setBounds(50,320,500,500);
        
        etiqueta13.setVisible(false); //ocultamos etiqueta
        //tipografía, estilo y tamaño del texto.--------------------------------------------------------------------------------------------------------
        etiqueta1.setFont(new Font("Verdana",Font.BOLD, 20)); 
        etiqueta2.setFont(new Font("Verdana",Font.PLAIN,14));
        etiqueta3.setFont(new Font("Verdana",Font.PLAIN,16));
        etiqueta4.setFont(new Font("Verdana",Font.PLAIN,12));
        etiqueta10.setFont(new Font("Verdana",Font.PLAIN,12));
        etiqueta11.setFont(new Font("Verdana",Font.PLAIN,14));
        etiqueta12.setFont(new Font("Verdana",Font.PLAIN,12));
        etiqueta14.setFont(new Font("Verdana",Font.PLAIN,14));
        etiqueta15.setFont(new Font("Verdana",Font.PLAIN,12));
        etiqueta16.setFont(new Font("Verdana",Font.PLAIN,12));
        etiqueta17.setFont(new Font("Verdana",Font.BOLD, 20));
        etiqueta18.setFont(new Font("Verdana",Font.BOLD,16));
        etiquetaL.setFont(new Font("Verdana",Font.PLAIN, 12));
    }
     //BOTONES-------------------------------------------------------------------------------------------------------------------------------------------
    private void Botones(){
        //botones para registrar, aplicar o modificar lo ingresado por el usuario
        boton1 = new JButton("Generar Datos Aleatoriamente"); 
        boton2 = new JButton("Ingresar Proceso");             
        boton3 = new JButton("Deshacer Proceso");   
        boton4 = new JButton("Empezar Simulación");
        boton5 = new JButton("<html><body><b>Volver al Inicio</b></html></body>");
        boton6 = new JButton("<html><body><b>Mostrar Diagrama de Gantt</b></html></body>");
        //se añaden al panel
        Panel1.add(boton1);
        Panel1.add(boton2);
        Panel1.add(boton3);                                 
        Panel1.add(boton4);
        Panel2.add(boton5);
        Panel2.add(boton6);
        //se les adiciona eventos de presionado a los botones
        boton1.addActionListener(this);
        boton2.addActionListener(this);                      
        boton3.addActionListener(this);
        boton4.addActionListener(this);
        boton5.addActionListener(this); 
        boton6.addActionListener(this);
        //se ubican y cambian su tamaño
        boton1.setBounds(50,300,240,20);                     
        boton2.setBounds(400,330,150,20);
        boton3.setBounds(560,330,150,20);
        boton4.setBounds(690,570,150,40);
        boton5.setBounds(600,600,250,30);
        boton6.setBounds(600,560,250,30);
        
        boton4.setEnabled(false); //en un comienzo no habrán procesos ingresados, por eso se deshabilita
        boton3.setEnabled(false); //en un comienzo no habrán procesos ingresados, por eso se deshabilita
    }
    //TextFields----------------------------------------------------------------------------------------------------------------------------------------
    private void CamposTextuales(){
        numeroDeProcesos= new JTextField("1");  //numero de procesos
        instanteLlegada= new JTextField();      //instante llegada
        rafagaInicial= new JTextField();        //rafaga inicial
        rafagaES= new JTextField();             //rafaga e/s
        rafagaFinal= new JTextField();          //rafaga final
        prioridad= new JTextField();            //prioridad
        quantum= new JTextField("1");           //quantum
        //se añaden al panel
        Panel1.add(numeroDeProcesos);  
        Panel1.add(instanteLlegada);
        Panel1.add(rafagaInicial);                
        Panel1.add(rafagaES);
        Panel1.add(rafagaFinal);
        Panel1.add(prioridad);
        Panel1.add(quantum);
        //se ubican y cambian su tamaño
        numeroDeProcesos.setBounds(470,165,40,25);
        instanteLlegada.setBounds(495,248,40,25); 
        rafagaInicial.setBounds(675,248,40,25);  
        rafagaES.setBounds(450,283,40,25);
        rafagaFinal.setBounds(620,283,40,25);
        prioridad.setBounds(735,283,40,25);
        quantum.setBounds(690,440,40,30);
    }
    //COMBO_BOX--------------------------------------------------------------------------------------------------------------------------------------
    private void camposDeOpcion(){
        //opciones para que el usuario escoja el algoritmo de la cola 2.
        caja1= new JComboBox(lista1); 
        Panel1.add(caja1);
        caja1.setBounds(80,440,250,20);
        //opciones para que el usuario escoja si quiere los criterios para la simulacion
        caja2= new JComboBox(lista2); 
        Panel1.add(caja2);
        caja2.setBounds(80,510,270,20);
    }
    @Override //Lineas Pintadas----------------------------------------------------------------------------------------------------------------------
    public void paint(Graphics g) { 
        //se pintan lineas para dividir secciones de la ventana y se entienda mejor el paso a paso
        if(!panel){
            super.paint(g);
            g.drawLine(50,400,850,400);
            g.drawLine(312,170,312,380);
            g.drawLine(50,520,850,520);
        }else{
            super.paint(g);
        }
    }

    @Override //acciones de los botones-------------------------------------------------------------------------------------------------------------
    public void actionPerformed(ActionEvent ae) { 
        //GENERAR PROCESOS ALEATORIAMENTE___________________________________________________________________________________________________________
        if(ae.getSource()== boton1){            
            arregloProcesos= new Proceso[10];   //Se crea un arreglo que, por defecto, tendrá 10 procesos, siempre
            NumProcI=10;
            procesosAleat();                    //función que crea 10 procesos de forma aleatoria y los almacena en el arreglo de procesos.
            JOptionPane.showMessageDialog(null, "Proceda con los puntos 2 y 3 ","Realizado:",JOptionPane.INFORMATION_MESSAGE);
            count=10;
            boton4.setEnabled(true);
            boton2.setEnabled(false);
            boton3.setEnabled(true);
            numeroDeProcesos.setText("0");
        //_________________________________________________________________________________________________________________________________________  
        // INGRESAR PROCESOS_______________________________________________________________________________________________________________________  
        } else if (ae.getSource()== boton2){               
            //Si no hay procesos ingresados en el arreglo.
            if(count==0){                                       
                String NumProcS = numeroDeProcesos.getText();
                NumProcI = Integer.parseInt(NumProcS);          //se obtiene el numero de procesos(max 10).
                
                if(NumProcI>10){                                //si el numero de procesos excede el limite, se cambia ese valor por 10.
                    numeroDeProcesos.setText("10");
                    NumProcI=10;
                }
                
                arregloProcesos= new Proceso[NumProcI];          //se crea el arreglo con el numero de procesos determinados.
                
                numeroDeProcesos.setEditable(false);            //Se deshabilitan botones  y campos para evitar el mal uso de las entradas.
                boton1.setEnabled(false); 
                boton3.setEnabled(true);                         //Se habilita boton para deshacer entradas
                etiqueta13.setVisible(true);
              
            }
            //Si aun se permite el ingreso de procesos al arreglo.
            if(count!=NumProcI){                                    
                arregloProcesos[count]= new Proceso(count+1,        //agregamos un proceso al arreglo de procesos
                    Integer.parseInt(instanteLlegada.getText()),    //instante del proceso
                    Integer.parseInt(rafagaInicial.getText()),      //rafaga inicial proceso
                    Integer.parseInt(rafagaES.getText()),           //e/s proceso
                    Integer.parseInt(rafagaFinal.getText()),        //rafaga final proceso
                    Integer.parseInt(prioridad.getText()));         //prioridad proceso
               count++;
               numeroDeProcesos.setText(Integer.toString(NumProcI-count));//disminuye el campo de numProcesos.
               
               //Se vacian las cajas de texto para comididad al usuario
               instanteLlegada.setText("");
               rafagaInicial.setText("");
               rafagaES.setText("");
               rafagaFinal.setText("");
               prioridad.setText("");
            }
            //Sí ya se ingresaron todos los procesos al arreglo.
            if(count==NumProcI){    
                JOptionPane.showMessageDialog(null, "¡Se han ingresado todos los procesos! \n Proceda con los puntos 2 y 3 ",
                        "Realizado:",JOptionPane.INFORMATION_MESSAGE);
                boton2.setEnabled(false);
                boton4.setEnabled(true);
            }
         //________________________________________________________________________________________________________________________________________________
         //DESHACER PROCESO________________________________________________________________________________________________________________________________
        } else if (ae.getSource()== boton3){  
            count--;
            //Se vuelve a poner el proceso anterior para que el usuario rectifique o corrija.
            instanteLlegada.setText(Integer.toString(arregloProcesos[count].instanteProc));
            rafagaInicial.setText(Integer.toString(arregloProcesos[count].rafagaIProc));
            rafagaES.setText(Integer.toString(arregloProcesos[count].bloqueoProc));
            rafagaFinal.setText(Integer.toString(arregloProcesos[count].rafagaFProc));
            prioridad.setText(Integer.toString(arregloProcesos[count].prioridadProc));
            //aumenta el JTextField de numero de Procesos.
            numeroDeProcesos.setText(Integer.toString(NumProcI-count));   
            boton4.setEnabled(false);
            boton2.setEnabled(true);
            
            arregloProcesos[count]=null;
            
            //cuando no haya procesos en el arreglo            
            if(count==0){ 
                boton3.setEnabled(false);
                numeroDeProcesos.setEditable(true);
                boton1.setEnabled(true);
                etiqueta13.setVisible(false);
            }
         //________________________________________________________________________________________________________________________________________________
         //EMPEZAR SIMULACION ______________________________________________________________________________________________________________________________    
        } else if(ae.getSource()== boton4){    
            //LLamamos al Garbage Colector en un intento de liberar memoria
            System.gc();
            //creamos los resultados y planifidorProceso en su constructor llama las funciones necesariasp para iniciar la simulación.
            PlanificacionProcesos resultados = new PlanificacionProcesos(NumProcI, 
                                                                         arregloProcesos,
                                                                         caja1.getSelectedIndex(), 
                                                                         caja2.getSelectedIndex(),
                                                                         Integer.parseInt(quantum.getText()));
            //Extraemos los resultados
            arregloProcesos=resultados.arrayProcesos;
            historial=resultados.historial;
            //Los instanciamos en un JTable
            tablaProcesos tablaResultados= new tablaProcesos(arregloProcesos, NumProcI);
            //Los añadimos en un scroll    
            JScrollPane sc1 = new JScrollPane(tablaResultados.tabla);
            JScrollPane sc2 = new JScrollPane(tablaResultados.tablaProm);
            JScrollPane sc3 = new JScrollPane(tablaResultados.tablaDatos);
            //------------------------------------Actualizamos la ventana con los resultados---------------------------------------------------------
            Panel1.setVisible(false);
            panel=true;
            this.getContentPane().add(Panel2);             // añadimos el panel al objeto ventana.
            Panel2.setBackground(new Color(255, 223, 176));//establece un color para el panel.
            Panel2.setLayout(null);                        //desactivamos el diseño estandar de posicionamiento de la GUI
            //--AÑADIMOS ELEMENTOS---------------------------------------------------------------------------------------------------------------------
            etiqueta19= new JLabel("Cola 1: RR, Quantum: "+Integer.parseInt(quantum.getText()));
            
            switch (caja1.getSelectedIndex()) {
                case 0:
                    etiqueta20= new JLabel("Cola 2: FCFS");
                    break;
                case 1:
                    etiqueta20= new JLabel("Cola 2: SJF");
                    break;
                default:
                    etiqueta20= new JLabel("Cola 2: HRN");
                    break;
            }
            //---------------------------------------------------------------------------------------------------------------------------------
            if(caja1.getSelectedIndex()==0){
                etiqueta21= new JLabel("Criterios: Sí");
            }else{
                etiqueta21= new JLabel("Criterios: No");
            }
            //---------------------------------------------------------------------------------------------------------------------------------            
            Panel2.add(sc1);
            Panel2.add(sc2);
            Panel2.add(sc3);
            Panel2.add(etiqueta19);
            Panel2.add(etiqueta20);
            Panel2.add(etiqueta21);
      
            sc1.setBounds(50,80,600,170);
            sc2.setBounds(50,260,600,39);
            sc3.setBounds(50,330,600,150);
            etiqueta19.setBounds(670,80,200,50);
            etiqueta20.setBounds(670,110,200,50);
            etiqueta21.setBounds(670,140,200,50);
            //--------------------------------------------------------------------------------------------------------------------------------
        //______________________________________________________________________________________________________________________________________________
        //VOLVER AL MENU PRINCIPAL______________________________________________________________________________________________________________________
        }else if (ae.getSource()== boton5){
            this.dispose();
            System.gc();
            Ventana Principal= new Ventana();  //crea y muestra una ventana.
            Principal.setVisible(true);
        //______________________________________________________________________________________________________________________________________________
        //GENERAR DIAGRAMA DE GANT______________________________________________________________________________________________________________________ 
        }else{
            System.gc();
            JOptionPane.showMessageDialog(null, "¡Para Cerrar el gráfico presione la tecla ENTER! ",
                        "AVISO:",JOptionPane.INFORMATION_MESSAGE);
            final org.jfree.chart.demo.diagramaProcesos demo = 
                    new org.jfree.chart.demo.diagramaProcesos("Diagrama de gantt",arregloProcesos, historial, NumProcI);
            demo.pack();
            RefineryUtilities.centerFrameOnScreen(demo);
            demo.setVisible(true);
        }   
    }
    //--------------------------------------------------------------------------------------------------------------------------------------------------
    //procesosAleat genera para el array de arreglos 10 procesos con datos aleatorios
    public void procesosAleat(){
        for(int i=0;i<NumProcI;i++){
            Random rnd = new Random();
            arregloProcesos[i]= new Proceso(i+1,
                                           (int)(rnd.nextDouble() * 11),      //instante aleatorio en el intervalo[0,20]
                                           (int)(rnd.nextDouble() * 5 + 1),  //rafaga CPU inicial aleatoria en el intervalo[1,30]
                                           (int)(rnd.nextDouble() * 5 + 1),  //rafaga entrada/salida aleatoria en el intervalo[1,30]
                                           (int)(rnd.nextDouble() * 5 + 1),  //rafaga CPU aleatoria final en el intervalo[1,30]
                                           (int)(rnd.nextDouble() * 11));     //Prioridad en el intervalo[0,10]
        }
    }

}