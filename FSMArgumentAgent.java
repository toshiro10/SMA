/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testjade;

import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import static testjade.Constants.*;

/**
 *
 * @author Abdelkader
 */
public class FSMArgumentAgent extends DefaultAgent{

    //Message courant
    private  ACLMessage message ;
    private float price=1000.F;    
    
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
    private void setPrice(float value){
       this.price=value;
    }
    private void setPrice(Object[] args){
       if(args!= null && args.length>0){
           setPrice((float)args[0]);
       }
       else {
           System.out.println("no price specified" + getAID().getName());
       }
    }
    
    private class FSMPerformativeBehaviour extends FSMBehaviour{

        public FSMPerformativeBehaviour(Agent a){
            super(a);
        }   

        @Override
        public void onStart() {
            super.onStart(); 
            
            //Ajout du HandleMessageBehaviour en tant que premier état
            this.registerFirstState(new HandleMessageBehaviour(this.getAgent()), "WAITING");
            //Ajout du SendMessageBehaviour en tant qu'état intermédiaire
            this.registerState(new SendMessageBehaviour(this.getAgent()), "PROPOSING");
            //Ajout du WinnerBehaviour en tant que dernier état
            this.registerLastState(new WinnerBehaviour(this.getAgent()), "WINNER");
            //Ajout du LoserBehaviour en tant que dernier état
            this.registerLastState(new LoserBehaviour(this.getAgent()), "LOSER");
            //Transitions
            this.registerTransition("WAITING", "PROPOSING", ACLMessage.CFP);
            this.registerTransition("WAITING", "WINNER", ACLMessage.ACCEPT_PROPOSAL);
            this.registerTransition("WAITING", "LOSER", ACLMessage.REJECT_PROPOSAL);
            //Transition par défaut
            this.registerDefaultTransition("PROPOSING", "WAITING", new String[]{"WAITING", "PROPOSING"});    
            addBehaviour(this);
        }
        
        

        private class HandleMessageBehaviour extends SimpleBehaviour {
            
            MessageTemplate template;
            boolean finished;
            
            //Constructeur
            HandleMessageBehaviour(Agent a) {
                super(a);
            }
            
            @Override
            public void onStart() {
                System.out.println("onStart:: Beginning Handled " + toString());
                
                //initialisation du template par rapport aux templates prédéfinis
                template = MessageTemplate.or(TEMPLATE_CFP, TEMPLATE_ACCEPT);
                template = MessageTemplate.or(template, TEMPLATE_REJECT);
            }

            @Override
            public void action() {
                ACLMessage message;
                message = blockingReceive(template);
                if(message != null){
                    doMessage(message);
                } else {
                    System.out.println("block");
                    block();               
                }
                System.out.print("HANDLE MESSAGE :" + getMessage().getContent() + " Destinataire : " + myAgent.getAID().getName());
            }
            
            public void doMessage(ACLMessage message){
                setMessage(message);
            }
            
            @Override
            public int onEnd() {
                System.out.println("onEnd:: Performative Handled " + getMessage().getPerformative());
                finished = true;
                return getMessage().getPerformative();
            }
            
            @Override
            public boolean done() {
                return finished;
            }
            
        }
        
        private class SendMessageBehaviour extends OneShotBehaviour {
            ACLMessage reply;
            public SendMessageBehaviour(Agent a) {
                super(a);
            }

            @Override
            public void onStart() {
                //super.onStart();
                System.out.println("onStart:: Beginning proposal " + toString());               
                reply = getMessage().createReply(); 
                reply.setPerformative(ACLMessage.PROPOSE);
                reply.setContent(String.valueOf(getPrice()));
            }
            
            @Override
            public void action() {
                //Envoi de la reponse
                System.out.println("action:: envoi du message propose par l'agent " + myAgent.getName() + " : " +  reply.toString());
                send(reply);
            }
            
            @Override
            public int onEnd(){
                System.out.println("onEnd:: Performative Proposal " + message.getPerformative());
                return super.onEnd();
            }          
        }
        
        private abstract class MyFinalBehaviour extends OneShotBehaviour {
            
            public MyFinalBehaviour(Agent a){
                super(a);
            }
            
            @Override
            public void action(){
                System.out.println(result() + myAgent.getAID());
            }
            
            public abstract String result();
        }
        
        public class WinnerBehaviour extends MyFinalBehaviour {

            public WinnerBehaviour(Agent a) {
                super(a);
            }

            @Override
            public String result() {
                return "Winner :";
            }
            
            
        }
        
        public class LoserBehaviour extends MyFinalBehaviour {

            public LoserBehaviour(Agent a) {
                super(a);
            }
            
            @Override
            public String result() {
                return "Loser :";
            }           
        }     
    }   
}
