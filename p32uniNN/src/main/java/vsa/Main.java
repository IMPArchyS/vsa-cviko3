package vsa;

import entities.Kniha;
import entities.Osoba;
import java.util.ArrayList;
import java.util.List;

import javax.management.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.xml.namespace.QName;

import org.postgresql.core.NativeQuery;

public class Main {

    public static void main(String[] args) {
        Osoba autor1 = new Osoba();
        autor1.setMeno("Kernighan");
        Osoba autor2 = new Osoba();
        autor2.setMeno("Ritchey");
        Kniha kniha = new Kniha();
        kniha.setNazov("Jazyk C");
        kniha.setAutori(new ArrayList<>());
        kniha.getAutori().add(autor1);
        kniha.getAutori().add(autor2);
        Kniha kniha2 = new Kniha();
        kniha2.setNazov("jazyk java");
        kniha2.setAutori(new ArrayList<>());
        kniha2.getAutori().add(autor2);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("vsaPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {

            em.persist(kniha);
            em.persist(kniha2);
            em.persist(autor1);
            em.persist(autor2);
            
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        //System.out.println(najdiAutorov(3L));
        //System.out.println(pocetKnih("Ritchey"));
        //System.out.println(najdiKnihy("Ritchey"));
    }

    public static void persist(Object object) {
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
    
    //  CV4-1
    public static List<Osoba> najdiAutorov(Long idKnihy) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("vsaPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            Kniha k = em.find(Kniha.class, idKnihy);
            if (k == null) 
                return null;
            else 
                return k.getAutori();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return null;
    }
    // CV4-2
    public static int pocetKnih(String menoAutora) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("vsaPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            TypedQuery<Long> q1 = em.createQuery("select o.id from Osoba o where o.meno = :name", Long.class);
            q1.setParameter("name", menoAutora);
            if (q1.getResultList().isEmpty()) 
                return 0;

            TypedQuery<Long> q = em.createQuery("select k.id from Kniha k join k.autori a where a.meno = :name", Long.class);
            q.setParameter("name", menoAutora);
            return q.getResultList().size();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return 0;
    }
    // CV4-3
    public static List<Kniha> najdiKnihy(String menoAutora) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("vsaPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            TypedQuery<Long> q1 = em.createQuery("select o.id from Osoba o where o.meno = :name", Long.class);
            q1.setParameter("name", menoAutora);
            if (q1.getResultList().isEmpty()) 
                return null;

            TypedQuery<Kniha> q = em.createQuery("select k from Kniha k join k.autori a where a.meno = :name", Kniha.class);
            q.setParameter("name", menoAutora);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return null;
    }
}
