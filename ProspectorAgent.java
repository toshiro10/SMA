package testjade;

/**
 *
 * @author Julien SAUSSIER, Abdelkader ZEROUALI
 */
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.List;

import static testjade.Constants.*;

public class ProspectorAgent extends DefaultAgent {

    private ACLMessage winner;
    private String albumName;

    private String getAlbumName() {
        return albumName;
    }

    private void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    private void setAlbumName(Object[] args) {
        if (args != null && args.length > 0) {
            setAlbumName(String.valueOf(args[0]));
        } else {
            System.out.println("pas d'album spécifié" + getAID().getName());
        }
    }

    private void setWinner(ACLMessage msg) {
        winner = msg;
    }

    private ACLMessage getWinner() {
        return winner;
    }

    private boolean isWinnerId(AID agentId) {
        return winner.getSender() == agentId;
    }

    private boolean addVendor(AID agentId) {
        return vendors.add(agentId);
    }
    

    private List<AID> getLosers() {
        List<AID> temp = vendors;
        temp.remove(getWinner().getSender());
        return temp;
    }

    @Override
    protected void setup() {
        super.setup();
        
        setAlbumName(getArguments());
        
        addVendor(LILY);
        addVendor(JIM);
        addVendor(LOLA);
        
        ProspectBehaviour behaviour = new ProspectBehaviour(this);

        this.addBehaviour(new ProspectBehaviour(this));
    }

    class ParallelHandleBehaviour extends ParallelBehaviour {

        private float min = 1000.F;
        private ACLMessage currentMsg;

        //Comparaison prix min vs proposition
        private boolean isWinner(float price) {
            return price < min;
        }
       
       private void setCurrentWinnerMessage(ACLMessage currentMsg) {
            this.currentMsg = currentMsg;
        }

        private ACLMessage getCurrentWinnerMessage() {
            return currentMsg;
        }

        private void setMin(float value) {
            min = value;
        }

        private float getMin() {
            return min;
        }

        ParallelHandleBehaviour(Agent a) {
            //A la fin de tous les comportements éléments en parallèles
            //WHEN_ALL est passé en argument du constructeur
            super(a, ParallelBehaviour.WHEN_ALL);
            
            for(AID vendor : vendors){
                a.addBehaviour(new HandleProposeBehaviour(a, vendor));
            }
        }

        @Override
        public int onEnd() {
            
            
            System.out.println("onEnd : " + this.toString());
            System.out.println("onEnd : gagnant " + 
                    getCurrentWinnerMessage().getSender().getName());
            System.out.println("onEnd : prix final " + getCurrentWinnerMessage().getContent());
            
            //Renvoie du gagnant
            ((ProspectorAgent) getAgent()).setWinner((getCurrentWinnerMessage()));
            return (int)getMin();
        }

        private class MySimpleBehaviour extends SimpleBehaviour {

            private boolean isFinished = false;

            public MySimpleBehaviour(Agent a) {
                super(a);
            }

            @Override
            public void action() {
                System.out.println("Comportement " + this.toString());
            }

            @Override
            public boolean done() {
                return isFinished;
            }

            protected void isFinished() {
                isFinished = true;
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

            private void setTemplate() {
                MessageTemplate msgTplSender = MessageTemplate.MatchSender(getReceiver());
                template = MessageTemplate.and(PROPOSE_TPL, msgTplSender);
            }

            @Override
            public void onStart() {
                System.out.println(" onStart " + this.toString());
                setTemplate();
            }

            @Override
            public void action() {
                System.out.println(" action : gestion de la proposition " + this.toString());
                ACLMessage msgReceive = receive(template);
      
               
                if (msgReceive != null) {
                    handleMessage(msgReceive);
                    isFinished();
                } else {
                    block();
                }
            }
            
            //Lecture du message, vérification du prix
            //mise à jour prix/gagnant
            private void handleMessage(ACLMessage msg) {
                System.out.println("Gestion du message de proposition " + msg);
                int price = Integer.parseInt(msg.getContent());
                if (isWinner(price)) {
                    setMin(price); 
                    setCurrentWinnerMessage(msg);
                    System.out.println("winner : " + msg);
                    System.out.println("winner price: " + price);
                } else {
                    System.out.println("loser : " + msg);
                    System.out.println("loser price: " + price);
                }
            }
        }
    }

    class ProspectBehaviour extends SequentialBehaviour {

        ACLMessage msgAlbum;

        ProspectBehaviour(Agent a) {
            super(a);

            // Envoie un call for propose aux trois vendeurs
            addSubBehaviour(new OneShotBehaviour(this.getAgent()) {
                @Override
                public void onStart() {
                    super.onStart();
                    System.out.println("onStart : Comportement oneshot  " + this.toString());
                    msgAlbum = new ACLMessage(ACLMessage.CFP);
                    
                    msgAlbum.setContent(albumName);
                    //Initialisisation de la performative CFP
                    //msgAlbum.setPerformative(ACLMessage.CFP);

                    //msgAlbum.setContent(albumName);
                }

                @Override
                public void action() {
                    System.out.println("action : envoie du CFP " + this.getAgent());

                    send(msgAlbum);
                }
            });

            //Comportement parallèle qui reçoit en parallèle les trois messages propose et mets à jour à chaque réception de message le message gagnant (prix le plus bas).
            //Le comportement se finit à la fin des comportements éléments
            addSubBehaviour(new ParallelHandleBehaviour(this.getAgent()));

            //Comportement parallèle qui permet d’envoyer un message d’acceptation au gagnant et un message de rejet aux perdants
            addSubBehaviour(new ParallelChooseBehaviour(this.getAgent()));
        }

        @Override
        public int onEnd() {
            System.out.println("onEnd : fin Prospector ");

            return super.onEnd();
        }
    }

    //Comportement parallèle qui permet d’envoyer un message d’acceptation au gagnant et un message de rejet aux perdants
    class ParallelChooseBehaviour extends ParallelBehaviour {

        ParallelChooseBehaviour(Agent a) {
            //Ajout des subBehaviours
            addSubBehaviour(new AcceptBehaviour(this.getAgent()));
            addSubBehaviour(new RejectBehaviour(this.getAgent()));
        }
    }

    class AcceptBehaviour extends OneShotBehaviour {

        ACLMessage acptMsg;

        AcceptBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void onStart() {
            acptMsg = getWinner().createReply();

            acptMsg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

            System.out.println("onStart : proposition acceptée " + acptMsg);
        }

        @Override
        public void action() {
            send(acptMsg);

            System.out.println("action : envoie proposition acceptée ");
        }
    }

    class RejectBehaviour extends OneShotBehaviour {

        ACLMessage rjctMsg;

        RejectBehaviour(Agent a) {
            super(a);
        }
        
        //Ajout des différents losers dans la liste d'envoie
        private void addReceivers() {
            List<AID> losers = getLosers();
            for (AID loser : losers){
                rjctMsg.addReceiver(loser);
            }
            
        }

        @Override
        public void onStart() {
            addReceivers();

            rjctMsg.setPerformative(ACLMessage.REJECT_PROPOSAL);

            System.out.println("onStart : proposition rejetée " + rjctMsg);
        }

        @Override
        public void action() {
            send(rjctMsg);

            System.out.println("action : envoie proposition rejetée ");

        }
    }

}
