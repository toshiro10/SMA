/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testjade;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Julien SAUSSIER, Abdelkader ZEROUALI
 */
public interface Constants {
    
    AID JIM = new AID("JIM", AID.ISLOCALNAME);
    AID LOLA = new AID("LOLA", AID.ISLOCALNAME);
    AID LILY = new AID("LILY", AID.ISLOCALNAME);
    AID JACK = new AID("JACK", AID.ISLOCALNAME);
    
    List<AID> vendors = new ArrayList();
    
    ACLMessage CFP = new ACLMessage(ACLMessage.CFP);
    ACLMessage REJECT = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
    ACLMessage ACCEPT = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
    
    
    MessageTemplate CFP_TPL = MessageTemplate.MatchPerformative(ACLMessage.CFP);
    MessageTemplate ACCEPT_TPL = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
    MessageTemplate REJECT_TPL = MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL);
    MessageTemplate PROPOSE_TPL = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
}
