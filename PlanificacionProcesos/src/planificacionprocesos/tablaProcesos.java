package planificacionprocesos;
//-----------------LIBRERIAS--------------------------------//
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
//-----------------------------------------------------------//

public class tablaProcesos {
    public JTable tabla, tablaProm, tablaDatos;
    
    tablaProcesos(Proceso arrayProcesos[], int NumProc){
        //Creamos los moldes de las dos tablas:
        DefaultTableModel modeloDatos = new DefaultTableModel();
        DefaultTableModel modelo = new DefaultTableModel();
        DefaultTableModel modeloPromedio = new DefaultTableModel();
        //añadimos columnas a modelo
        modelo.addColumn("Proceso");
        modelo.addColumn("Tiempo Servicio(T)");
        modelo.addColumn("Tiempo Espera(E)");
        modelo.addColumn("Indicie Servicio(I)");
        modelo.addColumn("Tiempo Final(tf)");
        //añadimos columnas a modelo datos
        modeloDatos.addColumn("Proceso");
        modeloDatos.addColumn("Inst. llegada");
        modeloDatos.addColumn("Rafaga CPU ini.");
        modeloDatos.addColumn("Rafaga de E/S");
        modeloDatos.addColumn("Rafaga CPU fnl.");
        modeloDatos.addColumn("prioridad");
        //añadimos columnas a modelo promedio
        modeloPromedio.addColumn("");
        modeloPromedio.addColumn("Tiempo Servicio(T)");
        modeloPromedio.addColumn("Tiempo Espera(E)");
        modeloPromedio.addColumn("Indicie Servicio(I)");
        //inicializamos los datos del promedio
        int Tpromedio=0;
        int Epromedio=0;
        double Ipromedio=0;
        //ingresamos las tuplas a los modelo y vamos obteniendo el promedio.
        for(int i=0;i<NumProc;i++){
            
            String[] fila= {"P"+Integer.toString(arrayProcesos[i].idProc),
                            Integer.toString(arrayProcesos[i].T()),
                            Integer.toString(arrayProcesos[i].E()),
                            Double.toString(arrayProcesos[i].I()),
                            Integer.toString(arrayProcesos[i].tf)};
            modelo.addRow(fila);
            
            String[] fila1={"P"+Integer.toString(arrayProcesos[i].idProc),
                            Integer.toString(arrayProcesos[i].instanteProc),
                            Integer.toString(arrayProcesos[i].rafagaIProc),
                            Integer.toString(arrayProcesos[i].bloqueoProc),
                            Integer.toString(arrayProcesos[i].rafagaFProc),
                            Integer.toString(arrayProcesos[i].prioridadProc)};
            modeloDatos.addRow(fila1);
            
            Tpromedio+=arrayProcesos[i].T;
            Epromedio+=arrayProcesos[i].E();
            Ipromedio+=arrayProcesos[i].I();
        }
        //calculamos y obtenemos el promedio.
        Tpromedio= Tpromedio/NumProc;
        Epromedio= Epromedio/NumProc;
        Ipromedio= Ipromedio/NumProc;
        //creamos la tupla promedio y la añadimos A modeloPromedio
        String[] fila = {"PROMEDIO:",
                         Integer.toString(Tpromedio),
                         Integer.toString(Epromedio),
                         Double.toString(Ipromedio)};
        
        modeloPromedio.addRow(fila);
        //creamos las tablas a partir de su modelo
        tabla = new JTable(modelo);
        tablaDatos= new JTable(modeloDatos);
        tablaProm = new JTable(modeloPromedio);
    }
}