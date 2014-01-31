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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

@Indexed
@Entity
public class Place {

	private Integer id;
	private String name;
	private Set<Sorcerer> sorcerers = new HashSet<>();

	@Id
	@DocumentId
    @GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Field(store = Store.NO, index = Index.YES )
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@IndexedEmbedded
	@OneToMany
	public Set<Sorcerer> getSorcerers() {
		return sorcerers;
	}

	public void setSorcerers(Set<Sorcerer> sorcerers) {
		this.sorcerers = sorcerers;
	}

	@Override
	public String toString() {
		return "Place [id=" + id + ", name=" + name + ", sorcerers="
				+ sorcerers + "]";
	}

}
