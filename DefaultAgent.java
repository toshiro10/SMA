/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testjade;
import jade.core.*;

/**
 *
 * @author Abdelkader
 */
public class DefaultAgent extends Agent{
    @Override
    protected void setup(){
        //initialisation de l’agent
        System.out.println("Agent "+getAID().getName()+" ready");
    }  
    @Override
    protected void takeDown(){
        //traitement de fin
        System.out.println("Agent " +getAID().getLocalName()+" done");
    }
}
