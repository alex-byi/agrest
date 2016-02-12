package com.nhl.link.rest.it.fixture.cayenne.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import com.nhl.link.rest.it.fixture.cayenne.E15;
import com.nhl.link.rest.it.fixture.cayenne.E3;

/**
 * Class _E5 was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _E5 extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Date> DATE = new Property<Date>("date");
    public static final Property<String> NAME = new Property<String>("name");
    public static final Property<List<E15>> E15S = new Property<List<E15>>("e15s");
    public static final Property<List<E3>> E2S = new Property<List<E3>>("e2s");

    public void setDate(Date date) {
        writeProperty("date", date);
    }
    public Date getDate() {
        return (Date)readProperty("date");
    }

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void addToE15s(E15 obj) {
        addToManyTarget("e15s", obj, true);
    }
    public void removeFromE15s(E15 obj) {
        removeToManyTarget("e15s", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<E15> getE15s() {
        return (List<E15>)readProperty("e15s");
    }


    public void addToE2s(E3 obj) {
        addToManyTarget("e2s", obj, true);
    }
    public void removeFromE2s(E3 obj) {
        removeToManyTarget("e2s", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<E3> getE2s() {
        return (List<E3>)readProperty("e2s");
    }


}
