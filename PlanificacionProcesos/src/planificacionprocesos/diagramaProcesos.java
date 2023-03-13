package org.jfree.chart.demo;
//-----------------LIBRERIAS--------------------------------//

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import javax.swing.JButton;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.ApplicationFrame;
import planificacionprocesos.Proceso;
//---------------------------------------------------------//

public final class diagramaProcesos extends ApplicationFrame implements KeyListener {
    int historial[];
    Proceso arrayProc[];
    int numProc;
    int tiempoTotal;

    public diagramaProcesos(final String titulo, Proceso[] arrayProc ,int[] historial, int numProc) {

        super(titulo);
        this.historial=historial;
        this.arrayProc=arrayProc;
        this.numProc= numProc;

        //Conjunto de datos para el grafico, la funcion que los almacena esta mas abajo.
        final IntervalCategoryDataset dataset = createDataset();

        // crea un grafico de tipo GantChart
        final JFreeChart chart = ChartFactory.createGanttChart(
            "Planificador de procesos Con multiples colas",  // chart title
            "Procesos",              // domain axis label
            "Tiempo (ns)",              // range axis label
            dataset,             // data
            true,                // include legend
            true,                // tooltips
            false                // urls
        );
        
        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
        
        //Para cada barra se tendrá un ancho maximo del 35% de la ventana del grafico
        BarRenderer br = (BarRenderer) plot.getRenderer();
        br.setMaximumBarWidth(.35);
        
        //para el caso en el que hayan muchos procesos disminuimos el margen de la barra, para que el tamaño de estas
        //pueda aumentar y abarcar mas pantalla (Si disminuimos lo que las restringe obviamente estas barras tendrían que crecer...
        //no es muy lógico en la vida real, pero aqui tiene sentido). 
        if(numProc>=5){
            br.setItemMargin(-3);
        }else if(numProc>=8){
            br.setItemMargin(-7);
        }

        // se añade el grafico al panel
        final ChartPanel chartPanel = new ChartPanel(chart);
        // cambia el tipo de dato al representar los segundos
        DateAxis axis = (DateAxis) plot.getRangeAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("S"));
        
        //crea la ventana y ubica el panel en el que est el grafico
        chartPanel.setPreferredSize(new java.awt.Dimension(1000,600));
        
        JButton salir= new JButton("Cerrar Ventana");
        salir.setBounds(0,0,100,50);
        setContentPane(salir);
        setContentPane(chartPanel);
        addKeyListener(this);
    }
    
    //crea el conjunto de datos y los guarda en una coleccion que vendría siendo el conjunto de datos necesarios para hacer el grafico
    //La libreria se encarga de interpretar los datos y plasmarlos en una grafica.
    //RESUMEN: Tecnicamente todo lo que hace esta funcion es empaquetar la información obtenida en la simulacion.
    private IntervalCategoryDataset createDataset() {
        
        //creo una cantidad de serie de tareas (que vendría siendo los procesos)
        final TaskSeries[] s = new TaskSeries[numProc];
        //creo una cantidad de tareas que iran en la serie de tareas.
        final Task[] t = new Task[numProc];               
        
        for(int i=0;i<numProc;i++){
            s[i] = new TaskSeries("Proceso "+(i+1));   //asigno un nombre para identificar los procesos
            t[i] = new Task("Proceso "+(i+1), 
                    new SimpleTimePeriod(arrayProc[i].getInstanteI(),arrayProc[i].gettf())); //les asigno el intervalo en el que
        }                                                                                    //estuvieron en ejecucion
        
        //Hallamos el tiempo maximo para con este tiempo buscar en todo la planificacion las subtareas con su respectivo proceso
        int tiempoMax=0; 
        for(int i=0;i<numProc;i++){
            if(arrayProc[i].gettf()>tiempoMax){
                tiempoMax=arrayProc[i].gettf();
            }
        }
        
        //además con el tiempo maximo, creamos una n cantidad de subtareas... habrán tareas que no se INSTANCIARAN
        final Task[] st = new Task[tiempoMax+1];

        //buscamos y asignamos subtareas a las tareas correspondientes:
        for(int i=1;i<tiempoMax+1;i++){
            if(historial[i]!=0){
                st[i]=new Task("p"+(historial[i]-1),new SimpleTimePeriod(i-1,i));
                t[historial[i]-1].addSubtask(st[i]);
            }
        }
        //coleccion contendría todas las series de tareas que se obtuvieron de la simulacion
        final TaskSeriesCollection collection = new TaskSeriesCollection();
        
        for(int i=0;i<numProc;i++){
            s[i].add(t[i]);
            collection.add(s[i]);
        }

        return collection;
    }

    @Override
    public void keyTyped(KeyEvent ke) {}

    @Override
    public void keyPressed(KeyEvent ke) {
        if(ke.getKeyCode()== KeyEvent.VK_ENTER){
            dispose();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {}
}