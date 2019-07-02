/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testjade;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author Abdelkader
 */
public interface Constants {
    AID JACK = new AID("Jack", AID.ISLOCALNAME);
    AID LOLA = new AID("LOLA", AID.ISLOCALNAME);
    int SECOND  = 1000;
    ACLMessage REJECT = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
    ACLMessage ACCEPT = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
    
    //Le MessageTemplate CFP
    public static MessageTemplate TEMPLATE_CFP = MessageTemplate.MatchPerformative(ACLMessage.CFP);
    //Le MessageTemplate d'acceptation
    public static MessageTemplate TEMPLATE_ACCEPT = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
    //Le MessageTemplate de refus
    public static MessageTemplate TEMPLATE_REJECT = MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL);
}
