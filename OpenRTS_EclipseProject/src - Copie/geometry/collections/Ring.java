package geometry.collections;

import java.util.ArrayList;
import java.util.Collection;



// BEN ici, j'ai utiliser une arraylist sans trop savoir quelle est la meilleur collection à utiliser pour un anneau d'objets.
// j'ai cherché sur le net pour une collection qui acceler le traitement de la recherche d'objet, mais je n'ai rien trouvé
// d'ideal.

//BEN les supress warning et le serialVersion, j'y comprend rien, j'ai fait comme eclipse m'a proposé. il y a quelque chose à savoir?
@SuppressWarnings("hiding")
public class Ring<Object> extends ArrayList<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Ring() {
		
	}
	
	public Ring(Collection<Object> col) {
		super(col);
	}

	public Object getFirst() {
		if(isEmpty())
			throw new RuntimeException("This " + this.getClass().getName() +" is empty.");
		
		return get(0);
	}
		
	public Object getLast() {
		if(isEmpty())
			throw new RuntimeException("This " + this.getClass().getName() +" is empty.");
		
		return get(size() - 1);
	}

	// TODO comment faire un check de la taille de la liste, en une fois?
	// Sans qu'elle soit immutable, j'ai quand meme besoin de verifier que sa taille ne soit jamais nulle, non?
	// sans quoi ce genre de methode renverrai une out of bound exception
	public Object getPrevious(int index) {
		if(index == 0)
			return getLast();
		return get(index - 1);
	}

	public Object getPrevious(Object o) {
		Object debug = get(indexOf(o));
		if(debug != o)
			for (int i = 0; i < size(); i++)
				if(get(i) == o)
					return getPrevious(i);
		return getPrevious(indexOf(o));
	}

	public Object getNext(int index) {
		if(index == size() - 1)
			return getFirst();
		return get(index + 1);
	}
	
	public Object getNext(Object o) {
		// BEN J'ai un souci ici pour mes getNext et previous, qui lance des indexOf. il semble que le indexof fasse un equals(),
		// donc lorsqu'un polygon a deux points consecutifs egaux, le getNext ou previous bloque.
		// Comment faire pour ne chercher que les objets egaux de reference (et non de valeurs) dans une collection? Je crois que
		// ce sont les hashcode, mais je n'ai pas trop compris ce que j'en ai lut.
		Object debug = get(indexOf(o));
		if(debug != o) {
//			LogUtil.logger.warning("there are two or nore equals object in the ring."+debug+o);
			for (int i = 0; i < size(); i++)
				if(get(i) == o)
					return getNext(i);
		}
		return getNext(indexOf(o));
	}
	
	public void _shiftTo(Object start) {
		ArrayList<Object> newRing = new ArrayList<Object>();
		Object o = get(indexOf(start));
		do {
			newRing.add(o);
			// BEN thypiquement ici, si j'ai deux points equivalents dans mon polygone (cas des polygones � trou), le getNext, en cherchant
			// le premier objet equals() risque de me renvoyer mon objet start et me faire sortir de la boucle avant l'heure
			o = getNext(o);
		} while(o != start);
		clear();
		addAll(newRing);
	}

	public void shiftTo(Object start) {
		ArrayList<Object> newRing = new ArrayList<Object>();
		int index = indexOf(start);
		for (int i = 0; i < size(); i++) {
			Object o = get(index);
			newRing.add(o);
			index++;
			if(index == size())
				index = 0;
		}
		clear();
		addAll(newRing);
	}

	public void shiftTo(int startIndex) {
		shiftTo(get(startIndex));
	}
	
	
	@Override
	public int indexOf(java.lang.Object o) {
		int index = super.indexOf(o);
//		if(index == -1)
//			throw new RuntimeException("The given "+o.getClass().getName()+" doesn't exist in this "+this.getClass().getName()+".");
		return index;
	}
}
