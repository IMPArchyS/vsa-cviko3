package vsa;

import entities.Firma;
import entities.Kniha;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class Main {
    public static void main(String[] args) {
        Firma f = new Firma();
        f.setAdresa("Veda");

        Kniha k = new Kniha();
        k.setNazov("1984-opice");
        Kniha k2 = new Kniha();
        k2.setNazov("parizania");
        k.setVydavatel(f);
        k2.setVydavatel(f);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("vsaPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {

            em.persist(k);
            em.persist(k2);
            em.persist(f);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        pridajKnihu("parizania", "Veda");
        pridajKnihu("Nemci", "Veda");
        pridajKnihu("parizania", "Olomouc");
        pridajKnihu("Dogy", "Macky");
    }

    public static void pridajKnihu(String nazov, String adresa) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("vsaPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            // existuje kniha s menom nazov?
            TypedQuery<Kniha> q = em.createQuery("select k from Kniha k where k.nazov = :name", Kniha.class);
            q.setParameter("name", nazov);
            // existuje firma s adresou adresa?
            TypedQuery<Firma> q2 = em.createQuery("select f from Firma f where f.adresa = :adr", Firma.class);
            q2.setParameter("adr", adresa);
            // vytvor firmu a pushni ju do DB
            if (q2.getResultList().isEmpty()) {
                System.out.println("Vytvoril som firmu!");
                Firma f = new Firma();
                f.setAdresa(adresa);
                em.persist(f);
                // ak existuje kniha
                if (!q.getResultList().isEmpty()) {
                System.out.println("Priradil som firmu ku knihe!");
                    for (Kniha kniha : q.getResultList()) {
                        kniha.setVydavatel(f);
                        em.persist(kniha);
                    }
                }
            }
            // vytvor knihu, daj jej vydavatelstvo s adresa a pushni
            if (q.getResultList().isEmpty()) {
                System.out.println("Vytvoril som knihu!");
                Kniha k = new Kniha();
                k.setNazov(nazov);
                k.setVydavatel(q2.getSingleResult());
                em.persist(k);
            }
            // pushni na DB
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}
