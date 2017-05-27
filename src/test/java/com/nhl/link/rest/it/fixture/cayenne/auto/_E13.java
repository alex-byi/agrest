package com.nhl.link.rest.it.fixture.cayenne.auto;

import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import com.nhl.link.rest.it.fixture.cayenne.E12E13;

/**
 * Class _E13 was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _E13 extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<List<E12E13>> E1213 = Property.create("e1213", List.class);

    public void addToE1213(E12E13 obj) {
        addToManyTarget("e1213", obj, true);
    }
    public void removeFromE1213(E12E13 obj) {
        removeToManyTarget("e1213", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<E12E13> getE1213() {
        return (List<E12E13>)readProperty("e1213");
    }


}
