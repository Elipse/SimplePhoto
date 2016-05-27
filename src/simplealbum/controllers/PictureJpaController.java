/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.controllers;

import simplealbum.entities.Picture;
import simplealbum.controllers.exceptions.NonexistentEntityException;
import simplealbum.controllers.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;

/**
 *
 * @author elialva
 */
public class PictureJpaController implements Serializable {

    public PictureJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Picture picture) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(picture);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPicture(picture.getId()) != null) {
                throw new PreexistingEntityException("Picture " + picture + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Picture picture) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            picture = em.merge(picture);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = picture.getId();
                if (findPicture(id) == null) {
                    throw new NonexistentEntityException("The picture with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Picture picture;
            try {
                picture = em.getReference(Picture.class, id);
                picture.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The picture with id " + id + " no longer exists.", enfe);
            }
            em.remove(picture);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Picture> findPictureEntities() {
        return findPictureEntities(true, -1, -1);
    }

    public List<Picture> findPictureEntities(int maxResults, int firstResult) {
        return findPictureEntities(false, maxResults, firstResult);
    }

    private List<Picture> findPictureEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Picture as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Picture findPicture(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Picture.class, id);
        } finally {
            em.close();
        }
    }

    public int getPictureCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Picture as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
