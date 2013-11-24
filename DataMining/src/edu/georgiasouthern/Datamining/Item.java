package edu.georgiasouthern.Datamining;
import java.util.Comparator;

public class Item<T extends Comparable> implements Comparable<Item> {

    /**
     * Content of the item. Is implemented in a generic way in order to manage
     * several types of items.
     */
    private T id;

    /**
     * General constructor.
     * @param id Content of the item.
     */
    public Item(T id) {
        this.id = id;
    }

    /**
     * It gets the content of the item.
     * @return the content
     */
    public T getId() {
        return id;
    }

    /**
     * Get the string representation of this item
     * @return the string representation
     */
    @Override
    public String toString() {
        return "" + getId();
    }

    /**
     * Check if this item is equal to another (if their identifier are equal).
     * @param object the other item
     * @return true if yes, otherwise false
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof Item) {
            Item item = (Item) object;
            if ((item.getId().equals(this.getId()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Item i) {
        //return -id.compareTo(i.getId());
        return id.compareTo(i.getId());
    }
}

class itemComparator implements Comparator<Item> {

    @Override
    public int compare(Item o1, Item o2) {
        int value = o1.compareTo(o2);
        return -value;
    }
}
