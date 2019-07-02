/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testjade;

/**
 *
 * @author Abdelkader
 */
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * envoie trois messages Request avec un comportement Ã un coup Ã 1 seconde
 * Inform Ã 2 secondes Confirm Ã 3 secondes
 *
 * @author mj
 */
public class ProspectorAgent extends DefaultAgent {
    
    /**
     * Mettre une liste d'agent
     */

    private static ACLMessage cfp = ;
    private static ACLMessage reject = ;
    private AID jim = ;
    private AID lola = ;
    private AID lily = ;

    private List sellers = new ArrayList();

    private ACLMessage winner;

    private void setWinner(ACLMessage message) {
        winner = message;
    }

    private ACLMessage getWinner() {
        return winner;
    }

    private boolean isWinnerId(AID agentId) {
        return winner.getSender() == agentId;
    }
    //add a new seller

    private boolean addSellers(AID agentId) {

        return result;
    }
    //return the losers according the list of sellers and the final winner

    private List getLosers() {
        List temp = ;
        ...;
        return temp;
    }

    @Override
    protected void setup() {
        super.setup();
        //initialize the list of sellers with lily, jim and lola

        //add the ProspectBehaviour
    }

    class ParallelHandleBehaviour extends ParallelBehaviour {

        private int min = 1000;
        private ACLMessage current;

        //the winner propose the lower price 
        //comparison between the current minimal price and the price argument
        private boolean isWinner(int price) {
            return ....;
       }
       
       private void setCurrentWinner(ACLMessage message) {
            current = message;
        }

        private ACLMessage getCurrentWinner() {
            return current;
        }

        private void setMin(int value) {
            min = value;
        }

        private int getMin() {
            return min;
        }

        ParallelHandleBehaviour(Agent a) {
            //the behaviour is done when all the sub behaviour are done;

            //add the three sub behaviour which handle the propose message to 
            //lily lola and jim
        }

        @Override
        public int onEnd() {
            System.out.println("onEnd::" + this.toString());
            System.out.println("onEnd:: final winner " + getCurrentWinner());
            //set the final winner

            //return the final price
        }

        private class MySimpleBehaviour extends SimpleBehaviour {

            private boolean finished = false;

            public MySimpleBehaviour(Agent a) {
                super(a);
            }

            @Override
            public void action() {
                System.out.println("Behaviour name " + this.toString());
            }

            @Override
            public boolean done() {
                return finished;
            }

            protected void finished() {
                finished = true;
            }

            @Override
            public int onEnd() {
                System.out.println("onEnd " + this.toString());
                return super.onEnd();
            }
        }

        class HandleProposeBehaviour extends MySimpleBehaviour {

            private AID receiver;
            private MessageTemplate template;
            private MessageTemplate mtPropose = ...;
            public HandleProposeBehaviour(Agent a, AID receiver) {
                super(a);
                setReceiver(receiver);
            }

            private void setReceiver(AID agentId) {
                receiver = agentId;
            }

            private AID getReceiver() {
                return receiver;
            }
            //initialise the template of the handled message
            // sender is the receiver
            //performative is mtPropose

            private void setTemplate() {

            }

            @Override
            public void onStart() {
                System.out.println(" onStart " + this.toString());
                setTemplate();
            }

            @Override
            public void action() {
                System.out.println(" action:: handle propose " + this.toString());
                ACLMessage receive =
                ...;
               
       if (receive != null) {
                    handleMessage(receive);
                    finished();
                } else {
                    System.out.println(" behaviour blocked " + this.toString());
                    block();
                }
            }
            //handle the received message
            //parse the content as a price (type int)
            //if it is the winner (lower price)
            // state as the winner
            //otherwise println the current winner

            private void handleMessage(ACLMessage message) {
                System.out.println("handle propose message" + message);
                ...
                if (isWinner(price)) {
                    ..;
                } else {
                    ...
                }
            }
        }//HandleProposeBehaviour
    }//end ParallelHandleBehaviour

    class ProspectBehaviour extends SequentialBehaviour {

        ProspectBehaviour(Agent a) {
            super(a);

            // l'ajout d'un one-shot behaviour pour envoyer un CFP 
            addSubBehaviour(new OneShotBehaviour(this.getAgent()) {
                @Override
                public void onStart() {
                    super.onStart();
                    System.out.println("onStart:: Comportement   " + this.toString());
                    //initialisze the CFP message
                }

                @Override
                public void action() {
                    System.out.println("action:: send cfp de l'agent " + this.getAgent());
                    //send the cfp message

                }
            });
            //add the ParallelHandleBehaviour

            //add the ParallelChooseBehaviour
        }

        @Override
        public int onEnd() {
            System.out.println("onEnd:: fin activité de l'agent");
            ...
            return  
    

        
    

    ...;
         }
     }//ProspectBehaviour
    
     class ParallelChooseBehaviour extends ParallelBehaviour {

        ParallelChooseBehaviour(Agent a) {
            //the behaviour is done when all the sub behaviour are done;

            //add the two sub behaviour which send the accept and reject messages
        }
    } //Parallel Choose behaviour

    class AcceptBehaviour extends OneShotBehaviour {

        ACLMessage accept;

        AcceptBehaviour(Agent a) {
            super(a);

        }

        @Override
        public void onStart() {
            //initialize the accept message

            System.out.println("onStart::accept-proposal " + accept);
        }

        @Override
        public void action() {
            System.out.println("action::send accept-proposal ");
            //send the accept message
        }
    }

    class RejectBehaviour extends OneShotBehaviour {

        RejectBehaviour(Agent a) {
            super(a);
        }
        // add the losers as the receiver of the reject message

        private void addReceivers(List agents) {
            
        }
    }

    @Override
    public void onStart() {
        //initialize the reject message

        System.out.println("onStart::reject-proposal " + reject);
    }

    @Override
    public void action() {
        System.out.println("action::send reject-proposal ");
        //send the reject message

    }
}

}//end class
