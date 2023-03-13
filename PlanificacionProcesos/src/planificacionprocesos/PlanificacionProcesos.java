package planificacionprocesos;
//------------LIBRERIAS------------//
import java.util.LinkedList;
import java.util.Queue;
//--------------------------------//
public final class PlanificacionProcesos {
    //_____________________________________________________________________________________________________________________________________________________
    int numProcT;              //indica el numero de procesos entrantes 
    int enEjecucion=0;         //indica que hay un proceso en la cola2 y está en ejecucion(1 indica si es rafaga final y 2 si es rafaga inicial)
    Proceso arrayProcesos[];   //arreglo donde se almacenan la cantidad de procesos entrantes
    int algoritmC2;            //valor que determina que algoritmo se usara en la cola 2
    int tiempoEjec;            //tiempo total en ns que tomara el planificador como limite para la simulacion
    int t=0;                   //tiempo indexado desde 0 que servira como indicador de tiempo para la simulacion 
    Queue<Proceso> cola1,cola2;
    int idProcCola1=0;         //identificadores que nos permitira saber en que instante que  proceso ejecuto su rafaga dentro de su respectiva cola.
    int idProcCola2=0;         //ademas para saber cual proceso se ejecuto con anterioridar, es decir algo que nos permita ver lo hecho anteriormente
    int historial[];           //IMPORTANTE: historial es un arreglo que tendría el proceso que se ejecuto en un instante i
                               //Ej: historial[4]= 2 ... quiere decir que en el instante 4 se ejecuto el proceso 2.
    int quantum;
    int quantumActual; 
    int criterio;
    //_____________________________________________________________________________________________________________________________________________________
    //CONSTRUCTOR
    PlanificacionProcesos(int numProc, Proceso array[], int algoritmoC2, int criterio, int quantum){ 
        numProcT= numProc;
        arrayProcesos= array;
        algoritmC2= algoritmoC2;
        cola1 = new LinkedList(); //cola de alta prioridad
        cola2 = new LinkedList(); //cola de baja prioridad
        tiempoEjec= tiempoTotal();
        this.criterio=criterio;
        this.quantum=quantum;
        quantumActual=0;
        historial= new int[tiempoEjec+1];
        planificador(criterio);
    }

    //-------- planificador: ---------------------------------------------------------------------------------------------------------------------------
    // Para nuestro planificador:
    // 1) en primer lugar, decidimos que cualquier rafaga de CPU inicial se pasara a la cola 1 con algoritmo RR, y recordando  el enunciado esta cola 
    // será la de mayor prioridad, para las rafagas de CPU finales estas descenderan  la cola 2, de menor prioridad.
    // 2)En segundo lugar, tendremos 1 criterio de recompensa y 1 criterio de penalización, la penalización aplica para los procesos que estén
    // en cola 1, la recompensa  aplica para los procesos de la cola 2.
    // 3) en último lugar, la función planificador simulara cada instante en t= 1 cuanto, incluso para los elementos de la cola no apropiativa,
    // PERO ¡igual se respetará la  politica de la cola no apropiativa! es decir, no debería afectar el algoritmo ni su politica.
    //--------------------------------------------------------------------------------------------------------------------------------------------------
    public void planificador(int criterio){
        while(t<tiempoEjec){
            //Verificamos que rafagas de cpu están entrando en el instante t;
            rafagaIEntrante();                             
            rafagaFEntrante();
            //aumentamos una unidad de tiempo
            t+=1;
            //se aumenta el tiempo de espera de los procesos de la cola 2 
            aumentarTiempoEsperaCola2();  
            //Se disminuye para cada proceso bloqueado una unidad de de su bloqueo actual 
            disminuirBloqueo();
            //si la cola 1 no esta vacia aplicamos round robbin
            if(!cola1.isEmpty() && enEjecucion==0){                            
                RoundRobbin();
                historial[t]=idProcCola1;                    
            }else if(!cola2.isEmpty()){ 
                if (recompensa()){                  //preguntamos si el proceso merece ser recompensado 
                    cola1.add(cola2.poll());        //la recompensa es ascender a la cola 1
                    RoundRobbin();
                    historial[t]=idProcCola1;
                }else{ 
                    //aplicamos algoritmo de la cola 2 segun el caso
                    switch (algoritmC2) {                    
                        case 0:
                            FirstComeFirstServed();
                            break;
                        case 1:
                            ShortestJobFirst();
                            break;
                        default:
                            HighResponseNextTime();
                            break;
                    }
                    historial[t]=idProcCola2;
                }
            }else{
                //en el instante t no se ejecuto ningun proceso y se representará como un 0.
                historial[t]=0;           
            }
            //desbloqueamos procesos que hayan acabado su e/s.
            desbloquearProcesos();                                                                     
        }
    }
    
    //FUNCIONES AUXILIARES_______________________________________________________________________________________________________________________________
    public void aumentarTiempoEsperaCola2(){
        for(int i=0;i<cola2.size();i++){
            cola2.peek().tEspera+=1;
            cola2.add(cola2.poll());
        }    
    }
    //--------------------------------------------------------------------------------------------------------------------------------------------------
    //determina el tiempo total SEGURO a traves de la suma de todas las rafagas y añadiendole el instante de llegada del ultimo proceso en entrar
    public int tiempoTotal(){ 
        int total=0;          
        for(int i=0;i<numProcT;i++){
            total+=arrayProcesos[i].rafagaIProc;
            total+=arrayProcesos[i].rafagaFProc;
        }
        int ultimoInstante=0;
        for(int i=0;i<numProcT;i++){
            if(arrayProcesos[i].instanteProc>ultimoInstante){
                ultimoInstante=arrayProcesos[i].instanteProc;
            }
        }
        total+=ultimoInstante;
        return total;
    }
    //--------------------------------------------------------------------------------------------------------------------------------------------------
    //pregunta que procesos estan en ese instante en e/s y les resta una unidad de espera
    public void disminuirBloqueo(){ 
       for(int i=0;i<numProcT;i++){
           if(arrayProcesos[i].estado==false){
               arrayProcesos[i].bloqueoActual--;
           }
       }
    }
    //ifentifica si el proceso acaba de entrar  e/s y cambia su estado de true a false
    public void bloquearProceso(int posProcArray){ 
        if(arrayProcesos[posProcArray].rafagaIActual==0){
            arrayProcesos[posProcArray].estado=false;
        }
    }
    //pregunta para cada proceso si su bloqueoActual es 0, y si lo es quiere decir que ya no esta desbloqueado por lo tanto pasa a estar desbloqueado.
    public void desbloquearProcesos(){ 
        for(int i=0;i<numProcT;i++){                
           if(arrayProcesos[i].bloqueoActual==0){
               arrayProcesos[i].estado=true;
           }
       }                                
    } 
    //---------------------------------------------------------------------------------------------------------------------------------------------------
    public void rafagaIEntrante(){
        for(int i=0;i<numProcT;i++){
            if(cola1.isEmpty() && arrayProcesos[i].instanteProc==t){  //Preguntamos si hay un proceso que haya llegado en el instante t y cola1 esté vacia.
                cola1.add(arrayProcesos[i]);                          //lo ponemos en la cola
            }else if (arrayProcesos[i].instanteProc==t){
                priorizar(i);
            }
        }
    }
    
    //---------------------------------------------------------------------------------------------------------------------------------------------------
    //Funcion aux. de rafagaIEntrante que determina la prioridad que tiene la cpu al  atender un proceso nuevo en vez de uno que cumplio una
    // unidad de rafaga, anteponiendo los nuevos, siempre de penultimos y dejar el ultimo proceso ejecutado de último. 
    public void priorizar(int i){                   
        int count=0;                                 
        while(count<cola1.size()){                
            if (cola1.peek().idProc == idProcCola1 && quantumActual==quantum){ //la condicion del quantum va con el fin de que se
                cola1.add(arrayProcesos[i]);                                   //respeten los cuantos a cada proceso y no priorizar en ese caso.
                cola1.add(cola1.poll());
                
                count=cola1.size()+1;
            }else{
                cola1.add(cola1.poll());
                count++;
            }
        }
        if(count==cola1.size()){
            cola1.add(arrayProcesos[i]);
        }
    }
     //--------------------------------------------------------------------------------------------------------------------------------------------------
    public void rafagaFEntrante(){
        for(int i=0;i<numProcT;i++){
            if(arrayProcesos[i].bloqueoActual==0 && arrayProcesos[i].instanciado==false){   //Preguntamos si hay un proceso que haya hecho ya su e/s
                arrayProcesos[i].instanciado= true;
                cola2.add(arrayProcesos[i]);                                                //lo ponemos en la cola 2, todos entran a la cola 1
            }
        }
    }
    //__________________________________________________________________________________________________________________________________________________
    //ALGORITMOS._______________________________________________________________________________________________________________________________________
    //Round robbin: revisa el primer elemento de la cola, le procesa 1 unidad del primer elemento y se determina si, permanece o se retira el proceso
    //de la cola 1
    public void RoundRobbin(){
        int idProcesoAnterior= idProcCola1;      
        if(cola1.peek().bloqueoActual==0){      //el bloqueo igual a 0 indica implicitamente que ya paso su e/s y esta ejecutandose la rafaga de cpu final
            cola1.peek().rafagaFActual--;       //disminuimos en un instante la rafaga actual
        }else{
            cola1.peek().rafagaIActual--;       //en cualquier caso, disminuir la rafaga inicial.
        }
        
        idProcCola1= cola1.peek().idProc;
        
        //Condicion de finalización de proceso
        if((cola1.peek().rafagaIActual==0 && cola1.peek().bloqueoActual!=0)|| cola1.peek().rafagaFActual==0){ 
                                                                           
            if(cola1.peek().rafagaFActual==0){                              //si ya no hay mas rafagas de cpu.
                cola1.peek().tf=t;                                          //obtenemos el tf del proceso
            }else{
                bloquearProceso(cola1.peek().idProc-1);                 //funcion que pregunta si se acabo su rafaga y proceder a bloquearlo
            }
            cola1.poll();
            quantumActual++;
        //condicion de penalizacion:    
        }else if (penalizacion(idProcesoAnterior)){          //preguntamos si el proceso merece ser penalizado
            cola2.add(cola1.poll());                       // el castigo en este caso es descender a la cola 2
            quantumActual++;
        }else{
            quantumActual++;
        }

        if(cola1.isEmpty()){
            quantumActual=0;
        }else if(quantumActual==quantum){ //Permanencia en la cola
            if(cola1.peek().idProc==idProcCola1){ //Esta condicion verifica que el proceso no ha acabado aun, lo desencola y lo vuelve a encolar
                cola1.add(cola1.poll());
            }
            quantumActual=0;                     
        }
                                                       
    }
    //FCFS: A partir del fundamento de que el primero en llegar es por logica el primero en la cola, se procesa el primero parcialmente y luego se saca
    public void FirstComeFirstServed(){
        if(cola2.peek().bloqueoActual==0){      //el bloqueo igual a 0 del proceso indica implicitamente que ya paso su e/s y esta ejecutandose la rafaga final
            cola2.peek().rafagaFActual--;       //disminuimos un instante la rafaga actual
        }else{
            cola2.peek().rafagaIActual--;                  //en cualquier otro caso, disminuir la rafaga inicial.
        }
        
        enEjecucion=1;                                     //el proceso que entro a fcfs se está ejecutando
        
        idProcCola2= cola2.peek().idProc;
        
        if((cola2.peek().rafagaIActual==0 && cola2.peek().bloqueoActual!=0) || cola2.peek().rafagaFActual==0){ 
                                                                            //si se acaba el proceso modificado, se saca de la cola
            if(cola2.peek().rafagaFActual==0){                              //si ya no hay mas rafagas de cpu.
                cola2.peek().tf=t;                                          //obtenemos el tf del proceso
            }else{
                bloquearProceso(cola2.peek().idProc-1);               //funcion que pregunta si se acabo su rafaga y proceder a bloquearlo
            }
            cola2.poll();
            enEjecucion=0;                              //ya no se está ejecutando el proceso
        }      
        //Si analizamos el código nos daremos cuenta que es una copia del algoritmo RR planteado anteriormente 
        //pero a diferencia de round robbin este algoritmo, tiene solo una opcion,se saca de la cola 1 una vez terminado su rafagaI o rafagaF,
        //por lo que garantizmos que ejecutara todo su proceso y se respetará la política no aprop.
  
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    //SJF: revisa entre los elementos de su cola, quien tiene la rafaga de CPU de menor tiempo a evaluar, y la "ejecuta" 
    public void ShortestJobFirst(){
        
        if(enEjecucion==0){ //cuando casoEjec es 0, indica que no hay un proceso en la cola 2 ejecutandose
            int minimo=t;
            int idMinimo=0;
       
            //Dentro del for se valida y se busca que proceso es el mas corto para ejecutarlo
            for(int i =0;i<cola2.size();i++){
                if(cola2.peek().instanciado){                                        //Preguntamos si ya ejecuto su rafaga de e/s
                    if(cola2.peek().rafagaFActual == cola2.peek().rafagaFProc){      //Preguntamos si no se ha ejecutado parte de su rafaga Final
                        if(cola2.peek().rafagaFProc<minimo){
                            minimo=cola2.peek().rafagaFProc;
                            idMinimo=cola2.peek().idProc;
                            enEjecucion=1;
                        }
                    }else{
                        if(cola2.peek().rafagaFActual<minimo){
                            minimo=cola2.peek().rafagaFActual;
                            idMinimo=cola2.peek().idProc;
                            enEjecucion=1;
                        }
                    }
                }else{
                    if(cola2.peek().rafagaIActual == cola2.peek().rafagaIProc){      //Preguntamos si no se ha ejecutado parte de su rafaga inicial
                        if(cola2.peek().rafagaIProc<minimo){
                            minimo=cola2.peek().rafagaIProc;
                            idMinimo=cola2.peek().idProc;
                            enEjecucion=2;
                        }
                    }else{
                        if(cola2.peek().rafagaIActual<minimo){
                            minimo=cola2.peek().rafagaIActual;
                            idMinimo=cola2.peek().idProc;
                            enEjecucion=2;
                        }
                    }
                }
                cola2.add(cola2.poll());
            }//aqui acaba el for que determina el proceso minimo
            
            //Proceso adicional para poner en cola el proceso a ejecutar.
            for(int i =0;i<cola2.size();i++){
                if(idMinimo!=cola2.peek().idProc){
                    cola2.add(cola2.poll());
                }else{
                    i=cola2.size();
                }
            }
        }

        //Le aplicamos una unidad...
        switch(enEjecucion){ 
            case 1: cola2.peek().rafagaFActual-=1;
                    break;
            case 2: cola2.peek().rafagaIActual-=1;
                    break;
        }
        //guardamos el ultimo elemento ejecutado de la cola 2
        idProcCola2=  cola2.peek().idProc;    
        
        //si se acaba el proceso modificado, se saca de la cola
        if((cola2.peek().rafagaIActual==0 && cola2.peek().bloqueoActual!=0) || cola2.peek().rafagaFActual==0){ 
                                                                            
            if(cola2.peek().rafagaFActual==0){                              //si ya no hay mas rafagas de cpu.
                cola2.peek().tf=t;                                          //obtenemos el tf del proceso
            }else{
                bloquearProceso(cola2.peek().idProc-1);               //funcion que pregunta si se acabo su rafaga y proceder a bloquearlo
            }
            cola2.poll();
            enEjecucion=0; 
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    //HRN: aplica la operacion de tasa de retorno P y determina que proceso ejecutar a partir de la operacion.
    public void HighResponseNextTime(){
        int idProc=idProcCola2;
        //Aplicamos la tasa de retorno con los datos de la cola Prioridad= (tEspera + t)/t
        if(enEjecucion==0){
            double hrnMax=1;
            int tiempoEsperaMax=0;
            for(int i=0;i<cola2.size();i++){
                if(cola2.peek().instanciado){                                        //Preguntamos si ya ejecuto su rafaga de e/s
                    if(cola2.peek().rafagaFActual == cola2.peek().rafagaFProc){      //Preguntamos si no se ha ejecutado parte de su rafaga Final
                        cola2.peek().setHRN(2);
                    }else{
                        cola2.peek().setHRN(2);
                    }
                    enEjecucion=1;
                }else{
                    if(cola2.peek().rafagaIActual == cola2.peek().rafagaIProc){      //Preguntamos si no se ha ejecutado parte de su rafaga inicial
                        cola2.peek().setHRN(1);
                    }else{
                        cola2.peek().setHRN(1);
                    }
                    enEjecucion=2;
                }
                cola2.add(cola2.poll());
            }
            //buscamos el proceso con indice de retorno mas alto, si hay 2 o mas procesos con mas indices de retorno agregamos al que lleve esperando mas
            for(int i=0;i<cola2.size();i++){
                if(cola2.peek().prioridadHRN>hrnMax){
                    hrnMax=cola2.peek().prioridadHRN;
                    tiempoEsperaMax=cola2.peek().tEspera;
                    idProc=cola2.peek().idProc;
                    cola2.add(cola2.poll());
                }else if(cola2.peek().prioridadHRN==hrnMax){
                    if(cola2.peek().tEspera>tiempoEsperaMax){
                        hrnMax=cola2.peek().prioridadHRN;
                        tiempoEsperaMax=cola2.peek().tEspera;
                        idProc=cola2.peek().idProc;
                        cola2.add(cola2.poll());
                    }
                }else{
                    cola2.add(cola2.poll());
                }
            }
            //Proceso adicional para poner en cola el proceso ejecutado.
            for(int i =0;i<cola2.size();i++){
                if(idProc!=cola2.peek().idProc){
                    cola2.add(cola2.poll());
                }else{
                    i=cola2.size();
                }
            }
                        
        } //aqui acaba el caso de ejecución 0,, que indica que apenas se está evaluando el proceso...
          //Le aplicamos una unidad...

        switch(enEjecucion){
            case 1: arrayProcesos[idProc-1].rafagaFActual-=1;
                    break;
            case 2: arrayProcesos[idProc-1].rafagaIActual-=1;
                    break;
        }
        idProcCola2= idProc;    //guardamos el ultimo elemento ejecutado de la cola 2

        //si se acaba el proceso modificado, se saca de la cola
        if((cola2.peek().rafagaIActual==0 && cola2.peek().bloqueoActual!=0) || cola2.peek().rafagaFActual==0){ 
                                                                            
            if(cola2.peek().rafagaFActual==0){                              //si ya no hay mas rafagas de cpu.
                cola2.peek().tf=t;                                          //obtenemos el tf del proceso
            }else{
                bloquearProceso(cola2.peek().idProc-1);               //funcion que pregunta si se acabo su rafaga y proceder a bloquearlo
            }
            //Reseteamos sus valores debido a que este ya acabo su rafaga
            cola2.peek().tEspera=0;
            cola2.peek().prioridadHRN=1.0;
            enEjecucion=0;

            cola2.poll();
        }
        
    }
    
    // CRITERIOS -------------------------------------------------------------------------------------------------------------------------------------
    public boolean recompensa(){
        if(criterio==0){
            if(enEjecucion!=0){
                return false;
            //Condicion para resetear el tiempo de espera en cola 2 en caso de cumplir la recompesa y se esté usando HRN.
            }else if(algoritmC2==2 && cola2.peek().tEspera>=cola2.peek().tprocesos){
                cola2.peek().tEspera=0;
                return true;
            }
            //se pregunta: si el tiempo de espera del proceso en cola 2 es mayor o igual a la suma de sus rafagas de CPU.
            else return cola2.peek().tEspera>=cola2.peek().tprocesos;
        }else{
            return false;
        }
    }
    
    public boolean penalizacion(int IdProcesoAnterior){
        if(criterio==0){
            if(IdProcesoAnterior==0){ //si no hubo proceso antes de el, no se evalua obviamente
                return false;         //Se coloca esta condicion ya que al ejecutarse, el programa lanzaria error.
            }
            //se pregunta:  Si el proceso en cola1 tiene menor prioridad que el que se ejecutó antes de el
            else return cola1.peek().prioridadProc < arrayProcesos[IdProcesoAnterior-1].prioridadProc;
        }else{
            return false;
        }
    }
    
    //MAIN DEL SIMULADOR--------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args) {
       Ventana Principal= new Ventana();  //crea y muestra una ventana.
       Principal.setVisible(true);
    }
    //--------------------------------------------------------------------------------------------------------------------------------------
}
