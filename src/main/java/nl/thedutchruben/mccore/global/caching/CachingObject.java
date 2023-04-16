package nl.thedutchruben.mccore.global.caching;

import java.util.Date;

public abstract class CachingObject {

    /**
     * Returns the key/identifier of the caching object
     * 
     * @return
     */
    public abstract String getKey();

    /**
     * if true the data will also be saved on on the disk
     * 
     * @return
     */
    public abstract Boolean isPersistent();

    /**
     * Returns the date that the caching object is created
     * 
     * @return
     */
    public abstract Date getCreateDate();

    /**
     * Returns the data that was saved
     * 
     * @return
     */
    public abstract Object getData();

    public boolean isValid() {
        // TODO: Check if the object is still valid
        return true;
    }

    public void saveToDisk() {
        // TODO: Save the data to the disk
    }

}
