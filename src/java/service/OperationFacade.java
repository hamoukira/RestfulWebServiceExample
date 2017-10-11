/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package service;

import beans.Compte;
import beans.Operation;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Lmarbouh Mhamùed
 */
@Stateless
public class OperationFacade extends AbstractFacade<Operation> {

    @PersistenceContext(unitName = "TpWebServicePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OperationFacade() {
        super(Operation.class);
    }
    
    @EJB 
    CompteFacade compteFacade;

    public void createOp(Operation selected) {
        Compte compte = compteFacade.find(selected.getCompte().getRib());
        switch (selected.getType()) {
            case 1:
                compte.setSolde(compte.getSolde() + selected.getMontant());
                create(new Operation(compte, selected.getMontant(), selected.getType()));
                compteFacade.edit(compte);
                break;
            case 2:
                if (compte.getSolde() < selected.getMontant()) {
                break;
                } else {
                    compte.setSolde(compte.getSolde() - selected.getMontant());
                    create(new Operation(compte, selected.getMontant(), selected.getType()));
                    compteFacade.edit(compte);
                    break;
                }
            default:
                break;
        }
    }

}
