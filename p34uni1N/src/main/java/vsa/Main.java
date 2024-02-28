package vsa;

import entities.Firma;
import entities.Kniha;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class Main {
    public static void main(String[] args) {
        Firma f = new Firma();
        f.setAdresa("Veda");

        Kniha k1 = new Kniha();
        k1.setNazov("1984");
        Kniha k2 = new Kniha();
        k2.setNazov("Animal Farm");

        List<Kniha> kl = new ArrayList<>();
        kl.add(k1);
        kl.add(k2);
        f.setPublikacie(kl);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("vsaPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {

            em.persist(k1);
            em.persist(k2);
            em.persist(f);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        pridajKnihu("Animal Farm", "Jozin");
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
                f.setPublikacie(new ArrayList<Kniha>());
                // ak existuje kniha aktualizuj vydavatela
                if (!q.getResultList().isEmpty()) {
                    for (Kniha kniha : q.getResultList()) {
                        f.getPublikacie().add(kniha);
                    }
                }
                em.persist(f);
            }
            // vytvor knihu, daj jej vydavatelstvo s adresa a pushni
            if (q.getResultList().isEmpty()) {
                System.out.println("Vytvoril som knihu!");
                Kniha k = new Kniha();
                k.setNazov(nazov);
                for (Firma f : q2.getResultList()) {
                    f.getPublikacie().add(k);
                }
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
