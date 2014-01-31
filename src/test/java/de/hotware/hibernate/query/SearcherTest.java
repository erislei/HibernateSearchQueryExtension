/*  
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.*
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   (C) Martin Braun 2014
 */
package de.hotware.hibernate.query;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.hibernate.search.FullTextSession;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.junit.Before;
import org.junit.Test;

public class SearcherTest {

	private EntityManagerFactory emf;
	private MultiClassSearcher multiClassSearcher = new MultiClassSearcherImpl();

	@Before
	public void setup() {
		this.emf = Persistence.createEntityManagerFactory("Standalone");

		EntityManager em = this.emf.createEntityManager();
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

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

			em.flush();
			tx.commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}

	}

	@Test
	public void test() throws InterruptedException {
		FullTextSession session;
		{
			EntityManager em = this.emf.createEntityManager();
			FullTextEntityManager fem = Search.getFullTextEntityManager(em);
			session = fem.unwrap(FullTextSession.class);
		}
		try {
			session.createIndexer(Place.class)
					.progressMonitor(new MassIndexerProgressMonitorImpl())
					.startAndWait();

			SearchFactory searchFactory = session.getSearchFactory();
			assertTrue(session
					.createFullTextQuery(
							searchFactory.buildQueryBuilder()
									.forEntity(Place.class).get().all()
									.createQuery(), Place.class).list().size() > 0);

			// now that we tested some normal queries, let the magic happen:
			// well, this is just some normal easy magic :P
			PlaceQueryBean placeQuery = new PlaceQueryBean();
			placeQuery.setName("Valinor");
			@SuppressWarnings("unchecked")
			List<Place> list = this.multiClassSearcher
					.search(placeQuery, session, Place.class,
							PlaceQueryBean.class).getFullTextQuery().list();
			assertTrue(list.size() > 0);
			System.out.println(list);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public static class MassIndexerProgressMonitorImpl implements
			MassIndexerProgressMonitor {

		@Override
		public void documentsAdded(long arg0) {
		}

		@Override
		public void addToTotalCount(long arg0) {
		}

		@Override
		public void documentsBuilt(int arg0) {
		}

		@Override
		public void entitiesLoaded(int arg0) {
		}

		@Override
		public void indexingCompleted() {
			System.out.println("indexing complete");
		}

	}

}
