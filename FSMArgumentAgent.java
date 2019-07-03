package testjade;

import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import static testjade.Constants.*;

/**
 *
 * @author Julien SAUSSIER, Abdelkader ZEROUALI
 */
public class FSMArgumentAgent extends DefaultAgent{

    //Message courant
    private  ACLMessage message ;
    private float price;  
    
    @Override
    protected void setup(){
        super.setup();   
        setPrice(getArguments());
        this.addBehaviour(new FSMPerformativeBehaviour(this));
       
    }
    
    private ACLMessage getMessage(){
       return message;
    }
    private void setMessage(ACLMessage message){
       this.message=message;
    }
    private float getPrice(){
       return price;
    }
    private void setPrice(float price){
       this.price = price;
    }
    private void setPrice(Object[] args){
       if(args!= null && args.length>0){
           setPrice(Float.valueOf(String.valueOf(args[0])));
       }
       else {
           System.out.println("aucun prix spécifié" + getAID().getName());
       }
    }
    
    private class FSMPerformativeBehaviour extends FSMBehaviour{

        public FSMPerformativeBehaviour(Agent a){
            super(a);
        }   

        @Override
        public void onStart() {
            super.onStart(); 
            
            //Premier état - Attente message
            this.registerFirstState(new HandleMessageBehaviour(this.getAgent()), "ATTENTE");
            
            //Etat propose
            this.registerState(new SendMessageBehaviour(this.getAgent()), "PROPOSITION");
            
            //Etat message gagnant
            this.registerLastState(new WinnerBehaviour(this.getAgent()), "GAGNANT");
            
            //Etat message perdant
            this.registerLastState(new LoserBehaviour(this.getAgent()), "PERDANT");
            
            //Transitions
            this.registerTransition("ATTENTE", "PROPOSITION", ACLMessage.CFP);
            this.registerTransition("ATTENTE", "GAGNANT", ACLMessage.ACCEPT_PROPOSAL);
            this.registerTransition("ATTENTE", "PERDANT", ACLMessage.REJECT_PROPOSAL);
            
            //Transition par défaut
            this.registerDefaultTransition("PROPOSITION", "ATTENTE", new String[]{"ATTENTE", "PROPOSITION"});    
            addBehaviour(this);
        }
        
        
        //Traite les messages et retourne à la fin de son activité le performatif reçu
        private class HandleMessageBehaviour extends SimpleBehaviour {
            
            MessageTemplate tpl;
            boolean isFinished;
            
            HandleMessageBehaviour(Agent a) {
                super(a);
            }
            
            @Override
            public void onStart() {
                
                System.out.println("onStart : début " + toString());
                   
                tpl = MessageTemplate.or(CFP_TPL, ACCEPT_TPL);
                
                tpl = MessageTemplate.or(tpl, REJECT_TPL);
            }

            @Override
            public void action() {
                ACLMessage msg;
                
                //A new ACL message, blocking the agent until one is available.
                msg = blockingReceive(tpl);
                
                if(msg != null){
                    doReiceve(msg);
                } else {
                    block();               
                }
                
                System.out.print("Gestion du message :" + getMessage().getContent() + " Destinataire : " + myAgent.getAID().getName());
            }
            
            protected void doReiceve(ACLMessage msg){
                setMessage(msg);
            }
            
            @Override
            public int onEnd() {
                isFinished = true;
                
                System.out.println("onEnd : fin de la performative " + getMessage().getPerformative());
                
                return getMessage().getPerformative();
            }
            
            @Override
            public boolean done() {
                return isFinished;
            }
            
        }
        
        //Fait une proposition à l’envoyeur du message CFP avec le prix qui a été passé en argument à la création de l’agent
        private class SendMessageBehaviour extends OneShotBehaviour {
            ACLMessage rep;
            public SendMessageBehaviour(Agent a) {
                super(a);
            }

            @Override
            public void onStart() {               
                rep = getMessage().createReply(); 
                
                rep.setPerformative(ACLMessage.PROPOSE);
                
                rep.setContent(String.valueOf(getPrice()));
                
                System.out.println("onStart : début de la proposition " + toString());
            }
            
            @Override
            public void action() {                
                send(rep);
                
                System.out.println("action : envoie proposition " + myAgent.getName() + " : " +  rep.toString());
            }
            
            @Override
            public int onEnd(){
                System.out.println("onEnd : fin performative proposition " + message.getPerformative());
                
                return super.onEnd();
            }          
        }
        
        //Classe abstraite Winner/Loser
        private abstract class MyFinalBehaviour extends OneShotBehaviour {
            
            public MyFinalBehaviour(Agent a){
                super(a);
            }
            
            public abstract String result();
                        
            @Override
            public void action(){
                System.out.println(result() + myAgent.getAID());
            }
            
        }
        
        //État terminal Imprime le message gagnant
        public class WinnerBehaviour extends MyFinalBehaviour {

            public WinnerBehaviour(Agent a) {
                super(a);
            }

            @Override
            public String result() {
                return "Gagnant : ";
            }
            
            
        }
        
        //Etat terminal imprime le message perdant
        public class LoserBehaviour extends MyFinalBehaviour {

            public LoserBehaviour(Agent a) {
                super(a);
            }
            
            @Override
            public String result() {
                return "Perdant : ";
            }           
        }     
    }   
}
