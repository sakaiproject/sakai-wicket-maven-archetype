#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.logic;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import ${package}.dao.ProjectDao;
import ${package}.logic.SakaiProxy;
import ${package}.model.Thing;

/**
 * Implementation of {@link ProjectLogic}
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
@Slf4j
public class ProjectLogicImpl implements ProjectLogic {
	
	/**
	 * {@inheritDoc}
	 */
	public Thing getThing(long id) {
		
		//check cache 
		Element element = cache.get(id);
		if(element != null) {
			if(log.isDebugEnabled()) {
				log.debug("Fetching item from cache for: " + id);
			}
			return (Thing)element.getValue();
		}
		
		//if nothing from cache, get from db and cache it 
		Thing t = dao.getThing(id);
			
		if(t != null) {
			if(log.isDebugEnabled()) {
				log.debug("Adding item to cache for: " + id);
			}
			cache.put(new Element(id,t));
		}

		return t;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<Thing> getThings() {
		return dao.getThings();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean addThing(Thing t) {
		return dao.addThing(t);
	}
	
	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		log.info("init");
	}
	
	@Setter
	private ProjectDao dao;
	
	@Setter
	private Cache cache;

}
