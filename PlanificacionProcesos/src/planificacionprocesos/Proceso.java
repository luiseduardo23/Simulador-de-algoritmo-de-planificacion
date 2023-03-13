package planificacionprocesos;

public class Proceso {
    //-----------------------datos de entrada y datos para simulacion------------------------//
    int idProc;
    boolean estado;
    int tEspera;          //tiempo que ha estado listo, lo usaremos para el algort. HRN y para la recompensa.
    double prioridadHRN;  //por lo que, este valor cambiara mientras el proceso este en cola 2.                       
                        
    int instanteProc;
    int rafagaIProc;
    int bloqueoProc;
    int rafagaFProc;
    boolean instanciado;     //nuestro programa necesita saber si una rafaga final ya se encuentra en cola, por lo que necesitaremos una variable adicional
    int prioridadProc;
    
    int rafagaIActual;
    int bloqueoActual;
    int rafagaFActual;
    //---------------------------- informacion para la tabulacion-----------------------------//
    int tf;
    int tprocesos;
    int tprocesosYbloqueos;
    int T=0;
    //----------------------------------------------------------------------------------------//
    
    //CONSTRUCTOR
    Proceso(int id, int instante, int rafagaI,  int bloqueo, int rafagaF, int prioridad){ 
        idProc = id;
        estado= true;
        tEspera=0;
        
        instanteProc = instante;
        rafagaIProc = rafagaI;
        bloqueoProc = bloqueo;
        rafagaFProc = rafagaF;
        prioridadProc = prioridad;
        prioridadHRN=1;
        
        rafagaIActual=rafagaI;
        bloqueoActual=bloqueo;
        instanciado =false;
        rafagaFActual=rafagaF;
        
        tprocesos= rafagaI+rafagaF;
        tprocesosYbloqueos= tprocesos+bloqueo;
    }
    
    //se obtiene de cada proceso para la tabla:---//
    public int T(){T=tf - instanteProc;return T;}     //T: tiempo de servicios.
    
    public int E(){return T-tprocesosYbloqueos;}      //E: tiempo de espera.
    
    public double I(){ return tprocesos/(T*1.0);}     //I: indice de servicio.
    
    public int getInstanteI(){return instanteProc; }  //instante de llegada
    
    public int gettf(){return tf;}                    //tiempo final de ejec.
    
    //Funcion de prioridad para algoritmo HRN______________________________________________
    public void setHRN(int caso){
        switch(caso){
            case 1: prioridadHRN=((rafagaIActual*1.0)+(tEspera*1.0))/(rafagaIActual*1.0);
                    break;
            case 2: prioridadHRN= ((rafagaFActual*1.0)+(tEspera*1.0))/(rafagaFActual*1.0);
                    break;
        }
    }
    //____________________________________________________________________________________

}