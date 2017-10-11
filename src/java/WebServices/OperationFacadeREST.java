/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package WebServices;

import beans.Compte;
import beans.Operation;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Lmarbouh Mhamùed
 */
@Stateless
@Path("operation")
public class OperationFacadeREST extends AbstractFacade<Operation> {

    @PersistenceContext(unitName = "TpWebServicePU")
    private EntityManager em;

    public OperationFacadeREST() {
        super(Operation.class);
    }
    
    @EJB
    CompteFacadeREST compteFacadeREST;
    
    @POST
    @Path("effectuer")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public void creatOperation(@FormParam("rib") String rib, @FormParam("montant") Double montant,@FormParam("type") int type) {
        Compte compte = compteFacadeREST.find(rib);
        switch (type) {
            case 1:
                compte.setSolde(compte.getSolde() + montant);
                create(new Operation(compte, montant, type));
                compteFacadeREST.edit(compte);
                break;
            case 2:
                if (compte.getSolde() < montant) {
                break;
                } else {
                    compte.setSolde(compte.getSolde() - montant);
                    create(new Operation(compte, montant, type));
                    compteFacadeREST.edit(compte);
                    break;
                }
            default:
                break;
        }
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Operation entity) {
        super.create(entity);
    }

    @PUT
    @Path("edit/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Long id, Operation entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("delete/{id}")
    public void remove(@PathParam("id") Long id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("find/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Operation find(@PathParam("id") Long id) {
        return super.find(id);
    }

    @GET
    @Path("all")
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Operation> findAll() {
        return super.findAll();
    }

    @GET
    @Path("rang/{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Operation> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
