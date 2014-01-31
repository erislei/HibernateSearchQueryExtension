package de.hotware.hibernate.query;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.junit.Before;
import org.junit.Test;

public class SearcherTest {

	private EntityManagerFactory emf;

	@Before
	public void setup() {
		this.emf = Persistence.createEntityManagerFactory("Standalone");

		EntityManager em = this.emf.createEntityManager();
		try {
			EntityTransaction tx = em.getTransaction();
			
			Sorcerer gandalf = new Sorcerer();
			gandalf.setName("Gandalf");
			em.persist(gandalf);
			
			Sorcerer saruman = new Sorcerer();
			saruman.setName("Saruman");
			em.persist(saruman);
			
			Sorcerer radagast = new Sorcerer();
			radagast.setName("Radagast");
			em.persist(radagast);
			
			Sorcerer alatar = new Sorcerer();
			alatar.setName("Alatar");
			em.persist(alatar);
			
			Sorcerer pallando = new Sorcerer();
			pallando.setName("Pallando");
			em.persist(pallando);

			// populate this database with some stuff
			Place helmsDeep = new Place();
			helmsDeep.setName("Helm's Deep");
			Set<Sorcerer> sorcerersAtHelmsDeep = new HashSet<>();
			sorcerersAtHelmsDeep.add(gandalf);
			helmsDeep.setSorcerers(sorcerersAtHelmsDeep);
			em.persist(helmsDeep);

			Place valinor = new Place();
			valinor.setName("Valinor");
			Set<Sorcerer> sorcerersAtValinor = new HashSet<>();
			sorcerersAtValinor.add(saruman);
			valinor.setSorcerers(sorcerersAtValinor);
			em.persist(valinor);
			
			tx.commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}

	}

	@Test
	public void test() {
		FullTextSession session;
		{
			EntityManager em = this.emf.createEntityManager();
			FullTextEntityManager fem = Search.getFullTextEntityManager(em);
			session = fem.unwrap(FullTextSession.class);
		}
		try {

		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

}
