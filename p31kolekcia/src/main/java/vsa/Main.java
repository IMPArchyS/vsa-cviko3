package vsa;

import entities.Kniha;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class Main {
    public static void main(String[] args) {
        create();
        findById();
        jpqlDotaz();
    }
    static void create() {
        Kniha k = new Kniha();
        k.setId(333L);
        k.setNazov("Jazyk C");
        k.setAutori(new ArrayList<>());
        k.getAutori().add("Kernighan");
        k.getAutori().add("Ritchey");
        
        persist(k);
    }
    static void persist(Object object) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("vsaPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(object);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
    private static void findById() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("vsaPU");
        EntityManager em = emf.createEntityManager();
        Kniha k = null;
        em.getTransaction().begin();
        try {
            k = em.find(Kniha.class, 333L);
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        System.out.println("CV1-1: Najdena kniha by ID: " + k);
    }
    private static void jpqlDotaz() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("vsaPU");
        EntityManager em = emf.createEntityManager();
        
        TypedQuery<Kniha> q3 = em.createQuery("select k from Kniha k where k.autori='Ritchey'", Kniha.class);
        System.out.println("CV1-2: najdena kniha podla autora : " + q3.getSingleResult());
        em.close();
    }
}
